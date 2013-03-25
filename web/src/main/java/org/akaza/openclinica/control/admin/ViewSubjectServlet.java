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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ViewSubjectServlet extends SecureController {
	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest() throws Exception {

		SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
		FormProcessor fp = new FormProcessor(request);
		int subjectId = fp.getInt("id");

		if (subjectId == 0) {
			addPageMessage(respage.getString("please_choose_a_subject_to_view"));
			forwardPage(Page.SUBJECT_LIST_SERVLET);
		} else {
			SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

			// find all study subjects
			StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
			ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

			request.setAttribute("subject", subject);
			request.setAttribute("studySubs", studySubs);
			forwardPage(Page.VIEW_SUBJECT);

		}

	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
