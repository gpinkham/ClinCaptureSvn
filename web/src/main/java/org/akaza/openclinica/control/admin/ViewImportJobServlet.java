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

package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.TriggerRow;
import org.akaza.openclinica.web.job.ExampleSpringJob;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * The servlet for managing import jobs.
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class ViewImportJobServlet extends RememberLastPage {

	public static final String SAVED_VIEW_IMPORT_JOB_URL = "savedViewImportJobUrl";
	public static final String IMPORT_TRIGGER = "importTrigger";
	public static final int DESCRIPTION_COL = 3;
	public static final int ACTION_COL = 5;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");
		// TODO
		// changed to
		// allow only admin-level users
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}

		FormProcessor fp = new FormProcessor(request);
		// First we must get a reference to a scheduler
		StdScheduler scheduler = getStdScheduler();
		// then we pull all the triggers that are specifically named
		// IMPORT_TRIGGER.
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(IMPORT_TRIGGER));

		// the next bit goes out and processes all the triggers
		ArrayList triggerBeans = new ArrayList<TriggerBean>();

		for (TriggerKey triggerKey : triggerKeys) {
			Trigger trigger = scheduler.getTrigger(triggerKey);
			TriggerBean triggerBean = new TriggerBean();
			triggerBean.setFullName(trigger.getKey().getName());
			triggerBean.setPreviousDate(trigger.getPreviousFireTime());
			triggerBean.setNextDate(trigger.getNextFireTime());
			if (trigger.getDescription() != null) {
				triggerBean.setDescription(trigger.getDescription());
			}
			// this next bit of code looks at the job data map and pulls out
			// specific items
			JobDataMap dataMap;

			if (trigger.getJobDataMap().size() > 0) {
				dataMap = trigger.getJobDataMap();
				triggerBean.setStudyName(dataMap.getString(ExampleSpringJob.STUDY_NAME));
			}

			// this next bit of code looks to see if the trigger is paused
			logger.debug("Trigger Priority: " + trigger.getKey().getName() + " " + trigger.getPriority());
			if (scheduler.getTriggerState(triggerKey) == Trigger.TriggerState.PAUSED) {
				triggerBean.setActive(false);
				logger.debug("setting active to false for trigger: " + trigger.getKey().getName());
			} else {
				triggerBean.setActive(true);
				logger.debug("setting active to TRUE for trigger: " + trigger.getKey().getName());
			}
			triggerBeans.add(triggerBean);
			// our wrapper to show triggers
		}

		// set up the table here and get ready to send to the web page
		ArrayList allRows = TriggerRow.generateRowsFromBeans(triggerBeans);

		EntityBeanTable table = fp.getEntityBeanTable();
		String[] columns = { resword.getString("name"), resword.getString("previous_fire_time"),
				resword.getString("next_fire_time"), resword.getString("description"), resword.getString("study"), resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(DESCRIPTION_COL);
		table.hideColumnLink(ACTION_COL);
		table.setQuery("ViewImportJob", new HashMap());
		table.setRows(allRows);

		table.computeDisplay();

		request.setAttribute("table", table);

		forwardPage(Page.VIEW_IMPORT_JOB, request, response);

	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_VIEW_IMPORT_JOB_URL;
	}

	@Override
	protected String getDefaultUrl(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String eblFiltered = fp.getString("ebl_filtered");
		String eblFilterKeyword = fp.getString("ebl_filterKeyword");
		String eblSortColumnInd = fp.getString("ebl_sortColumnInd");
		String eblSortAscending = fp.getString("ebl_sortAscending");
		return "?submitted=1&ebl_page=1&ebl_sortColumnInd=" + (!eblSortColumnInd.isEmpty() ? eblSortColumnInd : "0")
				+ "&ebl_sortAscending=" + (!eblSortAscending.isEmpty() ? eblSortAscending : "1") + "&ebl_filtered="
				+ (!eblFiltered.isEmpty() ? eblFiltered : "0") + "&ebl_filterKeyword="
				+ (!eblFilterKeyword.isEmpty() ? eblFilterKeyword : "") + "&ebl_paginated=1";
	}

	@Override
	protected boolean userDoesNotUseJmesaTableForNavigation(HttpServletRequest request) {
		return request.getQueryString() == null
				|| (request.getQueryString().contains("tname") && request.getQueryString().contains("gname"));
	}
}
