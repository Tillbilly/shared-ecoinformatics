package au.edu.aekos.shared.solr.index;

import java.util.Date;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

//Test utility to load up some SHaRED like indexed documents for testing purposes
public class SubmissionSolrDocumentBuilder {

	public static SolrInputDocument initialiseSHaREDDocument(String id, 
			                                                 String doi, 
			                                                 String title, 
			                                                 String datasetNameForCitation,
			                                                 String datasetAbstract,
			                                                 Date firstVisit,
			                                                 Date lastVisit){
	    SolrInputDocument doc = new SolrInputDocument();
	    doc.addField("id", id);
	    doc.addField("doi", doi);
	    doc.addField("title", title);
	    doc.addField("datasetFormalName_t", title);
	    doc.addField("datasetNameForCitation_t", datasetNameForCitation);
	    doc.addField("description", datasetAbstract );
	    doc.addField("datasetAbstract_t", datasetAbstract );
	    doc.addField("firstStudyAreaVisitDate_dt", firstVisit);
	    doc.addField("lastStudyAreaVisitDate_dt", firstVisit);
	    return doc;
	}
	
	public static void addFloraSpecies(List<String> speciesNames, SolrInputDocument doc){
		for(String speciesName : speciesNames){
            doc.addField("studySpeciesFlora_txt", speciesName);
            doc.addField("species_txt", speciesName);
		}
	}
	
    public static void addFaunaSpecies(List<String> speciesNames, SolrInputDocument doc){
    	for(String speciesName : speciesNames){
    		doc.addField("studySpeciesFauna_txt", speciesName);
    		doc.addField("species_txt", speciesName);
    	}
	}
    
    public static void addSpatialFeature(String wktFeature, SolrInputDocument doc){
		doc.addField("geo_mv", wktFeature);
	}
}

//id,doi,title,description,datasetFormalName_t,datasetNameForCitation_t,datasetAbstract_t


//studySpeciesFlora_txt
//species_txt
//geo_mv
//firstStudyLocationVisitDate_dt
//lastStudyLocationVisitDate_dt

/*
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="id" />
<property name="indexFieldName" value="id" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="doi" />
<property name="indexFieldName" value="doi" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetFormalName" />
<property name="indexFieldName" value="title" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="description" />
<property name="indexFieldName" value="description" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetFormalName" />
<property name="indexFieldName" value="datasetFormalName_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetNameForCitation" />
<property name="indexFieldName" value="datasetNameForCitation_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetIdentifier" />
<property name="indexFieldName" value="datasetIdentifier_t" />
</bean>

<!-- Martin's suggested picker additions -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.projectName" />
<property name="indexFieldName" value="projectName_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.researchProgramName" />
<property name="indexFieldName" value="researchProgramName_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.researchProgramIdentifier" />
<property name="indexFieldName" value="researchProgramIdentifier_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.projectIdentifier" />
<property name="indexFieldName" value="projectIdentifier_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.projectIdentifierType" />
<property name="indexFieldName" value="projectIdentifierType_t" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.numberStudyLocation" />
<property name="indexFieldName" value="numberStudyLocation_i" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetAbstract" />
<property name="indexFieldName" value="datasetAbstract_t" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.ecologicalTheme" />
<property name="indexFieldName" value="ecologicalTheme_stxt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.ecologicalThemeSuggest" />
<property name="indexFieldName" value="ecologicalTheme_stxt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.threatsPressures" />
<property name="indexFieldName" value="threatsPressures_stxt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.threatsPressuresSuggest" />
<property name="indexFieldName" value="threatsPressures_stxt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.conservationManagement" />
<property name="indexFieldName" value="conservationManagement_stxt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.conservationManagementSuggest" />
<property name="indexFieldName" value="conservationManagement_stxt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.fieldsOfResearch" />
<property name="indexFieldName" value="fieldOfResearch_stxt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.socioEconomicObjectives" />
<property name="indexFieldName" value="socioEconomicObjectives_txt" />
</bean>

<!-- Study Location and Spatial  -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studyAreaDescription" />
<property name="indexFieldName" value="studyAreaDescription_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studyLocationFile" />
<property name="indexFieldName" value="geo_mv" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studyAreaGeometry" />
<property name="indexFieldName" value="geo_mv" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="bbox" />
<property name="indexFieldName" value="geo_mv" />
</bean>



<!-- Temporal -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.firstStudyAreaVisitDate" />
<property name="indexFieldName" value="firstStudyAreaVisitDate_dt" />
<property name="convertValueToClass" value="java.util.Date" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.lastStudyAreaVisitDate" />
<property name="indexFieldName" value="lastStudyAreaVisitDate_dt" />
<property name="convertValueToClass" value="java.util.Date" />
</bean>

<!-- IBRA -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.ibraRegion" />
<property name="indexFieldName" value="ibraRegion_txt" />
</bean>

<!-- Species Indexing - -->    
<!--  
SHD.studySpeciesFloraFile         entries to  studySpeciesFlora_txt, species_txt
SHD.studySpeciesFloraCommonFile
SHD.studySpeciesFaunaFile
SHD.studySpeciesFaunaCommonFile

SHD.taxonName                     entries to  studySpeciesFlora_txt, species_txt
SHD.floraCommonName
SHD.faunaTaxonomicName
SHD.faunaCommonName
-->

<!-- species_txt contains all of the species entries, fauna, flora including common -->
<!-- Flora File-->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFloraFile" />
<property name="indexFieldName" value="studySpeciesFlora_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFloraFile" />
<property name="indexFieldName" value="species_txt" />
</bean>
<!-- Flora Common File-->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFloraCommonFile" />
<property name="indexFieldName" value="studySpeciesFloraCommon_txt" />
<property name="speciesIndexFieldName" value="studySpeciesFlora_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFloraCommonFile" />
<property name="indexFieldName" value="species_txt" />
</bean>
<!-- Fauna File --> 
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFaunaFile" />
<property name="indexFieldName" value="studySpeciesFauna_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFaunaFile" />
<property name="indexFieldName" value="species_txt" />
</bean>
<!-- Fauna Common File -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFaunaCommonFile" />
<property name="indexFieldName" value="studySpeciesFaunaCommon_txt" />
<property name="speciesIndexFieldName" value="studySpeciesFauna_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.studySpeciesFaunaCommonFile" />
<property name="indexFieldName" value="species_txt" />
</bean>
<!-- Flora Picked -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.taxonName" />
<property name="indexFieldName" value="studySpeciesFlora_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.taxonName" />
<property name="indexFieldName" value="species_txt" />
</bean>
<!-- Flora Common Picked -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraCommonName" />
<property name="indexFieldName" value="studySpeciesFloraCommon_txt" />
<property name="speciesIndexFieldName" value="studySpeciesFlora_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraCommonName" />
<property name="indexFieldName" value="species_txt" />
<property name="speciesIndexFieldName" value="species_txt" />
</bean>

<!-- Fauna Picked -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaTaxonomicName" />
<property name="indexFieldName" value="studySpeciesFauna_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaTaxonomicName" />
<property name="indexFieldName" value="species_txt" />
</bean>

<!-- Fauna Common Picked -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaCommonName" />
<property name="indexFieldName" value="studySpeciesFaunaCommon_txt" />
<property name="speciesIndexFieldName" value="studySpeciesFauna_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaCommonName" />
<property name="indexFieldName" value="species_txt" />
<property name="speciesIndexFieldName" value="species_txt" />
</bean>


<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaGroup" />
<property name="indexFieldName" value="faunaGroup_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaGroupSuggest" />
<property name="indexFieldName" value="faunaGroup_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraGroup" />
<property name="indexFieldName" value="floraGroup_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraGroupSuggest" />
<property name="indexFieldName" value="floraGroup_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.environmentalFeatures" />
<property name="indexFieldName" value="environmentalFeatures_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.environmentalFeaturesSuggest" />
<property name="indexFieldName" value="environmentalFeatures_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.samplingDesign" />
<property name="indexFieldName" value="samplingDesign_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.samplingDesignSuggest" />
<property name="indexFieldName" value="samplingDesign_txt" />
</bean>

<!-- Flora, fauna technique -->


<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraTechnique" />
<property name="indexFieldName" value="floraTechnique_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.floraTechniqueSuggest" />
<property name="indexFieldName" value="floraTechnique_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaTechnique" />
<property name="indexFieldName" value="faunaTechnique_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.faunaTechniqueSuggest" />
<property name="indexFieldName" value="faunaTechnique_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.measurement" />
<property name="indexFieldName" value="measurement_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.measurementSuggest" />
<property name="indexFieldName" value="measurement_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.measurementTheme" />
<property name="indexFieldName" value="measurementTheme_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.measurementThemeSuggest" />
<property name="indexFieldName" value="measurementTheme_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.artefacts" />
<property name="indexFieldName" value="artefacts_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.artefactsSuggest" />
<property name="indexFieldName" value="artefacts_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.artefactsSuggest" />
<property name="indexFieldName" value="artefacts_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.associatedMaterialType" />
<property name="indexFieldName" value="associatedMaterialType_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.otherMaterialSuggest" />
<property name="indexFieldName" value="associatedMaterialType_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetContactName" />
<property name="indexFieldName" value="contactName_tn" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetContactOrganisation" />
<property name="indexFieldName" value="contactOrganisation_t" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetContactAddress" />
<property name="indexFieldName" value="contactAddress_tn" />
</bean>
<!--  
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetContactEmail" />
<property name="indexFieldName" value="contactEmail_tn" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.datasetContactPhone" />
<property name="indexFieldName" value="contactPhone_tn" />
</bean>

-->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.licenseType" />
<property name="indexFieldName" value="licenseType_t" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.embargo" />
<property name="indexFieldName" value="embargoDate_dt" />
<property name="convertValueToClass" value="java.util.Date" />
</bean>

<!-- Method names and abstract -->
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.methodName" />
<property name="indexFieldName" value="methodName_x_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.methodAbstract" />
<property name="indexFieldName" value="methodAbstract_x_txt" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.curationStatus" />
<property name="indexFieldName" value="curationStatus_t" />
</bean>

<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="author" />
<property name="indexFieldName" value="author_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.spatialScale" />
<property name="indexFieldName" value="spatialScale_txt" />
</bean>
<bean class="au.edu.aekos.shared.service.index.SubmissionIndexField">
<property name="sharedTag" value="SHD.spatialScaleSuggest" />
<property name="indexFieldName" value="spatialScale_txt" />
</bean>

*/