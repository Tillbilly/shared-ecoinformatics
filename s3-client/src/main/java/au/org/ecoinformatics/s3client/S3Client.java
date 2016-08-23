package au.org.ecoinformatics.s3client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;



/**
 * Crude command line client to use the s3client.jar as an executable jar, a shaded executable jar even!
 * @author btill
 */
public class S3Client {

	/*
	 * System.out.println("apiUrl apiKey secretKey container EXISTS key");
		System.out.println("apiUrl apiKey secretKey container PUT filepath key");
		System.out.println("apiUrl apiKey secretKey container GET key filepath");
		System.out.println("apiUrl apiKey secretKey container DELETE key");	
	 */
	
	
	public static final int API_URL_INDEX = 0;
	public static final int API_KEY_INDEX = 1;
	public static final int SECRETKEY_INDEX = 2;
	public static final int CONTAINER_INDEX = 3;
	public static final int OPERATION_INDEX = 4;
	
	public static final int EXISTS_KEY_INDEX = 5;
	
	public static final int PUT_FILEPATH_INDEX = 5;
	public static final int PUT_KEY_INDEX = 6;
	
	public static final int GET_KEY_INDEX = 5;
	public static final int GET_FILEPATH_INDEX = 6;
	
	public static final int DELETE_KEY_INDEX = 5;
	
	public static final String EXISTS_USAGE_MESSAGE = "apiUrl apiKey secretKey container EXISTS key";
	public static final String PUT_USAGE_MESSAGE = "apiUrl apiKey secretKey container PUT filepath key";
	public static final String GET_USAGE_MESSAGE = "apiUrl apiKey secretKey container GET key filepath";
	public static final String DELETE_USAGE_MESSAGE = "apiUrl apiKey secretKey container DELETE key";
	
	
	
	/*
	 * private static final String NECTAR_SWIFT_API_URL = "https://swift.rc.nectar.org.au:8888";
	 * private static final String API_KEY = "d9921bab82084cd4ac8beb43ec4da435";
	 * private static final String SECRET_KEY = "4b834e1a23d84be2a09a19cc6e32ad37";
	 * private static final String BUCKET_NAME = "shared_submissions";
	 */
	
	public static void main(String[] args) {
		System.out.println("Java S3Client by Ben Till");
		if(args == null || args.length < 6 ){
			printUsageInfo("Not enough arguments!");
			return;
		}
		manuallyInitialiseLog4j();
		S3RESTHttpClient s3client = initialiseClientFromArgs(args);
		
		String operation = args[OPERATION_INDEX];
		if("EXISTS".equals(operation)){
			performExists(s3client, args);
		}else if("PUT".equals(operation)){
			performPut(s3client, args);
		}else if("GET".equals(operation)){
			performGet(s3client, args);
		}else if("DELETE".equals(operation)){
			performDelete(s3client, args);
		}else{
			printUsageInfo("Invalid Operation - Choose from EXISTS,PUT,GET,DELETE");
		}

	}
	
	public static void performExists(S3RESTHttpClient s3client, String [] args){
		if(args.length <= EXISTS_KEY_INDEX ){
			System.out.println("Exist operation failed");
			System.out.println(EXISTS_USAGE_MESSAGE);
			return;
		}
		String key = args[EXISTS_KEY_INDEX];
		try {
			if(s3client.objectExistInBucket(key)){
				System.out.println("Object with key " + key + " exists in " + args[CONTAINER_INDEX]);
			}else{
				System.out.println("Object with key " + key + " DOES NOT exist in " + args[CONTAINER_INDEX]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void performPut(S3RESTHttpClient s3client, String [] args){
    	if(args.length <= PUT_KEY_INDEX ){
			System.out.println("Put operation failed");
			System.out.println(PUT_USAGE_MESSAGE);
			return;
		}
    	
    	String filePath = args[PUT_FILEPATH_INDEX];
		String key = args[PUT_KEY_INDEX];
		File file = new File(filePath);
		if(!file.exists() || !file.canRead()){
			System.out.println("File " + filePath + " can not be read. PUT failed.");
			return;
		}
		try {
			int responseCode = s3client.putFile(file, key);
			System.out.println("PUT finished with response code " + Integer.toString(responseCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void performGet(S3RESTHttpClient s3client, String [] args){
		if(args.length <= GET_FILEPATH_INDEX ){
			System.out.println("Get operation failed");
			System.out.println(GET_USAGE_MESSAGE);
			return;
		}
    	
    	String filePath = args[GET_FILEPATH_INDEX];
		String key = args[GET_KEY_INDEX];
		File file = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Filepath " + filePath + " can not be found. GET failed");
			return;
		}
		
		try {
			int responseCode = s3client.getFileStreaming(key, fos);
			System.out.println("GET operation completed with response " + Integer.toString(responseCode));
		} catch (Exception e) {
			System.out.println("GET failed");
			e.printStackTrace();
		}
	}
	//DELETE_KEY_INDEX
	public static void performDelete(S3RESTHttpClient s3client, String [] args){
		if(args.length <= DELETE_KEY_INDEX ){
			System.out.println("Delete operation failed");
			System.out.println(DELETE_USAGE_MESSAGE);
			return;
		}
		
		String key = args[DELETE_KEY_INDEX];
		try {
			int responseCode = s3client.deleteObject(key);
			System.out.println("DELETE operation completed with response " + Integer.toString(responseCode));
		} catch (Exception e) {
			System.out.println("Delete operation failed");
			e.printStackTrace();
		}
	}
	
	public static S3RESTHttpClient initialiseClientFromArgs(String[] args){
		String apiUrl = args[API_URL_INDEX];
		String apiKey = args[API_KEY_INDEX];
		String secretKey = args[SECRETKEY_INDEX];
		String container = args[CONTAINER_INDEX];
		System.out.println("Initialising client for container " + container);
		S3RESTHttpClient s3client = new S3RESTHttpClientImpl();
		s3client.setS3ApiUrl(apiUrl);
		s3client.setApiKey(apiKey);
		s3client.setSecretKey(secretKey);
		s3client.setBucket(container);
		return s3client;
	}
	
	public static void printUsageInfo(String error){
		if(error != null){
			System.out.println(error);
		}
		System.out.println("Usage: Command line args -  ");
		System.out.println(EXISTS_USAGE_MESSAGE);
		System.out.println(PUT_USAGE_MESSAGE);
		System.out.println(GET_USAGE_MESSAGE);
		System.out.println(DELETE_USAGE_MESSAGE);	
	}

	public static void manuallyInitialiseLog4j(){
		
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		ConsoleAppender console = new ConsoleAppender(); //create appender
		//configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.FATAL);
		console.activateOptions();
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);
		
	}
}
