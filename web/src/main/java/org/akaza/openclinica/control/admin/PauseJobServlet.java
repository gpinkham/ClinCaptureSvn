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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdScheduler;
import org.springframework.stereotype.Component;

/**
 * PauseJobServlet, a small servlet to pause/unpause a trigger in the scehduler. The basic premise, you provide a name
 * which has been validated by JavaScript on the JSP side with a simple confirm dialog box. You get here - the job is
 * either paused or unpaused. Possible complications, if we start using Priority for other things.
 * 
 * @author Tom Hickerson
 * 
 */
@SuppressWarnings({ "serial" })
@Component
public class PauseJobServlet extends Controller {

	private static final String groupImportName = "importTrigger";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		// TODO copied from CreateJobExport - DRY? tbh
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
		// above copied from create dataset servlet, needs to be changed to
		// allow only admin-level users

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		FormProcessor fp = new FormProcessor(request);
		String triggerName = fp.getString("tname");
		String gName = request.getParameter("gname");
		String finalGroupName;
		if ("".equals(gName) || "0".equals(gName)) {
			finalGroupName = XsltTriggerService.TRIGGER_GROUP_NAME;
		} else {// should equal 1
			finalGroupName = groupImportName;
		}
		String deleteMe = fp.getString("del");
		StdScheduler scheduler = getStdScheduler();
		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, finalGroupName));
		try {
			if (("y".equals(deleteMe)) && (ub.isSysAdmin())) {
				scheduler.deleteJob(trigger.getJobKey());
				// set return message here
				System.out.println("deleted job: " + triggerName);
				addPageMessage("The following job " + triggerName
						+ " and its corresponding Trigger have been deleted from the system.", request);
			} else {

				if (scheduler.getTriggerState(trigger.getKey()) == Trigger.TriggerState.PAUSED) {
					scheduler.resumeTrigger(trigger.getKey());
					System.out.println("-- resuming trigger! " + triggerName + " " + finalGroupName);
					addPageMessage("This trigger " + triggerName
							+ " has been resumed and will continue to run until paused or deleted.", request);
				} else {
					scheduler.pauseTrigger(trigger.getKey());
					System.out.println("-- pausing trigger! " + triggerName + " " + finalGroupName);
					addPageMessage("This trigger " + triggerName
							+ " has been paused, and will not run again until it is restored.", request);
				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// all validation done on JSP side
		// forward back to view job servlet here
		// set a message
		if ("".equals(gName) || "0".equals(gName)) {
			forwardPage(Page.VIEW_JOB_SERVLET, request, response);
		} else {
			forwardPage(Page.VIEW_IMPORT_JOB_SERVLET, request, response);
		}
	}

}
