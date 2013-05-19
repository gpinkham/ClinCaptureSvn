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

package org.akaza.openclinica.control;

import java.util.Collection;

import org.jmesa.core.CoreContext;
import org.jmesa.util.ItemUtils;
import org.jmesa.view.AbstractExportView;
import org.jmesa.view.component.Table;

public class XmlView extends AbstractExportView {

	public XmlView(Table table, CoreContext coreContext) {
		super(table, coreContext);
	}

	public byte[] getBytes() {
		String render = (String) render();
		return render.getBytes();
	}

	public Object render() {
		StringBuilder data = new StringBuilder();

		int rowcount = 0;
		Collection<?> items = getCoreContext().getPageItems();
		for (Object item : items) {
			rowcount++;

			if (rowcount == 1)
				data.append("" + ItemUtils.getItemValue(item, "ruleSetRuleId"));
			else
				data.append("," + ItemUtils.getItemValue(item, "ruleSetRuleId"));

		}

		return data.toString();
	}
}
