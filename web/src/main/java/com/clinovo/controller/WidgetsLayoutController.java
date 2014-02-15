package com.clinovo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.clinovo.bean.display.DisplayWidgetsLayoutBean;
import com.clinovo.dao.WidgetsLayoutDAO;
import com.clinovo.model.Widget;
import com.clinovo.model.WidgetsLayout;
import com.clinovo.service.WidgetService;
import com.clinovo.service.WidgetsLayoutService;
import com.lowagie.text.pdf.hyphenation.TernaryTree.Iterator;

@Controller
@SuppressWarnings({"unused","rawtypes"})

public class WidgetsLayoutController {
	
	private DiscrepancyNoteDAO discrepancieNoteDao;	
	private StudyDAO studyDAO;
	private StudyEventDAO studyEventDAO;
	private StudyEventDefinitionDAO studyEventDefinitionDAO;
	
	private UserAccountBean ub;
	private StudyBean sb;
	
	@Autowired
    private DataSource datasource;	
	
	@Autowired
	private WidgetsLayoutService widgetLayoutService;
	
	@Autowired
	private WidgetService widgetService;
	
	@RequestMapping("/configureHomePage")
	public ModelMap configureHomePageHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelMap model = new ModelMap();
		ResourceBundleProvider.updateLocale(request.getLocale());
				
		ub = (UserAccountBean) request.getSession().getAttribute("userBean");
		sb = (StudyBean) request.getSession().getAttribute("study");
		
		int studyId = sb.getId();
		int userId = ub.getId();
		int id = 1;
		
		List<WidgetsLayout> widgetsLayout = widgetLayoutService.findAllByStudyIdAndUserId(studyId,userId);
		List <DisplayWidgetsLayoutBean> dispayWidgetsLayout = new ArrayList<DisplayWidgetsLayoutBean>();
		
		for (int z=0; z<widgetsLayout.size();z++){                	
        	WidgetsLayout currentLayout = widgetsLayout.get(z);
            Widget currentWidget = widgetService.findByChildsId(currentLayout.getId());
        	
            String widgetName = currentWidget.getWidgetName().toLowerCase().replaceAll(" ", "_");
        	
            DisplayWidgetsLayoutBean currentDisplay = new DisplayWidgetsLayoutBean();
        	
        	currentDisplay.setWidgetName(widgetName+".jsp");
        	currentDisplay.setOrdinal(currentLayout.getOrdinal());
        	currentDisplay.setWidgetId(currentWidget.getId());
        	
        	dispayWidgetsLayout.add(currentDisplay);
        }
		
		Collections.sort(dispayWidgetsLayout, DisplayWidgetsLayoutBean.comparatorForDisplayWidgetsLayout);
	
		model.addAttribute("dispayWidgetsLayout", dispayWidgetsLayout);
		return model;
	}
	
	@RequestMapping("/saveHomePage")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public @ResponseBody 
	void saveHomePage(HttpServletRequest request){
		
		String orderInColumn1 = request.getParameter("orderInColumn1");
		String orderInColumn2 = request.getParameter("orderInColumn2");
		String unusedWidgets = request.getParameter("unusedWidgets");
		int userId = Integer.parseInt(request.getParameter("userId"));
		int studyId = Integer.parseInt(request.getParameter("studyId"));
						
		if(!orderInColumn1.equals("")){
			int ordinalCounter1 = 1;
			
			List <String> widgetsIdsColumn1 =  Arrays.asList(orderInColumn1.split("\\s*,\\s*"));
			
			for (int i1 = 0; i1 < widgetsIdsColumn1.size(); i1++){
				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(Integer.parseInt(widgetsIdsColumn1.get(i1)), studyId, userId);
				currentWidgetLayout.setOrdinal(ordinalCounter1);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
				ordinalCounter1 = ordinalCounter1  + 2;
			}
		}
		
		if(!orderInColumn2.equals("")){
			int ordinalCounter2 = 2;
			
			List <String> widgetsIdsColumn2 =  Arrays.asList(orderInColumn2.split("\\s*,\\s*"));
			
			for (int i2 = 0; i2 < widgetsIdsColumn2.size(); i2++){
				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(Integer.parseInt(widgetsIdsColumn2.get(i2)), studyId, userId);
				currentWidgetLayout.setOrdinal(ordinalCounter2);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
				ordinalCounter2 = ordinalCounter2  + 2;
			}
		}

		if(!unusedWidgets.equals("")){
			List <String> unusedWidgetsIds =  Arrays.asList(unusedWidgets.split("\\s*,\\s*"));
			
			for (int i3 = 0; i3 < unusedWidgetsIds.size(); i3++){
				WidgetsLayout currentWidgetLayout = widgetLayoutService.findByWidgetIdAndStudyIdAndUserId(Integer.parseInt(unusedWidgetsIds.get(i3)), studyId, userId);
				currentWidgetLayout.setOrdinal(0);
				widgetLayoutService.saveWidgetLayout(currentWidgetLayout);
			}
		}
		return;
	}
	
	@RequestMapping("/initNdsAssignedToMeWidget")
	public void initNdsAssignedToMeWidget(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");
		
		int currentUser =Integer.parseInt(request.getParameter("userId"));		
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		
		Integer newDns = getDiscrepancieNoteDAO().getViewNotesCountWithFilter(
				" AND dn.assigned_user_id = " + currentUser + " AND dn.resolution_status_id = 1", currentStudy);
		if (newDns==null){
			newDns=0;
		}
		Integer updatedDns = getDiscrepancieNoteDAO().getViewNotesCountWithFilter(
				" AND dn.assigned_user_id = " + currentUser + " AND dn.resolution_status_id = 2", currentStudy);		
		if (updatedDns==null){
			updatedDns=0;
		}
		Integer closedDns = getDiscrepancieNoteDAO().getViewNotesCountWithFilter(
				" AND dn.assigned_user_id = " + currentUser + " AND dn.resolution_status_id = 4", currentStudy);
		if (closedDns==null){
			closedDns=0;
		}
		String result = newDns + "," + updatedDns + "," + closedDns;
		response.getWriter().println(result);
	}
		
	private DiscrepancyNoteDAO getDiscrepancieNoteDAO(){
		if(discrepancieNoteDao==null){
			discrepancieNoteDao = new DiscrepancyNoteDAO(datasource);
		}
		return discrepancieNoteDao;
	}
	
	private StudyEventDAO getStudyEventDAO(){
		if(studyEventDAO==null){
			studyEventDAO=new StudyEventDAO(datasource);
		}
		return studyEventDAO;
	}
	
	private StudyEventDefinitionDAO getStudyEventDefinitionDAO(){
		if (studyEventDefinitionDAO==null){
			studyEventDefinitionDAO = new StudyEventDefinitionDAO(datasource);
		}
		return studyEventDefinitionDAO;
	}
}
