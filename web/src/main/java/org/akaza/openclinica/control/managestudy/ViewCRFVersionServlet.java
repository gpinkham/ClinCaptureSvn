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
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * ViewCRFVersionServlet.
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@Component
public class ViewCRFVersionServlet extends Controller {

	/**
	 * Checks whether the user has the right permission to proceed function.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws InsufficientPermissionException
	 *             the InsufficientPermissionException
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
				respage.getString("no_have_correct_privilege_current_study") + " "
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ItemDAO idao = getItemDAO();
		EventCRFDAO eventCrfDao = getEventCRFDAO();
		ItemDataDAO itemDataDao = getItemDataDAO();
		StudyEventDAO studyEventDao = getStudyEventDAO();
		CRFVersionDAO crfVersionDao = getCRFVersionDAO();
		ItemFormMetadataDAO ifmdao = getItemFormMetadataDAO();
		EventDefinitionCRFDAO eventDefinitionCrfDao = getEventDefinitionCRFDAO();

		FormProcessor fp = new FormProcessor(request);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		if (request.getMethod().equalsIgnoreCase("post")) {
			boolean dataChanged = false;
			boolean changeEventCrfs = false;
			int totalItems = fp.getInt("totalItems");
			for (int i = 1; i <= totalItems; i++) {
				int itemFormMetaId = fp.getInt("itemFormMetaId_".concat(Integer.toString(i)));
				boolean sdvRequired = fp.getInt("sdvRequired_".concat(Integer.toString(i))) == 1;
				ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) ifmdao.findByPK(itemFormMetaId);
				if (itemFormMetadataBean.isSdvRequired() != sdvRequired) {
					dataChanged = true;
					if (!itemFormMetadataBean.isSdvRequired()) {
						changeEventCrfs = true;
					}
				}
				itemFormMetadataBean.setSdvRequired(sdvRequired);
				ifmdao.update(itemFormMetadataBean);
			}
			if (dataChanged) {
				int crfVersionId = fp.getInt("crfVersionId");
				eventDefinitionCrfDao.updateEDCThatHasItemsToSDV(crfVersionId, SourceDataVerification.PARTIALREQUIRED);
				itemDataDao.updateItemDataSDVWhenCRFMetadataWasChanged(crfVersionId);
				if (changeEventCrfs) {
					eventCrfDao.updateEventCRFSDVWhenCRFMetadataWasChanged(crfVersionId);
					SubjectEventStatusUtil.determineSubjectEventStates(studyEventDao
							.findAllSDVStudyEvents(crfVersionId), new DAOWrapper(getStudyDAO(), crfVersionDao,
							studyEventDao, getStudySubjectDAO(), getEventCRFDAO(), getEventDefinitionCRFDAO(),
							getDiscrepancyNoteDAO()), null);
				}
			}
			addPageMessage(respage.getString("data_was_saved_successfully"), request);
			storePageMessages(request);
			response.sendRedirect(request.getContextPath().concat("/ViewCRF?crfId=")
					.concat(request.getParameter("crfId")));
		} else {
			int crfVersionId = fp.getInt("id");

			if (crfVersionId == 0) {
				addPageMessage(respage.getString("please_choose_a_crf_to_view_details"), request);
				forwardPage(Page.CRF_LIST_SERVLET, request, response);
			} else {
				CRFVersionBean version = (CRFVersionBean) crfVersionDao.findByPK(crfVersionId);
				// tbh
				CRFDAO crfdao = new CRFDAO(getDataSource());
				CRFBean crf = (CRFBean) crfdao.findByPK(version.getCrfId());
				// tbh, 102007
				SectionDAO sdao = new SectionDAO(getDataSource());
				ItemGroupDAO igdao = new ItemGroupDAO(getDataSource());
				ItemGroupMetadataDAO igmdao = new ItemGroupMetadataDAO(getDataSource());
				ArrayList<SectionBean> sections = (ArrayList<SectionBean>) sdao.findByVersionId(version.getId());
				HashMap versionMap = new HashMap();
				for (SectionBean section : sections) {
					versionMap.put(section.getId(), section.getItems());
					// YW 08-21-2007, add group metadata
					ArrayList<ItemGroupBean> igs = (ArrayList<ItemGroupBean>) igdao.findGroupBySectionId(section
							.getId());
					for (ItemGroupBean ig : igs) {
						ArrayList<ItemGroupMetadataBean> igms = (ArrayList<ItemGroupMetadataBean>) igmdao
								.findMetaByGroupAndSection(ig.getId(), section.getCRFVersionId(), section.getId());
						if (!igms.isEmpty()) {
							// Note, the following logic has been adapted here -
							// "for a given crf version,
							// all the items in the same group have the same group
							// metadata
							// so we can get one of them and set metadata for the
							// group"
							ig.setMeta(igms.get(0));
							ig.setItemGroupMetaBeans(igms);
						}
					}
					section.setGroups(igs);
					// YW >>
				}
				ArrayList<ItemBean> items = idao.findAllItemsByVersionId(version.getId());
				// YW 08-22-2007, if this crf_version_id doesn't exist in
				// item_group_metadata table,
				// items in this crf_version will not exist in item_group_metadata,
				// then different query will be used
				if (igmdao.versionIncluded(crfVersionId)) {
					for (ItemBean item : items) {
						ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(item.getId(), version.getId());

						item.setItemMeta(ifm);
						// logger.info("option******" +
						// ifm.getResponseSet().getOptions().size());
						ArrayList its = (ArrayList) versionMap.get(new Integer(ifm.getSectionId()));
						its.add(item);
					}
				} else {
					for (ItemBean item : items) {
						ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionIdNotInIGM(item.getId(),
								version.getId());

						item.setItemMeta(ifm);
						// logger.info("option******" +
						// ifm.getResponseSet().getOptions().size());
						ArrayList its = (ArrayList) versionMap.get(new Integer(ifm.getSectionId()));
						its.add(item);
					}
				}

				for (SectionBean section : sections) {
					section.setItems((ArrayList) versionMap.get(new Integer(section.getId())));
				}
				request.setAttribute("sections", sections);
				request.setAttribute("version", version);
				request.setAttribute("crfname", crf.getName());
				request.setAttribute("crf", crf);

				forwardPage(Page.VIEW_CRF_VERSION, request, response);

			}
		}
	}

}
