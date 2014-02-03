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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import oracle.jdbc.pool.OracleDataSource;

/**
 * Utility which handles connection and login, as prompted by OpenClinica control servlets. Updated August 2004 to
 * better handle connection pooling. Updated again in August 2004 to support SQL Statements in XML. Will require a
 * change to all control servlets; new SessionManagers will have to be stored in session, since we don't want to take in
 * all this information every time a user clicks on a new servlet.
 * 
 * @author Tom Hickerson
 * @author Jun Xu
 */
public class SessionManager {
	private UserAccountBean ub;

	private OracleDataSource ods;

	private DataSource ds;

	final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private String dbType;
	private UserAccountDAO uDAO = null;

	// TODO: this is a hack needs to be refactord
	private static DataSource staticDataSource;

	/**
	 * Constructor of SessionManager
	 * 
	 * @param userFromSession
	 * @param userName
	 * @throws SQLException
	 */
	public SessionManager(UserAccountBean userFromSession, String userName) throws SQLException {
		setupDataSource();
		setupUser(userFromSession, userName);
	}

	/**
	 * Constructor of SessionManager
	 * 
	 * @param userFromSession
	 * @param userName
	 * @throws SQLException
	 */
	public SessionManager(UserAccountBean userFromSession, String userName, ApplicationContext applicationContext)
			throws SQLException {
		this.ds = (DataSource) applicationContext.getBean("dataSource");
		staticDataSource = ds;
		setupUser(userFromSession, userName);
	}

	public SessionManager(ApplicationContext applicationContext) throws SQLException {
		this.ds = (DataSource) applicationContext.getBean("dataSource");
		staticDataSource = ds;

	}

	public void setupUser(UserAccountBean userFromSession, String userName) {
		if (userFromSession == null || StringUtil.isBlank(userFromSession.getName())) {
			uDAO = new UserAccountDAO(ds);
			if (userName == null) {
				userName = "";
			}
			ub = (UserAccountBean) uDAO.findByUserName(userName);
			logger.info("User  : {} , email address : {} Logged In ", ub.getName(), ub.getEmail());

		} else {
			ub = userFromSession;
		}
	}

	public void setupDataSource() {
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			dbType = CoreResources.getField("dbType");
			if ("oracle".equals(dbType)) {
				logger.debug("looking up oracle...");
				ds = (DataSource) env.lookup("SQLOracle");
			} else if ("postgres".equals(dbType)) {
				ds = (DataSource) env.lookup("SQLPostgres");
			}

		} catch (NamingException ne) {
			ne.printStackTrace();
			logger.warn("This is :" + ne.getMessage() + " when we tried to get the connection");
		}
	}

	public SessionManager() {
		setupDataSource();
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public UserAccountBean getUserBean() {
		return ub;
	}

	public void setConnection(Connection con) {
	}

	public void setUserBean(UserAccountBean user) {
		this.ub = user;
	}

	public DataSource getDataSource() {
		return ds;
	}

	public OracleDataSource getOracleDataSource() {
		return ods;
	}

	public static DataSource getStaticDataSource() {
		return staticDataSource;
	}

}
