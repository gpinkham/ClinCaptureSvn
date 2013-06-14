package org.akaza.openclinica.service.calendar;

import java.util.Date;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class EmailTriggerService {

	public EmailTriggerService() {

	}

	public static final String EMAIL = "contactEmail";
	public static final String USER_ID = "user_id";
	public static final String EVENT_NAME = "event_name";
	public static final String SCHEDULED_DAY = "scheduled_day";
	public static final String SUBJECT_NAME = "subject_name";
	public static final String DAYS_BETWEEN = "daysBetween";

	public SimpleTriggerImpl generateEmailSenderTrigger(StudyEventDefinitionBean sedTmp, StudySubjectBean ssb,
			Date sendEmailDay, int daysBetween, String contactEmail, UserAccountBean uaBean) {
		JobDataMap emailJobDataMap = new JobDataMap();
		emailJobDataMap.put(EMAIL, sedTmp.getEmailAdress());
		emailJobDataMap.put(USER_ID, uaBean.getId());
		emailJobDataMap.put(EVENT_NAME, sedTmp.getName());
		emailJobDataMap.put(SUBJECT_NAME, ssb.getLabel());
		emailJobDataMap.put(SCHEDULED_DAY, sedTmp.getScheduleDay());
		emailJobDataMap.put(DAYS_BETWEEN, Integer.toString(daysBetween));
		String triggerGroup = "CALENDAR";
		SimpleTriggerImpl sTrigger = new SimpleTriggerImpl();
		sTrigger.setName("event_" + sedTmp.getName() + " subject_" + ssb.getLabel() + " email_time_" + sendEmailDay);
		sTrigger.setGroup(triggerGroup);
		sTrigger.setRepeatCount(0);
		sTrigger.setRepeatInterval(1);
		sTrigger.setStartTime(sendEmailDay);
		sTrigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		sTrigger.setJobDataMap(emailJobDataMap);
		return sTrigger;
	}

}
