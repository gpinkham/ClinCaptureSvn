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

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.html.editor.DroplistFilterEditor;

import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SubjectStatusFilter extends DroplistFilterEditor {

	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();

	@Override
	protected List<Option> getOptions() {
		ResourceBundleProvider.updateLocale(getWebContext().getLocale());
		List<Option> options = new ArrayList<Option>();
		options.add(new Option("available", reswords.getString("available")));
		options.add(new Option("pending", reswords.getString("pending")));
		options.add(new Option("private", reswords.getString("private")));
		options.add(new Option("unavailable", reswords.getString("unavailable")));
		options.add(new Option("locked", reswords.getString("locked")));
		options.add(new Option("deleted", reswords.getString("deleted")));
		options.add(new Option("auto_deleted", reswords.getString("auto_deleted")));
		options.add(new Option("signed", reswords.getString("Signed")));
		options.add(new Option("frozen", reswords.getString("frozen")));
		options.add(new Option("source_data_verification", reswords.getString("nav_sdv")));
		return options;
	}
}
