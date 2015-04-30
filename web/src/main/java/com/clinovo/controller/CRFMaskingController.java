package com.clinovo.controller;

import com.clinovo.controller.base.BaseController;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.CRFMask;
import com.clinovo.service.CRFMaskingService;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This controller was created to handle CRFs Masking page functions.
 */
@Controller
@RequestMapping("/CRFsMasking")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CRFMaskingController extends BaseController {

	@Autowired
	private CRFMaskingService maskingService;

	private StudyDAO studyDAO;
	public static final String CRF_PREFIX = "_c";
	public static final String CRF_C_PREFIX = "C";
	public static final String EVENT_PREFIX = "_e";
	public static final String EVENT_C_PREFIX = "_E";
	public static final String STUDY_PREFIX = "_s";
	public static final String SITE_C_PREFIX = "S";
	public static final String ITEM_PREFIX = "crf_mask";

	/**
	 * This method is used to initialize Masking page.
	 *
	 * @param model   Model
	 * @param request HttpServletRequest
	 * @return String name of the page
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String openCRFsMaskingPage(Model model, HttpServletRequest request) {
		String page = "admin/crfs_masking";
		saveAttributesToModel(model, request);

		studyDAO = new StudyDAO(dataSource);
		UserAccountDAO userDAO = new UserAccountDAO(dataSource);
		int userId = Integer.parseInt(request.getParameter("userId"));
		UserAccountBean user = (UserAccountBean) userDAO.findByPK(userId);
		ArrayList<StudyBean> sBeans = (ArrayList<StudyBean>) studyDAO.findAllActiveWhereUserHasActiveRole(user.getName());
		ArrayList<StudyBean> studies = (ArrayList<StudyBean>) studyDAO
				.findAllActiveStudiesWhereUserHasRole(user.getName());

		LinkedHashMap<String, ArrayList<StudyBean>> sitesByStudies = getSitesByStudyMap(sBeans, user);
		HashMap<Integer, ArrayList<StudyEventDefinitionBean>> eventsByStudies = getEventsByStudiesMap(studies);
		HashMap<String, ArrayList<EventDefinitionCRFBean>> crfsByEvents = getCRFsByEventsAndSites(eventsByStudies,
				sitesByStudies);
		HashMap<String, String> maskedCRFs = getMaskedCRFsMap(user);
		ArrayList<StudyUserRoleBean> roles = (ArrayList<StudyUserRoleBean>) userDAO.findAllRolesByUserName(user.getName());

		if (isSiteLevelUser(sBeans)) {
			HashMap<Integer, StudyUserRoleBean> rolesByS = new HashMap<Integer, StudyUserRoleBean>();
			for (StudyUserRoleBean role : roles) {
				rolesByS.put(role.getStudyId(), role);
				model.addAttribute("rolesBySite", rolesByS);
			}
		} else {
			HashMap<String, StudyUserRoleBean> rolesByS = new HashMap<String, StudyUserRoleBean>();
			for (StudyUserRoleBean role : roles) {
				StudyBean study = (StudyBean) studyDAO.findByPK(role.getStudyId());
				rolesByS.put(study.getName(), role);
				model.addAttribute("rolesByStudy", rolesByS);
			}
		}

		model.addAttribute("sitesByStudies", sitesByStudies);
		model.addAttribute("eventsByStudies", eventsByStudies);
		model.addAttribute("crfsByEvents", crfsByEvents);
		model.addAttribute("maskedCRFs", maskedCRFs);
		model.addAttribute("user", user);
		return page;
	}

	/**
	 * This method will restore data that was entered on EditUser page.
	 *
	 * @param model      Model
	 * @param request    HttpServletRequest
	 * @param attributes ListOfAttributes that will be returned
	 * @return String name of the Page
	 */
	@RequestMapping(params = "submit_and_restore")
	public String submitCRFsMaskingPage(Model model, HttpServletRequest request, RedirectAttributes attributes) {
		String page = "redirect:/pages/EditUserAccount";
		saveAttributesToModel(model, request);
		attributes.addAllAttributes(model.asMap());
		studyDAO = new StudyDAO(dataSource);
		UserAccountDAO userDAO = new UserAccountDAO(dataSource);
		int userId = Integer.parseInt(request.getParameter("userId"));
		UserAccountBean user = (UserAccountBean) userDAO.findByPK(userId);
		Enumeration parameters = request.getParameterNames();

		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();

			if (paramName.contains(ITEM_PREFIX) && paramName.contains(CRF_PREFIX) && paramName.contains(EVENT_PREFIX)
					&& paramName
					.contains(STUDY_PREFIX)) {
				String crfIdS = paramName.substring(paramName.lastIndexOf(CRF_PREFIX), paramName.indexOf(EVENT_PREFIX))
						.replace(CRF_PREFIX, "");
				String eventIdS = paramName
						.substring(paramName.lastIndexOf(EVENT_PREFIX), paramName.indexOf(STUDY_PREFIX))
						.replace(EVENT_PREFIX, "");
				String siteIdS = paramName.substring(paramName.indexOf(STUDY_PREFIX)).replace(STUDY_PREFIX, "");
				int crfId = Integer.parseInt(crfIdS);
				int eventId = Integer.parseInt(eventIdS);
				int siteId = Integer.parseInt(siteIdS);
				CRFMask mask = maskingService.findByUserIdSiteIdAndCRFId(userId, siteId, crfId);

				if (request.getParameter(paramName).equals("masked")) {
					if (mask == null) {
						mask = new CRFMask();
					}
					StudyUserRoleBean sur = userDAO.findRoleByUserNameAndStudyId(user.getName(), siteId);
					if (!sur.isActive()) {
						StudyBean site = (StudyBean) studyDAO.findByPK(siteId);
						if (site.getParentStudyId() > 0) {
							sur = userDAO.findRoleByUserNameAndStudyId(user.getName(), site.getParentStudyId());
						}
					}
					if (sur.isActive()) {
						mask.setStudyId(siteId);
						mask.setUserId(user.getId());
						mask.setStudyEventDefinitionId(eventId);
						mask.setEventDefinitionCrfId(crfId);
						mask.setStudyUserRoleId(sur.getPrimaryKey());
						mask.setStatusId(Status.AVAILABLE.getId());
						maskingService.saveCRFMask(mask);
					}
				} else {
					if (mask != null) {
						maskingService.delete(mask);
					}
				}
			}
		}
		Locale locale = LocaleResolver.getLocale(request);
		ArrayList<String> messages = new ArrayList<String>();
		messages.add(messageSource.getMessage("crf_masking_success", null, locale) + " " + user.getName() + ".");
		attributes.addFlashAttribute("pageMessages", messages);

		return page;
	}

	/**
	 * Check if user have only Site level roles.
	 *
	 * @param siteBeans ArrayList of StudyBean
	 * @return boolean
	 */
	private boolean isSiteLevelUser(ArrayList<StudyBean> siteBeans) {
		for (StudyBean sBean : siteBeans) {
			if (!sBean.isSite()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Re-save all attributes to model.
	 *
	 * @param model   Model
	 * @param request HttpServletRequest
	 */
	private void saveAttributesToModel(Model model, HttpServletRequest request) {
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			String paramName = (String) parameters.nextElement();
			if (!paramName.contains(ITEM_PREFIX)) {
				model.addAttribute(paramName, request.getParameter(paramName));
			}
		}
	}

	/**
	 * Get HashMap of sites by study name.
	 *
	 * @param sBeans ArrayList Study
	 * @param user   UserAccountBean
	 * @return HashMap of sites stored by study name
	 */
	private LinkedHashMap<String, ArrayList<StudyBean>> getSitesByStudyMap(ArrayList<StudyBean> sBeans,
																	 UserAccountBean user) {
		LinkedHashMap<String, ArrayList<StudyBean>> sitesByStudies = new LinkedHashMap<String, ArrayList<StudyBean>>();

		// Create list of active sites for Site or Study level user.
		if (isSiteLevelUser(sBeans)) {
			ArrayList<StudyBean> parentStudies = (ArrayList<StudyBean>) studyDAO
					.findAllActiveStudiesWhereUserHasRole(user.getName());
			for (StudyBean study : parentStudies) {
				String studyName = study.getName();
				ArrayList<StudyBean> sitesInStudy = new ArrayList<StudyBean>();

				for (StudyBean site : sBeans) {
					if (site.getParentStudyId() == study.getId()) {
						sitesInStudy.add(site);
					}
				}
				sitesByStudies.put(studyName, sitesInStudy);
			}
		} else {
			for (StudyBean study : sBeans) {
				String studyName = study.getName();
				ArrayList<StudyBean> sitesInStudy = (ArrayList<StudyBean>) studyDAO.findAllByParent(study.getId());
				sitesInStudy.remove(study);
				sitesByStudies.put(studyName, sitesInStudy);
			}
		}
		return sitesByStudies;
	}

	/**
	 * This method collects list of events for each study.
	 *
	 * @param studies ArrayList of StudyBeans
	 * @return HashMap of StudyEventDefinitionBeans mapped by StudyId.
	 */
	private HashMap<Integer, ArrayList<StudyEventDefinitionBean>> getEventsByStudiesMap(
			ArrayList<StudyBean> studies) {

		HashMap<Integer, ArrayList<StudyEventDefinitionBean>> eventsMap = new HashMap<Integer, ArrayList<StudyEventDefinitionBean>>();
		StudyEventDefinitionDAO eventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		for (StudyBean study : studies) {
			int studyId = study.getId();			
			ArrayList<StudyEventDefinitionBean> events = eventDefinitionDAO.findAllActiveByStudy(study);
			eventsMap.put(studyId, events);
		}
		return eventsMap;
	}

	/**
	 * This this method returns lists of EventDefinitionCRFs mapped by "S" + SiteID + "_E" + EventId.
	 *
	 * @param eventsByStudies HashMap of event mapped by StudyId.
	 * @param sitesByStudies  HashMap of sites mapped by StudyName.
	 * @return list of EventDefinitionCRFs by Site and Event.
	 */
	private HashMap<String, ArrayList<EventDefinitionCRFBean>> getCRFsByEventsAndSites(
			HashMap<Integer, ArrayList<StudyEventDefinitionBean>> eventsByStudies,
			HashMap<String, ArrayList<StudyBean>> sitesByStudies) {

		HashMap<String, ArrayList<EventDefinitionCRFBean>> crfsMap = new HashMap<String, ArrayList<EventDefinitionCRFBean>>();
		EventDefinitionCRFDAO definitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		CRFDAO crfDao = new CRFDAO(dataSource);

		for (Map.Entry<String, ArrayList<StudyBean>> studyEntry : sitesByStudies.entrySet()) {
			ArrayList<StudyBean> sites = studyEntry.getValue();
			StudyBean study = (StudyBean) studyDAO.findByName(studyEntry.getKey());
			ArrayList<StudyEventDefinitionBean> events = eventsByStudies.get(study.getId());

			for (StudyBean site : sites) {
				for (StudyEventDefinitionBean event : events) {
					int eventId = event.getId();
					ArrayList<EventDefinitionCRFBean> crfs = (ArrayList<EventDefinitionCRFBean>) definitionCRFDAO
							.findAllByEventDefinitionId(site, eventId);
					for (EventDefinitionCRFBean edCrf : crfs) {
						CRFBean crf = (CRFBean) crfDao.findByPK(edCrf.getCrfId());
						edCrf.setName(crf.getName());
					}
					crfsMap.put(SITE_C_PREFIX + site.getId() + EVENT_C_PREFIX + eventId, crfs);
				}
			}
		}
		return crfsMap;
	}

	/**
	 * This method returns list of Masked CRFs.
	 *
	 * @param user UserAccountBean
	 * @return HashMap
	 */
	private HashMap<String, String> getMaskedCRFsMap(UserAccountBean user) {

		ArrayList<CRFMask> masks = (ArrayList<CRFMask>) maskingService.findByUserId(user.getId());
		HashMap<String, String> maskedMap = new HashMap<String, String>();

		for (CRFMask mask : masks) {
			maskedMap.put(CRF_C_PREFIX + mask.getEventDefinitionCrfId(), "true");
		}
		return maskedMap;
	}
}