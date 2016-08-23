package au.edu.aekos.shared.web.util;

import org.junit.Assert;

import org.junit.Test;

public class SharedUrlUtilsTest {

	@Test
	public void testRetrieveUsernameFromEmailAddress(){
		Assert.assertEquals("bob.jane", SharedUrlUtils.retrieveUsernameFromEmailAddress("bob.jane@tmart.com"));
	}
	
	@Test
	public void testGetHostname(){
		String hostname = SharedUrlUtils.getHostname();
		Assert.assertNotNull(hostname);
		System.out.println(hostname);
	}
}
