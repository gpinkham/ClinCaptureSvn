package com.clinovo.service.impl;

import com.clinovo.dao.EmailLogDAO;
import com.clinovo.model.EmailLog;
import com.clinovo.service.EmailLogService;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * AuditLogEmailServiceImpl.
 */
@Transactional
@Service("auditLogEmailService")
public class EmailLogServiceImpl implements EmailLogService {

	@Autowired
	private EmailLogDAO emailLogDAO;

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public EmailLog saveOrUpdate(EmailLog emailLog) {
		return emailLogDAO.saveOrUpdate(emailLog);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EmailLog> findAllParentsByStudyId(int studyId) {
		return emailLogDAO.findAllParentsByStudyId(studyId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EmailLog findById(int id) {
		EmailLog emailLog = emailLogDAO.findById(id);
		UserAccountDAO userAccountDAO = getUserAccountDao();
		emailLog.setSenderAccount((UserAccountBean) userAccountDAO.findByPK(emailLog.getSentBy()));
		return emailLog;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EmailLog> findAllByParentId(int id) {
		return emailLogDAO.findAllByParentId(id);
	}

	/**
	 * Get User Account Dao.
	 * @return UserAccountDAO
	 */
	public UserAccountDAO getUserAccountDao() {
		return new UserAccountDAO(dataSource);
	}
}
