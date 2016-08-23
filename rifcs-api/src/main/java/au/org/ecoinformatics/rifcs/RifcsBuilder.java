package au.org.ecoinformatics.rifcs;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import au.org.ecoinformatics.rifcs.jaxb.CitationDateType;
import au.org.ecoinformatics.rifcs.jaxb.CitationInfoType;
import au.org.ecoinformatics.rifcs.jaxb.CitationInfoType.CitationMetadata;
import au.org.ecoinformatics.rifcs.jaxb.CitationNameType;
import au.org.ecoinformatics.rifcs.jaxb.Collection;
import au.org.ecoinformatics.rifcs.jaxb.CoverageType;
import au.org.ecoinformatics.rifcs.jaxb.DatesType;
import au.org.ecoinformatics.rifcs.jaxb.DescriptionType;
import au.org.ecoinformatics.rifcs.jaxb.ElectronicAddressType;
import au.org.ecoinformatics.rifcs.jaxb.IdentifierType;
import au.org.ecoinformatics.rifcs.jaxb.LocationType;
import au.org.ecoinformatics.rifcs.jaxb.NameType;
import au.org.ecoinformatics.rifcs.jaxb.ObjectFactory;
import au.org.ecoinformatics.rifcs.jaxb.PhysicalAddressType;
import au.org.ecoinformatics.rifcs.jaxb.RegistryObjects;
import au.org.ecoinformatics.rifcs.jaxb.RegistryObjects.RegistryObject;
import au.org.ecoinformatics.rifcs.jaxb.RelatedInfoType;
import au.org.ecoinformatics.rifcs.jaxb.RelatedObjectType;
import au.org.ecoinformatics.rifcs.jaxb.RelationType;
import au.org.ecoinformatics.rifcs.jaxb.RelationType.Description;
import au.org.ecoinformatics.rifcs.jaxb.RightsInfoType;
import au.org.ecoinformatics.rifcs.jaxb.RightsType;
import au.org.ecoinformatics.rifcs.jaxb.RightsTypedInfoType;
import au.org.ecoinformatics.rifcs.jaxb.SpatialType;
import au.org.ecoinformatics.rifcs.jaxb.SubjectType;
import au.org.ecoinformatics.rifcs.jaxb.TemporalType;
import au.org.ecoinformatics.rifcs.vocabs.CitationDateTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.CitationIdentifierTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.CitationStyleEnum;
import au.org.ecoinformatics.rifcs.vocabs.CollectionRelationTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.CollectionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DateFormatEnum;
import au.org.ecoinformatics.rifcs.vocabs.DateTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DatesTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.DescriptionTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.ElectronicAddressTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.IdentifierTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.LicenseTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.NameTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.PhysicalAddressPartTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.PhysicalAddressTypeEnum;
import au.org.ecoinformatics.rifcs.vocabs.SpatialTypeEnum;

/**
 * Builder used to populate a RegistryObjects JAXB graph with RIFCS data.
 * 
 * Built originally for JAXB Objects generated from the rifcs 1.5 schema.
 * 
 * Also only supports a single registry object for a Collection ( at this stage )
 * 
 * @author btill
 */
public class RifcsBuilder {
	
	private RegistryObjects registryObjects; 
	private RegistryObject registryObject;
	private LocationType collectionLocationType;
	private CoverageType collectionCoverageType;
	private final ObjectFactory objectFactory = new ObjectFactory();
	
	public RifcsBuilder(String key, String originatingSource, String group) {
		registryObjects = new RegistryObjects();
		registryObject = new RegistryObject();
		registryObject.setKey(key); 
		if(originatingSource != null){
			RegistryObjects.RegistryObject.OriginatingSource  os = new RegistryObjects.RegistryObject.OriginatingSource ();
			os.setValue(originatingSource);
			registryObject.setOriginatingSource(os);
		}
		if(group != null){
			registryObject.setGroup(group);
		}
		registryObjects.getRegistryObject().add(registryObject);
	}
	
	/**
	 * Use this factory method to initialise the builder to generate a collection - for shared/aekos rifcss
	 * @param key
	 * @param originatingSource
	 * @param group
	 * @param dateModified
	 * @param dateAccessioned
	 * @param collectionType
	 * @return
	 */
	public static RifcsBuilder getCollectionBuilderInstance(String key, String originatingSource, String group, String dateModified, String dateAccessioned, CollectionTypeEnum collectionType ){
		RifcsBuilder builder = new RifcsBuilder(key, originatingSource, group);
		Collection collection = new Collection();
		collection.setType(collectionType.getValue());
		if(dateModified != null){
			collection.setDateModified(dateModified);
		}
		if(dateAccessioned != null){
			collection.setDateAccessioned(dateAccessioned);
		}
		builder.setCollection(collection);
		return builder;
	}

	/**
	 * Sets the identifier as a "local" type in the collection
	 * @param identifier
	 * @return
	 */
	public RifcsBuilder addCollectionIdentifier(String identifier){
		return addCollectionIdentifier(identifier, IdentifierTypeEnum.LOCAL);
	}
	
    public RifcsBuilder addCollectionIdentifier(String identifier, IdentifierTypeEnum type){
		IdentifierType id = new IdentifierType();
		id.setType(type.getValue());
		id.setValue(identifier);
		addToCollection(id);
		return this;
	}
	
    public RifcsBuilder addCollectionPrimaryName(String name){
    	return  addCollectionName(name, NameTypeEnum.PRIMARY);
    }
    
    public RifcsBuilder addCollectionName(String name, NameTypeEnum type){
    	NameType nameType = new NameType();
    	nameType.setType(type.getValue());
    	NameType.NamePart namePart = new NameType.NamePart();
    	namePart.setValue(name);
    	nameType.getNamePart().add(namePart);
    	return addCollectionNameType(nameType);
    }
    
    public RifcsBuilder addCollectionNameType(NameType nameType){
    	addToCollection(nameType);
    	return this;
    }
    
    public RifcsBuilder addCollectionDates(DatesTypeEnum datesType, Date fromDate ){
    	return addCollectionDates(datesType, RifcsUtils.convertDateToW3CDTF(fromDate), null);
    }
    
    /**
     * Dates must be provided in the W3CDTF Format
     * @param datesType
     * @param fromDate
     * @return
     */
    public RifcsBuilder addCollectionDates(DatesTypeEnum datesType, String fromDate ){
    	return addCollectionDates(datesType, fromDate, null);
    }
    
    /**
     * Dates must be provided in the W3CDTF Format
     * @param datesType
     * @param fromDate
     * @param toDate
     * @return
     */
    public RifcsBuilder addCollectionDates(DatesTypeEnum datesType, String fromDate, String toDate ){
    	DatesType dates = new DatesType();
    	dates.setType(datesType.getValue());
    	if(fromDate != null){
    		DatesType.Date fromDt = new DatesType.Date();
    		fromDt.setDateFormat(DateFormatEnum.W3CDTF.getValue());
    		fromDt.setType(DateTypeEnum.DATE_FROM.getValue());
    		fromDt.setValue(fromDate);
    		dates.getDate().add(fromDt);
    	}
    	if(toDate != null){
    		DatesType.Date toDt = new DatesType.Date();
    		toDt.setDateFormat(DateFormatEnum.W3CDTF.getValue());
    		toDt.setType(DateTypeEnum.DATE_TO.getValue());
    		toDt.setValue(toDate);
    		dates.getDate().add(toDt);
    	}
    	addToCollection(dates);
    	return this;
    }
    
    public RifcsBuilder addCollectionPhysicalAddress(PhysicalAddressTypeEnum physicalAddressType,
    		                               String singleLineAddressString,
    		                               String phoneNumber,
    		                               String faxNumber){
    	PhysicalAddressType physicalAddress = new PhysicalAddressType();
    	physicalAddress.setType(physicalAddressType.getValue());
    	if(singleLineAddressString != null){
    		PhysicalAddressType.AddressPart address = new PhysicalAddressType.AddressPart();
    		address.setValue(singleLineAddressString);
    		address.setType(PhysicalAddressPartTypeEnum.TEXT.getValue());
    		physicalAddress.getAddressPart().add(address);
    	}
    	if(phoneNumber != null){
    		PhysicalAddressType.AddressPart ph = new PhysicalAddressType.AddressPart();
    		ph.setValue(phoneNumber);
    		ph.setType(PhysicalAddressPartTypeEnum.TELEPHONE_NUMBER.getValue());
    		physicalAddress.getAddressPart().add(ph);
    	}
    	if(faxNumber != null){
    		PhysicalAddressType.AddressPart fax = new PhysicalAddressType.AddressPart();
    		fax.setValue(faxNumber);
    		fax.setType(PhysicalAddressPartTypeEnum.FAX_NUMBER.getValue());
    		physicalAddress.getAddressPart().add(fax);
    	}
    	LocationType.Address addr = new LocationType.Address();
    	addr.getElectronicOrPhysical().add(physicalAddress);
    	getCollectionLocationType().getAddress().add(addr);
    	return this;
    }
    
    public RifcsBuilder addCollectionPhysicalAddress(PhysicalAddressTypeEnum physicalAddressType,
            List<String> addressLines,
            String phoneNumber,
            String faxNumber){
		PhysicalAddressType physicalAddress = new PhysicalAddressType();
		physicalAddress.setType(physicalAddressType.getValue());
		if (addressLines != null && addressLines.size() > 0) {
			for(String addressLine : addressLines){
			    PhysicalAddressType.AddressPart addressPart = new PhysicalAddressType.AddressPart();
			    addressPart.setValue(addressLine);
			    addressPart.setType(PhysicalAddressPartTypeEnum.ADDRESS_LINE.getValue());
			    physicalAddress.getAddressPart().add(addressPart);
			}
		}
		if (phoneNumber != null) {
			PhysicalAddressType.AddressPart ph = new PhysicalAddressType.AddressPart();
			ph.setValue(phoneNumber);
			ph.setType(PhysicalAddressPartTypeEnum.TELEPHONE_NUMBER.getValue());
			physicalAddress.getAddressPart().add(ph);
		}
		if (faxNumber != null) {
			PhysicalAddressType.AddressPart fax = new PhysicalAddressType.AddressPart();
			fax.setValue(faxNumber);
			fax.setType(PhysicalAddressPartTypeEnum.FAX_NUMBER.getValue());
			physicalAddress.getAddressPart().add(fax);
		}
		LocationType.Address addr = new LocationType.Address();
		addr.getElectronicOrPhysical().add(physicalAddress);
		getCollectionLocationType().getAddress().add(addr);
		return this;
	}
    
    public RifcsBuilder addCollectionElectronicAddress(String emailAddress, String url ){
    	LocationType.Address address = new LocationType.Address();
    	if(emailAddress != null){
    		ElectronicAddressType electronicEmailAddress = new ElectronicAddressType();
        	electronicEmailAddress.setType(ElectronicAddressTypeEnum.EMAIL.getValue());
    		electronicEmailAddress.setValue(emailAddress);
    		address.getElectronicOrPhysical().add(electronicEmailAddress);
    	}
    	if(url != null){
    		ElectronicAddressType urlAddress = new ElectronicAddressType();
        	urlAddress.setType(ElectronicAddressTypeEnum.URL.getValue());
    		urlAddress.setValue(url);
    		address.getElectronicOrPhysical().add(urlAddress);
    	}
    	if(address.getElectronicOrPhysical().size() > 0){
    		getCollectionLocationType().getAddress().add(address);
    	}
    	return this;
    }
           
    /**
     * Dates should be provided in W3CDTF format
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public RifcsBuilder addCollectionTemporalCoverage(String dateFrom, String dateTo){
    	TemporalType temporal = new TemporalType();
    	if(dateFrom != null){
    		TemporalType.Date fromDt = new TemporalType.Date();
    		fromDt.setValue(dateFrom);
    		fromDt.setType(DateTypeEnum.DATE_FROM.getValue());
    		fromDt.setDateFormat(DateFormatEnum.W3CDTF.getValue());
    		temporal.getDateOrText().add(fromDt);
    	}
    	if(dateTo != null){
    		TemporalType.Date toDt = new TemporalType.Date();
    		toDt.setValue(dateTo);
    		toDt.setType(DateTypeEnum.DATE_TO.getValue());
    		toDt.setDateFormat(DateFormatEnum.W3CDTF.getValue());
    		temporal.getDateOrText().add(toDt);
    	}
    	getCollectionCoverageType().getSpatialOrTemporal().add(temporal);
    	return this;
    }
    
    public RifcsBuilder addCollectionSpatialCoverage(SpatialTypeEnum type, String value){
    	SpatialType spatialType = new SpatialType();
    	spatialType.setType(type.getValue());
    	spatialType.setValue(value);
    	getCollectionCoverageType().getSpatialOrTemporal().add(spatialType);
    	return this;
    }
    
    public RifcsBuilder addCollectionRelatedObject(String key, CollectionRelationTypeEnum type){
    	return addCollectionRelatedObject(key, type, null, null);
    }
    
    public RifcsBuilder addCollectionRelatedObject(String key, CollectionRelationTypeEnum type, String description, String url){
    	RelatedObjectType relatedObject = new RelatedObjectType();
    	relatedObject.setKey(key);
    	RelationType relationType = new RelationType();
    	relationType.setType(type.getValue());
    	if(description != null){
    	    Description desc = objectFactory.createRelationTypeDescription();
    	    desc.setValue(description);
    	    relationType.getDescriptionOrUrl().add(desc);
    	}
    	if(url != null){
    		relationType.getDescriptionOrUrl().add(url);
    	}
    	relatedObject.getRelation().add(relationType);
    	addToCollection(relatedObject);
    	return this;
    }
    
    /**
     * Type is mandatory but there is no vocabulary for subject type
     * @param value
     * @param type
     * @return
     */
    public RifcsBuilder addCollectionSubject(String value, String type){
    	SubjectType subject = new SubjectType();
    	subject.setValue(value);
    	subject.setType(type);
    	addToCollection(subject);
    	return this;
    }
    
    public RifcsBuilder addCollectionDescription(String description, DescriptionTypeEnum type){
    	DescriptionType descriptionType = new DescriptionType();
    	descriptionType.setValue(description);
    	descriptionType.setType(type.getValue());
    	addToCollection(descriptionType);
    	return this;
    }
    
   /**
    * 
    * @param rightsStatement
    * @param licenseString
    * @param licenseType This is optional, but include either licenseString OR licenseType - TERN-BY will have to be in the licenseString
    * @param accessRights
    * @return
    */
    public RifcsBuilder addCollectionRights(String rightsStatement, String licenseString, LicenseTypeEnum licenseType, String accessRights){
    	RightsType rights = new RightsType();
    	//Rights Statement 
    	if(rightsStatement != null){
    		RightsInfoType rightsInfoType = objectFactory.createRightsInfoType();
    		rightsInfoType.setValue(rightsStatement);
    		JAXBElement<RightsInfoType> el = objectFactory.createRightsTypeRightsStatement(rightsInfoType);
    		rights.getRightsStatementOrLicenceOrAccessRights().add(el);
    	}
    	//License
    	assignLicenseInfoToRightsElement(licenseString, licenseType, rights );
    	//Access Rights
    	if(accessRights != null){
    		RightsTypedInfoType rightsTypedInfoTypeAccess = objectFactory.createRightsTypedInfoType();
    		rightsTypedInfoTypeAccess.setValue(accessRights);
    		JAXBElement<RightsTypedInfoType> accessEl = objectFactory.createRightsTypeAccessRights(rightsTypedInfoTypeAccess);
    		rights.getRightsStatementOrLicenceOrAccessRights().add(accessEl);
    	}
    	if(rights.getRightsStatementOrLicenceOrAccessRights().size() > 0){
    		addToCollection(rights);
    	}
    	return this;
    }
    
    private void assignLicenseInfoToRightsElement(String licenseString, LicenseTypeEnum licenseType, RightsType rightsElement ){
    	if(licenseString == null && licenseType == null){
    		return;
    	}
    	RightsTypedInfoType rightsTypedInfoType = objectFactory.createRightsTypedInfoType();
    	if(licenseString != null){
    		rightsTypedInfoType.setValue(licenseString);
    	}
    	if(licenseType != null){
    		rightsTypedInfoType.setType(licenseType.getValue());
    	}
    	JAXBElement<RightsTypedInfoType> licenseEl = objectFactory.createRightsTypeLicence(rightsTypedInfoType);
    	rightsElement.getRightsStatementOrLicenceOrAccessRights().add(licenseEl);
    }
    
    /**
     * We aren't currently setting any relatedInfo, but client users are free to create their own
     * @param relatedInfo
     * @return
     */
    public RifcsBuilder addCollectionRelatedInfo(RelatedInfoType relatedInfo){
    	//RelatedInfoType relatedInfo = objectFactory.createRelatedInfoType()
    	addToCollection(relatedInfo);
    	return this;
    }
    
    /**
     * 
     * @param citationString
     * @param citationStyle may be null
     * @return
     */
    public RifcsBuilder addCollectionFullCitationString(String citationString, CitationStyleEnum citationStyle){
    	CitationInfoType citationInfo = objectFactory.createCitationInfoType();
    	CitationInfoType.FullCitation fullCitation = new CitationInfoType.FullCitation();
    	fullCitation.setValue(citationString);
    	if(citationStyle != null){
    		fullCitation.setStyle(citationStyle.getValue());
    	}
    	citationInfo.setFullCitation(fullCitation);
    	addToCollection(citationInfo);
    	return this;
    }
	
    /**
     * 
     * @param doi
     * @param title
     * @param publisher
     * @param version
     * @param submittedDate    W3CDTF format
     * @param publishedDate    W3CDTF format
     * @param authorList
     * @return
     */
    public RifcsBuilder addCollectionCitationMetadata(String doi, 
            String title, 
            String publisher,
            String version,
            String submittedDate,
            String publishedDate,
            List<String> authorList){
    	List<CitationDateType> citationDateList = new ArrayList<CitationDateType>();
    	if(submittedDate != null){
    		CitationDateType submittedCitationDate = new CitationDateType();
    		submittedCitationDate.setValue(submittedDate);
    		submittedCitationDate.setType(CitationDateTypeEnum.DATE_SUBMITTED.getValue());
    		citationDateList.add(submittedCitationDate);
    	}
    	if(publishedDate != null){
    		CitationDateType publishedCitationDate = new CitationDateType();
    		publishedCitationDate.setValue(publishedDate);
    		publishedCitationDate.setType(CitationDateTypeEnum.PUBLICATION_DATE.getValue());
    		citationDateList.add(publishedCitationDate);
    	}
    	List<CitationNameType> citationNameList = new ArrayList<CitationNameType>();
    	if(authorList.size() > 0){
	    	for(int x = 1; x <= authorList.size(); x++){
	    		CitationNameType name = new CitationNameType();
	    		name.setSeq(BigInteger.valueOf((long) x));
	    		CitationNameType.NamePart namePart = new CitationNameType.NamePart();
	    		namePart.setValue(authorList.get(x -1 ));
	    		name.getNamePart().add(namePart);
	    		citationNameList.add(name);
	    	}
    	}
    	return addCollectionCitationMetadata(doi, 
        		CitationIdentifierTypeEnum.DOI,
        		citationNameList,
                title, 
                publisher,
                version,
                citationDateList);
    }
    
    /**
     * @param citationId
     * @param citationIdType
     * @param contributorList
     * @param title
     * @param publisher
     * @param version
     * @param citationDateList	W3CDTF format
     * @return
     */
    public RifcsBuilder addCollectionCitationMetadata(String citationId, 
    		CitationIdentifierTypeEnum citationIdType,
    		List<CitationNameType> contributorList,
            String title, 
            String publisher,
            String version,
            List<CitationDateType> citationDateList){
    	CitationInfoType citationInfoType = new CitationInfoType();
    	CitationMetadata citationMetadata = objectFactory.createCitationInfoTypeCitationMetadata();
    	citationInfoType.setCitationMetadata(citationMetadata);
    	
    	//Order - 
    	//identifier
    	if(citationId != null){
    		IdentifierType idType = new IdentifierType();
    		idType.setValue(citationId);
    		if(citationIdType != null){
    			idType.setType(citationIdType.getValue());
    		}
    		JAXBElement<IdentifierType>  idElement = objectFactory.createCitationInfoTypeCitationMetadataIdentifier(idType);
    		citationMetadata.getIdentifierOrContributorOrTitle().add(idElement);
    	}
    	//contributor
    	if(contributorList != null && contributorList.size() > 0){
    		for(CitationNameType citationName : contributorList ){
    			JAXBElement<CitationNameType> contributorEl = objectFactory.createCitationInfoTypeCitationMetadataContributor(citationName);
    			citationMetadata.getIdentifierOrContributorOrTitle().add(contributorEl);
    		}
    	}
    	//title
    	JAXBElement<String> titleElement = objectFactory.createCitationInfoTypeCitationMetadataTitle(title);
    	citationMetadata.getIdentifierOrContributorOrTitle().add(titleElement);
    	//publisher
    	JAXBElement<String> publisherElement = objectFactory.createCitationInfoTypeCitationMetadataPublisher(publisher);
    	citationMetadata.getIdentifierOrContributorOrTitle().add(publisherElement);
    	//version
    	if (version != null) {
    		JAXBElement<String> versionElement = objectFactory.createCitationInfoTypeCitationMetadataVersion(version);
    		citationMetadata.getIdentifierOrContributorOrTitle().add(versionElement);
    	}
    	//Date(s)
    	if(citationDateList != null && citationDateList.size()  > 0){
    		for(CitationDateType citationDateType : citationDateList){
    			JAXBElement<CitationDateType> dateElement = objectFactory.createCitationInfoTypeCitationMetadataDate(citationDateType);
    			citationMetadata.getIdentifierOrContributorOrTitle().add(dateElement);
    		}
    	}
    	addToCollection(citationInfoType);
    	return this;
    }
    
    public RifcsBuilder addRelatedInfo(String identifierValue, String identifierType, String title) {
		RelatedInfoType relatedInfo = new RelatedInfoType();
		IdentifierType identifier = objectFactory.createIdentifierType();
		identifier.setValue(identifierValue);
		identifier.setType(identifierType);
		relatedInfo.getIdentifierOrRelationOrTitle().add(objectFactory.createRelatedInfoTypeIdentifier(identifier));
		relatedInfo.getIdentifierOrRelationOrTitle().add(objectFactory.createRelatedInfoTypeTitle(title));
		addToCollection(relatedInfo);
		return this;
	}
    
    public RifcsBuilder addRelatedInfo(String identifierValue, String identifierType) {
		RelatedInfoType relatedInfo = new RelatedInfoType();
		IdentifierType identifier = objectFactory.createIdentifierType();
		identifier.setValue(identifierValue);
		identifier.setType(identifierType);
		relatedInfo.getIdentifierOrRelationOrTitle().add(objectFactory.createRelatedInfoTypeIdentifier(identifier));
		addToCollection(relatedInfo);
		return this;
	}

    public String getXml() throws JAXBException{
    	Marshaller m = getRegistryObjectsMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(registryObjects, sw);
    	return sw.toString();
    }
    
    private Marshaller getRegistryObjectsMarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(RegistryObjects.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd");
	    return m;
	}
    
    private CoverageType getCollectionCoverageType(){
    	if(collectionCoverageType == null){
    		collectionCoverageType = new CoverageType();
    		addToCollection(collectionCoverageType);
    	}
    	return collectionCoverageType;
    }
	
    private LocationType getCollectionLocationType(){
    	if(collectionLocationType == null){
    		collectionLocationType = new LocationType();
    		addToCollection(collectionLocationType);
    	}
    	return collectionLocationType;
    }
	
	private void addToCollection(Object collectionElement){
		getCollection().getIdentifierOrNameOrDates().add(collectionElement);
	}
	
	public Collection getCollection() {
		return registryObject.getCollection();
	}

	public void setCollection(Collection collection) {
		registryObject.setCollection(collection);
	}

	public RegistryObjects getRegistryObjects() {
		return registryObjects;
	}
}
