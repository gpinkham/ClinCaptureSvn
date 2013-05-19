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
package org.akaza.openclinica.domain.rule.expression;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.logic.expressionTree.ExpressionNode;
import org.akaza.openclinica.logic.expressionTree.OpenClinicaExpressionParser;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.sql.DataSource;

/**
 * @author Krikor Krumlian
 * 
 */
public class OpenClinicaV1ExpressionProcessor implements ExpressionProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	ExpressionBean e;
	Pattern[] pattern;
	ExpressionNode node;
	OpenClinicaExpressionParser oep;
	DataSource ds;
	ExpressionService expressionService;
	ExpressionObjectWrapper expressionWrapper;
	ResourceBundle respage;

	public OpenClinicaV1ExpressionProcessor(ExpressionObjectWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
		this.e = expressionWrapper.getExpressionBean();

	}

	public String isRuleAssignmentExpressionValid() {
		try {
			expressionService = new ExpressionService(expressionWrapper);
			if (expressionService.ruleSetExpressionChecker(e.getValue())) {
				return null;
			} else {
				MessageFormat mf = new MessageFormat("");
				String errorCode = "OCRERR_0024";
				mf.applyPattern(respage.getString(errorCode));
				Object[] arguments = {};
				return errorCode + " : " + mf.format(arguments);
			}
		} catch (OpenClinicaSystemException e) {
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString(e.getErrorCode()));
			Object[] arguments = e.getErrorParams();
			return e.getErrorCode() + " : " + mf.format(arguments);
		}
	}

	public String isRuleExpressionValid() {
		try {
			oep = new OpenClinicaExpressionParser(expressionWrapper);
			String result = oep.parseAndTestEvaluateExpression(e.getValue());
			logger.info("Test Result : " + result);
			return null;
		} catch (OpenClinicaSystemException e) {
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString(e.getErrorCode()));
			Object[] arguments = e.getErrorParams();
			return e.getErrorCode() + " : " + mf.format(arguments);

		}
	}

	public String testEvaluateExpression() {
		try {
			oep = new OpenClinicaExpressionParser(expressionWrapper);
			String result = oep.parseAndTestEvaluateExpression(e.getValue());
			logger.info("Test Result : " + result);
			return "Pass : " + result;
		} catch (OpenClinicaSystemException e) {
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString(e.getErrorCode()));
			Object[] arguments = e.getErrorParams();
			return "Fail - " + e.getErrorCode() + " : " + mf.format(arguments);
		}
	}

	public HashMap<String, String> testEvaluateExpression(HashMap<String, String> testValues) {
		try {
			oep = new OpenClinicaExpressionParser(expressionWrapper);
			HashMap<String, String> resultAndTestValues = oep.parseAndTestEvaluateExpression(e.getValue(), testValues);
			String returnedResult = resultAndTestValues.get("result");
			logger.info("Test Result : " + returnedResult);
			resultAndTestValues.put("ruleValidation", "rule_valid");
			resultAndTestValues.put("ruleEvaluatesTo", returnedResult);

			return resultAndTestValues;
		} catch (OpenClinicaSystemException e) {
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString(e.getErrorCode()));
			Object[] arguments = e.getErrorParams();
			testValues.put("ruleValidation", "rule_invalid");
			testValues.put("ruleValidationFailMessage", e.getErrorCode() + " : " + mf.format(arguments));
			testValues.put("ruleEvaluatesTo", "");
			return testValues;
		}
	}

	public boolean process() {
		return false;
	}

	public void setExpression(ExpressionBean e) {
		this.e = e;
	}

	public void setRespage(ResourceBundle respage) {
		this.respage = respage;
	}

}
