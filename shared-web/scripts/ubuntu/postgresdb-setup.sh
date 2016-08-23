#!/bin/sh
#Sets up the db`s and roles in postgres

echo "Going to create the shared-dev role in postgres"
echo "Select 'Y' when asked if can create new databases"

createuser -P shared-dev

echo "user shared-dev now created in postgres."
echo "Creating the 'shared' database"

createdb -O shared-dev shared


#if storing lookup codes - createuser -P aekos     , createdb -O aekos aekos