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

package org.akaza.openclinica.web.table.sdv;

import org.akaza.openclinica.domain.SourceDataVerification;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * SDVRequirementFilter.
 */
public class SDVRequirementFilter extends DroplistFilterEditor {

	private boolean isItemLevelSDVAllowed;

	/**
	 * public constructor.
	 *
	 * @param isItemLevelSDVAllowed defines if the Item Level SDV feature is enabled for the study
	 */
	public SDVRequirementFilter(boolean isItemLevelSDVAllowed) {
		this.isItemLevelSDVAllowed = isItemLevelSDVAllowed;
	}

	@Override
	protected List<Option> getOptions() {

		List<Option> options = new ArrayList<Option>();
		if (isItemLevelSDVAllowed) {
			String optionA = SourceDataVerification.AllREQUIRED.toString() + " & "
					+ SourceDataVerification.PARTIALREQUIRED.toString();
			options.add(new Option(optionA, optionA));
		}
		for (SourceDataVerification sdv : SourceDataVerification.values()) {
			options.add(new Option(sdv.toString(), sdv.toString()));
		}
		if (!isItemLevelSDVAllowed) {
			String partialRequiredOption = SourceDataVerification.PARTIALREQUIRED.toString();
			options.remove(new Option(partialRequiredOption, partialRequiredOption));
		}
		return options;
	}
}
