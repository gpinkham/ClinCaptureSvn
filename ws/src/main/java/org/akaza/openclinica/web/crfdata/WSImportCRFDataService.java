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

package org.akaza.openclinica.web.crfdata;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
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
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * Import CRF Data Service.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WSImportCRFDataService {

	public static final Logger LOGGER = LoggerFactory.getLogger(WSImportCRFDataService.class.getName());

	private final DataSource ds;

	private ItemDataDAO itemDataDao;
	private ResourceBundle respage;

	/**
	 * Public contructor.
	 * @param ds DataSource
	 * @param locale Locale
	 */
	public WSImportCRFDataService(DataSource ds, Locale locale) {
		ResourceBundleProvider.updateLocale(locale);
		respage = ResourceBundleProvider.getPageMessagesBundle(locale);
		this.ds = ds;
	}

	/**
	 * Look up EventCRFBeans by the following: Study Subject, Study Event, CRF Version, using the
	 * findByEventSubjectVersion method in EventCRFDAO. May return more than one, hmm.
	 *
	 * @param odmContainer ODMContainer.
	 * @param ub UserAccountBean.
	 * @return List of EventCRFBeans.
	 */
	public List<EventCRFBean> fetchEventCRFBeans(ODMContainer odmContainer, UserAccountBean ub) {
		ArrayList<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
		ArrayList<Integer> eventCRFBeanIds = new ArrayList<Integer>();
		EventCRFDAO eventCrfDAO = new EventCRFDAO(ds);
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
		StudyDAO studyDAO = new StudyDAO(ds);
		StudyEventDAO studyEventDAO = new StudyEventDAO(ds);

		String studyOID = odmContainer.getCrfDataPostImportContainer().getStudyOID();
		StudyBean studyBean = studyDAO.findByOid(studyOID);
		ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
		for (SubjectDataBean subjectDataBean : subjectDataBeans) {
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();

			StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(subjectDataBean.getSubjectOID(),
					studyBean.getId());
			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();

				String sampleOrdinal = studyEventDataBean.getStudyEventRepeatKey() == null ? "1" : studyEventDataBean
						.getStudyEventRepeatKey();

				StudyEventDefinitionBean studyEventDefinitionBean = studyEventDefinitionDAO.findByOidAndStudy(
						studyEventDataBean.getStudyEventOID(), studyBean.getId(), studyBean.getParentStudyId());
				LOGGER.info("find all by def and subject " + studyEventDefinitionBean.getName() + " study subject "
						+ studySubjectBean.getName());
				StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO
						.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectBean.getId(),
								studyEventDefinitionBean.getId(), Integer.parseInt(sampleOrdinal));

				if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.LOCKED)
						|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)
						|| studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.STOPPED)) {
					continue;
				}

				for (FormDataBean formDataBean : formDataBeans) {

					CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);

					ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
					for (CRFVersionBean crfVersionBean : crfVersionBeans) {

						ArrayList<EventCRFBean> eventCrfBeans = eventCrfDAO.findByEventSubjectVersion(studyEventBean,
								studySubjectBean, crfVersionBean);
						// what if we have begun with creating a study
						// event, but haven't entered data yet? this would
						// have us with a study event, but no corresponding
						// event crf, yet.
						if (eventCrfBeans.isEmpty()) {
							LOGGER.debug("   found no event crfs from Study Event id " + studyEventBean.getId()
									+ ", location " + studyEventBean.getLocation());
							// spell out criteria and create a bean if
							// necessary, avoiding false-positives
							if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SCHEDULED)
									|| studyEventBean.getSubjectEventStatus().equals(
											SubjectEventStatus.DATA_ENTRY_STARTED)
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
		return eventCRFBeans;
	}

	/**
	 * Generate Summary Statistic.
	 * @param odmContainer ODMContainer
	 * @param wrappers List of DisplayItemBeanWrapper
	 * @return SummaryStatsBean
	 */
	@SuppressWarnings("unused")
	public SummaryStatsBean generateSummaryStatsBean(ODMContainer odmContainer, List<DisplayItemBeanWrapper> wrappers) {
		int countSubjects = 0;
		int countEventCRFs = 0;
		int discNotesGenerated = 0;
		for (DisplayItemBeanWrapper wr : wrappers) {
			HashMap validations = wr.getValidationErrors();
			discNotesGenerated += validations.size();
		}
		ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();
		countSubjects += subjectDataBeans.size();
		for (SubjectDataBean subjectDataBean : subjectDataBeans) {
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();

			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				countEventCRFs += 1;
			}
		}

		SummaryStatsBean ssBean = new SummaryStatsBean();
		ssBean.setDiscNoteCount(discNotesGenerated);
		ssBean.setEventCrfCount(countEventCRFs);
		ssBean.setStudySubjectCount(countSubjects);
		return ssBean;
	}

	/**
	 * Lookup Validation Errors.
	 * @param request HttpServletRequest
	 * @param odmContainer ODMContainer
	 * @param ub UserAccountBean
	 * @param totalValidationErrors HashMap of String String
	 * @param hardValidationErrors HashMap of String String
	 * @param permittedEventCRFIds ArrayList of Integers
	 * @return List of DisplayItemBeanWrapper.
	 * @throws OpenClinicaException in case of error in data.
	 */
	public List<DisplayItemBeanWrapper> lookupValidationErrors(HttpServletRequest request, ODMContainer odmContainer,
			UserAccountBean ub, HashMap<String, String> totalValidationErrors,
			HashMap<String, String> hardValidationErrors, ArrayList<Integer> permittedEventCRFIds)
			throws OpenClinicaException {

		DisplayItemBeanWrapper displayItemBeanWrapper = null;
		HashMap validationErrors;
		List<DisplayItemBeanWrapper> wrappers = new ArrayList<DisplayItemBeanWrapper>();
		ImportHelper importHelper = new ImportHelper();
		FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
		DiscrepancyValidator discValidator = new DiscrepancyValidator(request, discNotes);
		// create a second Validator, this one for hard edit checks
		HashMap<String, String> hardValidator = new HashMap<String, String>();

		StudyEventDAO studyEventDAO = new StudyEventDAO(ds);
		StudyDAO studyDAO = new StudyDAO(ds);
		StudyBean studyBean = studyDAO.findByOid(odmContainer.getCrfDataPostImportContainer().getStudyOID());
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
		StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(ds);
		HashMap<String, ItemDataBean> blankCheck = new HashMap<String, ItemDataBean>();
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
				int parentStudyId = studyBean.getParentStudyId();
				StudyEventDefinitionBean sedBean = sedDao.findByOidAndStudy(studyEventDataBean.getStudyEventOID(),
						studyBean.getId(), parentStudyId);
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
				LOGGER.debug("iterating through study event data beans: found " + studyEventDataBean.getStudyEventOID());
				int ordinal = 1;
				try {
					ordinal = new Integer(studyEventDataBean.getStudyEventRepeatKey());
				} catch (Exception e) {
					LOGGER.debug("Exception is thrown: " + e.getMessage());
				}
				StudyEventBean studyEvent = (StudyEventBean) studyEventDAO
						.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectBean.getId(), sedBean.getId(),
								ordinal);
				displayItemBeans = new ArrayList<DisplayItemBean>();

				for (FormDataBean formDataBean : formDataBeans) {
					int maxOrdinal = 1;
					
					CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
					EventCRFDAO eventCRFDAO = new EventCRFDAO(ds);
					ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formDataBean.getFormOID());
					ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
					if ((crfVersionBeans == null) || (crfVersionBeans.size() == 0)) {
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("your_crf_version_oid_did_not_generate"));
						Object[] arguments = { formDataBean.getFormOID() };

						throw new OpenClinicaException(mf.format(arguments), "");
					}
					CRFVersionBean crfVersion = crfVersionBeans.get(0);
					// if you have a mispelled form oid you get an error here
					// need to error out gracefully and post an error
					LOGGER.debug("iterating through form beans: found " + crfVersion.getOid());
					// may be the point where we cut off item groups etc and
					// instead work on sections
					EventCRFBean eventCRFBean = eventCRFDAO.findByEventCrfVersion(studyEvent, crfVersion);
					EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(ds);
					EventDefinitionCRFBean eventDefinitionCRF = eventDefinitionCRFDAO
							.findByStudyEventIdAndCRFVersionId(studyBean, studyEvent.getId(), crfVersion.getId());
					if (permittedEventCRFIds.contains(new Integer(eventCRFBean.getId()))) {
						for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {

							ArrayList<ItemBean> blankCheckItems = new ArrayList<ItemBean>();
							ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
							LOGGER.debug("iterating through group beans: " + itemGroupDataBean.getItemGroupOID());
							// put a checker in here
							ItemGroupDAO itemGroupDAO = new ItemGroupDAO(ds);
							ItemGroupBean testBean = itemGroupDAO.findByOid(itemGroupDataBean.getItemGroupOID());
							if (testBean == null) {
								MessageFormat mf = new MessageFormat("");
								mf.applyPattern(respage.getString("your_item_group_oid_for_form_oid"));
								Object[] arguments = { itemGroupDataBean.getItemGroupOID(), formDataBean.getFormOID() };

								throw new OpenClinicaException(mf.format(arguments), "");
							}
							totalItemDataBeanCount += itemDataBeans.size();

							for (ImportItemDataBean importItemDataBean : itemDataBeans) {
								LOGGER.debug("   iterating through item data beans: " + importItemDataBean.getItemOID());
								ItemDAO itemDAO = new ItemDAO(ds);
								ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(ds);

								List<ItemBean> itemBeans = itemDAO.findByOid(importItemDataBean.getItemOID());
								if (!itemBeans.isEmpty()) {
									ItemBean itemBean = itemBeans.get(0);
									LOGGER.debug("   found " + itemBean.getName());
									
									DisplayItemBean displayItemBean = new DisplayItemBean();
									displayItemBean.setItem(itemBean);

									ArrayList<ItemFormMetadataBean> metadataBeans = itemFormMetadataDAO
											.findAllByItemId(itemBean.getId());
									LOGGER.debug("      found metadata item beans: " + metadataBeans.size());
									int groupOrdinal = 1;
									if (itemGroupDataBean.getItemGroupRepeatKey() != null) {
										try {
											groupOrdinal = new Integer(itemGroupDataBean.getItemGroupRepeatKey());
											if (groupOrdinal > maxOrdinal) {
												maxOrdinal = groupOrdinal;
											}
										} catch (Exception e) {
											LOGGER.debug("found npe for group ordinals, line 344!");
										}
									}
									ItemDataBean itemDataBean = createItemDataBean(itemBean, eventCRFBean,
											importItemDataBean.getValue(), ub, groupOrdinal);
									blankCheckItems.add(itemBean);
									String newKey = groupOrdinal + "_" + itemGroupDataBean.getItemGroupOID() + "_"
											+ itemBean.getOid() + "_" + subjectDataBean.getSubjectOID();
									blankCheck.put(newKey, itemDataBean);
									LOGGER.info("adding " + newKey + " to blank checks");
									if (!metadataBeans.isEmpty()) {
										ItemFormMetadataBean metadataBean = metadataBeans.get(0);
										// also
										// possible
										// nullpointer
										displayItemBean.setData(itemDataBean);
										displayItemBean.setMetadata(metadataBean);
										// set event def crf?
										displayItemBean.setEventDefinitionCRF(eventDefinitionCRF);
										String eventCRFRepeatKey = studyEventDataBean.getStudyEventRepeatKey();
										// if you do indeed leave off this in the XML it will pass but return 'null' tbh
										attachValidator(displayItemBean, importHelper, discValidator, hardValidator,
												request, eventCRFRepeatKey, studySubjectBean.getOid());
										displayItemBeans.add(displayItemBean);

									} else {
										MessageFormat mf = new MessageFormat("");
										mf.applyPattern(respage.getString("no_metadata_could_be_found"));
										Object[] arguments = { importItemDataBean.getItemOID() };

										throw new OpenClinicaException(mf.format(arguments), "");
									}
								} else {
									// report the error there
									MessageFormat mf = new MessageFormat("");
									mf.applyPattern(respage.getString("no_item_could_be_found"));
									Object[] arguments = { importItemDataBean.getItemOID() };
									throw new OpenClinicaException(mf.format(arguments), "");
								}
							} // end item data beans
							LOGGER.debug(".. found blank check: " + blankCheck.toString());

							for (int i = 1; i <= maxOrdinal; i++) {
								for (ItemBean itemBean : blankCheckItems) {
									String newKey = i + "_" + itemGroupDataBean.getItemGroupOID() + "_"
											+ itemBean.getOid() + "_" + subjectDataBean.getSubjectOID();
									if (blankCheck.get(newKey) == null) {
										// if it already exists, Do Not Add It.
										ItemDataBean itemDataCheck = getItemDataDao()
												.findByItemIdAndEventCRFIdAndOrdinal(itemBean.getId(),
														eventCRFBean.getId(), i);
										LOGGER.debug("found item data bean id: " + itemDataCheck.getId()
												+ " for ordinal " + i);
										if (itemDataCheck.getId() == 0) {
											ItemDataBean blank = createItemDataBean(itemBean, eventCRFBean, "", ub, i);
											DisplayItemBean displayItemBean = new DisplayItemBean();
											displayItemBean.setItem(itemBean);
											displayItemBean.setData(blank);
											displayItemBean.setEventDefinitionCRF(eventDefinitionCRF);
											displayItemBeans.add(displayItemBean);
											LOGGER.debug("... adding display item bean");
										}
									}
									LOGGER.debug("found a blank at " + i + ", adding " + blankCheckItems.size()
											+ " blank items");
								}
							}
						} // end item group data beans
					}
					CRFDAO crfDAO = new CRFDAO(ds);
					CRFBean crfBean = crfDAO.findByVersionId(crfVersion.getCrfId());
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
					for (Object errorKey : hardValidator.keySet()) {
						LOGGER.debug(errorKey.toString() + " -- " + hardValidator.get(errorKey.toString()));
						hardValidationErrors.put(errorKey.toString(), hardValidator.get(errorKey.toString()));
					}

					String studyEventId = studyEvent.getId() + "";
					String crfVersionId = crfVersion.getId() + "";

					LOGGER.debug("creation of wrapper: original count of display item beans " + displayItemBeans.size()
							+ ", count of item data beans " + totalItemDataBeanCount + " count of validation errors "
							+ validationErrors.size() + " count of study subjects " + subjectDataBeans.size()
							+ " count of event crfs " + totalEventCRFCount + " count of hard error checks "
							+ hardValidator.size());
					// possibly create the import summary here
					LOGGER.debug("creation of wrapper: max ordinal found " + maxOrdinal);
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
					discValidator = new DiscrepancyValidator(request, discNotes);
				}
				wrappers.add(displayItemBeanWrapper);
			}
		}
		return wrappers;
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

	private void attachValidator(DisplayItemBean displayItemBean, ImportHelper importHelper,
			DiscrepancyValidator discValidator, HashMap<String, String> hardv,
			javax.servlet.http.HttpServletRequest request, String eventCRFRepeatKey, String studySubjectOID)
			throws OpenClinicaException {
		org.akaza.openclinica.bean.core.ResponseType rt = displayItemBean.getMetadata().getResponseSet()
				.getResponseType();
		String itemOid = displayItemBean.getItem().getOid() + "_" + eventCRFRepeatKey + "_"
				+ displayItemBean.getData().getOrdinal() + "_" + studySubjectOID;

		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			ItemFormMetadataBean ifm = displayItemBean.getMetadata();
			String widthDecimal = ifm.getWidthDecimal();
			// what if it's a date? parse if out so that we go from iso 8601 to
			// mm/dd/yyyy
			if (displayItemBean.getItem().getDataType().equals(ItemDataType.DATE)) {
				// pass it if it is blank, tbh
				if (!"".equals(displayItemBean.getData().getValue())) {
					String dateValue = displayItemBean.getData().getValue();
					SimpleDateFormat sdfSqldate = new SimpleDateFormat("yyyy-MM-dd");
					sdfSqldate.setLenient(false);
					try {
						// htaycher: database exspects format YYYY-MM-DD
						Date originalDate = sdfSqldate.parse(dateValue);
						// String replacementValue = new SimpleDateFormat("MM/dd/yyyy").format(originalDate);

						String replacementValue = new SimpleDateFormat("yyyy-MM-dd").format(originalDate);
						displayItemBean.getData().setValue(replacementValue);
					} catch (ParseException pe1) {
						// next version; fail if it does not pass iso 8601
						MessageFormat mf = new MessageFormat("");
						mf.applyPattern(respage.getString("you_have_a_date_value_which_is_not"));
						Object[] arguments = { displayItemBean.getItem().getOid() };
						hardv.put(itemOid, mf.format(arguments));
					}
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.ST)) {
				int width = Validator.parseWidth(widthDecimal);
				if (width > 0 && displayItemBean.getData().getValue().length() > width) {
					hardv.put(itemOid, "This value exceeds required width=" + width);
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.INTEGER)) {
				try {
					int width = Validator.parseWidth(widthDecimal);
					if (width > 0 && displayItemBean.getData().getValue().length() > width) {
						hardv.put(itemOid, "This value exceeds required width=" + width);
					}
					// now, didn't check decimal for testInt.
				} catch (Exception e) { // should be a sub class
					if (!"".equals(displayItemBean.getData().getValue())) {
						hardv.put(itemOid, "This value is not an integer.");
					}
				}
			} else if (displayItemBean.getItem().getDataType().equals(ItemDataType.REAL)) {
				try {
					new Float(displayItemBean.getData().getValue());
					int width = Validator.parseWidth(widthDecimal);
					if (width > 0 && displayItemBean.getData().getValue().length() > width) {
						hardv.put(itemOid, "This value exceeds required width=" + width);
					}
					int decimal = Validator.parseDecimal(widthDecimal);
					if (decimal > 0
							&& BigDecimal.valueOf(new Double(displayItemBean.getData().getValue()))
									.scale() > decimal) {
						hardv.put(itemOid, "This value exceeds required decimal=" + decimal);
					}
				} catch (Exception ee) {
					// pass if blank, tbh
					if (!"".equals(displayItemBean.getData().getValue())) {
						hardv.put(itemOid, "This value is not a real number.");
					}
				}
			}
			// what if it's a phone number? how often does that happen?

			request.setAttribute(itemOid, displayItemBean.getData().getValue());
			displayItemBean = importHelper.validateDisplayItemBeanText(discValidator, displayItemBean, itemOid);
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
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			String theValue = matchValueWithOptions(displayItemBean, displayItemBean.getData().getValue(),
					displayItemBean.getMetadata().getResponseSet().getOptions());
			request.setAttribute(itemOid, theValue);
			LOGGER.debug("        found the value for radio/single: " + theValue);
			if (theValue == null && displayItemBean.getData().getValue() != null
					&& !displayItemBean.getData().getValue().isEmpty()) {
				LOGGER.debug("-- theValue was NULL, the real value was " + displayItemBean.getData().getValue());
				hardv.put(itemOid, "This is not in the correct response set.");
			}
			displayItemBean = importHelper.validateDisplayItemBeanSingleCV(discValidator, displayItemBean, itemOid);
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			String theValue = matchValueWithManyOptions(displayItemBean, displayItemBean.getData().getValue(),
					displayItemBean.getMetadata().getResponseSet().getOptions());
			request.setAttribute(itemOid, theValue);
			if (theValue == null && displayItemBean.getData().getValue() != null
					&& !displayItemBean.getData().getValue().isEmpty()) {
				hardv.put(itemOid, "This is not in the correct response set.");
			}
			displayItemBean = importHelper.validateDisplayItemBeanMultipleCV(discValidator, displayItemBean, itemOid);
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

	private String matchValueWithManyOptions(DisplayItemBean displayItemBean, String value, List options) {
		String entireOptions = "";
		String[] simValues = value.split(",");
		if (!options.isEmpty()) {
			for (Object responseOption : options) {
				ResponseOptionBean responseOptionBean = (ResponseOptionBean) responseOption;
				entireOptions += responseOptionBean.getValue();

			}
			// remove spaces, since they are causing problems:
			entireOptions = entireOptions.replace(" ", "");
			ArrayList nullValues = displayItemBean.getEventDefinitionCRF().getNullValuesList();

			for (Object nullValue : nullValues) {
				NullValue nullValueTerm = (NullValue) nullValue;
				entireOptions += nullValueTerm.getName();
			}

			for (String sim : simValues) {
				sim = sim.replace(" ", "");
				if (!entireOptions.contains(sim)) {
					return null;
				}
			}
		}
		return value;
	}

	/**
	 * Validate Study Metadata.
	 * @param odmContainer ODMContainer
	 * @param currentStudyId int
	 * @return List of String
	 */
	public List<String> validateStudyMetadata(ODMContainer odmContainer, int currentStudyId) {
		List<String> errors = new ArrayList<String>();
		MessageFormat mf = new MessageFormat("");

		try {
			StudyDAO studyDAO = new StudyDAO(ds);
			String studyOid = odmContainer.getCrfDataPostImportContainer().getStudyOID();
			StudyBean studyBean = studyDAO.findByOid(studyOid);
			if (studyBean == null) {
				mf.applyPattern(respage.getString("your_study_oid_does_not_reference_an_existing"));
				Object[] arguments = { studyOid };

				errors.add(mf.format(arguments));
				LOGGER.debug("unknown study OID");
				throw new OpenClinicaException("Unknown Study OID", "");

			} else if (studyBean.getId() != currentStudyId) {
				mf.applyPattern(respage.getString("your_current_study_is_not_the_same_as"));
				Object[] arguments = { studyBean.getName() };
				errors.add(mf.format(arguments));
			}
			ArrayList<SubjectDataBean> subjectDataBeans = odmContainer.getCrfDataPostImportContainer().getSubjectData();

			StudySubjectDAO studySubjectDAO = new StudySubjectDAO(ds);
			StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(ds);
			CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
			ItemGroupDAO itemGroupDAO = new ItemGroupDAO(ds);
			ItemDAO itemDAO = new ItemDAO(ds);

			if (subjectDataBeans != null) { // need to do this so as not to
				for (SubjectDataBean subjectDataBean : subjectDataBeans) {
					String oid = subjectDataBean.getSubjectOID();
					StudySubjectBean studySubjectBean = studySubjectDAO.findByOidAndStudy(oid, studyBean.getId());
					if (studySubjectBean == null) {
						mf.applyPattern(respage.getString("your_subject_oid_does_not_reference"));
						Object[] arguments = { oid };
						errors.add(mf.format(arguments));

						LOGGER.debug("logged an error with subject oid " + oid);
					}

					ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
					if (studyEventDataBeans != null) {
						for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
							String sedOid = studyEventDataBean.getStudyEventOID();
							StudyEventDefinitionBean studyEventDefintionBean = studyEventDefinitionDAO
									.findByOidAndStudy(sedOid, studyBean.getId(), studyBean.getParentStudyId());
							if (studyEventDefintionBean == null) {
								mf.applyPattern(respage.getString("your_study_event_oid_for_subject_oid"));
								Object[] arguments = { sedOid, oid };
								errors.add(mf.format(arguments));

								LOGGER.debug("logged an error with se oid " + sedOid + " and subject oid " + oid);
							}

							ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
							if (formDataBeans != null) {
								for (FormDataBean formDataBean : formDataBeans) {
									String formOid = formDataBean.getFormOID();
									ArrayList<CRFVersionBean> crfVersionBeans = crfVersionDAO.findAllByOid(formOid);
									if (crfVersionBeans != null) {
										for (CRFVersionBean crfVersionBean : crfVersionBeans) {
											if (crfVersionBean == null) {
												mf.applyPattern(respage
														.getString("your_crf_version_oid_for_study_event_oid"));
												Object[] arguments = { formOid, sedOid };
												errors.add(mf.format(arguments));

												LOGGER.debug("logged an error with form " + formOid + " and se oid "
														+ sedOid);
											}
										}
									} else {
										mf.applyPattern(respage.getString("your_crf_version_oid_did_not_generate"));
										Object[] arguments = { formOid };
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
														mf.applyPattern(respage
																.getString("your_item_group_oid_for_form_oid"));
														Object[] arguments = { itemGroupOID, formOid };
														errors.add(mf.format(arguments));
													}
												}
											} else {
												mf.applyPattern(respage.getString("the_item_group_oid_did_not"));
												Object[] arguments = { itemGroupOID };
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
																Object[] arguments = { itemOID, itemGroupOID };
																errors.add(mf.format(arguments));
															} else {
																LOGGER.debug("found " + itemBean.getOid() + ", passing");
															}
														}
													}
												}
											} else {
												mf.applyPattern(respage
														.getString("the_item_group_oid_did_not_contain_item_data"));
												Object[] arguments = { itemGroupOID };
												errors.add(mf.format(arguments));

											}
										}
									} else {
										mf.applyPattern(respage.getString("your_study_event_contains_no_form_data"));
										Object[] arguments = { sedOid };
										errors.add(mf.format(arguments));
									}
								}
							}
						}
					}
				}
			}
		} catch (OpenClinicaException oce) {
			LOGGER.debug("Exception is thrown: " + oce.getMessage());
		} catch (NullPointerException npe) {
			LOGGER.debug("found a nullpointer here");
		}
		return errors;
	}

	private ItemDataDAO getItemDataDao() {
		itemDataDao = this.itemDataDao != null ? itemDataDao : new ItemDataDAO(ds);
		return itemDataDao;
	}
}
