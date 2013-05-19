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

package org.akaza.openclinica.core;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

public class OpenClinicaPasswordEncoder implements PasswordEncoder {

	PasswordEncoder currentPasswordEncoder;
	PasswordEncoder oldPasswordEncoder;

	public OpenClinicaPasswordEncoder() {
	}

	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		return currentPasswordEncoder.encodePassword(rawPass, salt);
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {

		boolean result = false;
		if (currentPasswordEncoder.isPasswordValid(encPass, rawPass, salt)
				|| oldPasswordEncoder.isPasswordValid(encPass, rawPass, salt)) {
			result = true;
		}
		return result;
	}

	public PasswordEncoder getCurrentPasswordEncoder() {
		return currentPasswordEncoder;
	}

	public void setCurrentPasswordEncoder(PasswordEncoder currentPasswordEncoder) {
		this.currentPasswordEncoder = currentPasswordEncoder;
	}

	public PasswordEncoder getOldPasswordEncoder() {
		return oldPasswordEncoder;
	}

	public void setOldPasswordEncoder(PasswordEncoder oldPasswordEncoder) {
		this.oldPasswordEncoder = oldPasswordEncoder;
	}

}
