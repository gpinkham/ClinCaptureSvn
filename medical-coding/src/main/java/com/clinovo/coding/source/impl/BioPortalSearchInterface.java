package com.clinovo.coding.source.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.SearchInterface;
import com.clinovo.http.HttpTransport;

public class BioPortalSearchInterface implements SearchInterface {

	private static final String ICD10_DICTIONARY_ID = "1516";
	private static final String MEDDRA_DICTIONARY_ID = "1422";
	private static final String CONCEPT_ID = "conceptId";
	private static final String PREFERRED_NAME = "preferredName";
	private static final String CONCEPT_ID_SHORT = "conceptIdShort";
	private static final Object DICTIONARY_NAME = "ontologyDisplayLabel";

	private HttpTransport transport = null;
	public static final String URL = "http://rest.bioontology.org";
	public static final String API_KEY = "b32c11a0-04e7-4120-975e-525819283996";

	public List<Classification> search(String term, String dictionary) throws Exception {

		if (transport == null)
			transport = new HttpTransport();

		HttpMethod method = new GetMethod(BioPortalSearchInterface.URL);

		method.setPath("/bioportal/search/");
		method.setQueryString(new NameValuePair[] {

		new NameValuePair("query", term), new NameValuePair("ontologyids", getDictionary(dictionary)),
				new NameValuePair("apikey", BioPortalSearchInterface.API_KEY)

		});

		transport.setMethod(method);

		return processResponse(transport.processRequest());
	}

	private String getDictionary(String dictionary) throws SearchException {
		
		if("meddra".equalsIgnoreCase(dictionary)) {
			return MEDDRA_DICTIONARY_ID;
		} else if("icd10".equalsIgnoreCase(dictionary)) {
			return ICD10_DICTIONARY_ID;
		}
		
		throw new SearchException("Unknown dictionary type specified");
	}

	private List<Classification> processResponse(String httpResponse) throws Exception {

		List<Classification> classifications = new ArrayList<Classification>();

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(new ByteArrayInputStream(httpResponse.getBytes("utf-8")));

		if (isValidResponse(document)) {
			
			NodeList searchBeans = document.getElementsByTagName("searchBean");
			
			for (int bean = 0; bean < searchBeans.getLength(); bean++) {

				Node node = searchBeans.item(bean);

				Classification classification = new Classification();
				for (int index = 0; index < node.getChildNodes().getLength(); index++) {

					if (node.getChildNodes().item(index).getNodeName().equals(CONCEPT_ID)) {

						classification.setId(node.getChildNodes().item(index).getTextContent());

					} else if (node.getChildNodes().item(index).getNodeName().equals(PREFERRED_NAME)) {

						classification.setTerm(node.getChildNodes().item(index).getTextContent());

					} else if (node.getChildNodes().item(index).getNodeName().equals(CONCEPT_ID_SHORT)) {

						classification.setCode(node.getChildNodes().item(index).getTextContent());

					} else if (node.getChildNodes().item(index).getNodeName().equals(DICTIONARY_NAME)) {

						classification.setDictionary(node.getChildNodes().item(index).getTextContent());
					}
				}

				classifications.add(classification);

			}
		}

		return classifications;
	}

	private boolean isValidResponse(Document document) throws Exception {
		
		// Does it have an error code
		Node errorCodeNode = document.getElementsByTagName("errorCode").item(0);
		
		if (errorCodeNode != null) {

			throw new SearchException(document.getElementsByTagName("longMessage").item(0).getTextContent());
		}

		return true;
	}

	public void setTransport(HttpTransport transport) {

		this.transport = transport;
	}
}
