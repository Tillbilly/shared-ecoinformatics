package au.edu.aekos.shared.data.dao;

import org.springframework.stereotype.Repository;

import au.edu.aekos.shared.data.entity.SharedAuthority;

@Repository
public class SharedAuthorityDaoImpl extends AbstractHibernateDao<SharedAuthority, Long> implements SharedAuthorityDao {

	@Override
	public Class<SharedAuthority> getEntityClass() {
		return SharedAuthority.class;
	}

}
