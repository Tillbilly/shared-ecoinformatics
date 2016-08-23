package au.edu.aekos.shared.data.dao;

import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;

public interface QuestionnaireConfigEntityDao extends HibernateDao<QuestionnaireConfigEntity, Long>{
	
	QuestionnaireConfigEntity getQuestionnaireConfigEntity(String fileName, String version);

}
