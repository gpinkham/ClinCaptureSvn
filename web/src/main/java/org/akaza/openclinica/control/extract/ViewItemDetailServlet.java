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
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * View all related metadata for an item
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
@Component
public class ViewItemDetailServlet extends Controller {

	public static String ITEM_ID = "itemId";
	public static String ITEM_OID = "itemOid";
	public static String ITEM_BEAN = "item";
	public static String VERSION_ITEMS = "versionItems";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        // StudyBean currentStudy = getCurrentStudy(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (mayViewData(ub, currentRole)) {
			return;
		}
		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int itemId = fp.getInt(ITEM_ID);
		String itemOid = fp.getString(ITEM_OID);
		ItemDAO idao = getItemDAO();
		ItemFormMetadataDAO ifmdao = getItemFormMetadataDAO();
		ItemGroupMetadataDAO igmdao = getItemGroupMetadataDAO();
		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFDAO cdao = getCRFDAO();
		SectionDAO sectionDao = getSectionDAO();

		if (itemId == 0 && itemOid == null) {
			addPageMessage(respage.getString("please_choose_an_item_first"), request);
			forwardPage(Page.ITEM_DETAIL, request, response);
			return;
		}
		ItemBean item = itemId > 0 ? (ItemBean) idao.findByPK(itemId) : (ItemBean) idao.findByOid(itemOid).get(0);
		ArrayList versions = idao.findAllVersionsByItemId(item.getId());
		ArrayList versionItems = new ArrayList();
		CRFBean crf = null;
		ItemFormMetadataBean imfBean = null;
		// finds each item metadata for each version
		for (int i = 0; i < versions.size(); i++) {
			Integer versionId = (Integer) versions.get(i);
			CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId.intValue());
			if (versionId != null && versionId.intValue() > 0) {
				if (igmdao.versionIncluded(versionId)) {
					imfBean = ifmdao.findByItemIdAndCRFVersionId(item.getId(), versionId.intValue());
					imfBean.setCrfVersionName(version.getName());
					crf = (CRFBean) cdao.findByPK(version.getCrfId());
					imfBean.setCrfName(crf.getName());
					versionItems.add(imfBean);
				} else {
					imfBean = ifmdao.findByItemIdAndCRFVersionIdNotInIGM(item.getId(), versionId.intValue());
					imfBean.setCrfVersionName(version.getName());
					crf = (CRFBean) cdao.findByPK(version.getCrfId());
					imfBean.setCrfName(crf.getName());
					versionItems.add(imfBean);
				}
			}

		}

		SectionBean section = (SectionBean) sectionDao.findByPK(imfBean.getSectionId());
		request.setAttribute(VERSION_ITEMS, versionItems);
		request.setAttribute(ITEM_BEAN, item);
		request.setAttribute("crf", crf);
		request.setAttribute("section", section);
		request.setAttribute("ifmdBean", imfBean);
		forwardPage(Page.ITEM_DETAIL, request, response);

	}

}
