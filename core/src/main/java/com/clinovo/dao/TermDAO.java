package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;

@Repository
@SuppressWarnings("unchecked")
public class TermDAO extends AbstractDomainDao<Term> {

	@Override
	public Class<Term> domainClass() {
		
		return Term.class;
	}
	
	public Term findByName(String name) {

		String query = "from " + getDomainClassName() + " do  where do.preferredName = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", name);

		return (Term) q.uniqueResult();
	}

	public Term findByCode(String code) {

		String query = "from " + getDomainClassName() + " do  where do.code = :code order by do.code asc";
		Query q = getCurrentSession().createQuery(query);
		q.setString("code", code);

		return (Term) q.uniqueResult();
	}

	public List<Term> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by preferredName asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<Term>) q.list();
	}

	public List<Term> findByDictionary(Dictionary dictionary) {

		Criteria criteria = this.getCurrentSession().createCriteria(getDomainClassName());
		criteria.add(Restrictions.eq("dictionary", dictionary));
		
		return criteria.list();
	}
	
	public void deleteTerm(Term term) {
		
		this.getCurrentSession().delete(term);
	}
}
