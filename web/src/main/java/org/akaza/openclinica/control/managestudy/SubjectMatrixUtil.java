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

package org.akaza.openclinica.control.managestudy;

/**
 * A Utility class for the issues involving the Subject matrix or table
 * 
 * @author Bruce W. Perry 3/2009
 */
public class SubjectMatrixUtil {

	/**
	 * Create an extended query string for a URL, based on a page number like "2" passed into the method.
	 * 
	 * @param pageNumber
	 *            A String
	 * @return A String representing the entire query string
	 */
	public String createPaginatingQuery(String pageNumber) {

		StringBuilder paginatingQuery = new StringBuilder("");
		if (pageNumber != null && (!"".equalsIgnoreCase(pageNumber))) {
			int tempNum = 0;
			try {
				tempNum = Integer.parseInt(pageNumber);
			} catch (NumberFormatException nfe) {
				// tempNum is already initialized to 0
			}
			if (tempNum > 0) {
				paginatingQuery = new StringBuilder(ListStudySubjectServlet.SUBJECT_PAGE_NUMBER).append("=").append(
						pageNumber);
				paginatingQuery.append("&ebl_paginated=1");
			}
		}
		return paginatingQuery.toString();
	}
}
