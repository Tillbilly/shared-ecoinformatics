package au.edu.aekos.shared.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;

import org.junit.Test;

public class SharedFileUtilsTest {

	/**
	 * Can we handle zero
	 */
	@Test
	public void testHumanReadableBytes01() {
		String result = SharedFileUtils.humanReadableByteCount(0, true);
		assertEquals("0 B", result);
	}
	
	/**
	 * Can we tell when a file is older or newer than a specified time?
	 */
	@Test
	public void testFileAgeTester(){
		File file = new File("src/test/resources/log4j.xml");
		if(! file.exists() ){
			fail();
		}
		long twoHoursAgo = new Date().getTime() - (60 * 60 * 2 * 1000);
		file.setLastModified(twoHoursAgo);
		assertTrue(SharedFileUtils.isFileOlderThan(file, 1));
		assertFalse(SharedFileUtils.isFileOlderThan(file, 436800)); //50 years ago
	}

}
