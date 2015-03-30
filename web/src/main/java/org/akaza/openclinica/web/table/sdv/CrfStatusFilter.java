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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.html.editor.DroplistFilterEditor;

/**
 * The drop list for the CRF Status filter.
 */
public class CrfStatusFilter extends DroplistFilterEditor {

	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();

	@Override
	protected List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		options.add(new Option(reswords.getString("completed"), reswords.getString("completed")));
		options.add(new Option(reswords.getString("locked"), reswords.getString("locked")));
		return options;
	}
}
