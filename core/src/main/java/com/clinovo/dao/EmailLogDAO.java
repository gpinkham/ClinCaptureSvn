package com.clinovo.dao;

import com.clinovo.model.EmailLog;
import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuditLogEmailDAO.
 */
@Repository
@SuppressWarnings("unchecked")
public class EmailLogDAO extends AbstractDomainDao<EmailLog> {

	@Override
	public Class<EmailLog> domainClass() {
		return EmailLog.class;
	}

	/**
	 * Find all parent Audit Log entities for the specific study.
	 * @param studyId int
	 * @return List of AuditLogEmail.
	 */
	public List<EmailLog> findAllParentsByStudyId(int studyId) {
		String query = "from  " + this.getDomainClassName() + " eal where eal.studyId = :studyId "
				+ "and eal.parentId = 0";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);
		return (List<EmailLog>) q.setCacheable(true).list();
	}

	/**
	 * Find all child Audit Log entities.
	 * @param parentId int
	 * @return List of AuditLogEmail.
	 */
	public List<EmailLog> findAllByParentId(int parentId) {
		String query = "from  " + this.getDomainClassName() + " eal where eal.parentId = :parentId "
				+ " order by eal.dateSent";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("parentId", parentId);

		return (List<EmailLog>) q.setCacheable(true).list();
	}
}