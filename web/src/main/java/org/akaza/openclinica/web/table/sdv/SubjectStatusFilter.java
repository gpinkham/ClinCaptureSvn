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

import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: May 19, 2009
 */
public class SubjectStatusFilter extends DroplistFilterEditor {
	// AVAILABLE, PENDING, PRIVATE, UNAVAILABLE, LOCKED, DELETED,
	// AUTO_DELETED, SIGNED, FROZEN,SOURCE_DATA_VERIFICATION
	@Override
	protected List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		options.add(new Option("available", "Available"));
		options.add(new Option("pending", "Pending"));
		options.add(new Option("private", "Private"));
		options.add(new Option("unavailable", "Unavailable"));
		options.add(new Option("locked", "Locked"));
		options.add(new Option("deleted", "Deleted"));
		options.add(new Option("auto_deleted", "Auto deleted"));
		options.add(new Option("signed", "Signed"));
		options.add(new Option("frozen", "Frozen"));
		options.add(new Option("source_data_verification", "SDV"));
		return options;
	}
}
