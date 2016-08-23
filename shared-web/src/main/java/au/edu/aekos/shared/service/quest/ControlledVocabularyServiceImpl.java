package au.edu.aekos.shared.service.quest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.CustomVocabularyDao;
import au.edu.aekos.shared.data.dao.NewAekosVocabEntryDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.CustomVocabulary;
import au.edu.aekos.shared.data.entity.NewAekosVocabEntry;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.questionnaire.QuestionnairePage.PageCustomVocab;
import au.edu.aekos.shared.questionnaire.jaxb.DefaultVocabulary;
import au.edu.aekos.shared.questionnaire.jaxb.DefaultVocabularyTag;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.security.SecurityService;
import au.edu.aekos.shared.web.json.dynatree.DynatreeNode;

/**
 * What happens when the trait name / value changes in AEKOS?
 * 
 * Does shared need to keep a record of the criteria_id/criteria_title ?
 * 
 * @author btill
 */
@Component
public class ControlledVocabularyServiceImpl implements ControlledVocabularyService {
	
	private static final Logger logger = LoggerFactory.getLogger(ControlledVocabularyServiceImpl.class);
	
	@Autowired
	@Qualifier("controlledVocabDataSource")
	private DataSource controlledVocabDataSource;
	
	@Autowired
	private CustomVocabularyDao customVocabularyDao;
	
	@Autowired
	private NewAekosVocabEntryDao newAekosVocabEntryDao;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private SecurityService securityService;
	
    private static final String CACHE_NAME= "controlled_vocabulary_cache";
	private final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
    private static final String DYNATREE_CACHE_NAME= "dynatree_vocab_cache";
	private final Cache dynatreeCache = CacheManager.getInstance().getCache(DYNATREE_CACHE_NAME);
	
	public Map<String,List<TraitValue>> prepareControlledVocabListsForPage(QuestionnairePage page) throws ControlledVocabularyNotFoundException {
		Map<String, List<TraitValue>> controlledVocabListMap = new HashMap<String,List<TraitValue>>();
		for(String traitName : page.getRequiredControlledVocabs() ){
			List<TraitValue> vocabList = getTraitValueList( traitName, false, true );
			if(vocabList == null || vocabList.size() == 0){
				logger.info("No controlled vocabulary found for trait name " + traitName +". Checking for default vocabulary.");
				//Look for a default vocab list
				DefaultVocabulary dv = page.getDefaultVocabularies().get(traitName);
				if(dv == null || dv.getListEntries().size() == 0 ){
					throw new ControlledVocabularyNotFoundException("No vocabulary found for trait name " + traitName);
				}
				addDefaultVocabularyToControlledVocabListMap(dv, controlledVocabListMap, traitName );
			}
			else{
				controlledVocabListMap.put(traitName, vocabList);
			}
		}
		for(PageCustomVocab pcv : page.getRequiredCustomVocabs() ){
			List<TraitValue> vocabList = getTraitValueList( pcv.vocabName, true, pcv.sortAlpha );
			if(vocabList == null || vocabList.size() == 0){
				logger.info("No custom vocabulary found for trait name " + pcv.vocabName +". Checking for default vocabulary.");
				//Look for a default vocab list
				DefaultVocabulary dv = page.getDefaultVocabularies().get(pcv.vocabName);
				if(dv == null || dv.getListEntries().size() == 0 ){
					throw new ControlledVocabularyNotFoundException("No vocabulary found for trait name " + pcv.vocabName);
				}
				addDefaultVocabularyToControlledVocabListMap(dv, controlledVocabListMap, pcv.vocabName );
			}
			else{
				controlledVocabListMap.put(pcv.vocabName, vocabList);
			}
		}
	    return controlledVocabListMap;
	}
	
	
	public List<TraitValue> getTraitValueList(String traitName, Boolean isCustom){
		return getTraitValueList(traitName, isCustom, true);
	}
	
	public List<TraitValue> getTraitValueList(String traitName, Boolean isCustom, boolean sortAlpha) {
		List<TraitValue> traitValueList = new ArrayList<TraitValue>();
		if(isCustom == null){
			List<String> customTraitList = getListOfAvailableCustomTraits();
			isCustom = customTraitList.contains(traitName);
		}
		
		LinkedHashMap<String, TraitValue> traitValueMap = getTraitValueMap(traitName, isCustom, sortAlpha);
		if( traitValueMap != null && traitValueMap.size() > 0 ){
			traitValueList.addAll( traitValueMap.values() );
		}
		return traitValueList;
	}

	private LinkedHashMap<String, TraitValue> getTraitValueMap(String traitName, boolean isCustom, boolean sortAlpha){
		if(cache.isKeyInCache(traitName ) && cache.get(traitName) != null){
			logger.debug("Retrieving Controlled Vocab for " + traitName+ " from cache" );
		    Element el = cache.get(traitName);
		    LinkedHashMap<String, TraitValue> tvm = (LinkedHashMap<String, TraitValue> ) el.getObjectValue();
		    if(tvm != null && tvm.size() > 0){
		    	return tvm;
		    }
		}
		LinkedHashMap<String, TraitValue> traitValueMap = null;
		if(isCustom){
			traitValueMap = retrieveCustomDefinedTraitValueMap(traitName, sortAlpha);
			logger.info("Adding Custom Vocab for " + traitName+ " to cache" );
		}else{
			traitValueMap =  retrieveTraitValueMapForAekosVocabulary(traitName);
			logger.info("Adding Controlled Vocab for " + traitName+ " to cache" );
		}
		
		Element elNew = new Element(traitName, traitValueMap);
		cache.put(elNew);
		return traitValueMap;
	}
	
	//Called after any edit to the underlying custom vocabulary table
	private void removeVocabFromCache(String vocabName){
		cache.remove(vocabName);
	}
	
	/**
	 * A reviewer has the ability to add new vocab entries for aekos managed vocabs.
	 * For example, 'Organisation'.
	 * 
	 * Until aekos is re-engineered better with discrete accessible components ( i.e. vocab management system )
	 * we are stuck with this intermediary solution
	 * 
	 * Basically, retrieve the aekos values,  retrieve the new values,  combine the lists.
	 * 
	 * @param traitName
	 * @return
	 */
	private LinkedHashMap<String, TraitValue> retrieveTraitValueMapForAekosVocabulary(String traitName){
		LinkedHashMap<String, TraitValue> aekosTraitValueMap = retrieveTraitValueMapFromAekos(traitName);
		List<TraitValue> newTraitValueList = retrieveNewAekosVocabTraiValueList(traitName);
        if(newTraitValueList == null || newTraitValueList.size() == 0){
        	return aekosTraitValueMap;
        }
		for(TraitValue tv : newTraitValueList ){
			if(! aekosTraitValueMap.containsKey(tv.getTraitValue())){
				aekosTraitValueMap.put(tv.getTraitValue(), tv);
			}
		}
		List<TraitValue> combinedList = new ArrayList<TraitValue>();
		combinedList.addAll( aekosTraitValueMap.values() );
		Collections.sort(combinedList);
		LinkedHashMap<String, TraitValue> combinedTraitValueMap = new LinkedHashMap<String, TraitValue>();
		for(TraitValue trV : combinedList){
			combinedTraitValueMap.put(trV.getTraitValue(), trV);
		}
		return combinedTraitValueMap;
	}
	
	private List<TraitValue> retrieveNewAekosVocabTraiValueList(String traitName){
		List<NewAekosVocabEntry> newEntryList = newAekosVocabEntryDao.retrieveNewEntriesForVocabulary(traitName);
		List<TraitValue> traitValueList = new ArrayList<TraitValue>();
		if(newEntryList != null ){
			for(NewAekosVocabEntry vocabEntry : newEntryList){
				TraitValue tv = new TraitValue(vocabEntry.getValue());
				traitValueList.add(tv);
			}
		}
		return traitValueList;
	}

	public static final String FLORA_TAXA_TRAIT_NAME = "taxon names";
	public static final String FAUNA_TAXA_TRAIT_NAME = "fauna taxon names";
	public static final String COMMON_FLORA_TRAIT_NAME = "common flora names";
	public static final String COMMON_FAUNA_TRAIT_NAME = "common fauna names";
	
	
	//Direct DB integration is a bit lame, but here we are.
	private final String QUERY_STRING = "select criteria_id, criteria_title from __lookup_codes where criteria_type = ? order by criteria_id"; 
	
    private LinkedHashMap<String, TraitValue> retrieveTraitValueMapFromAekos(String traitName){
    	if(traitName.equalsIgnoreCase(FLORA_TAXA_TRAIT_NAME) ){
    		return retrieveTaxonNames();
    	}else if(traitName.equalsIgnoreCase(FAUNA_TAXA_TRAIT_NAME)){
    		return retrieveFaunaNames();
    	}else if(traitName.equalsIgnoreCase(COMMON_FLORA_TRAIT_NAME)){
    		return retrieveCommonFloraNames();
    	}else if(traitName.equalsIgnoreCase(COMMON_FAUNA_TRAIT_NAME)){
    		return retrieveCommonFaunaNames();
    	}
    	LinkedHashMap<String, TraitValue> traitValueMap = new LinkedHashMap<String, TraitValue>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(QUERY_STRING);
			stmt.setString(1, traitName);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String traitValue = rs.getString("criteria_id");
				String displayValue = rs.getString("criteria_title");
				traitValueMap.put( traitValue , new TraitValue(traitValue, displayValue ));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	
		return traitValueMap;
	}
    
    private final String QUERY_STRING_TAXA = "select namewithoutauthorship, formattedname from r_speciesconcept where speciesconcepttype like 'NOM%' or speciesconcepttype like 'UNDER%' order by namewithoutauthorship"; 
    
    private LinkedHashMap<String, TraitValue> retrieveTaxonNames(){
    	LinkedHashMap<String, TraitValue> traitValueMap = new LinkedHashMap<String, TraitValue>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(QUERY_STRING_TAXA);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String traitValue = rs.getString("namewithoutauthorship");
				String formattedDisplayValue = rs.getString("formattedname");
				TraitValue tv = new TraitValue(traitValue);
				tv.setFormattedDisplayString(formattedDisplayValue.replaceAll("I>", "em>"));
				traitValueMap.put( traitValue , tv);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				logger.error("Failed to close the statement.", e);
			}
	    	try {
	    		if (connection != null) connection.close();
			} catch (SQLException e) {
				logger.error("Failed to close the connection.", e);
			}
		}
    	
		return traitValueMap;
    }
    
    private final String FAUNA_QUERY = 
    		"select distinct " + 
            "a.iname as name " + 
    		"from ( " +
    		" select " +
	        "    initcap(replace(replace(name,'\"',''), '''' , '' )) as iname, " + 
    		"    upper(name) as upname, " + 
    		"    name " +
    		" from r_speciesconceptfaunashared " + 
    		" where name not like '% %' " +
    		"   and name not like '%?%' " +
    		"   and name not like '[%' " +
    		"   and name not like '(%' " +
    		"   and rank != 'SubFamily' " + 
    		" UNION " +
    		" select  " +
    		"    replace(replace(name,'\"',''), '''' , '' ) as iname, " + 
    		"    upper(name) as upname, " + 
    		"    name " +
    		" from r_speciesconceptfaunashared " + 
    		" where name like '% %' " +
    		"   and name not like '%?%' " + 
    		"   and name not like '[%' " +
    		"   and name not like '(%' " +
    		"   and rank != 'SubFamily' " + 
    		" ) a " +
    		" order by name";
    
    private LinkedHashMap<String, TraitValue> retrieveFaunaNames(){
    	LinkedHashMap<String, TraitValue> traitValueMap = new LinkedHashMap<String, TraitValue>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(FAUNA_QUERY);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String traitValue = rs.getString("name");
				TraitValue tv = new TraitValue(traitValue);
				traitValueMap.put( traitValue , tv);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return traitValueMap;
    }
    
    public static final String FLORA_COMMON_NAME_QUERY = "select distinct initcap(regexp_replace(trim(common_name_string),E'\\t',' ')) as name, scientific_name_string from r_commonspeciesnamesflorashared order by name";
    public static final String FAUNA_COMMON_NAME_QUERY = "select distinct initcap(regexp_replace(trim(common_name_string),E'\\t',' ')) as name, scientific_name_string from r_commonspeciesnamesfaunashared order by name";
    
    private LinkedHashMap<String, TraitValue> retrieveCommonFaunaNames(){
    	return retrieveCommonNamesTraitMap(FAUNA_COMMON_NAME_QUERY);
    }
    
    private LinkedHashMap<String, TraitValue> retrieveCommonFloraNames(){
    	return retrieveCommonNamesTraitMap(FLORA_COMMON_NAME_QUERY);
    }
    
    private LinkedHashMap<String, TraitValue> retrieveCommonNamesTraitMap(String query){
    	LinkedHashMap<String, TraitValue> traitValueMap = new LinkedHashMap<String, TraitValue>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String traitValue = rs.getString("name");
				String scientificName = rs.getString("scientific_name_string");
				if(traitValueMap.containsKey(traitValue)){
					if(StringUtils.hasLength(scientificName)){
						traitValueMap.get(traitValue).getScientificNames().add(scientificName);
					}
				}else{
					TraitValue tv = new TraitValue(traitValue);
					if(StringUtils.hasLength(scientificName)){
						tv.getScientificNames().add(scientificName);
					}
					traitValueMap.put( traitValue , tv);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return traitValueMap;
    }		
    
	
    private LinkedHashMap<String, TraitValue> retrieveCustomDefinedTraitValueMap(String traitName, boolean sortAlpha){
    	LinkedHashMap<String, TraitValue> traitValueMap = new LinkedHashMap<String, TraitValue>();
    	List<CustomVocabulary> customVocabularyList = null;
    	if(sortAlpha){
    	    customVocabularyList = customVocabularyDao.retrieveCustomVocabularyByName(traitName);
    	}else{
    		customVocabularyList = customVocabularyDao.retrieveCustomVocabularyUnsorted(traitName);
    	}
    	
    	for(CustomVocabulary cv :customVocabularyList ){
    		TraitValue tv = new TraitValue(cv.getValue(), cv.getDisplayValue(), cv.getDisplayValueDescription());
    		tv.setParent(cv.getParentValue());
    		traitValueMap.put(cv.getValue(), tv);
    	}
    	return traitValueMap;
    }
	
	private void addDefaultVocabularyToControlledVocabListMap(DefaultVocabulary dv, Map<String,List<TraitValue>> controlledVocabListMap, String traitName){
		
		List<TraitValue> traitValueList = new ArrayList<TraitValue>();
		
	    for(DefaultVocabularyTag dvtag : dv.getListEntries() ){
	    	TraitValue tv = new TraitValue(dvtag.getValue());
	    	if( StringUtils.hasLength(dvtag.getDisplay()) ){
	    		tv.setDisplayString(dvtag.getDisplay());
	    	}
	    	if(StringUtils.hasLength(dvtag.getDescription())){
	    		tv.setDescription(dvtag.getDescription());
	    	}
	    	traitValueList.add(tv);
	    }
	    controlledVocabListMap.put(traitName, traitValueList);
	}

	/**
	 * Used for questionnaire summary / review / view
	 */
	public String getTraitDisplayText(String traitName, String traitValue) {
		LinkedHashMap<String, TraitValue> traitValueMap = getTraitValueMap( traitName, true , false );
		if(traitValueMap != null && traitValueMap.size() > 0){
			TraitValue tv = traitValueMap.get(traitValue);
			if(tv != null){
				return tv.getDisplayString();
			}
		}else{
			traitValueMap = getTraitValueMap( traitName, false, false );
			if(traitValueMap != null){
				TraitValue tv = traitValueMap.get(traitValue);
				if(tv != null){
					return tv.getDisplayString();
				}
			}
		}
		return null;
	}
	
	/**
	 * Return a LinkedHashMap now - more convenient to cache, as we now need to easily
	 * get at the Display value for a trait, rather than its aekos ID value.
	 * 
	 */
	@Deprecated  
    private List<TraitValue> retrieveTraitValueListFromAekos(String traitName){
    	List<TraitValue> traitValueList = new ArrayList<TraitValue>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(QUERY_STRING);
			stmt.setString(1, traitName);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String traitValue = rs.getString("criteria_id");
				String displayValue = rs.getString("criteria_title");
				traitValueList.add(new TraitValue(traitValue, displayValue ));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return traitValueList;
	}

	@Override @Transactional(propagation=Propagation.REQUIRED)
	public void loadCustomVocabularyFile(File file) throws IOException, SQLException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		loadCustomVocabularyBufferedReader( br, "ADMIN");
	}
	@Transactional(propagation=Propagation.REQUIRED)
	public void loadCustomVocabularyFromInputStream(InputStream is) throws IOException, SQLException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		loadCustomVocabularyBufferedReader( br, securityService.getLoggedInUsername());
	}
	
	private void loadCustomVocabularyBufferedReader(BufferedReader br, String username) throws IOException, SQLException {
		String line;
		int lineNumber = 0;
		//Top line should just be the columnNames
		List<CustomVocabulary> customVocabularyList = new ArrayList<CustomVocabulary>();
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if(lineNumber ==1){
				continue;
			}
			if(! StringUtils.hasLength(line)){
				break;
			}
			CustomVocabulary cv = buildCustomVocabFromFileLine(line);
			cv.setLoadedBy(username);
			customVocabularyList.add(cv);
			if(customVocabularyList.size() == 100){
				customVocabularyDao.batchLoad(customVocabularyList);
				customVocabularyList.clear();
			}
		}
		if(customVocabularyList.size() > 0){
			customVocabularyDao.batchLoad(customVocabularyList);
			customVocabularyList.clear();
		}
	}
	
	public CustomVocabulary buildCustomVocabFromFileLine(String line){
		//manually grab the content before the first comma, then between the first and second comma
		int firstCommaIndex = getNextIndexOfCSVComma(line, -1 );
		int secondCommaIndex = getNextIndexOfCSVComma(line, firstCommaIndex );
		int thirdCommaIndex = getNextIndexOfCSVComma(line, secondCommaIndex );
		int fourthCommaIndex = getNextIndexOfCSVComma(line, thirdCommaIndex );
		
		String vocabName = "";
		String value  = "";
		String parent  = null; //Used for tree view vocabularies
		String displayValue  = "";
		String displayValueDescription  = "";
		
		try{
			vocabName = line.substring(0,firstCommaIndex).replaceAll("\"", "");
			value = line.substring(firstCommaIndex + 1,secondCommaIndex).replaceAll("\"", "");
			if(secondCommaIndex < thirdCommaIndex - 1){
			    parent = line.substring(secondCommaIndex + 1,thirdCommaIndex);
			}
			if(parent != null){
				parent = parent.replaceAll("\"", "");
			}
			
			displayValue = line.substring(thirdCommaIndex + 1,fourthCommaIndex);
			if(displayValue != null){
				displayValue = displayValue.replaceAll("\"", "");
			}
			displayValueDescription = line.substring(fourthCommaIndex + 1);
			if(displayValueDescription != null){
				displayValueDescription = displayValueDescription.replaceAll("\"", "");
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		CustomVocabulary cv = new CustomVocabulary();
		cv.setVocabularyName(vocabName);
		cv.setValue(value);
		cv.setParentValue(parent);
		cv.setDisplayValue(displayValue);
		cv.setDisplayValueDescription(displayValueDescription);
	    cv.setLoadedBy("ADMIN");
	    cv.setLoadDate(new Date());
		return cv;
	}
	
	/**
	 * To start at the start of the String, set lastCommaIndex to -1
	 * 
	 * If there is no more comma`s, -1 is returned 
	 * 
	 * @param str
	 * @param lastCommaIndex
	 * @return
	 */
	private int getNextIndexOfCSVComma(String str, int lastCommaIndex ){
		if(str.charAt(lastCommaIndex + 1) == '\"'){
			return str.indexOf("\",", lastCommaIndex + 1 ) + 1;			
		}else{
			return str.indexOf(",", lastCommaIndex + 1 ) ;
		}
	}

	private final String CONTROLLED_VOCAB_NAME_QUERY = "select distinct criteria_type from __lookup_codes order by criteria_type"; 
	
	@Override
	public List<String> getListOfAvailableTraits() {
		List<String> controlledVocabCriteriaTypes = new ArrayList<String>();
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	try {
			connection = controlledVocabDataSource.getConnection();
			stmt = connection.prepareStatement(CONTROLLED_VOCAB_NAME_QUERY);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String criteriaType = rs.getString("criteria_type");
				controlledVocabCriteriaTypes.add(criteriaType);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
	    	try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return controlledVocabCriteriaTypes;
	}
	
	@SuppressWarnings("unchecked")
	public List<DynatreeNode> getDynatreeNodeVocabRepresentation(String traitName){
		logger.info("Retrieving " + traitName + " from dynatreeCache" );
		Element el = dynatreeCache.get(traitName);
		if(el == null || el.getObjectValue() == null ){
			logger.info("Node list for " + traitName + " not found, rebuilding." );
			List<String> customTraitList = getListOfAvailableCustomTraits();
			//Recursively Alphabetically sort the lists at the end.
			Map<String, DynatreeNode > nodes = new HashMap<String, DynatreeNode >();  
			List<String> topLevelNodeTitles = new ArrayList<String>();
			if(customTraitList.contains(traitName)){
				List<CustomVocabulary> customVocabularyList = customVocabularyDao.retrieveCustomVocabularyUnsorted(traitName);
		    	for(CustomVocabulary cv :customVocabularyList ){
		    		DynatreeNode node = new DynatreeNode(cv.getValue(), cv.getDisplayValue());
		    		node.setTooltip(cv.getDisplayValueDescription());
		    		nodes.put(cv.getValue(), node);
		    		if(! StringUtils.hasLength( cv.getParentValue() ) ){
		    			topLevelNodeTitles.add(cv.getValue());
		    		}else if(nodes.containsKey(cv.getParentValue())){
		    			nodes.get(cv.getParentValue()).addChild(node);
		    		}else{
		    			logger.error("dynatree controlled vocab " + traitName +" is ordered wrong, " + cv.getParentValue() + " not yet defined for " + cv.getValue());
		    		}
		    	}
		    }else{ //build node list from an aekos vocabulary - only for IBRA
		    	List<TraitValue>  traitValueList =  getTraitValueList(traitName , false);
		    	for(TraitValue tv :traitValueList ){
		    		DynatreeNode node = new DynatreeNode(tv.getTraitValue(), tv.getDisplayString());
		    		node.setTooltip(tv.getDescription());
		    		nodes.put(tv.getTraitValue(), node);
		    		topLevelNodeTitles.add(tv.getTraitValue());
		    	}
		    }
	    	//Now just build the list of nodes to return
	    	List<DynatreeNode> nodeList = new ArrayList<DynatreeNode>();
			for(String title : topLevelNodeTitles ){
				nodeList.add(nodes.get(title));
			}
			el = new Element(traitName, nodeList );
			dynatreeCache.put(el);
			logger.info("Node list for " + traitName + " with " + nodeList.size() + " elements added to cache" );
		}
		
		List<DynatreeNode> cachedNodeList = (List<DynatreeNode>) el.getObjectValue();
		return cloneNodeList(cachedNodeList); //May not be necessary - but can't find a definitive description about serialization vs object references- safe side to clone
	}

	private List<DynatreeNode> cloneNodeList(List<DynatreeNode> nodeList){
		List<DynatreeNode> clonedDynatreeNodeList = new ArrayList<DynatreeNode>();
		for(DynatreeNode dn : nodeList){
			clonedDynatreeNodeList.add(dn.clone());
		}
		return clonedDynatreeNodeList;
	}
	
	
	
	
	@Override
	public List<String> getListOfAvailableCustomTraits() {
		return customVocabularyDao.retrieveActiveCustomVocabularyNames();
	}

	@Override
	public boolean traitListContainsValue(String traitName, boolean isCustom,
			String value) {
		LinkedHashMap<String, TraitValue> traitValueMap = getTraitValueMap(traitName, isCustom, false);
		if(traitValueMap == null || traitValueMap.size() == 0){
			return false;
		}
		return traitValueMap.containsKey(value);
	}

	@Override
	public String getDisplayStringForTraitValue(String traitName, boolean isCustom, String value) {
		LinkedHashMap<String, TraitValue> tvm = getTraitValueMap(traitName, isCustom, false);
		if(tvm != null){
			TraitValue tv =  tvm.get(value);
			if(tv != null){
				return tv.getDisplayString();
			}
		}
		return null;
	}

	@Override @Transactional
	public Map<Submission, List<SubmissionAnswer>> getSubmissionAnswersContainingVocabValueAnswer(
			String vocabValue, String vocabularyName) {
		
		Map<Submission, List<SubmissionAnswer>> matchingAnswerMap = new HashMap<Submission, List<SubmissionAnswer>>();
		
		//Better to use the submission as the root entity, 
		//so we don't get historical submission answers. ( As oposed to querying answers directly )
		List<Submission> submissions = submissionDao.getSubmissionsWithVocabResponse(vocabValue);
		if(submissions == null || submissions.size() == 0){
			return matchingAnswerMap;
		}
		//Now for each of the submissions, need to analyse the questionnaire config to see which
		//question has the response for the given vocab, then see if the submission actually has
		//the response for the right question
		Map<Long,List<Submission>> confToSubmissionsMap = buildConfigToSubmissionsMap(submissions);
		for(Map.Entry<Long,List<Submission>> entry : confToSubmissionsMap.entrySet()){
			List<Question> questionsForVocab = new ArrayList<Question>();
			QuestionnaireConfigEntity qce = configService.getQuestionnaireConfigEntity(entry.getKey());
			try {
				QuestionnaireConfig qc = configService.parseConfigXML(qce);
				for(Question q : qc.getAllQuestions(true) ){
					if(vocabularyName.equals( q.getTraitName() ) ){
						questionsForVocab.add(q); //might be more than 1 question using the vocab
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(questionsForVocab.size() > 0){
				for(Submission sub : entry.getValue()){
				    List<SubmissionAnswer> matchingAnswerList = checkAnswersForVocabValue(sub, questionsForVocab, vocabValue);
				    if(matchingAnswerList != null && matchingAnswerList.size() > 0){
				    	matchingAnswerMap.put(sub, matchingAnswerList);
				    }
				}
			}
		}
		
		return matchingAnswerMap;
	}
	
	private Map<Long,List<Submission>> buildConfigToSubmissionsMap(List<Submission> submissionList){
		Map<Long,List<Submission>> confToSubmissionsMap = new HashMap<Long,List<Submission>>();
		for(Submission sub : submissionList){
			Long configId = sub.getQuestionnaireConfig().getId();
			if(! confToSubmissionsMap.containsKey(configId)){
				confToSubmissionsMap.put(configId, new ArrayList<Submission>());
			}
			confToSubmissionsMap.get(configId).add(sub);
		}
		return confToSubmissionsMap;
	}
	
	private List<SubmissionAnswer> checkAnswersForVocabValue(Submission sub, List<Question> vocabQuestions, String vocabValue){
		List<SubmissionAnswer> matchingSubmissionAnswerList = new ArrayList<SubmissionAnswer>();
		//First check normal answers
		Map<String, SubmissionAnswer> submissionAnswerMap = sub.getAnswersMappedByQuestionId();
		Map<String, List<SubmissionAnswer>> questionSetAnswerMap = sub.getQuestionIdToQuestionSetAnswerMap();
		for(Question q : vocabQuestions){
			if(submissionAnswerMap.containsKey(q.getId())){
				SubmissionAnswer sa = submissionAnswerMap.get(q.getId());
				if(sa.hasResponse()){
					if(ResponseType.getIsMultiselect(sa.getResponseType())){
						for(SubmissionAnswer msa : sa.getMultiselectAnswerList()){
							if(vocabValue.equalsIgnoreCase(msa.getResponse())){
								matchingSubmissionAnswerList.add(sa);
								break;
							}
						}
					}else if(vocabValue.equalsIgnoreCase(sa.getResponse())){
						matchingSubmissionAnswerList.add(sa);
					}
				}
			}else if(questionSetAnswerMap.containsKey(q.getId()) ){
				for(SubmissionAnswer sa : questionSetAnswerMap.get(q.getId())){
					if(sa.hasResponse()){
						if(ResponseType.getIsMultiselect(sa.getResponseType())){
							for(SubmissionAnswer msa : sa.getMultiselectAnswerList()){
								if(vocabValue.equalsIgnoreCase(msa.getResponse())){
									matchingSubmissionAnswerList.add(sa);
									break;
								}
							}
						}else if(vocabValue.equalsIgnoreCase(sa.getResponse())){
							matchingSubmissionAnswerList.add(sa);
						}
					}
				}
			}
		}
		return matchingSubmissionAnswerList;
	}

	@Override
	public TraitValue getCustomTraitValue(String vocabularyName, String vocabValue) {
		LinkedHashMap<String, TraitValue> traitValueMap = getTraitValueMap(vocabularyName, true, false);
		if(traitValueMap == null){
			return null;
		}
		return traitValueMap.get(vocabValue);
	}
	
	public TraitValue getTraitValue(String vocabularyName, String vocabValue){
		if(getListOfAvailableCustomTraits().contains(vocabularyName) ){
			return  getCustomTraitValue(vocabularyName, vocabValue);
		}
		LinkedHashMap<String, TraitValue> traitValueMap = getTraitValueMap(vocabularyName, false, true);
		if(traitValueMap == null){
			return null;
		}
		return traitValueMap.get(vocabValue);
	}
	
	

	@Override
	@Transactional
	public void updateCustomVocabValue(String vocabularyName, String originalValue, TraitValue value) {
		CustomVocabulary entry = customVocabularyDao.retrieveCustomVocabularyByName(vocabularyName, originalValue);
		String parentValue = null;
	    if(entry != null){
	    	parentValue = entry.getParentValue();
	    	entry.setActive(false);
	    	customVocabularyDao.saveOrUpdate(entry);
	    }
	    CustomVocabulary cv = mapTraitValueToCustomVocabulary(value, vocabularyName, parentValue);
	    customVocabularyDao.saveOrUpdate(cv);
	    removeVocabFromCache(vocabularyName);
	}

	@Override @Transactional
	public void removeCustomVocabValue(String vocabularyName, String vocabValue) {
		CustomVocabulary entry = customVocabularyDao.retrieveCustomVocabularyByName(vocabularyName, vocabValue);
		if(entry != null){
		    entry.setActive(Boolean.FALSE);
		    customVocabularyDao.saveOrUpdate(entry);
		}
		removeVocabFromCache(vocabularyName);
	}

	@Override @Transactional
	public boolean checkCustomVocabValueExists(String vocabularyName,
			TraitValue value) {
		CustomVocabulary cv = customVocabularyDao.retrieveCustomVocabularyByName(vocabularyName, value.getTraitValue());
		if(cv != null){
			return true;
		}
		return false;
	}
	
	@Override @Transactional
	public void addCustomVocabValue(String vocabularyName, TraitValue value) {
		CustomVocabulary cv = mapTraitValueToCustomVocabulary(value, vocabularyName, value.getParent());
		customVocabularyDao.saveOrUpdate(cv);
		removeVocabFromCache(vocabularyName);
	}
	
	private CustomVocabulary mapTraitValueToCustomVocabulary(TraitValue tv, String vocabularyName, String parent){
		CustomVocabulary customVocab = new CustomVocabulary();
		String value = tv.getTraitValue();
		customVocab.setValue(value);
		customVocab.setVocabularyName(vocabularyName);
		customVocab.setParentValue(parent);
		customVocab.setDisplayValue(StringUtils.hasLength(tv.getDisplayString()) ? tv.getDisplayString() : value );
		customVocab.setDisplayValueDescription(StringUtils.hasLength(tv.getDescription()) ? tv.getDescription() : value);
		customVocab.setLoadedBy( securityService.getLoggedInUsername() );
		customVocab.setLoadDate(new Date());
		return customVocab;
	}
	
	@Override @Transactional
	public void addSuggestedAekosVocabValue(String vocabularyName, TraitValue value){
		NewAekosVocabEntry entry = mapTraitValueToNewAekosVocabEntry(value, vocabularyName);
		newAekosVocabEntryDao.saveOrUpdate(entry);
		removeVocabFromCache(vocabularyName);
	}
	
	private NewAekosVocabEntry mapTraitValueToNewAekosVocabEntry(TraitValue tv, String vocabularyName ){
		NewAekosVocabEntry entry = new NewAekosVocabEntry();
		entry.setValue(tv.getTraitValue());
		entry.setVocabularyName(vocabularyName);
		entry.setLoadedBy( securityService.getLoggedInUsername() );
		entry.setLoadDate(new Date());
		return entry;
	}
	
	
	
	@Transactional
	public void streamCustomVocabulariesToOutputStream(OutputStream outputStream){
		List<CustomVocabulary> vocabs = customVocabularyDao.getAllForExtract();
		PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
		pw.println( "\"vocabularyName\",\"value\",\"parent\",\"displayValue\",\"displayValueDescription\"");
		for(CustomVocabulary cv : vocabs){
			StringBuilder sb = new StringBuilder();
			sb.append("\"").append(cv.getVocabularyName()).append("\",");
			sb.append("\"").append(cv.getValue()).append("\",");
			if(StringUtils.hasLength(cv.getParentValue())){
				sb.append("\"").append(cv.getParentValue()).append("\"");
			}
			sb.append(",");
			if(StringUtils.hasLength(cv.getDisplayValue())){
				sb.append("\"").append(cv.getDisplayValue()).append("\"");
			}
			sb.append(",");
			if(StringUtils.hasLength(cv.getDisplayValueDescription())){
				sb.append("\"").append(cv.getDisplayValueDescription()).append("\"");
			}
			pw.println(sb.toString());
		}
		pw.close();
	}

	
	@Override @Transactional
	public void addSuggestedValuesToVocab(String vocabularyName, List<TraitValue> traitList) {
		//First determine whether we are dealing with a custom or an 'aekos' vocab
		List<String> customVocabs = getListOfAvailableCustomTraits();
		if(customVocabs.contains(vocabularyName)){
			for(TraitValue tv : traitList){
				addCustomVocabValue(vocabularyName, tv);
			}
		}else{
			for(TraitValue tv : traitList){
				addSuggestedAekosVocabValue(vocabularyName, tv);
			}
		}
		
	}

}
