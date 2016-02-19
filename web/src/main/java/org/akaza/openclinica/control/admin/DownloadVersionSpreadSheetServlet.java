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

/* OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

@Component
public class DownloadVersionSpreadSheetServlet extends Controller {

	public static String CRF_ID = "crfId";
	public static String CRF_VERSION_ID = "crfVersionId";
	public static String CRF_VERSION_TEMPLATE = "CRF_Design_Template_v3.1.xls";
	public static String RANDOMIZATION_CRF_TEMPLATE = "Randomization_Form_v1.0.xls";

	/**
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

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				getResException().getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dir = SQLInitServlet.getField("filePath") + "crf" + File.separator + "new" + File.separator;
		FormProcessor fp = new FormProcessor(request);

		String crfIdString = fp.getString(CRF_ID);
		int crfVersionId = fp.getInt(CRF_VERSION_ID);

		CRFVersionDAO cvdao = getCRFVersionDAO();

		CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(crfVersionId);

		boolean isTemplate = fp.getBoolean("template");

		String excelFileName = crfIdString + version.getOid() + ".xls";

		File excelFile = null;
		String oldExcelFileName = crfIdString + version.getName() + ".xls";
		if (isTemplate) {

			String templateId = request.getParameter("template");

			// Blank CRF template
			if ("1".equals(templateId)) {

				excelFileName = CRF_VERSION_TEMPLATE;
				excelFile = getCoreResources().getFile(CRF_VERSION_TEMPLATE,
						"crf" + File.separator + "original" + File.separator);

			} else if ("2".equals(templateId)) {

				// Randomization CRF template
				excelFileName = RANDOMIZATION_CRF_TEMPLATE;
				excelFile = getCoreResources().getFile(RANDOMIZATION_CRF_TEMPLATE,
						"crf" + File.separator + "original" + File.separator);

			}

		} else {

			excelFile = new File(dir + excelFileName);
			// backwards compat
			File oldExcelFile = new File(dir + oldExcelFileName);
			if (oldExcelFile.exists() && oldExcelFile.length() > 0) {
				if (!excelFile.exists() || excelFile.length() <= 0) {
					// if the old name exists and the new name does not...
					excelFile = oldExcelFile;
					excelFileName = oldExcelFileName;
				}
			}

		}

		logger.info("looking for : " + (excelFile != null ? excelFile.getName() : null));
		if (excelFile == null || !excelFile.exists() || excelFile.length() <= 0) {
			addPageMessage(getResPage().getString("the_excel_is_not_available_on_server_contact"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\"" + excelFileName + "\";");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Pragma", "public");

			ServletOutputStream op = response.getOutputStream();
			DataInputStream in = null;
			try {
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Pragma", "public");
				response.setContentLength((int) excelFile.length());

				byte[] bbuf = new byte[(int) excelFile.length()];
				in = new DataInputStream(new FileInputStream(excelFile));
				int length;
				while (((length = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, length);
				}

				in.close();
				op.flush();
				op.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			} finally {
				if (in != null) {
					in.close();
				}
				if (op != null) {
					op.close();
				}
			}
		}

	}
}
