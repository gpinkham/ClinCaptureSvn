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

package org.akaza.openclinica.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A simple example of an annotated Spring Controller. Notice that it is a POJO; it does not implement any Spring
 * interfaces or extend Spring classes.
 */
@Controller("userController")
public class UserController {
	// Autowire the class that handles the sidebar structure with a configured
	// bean named "sidebarInit"
	@Autowired
	@Qualifier("sidebarInit")
	private SidebarInit sidebarInit;

	public UserController() {
	}

	/**
	 * The method is mapped to the URL /user.htm
	 * 
	 * @param request
	 *            The HttpServletRequest for storing attributes.
	 * @param userId
	 *            The id of the user.
	 * @return The return value is a ModelMap (instead of ModelAndView object), because the view name automatically
	 *         resolves to "user"
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping("/user")
	public ModelMap userHandler(HttpServletRequest request, @RequestParam("id") int userId) {
		ModelMap map = new ModelMap();
		List<String> userList = new ArrayList<String>();

		// set up request attributes for sidebar
		setUpSidebar(request);

		userList.add("Bruce");
		userList.add("Yufang");
		userList.add("Krikor");
		userList.add("Tom");

		// TODO: Get user from Hibernate DAO class
		// userList.add(userDao.loadUser(userId).getName())
		map.addObject(userList);
		return map;
	}

	private void setUpSidebar(HttpServletRequest request) {
		if (sidebarInit.getAlertsBoxSetup() == SidebarEnumConstants.OPENALERTS) {
			request.setAttribute("alertsBoxSetup", true);
		}

		if (sidebarInit.getInfoBoxSetup() == SidebarEnumConstants.OPENINFO) {
			request.setAttribute("infoBoxSetup", true);
		}
		if (sidebarInit.getInstructionsBoxSetup() == SidebarEnumConstants.OPENINSTRUCTIONS) {
			request.setAttribute("instructionsBoxSetup", true);
		}

		if (!(sidebarInit.getEnableIconsBoxSetup() == SidebarEnumConstants.DISABLEICONS)) {
			request.setAttribute("enableIconsBoxSetup", true);
		}

	}

	public SidebarInit getSidebarInit() {
		return sidebarInit;
	}

	public void setSidebarInit(SidebarInit sidebarInit) {
		this.sidebarInit = sidebarInit;
	}
}
