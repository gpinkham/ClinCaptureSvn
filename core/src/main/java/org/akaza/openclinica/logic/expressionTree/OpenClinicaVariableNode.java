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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

/**
 * @author Krikor Krumlian
 * 
 */
public class OpenClinicaVariableNode extends ExpressionNode {

	String number;
	Boolean optimiseRuleValidator = new Boolean(false);

	OpenClinicaVariableNode(String val, ExpressionService expressionService, Boolean optimiseRuleValidator) {
		number = val;
		this.expressionService = expressionService;
		this.optimiseRuleValidator = optimiseRuleValidator;
	}

	OpenClinicaVariableNode(String val, ExpressionService expressionService) {
		number = val;
		this.expressionService = expressionService;
	}

	@Override
	String getNumber() {
		return number;

	}

	/**
	 * 
	 * getTestValues() returns a hashMap of user defined values getResponseTestValues() is empty and will be filled with
	 * variables being processed
	 * 
	 * @param var
	 *            the default test value
	 * @return the Value
	 */
	private String theTest(String var) {
		if (getTestValues() == null) {
			return var;
		} else if (getTestValues().get(number) == null) {
			getTestValues().put(number, var);
			getResponseTestValues().put(number, var);
			return var;
		} else {
			getResponseTestValues().put(number, getTestValues().get(number));
			return getTestValues().get(number);
		}

	}

	@Override
	String testCalculate() throws OpenClinicaSystemException {

		validate();
		String variableValue = testCalculateVariable();
		if (variableValue != null) {
			return variableValue;
		}
		ItemBean item = getExpressionService().getItemBeanFromExpression(number);
		String testString = "test";
		String testInt = "1";
		String testBoolean = "true";
		String testDate = "2008-01-01";
		String testPDate = "";
		if (item != null) {
			ItemDataType itemDataType = ItemDataType.get(item.getItemDataTypeId());
			switch (itemDataType.getId()) {
			case 1: {
				return theTest(testBoolean);
			}
			case 2: {
				return theTest(testBoolean);
			}
			case 3: {
				return theTest(testString);
			}
			case 4: {
				return theTest(testString);
			}
			case 5: {
				return theTest(testString);
			}
			case 6: {
				return theTest(testInt);
			}
			case 7: {
				return theTest(testInt);
			}
			case 8: {
				return theTest(testString);
			}
			case 9: {
				return theTest(testDate);
			}
			case 10: {
				return theTest(testPDate);
			}
			case 11: {
				return theTest(testString + ".txt");
			}
			default:
				throw new OpenClinicaSystemException("OCRERR_0011");
			}
		} else {
			throw new OpenClinicaSystemException("OCRERR_0012", new String[] { number });
		}
	}

	@Override
	String calculate() throws OpenClinicaSystemException {
		// The value of the node is the number that it contains.
		// return number;
		validate();
		String variableValue = calculateVariable();
		if (variableValue != null) {
			return variableValue;
		}
		return getExpressionService().evaluateExpression(number);
	}

	void validate() throws OpenClinicaSystemException {

		if (calculateVariable() != null) {

		} else if (!getExpressionService().ruleExpressionChecker(number, optimiseRuleValidator)) {
			logger.info("Go down");
			throw new OpenClinicaSystemException("OCRERR_0013", new Object[] { number });
		}
	}

	private String calculateVariable() throws OpenClinicaSystemException {
		if (number.equals("_CURRENT_DATE")) {
			setDateParameter(true);
			DateMidnight dm = new DateMidnight();
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		} else if (number.equals("_SUBJECT_DOB")) {
			setDateParameter(true);
			if (getExpressionParser().getSubjectDob() == null) {
				if (getExpressionParser().isImportRulesMode()) {
					getExpressionParser().setSubjectDob(new Date());
				} else {
					throw new OpenClinicaSystemException("OCRERR_CANT_GET_SUBJEC_DOB", new Object[] {});
				}
			}
			DateMidnight dm = new DateMidnight(getExpressionParser().getSubjectDob().getTime());
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		} else if (number.equals("_SUBJECT_ENROLLMENT")) {
			setDateParameter(true);
			if (getExpressionParser().getSubjectEnrollment() == null) {
				if (getExpressionParser().isImportRulesMode()) {
					getExpressionParser().setSubjectEnrollment(new Date());
				} else {
					throw new OpenClinicaSystemException("OCRERR_CANT_GET_SUBJECT_ENROLLMENT", new Object[] {});
				}
			}
			DateMidnight dm = new DateMidnight(getExpressionParser().getSubjectEnrollment().getTime());
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		}
		return null;
	}

	private String testCalculateVariable() throws OpenClinicaSystemException {
		if (number.equals("_CURRENT_DATE")) {
			setDateParameter(true);
			DateMidnight dm = new DateMidnight();
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		} else if (number.equals("_SUBJECT_DOB")) {
			setDateParameter(true);
			if (getExpressionParser().getSubjectDob() == null) {
				if (getExpressionParser().isImportRulesMode()) {
					getExpressionParser().setSubjectDob(new Date());
				} else {
					throw new OpenClinicaSystemException("OCRERR_CANT_GET_SUBJEC_DOB", new Object[] {});
				}
			}
			DateMidnight dm = new DateMidnight(getExpressionParser().getSubjectDob().getTime());
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		} else if (number.equals("_SUBJECT_ENROLLMENT")) {
			setDateParameter(true);
			if (getExpressionParser().getSubjectEnrollment() == null) {
				if (getExpressionParser().isImportRulesMode()) {
					getExpressionParser().setSubjectEnrollment(new Date());
				} else {
					throw new OpenClinicaSystemException("OCRERR_CANT_GET_SUBJECT_ENROLLMENT", new Object[] {});
				}
			}
			DateMidnight dm = new DateMidnight(getExpressionParser().getSubjectEnrollment().getTime());
			DateTimeFormatter fmt = ISODateTimeFormat.date();
			return fmt.print(dm);
		}
		return null;
	}

	@Override
	void printStackCommands() {
		// On a stack machine, just push the number onto the stack.
		logger.info("  Push " + number);
	}
}
