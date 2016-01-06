/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.rest.service;

import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParameterPossibleValuesHolder;
import com.clinovo.rest.annotation.RestProvideAtLeastOneNotRequired;
import com.clinovo.rest.model.Response;
import com.clinovo.rest.service.base.BaseEventService;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.EDCItemMetadataService;
import com.clinovo.service.EventDefinitionCrfService;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.validator.EventDefinitionValidator;

/**
 * RestEventService.
 */
@Controller("restEventService")
@RequestMapping("/event")
@SuppressWarnings("rawtypes")
public class EventService extends BaseEventService {

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

	/**
	 * Method returns info about study event definition.
	 *
	 * @param id
	 *            int
	 * @return StudyEventDefinitionBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public StudyEventDefinitionBean getInfo(@RequestParam(value = "id") int id) throws Exception {
		return eventDefinitionService.fillEventDefinitionCrfs(getStudyEventDefinition(id), getCurrentStudy());
	}

	/**
	 * Method that creates new study event definition.
	 *
	 * @param name
	 *            String
	 * @param type
	 *            String
	 * @param description
	 *            String
	 * @param repeating
	 *            int
	 * @param category
	 *            String
	 * @param isReference
	 *            boolean
	 * @param schDay
	 *            int
	 * @param dayMax
	 *            int
	 * @param dayMin
	 *            int
	 * @param emailDay
	 *            int
	 * @param emailUser
	 *            String
	 * @return StudyEventDefinitionBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "type", values = "scheduled,unscheduled,common,calendared_visit")})
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public StudyEventDefinitionBean createEvent(@RequestParam("name") String name,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "description", defaultValue = "", required = false) String description,
			@RequestParam(value = "repeating", defaultValue = "false", required = false) boolean repeating,
			@RequestParam(value = "category", defaultValue = "", required = false) String category,
			@RequestParam(value = "isReference", defaultValue = "false", required = false) boolean isReference,
			@RequestParam(value = "schDay", defaultValue = "0", required = false) int schDay,
			@RequestParam(value = "maxDay", defaultValue = "0", required = false) int dayMax,
			@RequestParam(value = "minDay", defaultValue = "0", required = false) int dayMin,
			@RequestParam(value = "emailDay", defaultValue = "0", required = false) int emailDay,
			@RequestParam(value = "emailUser", defaultValue = "", required = false) String emailUser) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = createStudyEventDefinition(name, type, description,
				repeating, category, isReference, schDay, dayMax, dayMin, emailDay, emailUser);

		HashMap errors = EventDefinitionValidator.validate(configurationDao, getUserAccountDAO(), getCurrentStudy());

		ValidatorUtil.checkForErrors(errors);

		return createStudyEventDefinition(studyEventDefinitionBean);
	}

	/**
	 * Method that edits study event definition.
	 *
	 * @param id
	 *            int
	 * @param name
	 *            String
	 * @param type
	 *            String
	 * @param description
	 *            String
	 * @param repeating
	 *            int
	 * @param category
	 *            String
	 * @param isReference
	 *            boolean
	 * @param schDay
	 *            int
	 * @param dayMax
	 *            int
	 * @param dayMin
	 *            int
	 * @param emailDay
	 *            int
	 * @param emailUser
	 *            String
	 * @return StudyEventDefinitionBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestProvideAtLeastOneNotRequired
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "type", canBeNotSpecified = true, values = "scheduled,unscheduled,common,calendared_visit")})
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public StudyEventDefinitionBean editEvent(@RequestParam(value = "id") int id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "repeating", required = false) Boolean repeating,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "isReference", required = false) Boolean isReference,
			@RequestParam(value = "schDay", required = false) Integer schDay,
			@RequestParam(value = "maxDay", required = false) Integer dayMax,
			@RequestParam(value = "minDay", required = false) Integer dayMin,
			@RequestParam(value = "emailDay", required = false) Integer emailDay,
			@RequestParam(value = "emailUser", required = false) String emailUser) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = editStudyEventDefinition(id, name, type, description,
				repeating, category, isReference, schDay, dayMax, dayMin, emailDay, emailUser);

		HashMap errors = EventDefinitionValidator.validate(configurationDao, getUserAccountDAO(), getCurrentStudy());

		ValidatorUtil.checkForErrors(errors);

		return eventDefinitionService.updateOnlyTheStudyEventDefinition(studyEventDefinitionBean, getCurrentUser());
	}

	/**
	 * Method removes the study event definition.
	 *
	 * @param id
	 *            int
	 * @return StudyEventDefinitionBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public StudyEventDefinitionBean remove(@RequestParam(value = "id") int id) throws Exception {
		return eventDefinitionService.removeStudyEventDefinition(getStudyEventDefinition(id), getCurrentUser());
	}

	/**
	 * Method restores the study event definition.
	 *
	 * @param id
	 *            int
	 * @return StudyEventDefinitionBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/restore", method = RequestMethod.POST)
	public StudyEventDefinitionBean restore(@RequestParam(value = "id") int id) throws Exception {
		return eventDefinitionService.restoreStudyEventDefinition(getStudyEventDefinition(id), getCurrentUser());
	}

	/**
	 * Method adds crf to the study event definition.
	 *
	 * @param eventId
	 *            int
	 * @param crfName
	 *            String
	 * @param required
	 *            boolean
	 * @param passwordRequired
	 *            boolean
	 * @param defaultVersion
	 *            String
	 * @param hide
	 *            boolean
	 * @param sourceDataVerification
	 *            int
	 * @param dataEntryQuality
	 *            String
	 * @param emailWhen
	 *            String
	 * @param email
	 *            String
	 * @param tabbing
	 *            String
	 * @param acceptNewCrfVersions
	 *            boolean
	 * @return EventDefinitionCRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "sourceDataVerification", values = "1,2,3", valueDescriptions = "rest.sourceDataVerification.valueDescription"),
			@RestParameterPossibleValues(name = "dataEntryQuality", canBeNotSpecified = true, values = "dde,evaluation", valueDescriptions = "rest.dataEntryQuality.valueDescription"),
			@RestParameterPossibleValues(name = "emailWhen", canBeNotSpecified = true, values = "complete,sign", valueDescriptions = "rest.emailWhen.valueDescription"),
			@RestParameterPossibleValues(name = "tabbing", values = "leftToRight,topToBottom")})
	@RequestMapping(value = "/addCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean addCrf(@RequestParam(value = "eventId") int eventId,
			@RequestParam("crfName") String crfName, @RequestParam("defaultVersion") String defaultVersion,
			@RequestParam(value = "required", defaultValue = "true", required = false) boolean required,
			@RequestParam(value = "passwordRequired", defaultValue = "false", required = false) boolean passwordRequired,
			@RequestParam(value = "hide", defaultValue = "false", required = false) boolean hide,
			@RequestParam(value = "sourceDataVerification", defaultValue = "3", required = false) int sourceDataVerification,
			@RequestParam(value = "dataEntryQuality", defaultValue = "", required = false) String dataEntryQuality,
			@RequestParam(value = "emailWhen", defaultValue = "", required = false) String emailWhen,
			@RequestParam(value = "email", defaultValue = "", required = false) String email,
			@RequestParam(value = "tabbing", defaultValue = "leftToRight", required = false) String tabbing,
			@RequestParam(value = "acceptNewCrfVersions", defaultValue = "false", required = false) boolean acceptNewCrfVersions)
					throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId);

		EventDefinitionCRFBean eventDefinitionCrfBean = createEventDefinitionCRF(studyEventDefinitionBean,
				getCurrentUser(), crfName, defaultVersion, required, passwordRequired, hide, sourceDataVerification,
				dataEntryQuality, emailWhen, email, tabbing, acceptNewCrfVersions);

		HashMap errors = EventDefinitionValidator.validateNewEDC(messageSource, dataSource, eventId, defaultVersion,
				crfName, sourceDataVerification, emailWhen, email, studyEventDefinitionBean, getCurrentStudy());

		ValidatorUtil.checkForErrors(errors);

		return addEventDefinitionCRF(eventDefinitionCrfBean);
	}

	/**
	 * Method edits crf of study level in the study event definition.
	 *
	 * @param eventId
	 *            int
	 * @param crfName
	 *            String
	 * @param required
	 *            boolean
	 * @param passwordRequired
	 *            boolean
	 * @param defaultVersion
	 *            String
	 * @param hide
	 *            boolean
	 * @param sourceDataVerification
	 *            int
	 * @param dataEntryQuality
	 *            String
	 * @param emailWhen
	 *            String
	 * @param email
	 *            String
	 * @param tabbing
	 *            String
	 * @param acceptNewCrfVersions
	 *            boolean
	 * @param propagateChange
	 *            int
	 * @return EventDefinitionCRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RestProvideAtLeastOneNotRequired
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "sourceDataVerification", canBeNotSpecified = true, values = "1,2,3", valueDescriptions = "rest.sourceDataVerification.valueDescription"),
			@RestParameterPossibleValues(name = "dataEntryQuality", canBeNotSpecified = true, values = "dde,evaluation,none", valueDescriptions = "rest.dataEntryQualityWithNone.valueDescription"),
			@RestParameterPossibleValues(name = "emailWhen", canBeNotSpecified = true, values = "complete,sign,none", valueDescriptions = "rest.emailWhenWithNone.valueDescription"),
			@RestParameterPossibleValues(name = "tabbing", canBeNotSpecified = true, values = "leftToRight,topToBottom"),
			@RestParameterPossibleValues(name = "propagateChange", canBeNotSpecified = true, values = "1,2,3", valueDescriptions = "rest.propagateChange.valueDescription")})
	@RequestMapping(value = "/editStudyCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean editStudyCrf(@RequestParam(value = "eventId") int eventId,
			@RequestParam("crfName") String crfName,
			@RequestParam(value = "defaultVersion", required = false) String defaultVersion,
			@RequestParam(value = "required", required = false) Boolean required,
			@RequestParam(value = "passwordRequired", required = false) Boolean passwordRequired,
			@RequestParam(value = "hide", required = false) Boolean hide,
			@RequestParam(value = "sourceDataVerification", required = false) Integer sourceDataVerification,
			@RequestParam(value = "dataEntryQuality", required = false) String dataEntryQuality,
			@RequestParam(value = "emailWhen", required = false) String emailWhen,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "tabbing", required = false) String tabbing,
			@RequestParam(value = "acceptNewCrfVersions", required = false) Boolean acceptNewCrfVersions,
			@RequestParam(value = "propagateChange", required = false) Integer propagateChange) throws Exception {
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(eventId);

		EventDefinitionCRFBean eventDefinitionCRFBean = editParentEventDefinitionCRF(eventId, crfName, defaultVersion,
				required, passwordRequired, hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing,
				acceptNewCrfVersions, propagateChange);

		HashMap errors = EventDefinitionValidator.validateEDC(messageSource, dataSource, eventId,
				studyEventDefinitionBean, eventDefinitionCRFBean, getCurrentStudy(), edcItemMetadataService);

		ValidatorUtil.checkForErrors(errors);

		return updateParentEventDefinitionCRF(studyEventDefinitionBean, eventDefinitionCRFBean);
	}

	/**
	 * Method removes the event definition crf.
	 *
	 * @param eventId
	 *            int
	 * @param crfName
	 *            String
	 * @return EventDefinitionCRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/removeCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean removeCrf(@RequestParam("eventId") int eventId,
			@RequestParam("crfName") String crfName) throws Exception {
		return eventDefinitionCrfService.removeParentEventDefinitionCrf(
				getEventDefinitionCRF(eventId, crfName, getCurrentStudy(), false), getCurrentUser());
	}

	/**
	 * Method restores the event definition crf.
	 *
	 * @param eventId
	 *            int
	 * @param crfName
	 *            String
	 * @return EventDefinitionCRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/restoreCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean restoreCrf(@RequestParam("eventId") int eventId,
			@RequestParam("crfName") String crfName) throws Exception {
		return eventDefinitionCrfService.restoreParentEventDefinitionCrf(
				getEventDefinitionCRF(eventId, crfName, getCurrentStudy(), false), getCurrentUser());
	}

	/**
	 * Method deletes the event definition crf.
	 *
	 * @param eventId
	 *            int
	 * @param crfName
	 *            String
	 * @return Response
	 * @throws Exception
	 *             an Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteCrf", method = RequestMethod.POST)
	public Response deleteCrf(@RequestParam("eventId") int eventId, @RequestParam("crfName") String crfName)
			throws Exception {
		return deleteEventDefinitionCRF(eventId, crfName);
	}
}