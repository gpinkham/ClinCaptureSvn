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

import org.akaza.openclinica.bean.rule.action.EmailActionBean;
import org.akaza.openclinica.bean.rule.action.EmailActionProcessor;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.IEmailEngine;
import org.akaza.openclinica.dao.core.CoreResources;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: Pavel Date: 25.10.12
 */
public class EmailActionProcessorTest {

	private Mockery context = new Mockery();
	private IEmailEngine emailEngine;

	private EmailActionProcessor actionProcessor;

	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String SUBJECT = "subject";
	private static final String BODY = "email body";

	private static final String SUBJECT_KEY = "subject";
	private static final String BODY_KEY = "body";

	EmailActionBean actionBean;
	Map<String, String> bodyAndSubject;

	@Before
	public void setUp() throws Exception {
		emailEngine = context.mock(IEmailEngine.class);

		actionBean = new EmailActionBean();
		actionBean.setTo(TO);

		actionProcessor = new EmailActionProcessor(null);
		actionProcessor.setEmailEngine(emailEngine);

		CoreResources coreResource = new CoreResources();
		Properties properties = new Properties();
		properties.put("adminEmail", FROM);
		coreResource.setDataInfo(properties);

		bodyAndSubject = new HashMap<String, String>();
		bodyAndSubject.put(SUBJECT_KEY, SUBJECT);
		bodyAndSubject.put(BODY_KEY, BODY);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testExecute() throws MessagingException {
		context.checking(new Expectations() {
			{
				one(emailEngine).process(TO, EmailEngine.getAdminEmail(), SUBJECT, BODY);
			}
		});

		actionProcessor.execute(actionBean, 0, null, null, null, bodyAndSubject);
	}
}
