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

package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;

import java.util.Comparator;

// clinovo - ticket #41
public class CrfComparator implements Comparator {

	/*
	 * // by name public int compare(Object o1, Object o2) { String crfName1 = getCrfName(o1); String crfName2 =
	 * getCrfName(o2); return crfName1.compareTo(crfName2); }
	 * 
	 * private String getCrfName(Object o) { String crfName = ""; if (o instanceof DisplayEventCRFBean) { crfName =
	 * ((DisplayEventCRFBean)o).getEventCRF().getCrf().getName(); } else if (o instanceof DisplayEventDefinitionCRFBean)
	 * { crfName = ((DisplayEventDefinitionCRFBean)o).getEdc().getCrf().getName(); } return crfName; }
	 */

	// by real order
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
