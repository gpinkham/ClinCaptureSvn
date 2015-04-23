/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

package com.clinovo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.util.EventDefinitionCRFUtil;
import org.akaza.openclinica.util.StudyEventDefinitionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CodedItemService;
import com.clinovo.util.PageMessagesUtil;

/**
 * CompleteCRFDeleteController that handles requests from the delete crf page.
 */
@Controller
@RequestMapping("/deleteCRFVersion")
@SuppressWarnings({"rawtypes", "unchecked"})
public class DeleteCRFVersionController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CodedItemService codedItemService;

	@Autowired
	private RuleSetDao ruleSetDao;

	public static final String ACTION_PAGE = "admin/deleteCRFVersion";
	public static final String ERROR_PAGE = "redirect:/MainMenu?message=system_no_permission";
	public static final String CRF_LIST = "redirect:/ListCRF";

	/**
	 * Method that handles requests for delete crf version page.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param crfVersionId
	 *            crf version id
	 * @param model
	 *            model
	 * @return String
	 * @throws Exception
	 *             an exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainGet(HttpServletRequest request, Model model, @RequestParam("crfVersionId") int crfVersionId)
			throws Exception {

		StudyUserRoleBean userRole = (StudyUserRoleBean) request.getSession().getAttribute(BaseController.USER_ROLE);

		if (userRole.getRole() == Role.SYSTEM_ADMINISTRATOR || (userRole.getRole() == Role.STUDY_ADMINISTRATOR)) {

			CRFDAO crfDao = new CRFDAO(dataSource);
			EventCRFDAO eventCrfDAO = new EventCRFDAO(dataSource);
			CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
			DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(dataSource);
			EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);

			CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfVersionId);
			CRFBean crfBean = (CRFBean) crfDao.findByPK(crfVersionBean.getCrfId());
			int crfVersionsQuantity = crfVersionDao.findAllByCRF(crfBean.getId()).size();

			List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfVersionId);
			List<EventCRFBean> eventCrfBeanList = eventCrfDAO.findAllStartedByCrfVersion(crfVersionId);
			List<StudyEventDefinitionBean> eventDefinitionListAvailable = crfVersionsQuantity > 1
					? new ArrayList<StudyEventDefinitionBean>()
					: StudyEventDefinitionUtil.studyEventDefinitionListFilter(dataSource,
							eventDefinitionCrfDao.findAllByCRF(crfBean.getId()));
			List<StudyEventDefinitionBean> eventDefinitionListFull = StudyEventDefinitionUtil
					.studyEventDefinitionStatusUpdate(dataSource, crfBean.getId());

			List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCrfVersionId(crfVersionId);

			Map<String, List<String>> reassignedCrfVersionOid = validateAnotherVersionForDataReassign(crfBean, crfVersionId);

			model.addAttribute("crfBean", crfBean);
			model.addAttribute("crfVersionBean", crfVersionBean);
			model.addAttribute("crfDiscrepancyNotes", crfDiscrepancyNotes);
			model.addAttribute("reassignedCrfVersionOid", reassignedCrfVersionOid);
			model.addAttribute("ruleSetBeanList", ruleSetBeanList);
			model.addAttribute("eventDefinitionListAvailable", eventDefinitionListAvailable);
			model.addAttribute("eventDefinitionListFull", eventDefinitionListFull);
			model.addAttribute("eventCRFBeanList", eventCrfBeanList);

			if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0
					|| eventDefinitionListAvailable.size() > 0 || ruleSetBeanList.size() > 0) {
				PageMessagesUtil.addPageMessage(
						request,
						messageSource.getMessage("this_crf_version_has_associated_data", null,
								LocaleResolver.getLocale()));
				if (crfVersionsQuantity == 1) {
					PageMessagesUtil.addPageMessage(
							request,
							messageSource.getMessage("you_are_trying_to_delete_last_version", null,
									LocaleResolver.getLocale()));
				}


			} else if (reassignedCrfVersionOid.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String key : reassignedCrfVersionOid.keySet()) {
					sb.append(key).append(" ").append(messageSource.getMessage("contains_next_items", null,
							LocaleResolver.getLocale())).append(" ");
					List<String> value = reassignedCrfVersionOid.get(key);
						for (String element : value) {
							sb.append(element).append("</br>");
						}
					sb.append(" ").append(messageSource.getMessage("after_data_was_reassign", null,
							LocaleResolver.getLocale())).append(" ");
				}
				PageMessagesUtil.addPageMessage(request, sb.toString());

			} else {
				PageMessagesUtil.addPageMessage(
						request,
						messageSource.getMessage("this_crf_version_has_no_conflict_data", null,
								LocaleResolver.getLocale()));
			}

		} else {

			return ERROR_PAGE;
		}

		return ACTION_PAGE;
	}

	private Map<String, List<String>> validateAnotherVersionForDataReassign(CRFBean crfBean, int currentCRFVersionId) {

		ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(dataSource);
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		ItemDAO itemDAO = new ItemDAO(dataSource);
		EventCRFDAO eventCrfDAO = new EventCRFDAO(dataSource);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);

		List<EventCRFBean> eventCRFBeanList = eventCrfDAO.findAllByCRF(crfBean.getId());

		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();

		for (EventCRFBean eventCRFBean : eventCRFBeanList) {
			List<String> reassignedItemData = new ArrayList<String>();
			ArrayList<ItemFormMetadataBean> eCRFFromEventMetadata = itemFormMetadataDAO.findAllCrfVersionItemMetadata(eventCRFBean.getCRFVersionId());
			//ArrayList<ItemDataBean> itemDatas = itemDataDAO.findAllByEventCRFId(eventCRFBean.getCRFVersionId());
			ArrayList<ItemDataBean> itemDatas = itemDataDAO.findAllByEventCRFId(eventCRFBean.getId());
			//item data contains not valid item
			if (itemDatas.size() != eCRFFromEventMetadata.size()) {
				for (ItemDataBean itemDataBean : itemDatas) {
					for (ItemFormMetadataBean itemFormMetadata : eCRFFromEventMetadata) {
						if (itemFormMetadata.getItemId() != itemDataBean.getItemId() && !reassignedItemData.contains(itemDataBean)) {
							//get eCRF for delete metadata.
							ArrayList<ItemFormMetadataBean> currentCRFMetaData = itemFormMetadataDAO.findAllCrfVersionItemMetadata(currentCRFVersionId);
							for (ItemFormMetadataBean itemFormMetadataBean : currentCRFMetaData) {
								if (itemFormMetadataBean.getItemId() == itemFormMetadata.getItemId()) {
									//version to delete ref to this item.
									ItemBean itemBean = (ItemBean) itemDAO.findByPK(itemDataBean.getItemId());
									reassignedItemData.add(itemBean.getOid());
								}
							}

						}
					}
				}
			}
			if (reassignedItemData.size() > 0) {
				CRFVersionBean crfVersionBeanWithReassignedData = (CRFVersionBean) crfVersionDao.findByPK((eventCRFBean.getCRFVersionId()));
				resultMap.put(crfVersionBeanWithReassignedData.getOid(), reassignedItemData);
			}
		}
		return resultMap;
	}

	/**
	 * Method that handles requests from the confirm delete crf version page.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param crfVersionId
	 *            crf version id
	 * @return String
	 * @throws Exception
	 *             an exception
	 */
	@RequestMapping(method = RequestMethod.POST, params = "confirm")
	public String confirm(HttpServletRequest request, @RequestParam("crfVersionId") int crfVersionId) throws Exception {

		CRFDAO crfDao = new CRFDAO(dataSource);
		EventCRFDAO eventCrfDAO = new EventCRFDAO(dataSource);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);

		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfVersionId);
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfVersionBean.getCrfId());
		int crfVersionQuantity = crfVersionDao.findAllActiveByCRF(crfBean.getId()).size();

		List<RuleSetBean> ruleSetBeanList = ruleSetListFilter(crfVersionId);
		List<EventCRFBean> eventCrfBeanList = eventCrfDAO.findAllStartedByCrfVersion(crfVersionId);
		List<StudyEventDefinitionBean> eventDefinitionListAvailable = crfVersionQuantity > 1
				? new ArrayList<StudyEventDefinitionBean>()
				: StudyEventDefinitionUtil.studyEventDefinitionListFilter(dataSource,
						eventDefinitionCrfDao.findAllByCRF(crfBean.getId()));
		List<DiscrepancyNoteBean> crfDiscrepancyNotes = discrepancyNoteDao.findAllByCrfVersionId(crfVersionId);

		if (eventCrfBeanList.size() > 0 || crfDiscrepancyNotes.size() > 0 || eventDefinitionListAvailable.size() > 0
				|| ruleSetBeanList.size() > 0) {
			request.getSession().setAttribute("controllerMessage",
					messageSource.getMessage("this_crf_version_has_associated_data", null, LocaleResolver.getLocale()));
		} else {
			ArrayList items = crfVersionDao.findNotSharedItemsByVersion(crfVersionBean.getId());
			NewCRFBean nib = new NewCRFBean(dataSource, crfVersionBean.getCrfId());
			EventDefinitionCRFUtil.setDefaultCRFVersionInsteadOfDeleted(dataSource, crfVersionBean.getId());
			ruleSetDao.deleteRuleStudioMetadataByCRFVersionOID(crfVersionBean.getOid());
			nib.setDeleteQueries(crfVersionDao.generateDeleteQueries(crfVersionBean.getId(), items));
			nib.deleteFromDB();

			// Purge coded items
			codedItemService.deleteByCRFVersion(crfVersionBean.getId());

			request.getSession().setAttribute("controllerMessage",
					messageSource.getMessage("the_crf_version_has_been_removed", null, LocaleResolver.getLocale()));
		}

		return CRF_LIST;
	}

	private List<RuleSetBean> ruleSetListFilter(int crfVersionId) {
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);

		List<RuleSetBean> ruleSetBeanList = new ArrayList<RuleSetBean>();
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDAO.findByPK(crfVersionId);

		List<RuleSetBean> studyRuleSetBeanList = ruleSetDao.findByCrfVersionIdAndCrfVersionOid(crfVersionBean);
		for (RuleSetBean ruleSetRule : studyRuleSetBeanList) {
			ruleSetBeanList.add(ruleSetRule);
		}
		return ruleSetBeanList;
	}
}
