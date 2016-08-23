package au.edu.aekos.shared.geotools;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import au.edu.aekos.shared.spatial.GeometryReprojectionService;
import au.edu.aekos.shared.spatial.GeometryReprojectionServiceImpl;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


/**
 * Going to try and get some love out of geotools reprojection suite - 
 * 
 * Required to reproject spatial BBOX and site files for indexing / portal display
 * 
 * Our standard projection is EPSG:4283   -  lon lat GDA94 datum
 * 
 * @author btill
 */
public class GeotoolsReprojectionTest {
	
	@Test
	public void testSomeGeotoolsBasics() throws ParseException, NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException{
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		Point point = (Point) reader.read("POINT (138.2345 -37.0)" );
		Coordinate c = point.getCoordinate();
		Assert.assertEquals(138.2345, c.getOrdinate(Coordinate.X), 0.000001 );
		Assert.assertEquals(-37.0, c.getOrdinate(Coordinate.Y), 0.0000001 );
		
		CoordinateReferenceSystem crs1 = CRS.decode("EPSG:28354",true);
		CoordinateReferenceSystem crs2 = CRS.decode("EPSG:4283", true);
		Assert.assertNotNull(crs1);
		Assert.assertNotNull(crs2);
        boolean lenient = true; // allow for some error due to different datums
        MathTransform transform = CRS.findMathTransform(crs1, crs2, lenient);
        Point adelaideZone54Point = (Point) reader.read("POINT (280876 6132203)" );
        Point gda94Point = (Point) JTS.transform(adelaideZone54Point, transform);
        Assert.assertNotNull(gda94Point);
        System.out.println(gda94Point.toText());
        
        try{
            CRS.decode("EPSG:900913",true);
            Assert.fail();
        }catch(NoSuchAuthorityCodeException ex){
        	// success!
        }
        
        CoordinateReferenceSystem crsGoogle = CRS.decode("EPSG:3857",true);
        MathTransform transform2 = CRS.findMathTransform(crs2, crsGoogle, lenient);
        Point googlePoint = (Point) JTS.transform(gda94Point, transform2);
        Assert.assertNotNull(googlePoint);
        System.out.println(googlePoint.toText());
        
        MathTransform transform3 = CRS.findMathTransform(crsGoogle, crs1, lenient);
        Point orig = (Point) JTS.transform(googlePoint, transform3);
        System.out.println(orig.toText());
        
        Assert.assertEquals(280876.0, orig.getX(), 0.01);
        Assert.assertEquals(6132203.0, orig.getY(), 0.01);
        
	}
	
	@Test
	public void testGeometryReprojectionService() throws ParseException{
		GeometryReprojectionService grs = new GeometryReprojectionServiceImpl();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		Point adelaideZone54Point = (Point) reader.read("POINT (280876 6132203)" );
		List<String> wktList = new ArrayList<String>();
		wktList.add(adelaideZone54Point.toText());
		List<String> reprojectedWktList = grs.reprojectWktPointsToEPSG4283(wktList, "EPSG:28354", 6);
		System.out.println("*****");
		System.out.println(reprojectedWktList.get(0));
		System.out.println("*****");
	}
	
	@Test
	public void testGeometryReprojectionService_Poly() throws ParseException{
		GeometryReprojectionService grs = new GeometryReprojectionServiceImpl();
		String wktPoly54 = "POLYGON((280876 6132203,280880 6132203,280880 6132213,280876 6132213,280876 6132203))";
		String reprojected = grs.reprojectWktPolygonToEPSG4283(wktPoly54, "EPSG:28354", 6);
		Assert.assertNotNull(reprojected);
		System.out.println(reprojected);
		System.out.println("*****");
		
		//Test now with EPSG:4283 but fixing decimal precision
		String polyTest = "POLYGON((138.123456666 -35.0000000001,138.1234564999 -34.0000000001,138.12340000 -34.0000000001,138.123456666 -35.0000000001))";
		Assert.assertEquals("POLYGON((138.123457 -35,138.123456 -34,138.1234 -34,138.123457 -35))".replaceAll(" ", ""), grs.reprojectWktPolygonToEPSG4283(polyTest, "EPSG:4283", 6).replaceAll(" ", ""));
	}
}
