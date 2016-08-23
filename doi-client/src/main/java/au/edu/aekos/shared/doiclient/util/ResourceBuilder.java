package au.edu.aekos.shared.doiclient.util;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import au.edu.aekos.shared.doiclient.jaxb.ContributorType;
import au.edu.aekos.shared.doiclient.jaxb.DateType;
import au.edu.aekos.shared.doiclient.jaxb.DescriptionType;
import au.edu.aekos.shared.doiclient.jaxb.RelatedIdentifierType;
import au.edu.aekos.shared.doiclient.jaxb.RelationType;
import au.edu.aekos.shared.doiclient.jaxb.Resource;
import au.edu.aekos.shared.doiclient.jaxb.Resource.AlternateIdentifiers.AlternateIdentifier;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Dates;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Dates.Date;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Formats;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Identifier;
import au.edu.aekos.shared.doiclient.jaxb.Resource.RelatedIdentifiers.RelatedIdentifier;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Titles;
import au.edu.aekos.shared.doiclient.jaxb.Resource.Titles.Title;
import au.edu.aekos.shared.doiclient.jaxb.ResourceType;
import au.edu.aekos.shared.doiclient.jaxb.TitleType;

public class ResourceBuilder {

	private Resource resource; 
	
	public ResourceBuilder(){
		reset();
	}
	
	public void reset(){
		resource = new Resource();
		Identifier emptyIdentifier = new Identifier();
		emptyIdentifier.setIdentifierType("DOI");
		resource.setIdentifier(emptyIdentifier);
	}
	
	public Resource getResource(){
		
		return resource;
	}
	
	public ResourceBuilder setDoi(String doi){
		Identifier identifier = new Identifier();
		identifier.setIdentifierType("DOI");
		identifier.setValue(doi);
		resource.setIdentifier(identifier);
		return this;
	}
	
//	resource.getRelatedIdentifiers()
	public ResourceBuilder addRelatedIdentifier(String id, RelatedIdentifierType idType, RelationType relationType){
		RelatedIdentifier ri = new RelatedIdentifier();
		ri.setValue(id);
		ri.setRelationType(relationType);
		ri.setRelatedIdentifierType(idType);
		if(resource.getRelatedIdentifiers() == null ){
			Resource.RelatedIdentifiers relatedIdentifiers = new Resource.RelatedIdentifiers();
			relatedIdentifiers.getRelatedIdentifier().add(ri);
			resource.setRelatedIdentifiers(relatedIdentifiers);
		}else{
		    resource.getRelatedIdentifiers().getRelatedIdentifier().add(ri);
		}
		return this;
	}
	
	public ResourceBuilder addAlternateIdentifier(String id, String idType ){
		AlternateIdentifier altId = new AlternateIdentifier();
		altId.setValue(id);
		altId.setAlternateIdentifierType(idType);
		
		if( resource.getAlternateIdentifiers() == null ){
			Resource.AlternateIdentifiers altIds = new Resource.AlternateIdentifiers();
			altIds.getAlternateIdentifier().add(altId);
			resource.setAlternateIdentifiers(altIds);
		}else{
			resource.getAlternateIdentifiers().getAlternateIdentifier().add(altId);
		}
		return this;
	}

	public ResourceBuilder setTitle(String titleIn){
		return addTitle(titleIn, null);
	}
	
	public ResourceBuilder addTitle(String titleIn, TitleType titleType){
		Title title = new Title();
		title.setValue(titleIn);
		title.setTitleType(titleType);
		if(resource.getTitles() == null){
			Titles titles = new Titles();
			resource.setTitles(titles);
		}
		resource.getTitles().getTitle().add(title);
		return this;
	}
	
	/**
	 * Not sure on the best format for creator name just yet.
	 * @param name
	 * @return
	 */
	public ResourceBuilder addCreator(String name ){
		return addCreator(name, null);
	}
	
	public ResourceBuilder addCreator(String name, Resource.Creators.Creator.NameIdentifier nameIdentifier ){
		Resource.Creators.Creator c = new Resource.Creators.Creator();
		c.setCreatorName(name);
		c.setNameIdentifier(nameIdentifier);
		if(resource.getCreators() == null){
			Resource.Creators creators = new Resource.Creators();
			resource.setCreators(creators);
		}
		resource.getCreators().getCreator().add(c);
		return this;
	}

//	resource.getContributors()
	public ResourceBuilder addContributor(String contributorName, ContributorType contributorType){
		Resource.Contributors.Contributor contributor = new Resource.Contributors.Contributor();
		contributor.setContributorType(contributorType);
		QName contributorNameQName = new QName("http://datacite.org/schema/kernel-2.1", "contributorName");
		JAXBElement<String> contributorNameElement = new JAXBElement<String>(contributorNameQName, String.class, contributorName);
		contributor.getContent().add(contributorNameElement);
		if(resource.getContributors() == null){
			Resource.Contributors contributors = new Resource.Contributors();
			resource.setContributors(contributors);
		}
		resource.getContributors().getContributor().add(contributor);
		return this;
	}

	public static final String DATE_FORMAT_STR = "yyyy-MM-dd";

	public ResourceBuilder addDate(java.util.Date date, DateType dateType ){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STR);
		Date dateElement = new Date();
		dateElement.setValue(sdf.format(date));
		dateElement.setDateType(dateType);
		if(resource.getDates() == null ){
			resource.setDates(new Dates());
		}
		resource.getDates().getDate().add(dateElement);
		return this;
	}
	
	public ResourceBuilder addDescription(String description, DescriptionType descriptionType){
		
		Resource.Descriptions.Description descriptionElement = new Resource.Descriptions.Description();
		descriptionElement.getContent().add(description);
		descriptionElement.setDescriptionType(descriptionType);
		if(resource.getDescriptions() == null){
			resource.setDescriptions(new Resource.Descriptions());
		}
		resource.getDescriptions().getDescription().add(descriptionElement);
		return this;
	}
	
	
    @Deprecated //Not sure what has changed but it seems we validate against datacit v3 now - these <br/> s fail validation
	public ResourceBuilder addDescription(List<String> descriptionLines, DescriptionType descriptionType){
		if(descriptionLines == null){
			return this;
		}
		
		Resource.Descriptions.Description descriptionElement = new Resource.Descriptions.Description();
		
		QName contributorNameQName = new QName("http://datacite.org/schema/kernel-2.1", "br");
		for(String descriptionLine : descriptionLines){
			JAXBElement<String> brElement = new JAXBElement<String>(contributorNameQName, String.class, descriptionLine);
		    descriptionElement.getContent().add(brElement);
		}
		descriptionElement.setDescriptionType(descriptionType);
		
		if(resource.getDescriptions() == null){
			resource.setDescriptions(new Resource.Descriptions());
		}
		resource.getDescriptions().getDescription().add(descriptionElement);
		//resource.getDescriptions().getDescription().
		return this;
	}
	
//	resource.getFormats()
	public ResourceBuilder addFormat(String format){
		if(resource.getFormats() == null){
			resource.setFormats(new Formats());
		}
		resource.getFormats().getFormat().add(format);
		return this;
	}
	
	
//	resource.getLanguage()
	public ResourceBuilder addLanguage(String language){
		resource.setLanguage(language);
		return this;
	}
	
//	resource.getResourceType()
	public ResourceBuilder addResourceType(String content, ResourceType resourceType){
		Resource.ResourceType resourceTypeElement = new Resource.ResourceType();
		resourceTypeElement.setContent(content);
		resourceTypeElement.setResourceTypeGeneral(resourceType);
		resource.setResourceType(resourceTypeElement);
		return this;
	}
//	resource.getRights()
	/**
	 * Don`t really know how this works, but anyway . . . 
	 * @param rights
	 * @return
	 */
	public ResourceBuilder setRights(Object rights){
		resource.setRights(rights);
		return this;
	}
//	resource.getSizes()
	/**
	 * Don`t know how the sizes object works either.
	 * @param size
	 * @return
	 */
	public ResourceBuilder addSize(Object size){
		if(resource.getSizes() == null){
			resource.setSizes(new Resource.Sizes());
		}
		resource.getSizes().getSize().add(size);
		return this;
	}
	
	public ResourceBuilder addSubject(String subject ){
		return addSubject(subject, null);
	}
	
	
//	resource.getSubjects()
	public ResourceBuilder addSubject(String subject, String subjectScheme){
		if( resource.getSubjects() == null ){
            resource.setSubjects(new Resource.Subjects());	
		}
		Resource.Subjects.Subject sub = new Resource.Subjects.Subject();
		sub.setValue(subject);
		sub.setSubjectScheme(subjectScheme);
		resource.getSubjects().getSubject().add(sub);
		return this;
	}
	
    public ResourceBuilder setVersion(String version){
    	resource.setVersion(version);
    	return this;
    }
	
	public ResourceBuilder setPublisher(String publisher){
		resource.setPublisher(publisher);
		return this;
	}
	
	public ResourceBuilder setPublicationYear(String publicationYear){
		resource.setPublicationYear(publicationYear);
		return this;
	}
	
	
	
}
