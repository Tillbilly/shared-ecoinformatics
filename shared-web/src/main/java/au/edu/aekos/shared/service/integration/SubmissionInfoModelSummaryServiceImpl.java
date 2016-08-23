package au.edu.aekos.shared.service.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.data.dao.SubmissionLinkDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.data.entity.SubmissionData;
import au.edu.aekos.shared.data.entity.SubmissionDataType;
import au.edu.aekos.shared.data.entity.SubmissionLink;
import au.edu.aekos.shared.data.entity.SubmissionLinkType;
import au.edu.aekos.shared.data.entity.SubmissionStatus;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractor;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorException;
import au.edu.aekos.shared.data.infomodel.MetaInfoExtractorFactory;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;
import au.edu.aekos.shared.reports.SubmissionSummaryConfig;
import au.edu.aekos.shared.service.citation.CitationService;
import au.edu.aekos.shared.service.citation.CitationService.CitationDisplayType;
import au.edu.aekos.shared.service.citation.CitationStringServiceImpl;
import au.edu.aekos.shared.service.submission.EmbargoService;
import au.edu.aekos.shared.service.submission.SubmissionService;
import au.edu.aekos.shared.service.submission.SubmissionSpatialMetadataService;
import au.org.aekos.shared.api.informationModel.InformationModelFactory;
import au.org.aekos.shared.api.json.DatasetLink;
import au.org.aekos.shared.api.json.ResponseGetDatasetSummary;
import au.org.aekos.shared.api.json.SharedDatasetSummary;
import au.org.aekos.shared.api.json.SpeciesFileNameEntry;
import au.org.aekos.shared.api.model.dataset.SubmissionSummaryRow;
import au.org.aekos.shared.api.model.infomodel.InformationModelEntry;
import au.org.aekos.shared.api.model.infomodel.SharedInformationModel;



/**
 * Build the view of the information model based on a stored submission's meta model.
 * @author btill
 */
@Service
public class SubmissionInfoModelSummaryServiceImpl implements SubmissionInfoModelSummaryService {

	private static final Logger logger = LoggerFactory.getLogger(SubmissionInfoModelSummaryServiceImpl.class);
	private Map<String, DerivedInformationRowStrategy> derivedRowStrategies = null;

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private MetaInfoExtractorFactory metaInfoExtractorFactory;
	
	@Autowired
	private SubmissionFileInfoService submissionFileInfoService;
	
	@Autowired
	private SubmissionSummaryConfig summaryConfig;
	
	@Autowired
	private CitationService citationService;

	@Autowired
	private SubmissionSpatialMetadataService submissionSpatialMetadataService;
	
	@Autowired
	private EmbargoService embargoService;
	
	@Autowired
	private SubmissionLinkDao submissionLinkDao;
	
	@Override @Transactional
	public ResponseGetDatasetSummary buildSubmissionSummaryForPortal(Long datasetId, boolean checkPublished) {
		//Validate submission id and published, if required
		Submission sub = submissionService.retrieveSubmissionById(datasetId);
		if(sub == null) {
			return ResponseGetDatasetSummary.newInstanceFailure("No submission for ID " + datasetId);
		}
		if(checkPublished && !sub.isPubliclyAvailable()){
        	return ResponseGetDatasetSummary.newInstanceFailure(
        			"Submission " + datasetId + " does not have a status that allows it to be publicly available");
        }
		MetaInfoExtractor metaInfoExtractor = null;
		try {
			metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub, true);
		} catch (Exception e) {
			logger.error("Error creating metaInfoExtractor for data submission summary report - submission ID " + datasetId.toString(), e);
			return ResponseGetDatasetSummary.newInstanceFailure("Error creating metaInfoExtractor for data submission summary "
					+ "report - submission ID " + datasetId.toString());
		}
		SharedDatasetSummary summary = mapSubmissionToSummaryResponse(metaInfoExtractor);
		if (sub.isBeingPublished()) {
			return ResponseGetDatasetSummary.newInstancePendingPublish(summary);
		}
		List<SubmissionSummaryRow> rows = constructSubmissionSummaryRows(metaInfoExtractor, CitationDisplayType.AEKOS_PORTAL);
		summary.addAllRowData(rows);
		List<String> wktStrings = submissionSpatialMetadataService.getWktStringsForSubmission(metaInfoExtractor,summaryConfig.getSiteFileMetatag(), summaryConfig.getPickedGeometryMetatag(),
				summaryConfig.getBboxMinXMetatag(),summaryConfig.getBboxMinYMetatag(),
				summaryConfig.getBboxMaxXMetatag(),summaryConfig.getBboxMaxYMetatag(),
				summaryConfig.getBboxSRSMetatag());
		summary.addAllWktGeometry(wktStrings);
		summary.addAllSpeciesFileNameEntry(extractSpeciesFilenames(sub));
		addLinkInformation(summary, datasetId);
		return ResponseGetDatasetSummary.newInstanceSuccess(summary);
	}
	
	private void addLinkInformation(SharedDatasetSummary summary, Long datasetId) {
		List<SubmissionLink> links = submissionLinkDao.getLinksForSubmission(datasetId);
		List<InterimDatasetLink> interimLinks = new TreeList<InterimDatasetLink>();
		for (SubmissionLink currLink : links) {
			Submission linkedSubmission = currLink.getLinkedSubmission();
			boolean isOtherSubmissionNotPublished = !linkedSubmission.isPublished();
			if (isOtherSubmissionNotPublished) {
				continue;
			}
			boolean isSourceOfHasNewVersionLink = SubmissionLinkType.HAS_NEW_VERSION.equals(currLink.getLinkType()) && currLink.getSourceLink();
			boolean isTargetOfIsNewVersionLink = SubmissionLinkType.IS_NEW_VERSION_OF.equals(currLink.getLinkType()) && !currLink.getSourceLink();
			if (isSourceOfHasNewVersionLink || isTargetOfIsNewVersionLink) {
				summary.setNewerVersionPresent(true);
			}
			interimLinks.add(new InterimDatasetLink(linkedSubmission.getTitle(), linkedSubmission.getId(), currLink.getLinkType(), 
					currLink.getDescription(), linkedSubmission.getLastReviewDate(), currLink.getSourceLink()));
		}
		Collections.sort(interimLinks);
		for (int i = 0 ; i < interimLinks.size(); i++) {
			InterimDatasetLink currLink = interimLinks.get(i);
			String linkTitle;
			if (currLink.isSourceOfLink()) {
				linkTitle = currLink.getLinkType().getNormalTitle();
			} else {
				linkTitle = currLink.getLinkType().getInverseTitle();
			}
			DatasetLink link = new DatasetLink(currLink.getOtherDatasetTitle(), currLink.getOtherDatasetId(), 
					linkTitle, currLink.getLinkDescription(), i, currLink.getTargetDatasetPublished());
			summary.addLink(link);
		}
	}

	List<SpeciesFileNameEntry> extractSpeciesFilenames(Submission sub) {
		return submissionFileInfoService.retrieveSpeciesFileNames(sub);
	}

	private SharedDatasetSummary mapSubmissionToSummaryResponse(MetaInfoExtractor metaInfoExtractor){
		Submission submission = metaInfoExtractor.getSubmission();
		String submissionId = submission.getId().toString();
		String title = submission.getTitle();
		Date submissionDate = submission.getSubmissionDate();
		Date lastReviewDate = submission.getLastReviewDate();
		return new SharedDatasetSummary(submissionId, title, submissionDate, lastReviewDate, embargoService.isSubmissionUnderEmbargo(metaInfoExtractor));
	}
	
	@Transactional @Override
	public List<SubmissionSummaryRow> retrieveSubmissionSummaryRows(
			Long submissionId, boolean checkPublished) {
		Submission sub = submissionService.retrieveSubmissionById(submissionId);
		List<SubmissionSummaryRow> submissionSummaryRowList = new ArrayList<SubmissionSummaryRow>();
        if(sub == null){
        	submissionSummaryRowList.add(createErrorBean("No submission for ID " + submissionId ));
        	return submissionSummaryRowList;
        }else if(checkPublished && ! SubmissionStatus.PUBLISHED.equals(sub.getStatus())){
        	submissionSummaryRowList.add(createErrorBean("Submission " + submissionId + " does not have a PUBLISHED status and is not publicly available" ));
        	return submissionSummaryRowList;
        }
		MetaInfoExtractor metaInfoExtractor = null;
		try {
			metaInfoExtractor = metaInfoExtractorFactory.getInstance(sub, true);
		} catch (Exception e) {
			logger.error("Error creating metaInfoExtractor for data submission summary report - submission ID " + submissionId.toString(), e);
			submissionSummaryRowList.add(createErrorBean("Error creating metaInfoExtractor for data submission summary report - submission ID " + submissionId.toString() ));
			return submissionSummaryRowList;
		}
		return constructSubmissionSummaryRows(metaInfoExtractor, CitationDisplayType.PDF);
	}
	
	private List<SubmissionSummaryRow> constructSubmissionSummaryRows(MetaInfoExtractor metaInfoExtractor, CitationDisplayType displayType){
		List<SubmissionSummaryRow> submissionSummaryRowList = new ArrayList<SubmissionSummaryRow>();
		SharedInformationModel infoModel = InformationModelFactory.getSharedInformationModel();
		String currentGroup = null;
	    for(InformationModelEntry infoModelEntry : infoModel.getEntryList()){
	    	if(StringUtils.hasLength(currentGroup)){
	    	    if(! StringUtils.hasLength(infoModelEntry.getGroup()) 
	    	    		|| ! currentGroup.equals(infoModelEntry.getGroup())){ //i.e. group null, or it changes
	    	        currentGroup = null;	
	    	    }else{
	    	        continue; //keep looping until out of the group, its already been handled.	
	    	    }
	    	}
	    	currentGroup = infoModelEntry.getGroup();
	    	//Is the response retrievable from the metamodel 
	    	//- i.e. Answer or Derived in the info model ( might change this terminology . . . )
	    	if(Boolean.TRUE.equals( infoModelEntry.getQuestionnaireAnswer()) ){
	    		//So we either have a single value, multi value, or a question group
		    	//We know we have a question group because it is marked as such ( Group Name ) in the info model.
		    	if(StringUtils.hasLength(currentGroup)){
		    		List<InformationModelEntry> groupEntryList = infoModel.getEntriesForGroupTitle(currentGroup);
		    		createGroupSummaryRows( groupEntryList, metaInfoExtractor, submissionSummaryRowList );
		    	}else{
		    		try { //Multiple responses
			    		if(Boolean.TRUE.equals(infoModelEntry.getMultipleValues()) ){
			    			 createMultipleResponseRows(infoModelEntry, metaInfoExtractor, submissionSummaryRowList );
			    		}	
						else{  //Single Response
							createSingleResponseRow(infoModelEntry, metaInfoExtractor, submissionSummaryRowList);
			    		}
		    		} catch (MetaInfoExtractorException e) {
						logger.error("Error processing metatag " + infoModelEntry.getMetatag() + " for submission " + metaInfoExtractor.getSubmission().getId());
					}
		    	}
	    	}else if(StringUtils.hasLength(currentGroup) && currentGroup.equalsIgnoreCase( summaryConfig.getDatasetFileGroupName()) && metaInfoExtractor.getSubmission().hasDataFiles() ){
	    	    addSubmissionFileRows(infoModel.getEntriesForGroupTitle(currentGroup), metaInfoExtractor, submissionSummaryRowList);
	    	}else{ //Ignoring species file entries,  for now
	    		addDerivedInformationRow(infoModelEntry, metaInfoExtractor, submissionSummaryRowList , displayType);
	    	}
	    }
		return submissionSummaryRowList;
	}
	
	private interface DerivedInformationRowStrategy {
		void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, 
			List<SubmissionSummaryRow> rowList, CitationDisplayType displayType);
	}
	
	/*
	 * Derived information rows from the information model.
	 */
	private void addDerivedInformationRow(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, 
			List<SubmissionSummaryRow> rowList, CitationDisplayType displayType){
		if (derivedRowStrategies == null) {
			derivedRowStrategies = getDerivedRowStrategies();
		}
		String metatag = infoModelEntry.getMetatag();
		DerivedInformationRowStrategy strategy = derivedRowStrategies.get(metatag);
		if (strategy == null) {
			logger.error("No processing for info model metatag " + metatag);
			return;
		}
		strategy.doProcessing(infoModelEntry, metaInfoExtractor, rowList, displayType);
	}
	
	private void createNonNullTextRowAndAdd(String text, InformationModelEntry infoModelEntry, String metatag, List<SubmissionSummaryRow> rowList){
		if(!StringUtils.hasLength(text)) {
			return;
		}
		SubmissionSummaryRow row = new SubmissionSummaryRow(infoModelEntry.getSection(), infoModelEntry.getSubsection(), 
				infoModelEntry.getDisplayText(), text, ResponseType.TEXT.name(), metatag);
		rowList.add(row);
	}
	
	private void createMultipleResponseRows(InformationModelEntry modelEntry, MetaInfoExtractor extractor, List<SubmissionSummaryRow> reportBeanList ) throws MetaInfoExtractorException{
		List<String> responseList = extractor.getResponsesFromMultiselectTag(modelEntry.getMetatag());
		if(	responseList == null || responseList.size() == 0 ){
			if(Boolean.TRUE.equals(modelEntry.getMandatory() ) ){
				SubmissionSummaryRow bean = new SubmissionSummaryRow(modelEntry.getSection(), modelEntry.getSubsection(),  modelEntry.getDisplayText(), "", ResponseType.TEXT.name(), modelEntry.getMetatag());
				reportBeanList.add(bean);
			}
		}else{
			for(String response : responseList){
				SubmissionSummaryRow bean = new SubmissionSummaryRow(modelEntry.getSection(), modelEntry.getSubsection(), modelEntry.getDisplayText(), response, ResponseType.TEXT.name(), modelEntry.getMetatag());
				reportBeanList.add(bean);
			}
		}
	}
	
	private void createSingleResponseRow(InformationModelEntry modelEntry, MetaInfoExtractor extractor, List<SubmissionSummaryRow> reportBeanList) throws MetaInfoExtractorException{
		String response = extractor.getSingleResponseForMetatag(modelEntry.getMetatag());
		String type = "TEXT";
		SubmissionAnswer sa = extractor.getSubmissionAnswerForMetatag(modelEntry.getMetatag());
		if(sa != null){
			type = sa.getResponseType().name();
		}
		if(Boolean.TRUE.equals(modelEntry.getMandatory()) && response == null){
			response = "";
		}
		if( response != null){
			SubmissionSummaryRow bean = new SubmissionSummaryRow(modelEntry.getSection(), modelEntry.getSubsection(), modelEntry.getDisplayText(), response, type, modelEntry.getMetatag() );
			if(summaryConfig.getFileLinkMetatags().contains(modelEntry.getMetatag())){
				addLinkToSubmissionSummaryRow(modelEntry, extractor,  bean);
			}
			reportBeanList.add(bean);
		}
	}
	
	private void addLinkToSubmissionSummaryRow(InformationModelEntry modelEntry, MetaInfoExtractor extractor, SubmissionSummaryRow bean){
		try {
			SubmissionAnswer sa = extractor.getSubmissionAnswerForMetatag(modelEntry.getMetatag());
			if(sa == null){ //should'nt be null
				return;
			}
			if(ResponseType.IMAGE.equals(sa.getResponseType()) && sa.getAnswerImage() != null && StringUtils.hasLength( sa.getAnswerImage().getImageObjectId()) ){
				String imageId = sa.getAnswerImage().getImageObjectId();
				String imgLink = "/getSubmissionImage?submissionId="+extractor.getSubmissionId().toString() +"&questionId="+sa.getQuestionId()+"&imageId="+imageId ;
				bean.setLink(imgLink);
			}else if(ResponseType.SITE_FILE.equals(sa.getResponseType()) || ResponseType.SPECIES_LIST.equals(sa.getResponseType())){
				Long submissionDataId = findSubmissionDataIdForTextFileAnswer(sa,extractor.getSubmission());
				if(submissionDataId != null){
					String fileLink = "/viewPublishedTextFile?submissionId=" + extractor.getSubmissionId().toString() + "&dataId=" + submissionDataId.toString();
					bean.setLink(fileLink);
				}
			}
		} catch (MetaInfoExtractorException e) {
			logger.error("Error occured trying to extract submission answer for " +extractor.getSubmissionId().toString() + "  metatag " + modelEntry.getMetatag(), e);
		}
	}
	
	private Long findSubmissionDataIdForTextFileAnswer(SubmissionAnswer sa, Submission sub){
		String fileName = sa.getResponse();
		for(SubmissionData sd : sub.getSubmissionDataList() ){
			if(fileName.equals(sd.getFileName()) && 
					(SubmissionDataType.SITE_FILE.equals(sd.getSubmissionDataType()) ||
							SubmissionDataType.SPECIES_LIST.equals(sd.getSubmissionDataType()))){
				return sd.getId();
			}
		}
		return null;
	}
	

	private void createGroupSummaryRows(List<InformationModelEntry> groupEntryList, 
			MetaInfoExtractor metaInfo, 
			List<SubmissionSummaryRow> submissionSummaryRowList ){
		Set<String> requiredMetatags = new HashSet<String>();
		for(InformationModelEntry entry : groupEntryList){
			requiredMetatags.add(entry.getMetatag());
		}
		List<Map<String, List<String>>> questionSetResponseMapList = null;
		try {
			questionSetResponseMapList = metaInfo.getQuestionSetResponsesForMetatags(requiredMetatags);
		} catch (MetaInfoExtractorException e) {
			logger.error(e.getMessage(),e);
			return;
		}
		
		int groupIndex = 1;
		for(Map<String, List<String>> setResponseMap : questionSetResponseMapList){
			for(InformationModelEntry imEntry : groupEntryList ){
				String metatag = imEntry.getMetatag();
				String response = "";
				if(setResponseMap.containsKey(metatag) ){
					List<String> responseList = setResponseMap.get(metatag);
					if(responseList != null && responseList.size() > 0){
						response = responseList.get(0);
					}
				}
				SubmissionSummaryRow row =
						SubmissionSummaryRow.createGroupInstance( imEntry.getSection(), imEntry.getSubsection(), imEntry.getGroup() ,groupIndex, imEntry.getDisplayText() , response, ResponseType.TEXT.name(), metatag );
				submissionSummaryRowList.add(row);
			}
			groupIndex++;
		}
	}
	
	private void addSubmissionFileRows(List<InformationModelEntry> groupEntryList, MetaInfoExtractor metaInfo, 
             List<SubmissionSummaryRow> submissionSummaryRowList){
		int fileNumber = 0;
        for(SubmissionData sd : metaInfo.getSubmission().getSubmissionDataList() ){
        	fileNumber++;
        	for(InformationModelEntry me : groupEntryList){
                if(me.getMetatag().equals(summaryConfig.getFilenameMetatag() ) ){
                	submissionSummaryRowList.add(
                			SubmissionSummaryRow.createGroupInstance(me.getSection(), me.getSubsection(),me.getGroup(), fileNumber, me.getDisplayText(), sd.getFileName(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFileDescriptionMetatag() ) ){
        			submissionSummaryRowList.add(
        					SubmissionSummaryRow.createGroupInstance(me.getSection(), me.getSubsection(), me.getGroup(), fileNumber, me.getDisplayText(), sd.getFileDescription(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFileFormatMetatag())){
        			submissionSummaryRowList.add(
        					SubmissionSummaryRow.createGroupInstance(me.getSection(),me.getSubsection(), me.getGroup(), fileNumber, me.getDisplayText(), sd.getFormat(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFileFormatVersionMetatag())){
        			submissionSummaryRowList.add(
        					SubmissionSummaryRow.createGroupInstance(me.getSection(),me.getSubsection(), me.getGroup(), fileNumber, me.getDisplayText(), sd.getFormatVersion(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFileIdMetatag())){
        			submissionSummaryRowList.add(
        					SubmissionSummaryRow.createGroupInstance(me.getSection(),me.getSubsection(), me.getGroup(), fileNumber, me.getDisplayText(), sd.getId().toString(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFileTypeMetatag())){
        			submissionSummaryRowList.add(
        					SubmissionSummaryRow.createGroupInstance(me.getSection(), me.getSubsection(),me.getGroup(), fileNumber, me.getDisplayText(), sd.getSubmissionDataType().name(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else if(me.getMetatag().equals(summaryConfig.getFilesizeMetatag())){
        			submissionSummaryRowList.add(SubmissionSummaryRow.createGroupInstance(me.getSection(),me.getSubsection(), me.getGroup(), fileNumber, me.getDisplayText(), sd.getHumanReadableFileSize(), ResponseType.TEXT.name(), me.getMetatag()));
        		}else{
        			logger.error("Metatag " + me.getMetatag() +" not mapped in summary report config");
        		}
        	}
        }
	}
	
	private SubmissionSummaryRow createErrorBean(String message){
		SubmissionSummaryRow row = new SubmissionSummaryRow();
		row.setSection("ERROR");
		row.setTitle("ERROR");
		row.setValue(message);
		return row;
	}
	
	private HashedMap<String, DerivedInformationRowStrategy> getDerivedRowStrategies() {
		HashedMap<String, DerivedInformationRowStrategy> result = new HashedMap<String, DerivedInformationRowStrategy>();
		result.put(summaryConfig.getSubmissionIdMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//Submission ID
				SubmissionSummaryRow result = new SubmissionSummaryRow(infoModelEntry.getSection(), infoModelEntry.getSubsection(), 
						infoModelEntry.getDisplayText(), metaInfoExtractor.getSubmissionId().toString(),ResponseType.TEXT.name(), 
						infoModelEntry.getMetatag());
				rowList.add(result);
			}
		});
		result.put(summaryConfig.getDoiMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//DOI
				String doi = metaInfoExtractor.getSubmission().getDoi();
				if(! StringUtils.hasLength(doi)){
					doi = ""; 
				}
				SubmissionSummaryRow result = new SubmissionSummaryRow(infoModelEntry.getSection(), infoModelEntry.getSubsection(), 
						infoModelEntry.getDisplayText(), CitationStringServiceImpl.DOI_URL_PREFIX + doi, ResponseType.TEXT.name(), infoModelEntry.getMetatag());
				rowList.add(result);
			}
		});
		result.put(summaryConfig.getPublicationDateMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//publication date
				if(metaInfoExtractor.getSubmission().getLastReviewDate() == null) {
					return;
				}
				SubmissionSummaryRow bean = new SubmissionSummaryRow(infoModelEntry.getSection(), infoModelEntry.getSubsection(), 
						infoModelEntry.getDisplayText(), metaInfoExtractor.getSubmission().getLastReviewDate().toString(), 
						ResponseType.DATE.name(), infoModelEntry.getMetatag());
				rowList.add(bean);
			}
		});
		result.put(summaryConfig.getDcmiBBOXMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//DcmiBBOX
				SubmissionSummaryRow row = assignDcmiBBOXResponse(infoModelEntry,  metaInfoExtractor);
				rowList.add(row);
			}
			
			private SubmissionSummaryRow assignDcmiBBOXResponse(InformationModelEntry modelEntry, MetaInfoExtractor metaInfo){
				String dcmiBbox = submissionSpatialMetadataService.createDcmiBBOXStringForSubmission(metaInfo, 
						summaryConfig.getSiteFileMetatag(), summaryConfig.getPickedGeometryMetatag(),
						summaryConfig.getBboxMinXMetatag(), summaryConfig.getBboxMinYMetatag(),
						summaryConfig.getBboxMaxXMetatag(), summaryConfig.getBboxMaxYMetatag(),
						summaryConfig.getBboxSRSMetatag());
				SubmissionSummaryRow result = new SubmissionSummaryRow(modelEntry.getSection(), modelEntry.getSubsection(), 
						modelEntry.getDisplayText(), dcmiBbox, ResponseType.TEXT.name(), modelEntry.getMetatag());
				return result;
			}
		});
		result.put(summaryConfig.getSubmitterMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//submitter
				SubmissionSummaryRow row = new SubmissionSummaryRow(infoModelEntry.getSection(), infoModelEntry.getSubsection(), 
						infoModelEntry.getDisplayText(), metaInfoExtractor.getSubmission().getSubmittingUsername(),
						ResponseType.TEXT.name(), infoModelEntry.getMetatag());
				rowList.add(row);
			}
		});
		result.put(summaryConfig.getCitationMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//citation
				String citation = citationService.buildCitationString(metaInfoExtractor, displayType);
				createNonNullTextRowAndAdd(citation, infoModelEntry, infoModelEntry.getMetatag(), rowList);
			}
		});
		result.put(summaryConfig.getRightsStatementMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//Rights Statement
				String rights = citationService.buildRightsStatement(metaInfoExtractor);
				createNonNullTextRowAndAdd(rights, infoModelEntry, infoModelEntry.getMetatag(), rowList);
			}
		});
		result.put(summaryConfig.getAccessStatementMetatag(), new DerivedInformationRowStrategy() {
			@Override
			public void doProcessing(InformationModelEntry infoModelEntry, MetaInfoExtractor metaInfoExtractor, List<SubmissionSummaryRow> rowList,
					CitationDisplayType displayType) {
				//Access Statement
				String access = citationService.buildAccessStatement(metaInfoExtractor);
				createNonNullTextRowAndAdd(access, infoModelEntry, infoModelEntry.getMetatag(), rowList);
			}
		});
		return result;
	}
}
