package au.edu.aekos.shared.data.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionPublicationStatus;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.entity.storage.FileSystemStorageLocation;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

@Repository
@SuppressWarnings("unchecked")
public class SubmissionDaoImpl extends AbstractHibernateDao<Submission, Long> implements
		SubmissionDao {

	@Autowired
	private SubmissionDataDao submissionDataDao;
	
	@Override
	public Class<Submission> getEntityClass() {
		return Submission.class;
	}

	public Submission findSubmissionByIdEagerAnswer(Long submissionId){
		Criteria c = createCriteria().setFetchMode("answers", FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.add(Restrictions.eq("id", submissionId));
		return (Submission) c.uniqueResult();
	}
	
	//Gives - org.hibernate.loader.MultipleBagFetchException - removed from the interface.
	public Submission findSubmissionByIdEagerAnswerAndDataFile(Long submissionId) {
		Criteria c = createCriteria().setFetchMode("answers", FetchMode.JOIN)
				        .setFetchMode("submissionDataList", FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		c.add(Restrictions.eq("id", submissionId));
		return (Submission) c.uniqueResult();
	}
	
	@Override
	public List<Submission> findSubmissionsByUsername(String username) {
		Criteria c = createCriteria();
		Criteria submitterCriteria = c.createCriteria("submitter");
		submitterCriteria.add( Restrictions.eq("username", username) );
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@Override
	public SubmissionData retrieveSubmissionDataForSubmission(
			Long submissionId, Long submissionDataId) {
		Submission s = findById(submissionId);
		if( s != null){
			s.getSubmissionDataList().size();
			for(SubmissionData sd :  s.getSubmissionDataList()){
				if(submissionDataId.equals(sd.getId())){
					return submissionDataDao.findByIdEagerLocations(submissionDataId);
				}
			}
			
		}
		return null;
	}
	
	@Override
	public List<Submission> retrieveSubmissionsForReview( String usernameToExclude ) {
		
		String hql = "from submission s where s.status in :status_list ";
		if(usernameToExclude != null){
			hql += " and s.submitter.id <> :usernameToExclude ";
		}
		
		Query q = getCurrentSession().createQuery(hql);
		
		List<SubmissionStatus> statusList = new ArrayList<SubmissionStatus>();
		statusList.add(SubmissionStatus.SUBMITTED);
		statusList.add(SubmissionStatus.RESUBMITTED);
		statusList.add(SubmissionStatus.PEER_REVIEWED);
		statusList.add(SubmissionStatus.REJECTED);
		statusList.add(SubmissionStatus.REJECTED_SAVED);
		q.setParameterList("status_list", statusList);
		
		if(usernameToExclude != null){
			q.setString("usernameToExclude", usernameToExclude );
		}
		
		return q.list();
	}

	@Override
	public void updateSubmissionStatus(Long submissionId, SubmissionStatus submissionStatus) {
		Submission sub = findById(submissionId);
		if(sub != null){
			sub.setStatus(submissionStatus);
			save(sub);
		}
	}
	
	public void updateSubmissionStatusAndLastReviewDate(Long submissionId, SubmissionStatus status, Date reviewDate){
		Submission sub = findById(submissionId);
		if(sub != null){
			sub.setStatus(status);
			sub.setLastReviewDate(reviewDate);
			save(sub);
		}
	}

	@Override
	public Submission retrieveSubmissionByTitleAndUsername(
			String submissionTitle, String username) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("title",submissionTitle));
		c.add(Restrictions.ne("status", SubmissionStatus.DELETED));  //Exclude deleted submissions
		Criteria submitterCriteria = c.createCriteria("submitter");
		submitterCriteria.add( Restrictions.eq("username", username) );
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Submission) c.uniqueResult();
	}

	@Override
	public List<Submission> getListOfPublicationFailedSubmissions() {
		Criteria c = createCriteria();
		c.add(Restrictions.isNotNull("publicationStatus") ).
		  add(Restrictions.ne("publicationStatus", SubmissionPublicationStatus.PUBLISHED))
		  .add( Restrictions.eq( "status",SubmissionStatus.APPROVED));
		return c.list();
	}

	@Override
	public List<Submission> getSubmissionsWithVocabResponse(String response) {
		//Need to do three queries here 
		//- when a response is a direct submission answer,  part of a multiselect answer
		// Or part of a question set answer
		
		//Seeing as this does'nt need to be efficient ( part of an admin function )
		//I`ll do this as 3 seperate criteria queries - in the first instance. 
		
		//Where response is a direct submission answer
		Criteria c = createCriteria();
		Criteria answerCriteria = c.createCriteria("answers");
		answerCriteria.add(Restrictions.eq("response", response));
		ResponseType [] responseTypeArray = { ResponseType.CONTROLLED_VOCAB , ResponseType.CONTROLLED_VOCAB_SUGGEST };
		answerCriteria.add(Restrictions.in("responseType", responseTypeArray));
		List<Submission> directSubmissions = c.list();
		
		
		//Where the response is part of a multiselect answer
		Criteria cMS = createCriteria();
		Criteria answerCriteriaMS = cMS.createCriteria("answers");
		ResponseType [] responseTypeArrayMS = { ResponseType.MULTISELECT_CONTROLLED_VOCAB , ResponseType.MULTISELECT_CONTROLLED_VOCAB_SUGGEST, ResponseType.TREE_SELECT };
		answerCriteriaMS.add(Restrictions.in("responseType", responseTypeArrayMS));
		Criteria answerCriteriaSubMS = answerCriteriaMS.createCriteria("multiselectAnswerList");
		answerCriteriaSubMS.add(Restrictions.eq("response", response));
		//answerCriteriaSubMS.add(Restrictions.in("responseType", responseTypeArray));
		List<Submission> msSubmissions = cMS.list();
		
		//Response part of a question set.
		List<Submission> questionSetSubs = getSubmissionsContainingVocabResponseInQuestionSet(response);
		
		Set<Long> submissionIdsAdded = new HashSet<Long>();
		List<Submission> submissionsToReturn = new ArrayList<Submission>();
		addSubmissionsUnique(directSubmissions, submissionIdsAdded, submissionsToReturn );
		addSubmissionsUnique(msSubmissions, submissionIdsAdded, submissionsToReturn );
		addSubmissionsUnique(questionSetSubs, submissionIdsAdded, submissionsToReturn );
		return submissionsToReturn;
	}
	
	private List<Submission> getSubmissionsContainingVocabResponseInQuestionSet(String response){
		//Where the response is part of a question set
		Criteria cQS = createCriteria();
		Criteria answerCriteriaQS = cQS.createCriteria("answers");
		answerCriteriaQS.add(Restrictions.eq("responseType", ResponseType.MULTIPLE_QUESTION_GROUP));
		Criteria qsc = answerCriteriaQS.createCriteria("questionSetList");
		Criteria saCriteria = qsc.createCriteria("answerMap");
		saCriteria.add(Restrictions.eq("response", response));
		ResponseType [] responseTypeArray = { ResponseType.CONTROLLED_VOCAB , ResponseType.CONTROLLED_VOCAB_SUGGEST };
		saCriteria.add(Restrictions.in("responseType", responseTypeArray));
		
		List<Submission> subList1 = cQS.list();
		List<Submission> subList2 = getSubmissionsContainingVocabResponseInQuestionSetMultiple(response);
		
		//Return only submissions with unique IDs
		Set<Long> submissionIdsAdded = new HashSet<Long>();
		List<Submission> submissionsToReturn = new ArrayList<Submission>();
		addSubmissionsUnique(subList1, submissionIdsAdded, submissionsToReturn );
		addSubmissionsUnique(subList2, submissionIdsAdded, submissionsToReturn );
				
	    return submissionsToReturn;	
	}
		
	private void addSubmissionsUnique(List<Submission> submissionsToAdd, Set<Long> submissionIdsAdded, List<Submission> submissionList){
		if(submissionsToAdd != null && submissionsToAdd.size() > 0){
			for(Submission sub : submissionsToAdd){
				if(! submissionIdsAdded.contains(sub.getId()) ){
					submissionIdsAdded.add(sub.getId());
					submissionList.add(sub);
				}
			}
		}
		
	}
	
	private List<Submission> getSubmissionsContainingVocabResponseInQuestionSetMultiple(String response){
		//Where the response is part of a question set
		Criteria cQS = createCriteria();
		Criteria answerCriteriaQS = cQS.createCriteria("answers");
		answerCriteriaQS.add(Restrictions.eq("responseType", ResponseType.MULTIPLE_QUESTION_GROUP));
		Criteria qsc = answerCriteriaQS.createCriteria("questionSetList");
		Criteria saCriteria = qsc.createCriteria("answerMap");
		ResponseType [] responseTypeArrayMS = { ResponseType.MULTISELECT_CONTROLLED_VOCAB , ResponseType.MULTISELECT_CONTROLLED_VOCAB_SUGGEST };
		saCriteria.add(Restrictions.in("responseType", responseTypeArrayMS));
		Criteria answerCriteriaSubMS = saCriteria.createCriteria("multiselectAnswerList");
		answerCriteriaSubMS.add(Restrictions.eq("response", response));
		ResponseType [] responseTypeArray = { ResponseType.CONTROLLED_VOCAB , ResponseType.CONTROLLED_VOCAB_SUGGEST };
		answerCriteriaSubMS.add(Restrictions.in("responseType", responseTypeArray));
		return cQS.list();
	}

	@Override
	public List<Submission> getListOfSubmissionsForUserName(String username,
			List<SubmissionStatus> statusToInclude,
			List<SubmissionStatus> statusToExclude,
			List<Long> submissionIdsToExclude) {
		List<String> userList = new ArrayList<String>();
		userList.add(username);
		return getListOfSubmissionsForUsers(userList, statusToInclude, statusToExclude, submissionIdsToExclude);
	}
	
	@Override
	public List<Submission> getListOfSubmissionsForUsers(List<String> users,
			List<SubmissionStatus> statusToInclude,
			List<SubmissionStatus> statusToExclude,
			List<Long> submissionIdsToExclude) {
		Criteria c = createCriteria();
		c.add(Restrictions.in("submitter.username", users));
		if(statusToInclude != null && statusToInclude.size() > 0){
			c.add(Restrictions.in("status", statusToInclude));
		}
		
	    if(statusToExclude == null){
	    	statusToExclude = new ArrayList<SubmissionStatus>();
	    }
	    statusToExclude.addAll(SubmissionStatus.getDeprecatedStatuses());
		
		if(statusToExclude != null && statusToExclude.size() > 0){
			//This might generate some innefficient subquery
			//c.add(Restrictions.not( Restrictions.in("status", statusToExclude)) );
			for(SubmissionStatus subStat : statusToExclude){
				c.add(Restrictions.ne("status", subStat));
			}
		}
		if(submissionIdsToExclude != null && submissionIdsToExclude.size() > 0){
			for(Long subId : submissionIdsToExclude){
				c.add(Restrictions.ne("id", subId));
			}
		}
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	@Override
	public List<Long> findExistingSavedSubmissionIdForSubmission(Long submissionId) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("draftForSubmissionId", submissionId))
		  .add(Restrictions.ne("status", SubmissionStatus.DELETED)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Submission> subList = c.list();
		List<Long> returnList = new ArrayList<Long>();
		if(subList != null && subList.size() > 0){
			for(Submission sub : subList){
				returnList.add(sub.getId());
			}
		}
		return returnList;
	}

	@Override
	public List<Long> getListOfSubmissionIds(SubmissionStatus status) {
		Criteria c = createCriteria().add(Restrictions.eq("status", status));
		c.setProjection(Projections.distinct( Projections.property("id")) );
		c.addOrder(Order.asc("id"));
		return c.list();
	}

	@Override
	public List<Submission> getAllUndeletedSubmissions() {
		Criteria c = createCriteria();
        c.add(Restrictions.ne("status", SubmissionStatus.DELETED))
         .add(Restrictions.ne("status", SubmissionStatus.REMOVED))
         .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
	}

	@Override @Transactional
	public List<Submission> findDeletedSubmissionsWithFileSystemStorageLocations() {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("status", SubmissionStatus.DELETED));
		Criteria dataCriteria = c.createCriteria("submissionDataList", JoinType.INNER_JOIN);
		Criteria storageLocationCriteria = dataCriteria.createCriteria("storageLocations", JoinType.INNER_JOIN);
		storageLocationCriteria.add(Restrictions.eq("class", FileSystemStorageLocation.class));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Submission> deletedSubmissionList = c.list();
		for(Submission sub : deletedSubmissionList){  //Rehydrating storage locations within transaction
			for(SubmissionData sd : sub.getSubmissionDataList()){
				sd.getStorageLocations().size();
			}
		}
		return deletedSubmissionList;
	}
}
