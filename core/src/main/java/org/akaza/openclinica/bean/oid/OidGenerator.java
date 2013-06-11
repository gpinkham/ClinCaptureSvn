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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OID Generator solves the problems described below. We have Domain Objects that need to be assigned a specific OID. -
 * The OID is generated differently for every Domain Object - The OID keys depend on the Domain object ,So some domain
 * objects need two keys to make up an OID some need three ... - The number of Domain object needing an OID is small
 * with respect to the total amount of domain objects.
 * 
 * 
 * @author Krikor Krumlian
 * @see Strategy Pattern, Template Pattern
 */

public abstract class OidGenerator {

	public static final int MAX_OID_SUFFIX = 9999999;
	public static final int OID_SUFFIX_LENGTH = 7;
	public static final String ZERO = "0";

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((logger == null) ? 0 : logger.hashCode());
		result = prime * result + oidLength;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OidGenerator other = (OidGenerator) obj;
		if (logger == null) {
			if (other.logger != null)
				return false;
		} else if (!logger.equals(other.logger))
			return false;
		if (oidLength != other.oidLength)
			return false;
		return true;
	}

	private final int oidLength = 40;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public final String generateOid(String... keys) throws Exception {
		verifyArgumentLength(keys);
		String oid = createOid(keys);
		validate(oid);
		return oid;
	}

	public String randomizeOid(String input) {
		if (input == null || input.length() == 0)
			input = "";
		if (!input.endsWith("_"))
			input = input + "_";
		int intOid = getNextValue();
		if (intOid > MAX_OID_SUFFIX) {
			throw new RuntimeException("*** The current value in the [...]_OID_ID_SEQ more than " + MAX_OID_SUFFIX
					+ "!");
		}
		String endOid = "" + intOid;
		int forTo = OID_SUFFIX_LENGTH - endOid.length();
		for (int i = 1; i <= forTo; i++) {
			endOid = ZERO + endOid;
		}
		input = input + endOid;
		return input;
	}

	public int getNextValue() {
		int result = 0;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getDataSource().getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled())
					logger.warn("Connection is closed.");
				throw new SQLException();
			}
			ps = con.prepareStatement("SELECT nextval('" + getSequenceName() + "')");
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

	public abstract String getSequenceName();

	public abstract DataSource getDataSource();

	public abstract void setDataSource(DataSource dataSource);

	abstract void verifyArgumentLength(String... keys) throws Exception;

	abstract String createOid(String... keys);

	String stripNonAlphaNumeric(String input) {
		// Add capitalization too
		return input.trim().replaceAll("\\s+|\\W+", "");
	}

	String capitalize(String input) {
		return input.toUpperCase();
	}

	String truncateToXChars(String input, int x) {
		return input.length() > x ? input.substring(0, x) : input;
	}

	String truncateTo4Chars(String input) {
		return truncateToXChars(input, 4);
	}

	String truncateTo8Chars(String input) {
		return truncateToXChars(input, 8);
	}

	public boolean validate(String oid) throws Exception {
		Pattern pattern = Pattern.compile("^[A-Z_0-9]+$");
		Matcher matcher = pattern.matcher(oid);
		boolean isValid = matcher.matches();
		if (!isValid || oid.length() > oidLength || oid.length() <= 0) {
			throw new Exception();
		}
		return isValid;
	}

}
