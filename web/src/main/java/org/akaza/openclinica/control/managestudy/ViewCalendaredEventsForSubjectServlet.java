package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;

import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.service.calendar.CalendarFuncBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.joda.time.DateTime;
import org.joda.time.Interval;


@SuppressWarnings("serial")
public class ViewCalendaredEventsForSubjectServlet extends SecureController {
	
	public void mayProceed() throws InsufficientPermissionException {

	}
	
	private StudyEventDAO sedao;
	private StudyEventDefinitionDAO<?, ?> seddao;
	private StudySubjectDAO<?, ?> ssdao;
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public void processRequest() throws Exception {
		logger.info("servlet is connected");
		FormProcessor fp = new FormProcessor(request);
		sedao = new StudyEventDAO(sm.getDataSource());
		seddao = new StudyEventDefinitionDAO(sm.getDataSource());
		
		ssdao = new StudySubjectDAO(sm.getDataSource());
		ArrayList events = new ArrayList();
		int subjectId = fp.getInt("id", true);
		StudySubjectBean ssBean = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);
		logger.info("subjectId" +subjectId);
		ArrayList <StudyEventBean> seBeans = sedao.findAllBySubjectId(subjectId);
		logger.info("found a list of " + seBeans.size() + " study event beans");
		for (StudyEventBean seBean : seBeans) {
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(seBean.getStudyEventDefinitionId());
			logger.info("looking up type: " + sedBean.getType());
			if("calendared_visit".equalsIgnoreCase(sedBean.getType()) && !seBean.getSubjectEventStatus().isNotScheduled()) {
				logger.info("passed if loop");
				//try to found reference event for this event
				Date schDate = seBean.getDateStarted();
				Date refVisitDateCompleted = new DateTime(schDate.getTime()).minusDays(sedBean.getScheduleDay()).toDate();
				StudyEventBean refEventResult;
				boolean completeEvent = false;
				if (seBean.getSubjectEventStatus().isCompleted()) {
					refEventResult = getSubjectReferenceEventByDateCompleted(refVisitDateCompleted, ssBean);
					logger.info("found completed event");
					completeEvent = true;
				} else {
					refEventResult = getLastReferenceEvent(ssBean);
					logger.info("found non completed event");
				}
				CalendarFuncBean calendFuncBean = new CalendarFuncBean();
				if (!(getYearFromDate(refEventResult.getUpdatedDate()) == 1970) && refEventResult != null) {
					
					Date maxDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(sedBean.getMaxDay()).toDate();
					Date minDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(sedBean.getMinDay()).toDate();
					Date emailDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(sedBean.getEmailDay()).toDate();
					//set bean with values
					calendFuncBean.setFlagColor(getFlagColor(minDate, maxDate, seBean));
					calendFuncBean.setDateMax(maxDate);
					calendFuncBean.setDateMin(minDate);
					calendFuncBean.setDateSchedule(schDate);
					if(completeEvent) {
					calendFuncBean.setDateEmail(emailDate);
					} else {
						calendFuncBean.setDateEmail(emailDate);
					}
					calendFuncBean.setEventName(sedBean.getName());
					calendFuncBean.setReferenceVisit(sedBean.getReferenceVisit());
					calendFuncBean.setEventsReferenceVisit(seddao.findByPK(refEventResult.getStudyEventDefinitionId()).getName());	
					events.add(calendFuncBean);
				} else {
						logger.info("This event is RV or Event without RV");
						calendFuncBean.setDateSchedule(schDate);
						calendFuncBean.setEventName(sedBean.getName());
						calendFuncBean.setReferenceVisit(sedBean.getReferenceVisit());
						events.add(calendFuncBean);
				} 
			} 
		}
		request.setAttribute("subjectLabel", ssBean.getLabel());
		request.setAttribute("currentDate", new Date());
		request.setAttribute("events", events);
		forwardPage(Page.SHOW_CALENDAR_FUNC_PER_SUBJ);
	}
	
	@SuppressWarnings("unchecked")
	private StudyEventBean getLastReferenceEvent(StudySubjectBean ssBean) {
		StudyEventBean studyEventBeanRef = new StudyEventBean();
		List<StudyEventDefinitionBean> studyEventDefinitions = seddao.findReferenceVisitBeans();
		for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
			List<StudyEventBean> sebBeanArr = sedao.findAllByDefinitionAndSubject(studyEventDefinitionBean,ssBean);
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
		return studyEventBeanRef;
	}
	
	//joda getYear is deprecation method.
	private static int getYearFromDate(Date date) {
	    int result = -1;
	    if (date != null) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        result = cal.get(Calendar.YEAR);
	    }
	    return result;
	}
	
	private String getFlagColor(Date dayMin, Date dayMax, StudyEventBean seBean) {
		String flagColor;
		//Date dayMaxPlusInclusiveDay = new DateTime(dayMax.getTime()).plusDays(1).toDate();
		Date dayMaxTime = new DateTime(dayMax.getTime()).toDate();
		Interval timeRangeForStudyEvent = new Interval(dayMin.getTime(),dayMaxTime.getTime());
		if (seBean.getUpdatedDate() != null && seBean.getSubjectEventStatus().isCompleted()) {
			logger.info("===================");
			logger.info("seBean.getUpdatedDate() = "+seBean.getUpdatedDate());
			logger.info("timeRangeForStudyEvent.getStart() = "+timeRangeForStudyEvent.getStart().toDate());
			logger.info("timeRangeForStudyEvent.getEnd() = "+timeRangeForStudyEvent.getEnd().toDate());
			logger.info("===================");
			if ((timeRangeForStudyEvent.getStart().isAfter(seBean.getUpdatedDate().getTime()) || timeRangeForStudyEvent
					.getEnd().isBefore(seBean.getUpdatedDate().getTime()))) {
				logger.info("out of range");
				flagColor = "red";
				return flagColor;
			} else if (!timeRangeForStudyEvent.getStart().isAfter(seBean.getUpdatedDate().getTime())
					&& !timeRangeForStudyEvent.getEnd().isBefore(seBean.getUpdatedDate().getTime())) {
				logger.info("in max min range");
				return flagColor = "green";
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private StudyEventBean getSubjectReferenceEventByDateCompleted(Date dateCompleted, StudySubjectBean ssBean) {
			StudyEventBean refEventResult = new StudyEventBean();
			ArrayList<StudyEventDefinitionBean> refEventDefs = seddao.findReferenceVisitBeans();
			for (StudyEventDefinitionBean refEventDef : refEventDefs) {
				ArrayList<StudyEventBean> refEventBeans = sedao.findAllByStudySubjectAndDefinition(ssBean, refEventDef);
				if (refEventBeans.size() > 0) {
					for (StudyEventBean refEventBean : refEventBeans) {
						if (refEventBean.getSubjectEventStatus().isCompleted() && refEventBean.getUpdatedDate().equals(dateCompleted)) {
							logger.info("nashel referense event for completed event using his dateUpdate");
							refEventResult = refEventBean;
							refEventResult.setStudyEventDefinition(refEventBean.getStudyEventDefinition());
							break;
						}
					}
				}
			}
			return refEventResult;
	}
}

