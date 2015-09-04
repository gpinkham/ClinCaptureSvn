package org.akaza.openclinica.service.calendar;

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

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"unchecked", "deprecation"})
public class CalendarLogic {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarLogic.class);
	private static final int ZERO_YEAR = 1970;

	private StdScheduler scheduler;
	private DataSource ds;
	private ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
	private ResourceBundle resword = ResourceBundleProvider.getWordsBundle();

	public CalendarLogic(DataSource ds, StdScheduler scheduler) {
		this.ds = ds;
		this.scheduler = scheduler;

	}

	public void scheduleSubjectEvents(StudyEventBean studyEventBean) {

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDAO sed = new StudyEventDAO(ds);
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		int subjectId = studyEventBean.getStudySubjectId();
		StudyDAO sdao = new StudyDAO(ds);
		StudyBean studyBean = sdao.findByStudySubjectId(subjectId);
		studyBean = studyBean.getParentStudyId() > 0 ? (StudyBean) sdao.findByPK(studyBean.getParentStudyId())
				: studyBean;
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, studyBean);
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean
				.getStudyEventDefinitionId());
		//
		StudyEventBean studyEventBeanRef = new StudyEventBean();
		if (sedb.getReferenceVisit()) {
			List<StudyEventDefinitionBean> studyEventDefinitions = seddao.findReferenceVisitBeans();
			for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
				List<StudyEventBean> seb = sed.findAllByDefinitionAndSubject(studyEventDefinitionBean, ssb);
				for (StudyEventBean studyEventBeanReferenceVisit : seb) {
					if (studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
						if (getDateEndedIfExist(studyEventBeanRef) == null) {
							studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
						} else {
							if (getDateEndedIfExist(studyEventBeanRef)
									.before(getDateEndedIfExist(studyEventBeanReferenceVisit))) {
								studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
							}
						}
					}
				}
			}
			LOGGER.debug("RefEvent ID " + studyEventBeanRef.getId());
			StudyEventDefinitionBean studyEventDefBeanRef = (StudyEventDefinitionBean) seddao
					.findByPK(studyEventBeanRef.getStudyEventDefinitionId());
			LOGGER.debug("Latest reference visit date for this subject updated" + studyEventBeanRef.getUpdatedDate()
					+ " and called " + studyEventDefBeanRef.getName());
			// schedule all other events using this date and their sch_day fields
			int subjectDynGroupId = ssb.getDynamicGroupClassId();
			StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(ds);
			StudyGroupClassBean sgcb = new StudyGroupClassBean();
			StudyGroupClassBean defaultGroup = sgcdao.findDefaultByStudyId(studyBean.getId());
			if (subjectDynGroupId == 0) {
				sgcb = defaultGroup;
			} else {
				sgcb.setId(subjectDynGroupId);
			}
			List<StudyEventDefinitionBean> sedForSch;
			LOGGER.debug("study bean id " + studyBean.getId());
			LOGGER.debug("subject dyn group id " + subjectDynGroupId);
			if (sgcb.getId() == 0) {
				sedForSch = seddao.findAllByStudy(studyBean);
			} else {
				// find all events from subject dynamic group
				sedForSch = seddao.findAllActiveOrderedByStudyGroupClassId(sgcb.getId());
				// find all events from default group (if events aren't already added)
				if (sgcb.getId() != defaultGroup.getId()) {
					sedForSch.addAll(seddao.findAllActiveOrderedByStudyGroupClassId(defaultGroup.getId()));
				}
				// find all non-grouped events
				sedForSch.addAll(seddao.findAllActiveNotClassGroupedByStudyId(studyBean.getId()));
			}
			LOGGER.debug("found list " + sedForSch.size());
			for (StudyEventDefinitionBean sedTmp : sedForSch) {
				LOGGER.debug("found name " + sedTmp.getName());
				if (!sedTmp.getReferenceVisit() && "calendared_visit".equalsIgnoreCase(sedTmp.getType())) {
					ArrayList<StudyEventBean> eventsForValidation = sed.findAllByStudySubjectAndDefinition(ssb, sedTmp);
					if (eventsForValidation.size() == 0) {
						StudyEventBean studyEvent = new StudyEventBean();
						studyEvent.setStudyEventDefinitionId(sedTmp.getId());
						studyEvent.setStudySubjectId(subjectId);
						studyEvent.setStartTimeFlag(false);
						int schDay = sedTmp.getScheduleDay();
						DateTime dateTimeCompleted = getDateTimeEndedIfExist(studyEventBeanRef).plusDays(schDay);
						studyEvent.setDateStarted(dateTimeCompleted.toDate());
						studyEvent.setOwner(getUserByEmailId(sedTmp.getUserEmailId()));
						studyEvent.setStatus(Status.AVAILABLE);
						studyEvent.setLocation("");
						studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
						studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(sedTmp, ssb) + 1);
						studyEvent.setReferenceVisitId(studyEventBeanRef.getId());
						sed.create(studyEvent);
						UserAccountDAO useracdao = new UserAccountDAO(ds);
						UserAccountBean useracBean = (UserAccountBean) useracdao.findByPK(sedTmp.getUserEmailId());
						try {
							LOGGER.debug("Start quartz scheduler for this event");
							DateTime dateTimeEmail = getDateTimeEndedIfExist(studyEventBeanRef);
							dateTimeEmail = dateTimeEmail.plusDays(sedTmp.getEmailDay());
							int daysBetween = Days.daysBetween(dateTimeEmail.toDateMidnight(),
									dateTimeCompleted.toDateMidnight()).getDays();
							scheduleEmailQuartz(sedTmp, ssb, dateTimeEmail.toDate(), daysBetween,
									useracBean.getEmail(), useracBean, studyBean.getName());
						} catch (SchedulerException e) {
							LOGGER.error(e.getMessage());
						}
					}
				}
			}
		}
	}

	public String maxMinDaysValidator(StudyEventBean studyEventBean) {
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
			// Ref Event can be null
			if (!studyEventBeanRef.getSubjectEventStatus().isSourceDataVerified()
					&& !studyEventBeanRef.getSubjectEventStatus().isCompleted()
					&& !studyEventBeanRef.getSubjectEventStatus().isSigned()) {
				LOGGER.debug("RV for this events is not Completed/SDV/Signed");
				studyEventBeanRef = new StudyEventBean();
			}

			boolean refEventsIsEmpty = false;
			if (getYearFromDate(studyEventBeanRef.getUpdatedDate()) == ZERO_YEAR) {
				studyEventBeanRef.setUpdatedDate(new Date());
				LOGGER.debug("RV not founds so get currentDate and skip DN");
				refEventsIsEmpty = true;
			}

			StudyEventDefinitionBean infoBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBeanRef
					.getStudyEventDefinitionId());
			LOGGER.debug("Reference visit name " + infoBean.getName());
			LOGGER.debug("Latest referense visit date ended " + getDateEndedIfExist(studyEventBeanRef));
			LOGGER.debug("Information about filled event. OID? " + seBean.getOid());

			DateTime dateTimeCurrent = new DateTime();
			DateTime referenceEventStartDate = new DateTime(studyEventBeanRef.getUpdatedDate().getTime());
			LOGGER.debug("Days range for the filled event. from "
					+ referenceEventStartDate.plusDays(dayMin).toDateMidnight() + " to "
					+ referenceEventStartDate.toDateMidnight().plusDays(dayMax + 1));
			Interval timeRangeForStudyEvent = new Interval(referenceEventStartDate.plusDays(dayMin).toDateMidnight(),
					referenceEventStartDate.toDateMidnight().plusDays(dayMax + 1));

			if (timeRangeForStudyEvent.getStart().isAfter(dateTimeCurrent) && !refEventsIsEmpty) {
				LOGGER.debug("Early start");
				messageReturn = resexception.getString("data_has_enteret_too_early_in_the_calendar");
				DiscrepancyNoteBean parent = createDiscrepancyNote(true, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(true, ssb, seBean, studyEventBean, parent.getId());
			} else if (timeRangeForStudyEvent.getEnd().isBefore(dateTimeCurrent) && !refEventsIsEmpty) {
				LOGGER.debug("Late start");
				messageReturn = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
				DiscrepancyNoteBean parent = createDiscrepancyNote(false, ssb, seBean, studyEventBean, null);
				createDiscrepancyNote(false, ssb, seBean, studyEventBean, parent.getId());
			}
		}

		return messageReturn;
	}

	private UserAccountBean getUserByEmailId(int userId) {
		UserAccountDAO uadao = new UserAccountDAO(ds);
		return (UserAccountBean) uadao.findByPK(userId);
	}

	private DiscrepancyNoteBean createDiscrepancyNote(boolean statement, StudySubjectBean studySubjectBean,
			StudyEventDefinitionBean sedb, StudyEventBean studyEventBean, Integer parentId) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		if (statement) {
			String message = resexception.getString("data_has_enteret_too_early_in_the_calendar");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_min");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("start_date"));
			note.setColumn("date_start");
		} else {
			String message = resexception.getString("data_has_enteret_too_late_in_the_calendaror");
			note.setDescription(message);
			message = resexception.getString("a_user_has_entered_data_for_subject_max");
			message = message.replace("{0}", studySubjectBean.getLabel()).replace("{1}", sedb.getName());
			note.setDetailedNotes(message);
			note.setEntityName(resword.getString("end_date"));
			note.setColumn("date_end");
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

	private void scheduleEmailQuartz(StudyEventDefinitionBean sedTmp, StudySubjectBean ssb, Date sendEmailDay,
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

	public static DateTime getDateTimeEndedIfExist(StudyEventBean studyEventBeanRef) {
		return new DateTime(getDateEndedIfExist(studyEventBeanRef).getTime());
	}
	
	public static Date getDateEndedIfExist(StudyEventBean studyEventBeanRef) {
		if (studyEventBeanRef.getDateEnded() != null) {
			return studyEventBeanRef.getDateEnded();
		} else {
			return studyEventBeanRef.getUpdatedDate();
		}
	}
}
