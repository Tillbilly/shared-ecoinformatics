package au.edu.aekos.shared.data.entity;

import static org.junit.Assert.*;

import org.junit.Test;

public class SharedUserTest {

	private static final String UNIX_NEWLINE = "\n";
	private static final String WINDOWS_NEWLINE = "\r\n";

	@Test
	public void testGetCleanedPostalAddress01() {
		SharedUser objectUnderTest = new SharedUser();
		objectUnderTest.setPostalAddress("123 Fake St"+WINDOWS_NEWLINE+"Fakesville"+WINDOWS_NEWLINE+"SA 5000");
		String result = objectUnderTest.getCleanedPostalAddress();
		assertEquals("123 Fake St, Fakesville, SA 5000", result);
	}
	
	@Test
	public void testGetCleanedPostalAddress02() {
		SharedUser objectUnderTest = new SharedUser();
		objectUnderTest.setPostalAddress("123 Fake St"+UNIX_NEWLINE+"Fakesville"+UNIX_NEWLINE+"SA 5000");
		String result = objectUnderTest.getCleanedPostalAddress();
		assertEquals("123 Fake St, Fakesville, SA 5000", result);
	}
}
