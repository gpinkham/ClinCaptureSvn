/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.producer;

import org.akaza.openclinica.exception.CRFReadingException;

/**
 * ErrorMessageProducer.
 */
public interface ErrorMessageProducer {

	/**
	 * Generate error message if crf version is blank.
	 *
	 * @throws CRFReadingException
	 *             the CRFReadingException
	 */
	void crfVersionIsBlank() throws CRFReadingException;

	/**
	 * Generate error message if crf version length is exceeded.
	 */
	void crfVersionLengthIsExceeded();

	/**
	 * Generate error message if crf version description length is exceeded.
	 */
	void crfVersionDescriptionLengthIsExceeded();

	/**
	 * Generate error message if crf revision notes is blank.
	 */
	void crfRevisionNotesIsBlank();

	/**
	 * Generate error message if crf revision notes length is exceeded.
	 */
	void crfRevisionNotesLengthIsExceeded();

	/**
	 * Generate error message if crf name is blank.
	 * 
	 * @throws CRFReadingException
	 *             the CRFReadingException
	 */
	void crfNameIsBlank() throws CRFReadingException;

	/**
	 * Generate error message if crf name length is exceeded.
	 */
	void crfNameLengthIsExceeded();

	/**
	 * Generate error message if did not match crf name.
	 * 
	 * @param crfName
	 *            String
	 * @throws CRFReadingException
	 *             the CRFReadingException
	 */
	void didNotMatchCrfName(String crfName) throws CRFReadingException;

	/**
	 * Generate error message if crf version has already been used.
	 *
	 * @throws CRFReadingException
	 *             the CRFReadingException
	 */
	void crfVersionHasAlreadyBeenUsed() throws CRFReadingException;

	/**
	 * Generate error message if section label is blank.
	 */
	void sectionLabelIsBlank();

	/**
	 * Generate error message if section label length is exceeded.
	 */
	void sectionLabelLengthIsExceeded();

	/**
	 * Generate error message if section label is duplicated.
	 */
	void sectionLabelIsDuplicated();

	/**
	 * Generate error message if section title is blank.
	 */
	void sectionTitleIsBlank();

	/**
	 * Generate error message if section title length is exceeded.
	 */
	void sectionTitleLengthIsExceeded();

	/**
	 * Generate error message if section instructions length is exceeded.
	 */
	void sectionInstructionsLengthIsExceeded();

	/**
	 * Generate error message if section page number length is exceeded.
	 */
	void sectionPageNumberLengthIsExceeded();

	/**
	 * Generate error message if group label is blank.
	 */
	void groupLabelIsBlank();

	/**
	 * Generate error message if group label length is exceeded.
	 */
	void groupLabelLengthIsExceeded();

	/**
	 * Generate error message if group label is duplicated.
	 */
	void groupLabelIsDuplicated();

	/**
	 * Generate error message if group header length is exceeded.
	 */
	void groupHeaderLengthIsExceeded();

	/**
	 * Generate error message if repeat num is wrong.
	 */
	void repeatNumIsWrong();

	/**
	 * Generate error message if repeat max is wrong.
	 */
	void repeatMaxIsWrong();

	/**
	 * Generate error message if item name is blank.
	 */
	void itemNameIsBlank();

	/**
	 * Generate error message if item name length is exceeded.
	 */
	void itemNameLengthIsExceeded();

	/**
	 * Generate error message if item name is duplicated.
	 */
	void itemNameIsDuplicated();

	/**
	 * Generate error message if item name is not matching regexp.
	 */
	void itemNameIsNotMatchingRegexp();

	/**
	 * Generate error message if item description is blank.
	 */
	void itemDescriptionIsBlank();

	/**
	 * Generate error message if item description length is exceeded.
	 */
	void itemDescriptionLengthIsExceeded();

	/**
	 * Generate error message if left item text length is exceeded.
	 */
	void itemLeftTextLengthIsExceeded();

	/**
	 * Generate error message if right item text length is exceeded.
	 */
	void itemRightTextLengthIsExceeded();

	/**
	 * Generate error message if right item header length is exceeded.
	 */
	void itemHeaderLengthIsExceeded();

	/**
	 * Generate error message if right item sub header length is exceeded.
	 */
	void itemSubHeaderLengthIsExceeded();

	/**
	 * Generate error message if item section label is not valid.
	 */
	void itemSectionLabelIsNotValid();

	/**
	 * Generate error message if right item section label length is exceeded.
	 */
	void itemSectionLabelLengthIsExceeded();

	/**
	 * Generate error message if item group label is not valid.
	 */
	void itemGroupLabelIsNotValid();

	/**
	 * Generate error message if right item group label length is exceeded.
	 */
	void itemGroupLabelLengthIsExceeded();

	/**
	 * Generate error message if right item units length is exceeded.
	 */
	void itemUnitsLengthIsExceeded();

	/**
	 * Generate error message if parent item is not valid.
	 */
	void itemParentItemIsNotValid();

	/**
	 * Generate error message if has nested parent items.
	 */
	void hasNestedParentItem();

	/**
	 * Generate error message if repeating group has parent items.
	 */
	void repeatingGroupHasParentItem();

	/**
	 * Generate error message if response type is blank.
	 */
	void responseTypeIsBlank();

	/**
	 * Generate error message if response type is not valid.
	 */
	void responseTypeIsNotValid();

	/**
	 * Generate error message if has radio with default value.
	 */
	void hasRadioWithDefault();

	/**
	 * Generate error message if response label is blank.
	 */
	void responseLabelIsBlank();

	/**
	 * Generate error message if response options text is blank.
	 */
	void responseOptionsTextIsBlank();

	/**
	 * Generate error message if has different number of options text for the same response option label.
	 */
	void itemHasDifferentNumberOfOptionsText();

	/**
	 * Generate error message if has different values for options text for the same response option label.
	 */
	void itemHasDifferentValuesForOptionsText();

	/**
	 * Generate error message if response options values is blank.
	 */
	void responseOptionsValuesIsBlank();

	/**
	 * Generate error message if has different number of options values for the same response option label.
	 */
	void itemHasDifferentNumberOfOptionsValues();

	/**
	 * Generate error message if has different values for options values for the same response option label.
	 */
	void itemHasDifferentValuesForOptionsValues();

	/**
	 * Generate error message if expression does not start with func.
	 */
	void expressionDoesNotStartWithFunc();

	/**
	 * Generate error message if expression is not valid.
	 */
	void expressionIsNotValid();

	/**
	 * Generate error message if has incomplete option value pair.
	 */
	void itemHasIncompleteOptionValuePair();

	/**
	 * Generate error message if item must be listed before another item.
	 */
	void itemMustBeListedBeforeAnotherItem();

	/**
	 * Generate error message if items must have the same group.
	 */
	void itemsMustHaveTheSameGroup();

	/**
	 * Generate error message if items should not have the same group.
	 */
	void itemsShouldNotHaveTheSameGroup();

	/**
	 * Generate error message if item data type is blank.
	 */
	void itemDataTypeIsBlank();

	/**
	 * Generate error message if item data type is not valid.
	 */
	void itemDataTypeIsNotValid();

	/**
	 * Generate error message if item data type should be file.
	 */
	void itemDataTypeShouldBeFile();

	/**
	 * Generate error message if response options values should be integer.
	 */
	void responseOptionsValuesShouldBeInteger();

	/**
	 * Generate error message if response options values should be real.
	 */
	void responseOptionsValuesShouldBeReal();

	/**
	 * Generate error message if does not match the data type of the item with the same response label.
	 */
	void doesNotMatchDataTypeOfItemWithSameResponseLabel();

	/**
	 * Generate error message if width decimal is not available.
	 */
	void widthDecimalIsNotAvailable();

	/**
	 * Generate error message if width decimal has errors.
	 */
	void widthDecimalHasErrors();

	/**
	 * Generate error message if regexp is not valid.
	 */
	void regexpIsNotValid();

	/**
	 * Generate error message if validation column is not valid.
	 */
	void validationColumnIsNotValid();

	/**
	 * Generate error message if regexp error msg is blank.
	 */
	void regexpErrorMsgIsBlank();

	/**
	 * Generate error message if regexp error msg length is exceeded.
	 */
	void regexpErrorMsgLengthIsExceeded();

	/**
	 * Generate error message if regexp is invalid regular expression.
	 */
	void regexpIsInvalidRegularExpression();

	/**
	 * Generate error message if if validation column has invalid regular expression.
	 */
	void validationColumnHasInvalidRegularExpression();

	/**
	 * Generate error message if phi is not valid.
	 */
	void phiIsNotValid();

	/**
	 * Generate error message if required is not valid.
	 */
	void requiredIsNotValid();

	/**
	 * Generate error message if required is not valid.
	 */
	void itemDisplayStatusIsNotValid();

	/**
	 * Generate error message if control response value is not valid.
	 */
	void controlResponseValueIsNotValid();

	/**
	 * Generate error message if control item name is not valid.
	 */
	void controlItemNameIsNotValid();

	/**
	 * Generate error message if simple conditional display is not valid.
	 */
	void simpleConditionalDisplayIsNotValid();

	/**
	 * Generate error message if ontology name is not valid.
	 */
	void ontologyNameIsNotValid();

	/**
	 * Generate error message if need to update coding item type to code.
	 */
	void needToUpdateCodingItemTypeToCode();

	/**
	 * Generate error message if need to update medical coding reference item type.
	 */
	void needToUpdateMedicalCodingReferenceItemType();

	/**
	 * Generate error message if response label has been used for another ResponseType.
	 */
	void responseLabelHasBeenUsedForAnotherResponseType();

	/**
	 * Generate error message if an item of one group belongs to more than one section.
	 */
	void itemOfOneGroupBelongsToMoreThanOneSection();

	/**
	 * Generate error message if there is a not unique item placement in groups.
	 */
	void notUniqueItemPlacementInGroups();

	/**
	 * Generate error message if crf does not have items.
	 * 
	 * @throws CRFReadingException
	 *             the CRFReadingException
	 */
	void crfShouldHaveAtLeastOneItem() throws CRFReadingException;
}
