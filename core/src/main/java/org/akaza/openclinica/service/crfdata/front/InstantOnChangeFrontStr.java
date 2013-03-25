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
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.service.crfdata.front;

import org.akaza.openclinica.domain.crfdata.InstantOnChangePairContainer;

import java.io.Serializable;

/**
 * <P>
 * Composited by sets of 3-element set in order : item group oid, item id and one string option. Three elements are
 * chained up by INNER FrontStrDelimiter, so do sets.
 * 
 */
// ywang (Aug., 2011)
public class InstantOnChangeFrontStr extends AbstractFrontStr implements Serializable {

	private static final long serialVersionUID = -1799222257393501943L;

	public InstantOnChangeFrontStr() {
		super();
		this.frontStrDelimiter = FrontStrDelimiter.INNER;
	}

	/**
	 * Precondition for InstantPairContainer: itemId > 0; option cannot be empty. igOid will be assigned "IG_" if not
	 * size > 0.
	 */
	public void chainUpFrontStr(InstantOnChangePairContainer instantPair) {
		if (this.frontStr.length() > 0) {
			frontStr.append(this.frontStrDelimiter.getCode());
		}
		frontStr.append(innerStr(instantPair.getDestItemGroupOid(), instantPair.getDestItemId(),
				instantPair.getOptionValue()));
	}

	private String innerStr(String igOid, Integer itemId, String option) {
		if (itemId == null || itemId <= 0 || option == null || option.length() <= 0) {
			return "";
		} else {
			StringBuffer buf = new StringBuffer(igOid == null || "".equals(igOid) ? "IG_" : igOid);
			buf.append(this.frontStrDelimiter.getCode());
			buf.append(itemId);
			buf.append(this.frontStrDelimiter.getCode());
			buf.append(option);
			return buf.toString();
		}
	}
}
