package com.clinovo.rule.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;
import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;

public class HttpTransportProtocolTest extends BaseTest {

	private HttpClient client;
	private SubmissionContext context;
	private HttpTransportProtocol protocol = null;

	@Before
	public void setUp() throws Exception {
		
		Randomization randomization = createRandomization();
		
		context = new JSONSubmissionContext();
		
		HttpClient authClient = createMockHttpClient(authenticationToken.toString(), HttpStatus.SC_OK);
		context.setHttpClient(authClient);
		
		context.setRandomization(randomization);
		
		client = createMockHttpClient("{TreatmentID:\"some-treatment\", PatientID: \"subject2\",RandomizationResult:\"some-result\"}", HttpStatus.SC_OK);

		protocol = new HttpTransportProtocol();
		protocol.setHttpClient(client);

		context.setRandomization(randomization);
		protocol.setSubmissionContext(context);
		
	}

	@Test(expected = WebServiceException.class)
	public void testThatCallThrowsExceptionWhenCalledWithInvalidInput() throws Exception {

		protocol.setSubmissionContext(null);
		protocol.call();
	}

	@Test(expected = WebServiceException.class)
	public void testThatFailedHttpCallRaisesWebServiceException() throws Exception {

		String failureMessage = "{message:Respect other people's security you tard}";
		HttpClient client = createMockHttpClient(failureMessage, HttpStatus.SC_FORBIDDEN);
		
		protocol.setHttpClient(client);
		
		protocol.call();
	}

	@Test(expected = WebServiceException.class)
	public void testThatUnVailableHttpReturnCodeHttpCallRaisesWebServiceException() throws Exception {
		
		String failureMessage = "{message:Respect other people's security you tard}";
		
		HttpClient client = createMockHttpClient(failureMessage, HttpStatus.SC_SERVICE_UNAVAILABLE);
		protocol.setHttpClient(client);

		protocol.call();
	}

	@Test
	public void testThatCallDoesNotReturnNull() throws Exception {

		RandomizationResult result = protocol.call();
		assertNotNull("Should never return null", result);
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithTreatment() throws Exception {

		RandomizationResult result = protocol.call();

		assertNotNull("Should have a valid Treatment specified", result.getTreatment());
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectTreatment() throws Exception {

		RandomizationResult result = protocol.call();

		assertEquals("Should have a correct Treatment specified", "some-treatment", result.getTreatment());
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithPatientId() throws Exception {

		RandomizationResult result = protocol.call();

		assertNotNull("Should have a valid patient Id specified", result.getPatientId());
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectPatientId() throws Exception {

		RandomizationResult result = protocol.call();

		assertEquals("Should have a correct patientId specified", "subject2", result.getPatientId());
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithSiteId() throws Exception {

		RandomizationResult result = protocol.call();

		assertNotNull("Should have a valid Randomization result specified", result.getRandomizationResult());
	}

	@Test
	public void testThatCallReturnsValidWebServiceResultWithACorrectSiteId() throws Exception {

		RandomizationResult result = protocol.call();

		assertEquals("Should have a correct Randomization result specified", "some-result",
				result.getRandomizationResult());
	}
	
	/**
	 * Adding a test here to profile the type of rando response which does not have a rando ID
	 * @throws Exception
	 */
	@Test
	public void testThatCallReturnsNoRandomizationIDWithACorrectSiteId() throws Exception {

		RandomizationResult result = protocol.call();

		assertEquals("Does not necessarily have to have a Randomization ID", "", result.getRandomizationID());
	}
}
