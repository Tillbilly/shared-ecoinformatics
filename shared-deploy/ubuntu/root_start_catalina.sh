#!/bin/sh
JAVA_OPTS="-Xms512M -Xmx2048M -server -XX:+UseParallelGC -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled -XX:MaxPermSize=128M"
export JAVA_OPTS
JAVA_HOME=/usr/local/jdk1.7.0_40
export JAVA_HOME
nohup ./catalina.sh start
