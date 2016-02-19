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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * PauseJobServlet, a small servlet to pause/unpause a trigger in the scehduler. The basic premise, you provide a name
 * which has been validated by JavaScript on the JSP side with a simple confirm dialog box. You get here - the job is
 * either paused or unpaused. Possible complications, if we start using Priority for other things.
 * 
 * @author Tom Hickerson
 * 
 */
@Component
public class PauseJobServlet extends Controller {

	private static final String GROUP_IMPORT_NAME = "importTrigger";
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		// TODO copied from CreateJobExport - DRY? tbh
		if (ub.isSysAdmin() || ub.isTechAdmin()) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				getResException().getString("not_allowed_access_extract_data_servlet"), "1");
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
		} else { // should equal 1
			finalGroupName = GROUP_IMPORT_NAME;
		}
		String deleteMe = fp.getString("del");
		StdScheduler scheduler = getStdScheduler();
		Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, finalGroupName));
		try {
			if (("y".equals(deleteMe)) && (ub.isSysAdmin())) {
				scheduler.deleteJob(trigger.getJobKey());
				// set return message here
				logger.debug("Deleted job: " + triggerName);
				addPageMessage(getResPage().getString("the_following_job") + " " + triggerName
						+ " " + getResPage().getString("corresponding_trigger_deleted"), request);
			} else {

				if (scheduler.getTriggerState(trigger.getKey()) == Trigger.TriggerState.PAUSED) {
					scheduler.resumeTrigger(trigger.getKey());
					logger.debug("Resuming trigger! " + triggerName + " " + finalGroupName);
					addPageMessage(getResPage().getString("this_trigger") + " " + triggerName
							+ " " + getResPage().getString("resumed_and_will_continue"), request);
				} else {
					scheduler.pauseTrigger(trigger.getKey());
					logger.debug("Pausing trigger! " + triggerName + " " + finalGroupName);
					addPageMessage(getResPage().getString("this_trigger") + " " + triggerName
							+ " " + getResPage().getString("has_been_paused"), request);
				}
			}
		} catch (NullPointerException e) {
		    logger.error("Error: " + e.getMessage());
			e.printStackTrace();
		}
		if ("".equals(gName) || "0".equals(gName)) {
			forwardPage(Page.VIEW_JOB_SERVLET, request, response);
		} else {
			forwardPage(Page.VIEW_IMPORT_JOB_SERVLET, request, response);
		}
	}

}
