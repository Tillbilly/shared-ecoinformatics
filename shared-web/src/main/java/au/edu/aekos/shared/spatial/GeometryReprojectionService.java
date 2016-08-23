package au.edu.aekos.shared.spatial;

import java.util.List;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * The standard SRS for geometry in the solr index is EPSG:4283 - 
 * corresponding to longitude latitude with GDA94 datum - i.e. the australian standard.
 * 
 * This service will reproject geometries captured from the site file upload OR the bounding box specification to this
 * coordinate reference system.
 * 
 * We are using geotools and related libraries to do this - mainly jts ( java topology suite )
 * 
 * @author btill
 */
public interface GeometryReprojectionService {
	
	List<String> reprojectWktPointsToEPSG4283(List<String> wktPointList, String fromProjectionCode, int decimalPlaces);
	
	Geometry reprojectGeometry(Geometry geom, String epsgCodeFrom,  String epsgCodeTo) throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException;

	public void fixGeometryPrecision(Geometry geom, int numDecimalPlaces);

	String reprojectWktPolygonToEPSG4283(String wktPoly, String fromProjectionCode, int decimalPlaces);
	
	BBOX reprojectBBOXToEPSG4283(BBOX bbox, String fromProjectionCode, int decimalPlaces);
	
}
