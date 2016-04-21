/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.service.impl;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.SasNameValidator;
import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.domain.datamap.ResponseSet;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.impl.BaseCrfBuilder;
import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.OperationType;
import com.clinovo.lib.crf.enums.RealValueKey;
import com.clinovo.lib.crf.enums.SheetName;
import com.clinovo.lib.crf.validator.CommonValidator;
import com.clinovo.lib.crf.validator.WorksheetValidator;
import com.clinovo.util.CodingFieldsUtil;

/**
 * SpreadSheetServiceImpl.
 */
@Service
@SuppressWarnings({"unchecked", "unused"})
public class SpreadSheetImportCrfServiceImpl extends BaseImportCrfService {

	public static final Logger LOGGER = LoggerFactory.getLogger(SpreadSheetImportCrfServiceImpl.class);

	@Autowired
	private DataSource dataSource;

	private boolean isBlank(String value) {
		return StringUtil.isBlank(value);
	}

	private boolean isInt(String value) {
		boolean result = true;
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			result = false;
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private int toInt(String value) {
		int result = 0;
		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}

	private void buildHtml(ExcelCrfBuilder crfBuilder) {
		crfBuilder.getHtmlBuffer().append(crfBuilder.getCurrentSheetName()).append("<br>");
		crfBuilder.getHtmlBuffer().append(
				"<div class=\"box_T\"><div class=\"box_L\"><div class=\"box_R\"><div class=\"box_B\"><div class=\"box_TL\"><div class=\"box_TR\"><div class=\"box_BL\"><div class=\"box_BR\">");

		crfBuilder.getHtmlBuffer().append("<div class=\"textbox_center\">");
		crfBuilder.getHtmlBuffer().append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"");
		crfBuilder.getHtmlBuffer().append("caption=\"").append(crfBuilder.getCurrentSheetName()).append("\"")
				.append(">");

		for (int i = 0; i < crfBuilder.getNumRows(); i++) {
			crfBuilder.getHtmlBuffer().append("<tr>");
			if (crfBuilder.getCurrentSheet().getRow(i) == null) {
				continue;
			}

			int numCells = crfBuilder.getCurrentSheet().getRow(i).getLastCellNum();

			for (int y = 0; y < numCells; y++) {
				int cellType;
				Cell cell = crfBuilder.getCurrentSheet().getRow(i).getCell(y);
				String errorKey = crfBuilder.getCurrentSheetNumber() + "," + i + "," + y;
				String errorHolder = "<![CDATA[" + errorKey + "]]>";
				if (cell == null) {
					cellType = Cell.CELL_TYPE_BLANK;
				} else {
					cellType = cell.getCellType();
				}
				switch (cellType) {
					case Cell.CELL_TYPE_BLANK :
						crfBuilder.getHtmlBuffer().append("<td class=\"table_cell\">").append(errorHolder)
								.append("&nbsp;</td>");
						break;
					case Cell.CELL_TYPE_NUMERIC :
						crfBuilder.getHtmlBuffer().append("<td class=\"table_cell\">")
								.append(cell.getNumericCellValue()).append(" ").append(errorHolder)
								.append("&nbsp;</td>");
						break;
					case Cell.CELL_TYPE_STRING :
						crfBuilder.getHtmlBuffer().append("<td class=\"table_cell\">").append(cell.getStringCellValue())
								.append(" ").append(errorHolder).append("&nbsp;</td>");
						break;
					default :
						crfBuilder.getHtmlBuffer().append("<td class=\"table_cell\">").append(errorHolder)
								.append("&nbsp;</td>");
				}
			}
			crfBuilder.getHtmlBuffer().append("</tr>");
		}
		crfBuilder.getHtmlBuffer().append("</table>");
		crfBuilder.getHtmlBuffer().append("<br></div>");
		crfBuilder.getHtmlBuffer().append("</div></div></div></div></div></div></div></div>");
		crfBuilder.getHtmlBuffer().append("</div><br>");
	}

	private void processCrfSheet(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.CRF.getSheetNumber());
		if (crfBuilder.hasNextRow()) {
			String crfName = crfBuilder.getCellValue(CellName.CRF_NAME, true);
			crfBuilder.getCrfBean().setName(crfName);
			crfBuilder.getCrfBean().setDescription(crfName);
			crfBuilder.getCrfBean().setStatus(Status.AVAILABLE);
			crfBuilder.getCrfBean().setOwner(crfBuilder.getOwner());
			crfBuilder.getCrfBean().setStudyId(crfBuilder.getStudyBean().getId());
			crfBuilder.getCrfVersionBean().setName(crfBuilder.getCellValue(CellName.CRF_VERSION));
			crfBuilder.getCrfVersionBean()
					.setDescription(crfBuilder.getCellValue(CellName.CRF_VERSION_DESCRIPTION, true));
			crfBuilder.getCrfVersionBean().setStatus(Status.AVAILABLE);
			crfBuilder.getCrfVersionBean().setRevisionNotes(crfBuilder.getCellValue(CellName.CRF_REVISION_NOTES, true));
			crfBuilder.getCrfVersionBean().setOwner(crfBuilder.getOwner());
		}
		buildHtml(crfBuilder);
	}

	private void processSectionsSheet(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.SECTIONS.getSheetNumber());
		while (crfBuilder.hasNextRow()) {
			String label = crfBuilder.getCellValue(CellName.SECTION_LABEL, true);
			crfBuilder.setCurrentSection(newSectionBean(crfBuilder));
			crfBuilder.getCurrentSection().setInstructions(crfBuilder.getCellValue(CellName.SECTION_INSTRUCTIONS));
			crfBuilder.getCurrentSection().setSubtitle(crfBuilder.getCellValue(CellName.SECTION_SUBTITLE, false));
			crfBuilder.getCurrentSection().setTitle(crfBuilder.getCellValue(CellName.SECTION_TITLE, true));
			crfBuilder.getCurrentSection().setLabel(label);
			// we do not support parent sections
			crfBuilder.getCurrentSection().setParentId(0);
			crfBuilder.getCurrentSection().setBorders(toInt(crfBuilder.getCellValue(CellName.SECTION_BORDERS, true)));
			crfBuilder.getCurrentSection().setPageNumberLabel(crfBuilder.getCellValue(CellName.SECTION_PAGE_NUMBER));
			crfBuilder.getCurrentSection().setStatus(Status.AVAILABLE);
			crfBuilder.getCurrentSection().setOrdinal(crfBuilder.getNextSectionOrdinal());
			crfBuilder.getCurrentSection().setOwner(crfBuilder.getOwner());
			crfBuilder.getSections().add(crfBuilder.getCurrentSection());
			crfBuilder.getSectionLabelMap().put(label, crfBuilder.getCurrentSection());
		}
	}

	private void processGroupsSheet(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.GROUPS.getSheetNumber());

		ItemGroupBean itemGroupBean = newItemGroupBean(crfBuilder);
		itemGroupBean.setStatus(Status.AVAILABLE);
		itemGroupBean.setOwner(crfBuilder.getOwner());
		itemGroupBean.setName(UNGROUPED);
		itemGroupBean.setMeta(new ItemGroupMetadataBean());
		crfBuilder.getItemGroups().add(itemGroupBean);
		crfBuilder.setDefaultItemGroupBean(itemGroupBean);
		crfBuilder.getItemGroupLabelMap().put(itemGroupBean.getName(), itemGroupBean);

		itemGroupBean.getMeta().setBorders(0);
		itemGroupBean.getMeta().setHeader("");
		itemGroupBean.getMeta().setSubheader("");
		itemGroupBean.getMeta().setShowGroup(true);
		itemGroupBean.getMeta().setRowStartNumber(0);
		itemGroupBean.getMeta().setRepeatingGroup(false);
		crfBuilder.getItemGroupLabelToMetaMap().put(itemGroupBean.getName(), itemGroupBean.getMeta());

		while (crfBuilder.hasNextRow()) {
			String label = crfBuilder.getCellValue(CellName.GROUP_LABEL, true);

			String groupHeader = crfBuilder.getCellValue(CellName.GROUP_HEADER);
			groupHeader = org.akaza.openclinica.core.form.StringUtil.escapeSingleQuote(groupHeader);

			String groupRepeatNumber = crfBuilder.getCellValue(CellName.GROUP_REPEAT_NUMBER);
			groupRepeatNumber = isBlank(groupRepeatNumber) ? ONE : groupRepeatNumber;

			String groupRepeatMax = crfBuilder.getCellValue(CellName.GROUP_REPEAT_MAX);
			groupRepeatMax = isBlank(groupRepeatMax) ? FORTY : groupRepeatMax;

			boolean isShowGroup = true;
			String showGroup = crfBuilder.getCellValue(CellName.GROUP_DISPLAY_STATUS);
			if (!StringUtil.isBlank(showGroup)) {
				try {
					isShowGroup = !ZERO.equals(showGroup);
					isShowGroup = !HIDE.equalsIgnoreCase(showGroup);
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
			}

			itemGroupBean = newItemGroupBean(crfBuilder);
			itemGroupBean.setStatus(Status.AVAILABLE);
			itemGroupBean.setOwner(crfBuilder.getOwner());
			itemGroupBean.setName(label);
			itemGroupBean.setMeta(new ItemGroupMetadataBean());
			crfBuilder.getItemGroups().add(itemGroupBean);
			crfBuilder.getItemGroupLabelMap().put(itemGroupBean.getName(), itemGroupBean);

			itemGroupBean.getMeta().setBorders(0);
			itemGroupBean.getMeta().setSubheader("");
			itemGroupBean.getMeta().setRowStartNumber(0);
			itemGroupBean.getMeta().setHeader(groupHeader);
			itemGroupBean.getMeta().setRepeatingGroup(true);
			itemGroupBean.getMeta().setShowGroup(isShowGroup);
			itemGroupBean.getMeta().setRepeatMax(toInt(groupRepeatMax));
			itemGroupBean.getMeta().setRepeatNum(toInt(groupRepeatNumber));
			crfBuilder.getItemGroupLabelToMetaMap().put(itemGroupBean.getName(), itemGroupBean.getMeta());
		}
		buildHtml(crfBuilder);
	}

	private void prepareResponseSet(ExcelCrfBuilder crfBuilder) throws Exception {
		String responseTypeValue = crfBuilder.getCellValue(CellName.ITEM_RESPONSE_TYPE).toLowerCase();
		ResponseType responseType = ResponseType.findByName(responseTypeValue)
				? ResponseType.getByName(responseTypeValue)
				: ResponseType.TEXT;

		String responseLabel = crfBuilder.getCellValue(CellName.ITEM_RESPONSE_LABEL);

		String responseOptions = crfBuilder.reformat(crfBuilder.getCellValue(CellName.ITEM_RESPONSE_OPTIONS_TEXT));

		String responseValues = crfBuilder
				.reformat(crfBuilder.getCellValue(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS));

		if (responseType.getId() == ResponseType.TEXT.getId() || responseType.getId() == ResponseType.TEXTAREA.getId()
				|| responseType.getId() == ResponseType.FILE.getId()) {
			responseLabel = responseType.getCode();
			responseValues = responseType.getCode();
			responseOptions = responseType.getCode();
			crfBuilder.getRow().createCell(crfBuilder.getColumnNumber(CellName.ITEM_RESPONSE_LABEL))
					.setCellValue(responseType.getCode().toLowerCase() + AUTO_CHANGED_CELL);
			crfBuilder.getRow().createCell(crfBuilder.getColumnNumber(CellName.ITEM_RESPONSE_OPTIONS_TEXT))
					.setCellValue(responseType.getCode().toLowerCase() + AUTO_CHANGED_CELL);
			crfBuilder.getRow().createCell(crfBuilder.getColumnNumber(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS))
					.setCellValue(responseType.getCode().toLowerCase() + AUTO_CHANGED_CELL);
		}

		if (responseType.getId() == ResponseType.INSTANT_CALCULATION.getId()) {
			crfBuilder.getCurrentItem().setUnits("");
		}

		if (responseType.getId() == ResponseType.CALCULATION.getId()
				|| responseType.getId() == ResponseType.GROUP_CALCULATION.getId()) {
			responseLabel = responseType.getCode();
			responseOptions = responseType.getCode();
			crfBuilder.getRow().createCell(crfBuilder.getColumnNumber(CellName.ITEM_RESPONSE_LABEL))
					.setCellValue(responseType.getCode().toLowerCase() + AUTO_CHANGED_CELL);
			crfBuilder.getRow().createCell(crfBuilder.getColumnNumber(CellName.ITEM_RESPONSE_OPTIONS_TEXT))
					.setCellValue(responseType.getCode().toLowerCase() + AUTO_CHANGED_CELL);
		}

		crfBuilder.getCurrentItem().setResponseSet(new ResponseSet());
		crfBuilder.getCurrentItem().getResponseSet().setLabel(responseLabel);
		crfBuilder.getCurrentItem().getResponseSet().setOptionsText(responseOptions);
		crfBuilder.getCurrentItem().getResponseSet().setOptionsValues(responseValues);
		crfBuilder.getCurrentItem().getResponseSet().setResponseType(newResponseType(responseType));
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.RESPONSE_TYPE, responseTypeValue);
	}

	private void prepareItemFormMetadata(ExcelCrfBuilder crfBuilder) throws Exception {
		String groupLabel = crfBuilder.getCellValue(CellName.ITEM_GROUP_LABEL);

		String parentItemName = crfBuilder.getCellValue(CellName.ITEM_PARENT_ITEM);

		String required = crfBuilder.getCellValue(CellName.ITEM_REQUIRED);
		required = StringUtil.isBlank(required) ? ZERO : required;
		boolean isRequired = required.equals(ONE);

		String showItem = crfBuilder.getCellValue(CellName.ITEM_DISPLAY_STATUS);
		boolean isShowItem = !showItem.equalsIgnoreCase(HIDE);

		String codeRef = crfBuilder.getCellValue(CellName.ITEM_CODE_REF);

		crfBuilder.getCurrentItem().getItemMeta().setHeader(crfBuilder.getCellValue(CellName.ITEM_HEADER));
		crfBuilder.getCurrentItem().getItemMeta().setSubHeader(crfBuilder.getCellValue(CellName.ITEM_SUBHEADER));
		if (!StringUtil.isBlank(parentItemName)
				&& !parentItemName.equalsIgnoreCase(crfBuilder.getCurrentItem().getName())) {
			crfBuilder.getCurrentItem().getItemMeta().setParentLabel(parentItemName);
			ItemBeanExt itemBean = crfBuilder.getItemNameToItemMap().get(parentItemName);
			if (itemBean != null) {
				crfBuilder.getCurrentItem().setParentItemBean(itemBean);
				crfBuilder.getCurrentItem().getItemMeta().setParentId(itemBean.getId());
			}
		}
		crfBuilder.getCurrentItem().getItemMeta()
				.setGroupLabel(StringUtil.isBlank(groupLabel) ? UNGROUPED : groupLabel);
		crfBuilder.getCurrentItem().getItemMeta()
				.setColumnNumber(toInt(crfBuilder.getCellValue(CellName.ITEM_COLUMN_NUMBER)));
		crfBuilder.getCurrentItem().getItemMeta()
				.setQuestionNumberLabel(crfBuilder.getCellValue(CellName.ITEM_QUESTION_NUMBER));
		crfBuilder.getCurrentItem().getItemMeta()
				.setPageNumberLabel(crfBuilder.getCellValue(CellName.ITEM_PAGE_NUMBER));
		crfBuilder.getCurrentItem().getItemMeta()
				.setLeftItemText(crfBuilder.getCellValue(CellName.ITEM_LEFT_ITEM_TEXT));
		crfBuilder.getCurrentItem().getItemMeta()
				.setRightItemText(crfBuilder.getCellValue(CellName.ITEM_RIGHT_ITEM_TEXT));
		crfBuilder.getCurrentItem().getItemMeta().setRegexp(crfBuilder.getCellValue(CellName.ITEM_VALIDATION));
		crfBuilder.getCurrentItem().getItemMeta()
				.setRegexpErrorMsg(crfBuilder.getCellValue(CellName.ITEM_VALIDATION_ERROR_MESSAGE, true));
		crfBuilder.getCurrentItem().getItemMeta().setOrdinal(crfBuilder.getNextItemMetadataOrdinal());
		crfBuilder.getCurrentItem().getItemMeta().setRequired(isRequired);
		crfBuilder.getCurrentItem().getItemMeta().setDefaultValue(crfBuilder.getCellValue(CellName.ITEM_DEFAULT_VALUE));
		crfBuilder.getCurrentItem().getItemMeta()
				.setResponseLayout(crfBuilder.getCellValue(CellName.ITEM_RESPONSE_LAYOUT, true));
		crfBuilder.getCurrentItem().getItemMeta().setShowItem(isShowItem);
		crfBuilder.getCurrentItem().getItemMeta().setCodeRef(codeRef);
		crfBuilder.getCurrentItem().getItemMeta().setWidthDecimal(crfBuilder.getCellValue(CellName.ITEM_WIDTH_DECIMAL));
		crfBuilder.getCurrentItem().getItemMeta().setSectionName(crfBuilder.getCellValue(CellName.ITEM_SECTION_LABEL));
		crfBuilder.getItemNameToMetaMap().put(crfBuilder.getCurrentItem().getName(),
				crfBuilder.getCurrentItem().getItemMeta());
		crfBuilder.getCurrentItem().setRealValue(RealValueKey.REQUIRED, required);

	}

	private void prepareCurrent(ExcelCrfBuilder crfBuilder) {
		String itemName = crfBuilder.getCellValue(CellName.ITEM_NAME, true);
		ItemBeanExt itemBean = (ItemBeanExt) crfBuilder.getItems().get(crfBuilder.getIndex());
		crfBuilder.setCurrentSection(itemBean.getSectionBean());
		crfBuilder.setCurrentItem(itemBean);
	}

	private void prepareItems(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.ITEMS.getSheetNumber());
		while (crfBuilder.hasNextRow()) {
			String itemName = crfBuilder.getCellValue(CellName.ITEM_NAME, true);
			String validSasName = new SasNameValidator().getValidName(itemName);

			crfBuilder.setCurrentSection(
					crfBuilder.getSectionLabelMap().get(crfBuilder.getCellValue(CellName.ITEM_SECTION_LABEL)));

			crfBuilder.setCurrentItem(new ItemBeanExt(crfBuilder));
			crfBuilder.getCurrentItem().setSectionBean(crfBuilder.getCurrentSection());
			crfBuilder.getCurrentItem().setItemMeta(new ItemFormMetadataBean());
			crfBuilder.getItems().add(crfBuilder.getCurrentItem());

			String groupLabel = crfBuilder.getCellValue(CellName.ITEM_GROUP_LABEL);
			crfBuilder.getCurrentItem()
					.setItemGroupBean(StringUtil.isBlank(groupLabel)
							? crfBuilder.getItemGroupLabelMap().get(UNGROUPED)
							: crfBuilder.getItemGroupLabelMap().get(groupLabel));

			if (crfBuilder.getCurrentItem().getItemGroupBean() != null) {
				crfBuilder.getCurrentItem().getItemGroupBean()
						.setMeta(StringUtil.isBlank(groupLabel)
								? crfBuilder.getItemGroupLabelToMetaMap().get(UNGROUPED)
								: crfBuilder.getItemGroupLabelToMetaMap().get(groupLabel));
			}

			String dataTypeValue = crfBuilder.getCellValue(CellName.ITEM_DATA_TYPE, true).toLowerCase();
			int itemDataTypeId = ItemDataType.findByName(dataTypeValue)
					? ItemDataType.getByName(dataTypeValue).getId()
					: ItemDataType.ST.getId();

			String phi = crfBuilder.getCellValue(CellName.ITEM_PHI);
			phi = StringUtil.isBlank(phi) ? ZERO : phi;
			boolean phiBoolean = phi.equals(ONE);

			String itemCodeRef = crfBuilder.getCellValue(CellName.ITEM_CODE_REF);
			if (CodingFieldsUtil.getEnumAsList(itemCodeRef) != null) {
				crfBuilder.getCodingRefItemNames().add(itemName);
			}

			crfBuilder.getCurrentItem().setName(itemName);
			crfBuilder.getCurrentItem().setPhiStatus(phiBoolean);
			crfBuilder.getCurrentItem().setSasName(validSasName);
			crfBuilder.getCurrentItem().setUnits(crfBuilder.getCellValue(CellName.ITEM_UNITS));
			crfBuilder.getCurrentItem().setDescription(crfBuilder.getCellValue(CellName.ITEM_DESCRIPTION_LABEL, true));
			crfBuilder.getCurrentItem().setItemReferenceTypeId(1);
			crfBuilder.getCurrentItem().setStatus(Status.AVAILABLE);
			crfBuilder.getCurrentItem().setOwner(crfBuilder.getOwner());
			crfBuilder.getCurrentItem().setItemDataTypeId(itemDataTypeId);
			crfBuilder.getCurrentItem().getItemMeta()
					.setWidthDecimal(crfBuilder.getCellValue(CellName.ITEM_WIDTH_DECIMAL));
			crfBuilder.getCurrentItem().setRealValue(RealValueKey.ITEM_DATA_TYPE, dataTypeValue);
			crfBuilder.getCurrentItem().setRealValue(RealValueKey.PHI, phi);
			crfBuilder.getItemNameToItemMap().put(crfBuilder.getCurrentItem().getName(), crfBuilder.getCurrentItem());
		}
	}

	private void prepareMeta(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.ITEMS.getSheetNumber());
		while (crfBuilder.hasNextRow()) {
			prepareCurrent(crfBuilder);
			prepareResponseSet(crfBuilder);
			prepareItemFormMetadata(crfBuilder);
		}
	}

	private void prepareSimpleConditionalDisplay(ExcelCrfBuilder crfBuilder) throws Exception {
		crfBuilder.goToSheet(SheetName.ITEMS.getSheetNumber());
		while (crfBuilder.hasNextRow()) {
			prepareCurrent(crfBuilder);
			String simpleConditionalDisplay = crfBuilder.getCellValue(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY);
			crfBuilder.getCurrentItem().setRealValue(RealValueKey.SCD_DATA, simpleConditionalDisplay);
			simpleConditionalDisplay = crfBuilder.reformat(simpleConditionalDisplay);
			String[] values = crfBuilder.split(simpleConditionalDisplay, ",");
			if (values.length == INT_3) {
				String controlItemName = values[0] != null ? values[0].trim() : "";
				String optionValue = values[1] != null ? values[1].trim() : "";
				String message = values[2] != null ? values[2].trim() : "";

				crfBuilder.getCurrentItem().setSimpleConditionalDisplayBean(new SimpleConditionalDisplayBean());
				crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setControlItemName(controlItemName);
				crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setOptionValue(optionValue);
				crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setMessage(message);
			}
		}
	}

	private void processItemsSheet(ExcelCrfBuilder crfBuilder) throws Exception {
		prepareItems(crfBuilder);
		prepareMeta(crfBuilder);
		prepareSimpleConditionalDisplay(crfBuilder);
		buildHtml(crfBuilder);
	}

	private void processWorkbook(ExcelCrfBuilder crfBuilder) throws Exception {
		processCrfSheet(crfBuilder);
		processSectionsSheet(crfBuilder);
		processGroupsSheet(crfBuilder);
		processItemsSheet(crfBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void importNewCrf(BaseCrfBuilder crfBuilder) throws Exception {
		crfBuilder.setOperationType(crfBuilder.getCrfBean().getId() > 0
				? OperationType.IMPORT_NEW_CRF_VERSION
				: OperationType.IMPORT_NEW_CRF);
		WorksheetValidator.validate((ExcelCrfBuilder) crfBuilder);
		processWorkbook((ExcelCrfBuilder) crfBuilder);
		CommonValidator.validate(crfBuilder);
	}

	/**
	 * {@inheritDoc}
	 */
	public void importNewCrfVersion(BaseCrfBuilder crfBuilder, int crfId) throws Exception {
		crfBuilder.getCrfBean().setId(crfId);
		importNewCrf(crfBuilder);
	}
}
