package au.edu.aekos.shared.data.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.QuestionnaireConfigEntity;

@Repository
public class QuestionnaireConfigEntityDaoImpl extends AbstractHibernateDao<QuestionnaireConfigEntity,Long> implements QuestionnaireConfigEntityDao {

	@Override
	public Class<QuestionnaireConfigEntity> getEntityClass() {
		return QuestionnaireConfigEntity.class;
	}
	
	@Override
	public QuestionnaireConfigEntity getQuestionnaireConfigEntity(
			String fileName, String version) {
		Criteria c = createCriteria();
		c.add(Restrictions.eq("configFileName", fileName))
			.add(Restrictions.eq("version", version))
			.add(Restrictions.eq("active", true));
		return (QuestionnaireConfigEntity) c.uniqueResult();
	}
}
