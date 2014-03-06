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
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

public class BioPortalSearchInterface implements SearchInterface {


    private List<ClassificationElement> classificationElementsForRecurse;
    private List<Classification> classifications;
    private String dictionary = "";

    private static final int THREADS_NUMBER = 1000;
    private static final String MEDDRA = "MEDDRA";
    private static final String ICD9CM = "ICD9CM";
    private static final String ICD10 = "ICD10";

    public List<Classification> search(String term, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		dictionary = getDictionary(termDictionary);
		classifications = Collections.synchronizedList(new ArrayList<Classification>());
		classificationElementsForRecurse = Collections.synchronizedList(new ArrayList<ClassificationElement>());

		JsonArray responseTermsArray = collectAllTermsData(term, bioontologyUrl, bioontologyApiKey);

		List<List<String>> listWithAttribues = new ArrayList<List<String>>();

		for (int i = 0; i < responseTermsArray.size(); i++) {

			JsonObject jsonObjectElement = responseTermsArray.get(i).getAsJsonObject();

			String codeHttpPath = jsonObjectElement.get("@id").getAsString();
			String prefLabel = jsonObjectElement.get("prefLabel").getAsString();
			String treePath = bioontologyUrl + "/ontologies/" + dictionary + "/classes/" + codeHttpPath.replace("://", "%3A%2F%2F").replaceAll("/", "%2F") + "/tree"
					+ "?no_links=true&no_context=true&api_key=" + bioontologyApiKey;

			codeHttpPath = codeHttpPath.indexOf("/MDR/") > 0 ? codeHttpPath.replace("/MDR/", "/MEDDRA/") : codeHttpPath;

			List<String> element = new ArrayList<String>();
			element.add(treePath);
			element.add(codeHttpPath);
			element.add(prefLabel);

			listWithAttribues.add(element);
		}

		int numberOfThreads = THREADS_NUMBER;

		if (listWithAttribues.size() < THREADS_NUMBER) {

			numberOfThreads = listWithAttribues.size();
		}

        //get attributes list for each thread
		List<List<List<String>>> attributesPartialLists = chopAttributesListForParts(listWithAttribues, numberOfThreads);

		List<Thread> threads = new ArrayList<Thread>();

		for (List<List<String>> attributesList : attributesPartialLists) {

			BioportalThread bioontologyThread = new BioportalThread(attributesList, bioontologyApiKey);
			threads.add(bioontologyThread);
			bioontologyThread.start();
		}

		for (Thread t : threads) {
			t.join();
		}

        return classifications;
    }

	private JsonArray collectAllTermsData(String term, String bioontologyUrl, String bioontologyApiKey) throws Exception {

		String response = getTermsResponse(term, bioontologyUrl, bioontologyApiKey);

		String secondPageUrl = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("links").get("nextPage").toString().replaceAll("\"", "");
		JsonArray jarray = new JsonParser().parse(response).getAsJsonObject().getAsJsonArray("collection");

		if (secondPageUrl.indexOf("null") < 0) {

			recursivePageReader(secondPageUrl, bioontologyApiKey, jarray);
		}

		return jarray;
	}

	private JsonArray recursivePageReader(String pageUrl, String bioontologyApiKey, JsonArray jarray) throws Exception {

		String response = getPageData(pageUrl, bioontologyApiKey);

		JsonObject jobject = new JsonParser().parse(response).getAsJsonObject();
		jarray.addAll(jobject.getAsJsonArray("collection"));
		pageUrl = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("links").get("nextPage").toString().replaceAll("\"", "");

		if (pageUrl.indexOf("null") < 0) {

			recursivePageReader(pageUrl, bioontologyApiKey, jarray);
		}

		return jarray;
	}

	public String getTermsResponse(String term, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        HttpMethod method = new GetMethod(bioontologyUrl);

        method.setPath("/search");
        method.setQueryString(new NameValuePair[]{

                new NameValuePair("q", term), new NameValuePair("ontologies", dictionary),
				new NameValuePair("no_context", "true"),
				new NameValuePair("no_links", "true"),
                new NameValuePair("apikey", bioontologyApiKey)

        });

        HttpTransport transport = new HttpTransport();
        transport.setMethod(method);

        return transport.processRequest();
    }

    public String getPageData(String urlPath, String bioontologyApiKey) throws Exception {

        HttpMethod method = new GetMethod();
        method.setPath(urlPath);
        method.setRequestHeader(new Header("Authorization", "apikey token=" + bioontologyApiKey));
        HttpTransport transport = new HttpTransport();
        transport.setMethod(method);

        return transport.processRequest();
    }

    private void recursiveTreeResponseParser(String treeResponse, String prefLabel, String codeHttp) throws SearchException {

        JsonArray jarray = new JsonParser().parse(treeResponse).getAsJsonArray();

        for (int i = 0; i < jarray.size(); i++) {

            JsonObject jsonObject = jarray.get(i).getAsJsonObject();
            ClassificationElement clasificationElementTmp = new ClassificationElement();

            JsonArray jsonArrayWithChildItem = jsonObject.getAsJsonArray("children");
            if (jsonArrayWithChildItem != null & jsonArrayWithChildItem.size() > 0) {

                clasificationElementTmp.setCodeName(jsonObject.get("prefLabel").getAsString());
                classificationElementsForRecurse.add(clasificationElementTmp);

                recursiveTreeResponseParser(jsonObject.get("children").toString(), prefLabel, codeHttp);

            } else if (jsonObject.get("prefLabel").getAsString().equalsIgnoreCase((prefLabel))) {

                clasificationElementTmp.setCodeName(prefLabel);
                classificationElementsForRecurse.add(clasificationElementTmp);

                break;
            }
        }

        Classification classification = new Classification();
        classification.setHttpPath(codeHttp);
        classification.setClassificationElement(new ArrayList<ClassificationElement>(classificationElementsForRecurse));

        classificationElementsForRecurse = Collections.synchronizedList(new ArrayList<ClassificationElement>());

        if (dictionary.equals(MEDDRA) && classification.getClassificationElement().size() == 4 ||
                dictionary.equals(ICD9CM) && classification.getClassificationElement().size() == 3 ||
                dictionary.equals(ICD10) && classification.getClassificationElement().size() == 3) {

            CompleteClassificationFieldsUtil.completeClassificationNameFields(classification.getClassificationElement(), dictionary);

            classifications.add(classification);
        }

        return;
    }

    private class BioportalThread extends Thread {

        List<List<String>> list = null;
        String apiKey = "";

        BioportalThread(List<List<String>> list, String apikey) {

            this.list = list;
            this.apiKey = apikey;
        }

        public void run() {

            for (List<String> part : list) {

                if (part.size() == 3) {

                    String treePath = part.get(0);
                    String codeHttp = part.get(1);
                    String verbTerm = part.get(2);

                    try {

                        String termTree = getPageData(treePath, apiKey);
                        recursiveTreeResponseParser(termTree, verbTerm, codeHttp);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public void getClassificationCodes(Classification classification, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        dictionary = getDictionary(termDictionary);

        List<Thread> threads = new ArrayList<Thread>();

        for (ClassificationElement classificationElement : classification.getClassificationElement()) {

            SearchCodeThread searchCodeThread = new SearchCodeThread(classificationElement, bioontologyUrl, bioontologyApiKey);
            threads.add(searchCodeThread);
            searchCodeThread.start();
        }

        for (Thread t : threads) {
            t.join();
        }

    }

    private class SearchCodeThread extends Thread {

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

                getCodeResponse(classificationElement, bioontologyUrl, bioontologyApiKey);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void getCodeResponse(ClassificationElement classificationElement, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        if (!classificationElement.getCodeName().equals("")) {

            HttpTransport transport = new HttpTransport();

            HttpMethod method = new GetMethod(bioontologyUrl);

            method.setPath("/search");
            method.setQueryString(new NameValuePair[]{

                    new NameValuePair("q", classificationElement.getCodeName()), new NameValuePair("ontologies", dictionary),
                    new NameValuePair("include", "prefLabel,notation"),
                    new NameValuePair("exact_match", "true"),
					new NameValuePair("no_links", "true"),
					new NameValuePair("no_context", "true"),
                    new NameValuePair("apikey", bioontologyApiKey)

            });

            transport.setMethod(method);

            String codeResult = transport.processRequest();
            JsonObject jobject = new JsonParser().parse(codeResult).getAsJsonObject();
            JsonArray jsonArray = jobject.getAsJsonArray("collection");

            if (!jsonArray.isJsonNull() && jsonArray.size() > 0) {

                String codeValue = jsonArray.get(0).getAsJsonObject().get("notation").getAsString();
                classificationElement.setCodeValue(codeValue);
            }
        }
    }

    public static List<List<List<String>>> chopAttributesListForParts(final List<List<String>> ls, final int parts) {

        final List<List<List<String>>> listParts = new ArrayList<List<List<String>>>();

        if (parts > 0) {

            final int numberOfParts = ls.size() / parts;

            int itemsLeftOver = ls.size() % parts;
            int itemsTake = numberOfParts;

            for (int i = 0, iT = ls.size(); i < iT; i += itemsTake) {

                if (itemsLeftOver > 0) {
                    itemsLeftOver--;
                    itemsTake = numberOfParts + 1;

                } else {

                    itemsTake = numberOfParts;
                }

                List<List<String>> list = new ArrayList<List<String>>(ls.subList(i, Math.min(iT, i + itemsTake)));

                listParts.add(list);
            }
        }
        return listParts;
    }


	private String getDictionary(String dictionary) throws SearchException {

		if ("meddra".equalsIgnoreCase(dictionary)) {
			return MEDDRA;
		} else if ("icd 10".equalsIgnoreCase(dictionary)) {
			return ICD10;
		} else if ("icd 9cm".equalsIgnoreCase(dictionary)) {
			return ICD9CM;
		}

		throw new SearchException("Unknown dictionary type specified");
	}
}
