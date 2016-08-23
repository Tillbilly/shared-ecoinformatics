package au.edu.aekos.shared.service.quest;

import java.util.Map;

import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidationException;

public class PlaceholderQuestionnaireConfigServiceImpl implements QuestionnaireConfigService {

	@Override
	public QuestionnaireConfig readQuestionnaireConfig(String configFileName,
			boolean persistIfNew, boolean validateTraits) throws Exception {
		return null;
	}

	@Override
	public QuestionnaireConfig getQuestionnaireConfig(String fileName,
			String version, boolean validateTraits) throws Exception {
		return null;
	}

	@Override
	public QuestionnaireConfig getQuestionnaireConfig() throws Exception {
		return null;
	}

	@Override
	public QuestionnaireConfig getQuestionnaireConfig(Submission sub)
			throws Exception {
		return null;
	}

	@Override
	public QuestionnaireConfigEntity getQuestionnaireConfigEntity(
			Long questionnaireConfigId) {
		return null;
	}

	@Override
	public QuestionnaireConfig parseConfigXML(
			QuestionnaireConfigEntity configEntity) throws Exception {
		return null;
	}

	@Override
	public void validateConfig(QuestionnaireConfig config,
			boolean validateTraits)
			throws QuestionnaireConfigValidationException {
		
	}

	@Override
	public Map<String, String> getMetatagToQuestionIdMap(Submission sub)
			throws Exception {
		return null;
	}

	@Override
	public Map<String, String> getMetatagToQuestionIdMap(
			Long questionnaireConfigId) throws Exception {
		return null;
	}

	@Override
	public Boolean isQuestionnaireConfigLatestVersion(
			Long questionnaireConfigEntityId) {
		return null;
	}

	@Override
	public QuestionnaireConfigEntity getLatestQuestionnaireConfigEntity() {
		return null;
	}
}
