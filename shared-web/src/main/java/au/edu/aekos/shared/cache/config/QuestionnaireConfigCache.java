package au.edu.aekos.shared.cache.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class QuestionnaireConfigCache {

    private static final String CACHE_NAME= "questionnaire_config_cache";
	private Cache configCache = null;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	@Qualifier(value="taskExecutor")
	private AsyncTaskExecutor taskExecutor;
	
	private Cache getCache(){
		if(configCache == null){
		    CacheManager cacheManager = CacheManager.getInstance();
		    configCache = cacheManager.getCache(CACHE_NAME);
		}
		return configCache;
	}
	
	public QuestionnaireConfigElement getQuestionnaireConfigBySubmissionId(Long submissionId){
		Element el = getCache().get(submissionId);
		if(el == null){
			initialiseEntryInCache(submissionId);
			el = getCache().get(submissionId);
		}
		if(el != null){
			return (QuestionnaireConfigElement) el.getObjectValue();
		}
		return null;
	}
	
	private void initialiseEntryInCache(Long submissionId){
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		if(sub != null){
			try {
				QuestionnaireConfig qc = questionnaireConfigService.parseConfigXML(sub.getQuestionnaireConfig());
				if(qc != null){
					QuestionnaireConfigElement qce = new QuestionnaireConfigElement(qc);
					Element el = new Element(submissionId, qce);
					getCache().put(el);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void initCacheEntry(Long submissionId, QuestionnaireConfig qc){
		if(! getCache().isKeyInCache(submissionId)){
			QuestionnaireConfigElement qce = new QuestionnaireConfigElement(qc);
			Element el = new Element(submissionId, qce);
			getCache().put(el);
		}
	}
	
	public void asyncInitialiseEntryInCache(Long submissionId){
		if(! getCache().isKeyInCache(submissionId)){
		    taskExecutor.execute(new InitialiseCacheTask(submissionId));
		}
	}
	
    private class InitialiseCacheTask implements Runnable {
		
        private Long submissionId;
		
		public InitialiseCacheTask(Long submissionId){
			this.submissionId = submissionId;
		}
		
		@Override
		public void run() {
			initialiseEntryInCache(submissionId);
		}
	}
}
