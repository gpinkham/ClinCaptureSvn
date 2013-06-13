package com.clinovo.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.model.Randomization;

public class SubmissionContextTest extends BaseTest {

	private HttpClient client;
	private SubmissionContext context;

	@Before
	public void setUp() throws Exception {

		context = new JSONSubmissionContext();

		Randomization randomization = createRandomization();
		
		context.setRandomization(randomization);
		
		client = createMockHttpClient(authenticationToken.toString(), HttpStatus.SC_OK);
		
		context.setHttpClient(client);

	}
	
	@Test
	public void testThatAuthenticateDoesNotReturnNull() throws Exception {

		assertNotNull("Should never return null", context.authenticate());
	}

	@Test
	public void testThatAuthenticateReturnsValidAuthToken() throws Exception {

		JSONObject token = new JSONObject(context.authenticate());

		assertEquals("The auth token should be equal the expected type", "\"36055d77-5f0c-4121-bd87-bb8fb6132605\"",
				token.getString("Token").replaceAll("]|\\[", ""));

	}
}
