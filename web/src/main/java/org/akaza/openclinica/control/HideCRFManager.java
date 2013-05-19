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

import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;

/**
 * Created by IntelliJ IDEA. User: bruceperry Date: Feb 8, 2009 Time: 3:13:24 PM To change this template use File |
 * Settings | File Templates.
 */
public class HideCRFManager {

	private HideCRFManager() {
	}

	public void optionallyCheckHideCRFProp(DisplayStudyEventBean displayStudyEventBean) {

		EventDefinitionCRFBean tempEventCRFBean = new EventDefinitionCRFBean();

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getAllEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				tempEventCRFBean.setHidden(true);
			}
		}

		for (DisplayEventCRFBean deCRFBean : displayStudyEventBean.getDisplayEventCRFs()) {
			tempEventCRFBean = deCRFBean.getEventDefinitionCRF();
			if (tempEventCRFBean.isHideCrf()) {
				tempEventCRFBean.setHidden(true);
			}
		}

	}
}
