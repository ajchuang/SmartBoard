#!/bin/bash
# clean existing class files
rm *.class 1>/dev/null 2>/dev/null
rm ./src/*.class 1>/dev/null 2>/dev/null

# Do the compilation
javac -cp "./lib/javacv.jar:./lib/javacpp.jar:./lib/javacv-macosx-x86_64i.jar:opencv-2.4.8-macosx-x86_64.jar:./ffmpeg-2.1.1-macosx-x86_64.jar:./src:./" ./src/*.java 2>&1 1>build.log

# Move resulting class files to the root folder
mv ./src/*.class ./ 1>/dev/null 2>/dev/null

# show errors
cat build.log &

