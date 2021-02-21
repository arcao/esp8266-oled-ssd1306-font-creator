/*
Based on squix78 implementation (MIT License) from:
https://github.com/squix78/esp8266-oled-ssd1306-font-converter/blob/81bf43601496b2e607563d374a03b8b385e31940/src/main/java/ch/squix/esp8266/fontconverter/rest/FontConverterV3.java

Some parts was changed or improved for application usage by Arcao.
*/
package com.arcao.fontcreator;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontConverterV3 {

    private int endChar = 256;
    private int startChar = 32;

    private Graphics2D g;
    private FontMetrics fontMetrics;
    private BufferedImage image;
    private Charset charset;
	
	private int yoffset;


    public FontConverterV3(Font font) {
        image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setFont(font);
        fontMetrics = g.getFontMetrics();
    }

    public FontConverterV3(Font font, Charset charset) {
        this(font);
        this.charset = charset;
    }

    public void printFontData(StringBuilder builder, int yoffset, String characters) {
        calculateStartEnd(characters);
		this.yoffset=yoffset;
        List<LetterData> letterList = produceLetterDataList(characters);

        String fontName = g.getFont().getFontName().replaceAll("[\\s\\-\\.]", "_") + "_"
                + getFontStyle() + "_" + g.getFont().getSize();
        builder.append("// Font table version: 3\n");
        builder.append("// Created by FontCreator (https://github.com/arcao/esp8266-oled-ssd1306-font-creator.\n");
        builder.append("// In case of problems make sure that you are using the font file with the correct version!\n");
        if (characters != null && !characters.isEmpty()) {
            builder.append("// contains only \"");
            builder.append(characters);
            builder.append("\"\n");
        }
        builder.append(String.format("const uint8_t %s[] PROGMEM = {\n", fontName));
        writeHexValue(builder, "Width", getMaxCharWidth());
        writeHexValue(builder, "Height", getMaxCharHeight());
        writeHexValue(builder, "First Char", startChar);
        writeHexValue(builder, "Numbers of Chars", endChar - startChar);
        builder.append("\n");
        builder.append("\t// Jump Table:\n");

        int lastJumpPoint = 0;
        for (LetterData letter : letterList) {
            int letterWidth = letter.getWidth();
            int size = letter.getByteSize();
            String code = "" + ((int) letter.getCode()) + "=" + letter.getCode();
            if (letter.isVisible()) {
                writeJumpTable(builder, code, lastJumpPoint, size, letterWidth);
                lastJumpPoint += size;
            } else {
                writeJumpTable(builder, code, 0xFFFF, size, letterWidth);
            }
        }

        builder.append("\n");
        builder.append("\t// Font Data:\n");

        letterList.stream().filter(LetterData::isVisible).forEach(letter -> {
            builder.append("\t");
            builder.append(letter.toString());
            if ((int) letter.getCode() != endChar - 1) {
                builder.append(",");
            }
            builder.append(String.format("\t// %d\n", (int) letter.getCode()));
        });

        builder.append("};\n");
    }

    private List<LetterData> produceLetterDataList(String characters) {
        List<LetterData> letterDataList = new ArrayList<>(endChar - startChar);
        for (int i = startChar; i < endChar; i++) {
            char ch;
            if (charset == null) {
                ch = (char) i;
            } else {
                ch = new String(new byte[]{(byte) (i & 0xFF)}, charset).charAt(0);
            }
            LetterData letter = createLetterData(ch);
            letterDataList.add(letter);
            if (characters != null && !characters.isEmpty() && characters.indexOf(ch) < 0) {
                letter.visible = false;
            }
        }
        return letterDataList;
    }

    private LetterData createLetterData(char code) {
        BufferedImage letterImage = drawLetter(code);

        int width = fontMetrics.charWidth(code);
        int height = fontMetrics.getHeight();

        int arrayHeight = (int) Math.ceil((double) height / 8.0);
        int arraySize = width * arrayHeight;

        int character[] = new int[arraySize];

        boolean isVisibleChar = false;

        if (width > 0) {
            for (int i = 0; i < arraySize; i++) {
                int xImg = (i / arrayHeight);
                int yImg = (i % arrayHeight) * 8;
                int currentByte = 0;
                for (int b = 0; b < 8; b++) {
                    if (yImg + b <= height) {
                        if (letterImage.getRGB(xImg, yImg + b) == Color.BLACK.getRGB()) {
                            isVisibleChar = true;
                            currentByte = currentByte | (1 << b);
                        } else {
                            currentByte = currentByte & ~(1 << b);
                        }
                    }
                }

                character[i] = (byte) currentByte;
            }
        }

        // Remove rightmost zeros to save bytes
        int lastByteNotNull = -1;
        for (int i = 0; i < character.length; i++) {
            if (character[i] != 0)
                lastByteNotNull = i;
        }

        character = Arrays.copyOf(character, lastByteNotNull + 1);

        return new LetterData(code, character, width, isVisibleChar);
    }

    private BufferedImage drawLetter(char code) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 450, 250);
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(code), 0, fontMetrics.getAscent() + fontMetrics.getLeading() - yoffset);
        return image;
    }


    private String getFontStyle() {
        Font font = g.getFont();
        if (font.isPlain()) {
            return "Plain";
        } else if (font.isItalic() && font.isBold()) {
            return "ItalicBold";
        } else if (font.isBold()) {
            return "Bold";
        } else if (font.isItalic()) {
            return "Italic";
        }
        return "";
    }

    private void writeJumpTable(StringBuilder builder, String label, int jump, int size, int width) {
        builder.append(String.format("\t0x%02X, ", (jump >> 8) & 0xFF)); // MSB
        builder.append(String.format("0x%02X, ", jump & 0xFF)); // LSB
        builder.append(String.format("0x%02X, ", size)); // byteSize
        builder.append(String.format("0x%02X, ", width)); // WIDTH
        builder.append(String.format(" // %s:%d\n", label, jump));
    }

    private void writeHexValue(StringBuilder builder, String label, int value) {
        builder.append(String.format("\t0x%02X, // %s: %d", value, label, value));
        builder.append("\n");
    }

    private int getMaxCharWidth() {
        int maxWidth = 0;
        for (int i = startChar; i < endChar; i++) {
            maxWidth = Math.max(maxWidth, fontMetrics.charWidth((char) i));
        }
        return maxWidth;
    }

    private int getMaxCharHeight() {
        return fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + fontMetrics.getLeading();
    }

    private void calculateStartEnd(String characters) {
        if (characters != null && !characters.isEmpty()) {
            startChar = 255;
            endChar = 0;
            for (int i = 0; i < characters.length(); i++) {
                int idx = characterTargetIndex(characters.charAt(i));
                if (idx < startChar) {
                    startChar = idx;
                }
                if (idx >= endChar) {
                    endChar = idx + 1;
                }
            }
        }
    }

    private int characterTargetIndex(char c) {
        int ch;
        if (charset == null) {
            ch = c;
        } else {
            ch = Character.toString(c).getBytes(charset)[0] & 0xFF;
        }
        return ch;
    }

    private class LetterData {

        private char code;
        private int[] bytes;
        private int width;
        private boolean visible;

        LetterData(char code, int[] bytes, int width, boolean visible) {
            this.code = code;
            this.bytes = bytes;
            this.width = width;
            this.visible = visible;
        }

        char getCode() {
            return code;
        }

        int getWidth() {
            return width;
        }

        boolean isVisible() {
            return visible;
        }

        int getByteSize() {
            return bytes.length;
        }

        public String toString() {
            if (bytes.length <= 0 || !visible) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                if (i > 0) {
                    builder.append(",");
                }
                builder.append(String.format("0x%02X", (byte) bytes[i]));
            }
            return builder.toString();

        }
    }

}
