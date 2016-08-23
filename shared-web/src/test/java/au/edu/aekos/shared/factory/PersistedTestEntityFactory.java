package au.edu.aekos.shared.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.aekos.shared.data.dao.SharedUserDao;
import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.SharedUser;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.Answer;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.jaxb.Question;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionService;


/**
 * Transactions must be started before calling methods in this class
 * @author a1042238
 *
 */
@Component
public class PersistedTestEntityFactory {
	
	@Autowired
	private QuestionnaireConfigService configService;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private SharedUserDao userDao;
	
	
	
	public Submission getDefaultTestSubmissionEntity(String username, SubmissionStatus status) throws Exception{
		QuestionnaireConfig qc = configService.getQuestionnaireConfig("test-questionnaire.xml", "0.1", false);
		
		DisplayQuestionnaire dc = new DisplayQuestionnaire(qc);
		populateTestQuestionnaireAnswers1(dc, false);
		//Need to add at least 1 submission file!! File does`nt actually have to exist
		SubmissionData sd = new SubmissionData();
		sd.setFileName("testfile.zip");
		sd.setFileDescription("its a test file that does`nt exist");
		sd.setFileSizeBytes(new Long(500000));
		
		//Make up a dodgy file system storage location
		FileSystemStorageLocation sl = new FileSystemStorageLocation();
		sl.setFspath("/this/is/madeup");
		sl.setObjectName("testObject.jpg");
		sd.getStorageLocations().add(sl);
		
		dc.getSubmissionDataList().add(sd);
		
		Long subId = submissionService.createNewSubmission(username , dc, status);
		return submissionDao.findById(subId);
	}
	
	public SharedUser createUserWithUsername(String username){
		SharedUser newUser = new SharedUser();
		newUser.setUsername(username);
		newUser.setEmailAddress("test@testy.com");
		userDao.save(newUser);
		return newUser;
	}
	
	
	private void populateTestQuestionnaireAnswers1(DisplayQuestionnaire dc, boolean populateNonMandatory){
		Map<String,Answer> answerMap = dc.getAllAnswers();
		Map<String,Question> questionMap = dc.getAllQuestionsMapFromConfig(); //To see if the answer is mandatory
		for(Answer answer : answerMap.values()){
			Question q = questionMap.get( answer.getQuestionId() );
			if(q.getResponseMandatory() || populateNonMandatory){
				switch(answer.getResponseType()){
				case TEXT :
					answer.setResponse("TEST RESPONSE");
					break;
				case CONTROLLED_VOCAB:
					answer.setResponse( q.getDefaultVocabulary().getListEntries().get(0).getValue() );
					break;
				case CONTROLLED_VOCAB_SUGGEST:
					answer.setResponse("OTHER");
					answer.setSuggestedResponse("Test suggested");
					break;
				case DATE:
					answer.setResponse("12/12/2002");
					break;
				case YES_NO:
					answer.setResponse("YES");
				case IMAGE:
//					ImageAnswer ia = new ImageAnswer()
//					ia.
//					
//					dc.getImageAnswerMap().put(key, value)
					break;
					
				case MULTISELECT_TEXT:
					answer.getMultiselectAnswerList().clear();
					Answer multi1 = new Answer();
					multi1.setResponseType(ResponseType.getRawType(answer.getResponseType()));
					multi1.setResponse("TEST MULTI1");
					multi1.setQuestionId(answer.getQuestionId());
					answer.getMultiselectAnswerList().add(multi1);
					
					Answer multi2 = new Answer();
					multi2.setResponseType(ResponseType.getRawType(answer.getResponseType()));
					multi2.setResponse("TEST MULTI2");
					multi2.setQuestionId(answer.getQuestionId());
					answer.getMultiselectAnswerList().add(multi2);
					break;
					
				default: break;	
				}
			}
		}
	}
	
	
	
	

}
