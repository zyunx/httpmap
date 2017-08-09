#!/bin/bash

PWD=$(pwd)

MAIN_CLASS=net.zyunx.httpmap.Main

CLASSPATH=$PWD/conf:.

for jar in $PWD/lib/*.jar ; do
	CLASSPATH=$jar:$CLASSPATH
done

java -cp $CLASSPATH $MAIN_CLASS

