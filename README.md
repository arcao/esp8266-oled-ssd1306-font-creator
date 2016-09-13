# esp8266-oled-ssd1306-font-creator
Font creator for the [esp8266-oled-ssd1306](https://github.com/squix78/esp8266-oled-ssd1306) library. It's generate font.h file in Font V3 format used in this library.

## Download
You can download pre-build console application from [latest release page]:(https://github.com/arcao/esp8266-oled-ssd1306-font-creator/releases/latest). For running this application you need `JRE 1.8.x` (`Sun JRE` or `OpenJDK`) and newer. In `fontcreator/bin` directory are located `bat`/`bash` scripts to execute the app. For usages see bellow...

## Install from latest sources
For installation you need to have installed `Sun JDK 1.8` or `OpenJDK 1.8`.

1. Download the source package [from this page](https://github.com/arcao/esp8266-oled-ssd1306-font-creator/archive/master.zip)
2. Unzip the archive.
3. Run `gradlew installDist` in `esp8266-oled-ssd1306-font-creator-master` directory. It should prepare distribution `zip` and `tar` files in `build/distributions` directory and also unpack the distribution archive in `build/install`. 
4. Go to `build/install/fontcreator/bin` subdirectory. There are located `bat`/`bash` scripts to execute the app. For usages see bellow...

## Usage
```
Font creator for the esp8266-oled-ssd1306 library
Created by Arcao (http://arcao.com), based on squix78 (http://blog.squix.ch) work.

Usage: fontcreator [options] [FONT]

Non-option arguments:
[FONT] -- system font name or path to font file to be processed

Option                Description
------                -----------
-b, --bold            use bold font variant
-c, --charset         font table index charset (default: iso-8859-1)
-h, --help            display this help and exit
-i, --italic          use italic font style
-o, --output [File]   write .h font table to file instead to StdOut
-s, --size <Integer>  font size
```

## License
```
Copyright 2016 Martin "Arcao" Sloup

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
