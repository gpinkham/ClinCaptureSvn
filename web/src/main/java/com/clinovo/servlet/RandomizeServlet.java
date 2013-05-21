package com.clinovo.servlet;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.model.WebServiceResult;
import com.clinovo.rule.WebServiceAction;
import com.clinovo.rule.ext.HttpTransportProtocol;

@SuppressWarnings("serial")
public class RandomizeServlet extends SecureController {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());
	private RuleSetServiceInterface ruleSetService;

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.MONITOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	@Override
	protected void processRequest() throws Exception {

		log.info("Processing randomize request");

		PrintWriter writer = response.getWriter();

		try {

			String pace = request.getParameter("pace");
			String crfId = request.getParameter("crf");
			String studyId = request.getParameter("study");

			if (isCrfComplete(crfId)) {

				getSiteId(studyId);
				String trialId = "";
				String siteId = getSiteId(studyId);

				// Determine the trial to use
				if (pace.toString().equals("1"))
					trialId = "1199";
				if (pace.toString().equals("2"))
					trialId = "1198";

				String patientId = request.getParameter("subject");

				WebServiceAction action = new WebServiceAction();

				// username and password
				action.setUsername(CoreResources.getField("randomizationusername"));
				action.setPassword(CoreResources.getField("randomizationpassword"));

				// Rando details
				action.setSiteId(siteId);
				action.setTrialId(trialId);
				action.setPatientId(patientId);

				// Https details
				action.setRandomizationUrl(CoreResources.getField("randomizationUrl"));
				action.setAuthenticationUrl(CoreResources.getField("randomizationAuthenticationUrl"));

				SubmissionContext context = new JSONSubmissionContext();
				context.setAction(action);

				HttpTransportProtocol protocol = new HttpTransportProtocol();
				protocol.setSubmissionContext(context);

				WebServiceResult result = protocol.call();

				writer.write(result.getRandomizationResult());
				writer.flush();

			} else {

				// CRF not complete
				writer.write("The crf is not complete. Please make sure you have executed all rules before randomizing");
				writer.flush();
			}

		} catch (Exception ex) {

			log.error("Randomization Error: {0}", ex.getMessage());
			writer.write(ex.getMessage());
			writer.flush();

		}
	}

	private boolean isCrfComplete(String crfId) {

		log.info("Asserting the status of crf with id: {0} ", crfId);

		HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> result = getRuleSetService()
				.runRulesInBulk(crfId, ExecutionMode.DRY_RUN, currentStudy, ub);
		if (result.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	protected String getSiteId(String studyId) throws RandomizationException {
		
		String siteId = "";
		if(currentStudy.isSite(Integer.parseInt(studyId))) {
			
			siteId = currentStudy.getIdentifier();
			
		} else {
			
			throw new RandomizationException("Randomization can only be performed on a site.");
		}
		
		return siteId;
	}

	private RuleSetServiceInterface getRuleSetService() {

		ruleSetService = this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess
				.getApplicationContext(context).getBean("ruleSetService");
		ruleSetService.setMailSender((JavaMailSenderImpl) SpringServletAccess.getApplicationContext(context).getBean(
				"mailSender"));
		ruleSetService.setContextPath(getContextPath());
		ruleSetService.setRequestURLMinusServletPath(getRequestURLMinusServletPath());
		return ruleSetService;
	}

}
