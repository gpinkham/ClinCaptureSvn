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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.AuditEventDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.AuditEventRow;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.job.ExampleSpringJob;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdScheduler;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class ViewSingleJobServlet extends Controller {

	private static final String TRIGGER_IMPORT_GROUP = "importTrigger";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String triggerName = fp.getString("tname");
		String gName = fp.getString("gname");
		String groupName;
		if (gName.equals("") || gName.equals("0")) {
			groupName = XsltTriggerService.TRIGGER_GROUP_NAME;
		} else {
			groupName = TRIGGER_IMPORT_GROUP;
		}
		StdScheduler scheduler = getStdScheduler();
		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, groupName));

		if (trigger == null) {
			System.out.println("*** reset trigger group name");
			groupName = XsltTriggerService.TRIGGER_GROUP_NAME;
			trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName.trim(), groupName));
		}
		logger.debug("found trigger name: " + triggerName);
		logger.debug("found group name: " + groupName);
		TriggerBean triggerBean = new TriggerBean();
		JobDataMap dataMap;
		AuditEventDAO auditEventDAO = getAuditEventDAO();

		try {
			triggerBean.setFullName(trigger.getKey().getName());
			triggerBean.setPreviousDate(trigger.getPreviousFireTime());
			triggerBean.setNextDate(trigger.getNextFireTime());
			if (scheduler.getTriggerState(trigger.getKey()) == Trigger.TriggerState.PAUSED) {
				triggerBean.setActive(false);
				logger.debug("setting active to false for trigger: " + trigger.getKey().getName());
			} else {
				triggerBean.setActive(true);
				logger.debug("setting active to TRUE for trigger: " + trigger.getKey().getName());
			}
			// <<
			if (trigger.getDescription() != null) {
				triggerBean.setDescription(trigger.getDescription());
			}
			if (trigger.getJobDataMap().size() > 0) {
				dataMap = trigger.getJobDataMap();
				String contactEmail = dataMap.getString(XsltTriggerService.EMAIL);
				logger.debug("found email: " + contactEmail);
				if (gName.equals("") || gName.equals("0")) {
					String exportFormat = dataMap.getString(XsltTriggerService.EXPORT_FORMAT);
					String periodToRun = dataMap.getString(ExampleSpringJob.PERIOD);
					int dsId = dataMap.getInt(ExampleSpringJob.DATASET_ID);
					triggerBean.setExportFormat(exportFormat);
					triggerBean.setPeriodToRun(periodToRun);
					DatasetDAO datasetDAO = getDatasetDAO();
					DatasetBean dataset = (DatasetBean) datasetDAO.findByPK(dsId);
					triggerBean.setDataset(dataset);
				}
				int userId = dataMap.getInt(ExampleSpringJob.USER_ID);
				// need to set information, extract bean, user account bean

				UserAccountDAO userAccountDAO = getUserAccountDAO();

				triggerBean.setContactEmail(contactEmail);

				UserAccountBean userAccount = (UserAccountBean) userAccountDAO.findByPK(userId);

				triggerBean.setUserAccount(userAccount);

				ArrayList<AuditEventBean> triggerLogs = auditEventDAO.findAllByAuditTable(trigger.getKey().getName());

				// set the table for the audit event beans here

				ArrayList allRows = AuditEventRow.generateRowsFromBeans(triggerLogs);

				EntityBeanTable table = getEntityBeanTable();
				String[] columns = { getResWord().getString("date_and_time"), getResWord().getString("action_message"),
						getResWord().getString("entity_operation"), getResWord().getString("changes_and_additions"),
						getResWord().getString("actions")};

				table.setColumns(new ArrayList(Arrays.asList(columns)));
				table.setAscendingSort(false);
				table.hideColumnLink(1);
				table.hideColumnLink(3);
				table.hideColumnLink(4);

				table.setQuery("ViewSingleJob?tname=" + triggerName + "&gname=" + gName, new HashMap());
				table.setRows(allRows);
				table.computeDisplay();

				request.setAttribute("table", table);
			}

		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.debug(" found NPE " + e.getMessage());
			e.printStackTrace();
		}
		// need to show the extract for which this runs, which files, etc
		// in other words the job data map

		request.setAttribute("triggerBean", triggerBean);

		request.setAttribute("groupName", groupName);

		forwardPage(Page.VIEW_SINGLE_JOB, request, response);
	}
}
