executable jar s3client.

to use, java -jar s3clientexec.jar  will show you usage.

Can perform EXISTS, PUT, GET and DELETE operations.

e.g.

Usage: Command line args -  
java -jar s3clientexec.jar apiUrl apiKey secretKey container EXISTS key
java -jar s3clientexec.jar apiUrl apiKey secretKey container PUT filepath key
java -jar s3clientexec.jar apiUrl apiKey secretKey container GET key filepath
java -jar s3clientexec.jar apiUrl apiKey secretKey container DELETE key