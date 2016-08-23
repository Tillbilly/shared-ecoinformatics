package au.org.aekos.shared.api.model.dataset;

/**
 * dual use bean working from the information model to provide data for jasper reports submission summary report,
 * and to hold data for the submissionDetailsService accessed from the aekos portal
 * @author btill
 */
public class SubmissionSummaryRow {
	
	private String section;
	private String subsection;
	private String title;
	private String value;
	private String groupName;
	private Integer groupIndex;
	private String type;
	private String metatag;
	private String link;
	
	public SubmissionSummaryRow() {
		super();
	}
	
	public SubmissionSummaryRow(String section, String subsection, String title, String value){
		super();
		this.section = section;
		this.subsection = subsection;
		this.title = title;
		this.value = value;
	}
	
	public SubmissionSummaryRow(String section, String subsection, String title, String value,
			String type, String metatag) {
		super();
		this.section = section;
		this.subsection = subsection;
		this.title = title;
		this.value = value;
		this.type = type;
		this.metatag = metatag;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getSubsection() {
		return subsection;
	}
	public void setSubsection(String subsection) {
		this.subsection = subsection;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Integer getGroupIndex() {
		return groupIndex;
	}
	public void setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMetatag() {
		return metatag;
	}
	public void setMetatag(String metatag) {
		this.metatag = metatag;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	public static SubmissionSummaryRow createGroupInstance( String section, String subsection, String group, int groupIndex , String title , String value, String type, String metatag ){
		SubmissionSummaryRow row = new SubmissionSummaryRow(section, subsection, title, value, type, metatag);
		row.setGroupName(group);
		row.setGroupIndex(groupIndex);
		return row;
	}
	
	public boolean hasGroup() {
		return groupName != null && groupName.trim().length() > 0;
	}


}
