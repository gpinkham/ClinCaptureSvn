package com.clinovo.context;

import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;

public interface SubmissionContext {

	Randomization getRandomization();

	void setHttpClient(HttpClient client);

	String authenticate() throws Exception;

	void setRandomization(Randomization randomization);

	List<Header> getHttpHeaders() throws Exception;

	RequestEntity getRequestEntity() throws Exception;

	RandomizationResult processResponse(String response, int httpStatus) throws Exception;

}
