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

package org.akaza.openclinica.bean.oid;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Assumes we are getting the unique protocol id from a study, and truncating to eight chars.
 * 
 * @author thickerson
 * 
 */
public class StudyOidGenerator extends OidGenerator {

	private final int argumentLength = 1;

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
			ps = con.prepareStatement("SELECT nextval('study_oid_id_seq')");
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
		String oid = "S_";
		String uniqueProtocolID = keys[0];
		uniqueProtocolID = truncateTo8Chars(capitalize(stripNonAlphaNumeric(uniqueProtocolID)));

		if (uniqueProtocolID.length() == 0) {
			uniqueProtocolID = randomizeOid("");
		}
		oid = oid + uniqueProtocolID;
		return oid;
	}

}
