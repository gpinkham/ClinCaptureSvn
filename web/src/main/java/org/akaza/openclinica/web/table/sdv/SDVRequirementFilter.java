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

package org.akaza.openclinica.web.table.sdv;

import org.akaza.openclinica.domain.SourceDataVerification;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 19, 2009
 */
public class SDVRequirementFilter extends DroplistFilterEditor {

	@Override
	protected List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		String optionA = SourceDataVerification.AllREQUIRED.toString() + " & "
				+ SourceDataVerification.PARTIALREQUIRED.toString();
		options.add(new Option(optionA, optionA));
		for (SourceDataVerification sdv : SourceDataVerification.values()) {
			if (sdv != SourceDataVerification.NOTAPPLICABLE) {
				options.add(new Option(sdv.toString(), sdv.toString()));
			}
		}

		return options;
	}
}
