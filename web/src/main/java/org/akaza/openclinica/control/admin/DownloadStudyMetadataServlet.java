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
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.logic.odmExport.AdminDataCollector;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "serial" })
@Component
public class DownloadStudyMetadataServlet extends Controller {

	public static String STUDY_ID = "studyId";

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

		if (ub.isSysAdmin()) {
			return;
		}
		if (SubmitDataServlet.mayViewData(ub, currentRole)) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_admin"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		MetaDataCollector mdc = new MetaDataCollector(getDataSource(), currentStudy, getRuleSetRuleDao());
		AdminDataCollector adc = new AdminDataCollector(getDataSource(), currentStudy);
		MetaDataCollector.setTextLength(200);

		ODMBean odmb = mdc.getODMBean();
		odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 OpenClinica-ODM1-3-0-OC2-0.xsd");
		ArrayList<String> xmlnsList = new ArrayList<String>();
		xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"");
		// xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/openclinica_odm/v1.3\"");
		xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/odm_ext_v130/v3.1\"");
		xmlnsList.add("xmlns:OpenClinicaRules=\"http://www.openclinica.org/ns/rules/v3.1\"");
		odmb.setXmlnsList(xmlnsList);
		odmb.setODMVersion("oc1.3");
		mdc.setODMBean(odmb);
		adc.setOdmbean(odmb);
		mdc.collectFileData();
		adc.collectFileData();

		FullReportBean report = new FullReportBean();
		report.setAdminDataMap(adc.getOdmAdminDataMap());
		report.setOdmStudyMap(mdc.getOdmStudyMap());
		report.setCoreResources(getCoreResources());
		report.setOdmBean(mdc.getODMBean());
		report.setODMVersion("oc1.3");
		report.createStudyMetaOdmXml(Boolean.FALSE);

		request.setAttribute("generate", report.getXmlOutput().toString().trim());
		Page finalTarget = Page.EXPORT_DATA_CUSTOM;
		finalTarget.setFileName("/WEB-INF/jsp/extract/downloadStudyMetadata.jsp");
		forwardPage(finalTarget, request, response);
	}
}
