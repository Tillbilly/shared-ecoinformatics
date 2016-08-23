package au.edu.aekos.shared;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.PageAnswersModel;
import au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.citation.CitationStringServiceImpl;

/**
 * Provides a mini-DSL to make building test data easier
 */
public class QuestionnaireObjectBuilders {
	private QuestionnaireObjectBuilders() {}
	
	/**
	 * Creates an answer map. Initially used in the {@link CitationStringServiceImpl} class.
	 * 
	 * @param answers	The answers to place inside the result
	 * @return			An answer map
	 */
	public static Map<String, Answer> answerMap(Answer...answers){
		Map<String, Answer> result = new HashMap<String, Answer>();
		for (Answer currAnswer : answers) {
			result.put(currAnswer.getQuestionId(), currAnswer);
		}
		return result;
	}
	
	/**
	 * Creates a new DisplayQuestionnaire from the supplied config and populates it with the supplied answers.
	 * 
	 * @param config		config to use
	 * @param answerMap		answers to prepropulate
	 * @return				new DisplayQuestionnaire instance
	 */
	public static DisplayQuestionnaire dqWithConfigAndAnswers(QuestionnaireConfig config, Map<String, Answer> answerMap) {
		DisplayQuestionnaire result = new DisplayQuestionnaire(config);
		PageAnswersModel pageAnswers = new PageAnswersModel();
		pageAnswers.setAnswers(answerMap);
		result.getPages().get(0).setPageAnswers(pageAnswers);
		return result;
	}
	
	/**
	 * Creates an answer that represents an answer to a multiple question group
	 * 
	 * @param questionGroup		The question group that this relates to (i.e. is answering)
	 * @param answerGroups		The repeating groups to place inside the result
	 * @return					The built answer
	 */
	public static Answer multipleAnswerGroup(MultipleQuestionGroup questionGroup, AnswerGroupPlaceholder...answerGroups) {
		Answer result = new Answer(questionGroup);
		for (int i=0; i < answerGroups.length;i++) {
			AnswerGroupPlaceholder currPlaceholder = answerGroups[i];
			Map<String, Answer> answerGroup;
			try {
				answerGroup = result.getAnswerSetList().get(i);
			} catch (IndexOutOfBoundsException e) {
				Map<String, Answer> newAnswerGroup = new HashMap<String, Answer>();
				result.getAnswerSetList().add(i, newAnswerGroup);
				answerGroup = result.getAnswerSetList().get(i);
			}
			for (Answer currAnswer : currPlaceholder.answers) {
				answerGroup.put(currAnswer.getQuestionId(), currAnswer);
			}
		}
		return result;
	}
	
	/**
	 * Creates a basic single answer
	 * 
	 * @param questionId		The question that is being answered
	 * @param response			The answer code value
	 * @return					The answer object
	 */
	public static Answer answer(String questionId, String response) {
		Answer result = new Answer();
		result.setQuestionId(questionId);
		result.setResponse(response);
		return result;
	}
	
	/**
	 * Creates an answer that simulates selecting OTHER and suggesting a value
	 * 
	 * @param questionId			question that is being answered
	 * @param suggestedResponse		value in text box that would appear on the UI
	 * @return						answer object
	 */
	public static Answer answerOther(String questionId, String suggestedResponse) {
		Answer result = new Answer();
		result.setQuestionId(questionId);
		result.setResponse("OTHER");
		result.setSuggestedResponse(suggestedResponse);
		return result;
	}
	
	/**
	 * Creates a placeholder with the required information to populate an
	 * answer to a multiple question group
	 * 
	 * @param answers	The answer to place inside the result
	 * @return			The built placeholder
	 */
	public static AnswerGroupPlaceholder answerGroup(Answer...answers) {
		return new AnswerGroupPlaceholder(answers);
	}
	
	public static class AnswerGroupPlaceholder {
		private final Answer[] answers;
		
		public AnswerGroupPlaceholder(Answer[] answers) {
			this.answers = answers;
		}
		
		public Answer[] getAnswers() {
			return answers;
		}
	}
	
	/**
	 * A shorthand way to create a SubmissionAnswer
	 * 
	 * @param questionId	ID of the question this object answers
	 * @param response		Response text that the virtual user has entered
	 * @param metatag		Metatag of the question this object answers
	 * @return				A built SubmissionAnswer
	 */
	public static SubmissionAnswer submissionAnswer(String questionId, String response, String metatag) {
		SubmissionAnswer result = new SubmissionAnswer();
		result.setQuestionId(questionId);
		result.setResponse(response);
		result.setMetaTag(metatag);
		return result;
	}
	
	/**
	 * Helper for loading an XML questionnaire config from <code>src/test/resources</code>
	 * 
	 * @param filename	Name of the file in <code>src/test/resources</code> e.g. mySpecialTestQuestionnaire.xml
	 * @return			The deserialised config
	 */
	public static QuestionnaireConfig getConfig(String filename) {
		try {
			JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
			Unmarshaller un = context.createUnmarshaller();
			return (QuestionnaireConfig) un.unmarshal( new File("src/test/resources/" + filename) );
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Finds the MultipleQuestionGroup with the supplied ID or explodes if it can't find it.
	 * 
	 * @param config	searches in here for the question
	 * @param id		ID of the question to find (must be of type MultipleQuestionGroup)
	 * @return			question if found, otherwise an exception is thrown
	 */
	public static MultipleQuestionGroup getMultipleQuestionGroupWithId(QuestionnaireConfig config, String id) {
		List<MultipleQuestionGroup> mqgList = config.getAllMultipleQuestionGroups();
		for(MultipleQuestionGroup mqg : mqgList){
			if(id.contentEquals(mqg.getId())){
				return mqg;
			}
		}
		throw new RuntimeException("Data error: couldn't find a MultipleQuestionGroup with ID: " + id);
	}
}
