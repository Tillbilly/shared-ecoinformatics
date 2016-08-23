package au.edu.aekos.shared.web.json;

import java.io.Serializable;

public class JsonSite implements Serializable{
   
	private static final long serialVersionUID = 7215592292590419561L;
	
	private String xCoord;
    private String yCoord;
    private String zone;
    private String comments;
    private String siteId;
    
	public JsonSite(String xCoord, String yCoord, String zone, String comments,
			String dateStr, String siteId, String visitId) {
		super();
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zone = zone;
		this.comments = comments;
		this.siteId = siteId;
	}
	public JsonSite() {
		super();
	}

	public String getxCoord() {
		return xCoord;
	}
	public void setxCoord(String xCoord) {
		this.xCoord = xCoord;
	}
	public String getyCoord() {
		return yCoord;
	}
	public void setyCoord(String yCoord) {
		this.yCoord = yCoord;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
