package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
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
				StudyEventBean refEventResult = (StudyEventBean) sedao.findByPK(seBean.getReferenceVisitId());
				Date schDate = seBean.getDateStarted();
				CalendarFuncBean calendFuncBean = new CalendarFuncBean();
				if (!(refEventResult == null) && seBean.getReferenceVisitId() != 0) {
					if(refEventResult.getSubjectEventStatus().isCompleted() || refEventResult.getSubjectEventStatus().isSourceDataVerified() || refEventResult.getSubjectEventStatus().isSigned()) {
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
					}
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
	
}

