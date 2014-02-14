/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control;

import com.clinovo.bean.display.DisplayWidgetsLayoutBean;
import com.clinovo.model.Widget;
import com.clinovo.model.WidgetsLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.admin.EventStatusStatisticsTableFactory;
import org.akaza.openclinica.control.admin.SiteStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudyStatisticsTableFactory;
import org.akaza.openclinica.control.admin.StudySubjectStatusStatisticsTableFactory;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.submit.ListStudySubjectTableFactory;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

/**
 * 
 * The main controller servlet for all the work behind study sites for OpenClinica.
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class MainMenuServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		ub.incNumVisitsToMainMenu();
		
		request.getSession().setAttribute(USER_BEAN_NAME, ub);
		request.getSession().setAttribute("userRole", currentRole);
		request.setAttribute("iconInfoShown", true);
		request.setAttribute("closeInfoShowIcons", false);

		if (ub == null || ub.getId() == 0) {// in case database connection is
			// broken
			forwardPage(Page.MENU, false, request, response);
			return;
		}
		
		StudyDAO sdao = new StudyDAO(getDataSource());
		ArrayList studies;

		long pwdExpireDay = Long.parseLong(SQLInitServlet.getField("pwd.expiration.days"));
		Date lastPwdChangeDate = ub.getPasswdTimestamp();

		// a flag tells whether users are required to change pwd upon the first
		// time log in or pwd expired
		int pwdChangeRequired = Integer.parseInt(SQLInitServlet.getField("pwd.change.required"));
		// update last visit date to current date
		UserAccountDAO udao = getUserAccountDAO();
		UserAccountBean ub1 = (UserAccountBean) udao.findByPK(ub.getId());
		ub1.setLastVisitDate(new Date(System.currentTimeMillis()));
		// have to actually set the above to a timestamp? tbh
		ub1.setOwner(ub1);
		ub1.setUpdater(ub1);
		udao.update(ub1);

		// Use study Id in JSPs
		request.setAttribute("studyId", currentStudy.getId());
		// Event Definition list and Group Class list for add suybject window.
		request.setAttribute("allDefsArray", getEventDefinitionsByCurrentStudy(request));
		request.setAttribute("studyGroupClasses", getStudyGroupClassesByCurrentStudy(request));
		
		// not a new user
		if (lastPwdChangeDate != null || pwdChangeRequired == 0) {

			if (lastPwdChangeDate == null) {
				lastPwdChangeDate = new Date();
			}
			Calendar cal = Calendar.getInstance();
			// compute difference between current date and lastPwdChangeDate
			long difference = Math.abs(cal.getTime().getTime() - lastPwdChangeDate.getTime());
			long days = difference / (1000 * 60 * 60 * 24);
			request.getSession().setAttribute("passwordExpired", "no");

			if (pwdExpireDay != 0 && days >= pwdExpireDay) {// password expired, need to be changed
				studies = (ArrayList) sdao.findAllByUser(ub.getName());
				request.setAttribute("studies", studies);
				request.getSession().setAttribute("userBean1", ub);
				addPageMessage(respage.getString("password_expired"), request);
				// Add the feature that if password is expired,
				// have to go through /ResetPassword page
				request.getSession().setAttribute("passwordExpired", "yes");
				if (pwdChangeRequired == 1) {
					request.setAttribute("mustChangePass", "yes");
					addPageMessage(respage.getString("your_password_has_expired_must_change"), request);
				} else {
					request.setAttribute("mustChangePass", "no");
					addPageMessage(
							respage.getString("password_expired") + " "
									+ respage.getString("if_you_do_not_want_change_leave_blank"), request);
				}
				forwardPage(Page.RESET_PASSWORD, request, response);
			} else {
				
				String userAgent = request.getHeader("User-Agent");
				int browserVer = 0;
					
				if(userAgent.contains("MSIE")){ //Checking if Internet Explorer
					String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
					browserVer=Integer.parseInt(substring.split(" ")[1]);
				}
				
				if (ub.getNumVisitsToMainMenu() <= 1) {
					if (ub.getLastVisitDate() != null) {
						addPageMessage(
								respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". "
										+ respage.getString("last_logged") + " "
										+ getLocalDf(request).format(ub.getLastVisitDate()) + ". ", request);
					} else {
						addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName()
								+ ". ", request);
					}

					if (currentStudy.getStatus().isLocked()) {
						addPageMessage(respage.getString("current_study_locked"), request);
					} else if (currentStudy.getStatus().isFrozen()) {
						addPageMessage(respage.getString("current_study_frozen"), request);
					}
				}

				Integer assignedDiscrepancies = getDiscrepancyNoteDAO().getViewNotesCountWithFilter(
						" AND dn.assigned_user_id = " + ub.getId() + " AND dn.resolution_status_id IN (1,2,3)",
						currentStudy);
				request.setAttribute("assignedDiscrepancies", assignedDiscrepancies == null ? 0 : assignedDiscrepancies);

				int parentStudyId = currentStudy.getParentStudyId() > 0 ? currentStudy.getParentStudyId()
						: currentStudy.getId();
				StudyParameterValueDAO spvdao = getStudyParameterValueDAO();
				StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
				currentStudy.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
				String idSetting = parentSPV.getValue();
				if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
					request.setAttribute("label", resword.getString("id_generated_Save_Add"));
				}
				if (browserVer!=0 && browserVer<8){
				if (currentRole.isInvestigator() || currentRole.isClinicalResearchCoordinator()) {
					setupListStudySubjectTable(request, response);
				}
				if (currentRole.getRole() == Role.STUDY_MONITOR) {
					setupSubjectSDVTable(request);
				} else if (currentRole.getRole().equals(Role.STUDY_CODER)) {
					response.sendRedirect(request.getContextPath() + "/pages/codedItems");
					return;
				} else if (currentRole.isSysAdmin() || currentRole.isStudyAdministrator()
						|| currentRole.isStudyDirector()) {
					if (currentStudy.getStatus().isPending()) {
						request.getSession().setAttribute("skipURL", "true");

						response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
						return;
					}
					setupStudySiteStatisticsTable(request, response);
					setupSubjectEventStatusStatisticsTable(request, response);
					setupStudySubjectStatusStatisticsTable(request, response);
					if (currentStudy.getParentStudyId() == 0) {
						setupStudyStatisticsTable(request, response);
					}

				}
				udao.updatePasswdHistory(ub);
				forwardPage(Page.MENU, request, response);
				request.setAttribute("displayPageVersion", "old");
				} else {
	
				if (currentRole.isSysAdmin() || currentRole.isStudyAdministrator()	|| currentRole.isStudyDirector()) {			
					if (currentStudy.getStatus().isPending()) {
						request.getSession().setAttribute("skipURL", "true");
						response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
						return;
					}
				}
									
				StudyBean sb = (StudyBean) request.getSession().getAttribute("study");
	
				int studyId = sb.getId();
				int userId = ub.getId();
				List<WidgetsLayout> widgetsLayout = getWidgetsLayoutService().findAllByStudyIdAndUserId(studyId, userId);
				List <DisplayWidgetsLayoutBean> dispayWidgetsLayout = new ArrayList<DisplayWidgetsLayoutBean>();

				if(widgetsLayout.size()==0||widgetsLayout==null){
					int widgetsOrdinalCounter = 1;
					List <Widget> widgets = getWidgetService().findAll();                	

					for(int w = 0; w < widgets.size(); w++){
						Widget currentWidget = widgets.get(w);
						String accesToCurrentWidget = currentWidget.getHaveAccess();                		

						if ((currentStudy.getParentStudyId()>0 && currentWidget.isSiteMetrics()) || (currentStudy.getParentStudyId() == 0 && currentWidget.isStudyMetrics() == true)){

							int currentUserRole = currentRole.getRole().getId();
							if (accesToCurrentWidget.contains(Integer.toString(currentUserRole))){
								WidgetsLayout currentWidgetsLayout = new WidgetsLayout();	                		
								currentWidgetsLayout.setStudyId(studyId);
								currentWidgetsLayout.setUserId(userId);

								String defaultFor = currentWidget.getDisplayAsDefault();

								if (defaultFor.contains(Integer.toString(currentUserRole))){
									currentWidgetsLayout.setOrdinal(widgetsOrdinalCounter);
									widgetsOrdinalCounter++;
								} else {
									currentWidgetsLayout.setOrdinal(0);
								}
									widgetsLayout.add(currentWidgetsLayout);
									currentWidget.getWidgetsLayout().add(currentWidgetsLayout);
								} else {
									//user have no access to this widget
								}
							}
					getWidgetService().saveWidget(currentWidget);
					}          	
				}
					                
				for (int z=0; z<widgetsLayout.size();z++){                	
					WidgetsLayout currentLayout = widgetsLayout.get(z);
					Widget currentWidget = getWidgetService().findByChildsId(currentLayout.getId());

					String widgetName = currentWidget.getWidgetName().toLowerCase().replaceAll(" ", "_");

					DisplayWidgetsLayoutBean currentDisplay = new DisplayWidgetsLayoutBean();

					currentDisplay.setWidgetName(widgetName+".jsp");
					currentDisplay.setOrdinal(currentLayout.getOrdinal());
					currentDisplay.setWidgetId(currentWidget.getId());

					dispayWidgetsLayout.add(currentDisplay);
				}

				Collections.sort(dispayWidgetsLayout, DisplayWidgetsLayoutBean.comparatorForDisplayWidgetsLayout);
				request.setAttribute("dispayWidgetsLayout", dispayWidgetsLayout);
				request.setAttribute("displayPageVersion", "new");

				udao.updatePasswdHistory(ub);
				forwardPage(Page.MENU, request, response);					
			}
		}

		} else {
			studies = (ArrayList) sdao.findAllByUser(ub.getName());
			request.setAttribute("studies", studies);
			request.getSession().setAttribute("userBean1", ub);

			if (pwdChangeRequired != 1) {
				udao.updatePasswdHistory(ub);
				forwardPage(Page.MENU, request, response);
			}
		}

	}

	private void setupSubjectSDVTable(HttpServletRequest request) {
		StudyBean currentStudy = getCurrentStudy(request);
		request.setAttribute("studyId", currentStudy.getId());
		request.setAttribute("showMoreLink", "true");
		String sdvMatrix = getSDVUtil().renderEventCRFTableWithLimit(request, currentStudy.getId(), "");
		request.setAttribute("sdvMatrix", sdvMatrix);
	}

	private void setupStudySubjectStatusStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudySubjectStatusStatisticsTableFactory factory = new StudySubjectStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySubjectStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySubjectStatusStatistics", studySubjectStatusStatistics);
	}

	private void setupSubjectEventStatusStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		EventStatusStatisticsTableFactory factory = new EventStatusStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyEventDao(getStudyEventDAO());
		factory.setStudyDao(getStudyDAO());
		String subjectEventStatusStatistics = factory.createTable(request, response).render();
		request.setAttribute("subjectEventStatusStatistics", subjectEventStatusStatistics);
	}

	private void setupStudySiteStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		SiteStatisticsTableFactory factory = new SiteStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studySiteStatistics = factory.createTable(request, response).render();
		request.setAttribute("studySiteStatistics", studySiteStatistics);
	}

	private void setupStudyStatisticsTable(HttpServletRequest request, HttpServletResponse response) {
		StudyBean currentStudy = getCurrentStudy(request);
		StudyStatisticsTableFactory factory = new StudyStatisticsTableFactory();
		factory.setStudySubjectDao(getStudySubjectDAO());
		factory.setCurrentStudy(currentStudy);
		factory.setStudyDao(getStudyDAO());
		String studyStatistics = factory.createTable(request, response).render();
		request.setAttribute("studyStatistics", studyStatistics);
	}

	private void setupListStudySubjectTable(HttpServletRequest request, HttpServletResponse response) {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
		factory.setStudyEventDefinitionDao(getStudyEventDefinitionDAO());
		factory.setSubjectDAO(getSubjectDAO());
		factory.setStudySubjectDAO(getStudySubjectDAO());
		factory.setStudyEventDAO(getStudyEventDAO());
		factory.setStudyBean(currentStudy);
		factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
		factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
		factory.setStudyDAO(getStudyDAO());
		factory.setCurrentRole(currentRole);
		factory.setCurrentUser(ub);
		factory.setEventCRFDAO(getEventCRFDAO());
		factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
		factory.setDiscrepancyNoteDAO(getDiscrepancyNoteDAO());
		factory.setStudyGroupDAO(getStudyGroupDAO());
		factory.setDynamicEventDao(getDynamicEventDao());
		factory.setSortForMainMenuServlet(true);
		String findSubjectsHtml = factory.createTable(request, response).render();
		request.setAttribute("findSubjectsHtml", findSubjectsHtml);
	}
}
