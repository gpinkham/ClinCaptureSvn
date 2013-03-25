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

package org.akaza.openclinica.domain.technicaladmin;

import org.akaza.openclinica.domain.enumsupport.CodedEnum;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.HashMap;
import java.util.ResourceBundle;

/*
 * Use this enum as login status holder
 * @author Krikor Krumlian
 *
 */

public enum LoginStatus implements CodedEnum {

	SUCCESSFUL_LOGIN(1, "successful_login"), FAILED_LOGIN(2, "failed_login"), FAILED_LOGIN_LOCKED(3,
			"failed_login_locked"), SUCCESSFUL_LOGOUT(4, "successful_logout");

	private int code;
	private String description;

	LoginStatus(int code) {
		this(code, null);
	}

	LoginStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	@Override
	public String toString() {
		ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
		return resterm.getString(getDescription());
	}

	public static LoginStatus getByName(String name) {
		return LoginStatus.valueOf(LoginStatus.class, name);
	}

	public static LoginStatus getByCode(Integer code) {
		HashMap<Integer, LoginStatus> enumObjects = new HashMap<Integer, LoginStatus>();
		for (LoginStatus theEnum : LoginStatus.values()) {
			enumObjects.put(theEnum.getCode(), theEnum);
		}
		return enumObjects.get(Integer.valueOf(code));
	}

	public Integer getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

}
