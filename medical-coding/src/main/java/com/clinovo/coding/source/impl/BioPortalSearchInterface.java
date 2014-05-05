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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOÃ¢â‚¬â„¢S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.coding.source.impl;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.SearchInterface;
import com.clinovo.http.HttpTransport;
import com.clinovo.util.CompleteClassificationFieldsUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BioPortalSearchInterface implements SearchInterface {

    private String dictionary = "";

    private static final String MEDDRA = "MEDDRA";
    private static final String ICD9CM = "ICD9CM";
    private static final String ICD10 = "ICD10";
	private static final String WHOD = "WHOD";

	public List<Classification> search(String term, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		Logger logger = LoggerFactory.getLogger(getClass().getName());

		dictionary = getDictionary(termDictionary);
		term = dictionary.equals(WHOD) ? term.replaceAll(" & ", "_and_").replaceAll(" ", "_") : term;

		List<Classification> classifications = new ArrayList<Classification>();

		String responseTerms = termListRequest(term, bioontologyUrl, bioontologyApiKey);
		JsonArray termListArray = new JsonParser().parse(responseTerms).getAsJsonObject().getAsJsonArray("collection");

		logger.info("Search term returns: " + termListArray.size() + " items");

		for (int i = 0; i < termListArray.size(); i++) {

			JsonObject jsonObjectElement = termListArray.get(i).getAsJsonObject();

			String codeHttpPath = jsonObjectElement.get("@id").getAsString();
			String prefLabel = jsonObjectElement.get("prefLabel").getAsString();

			if (prefLabel.indexOf("_com") < 0) {

				String whodPreferredTerm = dictionary.equals(WHOD) ? prefLabel.substring(prefLabel.indexOf("@"), prefLabel.length()).indexOf("Y") > 0 ? "Yes" : prefLabel.substring(prefLabel.indexOf("@"), prefLabel.length()).indexOf("N") > 0 ? "No" : "" : "";
				prefLabel = dictionary.equals(WHOD) ? prefLabel.substring(0, prefLabel.indexOf("@")).replaceAll("_and_", "_&_").replaceAll("_", " ") : prefLabel;

				ClassificationElement classificationElement = new ClassificationElement();
				classificationElement.setElementName(getFirstElementName(termDictionary));
				classificationElement.setCodeName(prefLabel);

				Classification classification = new Classification();
				classification.setHttpPath(codeHttpPath);
				classification.addClassificationElement(classificationElement);

				if (dictionary.equals(WHOD)) {
					ClassificationElement prefClassificationElement = new ClassificationElement();
					prefClassificationElement.setElementName("Preferred");
					prefClassificationElement.setCodeName(whodPreferredTerm);
					classification.addClassificationElement(prefClassificationElement);
				}

				classifications.add(classification);
			}
		}
		Collections.sort(classifications, new ClassificationSortByReference());
		return classifications;
	}

	public String termListRequest(String term, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        HttpMethod method = new GetMethod(bioontologyUrl);

        method.setPath("/search");
        method.setQueryString(new NameValuePair[]{

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


	public void getClassificationCodes(Classification classification, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		Logger logger = LoggerFactory.getLogger(getClass().getName());
		dictionary = getDictionary(termDictionary);

		ExecutorService service = Executors.newFixedThreadPool(classification.getClassificationElement().size());

		for (ClassificationElement classificationElement : classification.getClassificationElement()) {

			service.submit(new SearchCodeThread(classificationElement, bioontologyUrl, bioontologyApiKey));
		}

		service.shutdown();

		try {

			service.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

			logger.error("Get code threads didn't finish in 30 seconds");
			throw new SearchException(e.getMessage());
		}

		if (dictionary.equals(WHOD)) {
			for (ClassificationElement classificationElement : classification.getClassificationElement()) {
				String codeName = classificationElement.getCodeName();
				String codeValue = classificationElement.getCodeValue();
				classificationElement.setCodeName(!codeName.isEmpty() ? codeName.substring(0, codeName.indexOf("@")).replaceAll("_and_", "_&_").replaceAll("_", " ") : "");
				classificationElement.setCodeValue(!codeValue.isEmpty() ? codeValue.substring(0, codeValue.indexOf("@")).replaceAll("_and_", "_&_").replaceAll("_", " ") : "");
			}
		}
	}

	public Classification getClassificationTerms(String termUrl, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		dictionary = getDictionary(termUrl);
		String treePath = bioontologyUrl + "/ontologies/" + dictionary + "/classes/" + termUrl.replace("://", "%3A%2F%2F").replaceAll("/", "%2F").replaceAll("@", "%40").replaceAll("#", "%23") + "/tree"
				+ "?no_links=true&no_context=true&api_key=" + bioontologyApiKey;

		String termTree = getPageDataRequest(treePath, bioontologyApiKey);

		Classification classification = new Classification();
		recursiveTreeResponseParser(termTree, classification);
		CompleteClassificationFieldsUtil.completeClassificationNameFields(classification.getClassificationElement(), dictionary);

		return classification;
	}

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

		Logger logger = LoggerFactory.getLogger(getClass().getName());

		ClassificationElement classificationElement;
		String bioontologyUrl = "";
		String bioontologyApiKey = "";

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

					String codeValue = responseArray.get(0).getAsJsonObject().get("notation").getAsString();
					classificationElement.setCodeValue(codeValue);
				}

			} catch (Exception ex) {

				logger.error(ex.getMessage());
			}
		}
	}

	public String getTermCodeRequest(ClassificationElement classificationElement, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		String responseResult = "";

		if (!classificationElement.getCodeName().isEmpty()) {

			HttpTransport transport = new HttpTransport();
			HttpMethod method = new GetMethod(bioontologyUrl);

			method.setPath("/search");
			method.setQueryString(new NameValuePair[] {

					new NameValuePair("q", classificationElement.getCodeName()), new NameValuePair("ontologies", dictionary),
					new NameValuePair("include", "prefLabel,notation"),
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
		if (term.contains("meddra") || term.contains("mdr")) {
			return MEDDRA;
		} else if (term.contains("icd10") || term.contains("icd 10")) {
			return ICD10;
		} else if (term.contains("icd9") || term.contains("icd 9")) {
			return ICD9CM;
		} else if (term.contains("whod")) {
			return WHOD;
		}

		throw new SearchException("Unknown dictionary type specified");
	}

	private String getFirstElementName(String termDictionary) throws SearchException {

		if ("meddra".equalsIgnoreCase(termDictionary)) {

			return "LLT";
		} else if ("icd 10".equalsIgnoreCase(termDictionary) || "icd 9cm".equalsIgnoreCase(termDictionary)) {

			return "EXT";
		} else if ("whod".equalsIgnoreCase(termDictionary)) {

			return "MPN";
		}

		throw new SearchException("Unknown dictionary type specified");
	}

	private class ClassificationSortByReference implements Comparator<Classification> {
		public int compare(Classification o1, Classification o2) {
			for (ClassificationElement classification : o1.getClassificationElement()) {
				for (ClassificationElement classification2 : o2.getClassificationElement()) {
					if (classification.getElementName().equals("Preferred") && classification.getElementName().equals(classification2.getElementName())) {
						if ((classification.getCodeName().equals("No") && classification2.getCodeName().equals("Yes"))) {
							return 1;
						} else if (classification.getCodeName().equals("Yes") && classification2.getCodeName().equals("No")) {
							return -1;
						}
					}
				}
			}
			return 0;
		}
	}

}
