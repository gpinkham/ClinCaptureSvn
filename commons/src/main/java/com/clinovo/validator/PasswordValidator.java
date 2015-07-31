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

package com.clinovo.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.dao.hibernate.PasswordRequirementsDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;

/**
 * PasswordValidator.
 * 
 * @author Leonel Gayard
 * @author Doug Rodrigues (douglas.rodrigues@openclinica.com)
 * 
 */
public final class PasswordValidator {

	private PasswordValidator() {
	}

	private static boolean hasLowerCaseChars(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (Character.isLowerCase(str.charAt(i)))
				return true;
		}
		return false;
	}

	private static boolean hasUpperCaseChars(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (Character.isUpperCase(str.charAt(i)))
				return true;
		}
		return false;
	}

	private static boolean hasDigits(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (Character.isDigit(str.charAt(i)))
				return true;
		}
		return false;
	}

	private static boolean hasSpecialChars(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (PasswordRequirementsDao.SPECIALS.indexOf(str.charAt(i)) >= 0)
				return true;
		}
		return false;
	}
	/**
	 * Validates whether a new password meets the requirements set by the administrator.
	 * 
	 * @param passwordRequirementsDao
	 *            PasswordRequirementsDao
	 * @param userDao
	 *            UserAccountDAO
	 * @param userId
	 *            int
	 * @param newPassword
	 *            String
	 * @param newHash
	 *            String
	 * @param resexception
	 *            ResourceBundle
	 * @return list of strings with validation errors; empty list if password meets all validation requirements
	 */
	public static List<String> validatePassword(PasswordRequirementsDao passwordRequirementsDao, UserAccountDAO userDao,
			int userId, String newPassword, String newHash, ResourceBundle resexception) {
		ArrayList<String> errors = new ArrayList<String>();

		UserAccountBean userBean = (UserAccountBean) userDao.findByPK(userId);
		if (userBean.getPasswd().equals(newHash)) {
			errors.add(resexception.getString("pwd_cannot_reuse"));
		}

		int minLen = passwordRequirementsDao.minLength(), maxLen = passwordRequirementsDao.maxLength();

		if (newPassword.length() == 0) {
			return new ArrayList<String>();
		}

		if (minLen > 0 && newPassword.length() < minLen) {
			errors.add(resexception.getString("pwd_too_short") + " " + minLen + " " + resexception.getString("chars"));
		}

		if (maxLen > 0 && newPassword.length() > maxLen) {
			errors.add(resexception.getString("pwd_too_long") + " " + maxLen + " " + resexception.getString("chars"));
		}
		if (passwordRequirementsDao.hasLower() && !hasLowerCaseChars(newPassword)) {
			errors.add(resexception.getString("pwd_needs_lower_case"));
		}
		if (passwordRequirementsDao.hasUpper() && !hasUpperCaseChars(newPassword)) {
			errors.add(resexception.getString("pwd_needs_upper_case"));
		}
		if (passwordRequirementsDao.hasDigits() && !hasDigits(newPassword)) {
			errors.add(resexception.getString("pwd_needs_digits"));
		}
		if (passwordRequirementsDao.hasSpecials() && !hasSpecialChars(newPassword)) {
			errors.add(resexception.getString("pwd_needs_special_chars"));
		}
		return errors;
	}
}
