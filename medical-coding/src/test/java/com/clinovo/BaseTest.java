package com.clinovo;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpClientMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

	protected static String searchResult;
    protected static String treeResult;

	private final static Logger log = LoggerFactory.getLogger(BaseTest.class.getName());

	static {

		try {

            searchResult = readFile("src/test/resources/com/clinovo/coding/ExampleSearchResponse.json");
            treeResult = readFile("src/test/resources/com/clinovo/coding/ExampleTreeResponse.json");

		} catch (Exception ex) {

			log.error(ex.getMessage());
		}
	}

	protected static String readFile(String fileName) throws Exception {

		StringBuilder fileContents = new StringBuilder();
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

		try {

			String currentLine = fileReader.readLine();
			while(currentLine != null) {

				fileContents.append(currentLine);
				currentLine = fileReader.readLine();
			}
		} finally {

			fileReader.close();
		}

		return fileContents.toString();
	}

	protected HttpClient stubHttpClient(int responseStatus, String responseBody) {

		return new HttpClientMock(responseStatus, responseBody);
	}
}
