/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

package org.akaza.openclinica.bean.extract;

/**
 * Validate if a SAS variable name is valid.
 * 
 * <p>
 * A valid SAS variable name should follow the rule that
 * <li>it can be up to eight characters long
 * <li>it can consist of only letters, digits and underscore characters.
 * <li>its first character cannot be a digit <br>
 * <p>
 * Rules for creating a valid SAS variable name:
 * <li>Replace any invalid character with an underscore
 * <li>If the first character is a digit, it is replaced by an underscore
 * <li>If a name is longer than 8 characters, it will be truncated to 8 characters. If it results in non-unique name in
 * a data file, sequential numbers are used to replace its letters at the end. By default, the size of sequential
 * numbers is 3.
 */

public class SasNameValidator extends NameValidator {

	private int nameMaxLength;

	private static final int DEFAULT_NAME_MAX_LENGTH = 8;

	private static final int RADIX = 36;

	public SasNameValidator() {

		nameMaxLength = DEFAULT_NAME_MAX_LENGTH;
	}

	/**
	 * Get unique SAS name using 36 radix.
	 *
	 * @param variableName String
	 * @return String
	 */
	@Override
	public String getValidName(String variableName) {

		int maxValue = this.computeMaxValue(RADIX, this.digitSize);
		// if variableName is null, automatically generate
		if (variableName == null || variableName.trim().length() == 0) {
			return getNextSequentialString(maxValue);
		}
		int i;

		// get all chars from the string first
		String temp = variableName.trim();
		char[] c = temp.length() > getNameMaxLength()
				? temp.substring(0, getNameMaxLength()).toCharArray() : temp.toCharArray();

		// replacing every invalid character with the replacingChar
		for (i = 0; i < c.length; ++i) {
			if (!isValid(c[i])) {
				c[i] = replacingChar;
			}
		}

		// if the first one is a digit
		if (c[0] >= '0' && c[0] <= '9') {
			// if there is already 32 characters
			if (c.length >= getNameMaxLength()) {
				for (i = c.length - 1; i >= 1; --i) {
					c[i] = c[i - 1];
				}
				c[0] = replacingChar;
			} else {
				char[] cc = new char[c.length + 1];
				cc[0] = replacingChar;
				for (i = 1; i < cc.length; ++i) {
					cc[i] = c[i - 1];
				}
				c = cc;
			}
		}

		String originalItemName = new String(c);
		String resultSasItemName = originalItemName;
		// if not unique
		while (uniqueNameTable.contains(resultSasItemName)) {
			String nextSequential = getNextSequentialString(maxValue);
			int originalItemNameLengthToBePreserved = getNameMaxLength() - nextSequential.length();
			if (originalItemName.length() > originalItemNameLengthToBePreserved) {
				resultSasItemName = originalItemName.substring(0, originalItemNameLengthToBePreserved) + nextSequential;
			} else {
				resultSasItemName = originalItemName + nextSequential;
			}
		}
		uniqueNameTable.add(resultSasItemName);
		return resultSasItemName;
	}

	// only alphabets, digits, and _ are valid
	@Override
	protected boolean isValid(char c) {

		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
	}

	/**
	 * Get next sequential String using 36 radix. This sequential string should be smaller than maxValue.
	 * 
	 * @return String
	 */
	@Override
	public String getNextSequentialString(int maxValue) {

		if (this.sequential >= maxValue) {
			System.exit(1);
		}
		String nextSequential = Integer.toString(sequential, RADIX);
		++this.sequential;
		return nextSequential.toUpperCase();
	}

	private int computeMaxValue(int base, int digitSize) {
		return (int) Math.pow(base, digitSize);
	}

	public int getNameMaxLength() {
		return nameMaxLength;
	}

	public void setNameMaxLength(int nameMaxLength) {
		this.nameMaxLength = nameMaxLength;
	}
}
