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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A type-safe enumeration class for resolution status of discrepancy notes.
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public final class ResolutionStatus extends Term {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ResolutionStatus.class.getName());

	public static final ResolutionStatus INVALID = new ResolutionStatus(0, "invalid", null, 1);

	public static final ResolutionStatus OPEN = new ResolutionStatus(1, "New", "images/icon_Note.gif", 2);

	public static final ResolutionStatus OPEN_DCF = new ResolutionStatus(6, "new_dcf", "images/icon_Note.gif", 3);

	public static final ResolutionStatus UPDATED = new ResolutionStatus(2, "Updated", "images/icon_flagYellow.gif", 4);

	public static final ResolutionStatus RESOLVED = new ResolutionStatus(3, "Resolution_Proposed",
			"images/icon_flagBlack.gif", 5);

	public static final ResolutionStatus CLOSED = new ResolutionStatus(4, "Closed", "images/icon_flagGreen.gif", 6);

	public static final ResolutionStatus NOT_APPLICABLE = new ResolutionStatus(5, "Not_Applicable",
			"images/icon_flagWhite.gif", 7);

	public static final List<ResolutionStatus> SIMPLE_LIST = Arrays.asList(OPEN, UPDATED, CLOSED);

	private static final ResolutionStatus[] MEMBERS = { OPEN, OPEN_DCF, UPDATED, RESOLVED, CLOSED, NOT_APPLICABLE };

	private static final List<ResolutionStatus> MEMBERS_TO_DISPLAY_DNS_STATISTICS
			= Arrays.asList(OPEN, UPDATED, RESOLVED, CLOSED, NOT_APPLICABLE);

	public static final List MEMBERS_LIST = Arrays.asList(MEMBERS);

	private String iconFilePath;

	private int displayPriority;

	private ResolutionStatus() {
	}

	private ResolutionStatus(int id, String name, String path, int displayPriority) {
		super(id, name);
		this.iconFilePath = path;
		this.displayPriority = displayPriority;
	}

	public boolean isInvalid() {
		return this == ResolutionStatus.INVALID;
	}

	public boolean isOpen() {
		return this == ResolutionStatus.OPEN;
	}

	public boolean isOpenWithDCF() {
		return this == ResolutionStatus.OPEN_DCF;
	}

	public boolean isClosed() {
		return this == ResolutionStatus.CLOSED;
	}

	public boolean isUpdated() {
		return this == ResolutionStatus.UPDATED;
	}

	public boolean isResolved() {
		return this == ResolutionStatus.RESOLVED;
	}

	public boolean isNotApplicable() {
		return this == ResolutionStatus.NOT_APPLICABLE;
	}

	public static ResolutionStatus[] getMembers() {
		return MEMBERS;
	}

	public static List<ResolutionStatus> getMembersForDisplayStatistics() {
		return MEMBERS_TO_DISPLAY_DNS_STATISTICS;
	}

	/**
	 * Determines if a resolution status with specified ID exists.
	 *
	 * @param id ID to search by
	 * @return <code>true</code> if the resolution status exists;
	 * <code>false</code> - otherwise
	 */
	public static boolean contains(int id) {
		return Term.contains(id, MEMBERS_LIST);
	}

	/**
	 * Returns a resolution status entity by its ID.
	 *
	 * @param id ID to search by
	 * @return resolution status entity; if a resolution status was not found - returns <code>null</code>
	 */
	public static ResolutionStatus get(int id) {
		Term term = Term.get(id, MEMBERS_LIST);
		return (term instanceof ResolutionStatus) ? (ResolutionStatus) term : null;
	}

	/**
	 * Returns a resolution status entity by its name.
	 *
	 * @param name resolution status name
	 * @return resolution status entity; if a resolution status was not found - returns the resolution status INVALID
	 */
	public static ResolutionStatus getByName(String name) {
		for (Object aList : MEMBERS_LIST) {
			ResolutionStatus temp = (ResolutionStatus) aList;
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return INVALID;
	}

	/**
	 * Returns full list of resolution statuses.
	 *
	 * @return full list of resolution statuses
	 */
	public static ArrayList toArrayList() {
		return new ArrayList(MEMBERS_LIST);
	}

	public String getIconFilePath() {
		return iconFilePath;
	}

	public void setIconFilePath(String iconFilePath) {
		this.iconFilePath = iconFilePath;
	}

	public int getDisplayPriority() {
		return displayPriority;
	}
}
