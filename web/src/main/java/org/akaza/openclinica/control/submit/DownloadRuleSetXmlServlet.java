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

/* OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Download Rule Set in XML format.
 */
@Component
@SuppressWarnings("unused")
public class DownloadRuleSetXmlServlet extends Controller {

	protected final Logger log = LoggerFactory.getLogger(DownloadRuleSetXmlServlet.class);
	private static final long serialVersionUID = 5381321212952389008L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(
				getResPage().getString("no_have_correct_privilege_current_study")
						+ getResPage().getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, getResException().getString("not_study_director"), "1");

	}

	private OutputStreamWriter handleLoadCastor(OutputStreamWriter writer, RulesPostImportContainer rpic) {

		try {
			// Create Mapping
			Mapping mapping = new Mapping();
			mapping.loadMapping(getCoreResources().getURL("mappingMarshaller.xml"));
			// Create XMLContext
			XMLContext xmlContext = new XMLContext();
			xmlContext.addMapping(mapping);

			Marshaller marshaller = xmlContext.createMarshaller();
			marshaller.setWriter(writer);
			marshaller.marshal(rpic);
			return writer;

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
		} catch (Exception e) {
			throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
		}
	}

	private RulesPostImportContainer prepareRulesPostImportRuleSetRuleContainer(String ruleSetRuleIds) {
		List<RuleSetRuleBean> ruleSetRules = new ArrayList<RuleSetRuleBean>();
		RulesPostImportContainer rpic = new RulesPostImportContainer();

		// protect your inputs - generates num format exception
		if (!"".equals(ruleSetRuleIds)) {
			String[] splitExpression = ruleSetRuleIds.split(",");
			for (String string : splitExpression) {
				RuleSetRuleBean rsr = getRuleSetService().getRuleSetRuleDao().findById(Integer.valueOf(string));
				ruleSetRules.add(rsr);
			}
			if (ruleSetRules.size() > 0) {
				rpic.populate(ruleSetRules);
			}
		}
		return rpic;
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StudyBean currentStudy = getCurrentStudy(request);

		// String ruleSetId = request.getParameter("ruleSetId");
		String ruleSetRuleIds = request.getParameter("ruleSetRuleIds");

		String dir = SQLInitServlet.getField("filePath") + "rules" + File.separator;
		Long time = System.currentTimeMillis();
		File f = new File(dir + "rules" + currentStudy.getOid() + "-" + time + ".xml");
		try {
			OutputStreamWriter charOutput = new OutputStreamWriter(
					new FileOutputStream(f),
					Charset.forName("UTF-8").newEncoder()
			);
			handleLoadCastor(charOutput, prepareRulesPostImportRuleSetRuleContainer(ruleSetRuleIds));
		} catch (FileNotFoundException e) {
			addPageMessage(getResWord().getString("file_path_is_invalid"), request);
			forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);
			return;
		}

		downloadFile(f, "rules" + currentStudy.getOid() + "-" + time + ".xml", "text/xml", response);
	}
}
