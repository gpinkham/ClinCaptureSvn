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
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import com.clinovo.util.ValidatorHelper;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.FileUploadHelper;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.crfdata.ImportCRFDataService;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;


/**
 * Create a new CRF verison by uploading excel file. Makes use of several other classes to validate and provide accurate
 * validation. More specifically, uses XmlSchemaValidationHelper, ImportCRFDataService, ODMContainer, and others to
 * import all the XML in the ODM 1.3 standard.
 * 
 */
@SuppressWarnings("serial")
@Component
public class ImportCRFDataServlet extends Controller {

	/**
     *
     * @param request
     * @param response
     */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		checkStudyLocked(Page.MENU_SERVLET, respage.getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.MENU_SERVLET, respage.getString("current_study_frozen"), request, response);

		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
                + respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);
        StudyBean currentStudy = getCurrentStudy(request);
        
        StudyInfoPanel panel= getStudyInfoPanel(request);
        panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);
		// keep the module in the session
		request.getSession().setAttribute(MODULE, module);

		String action = request.getParameter("action");
		CRFVersionBean version = (CRFVersionBean) request.getSession().getAttribute("version");
		
		File xsdFile2 = new File(SpringServletAccess.getPropertiesDir(getServletContext()) + "ODM1-2-1.xsd");

		if (StringUtil.isBlank(action)) {
			logger.info("action is blank");
			request.setAttribute("version", version);
			forwardPage(Page.IMPORT_CRF_DATA, request, response);
		}
		if ("confirm".equalsIgnoreCase(action)) {
			String dir = SQLInitServlet.getField("filePath");
			if (!new File(dir).exists()) {
				logger.info("The filePath in datainfo.properties is invalid " + dir);
				addPageMessage(respage.getString("filepath_you_defined_not_seem_valid"), request);
				forwardPage(Page.IMPORT_CRF_DATA, request, response);
			}
			// All the uploaded files will be saved in filePath/crf/original/
			String theDir = dir + "crf" + File.separator + "original" + File.separator;
			if (!new File(theDir).isDirectory()) {
				new File(theDir).mkdirs();
				logger.info("Made the directory " + theDir);
			}
			File f = null;
			try {
                HashMap errorsMap = new HashMap();
				f = uploadFile(request, errorsMap, theDir, version);
			} catch (Exception e) {
				logger.warn("*** Found exception during file upload***");
				e.printStackTrace();

			}
			if (f == null) {
				forwardPage(Page.IMPORT_CRF_DATA, request, response);
			}
			
			boolean fail = false;
			ODMContainer odmContainer = new ODMContainer();
            request.getSession().removeAttribute("odmContainer");
			JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			//Create SAXSource
			InputSource inputSource = new InputSource(new FileInputStream(f));
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				logger.debug("Found crf data container for study oid: "
						+ odmContainer.getCrfDataPostImportContainer().getStudyOID());
				logger.debug("found length of subject list: "
						+ odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
				addPageMessage(respage.getString("passed_xml_validation"), request);
			} catch (Exception me1) {
				me1.printStackTrace();
				logger.info("found exception with xml transform");
				logger.info("trying 1.2.1");
				try {
                    XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
					schemaValidator.validateAgainstSchema(f, xsdFile2);
					odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				} catch (Exception me2) {
					// not sure if we want to report me2
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("your_xml_is_not_well_formed"));
					Object[] arguments = { me2.getMessage() };
					addPageMessage(mf.format(arguments), request);
					forwardPage(Page.IMPORT_CRF_DATA, request, response);
				}
			}

			List<String> errors = new ImportCRFDataService(getDataSource(), request.getLocale()).validateStudyMetadata(odmContainer, ub.getActiveStudyId());
			if (errors != null) {
				// add to session
				// forward to another page
				logger.info(errors.toString());
				for (String error : errors) {
					addPageMessage(error, request);
				}
				if (errors.size() > 0) {
					// fail = true;
					forwardPage(Page.IMPORT_CRF_DATA, request, response);
				} else {
					addPageMessage(respage.getString("passed_study_check"), request);
					addPageMessage(respage.getString("passed_oid_metadata_check"), request);
				}

			}
			logger.debug("passed error check");

            ImportCRFDataService importCRFDataService = new ImportCRFDataService(getDataSource(), request.getLocale());
			List<EventCRFBean> eventCRFBeans = importCRFDataService.fetchEventCRFBeans(odmContainer, ub);
			List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
			HashMap<String, String> totalValidationErrors = new HashMap<String, String>();
			HashMap<String, String> hardValidationErrors = new HashMap<String, String>();
			
			if (eventCRFBeans == null) {
				fail = true;
				addPageMessage(respage.getString("no_event_status_matching"), request);
			} else {
				ArrayList<Integer> permittedEventCRFIds = new ArrayList<Integer>();
				logger.info("found a list of eventCRFBeans: " + eventCRFBeans.toString());

				logger.debug("found event crfs " + eventCRFBeans.size());
				if (!eventCRFBeans.isEmpty()) {
					for (EventCRFBean eventCRFBean : eventCRFBeans) {
						DataEntryStage dataEntryStage = eventCRFBean.getStage();
						Status eventCRFStatus = eventCRFBean.getStatus();

						logger.info("Event CRF Bean: id " + eventCRFBean.getId() + ", data entry stage "
								+ dataEntryStage.getName() + ", status " + eventCRFStatus.getName());
						if (eventCRFStatus.equals(Status.AVAILABLE)
								|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY)
								|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
								|| dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
								|| dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
							permittedEventCRFIds.add(new Integer(eventCRFBean.getId()));
						} 
					}

					// so that we don't repeat this following message
					// did we exclude all the event CRFs? if not, pass, else fail
					if (eventCRFBeans.size() >= permittedEventCRFIds.size()) {
						addPageMessage(respage.getString("passed_event_crf_status_check"), request);
					} else {
						fail = true;
						addPageMessage(respage.getString("the_event_crf_not_correct_status"), request);
					}

					try {
						List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
						tempDisplayItemBeanWrappers = importCRFDataService.lookupValidationErrors(
								new ValidatorHelper(request, getConfigurationDao()), odmContainer, ub,
								totalValidationErrors, hardValidationErrors, permittedEventCRFIds);
						logger.info("generated display item bean wrappers " + tempDisplayItemBeanWrappers.size());
						logger.info("size of total validation errors: " + totalValidationErrors.size());
						logger.info("size of hard validation errors: " + hardValidationErrors.size());
						displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
					} catch (NullPointerException npe1) {
						// what if you have 2 event crfs but the third is a fake?
						fail = true;
						logger.debug("threw a NPE after calling lookup validation errors");
						addPageMessage(respage.getString("an_error_was_thrown_while_validation_errors"), request);
					} catch (OpenClinicaException oce1) {
						fail = true;
						logger.debug("threw an OCE after calling lookup validation errors "
								+ oce1.getOpenClinicaMessage());
						addPageMessage(oce1.getOpenClinicaMessage(), request);
					}
				} else {
					fail = true;
					addPageMessage(respage.getString("no_event_crfs_matching_the_xml_metadata"), request);
				}
			}
			if (fail) {
				logger.debug("failed here - forwarding...");
				forwardPage(Page.IMPORT_CRF_DATA, request, response);
			} else {
				addPageMessage(respage.getString("passing_crf_edit_checks"), request);
                request.getSession().setAttribute("odmContainer", odmContainer);
                request.getSession().setAttribute("importedData", displayItemBeanWrappers);
                request.getSession().setAttribute("validationErrors", totalValidationErrors);
                request.getSession().setAttribute("hardValidationErrors", hardValidationErrors);

				logger.debug("+++ content of total validation errors: " + totalValidationErrors.toString());
				SummaryStatsBean ssBean = new ImportCRFDataService(getDataSource(), request.getLocale())
						.generateSummaryStatsBean(odmContainer, displayItemBeanWrappers);
                request.getSession().setAttribute("summaryStats", ssBean);
                request.getSession().setAttribute("subjectData", odmContainer.getCrfDataPostImportContainer().getSubjectData());
				if (request.getAttribute("hasSkippedItems") != null) {
					addPageMessage(resword.getString("import_msg_part1")
							+ " "
							+ (currentStudy.getParentStudyId() > 0 ? resword.getString("site") : resword
									.getString("study")) + " " + resword.getString("import_msg_part2"), request);
				}
				forwardPage(Page.VERIFY_IMPORT_SERVLET, request, response);
			}
		}
	}

	/*
	 * Given the MultipartRequest extract the first File validate that it is an xml file and then return it.
	 */
	private File getFirstFile(HttpServletRequest request, HashMap errorsMap) {
		File f = null;
        FileUploadHelper uploadHelper = new FileUploadHelper();
		List<File> files = uploadHelper.returnFiles(request, getServletContext());
		for (File file : files) {
			f = file;
			if (f == null || f.getName() == null) {
				logger.info("file is empty.");
				Validator.addError(errorsMap, "xml_file", "You have to provide an XML file!");
			} else if (f.getName().indexOf(".xml") < 0 && f.getName().indexOf(".XML") < 0) {
				logger.info("file name:" + f.getName());
				// TODO change the message below
				addPageMessage(respage.getString("file_you_uploaded_not_seem_xml_file"), request);
				f = null;
			}
		}
		return f;
	}

	/**
	 * Uploads the xml file
	 * 
	 * @param version
	 * @throws Exception
	 */
	public File uploadFile(HttpServletRequest request, HashMap errorsMap, String theDir, CRFVersionBean version) throws Exception {
		return getFirstFile(request, errorsMap);
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
}
