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

package com.clinovo.coding.source.impl;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.SearchInterface;
import com.clinovo.http.HttpTransport;
import com.clinovo.util.CodingFieldsUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The search interface implementation for working with bioportal web-services.
 */
public class BioPortalSearchInterface implements SearchInterface {

	private String dictionary = "";

	private static final String ICD9CM = "ICD9CM";
	private static final String ICD10 = "ICD10";
	private static final String CTCAE = "CTCAE";
	private static final String CTC_CODE = "http://ncicb.nci.nih.gov/xml/owl/EVS/ctcae.owl#code";
	private static final int THREAD_LIVE_TIME = 30;

	/**
	 * Returns the list of terms from bioportal.
	 *
	 * @param term              the term for search.
	 * @param termDictionary    the term dictionary.
	 * @param bioontologyUrl    the bioportal web-services url.
	 * @param bioontologyApiKey the bioportal web-services unique key.
	 * @return the list of term.
	 * @throws Exception For all exceptions
	 */
	public List<Classification> search(String term, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		Logger logger = LoggerFactory.getLogger(getClass().getName());
		dictionary = getDictionary(termDictionary);
		String gradeNumber = dictionary.equals(CTCAE) && term.contains("(") && term.contains(")")
				? term.substring(term.indexOf("(") + 1, term.indexOf(")")).toLowerCase() : "grade";
		term = dictionary.equals(CTCAE) && term.contains("(") && term.contains(")")
				? term.substring(0, term.indexOf("(")) : term;

		List<Classification> classifications = new ArrayList<Classification>();

		String responseTerms = termListRequest(term, bioontologyUrl, bioontologyApiKey);
		JsonArray termListArray = new JsonParser().parse(responseTerms).getAsJsonObject().getAsJsonArray("collection");

		logger.info("Search term returns: " + termListArray.size() + " items");

		for (int i = 0; i < termListArray.size(); i++) {
			JsonObject jsonObjectElement = termListArray.get(i).getAsJsonObject();
			String codeHttpPath = jsonObjectElement.get("@id").getAsString();
			String prefLabel = jsonObjectElement.get("prefLabel").getAsString();
			boolean isPrefLabel = true;
			if (dictionary.equals(CTCAE)) {
				isPrefLabel = prefLabel.toLowerCase().contains(gradeNumber);
			}
			if (isPrefLabel) {
				List<Classification> classificationResponse = new ArrayList<Classification>();
				CodingFieldsUtil.firstResponse(classificationResponse, dictionary, prefLabel, codeHttpPath);
				classifications.addAll(classificationResponse);
			}
		}
		return classifications;
	}

	/**
	 * Returns the response from bioportal web-service as a string.
	 *
	 * @param term              the term for search.
	 * @param bioontologyUrl    the bioportal web-services url.
	 * @param bioontologyApiKey the bioportal web-services unique key.
	 * @return the response as a String
	 * @throws Exception For all exceptions.
	 */
	public String termListRequest(String term, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		HttpMethod method = new GetMethod(bioontologyUrl);

		method.setPath("/search");
		method.setQueryString(new NameValuePair[] {
				new NameValuePair("q", term), new NameValuePair("ontologies", dictionary),
				new NameValuePair("no_context", "true"),
				new NameValuePair("pagesize", "5000"),
				new NameValuePair("no_links", "true"),
				new NameValuePair("apikey", bioontologyApiKey)

		});

		HttpTransport transport = new HttpTransport();
		transport.setMethod(method);

		return transport.processRequest();
	}

	/**
	 * Gets codes from the web-service and sets them to the classification elements.
	 *
	 * @param classification    the classification element with term names
	 * @param termDictionary    the classification dictionary.
	 * @param bioontologyUrl    the bioportal web-services url.
	 * @param bioontologyApiKey the bioportal web-services unique key.
	 * @throws Exception For all exceptions.
	 */
	public void getClassificationCodes(Classification classification, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		Logger logger = LoggerFactory.getLogger(getClass().getName());
		dictionary = getDictionary(termDictionary);
		ExecutorService service = Executors.newFixedThreadPool(classification.getClassificationElement().size());
		for (ClassificationElement classificationElement : classification.getClassificationElement()) {
			service.submit(new SearchCodeThread(classificationElement, bioontologyUrl, bioontologyApiKey));
		}
		service.shutdown();

		try {
			service.awaitTermination(THREAD_LIVE_TIME, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Get code threads didn't finish in 30 seconds");
			throw new SearchException(e.getMessage());
		}
	}

	/**
	 * Returns classification with additional elements.
	 *
	 * @param termUrl           the term http url.
	 * @param bioontologyUrl    the bioportal web-services url.
	 * @param bioontologyApiKey the bioportal web-services unique key.
	 * @return the classification with additional classification elements.
	 * @throws Exception For all exceptions.
	 */
	public Classification getClassificationTerms(String termUrl, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		dictionary = getDictionary(termUrl);
		String treePath = bioontologyUrl + "/ontologies/" + dictionary + "/classes/" + termUrl.replace("://", "%3A%2F%2F").replaceAll("/", "%2F").replaceAll("@", "%40").replaceAll("#", "%23") + "/tree"
				+ "?no_links=true&no_context=true&api_key=" + bioontologyApiKey;

		String termTree = getPageDataRequest(treePath, bioontologyApiKey);

		Classification classification = new Classification();
		recursiveTreeResponseParser(termTree, classification);
		CodingFieldsUtil.completeClassificationNameFields(classification.getClassificationElement(), dictionary);

		return classification;
	}

	/**
	 * Returns the list of additional terms as string.
	 *
	 * @param urlPath           the term bioontology web-services http path.
	 * @param bioontologyApiKey the bioportal web-services unique key.
	 * @return the list of additional term as string.
	 * @throws Exception For all exceptions.
	 */
	public String getPageDataRequest(String urlPath, String bioontologyApiKey) throws Exception {

		HttpMethod method = new GetMethod();
		method.setPath(urlPath);
		method.setRequestHeader(new Header("Authorization", "apikey token=" + bioontologyApiKey));
		HttpTransport transport = new HttpTransport();
		transport.setMethod(method);

		return transport.processRequest();
	}

	private void recursiveTreeResponseParser(String treeResponse, Classification classification) throws SearchException {

		JsonArray jarray = new JsonParser().parse(treeResponse).getAsJsonArray();

		for (int i = 0; i < jarray.size(); i++) {

			JsonObject jsonObject = jarray.get(i).getAsJsonObject();
			JsonArray jsonArrayWithChildItem = jsonObject.getAsJsonArray("children");
			ClassificationElement classificationElement = new ClassificationElement();

			if (jsonArrayWithChildItem != null && jsonArrayWithChildItem.size() > 0) {

				String term = jsonObject.get("prefLabel").getAsString();

				if (!term.isEmpty()) {

					classificationElement.setCodeName(term);
					classification.addClassificationElement(classificationElement);
				}

				recursiveTreeResponseParser(jsonObject.get("children").toString(), classification);
			}
		}
	}

	private class SearchCodeThread extends Thread {

		private Logger logger = LoggerFactory.getLogger(getClass().getName());
		private ClassificationElement classificationElement;
		private String bioontologyUrl = "";
		private String bioontologyApiKey = "";

		SearchCodeThread(ClassificationElement classificationElement, String bioontologyUrl, String bioontologyApiKey) {

			this.classificationElement = classificationElement;
			this.bioontologyUrl = bioontologyUrl;
			this.bioontologyApiKey = bioontologyApiKey;
		}

		public void run() {
			try {
				String response = getTermCodeRequest(classificationElement, bioontologyUrl, bioontologyApiKey);
				JsonArray responseArray = new JsonParser().parse(response).getAsJsonObject().getAsJsonArray("collection");
				if (!responseArray.isJsonNull() && responseArray.size() > 0) {
					String codeValue = "";
					if (responseArray.get(0).getAsJsonObject().get("@id").getAsString().indexOf("ctcae") > 0) {
						codeValue = responseArray.get(0).getAsJsonObject().get("properties").getAsJsonObject().get(CTC_CODE).getAsJsonArray().get(0).getAsString();
					} else {
						codeValue = responseArray.get(0).getAsJsonObject().get("notation").getAsString();
					}
					classificationElement.setCodeValue(codeValue);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	/**
	 * Returns the term code as string.
	 *
	 * @param classificationElement the classification element with term name.
	 * @param bioontologyUrl        the bioportal web-services url.
	 * @param bioontologyApiKey     the bioportal web-services unique key.
	 * @return the term code as string.
	 * @throws Exception For all exceptions.
	 */
	public String getTermCodeRequest(ClassificationElement classificationElement, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		String responseResult = "";

		if (!classificationElement.getCodeName().isEmpty()) {
			HttpTransport transport = new HttpTransport();
			HttpMethod method = new GetMethod(bioontologyUrl);
			method.setPath("/search");
			method.setQueryString(new NameValuePair[] {
					new NameValuePair("q", classificationElement.getCodeName()), new NameValuePair("ontologies", dictionary),
					new NameValuePair("include", "prefLabel,notation,properties"),
					new NameValuePair("exact_match", "true"),
					new NameValuePair("no_links", "true"),
					new NameValuePair("no_context", "true"),
					new NameValuePair("apikey", bioontologyApiKey)
			});
			transport.setMethod(method);
			responseResult = transport.processRequest();
		}

		return responseResult;
	}

	private String getDictionary(String term) throws SearchException {
		term = term.toLowerCase();
		if (term.contains("icd10") || term.contains("icd 10")) {
			return ICD10;
		} else if (term.contains("icd9") || term.contains("icd 9")) {
			return ICD9CM;
		} else if (term.contains("ctcae")) {
			return CTCAE;
		}

		throw new SearchException("Unknown dictionary type specified");
	}
}
