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
package org.akaza.openclinica.control.extract;

import java.util.Locale;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

@SuppressWarnings({ "rawtypes", "serial" })
public class AccessFileServlet extends SecureController {

	Locale locale;

	public static String getLink(int fId) {
		return "AccessFile?fileId=" + fId;
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int fileId = fp.getInt("fileId");
		ArchivedDatasetFileDAO asdfdao = new ArchivedDatasetFileDAO(sm.getDataSource());
		DatasetDAO dsDao = new DatasetDAO(sm.getDataSource());
		ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fileId);
		StudyDAO studyDao = new StudyDAO(sm.getDataSource());
		DatasetBean dsBean = (DatasetBean) dsDao.findByPK(asdfBean.getDatasetId());
		int parentId = currentStudy.getParentStudyId();
		if (parentId == 0)// Logged in at study level
		{
			StudyBean studyBean = (StudyBean) studyDao.findByPK(dsBean.getStudyId());
			parentId = studyBean.getParentStudyId();// parent id of dataset created

		}
		// logic: is parentId of the dataset created not equal to currentstudy? or is current study
		if ((parentId) != currentStudy.getId())
			if (dsBean.getStudyId() != currentStudy.getId()) {
				addPageMessage(respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"));
				throw new InsufficientPermissionException(Page.MENU_SERVLET,
						resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
			}

		Page finalTarget = Page.EXPORT_DATA_CUSTOM;
		
		if (asdfBean.getFileReference().endsWith(".zip")) {
			response.setHeader("Content-disposition", "attachment; filename=\"" + asdfBean.getName() + "\";");
			response.setContentType("application/zip");
			// response.setContentType("application/download");
		} else if (asdfBean.getFileReference().endsWith(".pdf")) {
			response.setHeader("Content-disposition", "attachment; filename=\"" + asdfBean.getName() + "\";");
			response.setContentType("application/pdf");
			// response.setContentType("application/download; application/pdf");
		} else if (asdfBean.getFileReference().endsWith(".csv")) {
			response.setHeader("Content-disposition", "attachment; filename=\"" + asdfBean.getName() + "\";");
			response.setContentType("text/csv");
			// response.setContentType("application/download; text/csv");
		} else if (asdfBean.getFileReference().endsWith(".xml")) {
			response.setHeader("Content-disposition", "attachment; filename=\"" + asdfBean.getName() + "\";");
			response.setContentType("text/xml");
			// response.setContentType("application/download; text/xml");
		} else if (asdfBean.getFileReference().endsWith(".html")) {
			response.setHeader("Content-disposition", "filename=\"" + asdfBean.getName() + "\";");
			response.setContentType("text/html; charset=utf-8");
		} 

		System.out.println("just set content type: " + response.getContentType());
		finalTarget.setFileName("/WEB-INF/jsp/extract/generatedFileDataset.jsp");
		request.setAttribute("generate", asdfBean.getFileReference());
		response.setHeader("Pragma", "public");
		forwardPage(finalTarget);
	}

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

}
