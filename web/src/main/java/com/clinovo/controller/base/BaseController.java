package com.clinovo.controller.base;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.quartz.impl.StdScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class BaseController {

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected ConfigurationDao configurationDao;

	@Autowired
	private org.akaza.openclinica.core.SecurityManager securityManager;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	private OpenClinicaSchedulerFactoryBean scheduler;

	public static final String INPUT_MESSAGES = "formMessages";

	public static final String CURRENT_STUDY = "study";

	public static final String CURRENT_USER = "userBean";

	public ArrayList getAllStudies() {
		StudyDAO studyDAO = new StudyDAO(dataSource);
		return (ArrayList) studyDAO.findAll();
	}

	public ConfigurationDao getConfigurationDao() {
		return configurationDao;
	}

	public void setInputMessages(HashMap messages, HttpServletRequest request) {
		request.setAttribute(INPUT_MESSAGES, messages);
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	public StdScheduler getStdScheduler() {
		return (StdScheduler) scheduler.getScheduler();
	}

	/**
	 * Get UserDetails from Context.
	 *
	 * @return UserDetails.
	 */
	protected UserDetails getUserDetails() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return (UserDetails) principal;
		} else {
			return null;
		}
	}

	/**
	 * Returns current study bean from client session.
	 *
	 * @param request HttpServletRequest
	 * @return StudyBean
	 */
	public StudyBean getCurrentStudy(HttpServletRequest request) {
		return (StudyBean) request.getSession().getAttribute(CURRENT_STUDY);
	}

	/**
	 * Returns current user account bean from client session.
	 *
	 * @param request HttpServletRequest
	 * @return UserAccountBean
	 */
	public UserAccountBean getCurrentUser(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute(CURRENT_USER);
	}
}
