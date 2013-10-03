/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.context.impl;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.model.Randomization;

public abstract class DefaultSubmissionContext implements SubmissionContext {

	protected PostMethod method = null;
	protected Randomization randomization;
	protected String currentAuthToken = null;
	protected HttpClient client = new HttpClient();
	public static final int DUPLICATION_RANDOMIZATION = 510;

	protected final Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * Constructs the body of the randomization authentication object.
	 * 
	 * @return A string to pass over the wire to the randomization end-point for authentication
	 * 
	 * @throws Exception For Parse errors
	 */
	abstract String getBody() throws Exception;

	public String authenticate() throws Exception {

		// Re-use token if existing
		if (currentAuthToken != null) {
			return currentAuthToken;
		}
		
		method = new PostMethod(randomization.getAuthenticationUrl());
		
		// Allow for testing
		if (client == null)
			client = new HttpClient();

		Header contentTypeHeader = new Header();
		contentTypeHeader.setName("Content-Type");
		contentTypeHeader.setValue("application/json");

		Header acceptHeader = new Header();
		acceptHeader.setName("Accept");
		acceptHeader.setValue("application/json");

		method.addRequestHeader(acceptHeader);
		method.addRequestHeader(contentTypeHeader);

		method.setRequestEntity(new StringRequestEntity(getBody(), "application/json", "utf-8"));

		log.info("Making randomization post request with: {} ", getBody());
		client.executeMethod(method);

		String response = method.getResponseBodyAsString();

		return response;
	}

	public void setRandomization(Randomization randomization) {
		this.randomization = randomization;
	}

	public Randomization getRandomization() {
		return this.randomization;
	}

	public void setHttpClient(HttpClient client) {

		this.client = client;
	}
}
