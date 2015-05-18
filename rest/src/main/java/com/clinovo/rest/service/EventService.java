package com.clinovo.rest.service;

import java.util.HashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.annotation.RestScope;
import com.clinovo.rest.enums.Scope;
import com.clinovo.rest.enums.UserRole;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.RequestUtil;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.EventDefinitionService;
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
}