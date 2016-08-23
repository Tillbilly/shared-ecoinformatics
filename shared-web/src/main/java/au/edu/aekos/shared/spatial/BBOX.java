package au.edu.aekos.shared.spatial;

import org.springframework.util.StringUtils;

public class BBOX {
    public static final String DEFAULT_PROJECTION = "GDA94"; //Used for rifcs iso bbox string
	private Double xmin;
	private Double xmax;
	private Double ymin;
	private Double ymax;
	private String coordinateSystem;
	
	public BBOX() {
		super();
	}
	
	public BBOX(Double xmin, Double ymin, Double xmax, Double ymax, String coordinateSystem) {
		super();
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.coordinateSystem = coordinateSystem;
	}

	public BBOX(Double xmin, Double ymin, Double xmax, Double ymax) {
		super();
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}


	public Double getXmin() {
		return xmin;
	}
	public void setXmin(Double xmin) {
		this.xmin = xmin;
	}
	public Double getXmax() {
		return xmax;
	}
	public void setXmax(Double xmax) {
		this.xmax = xmax;
	}
	public Double getYmin() {
		return ymin;
	}
	public void setYmin(Double ymin) {
		this.ymin = ymin;
	}
	public Double getYmax() {
		return ymax;
	}
	public void setYmax(Double ymax) {
		this.ymax = ymax;
	}
	
	public void setValues(Double xmin, Double ymin, Double xmax, Double ymax){
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}
	
	public String getCoordinateSystem() {
		return coordinateSystem;
	}
	/*
	 * The coordinate system should be in common String format i.e. GDA94 , UTM45 , not EPSG codes.
	 */
	public void setCoordinateSystem(String coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}
	
	public static final Integer DEFAULT_BOX_DP = 5;
	public String getIso19139dcmiBox(){
		return getIso19139dcmiBox(DEFAULT_BOX_DP);
	}
	public String getIso19139dcmiBox(Integer maxDecimalPlaces){
		String format = "%." + maxDecimalPlaces.toString() + "f";
		StringBuilder sb = new StringBuilder();
    	sb.append("northlimit=").append(String.format(format, ymax)).append("; ");
    	sb.append("southlimit=").append(String.format(format,ymin)).append("; ");
    	sb.append("eastlimit=").append(String.format(format,xmax)).append("; ");
    	sb.append("westlimit=").append(String.format(format,xmin)).append("; ");
    	sb.append("projection=");
    	if(StringUtils.hasLength(coordinateSystem)){
    		sb.append(coordinateSystem);
    	}else{
    		sb.append(DEFAULT_PROJECTION);
    	}
    	return sb.toString();
	}
	
	//Constructs a wkt polygon from the 
	public String getWktPolygon(){
		if( xmin == null || ymin == null || xmax == null || ymax == null){
			return null;
		}
		StringBuilder polygonWkt = new StringBuilder("POLYGON((");
		polygonWkt.append(xmin.toString()).append(" ").append(ymin.toString()).append(",");
		polygonWkt.append(xmax.toString()).append(" ").append(ymin.toString()).append(",");
		polygonWkt.append(xmax.toString()).append(" ").append(ymax.toString()).append(",");
		polygonWkt.append(xmin.toString()).append(" ").append(ymax.toString()).append(",");
		polygonWkt.append(xmin.toString()).append(" ").append(ymin.toString());  //Close the polygon
		polygonWkt.append("))");
		return polygonWkt.toString();
	}
}
