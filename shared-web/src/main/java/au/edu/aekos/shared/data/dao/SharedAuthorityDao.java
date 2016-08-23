package au.edu.aekos.shared.data.dao;

import au.edu.aekos.shared.data.entity.SharedAuthority;

/**
 * For some reason, cascading wasn't removing SharedAuthorities, only the relationship in
 * the association table, this dao is here to clean up.
 * @author btill
 */
public interface SharedAuthorityDao  extends HibernateDao<SharedAuthority, Long>  {
	 
	
}
