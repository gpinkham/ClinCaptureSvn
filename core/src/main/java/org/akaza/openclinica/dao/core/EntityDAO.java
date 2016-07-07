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
package org.akaza.openclinica.dao.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.ApplicationConstants;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.dao.cache.EhCacheWrapper;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p/>
 * EntityDAO.java, the generic data access object class for the database layer, by Tom Hickerson, 09/24/2004
 * <p/>
 * A signalling system was added on 7 Dec 04 to indicate the success or failure of a query. A query is considered
 * successful iff a SQLException was not thrown in the process of executing the query.
 * <p/>
 * The system can be used by outside classes / subclasses as follows: - Immediately after calling select or execute,
 * isQuerySuccessful() is <code>true</code> if the query was successful, <code>false</code> otherwise. - If
 * isQuerySuccessful returns <code>false</code> getFailureDetails() returns the SQLException which was thrown.
 * <p/>
 * In order to maintain the system, the following invariants must be maintained by developers: 1. Every method executing
 * a query must call clearSignals() as the first statement. 2. Every method executing a query must call either
 * signalSuccess or signalFailure before returning.
 * <p/>
 * At the time of writing, the only methods which execute queries are select and execute.
 * 
 * @author thickerson
 * @param <V>
 * @param <K>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class EntityDAO<K, V extends ArrayList> implements DAOInterface {

	private static final int DATASET_ITEM_STATUS_INVALID_ID = 0;

	private static final int DATASET_ITEM_STATUS_COMPLETED_ID = 1;

	private static final int DATASET_ITEM_STATUS_NON_COMPLETED_ID = 2;

	private static final int DATASET_ITEM_STATUS_COMPLETED_AND_NON_COMPLETED_ID = 3;

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	
	private DataSource dataSource;

	private Connection connection;

	protected String digesterName;

	protected DAODigester digester;

	private HashMap setTypes = new HashMap();

	private EhCacheWrapper cache;

	private boolean querySuccessful;

	private SQLException failureDetails;

	/**
	 * Should the name of a query which refers to a SQL command of the following form:
	 * <code>SELECT currval('sequence') AS key</code> The column name "key" is required, as getCurrentPK() relies on it.
	 */
	protected String getCurrentPKName;

	/**
	 * Should the name of a query which refers to a SQL command of the following form:
	 * <code>SELECT nextval('sequence') AS key</code> The column name "key" is required, as getNextPK() relies on it.
	 */
	protected String getNextPKName;

	private int latestPK;

	protected Locale locale = ResourceBundleProvider.getLocale();

	protected String oc_df_string = "";

	protected String local_df_string = "";

	protected String local_yf_string = "";

	protected String local_ymf_string = "";

	/**
	 * Constructor.
	 *
	 * @param dataSource data source object
	 */
	public EntityDAO(DataSource dataSource) {
		this.dataSource = dataSource;
		setDigesterName();
		digester = SQLFactory.getInstance().getDigester(digesterName);
		initializeI18nStrings();
		setCache(SQLFactory.getInstance().getEhCacheWrapper());
	}

	/**
	 * Constructor.
	 *
	 * @param dataSource data source object
	 * @param connection data base connection object
	 */
	public EntityDAO(DataSource dataSource, Connection connection) {
		this.dataSource = dataSource;
		this.connection = connection;
		setDigesterName();
		digester = SQLFactory.getInstance().getDigester(digesterName);
		initializeI18nStrings();
		setCache(SQLFactory.getInstance().getEhCacheWrapper());
	}


	public DataSource getDataSource() {
		return dataSource;
	}

	public Connection getConnection() {
		return connection;
	}

	protected abstract void setDigesterName();

	public DAODigester getDigester() {
		return digester;
	}

	public void setCache(final EhCacheWrapper cache) {
		this.cache = cache;
	}

	public EhCacheWrapper getCache() {
		return cache;
	}

	public SQLException getFailureDetails() {
		return failureDetails;
	}

	public boolean isQuerySuccessful() {
		return querySuccessful;
	}

	public String getGetCurrentPKName() {
		return getCurrentPKName;
	}

	public void setGetCurrentPKName(String getCurrentPKName) {
		this.getCurrentPKName = getCurrentPKName;
	}

	public String getGetNextPKName() {
		return getNextPKName;
	}

	public void setGetNextPKName(String getNextPKName) {
		this.getNextPKName = getNextPKName;
	}

	public int getLatestPK() {
		return latestPK;
	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * Expects to enter the type of object to retrieve from the database.
	 * 
	 * @param num
	 *            the order the column should be extracted from the database
	 * @param type
	 *            the number that is equal to TypeNames
	 */
	public void setTypeExpected(int num, int type) {
		setTypes.put(num, type);
	}

	/**
	 * Returns the type of object to retrieve from the database.
	 *
	 * @param num
	 *            the order the column should be extracted from the database
	 * @return a proper type id from TypeNames
	 */
	public Integer getTypeExpected(int num) {
		return (Integer) setTypes.get(num);
	}

	/**
	 * Clears expected types map.
	 */
	public void unsetTypeExpected() {
		setTypes = new HashMap();
	}

	/**
	 * select, a static query interface to the database, returning an array of hashmaps that contain key->object pairs.
	 * <P>
	 * This is the first operation created for the database, so therefore it is the simplest; cull information from the
	 * database but not specify any parameters.
	 * 
	 * @param query
	 *            a static query of the database.
	 * @return ArrayList of HashMaps carrying the database values.
	 */
	public ArrayList select(String query) {
		clearSignals();

		ArrayList results = new ArrayList();
		ResultSet rs = null;
		connection = null;
		Statement ps = null;
		logger.trace("query???" + query);
		try {
			connection = dataSource.getConnection();
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.select!");	
				}
				throw new SQLException();
			}
			ps = connection.createStatement();
			rs = ps.executeQuery(query);
			if (logger.isInfoEnabled()) {
				logger.trace("Executing static query, EntityDAO.select: " + query);
			}
			signalSuccess();
			results = this.processResultRows(rs);

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exeception while executing static query, EntityDAO.select: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return results;
	}

	/**
	 * Select method.
	 *
	 * @param query
	 *            String
	 * @param variables
	 *            HashMap
	 * @return ArrayList
	 */
	public ArrayList<V> select(String query, HashMap variables) {
		return select(query, variables, null);
	}

	/**
	 * Select method.
	 *
	 * @param query
	 *            String
	 * @param variables
	 *            HashMap
	 * @param con
	 *            Connection
	 * @return ArrayList
	 */
	public ArrayList<V> select(String query, HashMap variables, Connection con) {

		clearSignals();
		ArrayList results = new ArrayList();
		ResultSet rs = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		PreparedStatement ps = null;
		boolean isTransactional = false;
		if (con != null) {
			isTransactional = true;
		}

		try {
			if (!isTransactional) {
				con = dataSource.getConnection();
			}
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.select!");
				}
				throw new SQLException();
			}

			ps = con.prepareStatement(query);
			ps = psf.generate(ps);
			rs = ps.executeQuery();
			results = this.processResultRows(rs);

			if (logger.isInfoEnabled()) {
				logger.trace("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic query, EntityDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			if (!isTransactional) {
				closeIfNecessary(con, rs, ps);
			} else {
				closeIfNecessary(rs, ps);
			}
		}
		return results;
	}

	/**
	 * added by clinovo to be backwards-compateble with our code. the real futz: OC 'upgraded' to put hashmaps
	 * everywhere in their selects, or put their own select in the daos (example: ItemGroupMetadataDAO) for caching
	 * purposes. We are keeping the more generic Map container so as to not have to break everything.
	 * 
	 * @param query
	 *            String
	 * @param variables
	 *            Map
	 * @return ArrayList<V>
	 */
	public ArrayList<V> select(String query, Map variables) {
		
		clearSignals();
		ArrayList results = new ArrayList();
		ResultSet rs = null;
		connection = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		PreparedStatement ps = null;

		try {
			connection = dataSource.getConnection();
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.select!");	
				}
				throw new SQLException();
			}

			ps = connection.prepareStatement(query);
			ps = psf.generate(ps);
			rs = ps.executeQuery();
			results = this.processResultRows(rs);

			if (logger.isInfoEnabled()) {
				logger.trace("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic query, EntityDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return results;
	}

	/**
	 * Select method.
	 *
	 * @param query SQL query
	 * @param con   data base connection
	 * @return list of entities, fetched by query
	 */
	public ArrayList select(String query, Connection con) {

		clearSignals();
		ArrayList results = new ArrayList();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.select!");	
				}
				throw new SQLException();
			}

			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			if (logger.isInfoEnabled()) {
				logger.trace("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();
			results = this.processResultRows(rs);
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exeception while executing dynamic query, EntityDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(rs, ps);
		}
		return results;
	}

	/**
	 * Select method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 * @return list of entities, fetched by query
	 */
	protected ArrayList<V> selectByCache(String query, HashMap variables) {

		clearSignals();
		ArrayList results = new ArrayList();
		K key;
		ResultSet rs = null;
		connection = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		PreparedStatement ps = null;

		try {
			connection = dataSource.getConnection();
			if (connection.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.select!");
				}
				throw new SQLException();
			}

			ps = connection.prepareStatement(query);
			ps = psf.generate(ps);
			key = (K) ps.toString();
			results = (V) cache.get(key);
			if (results == null) {
				rs = ps.executeQuery();
				results = this.processResultRows(rs);
				if (results != null) {
					cache.put(key, results);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.trace("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic query, EntityDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(connection, rs, ps);
		}
		return results;
	}

	/**
	 * Execute method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 */
	public void execute(String query, HashMap variables) {
		connection = null;
		execute(query, variables, null, false);
	}

	/**
	 * Execute method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 * @param con       data base connection
	 */
	public void execute(String query, HashMap variables, Connection con) {
		execute(query, variables, con, false);
	}

	/**
	 * Execute method.
	 *
	 * @param query                SQL query
	 * @param variables            parameters for query
	 * @param con                  data base connection
	 * @param useCallableStatement defines if callable statement must be used
	 */
	public void execute(String query, HashMap variables, Connection con, boolean useCallableStatement) {

		clearSignals();
		boolean isTransactional = false;
		if (con != null) {
			isTransactional = true;
		}

		PreparedStatement ps = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		try {
			if (!isTransactional) {
				con = dataSource.getConnection();
			}
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.execute!");
				}
				throw new SQLException();
			}
			ps = useCallableStatement ? con.prepareCall(query) : con.prepareStatement(query);
			ps = psf.generate(ps);
			if (ps.executeUpdate() < 0) {
				logger.warn("Problem with executing dynamic query, EntityDAO: " + query);
				throw new SQLException();
			} else {
				signalSuccess();
				logger.debug("Executing dynamic query, EntityDAO: " + query);
			}
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic statement, EntityDAO.execute: " + query + ": "
						+ sqle.getMessage());
			}
			if (logger.isErrorEnabled()) {
				logger.error(sqle.getMessage(), sqle);
			}
		} finally {
			if (!isTransactional) {
				this.closeIfNecessary(con, ps);
			} else {
				closePreparedStatement(ps);
			}
		}
	}

	/**
	 * Execute method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 * @param nullVars  defines which query parameters must be set to null
	 */
	public void execute(String query, HashMap variables, HashMap nullVars) {
		connection = null;
		execute(query, variables, nullVars, null);
	}

	/**
	 * Execute method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 * @param nullVars  defines which query parameters must be set to null
	 * @param con       data base connection
	 */
	public void execute(String query, HashMap variables, HashMap nullVars, Connection con) {

		clearSignals();
		boolean isTransactional = false;
		if (con != null) {
			isTransactional = true;
		}

		PreparedStatement ps = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables, nullVars);
		try {
			if (!isTransactional) {
				con = dataSource.getConnection();
			}
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.execute!");
				}
				throw new SQLException();
			}
			ps = con.prepareStatement(query);
			ps = psf.generate(ps);
			if (ps.executeUpdate() < 0) {
				logger.warn("Problem with executing dynamic query, EntityDAO: " + query);
				throw new SQLException();
			} else {
				signalSuccess();
				logger.debug("Executing dynamic query, EntityDAO: " + query);
			}
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exeception while executing dynamic statement, EntityDAO.execute: " + query + ": "
						+ sqle.getMessage());
			}

			if (logger.isErrorEnabled()) {
				logger.error(sqle.getMessage(), sqle);
			}
		} finally {
			if (!isTransactional) {
				this.closeIfNecessary(con, ps);
			} else {
				closePreparedStatement(ps);
			}
		}
	}

	/**
	 * This method inserts one row for an entity table and gets latestPK of this row.
	 *
	 * @param query
	 *            String
	 * @param variables
	 *            HashMap
	 *
	 */
	public void executeWithPK(String query, HashMap variables) {
		connection = null;
		executeWithPK(query, variables, new HashMap(), null);
	}

	/**
	 * This method inserts one row for an entity table and gets latestPK of this row.
	 * 
	 * @param query
	 *            String
	 * @param variables
	 *            HashMap
	 * @param nullVars
	 *            HashMap
	 * 
	 */
	public void executeWithPK(String query, HashMap variables, HashMap nullVars) {
		connection = null;
		executeWithPK(query, variables, nullVars, null);
	}

	/**
	 * Execute method.
	 *
	 * @param query     SQL query
	 * @param variables parameters for query
	 * @param nullVars  defines which query parameters must be set to null
	 * @param con       data base connection
	 */
	public void executeWithPK(String query, HashMap variables, HashMap nullVars, Connection con) {

		clearSignals();
		boolean isTransactional = false;
		if (con != null) {
			isTransactional = true;
		}

		PreparedStatement ps = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables, nullVars);
		try {
			if (!isTransactional) {
				con = dataSource.getConnection();
			}
			if (con.isClosed()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Connection is closed: EntityDAO.execute!");
				}
				throw new SQLException();
			}
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps = psf.generate(ps);
			if (ps.executeUpdate() != 1) {
				logger.warn("Problem with executing dynamic query, EntityDAO: " + query);
				throw new SQLException();

			} else {
				logger.trace("Executing dynamic query, EntityDAO: " + query);
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (generatedKeys.next()) {
					latestPK = generatedKeys.getInt(1);
					signalSuccess();
					return;
				}
				if (getCurrentPKName == null) {
					this.latestPK = 0;
				}
				this.unsetTypeExpected();
				this.setTypeExpected(1, TypeNames.INT);
				ArrayList al = select(digester.getQuery(getCurrentPKName), con);
				if (al.size() > 0) {
					HashMap h = (HashMap) al.get(0);
					this.latestPK = (Integer) h.get("key");
				}
			}
		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic statement, EntityDAO.execute: " + query + ": "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
			if (logger.isErrorEnabled()) {
				logger.error(sqle.getMessage(), sqle);
			}
		} finally {
			if (!isTransactional) {
				this.closeIfNecessary(con, ps);
			} else {
				closePreparedStatement(ps);
			}
		}
	}

	/**
	 * Reads entity properties from a ResultSet object, based on expected types map.
	 *
	 * @param rs ResultSet object
	 * @return list of maps. Each map contains set of properties of specific entity, fetched from the data base
	 */
	protected ArrayList processResultRows(ResultSet rs) {

		ArrayList al = new ArrayList();
		HashMap hm;
		try {
			while (rs.next()) {
				hm = new HashMap();
				ResultSetMetaData rsmd = rs.getMetaData();

				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String column = rsmd.getColumnName(i).toLowerCase();
					Integer type = getTypeExpected(i);
					if (null != type) {
						switch (type) {
						case TypeNames.BINARY_STREAM:
							hm.put(column, rs.getBinaryStream(i));
							break;
						case TypeNames.DATE:
							hm.put(column, rs.getDate(i));
							break;
						case TypeNames.TIMESTAMP:
							hm.put(column, rs.getTimestamp(i));
							break;
						case TypeNames.DOUBLE:
							hm.put(column, rs.getDouble(i));
							if (rs.wasNull()) {
								hm.put(column, (double) 0);
							}
							break;
						case TypeNames.BOOL:
							if (CoreResources.getDBType().equals("oracle")) {
								hm.put(column, rs.getString(i).equals("1"));
								if (rs.wasNull()) {
									if (column.equalsIgnoreCase("start_time_flag")
											|| column.equalsIgnoreCase("end_time_flag")) {
										hm.put(column, false);
									} else {
										hm.put(column, true);
									}
								}
							} else {
								hm.put(column, rs.getBoolean(i));
								if (rs.wasNull()) {
									if (column.equalsIgnoreCase("start_time_flag")
											|| column.equalsIgnoreCase("end_time_flag")) {
										hm.put(column, false);
									} else {
										hm.put(column, true);
									}
								}
							}
							break;
						case TypeNames.FLOAT:
							hm.put(column, rs.getFloat(i));
							if (rs.wasNull()) {
								hm.put(column, 0);
							}
							break;
						case TypeNames.INT:
							hm.put(column, rs.getInt(i));
							if (rs.wasNull()) {
								hm.put(column, 0);
							}
							break;
						case TypeNames.STRING:
							hm.put(column, rs.getString(i));
							if (rs.wasNull()) {
								hm.put(column, "");
							}
							break;
						case TypeNames.CHAR:
							hm.put(column, rs.getString(i));
							if (rs.wasNull()) {
								char x = 'x';
								hm.put(column, x);
							}
							break;
						default:
						}
					}
				}
				al.add(hm);
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while processing result rows, EntityDAO.select: " + ": " + sqle.getMessage()
						+ ": array length: " + al.size());
				sqle.printStackTrace();
			}
		}
		return al;
	}

	/**
	 * Returns the next value of the primary key sequence, if <code> getNextPKName </code> is non-null, or null if
	 * <code> getNextPKName </code> is null.
	 *
	 * @return the next value of the primary key sequence
	 */
	public int getNextPK() {
		int answer = 0;

		if (getNextPKName == null) {
			return answer;
		}

		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);

		ArrayList<HashMap<String, ?>> al = select(digester.getQuery(getNextPKName));
		if (al.size() > 0) {
			HashMap<String, ?> h = al.get(0);
			answer = (Integer) h.get("key");
		}
		return answer;
	}

	/**
	 * Returns the current value of the primary key sequence, if <code> getCurrentPKName </code> is non-null, or null if
	 * <code> getCurrentPKName </code> is null.
	 *
	 * @return the current value of the primary key sequence
	 */
	public int getCurrentPK() {

		int answer = 0;
		if (getCurrentPKName == null) {
			return answer;
		}
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		ArrayList al = select(digester.getQuery(getCurrentPKName));
		if (al.size() > 0) {
			HashMap h = (HashMap) al.get(0);
			answer = (Integer) h.get("key");
		}
		return answer;
	}

	/**
	 * This method executes a "findByPK-style" query. Such a query has two characteristics:
	 * <ol>
	 * <li>The columns selected by the SQL are all of the columns in the table relevant to the DAO, and only those
	 * columns. (e.g., in StudyDAO, the columns selected are all of the columns in the study table, and only those
	 * columns.)
	 * <li>It returns at most one EntityBean.
	 * <li>Typically this means that the WHERE clause includes the columns in a candidate key with "=" criteria.
	 * <li>e.g., "WHERE item_id = ?" when selecting from item
	 * <li>e.g., "WHERE item_id = ? AND event_crf_id=?" when selecting from item_data
	 * </ol>
	 * 
	 * Note that queries which join two tables may be included in the definition of "findByPK-style" query, as long as
	 * the first criterion is met.
	 * 
	 * @param queryName
	 *            The name of the query which should be executed.
	 * @param variables
	 *            The set of variables used to populate the PreparedStatement; should be empty if none are needed.
	 * @return The EntityBean selected by the query.
	 */
	protected EntityBean executeFindByPKQuery(String queryName, HashMap variables) {

		EntityBean answer = new EntityBean();
		String sql = digester.getQuery(queryName);
		logger.debug("query:" + queryName + "variables:" + variables);
		ArrayList rows;
		if (variables == null || variables.isEmpty()) {
			rows = this.select(sql);
		} else {
			rows = this.select(sql, variables);
		}
		Iterator it = rows.iterator();
		if (it.hasNext()) {
			answer = (EntityBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return answer;
	}
	
	/**
	 * Close method.
	 *
	 * @param con data base connection to close
	 * @param rs  ResultSet object to close
	 * @param ps  statement object to close
	 */
	protected void closeIfNecessary(Connection con, ResultSet rs, Statement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception thrown in EntityDAO.closeIfNecessary");
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Close method.
	 *
	 * @param rs ResultSet object to close
	 * @param ps statement object to close
	 */
	protected void closeIfNecessary(ResultSet rs, PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception thrown in EntityDAO.closeIfNecessary(rs,ps)");
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Close method.
	 *
	 * @param con data base connection to close
	 * @param ps  statement object to close
	 */
	protected void closeIfNecessary(Connection con, PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception thrown in EntityDAO.closeIfNecessary");
				sqle.printStackTrace();
			}
		}
	}

	/**
	 * Clear the signals which indicate the success or failure of the query. This method should be called at the
	 * beginning of every select or execute method.
	 */
	protected void clearSignals() {
		querySuccessful = false;
	}

	/**
	 * Signal that the query was successful. Either this method or signalFailure should be called by the time a select
	 * or execute method returns.
	 */
	protected void signalSuccess() {
		querySuccessful = true;
	}

	/**
	 * Signal that the query was unsuccessful. Either this method or signalSuccess should be called by the time a select
	 * or execute method returns.
	 * 
	 * @param sqle
	 *            The SQLException which was thrown by PreparedStatement.execute/executeUpdate.
	 */
	protected void signalFailure(SQLException sqle) {
		querySuccessful = false;
		failureDetails = sqle;
	}

	protected String selectString(HashMap hm, String column) {
		if (hm.containsKey(column)) {
			try {
				String s = (String) hm.get(column);
				if (s != null) {
					return s;
				}
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	protected int selectInt(HashMap hm, String column) {
		if (hm.containsKey(column)) {
			try {
				Integer i = (Integer) hm.get(column);
				if (i != null) {
					return i;
				}
			} catch (Exception e) {
				return 0;
			}
		}
		return 0;
	}

	protected boolean selectBoolean(HashMap hm, String column) {
		if (hm.containsKey(column)) {
			try {
				Boolean b = (Boolean) hm.get(column);
				if (b != null) {
					return b;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 *  Sets localized date formats.
	 */
	private void initializeI18nStrings() {
		if (locale != null) {
			oc_df_string = ApplicationConstants.getDateFormatInItemData();
			local_df_string = ResourceBundleProvider.getFormatBundle(locale).getString("date_format_string");
			local_yf_string = ResourceBundleProvider.getFormatBundle(locale).getString("date_format_year");
			local_ymf_string = ResourceBundleProvider.getFormatBundle(locale).getString("date_format_year_month");
		}
	}

	/**
	 * getECStatusConstraint.
	 *
	 * @param datasetItemStatusId item status ID
	 * @return status constraints
	 */
	public String getECStatusConstraint(int datasetItemStatusId) {

		String statusConstraint;
		switch (datasetItemStatusId) {
		default:
		case DATASET_ITEM_STATUS_INVALID_ID:
		case DATASET_ITEM_STATUS_COMPLETED_ID:
			statusConstraint = "in (2,6)";
			break;
		case DATASET_ITEM_STATUS_NON_COMPLETED_ID:
			statusConstraint = "not in (2,6,5,7)";
			break;
		case DATASET_ITEM_STATUS_COMPLETED_AND_NON_COMPLETED_ID:
			statusConstraint = "not in (5,7)";
			break;
		}
		return statusConstraint;
	}

	/**
	 * getItemDataStatusConstraint.
	 *
	 * @param datasetItemStatusId item status ID
	 * @return status constraints
	 */
	public String getItemDataStatusConstraint(int datasetItemStatusId) {

		String statusConstraint;
		switch (datasetItemStatusId) {
		default:
		case DATASET_ITEM_STATUS_INVALID_ID:
		case DATASET_ITEM_STATUS_COMPLETED_ID:
			statusConstraint = "in (2,6)";
			break;
		case DATASET_ITEM_STATUS_NON_COMPLETED_ID:
			statusConstraint = "not in (6,5,7)";
			break;
		case DATASET_ITEM_STATUS_COMPLETED_AND_NON_COMPLETED_ID:
			statusConstraint = "not in (5,7)";
			break;
		}
		return statusConstraint;
	}

	/**
	 * Close prepared statement.
	 *
	 * @param ps prepared statement to close
	 */
	protected void closePreparedStatement(PreparedStatement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException sqle) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception thrown in EntityDAO.closeIfNecessary");
				logger.error(sqle.getMessage(), sqle);
			}
		}
	}
}
