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

package org.akaza.openclinica.service.rule.expression;

import junit.framework.TestCase;

public class ExpressionServiceTest extends TestCase {

	public void testStatement() {

		org.apache.commons.dbcp.BasicDataSource ds = new org.apache.commons.dbcp.BasicDataSource();
		ExpressionService expressionService = new ExpressionService(ds);

		// Syntax
		assertEquals(false,
				expressionService.checkSyntax("StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
		assertEquals(false,
				expressionService.checkSyntax(".StudyEventName[ALL].FormName1.ItemGroupName[ALL].ItemName11."));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]..ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].ITEM_GROUP_OID[ALL]..ITEM_OID_11"));

		// STUDY_EVENT_DEFINITION_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[10].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[1].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[10004].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY12_EVENT_OID_12[123].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID.FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[Krikor].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[KK].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVE[NT_OID[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("[].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("[.FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID$[12].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[123].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

		// CRF_OID or CRF_VERSION_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID_123.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM123_2__OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[ALL].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID[].ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID].ITEM_GROUP_OID[ALL].ITEM_OID_11"));

		// ITEM_GROUP_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[23].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[2].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[100].ITEM_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID.ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID__[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID_123[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_ITEM_GROUP_12_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.12_iTEM_GROUP_12_OID[ALL].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID].ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[.ITEM_OID_11"));
		assertEquals(false, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALLL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]$.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL]_KK.ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[0].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[0].ITEM_OID_11"));

		// ITEM_OID
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OI123D_11"));
		assertEquals(true,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITE123M_OID_11"));
		assertEquals(true, expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11["));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11]"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11[]"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OID_11$"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM_OId_11"));
		assertEquals(false,
				expressionService.checkSyntax("STUDY_EVENT_OID[ALL].FORM_OID.ITEM_GROUP_OID[ALL].ITEM-_OID_11"));

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// MockContextFactory.revertSetAsInitial();
	}
}
