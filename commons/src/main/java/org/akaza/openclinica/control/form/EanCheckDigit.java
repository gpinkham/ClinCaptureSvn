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

package org.akaza.openclinica.control.form;

/**
 * EanCheckDigit.
 */
public class EanCheckDigit {

	private static final int[] POSITION_WEIGHT = new int[]{3, 1};
	private int modulus = 10;

	public boolean isValid(String code) {

		if (code.length() != 13) {
			return false;
		}
		if (code.equals("0000000000000")) {
			return true;
		}
		try {
			int modulusResult = calculateModulus(code, true);
			return (modulusResult == 0);
		} catch (Exception ex) {
			return false;
		}
	}

	protected int calculateModulus(String code, boolean includesCheckDigit) throws Exception {
		int total = 0;
		for (int i = 0; i < code.length(); i++) {
			int lth = code.length() + (includesCheckDigit ? 0 : 1);
			int leftPos = i + 1;
			int rightPos = lth - i;
			int charValue = toInt(code.charAt(i), leftPos, rightPos);
			total += weightedValue(charValue, leftPos, rightPos);
		}
		if (total == 0) {
			throw new Exception("Invalid code, sum is zero");
		}
		return (total % modulus);
	}

	protected int toInt(char character, int leftPos, int rightPos) throws Exception {
		if (Character.isDigit(character)) {
			return Character.getNumericValue(character);
		} else {
			throw new Exception("Invalid Character[" + leftPos + "] = '" + character + "'");
		}
	}

	protected int weightedValue(int charValue, int leftPos, int rightPos) {
		int weight = POSITION_WEIGHT[rightPos % 2];
		return (charValue * weight);
	}
}
