I had trouble bundling up the war changes required, the war seemed to get corrupted, 
so what you need to do is deploy the solr-4.5.0.war from the dist directory in the solr install
at /mnt/shared/solr-4.5.0/dist  into tomcat.

Then start, then stop tomcat.

Rename the original war to .war.bu so it does'nt overwrite your changes.

You need to then copy jts-1.13.jar to ./webapps/solr/WEB-INF/lib

If you want to secure solr,  you will also need to alter the web.xml.

Add the following to the end of the web.xml -

 <security-role>
    <role-name>solrWrite</role-name>
  </security-role>

  <security-constraint>
    <web-resource-collection>
      <web-resource-name>Solr authenticated application</web-resource-name>
      <url-pattern>/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
      <role-name>solrWrite</role-name>
    </auth-constraint>
  </security-constraint>

  <login-config>
    <auth-method>BASIC</auth-method>
    <realm-name>User Database</realm-name>
  </login-config>



After running the script solr-setup.sh as root,  you will need to alter the tomcat-users.xml file,
create the solrWrite role and create a user with the solrWrite role.

Thats it. 



<tomcat-users>
    <role rolename="solrWrite" />
    <user username="solr" password="solr" roles="solrWrite" />
</tomcat-users>  
