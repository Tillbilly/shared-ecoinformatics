package au.org.aekos.shared.api.informationModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aekos.shared.api.model.infomodel.SharedInformationModel;

import com.google.gson.Gson;


public class InformationModelFactoryTest {

	private static final Logger logger = LoggerFactory.getLogger(InformationModelFactoryTest.class);
	private String line1 = "SHD.iso19139dcmiBox,iso19139 DCMI Box,\"Bounding box specified in the iso19139 dcmi box format, lon lat GDA94 datum ( EPSG:4283)\",D,Y,N,,,,,,";
	private String line2 = "SHD.associatedMaterialTypeSuggest,Suggested Associated Material Type,User suggested type of associated material not contained in the SHaRED vocabulary,A,N,N,,SHD.associatedMaterialType,,Associated Material,Associated Material,";
	
	/**
	 * Can we tokenise strings as expected?
	 */
	@Test
	public void testLineTokenisation(){
		String [] tokens = InformationModelFactory.stringTokeniseModelEntryLine(line1);
		for(int x = 0; x < tokens.length; x++){
			logger.info(tokens[x]);
			assertTrue(tokens[9] == null);
		}
		String [] tokens2 = InformationModelFactory.stringTokeniseModelEntryLine(line2);
		for(int x = 0; x < tokens2.length; x++){
			logger.info(tokens2[x]);
			assertEquals("Associated Material",tokens2[9]);
		}
	}
	
	/**
	 * Does the information model factory output something non-null?
	 */
	@Test
	public void testInformationModelFactory(){
		SharedInformationModel infoModel = InformationModelFactory.getSharedInformationModel();
		assertNotNull(infoModel);
		assertTrue(infoModel.getEntryList().size() > 0);
	}
	
	/**
	 * Can we serialise then deserialise the information model?
	 */
	@Test
	public void testJsonSerialisationDeserialisation(){
		SharedInformationModel infoModel = InformationModelFactory.getSharedInformationModel();
		Gson gson = new Gson();
		String str = gson.toJson(infoModel);
		assertFalse(str.contains("entriesForGroupTitle"));
		assertFalse(str.contains("metatagToInfoModelEntryMap"));
		SharedInformationModel model2 = gson.fromJson(str, SharedInformationModel.class);
		assertEquals(infoModel.getEntryList().size(), model2.getEntryList().size());
	}
}
