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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Krikor Krumlian
 * 
 */
public class ExpressionTreeHelper {

	protected static final Logger logger = LoggerFactory.getLogger(ExpressionTreeHelper.class.getName());
	final static String yyyyMMddDashes = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}";
	final static String yyyyMMddSlashes = "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}";
	final static String MMddyyyySlashes = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}";
	final static String MMddyyyyDashes = "[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}";
	final static String yyyyMMddFORMATSlashes = "yyyy/MM/dd";
	final static String MMddyyyyFORMATSlashes = "MM/dd/yyyy";
	final static String yyyyMMddFORMATDashes = "yyyy-MM-dd";
	final static String MMddyyyyFORMATDashes = "MM-dd-yyyy";
	final static String ddMMMyyyyFORMATDashes = "dd-MMM-yyyy";

	static Date getDate(String dateString) {
		logger.info("DateString : " + dateString);
		String[] componentsOfDate = dateString.split("[/|.|-]");
		if (componentsOfDate.length == 3) {
			dateString = componentsOfDate[0] + "-" + componentsOfDate[1] + "-" + componentsOfDate[2];
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date d = sdf.parse(dateString);
				return d;
			} catch (ParseException e) {
				throw new OpenClinicaSystemException("OCRERR_0004", new Object[] { dateString });
			}
		} else {
			throw new OpenClinicaSystemException("OCRERR_0004", new Object[] { dateString });
		}
	}

	static Date getDateFromddMMMyyyyDashes(String dateString) {
		logger.info("DateString : " + dateString);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date d = sdf.parse(dateString);
			return d;
		} catch (ParseException e) {
			throw new OpenClinicaSystemException("OCRERR_0004", new Object[] { dateString });
		}
	}

	static private boolean isDate(String dateString, String format, String dateRegexp) {
		logger.info("DateString : " + dateString);

		if (!dateString.matches(dateRegexp)) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date theDate = null;
		try {
			theDate = sdf.parse(dateString);
		} catch (ParseException e) {
			return false;
		}

		if (!sdf.format(theDate).equals(dateString)) {
			return false;
		}

		return true;
	}

	static public boolean isDateyyyyMMdd(String dateString) {
		String dateFormat = yyyyMMddFORMATSlashes;
		String dateRegexp = yyyyMMddSlashes;
		String dateFormatDashes = yyyyMMddFORMATDashes;
		String dateRegexpDashes = yyyyMMddDashes;

		if (isDate(dateString, dateFormat, dateRegexp) || isDate(dateString, dateFormatDashes, dateRegexpDashes))
			return true;
		else
			return false;
	}

	static public boolean isDateddMMMyyyyDashes(String dateString) {
		String dateFormat = ddMMMyyyyFORMATDashes;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date theDate = null;
		try {
			theDate = sdf.parse(dateString);
		} catch (ParseException e) {
			logger.info("dateString=" + dateString + " failed parse format: " + dateFormat);
			return false;
		}
		if (!sdf.format(theDate).equals(dateString)) {
			logger.info("dateSring=" + dateString + " has been parsed to " + theDate
					+ " which cannot be formatted back to " + dateString);
			return false;
		}
		return true;
	}

	static public String isValidDateMMddyyyy(String theString) {
		String dateFormat = MMddyyyyFORMATSlashes;
		String dateRegexp = MMddyyyySlashes;
		return ifValidDateFormatAsyyyyMMdd(theString, dateFormat, dateRegexp);
	}

	static public String ifValidDateFormatAsyyyyMMdd(String theString, String format, String dateRegexp) {

		if (!theString.matches(dateRegexp)) {
			return theString;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date testDate = null;

		// Tried to parse with the above format
		try {
			testDate = sdf.parse(theString);
		} catch (ParseException e) {
			return theString;
		}

		// dateformat.parse will accept any date as long as it's in the format
		// you defined, it simply rolls dates over, for example, december 32
		// becomes jan 1 and december 0 becomes november 30
		// This statement will make sure that once the string
		// has been checked for proper formatting that the date is still the
		// date that was entered, if it's not, we assume that the date is invalid

		if (!sdf.format(testDate).equals(theString)) {
			return theString;
		}

		// if we make it to here without getting an error it is assumed that
		// the date was a valid one and that it's in the proper format

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String theNewString = sdf2.format(testDate);
		return theNewString;
	}

}
