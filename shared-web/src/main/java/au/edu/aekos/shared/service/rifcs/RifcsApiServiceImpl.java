package au.edu.aekos.shared.service.rifcs;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.JSchException;

import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.meta.CommonConceptMetatagConfig;
import au.edu.aekos.shared.service.citation.CitationService;
import au.edu.aekos.shared.service.citation.CitationService.CitationDisplayType;
import au.edu.aekos.shared.service.citation.CitationStringServiceImpl;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.service.submission.SubmissionSpatialMetadataService;
import au.org.ecoinformatics.rifcs.RifcsBuilder;
import au.org.ecoinformatics.rifcs.RifcsUtils;
import au.org.ecoinformatics.rifcs.vocabs.CollectionRelationTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.CollectionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DatesTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DescriptionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.PhysicalAddressTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.SpatialTypeEnum;

/**
 * Builds a rifcs document using the rifcs-api  rifcs 1.5 schema.
 * 
 * The method 'buildRifcsXmlFromSubmission(Submission sub) is the core of the matter.
 * 
 * @author btill
 */
@Service
public class RifcsApiServiceImpl implements RifcsService{
	
	private static final Logger logger = LoggerFactory.getLogger(RifcsApiServiceImpl.class);
	private static final String DOI_URL_PREFIX = CitationStringServiceImpl.DOI_URL_PREFIX;
	/** Code that we have in our vocab for a DOI typed identifier */
	private static final String DOI_IDENTIFIER_DISPLAY_VALUE = "DOI";
	/** Name for the RelatedInfo/Identifier[type] attribute in the RIF-CS document */
	private static final String DOI_IDENTIFIER_TYPE_NAME_FOR_RIFCS = "Digital Object Identifier";
	
	@Autowired
	private SharedRifcsMappingConfig config;
	
	@Autowired
	private CommonConceptMetatagConfig metatagConfig;
	
	@Autowired
	private CitationService citationService;
	
	@Autowired
	private RifcsFileService rifcsFileService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private SubmissionSpatialMetadataService submissionSpatialMetadataService;
	
	@Transactional
	public String processRifcs(Submission submission) throws MetaInfoExtractorException, RifcsServiceException, IOException, JSchException{
		String rifcsXml = buildRifcsXmlFromSubmission(submission);
		String filename = getSubmissionRifcsFileName(submission);
		File file = rifcsFileService.writeXmlToLocalFile(rifcsXml, filename);
		rifcsFileService.scpToOaiServer(file);
		return rifcsXml;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public File generateRifcsFileForSubmission(Submission submission) throws MetaInfoExtractorException, IOException, RifcsServiceException {
		return generateRifcsFileForSubmissionHelper(submission);
	}

	/**
	 * Only exists so we can call it without a transaction in a unit test.
	 */
	File generateRifcsFileForSubmissionHelper(Submission submission) throws MetaInfoExtractorException, RifcsServiceException, IOException {
		String rifcsXml = buildRifcsXmlFromSubmission(submission);
		String filename = getSubmissionRifcsFileName(submission);
		File file = rifcsFileService.writeXmlToLocalFile(rifcsXml, filename);
		return file;
	}
	
	@Transactional
	public String buildRifcsXmlFromSubmission(Submission sub) throws MetaInfoExtractorException, RifcsServiceException {
		MetaInfoExtractor metaInfoExtractor;
		try {
			metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub.getId(), true);
		} catch (Exception e) {
			logger.error("Error creating metaInfoExtractor", e);
			throw new MetaInfoExtractorException("Error creating metaInfoExtractor", e);
		}
		RifcsBuilder rifcsBuilder = initialiseRifcsBuilder(sub);
		addSubmissionDates(rifcsBuilder, metaInfoExtractor);
		assignName(rifcsBuilder, metaInfoExtractor );
		processLocation(rifcsBuilder, metaInfoExtractor);
		processTemporalCoverage(rifcsBuilder,metaInfoExtractor);
		processSpatialCoverage(rifcsBuilder, metaInfoExtractor);
		addRelatedObjects(rifcsBuilder);
		processSubjects(rifcsBuilder, metaInfoExtractor);
		assignDescription(rifcsBuilder, metaInfoExtractor);
		processRelatedInfo(rifcsBuilder, metaInfoExtractor);
		processRights(rifcsBuilder, metaInfoExtractor);
		processCitationInfo(rifcsBuilder, metaInfoExtractor);
		String xml = null;
		try{
			xml = rifcsBuilder.getXml();
		}catch(JAXBException ex){
			throw new RifcsServiceException("Error occured generating JAXB XML for RegistryObjects", ex);
		}
		//Hack to deal with non-ASCII characters ( even though we specify UTF-8 - i.e. the AE character )
		//xml = RifcsUtils.asciiCleanse(xml);
		return xml;
	}
	
	private RifcsBuilder initialiseRifcsBuilder(Submission submission){
		String key = config.getRegistryObjectKeyPrefix() + submission.getId().toString();
		String originatingSource = config.getOriginatingSource();
		String group = config.getGroup();
		String dateAccessioned = RifcsUtils.convertDateToW3CDTF(submission.getLastReviewDate());
		String dateLastModified = dateAccessioned; //for now . . . No concept of a modified submission post publish atm. 
		RifcsBuilder rifcsBuilder = RifcsBuilder.getCollectionBuilderInstance(key, originatingSource, group, dateLastModified, dateAccessioned, CollectionTypeEnum.DATASET);
		rifcsBuilder.addCollectionIdentifier(key);
		return rifcsBuilder;
	}
	
	public void addSubmissionDates( RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor   ) throws MetaInfoExtractorException{
		Date submittedDate = metaInfoExtractor.getSubmission().getSubmissionDate();
		if(submittedDate != null){
		    rifcsBuilder.addCollectionDates(DatesTypeEnum.DATE_SUBMITTED, submittedDate);
		}
		Date acceptedDate = metaInfoExtractor.getSubmission().getLastReviewDate();
		if(acceptedDate != null){
			rifcsBuilder.addCollectionDates(DatesTypeEnum.DATE_ACCEPTED, acceptedDate);
		}
	}
	
	public void assignName( RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor   ) throws MetaInfoExtractorException{
		String collectionNameTag = config.getCollectionNameTag();
		String response = metaInfoExtractor.getSingleResponseForMetatag(collectionNameTag);
		rifcsBuilder.addCollectionPrimaryName(response);
	}
	
	public void processLocation(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor   ) throws MetaInfoExtractorException{
	    //First try and create a postal address location
		List<String> addressLines = new ArrayList<String>();
		//First line contact name
		SubmissionAnswer contactNameAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getContactNameMetatag());
		if(contactNameAnswer != null && contactNameAnswer.hasResponse()){
			addressLines.add(contactNameAnswer.getResponse());
		}
		//Contact Organisation
		String contactOrgResponse = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getContactOrganisationMetatag());
		if(StringUtils.hasLength(contactOrgResponse)){
			addressLines.add(contactOrgResponse);
		}
		SubmissionAnswer postAddrAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getContactPostalAddressMetatag());
		if(postAddrAnswer != null && postAddrAnswer.hasResponse()){
			addressLines.add(postAddrAnswer.getResponse());
		}
		//Phone
		SubmissionAnswer phoneAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getContactPhoneNumberMetatag());
		String phoneNumber = null;
		if(phoneAnswer != null && phoneAnswer.hasResponse()){
			phoneNumber = phoneAnswer.getResponse();
		}
		
		rifcsBuilder.addCollectionPhysicalAddress(PhysicalAddressTypeEnum.STREET_ADDRESS, addressLines, phoneNumber, null);
		
		//Email
		SubmissionAnswer emailAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getContactEmailMetatag());
		String email = null;
		if(emailAnswer != null && emailAnswer.hasResponse()){
			email = emailAnswer.getResponse();
		}
		//Url
		String url = config.getElectronicLocationUrlPrefix() + metaInfoExtractor.getSubmissionId().toString();
		rifcsBuilder.addCollectionElectronicAddress(email, url);
	}
	
	private void processSubjects(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor   ) throws MetaInfoExtractorException{
		//All of these subject answers should be multiselect
		SubmissionAnswer anzsrcforAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getAnzsrcforMetatag());
		processSubjectAnswer(anzsrcforAnswer,"anzsrc-for",rifcsBuilder,ProcessSubjectAnswerType.ANZSRC_FOR_OR_SEO);
		
		SubmissionAnswer anzsrcseoAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(metatagConfig.getAnzsrcseoMetatag());
		processSubjectAnswer(anzsrcseoAnswer,"anzsrc-seo",rifcsBuilder,ProcessSubjectAnswerType.ANZSRC_FOR_OR_SEO);
		
		for(String subjectTag : config.getSubjectLocalTagList()){
			SubmissionAnswer subjectLocalAnswer = metaInfoExtractor.getSubmissionAnswerForMetatag(subjectTag);
			processSubjectAnswer(subjectLocalAnswer,"local",rifcsBuilder,ProcessSubjectAnswerType.LOCAL);
		}
	}
	
	private void processTemporalCoverage(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor  ) throws MetaInfoExtractorException{
		String dateFromStr = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getTemporalCoverageFromDateMetatag());
		if(StringUtils.hasLength(dateFromStr)){
			dateFromStr = convertSharedToRifcsDateFormat(dateFromStr);
		}
		String dateToStr =  metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getTemporalCoverageToDateMetatag());
		if(StringUtils.hasLength(dateToStr)){
			dateToStr = convertSharedToRifcsDateFormat(dateToStr);
		}
        if(dateFromStr != null || dateToStr != null){		
		    rifcsBuilder.addCollectionTemporalCoverage(dateFromStr, dateToStr);
        }
	}
	
	//Returns null if something goes wrong
	private String convertSharedToRifcsDateFormat(String sharedDateStr){
		SimpleDateFormat sdfShared = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfRifcs = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdfRifcs.format( sdfShared.parse(sharedDateStr) );
		} catch (ParseException e) {
			logger.error("RIFCS temporal coverage : Failed to parse shared date String:" + sharedDateStr, e);
			return null;
		}
	}
	
	private void processSpatialCoverage(RifcsBuilder rifcsBuilder,MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
	    //Study location description
		String spatialDesc = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSpatialCoverageDescriptionMetatag());
		if(StringUtils.hasLength(spatialDesc)){
			rifcsBuilder.addCollectionSpatialCoverage(SpatialTypeEnum.TEXT, spatialDesc);
		}
		String isoDcmiBbox = submissionSpatialMetadataService.createDcmiBBOXStringForSubmission(metaInfoExtractor, 
				metatagConfig.getSiteFileMetatag(), 
				metatagConfig.getJsonGeoFeatureSetMetatag(),
				metatagConfig.getBboxMinXMetatag(),
				metatagConfig.getBboxMinYMetatag(),
				metatagConfig.getBboxMaxXMetatag(),
				metatagConfig.getBboxMaxYMetatag(),
				metatagConfig.getBboxSRSMetatag());
		if(StringUtils.hasLength(isoDcmiBbox)){
		    rifcsBuilder.addCollectionSpatialCoverage(SpatialTypeEnum.ISO19139DCMIBOX, isoDcmiBbox );
		}
	}
	
	private void assignDescription(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor ) throws MetaInfoExtractorException{
		String description = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getDatasetDescriptionMetatag());
		if(StringUtils.hasLength(description)){
			rifcsBuilder.addCollectionDescription(description, DescriptionTypeEnum.FULL);
		}
	}

	private enum ProcessSubjectAnswerType {
		LOCAL(new ProcessSubjectAnswerInstructionStrategy() {
			@Override
			public void doProcess(RifcsBuilder rifcsBuilder, SubmissionAnswer answer, String type) {
				String response = answer.getResponse();
				if (!StringUtils.hasLength(response)) {
					response = answer.getSuggestedResponse();
				}
				rifcsBuilder.addCollectionSubject(response, type);
			}
		}),
		ANZSRC_FOR_OR_SEO(new ProcessSubjectAnswerInstructionStrategy() {
			@Override
			public void doProcess(RifcsBuilder rifcsBuilder, SubmissionAnswer answer, String type) {
				String trimmedResponse = answer.getResponse().trim();
				switch (trimmedResponse.length()) {
				case 1:
				case 3:
				case 5:
					rifcsBuilder.addCollectionSubject("0" + trimmedResponse, type);
					return;
				}
				rifcsBuilder.addCollectionSubject(trimmedResponse, type);
			}
		});

		private final ProcessSubjectAnswerInstructionStrategy strategy;
		
		private ProcessSubjectAnswerType(ProcessSubjectAnswerInstructionStrategy strategy) {
			this.strategy = strategy;
		}

		public void getValue(RifcsBuilder rifcsBuilder, SubmissionAnswer answer, String type) {
			strategy.doProcess(rifcsBuilder, answer, type);
		}
		
		private interface ProcessSubjectAnswerInstructionStrategy {
			void doProcess(RifcsBuilder rifcsBuilder, SubmissionAnswer answer, String type);
		}
	}
	
	private void processSubjectAnswer(SubmissionAnswer answer, String type, RifcsBuilder rifcsBuilder, ProcessSubjectAnswerType instruction) {
		if(answer == null || !answer.hasResponse()) {
			return;
		}
		for(SubmissionAnswer currAnswer : answer.getMultiselectAnswerList()) {
			if(!currAnswer.hasResponse()) {
				continue;
			}
			instruction.getValue(rifcsBuilder, currAnswer, type);
		}
	}
	
	private void processCitationInfo(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		List<String> authorList = metaInfoExtractor.getCitationFormatAuthorNameList(metatagConfig.getAuthorGivenNameMetatag(), metatagConfig.getAuthorSurnameMetatag());
		String doi = metaInfoExtractor.getSubmission().getDoi();
		String title = metaInfoExtractor.getSingleResponseForMetatag(config.getCollectionNameTag());
		String version = metaInfoExtractor.getSingleResponseForMetatag(config.getVersionTag());
		String submittedDateStr = null;
		String publishedDateStr = null;
		Date submittedDate = metaInfoExtractor.getSubmission().getSubmissionDate();
		if(submittedDate != null){
			submittedDateStr = RifcsUtils.convertDateToW3CDTF(submittedDate);
		}
		Date lastReviewDate = metaInfoExtractor.getSubmission().getLastReviewDate();
		if(lastReviewDate != null){
			publishedDateStr = RifcsUtils.convertDateToW3CDTFYearOnly(lastReviewDate);
		}
		String citationString = citationService.buildCitationString(metaInfoExtractor, CitationDisplayType.RIFCS);
		rifcsBuilder.addCollectionFullCitationString(citationString, null);
		String publisherString = citationService.buildPublisherStringForRifcs(metaInfoExtractor);
		rifcsBuilder.addCollectionCitationMetadata(
				DOI_URL_PREFIX + doi, 
                title, 
                publisherString, 
                version,
                submittedDateStr, 
                publishedDateStr, 
                authorList);
	}
	
	private void processRelatedInfo(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException {
		Set<String> requiredGroupMetatags = new HashSet<>(Arrays.asList(
				metatagConfig.getAssociatedMaterialIdentifierType(), 
				metatagConfig.getAssociatedMaterialIdentifier(),
				metatagConfig.getAssociatedMaterialDescription()));
		List<Map<String, List<String>>> questionSets;
		try {
			questionSets = metaInfoExtractor.getQuestionSetResponsesForMetatags(requiredGroupMetatags);
		} catch (MetaInfoExtractorException e) {
			// These tags don't exist in this submission
			return;
		}
		for (Map<String, List<String>> currQuestionSet : questionSets) {
			List<String> currIdentifierTypeValues = currQuestionSet.get(metatagConfig.getAssociatedMaterialIdentifierType());
			List<String> currIdentifierValues = currQuestionSet.get(metatagConfig.getAssociatedMaterialIdentifier());
			boolean hasNoIdentifierTypeOrNoIdentifierOrIncorrectType = currIdentifierTypeValues == null || 
					currIdentifierValues == null || !DOI_IDENTIFIER_DISPLAY_VALUE.equalsIgnoreCase(currIdentifierTypeValues.get(0));
			if (hasNoIdentifierTypeOrNoIdentifierOrIncorrectType) {
				continue;
			}
			String currIdentifier = currIdentifierValues.get(0);
			List<String> currDescriptionValues = currQuestionSet.get(metatagConfig.getAssociatedMaterialDescription());
			boolean hasNoDescription = currDescriptionValues == null;
			if (hasNoDescription) {
				rifcsBuilder.addRelatedInfo(currIdentifier, DOI_IDENTIFIER_TYPE_NAME_FOR_RIFCS);
				continue;
			}
			rifcsBuilder.addRelatedInfo(currIdentifier, DOI_IDENTIFIER_TYPE_NAME_FOR_RIFCS, currDescriptionValues.get(0));
		}
	}

	private void processRights(RifcsBuilder rifcsBuilder, MetaInfoExtractor metaInfoExtractor) throws MetaInfoExtractorException{
		String license = metaInfoExtractor.getSingleResponseForMetatag(metatagConfig.getSubmissionLicenceTypeMetatag());
		String rightsStatement = citationService.buildRightsStatement(metaInfoExtractor);
		String accessStatement = citationService.buildAccessStatement(metaInfoExtractor);
		if(StringUtils.hasLength(license) || StringUtils.hasLength(rightsStatement) ||
				StringUtils.hasLength(accessStatement)){
			rifcsBuilder.addCollectionRights(rightsStatement, license, null, accessStatement);
		}
	}

	private void addRelatedObjects(RifcsBuilder rifcsBuilder) {
		for(RelatedObjectConfig roConfig : config.getRelatedObjectConfigList() ){
			CollectionRelationTypeEnum relType = getCollectionRelationTypeEnumFromValue(roConfig.getRelationType());
			rifcsBuilder.addCollectionRelatedObject(roConfig.getKey(), relType ,roConfig.getDescription(), roConfig.getUrl());
		}
	}
	
	private CollectionRelationTypeEnum getCollectionRelationTypeEnumFromValue(String value){
		CollectionRelationTypeEnum type = CollectionRelationTypeEnum.getCollectionRelationTypeEnumFromValue(value);
		if(type == null){
			return CollectionRelationTypeEnum.HAS_ASSOCIATION_WITH;
		}
		return type;
	}
	
	//TODO = check for a unique rifcs file name!!
	private String getSubmissionRifcsFileName(Submission sub){
		String title = sub.getTitle().replaceAll("\\W", "");
		return title + ".xml";
	}
	
	@Override @Transactional
	public int regenerateRifcsForAllPublishedSubmissions() {
		logger.info("Regenerating All Rifcs Files");
		List<Long> publishedSubmissionList = submissionService.getPublishedSubmissionIdList();
		if(publishedSubmissionList == null || publishedSubmissionList.size() == 0){
			return 0;
		}
		for(Long subId : publishedSubmissionList){
			Submission sub = submissionService.retrieveSubmissionById(subId);
			logger.info("About to generate RIFCS for submission ID" + subId.toString());
			try{
				String rifcsXml = buildRifcsXmlFromSubmission(sub);
				String filename = getSubmissionRifcsFileName(sub);
				File file = rifcsFileService.writeXmlToLocalFile(rifcsXml, filename);
				logger.info(rifcsXml);
				logger.info(file.getPath());
			}catch(Exception ex){
				logger.error("Error occured regenerating rifcs file for submissionId " + subId.toString(), ex);
			}
		}
		return publishedSubmissionList.size();
	}
}
