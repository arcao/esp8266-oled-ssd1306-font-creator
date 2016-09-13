package com.arcao.fontcreator;


import java.io.File;

interface Arguments {
    boolean help();

    int size();

    boolean bold();

    boolean italic();

    String charset();

    File outputFile();

    String font();
}
