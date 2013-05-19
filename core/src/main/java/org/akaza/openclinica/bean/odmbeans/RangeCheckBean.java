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

/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */

public class RangeCheckBean {
	private String comparator;
	private String softHard;
	private String checkValue;
	private TranslatedTextBean errorMessage;
	private ArrayList<ElementRefBean> measurementUnitRefs;

	public RangeCheckBean() {
		errorMessage = new TranslatedTextBean();
		measurementUnitRefs = new ArrayList<ElementRefBean>();
	}

	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public String getComparator() {
		return this.comparator;
	}

	public void setSoftHard(String constraint) {
		this.softHard = constraint;
	}

	public String getSoftHard() {
		return this.softHard;
	}

	public void setCheckValue(String value) {
		this.checkValue = value;
	}

	public String getCheckValue() {
		return this.checkValue;
	}

	public void setErrorMessage(TranslatedTextBean errorMessage) {
		this.errorMessage = errorMessage;
	}

	public TranslatedTextBean getErrorMessage() {
		return this.errorMessage;
	}

	public ArrayList<ElementRefBean> getMeasurementUnitRefs() {
		return measurementUnitRefs;
	}

	public void setMeasurementUnitRefs(ArrayList<ElementRefBean> measurementUnitRefs) {
		this.measurementUnitRefs = measurementUnitRefs;
	}
}
