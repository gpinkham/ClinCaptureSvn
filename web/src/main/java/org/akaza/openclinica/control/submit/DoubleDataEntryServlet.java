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
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
import org.akaza.openclinica.control.form.ScoreItemValidator;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({"rawtypes", "serial"})
@Component
public class DoubleDataEntryServlet extends DataEntryServlet {

	public static final String COUNT_VALIDATE = "countValidate";
	public static final String DDE_ENTERED = "ddeEntered";
	public static final String DDE_PROGESS = "doubleDataProgress";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
		HttpSession session = request.getSession();

		getInputBeans(request);
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		FormProcessor fp = new FormProcessor(request);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);

		// The following COUNT_VALIDATE session attribute is not
		// accessible,
		// for unknown reasons (threading problems?), when
		// double-data entry displays error messages; it's value is always 0; so
		// I have to create my
		// own session variable here to keep track of DDE stages

		// We'll go by the SectionBean's ordinal first
		int tabNumber = 1;
		if (sb != null) {
			tabNumber = sb.getOrdinal();
		}
		// if tabNumber still isn't valid, check the "tab" parameter
		if (tabNumber < 1) {
			String tab = fp.getString("tab");
			if (tab == null || tab.length() < 1) {
				tabNumber = 1;
			} else {
				tabNumber = fp.getInt("tab");
			}
		}
		SectionDAO sectionDao = getSectionDAO();
		int crfVersionId = ecb.getCRFVersionId();
		int eventCRFId = ecb.getId();
		ArrayList sections = sectionDao.findAllByCRFVersionId(crfVersionId);
		int sectionSize = sections.size();

		HttpSession mySession = request.getSession();
		DoubleDataProgress doubleDataProgress = (DoubleDataProgress) mySession.getAttribute(DDE_PROGESS);
		if (doubleDataProgress == null || doubleDataProgress.getEventCRFId() != eventCRFId) {
			doubleDataProgress = new DoubleDataProgress(sectionSize, eventCRFId);
			mySession.setAttribute(DDE_PROGESS, doubleDataProgress);
		}
		boolean hasVisitedSection = doubleDataProgress.getSectionVisited(tabNumber, eventCRFId);

		// setting up one-time validation here
		// admit that it's an odd place to put it, but where else?
		// placing it in dataentryservlet is creating too many counts
		int keyId = ecb.getId();
		Integer count = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);
		if (count != null) {
			count++;
			session.setAttribute(COUNT_VALIDATE + keyId, count);
			logger.info("^^^just set count to session: " + count);
		} else {
			count = 0;
			session.setAttribute(COUNT_VALIDATE + keyId, count);
			logger.info("***count not found, set to session: " + count);
		}

		DataEntryStage stage = ecb.getStage();
		if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) && !hasVisitedSection) {
			// if the user has not entered this section yet in Double Data
			// Entry, then
			// set a flag that default values should be shown in the form
			request.setAttribute(DDE_ENTERED, true);

		}
		// Now update the session attribute
		doubleDataProgress.setSectionVisited(eventCRFId, tabNumber, true);
		mySession.setAttribute("doubleDataProgress", doubleDataProgress);
		session.setAttribute("mayProcessUploading", "true");
	}

	@Override
	protected void putDataEntryStageFlagToRequest(HttpServletRequest request) {
		request.setAttribute(DATA_ENTRY_STAGE, DataEntryStage.DOUBLE_DATA_ENTRY);
	}

	@Override
	protected boolean validateInputOnFirstRound() {
		return true;
	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			HttpServletRequest request) {

		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();
		HttpSession session = request.getSession();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		boolean isSingleItem = false;
		if (StringUtil.isBlank(inputName)) {
			// for single items
			inputName = getInputName(dib);
			isSingleItem = true;
		}

		// we only give warning to user if data entered in DDE is different from
		// IDE when the first
		// time user hits 'save'
		int keyId = ecb.getId();
		Integer validationCount = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);

		ItemDataBean valueToCompareTmp = dib.getData();
		ItemDataBean valueToCompare = copyItemDataBean(valueToCompareTmp);

		if (!isSingleItem) {
			valueToCompare = dib.getDbData();
		}

		if (isSingleItem) {
			dib = loadFormValue(dib, request);
		}

		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		boolean showOriginalItem = dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, valueToCompare);
		boolean showItem = dib.getMetadata().isShowItem();
		if (!showItem && dib.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
			showItem = true;
		}
		boolean showDuplicateItem = dynamicsMetadataService.hasPassedDDE(dib.getMetadata(), ecb, valueToCompare);
		logger.debug("*** show original item has value " + dib.getData().getValue() + " and show item has value "
				+ valueToCompare.getValue());
		logger.debug("--- show original: " + showOriginalItem + " show duplicate: " + showDuplicateItem
				+ " and just show item: " + showItem);
		logger.debug("VALIDATION COUNT " + validationCount);
		if (showOriginalItem && showDuplicateItem || showItem) {
			if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.FILE)) {
				dib = validateDisplayItemBeanText(v, dib, inputName, request);
				if (validationCount == null || validationCount == 0) {
					v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
					v.setErrorMessage(respage.getString("value_you_specified") + " " + valueToCompare.getValue() + " "
							+ respage.getString("from_initial_data_entry"));
				}

			} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
				dib = validateDisplayItemBeanSingleCV(v, dib, inputName);

				if (validationCount == null || validationCount == 0) {
					v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
					String errorValue = valueToCompare.getValue();

					java.util.ArrayList options = dib.getMetadata().getResponseSet().getOptions();

					for (Object option : options) {
						ResponseOptionBean rob = (ResponseOptionBean) option;
						if (rob.getValue().equals(errorValue)) {
							errorValue = rob.getText();
						}
					}
					v.setErrorMessage(respage.getString("value_you_specified") + " " + errorValue + " "
							+ respage.getString("from_initial_data_entry"));
				}
			} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
				dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);

				if (validationCount == null || validationCount == 0) {
					v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, true);
					String errorValue = valueToCompare.getValue();
					String errorTexts = "";

					java.util.ArrayList options = dib.getMetadata().getResponseSet().getOptions();

					for (int u = 0; u < options.size(); u++) {
						ResponseOptionBean rob = (ResponseOptionBean) options.get(u);
						if (errorValue.contains(rob.getValue())) {
							errorTexts = errorTexts + rob.getText();
							if (u < options.size() - 1) {
								errorTexts = errorTexts + ", ";
							}
						}
					}
					v.setErrorMessage(respage.getString("value_you_specified") + " " + errorTexts + " "
							+ respage.getString("from_initial_data_entry"));
				}
			}

		}

		return dib;

	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
			DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups,
			HttpServletRequest request, HttpServletResponse response) {
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		HttpSession session = request.getSession();
		logger.info("===got this far");
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		int keyId = ecb.getId();
		Integer validationCount = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);

		formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
		logger.info("found formgroups size for " + digb.getGroupMetaBean().getName() + ": " + formGroups.size()
				+ " compare to db groups size: " + digbs.size());

		String inputName = "";
		for (int i = 0; i < formGroups.size(); i++) {
			DisplayItemGroupBean displayGroup = formGroups.get(i);

			List<DisplayItemBean> items = displayGroup.getItems();
			for (DisplayItemBean displayItem : items) {
				inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem, !displayGroup.isAuto());
				validateDisplayItemBean(v, displayItem, inputName, request);
			}

			if (validationCount == null || validationCount == 0) {
				if (i == 0 && formGroups.size() != digbs.size()) {
					v.addValidation(inputName + "group", Validator.DIFFERENT_NUMBER_OF_GROUPS_IN_DDE);
					// TODO internationalize this string, tbh
					v.setErrorMessage("There are additional values here that were not present in the initial data entry. You have entered a different number of groups"
							+ " for the item groups containing " + inputName);

				}
			}
		}

		return formGroups;

	}

	@Override
	protected DisplayItemBean validateCalcTypeDisplayItemBean(ScoreItemValidator sv, DisplayItemBean dib,
			String inputName, HttpServletRequest request) {

		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();
		ItemDataDAO iddao = getItemDataDAO();
		boolean isSingleItem = false;
		if (StringUtil.isBlank(inputName)) {
			// for single items
			inputName = getInputName(dib);
			isSingleItem = true;
		}
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ItemDataBean valueToCompare = new ItemDataBean();
		if (isSingleItem) {
			int idId = dib.getData().getId();
			if (idId > 0) {
				valueToCompare = (ItemDataBean) iddao.findByPK(idId);
			}
		} else {
			valueToCompare = dib.getDbData();
		}
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
			boolean showOriginalItem = dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, valueToCompare);
			boolean showItem = dib.getMetadata().isShowItem();
			boolean showDuplicateItem = dynamicsMetadataService.hasPassedDDE(dib.getMetadata(), ecb, valueToCompare);

			if (showOriginalItem && showDuplicateItem || showItem) {
				dib = validateDisplayItemBeanText(sv, dib, inputName, request);
			}
			if (showOriginalItem && showDuplicateItem || showItem) {
				sv.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
				sv.setErrorMessage(respage.getString("value_you_specified") + " " + valueToCompare.getValue() + " "
						+ respage.getString("from_initial_data_entry"));
			}
		}
		return dib;
	}

	@Override
	protected Status getBlankItemStatus() {
		return Status.PENDING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus ()
	 */
	@Override
	protected Status getNonBlankItemStatus(HttpServletRequest request) {
		return Status.UNAVAILABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations ()
	 */
	@Override
	protected String getEventCRFAnnotations(HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		return ecb.getValidatorAnnotations();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations (java.lang.String)
	 */
	@Override
	protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		ecb.setValidatorAnnotations(annotations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
	 */
	@Override
	protected Page getJSPPage() {
		return Page.DATA_ENTRY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
	 */
	@Override
	protected Page getServletPage(HttpServletRequest request) {
		FormProcessor fp = new FormProcessor(request);
		String tabId = fp.getString("tab", true);
		String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
		String hideSaveAndNextButton = fp.getString("hsnb", true);
		if (hideSaveAndNextButton.equals("1")) {
			request.setAttribute("hideSaveAndNextButton", true);
		}
		request.setAttribute("system_lang", CoreResources.getSystemLocale().toString());
		if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
			return Page.DOUBLE_DATA_ENTRY_SERVLET;
		} else {
			Page target = Page.DOUBLE_DATA_ENTRY_SERVLET;
			target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId + "&tab="
					+ tabId);
			return target;
		}

	}

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
			RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation,
			ArrayList<String> messages, HttpServletRequest request) {

		boolean isSingleItem = false;
		if (StringUtil.isBlank(inputName)) {
			// for single items
			inputName = getInputName(dib);
			isSingleItem = true;
		}

		if (isSingleItem) {
			dib = loadFormValue(dib, request);
		}
		if (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation) {
			messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
			dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
		}

		return dib;

	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
			DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs, List<DisplayItemGroupBean> formGroups,
			RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, HttpServletRequest request,
			HttpServletResponse response) {
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
		logger.info("found formgroups size for " + digb.getGroupMetaBean().getName() + ": " + formGroups.size()
				+ " compare to db groups size: " + digbs.size());

		for (DisplayItemGroupBean displayGroup : formGroups) {
			List<DisplayItemBean> items = displayGroup.getItems();
			int order = displayGroup.getOrdinal();

			for (DisplayItemBean displayItem : items) {
				String inputName = getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem, !displayGroup.isAuto());

				if (displayItem.getMetadata().isShowItem()
						|| getDynamicsMetadataService().isShown(displayItem.getItem().getId(), ecb,
						displayItem.getData())) {
					// add the validation
					if (groupOrdinalPLusItemOid.containsKey(displayItem.getItem().getOid())
							|| groupOrdinalPLusItemOid.containsKey(String.valueOf(order + 1)
							+ displayItem.getItem().getOid())) {
						System.out.println("IN : " + String.valueOf(order + 1) + displayItem.getItem().getOid());
						validateDisplayItemBean(
								v,
								displayItem,
								inputName,
								rv,
								groupOrdinalPLusItemOid,
								true,
								groupOrdinalPLusItemOid.get(String.valueOf(order + 1) + displayItem.getItem().getOid()),
								request);
					} else {
						validateDisplayItemBean(v, displayItem, inputName, rv, groupOrdinalPLusItemOid, false, null,
								request);
					}
				} else {
					System.out.println("OUT : " + String.valueOf(order + 1) + displayItem.getItem().getOid());
				}
				// validateDisplayItemBean(v, displayItem, inputName);
			}

		}
		return formGroups;
	}

	@Override
	protected boolean shouldRunRules() {
		return true;
	}

	@Override
	protected boolean isAdministrativeEditing() {
		return false;
	}

	@Override
	protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
		return false;
	}

	@SuppressWarnings("deprecation")
	private ItemDataBean copyItemDataBean(ItemDataBean src) {
		ItemDataBean result = new ItemDataBean();
		result.setEventCRFId(src.getEventCRFId());
		result.setItemId(src.getItemId());
		result.setValue(src.getValue());
		result.setOrdinal(src.getOrdinal());
		result.setSelected(src.isSelected());
		result.setAuditLog(src.isAuditLog());
		result.setCreatedDate(src.getCreatedDate());
		result.setUpdatedDate(src.getUpdatedDate());
		result.setOwner(src.getOwner());
		result.setOwnerId(src.getOwnerId());
		result.setUpdater(src.getUpdater());
		result.setUpdaterId(src.getUpdaterId());
		result.setStatus(src.getStatus());

		return result;
	}
}
