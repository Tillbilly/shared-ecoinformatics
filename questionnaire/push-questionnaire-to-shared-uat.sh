#!/bin/bash
cd `dirname $0`
QUESTIONNAIRE_FILE=sharedQuestionnaire.xml
REMOTE_UPLOAD_DIR=/home/ubuntu/upload/
NEW_VERSION=`date +%Y%m%d_%H%M%S`
WEBAPP_DIR=/usr/local/apache-tomcat-7.0.42/webapps/shared-web/WEB-INF/classes
SHARED_CONFIG_FILE=shared-web.properties
VERSION_PROP=default.questionnaire.file.version
PEM_PATH=$1
TESTING=false
if [ -z "$PEM_PATH" ]; then
  echo "ERROR: you need to pass the path to the shared-uat PEM file."
  echo "usage: $0 <pem-path>"
  echo "   eg: $0 ~/.ssh/ecoinfrastructure.pem"
  exit 1
fi
if [ ! -f "$PEM_PATH" ]; then
  echo "ERROR: the supplied PEM file path either doesn't exist or isn't a file - $PEM_PATH"
  exit 1
fi
if [ ! -f "$QUESTIONNAIRE_FILE" ]; then
  echo "Programmer error: cannot find questionnaire file - $QUESTIONNAIRE_FILE"
  exit 1
fi
if [ "$2" == "test" ]; then
  TESTING=true
fi
# set the version to a second-specific timestamp
echo "Setting the version to $NEW_VERSION"
sed -i.bak "s/<version>.*/<version>$NEW_VERSION<\/version>/" $QUESTIONNAIRE_FILE
find . -name "*.bak" -exec rm '{}' \; # Thanks http://stackoverflow.com/a/22084103/1410035 for the sed trick
# commit the change
if [ $TESTING == false ]; then
  git add $QUESTIONNAIRE_FILE
  git commit -m "autocommitting questionnaire before upload to SHaRED UAT. New version $NEW_VERSION"
else
  echo "In testing mode so no commit is performed"
fi
# upload the new questionnaire
scp -i "$PEM_PATH" $QUESTIONNAIRE_FILE ubuntu@shared-uat.ecoinformatics.org.au:$REMOTE_UPLOAD_DIR
# move the questionnaire, update the config to look for the new version and bounce the webapp
REMOTE_COMMAND="sudo mv $REMOTE_UPLOAD_DIR/$QUESTIONNAIRE_FILE $WEBAPP_DIR && "
REMOTE_COMMAND+="sudo sed -i 's/$VERSION_PROP.*/$VERSION_PROP=$NEW_VERSION/' $WEBAPP_DIR/$SHARED_CONFIG_FILE && "
REMOTE_COMMAND+="sudo touch $WEBAPP_DIR/../web.xml"
ssh -i "$PEM_PATH" ubuntu@shared-uat.ecoinformatics.org.au $REMOTE_COMMAND
echo "Waiting for webapp to restart. We don't actually check if it's done, we just do nothing for 70 seconds and expect it to be done at the end."
sleep 70
echo "DONE!! Well, it should be, we don't actually know. Don't forget you need to start a new submission to get the new questionnaire."
