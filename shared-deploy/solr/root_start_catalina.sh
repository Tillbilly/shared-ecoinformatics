#!/bin/sh
JAVA_HOME=/usr/local/jdk1.7.0_40
export JAVA_HOME
JAVA_OPTS="-Dsolr.solr.home=/mnt/shared/solr-4.5.0/production/solr -Xms512M -Xmx2048M -server -XX:+UseParallelGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=128M"
export JAVA_OPTS
nohup ./catalina.sh start
