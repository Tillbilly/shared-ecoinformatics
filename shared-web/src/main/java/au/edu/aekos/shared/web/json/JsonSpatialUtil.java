package au.edu.aekos.shared.web.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import au.edu.aekos.shared.spatial.BBOX;



public class JsonSpatialUtil {

	public static final Logger logger = LoggerFactory.getLogger(JsonSpatialUtil.class);
	
	public static BBOX calculateBBOXFromJsonGeoFeatureSet(JsonGeoFeatureSet geoFeatureSet){
		BBOX bbox = new BBOX();
		bbox.setCoordinateSystem(BBOX.DEFAULT_PROJECTION);
		
		Double xmin = Double.MAX_VALUE;
		Double xmax = Double.NEGATIVE_INFINITY;
		Double ymin = Double.MAX_VALUE;
		Double ymax = Double.NEGATIVE_INFINITY;
		
		for(JsonFeature feature : geoFeatureSet.getFeatures() ){
			WKTReader wktReader = new WKTReader();
			Geometry geom;
			try {
				geom = wktReader.read(feature.getGeometry());
			} catch (ParseException e) {
				logger.error("Failed parsing geoFeatureSetGeometry for wkt string " + feature.getGeometry(), e);
				return null;
			}
			Envelope env = geom.getEnvelopeInternal();			
			if( xmin.compareTo(env.getMinX() ) > 0){
	        	xmin = env.getMinX();
	        }
	        if(xmax.compareTo(env.getMaxX()) < 0){
	        	xmax = env.getMaxX();
	        }
	        if(ymin.compareTo(env.getMinY()) > 0){
	        	ymin = env.getMinY();
	        }
	        if(ymax.compareTo(env.getMaxY()) < 0){
	        	ymax = env.getMaxY();
	        }
		}
		bbox.setValues(xmin, ymin, xmax, ymax);
		return bbox;
	}
	
	
	
}
