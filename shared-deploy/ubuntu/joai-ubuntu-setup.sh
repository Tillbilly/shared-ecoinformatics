#!/bin/sh
#Be sure to copy up this script, 
#along with:
#  root_start_catalina.sh
#  root_stop_catalina.sh
#  apache-tomcat-7.0.42.tar.gz
#  jdk-7u40-linux-x64.tar.gz
#  tomcat1.conf
#
#  Up to /home/ubuntu  on your new instance
#
#Execute this as root - sudo -s


mv apache-tomcat-7.0.42.tar.gz /usr/local
mv tomcat1.conf /etc/init/tomcat.conf
mv jdk-7u40-linux-x64.tar.gz /usr/local
cd /usr/local

tar -xzvf apache-tomcat-7.0.42.tar.gz
tar -xzvf jdk-7u40-linux-x64.tar.gz

cd /usr/local/apache-tomcat-7.0.42/bin
mv /home/ubuntu/root_start_catalina.sh .
mv /home/ubuntu/root_stop_catalina.sh .

mkdir /mnt/rifcs
mkdir /mnt/rifcs/aekos
mkdir /mnt/rifcs/shared
mkdir /mnt/rifcs/shared-dev


