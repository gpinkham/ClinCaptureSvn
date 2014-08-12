/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * AccessFileServlet class.
 */
@SuppressWarnings({ "serial" })
@Component
public class AccessFileServlet extends Controller {

	/**
	 * Method builds link to download a file by file id.
	 * 
	 * @param fId
	 *            file id
	 * @return String
	 */
	public String getLink(int fId) {
		return "AccessFile?fileId=" + fId;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request.getSession().getAttribute("redirectAfterLogin") != null) {
			response.sendRedirect(request.getContextPath() + Page.MENU_SERVLET.getFileName());
			return;
		}
		FormProcessor fp = new FormProcessor(request);
		int fileId = fp.getInt("fileId");
		ArchivedDatasetFileDAO asdfdao = getArchivedDatasetFileDAO();
		DatasetDAO dsDao = getDatasetDAO();
		ArchivedDatasetFileBean asdfBean = (ArchivedDatasetFileBean) asdfdao.findByPK(fileId);
		StudyDAO studyDao = getStudyDAO();
		DatasetBean dsBean = (DatasetBean) dsDao.findByPK(asdfBean.getDatasetId());
		StudyBean currentStudy = getCurrentStudy(request);
		int parentId = currentStudy.getParentStudyId();
		if (parentId == 0) {
			// Logged in at study level
			StudyBean studyBean = (StudyBean) studyDao.findByPK(dsBean.getStudyId());
			parentId = studyBean.getParentStudyId();
			// parent id of dataset created

		}
		// logic: is parentId of the dataset created not equal to currentstudy? or is current study
		if ((parentId) != currentStudy.getId()) {
			if (dsBean.getStudyId() != currentStudy.getId()) {
				addPageMessage(
						respage.getString("no_have_correct_privilege_current_study")
								+ respage.getString("change_study_contact_sysadmin"), request);
				throw new InsufficientPermissionException(Page.MENU_SERVLET,
						resexception.getString("not_allowed_access_extract_data_servlet"), "1");
			}
		}
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
		request.setAttribute("generate", asdfBean.getFileReference());
		writeFile(request, response);
	}

	private void writeFile(HttpServletRequest request, HttpServletResponse response) {
		String path = (String) request.getAttribute("generate");
		System.out.println("file path found at jsp " + path);
		if (path != null) {
			ServletOutputStream sos = null;
			BufferedOutputStream bos = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				if (!path.endsWith(".html")) {
					response.setContentType("application/download");
				}
				response.setHeader("Pragma", "public");
				sos = response.getOutputStream();

				bos = new BufferedOutputStream(sos);
				java.io.File local = new java.io.File(path);
				is = new FileInputStream(local);
				bis = new BufferedInputStream(is);
				int length = (int) local.length();
				int bytesRead;
				byte[] buff = new byte[length];

				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} catch (Exception ee) {
				logger.error("Failed downloading dataset file in the AccessFileServlet.java: 135", ee);
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException ex) {
						logger.error(
								"Failed to close instance of BufferedInputStream in the AccessFileServlet.java: 141",
								ex);
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException ex) {
						logger.error("Failed to close instance of FileInputStream in the AccessFileServlet.java: 148",
								ex);
					}
				}
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException ex) {
						logger.error(
								"Failed to close instance of BufferedOutputStream in the AccessFileServlet.java: 155",
								ex);
					}
				}
				if (sos != null) {
					try {
						sos.flush();
						sos.close();
					} catch (IOException ex) {
						logger.error(
								"Failed to close instance of ServletOutputStream in the AccessFileServlet.java: 163",
								ex);
					}
				}
			}
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR) || currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.STUDY_MONITOR)) {
			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

}
