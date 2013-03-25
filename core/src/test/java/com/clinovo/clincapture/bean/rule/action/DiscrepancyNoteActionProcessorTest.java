/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo.clincapture.bean.rule.action;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.action.DiscrepancyNoteActionProcessor;
import org.akaza.openclinica.bean.rule.action.RuleActionBean;
import org.akaza.openclinica.service.managestudy.IDiscrepancyNoteService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Pavel Date: 25.10.12
 */
public class DiscrepancyNoteActionProcessorTest {

	private Mockery context = new Mockery();
	private IDiscrepancyNoteService noteService;

	private DiscrepancyNoteActionProcessor actionProcessor;

	private static final int ITEM_DATA_BEAN_ID = 2;
	private static final String DESCRIPTION = "description";
	private static final String ITEM_DATA = "item data";

	private StudyBean studyBean;
	private UserAccountBean accountBean;
	private RuleActionBean ruleAction;

	@Before
	public void setUp() throws Exception {
		noteService = context.mock(IDiscrepancyNoteService.class);

		studyBean = new StudyBean();
		accountBean = new UserAccountBean();
		ruleAction = new RuleActionBean();
		ruleAction.setCuratedMessage(DESCRIPTION);

		actionProcessor = new DiscrepancyNoteActionProcessor(null);
		actionProcessor.setDiscrepancyNoteService(noteService);

	}

	@Test
	public void testExecute() {

		context.checking(new Expectations() {
			{
				one(noteService).saveFieldNotes(DESCRIPTION, ITEM_DATA_BEAN_ID, ITEM_DATA, studyBean, accountBean);
			}
		});

		actionProcessor.execute(ruleAction, ITEM_DATA_BEAN_ID, ITEM_DATA, studyBean, accountBean);

		context.assertIsSatisfied();
	}
}
