package com.clinovo.http;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;

public class HttpTransportTest extends BaseTest {

	HttpTransport transport = new HttpTransport();
	
	@Before
	public void setUp() {

        HttpMethod method = new GetMethod("http://1.1.1.1");

        method.setPath("/search");
        method.setQueryString(new NameValuePair[] {

                new NameValuePair("q", "term"), new NameValuePair("ontologies",  "dictionary"),
                new NameValuePair("apikey", "api key")

        });

		transport.setMethod(method);
		transport.setClient(stubHttpClient(200, searchResult.toString()));
	}

	@Test
	public void testThatGetResultsDoesNotReturnNull() throws Exception {

		assertNotNull(transport.processRequest());
	}
	
	@Test(expected=HttpTransportException.class)
	public void testThatGetResultThrowsErrorOnBadRequestHttpStatus() throws Exception {
		
		transport.setClient(stubHttpClient(400, searchResult.toString()));
		transport.processRequest();
	}
	
	@Test(expected=HttpTransportException.class)
	public void testThatGetResultThrowsErrorOnUnAvailableHttpStatus() throws Exception {
		
		transport.setClient(stubHttpClient(503, searchResult.toString()));
		transport.processRequest();
	}
}
