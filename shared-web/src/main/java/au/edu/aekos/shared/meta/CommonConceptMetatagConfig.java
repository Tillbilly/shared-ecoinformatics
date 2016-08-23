package au.edu.aekos.shared.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Work in progress - bring all of the common metatag concepts together
 * in the one place for use in the application.
 * 
 * Relates an application concept to a metatag.
 * 
 * ( to be) Used by - report configuration, DOI writing, RIFCS, INDEX, validation .
 * 
 * @author btill
 */
public class CommonConceptMetatagConfig {

	private String submissionTitleMetatag;
	private String datasetNameMetatag;
	private String datasetVersionMetatag;
	private String datasetDescriptionMetatag;
	
	private String authorGivenNameMetatag;
	private String authorSurnameMetatag;
	
	private String embargoDateMetatag;
	
	private String contactNameMetatag;
	private String contactOrganisationMetatag;
	private String contactPostalAddressMetatag;
	private String contactPhoneNumberMetatag;
	private String contactEmailMetatag;
	
	private String submissionLicenceTypeMetatag;
	private String legalContactOrgMetatag;
	private String legalContactOrgTraitName;
	
	private String anzsrcforMetatag;
	private String anzsrcseoMetatag;
	
	private String temporalCoverageFromDateMetatag;
	private String temporalCoverageToDateMetatag;
	
	private String spatialCoverageDescriptionMetatag;
	
	private String siteFileMetatag;
	
	private String jsonGeoFeatureSetMetatag;
	
	private String bboxMinXMetatag;
	private String bboxMinYMetatag;
	private String bboxMaxXMetatag;
	private String bboxMaxYMetatag;
	private String bboxSRSMetatag;
	
	private String associatedMaterialIdentifier;
	private String associatedMaterialIdentifierType;
	private String associatedMaterialDescription;
	
	public String getSubmissionTitleMetatag() {
		return submissionTitleMetatag;
	}

	public void setSubmissionTitleMetatag(String submissionTitleMetatag) {
		this.submissionTitleMetatag = submissionTitleMetatag;
	}

	public String getDatasetNameMetatag() {
		return datasetNameMetatag;
	}

	public void setDatasetNameMetatag(String datasetNameMetatag) {
		this.datasetNameMetatag = datasetNameMetatag;
	}

	public String getContactNameMetatag() {
		return contactNameMetatag;
	}

	public void setContactNameMetatag(String contactNameMetatag) {
		this.contactNameMetatag = contactNameMetatag;
	}

	public String getContactOrganisationMetatag() {
		return contactOrganisationMetatag;
	}

	public void setContactOrganisationMetatag(String contactOrganisationMetatag) {
		this.contactOrganisationMetatag = contactOrganisationMetatag;
	}

	public String getContactPostalAddressMetatag() {
		return contactPostalAddressMetatag;
	}

	public void setContactPostalAddressMetatag(String contactPostalAddressMetatag) {
		this.contactPostalAddressMetatag = contactPostalAddressMetatag;
	}

	public String getContactPhoneNumberMetatag() {
		return contactPhoneNumberMetatag;
	}

	public void setContactPhoneNumberMetatag(String contactPhoneNumberMetatag) {
		this.contactPhoneNumberMetatag = contactPhoneNumberMetatag;
	}

	public String getContactEmailMetatag() {
		return contactEmailMetatag;
	}

	public void setContactEmailMetatag(String contactEmailMetatag) {
		this.contactEmailMetatag = contactEmailMetatag;
	}

	public String getSubmissionLicenceTypeMetatag() {
		return submissionLicenceTypeMetatag;
	}

	public void setSubmissionLicenceTypeMetatag(String submissionLicenceTypeMetatag) {
		this.submissionLicenceTypeMetatag = submissionLicenceTypeMetatag;
	}

	public String getLegalContactOrgMetatag() {
		return legalContactOrgMetatag;
	}

	public void setLegalContactOrgMetatag(String legalContactOrgMetatag) {
		this.legalContactOrgMetatag = legalContactOrgMetatag;
	}

	public String getAnzsrcforMetatag() {
		return anzsrcforMetatag;
	}

	public void setAnzsrcforMetatag(String anzsrcforMetatag) {
		this.anzsrcforMetatag = anzsrcforMetatag;
	}

	public String getAnzsrcseoMetatag() {
		return anzsrcseoMetatag;
	}

	public void setAnzsrcseoMetatag(String anzsrcseoMetatag) {
		this.anzsrcseoMetatag = anzsrcseoMetatag;
	}

	public String getDatasetDescriptionMetatag() {
		return datasetDescriptionMetatag;
	}

	public void setDatasetDescriptionMetatag(String datasetDescriptionMetatag) {
		this.datasetDescriptionMetatag = datasetDescriptionMetatag;
	}

	public String getTemporalCoverageFromDateMetatag() {
		return temporalCoverageFromDateMetatag;
	}

	public void setTemporalCoverageFromDateMetatag(
			String temporalCoverageFromDateMetatag) {
		this.temporalCoverageFromDateMetatag = temporalCoverageFromDateMetatag;
	}

	public String getTemporalCoverageToDateMetatag() {
		return temporalCoverageToDateMetatag;
	}

	public void setTemporalCoverageToDateMetatag(
			String temporalCoverageToDateMetatag) {
		this.temporalCoverageToDateMetatag = temporalCoverageToDateMetatag;
	}

	public String getSpatialCoverageDescriptionMetatag() {
		return spatialCoverageDescriptionMetatag;
	}

	public void setSpatialCoverageDescriptionMetatag(
			String spatialCoverageDescriptionMetatag) {
		this.spatialCoverageDescriptionMetatag = spatialCoverageDescriptionMetatag;
	}

	public String getSiteFileMetatag() {
		return siteFileMetatag;
	}

	public void setSiteFileMetatag(String siteFileMetatag) {
		this.siteFileMetatag = siteFileMetatag;
	}

	public String getJsonGeoFeatureSetMetatag() {
		return jsonGeoFeatureSetMetatag;
	}

	public void setJsonGeoFeatureSetMetatag(String jsonGeoFeatureSetMetatag) {
		this.jsonGeoFeatureSetMetatag = jsonGeoFeatureSetMetatag;
	}

	public String getEmbargoDateMetatag() {
		return embargoDateMetatag;
	}

	public void setEmbargoDateMetatag(String embargoDateMetatag) {
		this.embargoDateMetatag = embargoDateMetatag;
	}

	public String getBboxMinXMetatag() {
		return bboxMinXMetatag;
	}

	public void setBboxMinXMetatag(String bboxMinXMetatag) {
		this.bboxMinXMetatag = bboxMinXMetatag;
	}

	public String getBboxMinYMetatag() {
		return bboxMinYMetatag;
	}

	public void setBboxMinYMetatag(String bboxMinYMetatag) {
		this.bboxMinYMetatag = bboxMinYMetatag;
	}

	public String getBboxMaxXMetatag() {
		return bboxMaxXMetatag;
	}

	public void setBboxMaxXMetatag(String bboxMaxXMetatag) {
		this.bboxMaxXMetatag = bboxMaxXMetatag;
	}

	public String getBboxMaxYMetatag() {
		return bboxMaxYMetatag;
	}

	public void setBboxMaxYMetatag(String bboxMaxYMetatag) {
		this.bboxMaxYMetatag = bboxMaxYMetatag;
	}

	public String getBboxSRSMetatag() {
		return bboxSRSMetatag;
	}

	public void setBboxSRSMetatag(String bboxSRSMetatag) {
		this.bboxSRSMetatag = bboxSRSMetatag;
	}

	public String getDatasetVersionMetatag() {
		return datasetVersionMetatag;
	}

	public void setDatasetVersionMetatag(String datasetVersionMetatag) {
		this.datasetVersionMetatag = datasetVersionMetatag;
	}

	public String getLegalContactOrgTraitName() {
		return legalContactOrgTraitName;
	}

	public void setLegalContactOrgTraitName(String legalContactOrgTraitName) {
		this.legalContactOrgTraitName = legalContactOrgTraitName;
	}

	public String getAuthorGivenNameMetatag() {
		return authorGivenNameMetatag;
	}

	public void setAuthorGivenNameMetatag(String authorGivenNameMetatag) {
		this.authorGivenNameMetatag = authorGivenNameMetatag;
	}

	public String getAuthorSurnameMetatag() {
		return authorSurnameMetatag;
	}

	public void setAuthorSurnameMetatag(String authorSurnameMetatag) {
		this.authorSurnameMetatag = authorSurnameMetatag;
	}

	public String getAssociatedMaterialIdentifier() {
		return associatedMaterialIdentifier;
	}

	public void setAssociatedMaterialIdentifier(String associatedMaterialIdentifier) {
		this.associatedMaterialIdentifier = associatedMaterialIdentifier;
	}

	public String getAssociatedMaterialIdentifierType() {
		return associatedMaterialIdentifierType;
	}

	public void setAssociatedMaterialIdentifierType(String associatedMaterialIdentifierType) {
		this.associatedMaterialIdentifierType = associatedMaterialIdentifierType;
	}

	public String getAssociatedMaterialDescription() {
		return associatedMaterialDescription;
	}

	public void setAssociatedMaterialDescription(String associatedMaterialDescription) {
		this.associatedMaterialDescription = associatedMaterialDescription;
	}

	public List<String> getRequiredMetatags(){
		List<String> metatags = new ArrayList<String>();
		Class<? extends CommonConceptMetatagConfig> clazz = this.getClass();
		for(Method method : clazz.getMethods()){
			String methodName = method.getName();
			if(methodName.startsWith("get") && methodName.endsWith("Metatag")){
				try {
					String value = (String) method.invoke(this);
					if(StringUtils.hasLength(value)){
						metatags.add(value);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return metatags;
	}
}
