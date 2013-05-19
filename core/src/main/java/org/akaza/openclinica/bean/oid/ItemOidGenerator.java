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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class ItemOidGenerator extends OidGenerator {

	private final int argumentLength = 2;

	private DataSource dataSource;

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int getNextValue() {
		int result = 0;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled())
					logger.warn("Connection is closed: GenericDAO.select!");
				throw new SQLException();
			}
			ps = con.prepareStatement("SELECT nextval('item_oid_id_seq')");
			rs = ps.executeQuery();
			result = rs.next() ? rs.getInt(1) : 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	void verifyArgumentLength(String... keys) throws Exception {
		if (keys.length != argumentLength) {
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

		crfName = truncateToXChars(capitalize(stripNonAlphaNumeric(crfName)), 5);
		itemLabel = truncateToXChars(capitalize(stripNonAlphaNumeric(itemLabel)), 27);

		oid = oid + crfName + "_" + itemLabel;

		// If oid is made up of all special characters then
		if (oid.equals("I_") || oid.equals("I__")) {
			oid = randomizeOid("I_");
		}
		logger.info("OID : " + oid);
		return oid;
	}
}
