package org.akaza.openclinica.web.table.sdv;

import org.jmesa.core.filter.FilterMatcher;

public class SDVEventNameMatcher implements FilterMatcher {

	public boolean evaluate(Object itemValue, String filterValue) {

		String item = String.valueOf(itemValue);
		String filter = String.valueOf(filterValue);

		return item.replaceFirst(filter, "").matches("\\(\\d+\\)");
	}

}
