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

import org.akaza.openclinica.bean.admin.TriggerBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.TriggerRow;
import org.akaza.openclinica.web.job.ExampleSpringJob;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;

@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ViewImportJobServlet extends SecureController {

	private static String SCHEDULER = "schedulerFactoryBean";
	private static String IMPORT_TRIGGER = "importTrigger";

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
		// changed to
		// allow only admin-level users
	}

	private StdScheduler getScheduler() {
		scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(
				context).getBean(SCHEDULER);
		return scheduler;
	}

	@Override
	protected void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		// First we must get a reference to a scheduler
		scheduler = getScheduler();
		// then we pull all the triggers that are specifically named
		// IMPORT_TRIGGER.
		String[] triggerNames = scheduler.getTriggerNames(IMPORT_TRIGGER);

		// the next bit goes out and processes all the triggers
		ArrayList triggerBeans = new ArrayList<TriggerBean>();

		for (String triggerName : triggerNames) {
			Trigger trigger = scheduler.getTrigger(triggerName, IMPORT_TRIGGER);
			logger.debug("found trigger, full name: " + trigger.getFullName());
			try {
				logger.debug("prev fire time " + trigger.getPreviousFireTime().toString());
				logger.debug("next fire time " + trigger.getNextFireTime().toString());
				logger.debug("final fire time: " + trigger.getFinalFireTime().toString());
			} catch (NullPointerException npe) {
				// could be nulls in the dates, etc
			}

			TriggerBean triggerBean = new TriggerBean();
			triggerBean.setFullName(trigger.getName());
			triggerBean.setPreviousDate(trigger.getPreviousFireTime());
			triggerBean.setNextDate(trigger.getNextFireTime());
			if (trigger.getDescription() != null) {
				triggerBean.setDescription(trigger.getDescription());
			}
			// this next bit of code looks at the job data map and pulls out
			// specific items
			JobDataMap dataMap = new JobDataMap();

			if (trigger.getJobDataMap().size() > 0) {
				dataMap = trigger.getJobDataMap();
				triggerBean.setStudyName(dataMap.getString(ExampleSpringJob.STUDY_NAME));
			}

			// this next bit of code looks to see if the trigger is paused
			logger.debug("Trigger Priority: " + trigger.getName() + " " + trigger.getPriority());
			if (scheduler.getTriggerState(triggerName, IMPORT_TRIGGER) == Trigger.STATE_PAUSED) {
				triggerBean.setActive(false);
				logger.debug("setting active to false for trigger: " + trigger.getName());
			} else {
				triggerBean.setActive(true);
				logger.debug("setting active to TRUE for trigger: " + trigger.getName());
			}
			triggerBeans.add(triggerBean);
			// our wrapper to show triggers
		}

		// set up the table here and get ready to send to the web page

		ArrayList allRows = TriggerRow.generateRowsFromBeans(triggerBeans);

		EntityBeanTable table = fp.getEntityBeanTable();
		String[] columns = { "Name", "Previous Fire Time", "Next Fire Time", "Description", "Study",
				resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(3);
		table.hideColumnLink(5);
		table.setQuery("ViewImportJob", new HashMap());
		table.setSortingColumnInd(0);
		table.setRows(allRows);
		table.computeDisplay();

		request.setAttribute("table", table);

		forwardPage(Page.VIEW_IMPORT_JOB);

	}

}
