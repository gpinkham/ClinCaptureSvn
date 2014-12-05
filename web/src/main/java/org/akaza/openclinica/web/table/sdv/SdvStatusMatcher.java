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
import org.jmesa.core.filter.FilterMatcher;

import java.util.ResourceBundle;

/**
 * A FilterMatcher designed to filter values of source data verification in a Jmesa table cell.
 */
public class SdvStatusMatcher implements FilterMatcher {

	private ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();

	public boolean evaluate(Object itemValue, String filterValue) {

		String item = String.valueOf(itemValue);
		String filter = String.valueOf(filterValue);

		return (filter.equalsIgnoreCase(reswords.getString("not_done"))
				|| (filter.equalsIgnoreCase(reswords.getString("complete")) && (item.contains("icon_DoubleCheck"))));
	}
}
