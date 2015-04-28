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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.ValidatorHelper;

@SuppressWarnings("rawtypes")
public class DiscrepancyValidatorTest {

	private FormDiscrepancyNotes notes;

	private MockHttpServletRequest request;

	private DiscrepancyValidator validator;

	@Before
	public void setUp() {
		Locale locale = new Locale("en");

		notes = new FormDiscrepancyNotes();

		request = new MockHttpServletRequest();
		LocaleResolver.updateLocale(request, locale);
		ResourceBundleProvider.updateLocale(LocaleResolver.getLocale(request));

		request.setParameter("field1", "1234");

		validator = new DiscrepancyValidator(new ValidatorHelper(request, Mockito.mock(ConfigurationDao.class)), notes);
	}

	@Test
	public void testThatValidateMethodReturnsCorrectErrorsMapSizeIfParameterIsNotEmpty() {
		validator.addValidation("field1", Validator.NO_BLANKS);
		HashMap map = validator.validate();
		assertTrue(map.size() == 0);
	}
	
	@Test
	public void testThatValidateMethodReturnsCorrectErrorsMapSizeIfParameterIsEmpty() {
		validator.addValidation("field111", Validator.NO_BLANKS);
		HashMap map = validator.validate();
		assertTrue(map.size() == 1);
		assertTrue(((ArrayList) map.get("field111")).size() == 1);
	}

	@Test
	public void testThatAnyValidationWillNotBeExecutedIfDNIsPresentAndTheAlwaysExecutedIsFalse() {
		notes.addNote("field111", new DiscrepancyNoteBean());
		validator.addValidation("field111", Validator.NO_BLANKS);
		validator.addValidation("field111", Validator.IS_A_DATE);
		validator.addValidation("field111", Validator.IS_AN_INTEGER);
		HashMap map = validator.validate("field111");
		assertTrue(map.size() == 0);
	}

	@Test
	public void testThatOnlyLastValidationWillBeExecutedIfTheAlwaysExecutedIsTrue() {
		notes.addNote("field111", new DiscrepancyNoteBean());
		validator.addValidation("field111", Validator.NO_BLANKS);
		validator.addValidation("field111", Validator.IS_A_DATE);
		validator.addValidation("field111", Validator.IS_AN_INTEGER);
		validator.alwaysExecuteLastValidation("field111");
		HashMap map = validator.validate("field111");
		assertTrue(((ArrayList) map.get("field111")).size() == 1);
	}
}
