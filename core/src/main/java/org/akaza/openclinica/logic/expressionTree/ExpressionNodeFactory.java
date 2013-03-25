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
class ExpressionNodeFactory {

	static ExpressionNode getExpNode(Operator op, ExpressionNode node1, ExpressionNode node2) {
		if (op == Operator.PLUS || op == Operator.MINUS || op == Operator.MULTIPLY || op == Operator.DIVIDE) {
			return new ArithmeticOpNode(op, node1, node2);
		} else if (op == Operator.GREATER_THAN || op == Operator.GREATER_THAN_EQUAL || op == Operator.LESS_THAN
				|| op == Operator.LESS_THAN_EQUAL) {
			return new RelationalOpNode(op, node1, node2);
		} else if (op == Operator.EQUAL || op == Operator.NOT_EQUAL || op == Operator.CONTAINS) {
			return new EqualityOpNode(op, node1, node2);
		} else if (op == Operator.OR || op == Operator.AND) {
			return new ConditionalOpNode(op, node1, node2);
		} else {
			throw new OpenClinicaSystemException("OCRERR_0003");
		}

	}
}
