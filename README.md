# shared-ecoinformatics
A copy of the work I did for TERN Ecoinformatics, including a nice S3 Client project, amongst other things.  Work undertaken between 2012 and 2014.  
This code was retrieved in June 2016.

SHaRED is deployed in production at http://www.shared.org.au/

Datasets submitted thru SHaRED are visible within the aekos portal  http://aekos.org.au/


The official code host is hosted in a private bitbucket account,  but is meant to be open sourced.  It was created as a NeCTAR project.

I`ll write more here when I have some time.

Myself, (Ben Till) is the primary author,  but Tom Saleeba has contributed a reasonable amount to this codebase,  including, but not limited to,  the shared-api module,  the UI polish in the shared-web module, any Hamcrest tests,  any new looking devopsy stuff.

A spiel about SHaRED hosted by ANDS  - https://researchdata.ands.org.au/shared-submission-harmonization-ecological-data/357684


The below documentation was written by Tom Saleeba,  who also maintains the codebase at TERN Ecoinformatics.

# SHaRED
## Bare minium set up to satisfy the AEKOS dependency

*Note: If you are going to build all of the SHaRED repository then you don't need to do this as the other instructions in here satisfy AEKOS.*

#### Prerequisites
 - Java 1.6 JDK
 - Maven 3.x

##### The dependency tree
The AEKOS project (consisting of `aekos-backend` and `aekos-portal`) depends on SHaRED.
SHaRED depends on ecoinformatics-rifcs.

#### Steps

 1. Clone and build the `ecoinformatics-rifcs` repository
 1. Clone this SHaRED repository
 1. Run these commands to build all the required modules and install them to your maven repository:

        cd shared/pom
        mvn clean install --projects au.org.aekos.shared:shared-api

 1. Look for a `BUILD SUCCESS` message at the end.
 1. You can now build AEKOS projects.

## Full set up

#### Prerequisites

  - The ecoinformatics-rifcs repository built and installed into your maven repo
  - PostgreSQL 9.2 (with pgAdmin)
  - Java 1.6 JDK
  - Eclipse (or STS) - Not actually required but all the cool kids use it
  - Maven 3.x
  - Tomcat 7 (or vFabric tc Server)

#### Notes when building on Windows
*There is a Windows specific maven profile for `shared-web`, instructions are given at the relevant point in this doco. This means that anywhere that `shared-web.properties` is referred to, Windows users should instead use `shared-web-windows-dev.properties`.*


####Steps
  1. Install PostgreSQL, init a DB and start the service
  1. Create two roles and make both of them superusers
    - User: `shared-dev`, Pass: `shared-dev`
    - User: `aekos`, Pass: `AekosAdmin`
  1. Create two databases
    - `shared` owned by `shared-dev`, and
    - `aekos` owned by `aekos`
  1. Create some directories that have `rw` privileges for the user that will run tomcat:
    - `/data` to satisfy the values of `shared.upload.tempimage.path` and `submission.upload.tempdir` in `shared-web.properties`, and 
    - `/aekos/shared-rifcs` to satisfy the value of `rifcs.xmlFileOutputDir` in `shared-web.properties`
  1. Restore the dump `shared-web/data/aekosForShared<version>.backup` (whatever the newest version is) to the `aekos` DB. If you restore this to a database that is also used for the AEKOS portal, you don't need all the tables because some already exist such as `__lookup_codes`.
  1. Follow the steps in the section titled [Getting RIFCS tests to pass](#markdown-header-getting-rifcs-tests-to-pass)
  1. Using maven, build everything by running against the parent POM (this will build all project in the correct order)
    
            cd shared/pom
            mvn clean install

  1. **Then run this if you're on Windows** because we have a maven profile for building `shared-web` on Windows so you'll be overwriting the *nix/OSX build you just produced but only for the `shared-web` project:
    
            cd shared/shared-web
            mvn clean install -Pwindows
  
  1. Start tomcat and deploy the shared-web WAR to it (this will trigger Hibernate to create the database schema)
  1. Now the tables we need exist we can create some users by running the contents of the `shared-web/sql/init_default_users.sql` script against the `shared` database. The users and their passwords are commented in the file.
  1. You should now be able to access the webapp at localhost:8080/shared-web (assuming tomcat is on the default port and that you renamed the maven artifact from `shared-web-<version>-SNAPSHOT.war` to `shared-web.war`)
  1. Lastly you need to add some custom vocabulary entries.  
Note: you can do this as any user with the `ROLE_VOCAB_MANAGER` role. These vocab CSVs are also attached to `SHD-156` and on the network `S:\Science\EES\TERN\SHaRED\Questionnaire\vocabularies`.
    - Login to the portal as user: `admin`, check the `shared-web/sql/init_default_users.sql` file for the password
    - Open `Vocabulary Management`
    - browse for a file for the `Replace custom vocabularies` functionality 
    - select the latest version of `shared-web/src/test/resources/customVocabs_<version>.csv`
    - press `Replace Vocabs`
    - check `catalina.out` and `shared-web.log` in the tomcat `logs` directory for any exceptions

## Post set up

### Important things to change!!

  1. In `NotificationEmailTest.java`, change the email address to be your own so you don't spam other people every time you build
  1. In `shared-web/src/main/resources/shared-web.properties`, change the `shared.sysadmin.email` value to be your own email address so you don't spam other people every time you build

### Getting RIFCS tests to pass

  1. Get a hold of the `shared-qa.pem` file (ask someone important looking)
  1. Stick it where the `rifcs.oaiserver.privatekey` value in `shared-web.properties` specifies

### Importing projects into Eclipse

We don't store Eclipse project files in git because they're generated content. So, before you can import the projects into Eclipse, you need to generate the project files using maven.

  1. It's assumed you've already followed the steps in "Getting set up" so all the artifacts in the repo have been built and installed into your maven repo.
  1. Generate the Eclipse project files for the `shared-web` project by opening a terminal and doing the following:
                cd shared-web
                mvn eclipse:clean eclipse:eclipse -DdownloadJavadocs
  1. Import the `shared-web` project into Eclipse with something like: Open Eclipse -> File -> Import -> Projects from Git -> Existing local repository -> Add -> (browse to where you cloned the `shared` repo) -> (select the `shared` entry) -> Next -> Next -> (tick `shared-web`) -> Finish
  1. Repeat for each other project you wish to import
  1. Optional: `shared-web` should appear as a Dynamic Web Project so you can create a Tomcat server in Eclipse and deploy it there with all the benefits of hot swapping, etc. Google for more info.

### Setting up shared-web as a dynamic web project

It should all be configured in the POM so when you `mvn eclipse:eclipse` the project and import it to Eclipse, the Deployment Assembly item should be in the properties of the project (and it should already be configured correctly). If it's not then you're kinda stuffed because we never figured out how to fix it.

## Other helpful stuff

### Deploying a production version of shared-web

Things to check before building a WAR:

 1. if you've made changes to the questionnaire, have you updated the version number?
 1. do you know what the password for the production Solr instance is?
 1. have you bumped the version number in:
    - `shared/shared-web/src/main/resources/shared-web.properties`
    - all the `.properties` files under `shared/shared-web/deploy/profiles/`
    - the POM `shared/shared-web/pom.xml`

Now we're ready to build the WAR. The best way to do this is using a build machine like Jenkins, which is still hopefully alive as you read this. There should be a job configured that will build a production WAR for you and the way you indicate what to build is using a git tag.

So, the steps are:

 1. make sure everything is committed in your `shared` repo
 2. tag the correct commit with something like `git tag -am "production release of SHaRED" v2.2.2-rc1` with the version as appropriate and the rcN incremented each time you create a release candidate because it might take a few builds to get something production ready
 3. push the commits and tags to BitBucket with `git push && git push --tags`
 4. open Jenkins and *configure* the job that will build the production WAR for SHaRED (`shared-web-prod` at the time of writing)
 5. change the *Branch Specifier* to the tag you just created
 6. trigger a build of the job. If you don't have this jenkins job and you're building locally, you need to build with the `production` maven profile: `mvn clean install -Pproduction -DskipITs` but only do this as a last resort.
 7. once the build has finished, open the jenkins page for that build, open the `shared-web` module and download the WAR
 8. Open the WAR with an archive tool that lets you edit files in the WAR without extracting it (the Linux Mint default one lets you do this, don't think OSX has anything out of the box and 7zip will probably work for Windows)
 9. edit the file `/WEB-INF/classes/shared-web.properties` and set the property `solr.pubindex.password`. I can't tell you what it is here because it's obviously super secret
 10. Save the file and make sure the WAR is updated with the change
 11. push the WAR onto the SHaRED prod VM
 12. login to the SHaRED prod webapp as `admin` and use the *check active sessions* menu option to make sure no-one else is using the app
 12. SSH to the SHaRED prod VM
 13. go to `/usr/local/apache-tomcat-7.0.42/webapps/`
 14. if you're doing the deploy during business hours, you'll want to be as quick as possible. We want to backup the current WAR (for disaster recovery) and copy the new WAR in with `sudo mv shared-web.war shared-web.war.N.N.N && sudo cp ~/upload/shared-web-N.N.N-SNAPSHOT-production.war shared-web.war`. If you're outside business hours then you can probably take your time.
 15. tail the `logs/catalina-YYYY-MM-DD.log` file to make sure the WAR is being deployed and to watch for any errors
 16. go to the webpage and make sure the deploy completed and run any health check tests required

#### "NoClassDefFoundError: org/bouncycastle/crypto/modes/SICBlockCipher" error
Note: on a few occasions where I've done the hot deploy outlined above, I've had an error thrown during publishing `NoClassDefFoundError: org/bouncycastle/crypto/modes/SICBlockCipher`. This has been fixed by restarting tomcat but after that, the submission that failed publishing will likely have an `APPROVED` status. To fix this, connect to the Postgres instance on the machine and update the 'status' field of the record in the 'submission' table using the submission ID as the key. Set the status to one of 'SUBMITTED', 'RESUBMITTED' or 'PEER_REVIEWED'. Any of those will make the submission appear in the 'Review Submissions' page of the webapp so you can have another go at publishing. If you put notes into the review page, I'm not sure what will happen with them, you may need to re-enter them.

### Deploying a QA/UAT version of shared-web

This is essentially the same as the procedure for production above with the following differences:

 - You don't need to (and probably shouldn't anyway) tag the repo, just build the latest master or whatever your branch is
 - Jenkins has a `shared (QA profile)` job that you can use for the build otherwise just use the `qa` maven profile: `mvn clean install -DskipITs -Pqa`
 - You don't need to edit the properties file in the WAR because there's no secret information

Remember, don't deploy a QA version to prod. Build a prod version for that.

### Deploying a new questionnaire config

  1. Modify the `src/main/resources/sharedQuestionnaire.xml` file to add/remove questions
  1. ...and bump the version number, that is the `<version>` tag, to anything that doesn't appear in the `questionnaire_config` table in the `shared` database, i.e.

            SELECT version FROM questionnaire_config
    
  1. Open the appropriate .properties file (most likely `shared-web/src/main/resources/shared-web-dev.properties`)
  1. Bump the `default.questionnaire.file.version` to match what you entered into the `<version>` tag in the questionnaire config
  1. Restart tomcat and the webapp will (should) automatically reload the config WHEN you create a new submission
  1. (Optionally) If it doesn't reload then rename the questionnaire config file and update the `default.questionnaire.file.name` value in the .properties to point to the new name. Or you could clear the tomcat work directory to make it refresh its cache. This is all to do with Eclipse caching files, running the WAR in a standalone tomcat shouldn't experience any of these issues.

### Installing a new certificate from TERN HQ for the DOI minting service

SHaRED needs to mint DOIs when it publishes a submission. It does this using the service provided by TERN HQ and this service requires us to have an up-to-date certificate in a Bouncy Castle keystore that the DOI client uses. You can get the latest version using your web browser by going to the service endpoint (https://doi.tern.uq.edu.au/index.php at the time of writing) and exporting the certificate either as a PEM (Base64-encoded ASCII, single certificate) and converting it to DER (.cer file) or by exporting straight to DER-encoded binary, single certificate straight up. The process to create a new keystore seems to want the DER format.

Once you have the certificate:

 1. go to shared/doi-client/security
 1. replace the `terndoi.cer` file with the new certificate. If you need to convert a PEM to a .cer, look in `convert-cer-to-pem.sh` for inspiration.
 1. delete the existing `ternkeystore.bks` keystore
 1. run the `create_keystore.sh` script to create a new keystore with the new certificate
 1. copy the `ternkeystore.bks` keystore to `shared/doi-client/src/main/resources/`, `shared/doi-client/src/test/resources/` and `shared/shared-web/src/main/resources/`
 1. run the `shared-web/src/test/java/au/edu/aekos/shared/service/doi/DoiMintingServiceTestIT.java` test and if you don't get a `javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated` message, then the change has worked. Note, you might get another error like `DOI not minted due to a response code of 'errUrlNotResolvable' - Failed to mint DOI` but getting this far means you can connect to the service. Fixing this issue is out of scope here.
 1. now you need to deploy a new version of the shared-web portal with the updated keystore

ಠ_ಠ Noticed something wrong with this doco? Fix it, it'll make you 150% cooler.

