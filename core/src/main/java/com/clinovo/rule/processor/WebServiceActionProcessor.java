package com.clinovo.rule.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.xml.ws.WebServiceException;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.domain.rule.action.ActionProcessor;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.RuleRunner.RuleRunnerMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.context.SubmissionContext;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.ext.HttpTransportProtocol;

public class WebServiceActionProcessor implements ActionProcessor {

	private HttpTransportProtocol protocol;
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	public Object execute(SubmissionContext context) throws Exception {

		// Allow for testing
		if (protocol == null) {

			protocol = new HttpTransportProtocol();
		}

		protocol.setSubmissionContext(context);

		ExecutorService executor = Executors.newFixedThreadPool(5);
		FutureTask<WebServiceResult> webServiceCall = new FutureTask<WebServiceResult>(protocol);

		try {
			
			log.info("Processing web service rule action with id: {}", context.getAction().getId());
			
			executor.execute(webServiceCall);
			
		} catch (Exception ex) {
			throw new WebServiceException(ex);
		}

		return webServiceCall.get();
	}

	public void setTransportProtocol(HttpTransportProtocol protocol) {
		this.protocol = protocol;
	}

	public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode,
			RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
			UserAccountBean ub, Object... arguments) {

		// Do nothing
		return null;
	}

}