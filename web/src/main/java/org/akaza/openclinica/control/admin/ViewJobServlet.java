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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.RememberLastPage;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.service.extract.XsltTriggerService;
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

/**
 * Generates the list of jobs and allow us to view them.
 * 
 * @author thickerson
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewJobServlet extends RememberLastPage {

	public static final String SAVED_VIEW_EXPORT_JOB_URL = "savedViewExportJobUrl";
	public static final int DESCRIPTION_COL = 3;
	public static final int ACTION_COL = 7;

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
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (shouldRedirect(request, response)) {
			return;
		}
		StudyBean currentStudy = getCurrentStudy(request);
		FormProcessor fp = new FormProcessor(request);
		// First we must get a reference to a scheduler
		StdScheduler scheduler = getStdScheduler();
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher
				.triggerGroupEquals(XsltTriggerService.TRIGGER_GROUP_NAME));

		ArrayList triggerBeans = new ArrayList();
		for (TriggerKey triggerKey : triggerKeys) {
			Trigger trigger = scheduler.getTrigger(triggerKey);
			try {
				logger.debug("prev fire time " + trigger.getPreviousFireTime().toString());
				logger.debug("next fire time " + trigger.getNextFireTime().toString());
				logger.debug("final fire time: " + trigger.getFinalFireTime().toString());
			} catch (NullPointerException npe) {
				// could be nulls in the dates, etc
				logger.warn("threw NPE with getting properties from trigger");
			}

			TriggerBean triggerBean = new TriggerBean();
			triggerBean.setFullName(trigger.getKey().getName());
			triggerBean.setPreviousDate(trigger.getPreviousFireTime());
			triggerBean.setNextDate(trigger.getNextFireTime());
			if (trigger.getDescription() != null) {
				triggerBean.setDescription(trigger.getDescription());
			}
			// setting: frequency, dataset name
			JobDataMap dataMap;
			DatasetDAO datasetDAO = getDatasetDAO();
			StudyDAO studyDao = getStudyDAO();

			if (trigger.getJobDataMap().size() > 0) {
				dataMap = trigger.getJobDataMap();
				int dsId = dataMap.getInt(ExampleSpringJob.DATASET_ID);
				String periodToRun = dataMap.getString(ExampleSpringJob.PERIOD);
				triggerBean.setPeriodToRun(periodToRun);
				DatasetBean dataset = (DatasetBean) datasetDAO.findByPK(dsId);
				triggerBean.setDataset(dataset);
				triggerBean.setDatasetName(dataset.getName());
				StudyBean study = (StudyBean) studyDao.findByPK(dataset.getStudyId());
				triggerBean.setStudyName(study.getName());
			}
			logger.debug("Trigger Priority: " + trigger.getKey().getName() + " " + trigger.getPriority());
			if (scheduler.getTriggerState(triggerKey) == Trigger.TriggerState.PAUSED) {
				triggerBean.setActive(false);
				logger.debug("setting active to false for trigger: " + trigger.getKey().getName());
			} else {
				triggerBean.setActive(true);
				logger.debug("setting active to TRUE for trigger: " + trigger.getKey().getName());
			}
			StudyBean jobStudyBean = (StudyBean) studyDao.findByPK((Integer) trigger.getJobDataMap().get("studyId"));
			if (jobStudyBean.getId() == currentStudy.getId() || jobStudyBean.getParentStudyId() == currentStudy.getId()) {
				triggerBeans.add(triggerBean);
			}
		}

		ArrayList allRows = TriggerRow.generateRowsFromBeans(triggerBeans);

		EntityBeanTable table = fp.getEntityBeanTable();
		String[] columns = {resword.getString("name"), resword.getString("previous_fire_time"),
				resword.getString("next_fire_time"), resword.getString("description"), resword.getString("study"),
				resword.getString("period_to_run"), resword.getString("dataset"), resword.getString("actions")};
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(DESCRIPTION_COL);
		table.hideColumnLink(ACTION_COL);
		table.setQuery("ViewJob", new HashMap());
		table.setRows(allRows);
		table.computeDisplay();

		request.setAttribute("table", table);

		forwardPage(Page.VIEW_JOB, request, response);
	}

	@Override
	protected String getUrlKey(HttpServletRequest request) {
		return SAVED_VIEW_EXPORT_JOB_URL;
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
