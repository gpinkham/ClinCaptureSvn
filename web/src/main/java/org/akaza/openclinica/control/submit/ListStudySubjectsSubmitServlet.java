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

/*
 * Created on Jan 21, 2005
 */
package org.akaza.openclinica.control.submit;

import java.util.Locale;

import org.akaza.openclinica.control.managestudy.ListStudySubjectServlet;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({ "serial" })
public class ListStudySubjectsSubmitServlet extends ListStudySubjectServlet {

	Locale locale;

	@Override
	protected Page getJSP() {
		return Page.SUBMIT_DATA;
	}

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();
		if (ub.isSysAdmin()) {
			return;
		}

		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	protected String getBaseURL() {
		return "ListStudySubjectsSubmit";
	}
}
