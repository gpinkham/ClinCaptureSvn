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
 * copyright 2003-2007 Akaza Research
 */

package org.akaza.openclinica.bean.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

public final class Utils {

	private static Utils ref;

	private Utils() {
	}

	public static Utils getInstance() {
		if (ref == null) {
			ref = new Utils();
		}
		return ref;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * This Method will compare the two Dates and return a String with number of years , weeks and days.
	 *
	 * @param eventStartDate
	 *            The event start date
	 * @param subjectDOB
	 *            the Subject's date of birth
	 * @return String
	 */
	public String processAge(Date eventStartDate, Date subjectDOB) {
		int years = 0, months = 0, days = 0;
		String ret = "";

		if (eventStartDate == null || subjectDOB == null) {
			return "N/A";
		}

		// example : 10/20/2006
		Calendar eventsd = Calendar.getInstance();
		eventsd.setTime(eventStartDate);
		long init = eventsd.getTimeInMillis();

		// example : 10/20/1990
		Calendar dob = Calendar.getInstance();
		dob.setTime(subjectDOB);
		long latter = dob.getTimeInMillis();

		// logger.info("<<< event start date: "+eventsd.toString());
		// logger.info("<<< subject birth date: "+dob.toString());
		long difference = Math.abs(init - latter);
		double daysDifference = Math.floor(difference / 1000 / 60 / 60 / 24);
		// logger.info("<<< found age, days difference "+daysDifference);

		if (daysDifference > 200 * 365.24) {
			return "N/A";
			// year is probably set to 0001, in which case DOB was not used but
			// is now
		}

		// Get the number of years
		while (daysDifference - 365.24 > 0) {
			daysDifference = daysDifference - 365.24;
			years++;
		}

		// Get the number of months
		while (daysDifference - 30.43 > 0) {
			daysDifference = daysDifference - 30.43;
			months++;
		}

		// Get the number of days
		while (daysDifference - 1 >= 0) {
			daysDifference = daysDifference - 1;
			days++;
			// was off by one day, hope this fixes it, tbh 102007
		}
		ResourceBundle reswords = ResourceBundleProvider.getWordsBundle();
		if (years > 0)
			ret = ret + years + " " + reswords.getString("Years") + " - ";
		if (months > 0)
			ret = ret + months + " " + reswords.getString("Months") + " - ";
		if (days > 0)
			ret = ret + days + " " + reswords.getString("Days");
		// also changed the above, tbh 10 2007
		if (ret.equals(""))
			ret = reswords.getString("Less_than_a_day");
		return ret;

	}

	/**
	 * Convert string with from_pattern to string with to_pattern. Browser locale.
	 * 
	 * @param itemValue
	 *            String
	 * @param from_pattern
	 *            String
	 * @param to_pattern
	 *            String
	 * @param locale
	 *            Locale
	 * 
	 * @return String
	 */
	public static String convertedItemDateValue(String itemValue, String from_pattern, String to_pattern, Locale locale) {
		if (itemValue != null && !StringUtil.isFormatDate(itemValue, to_pattern)) {
			SimpleDateFormat sdf = new SimpleDateFormat(from_pattern, locale);
			sdf.setLenient(false);
			try {
				java.util.Date date = sdf.parse(itemValue);
				return new SimpleDateFormat(to_pattern, locale).format(date);
			} catch (ParseException fe) {
				return itemValue;
			}
		} else {
			return itemValue;
		}
	}

	/**
	 * Convert string with from_pattern to string with to_pattern. Default locale
	 */
	public static String convertedItemDateValue(String itemValue, String from_pattern, String to_pattern) {
		return convertedItemDateValue(itemValue, from_pattern, to_pattern, Locale.getDefault());
	}

	/**
	 * Zip StringBuffer to a file.
	 * 
	 * @param fileName
	 *            String
	 * @param filePath
	 *            String
	 * @param content
	 *            StringBuffer
	 * @throws IOException
	 *             the IOException
	 * @return boolean
	 */
	public static boolean createZipFile(String fileName, String filePath, StringBuffer content) throws IOException {

		ZipOutputStream z = null;

		try {
			File dir = new File(filePath);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			z = new ZipOutputStream(new FileOutputStream(new File(dir, fileName + ".zip")));
			z.putNextEntry(new ZipEntry(fileName));
			byte[] bytes = content.toString().getBytes();
			z.write(bytes, 0, bytes.length);
			z.closeEntry();
			z.finish();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (z != null) {
				z.close();
			}
		}
	}

	/**
	 * age = the_year_of_controlDate - the_year_of_birthDate.
	 * 
	 * @param birthDate
	 *            Date
	 * @param controlDate
	 *            Date
	 * @return Integer
	 */
	public static Integer getAge(Date birthDate, Date controlDate) {
		Integer age = -1;
		if (birthDate.before(controlDate)) {
			Calendar dateOfBirth = Calendar.getInstance();
			dateOfBirth.setTime(birthDate);
			Calendar theDate = Calendar.getInstance();
			theDate.setTime(controlDate);
			age = theDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			Calendar today = Calendar.getInstance();
			// add the age to the year to see if it's happened yet
			dateOfBirth.add(Calendar.YEAR, age);
			// subtract one from the age if the birthday hasn't happened yet
			if (today.before(dateOfBirth)) {
				age--;
			}
		}
		return age;
	}

	public static String getAttachedFilePath(StudyBean study) {
		String attachedFilePath = CoreResources.getField("attached_file_location");
		// @pgawade 15-April-2011: issue #8682
		if (attachedFilePath == null || attachedFilePath.length() <= 0) {
			// attachedFilePath = CoreResources.getField("filePath") +
			// "attached_files" + File.separator + study.getIdentifier() +
			// File.separator;
			attachedFilePath = CoreResources.getField("filePath") + File.separator + "attached_files" + File.separator
					+ study.getOid() + File.separator;
		} else {
			// attachedFilePath += study.getIdentifier() + File.separator;
			attachedFilePath += File.separator + study.getOid() + File.separator;
		}
		return attachedFilePath;
	}

	public static String getAttachedFileRootPath() {
		String rootPath = CoreResources.getField("attached_file_location");
		if (rootPath == null || rootPath.length() <= 0) {
			rootPath = CoreResources.getField("filePath") + "attached_files" + File.separator;
		}
		return rootPath;
	}

	/*
	 * see if a regular expression fits
	 * 
	 * @author thickerson August 5th 2010
	 */
	public static boolean isMatchingRegexp(String input, String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(input);
		return m.matches();
	}

	public static boolean isWithinRegexp(String input, String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(input);
		return m.find();
	}
}
