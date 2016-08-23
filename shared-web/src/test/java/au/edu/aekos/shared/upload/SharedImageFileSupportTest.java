package au.edu.aekos.shared.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SharedImageFileSupportTest {

	@Test
	public void testGetFileNameSuffix(){
		String filename = "picture.jpg";
		assertEquals("jpg", SharedImageFileSupport.getFileSuffix(filename) );
		
		filename = "picture";
		assertEquals("", SharedImageFileSupport.getFileSuffix(filename) );
		
		filename = null;
		assertEquals("", SharedImageFileSupport.getFileSuffix(filename) );
		
		filename = "a.b";
		assertEquals("b", SharedImageFileSupport.getFileSuffix(filename) );
	}
	
	@Test
	public void testImageTypeSupported(){
		String filename = "picture.jpg";
		assertTrue(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture.jpeg";
		assertTrue(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture.png";
		assertTrue(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture.gif";
		assertTrue(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture.bmp";
		assertTrue(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture.bmpx";
		assertFalse(SharedImageFileSupport.checkImageTypeSupported(filename));
		filename = "picture";
		assertFalse(SharedImageFileSupport.checkImageTypeSupported(filename));
	}
}
