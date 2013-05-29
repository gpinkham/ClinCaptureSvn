package org.akaza.openclinica.service.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class CalendarLogic {
	
	StdScheduler scheduler;
	DataSource ds;
	private ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
	private ResourceBundle resword = ResourceBundleProvider.getWordsBundle();
	
	public CalendarLogic(DataSource ds, StdScheduler scheduler) {
		this.ds = ds;
		this.scheduler = scheduler;

	}

	public void ScheduleSubjectEvents(StudyEventBean studyEventBean) {
		
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDAO sed = new StudyEventDAO(ds);
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		int subjectId = studyEventBean.getStudySubjectId();
		StudyDAO sdao = new StudyDAO(ds);
		StudyBean studyBean = sdao.findByStudySubjectId(subjectId);
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, studyBean);
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean.getStudyEventDefinitionId());
		//
		StudyEventBean studyEventBeanRef = new StudyEventBean();
		if (sedb.getReferenceVisit()) {
			List<StudyEventDefinitionBean> studyEventDefinitions = seddao.findReferenceVisitBeans();
			for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
				List<StudyEventBean> seb = sed.findAllByDefinitionAndSubject(studyEventDefinitionBean, ssb);
				for (StudyEventBean studyEventBeanReferenceVisit : seb) {
					if (studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
						if (studyEventBeanRef.getUpdatedDate() == null) {
							studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
						} else {
							if (studyEventBeanRef.getUpdatedDate().before(studyEventBeanReferenceVisit.getUpdatedDate())) {
								studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
							}
						}
					}
				}
			}
			System.out.println("Latest reference visit date for this subject " + studyEventBeanRef.getUpdatedDate());
			//schedule all other events using this date and their sch_day fields
			List<StudyEventDefinitionBean> sedForSch = seddao.findAllByStudy(studyBean);
			for (StudyEventDefinitionBean sedTmp : sedForSch) {
				if (!sedTmp.getReferenceVisit() && "calendared_visit".equalsIgnoreCase(sedTmp.getType())) {
					ArrayList<StudyEventBean> eventsForValidation = sed.findAllByStudySubjectAndDefinition(ssb, sedTmp);
					if (eventsForValidation.size() == 0) {
						StudyEventBean studyEvent = new StudyEventBean();
						studyEvent.setStudyEventDefinitionId(sedTmp.getId());
						studyEvent.setStudySubjectId(subjectId);
						studyEvent.setStartTimeFlag(false);
						int schDay = sedTmp.getScheduleDay();
						DateTime dateTimeCompleted = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
						dateTimeCompleted = dateTimeCompleted.plusDays(schDay);
						studyEvent.setDateStarted(dateTimeCompleted.toDate());
						studyEvent.setOwner(getUserByEmail(sedb.getEmailAdress()));
						studyEvent.setStatus(Status.AVAILABLE);
						studyEvent.setLocation("");
						studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
						studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(sedTmp, ssb) + 1);
						studyEvent = (StudyEventBean) sed.create(studyEvent);
						UserAccountDAO useracdao = new UserAccountDAO(ds);
						UserAccountBean useracBean = (UserAccountBean) useracdao.findByUserEmail(sedTmp.getEmailAdress());
						try {
							System.out.println("Start quartz scheduler for this event");
							DateTime dateTimeEmail = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
							dateTimeEmail = dateTimeEmail.plusDays(sedTmp.getEmailDay());
							int daysBetween = Days.daysBetween(dateTimeEmail.toDateMidnight(), dateTimeCompleted.toDateMidnight()).getDays();
							ScheduleEmailQuartz(sedTmp, ssb, dateTimeEmail.toDate(), daysBetween, sedTmp.getEmailAdress(), useracBean);
						} catch (SchedulerException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public String MaxMinDaysValidator(StudyEventBean studyEventBean) {
		String messageReturn = "empty";
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		StudyEventDAO sedao = new StudyEventDAO(ds);
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDefinitionBean seBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean.getStudyEventDefinitionId());
		if(!seBean.getReferenceVisit() && seBean.getType().equalsIgnoreCase("calendared_visit")) {
		int dayMax = seBean.getMaxDay(); 
		int dayMin = seBean.getMinDay();
		int subjectId = studyEventBean.getStudySubjectId();
		StudyDAO sdao = new StudyDAO(ds);
		StudyBean studyBean = sdao.findByStudySubjectId(subjectId);
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, studyBean);
		StudyEventBean studyEventBeanRef = new StudyEventBean();
		
		List<StudyEventDefinitionBean> studyEventDefinitions = seddao.findReferenceVisitBeans();
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
			List<StudyEventBean> sebBeanArr = sedao.findAllByDefinitionAndSubject(studyEventDefinitionBean, ssb);
			for (StudyEventBean studyEventBeanReferenceVisit : sebBeanArr) {
				if (studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
					if (studyEventBeanRef.getUpdatedDate() == null) {
						studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
					} else {
						if (studyEventBeanRef.getUpdatedDate().before(studyEventBeanReferenceVisit.getDateStarted())) {
							studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
						}
					}
				}
			}
		}
		boolean refEventsIsEmpty = false;
		if(studyEventBeanRef.getUpdatedDate() == null) {
			studyEventBeanRef.setUpdatedDate(new Date());
			System.out.println("RV not founds so get currentDate and skip DN");
			refEventsIsEmpty = true;
		}
		
		StudyEventDefinitionBean infoBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBeanRef.getStudyEventDefinitionId());
		System.out.println("Reference visit name " + infoBean.getName());
		System.out.println("Latest referense visit date complete " + studyEventBeanRef.getUpdatedDate());
		System.out.println("Information about filled event. OID? " +seBean.getOid());
		
			DateTime dateTimeCurrent = new DateTime();
			DateTime ReferenceEventStartDate = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
			System.out.println("Days range for the filled event. from " +ReferenceEventStartDate.plusDays(dayMin).toDateMidnight() +" to " +ReferenceEventStartDate.toDateMidnight().plusDays(dayMax +1));
			Interval timeRangeForStudyEvent =  new Interval (ReferenceEventStartDate.plusDays(dayMin).toDateMidnight(), ReferenceEventStartDate.toDateMidnight().plusDays(dayMax +1));
			if (timeRangeForStudyEvent.getStart().isAfter(dateTimeCurrent) && !refEventsIsEmpty) {
				System.out.println("Early start");
				messageReturn = resexception.getString("data_has_enteret_too_early_in_the_calendar");
				DiscrepancyNoteBean parent = createDiscrepancyNote(true, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(true, ssb, seBean, studyEventBean, parent.getId());
			} else if(timeRangeForStudyEvent.getEnd().isBefore(dateTimeCurrent) && !refEventsIsEmpty) {
				System.out.println("Late start");
				messageReturn = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
				DiscrepancyNoteBean parent = createDiscrepancyNote(false, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(false, ssb, seBean, studyEventBean, parent.getId());
			}
		}
		
		return messageReturn;
	}
	
	private UserAccountBean getUserByEmail(String userEmail) {
		UserAccountDAO uadao = new UserAccountDAO(ds);
		UserAccountBean userBean = (UserAccountBean) uadao.findByUserEmail(userEmail);
		return userBean;
	}
	
	private DiscrepancyNoteBean createDiscrepancyNote (boolean statement, StudySubjectBean studySubjectBean, StudyEventDefinitionBean sedb, StudyEventBean studyEventBean, Integer parentId) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		if(statement == true){
			String message = resexception.getString("data_has_enteret_too_early_in_the_calendar");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_min");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("start_date"));
			note.setColumn("start_date");
		} 
		if(statement == false) {
			String message = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_max");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("end_date"));
			note.setColumn("end_date");
		}
		
		note.setOwner(getUserByEmail(sedb.getEmailAdress()));
		note.setAssignedUserId(getUserByEmail(sedb.getEmailAdress()).getId());
		note.setCreatedDate(new Date());
		note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.QUERY.getId());
		StudyDAO sdao = new StudyDAO(ds);
		StudyBean studyBean = sdao.findByStudySubjectId(studySubjectBean.getId());
		if (parentId != null) {
			note.setParentDnId(parentId);
		}
		note.setStudyId(studyBean.getId());
		note.setEntityType("studyEvent");
		note.setEntityId(studyEventBean.getId());
		
		note.setEventName(sedb.getName());
		note.setEventStart(studyEventBean.getCreatedDate());
		note.setSubjectName(studySubjectBean.getName());

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(ds);
		note = (DiscrepancyNoteBean) dndao.create(note);
		dndao.createMapping(note);
		return note;
	}
	
	private void ScheduleEmailQuartz(StudyEventDefinitionBean sedTmp,
			StudySubjectBean ssb, Date sendEmailDay, int daysBetween, String contactEmail,
			UserAccountBean uaBean) throws SchedulerException {
		EmailTriggerService emailTriggerService = new EmailTriggerService();
		SimpleTrigger trigger = emailTriggerService.generateEmailSenderTrigger(sedTmp, ssb, sendEmailDay, daysBetween, contactEmail, uaBean);
		trigger.setDescription("email day for " + ssb.getLabel() + " in "+ sedTmp.getName());
		JobDetailBean jobDetailBean = new JobDetailBean();
		jobDetailBean.setGroup(trigger.getGroup());
		jobDetailBean.setName(trigger.getName());
		jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
		jobDetailBean.setJobDataMap(trigger.getJobDataMap());
		jobDetailBean.setDurability(true);
		jobDetailBean.setVolatility(false);
		scheduler.scheduleJob(jobDetailBean, trigger);
	}
	
	
}