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

package com.clinovo.lib.crf.producer.impl;

import org.akaza.openclinica.exception.CRFReadingException;

import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.producer.ErrorMessageProducer;

/**
 * JsonErrorMessageProducer.
 */
public class JsonErrorMessageProducer implements ErrorMessageProducer {

	private JsonCrfBuilder crfBuilder;

	/**
	 * Constructor.
	 *
	 * @param crfBuilder
	 *            JsonCrfBuilder
	 */
	public JsonErrorMessageProducer(JsonCrfBuilder crfBuilder) {
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
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.revisionNotesIsBlank"));
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
		throw new CRFReadingException(crfBuilder.getMessage("the") + " " + crfBuilder.getMessage("crfName") + " '"
				+ crfBuilder.getCrfBean().getName() + "' " + crfBuilder.getMessage("did_not_match_crf_name") + " '"
				+ crfName + "'.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.sectionLabelIsBlank") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_label_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsDuplicated() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.sectionLabelIsDuplicated",
				new Object[]{crfBuilder.getCurrentItem().getItemMeta().getSectionName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.sectionTitleIsBlank") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_title_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionInstructionsLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_instruction_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionPageNumberLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_page_number_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.groupLabelIsBlank") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_label_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsDuplicated() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.groupLabelIsDuplicated")
						+ crfBuilder.getMessage("rest.crfservice.checkItem",
								new Object[]{crfBuilder.getCurrentItem().getItemMeta().getGroupLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupHeaderLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_header_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatNumIsWrong() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.groupRepeatNumber") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatMaxIsWrong() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.groupRepeatMax") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemNameIsBlank") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_name_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsDuplicated() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemNameIsDuplicated",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsNotMatchingRegexp() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemNameFormat") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemDescriptionIsBlank") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_desc_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemLeftTextLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("left_item_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemRightTextLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("right_item_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHeaderLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_header_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSubHeaderLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("item_subheader_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemSectionLabelIsNotValid", new Object[]{
				crfBuilder.getCurrentItem().getItemMeta().getSectionName(), crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("section_label_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemGroupLabelIsNotValid", new Object[]{
				crfBuilder.getCurrentItem().getItemMeta().getGroupLabel(), crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("group_label_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemUnitsLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("units_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemParentItemIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemParentItemIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasNestedParentItem() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.hasNestedParentItem",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatingGroupHasParentItem() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.repeatingGroupHasParentItem",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.resonseTypeIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.resonseTypeIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasRadioWithDefault() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.hasRadioWithDefault" + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()})));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.resonseLabelIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsTextIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.resonseOptionsTextIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsText() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemHasDifferentNumberOfOptionsText",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsText() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemHasDifferentValuesForOptionsText",
				new Object[]{crfBuilder.getCurrentItem().getResponseSet().getLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.resonseOptionsValuesIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsValues() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemHasDifferentNumberOfOptionsValues",
				new Object[]{crfBuilder.getCurrentItem().getResponseSet().getLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsValues() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemHasDifferentValuesForOptionsValues",
				new Object[]{crfBuilder.getCurrentItem().getResponseSet().getLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionDoesNotStartWithFunc() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.expressionStartsWithFunc",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.expressionIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasIncompleteOptionValuePair() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemHasIncompleteOptionValuePair",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemMustBeListedBeforeAnotherItem() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemMustBeListedBeforeAnotherItem",
				new Object[]{crfBuilder.getCurrentMessage().toString(), crfBuilder.getCurrentItem().getName()}));
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsMustHaveTheSameGroup() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemsMustHaveTheSameGroup",
				new Object[]{crfBuilder.getCurrentMessage().toString(), crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsShouldNotHaveTheSameGroup() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemsShouldNotHaveTheSameGroup",
				new Object[]{crfBuilder.getCurrentMessage().toString(), crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemDataTypeIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemDataTypeIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeShouldBeFile() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemDataTypeShouldBeFile",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeInteger() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.responseOptionsValuesShouldBeInteger",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeReal() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.responseOptionsValuesShouldBeReal",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void doesNotMatchDataTypeOfItemWithSameResponseLabel() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.doesNotMatchDataTypeOfItemWithSameResponseLabel",
						new Object[]{crfBuilder.getCurrentItem().getName(),
								crfBuilder.getCurrentItem().getResponseSet().getLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalIsNotAvailable() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.widthDecimalIsNotAvailableFor",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalHasErrors() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.widthDecimalHasErrors",
				new Object[]{crfBuilder.getCurrentItem().getName(), crfBuilder.getCurrentMessage().toString()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.regexpIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.validationColumnIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgIsBlank() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.regexpErrorMsgIsBlank",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgLengthIsExceeded() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("regexp_errror_length_error") + crfBuilder
				.getMessage("rest.crfservice.checkItem", new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsInvalidRegularExpression() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.regexpIsInvalidRegularExpression",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnHasInvalidRegularExpression() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.validationColumnHasInvalidRegularExpression",
						new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void phiIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.phiIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void requiredIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.requiredIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDisplayStatusIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.itemDisplayStatusIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlResponseValueIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.controlResponseValueIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlItemNameIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.controlItemNameIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void simpleConditionalDisplayIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.simpleConditionalDisplayIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void ontologyNameIsNotValid() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.ontologyNameIsNotValid",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateCodingItemTypeToCode() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.needToUpdateCodingItemTypeToCode",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateMedicalCodingReferenceItemType() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.needToUpdateMedicalCodingReferenceItemType",
						new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelHasBeenUsedForAnotherResponseType() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.responseLabelHasBeenUsedForAnotherResponseType",
						new Object[]{crfBuilder.getCurrentItem().getName()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemOfOneGroupBelongsToMoreThanOneSection() {
		crfBuilder.getErrorsList()
				.add(crfBuilder.getMessage("rest.crfservice.itemOfOneGroupBelongsToMoreThanOneSection",
						new Object[]{crfBuilder.getCurrentItem().getItemMeta().getGroupLabel()}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void notUniqueItemPlacementInGroups() {
		crfBuilder.getErrorsList().add(crfBuilder.getMessage("rest.crfservice.notUniqueItemPlacementInGroups",
				new Object[]{crfBuilder.getCurrentItem().getName()}));
	}
}
