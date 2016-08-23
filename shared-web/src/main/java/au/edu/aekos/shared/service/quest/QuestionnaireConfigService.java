package au.edu.aekos.shared.service.quest;

import java.util.Map;

import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidationException;

public interface QuestionnaireConfigService {

	QuestionnaireConfig readQuestionnaireConfig(String configFileName, boolean persistIfNew, boolean validateTraits) throws Exception;
	
	QuestionnaireConfig getQuestionnaireConfig(String fileName, String version, boolean validateTraits) throws Exception;
	
	QuestionnaireConfig getQuestionnaireConfig() throws Exception;
	
	QuestionnaireConfig getQuestionnaireConfig(Submission sub) throws Exception;
	
	QuestionnaireConfigEntity getQuestionnaireConfigEntity(Long questionnaireConfigId);
	
	QuestionnaireConfigEntity getLatestQuestionnaireConfigEntity() throws Exception;
	
	QuestionnaireConfig parseConfigXML(QuestionnaireConfigEntity configEntity) throws Exception;
	
	void validateConfig(QuestionnaireConfig config, boolean validateTraits) throws QuestionnaireConfigValidationException;
	
	Map<String,String> getMetatagToQuestionIdMap(Submission sub) throws Exception;
	
	Map<String,String> getMetatagToQuestionIdMap(Long questionnaireConfigId) throws Exception;
	
	Boolean isQuestionnaireConfigLatestVersion(Long questionnaireConfigEntityId);
	
	
	
}
