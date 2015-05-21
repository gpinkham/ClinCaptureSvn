/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class OpenClinicaExpressionParserTest extends TestCase {

	@Test
	public void testNumberGreaterThanNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("4 gt 3");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testNumberGreaterThanNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 gt 4");		
		assertEquals("The result should be false", "false", result);
	}
	
	@Test
	public void testNumberLessThanNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2 lt 3");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testNumberLessThanNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("5 lt 4");		
		assertEquals("The result should be false", "false", result);
	}
	
	@Test
	public void testNumberGreaterThanOrEqualToNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("4 gte 4");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testNumberGreaterThanOrEqualToNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("4 gte 5");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testNumberLessThanOrEqualToNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 lte 3");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testNumberLessThanOrEqualToNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 lte 2");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testNumberEqualToNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("4 eq 4");		
		assertEquals("The result should be true", "true", result);
	}

	@Test
	public void testNumberEqualToNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("4 eq 3");		
		assertEquals("The result should be false", "false", result);
	}
	
	@Test
	public void testNumberNotEqualToNumberReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 ne 6");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testNumberNotEqualToNumberReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 ne 3");		
		assertEquals("The result should be false", "false", result);
	}
	
	@Test
	public void testNumberPlusNumberInEvaluateExpression() throws OpenClinicaSystemException{

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 + 3");	
		assertEquals("The result should be 6.0", "6.0", result);
	}
	
	@Test
	public void testNumberMinusNumberInEvaluateExpression() throws OpenClinicaSystemException{

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("7 - 3");	
		assertEquals("The result should be 4.0", "4.0", result);
	}
	
	@Test
	public void testNumberMultiplyByNumberInEvaluateExpression() throws OpenClinicaSystemException{

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("3 * 3");	
		assertEquals("The result should be 9.0", "9.0", result);
	}
	
	@Test
	public void testNumberDivideByNumberInEvaluateExpression() throws OpenClinicaSystemException{

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("12 / 3");	
		assertEquals("The result should be 4.0", "4.0", result);
	}

	@Test
	public void testAndReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("(4 gt 3) and (3 gt 0)");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testAndReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("(4 gt 3) and (3 gt 4)");		
		assertEquals("The result should be false", "false", result);
	}
	
	@Test
	public void testOrReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("(4 gt 5) or (3 gt 0)");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testOrReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException{
		
		String expression = "(4 gt 5) or (3 gt 4)";
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression(expression);
		
		assertEquals("The result should be false", "false", result);
	}

	@Test
	public void testContainsReturnsTrueInParseAndTestEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("455896 ct 5");
		assertEquals("The result should be true", "true", result);
		
	}
	
	@Test
	public void testContainsReturnsFalseInParseAndTestEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("455896 ct 7");
		assertEquals("The result should be true", "false", result);
		
	}
	
	@Test
	public void testNotContainsReturnsTrueInParseAndTestEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("455896 nct 7");
		assertEquals("The result should be true", "true", result);		
	}
	
	@Test
	public void testNotContainsReturnsFalseInParseAndTestEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("455896 nct 5");
		assertEquals("The result should be true", "false", result);		
	}
	
	@Test
	public void testDatePlusNumberInDateArithmeticEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2008-02-02 + 30");		
		assertEquals("The result should be 2008-03-03", "2008-03-03", result);
	}
	
	@Test
	public void testNumberPlusDateInDateArithmeticEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("30 + 2008-02-02");		
		assertEquals("The result should be 2008-03-03", "2008-03-03", result);
	}
	
	@Test
	public void testDateMinusNumberInDateArithmeticEvaluateExpression() throws OpenClinicaSystemException{
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 - 20");		
		assertEquals("The result should be 2008-12-26", "2008-12-26", result);
	}
	
	@Test
	public void testDateMinusDateInDateArithmeticEvaluateExpression() throws OpenClinicaSystemException{
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2010-01-15 - 2009-01-15");		
		assertEquals("The result should be 365", "365", result);
	}
	
	@Test
	public void testDateGreaterThanDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 gt 2008-12-02");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateGreaterThanDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 gt 2009-12-02");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDateLessThanDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 lt 2009-12-02");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateLessThanDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 lt 2008-12-02");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDateGreaterThanOrEqualToDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 gte 2009-01-15");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateGreaterThanOrEqualToDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 gte 2009-12-02");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDateLessThanOrEqualToDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 lte 2009-01-15");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateLessThanOrEqualDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 lte 2008-12-02");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDateEqualToDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 eq 2009-01-15");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateEqualToDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 eq 2009-12-02");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDateNotEqualToDateReturnsTrueInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 ne 2008-12-02");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testDateNotEqualDateReturnsFalseInEvaluateExpression() throws OpenClinicaSystemException {

		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("2009-01-15 ne 2009-01-15");		
		assertEquals("The result should be true", "false", result);
	}
	
	@Test
	public void testDatePlusStringThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 + \"kk\"");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDatePlusDateThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 + 2008-12-04");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDateMultiplyByDateThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 * 2008-12-04");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDateMultiplyByNumberThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 * 2");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDateDivideByDateThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 / 2008-12-04");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDateDivideByNumberThrowsOpenClinicaSystemExceptionWithErrorCode0001(){
		
		OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
		try {
			expressionParserA.parseAndTestEvaluateExpression("2008-12-03 / 2");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0001", e.getErrorCode());
		}
	}
	
	@Test
	public void testDatePlusStringWithSyntaxErrorThowsOpenClinicaSystemExceptionWithErrorCode0005(){
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		try {
			expressionParser.parseAndTestEvaluateExpression("2008-12-03 + \"kk gt 2008-12-04");
			assertFalse(true);
		} catch (OpenClinicaSystemException e) {
			assertEquals("OCRERR_0005", e.getErrorCode());
		}
	}
	
	@Test
	public void testOperatorPrecedencePlusMinusMultiplyDivideInEvaluateExpression() throws OpenClinicaSystemException {
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("14 + 12 - 3 *8 / 2");		
		assertEquals("The result should be true", "14.0", result);
	}
	
	@Test
	public void testOperatorPrecedenceArithmeticComparisonArithmeticInEvaluateExpression() throws OpenClinicaSystemException {
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
		String result = expressionParser.parseAndTestEvaluateExpression("(14 + 12 - 3 *8 / 2) eq (7 * 2)");		
		assertEquals("The result should be true", "true", result);
	}
	
	@Test
	public void testCurrentDateInEvaluateExpression() throws OpenClinicaSystemException {
		DateMidnight dm = new DateMidnight();
		DateTimeFormatter fmt = ISODateTimeFormat.date();

		String current_date = fmt.print(dm);
		String expression = "_CURRENT_DATE eq " + current_date;
		
		OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();

		String result = expressionParser.parseAndEvaluateExpression(expression);

		assertEquals("The result should be true", "true", result);
	}
}
