package au.edu.aekos.shared.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import au.edu.aekos.shared.web.json.JsonUploadSiteFileResponse;

import com.google.gson.Gson;

public class GsonFormatTests {
	
	@Test
	public void testGsonParsingWeirdness(){
		Gson gson = new Gson();
		JsonUploadSiteFileResponse jsonObj = new JsonUploadSiteFileResponse();
		jsonObj.setCoordSys("EPSG:4283");
        String str = gson.toJson(jsonObj);
        assertTrue( str.contains("EPSG:4283") );
	}
}
