package au.edu.aekos.shared.data.infomodel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.QuestionSetEntity;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;


/**
 * Creates a fully hydrated meta info extractor
 * @author btill
 */
@Component
public class MetaInfoExtractorFactory {

	private static final Logger logger = LoggerFactory.getLogger(MetaInfoExtractorFactory.class);
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private MetatagEvolutionConfig evolutionConfig;
	
	@Autowired
	private ControlledVocabularyService controlledVocabService;
	
	/* Caching requires some special treatment with regards to removing and reinstalling 
	 * the controlled vocabulary service reference, and useDisplayValues flag.
	 */
	public static final String CACHE_NAME = "metainfo_extractor_cache";
	private final Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
	
	@Transactional
	public MetaInfoExtractor getInstance(Long submissionId ) throws Exception{
		return getInstance(submissionId, false);
	}
	
	@Transactional
	public MetaInfoExtractor getInstance(Long submissionId, boolean  useVocabDisplayValues) throws Exception{
		Submission submission = submissionDao.findById(submissionId);
		return getInstance(submission, useVocabDisplayValues );
	}
	
	@Transactional
	public MetaInfoExtractor getInstance(Submission submission) throws Exception{
		return getInstance(submission, false);
	}
	
	/**
	 * The cached version needs to re-inject the controlled vocab service and the useVocabDisplayValues setting,
	 * hopefully all of the SubmissionAnswers are fully hydrated before cache serialisation ( we shall see . . . )
	 * @param submission
	 * @param useVocabDisplayValues
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public MetaInfoExtractor getInstance(Submission submission, boolean  useVocabDisplayValues) throws Exception{
		MetaInfoExtractor cachedMie = retrieveFromCache(submission.getId(), useVocabDisplayValues );
		if(cachedMie != null){
			return cachedMie;
		}
		
		Submission hydratedSubmission = submissionDao.findSubmissionByIdEagerAnswer(submission.getId() );
		//Hydrate collections by using size
		for(SubmissionAnswer sa : hydratedSubmission.getAnswers()){
			if(sa.getMultiselectAnswerList() != null){
				sa.getMultiselectAnswerList().size();
			}
			if(sa.getQuestionSetList() != null && sa.getQuestionSetList().size() > 0){
				for(QuestionSetEntity qse : sa.getQuestionSetList()){
					qse.getAnswerMap().size();
				}
			}
			if(ResponseType.IMAGE.equals(sa)){
				sa.getAnswerImage();
			}
		}
		QuestionnaireConfig questionnaireConfig = questionnaireConfigService.getQuestionnaireConfig(submission);
		Map<String,String> metatagToQuestionIdMap = questionnaireConfigService.getMetatagToQuestionIdMap(hydratedSubmission);
		Map<String, SubmissionAnswer> questionIdToSubmissionAnswerMap = hydratedSubmission.getAnswersMappedByQuestionId();
		Map<String, List<SubmissionAnswer>> qsaMap = hydratedSubmission.getQuestionIdToQuestionSetAnswerMap();
		//Hydrate all of the file information too
		if(hydratedSubmission.getSubmissionDataList() != null && hydratedSubmission.getSubmissionDataList().size() > 0){
			for(SubmissionData sd : hydratedSubmission.getSubmissionDataList() ){
				sd.getFileSystemStorageLocations().size();
			}
		}
		MetaInfoExtractor mie = new MetaInfoExtractor(submission, questionnaireConfig, metatagToQuestionIdMap, questionIdToSubmissionAnswerMap, qsaMap, evolutionConfig);
		addToCache(submission.getId(), mie);
		mie.setControlledVocabService(controlledVocabService);
		mie.setUseVocabDisplayValues(useVocabDisplayValues);
		return mie; //new MetaInfoExtractor(submission, questionnaireConfig, metatagToQuestionIdMap, questionIdToSubmissionAnswerMap, qsaMap, evolutionConfig, controlledVocabService, useVocabDisplayValues);
	}
	
	public void removeSubmissionFromCache(Long submissionId){
		MetaInfoExtractorCacheKey key = new MetaInfoExtractorCacheKey(submissionId);
		logger.info("MetaInfoExtractor for submission ID:" + submissionId.toString() + " manually removed from cache");
		cache.remove(key);
	}
	
	private MetaInfoExtractor retrieveFromCache(Long submissionId, boolean useVocabDisplayValues ){
		MetaInfoExtractorCacheKey key = new MetaInfoExtractorCacheKey(submissionId);
		Element el = cache.get(key);
		if(el != null){
			logger.info("MetaInfoExtractor for submission ID:" + submissionId.toString() + " retrieved from cache");
			MetaInfoExtractor mie = ( MetaInfoExtractor) el.getObjectValue(); 
			mie.setControlledVocabService(controlledVocabService);
			mie.setUseVocabDisplayValues(useVocabDisplayValues);
			return mie;
		}
		return null;
	}
	
	private void addToCache(Long submissionId, MetaInfoExtractor mie){
	    mie.setControlledVocabService(null);
	    cache.put(new Element(new MetaInfoExtractorCacheKey(submissionId), mie));
	    logger.info("MetaInfoExtractor for submission ID:" + submissionId.toString() + " added to cache");
    }
	
	private class MetaInfoExtractorCacheKey implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private Long submissionId;
		
		public MetaInfoExtractorCacheKey(Long submissionId){
			super();
			this.submissionId = submissionId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((submissionId == null) ? 0 : submissionId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetaInfoExtractorCacheKey other = (MetaInfoExtractorCacheKey) obj;
			if (submissionId == null) {
				if (other.submissionId != null)
					return false;
			} else if (!submissionId.equals(other.submissionId))
				return false;
			return true;
		}
	}
}
