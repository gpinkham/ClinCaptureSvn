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

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.rule.FileUploadHelper;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.core.LocaleResolver;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.crfdata.ImportCRFDataService;
import org.xml.sax.InputSource;


/**
 * Create a new CRF verison by uploading excel file. Makes use of several other classes to validate and provide accurate
 * validation. More specifically, uses XmlSchemaValidationHelper, ImportCRFDataService, ODMContainer, and others to
 * import all the XML in the ODM 1.3 standard.
 * 
 */
@SuppressWarnings("serial")
public class ImportCRFDataServlet extends SecureController {

	Locale locale;

	private ImportCRFDataService dataService;

	XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
	FileUploadHelper uploadHelper = new FileUploadHelper();

	/**
     *
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		checkStudyLocked(Page.MENU_SERVLET, respage.getString("current_study_locked"));
		checkStudyFrozen(Page.MENU_SERVLET, respage.getString("current_study_frozen"));

		locale = LocaleResolver.getLocale(request);
		if (ub.isSysAdmin()) {
			return;
		}

		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR) || r.equals(Role.INVESTIGATOR)
				|| r.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest() throws Exception {
		resetPanel();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);
		// keep the module in the session
		session.setAttribute(MODULE, module);

		String action = request.getParameter("action");
		CRFVersionBean version = (CRFVersionBean) session.getAttribute("version");
		
		File xsdFile2 = new File(SpringServletAccess.getPropertiesDir(context) + "ODM1-2-1.xsd");

		if (StringUtil.isBlank(action)) {
			logger.info("action is blank");
			request.setAttribute("version", version);
			forwardPage(Page.IMPORT_CRF_DATA);
		}
		if ("confirm".equalsIgnoreCase(action)) {
			String dir = SQLInitServlet.getField("filePath");
			if (!new File(dir).exists()) {
				logger.info("The filePath in datainfo.properties is invalid " + dir);
				addPageMessage(respage.getString("filepath_you_defined_not_seem_valid"));
				forwardPage(Page.IMPORT_CRF_DATA);
			}
			// All the uploaded files will be saved in filePath/crf/original/
			String theDir = dir + "crf" + File.separator + "original" + File.separator;
			if (!new File(theDir).isDirectory()) {
				new File(theDir).mkdirs();
				logger.info("Made the directory " + theDir);
			}
			File f = null;
			try {
				f = uploadFile(theDir, version);

			} catch (Exception e) {
				logger.warn("*** Found exception during file upload***");
				e.printStackTrace();

			}
			if (f == null) {
				forwardPage(Page.IMPORT_CRF_DATA);
			}
			
			boolean fail = false;
			ODMContainer odmContainer = new ODMContainer();
			session.removeAttribute("odmContainer");
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
				addPageMessage(respage.getString("passed_xml_validation"));
			} catch (Exception me1) {
				me1.printStackTrace();
				logger.info("found exception with xml transform");
				logger.info("trying 1.2.1");
				try {
					schemaValidator.validateAgainstSchema(f, xsdFile2);
					odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				} catch (Exception me2) {
					// not sure if we want to report me2
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("your_xml_is_not_well_formed"));
					Object[] arguments = { me2.getMessage() };
					addPageMessage(mf.format(arguments));
					forwardPage(Page.IMPORT_CRF_DATA);
				}
			}

			List<String> errors = getImportCRFDataService().validateStudyMetadata(odmContainer, ub.getActiveStudyId());
			if (errors != null) {
				// add to session
				// forward to another page
				logger.info(errors.toString());
				for (String error : errors) {
					addPageMessage(error);
				}
				if (errors.size() > 0) {
					// fail = true;
					forwardPage(Page.IMPORT_CRF_DATA);
				} else {
					addPageMessage(respage.getString("passed_study_check"));
					addPageMessage(respage.getString("passed_oid_metadata_check"));
				}

			}
			logger.debug("passed error check");

			List<EventCRFBean> eventCRFBeans = getImportCRFDataService().fetchEventCRFBeans(odmContainer, ub);
			List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
			HashMap<String, String> totalValidationErrors = new HashMap<String, String>();
			HashMap<String, String> hardValidationErrors = new HashMap<String, String>();
			
			if (eventCRFBeans == null) {
				fail = true;
				addPageMessage(respage.getString("no_event_status_matching"));
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
						addPageMessage(respage.getString("passed_event_crf_status_check"));
					} else {
						fail = true;
						addPageMessage(respage.getString("the_event_crf_not_correct_status"));
					}

					try {
						List<DisplayItemBeanWrapper> tempDisplayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();
						tempDisplayItemBeanWrappers = getImportCRFDataService().lookupValidationErrors(request,
								odmContainer, ub, totalValidationErrors, hardValidationErrors, permittedEventCRFIds);
						logger.debug("generated display item bean wrappers " + tempDisplayItemBeanWrappers.size());
						logger.debug("size of total validation errors: " + totalValidationErrors.size());
						displayItemBeanWrappers.addAll(tempDisplayItemBeanWrappers);
					} catch (NullPointerException npe1) {
						// what if you have 2 event crfs but the third is a fake?
						fail = true;
						logger.debug("threw a NPE after calling lookup validation errors");
						addPageMessage(respage.getString("an_error_was_thrown_while_validation_errors"));
					} catch (OpenClinicaException oce1) {
						fail = true;
						logger.debug("threw an OCE after calling lookup validation errors "
								+ oce1.getOpenClinicaMessage());
						addPageMessage(oce1.getOpenClinicaMessage());
					}
				} else {
					fail = true;
					addPageMessage(respage.getString("no_event_crfs_matching_the_xml_metadata"));
				}
			}
			if (fail) {
				logger.debug("failed here - forwarding...");
				forwardPage(Page.IMPORT_CRF_DATA);
			} else {
				addPageMessage(respage.getString("passing_crf_edit_checks"));
				session.setAttribute("odmContainer", odmContainer);
				session.setAttribute("importedData", displayItemBeanWrappers);
				session.setAttribute("validationErrors", totalValidationErrors);
				session.setAttribute("hardValidationErrors", hardValidationErrors);

				logger.debug("+++ content of total validation errors: " + totalValidationErrors.toString());
				SummaryStatsBean ssBean = getImportCRFDataService().generateSummaryStatsBean(odmContainer,
						displayItemBeanWrappers);
				session.setAttribute("summaryStats", ssBean);
				session.setAttribute("subjectData", odmContainer.getCrfDataPostImportContainer().getSubjectData());
				forwardPage(Page.VERIFY_IMPORT_SERVLET);
			}
		}
	}

	/*
	 * Given the MultipartRequest extract the first File validate that it is an xml file and then return it.
	 */
	private File getFirstFile() {
		File f = null;
		List<File> files = uploadHelper.returnFiles(request, context);
		for (File file : files) {
			f = file;
			if (f == null || f.getName() == null) {
				logger.info("file is empty.");
				Validator.addError(errors, "xml_file", "You have to provide an XML file!");
			} else if (f.getName().indexOf(".xml") < 0 && f.getName().indexOf(".XML") < 0) {
				logger.info("file name:" + f.getName());
				// TODO change the message below
				addPageMessage(respage.getString("file_you_uploaded_not_seem_xml_file"));
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
	public File uploadFile(String theDir, CRFVersionBean version) throws Exception {

		return getFirstFile();
	}

	public ImportCRFDataService getImportCRFDataService() {
		dataService = this.dataService != null ? dataService : new ImportCRFDataService(sm.getDataSource(), locale);
		return dataService;
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}
}
