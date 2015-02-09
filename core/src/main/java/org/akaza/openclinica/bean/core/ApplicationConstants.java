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

package org.akaza.openclinica.bean.core;

public class ApplicationConstants {

	public static String getDateFormatInItemData() {
		return "yyyy-MM-dd";
	}

	public static String getDateFormatInExtract() {
		return "yyyy-MM-dd";
	}

	/**
	 * added clinovo 12/19/2012
	 * 
	 * @return
	 */
	public static String getPDateFormatInSavedData() {
		return "yyyy-MM";
	}
	
	public static String getDateFormatInStudyEvent() {
		return "yyyy-MM-dd HH:mm:ss";
	}

}
