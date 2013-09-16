package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Synonym;

@Repository
@SuppressWarnings("unchecked")
public class SynonymDAO extends AbstractDomainDao<Synonym> {

	@Override
	public Class<Synonym> domainClass() {
		return Synonym.class;
	}

	public Synonym findByName(String synonymName) {

		String query = "from " + getDomainClassName() + " do  where do.name = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", synonymName);

		return (Synonym) q.uniqueResult();
	}

	public Synonym findByCode(String code) {

		String query = "from " + getDomainClassName() + " do  where do.code = :code";
		Query q = getCurrentSession().createQuery(query);
		q.setString("code", code);

		return (Synonym) q.uniqueResult();
	}

	public List<Synonym> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by name asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<Synonym>) q.list();
	}

	public List<Synonym> findByDictionary(Dictionary dictionary) {

		Criteria criteria = this.getCurrentSession().createCriteria(getDomainClassName());
		criteria.add(Restrictions.eq("dictionary", dictionary));
		
		return criteria.list();
	}

	public void deleteSynonym(Synonym synon) {
		
		this.getCurrentSession().delete(synon);
	}
}
