/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.clinovo.i18n.LocaleResolver;

/**
 * Servlet to call appropriate application pages corresponding to supported RESTful URLs.
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
@Component
public class UrlRewriterServlet extends Controller {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private static final int STUDY_KEY = 0;
	private static final int STUDY_SUBJECT_KEY = 1;
	private static final int EVENT_REPEAT_KEY = 2;
	private static final int FORM_OID_KEY = 3;
	private static final int ITEM_GROUP_OID_KEY = 4;

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {

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
			String restUrlStart = "/ClinicalData/html/view/";
			if ((null != requestURI) && (requestURI.contains("/ClinicalData/html/view/"))) {
				requestOIDStr = requestURI.substring(requestURI.indexOf(restUrlStart) + restUrlStart.length(),
						requestURI.length());
			}

			ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle(LocaleResolver.getLocale(request));
			ocResource = getOpenClinicaResourceFromURL(requestOIDStr, resexception);
			if (null != ocResource) {
				if (ocResource.isInValid()) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					HashMap errors = getErrorsHolder(request);
					Validator.addError(errors, getResWord().getString("error"), ocResource.getMessages().get(0));
					request.setAttribute("formMessages", errors);
				}

				if ((null != ocResource) && (ocResource.getFormVersionOID() != null)) {
					HashMap<String, String> mapQueryParams = getQueryStringParameters(requestQueryStr);

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
			logger.error("Error: " + e.getMessage());
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
	 * Method to parse the request URL parameters and get the respective database identifiers.
	 *
	 */
	public OpenClinicaResource getOpenClinicaResourceFromURL(String URLPath, ResourceBundle resexception)
			throws UnsupportedEncodingException {
		OpenClinicaResource openClinicaResource = new OpenClinicaResource();

		if ((null != URLPath) && (!URLPath.equals(""))) {
			if (URLPath.contains("/")) {
				String[] tokens = URLPath.split("/");
				if (tokens.length != 0) {
					String urlParamValue = "";
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

						urlParamValue = URLDecoder.decode(tokens[i].trim(), "UTF-8").trim();
						logger.info("URLPAramValue::" + urlParamValue);
						if ((null != urlParamValue) && (!urlParamValue.equals(""))) {
							switch (i) {
								case STUDY_KEY : {
									study = stdao.findByOid(urlParamValue);
									if (study == null) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages().add(
												resexception.getString("invalid_study_oid"));
										return openClinicaResource;
									} else {
										openClinicaResource.setStudyOID(urlParamValue);
										if (null != study) {
											openClinicaResource.setStudyID(study.getId());
										}
									}
									break;
								}

								case STUDY_SUBJECT_KEY : { // StudySubjectKey
									subject = ssubdao.findByOidAndStudy(urlParamValue, study.getId());
									if (subject == null) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages().add(
												resexception.getString("invalid_subject_oid"));
										return openClinicaResource;
									} else {
										openClinicaResource.setStudySubjectOID(urlParamValue);
										if (null != subject) {
											studySubjectId = subject.getId();
											openClinicaResource.setStudySubjectID(studySubjectId);
										}
									}
									break;
								}

								case EVENT_REPEAT_KEY : {
									// repeat key
									String seoid = "";
									eventRepeatKey = null;
									String eventOrdinal = "";
									if (urlParamValue.contains("%5B") && urlParamValue.contains("%5D")) {
										seoid = urlParamValue.substring(0, urlParamValue.indexOf("%5B"));
										openClinicaResource.setStudyEventDefOID(seoid);
										eventOrdinal = urlParamValue.substring(urlParamValue.indexOf("%5B") + 3,
												urlParamValue.indexOf("%5D"));
										eventRepeatKey = Integer.parseInt(eventOrdinal.trim());
									} else if (urlParamValue.contains("[") && urlParamValue.contains("]")) {
										seoid = urlParamValue.substring(0, urlParamValue.indexOf("["));
										logger.info("seoid" + seoid);
										openClinicaResource.setStudyEventDefOID(seoid);
										eventOrdinal = urlParamValue.substring(urlParamValue.indexOf("[") + 1,
												urlParamValue.indexOf("]"));
										logger.info("eventOrdinal::" + eventOrdinal);
										eventRepeatKey = Integer.parseInt(eventOrdinal.trim());
									} else {
										seoid = urlParamValue;
										openClinicaResource.setStudyEventDefOID(seoid);
										logger.info("seoid" + seoid);
									}
									if ((null != seoid) && (null != study)) {
										sed = sedefdao
												.findByOidAndStudy(seoid, study.getId(), study.getParentStudyId());
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
										studyEvent = (StudyEventBean) sedao
												.findByStudySubjectIdAndDefinitionIdAndOrdinal(subject.getId(),
														sed.getId(), eventRepeatKey);
										// this method return new StudyEvent (not null) even if no studyEvent can be
										// found
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

								case FORM_OID_KEY : { // form OID

									openClinicaResource.setFormVersionOID(urlParamValue);

									cv = crfvdao.findByOid(urlParamValue);
									if (cv == null) {
										openClinicaResource.setInValid(true);
										openClinicaResource.getMessages()
												.add(resexception.getString("invalid_crf_oid"));
										return openClinicaResource;
									} else {
										openClinicaResource.setFormVersionID(cv.getId());
										// validate if crf is removed
										if (cv.getStatus().equals(Status.DELETED)) {
											openClinicaResource.setInValid(true);
											openClinicaResource.getMessages()
													.add(resexception.getString("removed_crf"));
											return openClinicaResource;
										} else {
											if (null != study) {
												HashMap studySubjectCRFDataDetails = sedao.getStudySubjectCRFData(
														study, studySubjectId, eventDefId, urlParamValue,
														eventRepeatKey);
												if ((null != studySubjectCRFDataDetails)
														&& (studySubjectCRFDataDetails.size() != 0)) {
													if (studySubjectCRFDataDetails.containsKey("event_crf_id")) {
														openClinicaResource
																.setEventCrfId((Integer) studySubjectCRFDataDetails
																		.get("event_crf_id"));
													}

													if (studySubjectCRFDataDetails
															.containsKey("event_definition_crf_id")) {
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

								case ITEM_GROUP_OID_KEY : {
									String igoid = "";
									String igRepeatKey = "";
									if (urlParamValue.contains("[")) {
										igoid = urlParamValue.substring(1, urlParamValue.indexOf("["));
										igRepeatKey = urlParamValue.substring(urlParamValue.indexOf("["),
												urlParamValue.indexOf("}]"));
									}
									if ((null != igoid) && (null != cv)) {
										ig = igdao.findByOidAndCrf(urlParamValue, cv.getCrfId());

										if (null != ig) {
											openClinicaResource.setItemGroupID(ig.getId());
										}
									}
									if (null != igRepeatKey) {
										openClinicaResource.setItemGroupRepeatKey(Integer.parseInt(igRepeatKey));
									}
									break;
								}

								default : {
									break;
								}
							}
						}
					}
				}
			}
		}

		return openClinicaResource;
	}
}
