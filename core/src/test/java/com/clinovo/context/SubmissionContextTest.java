package com.clinovo.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.rule.WebServiceAction;

public class SubmissionContextTest extends BaseTest {

	private PostMethod method;
	private SubmissionContext context;

	@Before
	public void setUp() throws Exception {

		context = new JSONSubmissionContext();

		WebServiceAction action = createWebServiceAction();
		
		context.setAction(action);
		
		method = createPostMethodMock(context.getAction().getAuthenticationUrl(), authenticationToken.toString());
		
		context.setHttpMethod(method);

	}
	
	@Test
	public void testThatAuthenticateDoesNotReturnNull() throws Exception {

		assertNotNull("Should never return null", context.authenticate(context.getAction()));
	}

	@Test
	public void testThatAuthenticateReturnsValidAuthToken() throws Exception {

		JSONObject token = new JSONObject(context.authenticate(context.getAction()));

		assertEquals("The auth token should be equal the expected type", "36055d77-5f0c-4121-bd87-bb8fb6132605",
				token.get("Token"));

	}
}
