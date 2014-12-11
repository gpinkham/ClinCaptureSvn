package com.clinovo.util;

import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetAuditDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * RuleSetServiceUtil.
 */
public final class RuleSetServiceUtil {

	private RuleSetServiceUtil() {
	}

	/**
	 * Method returns instance of the RuleSetService (instance is singleton per request).
	 *
	 * @return RuleSetService
	 */
	public static RuleSetService getRuleSetService() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		RuleSetService ruleSetService = (RuleSetService) request.getAttribute("ruleSetService");
		if (ruleSetService == null) {
			WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request
					.getSession().getServletContext());
			String requestUrl = request.getScheme() + "://" + request.getSession().getAttribute("domain_name")
					+ request.getRequestURI().replaceAll(request.getServletPath(), "");
			ruleSetService = createRuleSetService(applicationContext);
			ruleSetService.setRequestURLMinusServletPath(requestUrl);
			ruleSetService.setContextPath(request.getContextPath().replaceAll("/", ""));
			request.setAttribute("ruleSetService", ruleSetService);
		}
		return ruleSetService;
	}

	/**
	 * Method that creates RuleSetService instance.
	 *
	 * @param applicationContext
	 *            ApplicationContext
	 * @return RuleSetService
	 */
	public static RuleSetService createRuleSetService(ApplicationContext applicationContext) {
		DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
		DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao = (DynamicsItemFormMetadataDao) applicationContext
				.getBean("dynamicsItemFormMetadataDao");
		DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao = (DynamicsItemGroupMetadataDao) applicationContext
				.getBean("dynamicsItemGroupMetadataDao");
		JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
		RuleDao ruleDao = (RuleDao) applicationContext.getBean("ruleDao");
		RuleSetDao ruleSetDao = (RuleSetDao) applicationContext.getBean("ruleSetDao");
		RuleSetRuleDao ruleSetRuleDao = (RuleSetRuleDao) applicationContext.getBean("ruleSetRuleDao");
		RuleSetAuditDao ruleSetAuditDao = (RuleSetAuditDao) applicationContext.getBean("ruleSetAuditDao");
		RuleActionRunLogDao ruleActionRunLogDao = (RuleActionRunLogDao) applicationContext
				.getBean("ruleActionRunLogDao");
		return new RuleSetService(dataSource, dynamicsItemFormMetadataDao, dynamicsItemGroupMetadataDao, mailSender,
				ruleDao, ruleSetDao, ruleSetRuleDao, ruleSetAuditDao, ruleActionRunLogDao);
	}
}
