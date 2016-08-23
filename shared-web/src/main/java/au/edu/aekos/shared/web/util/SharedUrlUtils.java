package au.edu.aekos.shared.web.util;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class SharedUrlUtils {

	/**
	 * Used to construct the base URLs to send out for rejected submissions,
	 * for the user to click back in on.
	 * @param request
	 * @return
	 */
	public static String constructBaseServletUrl(HttpServletRequest request){
		String baseUrl = request.getScheme() + "://" + request.getServerName();
		Integer port = request.getServerPort();
		if(port != null){
			baseUrl += ":" + port.toString();
		}
		return baseUrl + request.getContextPath();
	}
	
	public static String retrieveTopLevelAddressFromUrlString(String url){
		Pattern p = Pattern.compile("(http://)*(\\S+?)(:|/)+.*");
		Matcher m = p.matcher(url);
		if(m.matches()){
			return m.group(2);
		}
		return null;
	}
	
	public static String retrievePortFromUrlString(String url){
		if(! url.contains(":")){
			return "80";
		}
		Pattern p = Pattern.compile(".+?:(\\d+).*");
		Matcher m = p.matcher(url);
		if(m.matches()){
			return m.group(1);
		}
		return "80";
	}
	
	//Returns the part of an email address before the '@'
	//just returns the inputString if no @ present, or the String is null
	//its a bit defensive, but its late in the day . . .
	public static String retrieveUsernameFromEmailAddress(String emailAddress){
		if(StringUtils.hasLength(emailAddress)){
			int atIndx = emailAddress.indexOf('@');
			if(atIndx < 0){
				return emailAddress;
			}
			return emailAddress.substring(0, atIndx);
		}
		return emailAddress;
	}
	
	public static String getHostname(){
		String hostname = "";
		try{
		    hostname = InetAddress.getLocalHost().getHostName();
		}catch(Exception ex){
			//Don't really need to do anything
		}
		return hostname;
	}
	
	
}
