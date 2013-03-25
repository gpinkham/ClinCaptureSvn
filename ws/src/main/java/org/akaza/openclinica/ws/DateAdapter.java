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

package org.akaza.openclinica.ws;

import org.akaza.openclinica.exception.OpenClinicaSystemException;

import java.util.Date;
import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateAdapter extends XmlAdapter<String, Date> {

	// the desired format
	private String pattern = "MM/dd/yyyy";

	public String marshal(Date date) throws Exception {
		return new SimpleDateFormat(pattern).format(date);
	}

	public Date unmarshal(String dateString) throws Exception {
		throw new OpenClinicaSystemException("Please implement me!!");
	}

}
