package com.clinovo.service.impl;

import com.clinovo.dao.AuditLogRandomizationDAO;
import com.clinovo.model.AuditLogRandomization;
import com.clinovo.service.AuditLogRandomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Audit Log Randomization Service Implementation.
 */
@Transactional
@Service("auditLogRandomizationService")
public class AuditLogRandomizationServiceImpl implements AuditLogRandomizationService {

	@Autowired
	private AuditLogRandomizationDAO auditLogRandomizationDAO;

	/**
	 * {@inheritDoc}
	 */
	public AuditLogRandomization saveOrUpdate(AuditLogRandomization auditLogRandomization) {
		return auditLogRandomizationDAO.saveOrUpdate(auditLogRandomization);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<AuditLogRandomization> findAllByStudySubjectId(int studySubjectId) {
		List<AuditLogRandomization> resultList = auditLogRandomizationDAO.findAllByStudySubjectId(studySubjectId);
		return resultList == null ? new ArrayList<AuditLogRandomization>() : resultList;
	}
}
