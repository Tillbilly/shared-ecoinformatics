package au.edu.aekos.shared.data.dao;

import java.util.List;

/**
 * 
 * @author btill
 */
public interface HibernateDao<T, U> {
	/**
	 * This is required to correctly initialise Criteria's
	 */
	Class<T> getEntityClass();

	T findById(U id);

	List<T> getAll();

	void save(T entity);
	
	void saveOrUpdate( T entity );

	void update(T entity);

	void delete(T entity);
	
	void flush();

}