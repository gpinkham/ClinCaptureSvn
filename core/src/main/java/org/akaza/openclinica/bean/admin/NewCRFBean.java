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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright� 2003-2005 Akaza Research
 */

package org.akaza.openclinica.bean.admin;

import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

/**
 * The workhorse for instrument generation, the NewInstrumentBean holds some information, but also updates and inserts
 * rows in the database, using the insertToDB method. </p>
 * <P>
 * Currently, we do not make use of dbInstructions yet.
 * 
 * @author thickerson
 * @version 1.1 modified by jxu
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class NewCRFBean extends Object implements java.io.Serializable {

	private DataSource ds;
	private DAODigester digester;

	private ArrayList queries = new ArrayList();
	private ArrayList errors = new ArrayList();
	private ArrayList deleteQueries = new ArrayList();
	private ArrayList deleteErrors = new ArrayList();
	private String htmlTable = null;
	private HashMap dbInstructions = new HashMap();
	private HashMap itemNames = new HashMap();
	private HashMap itemQueries = new HashMap();// queries to insert items
	private HashMap crfVersions = new HashMap();
	private HashMap items = new HashMap();// construct itemBeans and save them
	private HashMap backupItemQueries = new HashMap();
	private int crfId = 0;
	private HashMap itemGroupNames = new HashMap();
	private String versionName = "";

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public NewCRFBean(DataSource ds, int crfId) {
		this.ds = ds;
		String digesterName = SQLFactory.getInstance().DAO_CRF;
		digester = SQLFactory.getInstance().getDigester(digesterName);
		try {
			this.setItemNames(this.listItemNames(crfId));// crf id used here
			this.setItemGroupNames(this.listGroupNames(crfId));
			this.setCrfVersions(this.listVersionNames());
		} catch (Exception pe) {
			pe.printStackTrace();
			logger.info("hit an exception in creating new crf bean;" + " empty item name list exists");
			this.setItemNames(new HashMap());
			this.setCrfVersions(new HashMap());
		}
	}

	public void setQueries(ArrayList q) {
		this.queries = q;
	}

	public void setDeleteQueries(ArrayList q) {
		this.deleteQueries = q;
	}

	public void setBackupItemQueries(HashMap q) {
		this.backupItemQueries = q;
	}

	public void setCrfId(int x) {
		this.crfId = x;
	}

	public int getCrfId() {
		return crfId;
	}

	/**
	 * @return the itemGroupNames
	 */
	public HashMap getItemGroupNames() {
		return itemGroupNames;
	}

	/**
	 * @param itemGroupNames
	 *            the itemGroupNames to set
	 */
	public void setItemGroupNames(HashMap itemGroupNames) {
		this.itemGroupNames = itemGroupNames;
	}

	public void setErrors(ArrayList e) {
		this.errors = e;
	}

	public void setDeleteErrors(ArrayList e) {
		this.deleteErrors = e;
	}

	public void setHtmlTable(String h) {
		this.htmlTable = h;
	}

	public void setDbInstructions(HashMap d) {
		this.dbInstructions = d;
	}

	public void setCrfVersions(HashMap hm) {
		this.crfVersions = hm;
	}

	public ArrayList getQueries() {
		return queries;
	}

	public ArrayList getDeleteQueries() {
		return deleteQueries;
	}

	public ArrayList getErrors() {
		return errors;
	}

	public ArrayList getDeleteErrors() {
		return deleteErrors;
	}

	public String getHtmlTable() {
		return htmlTable;
	}

	public HashMap getDbInstructions() {
		return dbInstructions;
	}

	public void setItemNames(HashMap hm) {
		this.itemNames = hm;
	}

	public HashMap getItemNames() {
		return itemNames;
	}

	public void setItems(HashMap hm) {
		this.items = hm;
	}

	public HashMap getItems() {
		return items;
	}

	public void setItemQueries(HashMap hm) {
		this.itemQueries = hm;
	}

	public HashMap getItemQueries() {
		return itemQueries;
	}

	public HashMap getBackupItemQueries() {
		return backupItemQueries;
	}

	public HashMap getCrfVersions() {
		return crfVersions;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String vn) {
		this.versionName = vn;
	}

	public HashMap listVersionNames() throws OpenClinicaException {
		/*
		 * serves up a list of all crf names and versions, necessary for checking for duplicates. Added by tbh, 7-25-03
		 */
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap returnMe = new HashMap();
		String sql = digester.getQuery("findVersionNamesForCRF");

		try {
			con = ds.getConnection();
			if (con.isClosed()) {
				throw new OpenClinicaException("Con is closed: NewCRFBean", "");
			}
			ps = con.prepareStatement(sql);
			ps.setInt(1, crfId);
			rs = ps.executeQuery();
			String key = null;
			String value = null;
			while (rs.next()) {
				key = rs.getString(1);
				value = rs.getString(2);
				returnMe.put(key, value);
			}
			return returnMe;
		} catch (SQLException se) {
			se.printStackTrace();
			throw new OpenClinicaException("SQLException: " + se.getMessage(), "");
		} catch (OpenClinicaException pe) {
			pe.printStackTrace();
			throw new OpenClinicaException("OpenClinicaException: " + pe.getMessage(), "");
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
				throw new OpenClinicaException(e.getMessage(), "1");
			}

		}

	}

	public HashMap listItemNames(int crfId) throws OpenClinicaException {
		/*
		 * serves up a list of all item names in a CRF, necessary for checking for duplicates and creating new items.
		 */
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap returnMe = new HashMap();
		String sql = digester.getQuery("findItemNamesByCRF");
		logger.info("crf id: *******" + crfId);
		try {
			con = ds.getConnection();
			if (con.isClosed()) {
				throw new OpenClinicaException("Con is closed: NewCRFBean", "");
			}
			ps = con.prepareStatement(sql);
			ps.setInt(1, crfId);
			rs = ps.executeQuery();
			String key = null;
			String value = null;
			while (rs.next()) {
				key = rs.getString(1);
				value = "1";
				returnMe.put(key, value);
			}
			return returnMe;
		} catch (SQLException se) {
			se.printStackTrace();
			throw new OpenClinicaException("SQLException: " + se.getMessage(), "");
		} catch (OpenClinicaException pe) {
			pe.printStackTrace();
			throw new OpenClinicaException("OpenClinicaException: " + pe.getMessage(), "");
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
				throw new OpenClinicaException(e.getMessage(), "1");
			}

		}

	}

	public HashMap listGroupNames(int crfId) throws OpenClinicaException {
		/*
		 * serves up a list of all item group names in a CRF, necessary for checking for duplicates and creating new
		 * items.
		 */
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap returnMe = new HashMap();
		String sql = digester.getQuery("findItemGroupNamesByCRF");
		try {
			con = ds.getConnection();
			if (con.isClosed()) {
				throw new OpenClinicaException("Con is closed: NewCRFBean", "");
			}
			ps = con.prepareStatement(sql);
			ps.setInt(1, crfId);
			rs = ps.executeQuery();
			String key = null;
			String value = null;
			while (rs.next()) {
				key = rs.getString(1);
				value = "1";
				returnMe.put(key, value);
			}
			return returnMe;
		} catch (SQLException se) {
			se.printStackTrace();
			throw new OpenClinicaException("SQLException: " + se.getMessage(), "");
		} catch (OpenClinicaException pe) {
			pe.printStackTrace();
			throw new OpenClinicaException("OpenClinicaException: " + pe.getMessage(), "");
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
				throw new OpenClinicaException(e.getMessage(), "1");
			}

		}

	}

	public void insertToDB() throws OpenClinicaException {
		/*
		 * insertToDB() will implement rollback functionality, throwing an error will write the error to the setErrors
		 * and redirect to the error page, rather than to the success page, tbh, 6-6-03
		 */
		Statement s = null;
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList error = new ArrayList();
		int count = 0;
		try {

			con = ds.getConnection();
			if (con.isClosed()) {
				String msg = "The connection to the database is not open.";
				error.add(msg);
				throw new OpenClinicaException("newCRFBean, insertToDB, connection not open", "1");
			}
			
			// commenting this out temporarily so that mistakes are not made,
			con.setAutoCommit(false);
			Set mySet = itemQueries.entrySet();
			logger.info("---start of item query generation here---");
			// System.out.println("start query generation");
			for (Iterator itvl = mySet.iterator(); itvl.hasNext();) {
				Map.Entry ment = (Map.Entry) itvl.next();
				String pQuery = (String) ment.getValue();
				s = con.createStatement();
				s.executeUpdate(pQuery);
				s.close();
				// this might throw off the 'error' count, who can say?
				// of course, the queries are simple enough that maybe we'll
				// never throw
				// an error. Never say never though...
			}
			logger.info("---pause in query generation, items---");

			// Iterator it = queries.iterator();
			// using iterator gets us out of order, and throws everything off.
			// try a for loop instead
			int last = queries.size();
			for (int th = 0; th < last; th++) {
				String query = (String) queries.get(th);// it.next();
				count = th;
				s = con.createStatement();
				s.executeUpdate(query);
				s.close();
				error.add(query);
			}

			// the below lines are temporarily commented out for instrument
			// upload, tbh 8-13
			con.commit();
			logger.info("---end of query generation, all queries committed---");
			con.setAutoCommit(true);
			logger.info("---end of query generation, autocommit set to true---");
			// at this point we check to see if there is a active version, if
			// not, set THIS
			// to be the active version
			if (crfId != 0) {
				String sql = digester.getQuery("findDefaultVersion");
				logger.info("findDefaultVersion [" + sql + "]");
				ps = con.prepareStatement(sql);
				ps.setInt(1, crfId);
				rs = ps.executeQuery();
				if (rs.next()) {
					if (rs.wasNull()) {

						String sql2 = digester.getQuery("updateDefaultVersion");
						ps = con.prepareStatement(sql2);
						ps.setInt(1, crfId);
						ps.setInt(2, crfId);
						if (ps.executeUpdate() != 1) {
							throw new OpenClinicaException(
									"error, updated more than one row, smart assigner part of insertToDB, NewCRFBean",
									"");
						}
					}
				}
				rs.close();
				ps.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
			try {
				con.rollback();
				logger.info("Error detected, rollback " + se.getMessage());
				String msg2 = "The following error was returned from the database: " + se.getMessage()
						+ " using the following query: " + queries.get(count);
				error.add(msg2);
				this.setErrors(error);
				con.setAutoCommit(true);
				throw new OpenClinicaException("", "");
			} catch (SQLException seq) {
				seq.printStackTrace();
				logger.info("Error within rollback " + seq.getMessage());
				String msg2 = "The following error was returned from the database: " + seq.getMessage();
				error.add(msg2);
				this.setErrors(error);
				throw new OpenClinicaException("", "");
			}
		} catch (OpenClinicaException pe) {
			pe.printStackTrace();
			try {
				con.rollback();
				logger.info("OpenClinica Error detected, rollback " + pe.getMessage());
				String msg2 = "The following error was returned from the application: " + pe.getMessage();
				error.add(msg2);
				this.setErrors(error);
				con.setAutoCommit(true);
				throw new OpenClinicaException("", "");
			} catch (SQLException seq) {
				seq.printStackTrace();
				logger.info("OpenClinica Error within rollback " + seq.getMessage());
				String msg2 = "The following error was returned from the application: " + seq.getMessage();
				error.add(msg2);
				this.setErrors(error);
				throw new OpenClinicaException("", "");
			}

		} finally {
			try {
				if (con != null)
					con.close();
				if (s != null)
					s.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new OpenClinicaException(e.getMessage(), "1");
			}

		}
	}

	public void deleteFromDB() throws OpenClinicaException {
		Statement s = null;
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ArrayList error = new ArrayList();
		int count = 0;
		try {

			con = ds.getConnection();
			if (con.isClosed()) {
				String msg = "The connection to the database is not open.";
				error.add(msg);
				throw new OpenClinicaException("newCRFBean, deleteFromDB, connection not open", "1");
			}
			// commenting this out temporarily so that mistakes are not made
			con.setAutoCommit(false);

			// delete version and related info
			int last = deleteQueries.size();
			for (int th = 0; th < last; th++) {
				String query = (String) deleteQueries.get(th);// it.next();
				count = th;
				s = con.createStatement();
				s.executeUpdate(query);
				s.close();
				error.add(query);
			}

			con.commit();
			logger.info("---end of delete query generation, all queries committed---");
			con.setAutoCommit(true);
			logger.info("---end of delete query generation, autocommit set to true---");

		} catch (SQLException se) {
			se.printStackTrace();
			try {
				con.rollback();
				logger.info("Error detected, rollback " + se.getMessage());
				String msg2 = "The following error was returned from the database: " + se.getMessage()
						+ " using the following query: " + deleteQueries.get(count);
				error.add(msg2);
				this.setDeleteErrors(error);
				con.setAutoCommit(true);
				throw new OpenClinicaException("", "");
			} catch (SQLException seq) {
				seq.printStackTrace();
				logger.info("Error within rollback " + seq.getMessage());
				String msg2 = "The following error was returned from the database: " + seq.getMessage();
				error.add(msg2);
				this.setDeleteErrors(error);
				throw new OpenClinicaException("", "");
			}
		} catch (OpenClinicaException pe) {
			pe.printStackTrace();
			try {
				con.rollback();
				logger.info("OpenClinica Error detected, rollback " + pe.getMessage());
				String msg2 = "The following error was returned from the application: " + pe.getMessage();
				error.add(msg2);
				this.setDeleteErrors(error);
				con.setAutoCommit(true);
				throw new OpenClinicaException("", "");
			} catch (SQLException seq) {
				seq.printStackTrace();
				logger.info("OpenClinica Error within rollback " + seq.getMessage());
				String msg2 = "The following error was returned from the application: " + seq.getMessage();
				error.add(msg2);
				this.setDeleteErrors(error);
				throw new OpenClinicaException("", "");
			}

		} finally {
			try {
				if (con != null)
					con.close();
				if (s != null)
					s.close();
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new OpenClinicaException(e.getMessage(), "1");
			}

		}
	}
}
