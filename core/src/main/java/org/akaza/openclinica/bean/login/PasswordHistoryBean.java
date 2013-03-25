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

/*
 * ClinCapture is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.login;

import java.util.Date;

/**
 * @author tatangsurja (Clinovo)
 * 
 * 
 */
public class PasswordHistoryBean {

	private int passwordId;
	private String userName;
	private int userId;
	private String password;
	private Date dateFirstUsed;
	private Date dateLastUsed;

	/**
	 * @return Returns the passwordId.
	 */
	public int getPasswordId() {
		return passwordId;
	}

	/**
	 * @param passwordId
	 *            The passwordId to set.
	 */
	public void setPasswordId(int passwordId) {
		this.passwordId = passwordId;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return Returns the userId.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            The userId to set.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the dateFirstUsed.
	 */
	public Date getDateFirstUsed() {
		return dateFirstUsed;
	}

	/**
	 * @param dateFirstUsed
	 *            The dateFirstUsed to set.
	 */
	public void setDateFirstUsed(Date dateFirstUsed) {
		this.dateFirstUsed = dateFirstUsed;
	}

	/**
	 * @return Returns the dateLastUsed.
	 */
	public Date getDateLastUsed() {
		return dateLastUsed;
	}

	/**
	 * @param dateLastUsed
	 *            The dateLastUsed to set.
	 */
	public void setDateLastUsed(Date dateLastUsed) {
		this.dateLastUsed = dateLastUsed;
	}

}
