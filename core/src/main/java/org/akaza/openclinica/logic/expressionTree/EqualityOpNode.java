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
public class EqualityOpNode extends ExpressionNode {
	Operator op; // The operator.
	ExpressionNode left; // The expression for its left operand.
	ExpressionNode right; // The expression for its right operand.

	EqualityOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
		// Construct a BinOpNode containing the specified data.
		assert op == Operator.EQUAL || op == Operator.NOT_EQUAL
				|| op == Operator.CONTAINS;
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
			Float fx = Float.valueOf(l);
			Float fy = Float.valueOf(r);
			x = fx.toString();
			y = fy.toString();
		} catch (NumberFormatException nfe) {
			// Don't do anything cause we were just testing above.
		}
		if (x == null && y == null) {
			x = String.valueOf(l);
			y = String.valueOf(r);
		}
		return calc(x, y);
	}

	@Override
	String calculate() throws OpenClinicaSystemException {
		String x = null;
		String y = null;
		String l = left.value();
		String r = right.value();
		if (dateShouldBeEntered(left, right)) {
			throw new OpenClinicaSystemException(
					"OCRERR_DATE_SHOULD_BE_ENTERED", new Object[] {});
		}
		try {
			Float fx = Float.valueOf(l);
			Float fy = Float.valueOf(r);
			x = fx.toString();
			y = fy.toString();
		} catch (NumberFormatException nfe) {
			// Don't do anything cause we were just testing above.
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
