/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.servlet;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
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

/**
 * End-point for all randomization calls
 *
 */
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
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {

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

						randomize(writer);

					} else if ("1".equals(eligibility)) {

						throw new RandomizationException(
								"The subject has not completed the IE criteria. Complete IE criteria before randomizing the subject");
					}
				} else {

					randomize(writer);
				}

			} else {

				throw new RandomizationException(
						"The crf is not complete. Please make sure you have executed all rules before attempting to randomize the subject");
			}

		} catch (Exception ex) {

			log.error("Randomization Error: {0}", ex.getMessage());
			writer.write("Exception: " + ex.getMessage());
			writer.flush();
		}
	}

	private void randomize(PrintWriter writer) throws Exception {

		result = initiateRandomizationCall(request);		
		result.setStudyId(String.valueOf(getStudyId()));
		
		// Set expected context
		RandomizationUtil.setSessionManager(sm);
		RandomizationUtil.setCurrentStudy(currentStudy);

		// Assign subject to group
		RandomizationUtil.assignSubjectToGroup(result);

		JSONObject randomizationResult = new JSONObject();

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = new Date();

		randomizationResult.put("date", dateFormat.format(date));
		randomizationResult.put("result", result.getRandomizationResult());

		writer.write(randomizationResult.toString());
		writer.flush();
	}

	private RandomizationResult initiateRandomizationCall(HttpServletRequest request) throws Exception {

		String trialId = "";

		// Get Trial Id configured in the CRF
		String crfConfiguredTrialId = request.getParameter("trialId");

		// Get TrialId configured in datainfo.properties
		String configuredTrialId = CoreResources.getField("randomizationTrialId");

		// Check if the datainfo.properties trial config is "empty or zero"
		if (RandomizationUtil.isConfiguredTrialIdValid(configuredTrialId)) {

			// Trial Id should be configured in one place
			if (RandomizationUtil.isTrialIdDoubleConfigured(configuredTrialId, crfConfiguredTrialId)) {

				throw new RandomizationException(
						"Trial ID must be specified either on the server or the CRF but not both.");

			} else {

				trialId = configuredTrialId;
			}

		} else if (RandomizationUtil.isCRFSpecifiedTrialIdValid(crfConfiguredTrialId)) {

			trialId = crfConfiguredTrialId;

		} else {

			// Valid Trial Id must be specified at least in one place (CRF or datainfo.properties)
			throw new RandomizationException("Specify a valid Trial Id to proceed.");
		}

		String strataLevel = request.getParameter("strataLevel").equals("null") ? "" : request
				.getParameter("strataLevel");

		String siteId = getSite();
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

	protected String getSite() throws RandomizationException {

		if (currentStudy.isSite(currentStudy.getId())) {

			return currentStudy.getIdentifier();

		} else {

			throw new RandomizationException("Randomization can only be performed on a site.");
		}
	}

	@SuppressWarnings("rawtypes")
	protected int getStudyId() {
		
		StudyBean study = null;
		StudyDAO studyDAO = new StudyDAO(sm.getDataSource());

		if (currentStudy.getParentStudyId() > 0) {
			
			study = (StudyBean) studyDAO.findByPK(currentStudy.getParentStudyId());
		} else {
			
			study = currentStudy;
		}
		
		return study.getId();
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
