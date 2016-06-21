#!/bin/bash
WORKDIR=`pwd`
OUTPUT=output
ENGINE_DIR=output/engine
mkdir -p $ENGINE_DIR
mvn  clean dependency:copy-dependencies -DoutputDirectory=$OUTPUT/lib package  -Dmaven.test.skip=true
cp target/pbrpc-0.0.1-SNAPSHOT.jar $OUTPUT

jarfilelist=`ls -l $OUTPUT/lib/*.jar | awk '{print $NF}'`
jarfiles=

for file in $jarfilelist 
do
	if [ jarfiles == '' ]; then
		jarfiles=$file
	else 
		jarfiles=$jarfiles:$file
	fi
done
cd $ENGINE_DIR && jar xf ../pbrpc-0.0.1-SNAPSHOT.jar
cd $WORKDIR 
JVM_HEAP_MAX=4096
JVM_OPT="-server -Xmx${JVM_HEAP_MAX}m -Xms${JVM_HEAP_MAX}m -XX:SurvivorRatio=8 -XX:NewRatio=4 -XX:PermSize=128m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintCommandLineFlags -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:ParallelCMSThreads=4 -XX:+CMSClassUnloadingEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=1 -XX:CMSInitiatingOccupancyFraction=72"
java $JVM_OPT -Dfile.encoding=UTF-8 -cp $ENGINE_DIR:$jarfiles dos.des.server.App

