package au.edu.aekos.shared.geotools;

import org.junit.Assert;

import org.junit.Test;

import au.edu.aekos.shared.spatial.BBOX;

public class BBOXWktTest {

	@Test
	public void testBBOXWktStringGeneration(){
		Double minX = Double.valueOf(10.0);
		Double minY = Double.valueOf(-20.0);
		Double maxX = Double.valueOf(20.0);
		Double maxY = Double.valueOf(-10.0);
		
		BBOX bbox = new BBOX(minX,minY,maxX,maxY);
		Assert.assertEquals("POLYGON((10.0 -20.0,20.0 -20.0,20.0 -10.0,10.0 -10.0,10.0 -20.0))", bbox.getWktPolygon());
		
	}
	
}
