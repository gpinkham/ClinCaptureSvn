package com.clinovo.rest.service;

import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParametersPossibleValues;
import com.clinovo.rest.annotation.RestScope;
import com.clinovo.rest.enums.Scope;
import com.clinovo.rest.enums.UserRole;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.RequestUtil;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.EventDefinitionService;
import com.clinovo.service.ItemSDVService;
import com.clinovo.util.EmailUtil;
import com.clinovo.validator.EventDefinitionValidator;

/**
 * RestEventService.
 */
@Controller("restEventService")
@RequestMapping("/event")
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventService extends BaseService {

	@Autowired
	private EventDefinitionService eventDefinitionService;

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private ItemSDVService itemSDVService;

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
	@RestAccess(UserRole.ANY_ADMIN)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	@RestScope(Scope.STUDY)
	@RestParametersPossibleValues({@RestParameterPossibleValues(name = "type", values = "scheduled,unscheduled,common,calendared_visit")})
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
		StudyBean studyBean = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		UserAccountBean ownerUser = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);

		HashMap errors = EventDefinitionValidator.validate(RequestUtil.getRequest(), configurationDao,
				new UserAccountDAO(dataSource).findAllByStudyId(studyBean.getId()), true);

		ValidatorUtil.checkForErrors(errors);

		StudyEventDefinitionBean studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName(name);
		studyEventDefinitionBean.setRepeating(!type.equals(EventDefinitionValidator.CALENDARED_VISIT) && repeating);
		studyEventDefinitionBean.setCategory(category);
		studyEventDefinitionBean.setDescription(description);
		studyEventDefinitionBean.setType(type);
		studyEventDefinitionBean.setOwner(ownerUser);
		studyEventDefinitionBean.setStudyId(studyBean.getId());
		if (type.equals(EventDefinitionValidator.CALENDARED_VISIT)) {
			studyEventDefinitionBean.setMaxDay(isReference ? 0 : dayMax);
			studyEventDefinitionBean.setMinDay(isReference ? 0 : dayMin);
			studyEventDefinitionBean.setScheduleDay(isReference ? 0 : schDay);
			studyEventDefinitionBean.setEmailDay(isReference ? 0 : emailDay);
			studyEventDefinitionBean.setReferenceVisit(isReference);
		}

		eventDefinitionService.createStudyEventDefinition(studyBean, isReference ? "" : emailUser,
				studyEventDefinitionBean);

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
	@RestAccess(UserRole.ANY_ADMIN)
	@RequestMapping(value = "/addCrf", method = RequestMethod.POST)
	@ResponseBody
	@RestScope(Scope.STUDY)
	@RestParametersPossibleValues({
			@RestParameterPossibleValues(name = "sourcedataverification", values = "1,2,3", valueDescriptions = "1 -> Entire CRF, 2 -> Specific Items, 3 -> Not Required"),
			@RestParameterPossibleValues(name = "dataentryquality", canBeNotSpecified = true, values = "dde,evaluation", valueDescriptions = "dde -> Double Data Entry, evaluation -> CRF data evaluation"),
			@RestParameterPossibleValues(name = "emailwhen", canBeNotSpecified = true, values = "complete,sign"),
			@RestParameterPossibleValues(name = "tabbing", values = "leftToRight,topToBottom")})
	public EventDefinitionCRFBean addCrf(
			@RequestParam(value = "eventid") int eventId,
			@RequestParam("crfname") String crfName,
			@RequestParam("defaultversion") String defaultVersion,
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
		UserAccountBean currentUser = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);

		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		StudyEventDefinitionDAO studyEventDefinitionDao = new StudyEventDefinitionDAO(dataSource);

		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullName(defaultVersion, crfName);
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDao
				.findByPK(eventId);

		boolean hasSDVRequiredItems = itemSDVService.hasItemsToSDV(crfVersionBean.getCrfId());

		HashMap errors = EventDefinitionValidator.validateCrfAdding(messageSource, dataSource, eventId, defaultVersion,
				crfName, sourceDataVerification, hasSDVRequiredItems, studyEventDefinitionBean, crfVersionBean,
				currentStudy);

		ValidatorUtil.checkForErrors(errors);

		EventDefinitionCRFBean eventDefinitionCrfBean = new EventDefinitionCRFBean();
		eventDefinitionCrfBean.setEventName(studyEventDefinitionBean.getName());
		eventDefinitionCrfBean.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
		eventDefinitionCrfBean.setRequiredCRF(required);
		eventDefinitionCrfBean.setDefaultVersionId(crfVersionBean.getId());
		eventDefinitionCrfBean.setDefaultVersionName(crfVersionBean.getName());
		eventDefinitionCrfBean.setElectronicSignature(passwordRequired);
		eventDefinitionCrfBean.setAcceptNewCrfVersions(acceptNewCrfVersions);
		eventDefinitionCrfBean.setHideCrf(hide);
		eventDefinitionCrfBean.setDoubleEntry(dataEntryQuality.equals("dde"));
		eventDefinitionCrfBean.setEvaluatedCRF(dataEntryQuality.equals("evaluation"));
		eventDefinitionCrfBean.setSourceDataVerification(SourceDataVerification.getByCode(sourceDataVerification));
		eventDefinitionCrfBean.setCrfName(crfName);
		eventDefinitionCrfBean.setCrfId(crfVersionBean.getCrfId());
		eventDefinitionCrfBean.setStudyId(currentStudy.getId());
		if (emailWhen.equals("sign") || emailWhen.equals("complete")) {
			if (!EmailUtil.isValid(email)) {
				throw new RestException(messageSource, "rest.event.emailAddressIsNotValid");
			}
			eventDefinitionCrfBean.setEmailStep(emailWhen);
			eventDefinitionCrfBean.setEmailTo(email);
		}
		eventDefinitionCrfBean.setTabbingMode(tabbing);
		eventDefinitionCrfBean.setOwner(currentUser);

		eventDefinitionService.addEventDefinitionCrf(eventDefinitionCrfBean);

		return eventDefinitionCrfBean;
	}
}