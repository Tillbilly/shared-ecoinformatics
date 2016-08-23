package au.edu.aekos.shared.spatial;

import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


public class ConvexHullUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConvexHullUtil.class);
	
	public static String calculateConvexHullFromWktPoints(List<String> wktPointList) throws ParseException{
		if(wktPointList == null || wktPointList.size() == 0){
			return null;
		}
		if(wktPointList.size() == 1){
			return wktPointList.get(0);
		}
		Coordinate [] coordArray = build2dCoordinateArrayForPoints(wktPointList);
		ConvexHull convexHull = new ConvexHull(coordArray, JTSFactoryFinder.getGeometryFactory(null));
		Geometry geom = convexHull.getConvexHull();
		return geom.toText();
	}
	
	public static Coordinate [] build2dCoordinateArrayForPoints(List<String> wktPointList) throws ParseException{
		Coordinate [] coordinateArray = new Coordinate[wktPointList.size()];
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		for(int x = 0; x < wktPointList.size(); x++){
			Point point = (Point) reader.read(wktPointList.get(x) );
			Coordinate c = point.getCoordinate();
			coordinateArray[x] = c;
		}
		return coordinateArray;
	}
	
	
	//Sometimes solr4j spits on the convex hull calculation on the solr indexing.  We can check for this, and just create the BBOX for the convex hull
	public static String calculateConvexHullFromWktGeometryList(List<String> wktGeometryStrings, boolean solr4jFix) throws ParseException{
		if(wktGeometryStrings == null || wktGeometryStrings.size() == 0){
			return null;
		}
		Coordinate [] coordArray = build2dCoordinateArrayForGeometries(wktGeometryStrings);
		ConvexHull convexHull = new ConvexHull(coordArray, JTSFactoryFinder.getGeometryFactory(null));
		Geometry geom = convexHull.getConvexHull();
		String convexHullWkt = geom.toText();
		if(solr4jFix){
			try{
				//This validates whether solrj will spit on the other side
			    JtsSpatialContext.GEO.readShape(convexHullWkt);
			 }catch(TopologyException ex){
				logger.info("Solr4j convex hull fail - returning BBOX envelope instead");
				logger.info(convexHullWkt);
				String bboxTxt = geom.getEnvelope().toText();
				logger.info(bboxTxt);
				return bboxTxt;
			 }
		}
		return convexHullWkt;
	}
	
	public static Coordinate [] build2dCoordinateArrayForGeometries(List<String> wktGeomList) throws ParseException{
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		WKTReader reader = new WKTReader(geometryFactory);
		List<Coordinate> coordinateList = new ArrayList<Coordinate>();
		for(String wktGeom : wktGeomList){
			Geometry geom = reader.read(wktGeom);
			for(Coordinate c : geom.getCoordinates() ){
				coordinateList.add(c);
			}
		}
		Coordinate [] coordinateArray = new Coordinate[coordinateList.size()];
		return coordinateList.toArray(coordinateArray);
	}
	
}
