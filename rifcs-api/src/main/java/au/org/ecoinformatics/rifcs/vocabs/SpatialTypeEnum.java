package au.org.ecoinformatics.rifcs.vocabs;

public enum SpatialTypeEnum {

    GML_KML_POLY_COORDS("gmlKmlPolyCoords","A set of KML long/lat co-ordinates derived from GML (OpenGIS Geography Markup Language) defining a polygon as described by the KML coordinates element but without the altitude component"),
    GPX("gpx","the GPS Exchange Format"),
    ISO31661("iso31661","ISO 3166-1 Codes for the representation of names of countries and their subdivisions - Part 1,Country codes"),
    ISO31662("iso31662","Codes for the representation of names of countries and their subdivisions - Part 2,Country subdivision codes"),
    ISO31663("iso31663","ISO 3166-3 Codes for country names which have been deleted from ISO 3166-1 since its first publication in 1974."),
    ISO19139DCMIBOX("iso19139dcmiBox","DCMI Box notation derived from bounding box metadata conformant with the iso19139 schema"),
    KML_POLY_COORDS("kmlPolyCoords","A set of KML (Keyhole Markup Language) long/lat co-ordinates defining a polygon as described by the KML coordinates element"),
    DCMI_POINT("dcmiPoint","spatial location information specified in DCMI Point notation"),
    TEXT("text","free-text representation of spatial location");

	SpatialTypeEnum(String value, String description){
		this.value = value;
		this.description = description;
	}

	public final String value;
	public final String description;
	
	public String getValue(){
		return value;
	}
	public String getDescription(){
		return description;
	}

}
