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

import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SummaryStatsBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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

	public String generateSummaryStatsMessage(SummaryStatsBean ssBean, ResourceBundle respage,
			HashMap<String, String> validationMsgs) {
		// TODO i18n
		StringBuffer sb = new StringBuffer();
		sb.append("");
		sb.append("Summary Statistics: ");
		sb.append("Subjects Affected: " + ssBean.getStudySubjectCount() + ", ");
		sb.append("Event CRFs Affected: " + ssBean.getEventCrfCount() + ", ");
		sb.append("# of Warnings: " + validationMsgs.size() + ", ");
		sb.append("# of Discrepancy Notes: " + ssBean.getDiscNoteCount() + ". ");

		return sb.toString();
	}

	public String generateHardValidationErrorMessage(ArrayList<SubjectDataBean> subjectData,
			HashMap<String, String> hardValidationErrors, String groupRepeatKey) {
		StringBuffer sb = new StringBuffer();
		String studyEventRepeatKey = null;
		sb.append("");
		for (SubjectDataBean subjectDataBean : subjectData) {
			ArrayList<StudyEventDataBean> studyEventDataBeans = subjectDataBean.getStudyEventData();
			for (StudyEventDataBean studyEventDataBean : studyEventDataBeans) {
				studyEventRepeatKey = studyEventDataBean.getStudyEventRepeatKey();

				ArrayList<FormDataBean> formDataBeans = studyEventDataBean.getFormData();
				for (FormDataBean formDataBean : formDataBeans) {
					ArrayList<ImportItemGroupDataBean> itemGroupDataBeans = formDataBean.getItemGroupData();
					for (ImportItemGroupDataBean itemGroupDataBean : itemGroupDataBeans) {
						ArrayList<ImportItemDataBean> itemDataBeans = itemGroupDataBean.getItemData();
						for (ImportItemDataBean itemDataBean : itemDataBeans) {

							String oidKey = itemDataBean.getItemOID() + "_" + studyEventRepeatKey + "_"
									+ groupRepeatKey + "_" + subjectDataBean.getSubjectOID();
							if (hardValidationErrors.containsKey(oidKey)) {
								sb.append(itemDataBean.getItemOID());
								sb.append(": ");
								sb.append(itemDataBean.getValue() + " -- ");
								sb.append(hardValidationErrors.get(oidKey));
								sb.append("");
							}

						}
					}
				}
			}
		}

		sb.append("");
		return sb.toString();
	}

}
