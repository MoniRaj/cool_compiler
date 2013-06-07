#!/bin/bash

#JFLEX='REPLACE/WITH/PATH/TO/JFLEX'
JFLEX='jflex'

#BEAVERJAR='REPLACE/WITH/PATH/TO/BEAVER'
BEAVERJAR='../beaver/beaver-cc.jar'

#JAVAC='REPLACE/WITH/PATH/TO/JAVAC'
#JAVA='REPLACE/WITH/PATH/TO/JAVA'
JAVAC='javac'
JAVA='java'

COOLFILES=$@

echo "Cleaning up old files"
rm *.class
rm ../ast/*.class
rm ../beaver/*.class
rm ../typecheck/*.class
rm main.cool
rm Terminals.java
mv *.stat stat/

echo "Building parser: ${JAVA} -jar $BEAVERJAR -tAT cool.grammar"
$JAVA -jar $BEAVERJAR -ctaT cool.grammar

echo "Building scanner: ${JFLEX} scanner.flex"
$JFLEX scanner.flex

echo "Compiling Cool compiler: javac Driver.java CoolScanner.java CoolParser.java ErrorReport.java"
$JAVAC -classpath .. Driver.java CoolScanner.java CoolParser.java ErrorReport.java

echo "Executing Cool compiler on source files: java Driver ${COOLFILES}"
$JAVA -classpath .. main/Driver $COOLFILES || { echo 'Failed: Exiting.'; exit 1; }

echo "Converting main.ll to main.s"
$LLC main.ll || { echo 'Failed: Exiting.'; exit 1; }

echo "Compiling .s files into executable"
$CLANG driver.c main.ll -o main_program
