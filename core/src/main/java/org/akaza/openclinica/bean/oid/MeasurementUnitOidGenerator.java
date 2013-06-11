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
 * Maximum length of measurement_unit_oid is 40.
 */
public class MeasurementUnitOidGenerator extends OidGenerator {

	public static final String SEQ_NAME = "measurement_unit_oid_id_seq";

	private final int argumentLength = 1;

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
		String oid = this.truncateToXChars("MU_" + capitalize(stripNonAlphaNumeric(keys[0])), 35);

		// If oid is made up of all special characters then
		if (oid.equals("MU_")) {
			oid = randomizeOid("MU_");
		}
		logger.info("OID : " + oid);
		return oid;
	}

	@Override
	String stripNonAlphaNumeric(String input) {
		return input.trim().replaceAll("[^a-zA-Z_0-9]", "");
	}

	public String generateOidNoValidation(String... keys) throws Exception {
		verifyArgumentLength(keys);
		String oid = createOid(keys);
		return oid;
	}

}
