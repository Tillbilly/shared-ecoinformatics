Example Spring Bean xml definition - A lot of the 'extra' settings are used by the shared-web application, 
which is where this code has been lifted from.

 <bean id="s3RestClientDev" class="au.org.ecoinformatics.s3client.S3RESTHttpClientImpl">
    <property name="active" value="true" />
    <property name="objectStoreName" value="nectar_shared_dev" />
    <property name="S3ApiUrl" value="https://swift.rc.nectar.org.au:8888" />
    <property name="bucket" value="shared_submissions" />
    <property name="apiKey" value="d9921bab82084cd4ac8beb43ec4da435" />
    <property name="secretKey" value="4b834e1a23d84be2a09a19cc6e32ad37" />
</bean>


When deployed to production, wire up the apiKey and the secret key from a non-checked in property file.
SHaRED uses a file called s3config.properties, and the apiKey and secretKey are read from that file at server startup.

See ObjectStoreServiceImpl.afterPropertiesSet()  in the shared-web project for details.
 