/*
 * ******************************************************************************
 *  * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 *  * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the Lesser GNU General Public License along with this program.
 *  \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 *  *****************************************************************************
 *
 *
 *  * OpenClinica is distributed under the
 *  * GNU Lesser General Public License (GNU LGPL).
 *
 *  * For details see: http://www.openclinica.org/license
 *  * copyright 2003-2005 Akaza Research
 *
 */

package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.service.calendar.CalendarFuncBean;
import org.akaza.openclinica.service.calendar.CalendarLogic;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.CalendarEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class ViewCalendaredEventsForSubjectServlet extends Controller {

	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);
		logger.debug("servlet is connected");
		FormProcessor fp = new FormProcessor(request);
		StudyEventDAO sedao = new StudyEventDAO(getDataSource());
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(getDataSource());
		StudySubjectDAO ssdao = new StudySubjectDAO(getDataSource());
		ArrayList events = new ArrayList();
		int subjectId = fp.getInt("id", true);
		StudySubjectBean ssBean = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, currentStudy);
		ArrayList<StudyEventBean> seBeans = sedao.findAllByStudySubject(ssBean);
		for (StudyEventBean seBean : seBeans) {
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(seBean
					.getStudyEventDefinitionId());
			logger.debug("looking up type: " + sedBean.getType());
			if ("calendared_visit".equalsIgnoreCase(sedBean.getType())
					&& !seBean.getSubjectEventStatus().isNotScheduled()) {
				// try to found reference event for this event
				StudyEventBean refEventResult = (StudyEventBean) sedao.findByPK(seBean.getReferenceVisitId());
				Date dateStart = seBean.getDateStarted();
				CalendarFuncBean calendFuncBean = new CalendarFuncBean();
				if (refEventResult != null && seBean.getReferenceVisitId() != 0) {
					if (refEventResult.getSubjectEventStatus().isCompleted()
							|| refEventResult.getSubjectEventStatus().isSourceDataVerified()
							|| refEventResult.getSubjectEventStatus().isSigned()) {
						
						Date maxDate = CalendarLogic.getDateTimeEndedIfExist(refEventResult).plusDays(
								sedBean.getMaxDay()).toDate();
						Date minDate = CalendarLogic.getDateTimeEndedIfExist(refEventResult).plusDays(
								sedBean.getMinDay()).toDate();
						int daysBetween = sedBean.getScheduleDay() - sedBean.getEmailDay();
						Date emailDay = new DateTime(seBean.getDateStarted()).minusDays(daysBetween).toDate();
						// set bean with values
						calendFuncBean.setDateMax(maxDate);
						calendFuncBean.setDateMin(minDate);
						calendFuncBean.setDateSchedule(dateStart);
						calendFuncBean.setDateEmail(emailDay);
						calendFuncBean.setEventName(sedBean.getName());
						if ("true".equalsIgnoreCase(String.valueOf(sedBean.getReferenceVisit()))) {
							calendFuncBean.setReferenceVisit("Yes");
						} else {
							calendFuncBean.setReferenceVisit("No");
						}
						calendFuncBean.setEventsReferenceVisit(seddao.findByPK(
								refEventResult.getStudyEventDefinitionId()).getName());
						events.add(calendFuncBean);
					}
				} else {
					logger.debug("This event is RV or Event without RV");
					calendFuncBean.setEventName(sedBean.getName());
					if ("true".equalsIgnoreCase(String.valueOf(sedBean.getReferenceVisit()))) {
						calendFuncBean.setReferenceVisit("Yes");
					} else {
						calendFuncBean.setReferenceVisit("No");
					}
					// set dateMax, dateMin, dateEmail using Date(0)
					// for correct sort table display
					calendFuncBean.setDateSchedule(new Date(0));
					calendFuncBean.setDateEmail(new Date(0));
					calendFuncBean.setDateMax(new Date(0));
					calendFuncBean.setDateMin(new Date(0));
					events.add(calendFuncBean);
				}

			}
		}
		// request.setAttribute("events", events);
		request.setAttribute("table", getTable(events, subjectId));
		request.setAttribute("subjectLabel", ssBean.getLabel());
		request.setAttribute("currentDate", new Date());
		forwardPage(Page.SHOW_CALENDAR_FUNC_PER_SUBJ, request, response);
	}

	private EntityBeanTable getTable(ArrayList events, int subjectId) {
		EntityBeanTable table = getEntityBeanTable();
		String[] columns = { getResWord().getString("calendared_event_name"), getResWord().getString("min_max_date_range"),
				getResWord().getString("schedule_date"), getResWord().getString("user_email_date"),
				getResWord().getString("is_reference_event"), getResWord().getString("reference_visit_for_event")};
		ArrayList rows = CalendarEventRow.generateRowsFromBeans(events);
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		HashMap args = new HashMap();
		args.put("id", Integer.toString(subjectId));
		table.setQuery("ViewCalendaredEventsForSubject", args);
		table.setSortingIfNotExplicitlySet(CalendarEventRow.COL_SCHEDULE_DATE, true);
		table.setRows(rows);
		table.computeDisplay();
		return table;
	}
}
