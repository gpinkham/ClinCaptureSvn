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
 * Created on Aug 26, 2004
 *
 *
 */
package org.akaza.openclinica.dao.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PreparedStatementFactory {
	private Map variables = new HashMap();
	private HashMap nullVars = new HashMap();// to handle null
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public PreparedStatementFactory() {
	}

	public PreparedStatementFactory(Map variables) {
		this.variables = variables;
	}

	/**
	 * If need to handle null inputs
	 * 
	 * @param variables
	 * @param nullVars
	 */
	public PreparedStatementFactory(HashMap variables, HashMap nullVars) {
		this.variables = variables;
		this.nullVars = nullVars;
	}

	public void addVariable(int order, Object param) {
		variables.put(Integer.valueOf(order), param);
	}

	public PreparedStatement generate(PreparedStatement ps) throws SQLException, NullPointerException {

		Set varSet = variables.entrySet();
		for (Iterator varIt = varSet.iterator(); varIt.hasNext();) {
			Map.Entry varMe = (Map.Entry) varIt.next();
			Integer order = (Integer) varMe.getKey();
			Object objParam = varMe.getValue();
			if (objParam == null) {
				logger.warn("found null object! " + order);
				if (nullVars.get(order) != null) {
					Integer nullType = (Integer) nullVars.get(order);
					ps.setNull(order.intValue(), nullType.intValue());
				} else {
					throw new NullPointerException("No type found for this null object at order:" + order
							+ ", make sure you set the type in your DAO.");
				}
			} else {
				String objType = objParam.getClass().getName();

				logger.trace("\nfound object name:[" + objType + "] [" + order + "] value[" + objParam + "]");

				if ("java.lang.String".equals(objType)) {
					ps.setString(order.intValue(), objParam.toString());
				} else if ("java.lang.Float".equals(objType)) {
					Float objFloatParam = (Float) objParam;
					ps.setFloat(order.intValue(), objFloatParam.floatValue());
				} else if ("java.lang.Integer".equals(objType)) {
					Integer objIntParam = (Integer) objParam;
					ps.setInt(order.intValue(), objIntParam.intValue());
				} else if ("java.util.Date".equals(objType)) {
					java.util.Date objTempDate = (java.util.Date) objParam;
					java.sql.Date objDateParam = new java.sql.Date(objTempDate.getTime());
					// (java.sql.Date)objParam;
					ps.setDate(order.intValue(), objDateParam);
				} else if ("java.sql.Date".equals(objType)) {// added by
					// jxu,2004-10-26
					// a date from DB but not set on page, still sql date type
					ps.setDate(order.intValue(), (java.sql.Date) objParam);
				} else if ("java.sql.Timestamp".equals(objType)) {
					ps.setTimestamp(order.intValue(), (java.sql.Timestamp) objParam);
				} else if ("java.lang.Boolean".equals(objType)) {
					// BADS FLAG
					if (CoreResources.getDBName().equals("oracle")) {
						Boolean objBoolParam = (Boolean) objParam;
						ps.setString(order.intValue(), objBoolParam ? "1" : "0");
					} else {
						Boolean objBoolParam = (Boolean) objParam;
						ps.setBoolean(order.intValue(), objBoolParam.booleanValue());
					}

				} else if ("java.lang.Byte".equals(objType)) {
					ps.setObject(order.intValue(), objParam, Types.BIT);
				} else if ("java.lang.Character".equals(objType)) {
					ps.setObject(order.intValue(), objParam, Types.CHAR);
				} else if ("java.lang.Double".equals(objType)) {
					ps.setObject(order.intValue(), objParam, Types.DOUBLE);
				} else if ("java.lang.Long".equals(objType)) {
					ps.setObject(order.intValue(), objParam, Types.NUMERIC);
				} else if ("java.lang.Short".equals(objType)) {
					ps.setObject(order.intValue(), objParam, Types.SMALLINT);
				} else if ("java.math.BigDecimal".equals(objType)) {
					ps.setBigDecimal(order.intValue(), (java.math.BigDecimal) objParam);
				} else {
					// throw missing variable type exception here???
					throw new NullPointerException("did not find object, possible null at " + order);
				}
			}// end of else loop
		}// end of for loop

		return ps;
	}
}
