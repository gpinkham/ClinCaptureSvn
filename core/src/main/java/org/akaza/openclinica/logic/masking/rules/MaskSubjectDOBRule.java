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
 * Created on Sep 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.akaza.openclinica.logic.masking.rules;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.logic.core.BusinessRule;

/**
 * @author thickerson
 * 
 * 
 */
public class MaskSubjectDOBRule implements BusinessRule {
	public boolean isPropertyTrue(String s) {
		if (s.equals(this.getClass().getName())) {
			return true;
		} else {
			return false;
		}
	}

	public EntityBean doAction(EntityBean sb) {
		// cast to a subject bean
		SubjectBean ssb = (SubjectBean) sb;
		ssb.setDateOfBirth(null);// effectively xx-xx-xxxx
		return sb;
	}

}
