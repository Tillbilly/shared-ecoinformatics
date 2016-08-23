package au.edu.aekos.shared.geotools;

import org.junit.Assert;
import org.junit.Test;

import au.edu.aekos.shared.spatial.CRSBounds;

public class CRSBoundsConfigTest {
	
	@Test
	public void testCoordinateSystemSupported(){
		Assert.assertTrue(CRSBounds.isCoordSystemSupport("EPSG:4283"));
		Assert.assertTrue(CRSBounds.isCoordSystemSupport("EPSG:28351"));
		Assert.assertFalse(CRSBounds.isCoordSystemSupport("EPSG:28451"));
	}
	
	@Test
	public void testBoundsFor4283(){
		String epsg = "EPSG:4283";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(-179.0, -90.0, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(0.0, 0.0, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(22.0, -33.0, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(180.0, 91.0, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(181.0, 22.0, epsg));
	}
	@Test
	public void testBoundsFor4326(){
		String epsg = "EPSG:4326";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(-179.0, -90.0, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(0.0, 0.0, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(22.0, -33.0, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(180.0, 91.0, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(181.0, 22.0, epsg));
	}
	@Test
	public void testBoundsFor28349(){
		String epsg = "EPSG:28349";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(190000, 6960000, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(800000, 7500000, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28350(){
		String epsg = "EPSG:28350";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(190000, 6960000, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(800000, 7500000, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	
	@Test
	public void testBoundsFor28351(){
		String epsg = "EPSG:28351";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(176000, 6212000, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(824656, 8494511, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28352(){
		String epsg = "EPSG:28352";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(171801, 6400174, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(828199, 8815595, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28353(){
		String epsg = "EPSG:28353";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(171801, 6000791.7731, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(828199, 8815595, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28354(){
		String epsg = "EPSG:28354";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(171173, 5534626, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(828827, 8882020, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28355(){
		String epsg = "EPSG:28355";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(175480, 5112644, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(175460, 5112600, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(175480, 5112600, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(824520, 8483438, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(22, 9000000, epsg));
	}
	@Test
	public void testBoundsFor28356(){
		String epsg = "EPSG:28356";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(189587, 5812135, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(189587, 5812134, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(189586, 5812135, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(810413, 7597371, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(810414, 7597371, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(810413, 7597372, epsg));

	}
	@Test
	public void testBoundsFor28357(){
		String epsg = "EPSG:28357";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(306695, 3923338, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(306695, 3923337, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(306694, 3923338, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(693305, 3934459, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(693305, 3934460, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(693306, 3934459, epsg));
	}
	@Test
	public void testBoundsFor28358(){
		String epsg = "EPSG:28358";
		Assert.assertTrue(CRSBounds.isCoordSystemSupport(epsg) );
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(171072, 3789859, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(171072, 3789858, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(171071, 3789859, epsg));
		Assert.assertTrue(CRSBounds.isCoordinateInBounds(828928, 8893091, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(828929, 8893091, epsg));
		Assert.assertFalse(CRSBounds.isCoordinateInBounds(828928, 8893092, epsg));
	}
	

}
