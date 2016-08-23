#Run this after tomcat and java have been installed on the server, in the standard shared locations
#i.e. do the install via the script in the ../ubuntu directory
#Run as root
######################################
mv commons-logging-1.1.3.jar /usr/local/apache-tomcat-7.0.42/lib
mv log4j-1.2.17.jar /usr/local/apache-tomcat-7.0.42/lib
mv log4j.properties /usr/local/apache-tomcat-7.0.42/lib
mv slf4j-api-1.6.6.jar /usr/local/apache-tomcat-7.0.42/lib
mv slf4j-log4j12-1.6.6.jar /usr/local/apache-tomcat-7.0.42/lib
mv tomcat.conf /etc/init
mv root_start_catalina.sh /usr/local/apache-tomcat-7.0.42/bin
mv solr-4.5.0.tgz /mnt/shared
cd /mnt/shared
tar -xzvf solr-4.5.0.tgz
cd solr-4.5.0
mkdir production
cp -r ./example/solr ./production
cp /home/ubuntu/schema.xml ./production/solr/collection1/conf/schema.xml
