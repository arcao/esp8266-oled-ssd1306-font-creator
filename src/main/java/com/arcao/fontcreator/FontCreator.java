package com.arcao.fontcreator;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import joptsimple.OptionException;

public class FontCreator {
    private static final ArgumentParser parser = new ArgumentParser();

    private static void showHelp() {
        System.out.println("Font creator for the esp8266-oled-ssd1306 library");
        System.out.println("Created by Arcao (http://arcao.com), based on squix78 (http://blog.squix.ch) work.");
        System.out.println("");
        System.out.print("Usage: fontcreator [options] [FONT]");
        System.out.println("");
        parser.printHelp();
    }

    public static void main(String args[]) {
        if (args == null || args.length == 0) {
            showHelp();
            return;
        }

        Arguments arguments;
        try {
            arguments = parser.parse(args);
        } catch (OptionException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.err.println();
            showHelp();
            return;
        }

        if (arguments.help()) {
            showHelp();
            return;
        }

        try {
            int fontStyle = Font.PLAIN;
            if (arguments.bold())
                fontStyle |= Font.BOLD;
            if (arguments.italic())
                fontStyle |= Font.ITALIC;

            Font font;
            File fontFile = new File(arguments.font());
            if (fontFile.exists()) {
                font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(fontStyle, arguments.size());
            } else {
                font = new Font(arguments.font(), fontStyle, arguments.size());
            }

            Charset charset = Charset.forName("iso-8859-1");
            if (arguments.charset() != null)
                charset = Charset.forName(arguments.charset());

            FontConverterV3 fontConverter = new FontConverterV3(font, charset);

            StringBuilder builder = new StringBuilder();
            fontConverter.printFontData(builder, arguments.yoffset());

            PrintStream out = System.out;
            if (arguments.outputFile() != null) {
                out = new PrintStream(new FileOutputStream(arguments.outputFile()), true, "utf-8");
            }

            out.print(builder.toString());
            out.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
