package au.edu.aekos.shared.data.dao;

import java.util.List;
import java.util.Set;

import au.edu.aekos.shared.data.entity.ReusableResponseGroup;

public interface ReusableResponseGroupDao extends HibernateDao<ReusableResponseGroup, Long>{
	
	List<ReusableResponseGroup> retrieveResponseGroups(Long questionnaireId, String userId, String groupId);
	
	//Optimised query for display questionnaire initialisation
	List<ReusableResponseGroup> retrieveResponseGroups(Long questionnaireId, String userId, Set<String> groupIds);
	
	ReusableResponseGroup retrieveResponseGroupByName(String groupName, Long questionnaireId, String userId, String groupId);
	
	
	

}
