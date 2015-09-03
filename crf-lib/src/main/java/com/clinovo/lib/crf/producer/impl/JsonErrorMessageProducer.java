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
@SuppressWarnings("unused")
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
	public void crfVersionIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfVersionDescriptionLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfRevisionNotesIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfRevisionNotesLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameIsBlank() throws CRFReadingException {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void crfNameHasAlreadyBeenUsed() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void didNotMatchCrfName(String crfName) throws CRFReadingException {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionLabelIsDuplicated() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionTitleLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionInstructionsLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void sectionPageNumberLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupLabelIsDuplicated() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void groupHeaderLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatNumIsWrong() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatMaxIsWrong() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsDuplicated() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemNameIsNotMatchingRegexp() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDescriptionLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemLeftTextLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemRightTextLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHeaderLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSubHeaderLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemSectionLabelLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemGroupLabelLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemUnitsLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemParentItemIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasNestedParentItem() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void repeatingGroupHasParentItem() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseTypeIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void hasRadioWithDefault() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelShouldBeFile() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsTextIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsText() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsText() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentNumberOfOptionsValues() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasDifferentValuesForOptionsValues() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionStartsWithFunc() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void expressionIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemHasIncompleteOptionValuePair() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemMustBeListedBeforeAnotherItem() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsMustHaveTheSameGroup() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemsShouldNotHaveTheSameGroup() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDataTypeShouldBeFile() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeInteger() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseOptionsValuesShouldBeReal() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void doesNotMatchDataTypeOfItemWithSameResponseLabel() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalIsNotAvailable() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void widthDecimalHasErrors() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgIsBlank() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpErrorMsgLengthIsExceeded() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void regexpIsInvalidRegularExpression() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void validationColumnHasInvalidRegularExpression() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void phiIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void requiredIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemDisplayStatusIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlResponseValueIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void controlItemNameIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void simpleConditionalDisplayIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void ontologyNameIsNotValid() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateCodingItemTypeToCode() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void needToUpdateMedicalCodingReferenceItemType() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	public void responseLabelHasBeenUsedForAnotherResponseType() {
		//
	}
}
