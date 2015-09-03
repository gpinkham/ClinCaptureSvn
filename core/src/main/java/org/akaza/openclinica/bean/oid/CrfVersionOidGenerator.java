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
 * @author Krikor Krumlian
 * 
 */
public class CrfVersionOidGenerator extends OidGenerator {

	public static final String SEQ_NAME = "crf_version_oid_id_seq";
	public static final int INT_10 = 10;
	public static final int INT_25 = 25;

	private final int argumentLength = 2;

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
		if (keys.length != argumentLength) {
			throw new Exception();
		}
	}

	@Override
	String createOid(String... keys) {
		logger.info("In Create OID");
		String oid;
		String crfOid = keys[0];
		String crfVersion = keys[1];

		crfVersion = truncateToXChars(capitalize(stripNonAlphaNumeric(crfVersion)), INT_10);
		crfOid = "F_" + truncateToXChars(capitalize(stripNonAlphaNumeric(crfOid)), INT_25);

		logger.info(crfOid);
		logger.info(crfVersion);
		oid = crfOid + "_" + crfVersion;

		if (crfVersion.length() == 0) {
			oid = randomizeOid(oid);
		}
		return oid;
	}
}
