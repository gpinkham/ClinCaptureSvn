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

package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 19, 2008 Time: 1:45:28 PM To change this template use File |
 * Settings | File Templates.
 */
@SuppressWarnings("serial")
public class ChooseDownloadFormat extends SecureController {
	protected void processRequest() throws Exception {
		// FormProcessor fp = new FormProcessor(request);
		String subjectId = request.getParameter("subjectId");
		request.setAttribute("subjectId", subjectId);
		String resolutionStatus = request.getParameter("resolutionStatus");
		request.setAttribute("resolutionStatus", resolutionStatus);
		String discNoteType = request.getParameter("discNoteType");
		request.setAttribute("discNoteType", discNoteType);
		// provide the study name or identifier
		String studyIdentifier = "";
		if (this.currentStudy != null) {
			studyIdentifier = currentStudy.getIdentifier();
		}
		request.setAttribute("studyIdentifier", studyIdentifier);
		forwardPage(Page.CHOOSE_DOWNLOAD_FORMAT);

	}

	protected void mayProceed() throws InsufficientPermissionException {

	}
}
