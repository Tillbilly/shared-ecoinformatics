package au.edu.aekos.shared.service.submission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.citation.CitationDataProvider;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;

@Service
public class ExampleCitationDataProviderFactory {

	private static final Logger logger = LoggerFactory.getLogger(ExampleCitationDataProviderFactory.class);
	
	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	private String doiFragment;
	private String accessDateFragment;
	private String newLineFragment;
	
	@Autowired
	private ControlledVocabularyService vocabularyService;
	
	public CitationDataProvider newExampleCitationDataProvider(DisplayQuestionnaire dq) {
		Map<String,String> metatagMap = dq.getConfig().getMetatagToQuestionIdMap();
		Map<String, Answer> answerMap = dq.getAllAnswers();
		List<String> authors = buildAuthorList(metatagMap, answerMap, dq.getConfig());
		String publicationYear = thisYear();
		String datasetVersion = getMetatagAnswer(metatagConfig.getDatasetVersionMetatag(), metatagMap, answerMap);
		String datasetName = getMetatagAnswer(metatagConfig.getDatasetNameMetatag(),metatagMap, answerMap);
		List<String> legalContactOrgs = buildLegalContactOrgList(metatagMap, answerMap, dq.getConfig());
		return new CitationDataProvider(authors, publicationYear, datasetVersion, datasetName, legalContactOrgs, 
				doiFragment, accessDateFragment, newLineFragment);
	}
	
	List<String> buildAuthorList(Map<String,String> metatagMap, Map<String, Answer> answerMap, QuestionnaireConfig config){
		String authorGivenNameMetatag = metatagConfig.getAuthorGivenNameMetatag();
		String authorSurnameMetatag = metatagConfig.getAuthorSurnameMetatag();
		String givenNameQuestionId = metatagMap.get(authorGivenNameMetatag);
		String surnameQuestionId = metatagMap.get(authorSurnameMetatag);
		MultipleQuestionGroup parentQuestion = config.getQuestionAncestor(givenNameQuestionId, MultipleQuestionGroup.class);
		if (parentQuestion == null) {
			parentQuestion = config.getQuestionAncestor(surnameQuestionId, MultipleQuestionGroup.class);
		}
		if (parentQuestion == null) {
			logger.error("Citation Error - Can't find answer for '"+ authorGivenNameMetatag + "' or '" + authorSurnameMetatag + "' in question set.");
			throw new InvalidAuthorNameQuestionException();
		}
		Answer parentAnswer = answerMap.get(parentQuestion.getId());
		List<String> result = new ArrayList<String>();
		for( Map<String, Answer> answerSet : parentAnswer.getAnswerSetList() ){
			if(questionHasNoAnswer(answerSet, surnameQuestionId)){
				logger.error("Citation Error - Can't find answer for '"+ authorSurnameMetatag + "' in question set.");
				continue;
			}
			Answer authorGivenNameAnswer = answerSet.get(givenNameQuestionId);
			Answer authorSurameAnswer = answerSet.get(surnameQuestionId);
			result.add(formatAuthorName(authorGivenNameAnswer.getResponse(), authorSurameAnswer.getResponse()));
		}
		return result;
	}

	List<String> buildLegalContactOrgList(Map<String, String> metatagMap, Map<String, Answer> answerMap, QuestionnaireConfig config) {
		String legalContactOrgMetatag = metatagConfig.getLegalContactOrgMetatag();
		String legalContactOrgQuestionId = metatagMap.get(legalContactOrgMetatag);
		if (questionDoesntExist(legalContactOrgQuestionId)) {
			return Collections.emptyList();
		}
		MultipleQuestionGroup parentQuestion = config.getQuestionById(legalContactOrgQuestionId).findAncestorOfType(MultipleQuestionGroup.class);
		if (parentQuestion == null) {
			logger.error("Citation Error - Can't find answer for '"+ legalContactOrgMetatag + "'");
			throw new InvalidContactOrgQuestionTypeException();
		}
		Answer parentAnswer = answerMap.get(parentQuestion.getId());
		List<String> result = new ArrayList<String>();
		for(Map<String, Answer> answerSet : parentAnswer.getAnswerSetList()){
			if(questionHasNoAnswer(answerSet, legalContactOrgQuestionId)){
				logger.error("Citation Error - Can't find answer for '"+ legalContactOrgMetatag + "'");
				continue;
			}
			Answer currAnswer = answerSet.get(legalContactOrgQuestionId);
			if (currAnswer.isAnsweredAsOther()) {
				result.add(currAnswer.getSuggestedResponse());
				continue;
			}
			String codeValue = currAnswer.getResponse();
			String displayValue = vocabularyService.getTraitDisplayText(metatagConfig.getLegalContactOrgTraitName(), codeValue);
			result.add(displayValue);
		}
		return result;
	}
	
	/**
	 * The user may be working on a submission that doesn't have the legal contact question so we need to be prepared for it.
	 */
	private boolean questionDoesntExist(String legalContactOrgQuestionId) {
		return legalContactOrgQuestionId == null;
	}
	
	private boolean questionHasNoAnswer(Map<String, Answer> answerMap, String questionId) {
		if (!answerMap.containsKey(questionId)) {
			return true;
		}
		if (!StringUtils.hasLength(answerMap.get(questionId).getResponse())) {
			return true;
		}
		return false;
	}

	public static String thisYear() {
		Calendar c = GregorianCalendar.getInstance();
		return Integer.toString(c.get(Calendar.YEAR));
	}
	
	private String getMetatagAnswer(String metatag, Map<String,String> metatagMap, Map<String, Answer> answerMap){
		if(metatagMap.containsKey(metatag) && 
				answerMap.containsKey(metatagMap.get(metatag)) ){
			Answer a = answerMap.get(metatagMap.get(metatag) );
			if(a.hasResponse()){
				return a.getResponse();
			}
		}
		return "";
	}
	
	static String getParentQuestionId(String questionId){
		int indx = questionId.lastIndexOf('.');
		if(indx > -1){
			return questionId.substring(0, indx);
			
		}else{
			return questionId;
		}
	}

	/**
	 * Formats the author's name correctly to be used directly in the citation string
	 * 
	 * @param initials		initials of the author
	 * @param surname		full surname
	 * @return				Name formatted to suit the citation string
	 */
	String formatAuthorName(String initials, String surname) {
		final String nullSafeSurname = StringUtils.hasLength(surname) ? surname : "";
		if (StringUtils.hasLength(initials)) {
			return nullSafeSurname + ", " + initials;
		}
		return nullSafeSurname;
	}

	public void setVocabularyService(ControlledVocabularyService vocabularyService) {
		this.vocabularyService = vocabularyService;
	}

	public void setDoiFragment(String doiFragment) {
		this.doiFragment = doiFragment;
	}

	public void setAccessDateFragment(String accessDateFragment) {
		this.accessDateFragment = accessDateFragment;
	}

	public void setNewLineFragment(String newLineFragment) {
		this.newLineFragment = newLineFragment;
	}

	public void setMetatagConfig(CommonConceptMetatagConfig metatagConfig) {
		this.metatagConfig = metatagConfig;
	}
}
