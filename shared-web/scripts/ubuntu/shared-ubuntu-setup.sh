#!/bin/sh
#Be sure to copy up this script, 
#along with:
#  root_start_catalina.sh
#  root_stop_catalina.sh
#  apache-tomcat-7.0.29.tar.gz
#  jdk-6u24-linux-x64.bin
#
#  Up to /home/ubuntu  on your new instance
#
#Execute this as root - sudo -s

mv apache-tomcat-7.0.29.tar.gz /usr/local
mv jdk-6u24-linux-x64.bin /usr/local
cd /usr/local

chmod a+x ./jdk-6u24-linux-x64.bin
./jdk-6u24-linux-x64.bin

tar -xzvf apache-tomcat-7.0.29.tar.gz

cd /usr/local/apache-tomcat-7.0.29/bin
mv /home/ubuntu/root_start_catalina.sh .
mv /home/ubuntu/root_stop_catalina.sh .
mkdir /mnt/shared
mkdir /mnt/shared/upload-tmp
mkdir /mnt/shared/images

apt-get update
apt-get install postgresql


#also need to add the server name to /etc/hosts  
#
#
#  echo "127.0.0.1 localhost shared-qa" >> /etc/hosts

