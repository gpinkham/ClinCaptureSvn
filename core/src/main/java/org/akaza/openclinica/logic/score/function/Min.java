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

package org.akaza.openclinica.logic.score.function;

import org.apache.commons.math.stat.StatUtils;

public class Min extends AbstractFunction {
	public Min() {
		super();
	}

	/**
	 * If one argument is "", then the value of this function will be "" too.
	 * 
	 * @see Function#execute
	 */

	public void execute() {
		logger.info("Execute the function Min... ");

		double[] values = new double[argumentCount()];
		for (int i = 0; i < argumentCount(); i++) {
			String arg = getArgument(i).toString();
			if (arg == null || arg.length() == 0) {
				value = "";
				return;
			}
			try {
				values[i] = Double.parseDouble(arg);
			} catch (Exception e) {
				errors.put(new Integer(errorCount++), "Unparseable number:" + " " + arg + " " + "in evaluation of"
						+ " Min(); ");
			}
		}
		if (errors.size() > 0) {
			logger.error("The following errors happended when Min() evaluation was performed: " + errors);
			value = "";
			return;
		}

		if (values != null && values.length > 0) {
			double v = StatUtils.min(values);
			value = Double.toString(v);
		} else {
			value = "";
		}
	}

}
