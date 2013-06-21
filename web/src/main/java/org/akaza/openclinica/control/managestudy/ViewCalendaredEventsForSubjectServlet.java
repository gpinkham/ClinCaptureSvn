package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
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
		ArrayList <StudyEventBean> seBeans = sedao.findAllBySubjectId(subjectId);
		for (StudyEventBean seBean : seBeans) {
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(seBean.getStudyEventDefinitionId());
			logger.info("looking up type: " + sedBean.getType());
			if("calendared_visit".equalsIgnoreCase(sedBean.getType()) && !seBean.getSubjectEventStatus().isNotScheduled()) {
				//try to found reference event for this event
				StudyEventBean refEventResult = null;
				if (seBean.getSubjectEventStatus().isCompleted()) {
					StudyEventDefinitionBean sedBeanTmp = (StudyEventDefinitionBean) seddao.findByName(seBean.getReferenceVisitName());
					ArrayList <StudyEventBean> seBeanTmp = sedao.findAllByStudySubjectAndDefinition(ssBean, sedBeanTmp);
					if (seBeanTmp.size() > 0) {
						refEventResult = seBeanTmp.get(0);
					}
					logger.info("found for completed event");
				} else {
					refEventResult = getLastReferenceEvent(ssBean);
					logger.info("found for non completed event");
				}
				CalendarFuncBean calendFuncBean = new CalendarFuncBean();
				Date schDate = seBean.getDateStarted();
				if (refEventResult != null) {
					
					Date maxDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(sedBean.getMaxDay()).toDate();
					Date minDate = new DateTime(refEventResult.getUpdatedDate().getTime()).plusDays(sedBean.getMinDay()).toDate();
					int daysBetween = sedBean.getScheduleDay() - sedBean.getEmailDay();
					Date emailDay = new DateTime(seBean.getDateStarted()).minusDays(daysBetween).toDate();
					//set bean with values
					calendFuncBean.setDateMax(maxDate);
					calendFuncBean.setDateMin(minDate);
					calendFuncBean.setDateSchedule(schDate);
					calendFuncBean.setDateEmail(emailDay);
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
				if (studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED) 
						|| studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.SOURCE_DATA_VERIFIED) 
						|| studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)) {
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
}

