package com.clinovo.context;

import org.apache.commons.httpclient.HttpMethod;

import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;

public interface SubmissionContext {

	WebServiceAction getAction();
	
	void setHttpMethod(HttpMethod method);
	
	void setAction(WebServiceAction action);
	
	String authenticate(WebServiceAction action) throws Exception;
	
	WebServiceResult processResponse(String response, int httpStatus) throws Exception;

}
