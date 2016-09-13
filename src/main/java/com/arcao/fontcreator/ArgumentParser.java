package com.arcao.fontcreator;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import static java.util.Arrays.asList;

public class ArgumentParser {
    private final OptionParser parser = new OptionParser();

    private final OptionSpec<Void> help;
    private final OptionSpec<Integer> size;
    private final OptionSpec<Void> bold;
    private final OptionSpec<Void> italic;
    private final OptionSpec<String> charset;
    private final OptionSpec<File> outputFile;
    private final OptionSpec<String> font;

    public ArgumentParser() {
        help = parser.acceptsAll(asList("h", "help"), "display this help and exit").forHelp();
        size = parser.acceptsAll(asList("s", "size"), "font size").withRequiredArg().ofType(Integer.class);
        bold = parser.acceptsAll(asList("b", "bold"), "use bold font variant");
        italic = parser.acceptsAll(asList("i", "italic"), "use italic font style");
        charset = parser.acceptsAll(asList("c", "charset"), "font table index charset").withOptionalArg().defaultsTo("iso-8859-1").ofType(String.class);
        outputFile = parser.acceptsAll(asList("o", "output"), "write .h font table to file instead to StdOut").withOptionalArg().ofType(File.class);
        font = parser.nonOptions("system font name or path to font file to be processed").describedAs("FONT").ofType(String.class);
    }

    public Arguments parse(String args[]) throws OptionException {
        OptionSet options = parser.parse(args);

        return new Arguments() {
            @Override
            public boolean help() {
                return options.has(help);
            }

            @Override
            public int size() {
                return options.valueOf(size);
            }

            @Override
            public boolean bold() {
                return options.has(bold);
            }

            @Override
            public boolean italic() {
                return options.has(italic);
            }

            @Override
            public String charset() {
                return options.valueOf(charset);
            }

            @Override
            public File outputFile() {
                return options.valueOf(outputFile);
            }

            @Override
            public String font() {
                return options.valueOf(font);
            }
        };
    }

    public void printHelp() {
        try {
            parser.printHelpOn(System.out);
        } catch (IOException e) {
            // ignore
        }
    }
}
