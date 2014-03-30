#!/bin/bash
rm *.class
javac -cp "./lib/javacv.jar:./lib/javacpp.jar:./lib/javacv-macosx-x86_64i.jar:opencv-2.4.8-macosx-x86_64.jar:./ffmpeg-2.1.1-macosx-x86_64.jar:./src:./" ./src/*.java
cp ./src/*.class ./




