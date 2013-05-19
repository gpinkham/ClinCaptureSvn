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
 *
 * Created on Feb 23, 2005
 */
package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ExtractCRFVersionBean extends EntityBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private String code;
	private String crfName;

	OrderedEntityBeansSet items = new OrderedEntityBeansSet(new ItemBean());

	public ItemBean addItem(Integer itemId, String itemName) {
		if (itemId == null || itemName == null) {
			logger.info("item null!");
			return new ItemBean();
		}

		ItemBean ib = new ItemBean();
		ib.setId(itemId.intValue());
		ib.setName(itemName);
		return (ItemBean) items.add(ib);
	}

	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return Returns the crfName.
	 */
	public String getCrfName() {
		return crfName;
	}

	/**
	 * @param crfName
	 *            The crfName to set.
	 */
	public void setCrfName(String crfName) {
		this.crfName = crfName;
	}

	public OrderedEntityBeansSet getItems() {
		return items;
	}

	public static String getCode(int ind) {
		return "" + ind;
	}
}
