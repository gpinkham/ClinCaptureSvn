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
 * For details see: http://www.openclinica.org/license copyright 
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (Aug, 2010)
 * 
 */

public class RelatedInformationBean {
	private String MEDLINEIdentifier;
	private String ResultsReference;
	private String URLReference;
	private String URLDescription;

	public String getMEDLINEIdentifier() {
		return MEDLINEIdentifier;
	}

	public void setMEDLINEIdentifier(String mEDLINEIdentifier) {
		MEDLINEIdentifier = mEDLINEIdentifier;
	}

	public String getResultsReference() {
		return ResultsReference;
	}

	public void setResultsReference(String resultsReference) {
		ResultsReference = resultsReference;
	}

	public String getURLReference() {
		return URLReference;
	}

	public void setURLReference(String uRLReference) {
		URLReference = uRLReference;
	}

	public String getURLDescription() {
		return URLDescription;
	}

	public void setURLDescription(String uRLDescription) {
		URLDescription = uRLDescription;
	}
}
