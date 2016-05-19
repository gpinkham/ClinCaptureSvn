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

package org.akaza.openclinica.web.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.quartz.JobDataMap;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.clinovo.util.ValidatorHelper;

@SuppressWarnings({"rawtypes"})
public class TriggerService {

	public TriggerService() {
		// do nothing, for the moment
	}

	public static final String PERIOD = "periodToRun";
	public static final String TAB = "tab";
	public static final String CDISC = "cdisc";
	public static final String SPSS = "spss";
	public static final String DATASET_ID = "dsId";
	public static final String DATE_START_JOB = "job";
	public static final String EMAIL = "contactEmail";
	public static final String JOB_NAME = "jobName";
	public static final String JOB_DESC = "jobDesc";
	public static final String USER_ID = "user_id";
	public static final String STUDY_NAME = "study_name";
	public static final String STUDY_OID = "study_oid";
	public static final String DIRECTORY = "filePathDir";
	public static final String STUDY_ID = "studyId";

	private static String IMPORT_TRIGGER = "importTrigger";
	public static ResourceBundle resexception;

	public SimpleTriggerImpl generateImportTrigger(FormProcessor fp, UserAccountBean userAccount, StudyBean study,
			String locale, Date startTime) {
		Date startDateTime = startTime != null ? startTime : new Date(System.currentTimeMillis());
		return generateImportTrigger(fp, userAccount, study, startDateTime, locale);
	}

	public SimpleTriggerImpl generateImportTrigger(FormProcessor fp, UserAccountBean userAccount, StudyBean study,
			Date startDateTime, String locale) {

		String jobName = fp.getString(JOB_NAME);

		String email = fp.getString(EMAIL);
		String jobDesc = fp.getString(JOB_DESC);
		String directory = fp.getString(DIRECTORY);

		// what kinds of periods do we have? hourly, daily, weekly?
		long interval = 0;
		int hours = fp.getInt("hours");
		int minutes = fp.getInt("minutes");
		if (hours > 0) {
			long hoursInt = hours * 3600000;
			interval = interval + hoursInt;
		}
		if (minutes > 0) {
			long minutesInt = minutes * 60000;
			interval = interval + minutesInt;
		}
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setJobName(jobName);
		trigger.setJobGroup(IMPORT_TRIGGER);
		trigger.setRepeatCount(64000);
		trigger.setRepeatInterval(interval);
		trigger.setDescription(jobDesc);
		// set just the start date
		trigger.setStartTime(startDateTime);
		trigger.setName(jobName);// + datasetId);
		trigger.setGroup(IMPORT_TRIGGER);// + datasetId);
		trigger.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
		// set job data map
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put(EMAIL, email);
		jobDataMap.put(USER_ID, userAccount.getId());
		jobDataMap.put(STUDY_NAME, study.getName());
		jobDataMap.put(STUDY_OID, study.getOid());
		jobDataMap.put(DIRECTORY, directory);
		jobDataMap.put(ExampleSpringJob.LOCALE, locale);
		jobDataMap.put("hours", hours);
		jobDataMap.put("minutes", minutes);

		trigger.setJobDataMap(jobDataMap);
		// trigger.setVolatility(false);
		return trigger;
	}

	public String generateSummaryStatsMessage(SummaryStatsBean ssBean, ResourceBundle respage, ResourceBundle reword) {

		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\' width=\'100%\'>");
		sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'>" + respage.getString("summary_statistics")
				+ ":</td> </tr> <tr valign=\'top\'>");
		sb.append("<td class=\'table_cell_left\'>" + respage.getString("subjects_affected") + ": "
				+ ssBean.getStudySubjectCount() + "</td> </tr>");
		sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'>" + respage.getString("event_crfs_affected")
				+ ": " + ssBean.getEventCrfCount() + "</td> </tr> ");
		sb.append("<tr valign=\'top\'><td class=\'table_cell_left\'>" + respage.getString("validation_rules_generated")
				+ ": " + ssBean.getDiscNoteCount() + "</td> </tr> </table>");

		if (ssBean.getUnavailableCRFVersionOIDs().size() > 0) {
			sb.append("<br/>");
			sb.append("<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\' width=\'100%\'>");
			sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'>"
					+ reword.getString("import_contains_unavailable_crf_versions") + "</td> </tr>");
			sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'>"
					+ reword.getString("unavailable_crf_version_oids") + ":</td> </tr>");
			for (String crfVersionOID: ssBean.getUnavailableCRFVersionOIDs()) {
				sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'>" + crfVersionOID + "</td> </tr>");
			}
			sb.append("</table>");
		}
		return sb.toString();
	}

	public String generateHardValidationErrorMessage(ArrayList<SubjectDataBean> subjectData,
			HashMap<String, String> hardValidationErrors, boolean isValid, boolean hasSkippedItems,
			ResourceBundle respage, ResourceBundle resword, SummaryStatsBean summaryStats) {
		StringBuffer sb = new StringBuffer();
		String studyEventRepeatKey = "1";
		String groupRepeatKey = "1";

		int subjectOidSpan = hasSkippedItems ? 5 : 4;
		int eventCrfOidSpan = hasSkippedItems ? 4 : 3;
		int studyEventOidSpan = hasSkippedItems ? 4 : 3;
		int crfVersionOidSpan = hasSkippedItems ? 3 : 2;
		int formOidSpan = hasSkippedItems ? 3 : 2;
		int itemGroupOidSpan = hasSkippedItems ? 3 : 2;

		sb.append("<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\' width=\'100%\'>");
		for (SubjectDataBean subjectDataBean : subjectData) {
			sb.append("<tr valign=\'top\'> <td class=\'table_header_row\' colspan=\'" + subjectOidSpan
					+ "\'>Study Subject: " + subjectDataBean.getSubjectOID() + "</td> </tr>");
			// next step here
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'>" + respage.getString("event_crf_oid")
						+ "</td> <td class=\'table_header_row\' colspan=\'" + eventCrfOidSpan + "\'></td>");
				sb.append("</tr> <tr valign=\'top\'> <td class=\'table_cell_left\'>");
				sb.append(studyEventDataBean.getStudyEventOID());
				if (studyEventDataBean.getStudyEventRepeatKey() != null) {
					studyEventRepeatKey = studyEventDataBean.getStudyEventRepeatKey();
					sb.append(" (").append(respage.getString("repeat_key"))
							.append(studyEventDataBean.getStudyEventRepeatKey()).append(")");
				} else {
					// reset
					studyEventRepeatKey = "1";
				}
				sb.append("</td> <td class=\'table_cell\' colspan=\'" + studyEventOidSpan + "\'></td> </tr>");
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
				for (FormDataBean formDataBean : formDataBeans) {
					sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'></td> ");
					sb.append("<td class=\'table_header_row\'>" + respage.getString("crf_version_oid")
							+ "</td> <td class=\'table_header_row\' colspan=\'" + crfVersionOidSpan + "\'></td></tr>");
					sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'></td> <td class=\'table_cell\'>");
					sb.append(formDataBean.getFormOID());
					sb.append("</td> <td class=\'table_cell\' colspan=\'" + formOidSpan + "\'></td> </tr>");
					ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
					for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
						sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'></td>");
						sb.append("<td class=\'table_header_row\'></td> <td class=\'table_header_row\' colspan=\'"
								+ itemGroupOidSpan + "\'>");
						sb.append(itemGroupDataBean.getItemGroupOID());
						if (itemGroupDataBean.getItemGroupRepeatKey() != null) {
							groupRepeatKey = itemGroupDataBean.getItemGroupRepeatKey();
							sb.append(" (").append(respage.getString("repeat_key"))
									.append(" " + itemGroupDataBean.getItemGroupRepeatKey()).append(")");
						} else {
							groupRepeatKey = "1";
						}
						sb.append("</td></tr>");
						ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
						for (ImportItemDataBean itemDataBean : itemDataBeans) {
							String oidKey = itemDataBean.getItemOID() + "_" + studyEventRepeatKey + "_"
									+ groupRepeatKey + "_" + subjectDataBean.getSubjectOID();
							if (!isValid) {
								if (hardValidationErrors.containsKey(oidKey)) {
									sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'></td>");
									sb.append("<td class=\'table_cell\'></td> <td class=\'table_cell\'><font color=\'red\'>");
									sb.append(itemDataBean.getItemOID());
									sb.append("</font></td> <td class=" + "\'table_cell\'>");
									sb.append(itemDataBean.getValue() + "<br/>");
									sb.append(hardValidationErrors.get(oidKey));
									sb.append("</td></tr>");
								}
							} else {
								if (!hardValidationErrors.containsKey(oidKey)) {
									sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'></td>");
									sb.append("<td class=\'table_cell\'></td> <td class=\'table_cell\'>");
									sb.append(itemDataBean.getItemOID());
									sb.append("</td> <td class=" + "\'table_cell\'>");
									sb.append(itemDataBean.getValue());
									sb.append("</td>");
									if (hasSkippedItems) {
										String skipMsg = "";
										if (itemDataBean.isSkip()) {
											skipMsg = respage.getString("was_skipped");
										}
										if (summaryStats.getUnavailableCRFVersionOIDs().contains(formDataBean.getFormOID())) {
											skipMsg = respage.getString("was_skipped").concat("<br/>")
													.concat(resword.getString("crf_version_was_unavailable"));
										}
										sb.append("<td class='table_cell'>" + skipMsg + "</td>");
									}
									sb.append("</tr>");
								}
							}
						}
					}
				}
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public String generateValidMessage(ArrayList<SubjectDataBean> subjectData,
			HashMap<String, String> totalValidationErrors, boolean hasSkippedItems, ResourceBundle respage,
			ResourceBundle resword, SummaryStatsBean summaryStats) {
		return generateHardValidationErrorMessage(subjectData, totalValidationErrors, true, hasSkippedItems, respage,
				resword, summaryStats);
	}

	public HashMap validateImportJobForm(FormProcessor fp, ValidatorHelper validatorHelper,
			Set<TriggerKey> triggerKeys, String properName) {
		Validator v = new Validator(validatorHelper);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		if (!"".equals(fp.getString(EMAIL))) {
			v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		}

		String hours = fp.getString("hours");
		String minutes = fp.getString("minutes");

		HashMap errors = v.validate();
		Locale locale = validatorHelper.getLocale();
		ResourceBundleProvider.updateLocale(locale);
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		Matcher matcher = Pattern.compile("[^\\w_\\d ]").matcher(fp.getString(JOB_NAME));
		boolean isContainSpecialSymbol = matcher.find();
		if (isContainSpecialSymbol) {
			Validator.addError(errors, JOB_NAME, resexception.getString("dataset_should_not_contain_any_special"));
		}
		int studyId = fp.getInt(STUDY_ID);
		if (!(studyId > 0)) {
			Validator.addError(errors, STUDY_ID, resexception.getString("the_study_should_be_selected"));
		}
		if ((hours.equals("0")) && (minutes.equals("0"))) {
			// throw an error here, at least one should be greater than zero
			// errors.put(TAB, "Error Message - Pick one of the below");
			Validator.addError(errors, "hours", resexception.getString("at_least_one_of_the_following"));
		}
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(fp.getString(JOB_NAME)) && (!triggerKey.getName().equals(properName))) {
				Validator.addError(errors, JOB_NAME,
						resexception.getString("a_job_with_that_name_already_exist_please_pick"));
			}
		}
		return errors;
	}

	public HashMap validateImportJobForm(FormProcessor fp, ValidatorHelper validatorHelper, Set<TriggerKey> triggerKeys) {
		return validateImportJobForm(fp, validatorHelper, triggerKeys, "");
	}
}
