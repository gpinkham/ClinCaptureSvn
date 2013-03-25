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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.bean.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * String edit checks should only support EQUAL , NOTEQUAL
 */

public class StringEditCheck implements EditCheckInterface {

	// if ( x = y ) is true?
	String xSourceValue;
	String ySourceValue;
	Operator operator;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public StringEditCheck(String sourceValue, String sourceValue2, Operator operator) {
		super();
		xSourceValue = sourceValue;
		ySourceValue = sourceValue2;
		if (!isOperatorAccepted(operator)) {
			throw new RuntimeException("The provided Operator is not Accepted");
		}
	}

	private boolean isOperatorAccepted(Operator suppliedOperator) {
		if (suppliedOperator == Operator.EQUAL || suppliedOperator == Operator.NOTEQUAL) {
			this.operator = suppliedOperator;
			return true;
		}
		return false;
	}

	public boolean check() {
		logger.info("xSourceValue : " + xSourceValue);
		logger.info("xSourceValue : " + ySourceValue);
		logger.info("Operator : " + operator);
		if (operator == Operator.EQUAL) {
			return xSourceValue.equals(ySourceValue) ? true : false;
		}
		if (operator == Operator.NOTEQUAL) {
			return !xSourceValue.equals(ySourceValue) ? true : false;
		}
		return false;
	}
}
