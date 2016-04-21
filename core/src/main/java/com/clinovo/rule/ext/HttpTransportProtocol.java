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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rule.ext;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.model.RandomizationResult;

/**
 * Handles the http transport for all randomization calls to the randomization end-point. 
 * <p>
 * This calls assumes that the call has provided valid details required by the end-point that is being connected to. No guarantees are made about success.
 * The connection assumes the presence of an https protocol
 *
 */
public class HttpTransportProtocol implements Callable<RandomizationResult> {

	private HttpClient client = null;
	private SubmissionContext context;
	private PostMethod method = new PostMethod();

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Initiates the randomization call to the specified randomization end-point.
	 */
	public RandomizationResult call() throws Exception {

		log.info("Initiating call to web service");

		if (context == null)
			throw new WebServiceException("Randomization cannot be null or empty");

		if (client == null)
			client = new HttpClient();

		RandomizationResult result = new RandomizationResult();

		try {

			for (Header header : context.getHttpHeaders()) {

				method.addRequestHeader(header);
			}

			ProtocolSocketFactory sslFactory = new SSLProtocolSocketFactory();
			Protocol.registerProtocol("https", new Protocol("https", sslFactory, 443));

			HttpsURL url = new HttpsURL(context.getRandomization().getRandomizationUrl());
			method.setURI(url);
			method.setRequestEntity(context.getRequestEntity());

			int status = client.executeMethod(method);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] byteArray = new byte[1024];
			int count = 0;
			while ((count = method.getResponseBodyAsStream().read(byteArray, 0, byteArray.length)) > 0) {
				outputStream.write(byteArray, 0, count);
			}
			result = context.processResponse(new String(outputStream.toByteArray(), "UTF-8"), status);

		} catch (Exception ex) {

			log.error(ex.getMessage());
			throw new WebServiceException(ex);

		} finally {
			method.releaseConnection();
		}

		return result;
	}

	public void setHttpClient(HttpClient client) {
		this.client = client;
	}

	public void setSubmissionContext(SubmissionContext context) {
		this.context = context;
	}
}
