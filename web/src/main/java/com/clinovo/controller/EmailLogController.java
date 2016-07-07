package com.clinovo.controller;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.EmailLog;
import com.clinovo.model.EmailLogChildTableFactory;
import com.clinovo.model.EmailLogTableFactory;
import com.clinovo.service.EmailLogService;
import com.clinovo.service.EmailService;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.core.EmailEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * EmailAuditLogController.
 */
@EnableAsync
@Controller
public class EmailLogController extends SpringController {

	@Autowired
	private EmailLogService emailLogService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EmailService emailService;

	private Logger logger = LoggerFactory.getLogger(EmailLogController.class);

	/**
	 * Email Audit Log page controller.
	 * @param model Model.
	 * @param request HttpServletRequest.
	 * @param response HttpServletResponse.
	 * @return String.
	 */
	@RequestMapping("/EmailLog")
	public String emailAuditLogPage(Model model, HttpServletRequest request, HttpServletResponse response) {
		Locale locale = LocaleResolver.getLocale(request);
		StudyBean studyBean = RequestUtil.getCurrentStudy();
		List<EmailLog> logs = emailLogService.findAllParentsByStudyId(studyBean.getId());
		EmailLogTableFactory tableFactory = getEmailLogTableFactory(logs, locale);
		model.addAttribute("logs", logs);
		String table = tableFactory.createTable(request, response).render();
		model.addAttribute("dataTable", table);

		return "admin/emailLog";
	}

	/**
	 * View detailed info about email log entry.
	 * @param model Model
	 * @param logId int - id of the entry which will be viewed
	 * @param request HttpServletRequest.
	 * @param response HttpServletResponse.
	 * @return String page name
	 */
	@RequestMapping("/EmailLogDetails")
	public String viewEmailLogDetails(Model model, @RequestParam ("id") int logId, HttpServletRequest request,
									  HttpServletResponse response) {
		EmailLog emailLog = emailLogService.findById(logId);
		model.addAttribute("logEntry", emailLog);

		List<EmailLog> childEntries = emailLogService.findAllByParentId(logId);
		Locale locale = LocaleResolver.getLocale(request);
		EmailLogChildTableFactory tableFactory = getEmailLogChildTableFactory(childEntries, locale);
		String table = tableFactory.createTable(request, response).render();
		model.addAttribute("dataTable", table);

		String sentFromAdminEmail = Boolean.toString(emailLog.getSender().equals(EmailEngine.getAdminEmail()));
		model.addAttribute("sentFromAdminEmail", sentFromAdminEmail);
		return "admin/emailLogDetails";
	}

	/**
	 * Resend email.
	 * @param logId int
	 * @param useOriginalSender String
	 * @param response HttpServletResponse
	 * @throws IOException if unable create writer.
	 */
	@RequestMapping("/ResendEmail")
	public void resendEmail(@RequestParam ("id") int logId,
							@RequestParam ("useOriginalSender") String useOriginalSender,
							HttpServletResponse response) throws IOException {
		EmailLog emailLog = emailLogService.findById(logId);
		UserAccountBean currentUser = RequestUtil.getUserAccountBean();
		String sender = useOriginalSender.equals("true") ? emailLog.getSender() : EmailEngine.getAdminEmail();
		try {
			emailService.resendEmail(emailLog, sender, currentUser.getId());
		} catch (Exception ex) {
			logger.error("Unable to resend email: " + ex.getMessage());
		}
		response.getWriter().write("success");
	}

	/**
	 * Get EmailLogTableFactory.
	 * @param logs List<EmailLog> logs
	 * @param locale Locale locale
	 * @return EmailLogTableFactory
	 */
	public EmailLogTableFactory getEmailLogTableFactory(List<EmailLog> logs, Locale locale) {
		return new EmailLogTableFactory(dataSource, logs, messageSource, locale);
	}

	/**
	 * Get EmailLogChildTableFactory.
	 * @param childEntries List<EmailLog> logs
	 * @param locale Locale locale
	 * @return EmailLogTableFactory
	 */
	public EmailLogChildTableFactory getEmailLogChildTableFactory(List<EmailLog> childEntries, Locale locale) {
		return new EmailLogChildTableFactory(dataSource, childEntries, messageSource, locale);
	}
}
