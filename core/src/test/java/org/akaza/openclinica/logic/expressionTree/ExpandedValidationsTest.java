package org.akaza.openclinica.logic.expressionTree;

import junit.framework.TestCase;
import org.akaza.openclinica.exception.OpenClinicaSystemException;

import java.util.Date;

public class ExpandedValidationsTest extends TestCase {

    public void testAll() throws OpenClinicaSystemException {
        assertEquals("The result should be true", "true", testExpression("2008-12-05 gt 2007-12-04"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 lt 2009-12-04"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 eq 2008-12-03"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 ne 2009-12-03"));
        assertEquals("The result should be true", "true", testExpression("2024-12-05 gt _CURRENT_DATE"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 lt _CURRENT_DATE"));
        assertEquals("The result should be true", "true", testExpression("_CURRENT_DATE eq _CURRENT_DATE"));
        assertEquals("The result should be true", "true", testExpression("_CURRENT_DATE ne 2008-12-03"));
        assertEquals("The result should be true", "true", testExpression("2024-12-05 gt _SUBJECT_DOB"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 lt _SUBJECT_DOB"));
        assertEquals("The result should be true", "true", testExpression("_SUBJECT_DOB eq _SUBJECT_DOB"));
        assertEquals("The result should be true", "true", testExpression("_SUBJECT_DOB ne 2009-12-03"));
        assertEquals("The result should be true", "true", testExpression("2024-12-05 gt _SUBJECT_ENROLLMENT"));
        assertEquals("The result should be true", "true", testExpression("2008-12-03 lt _SUBJECT_ENROLLMENT"));
        assertEquals("The result should be true", "true", testExpression("_SUBJECT_ENROLLMENT eq _SUBJECT_ENROLLMENT"));
        assertEquals("The result should be true", "true", testExpression("_SUBJECT_ENROLLMENT ne 2008-10-03"));
    }

    private String testExpression(String expression) throws OpenClinicaSystemException {
        OpenClinicaExpressionParser expressionParser = new OpenClinicaExpressionParser();
        expressionParser.setSubjectDob(new Date());
        expressionParser.setSubjectEnrollment(new Date());
        return expressionParser.parseAndTestEvaluateExpression(expression);
    }
}
