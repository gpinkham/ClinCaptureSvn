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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.logic.score;

public class ScoreSymbol {
	/**
	 * TERM_SYMBOL is for all of arguments, variables, values.
	 */
	public static final char TERM_SYMBOL = 'T';
	public static final char FUNCTION_SYMBOL = 'F';
	public static final char ARGUMENT_SYMBOL = 'A';
	public static final char VARIABLE_SYMBOL = 'V';
	public static final char OPEN_PARENTH_SYMBOL = '(';
	public static final char CLOSE_PARENTH_SYMBOL = ')';
	public static final char ARITHMETIC_OPERATOR_SYMBOL = 'O';
	public static final char COMMA_SYMBOL = ',';
}
