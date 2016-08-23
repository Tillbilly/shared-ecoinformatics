package au.edu.aekos.shared.service.quest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest {
	@Test
	public void testRegexpSearchTermHighlighting(){
		String term = "ac";
		term = addUpperAndLowerFirstCharSearchTermCharacterOption(term);
		Pattern p = Pattern.compile("(.*?)("+term+")(.*?)");
		Matcher matcher = p.matcher("<em>Acacia studious</em>");
		if(matcher.matches() && matcher.groupCount() == 3 ){
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			String newString = matcher.group(1) + "<strong>" + matcher.group(2) + "</strong>" + matcher.group(3);
			Assert.assertEquals("<em><strong>Ac</strong>acia studious</em>", newString);
			return;
		}
		Assert.fail();
	}
	
	public String addUpperAndLowerFirstCharSearchTermCharacterOption(String term){
	    char c = term.charAt(0);
		if(! Character.isLetter(c)){
			return term;
		}
		char [] charArray = new char[4];
		charArray[0] = '[';
		charArray[3] = ']';
		if(Character.isUpperCase(c)){
			charArray[1] = c;
			charArray[2] = Character.toLowerCase(c);
		}
		else{
			charArray[1] = Character.toUpperCase(c);
			charArray[2] = c;
		}
		return String.copyValueOf(charArray) + term.substring(1);
	}
	
	
	
}
