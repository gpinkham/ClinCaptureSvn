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

package com.clinovo.lib.crf.service.impl;

import java.net.URLDecoder;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.domain.datamap.ResponseSet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.impl.BaseCrfBuilder;
import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.enums.FormStudioElement;
import com.clinovo.lib.crf.enums.OperationType;
import com.clinovo.lib.crf.enums.RealValueKey;
import com.clinovo.lib.crf.validator.CommonValidator;

/**
 * FormStudioServiceImpl.
 */
@Service
@SuppressWarnings("unused")
public class FormStudioImportCrfServiceImpl extends BaseImportCrfService {

	public static final Logger LOGGER = LoggerFactory.getLogger(FormStudioImportCrfServiceImpl.class);

	@Autowired
	private DataSource dataSource;

	private JSONArray getJSONArray(JSONObject jsonObject, String key) throws Exception {
		JSONArray result = new JSONArray();
		try {
			result = jsonObject.getJSONArray(key);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private boolean isYes(JSONObject jsonObject, String key) throws Exception {
		boolean result = false;
		try {
			result = URLDecoder.decode(jsonObject.getString(key), UTF_8).equalsIgnoreCase(YES);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private boolean getBoolean(JSONObject jsonObject, String key) throws Exception {
		boolean result = false;
		try {
			result = URLDecoder.decode(jsonObject.getString(key), UTF_8).equalsIgnoreCase(TRUE);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private String getString(JSONArray jsonArray, int index) throws Exception {
		String result = "";
		try {
			result = URLDecoder.decode(jsonArray.getString(index), UTF_8);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private Object getObject(JSONObject jsonObject, String key) throws Exception {
		Object result = null;
		try {
			result = jsonObject.get(key);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private String getString(JSONObject jsonObject, String key) throws Exception {
		String result = "";
		try {
			result = URLDecoder.decode(jsonObject.getString(key), UTF_8);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private int getInt(JSONObject jsonObject, String key) throws Exception {
		int result = 0;
		try {
			result = Integer.parseInt(URLDecoder.decode(jsonObject.getString(key), UTF_8));
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private ItemGroupBean createItemGroup(String groupName, JsonCrfBuilder crfBuilder) {
		ItemGroupBean itemGroupBean = new ItemGroupBean();
		itemGroupBean.setStatus(Status.AVAILABLE);
		itemGroupBean.setOwner(crfBuilder.getOwner());
		itemGroupBean.setName(groupName);
		crfBuilder.getItemGroups().add(itemGroupBean);
		crfBuilder.getItemGroupLabelMap().put(itemGroupBean.getName(), itemGroupBean);
		return itemGroupBean;
	}

	private void processPages(JsonCrfBuilder crfBuilder) throws Exception {
		crfBuilder.setDefaultItemGroupBean(createItemGroup(UNGROUPED, crfBuilder));
		JSONArray pages = getJSONArray(crfBuilder.getJsonObject(), PAGES);
		for (int i = 0; i < pages.length(); i++) {
			JSONObject jsonObj = pages.getJSONObject(i);

			crfBuilder.setCurrentSection(new SectionBean());
			crfBuilder.getCurrentSection().setInstructions(getString(jsonObj, INSTRUCTIONS));
			crfBuilder.getCurrentSection().setSubtitle(getString(jsonObj, SUB_TITLE));
			crfBuilder.getCurrentSection().setLabel(getString(jsonObj, NAME));
			crfBuilder.getCurrentSection().setTitle(getString(jsonObj, TITLE));
			if (crfBuilder.getCurrentSection().getTitle().trim().isEmpty()) {
				crfBuilder.getCurrentSection().setTitle(crfBuilder.getCurrentSection().getName());
			}
			crfBuilder.getCurrentSection().setStatus(Status.AVAILABLE);
			crfBuilder.getCurrentSection().setPageNumberLabel("");
			crfBuilder.getCurrentSection().setOrdinal(crfBuilder.getNextSectionOrdinal());
			crfBuilder.getCurrentSection().setOwner(crfBuilder.getOwner());
			crfBuilder.getSections().add(crfBuilder.getCurrentSection());
			crfBuilder.getSectionLabelMap().put(crfBuilder.getCurrentSection().getLabel(),
					crfBuilder.getCurrentSection());

			processQuestions(getJSONArray(jsonObj, QUESTIONS), null, crfBuilder);
		}
	}

	private void generateHiddenDataForStrataData(JsonCrfBuilder crfBuilder) {
		StringBuilder hiddenDataStringBuilder = new StringBuilder();
		if (crfBuilder.getCurrentItem().getName().toLowerCase().startsWith(RAND_STRATA_DATA.toLowerCase())) {
			int c = 0;
			hiddenDataStringBuilder.append("<input eleId=\"").append(crfBuilder.getCurrentItem().getName())
					.append("\" type=\"hidden\" ");
			String[] attrValues = crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues(),
					",");
			for (String attr : crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsText(), ",")) {
				hiddenDataStringBuilder.append(" ").append(attr).append("=\"").append(attrValues[c]).append("\" ");
				c++;
			}
			hiddenDataStringBuilder.append("><input name=\"requiredParam2Missing\" type=\"hidden\" value=\"")
					.append(crfBuilder.getMessage("pleaseSpecifyStratificationData")).append("\">");

			crfBuilder.getCurrentItem().getResponseSet().setResponseType(newResponseType(ResponseType.SELECT));
			crfBuilder.getCurrentItem().setDescription(STRATIFICATION_DATA);
			crfBuilder.getCurrentItem().getItemMeta().setHeader(crfBuilder.getMessage("strataVariable"));
			crfBuilder.getCurrentItem().getItemMeta().setRightItemText(crfBuilder.getCurrentItem().getItemMeta()
					.getRightItemText().concat(" ").concat(hiddenDataStringBuilder.toString()));
		}
	}

	private void createItemFormMetadata(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		crfBuilder.getCurrentItem().getItemMeta().setWidthDecimal("");
		String repeatingItemGroupHeader = getString(jsonObj, REPEATING_ITEM_GROUP_HEADER);
		crfBuilder.getCurrentItem().getItemMeta().setHeader(
				!repeatingItemGroupHeader.isEmpty() ? repeatingItemGroupHeader : crfBuilder.getCurrentHeader());
		crfBuilder.getCurrentItem().getItemMeta().setColumnNumber(getInt(jsonObj, COLUMN_NUMBER));
		if (crfBuilder.getCurrentItem().getName().equalsIgnoreCase(RAND_DATE)) {
			crfBuilder.getCurrentItem().getItemMeta().setQuestionNumberLabel("");
		} else if (isYes(jsonObj, NUMBERING)) {
			crfBuilder.getCurrentItem().getItemMeta().setQuestionNumberLabel(getString(jsonObj, POS).concat("."));
		}

		crfBuilder.getCurrentItem().getItemMeta().setPageNumberLabel("");
		crfBuilder.getCurrentItem().getItemMeta().setLeftItemText(getString(jsonObj, LEFT_TEXT));
		crfBuilder.getCurrentItem().getItemMeta().setRightItemText(getString(jsonObj, RIGHT_TEXT));
		crfBuilder.getCurrentItem().getItemMeta().setRegexp(getString(jsonObj, FIELD_VALIDATION));
		crfBuilder.getCurrentItem().getItemMeta().setRegexpErrorMsg(getString(jsonObj, VALIDATION_MESSAGE));
		crfBuilder.getCurrentItem().getItemMeta().setOrdinal(crfBuilder.getNextItemMetadataOrdinal());
		crfBuilder.getCurrentItem().getItemMeta().setRequired(isYes(jsonObj, REQUIRED));
		crfBuilder.getCurrentItem().getItemMeta().setResponseLayout(getString(jsonObj, LAYOUT));
		crfBuilder.getCurrentItem().getItemMeta()
				.setShowItem(!getString(jsonObj, DISPLAY_STATE).equalsIgnoreCase(HIDE));
		crfBuilder.getCurrentItem().getItemMeta().setCodeRef("");
		crfBuilder.getCurrentItem().getItemMeta().setSdvRequired(false);
		crfBuilder.getItemNameToMetaMap().put(crfBuilder.getCurrentItem().getName(),
				crfBuilder.getCurrentItem().getItemMeta());
		crfBuilder.getCurrentItem().getItemMeta().setSectionName(crfBuilder.getCurrentSection().getLabel());
		crfBuilder.getCurrentItem().getItemMeta()
				.setGroupLabel(crfBuilder.getCurrentItem().getItemGroupBean().getName());
		generateHiddenDataForStrataData(crfBuilder);
	}

	private void createItemGroupMetadata(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		ItemGroupBean itemGroupBean = (ItemGroupBean) getObject(jsonObj, REPEATING_GROUP);
		crfBuilder.getCurrentItem()
				.setItemGroupBean(itemGroupBean != null ? itemGroupBean : crfBuilder.getDefaultItemGroupBean());
		crfBuilder.getCurrentItem().getItemGroupBean().setMeta(new ItemGroupMetadataBean());
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setBorders(0);
		int maxRows = getInt(jsonObj, MAX_ROWS);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setRepeatMax(
				crfBuilder.getCurrentItem().getItemGroupBean() == crfBuilder.getDefaultItemGroupBean() ? 1 : maxRows);
		int minRows = getInt(jsonObj, MIN_ROWS);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setRepeatNum(
				crfBuilder.getCurrentItem().getItemGroupBean() == crfBuilder.getDefaultItemGroupBean() ? 1 : minRows);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setRowStartNumber(0);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setShowGroup(true);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setRepeatingGroup(itemGroupBean != null);
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setHeader(getString(jsonObj, REPEATING_GROUP_HEADER));
		crfBuilder.getCurrentItem().getItemGroupBean().getMeta().setSubheader("");
	}

	private void createResponseSet(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		String label;
		String optionsText;
		JSONArray jsonArray;
		String optionsValues;
		String defaultValue = "";
		ResponseType responseType;
		boolean resizable = getBoolean(jsonObj, RESIZABLE);
		crfBuilder.getCurrentItem().setResponseSet(new ResponseSet());
		String responseTypeValue = getString(jsonObj, TYPE).toUpperCase();
		FormStudioElement type = FormStudioElement.getByName(responseTypeValue);

		switch (type) {
			case TEXT :
			case DATE :
			case TIME :
			case NUMBER :
			case RANDOMIZATION :
				label = resizable ? TEXTAREA : TEXT;
				optionsText = resizable ? TEXTAREA : TEXT;
				optionsValues = resizable ? TEXTAREA : TEXT;
				responseType = resizable ? ResponseType.TEXTAREA : ResponseType.TEXT;
				break;
			case CALCULATION :
				label = CALCULATION;
				optionsText = CALCULATION;
				optionsValues = getString(jsonObj, DEFAULT_VALUE);
				responseType = isYes(jsonObj, GROUP_CALCULATION)
						? ResponseType.GROUP_CALCULATION
						: ResponseType.CALCULATION;
				break;
			case FILE :
				label = FILE;
				optionsText = FILE;
				optionsValues = FILE;
				responseType = ResponseType.FILE;
				break;
			case LIST :
				optionsText = "";
				optionsValues = "";
				label = getString(jsonObj, RESPONSE_LABEL);
				jsonArray = getJSONArray(jsonObj, OPTIONS);
				responseType = getBoolean(jsonObj, MULTI_SELECT) ? ResponseType.SELECTMULTI : ResponseType.SELECT;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObjectOption = jsonArray.getJSONObject(i);
					String optionValue = getString(jsonObjectOption, VALUE);
					optionsText = optionsText.concat(optionsText.isEmpty() ? "" : ",")
							.concat(getString(jsonObjectOption, NAME));
					optionsValues = optionsValues.concat(optionsValues.isEmpty() ? "" : ",").concat(optionValue);
					if (getBoolean(jsonObjectOption, DEFAULT)) {
						defaultValue = optionValue;
					}
				}
				if (getString(jsonObj, DEFAULT_VALUE).isEmpty()) {
					optionsText = ",".concat(optionsText);
					optionsValues = ",".concat(optionsValues);
				}
				break;
			case RADIO :
				optionsText = "";
				optionsValues = "";
				responseType = ResponseType.RADIO;
				label = getString(jsonObj, RESPONSE_LABEL);
				jsonArray = getJSONArray(jsonObj, OPTIONS);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObjectOption = jsonArray.getJSONObject(i);
					optionsText = optionsText.concat(optionsText.isEmpty() ? "" : ",")
							.concat(getString(jsonObjectOption, NAME));
					optionsValues = optionsValues.concat(optionsValues.isEmpty() ? "" : ",")
							.concat(getString(jsonObjectOption, VALUE));
				}
				break;
			case CHECKBOX :
				optionsText = "";
				optionsValues = "";
				responseType = ResponseType.CHECKBOX;
				label = getString(jsonObj, RESPONSE_LABEL);
				jsonArray = getJSONArray(jsonObj, OPTIONS);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObjectOption = jsonArray.getJSONObject(i);
					String optionValue = getString(jsonObjectOption, VALUE);
					optionsText = optionsText.concat(optionsText.isEmpty() ? "" : ",")
							.concat(getString(jsonObjectOption, NAME));
					optionsValues = optionsValues.concat(optionsValues.isEmpty() ? "" : ",").concat(optionValue);
					if (getBoolean(jsonObjectOption, DEFAULT)) {
						defaultValue = optionValue;
					}
				}
				break;
			default :
				label = TEXT;
				optionsText = TEXT;
				optionsValues = TEXT;
				responseType = ResponseType.TEXT;
		}

		if (responseType.getId() == ResponseType.INSTANT_CALCULATION.getId()) {
			crfBuilder.getCurrentItem().setUnits("");
		}

		crfBuilder.getCurrentItem().getItemMeta()
				.setDefaultValue(type == FormStudioElement.CALCULATION
						? ""
						: (type == FormStudioElement.LIST || type == FormStudioElement.CHECKBOX
								? defaultValue
								: getString(jsonObj, DEFAULT_VALUE)));

		crfBuilder.getCurrentItem().getResponseSet()
				.setLabel(label.trim().isEmpty() ? crfBuilder.getCurrentItem().getName() : label);
		crfBuilder.getCurrentItem().getResponseSet().setOptionsText(optionsText);
		crfBuilder.getCurrentItem().getResponseSet().setOptionsValues(optionsValues);
		crfBuilder.getCurrentItem().getResponseSet().setResponseType(newResponseType(responseType));
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.RESPONSE_TYPE,
				FormStudioElement.findByName(responseTypeValue)
						? responseType.getCode().toLowerCase()
						: responseTypeValue);
	}

	private void createSimpleConditionalDisplay(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		JSONArray jsonArray = getJSONArray(jsonObj, SL);
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.SCD_DATA, "");
		if (jsonArray.length() > 0) {
			String controlItemName = getString(jsonArray, 0).trim();
			String optionValue = getString(jsonArray, 1).trim();
			String message = getString(jsonArray, 2).trim();

			crfBuilder.getCurrentItem().setSimpleConditionalDisplayBean(new SimpleConditionalDisplayBean());
			crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setControlItemName(controlItemName);
			crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setOptionValue(optionValue);
			crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setMessage(message);
			crfBuilder.getCurrentItem().setRealValue(RealValueKey.SCD_DATA,
					controlItemName.concat(",").concat(optionValue).concat(",").concat(message));
		}
	}

	private int getItemDataTypeId(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		ItemDataType itemDataType = ItemDataType.ST;
		String itemDataTypeValue = getString(jsonObj, TYPE).toUpperCase();
		FormStudioElement type = FormStudioElement.getByName(itemDataTypeValue);
		if (type == FormStudioElement.NUMBER) {
			itemDataType = getString(jsonObj, NUMBER_TYPE).equalsIgnoreCase(DECIMAL)
					? ItemDataType.REAL
					: ItemDataType.INTEGER;
			itemDataTypeValue = itemDataType.getCode();
		} else if (type == FormStudioElement.DATE) {
			itemDataType = getBoolean(jsonObj, PARTIAL_DATE) ? ItemDataType.PDATE : ItemDataType.DATE;
			itemDataTypeValue = itemDataType.getCode();
		} else if (type == FormStudioElement.FILE) {
			itemDataType = ItemDataType.FILE;
			itemDataTypeValue = itemDataType.getCode();
		} else if (type == FormStudioElement.CALCULATION) {
			String calculationType = getString(jsonObj, CALCULATION_TYPE);
			itemDataType = calculationType.equalsIgnoreCase(INTEGER)
					? ItemDataType.INTEGER
					: (calculationType.equalsIgnoreCase(DECIMAL) ? ItemDataType.REAL : itemDataType);
			itemDataTypeValue = itemDataType.getCode();
		}
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.ITEM_DATA_TYPE,
				FormStudioElement.findByName(itemDataTypeValue)
						? itemDataType.getCode().toLowerCase()
						: itemDataTypeValue);
		return itemDataType.getId();
	}

	private void generateRandomizationFields(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		FormStudioElement type = FormStudioElement.getByName(getString(jsonObj, TYPE).toUpperCase());
		if (type == FormStudioElement.RANDOMIZATION) {
			crfBuilder.getCurrentItem().setName(RAND_RESULT);
			crfBuilder.getCurrentItem().setDescription(RANDOMIZATION_RESULT);
			crfBuilder.getCurrentItem().getItemMeta().setHeader(crfBuilder.getMessage("randomization"));
			crfBuilder.getCurrentItem().getItemMeta()
					.setLeftItemText(" <span id=\"rando\"><table><tr><td align=left style=\"width:120px\">"
							.concat(crfBuilder.getMessage("randomizationResult")).concat("</td></tr></table></span>"));
			crfBuilder.getCurrentItem().getItemMeta().setRightItemText("");
			crfBuilder.getCurrentItem().getItemMeta().setQuestionNumberLabel("");
			crfBuilder.getCurrentItem().getItemMeta()
					.setSubHeader(crfBuilder.getCurrentItem().getItemMeta().getSubHeader().concat(
							" <table width=500px ><tr><td class=\"aka_ques_block\"> </td><td><span id=\"Button\"><input eleId=\"randomize\" type=\"button\"value=\"Randomize Subject\" width=\"40\" onclick=\"randomizeSubject()\"></span></td></tr></table>"));
			jsonObj.put(ITEM_NAME, RAND_DATE);
			jsonObj.put(TYPE, TEXT);
			jsonObj.put(LEFT_TEXT, "<table><tr><td align=\"left\" style=\"width:120px;\">"
					.concat(crfBuilder.getMessage("dateOfRandomization")).concat("</td></tr></table>"));
			jsonObj.put(RIGHT_TEXT, "");
			jsonObj.put(HEADER, "");
			jsonObj.put(DESCRIPTION, DATE_OF_RANDOMIZATION);
			jsonObj.put(POS, "");
			ItemBeanExt currentItem = new ItemBeanExt(crfBuilder.getCurrentItem());
			crfBuilder.setCurrentItem(currentItem);
			crfBuilder.getItems().add(crfBuilder.getCurrentItem());
			createItemBean(jsonObj, crfBuilder);
		}
	}

	private void createItemBean(JSONObject jsonObj, JsonCrfBuilder crfBuilder) throws Exception {
		crfBuilder.getCurrentItem().setUnits("");
		crfBuilder.getCurrentItem().setName(getString(jsonObj, ITEM_NAME));
		crfBuilder.getCurrentItem().setDescription(getString(jsonObj, DESCRIPTION));
		crfBuilder.getCurrentItem().setPhiStatus(getBoolean(jsonObj, PHI_DATA));
		crfBuilder.getCurrentItem().setItemReferenceTypeId(1);
		crfBuilder.getCurrentItem().setStatus(Status.AVAILABLE);
		crfBuilder.getCurrentItem().setOwner(crfBuilder.getOwner());
		crfBuilder.getCurrentItem().setItemDataTypeId(getItemDataTypeId(jsonObj, crfBuilder));
		crfBuilder.getCurrentItem().setSectionBean(crfBuilder.getCurrentSection());
		crfBuilder.getCurrentItem().setItemMeta(new ItemFormMetadataBean());
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.REQUIRED, getString(jsonObj, REQUIRED));
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.PHI, getString(jsonObj, PHI_DATA));

		createResponseSet(jsonObj, crfBuilder);

		createItemGroupMetadata(jsonObj, crfBuilder);

		createItemFormMetadata(jsonObj, crfBuilder);

		createSimpleConditionalDisplay(jsonObj, crfBuilder);

		generateRandomizationFields(jsonObj, crfBuilder);
	}

	private JSONArray getChildrenQuestions(JSONObject jsonObject, JsonCrfBuilder crfBuilder) throws Exception {
		JSONArray childrenQuestions = new JSONArray();
		String itemName = getString(jsonObject, ITEM_NAME);
		FormStudioElement type = FormStudioElement.getByName(getString(jsonObject, TYPE).toUpperCase());
		if (type == FormStudioElement.GRID) {
			int rows = getInt(jsonObject, ROWS);
			int columns = getInt(jsonObject, COLUMNS);
			JSONArray rowsArray = getJSONArray(jsonObject, CHILDREN);
			for (int r = 0; r < rows; r++) {
				JSONArray columnsArray = rowsArray.getJSONArray(r);
				for (int c = 0; c < columns; c++) {
					childrenQuestions.put(columnsArray.getJSONObject(c));
				}
			}
		} else if (type == FormStudioElement.TABLE) {
			int maxRows = getInt(jsonObject, MAX_ROWS);
			int minRows = getInt(jsonObject, MIN_ROWS);
			String groupHeader = getString(jsonObject, HEADER);
			if (isYes(jsonObject, NUMBERING)) {
				groupHeader = getString(jsonObject, POS).concat(". ").concat(groupHeader);
			}
			ItemGroupBean itemGroupBean = createItemGroup(itemName, crfBuilder);
			JSONArray elementsArray = getJSONArray(jsonObject, CHILDREN);
			for (int e = 0; e < elementsArray.length(); e++) {
				JSONObject elementObject = elementsArray.getJSONObject(e);
				elementObject.put(MAX_ROWS, maxRows);
				elementObject.put(MIN_ROWS, minRows);
				elementObject.put(REPEATING_GROUP, itemGroupBean);
				elementObject.put(REPEATING_GROUP_HEADER, groupHeader);
				elementObject.put(REPEATING_ITEM_GROUP_HEADER, getString(elementObject, HEADER));
				childrenQuestions.put(elementObject);
			}
		} else {
			childrenQuestions = getJSONArray(jsonObject, CHILDREN);
		}
		return childrenQuestions;
	}

	private void processQuestions(JSONArray questions, JsonCrfBuilder crfBuilder) throws Exception {
		processQuestions(questions, null, crfBuilder);
	}

	private void processQuestions(JSONArray questions, ItemBeanExt parentItemBean, JsonCrfBuilder crfBuilder)
			throws Exception {
		for (int i = 0; i < questions.length(); i++) {
			boolean resetHeader = false;

			ItemBeanExt currentItem = new ItemBeanExt();
			currentItem.setParentItemBean(parentItemBean);

			JSONObject jsonObj = questions.getJSONObject(i);
			FormStudioElement type = FormStudioElement.getByName(getString(jsonObj, TYPE).toUpperCase());

			if (type == FormStudioElement.DIVIDER) {
				crfBuilder.setCurrentHeader(getString(jsonObj, LEFT_TEXT));
			} else if (type != FormStudioElement.GRID && type != FormStudioElement.TABLE) {
				resetHeader = true;
				crfBuilder.setCurrentItem(currentItem);
				crfBuilder.getItems().add(crfBuilder.getCurrentItem());
				createItemBean(jsonObj, crfBuilder);
			}

			processQuestions(getChildrenQuestions(jsonObj, crfBuilder), currentItem, crfBuilder);

			if (resetHeader) {
				crfBuilder.setCurrentHeader("");
			}
		}
	}

	private void processJson(JsonCrfBuilder crfBuilder) throws Exception {
		String crfName = getString(crfBuilder.getJsonObject(), NAME);

		crfBuilder.getCrfBean().setName(crfName);
		crfBuilder.getCrfBean().setDescription(crfName);
		crfBuilder.getCrfBean().setStatus(Status.AVAILABLE);
		crfBuilder.getCrfBean().setOwner(crfBuilder.getOwner());
		crfBuilder.getCrfBean().setStudyId(crfBuilder.getStudyBean().getId());

		crfBuilder.getCrfVersionBean().setDescription(crfName);
		crfBuilder.getCrfVersionBean().setRevisionNotes(crfName);
		crfBuilder.getCrfVersionBean().setStatus(Status.AVAILABLE);
		crfBuilder.getCrfVersionBean().setOwner(crfBuilder.getOwner());
		crfBuilder.getCrfVersionBean().setCrfId(crfBuilder.getCrfBean().getId());
		crfBuilder.getCrfVersionBean().setName(getString(crfBuilder.getJsonObject(), VERSION));

		processPages(crfBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void importNewCrf(BaseCrfBuilder crfBuilder) throws Exception {
		crfBuilder.setOperationType(OperationType.IMPORT_NEW_CRF);
		processJson((JsonCrfBuilder) crfBuilder);
		CommonValidator.validate(crfBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void importNewCrfVersion(BaseCrfBuilder crfBuilder, int crfId) throws Exception {
		crfBuilder.getCrfBean().setId(crfId);
		crfBuilder.setOperationType(OperationType.IMPORT_NEW_CRF_VERSION);
		processJson((JsonCrfBuilder) crfBuilder);
		CommonValidator.validate(crfBuilder);
	}
}
