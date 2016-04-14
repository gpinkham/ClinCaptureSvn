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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.validator.StudySubjectValidator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.clinovo.util.DateUtil;

/**
 * Enroll a new subject into system.
 * 
 * @author ssachs
 * @version CVS: $Id: AddNewSubjectServlet.java,v 1.15 2005/07/05 17:20:43 jxu Exp $
 */
@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
@Component
public class AddNewSubjectServlet extends SpringServlet {

	public static final String OPEN_FIRST_CRF = "openFirstCrf";
	public static final String TRUE = "true";
	public static final int INT_255 = 255;

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private final Object simpleLockObj = new Object();

	public static final String INPUT_UNIQUE_IDENTIFIER = "uniqueIdentifier";

	public static final String INPUT_GENDER = "gender";

	public static final String INPUT_LABEL = "label";

	public static final String INPUT_SECONDARY_LABEL = "secondaryLabel";

	public static final String INPUT_ENROLLMENT_DATE = "enrollmentDate";

	public static final String INPUT_EVENT_START_DATE = "startDate";

	public static final String INPUT_GROUP = "group";

	public static final String INPUT_FATHER = "father";

	public static final String INPUT_MOTHER = "mother";

	public static final String BEAN_GROUPS = "groups";

	public static final String BEAN_DYNAMIC_GROUPS = "dynamicGroups";

	public static final String BEAN_FATHERS = "fathers";

	public static final String BEAN_MOTHERS = "mothers";

	public static final String SUBMIT_EVENT_BUTTON = "submitEvent";

	public static final String SUBMIT_ENROLL_BUTTON = "submitEnroll";

	public static final String SUBMIT_DONE_BUTTON = "submitDone";

	public static final String EDIT_DOB = "editDob";

	public static final String EXISTING_SUB_SHOWN = "existingSubShown";

	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	public static final String STUDY_EVENT_DEFINITION = "studyEventDefinition";
	public static final String LOCATION = "location";
	public static final String SELECTED_DYN_GROUP_CLASS_ID = "selectedDynGroupClassId";
	public static final String DEFAULT_DYN_GROUP_CLASS_ID = "defaultDynGroupClassId";
	public static final String DEFAULT_DYN_GROUP_CLASS_NAME = "defaultDynGroupClassName";

	public static final String G_WARNING = "gWarning";
	public static final String D_WARNING = "dWarning";
	public static final String Y_WARNING = "yWarning";

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (!currentRole.getRole().equals(Role.INVESTIGATOR)
				&& !currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)
				&& !currentRole.getRole().equals(Role.SYSTEM_ADMINISTRATOR)) {
			addPageMessage(getResText().getString("no_have_correct_privilege_to_add_subject"), request);
			throw new InsufficientPermissionException(Page.MENU_SERVLET,
					getResText().getString("no_have_correct_privilege_to_add_subject"), "1");
		}

		String dob = "";
		String yob = "";
		String gender = "";
		boolean needUpdate = false;
		SubjectBean updateSubject = null;
		SimpleDateFormat localDf = getLocalDf(request);

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_locked"), request, response);
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, getResPage().getString("current_study_frozen"), request, response);

		StudySubjectDAO ssd = getStudySubjectDAO();
		StudyDAO stdao = getStudyDAO();
		StudyGroupClassDAO sgcdao = getStudyGroupClassDAO();
		List<StudyGroupClassBean> classes;
		List<StudyGroupClassBean> dynamicClasses;
		int defaultDynGroupClassId = 0;
		String defaultDynGroupClassName = "";
		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.setStudyInfoShown(false);
		FormProcessor fp = new FormProcessor(request);

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		int studyIdToSearchOn = currentStudy.isSite() ? currentStudy.getParentStudyId() : currentStudy.getId();
		StudyBean studyToSearchOn = currentStudy.isSite()
				? (StudyBean) stdao.findByPK(currentStudy.getParentStudyId())
				: currentStudy;
		classes = sgcdao.findAllActiveByStudyId(studyIdToSearchOn, true);
		dynamicClasses = getDynamicGroupClassesByStudyId(studyIdToSearchOn);

		if (dynamicClasses.size() > 0) {
			if (dynamicClasses.get(0).isDefault()) {
				defaultDynGroupClassId = dynamicClasses.get(0).getId();
				defaultDynGroupClassName = dynamicClasses.get(0).getName();
			}
		}
		fp.addPresetValue(DEFAULT_DYN_GROUP_CLASS_ID, defaultDynGroupClassId);
		fp.addPresetValue(DEFAULT_DYN_GROUP_CLASS_NAME, defaultDynGroupClassName);

		if (!fp.isSubmitted()) {
			if (fp.getBoolean("instr")) {
				request.getSession().removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
				forwardPage(Page.INSTRUCTIONS_ENROLL_SUBJECT, request, response);
			} else {
				setUpBeans(classes, request);
				request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);

				fp.addPresetValue(INPUT_ENROLLMENT_DATE, DateUtil.printDate(new Date(),
						getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));

				String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
				logger.info("subject id setting :" + idSetting);
				// set up auto study subject id
				// If idSetting is auto, do not calculate the next
				// available ID (label) for now
				if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
					String nextLabel = ssd.findNextLabel(currentStudy, studyToSearchOn);
					fp.addPresetValue(INPUT_LABEL, nextLabel);
				}
				setPresetValues(fp.getPresetValues(), request);
				request.getSession().setAttribute(FORM_DISCREPANCY_NOTES_NAME, new FormDiscrepancyNotes());
				forwardPage(Page.ADD_NEW_SUBJECT, request, response);
			}
		} else {
			// submitted
			// Record parameters' values on input page so those values
			// could be used to compare against
			// values in database <subject> table for "add existing subject"
			if (!fp.getBoolean(EXISTING_SUB_SHOWN)) {
				dob = fp.getString(StudySubjectValidator.INPUT_DOB);
				yob = fp.getString(StudySubjectValidator.INPUT_YOB);
				gender = fp.getString(INPUT_GENDER);
			}

			StudySubjectValidator validator = new StudySubjectValidator();
			HashMap errors = validator.validate(request, getConfigurationDao(), currentStudy, true);

			SubjectDAO sdao = getSubjectDAO();
			String uniqueIdentifier = fp.getString(INPUT_UNIQUE_IDENTIFIER);
			// global Id
			SubjectBean subjectWithSameId = new SubjectBean();
			boolean showExistingRecord = false;
			if (!uniqueIdentifier.equals("")) {
				boolean subjectWithSameIdInCurrentStudyTree = false;
				// checks whether there is a subject with same id inside current
				// study/site
				subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier, currentStudy.getId());

				if (subjectWithSameId.isActive()) { // ||
					Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
							getResException().getString("subject_with_person_ID") + " " + uniqueIdentifier + " "
									+ getResException().getString("is_already_enrolled_in_this_study"));

					subjectWithSameIdInCurrentStudyTree = true;
					logger.info("just added unique id in study tree");
				} else {
					// checks whether there is a subject with same id inside
					// sites of
					// current study
					subjectWithSameId = sdao.findByUniqueIdentifierAndParentStudy(uniqueIdentifier,
							currentStudy.getId());
					if (subjectWithSameId.isActive()) {
						StudySubjectBean ssub = ssd.findBySubjectIdAndStudy(subjectWithSameId.getId(), currentStudy);
						StudyBean site = (StudyBean) stdao.findByPK(ssub.getStudyId());
						Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
								getResException().getString("this_subject_person_ID") + " " + uniqueIdentifier
										+ getResException().getString("has_already_enrolled_site") + site.getName()
										+ getResException().getString("of_current_study_need_to_move")
										+ getResException().getString("please_have_user_manage_privileges"));
						subjectWithSameIdInCurrentStudyTree = true;
					} else {
						// check whether there is a subject with same id in the
						// parent study
						subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier,
								currentStudy.getParentStudyId());
						if (subjectWithSameId.isActive()) {
							Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
									getResException().getString("this_subject_with_person_ID") + " " + uniqueIdentifier
											+ getResException().getString("has_already_enrolled_parent_study"));

							subjectWithSameIdInCurrentStudyTree = true;
						} else {
							// YW 11-26-2007 << check whether there is a subject
							// with the same id in other sites of the same study
							subjectWithSameId = sdao.findByUniqueIdentifierAndParentStudy(uniqueIdentifier,
									currentStudy.getParentStudyId());
							if (subjectWithSameId.isActive()) {
								Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
										getResException().getString("this_subject_with_person_ID") + " "
												+ uniqueIdentifier
												+ getResException().getString("has_already_enrolled_site_study"));

								subjectWithSameIdInCurrentStudyTree = true;
							}
						}
					}
				}

				if (!subjectWithSameIdInCurrentStudyTree) {
					subjectWithSameId = sdao.findByUniqueIdentifier(uniqueIdentifier);
					// found subject with same id in other study
					if (subjectWithSameId.isActive()) {
						showExistingRecord = true;
					}
				}
			}

			boolean insertWithParents = fp.getInt(INPUT_MOTHER) > 0 || fp.getInt(INPUT_FATHER) > 0;

			if (fp.getInt(INPUT_MOTHER) > 0) {
				SubjectBean mother = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_MOTHER));
				if (mother == null || !mother.isActive() || mother.getGender() != 'f') {
					Validator.addError(errors, INPUT_MOTHER,
							getResException().getString("please_choose_valid_female_subject_as_mother"));
				}
			}

			if (fp.getInt(INPUT_FATHER) > 0) {
				SubjectBean father = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_FATHER));
				if (father == null || !father.isActive() || father.getGender() != 'm') {
					Validator.addError(errors, INPUT_FATHER,
							getResException().getString("please_choose_valid_male_subject_as_father"));
				}
			}

			String label = fp.getString(INPUT_LABEL).trim();
			// Shaoyu Su: if the form submitted for field "INPUT_LABEL" has
			// value of "AUTO_LABEL",
			// then Study Subject ID should be created when db row is inserted.
			if (!label.equalsIgnoreCase(getResWord().getString("id_generated_Save_Add"))) {
				StudySubjectBean subjectWithSameLabel = ssd.findByLabelAndStudy(label, currentStudy);

				StudySubjectBean subjectWithSameLabelInParent = new StudySubjectBean();
				if (currentStudy.getParentStudyId() > 0) {
					subjectWithSameLabelInParent = ssd.findSameByLabelAndStudy(label, currentStudy.getParentStudyId(),
							0); // blank id since the ss hasn't been created yet, tbh
				}
				if (subjectWithSameLabel.isActive() || subjectWithSameLabelInParent.isActive()) {
					Validator.addError(errors, INPUT_LABEL,
							getResException().getString("another_assigned_this_ID_choose_unique"));
				}
			}

			if (!classes.isEmpty()) {
				for (int i = 0; i < classes.size(); i++) {
					StudyGroupClassBean sgc = classes.get(i);
					int groupId = fp.getInt("studyGroupId" + i);
					String notes = fp.getString("notes" + i);

					if ("Required".equals(sgc.getSubjectAssignment()) && groupId == 0) {
						Validator.addError(errors, "studyGroupId" + i,
								getResException().getString("group_class_is_required"));
					}
					if (notes.trim().length() > INT_255) {
						Validator.addError(errors, "notes" + i, getResException().getString("notes_cannot_longer_255"));
					}
					sgc.setStudyGroupId(groupId);
					sgc.setGroupNotes(notes);
				}
			}

			if (!errors.isEmpty()) {

				addPageMessage(getResPage().getString("there_were_some_errors_submission"), request);

				setInputMessages(errors, request);
				fp.addPresetValue(StudySubjectValidator.INPUT_DOB, fp.getString(StudySubjectValidator.INPUT_DOB));
				fp.addPresetValue(StudySubjectValidator.INPUT_YOB, fp.getString(StudySubjectValidator.INPUT_YOB));
				fp.addPresetValue(INPUT_GENDER, fp.getString(INPUT_GENDER));
				fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, uniqueIdentifier);
				fp.addPresetValue(INPUT_LABEL, label);
				fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
				fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
				fp.addPresetValue(INPUT_EVENT_START_DATE, fp.getString(INPUT_EVENT_START_DATE));
				fp.addPresetValue(STUDY_EVENT_DEFINITION, fp.getInt(STUDY_EVENT_DEFINITION));
				fp.addPresetValue(LOCATION, fp.getString(LOCATION));
				fp.addPresetValue(SELECTED_DYN_GROUP_CLASS_ID, request.getParameter("dynamicGroupClassId"));

				if (currentStudy.isGenetic()) {
					String[] intFields = {INPUT_GROUP, INPUT_FATHER, INPUT_MOTHER};
					fp.setCurrentIntValuesAsPreset(intFields);
				}
				fp.addPresetValue(EDIT_DOB, fp.getString(EDIT_DOB));
				setPresetValues(fp.getPresetValues(), request);

				setUpBeans(classes, request);
				request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);
				boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

				if (!existingSubShown) {
					forwardPage(Page.ADD_NEW_SUBJECT, request, response);
				} else {
					forwardPage(Page.ADD_EXISTING_SUBJECT, request, response);
				}
			} else {
				// no errors
				StudySubjectBean studySubject = new StudySubjectBean();
				SubjectBean subject = new SubjectBean();
				boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

				if (showExistingRecord && !existingSubShown) {
					needUpdate = false;
					subject = subjectWithSameId;
					Calendar cal = Calendar.getInstance();
					int year = 0;
					if (subject.getDateOfBirth() != null) {
						cal.setTime(subject.getDateOfBirth());
						year = cal.get(Calendar.YEAR);
						fp.addPresetValue(StudySubjectValidator.INPUT_DOB, localDf.format(subject.getDateOfBirth()));
					} else {
						fp.addPresetValue(StudySubjectValidator.INPUT_DOB, "");
					}

					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")
							&& !subject.isDobCollected()) {
						fp.addPresetValue(StudySubjectValidator.INPUT_DOB, fp.getString(StudySubjectValidator.INPUT_DOB));
					}
					fp.addPresetValue(StudySubjectValidator.INPUT_YOB, String.valueOf(year));

					if (!currentStudy.getStudyParameterConfig().getGenderRequired().equals("false")) {
						fp.addPresetValue(INPUT_GENDER, subject.getGender() + "");
					} else {
						fp.addPresetValue(INPUT_GENDER, "");
					}

					// Shaoyu Su: delay setting INPUT_LABEL field
					if (!label.equalsIgnoreCase(getResWord().getString("id_generated_Save_Add"))) {
						fp.addPresetValue(INPUT_LABEL, label);
					}
					fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
					fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
					fp.addPresetValue(INPUT_EVENT_START_DATE, fp.getString(INPUT_EVENT_START_DATE));

					fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, subject.getUniqueIdentifier());

					fp.addPresetValue(INPUT_FATHER, subject.getFatherId());
					fp.addPresetValue(INPUT_MOTHER, subject.getMotherId());

					setPresetValues(fp.getPresetValues(), request);
					setUpBeans(classes, request);
					request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);

					int warningCount = 0;
					if (currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
						if (String.valueOf(subjectWithSameId.getGender()).equals(" ")) {
							fp.addPresetValue(G_WARNING, "emptytrue");
							fp.addPresetValue(INPUT_GENDER, gender);
							needUpdate = true;
							updateSubject = subjectWithSameId;
							updateSubject.setGender(gender.toCharArray()[0]);
							warningCount++;
						} else if (!String.valueOf(subjectWithSameId.getGender()).equals(gender)) {
							fp.addPresetValue(G_WARNING, "true");
							warningCount++;
						} else {
							fp.addPresetValue(G_WARNING, "false");
						}
					} else {
						fp.addPresetValue(G_WARNING, "false");
					}

					// Current study required dob
					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
						// date-of-birth in subject table is not completed
						if (!subjectWithSameId.isDobCollected()) {
							needUpdate = true;
							updateSubject = subjectWithSameId;
							updateSubject.setDobCollected(true);

							if (subjectWithSameId.getDateOfBirth() == null) {
								fp.addPresetValue(StudySubjectValidator.INPUT_DOB, dob);
								updateSubject.setDateOfBirth(new Date(dob));
							} else {
								String y = String.valueOf(subjectWithSameId.getDateOfBirth()).split("\\-")[0];
								String[] d = dob.split("\\/");
								// if year-of-birth in subject table
								if (!y.equals("0001")) {
									// if year-of-birth is different from dob's
									// year, use year-of-birth
									if (!y.equals(d[2])) {
										fp.addPresetValue(D_WARNING, "dobYearWrong");
										fp.addPresetValue(StudySubjectValidator.INPUT_DOB, d[0] + "/" + d[1] + "/" + y);
										updateSubject.setDateOfBirth(sdf.parse(d[0] + "/" + d[1] + "/" + y));
									} else {
										fp.addPresetValue(D_WARNING, "dobUsed");
										fp.addPresetValue(StudySubjectValidator.INPUT_DOB, dob);
										updateSubject.setDateOfBirth(sdf.parse(dob));
									}
								} else {
									fp.addPresetValue(D_WARNING, "emptyD");
									fp.addPresetValue(StudySubjectValidator.INPUT_DOB, dob);
									updateSubject.setDateOfBirth(sdf.parse(dob));
								}
							}
							warningCount++;
						} else if (!localDf.format(subjectWithSameId.getDateOfBirth()).equals(dob)) {
							fp.addPresetValue(D_WARNING, "currentDOBWrong");
							warningCount++;
						} else {
							fp.addPresetValue(D_WARNING, "false");
						}
					} else if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
						String y = String.valueOf(subjectWithSameId.getDateOfBirth()).split("\\-")[0];
						// year of date-of-birth in subject table is avaible
						if (!y.equals("0001")) {
							// year in subject table doesn't match yob,
							if (!y.equals(yob)) {
								fp.addPresetValue(Y_WARNING, "yobWrong");
								warningCount++;
							} else {
								fp.addPresetValue(Y_WARNING, "false");
							}
						} else {
							needUpdate = true;
							updateSubject = subjectWithSameId;
							fp.addPresetValue(Y_WARNING, "yearEmpty");
							fp.addPresetValue(StudySubjectValidator.INPUT_YOB, yob);
							updateSubject.setDateOfBirth(sdf.parse("01/01/" + yob));
							warningCount++;
						}
					} else {
						fp.addPresetValue(Y_WARNING, "false");
					}

					if (warningCount > 0) {
						forwardPage(Page.ADD_EXISTING_SUBJECT, request, response);
						return;
					}
				}
				// YW << If showExistingRecord, which means there is a record
				// for the subject
				// in <subject> table, the subject only needs to be inserted
				// into <studysubject> table.
				// In other words, if(!showExistingRecord), the subject needs
				// to be inserted into both <subject> and <studysubject> tables
				if (!showExistingRecord) {
					// YW >>
					if (!StringUtil.isBlank(fp.getString(INPUT_GENDER))) {
						subject.setGender(fp.getString(INPUT_GENDER).charAt(0));
					} else {
						subject.setGender(' ');
					}

					subject.setUniqueIdentifier(uniqueIdentifier);

					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
						if (!StringUtil.isBlank(fp.getString(StudySubjectValidator.INPUT_DOB))) {
							subject.setDateOfBirth(fp.getDate(StudySubjectValidator.INPUT_DOB));
							subject.setDobCollected(true);
						} else {
							subject.setDateOfBirth(null);
							subject.setDobCollected(false);
						}

					} else if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
						// generate a fake birthday in 01/01/YYYY format, only
						// the year is
						// valid
						// added the "2" to make sure that 'not used' is kept to
						// null, tbh 102007
						subject.setDobCollected(false);
						int intYob = fp.getInt(StudySubjectValidator.INPUT_YOB);
						Date fakeDate = new Date("01/01/" + intYob);

						String dobString = localDf.format(fakeDate);

						try {
							Date fakeDOB = localDf.parse(dobString);
							subject.setDateOfBirth(fakeDOB);
						} catch (ParseException pe) {
							subject.setDateOfBirth(new Date());
							addPageMessage(getResPage().getString("problem_happened_saving_year"), request);
						}

					}
					subject.setStatus(Status.AVAILABLE);
					subject.setOwner(ub);

					if (insertWithParents) {
						subject.setFatherId(fp.getInt(INPUT_FATHER));
						subject.setMotherId(fp.getInt(INPUT_MOTHER));
						subject = sdao.create(subject);
					} else {
						subject = sdao.create(subject);
					}

					if (!subject.isActive()) {
						throw new OpenClinicaException(getResException().getString("could_not_create_subject"), "3");
					}
					// YW << for showExistingRecord && existingSubShown,
					// If input value(s) is(are) different from database,
					// warning will be shown.
					// If value(s) in database is(are) empty, entered value(s)
					// could be used;
					// Otherwise, value(s) in database will be used.
					// For date-of-birth, if database only has year-of-birth,
					// the year in database will be used for year part
				} else if (existingSubShown) {
					if (!needUpdate) {
						subject = subjectWithSameId;
					} else {
						updateSubject.setUpdater(ub);
						updateSubject = (SubjectBean) sdao.update(updateSubject);

						if (!updateSubject.isActive()) {
							throw new OpenClinicaException("Could not create subject.", "5");
						}
						subject = updateSubject;
					}
				}
				// enroll the subject in the active study
				studySubject.setSubjectId(subject.getId());
				studySubject.setStudyId(currentStudy.getId());
				studySubject.setLabel(label);
				studySubject.setSecondaryLabel(fp.getString(INPUT_SECONDARY_LABEL));
				studySubject.setStatus(Status.AVAILABLE);

				if (!StringUtil.isBlank(fp.getString(INPUT_ENROLLMENT_DATE))) {
					studySubject.setEnrollmentDate(fp.getDateInputWithServerTimeOfDay(INPUT_ENROLLMENT_DATE));
				} else if (currentStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired().equals("no")) {
					studySubject.setEnrollmentDate(new Date());
				}

				studySubject.setOwner(ub);

				if (!"".equals(fp.getString("dynamicGroupClassId"))) {
					studySubject.setDynamicGroupClassId(Integer.valueOf(fp.getString("dynamicGroupClassId")));
				} else {
					studySubject.setDynamicGroupClassId(0);
				}

				// Shaoyu Su: prevent same label ("Study Subject ID")
				synchronized (simpleLockObj) {
					if (label.equalsIgnoreCase(getResWord().getString("id_generated_Save_Add"))) {
						int nextLabel = ssd.findTheGreatestLabel() + 1;
						studySubject.setLabel(nextLabel + "");
						studySubject = ssd.createWithoutGroup(studySubject);
						if (showExistingRecord && !existingSubShown) {
							fp.addPresetValue(INPUT_LABEL, label);
						}
					} else {
						studySubject = ssd.createWithoutGroup(studySubject);
					}
				}
				if (!classes.isEmpty() && studySubject.isActive()) {
					SubjectGroupMapDAO sgmdao = getSubjectGroupMapDAO();
					for (StudyGroupClassBean group : classes) {

						group.getStudyGroupId();
						group.getGroupNotes();
						SubjectGroupMapBean map = new SubjectGroupMapBean();
						map.setNotes(group.getGroupNotes());
						map.setStatus(Status.AVAILABLE);
						map.setStudyGroupId(group.getStudyGroupId());
						map.setStudySubjectId(studySubject.getId());
						map.setStudyGroupClassId(group.getId());
						map.setOwner(ub);
						if (map.getStudyGroupId() > 0) {
							sgmdao.create(map);
						}
					}
				}

				if (!studySubject.isActive()) {
					throw new OpenClinicaException(getResException().getString("could_not_create_study_subject"), "4");
				}

				// save discrepancy notes into DB
				DiscrepancyNoteService dnService = new DiscrepancyNoteService(getDataSource());
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession()
						.getAttribute(FORM_DISCREPANCY_NOTES_NAME);

				String[] subjectFields = { StudySubjectValidator.INPUT_DOB, StudySubjectValidator.INPUT_YOB, INPUT_GENDER };
				for (String element : subjectFields) {
					dnService.saveFieldNotes(element, fdn, subject.getId(), "subject", currentStudy);
				}
				dnService.saveFieldNotes(INPUT_ENROLLMENT_DATE, fdn, studySubject.getId(), "studySub", currentStudy);

				request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
				request.setAttribute(CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT, studySubject);
				request.setAttribute(CreateNewStudyEventServlet.INPUT_REQUEST_STUDY_SUBJECT, "no");
				request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

				addPageMessage(getResPage().getString("subject_with_unique_identifier") + studySubject.getLabel()
						+ getResPage().getString("X_was_created_succesfully"), request);

				String submitEvent = fp.getString(SUBMIT_EVENT_BUTTON);
				String submitEnroll = fp.getString(SUBMIT_ENROLL_BUTTON);
				fp.getString(SUBMIT_DONE_BUTTON);

				request.getSession().removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
				if (!StringUtil.isBlank(submitEvent)) {
					forwardPage(Page.CREATE_NEW_STUDY_EVENT_SERVLET, request, response);
				} else if (!StringUtil.isBlank(submitEnroll)) {
					// NEW MANTIS ISSUE 4770
					setUpBeans(classes, request);
					request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);
					fp.addPresetValue(INPUT_ENROLLMENT_DATE, DateUtil.printDate(new Date(),
							getUserAccountBean().getUserTimeZoneId(), DateUtil.DatePattern.DATE, getLocale()));

					String idSetting;
					idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();

					logger.info("subject id setting :" + idSetting);
					// set up auto study subject id
					if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
						String nextLabel = ssd.findNextLabel(currentStudy, studyToSearchOn);
						fp.addPresetValue(INPUT_LABEL, nextLabel);
					}

					setPresetValues(fp.getPresetValues(), request);
					request.getSession().setAttribute(FORM_DISCREPANCY_NOTES_NAME, new FormDiscrepancyNotes());
					forwardPage(Page.ADD_NEW_SUBJECT, request, response);
				} else {
					if (fp.getString(OPEN_FIRST_CRF).equalsIgnoreCase(TRUE)) {
						try {
							StudyEventDAO sedao = getStudyEventDAO();
							StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();

							StudyBean studyWithEventDefinitions = currentStudy;
							if (currentStudy.getParentStudyId() > 0) {
								studyWithEventDefinitions = new StudyBean();
								studyWithEventDefinitions.setId(currentStudy.getParentStudyId());
							}

							List<StudyEventDefinitionBean> eventDefinitions = selectNotStartedOrRepeatingSortedEventDefs(
									studySubject, studyWithEventDefinitions.getId(), seddao, sgcdao, sedao);

							if (studySubject.getEventStartDate() == null) {
								studySubject.setEventStartDate(new Date());
							}
							StudyEventBean seb = createStudyEvent(fp, getMaskingService()
									.returnFirstNotMaskedEvent(eventDefinitions, ub.getId(), currentStudy.getId())
									.getId(), studySubject);

							if (seb != null && seb.getId() > 0) {
								response.sendRedirect(
										request.getContextPath() + Page.ENTER_DATA_FOR_STUDY_EVENT_SERVLET.getFileName()
												+ "?eventId=" + seb.getId() + "&openFirstCrf=true");
								return;
							}
						} catch (Exception e) {
							logger.error(
									"An error has occured during processing the IDE for first crf of first study event in the study subject.",
									e);
						}
					}
					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET, request, response);
				}
			}
		}
	}

	protected StudyEventBean createStudyEvent(FormProcessor fp, Integer sedId, StudySubjectBean s) {
		StudyEventBean se = null;
		UserAccountBean ub = getUserAccountBean(fp.getRequest());
		int studyEventDefinitionId = sedId == null ? fp.getInt("studyEventDefinition") : sedId;
		String location = fp.getString("location");
		Date startDate = s.getEventStartDate();
		if (studyEventDefinitionId > 0) {
			String locationTerm = getResWord().getString("location");
			// don't allow user to use the default value 'Location' since
			// location
			// is a required field
			if (location.equalsIgnoreCase(locationTerm)) {
				addPageMessage(getResText().getString("not_a_valid_location"), fp.getRequest());
			} else {
				logger.info("will create event with new subject");
				StudyEventDAO sedao = getStudyEventDAO();
				StudyEventDefinitionDAO seddao = getStudyEventDefinitionDAO();
				se = new StudyEventBean();
				se.setLocation(location);
				se.setDateStarted(startDate);
				se.setDateEnded(startDate);
				se.setOwner(ub);
				se.setStudyEventDefinitionId(studyEventDefinitionId);
				se.setStatus(Status.AVAILABLE);
				se.setStudySubjectId(s.getId());
				se.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
				se.setSampleOrdinal(sedao.getMaxSampleOrdinal(sed, s) + 1);
				sedao.create(se);
			}

		} else {
			addPageMessage(getResPage().getString("no_event_sheduled_for_subject"), fp.getRequest());
		}
		return se;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.core.SpringServlet#mayProceed()
	 */
	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		String exceptionName = getResException().getString("no_permission_to_add_new_subject");
		String noAccessMessage = getResPage().getString("may_not_add_new_subject") + " "
				+ getResPage().getString("change_study_contact_sysadmin");

		if (maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage, request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
	}

	protected void setUpBeans(List<StudyGroupClassBean> classes, HttpServletRequest request) throws Exception {
		StudyGroupDAO sgdao = getStudyGroupDAO();

		SubjectDAO sdao = getSubjectDAO();
		ArrayList fathers = sdao.findAllByGender('m');
		ArrayList mothers = sdao.findAllByGender('f');

		ArrayList dsFathers = new ArrayList();
		ArrayList dsMothers = new ArrayList();

		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudyDAO stdao = getStudyDAO();

		displaySubjects(dsFathers, fathers, ssdao, stdao);
		displaySubjects(dsMothers, mothers, ssdao, stdao);

		request.setAttribute(BEAN_FATHERS, dsFathers);
		request.setAttribute(BEAN_MOTHERS, dsMothers);

		for (StudyGroupClassBean group : classes) {
			group.setStudyGroups(sgdao.findAllByGroupClass(group));
		}

		request.setAttribute(BEAN_GROUPS, classes);
	}

	/**
	 * Find study subject id for each subject, and construct displaySubjectBean.
	 *
	 * @param displayArray
	 *            ArrayList
	 * @param subjects
	 *            ArrayList
	 * @param ssdao
	 *            StudySubjectDAO
	 * @param stdao
	 *            StudyDAO
	 */
	public static void displaySubjects(ArrayList displayArray, ArrayList subjects, StudySubjectDAO ssdao,
			StudyDAO stdao) {

		for (Object subject1 : subjects) {
			SubjectBean subject = (SubjectBean) subject1;
			ArrayList studySubs = ssdao.findAllBySubjectId(subject.getId());
			String protocolSubjectIds = "";
			for (int j = 0; j < studySubs.size(); j++) {
				StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
				int studyId = studySub.getStudyId();
				StudyBean stu = (StudyBean) stdao.findByPK(studyId);
				String protocolId = stu.getIdentifier();
				if (j == studySubs.size() - 1) {
					protocolSubjectIds = protocolId + "-" + studySub.getLabel();
				} else {
					protocolSubjectIds = protocolId + "-" + studySub.getLabel() + ", ";
				}
			}
			DisplaySubjectBean dsb = new DisplaySubjectBean();
			dsb.setSubject(subject);
			dsb.setStudySubjectIds(protocolSubjectIds);
			displayArray.add(dsb);
		}
	}
}
