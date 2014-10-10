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

import com.clinovo.context.SubmissionContext;
import com.clinovo.context.impl.JSONSubmissionContext;
import com.clinovo.exception.RandomizationException;
import com.clinovo.model.Randomization;
import com.clinovo.model.RandomizationResult;
import com.clinovo.rule.ext.HttpTransportProtocol;
import com.clinovo.util.RandomizationUtil;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainer;
import org.akaza.openclinica.domain.rule.RuleBulkExecuteContainerTwo;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * End-point for all randomization calls.
 * 
 */
@SuppressWarnings("serial")
@Component
public class RandomizeServlet extends Controller {

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {

			return;
		}

		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("Processing randomize request");

		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		PrintWriter writer = response.getWriter();

		try {

			String crfId = request.getParameter("crf");

			String eligibility = request.getParameter("eligibility");

			if (isCrfComplete(currentStudy, ub, crfId)) {

				if (!eligibility.equals("null")) {

					// YES
					if ("0".equals(eligibility)) {

						randomize(request, writer);

					} else if ("1".equals(eligibility)) {

						throw new RandomizationException(
								"The subject has not completed the IE criteria. Complete IE criteria before randomizing the subject");
					}
				} else {

					randomize(request, writer);
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

	private void randomize(HttpServletRequest request, PrintWriter writer) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		// Set expected context
		RandomizationUtil.setSessionManager(getSessionManager(request));
		RandomizationUtil.setCurrentStudy(currentStudy);

		RandomizationResult result = initiateRandomizationCall(request);
		result.setStudyId(String.valueOf(getStudyId(currentStudy, getStudyDAO())));

		// Assign subject to group
		String assignRandomizationResultTo = (String) request.getSession().getAttribute("assignRandomizationResultTo");

		HashMap<String, ItemDataBean> itemsMap = RandomizationUtil.getRandomizationItemData(request);

		// Save randomization result and update all statuses
		RandomizationUtil.saveRandomizationResultToDatabase(result, itemsMap);
		RandomizationUtil.saveStratificationVariablesToDatabase(request);
		RandomizationUtil.saveTrialIDItemToDatabase(request);
		RandomizationUtil.checkAndUpdateEventCRFAndStudyEventStatuses(itemsMap);

		if (assignRandomizationResultTo.equals("dngroup")) {

			RandomizationUtil.assignSubjectToGroup(result);
		} else if (assignRandomizationResultTo.equals("ssid")) {

			RandomizationUtil.addRandomizationResultToSSID(result);
		} else {

			log.info("Subject" + result.getPatientId() + "was randomized successfully");
		}

		JSONObject randomizationResult = new JSONObject();

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = new Date();

		randomizationResult.put("date", dateFormat.format(date));
		randomizationResult.put("result", result.getRandomizationResult());

		writer.write(randomizationResult.toString());
		writer.flush();
	}

	private RandomizationResult initiateRandomizationCall(HttpServletRequest request) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		String trialId;

		// Get Trial Id configured in the CRF
		String crfConfiguredTrialId = request.getParameter("trialId");

		// Get TrialId configured in study parameters
		String configuredTrialId = RandomizationUtil.getRandomizationTrialIdByStudy(currentStudy);

		// Check if the study params trial config is "empty or zero"
		if (RandomizationUtil.isConfiguredTrialIdValid(configuredTrialId)) {

			// Trial Id should be configured in one place
			if (RandomizationUtil.isTrialIdDoubleConfigured(configuredTrialId, crfConfiguredTrialId)) {

				throw new RandomizationException(
						"Trial ID must be specified either in the study parameters or the CRF but not both.");

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

		String siteId = getSite(currentStudy);
		String patientId = request.getParameter("subject");
		String randomizationEnviroment = (String) request.getSession().getAttribute("randomizationEnviroment");

		Randomization randomization = new Randomization();

		// username and password
		randomization.setUsername(CoreResources.getField("randomizationusername"));
		randomization.setPassword(CoreResources.getField("randomizationpassword"));

		// Rando details
		randomization.setSiteId(siteId);
		randomization.setTrialId(trialId);
		randomization.setPatientId(patientId);
		randomization.setStratificationLevel(strataLevel);
		randomization.setTestOnly(Boolean.toString(randomizationEnviroment.equals("test")));

		// Https details
		randomization.setRandomizationUrl(CoreResources.getField("randomizationUrl"));
		randomization.setAuthenticationUrl(CoreResources.getField("randomizationAuthenticationUrl"));

		SubmissionContext context = new JSONSubmissionContext();
		context.setRandomization(randomization);

		HttpTransportProtocol protocol = new HttpTransportProtocol();
		protocol.setSubmissionContext(context);

		return protocol.call();
	}

	private boolean isCrfComplete(StudyBean currentStudy, UserAccountBean ub, String crfId) {

		log.info("Asserting the status of crf with id: {0} ", crfId);

		// Run rules on the CRF
		HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> result = getRuleSetService()
				.runRulesInBulk(crfId, ExecutionMode.DRY_RUN, currentStudy, ub);

		return result.isEmpty();
	}

	protected String getSite(StudyBean currentStudy) throws RandomizationException {

		if (currentStudy.isSite(currentStudy.getId())) {

			return currentStudy.getIdentifier();

		} else {

			throw new RandomizationException("Randomization can only be performed on a site.");
		}
	}

	protected int getStudyId(StudyBean currentStudy, StudyDAO studyDAO) {

		StudyBean study;

		if (currentStudy.getParentStudyId() > 0) {

			study = (StudyBean) studyDAO.findByPK(currentStudy.getParentStudyId());
		} else {

			study = currentStudy;
		}

		return study.getId();
	}
}
