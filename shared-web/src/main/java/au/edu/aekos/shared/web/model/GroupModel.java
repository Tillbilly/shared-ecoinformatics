package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.List;

import au.edu.aekos.shared.questionnaire.jaxb.ConditionalDisplay;

public class GroupModel implements SubmissionItem{
    private String groupId;
	private String title;
	private String description;
	private List<SubmissionItem> items = new ArrayList<SubmissionItem>();
	private ConditionalDisplay conditionalDisplay;
	private boolean visible = true;
	
	public boolean isVisible() {
		return visible;
	}

	public ConditionalDisplay getConditionalDisplay() {
		return conditionalDisplay;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setConditionalDisplay(ConditionalDisplay conditionalDisplay) {
		this.conditionalDisplay = conditionalDisplay;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public List<SubmissionItem> getItems() {
		return items;
	}

	public void setItems(List<SubmissionItem> items) {
		this.items = items;
	}
	public ItemType getItemType() {
		return ItemType.GROUP;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public List<QuestionModel> getAllQuestionModels(){
		List<QuestionModel> questionModelList = new ArrayList<QuestionModel>();
		for(SubmissionItem si : items){
			if(si.getItemType().equals(ItemType.QUESTION) || si.getItemType().equals(ItemType.QUESTION_SET) ){
				questionModelList.add((QuestionModel) si );
			}else if( si.getItemType().equals(ItemType.GROUP) ){
                GroupModel gm = (GroupModel) si;			
                questionModelList.addAll(gm.getAllQuestionModels());
			}
		}
        return questionModelList;		
	}
}
