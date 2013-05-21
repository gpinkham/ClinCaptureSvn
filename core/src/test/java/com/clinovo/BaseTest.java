/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.clinovo;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.auth.AuthState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.w3c.dom.Document;

import com.clinovo.rule.WebServiceAction;

public class BaseTest {

	// Web service JSON return data
	protected static JSONObject jsonReturnedData;
	protected static JSONObject authenticationToken;
	
	// XML return data
	protected static Document webServiceReturnValue;
	protected static Document webServiceActionDefinition;

	@BeforeClass
	public static void initialize() throws Exception {

		webServiceActionDefinition = readTestXMLFile("src/test/resources/WSActionRule.xml");
		webServiceReturnValue = readTestXMLFile("src/test/resources/WebServiceReturnValue.xml");
	}

	private static Document readTestXMLFile(String fileName) throws Exception {

		File fXmlFile = new File(fileName);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		return dBuilder.parse(fXmlFile);
	}

	@BeforeClass
	public static void createAuthToken() throws Exception {

		authenticationToken = new JSONObject();
		authenticationToken.append("Token", "36055d77-5f0c-4121-bd87-bb8fb6132605");

	}
	
	@BeforeClass
	public static void createJSONReturnData() throws Exception {

		jsonReturnedData = new JSONObject();
		jsonReturnedData.put("PatientID", "abc123");
		jsonReturnedData.put("RandomizationResult", "radiotherapy");
		jsonReturnedData.put("TreatmentID", "3");
		
	}

	protected WebServiceAction createWebServiceAction() {

		WebServiceAction action = new WebServiceAction();
		
		action.setUsername(webServiceActionDefinition.getElementsByTagName("username").item(0).getTextContent());
		action.setPassword(webServiceActionDefinition.getElementsByTagName("password").item(0).getTextContent());
		action.setTrialId(webServiceActionDefinition.getElementsByTagName("trialid").item(0).getTextContent());
		action.setSiteId(webServiceActionDefinition.getElementsByTagName("siteid").item(0).getTextContent());
		action.setPatientId(webServiceActionDefinition.getElementsByTagName("patientid").item(0)
				.getTextContent());
		action.setRandomizationUrl(webServiceActionDefinition.getElementsByTagName("randomizationurl").item(0)
				.getTextContent());
		action.setAuthenticationUrl(webServiceActionDefinition.getElementsByTagName("authenticationurl").item(0)
				.getTextContent());

		return action;
	}
	
	protected PostMethod createPostMethodMock(String url, String response) throws Exception {

		PostMethod method = Mockito.mock(PostMethod.class);

		// Expectations
		Mockito.when(method.getURI()).thenReturn(new URI(url, false));
		Mockito.when(method.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		Mockito.when(method.getResponseBodyAsString()).thenReturn(response);
		Mockito.when(method.getParams()).thenReturn(new HttpMethodParams());
		Mockito.when(method.getHostAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getProxyAuthState()).thenReturn(new AuthState());
		Mockito.when(method.getRequestHeaders(Mockito.anyString())).thenReturn(new Header[0]);

		return method;
	}
}