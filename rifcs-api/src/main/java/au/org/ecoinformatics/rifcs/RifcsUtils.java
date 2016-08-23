package au.org.ecoinformatics.rifcs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RifcsUtils {

	private static final String W3CDTF_DATE_FORMAT = "yyyy-MM-dd";
	public static final String UNKNOWN_ASCII_CHAR = "_?";
	
	public static String convertDateToW3CDTF(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(W3CDTF_DATE_FORMAT);
		return sdf.format(date);
	}
	
	public static String convertDateToW3CDTFYearOnly(Date date) {
		return convertDateToW3CDTF(date).substring(0, 4);
	}

    public static String asciiCleanse(String inputString){
    	Map<Character,String> charSubMap = new HashMap<Character, String>();
    	charSubMap.put(new Character('\u00C6'), "AE");
		charSubMap.put(new Character('\u0092'), "'");
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
