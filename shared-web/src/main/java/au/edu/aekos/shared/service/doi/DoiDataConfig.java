package au.edu.aekos.shared.service.doi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class DoiDataConfig {
	
	private String publisher;
	//private String publicationYearTag;
    //private String contactPersonContributorTag;
	//private String alternateIdentifierTag;
	//private String alternateIdentifierTypeTag;
    //private String descriptionTag;
	private List<String> subjectTagList = new ArrayList<String>();
	//private String subjectFORTag;
    private String anzsrcforTraitName = "anzsrcfor"; //default
	
	public List<String> getRequiredMetatags(){
		List<String> metatags = new ArrayList<String>();
		Class<? extends DoiDataConfig> clazz = this.getClass();
		for(Method method : clazz.getMethods()){
			String methodName = method.getName();
			if(methodName.startsWith("get") && methodName.endsWith("Tag")){
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
		//Add the subject tags
		if(subjectTagList != null){
			for(String subject:subjectTagList){
				metatags.add(subject);
			}
		}
		return metatags;
	}

	public String getAnzsrcforTraitName() {
		return anzsrcforTraitName;
	}

	public void setAnzsrcforTraitName(String anzsrcforTraitName) {
		this.anzsrcforTraitName = anzsrcforTraitName;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public List<String> getSubjectTagList() {
		return subjectTagList;
	}

	public void setSubjectTagList(List<String> subjectTagList) {
		this.subjectTagList = subjectTagList;
	}
	
	
	
}
