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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import java.util.Arrays;
import java.util.List;

// implicit controlled vocab, not stored in db

// Internationalized name and description in Term.getName and
// Term.getDescription()

public class NumericComparisonOperator extends Term {
	public static final NumericComparisonOperator EQUALS = new NumericComparisonOperator(1, "equal_to", "equal_to");
	public static final NumericComparisonOperator NOT_EQUALS = new NumericComparisonOperator(2, "not_equal_to",
			"not_equal_to");
	public static final NumericComparisonOperator LESS_THAN = new NumericComparisonOperator(3, "less_than", "less_than");
	public static final NumericComparisonOperator LESS_THAN_OR_EQUAL_TO = new NumericComparisonOperator(4,
			"less_than_or_equal_to", "less_than_or_equal_to");
	public static final NumericComparisonOperator GREATER_THAN = new NumericComparisonOperator(5, "greater_than",
			"greater_than");
	public static final NumericComparisonOperator GREATER_THAN_OR_EQUAL_TO = new NumericComparisonOperator(6,
			"greater_than_or_equal_to", "greater_than_or_equal_to");

	private static final NumericComparisonOperator[] members = { EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_OR_EQUAL_TO,
			GREATER_THAN, GREATER_THAN_OR_EQUAL_TO };
	public static final List list = Arrays.asList(members);

	private NumericComparisonOperator(int id, String name) {
		super(id, name);
	}

	private NumericComparisonOperator(int id, String name, String description) {
		super(id, name, description);
	}

	private NumericComparisonOperator() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static NumericComparisonOperator get(int id) {
		Term term = Term.get(id, list);
		return term instanceof NumericComparisonOperator ? (NumericComparisonOperator) term : null;
	}
}
