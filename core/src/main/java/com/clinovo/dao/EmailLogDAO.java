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
	 * Find all Audit Log entities for the specific study.
	 * @param studyId int
	 * @return List of AuditLogEmail.
	 */
	public List<EmailLog> findAllByStudyId(int studyId) {
		String query = "from  " + this.getDomainClassName() + " eal where eal.studyId = :studyId";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);

		return (List<EmailLog>) q.setCacheable(true).list();
	}
}