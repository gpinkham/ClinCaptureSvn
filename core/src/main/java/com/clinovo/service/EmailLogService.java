package com.clinovo.service;

import com.clinovo.model.EmailLog;

import java.util.List;

/**
 * Audit Log Email Service.
 */
public interface EmailLogService {

	/**
	 * Save entity to the database or update existing one.
	 * @param emailLog AuditLogEmail
	 * @return AuditLogEmail
	 */
	EmailLog saveOrUpdate(EmailLog emailLog);


	/**
	 * Find all Audit Log entities for the specific study.
	 * @param studyId int
	 * @return List of AuditLogEmail.
	 */
	List<EmailLog> findAllByStudyId(int studyId);


	/**
	 * Find audit entry by id.
	 * @param id int.
	 * @return AuditLogEntry
	 */
	EmailLog findById(int id);
}
