#!/bin/bash
java -cp "./:./lib/jna.jar:./lib/javacv.jar:./lib/javacpp.jar:./lib/javacv-macosx-x86_64i.jar:./:opencv-2.4.8-macosx-x86_64.jar:./ffmpeg-2.1.1-macosx-x86_64.jar" SmartBoard $1

