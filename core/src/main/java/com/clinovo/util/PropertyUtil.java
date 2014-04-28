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
package com.clinovo.util;

import com.clinovo.model.PropertyAccess;
import com.clinovo.model.PropertyType;

/**
 * @author Frank
 * 
 */
public class PropertyUtil {

	/***
	 * Gets PropertyType for passed string.
	 * 
	 * @param string
	 *            String
	 * @return PropertyType matching string; returns null if no match is found
	 */
	public static PropertyType getPropertyTypeByString(String string) {

		if (string.equals(PropertyType.TEXT.toString())) {
			return PropertyType.TEXT;
		}

		if (string.equals(PropertyType.RADIO.toString())) {
			return PropertyType.RADIO;
		}

		if (string.equals(PropertyType.COMBOBOX.toString())) {
			return PropertyType.COMBOBOX;
		}

		if (string.equals(PropertyType.DYNAMIC_INPUT.toString())) {
			return PropertyType.DYNAMIC_INPUT;
		}

		if (string.equals(PropertyType.DYNAMIC_RADIO.toString())) {
			return PropertyType.DYNAMIC_RADIO;
		}

		if (string.equals(PropertyType.FILE.toString())) {
			return PropertyType.FILE;
		}

		if (string.equals(PropertyType.PASSWORD.toString())) {
			return PropertyType.PASSWORD;
		}

		return null;
	}

	/***
	 * Gets PropertyAccess for passed string.
	 * 
	 * @param string
	 *            String
	 * @param forceRead
	 *            boolean
	 * @return PropertyType matching string; returns null if no match is found
	 */
	public static PropertyAccess getPropertyAccessByString(String string, boolean forceRead) {

		if (forceRead) {
			return PropertyAccess.READ;
		}

		if (string.equals(PropertyAccess.HIDDEN.toString())) {
			return PropertyAccess.HIDDEN;
		}

		if (string.equals(PropertyAccess.WRITE.toString())) {
			return PropertyAccess.WRITE;
		}

		if (string.equals(PropertyAccess.READ.toString())) {
			return PropertyAccess.READ;
		}

		return null;
	}
}
