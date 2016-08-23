package au.edu.aekos.shared.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test is an exploration on dealing with strange unicode characters from windows cp1252
 * and conversion to utf-8 and html encoding.
 * 
 * When people paste a block of text into the dataset or project description fields,
 * first the browser does its best to display the character, including possible conversion.  
 * Conversion may occur from the http post stream into java, then again when writing to the db.
 * 
 * We generally specify UTF-8 in our xml - if someone validates strictly when consuming the xml, 
 * and it contains unicode characters,their parsing may fail ( TERN Data Citation Service )
 * 
 * Obviously we need to identify and replace the strange character, this class explores the probelm space
 * 
 * @author btill
 */
public class CharacterSetExplorationTest {
	
	//Unicode Character 'PRIVATE USE TWO' (U+0092) - FileFormat.
	@Test
	public void testUnicodeCharacterDetection() throws CharacterCodingException{
		char uc = '\u0092';
		
		char [] ucString = {'b','a','\u0092','d',' ','\'' };
		
		String stringWithUc = "Fred Tom \u0092";
		
//		Charset iso8859 = StandardCharsets.ISO_8859_1 ;
//		Charset ascii = StandardCharsets.US_ASCII ;
		Charset utf8 = StandardCharsets.UTF_8;
		
		Assert.assertTrue(utf8.canEncode() );
		
		System.out.println(Charset.defaultCharset()); 
		// ( x-MacRoman -- read about stuff here :http://stackoverflow.com/questions/10369014/how-to-get-tomcat-to-understand-macroman-x-mac-roman-charset-from-my-mac-keybo
		
		System.out.println(ucString);
		System.out.println(stringWithUc);
		
		CharsetDecoder utf8Decoder = utf8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
		ByteBuffer bb = ByteBuffer.wrap(stringWithUc.getBytes(utf8));
		CharBuffer cb = utf8Decoder.decode(bb);
		System.out.println(cb.toString());
		checkCharacterEncoding(ucString);
		
		char [] aeString = {uc,'k','o','d','s' };
		checkCharacterEncoding(aeString);
		
	}
	
	private static void checkCharacterEncoding(char [] charArray){
	    CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
	    CharsetEncoder utf8Encoder = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
	    CharsetEncoder defaultCSEncoder = Charset.defaultCharset().newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
	    CharsetEncoder iso8859Encoder = StandardCharsets.ISO_8859_1.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
	    for(int x = 0; x < charArray.length ; x++){
			if(! asciiEncoder.canEncode(charArray[x]) ){
				System.out.println("Found character that can't be ascii encoded at position " + Integer.toString(x));
			}
			if(! utf8Encoder.canEncode(charArray[x]) ){
				System.out.println("Found character that can't be utf8 encoded at position " + Integer.toString(x));
			}
			if(! defaultCSEncoder.canEncode(charArray[x]) ){
				System.out.println("Found character that can't be default charset ( x-MacRoman on Mac java) encoded at position " + Integer.toString(x));
			}
			if(! iso8859Encoder.canEncode(charArray[x]) ){
				System.out.println("Found character that can't be iso8859 encoded at position " + Integer.toString(x));
			}
		}
	}
	
	public static final String xmlExample = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><resource xsi:schemaLocation=\"http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd\" xmlns=\"http://datacite.org/schema/kernel-2.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><identifier identifierType=\"DOI\"/><creators><creator><creatorName>Till, Benjamin \u0092</creatorName></creator></creators><titles><title>Test</title></titles><publisher>AEKOS</publisher><publicationYear>2009</publicationYear><subjects><subject>501</subject></subjects><descriptions><description descriptionType=\"Abstract\">All about my \u00C6 dataset its totes wicked doods \u1046</description></descriptions></resource>";
	
	public static final String xmlExampleResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><resource xsi:schemaLocation=\"http://datacite.org/schema/kernel-2.1 http://schema.datacite.org/meta/kernel-2.1/metadata.xsd\" xmlns=\"http://datacite.org/schema/kernel-2.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><identifier identifierType=\"DOI\"/><creators><creator><creatorName>Till, Benjamin '</creatorName></creator></creators><titles><title>Test</title></titles><publisher>AEKOS</publisher><publicationYear>2009</publicationYear><subjects><subject>501</subject></subjects><descriptions><description descriptionType=\"Abstract\">All about my AE dataset its totes wicked doods _?</description></descriptions></resource>";
	
	private static final Map<Character,String> charReplacementMap = new HashMap<Character, String>();
	
	{
		charReplacementMap.put(new Character('\u00C6'), "AE");
		charReplacementMap.put(new Character('\u0092'), "'");
	}
	
	@Test
	public void checkForUnicodeAndSubstituteReplace(){
		CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
		char [] xmlCharArray = xmlExample.toCharArray();
		StringBuilder output = new StringBuilder();
		for(int x = 0; x < xmlCharArray.length; x++){
			char ch = xmlCharArray[x];
			if(! asciiEncoder.canEncode(ch)){
				Character lookupChar = new Character(ch);
				if( charReplacementMap.containsKey(lookupChar)){
					output.append(charReplacementMap.get(lookupChar));
				}else{
					output.append("_?");
				}
			}else{
				output.append(ch);
			}
		}
		Assert.assertEquals(xmlExampleResult, output.toString());
	}
	
	@Test
	public void testAsciiCleanse(){
		String result = asciiCleanse(xmlExample, "_?", charReplacementMap);
		Assert.assertEquals(xmlExampleResult, result);
	}
	
   
	
	private static String asciiCleanse(String inputString, String unknownCharReplacement,  Map<Character,String> charReplacementMap){
		CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
		StringBuilder output = new StringBuilder();
		for(int x = 0; x < inputString.length(); x++){
			char ch = inputString.charAt(x);
			if(! asciiEncoder.canEncode(ch)){
				Character lookupChar = new Character(ch);
				if( charReplacementMap.containsKey(lookupChar)){
					output.append(charReplacementMap.get(lookupChar));
				}else{
					output.append(unknownCharReplacement);
				}
			}else{
				output.append(ch);
			}
		}
		return output.toString();
	}
	
	@Test
	public void testAsciiCleanse2(){
		String result = asciiCleanse2(xmlExample);
		Assert.assertEquals(xmlExampleResult, result);
	}
	
	private static final Map<Character,String> charSubMap = new HashMap<Character, String>();
		
		{
			charSubMap.put(new Character('\u00C6'), "AE");
			charSubMap.put(new Character('\u0092'), "'");
		}
	
	private static final String UNKNOWN_ASCII_CHAR = "_?";
	
	private static String asciiCleanse2(String inputString ){
		CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
		StringBuilder output = new StringBuilder();
		for(int x = 0; x < inputString.length(); x++){
			char ch = inputString.charAt(x);
			if(! asciiEncoder.canEncode(ch)){
				Character lookupChar = new Character(ch);
				if( charSubMap.containsKey(lookupChar)){
					output.append(charSubMap.get(lookupChar));
				}else{
					output.append(UNKNOWN_ASCII_CHAR);
				}
			}else{
				output.append(ch);
			}
		}
		return output.toString();
	}
	

}
