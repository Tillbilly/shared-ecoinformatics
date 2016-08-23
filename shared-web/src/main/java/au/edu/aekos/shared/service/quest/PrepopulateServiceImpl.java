package au.edu.aekos.shared.service.quest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.service.security.SecurityService;

@Service
public class PrepopulateServiceImpl implements PrepopulateService{

	private static Logger logger = LoggerFactory.getLogger(PrepopulateServiceImpl.class);
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	/**
	 * Currently only prepopulating data set contact details
	 * retrieved from the user details,
	 * if this changes, look at abstracting this stuff out a bit,
	 * make it configurable
	 * 
	 * Utilises the metatags in the questionnaire configuration.
	 */
	public void prepopulateQuestionnaire(DisplayQuestionnaire dq) {
		SharedUser su = securityService.getCurrentUser();
		Map<String,String> metatagToQIDMap = dq.getConfig().getMetatagToQuestionIdMap();
		Map<String,Answer> answerMap = dq.getAllAnswers();
		assignStringAnswer(su.getFullName(), metatagConfig.getContactNameMetatag(), metatagToQIDMap, answerMap);
		assignStringAnswer(su.getCleanedPostalAddress(), metatagConfig.getContactPostalAddressMetatag(), metatagToQIDMap, answerMap);
		assignStringAnswer(su.getEmailAddress(), metatagConfig.getContactEmailMetatag(), metatagToQIDMap, answerMap);
		assignStringAnswer(su.getPhoneNumber(), metatagConfig.getContactPhoneNumberMetatag(), metatagToQIDMap, answerMap);
		if(StringUtils.hasLength(su.getOrganisation() )){
			if( controlledVocabularyService.traitListContainsValue( ControlledVocabularyService.ORGANISATION_TRAIT  , false, su.getOrganisation() ) ){
				assignStringAnswer(su.getOrganisation(), metatagConfig.getContactOrganisationMetatag(), metatagToQIDMap, answerMap);
			}else{
				Answer a = assignStringAnswer("OTHER", metatagConfig.getContactOrganisationMetatag(), metatagToQIDMap, answerMap);
				if(a!=null){
					a.setSuggestedResponse(su.getOrganisation());
				}
			}
		}
	}
	
	private Answer assignStringAnswer(String val, String metatag, Map<String,String> metatagToQIDMap, Map<String,Answer> answerMap){
		String questionId = metatagToQIDMap.get(metatag);
		if(!StringUtils.hasLength(questionId) ){
			logger.error("Metatag " + metatag + " not found in current Questionnaire!");
			return null;
		}
		if(! answerMap.containsKey(questionId)){
			logger.error("Question Id " + questionId + " found from metatag " + metatag + " not found in current answerMap!");
			return null;
		}
		Answer a = answerMap.get(questionId);
		a.setResponse(val);
		return a;
	}
}
