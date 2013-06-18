package com.clinovo.servlet;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;
import com.clinovo.rule.ext.HttpTransportProtocol;
import com.clinovo.util.RandomizationUtil;

@SuppressWarnings("serial")
public class RandomizeServlet extends SecureController {

	private RandomizationResult result = null;
	private RuleSetServiceInterface ruleSetService;
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
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

			String crfId = request.getParameter("crf");

			String eligibility = request.getParameter("eligibility");

			if (isCrfComplete(crfId)) {

				if (eligibility != null) {

					// YES
					if ("0".equals(eligibility)) {

						result = initiateRandomizationCall(request);
						
						JSONObject randomizationResult = new JSONObject();
						
						DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
						Date date = new Date();
						
						randomizationResult.put("date", dateFormat.format(date));
						randomizationResult.put("result", result.getRandomizationResult());
						
						writer.write(randomizationResult.toString());
						writer.flush();

					} else if ("1".equals(eligibility)) {

						throw new RandomizationException("The subject has not completed the IE criteria. Complete IE criteria before randomizing the subject");
					}
				} else {
					
					result = initiateRandomizationCall(request);
					
					JSONObject randomizationResult = new JSONObject();
					
					DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
					Date date = new Date();
					
					randomizationResult.put("date", dateFormat.format(date));
					randomizationResult.put("result", result.getRandomizationResult());
					
					writer.write(randomizationResult.toString());
					writer.flush();
				}

			} else {

				throw new RandomizationException("The crf is not complete. Please make sure you have executed all rules before attempting to randomize the subject");
			}

		} catch (Exception ex) {

			log.error("Randomization Error: {0}", ex.getMessage());
			writer.write("Exception: " + ex.getMessage());
			writer.flush();
		}
	}

	private RandomizationResult initiateRandomizationCall(HttpServletRequest request) throws Exception {

		String trialId = "";
		String studyId = request.getParameter("study");

		// Get Trial Id configured in the CRF
		String crfConfiguredTrialId = request.getParameter("trialId");

		// Get TrialId configured in datainfo.properties
		String configuredTrialId = CoreResources.getField("randomizationTrialId");

		// Check if the datainfo.properties trial config is "empty or zero"
		if (RandomizationUtil.isConfiguredTrialIdValid(configuredTrialId)) {

			// Trial Id should be configured in one place
			if (RandomizationUtil.isTrialIdDoubleConfigured(configuredTrialId, crfConfiguredTrialId)) {

				throw new RandomizationException("Trial ID must be specified either on the server or the CRF but not both.");

			} else {

				trialId = configuredTrialId;
			}

		} else if (RandomizationUtil.isCRFSpecifiedTrialIdValid(crfConfiguredTrialId)) {

			trialId = crfConfiguredTrialId;
			
		} else {
			
			// Valid Trial Id must be specified at least in one place (CRF or datainfo.properties)
			throw new RandomizationException("Specify a valid Trial Id to proceed.");
		}

		String strataLevel = request.getParameter("strataLevel").equals("null") ? "" : request.getParameter("strataLevel");

		// This line should be removed
		String siteId = "PACE001"; //getSiteId(studyId);
		String patientId = request.getParameter("subject");

		Randomization randomization = new Randomization();

		// username and password
		randomization.setUsername(CoreResources.getField("randomizationusername"));
		randomization.setPassword(CoreResources.getField("randomizationpassword"));

		// Rando details
		randomization.setSiteId(siteId);
		randomization.setTrialId(trialId);
		randomization.setPatientId(patientId);
		randomization.setStratificationLevel(strataLevel);

		// Https details
		randomization.setRandomizationUrl(CoreResources.getField("randomizationUrl"));
		randomization.setAuthenticationUrl(CoreResources.getField("randomizationAuthenticationUrl"));

		SubmissionContext context = new JSONSubmissionContext();
		context.setRandomization(randomization);

		HttpTransportProtocol protocol = new HttpTransportProtocol();
		protocol.setSubmissionContext(context);

		RandomizationResult result = protocol.call();

		return result;
	}

	private boolean isCrfComplete(String crfId) {

		log.info("Asserting the status of crf with id: {0} ", crfId);
		
		// Run rules on the CRF
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
		if (currentStudy.isSite(Integer.parseInt(studyId))) {

			siteId = currentStudy.getName();

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
