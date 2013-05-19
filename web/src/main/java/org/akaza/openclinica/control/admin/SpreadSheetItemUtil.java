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

import org.akaza.openclinica.bean.core.ApplicationConstants;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.core.util.CrfTemplateColumnNameEnum;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.submit.ItemDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpreadSheetItemUtil {

	private String itemName;// 1
	private String descriptionLabel;// 2
	private String leftItemText;// 3
	private String sectionLabel;// 6
	private String groupLabel;// 7
	private String parentItem;// 10
	private int responseTypeId;// 14
	private String[] responseOptions;
	private String defaultValue;// 19
	private String dataType;

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
	 * @return the item_name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param item_name
	 *            the item_name to set
	 */
	public void setItemName(String item_name) {
		item_name = cleanProperty(item_name);
		this.itemName = item_name;
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
	 * @param left_item_text
	 *            the left_item_text to set
	 */
	public void setLeftItemText(String left_item_text) {
		this.leftItemText = left_item_text;
	}

	/**
	 * @return the section_label
	 */
	public String getSectionLabel() {
		return sectionLabel;
	}

	/**
	 * @param section_label
	 *            the section_label to set
	 */
	public void setSectionLabel(String section_label) {
		this.sectionLabel = cleanProperty(section_label);

	}

	/**
	 * @return the group_label
	 */
	public String getGroupLabel() {
		return groupLabel;
	}

	/**
	 * @param group_label
	 *            the group_label to set
	 */
	public void setGroupLabel(String group_label) {
		this.groupLabel = cleanProperty(group_label);
	}

	/**
	 * @return the parent_item
	 */
	public String getParentItem() {
		return parentItem;
	}

	/**
	 * @param parent_item
	 *            the parent_item to set
	 */
	public void setParentItem(String parent_item) {
		this.parentItem = cleanProperty(parent_item);

	}

	/**
	 * @return the responSe_type
	 */
	public int getResponseTypeId() {
		return responseTypeId;
	}

	/**
	 * @param responSe_type
	 *            the responSe_type to set
	 */
	public void setResponseTypeId(int response_type_id) {
		this.responseTypeId = response_type_id;
	}

	/**
	 * @return the default_value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param default_value
	 *            the default_value to set
	 */
	public void setDefaultValue(String default_value) {
		this.defaultValue = cleanProperty(default_value);
	}

	/**
	 * @return the data_type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param data_type
	 *            the data_type to set
	 */
	public void setDataType(String data_type) {
		this.dataType = cleanProperty(data_type);
	}

	public static boolean isItemWithSameParameterExists(String column_value, List<SpreadSheetItemUtil> row_items) {
		if (row_items == null || row_items.size() == 1) {
			return false;
		}
		SpreadSheetItemUtil item = isItemWithSameParameterExists(CrfTemplateColumnNameEnum.ITEM_NAME, column_value,
				row_items, false);
		return (item != null);
	}

	public static boolean isItemWithSameParameterExistsIncludingMyself(String column_value,
			List<SpreadSheetItemUtil> row_items) {
		if (row_items == null || row_items.size() == 1) {
			return true;
		}
		SpreadSheetItemUtil item = isItemWithSameParameterExists(CrfTemplateColumnNameEnum.ITEM_NAME, column_value,
				row_items, true);
		return (item != null);
	}

	public static SpreadSheetItemUtil isItemWithSameParameterExists(CrfTemplateColumnNameEnum param_column_index,
			String column_value, List<SpreadSheetItemUtil> row_items) {
		if (row_items == null || row_items.size() == 1) {
			return null;
		}
		return isItemWithSameParameterExists(param_column_index, column_value, row_items, false);
	}

	public static SpreadSheetItemUtil isItemWithSameParameterExists(CrfTemplateColumnNameEnum param_column_index,
			String column_value, List<SpreadSheetItemUtil> row_items, boolean isIncludingMyself) {

		int last_item_to_check = 0;// current item should not be included in evaluation
		for (SpreadSheetItemUtil cur_item : row_items) {
			if (!isIncludingMyself) {
				if (last_item_to_check == row_items.size() - 1) {
					break;
				}
			}
			last_item_to_check++;
			switch (param_column_index) {
			case ITEM_NAME: {
				if (cur_item.getItemName().equals(column_value)) {
					return cur_item;
				}
				break;
			}
			default:
				break;

			}
		}
		return null;

	}

	// TODO if we ever go to normal OO parsing of spreadsheet this method should be moved to
	// SpredSheetGroupUtil
	// the problem here that Group now can be ungrouped group
	public static void verifySectionGroupPlacementForItems(ArrayList<SpreadSheetItemUtil> row_items,
			ArrayList<String> ver_errors, HashMap<String, String> htmlErrors, int sheetNumber,
			ResourceBundle resPageMsg, HashMap<String, ItemGroupBean> itemGroups) {
		HashMap<String, String> group_section_map = new HashMap<String, String>();
		String section_label;
		int row_number = 1;
		for (SpreadSheetItemUtil cur_item : row_items) {
			row_number++;
			if (cur_item.getGroupLabel().length() < 1) {
				continue;
			}
			// verify that this is repeating group
			ItemGroupBean item_group = itemGroups.get(cur_item.getGroupLabel());
			boolean isRepeatingGroup = false;
			if (item_group == null) {
				// case when item has a group not listed in 'Groups' spreadSheet, error was processed before
			} else {
				isRepeatingGroup = item_group.getMeta().isRepeatingGroup();
			}
			if (!isRepeatingGroup) {
				continue;
			}
			section_label = group_section_map.get(cur_item.getGroupLabel());
			if (section_label != null) {// not first item in group
				if (!section_label.equals(cur_item.getSectionLabel())) {// error: items of one group belong to more than
																		// one section
					ver_errors.add(resPageMsg.getString("group_in_several_sections") + cur_item.getGroupLabel() + "'.");
					htmlErrors.put(
							sheetNumber + "," + (row_number - 1) + ","
									+ CrfTemplateColumnNameEnum.GROUP_LABEL.getCellNumber(),
							resPageMsg.getString("INVALID_VALUE"));

				}
			} else {// first item in group
				group_section_map.put(cur_item.getGroupLabel(), cur_item.getSectionLabel());
			}

		}
	}

	public void verifyParentID(ArrayList<SpreadSheetItemUtil> row_items, ArrayList<String> ver_errors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg,
			HashMap<String, ItemGroupBean> itemGroups) {

		int row_number = row_items.size();
		// BWP>>Prevent parent names that equal the Item names
		if (this.getItemName().equalsIgnoreCase(this.getParentItem())) {
			this.setParentItem("");

		}

		if (!this.getParentItem().isEmpty()) {
			SpreadSheetItemUtil cur_item = SpreadSheetItemUtil.isItemWithSameParameterExists(
					CrfTemplateColumnNameEnum.ITEM_NAME, this.getParentItem(), row_items);
			// Checking for a valid parent item name
			if (cur_item == null) {
				ver_errors.add(resPageMsg.getString("parent_id") + row_number + resPageMsg.getString("parent_id_1"));
				htmlErrors.put(
						sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));

			}
			// prevent more than one level of hierarchy for parent names (new ver)
			if (cur_item != null && cur_item.getParentItem() != null && cur_item.getParentItem().length() > 0) {
				ver_errors.add(resPageMsg.getString("nested_parent_id") + row_items.size()
						+ resPageMsg.getString("nested_parent_id_1"));
				htmlErrors.put(
						sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
			}
			// prevent item in RGroup to have parent id (new ver)
			// verify that this is repeating group
			if (itemGroups != null && itemGroups.size() > 0) {
				ItemGroupBean item_group = itemGroups.get(this.getGroupLabel());
				if (item_group != null) {
					boolean isRepeatingGroup = item_group.getMeta().isRepeatingGroup();
					if (isRepeatingGroup) {
						if (this.getParentItem().length() > 0) {
							ver_errors.add(resPageMsg.getString("parentId_group") + row_items.size()
									+ resPageMsg.getString("nested_parent_id_1"));
							htmlErrors.put(
									sheetNumber + "," + row_number + ","
											+ CrfTemplateColumnNameEnum.PARENT_ITEM.getCellNumber(),
									resPageMsg.getString("INVALID_FIELD"));
						}
					}
				}
			}
		}

	}

	public void verifySectionLabel(ArrayList<SpreadSheetItemUtil> row_items, ArrayList<String> ver_errors,
			ArrayList<String> secNames, HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg) {
		int row_number = row_items.size();
		StringBuffer str = new StringBuffer();
		if (this.getSectionLabel().length() == 0) {
			str.append(resPageMsg.getString("the") + " ");
			str.append(resPageMsg.getString("SECTION_LABEL_column") + " ");
			str.append(resPageMsg.getString("not_valid_section_at_row") + " ");
			str.append(row_number + ", " + resPageMsg.getString("items_worksheet_with_dot"));
			str.append(" " + resPageMsg.getString("check_to_see_that_there_is_valid_LABEL"));
			ver_errors.add(str.toString());
			htmlErrors.put(
					sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
					resPageMsg.getString("NOT_A_VALID_LABEL"));
		}

		if (this.getSectionLabel().length() > 2000) {
			ver_errors.add(resPageMsg.getString("section_label_length_error"));
			htmlErrors.put(
					sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
					resPageMsg.getString("NOT_A_VALID_LABEL"));
		}

		if (!secNames.contains(this.getSectionLabel())) {
			if (str.length() == 0) {
				str.append(resPageMsg.getString("the") + " ");
				str.append(resPageMsg.getString("SECTION_LABEL_column") + " ");
				str.append(resPageMsg.getString("not_valid_section_at_row") + " ");
				str.append(row_number + ", " + resPageMsg.getString("items_worksheet_with_dot"));
				str.append(" " + resPageMsg.getString("check_to_see_that_there_is_valid_LABEL"));
			}
			ver_errors.add(str.toString());

			htmlErrors.put(
					sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.SECTION_LABEL.getCellNumber(),
					resPageMsg.getString("NOT_A_VALID_LABEL"));
		}

	}

	public void verifyItemName(ArrayList<SpreadSheetItemUtil> row_items, ArrayList<String> ver_errors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg) {

		int k = row_items.size();
		String itemName = this.getItemName();
		// regexp to make sure it is all word characters, '\w+' in regexp terms
		if (!Utils.isMatchingRegexp(itemName, "\\w+")) {
			// different item error to go here
			ver_errors.add(resPageMsg.getString("item_name_column") + " " + resPageMsg.getString("was_invalid_at_row")
					+ " " + k + ", " + resPageMsg.getString("items_worksheet_with_dot") + " "
					+ resPageMsg.getString("you_can_only_use_letters_or_numbers"));
			htmlErrors.put(sheetNumber + "," + k + ",0", resPageMsg.getString("INVALID_FIELD"));
		}
		if (itemName.isEmpty()) {
			ver_errors.add(resPageMsg.getString("the") + " " + resPageMsg.getString("item_name_column") + " "
					+ resPageMsg.getString("was_blank_at_row") + " " + k + ", "
					+ resPageMsg.getString("items_worksheet_with_dot"));
			htmlErrors.put(sheetNumber + "," + k + "," + CrfTemplateColumnNameEnum.ITEM_NAME.getCellNumber(),
					resPageMsg.getString("required_field"));
		}
		if (itemName.length() > 255) {
			ver_errors.add(resPageMsg.getString("item_name_length_error"));
		}

		if (SpreadSheetItemUtil.isItemWithSameParameterExists(itemName, row_items)) {
			ver_errors.add(resPageMsg.getString("duplicate") + " " + resPageMsg.getString("item_name_column") + " "
					+ itemName + " " + resPageMsg.getString("was_detected_at_row") + " " + k + ", "
					+ resPageMsg.getString("items_worksheet_with_dot"));
			htmlErrors.put(sheetNumber + "," + k + "," + CrfTemplateColumnNameEnum.ITEM_NAME.getCellNumber(),
					resPageMsg.getString("INVALID_FIELD"));
		}

	}

	public void verifyDefaultValue(ArrayList<SpreadSheetItemUtil> row_items, ArrayList<String> ver_errors,
			HashMap<String, String> htmlErrors, int sheetNumber, ResourceBundle resPageMsg) {

		int row_number = row_items.size();

		if ("date".equalsIgnoreCase(this.getDataType()) && !"".equals(this.getDefaultValue())) {
			try {
				this.setDefaultValue(new SimpleDateFormat(ApplicationConstants.getDateFormatInItemData()).format(this
						.getDefaultValue()));
			} catch (Exception e) {
				this.setDefaultValue("");
			}
		}
		if (this.getDefaultValue().length() > 0) {
			if (this.getResponseTypeId() == ResponseType.CALCULATION.getId()
					|| this.getResponseTypeId() == ResponseType.GROUP_CALCULATION.getId()
					|| this.getResponseTypeId() == ResponseType.FILE.getId()
					|| this.getResponseTypeId() == ResponseType.INSTANT_CALCULATION.getId()) {
				ver_errors
						.add(resPageMsg.getString("default_value_not_allowed") + this.getItemName() + " "
								+ resPageMsg.getString("change_radio") + " "
								+ resPageMsg.getString("items_worksheet_with_dot"));
				htmlErrors.put(
						sheetNumber + "," + row_number + "," + CrfTemplateColumnNameEnum.DEFAULT_VALUE.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
			}

			// do not allow more than one value as a default value, value should be from response types
			else if (this.getResponseTypeId() == ResponseType.SELECT.getId()) {
				if (this.getDefaultValue().indexOf(',') != -1) {
					ver_errors.add(resPageMsg.getString("default_value_wrong_select") + row_number + ", "
							+ resPageMsg.getString("items_worksheet_with_dot"));
					htmlErrors.put(
							sheetNumber + "," + row_number + ","
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
	 * @param response_options
	 *            the response_options to set
	 */
	public void setResponseOptions(String[] response_options) {
		this.responseOptions = response_options;
	}

	public static void verifyUniqueItemPlacementInGroups(ArrayList<SpreadSheetItemUtil> row_items,
			ArrayList<String> ver_errors, HashMap<String, String> htmlErrors, int sheetNumber,
			ResourceBundle resPageMsg, String crfName, javax.sql.DataSource ds) {

		// get all items with group / version info from db
		ItemDAO idao = new ItemDAO(ds);
		int row_count = 1;
		int check_group_count = 0;
		StringBuffer item_messages = null;
		ArrayList<ItemGroupCrvVersionUtil> item_group_crf_records = idao
				.findAllWithItemGroupCRFVersionMetadataByCRFId(crfName);
		for (SpreadSheetItemUtil row_item : row_items) {
			item_messages = new StringBuffer();
			for (ItemGroupCrvVersionUtil check_group : item_group_crf_records) {
				check_group_count++;
				// we expect no more than one hit
				if (check_group.getItemName().equals(row_item.getItemName())
						&& !(row_item.getGroupLabel().equals("") && check_group.getGroupName().equals("Ungrouped"))) {

					if (!row_item.getGroupLabel().equals(check_group.getGroupName())
							&& check_group.getCrfVersionStatus() == 1) {
						item_messages.append(resPageMsg.getString("verifyUniqueItemPlacementInGroups_4")
								+ check_group.getGroupName());
						item_messages.append(resPageMsg.getString("verifyUniqueItemPlacementInGroups_5"));
						item_messages.append(check_group.getCrfVersionName());
						if (check_group_count != item_group_crf_records.size()) {
							item_messages.append("', ");
						}
					}
				}
			}

			if (item_messages.length() > 0) {
				htmlErrors.put(
						sheetNumber + "," + row_count + "," + CrfTemplateColumnNameEnum.GROUP_LABEL.getCellNumber(),
						resPageMsg.getString("INVALID_FIELD"));
				ver_errors
						.add(resPageMsg.getString("verifyUniqueItemPlacementInGroups_1") + row_item.getItemName()
								+ "' " + resPageMsg.getString("at_row") + " '" + row_count
								+ resPageMsg.getString("verifyUniqueItemPlacementInGroups_2") + row_item.getItemName()
								+ resPageMsg.getString("verifyUniqueItemPlacementInGroups_3")
								+ item_messages.toString() + ").");
			}
			row_count++;
		}
	}
}
