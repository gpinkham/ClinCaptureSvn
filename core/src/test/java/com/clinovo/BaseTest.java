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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientMock;
import org.json.JSONObject;
import org.junit.BeforeClass;

import com.clinovo.model.Randomization;

public class BaseTest {

	// Web service JSON return data
	protected static JSONObject randomizationResult;
	protected static JSONObject authenticationToken;
	
	@BeforeClass
	public static void createAuthToken() throws Exception {

		authenticationToken = new JSONObject();
		authenticationToken.append("Token", "36055d77-5f0c-4121-bd87-bb8fb6132605");

	}
	
	public Randomization createRandomization() {
		
		Randomization randomization = new Randomization();
		
		randomization.setInitials("BB");
		randomization.setTrialId("0001");
		randomization.setPatientId("SS_BB");
		randomization.setSiteId("clinovotest");
		randomization.setAuthenticationUrl("https://someurl.randomization.bb");
		randomization.setRandomizationUrl("https://someurl.randomization.bb");
		
		return randomization;
	}
	
	@BeforeClass
	public static void createJSONRandomizationResult() throws Exception {

		randomizationResult = new JSONObject();
		randomizationResult.put("PatientID", "abc123");
		randomizationResult.put("RandomizationResult", "radiotherapy");
		randomizationResult.put("TreatmentID", "3");
		
	}
	
	protected HttpClient createMockHttpClient(String response, int status) throws Exception {

		return new HttpClientMock(status, response);

	}
}