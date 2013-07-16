package org.akaza.openclinica.service.calendar;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class CalendarLogic {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
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
		studyBean = studyBean.getParentStudyId() > 0 ? (StudyBean)sdao.findByPK(studyBean.getParentStudyId()) : studyBean;
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
			System.out.println("RefEvent ID = " +studyEventBeanRef.getId());
			StudyEventDefinitionBean studyEventDefBeanRef = (StudyEventDefinitionBean) seddao.findByPK(studyEventBeanRef.getStudyEventDefinitionId());
			logger.info("Latest reference visit date for this subject updated" + studyEventBeanRef.getUpdatedDate() + " and called "+studyEventDefBeanRef.getName());
			//schedule all other events using this date and their sch_day fields
			int subjectDynGroupId = ssb.getDynamicGroupClassId();
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(ds);
			StudyGroupClassBean sgcb = new StudyGroupClassBean();
			if(subjectDynGroupId == 0) {
				sgcb = (StudyGroupClassBean) sgcdao.findDefaultByStudyId(studyBean.getId());
			} else {
				sgcb.setId(subjectDynGroupId);
			}
			List<StudyEventDefinitionBean> sedForSch;
			logger.debug("study bean id " + studyBean.getId());
			logger.debug("subject dyn group id " + subjectDynGroupId);
			if(sgcb.getId() == 0) {
				sedForSch = seddao.findAllByStudy(studyBean);
			} else {
				sedForSch = seddao.findAllActiveOrderedByStudyGroupClassId(sgcb.getId());
			}
			//find all non-grouped events
			List <StudyEventDefinitionBean> nonGroupedEvents = seddao.findAllActiveNotClassGroupedByStudyId(studyBean.getId());
			sedForSch.addAll(nonGroupedEvents);	
			logger.debug("found list " + sedForSch.size());
			for (StudyEventDefinitionBean sedTmp : sedForSch) {
				logger.debug("found name " + sedTmp.getName());
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
						studyEvent.setOwner(getUserByEmailId(sedTmp.getUserEmailId()));
						studyEvent.setStatus(Status.AVAILABLE);
						studyEvent.setLocation("");
						studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
						studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(sedTmp, ssb) + 1);
						studyEvent.setReferenceVisitId(studyEventBeanRef.getId());
						studyEvent = (StudyEventBean) sed.create(studyEvent);
						UserAccountDAO useracdao = new UserAccountDAO(ds);
						UserAccountBean useracBean = (UserAccountBean) useracdao.findByPK(sedTmp.getUserEmailId());
						try {
							logger.info("Start quartz scheduler for this event");
							DateTime dateTimeEmail = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
							dateTimeEmail = dateTimeEmail.plusDays(sedTmp.getEmailDay());
							int daysBetween = Days.daysBetween(dateTimeEmail.toDateMidnight(),
									dateTimeCompleted.toDateMidnight()).getDays();
							ScheduleEmailQuartz(sedTmp, ssb, dateTimeEmail.toDate(), daysBetween,
									useracBean.getEmail(), useracBean, studyBean.getName());
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
		StudyEventDefinitionBean seBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean
				.getStudyEventDefinitionId());
		if (!seBean.getReferenceVisit() && seBean.getType().equalsIgnoreCase("calendared_visit")
				&& studyEventBean.getSubjectEventStatus().isCompleted()) {
			int dayMax = seBean.getMaxDay();
			int dayMin = seBean.getMinDay();
			int subjectId = studyEventBean.getStudySubjectId();
			StudyDAO sdao = new StudyDAO(ds);
			StudyBean studyBean = sdao.findByStudySubjectId(subjectId);
			StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, studyBean);
			StudyEventBean studyEventBeanRef = (StudyEventBean) sedao.findByPK(studyEventBean.getReferenceVisitId());
			//Ref Event can be null
			if(!studyEventBeanRef.getSubjectEventStatus().isSourceDataVerified() && !studyEventBeanRef.getSubjectEventStatus().isCompleted() && !studyEventBeanRef.getSubjectEventStatus().isSigned()) {
				logger.info("RV for this events is not Completed/SDV/Signed");
				studyEventBeanRef = new StudyEventBean();
			}

			boolean refEventsIsEmpty = false;
			if (getYearFromDate(studyEventBeanRef.getUpdatedDate()) == 1970) {
				studyEventBeanRef.setUpdatedDate(new Date());
				logger.info("RV not founds so get currentDate and skip DN");
				refEventsIsEmpty = true;
			}

			StudyEventDefinitionBean infoBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBeanRef.getStudyEventDefinitionId());
			logger.info("Reference visit name " + infoBean.getName());
			logger.info("Latest referense visit date complete " + studyEventBeanRef.getUpdatedDate());
			logger.info("Information about filled event. OID? " + seBean.getOid());

			DateTime dateTimeCurrent = new DateTime();
			DateTime ReferenceEventStartDate = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
			logger.info("Days range for the filled event. from "
					+ ReferenceEventStartDate.plusDays(dayMin).toDateMidnight() + " to "
					+ ReferenceEventStartDate.toDateMidnight().plusDays(dayMax + 1));
			Interval timeRangeForStudyEvent = new Interval(ReferenceEventStartDate.plusDays(dayMin).toDateMidnight(),
					ReferenceEventStartDate.toDateMidnight().plusDays(dayMax + 1));
			
			if (timeRangeForStudyEvent.getStart().isAfter(dateTimeCurrent) && !refEventsIsEmpty) {
				logger.info("Early start");
				messageReturn = resexception.getString("data_has_enteret_too_early_in_the_calendar");
				DiscrepancyNoteBean parent = createDiscrepancyNote(true, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(true, ssb, seBean, studyEventBean, parent.getId());
			} else if (timeRangeForStudyEvent.getEnd().isBefore(dateTimeCurrent) && !refEventsIsEmpty) {
				logger.info("Late start");
				messageReturn = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
				DiscrepancyNoteBean parent = createDiscrepancyNote(false, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(false, ssb, seBean, studyEventBean, parent.getId());
			}
		}

		return messageReturn;
	}

	private UserAccountBean getUserByEmailId(int userId) {
		UserAccountDAO uadao = new UserAccountDAO(ds);
		UserAccountBean userBean = (UserAccountBean) uadao.findByPK(userId);
		return userBean;
	}

	private DiscrepancyNoteBean createDiscrepancyNote(boolean statement, StudySubjectBean studySubjectBean,
			StudyEventDefinitionBean sedb, StudyEventBean studyEventBean, Integer parentId) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		if (statement == true) {
			String message = resexception.getString("data_has_enteret_too_early_in_the_calendar");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_min");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("start_date"));
			note.setColumn("start_date");
		}
		if (statement == false) {
			String message = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_max");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("end_date"));
			note.setColumn("end_date");
		}

		note.setOwner(getUserByEmailId(sedb.getUserEmailId()));
		note.setAssignedUserId(getUserByEmailId(sedb.getUserEmailId()).getId());
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

	private void ScheduleEmailQuartz(StudyEventDefinitionBean sedTmp, StudySubjectBean ssb, Date sendEmailDay,
			int daysBetween, String contactEmail, UserAccountBean uaBean, String studyName) throws SchedulerException {
		EmailTriggerService emailTriggerService = new EmailTriggerService();
		SimpleTriggerImpl trigger = emailTriggerService.generateEmailSenderTrigger(sedTmp, ssb, sendEmailDay,
				daysBetween, contactEmail, uaBean, studyName);
		trigger.setDescription("email day for " + ssb.getLabel() + " in " + sedTmp.getName());
		JobDetailImpl jobDetailBean = new JobDetailImpl();
		jobDetailBean.setGroup(trigger.getGroup());
		jobDetailBean.setName(trigger.getName());
		jobDetailBean.setJobClass(org.akaza.openclinica.service.calendar.EmailStatefulJob.class);
		jobDetailBean.setJobDataMap(trigger.getJobDataMap());
		jobDetailBean.setDurability(true);
		scheduler.scheduleJob(jobDetailBean, trigger);
	}

	// joda getYear is deprecation method.
	private static int getYearFromDate(Date date) {
		int result = -1;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			result = cal.get(Calendar.YEAR);
		}
		return result;
	}

}
