#!/bin/sh
JAVA_HOME=/usr/local/jdk1.6.0_24
export JAVA_HOME
JAVA_OPTS="-Dsolr.solr.home=/mnt/solr"
export JAVA_OPTS
nohup ./catalina.sh start