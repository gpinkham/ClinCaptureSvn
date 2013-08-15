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

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.crfdata.*;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.quartz.JobDataMap;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;

@SuppressWarnings({ "rawtypes" })
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
	
	public SimpleTriggerImpl generateTrigger(FormProcessor fp, UserAccountBean userAccount, StudyBean study,
			String locale) {
		Date startDateTime = fp.getDateTime(DATE_START_JOB);
		// check the above?
		int datasetId = fp.getInt(DATASET_ID);
		String period = fp.getString(PERIOD);
		String email = fp.getString(EMAIL);
		String jobName = fp.getString(JOB_NAME);
		String jobDesc = fp.getString(JOB_DESC);
		String spss = fp.getString(SPSS);
		String tab = fp.getString(TAB);
		String cdisc = fp.getString(CDISC);
		String cdisc12 = fp.getString(ExampleSpringJob.CDISC12);
		String cdisc13 = fp.getString(ExampleSpringJob.CDISC13);
		String cdisc13oc = fp.getString(ExampleSpringJob.CDISC13OC);
		BigInteger interval = new BigInteger("0");
		if ("monthly".equalsIgnoreCase(period)) {
			interval = new BigInteger("2419200000"); // how many
			// milliseconds in
			// a month? should
			// be 24192000000
		} else if ("weekly".equalsIgnoreCase(period)) {
			interval = new BigInteger("604800000"); // how many
			// milliseconds in
			// a week? should
			// be 6048000000
		} else { // daily
			interval = new BigInteger("86400000");// how many
			// milliseconds in a
			// day?
		}
		// set up and commit job here

		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setJobName(jobName);
		trigger.setJobGroup("DEFAULT");
		trigger.setRepeatCount(64000);
		trigger.setRepeatInterval(interval.longValue());
		// set the job detail name,
		// based on our choice of format above
		// what if there is more than one detail?
		// what is the number of times it should repeat?
		// arbitrary large number, 64K should be enough :)

		trigger.setDescription(jobDesc);
		// set just the start date
		trigger.setStartTime(startDateTime);
		trigger.setName(jobName);// + datasetId);
		trigger.setGroup("DEFAULT");// + datasetId);
		trigger.setMisfireInstruction(SimpleTriggerImpl.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
		// set job data map
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(DATASET_ID, datasetId);
		jobDataMap.put(PERIOD, period);
		jobDataMap.put(EMAIL, email);
		jobDataMap.put(TAB, tab);
		jobDataMap.put(CDISC, cdisc);
		jobDataMap.put(ExampleSpringJob.CDISC12, cdisc12);
		jobDataMap.put(ExampleSpringJob.LOCALE, locale);
		// System.out.println("found 1.2: " +
		// jobDataMap.get(ExampleSpringJob.CDISC12));
		jobDataMap.put(ExampleSpringJob.CDISC13, cdisc13);
		// System.out.println("found 1.3: " +
		// jobDataMap.get(ExampleSpringJob.CDISC13));
		jobDataMap.put(ExampleSpringJob.CDISC13OC, cdisc13oc);
		// System.out.println("found 1.3oc: " +
		// jobDataMap.get(ExampleSpringJob.CDISC13OC));
		jobDataMap.put(SPSS, spss);
		jobDataMap.put(USER_ID, userAccount.getId());
		// StudyDAO studyDAO = new StudyDAO();
		jobDataMap.put(STUDY_ID, study.getId());
		jobDataMap.put(STUDY_NAME, study.getName());
		jobDataMap.put(STUDY_OID, study.getOid());

		trigger.setJobDataMap(jobDataMap);
		// trigger.setRepeatInterval(interval.longValue());
		// System.out.println("default for volatile: " + trigger.isVolatile());
		// trigger.setVolatility(false);
		return trigger;
	}

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
		System.out.println("just set directory: " + directory);
		jobDataMap.put(ExampleSpringJob.LOCALE, locale);
		jobDataMap.put("hours", hours);
		jobDataMap.put("minutes", minutes);

		trigger.setJobDataMap(jobDataMap);
		// trigger.setVolatility(false);
		return trigger;
	}

	public HashMap validateForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys,
			String properName) {
		Validator v = new Validator(request);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		// need to be unique too
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		v.addValidation(PERIOD, Validator.NO_BLANKS);
		v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);
		// v.addValidation(DATE_START_JOB + "Date", new Date(),
		// Validator.DATE_IS_AFTER_OR_EQUAL);
		// TODO job names will have to be unique, tbh

		String tab = fp.getString(TAB);
		String cdisc = fp.getString(CDISC);
		String cdisc12 = fp.getString(ExampleSpringJob.CDISC12);
		String cdisc13 = fp.getString(ExampleSpringJob.CDISC13);
		String cdisc13oc = fp.getString(ExampleSpringJob.CDISC13OC);
		String spss = fp.getString(SPSS);
		Date jobDate = fp.getDateTime(DATE_START_JOB);
		HashMap errors = v.validate();
		if ((tab == "") && (cdisc == "") && (spss == "") && (cdisc12 == "") && (cdisc13 == "") && (cdisc13oc == "")) {
			// throw an error here, at least one should work
			// errors.put(TAB, "Error Message - Pick one of the below");
			Validator.addError(errors, TAB, "Please pick at least one of the below.");
		}
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(fp.getString(JOB_NAME)) && (!triggerKey.getName().equals(properName))) {
				Validator.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
			}
		}
		if (jobDate.before(new Date())) {
			Validator.addError(errors, DATE_START_JOB + "Date", "This date needs to be later than the present time.");
		}
		return errors;
	}

	public String generateSummaryStatsMessage(SummaryStatsBean ssBean, ResourceBundle respage) {
		// TODO i18n
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\'0\' cellpadding=\'0\' cellspacing=\'0\' width=\'100%\'>");
		sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'>Summary Statistics:</td> </tr> <tr valign=\'top\'>");
		sb.append("<td class=\'table_cell_left\'>Subjects Affected: " + ssBean.getStudySubjectCount() + "</td> </tr>");
		sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'>Event CRFs Affected: " + ssBean.getEventCrfCount()
				+ "</td> </tr> ");
		sb.append("<tr valign=\'top\'><td class=\'table_cell_left\'>Validation Rules Generated: "
				+ ssBean.getDiscNoteCount() + "</td> </tr> </table>");
		/*
		 * <table border="0" cellpadding="0" cellspacing="0" width="100%">
		 * 
		 * <tr valign="top"> <td class="table_header_row">Summary Statistics:</td> </tr> <tr valign="top"> <td
		 * class="table_cell_left">Subjects Affected: <c:out value="${summaryStats.studySubjectCount}" /></td> </tr> <tr
		 * valign="top"> <td class="table_cell_left">Event CRFs Affected: <c:out value="${summaryStats.eventCrfCount}"
		 * /></td> </tr> <tr valign="top"> <td class="table_cell_left">Validation Rules Generated: <c:out
		 * value="${summaryStats.discNoteCount}" /></td> </tr>
		 * 
		 * </table>
		 */

		return sb.toString();
	}

	public String generateHardValidationErrorMessage(ArrayList<SubjectDataBean> subjectData,
			HashMap<String, String> hardValidationErrors, boolean isValid, boolean hasSkippedItems,
			ResourceBundle respage) {
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
			sb.append("<tr valign=\'top\'> <td class=\'table_header_row\' colspan=\'" + subjectOidSpan + "\'>Study Subject: "
					+ subjectDataBean.getSubjectOID() + "</td> </tr>");
			// next step here
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'>Event CRF OID</td> <td class=\'table_header_row\' colspan=\'" + eventCrfOidSpan + "\'></td>");
				sb.append("</tr> <tr valign=\'top\'> <td class=\'table_cell_left\'>");
				sb.append(studyEventDataBean.getStudyEventOID());
				if (studyEventDataBean.getStudyEventRepeatKey() != null) {
					studyEventRepeatKey = studyEventDataBean.getStudyEventRepeatKey();
					sb.append(" (Repeat key " + studyEventDataBean.getStudyEventRepeatKey() + ")");
				} else {
					// reset
					studyEventRepeatKey = "1";
				}
				sb.append("</td> <td class=\'table_cell\' colspan=\'" + studyEventOidSpan + "\'></td> </tr>");
				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
				for (FormDataBean formDataBean : formDataBeans) {
					sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'></td> ");
					sb.append("<td class=\'table_header_row\'>CRF Version OID</td> <td class=\'table_header_row\' colspan=\'" + crfVersionOidSpan + "\'></td></tr>");
					sb.append("<tr valign=\'top\'> <td class=\'table_cell_left\'></td> <td class=\'table_cell\'>");
					sb.append(formDataBean.getFormOID());
					sb.append("</td> <td class=\'table_cell\' colspan=\'" + formOidSpan + "\'></td> </tr>");
					ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
					for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
						sb.append("<tr valign=\'top\'> <td class=\'table_header_row\'></td>");
						sb.append("<td class=\'table_header_row\'></td> <td class=\'table_header_row\' colspan=\'" + itemGroupOidSpan + "\'>");
						sb.append(itemGroupDataBean.getItemGroupOID());
						if (itemGroupDataBean.getItemGroupRepeatKey() != null) {
							groupRepeatKey = itemGroupDataBean.getItemGroupRepeatKey();
							sb.append(" (Repeat key " + itemGroupDataBean.getItemGroupRepeatKey() + ")");
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
									/*
									 * <tr valign="top"> <td class="table_cell_left"></td> <td class="table_cell"></td>
									 * <td class="table_cell"><font color="red"><c:out
									 * value="${itemData.itemOID}"/></font></td> <td class="table_cell"> <c:out
									 * value="${itemData.value}"/><br/> <c:out value="${hardValidationErrors[oidKey]}"/>
									 * </td> </tr>
									 */
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
										sb.append("<td class='table_cell'>"
												+ (itemDataBean.isSkip() ? respage.getString("was_skipped") : "")
												+ "</td>");
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
			HashMap<String, String> totalValidationErrors, boolean hasSkippedItems, ResourceBundle respage) {
		return generateHardValidationErrorMessage(subjectData, totalValidationErrors, true, hasSkippedItems, respage);
	}

	public HashMap validateImportJobForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys,
			String properName) {
		Validator v = new Validator(request);
		v.addValidation(JOB_NAME, Validator.NO_BLANKS);
		// need to be unique too
		v.addValidation(JOB_DESC, Validator.NO_BLANKS);
		if (!"".equals(fp.getString(EMAIL))) {
			v.addValidation(EMAIL, Validator.IS_A_EMAIL);
		}
		// << tbh we are now allowing email to be optional
		// v.addValidation(PERIOD, Validator.NO_BLANKS);
		// v.addValidation(DIRECTORY, Validator.NO_BLANKS);
		// v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);

		// TODO job names will have to be unique, tbh

		String hours = fp.getString("hours");
		String minutes = fp.getString("minutes");

		HashMap errors = v.validate();
		Locale locale = request.getLocale();
		ResourceBundleProvider.updateLocale(locale);
		resexception = ResourceBundleProvider.getExceptionsBundle(locale);
		Matcher matcher = Pattern.compile("[^\\w_\\d ]").matcher(fp.getString(JOB_NAME));
		boolean isContainSpecialSymbol = matcher.find();
		if (isContainSpecialSymbol) {
			Validator.addError(errors, JOB_NAME, resexception
					.getString("dataset_should_not_contain_any_special"));
		}
		int studyId = fp.getInt(STUDY_ID);
		if (!(studyId > 0)) {
			Validator.addError(errors, STUDY_ID, "The study should be selected.");
		}
		if ((hours.equals("0")) && (minutes.equals("0"))) {
			System.out.println("got in the ERROR LOOP");
			// throw an error here, at least one should be greater than zero
			// errors.put(TAB, "Error Message - Pick one of the below");
			Validator.addError(errors, "hours", "At least one of the following should be greater than zero.");
		}
		for (TriggerKey triggerKey : triggerKeys) {
			if (triggerKey.getName().equals(fp.getString(JOB_NAME)) && (!triggerKey.getName().equals(properName))) {
				Validator.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
			}
		}
		return errors;
	}

	public HashMap validateImportJobForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys) {
		return validateImportJobForm(fp, request, triggerKeys, "");
	}

	public HashMap validateForm(FormProcessor fp, HttpServletRequest request, Set<TriggerKey> triggerKeys) {
		return validateForm(fp, request, triggerKeys, "");
	}

	public HashMap validateImportForm(HttpServletRequest request) {
		Validator v = new Validator(request);

		HashMap errors = v.validate();

		return errors;
	}
}
