package au.edu.aekos.shared.admin;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Acts as a shim so we can get access to the properties and easily list them all without
 * having to know the keys.
 */
public class ReadablePropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(ReadablePropertyPlaceholderConfigurer.class);
	
	public Properties getSharedProperties(){
		Properties result = new Properties();
		try {
			loadProperties(result);
			return result;
		} catch (IOException e) {
			logger.error("Error reading properties file(s). Returning an empty properties file instead.", e);
			result.put("failed.to.read.properties", true);
			return result;
		}
	}
}
