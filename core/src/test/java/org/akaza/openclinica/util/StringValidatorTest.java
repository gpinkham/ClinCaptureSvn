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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package org.akaza.openclinica.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for org.akaza.openclinica.util.StringValidator class.
 * 
 * @author Frank
 * 
 */
public class StringValidatorTest {

	/**
	 * Tests isValidURL method with valid url.
	 */
	@Test
	public void testUrlValidatorWithValidUrl() {
		assertTrue(StringValidator.isValidURL("http://www.clinovo.com"));
	}

	/**
	 * Tests isValidURL method with invalid url.
	 */
	@Test
	public void testUrlValidatorWithInvalidUrl() {
		assertFalse(StringValidator.isValidURL("Invalid URL"));
	}

	/**
	 * Tests isValidInteger method with valid positive integer.
	 */
	@Test
	public void testIntegerValidatorWithValidPositiveInteger() {
		assertTrue(StringValidator.isValidInteger("200"));
	}

	/**
	 * Tests isValidInteger with valid negative integer.
	 */
	@Test
	public void testIntegerValidatorWithValidNegativeInteger() {
		assertTrue(StringValidator.isValidInteger("-200"));
	}

	/**
	 * Tests isValidInteger with non-integer.
	 */
	@Test
	public void testIntegerValidatorWithNonInteger() {
		assertFalse(StringValidator.isValidInteger("67.9"));
	}

	/**
	 * Tests hasNumber method with valid text.
	 */
	@Test
	public void testHasNumberWithValidText() {
		assertTrue(StringValidator.hasNumber("Test has 1 number"));
	}

	/**
	 * Tests hasNumber method with invalid text.
	 */
	@Test
	public void testHasNumberWithInvalidText() {
		assertFalse(StringValidator.hasNumber("Test has no number"));
	}

	/**
	 * Tests isValidNumber method with positive floating point number.
	 */
	@Test
	public void testNumberValidatorWithValidPositiveFloatingPointNumber() {
		assertTrue(StringValidator.isValidNumber("200.90"));
	}

	/**
	 * Tests isValidNumber with positive integer.
	 */
	@Test
	public void testNumberValidatorWithValidPositiveInteger() {
		assertTrue(StringValidator.isValidNumber("200"));
	}

	/**
	 * Tests isValidNumber method with valid negative number.
	 */
	@Test
	public void testNumberValidatorWithValidNegativeNumber() {
		assertTrue(StringValidator.isValidNumber("-200.6"));
	}

	/**
	 * Tests isValidNumber method with text.
	 */
	@Test
	public void testNumberValidatorWithText() {
		assertFalse(StringValidator.isValidNumber("Not Number"));
	}

	/**
	 * Tests isValidDateYYYYMMDD method with valid date.
	 */
	@Test
	public void testDateYYYYMMDDValidatorWithValidDate() {
		assertTrue(StringValidator.isValidDateYYYYMMDD("2014-04-17"));
	}

	/**
	 * Tests isValidDateYYYYMMDD method with invalid date.
	 */
	@Test
	public void testDateYYYYMMDDValidatorWithInvalidDate() {
		assertFalse(StringValidator.isValidDateYYYYMMDD("2014-67-17"));
	}

	/**
	 * Tests the itemExitsInList method with input expecting true.
	 */
	@Test
	public void testThatItemExitsInListReturnsTrue() {
		List<String> stringList = new ArrayList<String>();
		String testString = "my string";
		stringList.add("String1");
		stringList.add("String2");
		stringList.add(testString);
		assertTrue(StringValidator.itemExitsInList(testString, stringList));
	}

	/**
	 * Tests the itemExitsInList method with input expecting false.
	 */
	@Test
	public void testThatItemExistsInListReturnsFalse() {
		List<String> stringList = new ArrayList<String>();
		String testString = "my string";
		stringList.add("String1");
		stringList.add("String2");
		stringList.add("String3");
		assertFalse(StringValidator.itemExitsInList(testString, stringList));
	}
}
