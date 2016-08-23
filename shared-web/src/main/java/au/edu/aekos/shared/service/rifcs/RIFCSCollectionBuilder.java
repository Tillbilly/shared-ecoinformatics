package au.edu.aekos.shared.service.rifcs;

import java.util.List;

import org.ands.rifcs.base.Address;
import org.ands.rifcs.base.AddressPart;
import org.ands.rifcs.base.CitationDate;
import org.ands.rifcs.base.CitationInfo;
import org.ands.rifcs.base.CitationMetadata;
import org.ands.rifcs.base.Collection;
import org.ands.rifcs.base.Contributor;
import org.ands.rifcs.base.Coverage;
import org.ands.rifcs.base.Description;
import org.ands.rifcs.base.Electronic;
import org.ands.rifcs.base.Location;
import org.ands.rifcs.base.Name;
import org.ands.rifcs.base.NamePart;
import org.ands.rifcs.base.Physical;
import org.ands.rifcs.base.RIFCS;
import org.ands.rifcs.base.RIFCSException;
import org.ands.rifcs.base.RIFCSWrapper;
import org.ands.rifcs.base.RegistryObject;
import org.ands.rifcs.base.RelatedObject;
import org.ands.rifcs.base.Relation;
import org.ands.rifcs.base.Right;
import org.ands.rifcs.base.Spatial;
import org.ands.rifcs.base.Subject;
import org.ands.rifcs.base.Temporal;
import org.springframework.util.StringUtils;


/**
 * Lifted from the tacticalRIFCS project - which contained too many aekos dependencies for my liking.
 * 
 * Will move to a common place later.
 * 
 * Does`nt follow the Builder pattern just yet ( i.e. returns itself for chaining of method calls, due to its heritage )
 *
 * @author btill
 */ @Deprecated //Now using rifcs-api for 1.5
public class RIFCSCollectionBuilder {

	private RIFCSWrapper rifcsWrapper = null;
	private RIFCS rifcs = null;
	private RegistryObject registryObject = null;
	private Collection c = null;
	
	public Collection initialiseCollection(String key, String originatingSource, String dateAccessioned, String dateModified, String group) throws RIFCSException{
		rifcsWrapper = new RIFCSWrapper();

		rifcs = rifcsWrapper.getRIFCSObject();
		registryObject = rifcs.newRegistryObject();
		registryObject.setKey(key);
		registryObject.setGroup(group);
		registryObject.setOriginatingSource(originatingSource);
		
		c = registryObject.newCollection();
		c.setType("collection");
		c.setDateAccessioned(dateAccessioned);
		c.setDateModified(dateModified);
		registryObject.addCollection(c);
		rifcs.addRegistryObject(registryObject);
		
		return c;
	}
	
	public void assignCollectionIdentifier(String type, String identifier) throws RIFCSException{
		if(c != null){
			c.addIdentifier(identifier, type);
		}
	}
	
	public void assignName(String type, String name) throws RIFCSException{
		if(c != null){
			Name n = c.newName();
			n.setType(type);
			NamePart np = n.newNamePart();
			np.setValue(name);
			n.addNamePart(np);
			c.addName(n);
		}
		
	}
	
	public void addRightToCollection(String accessRightValue, String accessRightsUri, String accessRightType,
			                         String licenseValue, String licenseUri, String licenseType,
			                         String rightStatementValue, String rightStatementUri ) throws RIFCSException{
			Right right= c.newRight();
			
			if(accessRightValue != null && accessRightType != null){
			    right.setAccessRights(accessRightValue, accessRightsUri, accessRightType);
			}
			
			if(licenseValue != null && licenseType != null){
			    right.setLicence(licenseValue, licenseUri, licenseType);
			}
			
			if(rightStatementValue != null){
			    right.setRightsStatement(rightStatementValue, rightStatementUri);
			}
			c.addRight(right);
		
	}
	
	public void addPhysicalAddress(String addressType, List<String> addrPartPairs ) throws RIFCSException{
		if(c.getLocations() == null || c.getLocations().size() == 0 ){
			Location loc = c.newLocation();
			c.addLocation(loc);
		}
		Location l = c.getLocations().get(0);
		Address addr = l.newAddress();
		Physical physAddr = addr.newPhysical();
		physAddr.setType(addressType);
		for(int x = 0; x < addrPartPairs.size() - 1 ; x =  x + 2){
			String type = addrPartPairs.get(x);
			String content = addrPartPairs.get(x + 1);
			AddressPart addrPart = physAddr.newAddressPart();
			addrPart.setType(type);
			addrPart.setValue(content);
			physAddr.addAddressPart(addrPart);
		}
		
        addr.addPhysical(physAddr);
		l.addAddress(addr);
	}
	
	public void addElectronicAddress(String type, String content) throws RIFCSException{
		if(c.getLocations() == null || c.getLocations().size() == 0 ){
			Location loc = c.newLocation();
			c.addLocation(loc);
		}
		Location l = c.getLocations().get(0);
		Address addr = l.newAddress();
		Electronic el = addr.newElectronic();
		el.setType(type);
		el.setValue(content);
		addr.addElectronic(el);
		l.addAddress(addr);
	}
	
	public void addRelatedObject(String key, String relationType, String relationDescription, String url) throws RIFCSException{
		RelatedObject ro = c.newRelatedObject();
		ro.setKey(key);
		Relation rel = ro.newRelation();
		rel.setType(relationType);
		rel.setDescription(relationDescription);
		rel.setURL(url);
		ro.addRelation(rel);
		c.addRelatedObject(ro);
	}
	
	public void addSubject(String type, String value) throws RIFCSException{
		if(c != null && StringUtils.hasLength(type) && StringUtils.hasLength(value)){
			Subject sub = c.newSubject();
			sub.setType(type);
			sub.setValue(value);
			c.addSubject(sub);
		}
	}
	
	public void addDescription(String type, String value) throws RIFCSException{
		Description desc = c.newDescription();
		desc.setType(type);
		desc.setValue(value);
		c.addDescription(desc);
	}
	
	public void addTemporalCoverage(String dateFromFormat, String dateFrom, String dateToFormat, String dateTo) throws RIFCSException{
	    Coverage cov = c.newCoverage();
	    Temporal t = cov.newTemporal();
	    if(StringUtils.hasLength(dateFromFormat)){
	        t.addDate(dateFrom, "dateFrom", dateFromFormat);
	    }
	    if(StringUtils.hasLength(dateToFormat)){
	        t.addDate(dateTo, "dateTo", dateToFormat);
	    }
	    cov.addTemporal(t);
	    c.addCoverage(cov);
	}
	
	public void addSpatialCoverage(String type, String value) throws RIFCSException {
		Coverage cov = c.newCoverage();
		Spatial sp = cov.newSpatial();
		sp.setType(type);
		sp.setValue(value);
		cov.addSpatial(sp);
		c.addCoverage(cov);
	}
	
	public void addRights(String rightsStatement, String license, String accessRights) throws RIFCSException{
		Right r = c.newRight();
		r.setRightsStatement(rightsStatement);
		r.setLicence(license);
		r.setAccessRights(accessRights);
		c.addRight(r);
	}
	
	public void addFullCitationAndMetadata(String fullCitationString, String doi, String title,
			String publisher, String placePublished, String submittedDate,
			String publishedDate, List<String> authorList)
			throws RIFCSException {
		CitationInfo ci = c.newCitationInfo();
		ci.setCitation(fullCitationString, null);
		CitationMetadata cm = ci.newCitationMetadata();
		if (StringUtils.hasLength(doi)) {
			cm.setIdentifier(doi, "doi");
		}
		if (StringUtils.hasLength(title)) {
			cm.setTitle(title);
		}
		if (StringUtils.hasLength(publisher)) {
			cm.setPublisher(publisher);
		}
		if (StringUtils.hasLength(placePublished)) {
			cm.setPlacePublished(placePublished);
		}
		if (StringUtils.hasLength(submittedDate)) {
			CitationDate cd = cm.newCitationDate();
			cd.setValue(submittedDate);
			cd.setType("dc.dateSubmitted");
			cm.addDate(cd);
		}
		if (StringUtils.hasLength(publishedDate)) {
			CitationDate cd = cm.newCitationDate();
			cd.setValue(submittedDate);
			cd.setType("dc.available");
			cm.addDate(cd);
		}
		if (authorList != null && authorList.size() > 0) {
			for (String author : authorList) {
				Contributor contributor = cm.newContributor();
				NamePart np = contributor.newNamePart();
				np.setValue(author);
				contributor.addNamePart(np);
				cm.addContributor(contributor);
			}
		}
		ci.addCitationMetadata(cm);
		c.addCitationInfo(ci);
	}
	
	
	
	public void addCitationMetadata(String doi, 
			                        String title, 
			                        String publisher, 
			                        String placePublished,
			                        String submittedDate,
			                        String publishedDate,
			                        List<String> authorList) throws RIFCSException{
		CitationInfo ci = c.newCitationInfo();
		CitationMetadata cm = ci.newCitationMetadata();
		if(StringUtils.hasLength(doi)){
			cm.setIdentifier(doi, "doi");
		}
		if(StringUtils.hasLength(title)){
			cm.setTitle(title);
		}
		if(StringUtils.hasLength(publisher)){
			cm.setPublisher(publisher);
		}
		if(StringUtils.hasLength(placePublished)){
			cm.setPlacePublished(placePublished);
		}
		if(StringUtils.hasLength(submittedDate)){
			CitationDate cd = cm.newCitationDate();
			cd.setValue(submittedDate);
			cd.setType("dc.dateSubmitted");
			cm.addDate(cd);
		}
        if(StringUtils.hasLength(publishedDate)){
        	CitationDate cd = cm.newCitationDate();
			cd.setValue(submittedDate);
			cd.setType("dc.available");
			cm.addDate(cd);
		}
        if(authorList != null && authorList.size() > 0){
        	for(String author : authorList){
				Contributor contributor = cm.newContributor();
				NamePart np = contributor.newNamePart();
				np.setValue(author);
				contributor.addNamePart(np);
				cm.addContributor(contributor);
			}
        }
        ci.addCitationMetadata(cm);
		c.addCitationInfo(ci);
	}
	
	
	public void addCitation(String style, String content) throws RIFCSException{
		CitationInfo ci = c.newCitationInfo();
		ci.setCitation(content, style);
		c.addCitationInfo(ci);
	}
	
	public void addCitationString(String citationString) throws RIFCSException {
		CitationInfo ci = c.newCitationInfo();
		ci.setCitation(citationString, null);
		c.addCitationInfo(ci);
	}
	
	public void addCitationAuthors(List<String> authorList) throws RIFCSException{
		if(authorList != null && authorList.size() > 0){
			CitationInfo ci = c.newCitationInfo();
			CitationMetadata cm = ci.newCitationMetadata();
			for(String author : authorList){
				Contributor contributor = cm.newContributor();
				NamePart np = contributor.newNamePart();
				np.setValue(author);
				contributor.addNamePart(np);
				cm.addContributor(contributor);
			}
			ci.addCitationMetadata(cm);
			c.addCitationInfo(ci);
		}
		
	}
	

	public RIFCSWrapper getRifcsWrapper() {
		return rifcsWrapper;
	}

	public void setRifcsWrapper(RIFCSWrapper rifcsWrapper) {
		this.rifcsWrapper = rifcsWrapper;
	}

	public RIFCS getRifcs() {
		return rifcs;
	}

	public void setRifcs(RIFCS rifcs) {
		this.rifcs = rifcs;
	}

	public RegistryObject getRegistryObject() {
		return registryObject;
	}

	public void setRegistryObject(RegistryObject registryObject) {
		this.registryObject = registryObject;
	}

	public Collection getC() {
		return c;
	}

	public void setC(Collection c) {
		this.c = c;
	}
	
}

