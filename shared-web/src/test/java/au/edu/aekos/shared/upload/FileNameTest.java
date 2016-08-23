package au.edu.aekos.shared.upload;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.edu.aekos.shared.web.util.SharedFileUtils;

public class FileNameTest {

	@Test
	public void testIncrementName(){
		String fileName = "fred.jpg";
		int increment = 1;
		String incremented = SharedFileUtils.addIncrementToFileName(fileName, increment);
		assertEquals("fred-1.jpg", incremented);
	}
}
