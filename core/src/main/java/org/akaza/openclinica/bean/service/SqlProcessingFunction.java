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

package org.akaza.openclinica.bean.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.core.util.ScriptRunner;

/**
 * Class to implement the datamart in SQL. by Tom Hickerson, 09/2010
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings("serial")
public class SqlProcessingFunction extends ProcessingFunction implements Serializable {

	private ExtractPropertyBean extractPropertyBean;
	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;
	private String databaseType;

	public SqlProcessingFunction(ExtractPropertyBean extractPropertyBean) {
		this.extractPropertyBean = extractPropertyBean;
		fileType = "sql";
	}

	/**
	 * The run() method will find the file name, runs the SQL on the assigned db, and returns a success/fail message.
	 * 
	 * This method assumes all variables are set in the XsltTransformJob, and that the database variables are correctly
	 * set in either extract.properties or datainfo.properties NOTE that if variables are set in extract, we do not try
	 * datainfo (it has to be correct somewhere)
	 * 
	 */
	public ProcessingResultType run() {
		Connection conn = null;
		ProcessingResultType resultError = null;
		Statement stmt = null;
		try {
			// load the proper database class below
			Properties props = new Properties();
			props.setProperty("user", databaseUsername);
			props.setProperty("password", databasePassword);
			conn = DriverManager.getConnection(databaseUrl, props);

			conn.setAutoCommit(true);
			File sqlFile = new File(getTransformFileName());

			ScriptRunner runner = new ScriptRunner(conn, true, false);
			runner.runScript(new BufferedReader(new FileReader(sqlFile)));

			if (conn != null) {
				conn.commit();
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultError = ProcessingResultType.FAIL;
			resultError.setUrl(""); // no url required
			resultError.setArchiveMessage("Failure thrown: " + e.getMessage());
			resultError.setDescription("Your job failed with the message of: " + e.getMessage());

			return resultError;

		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null) {
					conn.commit();
					conn.setAutoCommit(false);

					conn.close();
				}
				if (resultError != null)
					return resultError;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// set up the reply object
		ProcessingResultType result = ProcessingResultType.SUCCESS;
		result.setUrl(""); // no url required
		result.setArchiveMessage("Successfully run");
		result.setDescription("Your job ran successfully.");
		// replace with something from extract prop bean?
		return result;

	}

	public ExtractPropertyBean getExtractPropertyBean() {
		return extractPropertyBean;
	}

	public void setExtractPropertyBean(ExtractPropertyBean extractPropertyBean) {
		this.extractPropertyBean = extractPropertyBean;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
}
