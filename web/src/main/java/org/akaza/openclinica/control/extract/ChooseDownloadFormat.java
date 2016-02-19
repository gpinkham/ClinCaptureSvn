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

package org.akaza.openclinica.control.extract;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 19, 2008 Time: 1:45:28 PM To change this template use File |
 * Settings | File Templates.
 */
@Component
public class ChooseDownloadFormat extends Controller {
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudyBean currentStudy = getCurrentStudy(request);
		// FormProcessor fp = new FormProcessor(request);
		String subjectId = request.getParameter("subjectId");
		request.setAttribute("subjectId", subjectId);
		String resolutionStatus = request.getParameter("resolutionStatus");
		request.setAttribute("resolutionStatus", resolutionStatus);
		String discNoteType = request.getParameter("discNoteType");
		request.setAttribute("discNoteType", discNoteType);
		// provide the study name or identifier
		String studyIdentifier = "";
		if (currentStudy != null) {
			studyIdentifier = currentStudy.getIdentifier();
		}
		request.setAttribute("studyIdentifier", studyIdentifier);
		forwardPage(Page.CHOOSE_DOWNLOAD_FORMAT, request, response);

	}

	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        //
	}
}
