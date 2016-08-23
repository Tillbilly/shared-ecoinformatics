package au.edu.aekos.shared.service.quest;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import au.edu.aekos.shared.data.dao.QuestionnaireConfigEntityDao;
import au.edu.aekos.shared.data.entity.QuestionEntity;
import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.valid.QuestionnaireMetamodelValidator;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidationException;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidator;

@Service
public class QuestionnaireConfigServiceImpl implements QuestionnaireConfigService {
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionnaireConfigServiceImpl.class);
	
	@Value("${default.questionnaire.file.name}")
	private String defaultQuestionnaireFileName;
	
	@Value("${default.questionnaire.file.version}")
	private String defaultQuestionnaireFileVersion;
	
	@Value("${questionnaire.validate.metatags}")
	private Boolean validateMetatags = false;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private QuestionnaireConfigEntityDao configEntityDao;
	
	@Autowired
	private QuestionnaireMetamodelValidator metamodelValidator;
	
	public QuestionnaireConfig readQuestionnaireConfig(String configFileName, boolean persistIfNew, boolean validateTraits) throws Exception {
		logger.info("Attempting to read " + configFileName + " from classpath");
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    if(!StringUtils.hasLength(configFileName)){
	    	configFileName=defaultQuestionnaireFileName;
	    }
	    Resource rs = new ClassPathResource(configFileName);
        Object value = un.unmarshal(rs.getInputStream()); 
        QuestionnaireConfig config = ( QuestionnaireConfig ) value;
	    validateConfig(config, validateTraits);
	    if(persistIfNew){
	        persistConfigurationIfNew(configFileName, config);
	    }
	    return config;
	}

	public void validateConfig(QuestionnaireConfig config, boolean validateTraits) throws QuestionnaireConfigValidationException{
		QuestionnaireConfigValidator configValidator = new QuestionnaireConfigValidator();
        BindingResult br = new BeanPropertyBindingResult(config, "config"); 
        configValidator.validate(config, br);
        if(br.hasErrors()){
        	throw new QuestionnaireConfigValidationException(br);
        }
        //We also need to validate whether specified controlledVocabs exist!
        if(validateTraits){
            validateControlledVocabularyTraitNames(config);
        }
        if(validateMetatags){
        	metamodelValidator.validateQuestionnaireContainsPublishMetatags(config, br);
        	if(br.hasErrors()){
            	throw new QuestionnaireConfigValidationException(br);
            }
        }
	}
	
	private void validateControlledVocabularyTraitNames( QuestionnaireConfig config ) throws QuestionnaireConfigValidationException{
		logger.info("Validating trait vocabularies for " + config.getTitle() + " " + config.getVersion());
		
		List<String> customTraitList = controlledVocabularyService.getListOfAvailableCustomTraits();
		List<String> controlledVocabTraitList = controlledVocabularyService.getListOfAvailableTraits();
		//Need to iterate through all the questionnaireElements, keep the questions
		List<Question> failedQuestions = new ArrayList<Question>();
		for( Object obj : config.getItems().getEntryList() ){
		    if(obj instanceof Question){
		    	if( ! validateQuestionVocab(( Question) obj, customTraitList, controlledVocabTraitList) ){
		    		failedQuestions.add((Question) obj );
		    	}
		    }else if (obj instanceof QuestionGroup ){
		    	 failedQuestions.addAll( validateQuestionGroupVocabs((QuestionGroup) obj,customTraitList, controlledVocabTraitList) );
		    }else if (obj instanceof MultipleQuestionGroup ){
		    	failedQuestions.addAll ( validateMultipleQuestionGroupVocabs((MultipleQuestionGroup) obj,customTraitList, controlledVocabTraitList) );
		    }
		}
		if(failedQuestions.size() > 0){
			logger.error("Traits specified that don`t exist!");
			StringBuilder exceptionMessage = new StringBuilder("Traits specified that don`t exist!");
			for(Question failedQ : failedQuestions ){
				logger.error(failedQ.getId() + " " + failedQ.getTraitName() + " " + failedQ.getIsCustom());
				exceptionMessage.append(failedQ.getId()).append(" ").append(failedQ.getTraitName()).append(" ").append(failedQ.getIsCustom()).append(" ");
			}
			throw new QuestionnaireConfigValidationException(exceptionMessage.toString());
		}
	}
	
	/**
	 * Returns true if passes validation
	 * @param question
	 * @param customTraitList
	 * @param controlledVocabTraitList
	 * @return
	 */
	private boolean validateQuestionVocab(Question question, List<String> customTraitList, List<String> controlledVocabTraitList ){
		if(question.getResponseType().equals(ResponseType.CONTROLLED_VOCAB) ||
				question.getResponseType().equals(ResponseType.CONTROLLED_VOCAB_SUGGEST) ||
				question.getResponseType().equals(ResponseType.MULTISELECT_CONTROLLED_VOCAB ) ){
            if(question.getIsCustom() != null && question.getIsCustom()){
            	return customTraitList.contains( question.getTraitName() );
            }else{
            	return controlledVocabTraitList.contains(question.getTraitName());
            }
		}
		return true;
	}
	
    private List<Question> validateQuestionGroupVocabs(QuestionGroup questionGroup, List<String> customTraitList, List<String> controlledVocabTraitList ){
		List<Question> failedQuestions = new ArrayList<Question>();
		for(Object obj : questionGroup.getElements() ){
			if(obj instanceof Question){
		    	if( ! validateQuestionVocab(( Question) obj, customTraitList, controlledVocabTraitList) ){
		    		failedQuestions.add((Question) obj );
		    	}
		    }else if (obj instanceof QuestionGroup ){
		    	 failedQuestions.addAll( validateQuestionGroupVocabs((QuestionGroup) obj,customTraitList, controlledVocabTraitList) );
		    }else if (obj instanceof MultipleQuestionGroup ){
		    	failedQuestions.addAll ( validateMultipleQuestionGroupVocabs((MultipleQuestionGroup) obj,customTraitList, controlledVocabTraitList) );
		    }
		}
		return failedQuestions;
	}
	
    private List<Question> validateMultipleQuestionGroupVocabs(MultipleQuestionGroup multipleQuestionGroup, List<String> customTraitList, List<String> controlledVocabTraitList ){
		List<Question> failedQuestions = new ArrayList<Question>();
		for(Object obj : multipleQuestionGroup.getElements()){
			if(obj instanceof Question){
		    	if( ! validateQuestionVocab(( Question) obj, customTraitList, controlledVocabTraitList) ){
		    		failedQuestions.add((Question) obj );
		    	}
			}
		}
		return failedQuestions;
	}
	
	/**
	 * Retrieves the QuestionnaireConfig using default settings - from config  shared-web.properties 
	 * @return
	 */
	@Transactional
	public QuestionnaireConfig getQuestionnaireConfig() throws Exception{
		return getQuestionnaireConfig(defaultQuestionnaireFileName, defaultQuestionnaireFileVersion, false);
	}
	
	//Preferentially read from db, unless not found in the db, then try read from classpath
	@Transactional
	public QuestionnaireConfig getQuestionnaireConfig(String fileName, String version, boolean validateTraits) throws Exception{
		logger.info("Using questionnaire " + fileName + "  version '" + version +"'");
		QuestionnaireConfigEntity configEntity = configEntityDao.getQuestionnaireConfigEntity(fileName, version);
		if(configEntity == null){
			QuestionnaireConfig qc = readQuestionnaireConfig(fileName, true, validateTraits); 
			if(! qc.getVersion().equals(version) ){
				logger.error("Questionnaire " + fileName + "  version '" + version +"' does not exist. Please check configuration.");
				throw new QuestionnaireConfigNotFoundException( "Questionnaire " + fileName + "  version '" + version + "' not found. Please check configuration.");
			}
			return qc;
		}else{
			return parseConfigXML(configEntity) ;
		}
	}
	
	public QuestionnaireConfig parseConfigXML(QuestionnaireConfigEntity configEntity) throws Exception {
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object value = un.unmarshal( new StreamSource( new StringReader( configEntity.getXml() ) ) );
        QuestionnaireConfig config = ( QuestionnaireConfig ) value;
	    validateConfig(config, false);
	    config.setSmsQuestionnaireId( configEntity.getId() );
	    return config;
	}

	@Transactional
	private void persistConfigurationIfNew(String configFileName, QuestionnaireConfig config) throws IOException {
		logger.info("Check whether to persist config file " + configFileName + " version '" + config.getVersion() +"'");
		if( configEntityDao.getQuestionnaireConfigEntity(configFileName, config.getVersion() ) == null){
			Resource rs = new ClassPathResource(configFileName);
	        String xmlStr = IOUtils.toString(rs.getInputStream());
	        QuestionnaireConfigEntity newConfigEntity = createQuestionnaireConfigEntity(configFileName, config, xmlStr);
	        logger.info("Persisting config file " + configFileName + " version '" + config.getVersion() +"'");
	        configEntityDao.save(newConfigEntity);
	        config.setSmsQuestionnaireId(newConfigEntity.getId());
		}else{
			logger.info("Config not new. Persisted configuration exists for config file " + configFileName + " version '" + config.getVersion() +"'");
		}
	}
	
	private QuestionnaireConfigEntity createQuestionnaireConfigEntity(String configFileName, QuestionnaireConfig config, String xmlStr){
		QuestionnaireConfigEntity newConfig = new QuestionnaireConfigEntity();
		newConfig.setActive(true);
		newConfig.setConfigFileName(configFileName);
		newConfig.setImportDate(new Date());
		newConfig.setVersion(config.getVersion());
		newConfig.setXml(xmlStr);
		addConfigQuestionsToConfigEntity(newConfig, config);
		return newConfig;
	}
	
	private void addConfigQuestionsToConfigEntity(QuestionnaireConfigEntity newConfig, QuestionnaireConfig config){
		for(Object obj : config.getItems().getEntryList() ){
			if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.Question){
				addQuestionToConfigEntity((Question) obj, newConfig);
			}else if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup){
				processQuestionGroup((QuestionGroup) obj, newConfig);
			}
		}
	}

	private void processQuestionGroup(QuestionGroup group, QuestionnaireConfigEntity config){
		for(Object obj : group.getItems().getEntryList() ){
			if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.Question){
				addQuestionToConfigEntity((Question) obj, config);
			}else if(obj instanceof au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup){
				processQuestionGroup((QuestionGroup) obj, config);
			}
		}
	}
	
	private void addQuestionToConfigEntity( Question question,  QuestionnaireConfigEntity config){
		QuestionEntity questionEntity = new QuestionEntity();
		questionEntity.setQuestionId(question.getId());
		questionEntity.setQuestionText(question.getText());
		questionEntity.setResponseType(question.getResponseType());
		questionEntity.setTraitName(question.getTraitName());
		questionEntity.setConfig(config);
		config.getQuestionList().add(questionEntity);
	}

	@Transactional
	public QuestionnaireConfigEntity getQuestionnaireConfigEntity(
			Long questionnaireConfigId) {
		return configEntityDao.findById(questionnaireConfigId);
	}
	
	@Transactional
	public Map<String,String> getMetatagToQuestionIdMap(Submission sub) throws Exception{
		QuestionnaireConfig qc = parseConfigXML(sub.getQuestionnaireConfig());
	    return qc.getMetatagToQuestionIdMap();
	}

	@Override
	public QuestionnaireConfig getQuestionnaireConfig(Submission sub) throws Exception{
		return parseConfigXML(sub.getQuestionnaireConfig());
	}

	@Override @Transactional
	public Map<String, String> getMetatagToQuestionIdMap(
			Long questionnaireConfigId) throws Exception {
		QuestionnaireConfigEntity config = getQuestionnaireConfigEntity(questionnaireConfigId);
		QuestionnaireConfig qc = parseConfigXML(config);
	    return qc.getMetatagToQuestionIdMap();
	}
	
	@Transactional
	public Boolean isQuestionnaireConfigLatestVersion(Long questionnaireConfigEntityId){
		QuestionnaireConfigEntity configEntity = configEntityDao.findById(questionnaireConfigEntityId);
		if(defaultQuestionnaireFileName.equals(configEntity.getConfigFileName() ) &&
				defaultQuestionnaireFileVersion.equals(configEntity.getVersion()) ){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override @Transactional
	public QuestionnaireConfigEntity getLatestQuestionnaireConfigEntity() throws Exception {
		QuestionnaireConfigEntity qce =  configEntityDao.getQuestionnaireConfigEntity(defaultQuestionnaireFileName, defaultQuestionnaireFileVersion);
		if(qce != null){
			return qce;
		}
		getQuestionnaireConfig();
		return configEntityDao.getQuestionnaireConfigEntity(defaultQuestionnaireFileName, defaultQuestionnaireFileVersion);
	}
}
