package com.clinovo.coding.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.util.CompleteClassificationFieldsUtil;
import com.google.gson.*;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.SearchInterface;
import com.clinovo.http.HttpTransport;

public class BioPortalSearchInterface implements SearchInterface {

	private HttpTransport transport = null;
    private ArrayList<ClassificationElement> classificationElementsForRecurse;
    private String dictionary = "";

    private static final String MEDDRA = "MEDDRA";
    private static final String ICD9CM = "ICD9CM";
    private static final String ICD10 = "ICD10";

    public List<Classification> search(String term, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        dictionary = getDictionary(termDictionary);

        List<Classification> classifications = new ArrayList<Classification>();
        classificationElementsForRecurse = new ArrayList<ClassificationElement>();

        String termResponseResult = getTermsResponse(term, bioontologyUrl, bioontologyApiKey);

        JsonObject jobject = new JsonParser().parse(termResponseResult).getAsJsonObject();
        JsonArray jarray = jobject.getAsJsonArray("collection");

        for (int i = 0; i < jarray.size(); i++) {

            JsonObject jsonObjectElement = jarray.get(i).getAsJsonObject();
            JsonObject jsonObjectLinks = jsonObjectElement.getAsJsonObject("links");

            String treePath = jsonObjectLinks.get("tree").getAsString();
            String codeHttpPath = jsonObjectElement.get("@id").getAsString();
            String verbatimPrefLabel = jsonObjectElement.get("prefLabel").getAsString();

            if (treePath.contains("/tree") && treePath.contains(dictionary)) {

                Classification classification = new Classification();
                classification.setHttpPath(codeHttpPath);

                recursiveTreeResponseParser(getTreeResponse(treePath, bioontologyApiKey), verbatimPrefLabel);

                if (dictionary.equals(MEDDRA) && classificationElementsForRecurse.size() == 4 ||
                        dictionary.equals(ICD9CM) && classificationElementsForRecurse.size() == 3 ||
                        dictionary.equals(ICD10) && classificationElementsForRecurse.size() == 3) {
                    CompleteClassificationFieldsUtil.completeClassificationNameFields(classificationElementsForRecurse, dictionary);
                    classification.setClassificationElement(classificationElementsForRecurse);


                    classifications.add(classification);
                }

                classificationElementsForRecurse = new ArrayList<ClassificationElement>();
            }

        }

        return classifications;
    }

    private void recursiveTreeResponseParser(String treeResponse, String verbatimPrefLabel) {

        JsonArray jarray = new JsonParser().parse(treeResponse).getAsJsonArray();

        for (int i = 0; i < jarray.size(); i++) {

            JsonObject jsonObject = jarray.get(i).getAsJsonObject();
            ClassificationElement clasificationElementTmp = new ClassificationElement();

            if (jsonObject.getAsJsonArray("children").size() > 0) {

                clasificationElementTmp.setCodeName(jsonObject.get("prefLabel").getAsString());
                classificationElementsForRecurse.add(clasificationElementTmp);

                recursiveTreeResponseParser(jsonObject.get("children").toString(), verbatimPrefLabel);

            } else if (jsonObject.get("prefLabel").getAsString().equalsIgnoreCase((verbatimPrefLabel))) {

                clasificationElementTmp.setCodeName(verbatimPrefLabel);
                classificationElementsForRecurse.add(clasificationElementTmp);

                break;
            }
        }

        return;
    }

    public String getTreeResponse(String treePath, String bioontologyApiKey) throws Exception {

        HttpMethod method = new GetMethod();
        method.setPath(treePath);
        method.setRequestHeader(new Header("Authorization", "apikey token=" + bioontologyApiKey));

        transport.setMethod(method);

        return transport.processRequest();
    }

    public String getTermsResponse(String term, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        if (transport == null)
            transport = new HttpTransport();

        HttpMethod method = new GetMethod(bioontologyUrl);

        method.setPath("/search");
        method.setQueryString(new NameValuePair[] {

                new NameValuePair("q", term), new NameValuePair("ontologies",  dictionary),
                new NameValuePair("apikey", bioontologyApiKey)

        });

        transport.setMethod(method);

        return transport.processRequest();
    }

    //used by coding job only
    public void getClassificationCodes(Classification classification, String termDictionary, String bioontologyUrl, String bioontologyApiKey) throws Exception {

        for (ClassificationElement classfifcationElement : classification.getClassificationElement()) {
            if (!classfifcationElement.getCodeName().equals("")) {

                if (transport == null)
                    transport = new HttpTransport();

                if(dictionary == null)
                    dictionary = getDictionary(termDictionary);

                HttpMethod method = new GetMethod(bioontologyUrl);

                method.setPath("/search");
                method.setQueryString(new NameValuePair[]{

                        new NameValuePair("q", classfifcationElement.getCodeName()), new NameValuePair("ontologies", dictionary),
                        new NameValuePair("include", "prefLabel,notation"),
                        new NameValuePair("exact_match", "true"),
                        new NameValuePair("apikey", bioontologyApiKey)

                });

                transport.setMethod(method);

                String codeResult = transport.processRequest();
                JsonObject jobject = new JsonParser().parse(codeResult).getAsJsonObject();
                String codeValue = jobject.getAsJsonArray("collection").get(0).getAsJsonObject().get("notation").getAsString();
                classfifcationElement.setCodeValue(codeValue);
            }
        }
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

    public void setTransport(HttpTransport transport) {

        this.transport = transport;
    }

}
