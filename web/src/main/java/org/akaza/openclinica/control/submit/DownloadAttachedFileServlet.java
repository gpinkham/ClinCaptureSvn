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
package org.akaza.openclinica.control.submit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({"unchecked", "serial"})
@Component
public class DownloadAttachedFileServlet extends Controller {

	/**
	 * Checks whether the user has the correct privilege
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt("eventCRFId");
		EventCRFDAO edao = getEventCRFDAO();

		if (eventCRFId > 0) {
			if (!entityIncluded(eventCRFId, ub.getName(), edao)) {
				request.setAttribute("downloadStatus", "false");
				addPageMessage(getResPage().getString("you_not_have_permission_download_attached_file"), request);
				throw new InsufficientPermissionException(Page.DOWNLOAD_ATTACHED_FILE,
						getResException().getString("no_permission"), "1");
			}
		} else {
			request.setAttribute("downloadStatus", "false");
			addPageMessage(getResPage().getString("you_not_have_permission_download_attached_file"), request);
			throw new InsufficientPermissionException(Page.DOWNLOAD_ATTACHED_FILE,
					getResException().getString("no_permission"), "1");
		}

		if (ub.isSysAdmin()) {
			return;
		}
		if (mayViewData(ub, currentRole)) {
			return;
		}

		request.setAttribute("downloadStatus", "false");
		addPageMessage(getResPage().getString("you_not_have_permission_download_attached_file"), request);
		throw new InsufficientPermissionException(Page.DOWNLOAD_ATTACHED_FILE, getResException().getString("no_permission"),
				"1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);
		String filePathName = "";
		String fileNameOid = "";
		String fileName = fp.getString("fileName");
		File f = new File(fileName);
		if (fileName.length() > 0) {
			StudyBean parentStudy = getParentStudy();
			String testPath = Utils.getAttachedFileRootPath();
			String tail = File.separator + f.getName();
			String testName = testPath + parentStudy.getOid() + tail;
			File temp = new File(testName);
			if (temp.exists()) {
				filePathName = testName;
				logger.info(currentStudy.getName() + " existing filePathName=" + filePathName);
				fileNameOid = currentStudy.getOid();
			} else {
				if (currentStudy.isSite()) {
					String testOid = currentStudy.getOid();
					testName = testPath + testOid + tail;
					temp = new File(testName);
					if (temp.exists()) {
						filePathName = testName;
						logger.info("parent existing filePathName=" + filePathName);
						fileNameOid = testOid;

					}
				} else {
					ArrayList<StudyBean> sites = (ArrayList<StudyBean>) getStudyDAO().findAllByParent(
							currentStudy.getId());
					for (StudyBean s : sites) {
						testPath = Utils.getAttachedFilePath(s);
						testName = testPath + tail;
						File test = new File(testName);
						if (test.exists()) {
							filePathName = testName;
							logger.info("site of currentStudy existing filePathName=" + filePathName);
							fileNameOid = s.getOid();
							break;
						}
					}
				}
			}
		}
		logger.info("filePathName = " + filePathName + " fileName = " + fileName);
		File file = new File(filePathName);
		String realName = file.getName();
		logger.info("realName = " + realName);
		if (!file.exists() || file.length() <= 0) {
			addPageMessage(
					getResTerm().getString("file_upper_case") + " " + filePathName + " " + getResPage().getString("not_exist"),
					request);
		} else {
			// response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileNameOid + realName + "\";");
			response.setHeader("Pragma", "public");

			ServletOutputStream outStream = response.getOutputStream();
			DataInputStream inStream = null;
			try {
				response.setContentType("application/download");
				response.setHeader("Cache-Control", "max-age=0");
				response.setContentLength((int) file.length());

				byte[] bbuf = new byte[(int) file.length()];
				inStream = new DataInputStream(new FileInputStream(file));
				int length;
				while ((length = inStream.read(bbuf)) != -1) {
					outStream.write(bbuf, 0, length);
				}

				inStream.close();
				outStream.flush();
				outStream.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			} finally {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}
		}
	}

}
