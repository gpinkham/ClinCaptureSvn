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

package org.akaza.openclinica.exception;

/**
 * Description: this exception will be thrown in the class CdiscOdmXmlBean when multiple values are found for one item.
 * If this exception is thrown, it means either the database integrity is not consistent or my understanding of the
 * tables has flaw.
 * 
 * @author ywang
 */

public class MultipleItemValuesFoundInXMLParsingException extends Exception {
	public MultipleItemValuesFoundInXMLParsingException() {
		super();
	}

	public MultipleItemValuesFoundInXMLParsingException(String mesg) {
		super(mesg);
	}
}
