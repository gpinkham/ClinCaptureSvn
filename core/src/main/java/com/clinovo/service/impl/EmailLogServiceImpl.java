package com.clinovo.service.impl;

import com.clinovo.dao.EmailLogDAO;
import com.clinovo.model.EmailLog;
import com.clinovo.service.EmailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AuditLogEmailServiceImpl.
 */
@Transactional
@Service("auditLogEmailService")
public class EmailLogServiceImpl implements EmailLogService {

	@Autowired
	private EmailLogDAO emailLogDAO;

	/**
	 * {@inheritDoc}
	 */
	public EmailLog saveOrUpdate(EmailLog emailLog) {
		return emailLogDAO.saveOrUpdate(emailLog);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EmailLog> findAllByStudyId(int studyId) {
		return emailLogDAO.findAllByStudyId(studyId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EmailLog findById(int id) {
		return emailLogDAO.findById(id);
	}
}
