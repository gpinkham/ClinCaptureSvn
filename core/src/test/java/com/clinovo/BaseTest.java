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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientMock;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.w3c.dom.Document;

import com.clinovo.model.Randomization;

public class BaseTest {

	// Web service JSON return data
	protected static JSONObject randomizationResult;
	protected static JSONObject authenticationToken;
	
	// XML return data
	protected static Document xmlRandomiationResult;
	protected static Document randomizationDef;

	@BeforeClass
	public static void initialize() throws Exception {

		randomizationDef = readFile("src/test/resources/RandomizationRule.xml");
		xmlRandomiationResult = readFile("src/test/resources/XMLRandomizationResult.xml");
	}

	private static Document readFile(String fileName) throws Exception {

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
	public static void createJSONRandomizationResult() throws Exception {

		randomizationResult = new JSONObject();
		randomizationResult.put("PatientID", "abc123");
		randomizationResult.put("RandomizationResult", "radiotherapy");
		randomizationResult.put("TreatmentID", "3");
		
	}

	protected Randomization createRandomization() {

		Randomization randomization = new Randomization();
		
		randomization.setUsername(randomizationDef.getElementsByTagName("username").item(0).getTextContent());
		randomization.setPassword(randomizationDef.getElementsByTagName("password").item(0).getTextContent());
		randomization.setTrialId(randomizationDef.getElementsByTagName("trialid").item(0).getTextContent());
		randomization.setSiteId(randomizationDef.getElementsByTagName("siteid").item(0).getTextContent());
		randomization.setPatientId(randomizationDef.getElementsByTagName("patientid").item(0)
				.getTextContent());
		randomization.setRandomizationUrl(randomizationDef.getElementsByTagName("randomizationurl").item(0)
				.getTextContent());
		randomization.setAuthenticationUrl(randomizationDef.getElementsByTagName("authenticationurl").item(0)
				.getTextContent());

		return randomization;
	}
	
	protected HttpClient createMockHttpClient(String response, int status) throws Exception {

		HttpClient client = new HttpClientMock(status, response);

		return client;
	}
}