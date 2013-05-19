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
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.logic.score.function;

import java.util.HashMap;

public final class Decode extends AbstractFunction {
	public Decode() {
		super();
	}

	/**
	 * @see Function#execute(HashMap)
	 */
	public void execute() {
		logger.info("Execute the function Decode... ");

		String condition = getArgument(0).toString();

		if (condition == null || condition.length() == 0) {
			value = "";
			return;
		}
		boolean found = false;
		for (int i = 1; i < argumentCount() - 1; i += 2) {
			if (condition.equals(getArgument(i).toString())) {
				value = getArgument(i + 1).toString();
				found = true;
				break;
			}
		}

		if (!found) {
			if (argumentCount() % 2 == 0) {
				value = getArgument(argumentCount() - 1).toString();
			} else {
				value = "";
			}
		}
	}
}
