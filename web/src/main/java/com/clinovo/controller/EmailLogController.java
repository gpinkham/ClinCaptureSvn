package com.clinovo.controller;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.EmailLog;
import com.clinovo.model.EmailLogTableFactory;
import com.clinovo.service.EmailLogService;
import com.clinovo.util.RequestUtil;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SpringController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
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
		List<EmailLog> logs = emailLogService.findAllByStudyId(studyBean.getId());
		EmailLogTableFactory tableFactory = getEmailLogTableFactory(logs, locale);
		model.addAttribute("logs", logs);
		String table = tableFactory.createTable(request, response).render();
		model.addAttribute("dataTable", table);

		return "admin/emailLog";
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
}
