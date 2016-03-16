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

package org.akaza.openclinica.control.submit;

import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Is called when a row is being deleted from a repeating group by the user.
 * Deletes DNs from the session for removed repeating row
 * and reassigns DNs, created for rows with higher ordinal (if there are any), to the correct HTML input IDs
 * (row number in the HTML input ID is decreased for rows with higher ordinal, after a row is deleted from the middle).
 */
@Component
public class ShuffleDiscrepancyNotesServlet extends SpringServlet {

	public static final String ROW_PREFIX = "rp";
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		boolean shuffle = false;
		String rowPrefix = request.getParameter(ROW_PREFIX);
		int index = Integer.parseInt(rowPrefix.replaceAll(".*_", "").replaceAll("input.*", ""));
		FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) request.getSession()
				.getAttribute(FORM_DISCREPANCY_NOTES_NAME);

		if (newNotes != null) {
			for (String k : new TreeSet<String>(newNotes.getFieldNotes().keySet())) {
				int ind = Integer.parseInt(k.replaceAll(".*_", "").replaceAll("input.*", ""));
				if (ind >= index) {
					shuffle = true;
					break;
				}
			}
		}

		if (shuffle) {
			String splitter = "_";
			for (String k : new TreeSet<String>(newNotes.getFieldNotes().keySet())) {
				int ind = Integer.parseInt(k.replaceAll(".*_", "").replaceAll("input.*", ""));
				if (ind == index) {
					newNotes.getFieldNotes().remove(k);
				} else if (ind > index) {
					String[] fieldIDSplitted = k.split(splitter);
					String inputID = fieldIDSplitted[fieldIDSplitted.length - 1].replaceFirst("\\d+", "");
					fieldIDSplitted[fieldIDSplitted.length - 1] = Integer.toString(ind - 1).concat(inputID);
					String k2 = StringUtils.join(fieldIDSplitted, splitter);
					List<DiscrepancyNoteBean> noteList = newNotes.getFieldNotes().remove(k);
					for (DiscrepancyNoteBean dn : noteList) {
						dn.setField(k2);
					}
					newNotes.getFieldNotes().put(k2, noteList);
				}
			}
		}
		forwardPage(Page.SHUFFLE_DNS_PAGE, request, response);
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}
}
