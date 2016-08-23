package au.edu.aekos.shared.data.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao<T, U> implements HibernateDao<T, U> {
	@Autowired
	private SessionFactory sessionFactory;
	
	public Session getCurrentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	public Criteria createCriteria(){
		return getCurrentSession().createCriteria(getEntityClass());
	}
	
	@SuppressWarnings("unchecked")
	public T findById(U id){
		Criteria criteria = getCurrentSession().createCriteria(getEntityClass());
		criteria.add(Restrictions.eq("id", id));
		return (T) criteria.uniqueResult();
	}
	
	public void flush(){
		getCurrentSession().flush();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(){
	    return createCriteria().list();
	}
	
	public void save( T entity ){
	    getCurrentSession().persist( entity );
	}
	   
	public void update( T entity ){
        getCurrentSession().merge( entity );
    }
	
	public void saveOrUpdate( T entity ){
	    getCurrentSession().saveOrUpdate( entity );
	}
   
	public void delete( T entity ){
        getCurrentSession().delete( entity );
    }
	
	public abstract Class<T> getEntityClass();
	
}
