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

import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * The drop list for the CRF Status filter.
 */
public class CrfStatusFilter extends DroplistFilterEditor {
	@Override
	protected List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		// options.add(new Option("1", "Scheduled"));
		// options.add(new Option("2", "Not scheduled"));
		// options.add(new Option("3", "Data entry started"));
		options.add(new Option("Completed", "Completed"));
		// options.add(new Option("5", "Stopped"));
		// options.add(new Option("6", "Skipped"));
		options.add(new Option("Locked", "Locked"));
		// options.add(new Option("8", "Signed"));
		return options;
	}
}
