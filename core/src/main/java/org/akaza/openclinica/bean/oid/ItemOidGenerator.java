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

package org.akaza.openclinica.bean.oid;

import javax.sql.DataSource;

/**
 * Maximum length of item.oc_oid is 40.
 */
public class ItemOidGenerator extends OidGenerator {

	public static final String SEQ_NAME = "item_oid_id_seq";

	public static final int ARGUMENT_LENGTH = 2;
	public static final int CRF_NAME_CUT_TO = 5;
	public static final int ITEM_LABEL_CUT_TO = 24;

	private DataSource dataSource;

	@Override
	public String getSequenceName() {
		return SEQ_NAME;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	void verifyArgumentLength(String... keys) throws Exception {
		if (keys.length != ARGUMENT_LENGTH) {
			throw new Exception();
		}
	}

	@Override
	String createOid(String... keys) {
		String oid = "I_";
		String crfName = keys[0];
		String itemLabel = keys[1];

		logger.info(crfName);
		logger.info(itemLabel);

		crfName = truncateToXChars(capitalize(stripNonAlphaNumeric(crfName)), CRF_NAME_CUT_TO);
		itemLabel = truncateToXChars(capitalize(stripNonAlphaNumeric(itemLabel)), ITEM_LABEL_CUT_TO);

		oid = oid + crfName + "_" + itemLabel;

		// If oid is made up of all special characters then
		if (oid.equals("I_") || oid.equals("I__")) {
			oid = randomizeOid("I_");
		}
		logger.info("OID : " + oid);
		return oid;
	}
}
