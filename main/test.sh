#!/bin/bash

#JFLEX='REPLACE/WITH/PATH/TO/JFLEX'
JFLEX='jflex'

#BEAVERJAR='REPLACE/WITH/PATH/TO/BEAVER'
BEAVERJAR='beaver/beaver-cc.jar'

#JAVAC='REPLACE/WITH/PATH/TO/JAVAC'
#JAVA='REPLACE/WITH/PATH/TO/JAVA'
JAVAC='javac'
JAVA='java'

COOLFILE=$1

echo "Cleaning up old files"
rm *.class
rm ast/*.class
rm Terminals.java
mv *.stat stat/

echo "Building parser: ${JAVA} -jar $BEAVERJAR -tAT cool.grammar"
$JAVA -jar $BEAVERJAR -ctaT cool.grammar

echo "Building scanner: ${JFLEX} scanner.flex"
$JFLEX scanner.flex

echo "Compiling: javac Driver.java CoolScanner.java CoolParser.java ErrorReport.java"
$JAVAC Driver.java CoolScanner.java CoolParser.java ErrorReport.java

echo "Running: java Driver ${COOLFILE}"
$JAVA Driver $COOLFILE 
