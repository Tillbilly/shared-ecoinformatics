package au.org.aekos.shared.api.model.dataset;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class SharedSearchResultFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(SharedSearchResultFactory.class);
	private final List<GridCellMapper> gridCellMappings = new ArrayList<GridCellMapper>();
	private final Set<LicenseTypeMapping> licenseTypeMappings = new HashSet<LicenseTypeMapping>();
	
	@Value("${index-names.id}")
	private String idIndexName;
	
	@Value("${index-names.dataset.name}")
	private String datasetNameIndexName;
	
	@Value("${index-names.dataset.abstract}")
	private String datasetAbstractIndexName;
	
	@Value("${index-names.spatial-location}")
	private String spatialLocationIndexName;
	
	@Value("${index-names.image-url}")
	private String imageUrlIndexName;

	@Value("${index-names.thumbnail-url}")
	private String thumbnailUrlIndexName;
	
	@Value("${index-names.embargo-date}")
	private String embargoDateIndexName;
	
	@Value("${index-names.license-type}")
	private String licenseTypeIndexName;
	
	/**
	 * Creates a fully populated new instance from the data in the supplied Solr Document.
	 * 
	 * @param solrDoc	doc to extract data from
	 * @return			new instance of a search result
	 */
	public SharedSearchResult newSearchResultInstance(SolrDocument solrDoc) {
		String resultDatasetId = (String) solrDoc.getFirstValue(idIndexName);
		String resultTitleDescription = (String) solrDoc.getFirstValue(datasetNameIndexName);
		String resultAbstractDescription = (String) solrDoc.getFirstValue(datasetAbstractIndexName);
		String resultThumbnailUrl = (String) solrDoc.getFirstValue(thumbnailUrlIndexName);
		String resultImageUrl = (String) solrDoc.getFirstValue(imageUrlIndexName);
		String resultWkt = (String) solrDoc.getFirstValue(spatialLocationIndexName);
		Date today = new Date();
		boolean isEmbargoedToday = determineIsEmbargoedOnDate(solrDoc, today);
		String indexedLicenseType = (String) solrDoc.getFirstValue(licenseTypeIndexName);
		String exportedLicenseType = resolveExportLicenseType(indexedLicenseType);
		SharedSearchResult result = new SharedSearchResult(resultDatasetId, resultThumbnailUrl, resultImageUrl
				, resultTitleDescription, resultAbstractDescription, resultWkt, isEmbargoedToday, exportedLicenseType);
		for (GridCellMapper currMapper : gridCellMappings) {
			if (!currMapper.canExecute(solrDoc)) {
				continue;
			}
			result.addGridField(currMapper.map(solrDoc));
		}
		return result;
	}

	String resolveExportLicenseType(String indexedLicenseType) {
		for (LicenseTypeMapping currMapping : licenseTypeMappings) {
			if (currMapping.getIndexedValue().equals(indexedLicenseType)) {
				return currMapping.getExportedValue();
			}
		}
		logger.warn("Config error: couldn't find a mapping for the indexed license type: " + indexedLicenseType);
		return indexedLicenseType;
	}

	/**
	 * @param solrDoc			document to extract the embargo date from
	 * @param compareToDate		compare the embargo date to this date to determine if we are under embargo.
	 * 							Should be today but can be used against any day.
	 * @return					<code>true</code> if we are still under embargo, <code>false</code> otherwise
	 */
	boolean determineIsEmbargoedOnDate(SolrDocument solrDoc, Date compareToDate) {
		if (!solrDoc.containsKey(embargoDateIndexName))
			return false;
		Date embargoDate = (Date) solrDoc.getFirstValue(embargoDateIndexName);
		return compareToDate.before(embargoDate);
	}

	public void setIdIndexName(String idIndexName) {
		this.idIndexName = idIndexName;
	}

	public void setDatasetNameIndexName(String datasetNameIndexName) {
		this.datasetNameIndexName = datasetNameIndexName;
	}

	public void setDatasetAbstractIndexName(String datasetAbstractIndexName) {
		this.datasetAbstractIndexName = datasetAbstractIndexName;
	}

	public void setSpatialLocationIndexName(String spatialLocationIndexName) {
		this.spatialLocationIndexName = spatialLocationIndexName;
	}
	
	public void setGridCellMappings(List<GridCellMapper> gridCellMappings) {
		this.gridCellMappings.addAll(gridCellMappings);
	}
	
	public void setLicenseTypeMappings(Set<LicenseTypeMapping> licenseTypeMappings) {
		this.licenseTypeMappings.addAll(licenseTypeMappings);
	}

	public void setImageUrlIndexName(String imageUrlIndexName) {
		this.imageUrlIndexName = imageUrlIndexName;
	}

	public void setThumbnailUrlIndexName(String thumbnailUrlIndexName) {
		this.thumbnailUrlIndexName = thumbnailUrlIndexName;
	}
}
