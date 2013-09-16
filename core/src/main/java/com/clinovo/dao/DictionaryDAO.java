package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.Dictionary;

@Repository
public class DictionaryDAO extends AbstractDomainDao<Dictionary> {

	@Override
	public Class<Dictionary> domainClass() {
		return Dictionary.class;
	}
	
	@SuppressWarnings("unchecked")
	public List<Dictionary> findAll() {
		
		String query = "from  " + this.getDomainClassName() + " order by name asc";
		Query q = this.getCurrentSession().createQuery(query);
		
		return (List<Dictionary>) q.list();
	}

	public Dictionary findByName(String dictionaryName) {
		
		String query = "from " + getDomainClassName() + " do  where do.name = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", dictionaryName);
		
		return (Dictionary) q.uniqueResult();
	}

	public void deleteDictionary(Dictionary dictionary) {
		this.getCurrentSession().delete(dictionary);
	}
}
