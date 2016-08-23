package au.edu.aekos.shared.geotools;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Assert;
import org.junit.Test;

import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.spatial4j.core.shape.Shape;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import au.edu.aekos.shared.spatial.ConvexHullUtil;

public class ConvexHullUtilTest {
	
	private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	
	/**
	 * Can we create a convex hull with lots of points?
	 */
	@Test
	public void testToConvexHull01(){
		Coordinate [] coordinateArray = {
				new Coordinate(1.0, 2.5),
				new Coordinate(4.0, -2.5),
				new Coordinate(7.0, -3.5),
				new Coordinate(1.0, 0.5),
				new Coordinate(4.0, 2.5),
				new Coordinate(4.5, 3.0),
				new Coordinate(5.0, 2.5),
				new Coordinate(8.0, 8.5)
		};
		ConvexHull objectUnderTest = new ConvexHull(coordinateArray, geometryFactory);
		Geometry result = objectUnderTest.getConvexHull();
		assertThat(result.toText(), is("POLYGON ((7 -3.5, 4 -2.5, 1 0.5, 1 2.5, 8 8.5, 7 -3.5))"));
	}
	
	/**
	 * Can we create a convex hull with 1 point?
	 */
	@Test
	public void testToConvexHull02(){
		Coordinate c = new Coordinate(1.0, 2.5);
		Coordinate [] ca2 = { c };
		ConvexHull objectUnderTest = new ConvexHull(ca2, geometryFactory);
		Geometry result = objectUnderTest.getConvexHull();
		assertThat(result.toText(), is("POINT (1 2.5)"));
	}
	
	/**
	 * Can we create a convex hull with 2 points?
	 */
	@Test
	public void testConvexHullJTSApi(){
		Coordinate [] ca3 = {
				new Coordinate(1.0, 2.5),
				new Coordinate(2.0, 2.5)
		};
		ConvexHull objectUnderTest = new ConvexHull(ca3, geometryFactory);
		Geometry result = objectUnderTest.getConvexHull();
		assertThat(result.toText(), is("LINESTRING (1 2.5, 2 2.5)"));
	}
	
	/**
	 * Can we create a polygon convex hull from points?
	 */
	@Test
	public void testCalculateConvexHullFromWktPoints01() throws ParseException{
		List<String> pointWktList = new ArrayList<String>();
		pointWktList.add("POINT(123.0 1.0)");
		pointWktList.add("POINT(103.0 -7.0)");
		pointWktList.add("POINT(203.0 -22.0)");
		pointWktList.add("POINT(303.0 440.0)");
		pointWktList.add("POINT(101.0 -4.0)");
		String result = ConvexHullUtil.calculateConvexHullFromWktPoints(pointWktList);
		assertThat(result, is("POLYGON ((203 -22, 103 -7, 101 -4, 303 440, 203 -22))"));
	}
	
	/**
	 * Does adding an invalid element to the WKT list cause the expected exception?
	 */
	@Test
	public void testCalculateConvexHullFromWktPoints02() throws ParseException{
		List<String> pointWktList = new ArrayList<String>();
		pointWktList.add("POINT(123.0 1.0)");
		pointWktList.add("POINT(103.0 -7.0)");
		pointWktList.add("POINT(203.0 -22.0)");
		pointWktList.add("POINT(303.0 440.0)");
		pointWktList.add("POINT(101.0 -4.0)");
		pointWktList.add("Some Nonsense");
		try{
			ConvexHullUtil.calculateConvexHullFromWktPoints(pointWktList);
			fail();
		}catch(ParseException pex){
			// success
		}
	}
	
	/**
	 * Can we convert a group of polygons to a convex hull?
	 */
	@Test
	public void testCalculateConvexHullFromWktGeometryList01() throws ParseException{
		List<String> wktPolyList = new ArrayList<String>();
		wktPolyList.add("POLYGON((138.001 -40.0,139.0 -40.0,200.5 -20.0,138.001 -40.0))");
		wktPolyList.add("POLYGON((138.001 -40.0,136.0 -40.0,200.5 -24.0,138.001 -40.0))");
		wktPolyList.add("POLYGON((138.001 -40.0,132.0 -40.0,200.5 -27.0,138.001 -40.0))");
		wktPolyList.add("POINT(136.001 -30.0)");
		String result = ConvexHullUtil.calculateConvexHullFromWktGeometryList(wktPolyList, false);
		assertThat(result, is("POLYGON ((132 -40, 136.001 -30, 200.5 -20, 200.5 -27, 138.001 -40, 139 -40, 132 -40))"));
	}
	
	/**
	 * Does adding an invalid element to the WKT list cause the expected exception?
	 */
	@Test
	public void testCalculateConvexHullFromWktGeometryList02() throws ParseException{
		List<String> wktPolyList = new ArrayList<String>();
		wktPolyList.add("POLYGON((138.001 -40.0,139.0 -40.0,200.5 -20.0,138.001 -40.0))");
		wktPolyList.add("POLYGON((138.001 -40.0,136.0 -40.0,200.5 -24.0,138.001 -40.0))");
		wktPolyList.add("POLYGON((138.001 -40.0,132.0 -40.0,200.5 -27.0,138.001 -40.0))");
		wktPolyList.add("POINT(136.001 -30.0)");
		wktPolyList.add("BOOBAR");
		try{
			ConvexHullUtil.calculateConvexHullFromWktGeometryList(wktPolyList, false);
			fail();
		}catch(ParseException pex){
			// success
		}
	}
	
	/**
	 * Can the JTS library parse the convex hull we created?
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testCalculateConvexHullFromWktGeometryList03() throws ParseException{
		List<String> pointWktList = new ArrayList<String>();
		pointWktList.add("POINT(123.0 1.0)");
		String result = ConvexHullUtil.calculateConvexHullFromWktGeometryList(pointWktList, false);
		assertThat(result, is("POINT (123 1)"));
		Shape shape = JtsSpatialContext.GEO.readShape(result);
		assertNotNull(shape);
	}
	
	/*
	
	 ERROR: [doc=58991] Error adding field 'wktConvexHull'='POLYGON ((-105.9035347 -57.0674419, -158.110566 -51.5393023, -170.9425972 9.7472733, -171.2941597 43.4172525, -171.2941597 82.3492638, -24.6925972 82.3492638, -24.6925972 43.4172525, 162.3386528 43.0329958, 161.811309 42.646318, -23.9894722 -44.122854, 67.4167778 -44.122854, 55.9909966 -51.4298389, 161.811309 -51.4298389, 162.3386528 -51.5393023, -28.5597847 -57.0674419, -105.9035347 -57.0674419))' msg=found non-noded intersection between LINESTRING ( 254.0964653 -57.0674419, 201.889434 -51.5393023 ) and LINESTRING ( 180.0 -52.11667362290348, 331.4402153 -57.0674419 ) [ (219.55346713184127, -53.4097254713678, NaN) ]
    */
	/*
	POLYGON((-170.9425972 9.7472733,-170.9425972 78.8265516,-27.1535347 78.8265516,-27.1535347 9.7472733,-170.9425972 9.7472733))
	POLYGON((-105.9035347 -57.0674419,-105.9035347 16.5891252,-28.5597847 16.5891252,-28.5597847 -57.0674419,-105.9035347 -57.0674419))
	POLYGON((-23.9894722 -44.122854,-23.9894722 40.2755802,67.4167778 40.2755802,67.4167778 -44.122854,-23.9894722 -44.122854))
	POLYGON((55.9909966 -51.4298389,55.9909966 42.646318,161.811309 42.646318,161.811309 -51.4298389,55.9909966 -51.4298389))
	POLYGON((162.3386528 -51.5393023,162.3386528 43.0329958,-158.110566 43.0329958,-158.110566 -51.5393023,162.3386528 -51.5393023))
	POLYGON((-24.6925972 43.4172525,-24.6925972 82.3492638,-171.2941597 82.3492638,-171.2941597 43.4172525,-24.6925972 43.4172525))
	
	*/
	
	@SuppressWarnings("deprecation")
	@Test
	public void testConvexHullUtilProductionError() throws ParseException{
		List<String> polyWktList = new ArrayList<String>();
		polyWktList.add("POLYGON((-170.9425972 9.7472733,-170.9425972 78.8265516,-27.1535347 78.8265516,-27.1535347 9.7472733,-170.9425972 9.7472733))");
		polyWktList.add("POLYGON((-105.9035347 -57.0674419,-105.9035347 16.5891252,-28.5597847 16.5891252,-28.5597847 -57.0674419,-105.9035347 -57.0674419))");
		polyWktList.add("POLYGON((-23.9894722 -44.122854,-23.9894722 40.2755802,67.4167778 40.2755802,67.4167778 -44.122854,-23.9894722 -44.122854))");
		polyWktList.add("POLYGON((55.9909966 -51.4298389,55.9909966 42.646318,161.811309 42.646318,161.811309 -51.4298389,55.9909966 -51.4298389))");
		polyWktList.add("POLYGON((162.3386528 -51.5393023,162.3386528 43.0329958,-158.110566 43.0329958,-158.110566 -51.5393023,162.3386528 -51.5393023))");
		polyWktList.add("POLYGON((-24.6925972 43.4172525,-24.6925972 82.3492638,-171.2941597 82.3492638,-171.2941597 43.4172525,-24.6925972 43.4172525))");
		
		String convexHullString = ConvexHullUtil.calculateConvexHullFromWktGeometryList(polyWktList, false);
		System.out.println(convexHullString);
		Assert.assertEquals("POLYGON ((-105.9035347 -57.0674419, -158.110566 -51.5393023, -170.9425972 9.7472733, -171.2941597 43.4172525, -171.2941597 82.3492638, -24.6925972 82.3492638, -24.6925972 43.4172525, 162.3386528 43.0329958, 161.811309 42.646318, -23.9894722 -44.122854, 67.4167778 -44.122854, 55.9909966 -51.4298389, 161.811309 -51.4298389, 162.3386528 -51.5393023, -28.5597847 -57.0674419, -105.9035347 -57.0674419))", convexHullString);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		Geometry geom = reader.read(convexHullString);
		Assert.assertTrue(geom.isValid() );
		Assert.assertTrue(geom.isSimple());
		
		//Lessen the precision of the polygon points
		fixGeometryPrecision(geom, 5);
		Geometry gNew = geom.convexHull();
		System.out.println(gNew.toText());
		
		Polygon p = (Polygon) gNew;
		Geometry norm = p.norm();
		System.out.println(norm.toText());
		
		Geometry b = p.getBoundary();
		System.out.println(b.getGeometryType() );
//		LinearRing lr = (LinearRing) b;
		//b.g
		//EdgeNodingValidator env = new EdgeNodingValidator(     );
		
		//Now, see if we get the readShape error from spatialForJ
		boolean errorThrown = false;
		try{
		    JtsSpatialContext.GEO.readShape(convexHullString);
		    Assert.fail();
	    }catch(TopologyException ex){
	    	errorThrown = true;
	    }
		if(errorThrown){
			GeometryFactory geometryFactory2 = JTSFactoryFinder.getGeometryFactory(null);
			new WKTReader(geometryFactory2);
			Geometry geom2 = reader.read(convexHullString);
			String bboxStr = geom2.getEnvelope().toText();
			try{
			    JtsSpatialContext.GEO.readShape(bboxStr);
			}catch(TopologyException ex){
				Assert.fail();
			}
		}
		System.out.println(ConvexHullUtil.calculateConvexHullFromWktGeometryList(polyWktList, true));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSpatialForJError(){
	    try{
		    JtsSpatialContext.GEO.readShape("POLYGON ((" +
		    		"-105.9035347 -57.0674419, -158.110566 -51.5393023" +
		    		", -170.9425972 9.7472733, -171.2941597 43.4172525, " +
		    		"-171.2941597 82.3492638, -24.6925972 82.3492638, " +
		    		"162.3386528 43.0329958, 162.3386528 -51.5393023, " +
		    		"-28.5597847 -57.0674419, -105.9035347 -57.0674419))");
		    fail();
	    }catch(TopologyException ex){
	    	// success!
	    }
	}
	
	private void fixGeometryPrecision(Geometry geom, int numDecimalPlaces){
		geom.apply(new DecimalPrecisionCoordinateSequenceFilter(numDecimalPlaces));
	}
	
	private class DecimalPrecisionCoordinateSequenceFilter implements CoordinateSequenceFilter {

		private int decimalPlacePrecision = 5;
		
		public DecimalPrecisionCoordinateSequenceFilter(int decimalPlacePrecision) {
			super();
			this.decimalPlacePrecision = decimalPlacePrecision;
		}

		@Override
		public void filter(CoordinateSequence seq, int i) {
			double x = seq.getOrdinate(i, Coordinate.X);
			seq.setOrdinate(i, Coordinate.X, setDoublePrecision(x, decimalPlacePrecision));
			double y = seq.getOrdinate(i, Coordinate.Y);
			seq.setOrdinate(i, Coordinate.Y, setDoublePrecision(y, decimalPlacePrecision));
		}

		public double setDoublePrecision(double d, int numDecimalPlaces){
			BigDecimal bd = new BigDecimal(d);
			bd = bd.setScale(numDecimalPlaces, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
		
		@Override
		public boolean isDone() {
			return false;
		}

		@Override
		public boolean isGeometryChanged() {
			return true;
		}
	}
}
