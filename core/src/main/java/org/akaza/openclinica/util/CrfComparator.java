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

package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;

import java.util.Comparator;

@SuppressWarnings({"rawtypes"})
public class CrfComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		Integer ordinal1 = getOrdinal(o1);
		Integer ordinal2 = getOrdinal(o2);
		return ordinal1.compareTo(ordinal2);
	}

	private int getOrdinal(Object o) {
		Integer ordinal = 0;
		if (o instanceof DisplayEventCRFBean) {
			ordinal = ((DisplayEventCRFBean) o).getEventDefinitionCRF().getOrdinal();
		} else if (o instanceof DisplayEventDefinitionCRFBean) {
			ordinal = ((DisplayEventDefinitionCRFBean) o).getEdc().getOrdinal();
		}
		return ordinal;
	}
}
