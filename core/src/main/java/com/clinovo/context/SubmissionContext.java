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
package com.clinovo.context;

import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;

/**
 * Specifies a contract between a randomization specific type and the expected processing context. 
 * 
 * The implementing object should be context aware and provide an eco-system for the returned data to be processed to
 * the expected standard/format.
 *
 */
public interface SubmissionContext {

	/**
	 * Retrieves the randomization configuration used for this session.
	 * 
	 * @return Valid randomization configuration
	 */
	Randomization getRandomization();

	/**
	 * Sets the http client to use for a given randomization session. This can be switched depending on the expected return type -
	 * 
	 * @param client client to use
	 */
	void setHttpClient(HttpClient client);

	/**
	 * Authenticates the given credentials against randomize.net
	 * 
	 * @return A string (based on expected format) indicating the response. The client is supposed to process this according to what it expects.
	 * 
	 * @throws Exception - If and when the randomization falls over
	 */
	String authenticate() throws Exception;

	void setRandomization(Randomization randomization);

	/**
	 * Retrieves the required http headers for a given randomization session. These may include the authentication token or site to use.
	 * 
	 * @return A list of http headers that can be used in a given randomization session.
	 * 
	 * @throws Exception For failed authentication or Parsing issues
	 */
	List<Header> getHttpHeaders() throws Exception;

	/**
	 * Get the request entity that will be used to pass data to the randomization end point.
	 * 
	 * @return A valid request entity configured for this session
	 * 
	 * @throws Exception Any error that might arise during the creation of the request entity like Parsing errors
	 */
	RequestEntity getRequestEntity() throws Exception;

	/**
	 * Processes the returned response from the randomization end-point.
	 * 
	 * @param response The response to process
	 * @param httpStatus The http status from the randomization end-point
	 * 
	 * @return A valid Randomization Result
	 * 
	 * @throws Exception For any error that occurs during processing
	 */
	RandomizationResult processResponse(String response, int httpStatus) throws Exception;

}
