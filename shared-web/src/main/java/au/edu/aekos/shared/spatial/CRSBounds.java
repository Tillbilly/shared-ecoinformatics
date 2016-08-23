package au.edu.aekos.shared.spatial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class CRSBounds {
	
    private static final Map<String, BBOX> EPSG_BOUNDS_MAP;
    static {
    	Map<String, BBOX> boundsMap = new HashMap<String, BBOX>();
    	boundsMap.put("EPSG:4283", new BBOX(-180.0,-90.0,180.0,90.0));
    	boundsMap.put("EPSG:4326", new BBOX(-180.0000, -90.0000, 180.0000, 90.0000));
    	boundsMap.put("EPSG:3577", new BBOX(-2690013.3995, -5098960.4467, 2579169.8548, -1281018.4757));
    	boundsMap.put("EPSG:3112", new BBOX(-2918276.3772, -5287521.9260, 2362935.9369, -1372651.4100));
    	boundsMap.put("EPSG:28349", new BBOX(189586.6272, 6954598.0530, 810413.3728, 7597371.5494));
    	boundsMap.put("EPSG:28350", new BBOX(185287.0543, 6100653.3235, 814712.9457, 7830016.0842));
    	boundsMap.put("EPSG:28351", new BBOX(175342.3099, 6211597.9440, 824657.6901, 8494511.0513));
    	boundsMap.put("EPSG:28352", new BBOX(171800.9465, 6400173.7198, 828199.0535, 8815595.0072));
    	boundsMap.put("EPSG:28353", new BBOX(171800.9465, 6000791.7730, 828199.0535, 8815595.0072));
    	boundsMap.put("EPSG:28354", new BBOX(171172.5136, 5534625.2435, 828827.4864, 8882020.4168));
    	boundsMap.put("EPSG:28355", new BBOX(175479.2570, 5112643.4740, 824520.7430, 8483438.2879));
    	boundsMap.put("EPSG:28356", new BBOX(189586.6272, 5812134.5296, 810413.3728, 7597371.5494));
    	boundsMap.put("EPSG:28357", new BBOX(306694.5349, 3923337.2742, 693305.4651, 3934459.6225));
    	boundsMap.put("EPSG:28358", new BBOX(171071.2640, 3789858.6732, 828928.7360, 8893091.1458));
    	
    	EPSG_BOUNDS_MAP = Collections.unmodifiableMap(boundsMap);
    }
	
    public static boolean isCoordinateInBounds(double x, double y, String coordinateSystem){
    	if(!isCoordSystemSupport(coordinateSystem)){
    		return false;
    	}
    	return isXInBounds(x , coordinateSystem ) && isYInBounds(y , coordinateSystem );
    }
    
    public static boolean isXInBounds(double x, String coordinateSystem){
    	if(!isCoordSystemSupport(coordinateSystem))
    		return false;
    	return x >= EPSG_BOUNDS_MAP.get(coordinateSystem).getXmin() && x <= EPSG_BOUNDS_MAP.get(coordinateSystem).getXmax() ;
    }
    public static boolean isYInBounds(double y, String coordinateSystem){
    	if(!isCoordSystemSupport(coordinateSystem)){
    		return false;
    	}
    	return y >= EPSG_BOUNDS_MAP.get(coordinateSystem).getYmin() && y <= EPSG_BOUNDS_MAP.get(coordinateSystem).getYmax() ;
    }
	public static boolean isCoordSystemSupport(String epsgCode){
		return EPSG_BOUNDS_MAP.containsKey(epsgCode);
	}
	public static BBOX getBoundsBBOX(String coordinateSystem){
		if(!isCoordSystemSupport(coordinateSystem))
    		return null;
		return new BBOX(EPSG_BOUNDS_MAP.get(coordinateSystem).getXmin().doubleValue(),
				        EPSG_BOUNDS_MAP.get(coordinateSystem).getYmin().doubleValue(),
				        EPSG_BOUNDS_MAP.get(coordinateSystem).getXmax().doubleValue(),
				        EPSG_BOUNDS_MAP.get(coordinateSystem).getYmax().doubleValue(),
				        coordinateSystem);
	}
	
}
