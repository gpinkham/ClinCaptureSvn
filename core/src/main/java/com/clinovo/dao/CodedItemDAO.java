package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

@Repository
@SuppressWarnings("unchecked")
public class CodedItemDAO extends AbstractDomainDao<CodedItem> {

	@Override
	public Class<CodedItem> domainClass() {
		return CodedItem.class;
	}

	public List<CodedItem> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by verbatimTerm asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<CodedItem>) q.list();
	}

	public List<CodedItem> findByVerbatimTerm(String term) {

		String query = "from " + getDomainClassName() + " do  where do.verbatimTerm = :term";
		Query q = getCurrentSession().createQuery(query);
		q.setString("term", term);

		return (List<CodedItem>) q.list();
	}

	public List<CodedItem> findByCodedTerm(String term) {

		String query = "from " + getDomainClassName() + " do  where do.codedTerm = :term";
		Query q = getCurrentSession().createQuery(query);
		q.setString("term", term);

		return (List<CodedItem>) q.list();
	}

	public List<CodedItem> findByDictionary(String dictionary) {

		String query = "from " + getDomainClassName() + " do  where do.dictionary = :dictionary";
		Query q = getCurrentSession().createQuery(query);
		q.setString("dictionary", dictionary);

		return q.list();
	}

	public List<CodedItem> findByStatus(CodeStatus status) {

		String query = "from " + getDomainClassName() + " do  where do.status = :status";
		Query q = getCurrentSession().createQuery(query);
		q.setString("status", String.valueOf(status));

		return q.list();
	}

	public void deleteCodedItem(CodedItem codedItem) {
		
		this.getCurrentSession().delete(codedItem);
	}

	public CodedItem findByItemId(int codedItemItemId) {
		
		String query = "from " + getDomainClassName() + " do  where do.itemId = :itemId";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("itemId", codedItemItemId);
		
		return (CodedItem) q.uniqueResult();
	}

	public List<CodedItem> findByEventCRF(int eventCRFId) {
		
		String query = "from " + getDomainClassName() + " do  where do.eventCrfId = :eventCrfId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("eventCrfId", eventCRFId);

		return q.list();
	}

	public List<CodedItem> findByCRFVersion(int crfVersionId) {
		
		String query = "from " + getDomainClassName() + " do  where do.crfVersionId = :crfVersionId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("crfVersionId", crfVersionId);

		return q.list();
	}
}
