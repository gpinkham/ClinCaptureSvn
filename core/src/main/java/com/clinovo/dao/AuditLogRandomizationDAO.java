package com.clinovo.dao;

import com.clinovo.model.AuditLogRandomization;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Audit Log Randomization DAO.
 */
@Repository
@SuppressWarnings("unchecked")
public class AuditLogRandomizationDAO extends AbstractDomainDao<AuditLogRandomization> {

	@Override
	public Class<AuditLogRandomization> domainClass() {
		return AuditLogRandomization.class;
	}

	/**
	 * Find all randomization audit.
	 * @param studySubjectId int
	 * @return List<AuditLogRandomization>
	 */
	public List<AuditLogRandomization> findAllByStudySubjectId(int studySubjectId) {
		String query = "from  " + this.getDomainClassName() + " where studySubjectId = :studySubjectId";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("studySubjectId", studySubjectId);
		return (List<AuditLogRandomization>) q.list();
	}
}
