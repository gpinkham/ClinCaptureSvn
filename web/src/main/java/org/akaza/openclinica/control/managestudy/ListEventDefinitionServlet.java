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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.StudyEventDefinitionRow;

/**
 * Processes user reuqest to generate study event definition list
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked",  "serial"})
public class ListEventDefinitionServlet extends SecureController {

	Locale locale;

	/**
	 * Checks whether the user has the correct privilege
	 */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		session.removeAttribute("tmpCRFIdMap");
		session.removeAttribute("crfsWithVersion");
		session.removeAttribute("eventDefinitionCRFs");

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	/**
	 * Processes the request
	 */
	@Override
	public void processRequest() throws Exception {

		StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
		CRFDAO crfDao = new CRFDAO(sm.getDataSource());
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(sm.getDataSource());
		ArrayList seds = edao.findAllByStudy(currentStudy);

		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		for (int i = 0; i < seds.size(); i++) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
			Collection eventDefinitionCRFlist = edcdao.findAllParentsByDefinition(sed.getId());
			Map crfWithDefaultVersion = new LinkedHashMap();
			for (Iterator it = eventDefinitionCRFlist.iterator(); it.hasNext();) {
				// FIXME can this be reduced to a non - N^2 loop?
				EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) it.next();
				CRFBean crfBean = (CRFBean) crfDao.findByPK(edcBean.getCrfId());
				CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(edcBean.getDefaultVersionId());
				logger.info("ED[" + sed.getName() + "]crf[" + crfBean.getName() + "]dv[" + crfVersionBean.getName()
						+ "]");
				crfWithDefaultVersion.put(crfBean.getName(), crfVersionBean.getName());
			}
			sed.setCrfsWithDefaultVersion(crfWithDefaultVersion);
			logger.info("CRF size [" + sed.getCrfs().size() + "]");
			if (sed.getUpdater().getId() == 0) {
				sed.setUpdater(sed.getOwner());
				sed.setUpdatedDate(sed.getCreatedDate());
			}
			if (isPopulated(sed, sedao)) {
				sed.setPopulated(true);
			}
		}

		FormProcessor fp = new FormProcessor(request);
		EntityBeanTable table = fp.getEntityBeanTable();
		ArrayList allStudyRows = StudyEventDefinitionRow.generateRowsFromBeans(seds);

		String[] columns = { resword.getString("order"), resword.getString("name"), resword.getString("OID"),
				resword.getString("repeating"), resword.getString("type"), resword.getString("category"),
				resword.getString("populated"), resword.getString("date_created"), resword.getString("date_updated"),
				resword.getString("CRFs"), resword.getString("default_version"), resword.getString("actions") };
		table.setColumns(new ArrayList(Arrays.asList(columns)));
		// >> tbh #4169 09/2009
		table.hideColumnLink(2);
		table.hideColumnLink(3);
		table.hideColumnLink(4);
		table.hideColumnLink(6);
		table.hideColumnLink(7);
		table.hideColumnLink(8);
		table.hideColumnLink(9);
		table.hideColumnLink(10); // crfs, tbh
		table.hideColumnLink(11);
		table.hideColumnLink(12);
		table.setQuery("ListEventDefinition", new HashMap());

		table.setRows(allStudyRows);

		table.setPaginated(false);
		table.computeDisplay();

		request.setAttribute("table", table);
		request.setAttribute("defSize", new Integer(seds.size()));

		if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
			request.setAttribute("readOnly", true);
		}

		forwardPage(Page.STUDY_EVENT_DEFINITION_LIST);
	}

	/**
	 * Checked whether a definition is available to be locked
	 * 
	 * @param sed
	 * @return
	 */
	private boolean isPopulated(StudyEventDefinitionBean sed, StudyEventDAO sedao) {
		if (sedao.countNotRemovedEvents(sed.getId()) > 0) {
			return true;
		} else {
			return false;
		}
	}

}
