/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package org.akaza.openclinica.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Frank
 * 
 */
public class StringValidator {

	/***
	 * Checks if passed string is a valid URL
	 * 
	 * @param url
	 *            String
	 * @return boolean
	 */
	public static boolean isValidURL(String url) {

		Pattern urlPattern = Pattern.compile(
				"((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = urlPattern.matcher(url);
		return matcher.find();
	}

	/***
	 * Checks if passed string is contains at least one number
	 * 
	 * @param text
	 *            String
	 * @return boolean
	 */
	public static boolean hasNumber(String text) {

		Pattern textPattern = Pattern.compile(".*\\d.*");
		Matcher matcher = textPattern.matcher(text);
		return matcher.find();
	}

	/***
	 * Checks if passed string is a valid integer (non floating-point) e.g 200, 1, 6
	 * 
	 * @param integer
	 *            String
	 * @return boolean
	 */
	public static boolean isValidInteger(String integer) {

		Pattern integerPattern = Pattern.compile("^(?:-)?\\d+$");
		Matcher matcher = integerPattern.matcher(integer);
		return matcher.find();
	}

	/***
	 * Checks if passed string is a valid number (integer or floating-point) e.g 200, 3.2, -56, -98.01
	 * 
	 * @param number
	 *            String
	 * @return boolean
	 */
	public static boolean isValidNumber(String number) {

		Pattern numberPattern = Pattern.compile("^(?:-)?\\d+(\\.{1}\\d+)?$");
		Matcher matcher = numberPattern.matcher(number);
		return matcher.find();
	}

	/***
	 * Checks if passed string is a valid date in the format YYYY-MM-DD
	 * 
	 * @param date
	 *            String
	 * @return boolean
	 */
	public static boolean isValidDateYYYYMMDD(String date) {

		Pattern datePattern = Pattern.compile("((19|20)\\d\\d)(-)(0?[1-9]|1[012])(-)(0?[1-9]|[12][0-9]|3[01])");
		Matcher matcher = datePattern.matcher(date);
		return matcher.find();
	}
}
