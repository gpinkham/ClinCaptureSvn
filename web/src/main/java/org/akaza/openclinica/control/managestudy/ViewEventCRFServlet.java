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

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Views the detail of an event CRF
 * 
 * @author jxu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class ViewEventCRFServlet extends Controller {
	/**
     *
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int eventCRFId = fp.getInt("id", true);
		int studySubId = fp.getInt("studySubId", true);

		StudySubjectDAO subdao = new StudySubjectDAO(getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		ItemDAO idao = new ItemDAO(getDataSource());
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(getDataSource());
		CRFDAO cdao = new CRFDAO(getDataSource());
		SectionDAO secdao = new SectionDAO(getDataSource());

		if (eventCRFId == 0) {
			addPageMessage(getResPage().getString("please_choose_an_event_CRF_to_view"), request);
			forwardPage(Page.LIST_STUDY_SUBJECTS, request, response);
		} else {
			StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
			request.setAttribute("studySub", studySub);

			EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);
			CRFBean crf = cdao.findByVersionId(eventCRF.getCRFVersionId());
			request.setAttribute("crf", crf);

			ArrayList<SectionBean> sections = secdao.findAllByCRFVersionId(eventCRF.getCRFVersionId());
			for (SectionBean section : sections) {
				ArrayList<ItemDataBean> itemData = iddao.findAllByEventCRFId(eventCRFId);

				ArrayList displayItemData = new ArrayList();
				for (ItemDataBean id : itemData) {
					DisplayItemBean dib = new DisplayItemBean();
					ItemBean item = (ItemBean) idao.findByPK(id.getItemId());
					ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(item.getId(),
							eventCRF.getCRFVersionId());

					item.setItemMeta(ifm);
					dib.setItem(item);
					dib.setData(id);
					dib.setMetadata(ifm);
					displayItemData.add(dib);
				}
				section.setItems(displayItemData);
			}

			request.setAttribute("sections", sections);
			request.setAttribute("studySubId", Integer.toString(studySubId));
			forwardPage(Page.VIEW_EVENT_CRF, request, response);
		}
	}

}
