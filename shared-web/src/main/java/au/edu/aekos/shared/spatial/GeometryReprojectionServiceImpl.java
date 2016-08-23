package au.edu.aekos.shared.spatial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


@Service
public class GeometryReprojectionServiceImpl implements GeometryReprojectionService {
	
	private Logger logger = LoggerFactory.getLogger(GeometryReprojectionServiceImpl.class);

	public static final String EPSG4283 = "EPSG:4283";
	
	@Override
	public List<String> reprojectWktPointsToEPSG4283(List<String> wktPointList,
			String fromProjectionCode, int decimalPlaces) {
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		List<String> reprojectedWktList = new ArrayList<String>();
		for(String wktGeom : wktPointList){
			try{
			    Point point = (Point) reader.read(wktGeom);
			    Point rprPoint = (Point) reprojectGeometry(point, fromProjectionCode, EPSG4283 );
			    fixGeometryPrecision(rprPoint, decimalPlaces);
			    reprojectedWktList.add(rprPoint.toText());
			}catch(ParseException pex){
				logger.error("Error occured trying to reproject " + wktGeom +" from crs " + fromProjectionCode , pex);
			} catch (MismatchedDimensionException e) {
				logger.error("Error occured trying to reproject " + wktGeom +" from crs " + fromProjectionCode , e);
			} catch (NoSuchAuthorityCodeException e) {
				logger.error("Error occured trying to reproject " + wktGeom +" from crs " + fromProjectionCode , e);
			} catch (FactoryException e) {
				logger.error("Error occured trying to reproject " + wktGeom +" from crs " + fromProjectionCode , e);
			} catch (TransformException e) {
				logger.error("Error occured trying to reproject " + wktGeom +" from crs " + fromProjectionCode , e);
			}
		}
		return reprojectedWktList;
	}
	
	public Geometry reprojectGeometry(Geometry geom, String epsgCodeFrom,  String epsgCodeTo) throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException{
		CoordinateReferenceSystem crs1 = CRS.decode(epsgCodeFrom,true);
		CoordinateReferenceSystem crs2 = CRS.decode(epsgCodeTo, true);
		MathTransform transform = CRS.findMathTransform(crs1, crs2, true);
		return JTS.transform(geom, transform);
	}
	
	public void fixGeometryPrecision(Geometry geom, int numDecimalPlaces){
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

	@Override
	public String reprojectWktPolygonToEPSG4283(String wktPoly, String fromProjectionCode, int decimalPlaces) {
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		try{
		    Polygon poly = (Polygon) reader.read(wktPoly);
		    if(! EPSG4283.equalsIgnoreCase(fromProjectionCode)){
		        poly = (Polygon) reprojectGeometry(poly, fromProjectionCode, EPSG4283 );
		    }
		    fixGeometryPrecision(poly, decimalPlaces);
		    return poly.toText();
		}catch(ParseException pex){
			logger.error("Error occured trying to reproject " + wktPoly +" from crs " + fromProjectionCode , pex);
		} catch (MismatchedDimensionException e) {
			logger.error("Error occured trying to reproject " + wktPoly +" from crs " + fromProjectionCode , e);
		} catch (NoSuchAuthorityCodeException e) {
			logger.error("Error occured trying to reproject " + wktPoly +" from crs " + fromProjectionCode , e);
		} catch (FactoryException e) {
			logger.error("Error occured trying to reproject " + wktPoly +" from crs " + fromProjectionCode , e);
		} catch (TransformException e) {
			logger.error("Error occured trying to reproject " + wktPoly +" from crs " + fromProjectionCode , e);
		}
		return null;
	}

	@Override
	public BBOX reprojectBBOXToEPSG4283(BBOX bbox, String fromProjectionCode,
			int decimalPlaces) {
		
		Coordinate minCoord = new Coordinate(bbox.getXmin(), bbox.getYmin());
		GeometryFactory gf =JTSFactoryFinder.getGeometryFactory();
		try {
			Point minPoint = gf.createPoint(minCoord);
			Point minRprPoint = (Point) reprojectGeometry(minPoint, fromProjectionCode, EPSG4283 );
		    fixGeometryPrecision(minRprPoint, decimalPlaces);
			Coordinate maxCoord = new Coordinate(bbox.getXmax(), bbox.getYmax());
			Point maxPoint = gf.createPoint(maxCoord);
			Point maxRprPoint = (Point) reprojectGeometry(maxPoint, fromProjectionCode, EPSG4283 );
		    fixGeometryPrecision(minRprPoint, decimalPlaces);
		    BBOX reprojectedBbox = new BBOX(
			    minRprPoint.getCoordinate().getOrdinate(Coordinate.X),
			    minRprPoint.getCoordinate().getOrdinate(Coordinate.Y),
			    maxRprPoint.getCoordinate().getOrdinate(Coordinate.X),
			    maxRprPoint.getCoordinate().getOrdinate(Coordinate.Y) );
		    return reprojectedBbox;
		
		} catch (MismatchedDimensionException e) {
			logger.error("Error occured trying to reproject " + bbox.getWktPolygon() +" from crs " + fromProjectionCode , e);
		} catch (NoSuchAuthorityCodeException e) {
			logger.error("Error occured trying to reproject " + bbox.getWktPolygon() +" from crs " + fromProjectionCode , e);
		} catch (FactoryException e) {
			logger.error("Error occured trying to reproject " + bbox.getWktPolygon() +" from crs " + fromProjectionCode , e);
		} catch (TransformException e) {
			logger.error("Error occured trying to reproject " + bbox.getWktPolygon() +" from crs " + fromProjectionCode , e);
		}
		
		return null;
	}
}
