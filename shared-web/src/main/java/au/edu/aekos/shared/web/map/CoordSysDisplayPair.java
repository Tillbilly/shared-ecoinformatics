package au.edu.aekos.shared.web.map;

import java.io.Serializable;

public class CoordSysDisplayPair implements Serializable {

	private static final long serialVersionUID = 3756787301251019589L;
	
	private String epsgCode;
	private String displayStr;
	
	public CoordSysDisplayPair(String epsgCode, String displayStr) {
		super();
		this.epsgCode = epsgCode;
		this.displayStr = displayStr;
	}
	
	public CoordSysDisplayPair() {
		super();
	}

	public String getEpsgCode() {
		return epsgCode;
	}
	public void setEpsgCode(String epsgCode) {
		this.epsgCode = epsgCode;
	}
	public String getDisplayStr() {
		return displayStr;
	}
	public void setDisplayStr(String displayStr) {
		this.displayStr = displayStr;
	}
}
