/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.FileProperties;
import org.akaza.openclinica.bean.rule.FileUploadHelper;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Verify the Rule import , show records that have Errors as well as records that will be saved.
 * 
 * @author Krikor krumlian
 */
@Component
public class ImportRuleServlet extends Controller {
	private static final long serialVersionUID = 9116068126651934226L;
	protected final Logger log = LoggerFactory.getLogger(ImportRuleServlet.class);

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		UserAccountBean ub = getUserAccountBean(request);
		// Reset the study if RS is saving a rule for a different study
		if ((request.getParameter("study") != null && !request.getParameter("study").isEmpty()) && getCurrentStudy(request).getId() != Integer.parseInt(request.getParameter("study"))) {
			originalScope = (StudyBean) request.getSession().getAttribute(STUDY);
			request.getSession().setAttribute(STUDY, getStudyDAO().findByPK(Integer.valueOf(request.getParameter("study"))));
		}

		StudyBean currentStudy = getCurrentStudy(request);
		String action = request.getParameter("action");
		request.setAttribute("contextPath", getContextPath(request));
		request.setAttribute("hostPath", getHostPath(request));
		if (StringUtil.isBlank(action)) {
			forwardPage(Page.IMPORT_RULES, request, response);
		}
		if ("downloadrulesxsd".equalsIgnoreCase(action)) {
			File xsdFile = getCoreResources().getFile("rules.xsd", "rules" + File.separator);
			dowloadFile(xsdFile, "text/xml", request, response);
		}
		if ("downloadtemplate".equalsIgnoreCase(action)) {
			File file = getCoreResources().getFile("rules_template.xml", "rules" + File.separator);
			dowloadFile(file, "text/xml", request, response);
		}
		if ("downloadtemplateWithNotes".equalsIgnoreCase(action)) {
			File file = getCoreResources().getFile("rules_template_with_notes.xml", "rules" + File.separator);
			dowloadFile(file, "text/xml", request, response);
		}
		if ("confirm".equalsIgnoreCase(action)) {
			try {
				
				FileUploadHelper uploadHelper = new FileUploadHelper(new FileProperties("xml"));
				File ruleFile = uploadHelper.returnFiles(request, getServletContext(), getDirToSaveUploadedFileIn()).get(0);
				InputStream xsdFile = getCoreResources().getInputStream("rules.xsd");
				XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
				schemaValidator.validateAgainstSchema(ruleFile, xsdFile);
				RulesPostImportContainer importedRules = handleLoadCastor(ruleFile);
				logger.info(ub.getFirstName());
				// Editing a rule - remove existing rules
				boolean isCopiedRule = Boolean.valueOf(request.getParameter("copy"));
				if (request.getParameter("rs") != null && request.getParameter("rs").equals("true")
						&& request.getParameter("edit") != null && "true".equalsIgnoreCase(request.getParameter("edit"))
						&& !isCopiedRule) {
					
					RuleSetRuleBean ruleSetRule = getRuleSetRuleDao().findById(Integer.parseInt(request.getParameter("id")));
					RuleBean rule = ruleSetRule.getRuleBean();
					ExpressionBean expression = rule.getExpression();
					RuleSetBean ruleSet = ruleSetRule.getRuleSetBean();
					if (ruleSetRule != null) {
						Session session = getRuleDao().getSessionFactory().getCurrentSession();
						Transaction transaction = session.beginTransaction();
						for (RuleActionBean act : ruleSetRule.getActions()) {
							session.delete(act);
							session.delete(act.getRuleActionRun());
						}
						// Remove current rule and props
						session.delete(ruleSetRule);
						session.delete(ruleSet);
						session.delete(rule);
						session.delete(expression);
						transaction.commit();
						session.flush();
					}
				}

				RulesPostImportContainerService rulesPostImportContainerService = getRulesPostImportContainerService(currentStudy, ub);
				importedRules = rulesPostImportContainerService.validateRuleDefs(importedRules);
				importedRules = rulesPostImportContainerService.validateRuleSetDefs(importedRules);
				provideMessage(importedRules, request);
				// If request is coming from rule studio
				if (request.getParameter("rs") != null && request.getParameter("rs").equals("true")) {
					try {
						getRuleDao().getSessionFactory().getCurrentSession().clear();
						getRuleSetService().saveImport(importedRules);
					} catch (Exception ex) {
						logger.error("RS save error.", ex);
						response.sendError(500, ex.getMessage());
					}
				} else {
					request.getSession().setAttribute("importedData", importedRules);
					forwardPage(Page.VERIFY_RULES_IMPORT_SERVLET, request, response);
				}
			} catch (OpenClinicaSystemException re) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(re.getErrorCode() == null ? respage.getString("OCRERR_0016") : respage.getString(re.getErrorCode()));
				Object[] arguments = { re.getMessage() };
				if (re.getErrorCode() != null) {
					arguments = re.getErrorParams();
				}
				log.error(Arrays.toString(arguments));
				addPageMessage(mf.format(arguments), request);
				forwardPage(Page.IMPORT_RULES, request, response);
			}
		}
		// reset back to original scope
		if (originalScope != null) {
			request.getSession().setAttribute(STUDY, originalScope);
		}
	}

	private void provideMessage(RulesPostImportContainer rulesContainer, HttpServletRequest request) {
		int validRuleSetDefs = rulesContainer.getValidRuleSetDefs().size();
		int duplicateRuleSetDefs = rulesContainer.getDuplicateRuleSetDefs().size();
		int invalidRuleSetDefs = rulesContainer.getInValidRuleSetDefs().size();

		int duplicateRuleDefs = rulesContainer.getDuplicateRuleDefs().size();
		int invalidRuleDefs = rulesContainer.getInValidRuleDefs().size();

		if (validRuleSetDefs > 0 && duplicateRuleSetDefs == 0 && invalidRuleSetDefs == 0 && duplicateRuleDefs == 0
				&& invalidRuleDefs == 0) {
			addPageMessage(respage.getString("rules_Import_message1"), request);
		}
		if (duplicateRuleSetDefs > 0 && invalidRuleSetDefs == 0 && duplicateRuleDefs >= 0 && invalidRuleDefs == 0) {
			addPageMessage(respage.getString("rules_Import_message2"), request);
		}
		if (invalidRuleSetDefs > 0 && invalidRuleDefs >= 0) {
			addPageMessage(respage.getString("rules_Import_message3"), request);
		}
	}

	private String getDirToSaveUploadedFileIn() throws OpenClinicaSystemException {
		String dir = SQLInitServlet.getField("filePath");
		if (!new File(dir).exists()) {
			throw new OpenClinicaSystemException(respage.getString("filepath_you_defined_not_seem_valid"));
		}
		return dir + "rules" + File.separator + "original" + File.separator;
	}

	private RulesPostImportContainer handleLoadCastor(File xmlFile) {

		RulesPostImportContainer ruleImport;
		try {
			// create an XMLContext instance
			XMLContext xmlContext = new XMLContext();
			// create and set a Mapping instance
			Mapping mapping = xmlContext.createMapping();
			mapping.loadMapping(getCoreResources().getURL("mapping.xml"));

			xmlContext.addMapping(mapping);
			// create a new Unmarshaller
			Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
			unmarshaller.setWhitespacePreserve(false);
			unmarshaller.setClass(RulesPostImportContainer.class);
			// Create a Reader to the file to unmarshal from
			FileReader reader = new FileReader(xmlFile);
			ruleImport = (RulesPostImportContainer) unmarshaller.unmarshal(reader);
			ruleImport.initializeRuleDef();
			logRuleImport(ruleImport);
			return ruleImport;
		} catch (FileNotFoundException ex) {
			throw new OpenClinicaSystemException(ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new OpenClinicaSystemException(ex.getMessage(), ex.getCause());
		} catch (MarshalException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (ValidationException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		} catch (MappingException e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		}
	}

	private void logRuleImport(RulesPostImportContainer ruleImport) {
		logger.info("Total Number of RuleDefs Being imported : {} ", ruleImport.getRuleDefs().size());
		logger.info("Total Number of RuleAssignments Being imported : {} ", ruleImport.getRuleSets().size());
	}

	private RulesPostImportContainerService getRulesPostImportContainerService(StudyBean currentStudy,
			UserAccountBean ub) {
		RulesPostImportContainerService rulesPostImportContainerService = new RulesPostImportContainerService(
				getDataSource());
		rulesPostImportContainerService.setRuleDao(getRuleDao());
		rulesPostImportContainerService.setRuleSetDao(getRuleSetDao());
		rulesPostImportContainerService.setCurrentStudy(currentStudy);
		rulesPostImportContainerService.setRespage(respage);
		rulesPostImportContainerService.setUserAccount(ub);
		return rulesPostImportContainerService;
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}
}
