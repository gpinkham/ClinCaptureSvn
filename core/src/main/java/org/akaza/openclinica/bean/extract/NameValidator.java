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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2010 Akaza
 * Research
 * 
 */

package org.akaza.openclinica.bean.extract;

import java.util.TreeSet;

/**
 * Abstract class for special name validation
 * 
 * @auther ywang
 */

public abstract class NameValidator {
	protected TreeSet<String> uniqueNameTable = new TreeSet<String>();
	protected int digitSize;
	protected int sequential;
	protected char replacingChar;

	/**
	 * By default, digitSize=3, sequential=0, replacingChar='_',
	 */
	public NameValidator() {
		this.digitSize = 3;
		this.sequential = 1;
		this.replacingChar = '_';
	}

	/**
	 * Given a variable name, this methods returns a valid SAS name and it guarantees the uniqueness of this name
	 * 
	 * @param variableName
	 *            String
	 * @return String
	 */
	public abstract String getValidName(String variableName);

	protected abstract boolean isValid(char c);

	protected abstract String getNextSequentialString(int maxValue);

	public TreeSet<String> getUniqueNameTable() {
		return uniqueNameTable;
	}

	public void setUniqueNameTable(TreeSet<String> uniqueNameTable) {
		this.uniqueNameTable = uniqueNameTable;
	}

	public int getDigitSize() {
		return digitSize;
	}

	public void setDigitSize(int digitSize) {
		this.digitSize = digitSize;
	}

	public int getSequential() {
		return sequential;
	}

	public void setSequential(int sequential) {
		this.sequential = sequential;
	}

	public char getReplacingChar() {
		return replacingChar;
	}

	public void setReplacingChar(char replacingChar) {
		this.replacingChar = replacingChar;
	}
}
