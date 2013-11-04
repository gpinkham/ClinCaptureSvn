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
 * copyright 2008-2009 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Builds on top of PrintCRFServlet
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings({ "rawtypes" })
@Component
public class PrintCRFByIdServlet extends PrintCRFServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		return;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		StudyDAO studyDao = new StudyDAO(getDataSource());
		currentStudy = (StudyBean) studyDao.findByPK(1);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(getDataSource());
		if (request.getParameter("id") == null) {
			forwardPage(Page.LOGIN, request, response);
		}
		CRFVersionBean crfVersion = crfVersionDao.findByOid(request.getParameter("id"));
		request.setAttribute("study", currentStudy);
		if (crfVersion != null) {
			request.setAttribute("id", String.valueOf(crfVersion.getId()));
			super.processRequest(request, response);
		} else {
			forwardPage(Page.LOGIN, request, response);
		}
	}
}
