package au.edu.aekos.shared.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.aekos.shared.data.entity.SharedUser;

public class EditGroupUsersModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//The specified group admin user always needs to be in the group
	private Long groupId;
	private String groupAdminUser;
	private Map<String, EditGroupUser> inGroupMap = new HashMap<String, EditGroupUser>();
	private Map<String, EditGroupUser> notInGroupMap = new HashMap<String, EditGroupUser>();
	
	public EditGroupUsersModel() {
		super();
	}
	public EditGroupUsersModel(Long groupId, String groupAdminUser, List<SharedUser> usersInGroup, List<SharedUser> usersToPickFrom) {
		super();
		this.groupId = groupId;
		this.groupAdminUser = groupAdminUser;
		if(usersInGroup != null && usersInGroup.size() > 0){
			for(SharedUser su : usersInGroup ){
				inGroupMap.put(su.getUsername(), new EditGroupUser(su.getUsername(), su.getEmailAddress()));
			}
		}
		if(usersToPickFrom != null && usersToPickFrom.size() > 0){
			for(SharedUser su : usersToPickFrom ){
				notInGroupMap.put(su.getUsername(), new EditGroupUser(su.getUsername(), su.getEmailAddress()));
			}
		}
	}
	
	public String getGroupAdminUser() {
		return groupAdminUser;
	}
	public void setGroupAdminUser(String groupAdminUser) {
		this.groupAdminUser = groupAdminUser;
	}
	public Map<String, EditGroupUser> getInGroupMap() {
		return inGroupMap;
	}
	public void setInGroupMap(Map<String, EditGroupUser> inGroupMap) {
		this.inGroupMap = inGroupMap;
	}
	public Map<String, EditGroupUser> getNotInGroupMap() {
		return notInGroupMap;
	}
	public void setNotInGroupMap(Map<String, EditGroupUser> notInGroupMap) {
		this.notInGroupMap = notInGroupMap;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}
