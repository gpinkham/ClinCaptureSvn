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

import org.akaza.openclinica.exception.OpenClinicaSystemException;

/**
 * @author Krikor Krumlian
 * 
 */
public class ConstantNode extends ExpressionNode {
	String number; // The number.

	ConstantNode(String val) {
		// Construct a ConstNode containing the specified number.
		number = val;
	}

	@Override
	String getNumber() {
		return number;
	}

	@Override
	String testCalculate() throws OpenClinicaSystemException {
		return calculate();
	}

	@Override
	String calculate() throws OpenClinicaSystemException {
		// The value of the node is the number that it contains.
		return number;
	}

	@Override
	void printStackCommands() {
		// On a stack machine, just push the number onto the stack.
		logger.info("  Push " + number);
	}
}
