package au.edu.aekos.shared.service.rifcs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Holds the configuration for mapping from shared metatags to RIF-CS concepts
 * Wired up in a spring context - rifcs-context.xml
 * 
 * Refactored to configure the common citation metatags in the CitationConfig bean instead of here.
 * 
 * 
 * @author btill
 */
public class SharedRifcsMappingConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SharedRifcsMappingConfig.class);
	private String registryObjectKeyPrefix = "aekos.org.au/collection/shared/"; //default
	private String originatingSource = "shared.aekos.org.au" ; //default
	private String group = "Advanced Ecological Knowledge and Observation System"; //default
	private String collectionNameTag = "";
	private String versionTag = "";
    private String electronicLocationUrlPrefix;
	
    private List<RelatedObjectConfig> relatedObjectConfigList = new ArrayList<RelatedObjectConfig>();
	private List<String> subjectLocalTagList = new ArrayList<String>();
	
	//publication year direct from the submission application data
	public String getRegistryObjectKeyPrefix() {
		return registryObjectKeyPrefix;
	}
	public void setRegistryObjectKeyPrefix(String registryObjectKeyPrefix) {
		this.registryObjectKeyPrefix = registryObjectKeyPrefix;
	}
	public String getOriginatingSource() {
		return originatingSource;
	}
	public void setOriginatingSource(String originatingSource) {
		this.originatingSource = originatingSource;
	}
	public String getCollectionNameTag() {
		return collectionNameTag;
	}
	public void setCollectionNameTag(String collectionNameTag) {
		this.collectionNameTag = collectionNameTag;
	}
	public String getVersionTag() {
		return versionTag;
	}
	public void setVersionTag(String versionTag) {
		this.versionTag = versionTag;
	}
	//Used for questionnaire config validation
	public List<String> getRequiredMetatags(){
		List<String> metatags = new ArrayList<String>();
		Class<? extends SharedRifcsMappingConfig> clazz = this.getClass();
		for(Method method : clazz.getMethods()){
			String methodName = method.getName();
			if(methodName.startsWith("get") && methodName.endsWith("Tag")){
				try {
					String value = (String) method.invoke(this);
					if(StringUtils.hasLength(value)){
						metatags.add(value);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.error("Programmer error: failed to reflectively call method.", e);
				}
			}
		}
		//Add the subject tags
		if(subjectLocalTagList != null){
			for(String subject: subjectLocalTagList){
				metatags.add(subject);
			}
		}
		return metatags;
	}
	public List<String> getSubjectLocalTagList() {
		return subjectLocalTagList;
	}
	public void setSubjectLocalTagList(List<String> subjectLocalTagList) {
		this.subjectLocalTagList = subjectLocalTagList;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public List<RelatedObjectConfig> getRelatedObjectConfigList() {
		return relatedObjectConfigList;
	}
	public void setRelatedObjectConfigList(
			List<RelatedObjectConfig> relatedObjectConfigList) {
		this.relatedObjectConfigList = relatedObjectConfigList;
	}
	public String getElectronicLocationUrlPrefix() {
		return electronicLocationUrlPrefix;
	}
	public void setElectronicLocationUrlPrefix(String electronicLocationUrlPrefix) {
		this.electronicLocationUrlPrefix = electronicLocationUrlPrefix;
	}
}
