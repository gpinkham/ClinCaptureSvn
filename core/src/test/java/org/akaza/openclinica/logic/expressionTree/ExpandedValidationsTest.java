package org.akaza.openclinica.logic.expressionTree;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.junit.Before;
import org.junit.Test;

public class ExpandedValidationsTest {

	private OpenClinicaExpressionParser expressionParser = null;

	@Before
	public void setUp() throws OpenClinicaSystemException {
		
		expressionParser = new OpenClinicaExpressionParser();
		expressionParser.setSubjectDob(new Date());
		expressionParser.setSubjectEnrollment(new Date());
	}

	@Test
	public void testThatNotEqualExpressionOnSubjectEnrollmentEvaluatesCorrectlyOnAnotherDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_SUBJECT_ENROLLMENT ne 2008-10-03"));
	}

	@Test
	public void testThatEqualsExpressionOnSubjectEnrollmentVariableEvaluatesCorrectlyOnSubjectEnrollmentVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_SUBJECT_ENROLLMENT eq _SUBJECT_ENROLLMENT"));
	}

	@Test
	public void testThatLessThanExpressionOnDateEvaluatesCorrectlyOnSubjectEnrollmentVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 lt _SUBJECT_ENROLLMENT"));
	}

	@Test
	public void testThatGreaterThanExpressionOnDateEvaluatesCorrectOnSubjectEnrollmentVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2024-12-05 gt _SUBJECT_ENROLLMENT"));
	}

	@Test
	public void testThatNotEqualsExpressionEvaluatesSubjectDOBVariableCorrectlyOnAnotherDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_SUBJECT_DOB ne 2009-12-03"));
	}

	@Test
	public void testThatEqualsOnSubjectDOBEvaluatesCorrectlyWhenTestedOnSubjectDOBVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_SUBJECT_DOB eq _SUBJECT_DOB"));
	}

	@Test
	public void testThatLessThanExpressionWorksCorrectOnSubjectDOBVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 lt _SUBJECT_DOB"));
	}

	@Test
	public void testThatGreaterThanExpressionExecutesCorrectlyOnSubjectDOBVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2024-12-05 gt _SUBJECT_DOB"));
	}

	@Test
	public void testThatNotEqualOnCurrentDateExecutesCorrectOnAnotherDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_CURRENT_DATE ne 2008-12-03"));
	}

	@Test
	public void testThatEqualExpressionOnCurrentDateExecuteCorrectlyOnCurrentDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("_CURRENT_DATE eq _CURRENT_DATE"));
	}

	@Test
	public void testThatLessThanExpressionExecuteCorrectlyOnCurrentDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 lt _CURRENT_DATE"));
	}

	@Test
	public void testThatGreaterThanlExpressionExecuteCorrectlyOnCurrentDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2024-12-05 gt _CURRENT_DATE"));
	}

	@Test
	public void testThatNotEqualExpressionExecuteCorrectlyOnCurrentDateVariable() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 ne 2009-12-03"));
	}

	@Test
	public void testThatEqualsExpressionExecuteCorrectOnDates() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 eq 2008-12-03"));
	}

	@Test
	public void testThatLessThanExpressionExecuteCorrectOnDates() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-03 lt 2009-12-04"));
	}

	@Test
	public void testThatGreaterThanExpressionExecuteCorrectOnDates() {
		assertEquals("The result should be true", "true",
				expressionParser.parseAndTestEvaluateExpression("2008-12-05 gt 2007-12-04"));
	}
}
