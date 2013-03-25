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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.core;

/**
 * @author ywang
 * @author thickerson
 * 
 *         Constant values are matched with java.sql.Types constant field values
 */
public interface TypeNames {
	// YW 04-2007 << With following match, JDBC driver could know datatype >> YW
	public static final int ARRAY = 2003;// as ARRAY
	// public static final int ASCII_STREAM = 2;
	public static final int BIG_DECIMAL = 3; // as DECIMAL
	public static final int BINARY_STREAM = -2; // as BINARY
	public static final int BLOB = 2004;// as BLOB
	public static final int BYTE = -6; // as TINYINT
	// public static final int BYTES = 7;
	// public static final int CHARACTER_STREAM = 8;
	public static final int CLOB = 2005;// as CLOB
	public static final int DATE = 91;// as DATE
	public static final int DOUBLE = 8;// as DOUBLE
	public static final int FLOAT = 6;// as FLOAT
	public static final int INT = 4; // as INTEGER
	public static final int LONG = -5; // as BIGINT
	public static final int OBJECT = 2000; // as JAVA_OBJECT
	public static final int REF = 2006; // as REF
	public static final int SHORT = 5; // as SMALLINT
	public static final int STRING = 12; // as VARCHAR, LONGVARCHAR
	public static final int TIME = 92; // as TIME
	public static final int TIMESTAMP = 93; // as TIMESTAMP
	// added 10-13-2004, tbh
	public static final int CHAR = 1; // as CHAR
	public static final int BOOL = 16; // as BOOLEAN
}
