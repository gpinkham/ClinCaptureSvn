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

package org.akaza.openclinica.controller;

import org.springframework.stereotype.Component;

/**
 * This class represents the state of a sidebar in decorator.jsp. For example, if the Alerts/Messages should be
 * initially displayed as open, then the alertsBoxSetup property would be set to SidebarEnumConstants.OPENALERTS; if the
 * Icon Keys box is disabled for a certain display, then the iconsBoxSetup should be set to
 * SidebarEnumConstants.DISABLEICONS. These values are typically configured in a Spring bean. Date: Jan 14, 2009
 * 
 * @see SidebarEnumConstants
 */
@Component
public class SidebarInit {
	private SidebarEnumConstants alertsBoxSetup;
	private SidebarEnumConstants infoBoxSetup;
	private SidebarEnumConstants instructionsBoxSetup;
	private SidebarEnumConstants enableIconsBoxSetup;
	private SidebarEnumConstants iconsBoxSetup;

	public SidebarEnumConstants getInfoBoxSetup() {
		return infoBoxSetup;
	}

	public void setInfoBoxSetup(SidebarEnumConstants infoBoxSetup) {
		this.infoBoxSetup = infoBoxSetup;
	}

	public SidebarEnumConstants getInstructionsBoxSetup() {
		return instructionsBoxSetup;
	}

	public void setInstructionsBoxSetup(SidebarEnumConstants instructionsBoxSetup) {
		this.instructionsBoxSetup = instructionsBoxSetup;
	}

	public SidebarEnumConstants getEnableIconsBoxSetup() {
		return enableIconsBoxSetup;
	}

	public void setEnableIconsBoxSetup(SidebarEnumConstants enableIconsBoxSetup) {
		this.enableIconsBoxSetup = enableIconsBoxSetup;
	}

	public SidebarEnumConstants getIconsBoxSetup() {
		return iconsBoxSetup;
	}

	public void setIconsBoxSetup(SidebarEnumConstants iconsBoxSetup) {
		this.iconsBoxSetup = iconsBoxSetup;
	}

	public SidebarEnumConstants getAlertsBoxSetup() {
		return alertsBoxSetup;
	}

	public void setAlertsBoxSetup(SidebarEnumConstants alertsBoxSetup) {
		this.alertsBoxSetup = alertsBoxSetup;
	}
}
