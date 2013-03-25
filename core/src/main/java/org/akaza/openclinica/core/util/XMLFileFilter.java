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

package org.akaza.openclinica.core.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * For filtering the files in a directory for xml files.
 * @author jnyayapathi
 *
 */

/**
 * Rules to Follow to add classes/methods to Util package: No repeating the code. Make a class/method do just one thing.
 * No business logic code. Don't write code that isn't needed. No Coupling. Be more Modular Write code like your code is
 * an External API
 */
public class XMLFileFilter implements FilenameFilter {

	public boolean accept(File arg0, String name) {
		// TODO Auto-generated method stub
		return (name.endsWith(".xml"));

	}

}
