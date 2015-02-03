/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.util.DAOWrapper;
import org.akaza.openclinica.util.SubjectEventStatusUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * SDVItemController.
 */
@Controller
@RequestMapping("/sdvItem")
public class SDVItemController {

	public static final String CRF = "crf";
	public static final String SDV = "sdv";
	public static final String ITEM = "item";
	public static final String UN_SDV = "unsdv";
	public static final String ACTION = "action";
	public static final String COMPLETED = "completed";
	public static final String ITEM_DATA_ID = "itemDataId";
	public static final String ITEMS_TO_SDV = "itemsToSDV";

	@Autowired
	private DataSource dataSource;

	/**
	 * Main http get method.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String mainGet(HttpServletRequest request) throws Exception {
		UserAccountBean userAccountBean = (UserAccountBean) request.getSession().getAttribute(
				BaseController.USER_BEAN_NAME);

		String action = request.getParameter(ACTION);
		int itemDataId = Integer.parseInt(request.getParameter(ITEM_DATA_ID));

		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		ItemDataBean itemDataBean = (ItemDataBean) itemDataDao.findByPK(itemDataId);
		itemDataBean.setSdv(action.equalsIgnoreCase(SDV));
		itemDataDao.update(itemDataBean);

		String crf = "";
		int eventCrfId = itemDataBean.getEventCRFId();
		int itemsToSDV = itemDataDao.getItemsToSDV(eventCrfId);

		if (action.contentEquals(UN_SDV) || (action.contentEquals(SDV) && itemsToSDV == 0)) {
			EventCRFDAO eventCrfDao = new EventCRFDAO(dataSource);
			EventCRFBean eventCrfBean = (EventCRFBean) eventCrfDao.findByPK(eventCrfId);

			crf = action.contentEquals(SDV) && itemsToSDV == 0 ? SDV : (action.contentEquals(UN_SDV)
					&& eventCrfBean.isSdvStatus() ? COMPLETED : crf);
			eventCrfBean.setSdvStatus(action.contentEquals(SDV));
			eventCrfDao.update(eventCrfBean);

			StudyEventDAO studyEventDao = new StudyEventDAO(dataSource);
			StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCrfBean.getStudyEventId());
			SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, new DAOWrapper(new StudyDAO(dataSource),
					new CRFVersionDAO(dataSource), studyEventDao, new StudySubjectDAO(dataSource), new EventCRFDAO(
							dataSource), new EventDefinitionCRFDAO(dataSource), new DiscrepancyNoteDAO(dataSource)));
			studyEventBean.setUpdater(userAccountBean);
			studyEventDao.update(studyEventBean);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ITEM, SDV);
		jsonObject.put(CRF, crf);
		jsonObject.put(ITEMS_TO_SDV, Integer.toString(itemsToSDV));

		return jsonObject.toString();
	}
}
