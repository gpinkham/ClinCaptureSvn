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
 * copyright 2003-2011 Akaza Research
 */

package org.akaza.openclinica.control.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.ApplicationConstants;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.core.util.CrfTemplateColumnNameEnum;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.submit.ItemDAO;

/**
 * This util is used to generate CRF from Excel.
 */
public class SpreadSheetItemUtil {

	private String itemName; // 1
	private String descriptionLabel; // 2
	private String leftItemText; // 3
	private String sectionLabel; // 6
	private String groupLabel; // 7
	private String parentItem; // 10
	private int responseTypeId; // 14
	private String[] responseOptions;
	private String defaultValue; // 19
	private String dataType;

	private static final int MAX_SECTION_NAME_LENGTH = 2000;
	private static final int MAX_ITEM_NAME_LENGTH = 255;

	/**
	 * Default constructor.
	 */
	public SpreadSheetItemUtil() {
	}

	private String cleanProperty(String property) {
		if (property == null) {
			property = "";
		}
		property = property.trim();
		return property.replaceAll("<[^>]*>", "");

	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName
	 *            the itemName to set
	 */
	public void setItemName(String itemName) {
		itemName = cleanProperty(itemName);
		this.itemName = itemName;
	}

	/**
	 * @return the descriptionLabel
	 */
	public String getDescriptionLabel() {
		return descriptionLabel;
	}

	/**
	 * @param descriptionLabel
	 *            the descriptionLabel to set
	 */
	public void setDescriptionLabel(String descriptionLabel) {
		this.descriptionLabel = descriptionLabel;
	}

	/**
	 * @return the left_item_text
	 */
	public String getLeftItemText() {
		return leftItemText;
	}

	/**
	 * @param leftItemText
	 *            the left_item_text to set
	 */
	public void setLeftItemText(String leftItemText) {
		this.leftItemText = leftItemText;
	}

	/**
	 * @return the sectionLabel
	 */
	public String getSectionLabel() {
		return sectionLabel;
	}

	/**
	 * @param sectionLabel
	 *            the section_label to set
	 */
	public void setSectionLabel(String sectionLabel) {
		this.sectionLabel = cleanProperty(sectionLabel);

	}

	/**
	 * @return the group_label
	 */
	public String getGroupLabel() {
		return groupLabel;
	}

	/**
	 * @param groupLabel
	 *            the group_label to set
	 */
	public void setGroupLabel(String groupLabel) {
		this.groupLabel = cleanProperty(groupLabel);
	}

	/**
	 * @return the parent_item
	 */
	public String getParentItem() {
		return parentItem;
	}

	/**
	 * @param parentItem
	 *            the parentItem to set
	 */
	public void setParentItem(String parentItem) {
		this.parentItem = cleanProperty(parentItem);

	}

	/**
	 * @return the responseType
	 */
	public int getResponseTypeId() {
		return responseTypeId;
	}

	/**
	 * @param responseTypeId
	 *            the response_type_id to set
	 */
	public void setResponseTypeId(int responseTypeId) {
		this.responseTypeId = responseTypeId;
	}

	/**
	 * @return the default_value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the default_value to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = cleanProperty(defaultValue);
	}

	/**
	 * @return the data_type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the data_type to set
	 */
	public void setDataType(String dataType) {
		this.dataType = cleanProperty(dataType);
	}

	/**
	 * This method is used to check if item with the same name exists.
	 *
	 * @param columnValue
	 *            the value of the column
	 * @param rowItems
	 *            the List of the items
	 * @return the boolean result of the check.
	 */
	public static boolean isItemWithSameParameterExists(String columnValue, List<SpreadSheetItemUtil> rowItems) {
		if (rowItems == null || rowItems.size() == 1) {
			return false;
		}
		SpreadSheetItemUtil item = isItemWithSameParameterExists(CrfTemplateColumnNameEnum.ITEM_NAME, columnValue,
				rowItems, false);
		return (item != null);
	}

	/**
	 * This method is used to check if items with the same name exists including original copies of the items (if they
	 * exists in the DB).
	 *
	 * @param columnValue
	 *            the value of the column
	 * @param rowItems
	 *            the List of the items
	 * @return the boolean result of the check.
	 */
	public static boolean isItemWithSameParameterExistsIncludingMyself(String columnValue,
			List<SpreadSheetItemUtil> rowItems) {
		if (rowItems == null || rowItems.size() == 1) {
			return true;
		}
		SpreadSheetItemUtil item = isItemWithSameParameterExists(CrfTemplateColumnNameEnum.ITEM_NAME, columnValue,
				rowItems, true);
		return (item != null);
	}

	/**
	 * This method is used to check if item with the same parameter exists.
	 *
	 * @param paramColumnIndex
	 *            the index of the column in Excel.
	 * @param columnValue
	 *            the value in the column.
	 * @param rowItems
	 *            the list of the Items.
	 * @return the boolean result of the check.
	 */
	public static SpreadSheetItemUtil isItemWithSameParameterExists(CrfTemplateColumnNameEnum paramColumnIndex,
			String columnValue, List<SpreadSheetItemUtil> rowItems) {
		if (rowItems == null || rowItems.size() == 1) {
			return null;
		}
		return isItemWithSameParameterExists(paramColumnIndex, columnValue, rowItems, false);
	}

	/**
	 * This method is used to check if item with the same parameter exists.
	 *
	 * @param paramColumnIndex
	 *            the index of the column in Excel.
	 * @param columnValue
	 *            the value in the column.
	 * @param rowItems
	 *            the list of the Items.
	 * @param isIncludingMyself
	 *            the boolean variable - should we compare item with himself.
	 * @return the boolean result of the check.
	 */
	public static SpreadSheetItemUtil isItemWithSameParameterExists(CrfTemplateColumnNameEnum paramColumnIndex,
			String columnValue, List<SpreadSheetItemUtil> rowItems, boolean isIncludingMyself) {

		int lastItemToCheck = 0;
		// current item should not be included in evaluation
		for (SpreadSheetItemUtil curItem : rowItems) {
			if (!isIncludingMyself) {
				if (lastItemToCheck == rowItems.size() - 1) {
					break;
				}
			}
			lastItemToCheck++;
			switch (paramColumnIndex) {
				case ITEM_NAME : {
					if (curItem.getItemName().equals(columnValue)) {
						return curItem;
					}
					break;
				}
				default :
					break;
			}
		}
		return null;
	}

	public static void verifySectionGroupPlacementForItems(List<SpreadSheetItemUtil> rowItems, List<String> verErrors,
			Map<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg,
			Map<String, ItemGroupBean> itemGroups) {

		HashMap<String, String> groupSectionMap = new HashMap<String, String>();
		String sectionLabel;
		int rowNumber = 1;
		for (SpreadSheetItemUtil curItem : rowItems) {
			rowNumber++;
			if (curItem.getGroupLabel().length() < 1) {
				continue;
			}
			// verify that this is repeating group
			ItemGroupBean itemGroup = itemGroups.get(curItem.getGroupLabel());
			boolean isRepeatingGroup = false;
			if (itemGroup == null) {
				// case when item has a group not listed in 'Groups' spreadSheet, error was processed before
			} else {
				isRepeatingGroup = itemGroup.getMeta().isRepeatingGroup();
			}
			if (!isRepeatingGroup) {
				continue;
			}
			sectionLabel = groupSectionMap.get(curItem.getGroupLabel());
			if (sectionLabel != null) { // not first item in group
				if (!sectionLabel.equals(curItem.getSectionLabel())) {
					// error: items of one group belong to more than one section
					verErrors.add(resPageMsg.getString("group_in_several_sections") + curItem.getGroupLabel() + "'.");
					htmlErrors.put(
							sheetNumber + "," + (rowNumber - 1) + ","
									+ CrfTemplateColumnNameEnum.GROUP_LABEL.getCellNumber(),
							resPageMsg.getString("INVALID_VALUE"));
				}
			} else {
				// first item in group
				groupSectionMap.put(curItem.getGroupLabel(), curItem.getSectionLabel());
			}
		}
	}

	public void verifyParentID(ArrayList<SpreadSheetItemUtil> rowItems, ArrayList<String> verErrors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg,
			HashMap<String, ItemGroupBean> itemGroups) {

		int rowNumber = rowItems.size();
		// BWP>>Prevent parent names that equal the Item names
		if (this.getItemName().equalsIgnoreCase(this.getParentItem())) {
			this.setParentItem("");
		}
		if (!this.getParentItem().isEmpty()) {
			SpreadSheetItemUtil curItem = SpreadSheetItemUtil
					.isItemWithSameParameterExists(CrfTemplateColumnNameEnum.ITEM_NAME, this.getParentItem(), rowItems);
			// Checking for a valid parent item name
			if (curItem == null) {
				verErrors.add(resPageMsg.getString("parent_id") + rowNumber + resPageMsg.getString("parent_id_1"));
				htmlErrors.put(
						sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
			}
			// prevent more than one level of hierarchy for parent names (new ver)
			if (curItem != null && curItem.getParentItem() != null && curItem.getParentItem().length() > 0) {
				verErrors.add(resPageMsg.getString("nested_parent_id") + rowItems.size()
						+ resPageMsg.getString("nested_parent_id_1"));
				htmlErrors.put(
						sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
			}
			// prevent item in RGroup to have parent id (new ver)
			// verify that this is repeating group
			if (itemGroups != null && itemGroups.size() > 0) {
				ItemGroupBean itemGroup = itemGroups.get(this.getGroupLabel());
				if (itemGroup != null) {
					boolean isRepeatingGroup = itemGroup.getMeta().isRepeatingGroup();
					if (isRepeatingGroup) {
						if (this.getParentItem().length() > 0) {
							verErrors.add(resPageMsg.getString("parentId_group") + rowItems.size()
									+ resPageMsg.getString("nested_parent_id_1"));
							htmlErrors.put(
									sheetNumber + "," + rowNumber + ","
											+ CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
									resPageMsg.getString("INVALID_FIELD"));
						}
					}
				}
			}
		}
	}

	public void verifySectionLabel(ArrayList<SpreadSheetItemUtil> rowItems, ArrayList<String> verErrors,
			ArrayList<String> secNames, HashMap<String, String> htmlErrors, int sheetNumber,
			ResourceBundle resPageMsg) {
		int rowNumber = rowItems.size();
		StringBuffer str = new StringBuffer();
		if (this.getSectionLabel().length() == 0) {
			str.append(resPageMsg.getString("the") + " ");
			str.append(resPageMsg.getString("SECTION_LABEL_column") + " ");
			str.append(resPageMsg.getString("not_valid_section_at_row") + " ");
			str.append(rowNumber + ", " + resPageMsg.getString("items_worksheet_with_dot"));
			str.append(" " + resPageMsg.getString("check_to_see_that_there_is_valid_LABEL"));
			verErrors.add(str.toString());
			htmlErrors.put(
					sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
					resPageMsg.getString("NOT_A_VALID_LABEL"));
		} else {
			if (this.getSectionLabel().length() > MAX_SECTION_NAME_LENGTH) {
				verErrors.add(resPageMsg.getString("section_label_length_error"));
				htmlErrors.put(
						sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
						resPageMsg.getString("NOT_A_VALID_LABEL"));
			}
			if (!secNames.contains(this.getSectionLabel())) {
				if (str.length() == 0) {
					str.append(resPageMsg.getString("the") + " ");
					str.append(resPageMsg.getString("SECTION_LABEL_column") + " ");
					str.append(resPageMsg.getString("not_valid_section_at_row") + " ");
					str.append(rowNumber + ", " + resPageMsg.getString("items_worksheet_with_dot"));
					str.append(" " + resPageMsg.getString("check_to_see_that_there_is_valid_LABEL"));
				}
				verErrors.add(str.toString());
				htmlErrors.put(
						sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
						resPageMsg.getString("NOT_A_VALID_LABEL"));
			}
		}
	}

	public void verifyItemName(ArrayList<SpreadSheetItemUtil> rowItems, ArrayList<String> verErrors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg) {

		int k = rowItems.size();
		String itemName = this.getItemName();
		// regexp to make sure it is all word characters, '\w+' in regexp terms
		if (!Utils.isMatchingRegexp(itemName, "\\w+")) {
			// different item error to go here
			verErrors.add(resPageMsg.getString("item_name_column") + " " + resPageMsg.getString("was_invalid_at_row")
					+ " " + k + ", " + resPageMsg.getString("items_worksheet_with_dot") + " "
					+ resPageMsg.getString("you_can_only_use_letters_or_numbers"));
			htmlErrors.put(sheetNumber + "," + k + ",0", resPageMsg.getString("INVALID_FIELD"));
		}
		if (itemName.isEmpty()) {
			verErrors.add(resPageMsg.getString("the") + " " + resPageMsg.getString("item_name_column") + " "
					+ resPageMsg.getString("was_blank_at_row") + " " + k + ", "
					+ resPageMsg.getString("items_worksheet_with_dot"));
			htmlErrors.put(sheetNumber + "," + k + "," + CrfTemplateColumnNameEnum.ITEM_NAME.getCellNumber(),
					resPageMsg.getString("required_field"));
		}
		if (itemName.length() > MAX_ITEM_NAME_LENGTH) {
			verErrors.add(resPageMsg.getString("item_name_length_error"));
		}
		if (SpreadSheetItemUtil.isItemWithSameParameterExists(itemName, rowItems)) {
			verErrors.add(resPageMsg.getString("duplicate") + " " + resPageMsg.getString("item_name_column") + " "
					+ itemName + " " + resPageMsg.getString("was_detected_at_row") + " " + k + ", "
					+ resPageMsg.getString("items_worksheet_with_dot"));
			htmlErrors.put(sheetNumber + "," + k + "," + CrfTemplateColumnNameEnum.ITEM_NAME.getCellNumber(),
					resPageMsg.getString("INVALID_FIELD"));
		}
	}

	public void verifyDefaultValue(ArrayList<SpreadSheetItemUtil> rowItems, ArrayList<String> verErrors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg) {

		int rowNumber = rowItems.size();

		if ("date".equalsIgnoreCase(this.getDataType()) && !"".equals(this.getDefaultValue())) {
			try {
				this.setDefaultValue(new SimpleDateFormat(ApplicationConstants.getDateFormatInItemData())
						.format(this.getDefaultValue()));
			} catch (Exception e) {
				this.setDefaultValue("");
			}
		}
		if (this.getDefaultValue().length() > 0) {
			if (this.getResponseTypeId() == ResponseType.CALCULATION.getId()
					|| this.getResponseTypeId() == ResponseType.GROUP_CALCULATION.getId()
					|| this.getResponseTypeId() == ResponseType.FILE.getId()
					|| this.getResponseTypeId() == ResponseType.INSTANT_CALCULATION.getId()) {
				verErrors.add(resPageMsg.getString("default_value_not_allowed") + this.getItemName() + " " + resPageMsg
						.getString("change_radio") + " " + resPageMsg.getString("items_worksheet_with_dot"));
				htmlErrors.put(
						sheetNumber + "," + rowNumber + "," + CrfTemplateColumnNameEnum.DEFAULT_VALUE.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
			}
			// do not allow more than one value as a default value, value should be from response types
			else if (this.getResponseTypeId() == ResponseType.SELECT.getId()) {
				if (this.getDefaultValue().indexOf(',') != -1) {
					verErrors.add(resPageMsg.getString("default_value_wrong_select") + rowNumber + ", "
							+ resPageMsg.getString("items_worksheet_with_dot"));
					htmlErrors.put(
							sheetNumber + "," + rowNumber + ","
									+ CrfTemplateColumnNameEnum.DEFAULT_VALUE.getCellNumber(),
							resPageMsg.getString("INVALID_FIELD"));
				}
			}
		}
	}

	/**
	 * @return the response_options
	 */
	public String[] getResponseOptions() {
		return responseOptions;
	}

	/**
	 * @param responseOptions
	 *            the response_options to set
	 */
	public void setResponseOptions(String[] responseOptions) {
		this.responseOptions = responseOptions;
	}

	public static void verifyUniqueItemPlacementInGroups(List<SpreadSheetItemUtil> rowItems, List<String> verErrors,
			Map<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg, String crfName,
			javax.sql.DataSource ds) {

		// get all items with group / version info from db
		ItemDAO idao = new ItemDAO(ds);
		int rowCount = 1;
		int checkGroupCount = 0;
		StringBuffer itemMessages = null;
		ArrayList<ItemGroupCrvVersionUtil> itemGroupCrfRecords = idao
				.findAllWithItemGroupCRFVersionMetadataByCRFId(crfName);
		for (SpreadSheetItemUtil rowItem : rowItems) {
			itemMessages = new StringBuffer();
			for (ItemGroupCrvVersionUtil checkGroup : itemGroupCrfRecords) {
				checkGroupCount++;
				// we expect no more than one hit
				if (checkGroup.getItemName().equals(rowItem.getItemName())
						&& !(rowItem.getGroupLabel().equals("") && checkGroup.getGroupName().equals("Ungrouped"))) {

					if (!rowItem.getGroupLabel().equals(checkGroup.getGroupName())
							&& checkGroup.getCrfVersionStatus() == 1) {
						itemMessages.append(resPageMsg.getString("verifyUniqueItemPlacementInGroups_4")
								+ checkGroup.getGroupName());
						itemMessages.append(resPageMsg.getString("verifyUniqueItemPlacementInGroups_5"));
						itemMessages.append(checkGroup.getCrfVersionName());
						if (checkGroupCount != itemGroupCrfRecords.size()) {
							itemMessages.append("', ");
						}
					}
				}
			}
			if (itemMessages.length() > 0) {
				htmlErrors.put(
						sheetNumber + "," + rowCount + "," + CrfTemplateColumnNameEnum.GROUP_LABEL.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
				verErrors.add(resPageMsg.getString("verifyUniqueItemPlacementInGroups_1") + rowItem.getItemName() + "' "
						+ resPageMsg.getString("at_row") + " '" + rowCount + resPageMsg
						.getString("verifyUniqueItemPlacementInGroups_2") + rowItem.getItemName() + resPageMsg
						.getString("verifyUniqueItemPlacementInGroups_3") + itemMessages.toString() + ").");
			}
			rowCount++;
		}
	}
}
