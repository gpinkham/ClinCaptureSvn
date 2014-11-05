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

/**
 *
 */
package org.akaza.openclinica.control.urlRewrite;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Servlet to call appropriate application pages corresponding to supported RESTful URLs
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class UrlRewriterServlet extends Controller {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {

			String requestURI = request.getRequestURI();
			String requestQueryStr = request.getQueryString();

			OpenClinicaResource ocResource = null;
			String requestOIDStr = null;
			String RESTUrlStart = "/ClinicalData/html/view/";
			if ((null != requestURI) && (requestURI.contains("/ClinicalData/html/view/"))) {
				requestOIDStr = requestURI.substring(requestURI.indexOf(RESTUrlStart) + RESTUrlStart.length(),
						requestURI.length());
			}

			ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(SessionUtil.getLocale(request));
			ocResource = getOpenClinicaResourceFromURL(requestOIDStr, resexception);
			if (null != ocResource) {
				if (ocResource.isInValid()) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					HashMap errors = getErrorsHolder(request);
					Validator.addError(errors, "error:", ocResource.getMessages().get(0));
					request.setAttribute("formMessages", errors);
				}

				// If the form OID in the request uri is not null, it will be
				// interpretted as a request to
				// view form data and hence will be forwarded to servlet path
				// "/ViewSectionDataEntry"
				if ((null != ocResource) && (ocResource.getFormVersionOID() != null)) {
					HashMap<String, String> mapQueryParams = getQueryStringParameters(requestQueryStr);

					// set the required parameters into request
					if (null != ocResource.getEventDefinitionCrfId()) {
						request.setAttribute("eventDefinitionCRFId", ocResource.getEventDefinitionCrfId());
					}
					if (null != ocResource.getEventCrfId()) {
						request.setAttribute("eventCRFId", ocResource.getEventCrfId().toString());
					}
					if (null != ocResource.getStudyEventId()) {
						request.setAttribute("eventId", ocResource.getStudyEventId().toString());
						request.setAttribute("exitTo", "EnterDataForStudyEvent?eventId=" + ocResource.getStudyEventId());
					}
					if (null != ocResource.getStudySubjectID()) {
						request.setAttribute("studySubjectId", ocResource.getStudySubjectID().toString());
						if (request.getAttribute("exitTo") == null) {
							request.setAttribute("exitTo", "ViewStudySubject?id=" + ocResource.getStudySubjectID());
						}
					}
					if ((null != mapQueryParams) && (mapQueryParams.size() != 0)) {
						if (mapQueryParams.containsKey("tabId")) {
							request.setAttribute("tabId", mapQueryParams.get("tabId"));
						}
						if ((null != ocResource.getStudySubjectID()) && (mapQueryParams.containsKey("exitTo"))) {
							// request.setAttribute("exitTo", "ViewStudySubject?id=" + ocResource.getStudySubjectID());
						}
						SectionDAO sdao = getSectionDAO();
						if (mapQueryParams.containsKey("tabId")) {
							HashMap sectionIdMap = sdao.getSectionIdForTabId(ocResource.getFormVersionID(),
									Integer.parseInt(mapQueryParams.get("tabId")));
							Integer sectionId = null;
							if ((sectionIdMap != null) && (sectionIdMap.size() != 0)) {
								sectionId = (Integer) sectionIdMap.get("section_id");
							}
							if (null != sectionId) {
								request.setAttribute("sectionId", sectionId);
							}

						}
					}
					forwardPage(Page.VIEW_SECTION_DATA_ENTRY_SERVLET_REST_URL, request, response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, String> getQueryStringParameters(String queryString) {
		HashMap<String, String> mapQueryParams = new HashMap<String, String>();

		if ((null != queryString) && (!queryString.equalsIgnoreCase(""))) {
			if (queryString.contains("&")) {
				String[] tokens = queryString.split("&");
				if (tokens.length != 0) {
					String token = null;
					String tokenBeforeEq = null;
					String tokenAfterEq = null;
					for (int i = 0; i < tokens.length; i++) {
						token = tokens[i];
						if ((null != token) && (!token.equalsIgnoreCase("")) && (token.contains("="))) {
							tokenBeforeEq = token.substring(0, token.indexOf("="));
							tokenAfterEq = token.substring(token.indexOf("=") + 1);
							mapQueryParams.put(tokenBeforeEq, tokenAfterEq);
						}
					}
				}
			} else {
				if (queryString.contains("=")) {
					mapQueryParams.put(queryString.substring(0, queryString.indexOf("=")),
							queryString.substring(queryString.indexOf("=") + 1));
				}
			}
		}
		return mapQueryParams;
	}

	/**
	 * Method to parse the request URL parameters and get the respective database identifiers
	 * 
	 * @param URLPath
	 *            - example "S_CPCS/320999/SE_CPCS%5B1%5D/F_CPCS_1"
	 * @return
	 */
	public OpenClinicaResource getOpenClinicaResourceFromURL(String URLPath, ResourceBundle resexception)
			throws UnsupportedEncodingException {
		OpenClinicaResource openClinicaResource = new OpenClinicaResource();

		if ((null != URLPath) && (!URLPath.equals(""))) {
			if (URLPath.contains("/")) {
				String[] tokens = URLPath.split("/");
				if (tokens.length != 0) {
					String URLParamValue = "";
					StudyDAO stdao = getStudyDAO();
					StudySubjectDAO ssubdao = getStudySubjectDAO();
					StudyEventDefinitionDAO sedefdao = getStudyEventDefinitionDAO();
					CRFVersionDAO crfvdao = getCRFVersionDAO();
					ItemGroupDAO igdao = getItemGroupDAO();
					StudyEventDAO sedao = getStudyEventDAO();

					StudyBean study = null;
					StudySubjectBean subject = null;
					StudyEventDefinitionBean sed = null;
					CRFVersionBean cv = null;
					ItemGroupBean ig = null;
					StudyEventBean studyEvent = null;

					Integer studySubjectId = 0;
					Integer eventDefId = 0;
					Integer eventRepeatKey = 0;

					for (int i = 0; i < tokens.length; i++) {

						URLParamValue = URLDecoder.decode(tokens[i].trim(), "UTF-8").trim();
						logger.info("URLPAramValue::" + URLParamValue);
						if ((null != URLParamValue) && (!URLParamValue.equals(""))) {
							switch (i) {
							case 0: {// study OID
								study = stdao.findByOid(URLParamValue);
								// validate study OID
								if (study == null) {
									openClinicaResource.setInValid(true);
									openClinicaResource.getMessages().add(resexception.getString("invalid_study_oid"));
									return openClinicaResource;
								} else {
									openClinicaResource.setStudyOID(URLParamValue);
									if (null != study) {
										openClinicaResource.setStudyID(study.getId());
									}
								}
								break;
							}

							case 1: {// StudySubjectKey
								subject = ssubdao.findByOidAndStudy(URLParamValue, study.getId());
								// validate subject OID
								if (subject == null) {
									openClinicaResource.setInValid(true);
									openClinicaResource.getMessages()
											.add(resexception.getString("invalid_subject_oid"));
									return openClinicaResource;
								} else {
									openClinicaResource.setStudySubjectOID(URLParamValue);
									if (null != subject) {
										studySubjectId = subject.getId();
										openClinicaResource.setStudySubjectID(studySubjectId);
									}
								}
								break;
							}

							case 2: {// study event definition OID
								// separate study event OID and study event
								// repeat key
								String seoid = "";
								eventRepeatKey = null;
								String eventOrdinal = "";
								if (URLParamValue.contains("%5B") && URLParamValue.contains("%5D")) {
									seoid = URLParamValue.substring(0, URLParamValue.indexOf("%5B"));
									openClinicaResource.setStudyEventDefOID(seoid);
									eventOrdinal = URLParamValue.substring(URLParamValue.indexOf("%5B") + 3,
											URLParamValue.indexOf("%5D"));
									eventRepeatKey = Integer.parseInt(eventOrdinal.trim());
								} else if (URLParamValue.contains("[") && URLParamValue.contains("]")) {
									seoid = URLParamValue.substring(0, URLParamValue.indexOf("["));
									logger.info("seoid" + seoid);
									openClinicaResource.setStudyEventDefOID(seoid);
									eventOrdinal = URLParamValue.substring(URLParamValue.indexOf("[") + 1,
											URLParamValue.indexOf("]"));
									logger.info("eventOrdinal::" + eventOrdinal);
									eventRepeatKey = Integer.parseInt(eventOrdinal.trim());
								} else {
									seoid = URLParamValue;
									openClinicaResource.setStudyEventDefOID(seoid);
									logger.info("seoid" + seoid);
								}
								if ((null != seoid) && (null != study)) {
									sed = sedefdao.findByOidAndStudy(seoid, study.getId(), study.getParentStudyId());
									// validate study event oid
									if (null == sed) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages().add(
												resexception.getString("invalid_event_oid"));
										return openClinicaResource;
									} else {
										eventDefId = sed.getId();
										openClinicaResource.setStudyEventDefID(eventDefId);
									}
								}
								if (null != eventRepeatKey) {
									// validate the event ordinal specified exists in database
									studyEvent = (StudyEventBean) sedao.findByStudySubjectIdAndDefinitionIdAndOrdinal(
											subject.getId(), sed.getId(), eventRepeatKey);
									// this method return new StudyEvent (not null) even if no studyEvent can be found
									if (null == studyEvent || studyEvent.getId() == 0) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages().add(
												resexception.getString("invalid_event_ordinal"));
										return openClinicaResource;
									} else {
										openClinicaResource.setStudyEventRepeatKey(eventRepeatKey);
									}
								} else {
									eventRepeatKey = 1;
									openClinicaResource.setStudyEventRepeatKey(eventRepeatKey);
								}
								break;
							}

							case 3: {// form OID
								openClinicaResource.setFormVersionOID(URLParamValue);
								// validate the crf version oid
								cv = crfvdao.findByOid(URLParamValue);
								if (cv == null) {
									openClinicaResource.setInValid(true);
									openClinicaResource.getMessages().add(resexception.getString("invalid_crf_oid"));
									return openClinicaResource;
								} else {
									openClinicaResource.setFormVersionID(cv.getId());
									// validate if crf is removed
									if (cv.getStatus().equals(Status.DELETED)) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages().add(resexception.getString("removed_crf"));
										return openClinicaResource;
									} else {
										if (null != study) {
											HashMap studySubjectCRFDataDetails = sedao.getStudySubjectCRFData(study,
													studySubjectId, eventDefId, URLParamValue, eventRepeatKey);
											if ((null != studySubjectCRFDataDetails)
													&& (studySubjectCRFDataDetails.size() != 0)) {
												if (studySubjectCRFDataDetails.containsKey("event_crf_id")) {
													openClinicaResource
															.setEventCrfId((Integer) studySubjectCRFDataDetails
																	.get("event_crf_id"));
												}

												if (studySubjectCRFDataDetails.containsKey("event_definition_crf_id")) {
													openClinicaResource
															.setEventDefinitionCrfId((Integer) studySubjectCRFDataDetails
																	.get("event_definition_crf_id"));
												}

												if (studySubjectCRFDataDetails.containsKey("study_event_id")) {
													openClinicaResource
															.setStudyEventId((Integer) studySubjectCRFDataDetails
																	.get("study_event_id"));
												}
											} else {
												openClinicaResource.setInValid(true);
												openClinicaResource
														.getMessages()
														.add(resexception
																.getString("either_no_data_for_crf_or_data_entry_not_started"));
												return openClinicaResource;
											}
										}
									}
								}
								break;
							}

							case 4: {
								String igoid = "";
								String igRepeatKey = "";
								if (URLParamValue.contains("[")) {
									igoid = URLParamValue.substring(1, URLParamValue.indexOf("["));
									igRepeatKey = URLParamValue.substring(URLParamValue.indexOf("["),
											URLParamValue.indexOf("}]"));
								}
								if ((null != igoid) && (null != cv)) {
									ig = igdao.findByOidAndCrf(URLParamValue, cv.getCrfId());

									if (null != ig) {
										openClinicaResource.setItemGroupID(ig.getId());
									}
								}
								if (null != igRepeatKey) {
									openClinicaResource.setItemGroupRepeatKey(Integer.parseInt(igRepeatKey));
								}
								break;
							}

							case 5: {// item OID
								// item = idao.find
								break;
							}
							}// switch end
						}
					}
				}
			}
		}

		return openClinicaResource;
	}
}
