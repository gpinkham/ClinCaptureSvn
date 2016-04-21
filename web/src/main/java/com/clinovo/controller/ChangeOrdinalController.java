/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Controller
public class ChangeOrdinalController extends SpringController {

	@Autowired
	private DataSource datasource;

	public static final String USER_BEAN_NAME = "userBean";
	public static final String ERROR_PAGE = "redirect:/MainMenu?message=system_no_permission";
	public static final String EVENT_LIST_PAGE = "redirect:/ListEventDefinition";

	@RequestMapping("/changeDefinitionOrdinal")
	public String changeDefinitionOrdinalHandler(HttpServletRequest request, @RequestParam("current") int current, @RequestParam("previous") int previous) throws Exception {

		UserAccountBean ub = (UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);

		if (ub.isSysAdmin()) {
			increase(current, previous);
		} else {
			return ERROR_PAGE;
		}

		return EVENT_LIST_PAGE;
	}

	private void increase(int idCurrent, int idPrevious) {

		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(datasource);

		if (idCurrent > 0) {

			StudyEventDefinitionBean current = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(idCurrent);
			current.setOrdinal(current.getOrdinal() - 1);

			studyEventDefinitionDAO.update(current);
		}
		if (idPrevious > 0) {

			StudyEventDefinitionBean previous = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(idPrevious);
			previous.setOrdinal(previous.getOrdinal() + 1);

			studyEventDefinitionDAO.update(previous);
		}
	}
}
