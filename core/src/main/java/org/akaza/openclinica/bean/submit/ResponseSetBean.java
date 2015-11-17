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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.HashMap;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ResponseType;

/**
 * ResponseSetBean.
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class ResponseSetBean extends EntityBean {

	private org.akaza.openclinica.bean.core.ResponseType responseType;

	/**
	 * A set of options to display to the user. The elements are ResponseOptionBean objects.
	 */
	private ArrayList<ResponseOptionBean> options;

	/**
	 * A HashMap which tells us, for a given value, what is the index in the options array where the option with that
	 * value is stored? The keys are values, the values are Integer objects.
	 */
	private HashMap optionIndexesByValue;

	/**
	 * Contains the value of the item if the item is a text input. Not in the database.
	 */
	private String value;

	/**
	 * Constructor.
	 */
	public ResponseSetBean() {
		super();
		setResponseType(org.akaza.openclinica.bean.core.ResponseType.TEXT);
		options = new ArrayList<ResponseOptionBean>();
		optionIndexesByValue = new HashMap();
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return getName();
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		setName(label);
	}

	/**
	 * @return Returns the options.
	 */
	public ArrayList<ResponseOptionBean> getOptions() {
		return options;
	}

	/**
	 * @return Returns the responseType.
	 */
	public org.akaza.openclinica.bean.core.ResponseType getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType
	 *            The responseType to set.
	 */
	public void setResponseType(org.akaza.openclinica.bean.core.ResponseType responseType) {
		this.responseType = responseType;
	}

	/**
	 * @return Returns the responseTypeId.
	 */
	public int getResponseTypeId() {
		return responseType.getId();
	}

	/**
	 * @param responseTypeId
	 *            The responseTypeId to set.
	 */
	public void setResponseTypeId(int responseTypeId) {
		responseType = org.akaza.openclinica.bean.core.ResponseType.get(responseTypeId);
	}

	/**
	 * Set options.
	 * 
	 * @param optionsText
	 *            String
	 * @param optionsValues
	 *            String
	 */
	public void setOptions(String optionsText, String optionsValues) {
		if (responseType.getId() == ResponseType.CALCULATION.getId()
				|| responseType.getId() == ResponseType.GROUP_CALCULATION.getId()
				|| responseType.getId() == ResponseType.INSTANT_CALCULATION.getId()) {
			ResponseOptionBean ro = new ResponseOptionBean();
			ro.setValue(optionsValues.replace("\\,", ","));
			ro.setText(optionsText);
			options.add(ro);
			optionIndexesByValue.put(optionsValues, options.size() - 1);
		} else {
			String text1 = optionsText.replaceAll("\\\\,", "##");
			String value1 = optionsValues.replaceAll("\\\\,", "##");
			String[] texts = text1.split(",", -1);
			String[] values = value1.split(",", -1);
			for (int i = 0; i < values.length; i++) {
				ResponseOptionBean ro = new ResponseOptionBean();
				if (values[i] == null) {
					continue;
				}
				String value = values[i].trim();
				value = value.replaceAll("##", ",");
				ro.setValue(value);
				if (texts.length <= i || texts[i] == null) {
					ro.setText(value);
				} else {
					String t = texts[i].trim();
					String t1 = t.replaceAll("##", ",");
					ro.setText(t1);
				}
				options.add(ro);
				optionIndexesByValue.put(value, options.size() - 1);
			}
		}
	}

	/**
	 * Add an option to the array of response options.
	 * 
	 * @param ro
	 *            The ResponseOptionBean to add.
	 */
	public void addOption(ResponseOptionBean ro) {
		options.add(ro);
		optionIndexesByValue.put(ro.getValue(), options.size() - 1);
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Indicate that a ResponseOption with the specified value was or was not selected.
	 * 
	 * @param value
	 *            The value of the ResponseOption whose selection is specified.
	 * @param selected
	 *            <code>true</code> if the ResponseOption was selected, <code>false</code> otherwise.
	 */
	public void setSelected(String value, boolean selected) {
		if (optionIndexesByValue.containsKey(value)) {
			int ind = (Integer) optionIndexesByValue.get(value);

			if (ind >= 0 && ind < options.size()) {
				if (responseType.getId() == ResponseType.RADIO.getId()
						|| responseType.getId() == ResponseType.SELECT.getId()) {
					// only for radio and single-select menu
					options = removeSelection();
				}
				ResponseOptionBean rob = (ResponseOptionBean) options.get(ind);
				rob.setSelected(selected);
				options.set(ind, rob);
			}
		}
	}

	/**
	 * Removes selection.
	 * 
	 * @return ArrayList
	 */
	public ArrayList removeSelection() {
		ArrayList list = new ArrayList();
		for (ResponseOptionBean rob : options) {
			if (rob.isSelected()) {
				rob.setSelected(false);
			}
			list.add(rob);
		}
		return list;
	}
}
