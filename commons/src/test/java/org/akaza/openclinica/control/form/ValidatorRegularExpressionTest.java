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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ValidatorRegularExpressionTest {

	private String description;
	private String regularExpression;
	private ValidatorRegularExpression validatorRegularExpression;

	@Before
	public void setUp() {
		description = "test description";
		regularExpression = "test expression";
		validatorRegularExpression = new ValidatorRegularExpression(description, regularExpression);
	}

	@Test
	public void testThatGetDescriptionMethodReturnsCorrectValue() {
		assertTrue(validatorRegularExpression.getDescription().equals(description));
	}

	@Test
	public void testThatGetRegularExpressionMethodReturnsCorrectValue() {
		assertTrue(validatorRegularExpression.getRegularExpression().equals(regularExpression));
	}

}
