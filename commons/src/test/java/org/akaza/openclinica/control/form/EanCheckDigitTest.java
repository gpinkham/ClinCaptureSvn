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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 */
package org.akaza.openclinica.control.form;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EanCheckDigitTest {

	private EanCheckDigit eanCheckDigit;

	@Before
	public void setUp() {
		eanCheckDigit = new EanCheckDigit();
	}

	@Test
	public void testThatIsValidMethodReturnsCorrectValue() {
		assertTrue(eanCheckDigit.isValid("0000000000000"));
	}

	@Test
	public void testThatIsValidMethodReturnsCorrectValueForCode77() {
		assertFalse(eanCheckDigit.isValid("77"));
	}

	@Test
	public void testThatToIntReturnsCorrectValue() throws Exception {
		assertTrue(eanCheckDigit.toInt('1', 0, 1) == 1);
	}

	@Test
	public void testThatWeightedValueReturnsCorrectValue() throws Exception {
		assertTrue(eanCheckDigit.weightedValue(45, 0, 1) == 45);
	}
}
