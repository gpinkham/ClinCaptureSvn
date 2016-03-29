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

package com.clinovo.crfdata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.rulerunner.ExecutionMode;
import org.akaza.openclinica.logic.rulerunner.ImportDataRuleRunnerContainer;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.util.ImportSummaryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.service.ItemSDVService;
import com.clinovo.service.StudySubjectIdService;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;
import com.clinovo.util.ValidatorHelper;

/**
 * ImportCRFDataService.
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class ImportCRFDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImportCRFDataService.class);

	public static final int INT_52 = 52;

	private Locale locale;

	private final DataSource ds;

	private RuleSetService ruleSetService;

	private ItemSDVService itemSDVService;

	private StudySubjectIdService studySubjectIdService;

	private ResourceBundle respage;

	private ResourceBundle restext;

	private ResourceBundle resformat;

	/**
	 * Constructor.
	 * 
	 * @param ruleSetService
	 *            RuleSetService
	 * @param itemSDVService
	 *            ItemSDVService
	 * @param studySubjectIdService
	 *            StudySubjectIdService
	 * @param ds
	 *            DataSource
	 * @param locale
	 *            Locale
	 */
	public ImportCRFDataService(RuleSetService ruleSetService, ItemSDVService itemSDVService,
			StudySubjectIdService studySubjectIdService, DataSource ds, Locale locale) {
		ResourceBundleProvider.updateLocale(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		resformat = ResourceBundleProvider.getFormatBundle(locale);
		restext = ResourceBundleProvider.getTextsBundle(locale);
		this.ruleSetService = ruleSetService;
		this.itemSDVService = itemSDVService;
		this.studySubjectIdService = studySubjectIdService;
		this.locale = locale;
		this.ds = ds;
	}

	private static class ItemGroupComparator<T extends ImportItemGroupDataBean> implements Comparator<T> {
		public int compare(T g1, T g2) {
			int result = 0;
			if (g1 != null && g2 != null && g1.getItemGroupOID() != null && g2.getItemGroupOID() != null) {
				if (g1.getItemGroupRepeatKey() == null) {
					result = -1;
				} else if (g2.getItemGroupRepeatKey() == null) {
					result = 1;
				} else if (g1.getItemGroupOID().equals(g2.getItemGroupOID())) {
					result = (g1.getItemGroupRepeatKey() + g1.getItemGroupOID())
							.compareTo((g2.getItemGroupRepeatKey() + g2.getItemGroupOID()));
				} else {
					result = g1.getItemGroupOID().compareTo(g2.getItemGroupOID());
				}
			}
			return result;
		}
	}

	private StudySubjectBean createStudySubject(UserAccountBean ub, StudyBean studyBean,
			SubjectDataBean subjectDataBean, SubjectDAO subjectDAO, StudySubjectDAO studySubjectDAO,
			List<String> errors) throws OpenClinicaException {
		String subjectOid = subjectDataBean.getSubjectOID();
		String studySubjectId = subjectDataBean.getStudySubjectId();
		String idSetting = studyBean.getStudyParameterConfig().getSubjectIdGeneration();
		if (subjectOid == null || subjectOid.trim().isEmpty()) {
			if (!idSetting.equals("manual") && studySubjectId != null && !studySubjectId.trim().isEmpty()) {
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("study_subject_id_should_be_empty"));
				Object[] arguments = {studySubjectId};
				errors.add(mf.format(arguments));
				LOGGER.debug("Study subject id should be empty");
				throw new OpenClinicaException("Study subject id should be empty", "");
			}
			studySubjectId = idSetting.equals("manual")
					? subjectDataBean.getStudySubjectId()
					: studySubjectIdService.getNextStudySubjectId(studyBean.getIdentifier());
			subjectOid = "SS_" + studySubjectId.replaceAll(" |-", "").toUpperCase();
			if (studySubjectDAO.findByOid(subjectOid) != null) {
				return createStudySubject(ub, studyBean, subjectDataBean, subjectDAO, studySubjectDAO, errors);
			}
			if (studySubjectDAO.findByLabelAndStudy(studySubjectId, studyBean).getId() > 0) {
				if (idSetting.equals("manual")) {
					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage
							.getString("study_subject_id_is_not_unique_in_the_study_you_may_change_the_study_param"));
					Object[] arguments = {studySubjectId};
					errors.add(mf.format(arguments));
					LOGGER.debug("Study subject id is not unique");
					throw new OpenClinicaException("Study subject id is not unique", "");
				} else {
					return createStudySubject(ub, studyBean, subjectDataBean, subjectDAO, studySubjectDAO, errors);
				}
			}
		}

		if (studySubjectId == null || studySubjectId.trim().isEmpty()) {
			MessageFormat mf = new MessageFormat("");
			mf.applyPattern(respage.getString("study_subject_id_should_be_specifyed"));
			Object[] arguments = {studySubjectId};
			errors.add(mf.format(arguments));
			LOGGER.debug("The Study Subject Id should be specified");
			throw new OpenClinicaException("The Study Subject Id should be specified", "");
		}

		Date currentDate = new Date();

		SubjectBean subjectBean = new SubjectBean();
		subjectBean.setOwner(ub);
		subjectBean.setCreatedDate(currentDate);
		subjectBean.setStatus(Status.AVAILABLE);
		subjectBean.setUniqueIdentifier(studySubjectId);
		subjectBean = subjectDAO.create(subjectBean);

		StudySubjectBean studySubjectBean = new StudySubjectBean();
		studySubjectBean.setOid(subjectOid);
		studySubjectBean.setSubjectId(subjectBean.getId());
		studySubjectBean.setOwner(ub);
		studySubjectBean.setCreatedDate(currentDate);
		studySubjectBean.setStatus(Status.AVAILABLE);
		studySubjectBean.setStudyId(studyBean.getId());
		studySubjectBean.setLabel(studySubjectId);
		studySubjectBean.setSecondaryLabel(studySubjectId);
		studySubjectBean.setEnrollmentDate(currentDate);

		// in a future we should know how to process study subject's groups
		studySubjectBean = studySubjectDAO.create(studySubjectBean, false);
		subjectDataBean.setSubjectOID(studySubjectBean.getOid());
		return studySubjectBean;
	}

	private StudyEventBean scheduleStudyEvent(int sampleOrdinal, UserAccountBean ub, StudySubjectBean studySubjectBean,
			StudyEventDefinitionBean studyEventDefinitionBean, StudyEventDAO studyEventDAO) {
		Date currentDate = new Date();

		StudyEventBean studyEventBean = new StudyEventBean();
		studyEventBean.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
		studyEventBean.setStudySubjectId(studySubjectBean.getId());
		studyEventBean.setOwner(ub);
		studyEventBean.setStatus(Status.AVAILABLE);
		studyEventBean.setLocation("-");
		studyEventBean.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

		studyEventBean.setSampleOrdinal(sampleOrdinal);
		studyEventBean.setCreatedDate(currentDate);
		studyEventBean.setDateStarted(currentDate);
		studyEventBean.setDateEnded(currentDate);

		return (StudyEventBean) studyEventDAO.create(studyEventBean);
	}

	/**
	 * Fetches EventCRFBeans.
	 * 
	 * @param odmContainer
	 *            ODMContainer
	 * @param ub
	 *            UserAccountBean
	 * @return List<EventCRFBean>
	 */
	public List<EventCRFBean> fetchEventCRFBeans(ODMContainer odmContainer, UserAccountBean ub) {
		ArrayList<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
		ArrayList<Integer> eventCRFBeanIds = new ArrayList<Integer>();
		EventCRFDAO eventCrfDAO = new EventCRFDAO(ds);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
		StudyDAO studyDAO = new StudyDAO(ds);
		CRFDAO crfDAO = new CRFDAO(ds);
		StudyEventDAO studyEventDAO = new StudyEventDAO(ds);

		String studyOID = odmContainer.getCrfDataPostImportContainer().getStudyOID();
		StudyBean studyBean = studyDAO.findByOid(studyOID);
		StudyConfigService configService = new StudyConfigService(ds);
		studyBean = configService.setParametersForStudy(studyBean);
		ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
		for (SubjectDataBean subjectDataBean : subjectDataBeans) {
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();

			StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(subjectDataBean.getSubjectOID(),
					studyBean.getId());
			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();

				String sampleOrdinal = studyEventDataBean.getStudyEventRepeatKey() == null
						? "1"
						: studyEventDataBean.getStudyEventRepeatKey();

				StudyEventDefinitionBean studyEventDefinitionBean = studyEventDefinitionDAO.findByOidAndStudy(
						studyEventDataBean.getStudyEventOID(), studyBean.getId(), studyBean.getParentStudyId());
				LOGGER.info("find all by def and subject " + studyEventDefinitionBean.getName() + " study subject "
						+ studySubjectBean.getName());

				StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO
						.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectBean.getId(),
								studyEventDefinitionBean.getId(), Integer.parseInt(sampleOrdinal));

				if (studyEventBean.getId() == 0 && studyBean.getStudyParameterConfig()
						.getAutoScheduleEventDuringImport().equalsIgnoreCase("yes")) {
					studyEventBean = scheduleStudyEvent(Integer.parseInt(sampleOrdinal), ub, studySubjectBean,
							studyEventDefinitionBean, studyEventDAO);
				}

				if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED)
						|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)
						|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.STOPPED)) {
					return null;
				}
				for (FormDataBean formDataBean : formDataBeans) {

					CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);

					ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
					for (CRFVersionBean crfVersionBean : crfVersionBeans) {
						CRFBean crfBean = ((CRFBean) crfDAO.findByPK(crfVersionBean.getCrfId()));
						ArrayList<EventCRFBean> eventCrfBeans = new EventCRFDAO(ds)
								.findAllByStudyEventAndCrfOrCrfVersionOid(studyEventBean, crfBean.getOid());
						// what if we have begun with creating a study
						// event, but haven't entered data yet? this would
						// have us with a study event, but no corresponding
						// event crf, yet.
						if (eventCrfBeans.isEmpty()) {
							LOGGER.debug("   found no event crfs from Study Event id " + studyEventBean.getId()
									+ ", location " + studyEventBean.getLocation());
							// spell out criteria and create a bean if
							// necessary, avoiding false-positives
							if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.NOT_SCHEDULED)
									|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)
									|| studyEventBean.getSubjectEventStatus()
											.equals(SubjectEventStatus.DATA_ENTRY_STARTED)
									|| studyEventBean.getSubjectEventStatus()
											.equals(SubjectEventStatus.SOURCE_DATA_VERIFIED)
									|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
								EventCRFBean newEventCrfBean = new EventCRFBean();
								newEventCrfBean.setStudyEventId(studyEventBean.getId());
								newEventCrfBean.setStudySubjectId(studySubjectBean.getId());
								newEventCrfBean.setCRFVersionId(crfVersionBean.getId());
								newEventCrfBean.setDateInterviewed(new Date());
								newEventCrfBean.setOwner(ub);
								newEventCrfBean.setInterviewerName(ub.getName());
								newEventCrfBean.setCompletionStatusId(1); // place
								// filler
								newEventCrfBean.setStatus(Status.AVAILABLE);
								newEventCrfBean.setStage(DataEntryStage.INITIAL_DATA_ENTRY);
								// these will be updated later in the
								// workflow
								newEventCrfBean = (EventCRFBean) eventCrfDAO.create(newEventCrfBean);
								eventCrfBeans.add(newEventCrfBean);
								LOGGER.debug("   created and added new event crf");
							}
						}

						for (EventCRFBean ecb : eventCrfBeans) {
							Integer ecbId = ecb.getId();

							if (!eventCRFBeanIds.contains(ecbId)) {
								eventCRFBeans.add(ecb);
								eventCRFBeanIds.add(ecbId);
							}
						}
					}
				}
			}
		}
		// if it's null, throw an error, since they should be existing beans for
		// iteration one
		return eventCRFBeans;
	}

	/**
	 * Generates SummaryStatsBean.
	 * 
	 * @param odmContainer
	 *            ODMContainer
	 * @param wrappers
	 *            List<DisplayItemBeanWrapper>
	 * @return SummaryStatsBean
	 */
	public SummaryStatsBean generateSummaryStatsBean(ODMContainer odmContainer, List<DisplayItemBeanWrapper> wrappers) {
		int discNotesGenerated = 0;
		for (DisplayItemBeanWrapper wr : wrappers) {
			HashMap validations = wr.getValidationErrors();
			discNotesGenerated += validations.size();
		}
		Set<String> setOfSubjectOids = new HashSet<String>();
		Set<String> setOfStudyEventOids = new HashSet<String>();
		for (SubjectDataBean subjectDataBean : odmContainer.getCrfDataPostImportContainer().getSubjectData()) {
			setOfSubjectOids.add(subjectDataBean.getSubjectOID());
			for (StudyEventDataBean studyEventDataBean : subjectDataBean.getStudyEventData()) {
				setOfStudyEventOids.add(studyEventDataBean.getStudyEventOID());
			}
		}

		SummaryStatsBean ssBean = new SummaryStatsBean();
		ssBean.setDiscNoteCount(discNotesGenerated);
		ssBean.setEventCrfCount(setOfStudyEventOids.size());
		ssBean.setStudySubjectCount(setOfSubjectOids.size());
		return ssBean;
	}

	private EventCRFBean createEventCRFBean(UserAccountBean ub, int crfVersionId, StudySubjectBean studySubject,
			StudyEventBean studyEvent, EventCRFDAO eventCRFDAO) {

		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setAnnotations("");
		eventCRFBean.setCreatedDate(new Date());
		eventCRFBean.setCRFVersionId(crfVersionId);

		StudyParameterValueBean studyParameter = getStudyParameterValueDAO()
				.findByHandleAndStudy(studySubject.getStudyId(), "interviewerNameDefault");
		if ("blank".equals(studyParameter.getValue())) {
			eventCRFBean.setInterviewerName("");
		} else {
			eventCRFBean.setInterviewerName(studyEvent.getOwner().getName());
		}

		studyParameter = getStudyParameterValueDAO()
				.findByHandleAndStudy(studySubject.getStudyId(), "interviewDateDefault");
		if ("blank".equals(studyParameter.getValue())) {
			eventCRFBean.setDateInterviewed(null);
		} else {
			eventCRFBean.setDateInterviewed(studyEvent.getDateStarted());
		}

		eventCRFBean.setOwner(ub);
		eventCRFBean.setNotStarted(true);
		eventCRFBean.setStatus(Status.AVAILABLE);
		eventCRFBean.setCompletionStatusId(1);
		eventCRFBean.setStudySubjectId(studySubject.getId());
		eventCRFBean.setStudyEventId(studyEvent.getId());
		eventCRFBean.setValidateString("");
		eventCRFBean.setValidatorAnnotations("");
		return (EventCRFBean) eventCRFDAO.create(eventCRFBean);
	}

	/**
	 * Lookups validation errors.
	 * 
	 * @param validatorHelper
	 *            ValidatorHelper
	 * @param odmContainer
	 *            ODMContainer
	 * @param ub
	 *            UserAccountBean
	 * @param totalValidationErrors
	 *            HashMap<String, String>
	 * @param hardValidationErrors
	 *            HashMap<String, String>
	 * @param permittedEventCRFIds
	 *            ArrayList<Integer>
	 * @return List of DisplayItemBeanWrapper
	 * @throws OpenClinicaException
	 *             the OpenClinicaException
	 */
	public List<DisplayItemBeanWrapper> lookupValidationErrors(ValidatorHelper validatorHelper,
			ODMContainer odmContainer, UserAccountBean ub, HashMap<String, String> totalValidationErrors,
			HashMap<String, String> hardValidationErrors, ArrayList<Integer> permittedEventCRFIds)
					throws OpenClinicaException {

		ImportDataRefiner importDataRefiner = new ImportDataRefiner();
		DisplayItemBeanWrapper displayItemBeanWrapper = null;
		HashMap validationErrors;
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		DiscrepancyValidator discValidator = new DiscrepancyValidator(validatorHelper, discNotes);
		// create a second Validator, this one for hard edit checks
		HashMap<String, String> hardValidator = new HashMap<String, String>();
		Map<Integer, Set<Integer>> partialSectionIdMap = new HashMap<Integer, Set<Integer>>();

		StudyEventDAO studyEventDAO = new StudyEventDAO(ds);
		StudyDAO studyDAO = new StudyDAO(ds);
		StudyBean studyBean = studyDAO.findByOid(odmContainer.getCrfDataPostImportContainer().getStudyOID());
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
		StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(ds);
		StudyConfigService scs = new StudyConfigService(ds);
		studyBean = scs.setParametersForStudy(studyBean);
		ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
		int totalEventCRFCount = 0;
		int totalItemDataBeanCount = 0;
		for (SubjectDataBean subjectDataBean : subjectDataBeans) {
			ArrayList<DisplayItemBean> displayItemBeans;
			LOGGER.debug("iterating through subject data beans: found " + subjectDataBean.getSubjectOID());
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
			totalEventCRFCount += studyEventDataBeans.size();

			StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(subjectDataBean.getSubjectOID(),
					studyBean.getId());

			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				// resetting max ordinal here, to try and stop 'multiple item creation'
				int parentStudyId = studyBean.getParentStudyId();
				StudyEventDefinitionBean sedBean = sedDao.findByOidAndStudy(studyEventDataBean.getStudyEventOID(),
						studyBean.getId(), parentStudyId);
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
				LOGGER.debug(
						"iterating through study event data beans: found " + studyEventDataBean.getStudyEventOID());

				int ordinal = 1;
				try {
					ordinal = Integer.parseInt(studyEventDataBean.getStudyEventRepeatKey());
				} catch (Exception e) {
					// trying to catch NPEs, because tags can be without the
					// repeat key
				}
				StudyEventBean studyEvent = (StudyEventBean) studyEventDAO
						.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectBean.getId(), sedBean.getId(),
								ordinal);

				if (studyEvent.getId() == 0) {
					// we do the auto scheduling later - in the fetchEventCRFBeans method

					MessageFormat mf = new MessageFormat("");
					mf.applyPattern(respage.getString("your_study_event_oid_is_not_scheduled"));
					Object[] arguments = {sedBean.getOid()};

					throw new OpenClinicaException(mf.format(arguments), "");

				}

				displayItemBeans = new ArrayList<DisplayItemBean>();
				HashMap prevValidationErrors = new HashMap();
				for (FormDataBean formDataBean : formDataBeans) {
					CRFDAO crfDAO = new CRFDAO(ds);
					CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
					EventCRFDAO eventCRFDAO = new EventCRFDAO(ds);
					ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
					ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
					if ((crfVersionBeans == null) || (crfVersionBeans.size() == 0)) {
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("your_crf_version_oid_did_not_generate"));
						Object[] arguments = {formDataBean.getFormOID()};

						throw new OpenClinicaException(mf.format(arguments), "");
					}
					CRFVersionBean crfVersion = crfVersionBeans.get(0);
					// if you have a mispelled form oid you get an error here
					// need to error out gracefully and post an error
					LOGGER.debug("iterating through form beans: found " + crfVersion.getOid());
					// may be the point where we cut off item groups etc and
					// instead work on sections
					CRFBean crfBean = ((CRFBean) crfDAO.findByPK(crfVersion.getCrfId()));

					EventCRFBean eventCRFBean = null;
					ArrayList<EventCRFBean> eventCrfBeans = eventCRFDAO
							.findAllByStudyEventAndCrfOrCrfVersionOid(studyEvent, crfBean.getOid());
					if (eventCrfBeans.size() > 0) {
						eventCRFBean = eventCrfBeans.get(0);
						if (eventCRFBean.isNotStarted() && eventCRFBean.getCRFVersionId() != crfVersion.getId()) {
							eventCRFBean.setCRFVersionId(crfVersion.getId());
							eventCRFBean = (EventCRFBean) eventCRFDAO.update(eventCRFBean);
						}
					}
					if (eventCRFBean == null) {
						eventCRFBean = createEventCRFBean(ub, crfVersion.getId(), studySubjectBean, studyEvent,
								eventCRFDAO);
						permittedEventCRFIds.add(eventCRFBean.getId());
					}
					ItemDAO itemDAO = new ItemDAO(ds);
					ItemGroupDAO itemGroupDAO = new ItemGroupDAO(ds);
					ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(ds);
					ItemGroupMetadataDAO itemGroupMetadataDAO = new ItemGroupMetadataDAO(ds);
					EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(ds);
					EventDefinitionCRFBean eventDefinitionCRF = eventDefinitionCRFDAO
							.findByStudyEventIdAndCRFVersionId(studyBean, studyEvent.getId(), crfVersion.getId());
					Collections.sort(itemGroupDataBeans, new ItemGroupComparator());
					// Refine Item Group Data
					importDataRefiner.refineImportItemGroupData(itemGroupDataBeans,
							itemDAO.findAllItemsByVersionId(crfVersion.getId()),
							itemGroupDAO.findGroupByCrfVersionId(crfVersion.getId()),
							itemGroupMetadataDAO.findByCrfVersion(crfVersion.getId()));
					if (permittedEventCRFIds.contains(eventCRFBean.getId())) {
						for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {

							ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
							LOGGER.debug("iterating through group beans: " + itemGroupDataBean.getItemGroupOID());
							// put a checker in here
							ItemGroupBean testBean = itemGroupDAO.findByOid(itemGroupDataBean.getItemGroupOID());
							if (testBean == null) {
								MessageFormat mf = new MessageFormat("");
								mf.applyPattern(respage.getString("your_item_group_oid_for_form_oid"));
								Object[] arguments = {itemGroupDataBean.getItemGroupOID(), formDataBean.getFormOID()};

								throw new OpenClinicaException(mf.format(arguments), "");
							}
							totalItemDataBeanCount += itemDataBeans.size();
							HashMap<String, DisplayItemBean> nonDuplicationMap = new HashMap<String, DisplayItemBean>();
							for (ImportItemDataBean importItemDataBean : itemDataBeans) {
								LOGGER.debug(
										"   iterating through item data beans: " + importItemDataBean.getItemOID());
								List<ItemBean> itemBeans = itemDAO.findByOid(importItemDataBean.getItemOID());
								if (!itemBeans.isEmpty()) {
									ItemBean itemBean = itemBeans.get(0);
									itemBean.setImportItemDataBean(importItemDataBean);
									LOGGER.debug("   found " + itemBean.getName());
									DisplayItemBean displayItemBean = new DisplayItemBean();
									displayItemBean.setItem(itemBean);
									displayItemBean.setAutoAdded(importItemDataBean.getAutoAdded());
									String groupOrdinal = itemGroupDataBean.getItemGroupRepeatKey();
									ItemFormMetadataBean metadataBean = itemFormMetadataDAO
											.findAllByCRFVersionIdAndItemId(crfVersion.getId(), itemBean.getId());
									LOGGER.debug("      found metadata item bean: " + metadataBean);
									ItemDataBean itemDataBean = createItemDataBean(itemBean, eventCRFBean,
											importItemDataBean.getValue(), ub,
											groupOrdinal == null ? 1 : Integer.parseInt(groupOrdinal));

									String newKey = crfVersion.getOid() + "_" + groupOrdinal + "_"
											+ itemGroupDataBean.getItemGroupOID() + "_" + itemBean.getOid() + "_"
											+ subjectDataBean.getSubjectOID();
									LOGGER.info("adding " + newKey + " to blank checks");
									if (metadataBean != null) {
										displayItemBean.setData(itemDataBean);
										displayItemBean.setMetadata(metadataBean);
										// set event def crf?
										displayItemBean.setEventDefinitionCRF(eventDefinitionCRF);
										String eventCRFRepeatKey = studyEventDataBean.getStudyEventRepeatKey();
										// if you do indeed leave off this in the XML it will pass but return 'null' tbh
										attachValidator(displayItemBean, hardValidator, validatorHelper,
												eventCRFRepeatKey, studySubjectBean.getOid());
										checkExistingData(validatorHelper, displayItemBean, itemBean, studyBean);
										String key = displayItemBean.getData().getItemId() + "_"
												+ displayItemBean.getData().getEventCRFId() + "_"
												+ displayItemBean.getData().getOrdinal();
										if (!nonDuplicationMap.containsKey(key)) {
											nonDuplicationMap.put(key, displayItemBean);
										} else {
											MessageFormat mf = new MessageFormat("");
											mf.applyPattern(respage.getString("we_have_found_a_diplicate"));
											Object[] arguments = {importItemDataBean.getItemOID()};

											throw new OpenClinicaException(mf.format(arguments), "");
										}
										displayItemBeans.add(displayItemBean);
									} else {
										MessageFormat mf = new MessageFormat("");
										mf.applyPattern(respage.getString("no_metadata_could_be_found"));
										Object[] arguments = {importItemDataBean.getItemOID()};

										throw new OpenClinicaException(mf.format(arguments), "");
									}
								} else {
									// report the error there
									MessageFormat mf = new MessageFormat("");
									mf.applyPattern(respage.getString("no_item_could_be_found"));
									Object[] arguments = {importItemDataBean.getItemOID()};

									throw new OpenClinicaException(mf.format(arguments), "");
								}
							} // end item data beans
						} // end item group data beans
					}

					crfBean = crfDAO.findByVersionId(crfVersion.getCrfId());
					// seems like an extravagance, but is not contained in crf
					// version or event crf bean
					validationErrors = discValidator.validate();

					for (Object errorKey : validationErrors.keySet()) {
						if (!totalValidationErrors.containsKey(errorKey.toString())) {
							totalValidationErrors.put(errorKey.toString(), validationErrors.get(errorKey).toString());
						}
						LOGGER.debug("+++ adding " + errorKey.toString());
					}
					LOGGER.debug("-- hard validation checks: --");
					for (String errorKey : hardValidator.keySet()) {
						LOGGER.debug(errorKey + " -- " + hardValidator.get(errorKey));
						hardValidationErrors.put(errorKey, hardValidator.get(errorKey));
					}

					String studyEventId = studyEvent.getId() + "";
					String crfVersionId = crfVersion.getId() + "";

					LOGGER.debug("creation of wrapper: original count of display item beans " + displayItemBeans.size()
							+ ", count of item data beans " + totalItemDataBeanCount + " count of validation errors "
							+ validationErrors.size() + " count of study subjects " + subjectDataBeans.size()
							+ " count of event crfs " + totalEventCRFCount + " count of hard error checks "
							+ hardValidator.size());
							// possibly create the import summary here

					// check if we need to overwrite
					DataEntryStage dataEntryStage = eventCRFBean.getStage();
					boolean overwrite = false;
					if (dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)
							|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE)
							|| dataEntryStage.equals(DataEntryStage.INITIAL_DATA_ENTRY)
							|| dataEntryStage.equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
						overwrite = true;
					}
					displayItemBeanWrapper = new DisplayItemBeanWrapper(displayItemBeans, true, overwrite,
							validationErrors, studyEventId, crfVersionId, studyEventDataBean.getStudyEventOID(),
							studySubjectBean.getLabel(), eventCRFBean.getCreatedDate(), crfBean.getName(),
							crfVersion.getName(), studySubjectBean.getOid(),
							studyEventDataBean.getStudyEventRepeatKey());
					Set<Integer> sectionIds = partialSectionIdMap.get(eventCRFBean.getId());
					if (sectionIds == null) {
						sectionIds = new HashSet<Integer>();
						partialSectionIdMap.put(eventCRFBean.getId(), sectionIds);
					}
					sectionIds.addAll(formDataBean.getPartialSectionIds());
					displayItemBeanWrapper.setPartialSectionIdMap(partialSectionIdMap);
					displayItemBeanWrapper.getValidationErrors().putAll(prevValidationErrors);
					prevValidationErrors = displayItemBeanWrapper.getValidationErrors();
					// JN: Commenting out the following code, since we shouldn't re-initialize at this point, as
					// validationErrors would get overwritten and the
					// older errors will be overriden. Moving it after the form.
					// Removing the comments for now, since it seems to be creating duplicate Discrepancy Notes.
					discValidator = new DiscrepancyValidator(validatorHelper, discNotes);
					// reset to allow for new errors...
				}
				wrappers.add(displayItemBeanWrapper);
			}
		}

		return wrappers;
	}

	private void checkExistingData(ValidatorHelper validatorHelper, DisplayItemBean displayItemBean, ItemBean itemBean,
			StudyBean currentStudy) {
		if (currentStudy.getStudyParameterConfig().getReplaceExisitingDataDuringImport().equals("no")) {
			ItemDataBean existingItemDataBean = new ItemDataDAO(ds).findByItemIdAndEventCRFIdAndOrdinal(
					displayItemBean.getItem().getId(), displayItemBean.getData().getEventCRFId(),
					displayItemBean.getData().getOrdinal());
			if (existingItemDataBean != null && existingItemDataBean.getId() > 0) {
				displayItemBean.setSkip(true);
				validatorHelper.setAttribute("hasSkippedItems", true);
				itemBean.getImportItemDataBean().setSkip(true);
			}
		}
	}

	private ItemDataBean createItemDataBean(ItemBean itemBean, EventCRFBean eventCrfBean, String value,
			UserAccountBean ub, int ordinal) {

		ItemDataBean itemDataBean = new ItemDataBean();
		itemDataBean.setItemId(itemBean.getId());
		itemDataBean.setEventCRFId(eventCrfBean.getId());
		itemDataBean.setCreatedDate(new Date());
		itemDataBean.setOrdinal(ordinal);
		itemDataBean.setOwner(ub);
		itemDataBean.setStatus(Status.UNAVAILABLE);
		itemDataBean.setValue(value);

		return itemDataBean;
	}

	private void attachValidator(DisplayItemBean displayItemBean, HashMap<String, String> hardv,
			ValidatorHelper validatorHelper, String eventCRFRepeatKey, String studySubjectOID)
					throws OpenClinicaException {
		org.akaza.openclinica.bean.core.ResponseType rt = displayItemBean.getMetadata().getResponseSet()
				.getResponseType();
		String itemOid = displayItemBean.getItem().getOid() + "_" + eventCRFRepeatKey + "_"
				+ displayItemBean.getData().getOrdinal() + "_" + studySubjectOID;
		// note the above, generating an ordinal on top of the OID to view
		// errors. adding event crf repeat key here, tbh
		// need to add a per-subject differentator, what if its a diff subject
		// with the same item, repeat key and ordinal???

		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			ItemFormMetadataBean ifm = displayItemBean.getMetadata();
			String widthDecimal = ifm.getWidthDecimal();
			if (displayItemBean.getItem().getDataType().equals(ItemDataType.PDATE)) {
				if (!"".equals(displayItemBean.getData().getValue())) {
					try {
						SimpleDateFormat sdfSqlDate;
						if (StringUtil.isFormatDate(displayItemBean.getData().getValue(), "yyyy-MM-dd")) {
							sdfSqlDate = new SimpleDateFormat("yyyy-MM-dd");
							sdfSqlDate.parse(displayItemBean.getData().getValue());
						} else if (StringUtil.isPartialYear(displayItemBean.getData().getValue(), "yyyy")) {
							sdfSqlDate = new SimpleDateFormat("yyyy");
							sdfSqlDate.parse(displayItemBean.getData().getValue());
						} else if (StringUtil.isPartialYearMonth(displayItemBean.getData().getValue(), "yyyy-MM")) {
							sdfSqlDate = new SimpleDateFormat("yyyy-MM");
							sdfSqlDate.parse(displayItemBean.getData().getValue());
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("you_have_a_pdate_value_which_is_not"));
						Object[] arguments = {displayItemBean.getItem().getOid()};
						hardv.put(itemOid, mf.format(arguments));
					}
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.DATE)) {
				// what if it's a date? parse if out so that we go from iso 8601 to
				// mm/dd/yyyy
				if (!"".equals(displayItemBean.getData().getValue())) {
					String dateValue = displayItemBean.getData().getValue();
					SimpleDateFormat sdfSqlDate = new SimpleDateFormat("yyyy-MM-dd");
					try {
						sdfSqlDate.parse(dateValue);
						displayItemBean.getData().setValue(dateValue);
					} catch (ParseException pe) {
						try {
							/*
							 * here we are trying to parse the dates from the old XML files that were generated before
							 * the fix for the #414 we can remove it in a future
							 */
							sdfSqlDate = new SimpleDateFormat(resformat.getString("date_format_string"), locale);
							sdfSqlDate.parse(dateValue);
							displayItemBean.getData().setValue(dateValue);
						} catch (ParseException pe1) {
							// next version; fail if it does not pass iso 8601
							MessageFormat mf = new MessageFormat("");
							mf.applyPattern(respage.getString("you_have_a_date_value_which_is_not"));
							Object[] arguments = {displayItemBean.getItem().getOid()};
							hardv.put(itemOid, mf.format(arguments));
						}
					}
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.ST)) {
				int width = Validator.parseWidth(widthDecimal);
				if (width > 0 && displayItemBean.getData().getValue().length() > width) {
					hardv.put(itemOid, "This value exceeds required width=" + width);
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.INTEGER)) {
				// what if it's a number? should be only numbers
				try {
					int width = Validator.parseWidth(widthDecimal);
					if (width > 0 && displayItemBean.getData().getValue().length() > width) {
						hardv.put(itemOid, "This value exceeds required width=" + width);
					}
					// now, didn't check decimal for testInt.
				} catch (Exception e) {
					// should be a sub class
					if (!"".equals(displayItemBean.getData().getValue())) {
						hardv.put(itemOid, "This value is not an integer.");
					}
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.REAL)) {
				// what if it's a float? should be only numbers
				try {
					int width = Validator.parseWidth(widthDecimal);
					if (width > 0 && displayItemBean.getData().getValue().length() > width) {
						hardv.put(itemOid, "This value exceeds required width=" + width);
					}
					int decimal = Validator.parseDecimal(widthDecimal);
					if (decimal > 0 && BigDecimal.valueOf(Double.parseDouble(displayItemBean.getData().getValue()))
							.scale() > decimal) {
						hardv.put(itemOid, "This value exceeds required decimal=" + decimal);
					}
				} catch (Exception ee) {
					if (!"".equals(displayItemBean.getData().getValue())) {
						hardv.put(itemOid, "This value is not a real number.");
					}
				}
			}

			validatorHelper.setAttribute(itemOid, displayItemBean.getData().getValue());

		} else if (rt.equals(ResponseType.CALCULATION) || rt.equals(ResponseType.GROUP_CALCULATION)) {
			if (displayItemBean.getItem().getDataType().equals(ItemDataType.REAL)) {
				ItemFormMetadataBean ifm = displayItemBean.getMetadata();
				String widthDecimal = ifm.getWidthDecimal();
				int decimal = Validator.parseDecimal(widthDecimal);
				if (decimal > 0) {
					try {
						Double d = new Double(displayItemBean.getData().getValue());
						if (BigDecimal.valueOf(d).scale() > decimal) {
							hardv.put(itemOid, "This value exceeds required decimal=" + decimal);
						}
					} catch (Exception e) {
						if (!"".equals(displayItemBean.getData().getValue())) {
							hardv.put(itemOid, "This value is not a real number.");
						}
					}
				}
			}
		} else
			if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			String theValue = matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(),
					displayItemBean.getMetadata().getResponseSet().getOptions());
			validatorHelper.setAttribute(itemOid, theValue);
			LOGGER.debug("        found the value for radio/single: " + theValue);
			if (theValue == null && displayItemBean.getData().getValue() != null
					&& !displayItemBean.getData().getValue().isEmpty()) {
				LOGGER.debug("-- theValue was NULL, the real value was " + displayItemBean.getData().getValue());
				hardv.put(itemOid, "This is not in the correct response set.");
			}
		} else
				if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
						|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			String theValue = matchValueWithManyOptions(displayItemBean, displayItemBean.getData().getValue(),
					displayItemBean.getMetadata().getResponseSet().getOptions());
			validatorHelper.setAttribute(itemOid, theValue);
			if (theValue == null && displayItemBean.getData().getValue() != null
					&& !displayItemBean.getData().getValue().isEmpty()) {
				hardv.put(itemOid, "This is not in the correct response set.");
			}
		}
	}

	private String matchValueWithOptions(DisplayItemBean displayItemBean, String value, List options) {
		if (!options.isEmpty()) {
			for (Object responseOption : options) {
				ResponseOptionBean responseOptionBean = (ResponseOptionBean) responseOption;
				if (responseOptionBean.getValue().equals(value)) {
					displayItemBean.getData().setValue(((ResponseOptionBean) responseOption).getValue());
					return ((ResponseOptionBean) responseOption).getValue();

				}
			}
		}
		return null;
	}

	/*
	 * difference from the above is only a 'contains' in the place of an 'equals'. and a few other switches...also need
	 * to keep in mind that there are non-null values that need to be taken into account
	 */
	private String matchValueWithManyOptions(DisplayItemBean displayItemBean, String value, List options) {
		String entireOptions = "";

		String[] simValues = value.split(",");

		// also remove all spaces, so they will fit up with the entire set
		// of options
		boolean checkComplete;
		if (!options.isEmpty()) {
			for (Object responseOption : options) {
				ResponseOptionBean responseOptionBean = (ResponseOptionBean) responseOption;
				entireOptions += responseOptionBean.getValue();

			}
			// remove spaces, since they are causing problems:
			entireOptions = entireOptions.replace(" ", "");
			// following may be superfluous, tbh

			ArrayList nullValues = displayItemBean.getEventDefinitionCRF().getNullValuesList();

			for (Object nullValue : nullValues) {
				NullValue nullValueTerm = (NullValue) nullValue;
				entireOptions += nullValueTerm.getName();
			}

			for (String sim : simValues) {
				sim = sim.replace(" ", "");
				checkComplete = entireOptions.contains(sim);
				if (!checkComplete) {
					return null;
				}
			}
		}
		return value;
	}

	/**
	 * Validates study metadata.
	 * 
	 * @param odmContainer
	 *            ODMContainer
	 * @param currentStudyId
	 *            int
	 * @param ub
	 *            UserAccountBean
	 * @return List of String
	 */
	public List<String> validateStudyMetadata(ODMContainer odmContainer, int currentStudyId, UserAccountBean ub) {
		List<String> errors = new ArrayList<String>();
		MessageFormat mf = new MessageFormat("");
		ArrayList<SubjectInfoHolder> subjectInfoHolders = new ArrayList<SubjectInfoHolder>();

		try {
			StudyDAO studyDAO = new StudyDAO(ds);
			String studyOid = odmContainer.getCrfDataPostImportContainer().getStudyOID();
			StudyBean studyBean = studyDAO.findByOid(studyOid);
			if (studyBean == null) {
				mf.applyPattern(respage.getString("your_study_oid_does_not_reference_an_existing"));
				Object[] arguments = {studyOid};

				errors.add(mf.format(arguments));
				LOGGER.debug("unknown study OID");
				throw new OpenClinicaException("Unknown Study OID", "");

			} else if (studyBean.getId() != currentStudyId) {
				mf.applyPattern(respage.getString("your_current_study_is_not_the_same_as"));
				Object[] arguments = {studyBean.getName()};
				errors.add(mf.format(arguments));
			}
			StudyConfigService configService = new StudyConfigService(ds);
			studyBean = configService.setParametersForStudy(studyBean);
			ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();

			StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
			StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
			StudyEventDAO studyEventDAO = new StudyEventDAO(ds);
			CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
			ItemGroupDAO itemGroupDAO = new ItemGroupDAO(ds);
			EventCRFDAO eventCRFDAO = new EventCRFDAO(ds);
			SubjectDAO subjectDAO = new SubjectDAO(ds);
			ItemDAO itemDAO = new ItemDAO(ds);
			CRFDAO crfDAO = new CRFDAO(ds);

			if (subjectDataBeans != null) { // need to do this so as not to
				// throw the exception below and
				// report all available errors, tbh
				Map<String, String> createdOidsMap = new HashMap<String, String>();
				for (SubjectDataBean subjectDataBean : subjectDataBeans) {
					String oid = subjectDataBean.getSubjectOID();
					if ((oid == null || oid.trim().isEmpty())
							&& createdOidsMap.containsKey(subjectDataBean.getStudySubjectId())) {
						oid = createdOidsMap.get(subjectDataBean.getStudySubjectId());
						subjectDataBean.setSubjectOID(oid);
					}
					StudySubjectBean studySubjectBean = oid != null && !oid.trim().isEmpty()
							? studySubjectDAO.findByOid(oid)
							: null;
					SubjectInfoHolder subjectInfoHolder = new SubjectInfoHolder();
					if (studySubjectBean == null && studyBean.getStudyParameterConfig()
							.getAutoCreateSubjectDuringImport().equalsIgnoreCase("yes")) {
						studySubjectBean = createStudySubject(ub, studyBean, subjectDataBean, subjectDAO,
								studySubjectDAO, errors);
						createdOidsMap.put(studySubjectBean.getLabel(), studySubjectBean.getOid());
					}
					if (studySubjectBean == null) {
						mf.applyPattern(respage.getString("your_subject_oid_does_not_reference"));
						Object[] arguments = {oid};
						errors.add(mf.format(arguments));

						LOGGER.debug("logged an error with subject oid " + oid);
					} else if (studySubjectBean.getStudyId() != studyBean.getId()) {
						mf.applyPattern(respage.getString("your_subject_oid_is_linked_with_another_study"));
						Object[] arguments = {oid};
						errors.add(mf.format(arguments));

						LOGGER.debug("logged an error with subject oid " + oid);
					}

					if (studySubjectBean != null) {
						subjectInfoHolder.setOid(studySubjectBean.getOid());
						if (subjectInfoHolders.contains(subjectInfoHolder)) {
							mf.applyPattern(respage.getString("xml_multiple_tags_for_same_subject"));
							Object[] arguments = {oid};
							errors.add(mf.format(arguments));
						}
					}

					ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
					ArrayList<Integer> listOfCrfIds = new ArrayList<Integer>();
					if (studyEventDataBeans != null) {
						for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
							String sedOid = studyEventDataBean.getStudyEventOID();
							StudyEventDefinitionBean studyEventDefintionBean = studyEventDefinitionDAO
									.findByOidAndStudy(sedOid, studyBean.getId(), studyBean.getParentStudyId());
							if (studyEventDefintionBean == null) {
								mf.applyPattern(respage.getString("your_study_event_oid_for_subject_oid"));
								Object[] arguments = {sedOid, oid};
								errors.add(mf.format(arguments));
								LOGGER.debug("logged an error with se oid " + sedOid + " and subject oid " + oid);
							} else if (subjectInfoHolder.eventAlreadyPresent(sedOid)) {
								mf.applyPattern(respage.getString("xml_multiple_tags_for_same_event"));
								Object[] arguments = {sedOid, oid};
								errors.add(mf.format(arguments));
							} else {
								subjectInfoHolder.addEvent(sedOid);
								EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(ds);
								ArrayList<EventDefinitionCRFBean> requiredCrfs = eventDefinitionCrfDao
										.findAllByEventDefinitionId(studyEventDefintionBean.getId());

								for (EventDefinitionCRFBean def : requiredCrfs) {
									int crfId = def.getCrfId();
									listOfCrfIds.add(crfId);
								}
							}

							ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
							if (formDataBeans != null) {
								for (FormDataBean formDataBean : formDataBeans) {
									String formOid = formDataBean.getFormOID();
									if (subjectInfoHolder.eventCRFAlreadyPresent(sedOid, formOid)) {
										mf.applyPattern(respage.getString("xml_multiple_tags_for_same_crf"));
										Object[] arguments = {formOid, sedOid, oid};
										errors.add(mf.format(arguments));
									} else {
										subjectInfoHolder.addCRFToEvent(sedOid, formOid);
									}
									ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formOid);
									// ideally we should look to compare
									// versions within
									// seds;
									// right now just check nulls
									if (crfVersionBeans != null) {
										for (CRFVersionBean crfVersionBean : crfVersionBeans) {
											if (crfVersionBean == null) {
												mf.applyPattern(
														respage.getString("your_crf_version_oid_for_study_event_oid"));
												Object[] arguments = {formOid, sedOid};
												errors.add(mf.format(arguments));

												LOGGER.debug("logged an error with form " + formOid + " and se oid "
														+ sedOid);
											} else {
												CRFBean crfBean = ((CRFBean) crfDAO
														.findByPK(crfVersionBean.getCrfId()));
												if (!listOfCrfIds.contains(Integer.valueOf(crfBean.getId()))) {
													mf.applyPattern(respage.getString("crf_does_not_belong_to_event"));
													Object[] arguments = {formOid};
													errors.add(mf.format(arguments));
												}
												List<StudyEventBean> studyEventBeanList = (List<StudyEventBean>) studyEventDAO
														.findAllByDefinitionAndSubject(studyEventDefintionBean,
																studySubjectBean);
												for (StudyEventBean studyEventBean : studyEventBeanList) {
													List<EventCRFBean> eventCRFBeanList = (List<EventCRFBean>) eventCRFDAO
															.findAllByStudyEventAndCrfOrCrfVersionOid(studyEventBean,
																	crfBean.getOid());
													for (EventCRFBean eventCRFBean : eventCRFBeanList) {
														if (!eventCRFBean.isNotStarted()
																&& eventCRFBean.getCRFVersionId() != crfVersionBean
																		.getId()
																&& studyBean.getStudyParameterConfig()
																		.getReplaceExisitingDataDuringImport()
																		.equals("no")) {
															mf.applyPattern(respage.getString(
																	"you_already_have_started_other_crf_version_for_study_event_and_subject"));
															Object[] arguments = studyEventDefintionBean != null
																	&& studySubjectBean != null
																			? new Object[]{
																					studyEventDefintionBean.getName(),
																					studySubjectBean.getName()}
																			: null;
															errors.add(mf.format(arguments));
														}
													}
												}
											}
										}
									} else {
										mf.applyPattern(respage.getString("your_crf_version_oid_did_not_generate"));
										Object[] arguments = {formOid};
										errors.add(mf.format(arguments));
									}

									ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean
											.getItemGroupData();
									if (itemGroupDataBeans != null) {
										for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
											String itemGroupOID = itemGroupDataBean.getItemGroupOID();
											List<ItemGroupBean> itemGroupBeans = itemGroupDAO
													.findAllByOid(itemGroupOID);
											if (itemGroupBeans != null) {
												LOGGER.debug("number of item group beans: " + itemGroupBeans.size());
												LOGGER.debug("item group oid: " + itemGroupOID);
												for (ItemGroupBean itemGroupBean : itemGroupBeans) {
													if (itemGroupBean == null) {
														mf.applyPattern(
																respage.getString("your_item_group_oid_for_form_oid"));
														Object[] arguments = {itemGroupOID, formOid};
														errors.add(mf.format(arguments));
													}
												}
											} else {
												mf.applyPattern(respage.getString("the_item_group_oid_did_not"));
												Object[] arguments = {itemGroupOID};
												errors.add(mf.format(arguments));
											}

											ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean
													.getItemData();
											if (itemDataBeans != null) {
												for (ImportItemDataBean itemDataBean : itemDataBeans) {
													String itemOID = itemDataBean.getItemOID();
													List<ItemBean> itemBeans = itemDAO.findByOid(itemOID);
													if (itemBeans != null) {

														LOGGER.debug("found itembeans: ");

														for (ItemBean itemBean : itemBeans) {

															if (itemBean == null) {
																mf.applyPattern(respage
																		.getString("your_item_oid_for_item_group_oid"));
																Object[] arguments = {itemOID, itemGroupOID};
																errors.add(mf.format(arguments));

															} else {
																LOGGER.debug(
																		"found " + itemBean.getOid() + ", passing");

															}
														}
													}
												}
											} else {
												mf.applyPattern(respage
														.getString("the_item_group_oid_did_not_contain_item_data"));
												Object[] arguments = {itemGroupOID};
												errors.add(mf.format(arguments));
											}
										}
									} else {
										mf.applyPattern(respage.getString("your_study_event_contains_no_form_data"));
										Object[] arguments = {sedOid};
										errors.add(mf.format(arguments));
									}
								}

							}
						}
					}
					subjectInfoHolders.add(subjectInfoHolder);
				}
			}
		} catch (OpenClinicaException oce) {
			//
		} catch (NullPointerException npe) {
			LOGGER.debug("found a nullpointer here");
		}
		// if errors == null you pass, if not you fail
		return errors;
	}

	private void deleteEventCRF(UserAccountBean ub, int eventCrfId, ItemDataDAO itemDataDao, EventCRFDAO eventCrfDao,
			DiscrepancyNoteDAO discrepancyNoteDao) {
		ArrayList itemData = itemDataDao.findAllByEventCRFId(eventCrfId);
		for (Object anItemData : itemData) {
			ItemDataBean item = (ItemDataBean) anItemData;
			ArrayList discrepancyList = discrepancyNoteDao.findExistingNotesForItemData(item.getId());
			itemDataDao.deleteDnMap(item.getId());
			for (Object aDiscrepancyList : discrepancyList) {
				DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) aDiscrepancyList;
				discrepancyNoteDao.deleteNotes(noteBean.getId());
			}
			item.setUpdater(ub);
			itemDataDao.updateUser(item);
			itemDataDao.delete(item.getId());
		}
		// delete event crf
		eventCrfDao.deleteEventCRFDNMap(eventCrfId);
		eventCrfDao.delete(eventCrfId);
	}

	private DiscrepancyNoteBean createDiscrepancyNote(DiscrepancyNoteBean note, EventCRFBean eventCrfBean,
			DisplayItemBean displayItemBean, Integer parentId, DataSource ds, Connection con) {
		StudySubjectDAO ssdao = new StudySubjectDAO(ds, con);
		note.setDetailedNotes(restext.getString("failed_validation_check"));
		note.setCreatedDate(new Date());
		note.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());
		if (parentId != null) {
			note.setParentDnId(parentId);
		}
		note.setEntityType("ItemData");
		note.setEntityValue(displayItemBean.getData().getValue());

		note.setEventName(eventCrfBean.getName());
		note.setEventStart(eventCrfBean.getCreatedDate());
		note.setCrfName(displayItemBean.getEventDefinitionCRF().getCrfName());

		StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(eventCrfBean.getStudySubjectId());
		note.setSubjectName(ss.getName());

		note.setEntityId(displayItemBean.getData().getId());
		note.setColumn("value");

		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(ds, con);
		note = (DiscrepancyNoteBean) dndao.create(note, con);
		dndao.createMapping(note, con);

		return note;
	}

	/**
	 * Imports crf data.
	 * 
	 * @param studyBean
	 *            StudyBean
	 * @param ub
	 *            UserAccountBean
	 * @param skippedItemIds
	 *            Set<Integer>
	 * @param auditItemList
	 *            List<Map<String, Object>>
	 * @param displayItemBeanWrappers
	 *            List<DisplayItemBeanWrapper>
	 * @param summary
	 *            ImportSummaryInfo
	 * @param odmContainer
	 *            ODMContainer
	 * @return List of ImportDataRuleRunnerContainer
	 * @throws Exception
	 *             an Exception
	 */
	public List<ImportDataRuleRunnerContainer> importCrfData(StudyBean studyBean, UserAccountBean ub,
			Set<Integer> skippedItemIds, List<Map<String, Object>> auditItemList,
			List<DisplayItemBeanWrapper> displayItemBeanWrappers, ImportSummaryInfo summary, ODMContainer odmContainer)
					throws Exception {
		Connection con = null;
		List<ImportDataRuleRunnerContainer> containers;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			System.out.println("JDBC open connection for transaction");

			ItemDAO itemDao = new ItemDAO(ds, con);
			EventCRFDAO eventCrfDao = new EventCRFDAO(ds, con);
			ItemDataDAO itemDataDao = new ItemDataDAO(ds, con);
			StudyEventDAO studyEventDao = new StudyEventDAO(ds, con);
			DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(ds, con);
			EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(ds, con);
			StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds, con);

			// setup ruleSets to run if applicable
			LOGGER.debug("=== about to generate rule containers ===");
			containers = ruleRunSetup(odmContainer, true, con, ds, studyBean, ub);

			Set<Integer> studyEventIds = new HashSet<Integer>();
			for (DisplayItemBeanWrapper wrapper : displayItemBeanWrappers) {
				HashMap<Integer, EventCRFBean> idToEventCrfBeans = new HashMap<Integer, EventCRFBean>();
				LOGGER.debug("=== right before we check to make sure it is savable: " + wrapper.isSavable());
				if (wrapper.isSavable()) {
					LOGGER.debug("wrapper problems found : " + wrapper.getValidationErrors().toString());
					for (DisplayItemBean displayItemBean : wrapper.getDisplayItemBeans()) {
						EventCRFBean eventCrfBean;
						ItemDataBean itemDataBean;
						int eventCrfBeanId = displayItemBean.getData().getEventCRFId();
						if (idToEventCrfBeans.containsKey(eventCrfBeanId)) {
							eventCrfBean = idToEventCrfBeans.get(eventCrfBeanId);
						} else {
							eventCrfBean = (EventCRFBean) eventCrfDao.findByPK(eventCrfBeanId);
							if (!displayItemBean.isSkip()) {
								idToEventCrfBeans.put(eventCrfBeanId, eventCrfBean);
							}
						}
						if (!displayItemBean.isSkip()
								&& displayItemBean.getMetadata().getCrfVersionId() != eventCrfBean.getCRFVersionId()) {
							deleteEventCRF(ub, eventCrfBean.getId(), itemDataDao, eventCrfDao, discrepancyNoteDao);
							StudyEventBean studyEvent = (StudyEventBean) studyEventDao.findByPK(eventCrfBean.getStudyEventId());
							StudySubjectBean studySubject
									= (StudySubjectBean) studySubjectDAO.findByPK(eventCrfBean.getStudySubjectId());
							eventCrfBean = createEventCRFBean(ub, displayItemBean.getMetadata().getCrfVersionId(),
									studySubject, studyEvent, eventCrfDao);
							idToEventCrfBeans.put(eventCrfBean.getId(), eventCrfBean);
							for (DisplayItemBean dib : wrapper.getDisplayItemBeans()) {
								dib.getData().setEventCRFId(eventCrfBean.getId());
							}
						}
						LOGGER.debug("found value here: " + displayItemBean.getData().getValue());
						LOGGER.debug("found status here: " + eventCrfBean.getStatus().getName());
						StudyEventBean studyEventBean = (StudyEventBean) studyEventDao
								.findByPK(eventCrfBean.getStudyEventId());
						itemDataBean = itemDataDao.findByItemIdAndEventCRFIdAndOrdinal(
								displayItemBean.getItem().getId(), eventCrfBean.getId(),
								displayItemBean.getData().getOrdinal());
						summary.processStudySubject(eventCrfBean.getStudySubjectId(), displayItemBean.isSkip());
						summary.processStudyEvent(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum(),
								displayItemBean.isSkip());
						summary.processItem(studyEventBean.getId() + "_" + studyEventBean.getRepeatingNum() + "_"
								+ displayItemBean.getItem().getId() + "_" + displayItemBean.getData().getOrdinal(),
								displayItemBean.isSkip());
						if (!displayItemBean.isSkip()) {
							if (wrapper.isOverwrite() && itemDataBean.getStatus() != null) {
								LOGGER.debug("just tried to find item data bean on item name "
										+ displayItemBean.getItem().getName());
								itemDataBean.setUpdatedDate(new Date());
								itemDataBean.setUpdater(ub);
								itemDataBean.setValue(displayItemBean.getData().getValue());
								// set status?
								itemDataDao.update(itemDataBean, con);
								LOGGER.debug("updated: " + itemDataBean.getItemId());
								// need to set pk here in order to create dn
								displayItemBean.getData().setId(itemDataBean.getId());
							} else {
								itemDataBean = (ItemDataBean) itemDataDao.create(displayItemBean.getData(), con);
								LOGGER.debug("created: " + displayItemBean.getData().getItemId() + "event CRF ID = "
										+ eventCrfBean.getId() + "CRF VERSION ID =" + eventCrfBean.getCRFVersionId());
								displayItemBean.getData().setId(itemDataBean.getId());
							}
							ItemBean ibean = (ItemBean) itemDao.findByPK(displayItemBean.getData().getItemId());
							String itemOid = displayItemBean.getItem().getOid() + "_" + wrapper.getStudyEventRepeatKey()
									+ "_" + displayItemBean.getData().getOrdinal() + "_" + wrapper.getStudySubjectOid();
							if (wrapper.getValidationErrors().containsKey(itemOid)) {
								ArrayList messageList = (ArrayList) wrapper.getValidationErrors().get(itemOid);
								// could be more then one will have to iterate
								for (Object aMessageList : messageList) {
									DiscrepancyNoteBean note = new DiscrepancyNoteBean();
									note.setOwner(ub);
									note.setField(ibean.getName());
									note.setStudyId(studyBean.getId());
									note.setEntityName(ibean.getName());
									note.setDescription((String) aMessageList);
									DiscrepancyNoteBean parentDn = createDiscrepancyNote(note, eventCrfBean,
											displayItemBean, null, ds, con);
									createDiscrepancyNote(note, eventCrfBean, displayItemBean, parentDn.getId(), ds,
											con);
								}
							}
						} else {
							skippedItemIds.add(displayItemBean.getItem().getId());
							Map<String, Object> auditItemMap = new HashMap<String, Object>();
							auditItemMap.put("audit_log_event_type_id", INT_52);
							auditItemMap.put("user_id", ub.getId());
							auditItemMap.put("audit_table", "item_data");
							auditItemMap.put("entity_id", itemDataBean.getId());
							auditItemMap.put("entity_name", displayItemBean.getItem().getName());
							auditItemMap.put("old_value", itemDataBean.getValue());
							auditItemMap.put("new_value", displayItemBean.getData().getValue());
							auditItemMap.put("event_crf_id", displayItemBean.getData().getEventCRFId());
							auditItemList.add(auditItemMap);
						}
					}

					for (EventCRFBean eventCrfBean : idToEventCrfBeans.values()) {
						studyEventIds.add(eventCrfBean.getStudyEventId());

						Set<Integer> sectionIds = wrapper.getPartialSectionIdMap().remove(eventCrfBean.getId());

						eventCrfBean.setSdvStatus(false);
						eventCrfBean.setNotStarted(false);
						eventCrfBean.setStatus((sectionIds != null && sectionIds.size() > 0)
								|| eventCrfBean.getStatus().equals(Status.PARTIAL_DATA_ENTRY)
										? Status.PARTIAL_DATA_ENTRY
										: Status.AVAILABLE);
						StudySubjectBean studySubject =
								(StudySubjectBean) studySubjectDAO.findByPK(eventCrfBean.getStudySubjectId());
						StudyParameterValueBean studyParameter = getStudyParameterValueDAO()
								.findByHandleAndStudy(studySubject.getStudyId(), "markImportedCRFAsCompleted");
						if ("yes".equalsIgnoreCase(studyParameter.getValue())) {
							EventDefinitionCRFBean edcb = eventDefinitionCrfDao.findByStudyEventIdAndCRFVersionId(
									studyBean, eventCrfBean.getStudyEventId(), eventCrfBean.getCRFVersionId());

							eventCrfBean.setUpdater(ub);
							eventCrfBean.setUpdatedDate(new Date());
							eventCrfBean.setDateCompleted(new Date());
							eventCrfBean.setDateValidateCompleted(new Date());
							eventCrfBean.setStatus(Status.UNAVAILABLE);
							eventCrfBean.setStage(edcb.isDoubleEntry()
									? DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE
									: DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE);
							itemDataDao.updateStatusByEventCRF(eventCrfBean, Status.UNAVAILABLE, con);
						}

						eventCrfDao.update(eventCrfBean, con);
						itemSDVService.sdvCrfItems(eventCrfBean.getId(), ub.getId(), false, con);
						if (sectionIds != null) {
							for (int sectionId : sectionIds) {
								eventCrfDao.savePartialSectionInfo(eventCrfBean.getId(), sectionId, con);
							}
						}
					}

					for (int studyEventId : studyEventIds) {
						if (studyEventId > 0) {
							StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(studyEventId);
							seb.setUpdatedDate(new Date());
							seb.setUpdater(ub);
							studyEventDao.update(seb, con);
						}
					}
				}
			}

			con.commit();
			con.setAutoCommit(true);

			for (int studyEventId : studyEventIds) {
				if (studyEventId > 0) {
					StudyEventBean seb = (StudyEventBean) studyEventDao.findByPK(studyEventId);
					SubjectEventStatusUtil.determineSubjectEventState(seb, new DAOWrapper(ds));
					studyEventDao.update(seb, con);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
			throw ex;
		} finally {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
				try {
					con.close();
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
			}
		}
		return containers;
	}

	private List<ImportDataRuleRunnerContainer> ruleRunSetup(ODMContainer odmContainer, Boolean runRulesOptimisation,
			Connection connection, DataSource dataSource, StudyBean studyBean, UserAccountBean userBean) {
		List<ImportDataRuleRunnerContainer> containers = new ArrayList<ImportDataRuleRunnerContainer>();
		LOGGER.debug("=== about to check if odm container is null ===");
		if (odmContainer != null) {
			ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
			LOGGER.debug("=== found number of rules present: " + ruleSetService.getCountByStudy(studyBean) + " ===");
			if (ruleSetService.getCountByStudy(studyBean) > 0) {
				ImportDataRuleRunnerContainer container;
				for (SubjectDataBean subjectDataBean : subjectDataBeans) {
					container = new ImportDataRuleRunnerContainer();
					container.initRuleSetsAndTargets(dataSource, studyBean, subjectDataBean, ruleSetService);
					LOGGER.debug("=== found container: should run rules? " + container.getShouldRunRules() + " ===");
					if (container.getShouldRunRules()) {
						LOGGER.debug("=== added a container in run rule setup ===");
						containers.add(container);
					}
				}
				if (!containers.isEmpty()) {
					LOGGER.debug("=== running rules dry run ===");
					ruleSetService.runRulesInImportData(runRulesOptimisation, connection, containers, studyBean,
							userBean, ExecutionMode.DRY_RUN);
				}
			}
		}
		return containers;
	}

	/**
	 * Runs rules and generate message.
	 *
	 * @param runRulesOptimisation
	 *            Boolean
	 * @param skippedItemIds
	 *            Set<Integer>
	 * @param studyBean
	 *            StudyBean
	 * @param userBean
	 *            UserAccountBean
	 * @param containers
	 *            List<ImportDataRuleRunnerContainer>
	 * @return String
	 */
	public String runRulesAndGenerateMessage(Boolean runRulesOptimisation, Set<Integer> skippedItemIds,
			StudyBean studyBean, UserAccountBean userBean, List<ImportDataRuleRunnerContainer> containers) {
		String message = "";
		Connection con = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			LOGGER.debug("=== about to run rules ===");
			List<String> messages = new ArrayList<String>();
			if (containers != null && !containers.isEmpty()) {
				ruleSetService.getDynamicsMetadataService().getExpressionService().clearItemDataCache();
				HashMap<String, ArrayList<String>> summary = ruleSetService.runRulesInImportData(runRulesOptimisation,
						con, containers, skippedItemIds, studyBean, userBean, ExecutionMode.SAVE);
				LOGGER.debug("=== found summary " + summary.toString());
				messages = extractRuleActionWarnings(summary);
			}
			con.commit();
			con.close();
			message = ruleActionWarnings(messages);
		} catch (SQLException ex) {
			LOGGER.error("Error has occurred.", ex);
		} finally {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
				try {
					con.close();
				} catch (Exception ex) {
					LOGGER.error("Error has occurred.", ex);
				}
			}
		}
		return message;
	}

	/**
	 * Saves audit items.
	 * 
	 * @param auditItemList
	 *            List<Map<String, Object>
	 */
	public void saveAuditItems(List<Map<String, Object>> auditItemList) {
		if (auditItemList != null && auditItemList.size() > 0) {
			new AuditDAO(ds).saveItems(auditItemList);
		}
	}

	private List<String> extractRuleActionWarnings(HashMap<String, ArrayList<String>> summaryMap) {
		List<String> messages = new ArrayList<String>();
		if (summaryMap != null && !summaryMap.isEmpty()) {
			for (String key : summaryMap.keySet()) {
				StringBuilder mesg = new StringBuilder(key + " : ");
				for (String s : summaryMap.get(key)) {
					mesg.append(s).append(", ");
				}
				messages.add(mesg.toString());
			}
		}
		return messages;
	}

	private String ruleActionWarnings(List<String> warnings) {
		if (warnings.isEmpty()) {
			return "";
		} else {
			StringBuilder mesg = new StringBuilder(respage.getString("rule_action_warnings"));
			for (String s : warnings) {
				mesg.append(s).append("; ");
			}
			return mesg.toString();
		}
	}

	private StudyParameterValueDAO getStudyParameterValueDAO() {
		return new StudyParameterValueDAO(ds);
	}

	private class SubjectInfoHolder {
		private String oid;
		private HashMap<String, ArrayList<String>> eventsWithCRFs;

		public SubjectInfoHolder() {
			oid = "";
			eventsWithCRFs = new HashMap<String, ArrayList<String>>();
		}

		public String getOid() {
			return oid;
		}

		public void setOid(String oid) {
			this.oid = oid;
		}

		/**
		 * Add a new event to the map.
		 * @param eventOid String.
		 */
		public void addEvent(String eventOid) {
			if (!this.eventsWithCRFs.containsKey(eventOid)) {
				this.eventsWithCRFs.put(eventOid, new ArrayList<String>());
			}
		}

		/**
		 * Add a new CRF oid to the event.
		 * @param eventOid String
		 * @param crfOid String
		 */
		public void addCRFToEvent(String eventOid, String crfOid) {
			ArrayList<String> crfOIDs = this.eventsWithCRFs.get(eventOid);
			if (crfOIDs == null) {
				crfOIDs = new ArrayList<String>();
			}
			crfOIDs.add(crfOid);
			this.eventsWithCRFs.put(eventOid, crfOIDs);
		}

		/**
		 * Check if event is already present in the list.
		 * @param eventOid String
		 * @return boolean
		 */
		public boolean eventAlreadyPresent(String eventOid) {
			ArrayList<String> crfOIDs = this.eventsWithCRFs.get(eventOid);
			return  crfOIDs != null && crfOIDs.size() > 0;
		}

		public boolean eventCRFAlreadyPresent(String eventOid, String crfOid) {
			if (eventAlreadyPresent(eventOid)) {
				ArrayList<String> crfOIDs = this.eventsWithCRFs.get(eventOid);
				return crfOIDs.contains(crfOid);
			} else {
				return false;
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}
			if (!SubjectInfoHolder.class.isAssignableFrom(other.getClass())) {
				return false;
			}
			SubjectInfoHolder otherHolder = (SubjectInfoHolder) other;

			return this.oid.equals(otherHolder.oid);
		}
	}
}
