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

@SuppressWarnings("serial")
public class ExtractStudyEventDefinitionBeanOld extends EntityBean {
	private boolean repeating = false;
	private OrderedEntityBeansSet crfVersions = new OrderedEntityBeansSet(new ExtractCRFVersionBean());

	public ExtractCRFVersionBean addCRFVersion(Integer crfVersionId, String crfName, String crfVersionName) {
		if (crfVersionId == null || crfName == null || crfVersionName == null) {
			return new ExtractCRFVersionBean();
		}

		ExtractCRFVersionBean cvb = new ExtractCRFVersionBean();
		cvb.setId(crfVersionId.intValue());
		cvb.setName(crfVersionName);
		cvb.setCrfName(crfName);

		return (ExtractCRFVersionBean) crfVersions.add(cvb);
	}

	public void updateCRFVersion(ExtractCRFVersionBean cvb) {
		crfVersions.update(cvb);
	}

	public OrderedEntityBeansSet getCRFVersions() {
		return crfVersions;
	}

	public static String getCode(int ind) {
		if (ind > 26) {
			int digit1 = ind / 26;
			int digit2 = ind % 26;

			char letter1 = (char) ('A' + digit1);
			char letter2 = (char) ('A' + digit2);

			return "" + letter1 + letter2;
		} else {
			char letter = (char) ('A' + ind);

			return "" + letter;
		}
	}

	/**
	 * @return Returns the repeating.
	 */
	public boolean isRepeating() {
		return repeating;
	}

	/**
	 * @param repeating
	 *            The repeating to set.
	 */
	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
	}
}
