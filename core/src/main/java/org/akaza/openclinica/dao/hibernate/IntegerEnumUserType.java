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

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.rule.expression.Context;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.util.ReflectHelper;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A generic UserType that handles String-based JDK 5.0 Enums.
 * 
 * @author Gavin King
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IntegerEnumUserType implements EnhancedUserType, ParameterizedType {

	private Class<Context> enumClass;

	public void setParameterValues(Properties parameters) {
		String enumClassName = parameters.getProperty("enumClassname");
		try {
			enumClass = ReflectHelper.classForName(enumClassName);
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("Enum class not found", cnfe);
		}
	}

	public Class returnedClass() {
		return enumClass;
	}

	public int[] sqlTypes() {
		return new int[] { Hibernate.INTEGER.sqlType() };
	}

	public boolean isMutable() {
		return false;
	}

	public Object deepCopy(Object value) {
		return value;
	}

	public Serializable disassemble(Object value) {
		return (Context) value;
	}

	public Object replace(Object original, Object target, Object owner) {
		return original;
	}

	public Object assemble(Serializable cached, Object owner) {
		return cached;
	}

	public boolean equals(Object x, Object y) {
		return x == y;
	}

	public int hashCode(Object x) {
		return x.hashCode();
	}

	public Object fromXMLString(String xmlValue) {
		return Enum.valueOf(enumClass, xmlValue);
	}

	public String objectToSQLString(Object value) {
		return '\'' + ((Context) value).getCode().toString() + '\'';
	}

	public String toXMLString(Object value) {
		return ((Context) value).getCode().toString();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
		String name = rs.getString(names[0]);
		return rs.wasNull() ? null : Context.getByCode(Integer.parseInt(name));
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
		if (value == null) {
			st.setNull(index, Hibernate.INTEGER.sqlType());
		} else {
			st.setInt(index, ((Context) value).getCode());
		}
	}
}
