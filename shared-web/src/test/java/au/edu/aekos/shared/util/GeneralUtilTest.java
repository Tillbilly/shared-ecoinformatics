package au.edu.aekos.shared.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import au.edu.aekos.shared.service.index.SpeciesFileWriter;
import au.edu.aekos.shared.service.quest.ReusableGroupServiceImpl;
import au.edu.aekos.shared.web.util.SharedUrlUtils;


public class GeneralUtilTest {
	
	@Test
	public void testUUIDRandomKeyGenerator(){
		String randomKey = UUID.randomUUID().toString();
		System.out.println(randomKey);
		String randomKey2 = UUID.randomUUID().toString();
		System.out.println(randomKey2);
		assertFalse(randomKey.equals(randomKey2));
		
	}

	
	@Test
	public void testListStringContainsMethod(){
		String toTest = new String("toTest");
		
		List<String> testList = new ArrayList<String>();
		testList.add(new String("FRED"));
		testList.add(new String("toTest"));
		testList.add(new String("blerr"));
		
		assertTrue(testList.contains(toTest));
	}
	
	@Test
	public void testRegexpAndConstructTitleIncrement(){
		String response = ReusableGroupServiceImpl.incrementReusableGroupTitle("test");
		assertEquals("test (1)", response);
		
		response = ReusableGroupServiceImpl.incrementReusableGroupTitle("test (1)");
		assertEquals("test (2)", response);
		
		response = ReusableGroupServiceImpl.incrementReusableGroupTitle("test (9)");
		assertEquals("test (10)", response);
	}
	
	@Test
	public void testFakepathFilePathRemovalForFileName(){
		String filepathFull = "C:\\fakepath\\sites-test.csv";
		String fileNm =  filepathFull.substring( filepathFull.lastIndexOf("\\") + 1 );
		assertEquals("sites-test.csv", fileNm );
	}
	
	@Test
	public void testAddingNullPlaceholdersToArrayList(){
		List<String> stringList = new ArrayList<String>();
		
		try{
		    stringList.add(1, "test");
		    fail();
		}catch(java.lang.IndexOutOfBoundsException e){
			
		}
		//Add a null value add 0
		stringList.add(0, null);
		stringList.add(1, "test");
		
	}
	
	@Test
	public void parsingACSVLineWithCommas(){
		String str = "myvocab,123,\"Hello, I love you, won't you tell me your name?\"";
		//with substrings probably easiest
		int firstCommaIndex = str.indexOf(",");
		int secondCommaIndex = str.indexOf(",", firstCommaIndex + 1 );
		String firstPiece = str.substring(0,firstCommaIndex);
		assertEquals("myvocab", firstPiece);
		String secondPiece = str.substring(firstCommaIndex + 1,secondCommaIndex);
		assertEquals("123", secondPiece);
		String thirdPiece = str.substring(secondCommaIndex + 1);
		assertEquals("\"Hello, I love you, won't you tell me your name?\"", thirdPiece );
		thirdPiece = thirdPiece.replaceAll("\"", "");
		assertEquals("Hello, I love you, won't you tell me your name?", thirdPiece );
		
	}
	
	@Test
	public void parsingACSVLineWithCommasAndQuotes(){
		String str = "myvocab,123,\"Hello, I love you, won't you tell me your name?\",\"Hello, I love you, won't you tell me your name?\"";
		//with substrings probably easiest
		int firstCommaIndex = getNextIndexOfCSVComma(str, -1 );
		int secondCommaIndex = getNextIndexOfCSVComma(str, firstCommaIndex );
		int thirdCommaIndex = getNextIndexOfCSVComma(str, secondCommaIndex );
		
		String firstPiece = str.substring(0,firstCommaIndex);
		String secondPiece = str.substring(firstCommaIndex + 1,secondCommaIndex);
		String thirdPiece = str.substring(secondCommaIndex + 1,thirdCommaIndex);
		String fourthPiece = str.substring(thirdCommaIndex + 1);
		assertEquals("myvocab", firstPiece);
		assertEquals("123", secondPiece);
		assertEquals("\"Hello, I love you, won't you tell me your name?\"", thirdPiece );
		thirdPiece = thirdPiece.replaceAll("\"", "");
		assertEquals("Hello, I love you, won't you tell me your name?", thirdPiece );
		assertEquals("\"Hello, I love you, won't you tell me your name?\"", fourthPiece );
		fourthPiece = fourthPiece.replaceAll("\"", "");
		assertEquals("Hello, I love you, won't you tell me your name?", fourthPiece );
		
		//Lets do a parse for no quotes
//		String str2 = "myvocab,123,Hello I love you won't you tell me your name?,Hello, I love you won't you tell me your name?";
	}
	

	@Test
	public void parsingACSVLineWithCommasAndQuotes2(){
		String str = "myvocab,123,Hello I love you won't you tell me your name?,Hello I love you won't you tell me your name?";
		//with substrings probably easiest
		int firstCommaIndex = getNextIndexOfCSVComma(str, -1 );
		int secondCommaIndex = getNextIndexOfCSVComma(str, firstCommaIndex );
		int thirdCommaIndex = getNextIndexOfCSVComma(str, secondCommaIndex );
		
		String firstPiece = str.substring(0,firstCommaIndex);
		String secondPiece = str.substring(firstCommaIndex + 1,secondCommaIndex);
		String thirdPiece = str.substring(secondCommaIndex + 1,thirdCommaIndex);
		String fourthPiece = str.substring(thirdCommaIndex + 1);
		assertEquals("myvocab", firstPiece);
		assertEquals("123", secondPiece);
		assertEquals("Hello I love you won't you tell me your name?", thirdPiece);
		assertEquals("Hello I love you won't you tell me your name?", fourthPiece);
	}
	
	
	/**
	 * To start at the start of the String, set lastCommaIndex to -1
	 * 
	 * If there is no more comma`s, -1 is returned 
	 * 
	 * @param str
	 * @param lastCommaIndex
	 * @return
	 */
	private int getNextIndexOfCSVComma(String str, int lastCommaIndex ){
		if(str.charAt(lastCommaIndex + 1) == '\"'){
			return str.indexOf("\",", lastCommaIndex + 1 ) + 1;			
		}else{
			return str.indexOf(",", lastCommaIndex + 1 ) ;
		}
	}
	
	@Test  //Tests whether you can add objects to an ArrayList at any index
	public void insertAtIndexWithGapsArrayList(){
		List<String> stringArrayList = new ArrayList<String>();
		try{
		    stringArrayList.add(1, "Fred");
		    fail();
		}catch(Exception e){
			
		}
		stringArrayList = new ArrayList<String>();
		stringArrayList.add(0,null);
		stringArrayList.add(1,"fred");
	}
	
	@Test
	public void testPrintFormatForDoubles(){
		Double d = new Double(234.3456789);
		assertEquals("234.35", String.format("%.2f", d) );
		assertEquals("234.34567890", String.format("%.8f", d) );
		
		Double d2 = new Double(-234.3456789);
		assertEquals("-234.35", String.format("%.2f", d2) );
	}
	
	@Test
	public void testSharedUrlUtils(){
		assertEquals("123.456.789.112",SharedUrlUtils.retrieveTopLevelAddressFromUrlString("http://123.456.789.112:8080/solr") );
		assertEquals("123.456.789.112",SharedUrlUtils.retrieveTopLevelAddressFromUrlString("123.456.789.112:8080/solr") );
		assertEquals("123.456.789.112",SharedUrlUtils.retrieveTopLevelAddressFromUrlString("123.456.789.112/solr") );
		assertEquals("123.456.789.112",SharedUrlUtils.retrieveTopLevelAddressFromUrlString("http://123.456.789.112/solr") );
		
		assertEquals("8080",SharedUrlUtils.retrievePortFromUrlString("http://123.456.789.112:8080/solr") );
		assertEquals("8081",SharedUrlUtils.retrievePortFromUrlString("123.456.789.112:8081/solr") );
		assertEquals("80",SharedUrlUtils.retrievePortFromUrlString("123.456.789.112/solr") );
		
	}
	
	@Test
	public void testCreateAndCapitaliseFirstToken(){
	    assertEquals("Fred", SpeciesFileWriter.createAndCapitaliseFirstToken("fred dogg"));
	    assertEquals(null, SpeciesFileWriter.createAndCapitaliseFirstToken(" "));
	    assertEquals("Jason", SpeciesFileWriter.createAndCapitaliseFirstToken("JASON BIGGS JONES"));
	    assertEquals("Jason", SpeciesFileWriter.createAndCapitaliseFirstToken("JASON"));
	
	}
	
	@Test
	public void testPrefixGrabber(){
		String fieldName = "imageFile_s";
		assertEquals("imageFile", fieldName.substring(0, fieldName.indexOf('_') ) );
	}
	
	@Test
	public void testTitleCleanUp(){
		String title = "SleepyLizards: Fred \u0096 dogg";
		assertEquals("SleepyLizardsFreddogg", title.replaceAll("\\W", ""));
		
	}
	
	
//    private String incrementReusableGroupTitle(String originalTitle){
//		
//		
//		
//		
//		return originalTitle;
//	}
}
