package org.akaza.openclinica.service.calendar;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import com.clinovo.util.EmailUtil;
import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.OpenClinicaMailSender;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * EmailJob.
 */
public class EmailJob extends QuartzJobBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private ResourceBundle reswords;
	private DataSource dataSource;
	private OpenClinicaMailSender mailSender;

	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String EVENT_NAME = "event_name";
	public static final String SUBJECT_NAME = "subject_name";
	public static final String DAYS_BETWEEN = "daysBetween";
	public static final String STUDY_NAME = "study_name";

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Locale locale = new Locale(CoreResources.getSystemLanguage());
		ResourceBundleProvider.updateLocale(locale);
		reswords = ResourceBundleProvider.getWordsBundle();
		JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		SimpleTrigger trigger = (SimpleTrigger) context.getTrigger();

		TriggerBean triggerBean = new TriggerBean();
		triggerBean.setFullName(trigger.getKey().getName());
		String contactEmail = dataMap.getString(EMAIL);
		String eventName = dataMap.getString(EVENT_NAME);
		String subjectlabel = dataMap.getString(SUBJECT_NAME);
		String daysBetween = dataMap.getString(DAYS_BETWEEN);
		String studyName = dataMap.getString(STUDY_NAME);
		daysBetween = "in " + daysBetween + " days";
		System.out.println("EmailJob " + contactEmail);
		logger.error(contactEmail + " " + eventName + " " + subjectlabel);
		try {
			ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext()
					.get("applicationContext");
			dataSource = (DataSource) appContext.getBean("dataSource");
			mailSender = (OpenClinicaMailSender) appContext.getBean("openClinicaMailSender");
			int userId = dataMap.getInt(USER_ID);
			UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
			UserAccountBean ub = (UserAccountBean) userAccountDAO.findByPK(userId);
			triggerBean.setUserAccount(ub);
			try {
				if (contactEmail != null && !"".equals(contactEmail)) {
					mailSender.sendEmail(contactEmail, emailHeader(eventName, subjectlabel),
							emailTextMessage(ub, eventName, subjectlabel, daysBetween, studyName), true);

				}
			} catch (OpenClinicaSystemException e) {
				logger.error("=== throw an ocse === " + e.getMessage());
				e.printStackTrace();
			}

		} catch (Exception e) {
			logger.error("=== throw an ocse === " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String emailTextMessage(UserAccountBean ub, String eventName, String subjectLabel, String daysBetween, String studyName) {
		String emailTestMessage = "";
		emailTestMessage = EmailUtil.getEmailBodyStart();
		emailTestMessage += reswords.getString("day_email_message_htm");
		emailTestMessage = emailTestMessage.replace("{0}", ub.getFirstName() + " " + ub.getLastName())
				.replace("{1}", eventName).replace("{2}", subjectLabel).replace("{3}", studyName).replace("{4}", daysBetween).replace("{5}", studyName);
		emailTestMessage += EmailUtil.getEmailBodyEnd();
		emailTestMessage += EmailUtil.getEmailFooter(new Locale(CoreResources.getSystemLanguage()));
		return emailTestMessage;
	}

	private String emailHeader(String eventName, String subjectLabel) {
		String emailHeader = reswords.getString("reminder_for_event_and_subject");
		emailHeader = emailHeader.replace("{0}", eventName).replace("{1}", subjectLabel);
		return emailHeader;
	}
}
