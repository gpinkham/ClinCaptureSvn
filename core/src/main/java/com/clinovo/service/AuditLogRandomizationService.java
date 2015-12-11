package com.clinovo.service;

import com.clinovo.model.AuditLogRandomization;

import java.util.List;

/**
 * Audit Log Randomization Service.
 */
public interface AuditLogRandomizationService {

	/**
	 * Save entity to the database or update existing one.
	 * @param auditLogRandomization AuditLogRandomization
	 * @return AuditLogRandomization
	 */
	AuditLogRandomization saveOrUpdate(AuditLogRandomization auditLogRandomization);

	/**
	 * Find all by StudySubjectBean id.
	 * @param studySubjectId int
	 * @return List<AuditLogRandomization>
	 */
	List<AuditLogRandomization> findAllByStudySubjectId(int studySubjectId);
}
