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
public class EqualityOpNode extends ExpressionNode {
	Operator op; // The operator.
	ExpressionNode left; // The expression for its left operand.
	ExpressionNode right; // The expression for its right operand.

	EqualityOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
		// Construct a BinOpNode containing the specified data.
		assert op == Operator.EQUAL || op == Operator.NOT_EQUAL
				|| op == Operator.CONTAINS || op == Operator.NOTCONTAINS;
		assert left != null && right != null;
		this.op = op;
		this.left = left;
		this.right = right;
	}

	@Override
	String testCalculate() throws OpenClinicaSystemException {
		String x = null;
		String y = null;
		String l = left.testValue();
		String r = right.testValue();
		try {
			x = correctStringValue(l);
			y = correctStringValue(r);
		} catch (NumberFormatException nfe) {
			logger.error(nfe.getMessage());
		}
		if (x == null && y == null) {
			x = String.valueOf(l);
			y = String.valueOf(r);
		}
		return calc(x, y);
	}
	
	/***
	 * Returns string value of operand depending on the operator. The idea is to avoid unncessary .0 float values
	 * when performing a CONTAINS or NOTCONTAINS operation
	 * @param value
	 * @return
	 */
	private String correctStringValue(String value){
		try{

			//if value ends with .0 then it should be treated as a whole number
			if(value.endsWith(".0")){
				return String.valueOf((long)Float.valueOf(value).floatValue());
			}
			else {
				return String.valueOf(Float.valueOf(value));
			}
		
		} catch(NumberFormatException nfe){
			return value;
		}
	}

	@Override
	String calculate() throws OpenClinicaSystemException {
		String x = null;
		String y = null;
		String l = left.value();
		String r = right.value();
		
		try {
			Float fx = Float.valueOf(l);
			Float fy = Float.valueOf(r);
			x = fx.toString();
			y = fy.toString();
		} catch (NumberFormatException nfe) {
			logger.error(nfe.getMessage());
		}
		if (x == null && y == null) {
			x = String.valueOf(l);
			y = String.valueOf(r);
		}
		return calc(x, y);

	}

	private String calc(String x, String y) throws OpenClinicaSystemException {
		switch (op) {
		case EQUAL:
			return String.valueOf(x.equals(y));
		case NOT_EQUAL:
			return String.valueOf(!x.equals(y));
		case CONTAINS:
			return String.valueOf(x.contains(y));
		case NOTCONTAINS:
			return String.valueOf(!x.contains(y));
		default:
			throw new OpenClinicaSystemException("OCRERR_0002", new Object[] {
					left.value(), right.value(), op.toString() });
		}
	}

	@Override
	void printStackCommands() {
		// To evalute the expression on a stack machine, first do
		// whatever is necessary to evaluate the left operand, leaving
		// the answer on the stack. Then do the same thing for the
		// second operand. Then apply the operator (which means popping
		// the operands, applying the operator, and pushing the result).
		left.printStackCommands();
		right.printStackCommands();
		logger.info("  Operator " + op);
	}
}
