package au.edu.aekos.shared.service.doi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SubmissionDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.doiclient.jaxb.ContributorType;
import au.edu.aekos.shared.doiclient.jaxb.DescriptionType;
import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.doiclient.service.DoiClientConfig;
import au.edu.aekos.shared.doiclient.service.DoiClientService;
import au.edu.aekos.shared.doiclient.service.DoiClientServiceException;
import au.edu.aekos.shared.doiclient.util.ResourceBuilder;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;

@Service
public class DoiMintingServiceImpl implements DoiMintingService{

	private Logger logger = LoggerFactory.getLogger(DoiMintingServiceImpl.class);
	
	@Autowired
	private DoiClientService doiClientService;
	
	@Autowired
	private DoiDataConfig doiDataConfig;

	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	@Autowired
	private SubmissionDao submissionDao;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Value("${doi.urlsuffix}")
	private String urlSuffix = "/dataset/";
	
	@Override @Transactional(propagation=Propagation.REQUIRES_NEW)
	public String mintDoiForSubmission(Submission sub)
			throws DoiMintingException {
		Submission submission = submissionDao.findById(sub.getId());
		Resource res = null;
		try {
			res = buildResourceForSubmission(submission);
		} catch (MetaInfoExtractorException e) {
			logger.error("Error occured extracting metainfo", e);
			throw new DoiMintingException(e);
		}
		String doi = null;
		try {
			doi = doiClientService.mintDoi(res, urlSuffix + submission.getId().toString());
		} catch (DoiClientServiceException e) {
			logger.error("Error occured minting doi", e);
			throw new DoiMintingException("Error occured in doi client", e);
		}
		if(StringUtils.hasLength(doi)){
			submission.setDoi(doi);
			submissionDao.saveOrUpdate(submission);
		}
		return doi;
	}
	
	@Transactional
	public Resource buildResourceForSubmission(Submission sub) throws MetaInfoExtractorException {
		MetaInfoExtractor metaInfoExtractor = null;
		try{
		    metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub);
		}catch(Exception e){
			throw new MetaInfoExtractorException("Error occurred initialising meta info extractor", e);
		}
		ResourceBuilder builder = new ResourceBuilder();
		builder.setPublisher(doiDataConfig.getPublisher());
		setTitle(builder, metaInfoExtractor);
		setSubjects(builder, metaInfoExtractor);
		setPublicationYear(builder, metaInfoExtractor);
		//setContactPerson(builder, metaInfoExtractor);
		setCreators(builder, metaInfoExtractor);
		setDescription(builder, metaInfoExtractor);
		//addAlternateIdentifiers(builder, metatagToQuestionIdMap, sub);
		return builder.getResource();
	}
	
	private void setTitle(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String title = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetNameMetatag());
		builder.setTitle(title);
	}
	
	@SuppressWarnings("unused")
	private void setContactPerson(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String contactPerson = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactNameMetatag());
		if(StringUtils.hasLength(contactPerson)){
		    builder.addContributor(contactPerson, ContributorType.CONTACT_PERSON);
		}
	}
	
	private void setSubjects(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		//Field of research tags
		List<String> responseList = metaInfoExtractor.getResponsesFromMultiselectTag(metatagConfig.getAnzsrcforMetatag());
		for(String response : responseList){
		    String displayStr = controlledVocabularyService.getDisplayStringForTraitValue(doiDataConfig.getAnzsrcforTraitName(), true, response);
		    if(StringUtils.hasLength(displayStr)){
		    	builder.addSubject(displayStr);
		    }else if(StringUtils.hasLength(response)){
		    	builder.addSubject(response);
		    }
		}
		//Now to similarly process all of the subjects contained in the custom subject tag list.
		for(String subjectTag: doiDataConfig.getSubjectTagList()){
			List<String> subjectResponseList = metaInfoExtractor.getResponsesFromMultiselectTag(subjectTag);
			for(String response : subjectResponseList){
				if(StringUtils.hasLength(response)){
					builder.addSubject(response);
				}
			}
		}
	}
	
	private void setPublicationYear(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
			String response = metaInfoExtractor.getDatasetPublicationYear();
			if(StringUtils.hasLength(response)){
				builder.setPublicationYear(response);
			}
	}
	
	/* Currently nopt an explicit field in the submission
	private void setPublicationYearOld(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String response = metaInfoExtractor.getSingleResponseForMetatag(doiMetatagConfig.getPublicationYearTag());
		if(StringUtils.hasLength(response)){
			builder.setPublicationYear(response);
		}
   }
	*/
	
	private void setCreators(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		List<String> creatorNameList = metaInfoExtractor.getAuthorNameList(metatagConfig.getAuthorGivenNameMetatag(), metatagConfig.getAuthorSurnameMetatag());
		if(creatorNameList == null || creatorNameList.size() == 0){
            logger.error("Null creator list - using default creator");
            creatorNameList.add("Shared Aekos");
		}
		for(String creator : creatorNameList){
			if(StringUtils.hasLength(creator)){
				builder.addCreator(creator);
			}
		}
	}
	
	private void setDescription(ResourceBuilder builder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String description = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetDescriptionMetatag());
		if(!StringUtils.hasLength(description)){
			return;
		}
		//Do we need to split up the description lines???
		//List<String> descriptionLines = new ArrayList<String>();
		//buildDescriptionLineArray(description, descriptionLines, 80);
        builder.addDescription(description, DescriptionType.ABSTRACT);
    }
	@Deprecated //Until we get definitive version numbers, take this out - it stopped working suddenly
	//on the 28/11/2013
	private void buildDescriptionLineArray(String description, List<String> descriptionLines, int charPerLine){
		String [] words = description.split(" ");
		String line = "";
		for(String word : words){
			if(StringUtils.hasLength(word)){
				line += word + " ";
				if(line.length() > charPerLine){
					descriptionLines.add(line.trim());
					line = "";
				}
			}
		}
		if(StringUtils.hasLength(line)){
		    descriptionLines.add(line.trim());
		}
	}

	//The REQUIRES_NEW transaction propagation kills junit!!
	//Calling this method wraps the sub method in the existing transaction instead.
	@Transactional(propagation=Propagation.REQUIRED)
	public String testMintDoiForSubmission(Submission sub)
			throws DoiMintingException {
		return mintDoiForSubmission(sub);
	}

	@Override
	public String getDoiLandingPageBaseUrl() {
		DoiClientConfig config = doiClientService.getDoiClientConfig();
		String urlBase = "";
		if(config != null){ //Really should be not null in SHaRED
			urlBase = config.getTopLevelUrl();
		}
		return urlBase + urlSuffix;
	}

	void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}
}
