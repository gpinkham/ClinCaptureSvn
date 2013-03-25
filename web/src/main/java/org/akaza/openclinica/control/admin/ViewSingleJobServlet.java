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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.akaza.openclinica.bean.admin.AuditEventBean;
import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
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
import org.quartz.impl.StdScheduler;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class ViewSingleJobServlet extends SecureController {

	private static String TRIGGER_IMPORT_GROUP = "importTrigger";
	private static String SCHEDULER = "schedulerFactoryBean";
	private StdScheduler scheduler;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		String triggerName = fp.getString("tname");
		String gName = fp.getString("gname");
		String groupName = "";
		if (gName.equals("") || gName.equals("0")) {
			groupName = XsltTriggerService.TRIGGER_GROUP_NAME;
		} else { 
			groupName = TRIGGER_IMPORT_GROUP;
		}
		scheduler = getScheduler();
		Trigger trigger = scheduler.getTrigger(triggerName, groupName);

		if (trigger == null) {
			System.out.println("*** reset trigger group name");
			groupName = XsltTriggerService.TRIGGER_GROUP_NAME;
			trigger = scheduler.getTrigger(triggerName.trim(), groupName);
		}
		logger.debug("found trigger name: " + triggerName);
		logger.debug("found group name: " + groupName);
		TriggerBean triggerBean = new TriggerBean();
		JobDataMap dataMap = new JobDataMap();
		AuditEventDAO auditEventDAO = new AuditEventDAO(sm.getDataSource());

		try {
			triggerBean.setFullName(trigger.getName());
			triggerBean.setPreviousDate(trigger.getPreviousFireTime());
			triggerBean.setNextDate(trigger.getNextFireTime());
			if (scheduler.getTriggerState(triggerName, groupName) == Trigger.STATE_PAUSED) {
				triggerBean.setActive(false);
				logger.debug("setting active to false for trigger: " + trigger.getName());
			} else {
				triggerBean.setActive(true);
				logger.debug("setting active to TRUE for trigger: " + trigger.getName());
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
					DatasetDAO datasetDAO = new DatasetDAO(sm.getDataSource());
					DatasetBean dataset = (DatasetBean) datasetDAO.findByPK(dsId);
					triggerBean.setDataset(dataset);
				}
				int userId = dataMap.getInt(ExampleSpringJob.USER_ID);
				// need to set information, extract bean, user account bean

				UserAccountDAO userAccountDAO = new UserAccountDAO(sm.getDataSource());

				triggerBean.setContactEmail(contactEmail);

				UserAccountBean userAccount = (UserAccountBean) userAccountDAO.findByPK(userId);

				triggerBean.setUserAccount(userAccount);

				ArrayList<AuditEventBean> triggerLogs = auditEventDAO.findAllByAuditTable(trigger.getName());

				// set the table for the audit event beans here

				ArrayList allRows = AuditEventRow.generateRowsFromBeans(triggerLogs);

				EntityBeanTable table = fp.getEntityBeanTable();
				String[] columns = { resword.getString("date_and_time"), resword.getString("action_message"),
						resword.getString("entity_operation"),
						resword.getString("changes_and_additions"), resword.getString("actions") };

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

		forwardPage(Page.VIEW_SINGLE_JOB);
	}
}
