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
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.TableOfContentsServlet;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

/**
 * To view the table of content of an event CRF
 * 
 * @author jxu
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class ViewTableOfContentServlet extends SecureController {
	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " "
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int crfVersionId = fp.getInt("crfVersionId");
		// YW <<
		int sedId = fp.getInt("sedId");
		request.setAttribute("sedId", new Integer(sedId) + "");
		// YW >>
		DisplayTableOfContentsBean displayBean = getDisplayBean(sm.getDataSource(), crfVersionId);
		request.setAttribute("toc", displayBean);
		forwardPage(Page.VIEW_TABLE_OF_CONTENT);
	}

	public static DisplayTableOfContentsBean getDisplayBean(DataSource ds, int crfVersionId) {
		DisplayTableOfContentsBean answer = new DisplayTableOfContentsBean();

		ArrayList sections = getSections(crfVersionId, ds);
		answer.setSections(sections);

		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
		answer.setCrfVersion(cvb);

		CRFDAO cdao = new CRFDAO(ds);
		CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
		answer.setCrf(cb);

		answer.setEventCRF(new EventCRFBean());

		answer.setStudyEventDefinition(new StudyEventDefinitionBean());

		return answer;
	}

	public static ArrayList getSections(int crfVersionId, DataSource ds) {
		SectionDAO sdao = new SectionDAO(ds);

		HashMap numItemsBySectionId = sdao.getNumItemsBySectionId();
		ArrayList sections = sdao.findAllByCRFVersionId(crfVersionId);

		for (int i = 0; i < sections.size(); i++) {
			SectionBean sb = (SectionBean) sections.get(i);

			int sectionId = sb.getId();
			Integer key = new Integer(sectionId);
			sb.setNumItems(TableOfContentsServlet.getIntById(numItemsBySectionId, key));
			sections.set(i, sb);
		}

		return sections;
	}

}
