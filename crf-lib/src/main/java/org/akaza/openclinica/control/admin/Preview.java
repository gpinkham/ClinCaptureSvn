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

package org.akaza.openclinica.control.admin;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

@SuppressWarnings({ "rawtypes" })
public interface Preview {
	Map<String, Map> createCrfMetaObject(Workbook workbook);

	Map<Integer, Map<String, String>> createItemsOrSectionMap(Workbook workbook, String itemsOrSection);

	Map<Integer, Map<String, String>> createGroupsMap(Workbook workbook);

	Map<String, String> createCrfMap(Workbook workbook);
}
