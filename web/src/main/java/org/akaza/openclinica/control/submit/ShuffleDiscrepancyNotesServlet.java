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

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import java.util.*;

@SuppressWarnings({"unchecked", "serial"})
public class ShuffleDiscrepancyNotesServlet extends SecureController {

	public static final String ROW_PREFIX = "rp";
	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	@Override
	protected void processRequest() throws Exception {
		boolean shuffle = false;
		String rowPrefix = request.getParameter(ROW_PREFIX);
		int index = Integer.parseInt(rowPrefix.replaceAll(".*_manual", "").replaceAll("input.*", ""));
		FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) request.getSession().getAttribute(
				FORM_DISCREPANCY_NOTES_NAME);

		if (newNotes != null) {
			for (String k : new TreeSet<String>(newNotes.getFieldNotes().keySet())) {
				if (k.contains("_manual")) {
					int ind = Integer.parseInt(k.replaceAll(".*_manual", "").replaceAll("input.*", ""));
					if (ind > index) {
						shuffle = true;
						break;
					} else if (ind == index) {
						shuffle = true;
						index = ind;
						break;
					} else {
						continue;
					}
				}
			}
		}

		if (shuffle) {
			for (String k : new TreeSet<String>(newNotes.getFieldNotes().keySet())) {
				if (k.contains("_manual")) {
					int ind = Integer.parseInt(k.replaceAll(".*_manual", "").replaceAll("input.*", ""));
					if (ind == index) {
						newNotes.getFieldNotes().remove(k);
					} else if (ind > index) {
						String k2 = k.replace("_manual" + ind, "_manual" + (ind - 1));
						Object note = newNotes.getFieldNotes().remove(k);
						newNotes.getFieldNotes().put(k2, note);
					}
				}
			}
		}

		forwardPage(Page.SHUFFLE_DNS_PAGE);
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {
		//
	}
}
