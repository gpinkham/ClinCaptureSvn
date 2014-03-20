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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BioPortalSearchInterface implements SearchInterface {

    private String dictionary = "";

    private static final String MEDDRA = "MEDDRA";
    private static final String ICD9CM = "ICD9CM";
    private static final String ICD10 = "ICD10";

	public List<Classification> search(String term, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		Logger logger = LoggerFactory.getLogger(getClass().getName());

		dictionary = getDictionary(termDictionary);
		List<Classification> classifications = new ArrayList<Classification>();

		String responseTerms = termListRequest(term, bioontologyUrl, bioontologyApiKey);
		JsonArray termListArray = new JsonParser().parse(responseTerms).getAsJsonObject().getAsJsonArray("collection");

		logger.info("Search term returns: " + termListArray.size() + " items");

		for (int i = 0; i < termListArray.size(); i++) {

			JsonObject jsonObjectElement = termListArray.get(i).getAsJsonObject();

			String codeHttpPath = jsonObjectElement.get("@id").getAsString();
			String prefLabel = jsonObjectElement.get("prefLabel").getAsString();

			ClassificationElement classificationElement = new ClassificationElement();
			classificationElement.setElementName(getFirstElementName(termDictionary));
			classificationElement.setCodeName(prefLabel);

			Classification classification = new Classification();
			classification.setHttpPath(codeHttpPath);
			classification.addClassificationElement(classificationElement);

			classifications.add(classification);
		}

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
	}

	public Classification getClassificationTerms(String termUrl, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		dictionary = getDictionary(termUrl);
		String treePath = bioontologyUrl + "/ontologies/" + dictionary + "/classes/" + termUrl.replace("://", "%3A%2F%2F").replaceAll("/", "%2F") + "/tree"
				+ "?no_links=true&no_context=true&api_key=" + bioontologyApiKey;

		String termTree = getPageDataRequest(treePath, bioontologyApiKey);

		Classification classification = new Classification();
		recursiveTreeResponseParser(termTree, "", classification);
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

	private void recursiveTreeResponseParser(String treeResponse, String prefLabel, Classification classification) throws SearchException {

		JsonArray jarray = new JsonParser().parse(treeResponse).getAsJsonArray();

		for (int i = 0; i < jarray.size(); i++) {

			JsonObject jsonObject = jarray.get(i).getAsJsonObject();
			JsonArray jsonArrayWithChildItem = jsonObject.getAsJsonArray("children");
			ClassificationElement classificationElement = new ClassificationElement();

			if (jsonArrayWithChildItem != null & jsonArrayWithChildItem.size() > 0) {

				classificationElement.setCodeName(jsonObject.get("prefLabel").getAsString());
				classification.addClassificationElement(classificationElement);

				recursiveTreeResponseParser(jsonObject.get("children").toString(), prefLabel, classification);
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
		}

		throw new SearchException("Unknown dictionary type specified");
	}

	private String getFirstElementName(String termDictionary) throws SearchException {

		if ("meddra".equalsIgnoreCase(termDictionary)) {

			return "LLT";
		} else if ("icd 10".equalsIgnoreCase(termDictionary) || "icd 9cm".equalsIgnoreCase(termDictionary)) {

			return "EXT";
		}

		throw new SearchException("Unknown dictionary type specified");
	}
}
