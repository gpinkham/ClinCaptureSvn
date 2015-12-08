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

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestIgnoreDefaultValues;
import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParameterPossibleValuesHolder;
import com.clinovo.rest.annotation.RestProvideAtLeastOneNotRequired;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
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
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

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
			@RequestParam(value = "isreference", defaultValue = "false", required = false) boolean isReference,
			@RequestParam(value = "schday", defaultValue = "0", required = false) int schDay,
			@RequestParam(value = "maxday", defaultValue = "0", required = false) int dayMax,
			@RequestParam(value = "minday", defaultValue = "0", required = false) int dayMin,
			@RequestParam(value = "emailday", defaultValue = "0", required = false) int emailDay,
			@RequestParam(value = "emailuser", defaultValue = "", required = false) String emailUser) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		UserAccountBean owner = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);

		StudyEventDefinitionBean studyEventDefinitionBean = prepareNewStudyEventDefinition(name, type, description,
				repeating, category, isReference, schDay, dayMax, dayMin, emailDay, emailUser);

		HashMap errors = EventDefinitionValidator.validate(configurationDao, new UserAccountDAO(dataSource),
				currentStudy, true);

		ValidatorUtil.checkForErrors(errors);

		eventDefinitionService.createStudyEventDefinition(studyEventDefinitionBean, owner, currentStudy);

		if (studyEventDefinitionBean.getId() == 0) {
			throw new RestException(messageSource, "rest.createEvent.operationFailed");
		}

		return studyEventDefinitionBean;
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
			@RestParameterPossibleValues(name = "sourcedataverification", values = "1,2,3", valueDescriptions = "rest.sourcedataverification.valueDescription"),
			@RestParameterPossibleValues(name = "dataentryquality", canBeNotSpecified = true, values = "dde,evaluation", valueDescriptions = "rest.dataentryquality.valueDescription"),
			@RestParameterPossibleValues(name = "emailwhen", canBeNotSpecified = true, values = "complete,sign", valueDescriptions = "rest.emailwhen.valueDescription"),
			@RestParameterPossibleValues(name = "tabbing", values = "leftToRight,topToBottom")})
	@RequestMapping(value = "/addCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean addCrf(@RequestParam(value = "eventid") int eventId,
			@RequestParam("crfname") String crfName, @RequestParam("defaultversion") String defaultVersion,
			@RequestParam(value = "required", defaultValue = "true", required = false) boolean required,
			@RequestParam(value = "passwordrequired", defaultValue = "false", required = false) boolean passwordRequired,
			@RequestParam(value = "hide", defaultValue = "false", required = false) boolean hide,
			@RequestParam(value = "sourcedataverification", defaultValue = "3", required = false) int sourceDataVerification,
			@RequestParam(value = "dataentryquality", defaultValue = "", required = false) String dataEntryQuality,
			@RequestParam(value = "emailwhen", defaultValue = "", required = false) String emailWhen,
			@RequestParam(value = "email", defaultValue = "", required = false) String email,
			@RequestParam(value = "tabbing", defaultValue = "leftToRight", required = false) String tabbing,
			@RequestParam(value = "acceptnewcrfversions", defaultValue = "false", required = false) boolean acceptNewCrfVersions)
					throws Exception {
		UserAccountBean owner = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(eventId);

		EventDefinitionCRFBean eventDefinitionCrfBean = prepareNewEventDefinitionCRF(studyEventDefinitionBean, owner,
				currentStudy, crfName, defaultVersion, required, passwordRequired, hide, sourceDataVerification,
				dataEntryQuality, emailWhen, email, tabbing, acceptNewCrfVersions);

		HashMap errors = EventDefinitionValidator.validateNewEDC(messageSource, dataSource, eventId, defaultVersion,
				crfName, sourceDataVerification, emailWhen, email, studyEventDefinitionBean, currentStudy);

		ValidatorUtil.checkForErrors(errors);

		addEventDefinitionCRF(eventDefinitionCrfBean, currentStudy, owner);

		return eventDefinitionCrfBean;
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
	@RestIgnoreDefaultValues
	@RestProvideAtLeastOneNotRequired
	@RestParameterPossibleValuesHolder({
			@RestParameterPossibleValues(name = "sourcedataverification", canBeNotSpecified = true, values = "1,2,3", valueDescriptions = "rest.sourcedataverification.valueDescription"),
			@RestParameterPossibleValues(name = "dataentryquality", canBeNotSpecified = true, values = "dde,evaluation,none", valueDescriptions = "rest.dataentryquality.withNone.valueDescription"),
			@RestParameterPossibleValues(name = "emailwhen", canBeNotSpecified = true, values = "complete,sign,none", valueDescriptions = "rest.emailwhen.withNone.valueDescription"),
			@RestParameterPossibleValues(name = "tabbing", canBeNotSpecified = true, values = "leftToRight,topToBottom"),
			@RestParameterPossibleValues(name = "propagatechange", canBeNotSpecified = true, values = "1,2,3", valueDescriptions = "rest.propagatechange.valueDescription")})
	@RequestMapping(value = "/editStudyCrf", method = RequestMethod.POST)
	public EventDefinitionCRFBean editStudyCrf(@RequestParam(value = "eventid") int eventId,
			@RequestParam("crfname") String crfName,
			@RequestParam(value = "defaultversion", required = false) String defaultVersion,
			@RequestParam(value = "required", required = false) Boolean required,
			@RequestParam(value = "passwordrequired", required = false) Boolean passwordRequired,
			@RequestParam(value = "hide", required = false) Boolean hide,
			@RequestParam(value = "sourcedataverification", required = false) Integer sourceDataVerification,
			@RequestParam(value = "dataentryquality", required = false) String dataEntryQuality,
			@RequestParam(value = "emailwhen", required = false) String emailWhen,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "tabbing", required = false) String tabbing,
			@RequestParam(value = "acceptnewcrfversions", required = false) Boolean acceptNewCrfVersions,
			@RequestParam(value = "propagatechange", required = false) Integer propagateChange) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) new StudyEventDefinitionDAO(
				dataSource).findByPK(eventId);

		EventDefinitionCRFBean eventDefinitionCRFBean = prepareStudyEventDefinitionCRF(eventId, crfName, defaultVersion,
				required, passwordRequired, hide, sourceDataVerification, dataEntryQuality, emailWhen, email, tabbing,
				acceptNewCrfVersions, propagateChange);

		HashMap errors = EventDefinitionValidator.validateStudyEDC(messageSource, dataSource, eventId,
				studyEventDefinitionBean, eventDefinitionCRFBean, currentStudy, edcItemMetadataService);

		ValidatorUtil.checkForErrors(errors);

		updateEventDefinitionCRFForStudy(studyEventDefinitionBean, eventDefinitionCRFBean);

		return eventDefinitionCRFBean;
	}

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
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(id);
		eventDefinitionService.fillEventDefinitionCrfs(currentStudy, studyEventDefinitionBean);
		return studyEventDefinitionBean;
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
	@RestIgnoreDefaultValues
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
			@RequestParam(value = "isreference", required = false) Boolean isReference,
			@RequestParam(value = "schday", required = false) Integer schDay,
			@RequestParam(value = "maxday", required = false) Integer dayMax,
			@RequestParam(value = "minday", required = false) Integer dayMin,
			@RequestParam(value = "emailday", required = false) Integer emailDay,
			@RequestParam(value = "emailuser", required = false) String emailUser) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);

		StudyEventDefinitionBean studyEventDefinitionBean = prepareStudyEventDefinition(currentStudy, id, name, type,
				description, repeating, category, isReference, schDay, dayMax, dayMin, emailDay, emailUser);

		HashMap errors = EventDefinitionValidator.validate(configurationDao, new UserAccountDAO(dataSource),
				currentStudy, true);

		ValidatorUtil.checkForErrors(errors);

		eventDefinitionService.updateOnlyTheStudyEventDefinition(studyEventDefinitionBean, updater);

		return studyEventDefinitionBean;
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
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(id);
		eventDefinitionService.removeStudyEventDefinition(studyEventDefinitionBean, updater);
		return studyEventDefinitionBean;
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
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = getStudyEventDefinition(id);
		eventDefinitionService.restoreStudyEventDefinition(studyEventDefinitionBean, updater);
		return studyEventDefinitionBean;
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
	public EventDefinitionCRFBean removeCrf(@RequestParam("eventid") int eventId,
			@RequestParam("crfname") String crfName) throws Exception {
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = getStudyEventDefinitionCRF(eventId, crfName);
		eventDefinitionCrfService.removeParentEventDefinitionCrf(eventDefinitionCRFBean, updater);
		return eventDefinitionCRFBean;
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
	public EventDefinitionCRFBean restoreCrf(@RequestParam("eventid") int eventId,
			@RequestParam("crfname") String crfName) throws Exception {
		UserAccountBean updater = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = getStudyEventDefinitionCRF(eventId, crfName);
		eventDefinitionCrfService.restoreParentEventDefinitionCrf(eventDefinitionCRFBean, updater);
		return eventDefinitionCRFBean;
	}
}