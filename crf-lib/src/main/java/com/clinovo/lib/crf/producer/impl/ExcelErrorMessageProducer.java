/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.producer.impl;

import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.exception.CRFReadingException;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.RealValueKey;
import com.clinovo.lib.crf.producer.ErrorMessageProducer;

/**
 * ExcelErrorMessageProducer.
 */
public class ExcelErrorMessageProducer implements ErrorMessageProducer {

	private ExcelCrfBuilder crfBuilder;

	/**
	 * Constructor.
	 *
	 * @param crfBuilder
	 *            ExcelCrfBuilder
	 */
	public ExcelErrorMessageProducer(ExcelCrfBuilder crfBuilder) {
		this.crfBuilder = crfBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionIsBlank() throws CRFReadingException {
		throw new CRFReadingException(crfBuilder.getMessage("version_is_blank"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("version_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionDescriptionLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("version_description_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfRevisionNotesIsBlank() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("REVISION_NOTES_column") + " "
						+ crfBuilder.getMessage("was_blank_in_the_CRF_worksheet"));
		crfBuilder.getErrorsMap().put(0 + ",1,3", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfRevisionNotesLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("revision_notes_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameIsBlank() throws CRFReadingException {
		throw new CRFReadingException(crfBuilder.getMessage("crf_name_is_blank"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("crf_name_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameHasAlreadyBeenUsed() throws CRFReadingException {
		throw new CRFReadingException(crfBuilder.getMessage("crf_name_already_used"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionHasAlreadyBeenUsed() throws CRFReadingException {
		throw new CRFReadingException(crfBuilder.getMessage("crf_version_already_used"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void didNotMatchCrfName(String crfName) throws CRFReadingException {
		throw new CRFReadingException(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("CRF_NAME_column")
				+ " '" + crfBuilder.getCrfBean().getName() + "' " + crfBuilder.getMessage("did_not_match_crf_name")
				+ " '" + crfName + "'.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsBlank() {
		int row = crfBuilder.getCurrentSection().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentSection().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SECTION_LABEL_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + " " + ", "
						+ crfBuilder.getMessage("sections_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",0", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_label_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsDuplicated() {
		int row = crfBuilder.getCurrentSection().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentSection().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SECTION_LABEL_column") + " "
						+ crfBuilder.getMessage("was_a_duplicate_of") + " " + crfBuilder.getCurrentSection().getLabel()
						+ " " + crfBuilder.getMessage("at_row") + " " + row + ", "
						+ crfBuilder.getMessage("sections_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",0", crfBuilder.getMessage("DUPLICATE_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleIsBlank() {
		int row = crfBuilder.getCurrentSection().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentSection().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SECTION_TITLE_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("sections_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",1", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_title_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionInstructionsLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_instruction_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionPageNumberLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_page_number_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsBlank() {
		int row = crfBuilder.getCurrentItemGroup().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItemGroup().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("GROUP_LABEL_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("Groups_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",0", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_label_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsDuplicated() {
		int row = crfBuilder.getCurrentItemGroup().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItemGroup().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("GROUP_LABEL_column")
				+ " " + crfBuilder.getMessage("was_a_duplicate_of") + " " + crfBuilder.getCurrentItemGroup().getName()
				+ " " + crfBuilder.getMessage("at_row") + row + ", " + crfBuilder.getMessage("Groups_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",0", crfBuilder.getMessage("DUPLICATE_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupHeaderLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_header_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatNumIsWrong() {
		int row = crfBuilder.getCurrentItemGroup().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItemGroup().getSheetNumber();
		ItemGroupMetadataBean itemGroupMetadataBean = crfBuilder.getItemGroupLabelToMetaMap()
				.get(crfBuilder.getCurrentItemGroup().getName());
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("GROUP_REPEAT_NUM_column") + " "
						+ crfBuilder.getMessage("must_be_a_positive_integer_or_blank") + ". "
						+ itemGroupMetadataBean.getRepeatNum() + " " + crfBuilder.getMessage("at_row") + " " + row
						+ ", " + crfBuilder.getMessage("Groups_worksheet") + ". ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",2", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatMaxIsWrong() {
		int row = crfBuilder.getCurrentItemGroup().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItemGroup().getSheetNumber();
		ItemGroupMetadataBean itemGroupMetadataBean = crfBuilder.getItemGroupLabelToMetaMap()
				.get(crfBuilder.getCurrentItemGroup().getName());
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("GROUP_REPEAT_MAX_column") + " "
						+ crfBuilder.getMessage("must_be_a_positive_integer") + ". "
						+ itemGroupMetadataBean.getRepeatMax() + " " + crfBuilder.getMessage("at_row") + " " + row
						+ ", " + crfBuilder.getMessage("Groups_worksheet") + ". ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",3", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsBlank() {
		int row = crfBuilder.getCurrentItemGroup().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItemGroup().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("item_name_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_NAME),
				crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_name_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsDuplicated() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("duplicate") + " " + crfBuilder.getMessage("item_name_column") + " "
						+ crfBuilder.getCurrentItem().getName() + " " + crfBuilder.getMessage("was_detected_at_row")
						+ " " + row + ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_NAME),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsNotMatchingRegexp() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("item_name_column") + " " + crfBuilder.getMessage("was_invalid_at_row") + " "
						+ row + ", " + crfBuilder.getMessage("items_worksheet_with_dot") + " "
						+ crfBuilder.getMessage("you_can_only_use_letters_or_numbers"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",0", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("DESCRIPTION_LABEL_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",1", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_desc_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemLeftTextLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("left_item_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemRightTextLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("right_item_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHeaderLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_header_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSubHeaderLengthIsExceeded() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_subheader_length_error"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_SUBHEADER),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		StringBuffer str = new StringBuffer();
		str.append(crfBuilder.getMessage("the")).append(" ");
		str.append(crfBuilder.getMessage("SECTION_LABEL_column")).append(" ");
		str.append(crfBuilder.getMessage("not_valid_section_at_row")).append(" ");
		str.append(row).append(", ").append(crfBuilder.getMessage("items_worksheet_with_dot"));
		str.append(" ").append(crfBuilder.getMessage("check_to_see_that_there_is_valid_LABEL"));
		crfBuilder.getErrorsList().add(str.toString());
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_SECTION_LABEL),
				crfBuilder.getMessage("NOT_A_VALID_LABEL"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelLengthIsExceeded() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_label_length_error"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_SECTION_LABEL),
				crfBuilder.getMessage("NOT_A_VALID_LABEL"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("Error_found_at_row") + " \"" + row + "\""
						+ crfBuilder.getMessage("items_worksheet_with_dot") + crfBuilder.getMessage("GROUP_LABEL")
						+ "\"" + crfBuilder.getCurrentItem().getItemMeta().getGroupLabel() + "\" "
						+ crfBuilder.getMessage("does_not_exist_in_group_spreadsheet"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",6", crfBuilder.getMessage("GROUP_DOES_NOT_EXIST"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelLengthIsExceeded() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_label_length_error"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_GROUP_LABEL),
				crfBuilder.getMessage("NOT_A_VALID_LABEL"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemUnitsLengthIsExceeded() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("units_length_error"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_UNITS),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemParentItemIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("parent_id") + row + crfBuilder.getMessage("parent_id_1"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_PARENT_ITEM),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasNestedParentItem() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("nested_parent_id") + row + crfBuilder.getMessage("nested_parent_id_1"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_PARENT_ITEM),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatingGroupHasParentItem() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("parentId_group") + row + crfBuilder.getMessage("nested_parent_id_1"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_PARENT_ITEM),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_TYPE_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",13", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_TYPE_column") + " "
						+ crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",13", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasRadioWithDefault() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("radio_with_default") + row + crfBuilder.getMessage("change_radio"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_DEFAULT_VALUE),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_LABEL_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",14", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsTextIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_OPTIONS_TEXT_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",15", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsText() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage(("resp_label_with_different_resp_options")) + " " + row
				+ ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",15",
				crfBuilder.getMessage("resp_label_with_different_resp_options_html_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsText() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("resp_label_with_different_resp_options") + " " + row
				+ ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",15",
				crfBuilder.getMessage("resp_label_with_different_resp_options_html_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_VALUES_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsValues() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("resp_label_with_different_resp_values") + " " + row + ", "
				+ crfBuilder.getMessage("items_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16",
				crfBuilder.getMessage("resp_label_with_different_resp_values_html_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsValues() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("resp_label_with_different_resp_values") + " " + row + ", "
				+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16",
				crfBuilder.getMessage("resp_label_with_different_resp_values_html_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionDoesNotStartWithFunc() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("expression_not_start_with_func_at") + " " + row + ", "
				+ crfBuilder.getMessage("items_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("expression_invalid_at") + " " + row + ", "
				+ crfBuilder.getMessage("items_worksheet") + ": " + crfBuilder.getCurrentScoreValidatorErrorsBuffer());
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasIncompleteOptionValuePair() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("incomplete_option_value_pair") + " "
				+ crfBuilder.getMessage("RESPONSE_OPTIONS_column") + " " + crfBuilder.getMessage("and") + " "
				+ crfBuilder.getMessage("RESPONSE_VALUES_column") + " " + crfBuilder.getMessage("at_row") + row + " "
				+ crfBuilder.getMessage("items_worksheet") + "; " + crfBuilder.getMessage("perhaps_missing_comma"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",15",
				crfBuilder.getMessage("number_option_not_match"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("number_value_not_match"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemMustBeListedBeforeAnotherItem() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add("Item '" + crfBuilder.getCurrentMessage().toString() + "' must be listed before the item '"
						+ crfBuilder.getCurrentItem().getName() + "' at row " + row + ", items worksheet. ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", "INVALID FIELD");
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsMustHaveTheSameGroup() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add("Item '" + crfBuilder.getCurrentMessage().toString() + "' and item '"
						+ crfBuilder.getCurrentItem().getName() + "' must have a same GROUP_LABEL at row " + row
						+ ", items worksheet. ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", "INVALID FIELD");
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsShouldNotHaveTheSameGroup() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add("Item '" + crfBuilder.getCurrentMessage().toString() + "' and item '"
						+ crfBuilder.getCurrentItem().getName() + "' should not have a same GROUP_LABEL at row " + row
						+ ", items worksheet. ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", "INVALID FIELD");
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("DATA_TYPE_column") + " "
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",19", crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("DATA_TYPE_column") + " "
						+ crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",19", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeShouldBeFile() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("DATA_TYPE_column") + " "
						+ crfBuilder.getMessage("should_be_file") + crfBuilder.getMessage("at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",19", crfBuilder.getMessage("should_be_file"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeInteger() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_VALUES_column") + " "
						+ crfBuilder.getMessage("should_be_integer") + " " + crfBuilder.getMessage("at_row") + " " + row
						+ ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("should_be_integer"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeReal() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("RESPONSE_VALUES_column") + " "
						+ crfBuilder.getMessage("should_be_real") + " " + crfBuilder.getMessage("at_row") + " " + row
						+ ", " + crfBuilder.getMessage("items_worksheet") + ".");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",16", crfBuilder.getMessage("should_be_real"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void doesNotMatchDataTypeOfItemWithSameResponseLabel() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("DATA_TYPE_column") + " "
						+ crfBuilder.getMessage("does_not_match_the_item_data_type_with_the_same_response_label") + " "
						+ row + ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",19", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalIsNotAvailable() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("error_message_for_width_decimal_at") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet") + ":" + " "
						+ crfBuilder.getMessage("width_decimal_unavailable_for_single_multi_checkbox_radio"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",20", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalHasErrors() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("error_message_for_width_decimal_at") + " " + row + ", "
				+ crfBuilder.getMessage("items_worksheet") + ":" + " " + crfBuilder.getCurrentMessage());
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",20", crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getCurrentMessage() + ", " + crfBuilder.getMessage("at_row") + " "
				+ row + ", " + crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_VALIDATION),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("VALIDATION_column") + " "
						+ crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_VALIDATION),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgIsBlank() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("VALIDATION_ERROR_MESSAGE_column")
						+ crfBuilder.getMessage("was_blank_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot") + " "
						+ crfBuilder.getMessage("cannot_be_blank_if_VALIDATION_not_blank"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_VALIDATION_ERROR_MESSAGE),
				crfBuilder.getMessage("required_field"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("regexp_errror_length_error"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsInvalidRegularExpression() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("VALIDATION_column") + " "
						+ crfBuilder.getMessage("has_an_invalid_regular_expression_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot") + crfBuilder.getMessage("Example")
						+ " regexp: /[0-9]*/ ");
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_VALIDATION),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnHasInvalidRegularExpression() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("VALIDATION_column") + " "
						+ crfBuilder.getMessage("has_an_invalid_regular_expression_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot")
						+ crfBuilder.getMessage("regular_expression_contained") + " '\\\\', "
						+ crfBuilder.getMessage("it_should_only_contain_one") + "'\\'. ");
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_VALIDATION),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void phiIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("PHI_column")
						+ crfBuilder.getMessage("was_invalid_at_row") + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot") + crfBuilder.getMessage("PHI_column") + " "
						+ crfBuilder.getMessage("can_only_be_either_0_or_1"));
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_PHI),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void requiredIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("REQUIRED_column") + " "
						+ crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot") + crfBuilder.getMessage("REQUIRED_column")
						+ crfBuilder.getMessage("can_only_be_either_0_or_1"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_REQUIRED),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDisplayStatusIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("ITEM_DISPLAY_STATUS_column") + " "
						+ crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot")
						+ crfBuilder.getMessage("should_be_hide_for_scd"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_DISPLAY_STATUS),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlResponseValueIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SIMPLE_CONDITIONAL_DISPLAY_column")
						+ " " + crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot")
						+ crfBuilder.getMessage("control_response_value_invalid") + " "
						+ crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getOptionValue());
		crfBuilder.getErrorsMap()
				.put(sheetNumber + "," + row + ","
						+ crfBuilder.getColumnNumber(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlItemNameIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SIMPLE_CONDITIONAL_DISPLAY_column")
						+ " " + crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot")
						+ crfBuilder.getMessage("control_item_name_invalid") + " "
						+ crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getControlItemName());
		crfBuilder.getErrorsMap()
				.put(sheetNumber + "," + row + ","
						+ crfBuilder.getColumnNumber(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void simpleConditionalDisplayIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("SIMPLE_CONDITIONAL_DISPLAY_column")
						+ " " + crfBuilder.getMessage("was_invalid_at_row") + " " + row + ", "
						+ crfBuilder.getMessage("items_worksheet_with_dot") + crfBuilder.getMessage("correct_pattern"));
		crfBuilder.getErrorsMap()
				.put(sheetNumber + "," + row + ","
						+ crfBuilder.getColumnNumber(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void ontologyNameIsNotValid() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("please_specify_correct_ontology_name"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_CODE_REF),
				crfBuilder.getMessage("please_specify_correct_ontology_name"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateCodingItemTypeToCode() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("please_update_coding_item_type_to_code"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_CODE_REF),
				crfBuilder.getMessage("please_update_coding_item_type_to_code"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateMedicalCodingReferenceItemType() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("please_update_medical_coding_reference_item_type"));
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_DATA_TYPE),
				crfBuilder.getMessage("please_update_medical_coding_reference_item_type"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelHasBeenUsedForAnotherResponseType() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList()
				.add("Error found at row \"" + row + "\" in items worksheet. ResponseLabel \""
						+ crfBuilder.getCurrentItem().getResponseSet().getLabel() + "\" for ResponseType \""
						+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE)
						+ "\" has been used for another ResponseType.  ");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + ",14", "INVALID FIELD");
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemOfOneGroupBelongsToMoreThanOneSection() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_in_several_sections")
				+ crfBuilder.getCurrentItem().getItemMeta().getGroupLabel() + "'.");
		crfBuilder.getErrorsMap().put(
				sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.ITEM_GROUP_LABEL),
				crfBuilder.getMessage("INVALID_VALUE"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void notUniqueItemPlacementInGroups() {
		int row = crfBuilder.getCurrentItem().getRowNumber();
		int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("verifyUniqueItemPlacementInGroups_1")
				+ crfBuilder.getCurrentItem().getName() + "' " + crfBuilder.getMessage("at_row") + " '" + row
				+ crfBuilder.getMessage("verifyUniqueItemPlacementInGroups_2") + crfBuilder.getCurrentItem().getName()
				+ crfBuilder.getMessage("verifyUniqueItemPlacementInGroups_3")
				+ crfBuilder.getCurrentMessage().toString() + ").");
		crfBuilder.getErrorsMap().put(sheetNumber + "," + row + "," + crfBuilder.getColumnNumber(CellName.GROUP_LABEL),
				crfBuilder.getMessage("INVALID_FIELD"));
	}

}
