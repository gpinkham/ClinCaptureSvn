/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.http;

import static org.junit.Assert.assertNotNull;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;

import com.clinovo.BaseTest;

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
