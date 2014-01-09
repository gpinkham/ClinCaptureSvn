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
package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Prepares to process request of updating a study object
 * 
 * @author jxu
 */
@SuppressWarnings({ "serial" })
@Component
public class InitUpdateStudyServlet extends Controller {

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	/**
	 * Processes the request
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyDAO sdao = getStudyDAO();
		String idString = request.getParameter("id");
		logger.info("study id:" + idString);
		if (StringUtil.isBlank(idString)) {
			addPageMessage(respage.getString("please_choose_a_study_to_edit"), request);
			forwardPage(Page.STUDY_LIST_SERVLET, request, response);
		} else {
			int studyId = Integer.valueOf(idString.trim());
			StudyBean study = (StudyBean) sdao.findByPK(studyId);
			StudyConfigService scs = getStudyConfigService();
			study = scs.setParametersForStudy(study);

			logger.info("date created:" + study.getCreatedDate());
			logger.info("protocol Type:" + study.getProtocolType());

			request.getSession().setAttribute("newStudy", study);
			request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
			request.setAttribute("statuses", Status.toActiveArrayList());

			StudyInfoPanel panel = getStudyInfoPanel(request);
			panel.reset();
			panel.setStudyInfoShown(false);
			panel.setOrderedData(true);
			panel.setExtractData(false);
			panel.setSubmitDataModule(false);
			panel.setCreateDataset(false);
			panel.setIconInfoShown(true);
			panel.setManageSubject(false);

			forwardPage(Page.UPDATE_STUDY1, request, response);
		}

	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}

}
