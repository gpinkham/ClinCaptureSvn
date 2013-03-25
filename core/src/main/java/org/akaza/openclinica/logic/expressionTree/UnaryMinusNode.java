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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * @author Krikor Krumlian
 * 
 */
public class UnaryMinusNode extends ExpressionNode {
	ExpressionNode operand; // The operand to which the unary minus applies.

	UnaryMinusNode(ExpressionNode operand) {
		// Construct a UnaryMinusNode with the specified operand.
		assert operand != null;
		this.operand = operand;
	}

	@Override
	String testCalculate() throws OpenClinicaSystemException {
		return calculate();
	}

	@Override
	String calculate() {
		// The value is the negative of the value of the operand.
		String theOperand = operand.value();
		validate(theOperand);
		double neg = Double.valueOf(theOperand);
		return String.valueOf(-neg);
	}

	void validate(String theOperand) throws OpenClinicaSystemException {
		try {
			Double.valueOf(theOperand);
		} catch (NumberFormatException e) {
			throw new OpenClinicaSystemException("OCRERR_0015", new Object[] { theOperand });
		}
	}

	@Override
	void printStackCommands() {
		// To evaluate this expression on a stack machine, first do
		// whatever is necessary to evaluate the operand, leaving the
		// operand on the stack. Then apply the unary minus (which means
		// popping the operand, negating it, and pushing the result).
		operand.printStackCommands();
		logger.info("  Unary minus");
	}
}
