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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 * Created on Jul 7, 2005
 */
package org.akaza.openclinica.bean.extract;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ApplicationConstants;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class ExtractBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	public static final int SAS_FORMAT = 1;

	public static final int SPSS_FORMAT = 2;

	public static final int CSV_FORMAT = 3;

	public static final int PDF_FORMAT = 4;

	public static final int XLS_FORMAT = 5;

	public static final int TXT_FORMAT = 6;

	public static final String UNGROUPED = "Ungrouped";

	java.text.SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.getDateFormatInExtract());

	java.text.SimpleDateFormat long_sdf = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle().getString(
			"date_time_format_string"));

	private int format = 1;

	private String showUniqueId = "1";

	private StudyBean parentStudy;

	private StudyBean study;

	private DatasetBean dataset;

	private final DataSource ds;

	private Date dateCreated;

	// an array of StudyEventDefinitionBean objects
	private ArrayList studyEvents;

	private HashMap eventData;

	// an array of subjects and study_subject
	private final ArrayList subjects;
	private ArrayList hBASE_EVENTSIDE;
	private ArrayList hBASE_ITEMGROUPSIDE;
	private ArrayList aBASE_ITEMDATAID;

	private HashMap groupNames;

	// a hashmap of group names to use for generating the keys and column names
	/**
	 * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION - remove subjectsAdded - it is not used - add InKeysHelper - a
	 *      HashMap that will speed up the data extract display
	 */
	private HashMap hmInKeys;


	// keys are studySubjectId-studyEventDefinitionId-sampleOrdinal-crfId-ItemID
	// strings
	// values are the corresponding values in the item_data table
	private HashMap data;

	// keys are studyEventDefinitionId Integer
	// values are the maximum sample ordinal for that sed
	private final HashMap maxOrdinals;

	// keys are itemId Integer
	// values are Boolean.TRUE
	// if an item has its id in the keySet for this HashMap,
	// that means the user has chosen to display this item in the report
	private final HashMap selectedItems;

	private final HashMap selectedSEDs;

	private final HashMap selectedSEDCRFs;

	private HashMap<String, String> eventDescriptions;// for spss only

	private ArrayList<String> eventHeaders; // for displaying dataset in HTML
	// view,event

	// header

	private ArrayList<Object> itemNames;// for displaying dataset in HTML
	// view,item header

	private ArrayList rowValues; // for displaying dataset in html view

	private HashMap studyGroupMap;

	private HashMap studyGroupMaps;

	// to contain all the studysubject ids and link them to another hashmap, the
	// study group map above, tbh
	private ArrayList studyGroupClasses; // for displaying groups for

	private StudySubjectBean currentSubject;

	private int subjIndex = -1;

	private CRFBean currentCRF;

	private int maxItemDataBeanOrdinal = 0;

	private StudyEventDefinitionBean currentDef;

	private int sedIndex = -1;

	private ItemBean currentItem;

	public ExtractBean(DataSource ds) {
		this.ds = ds;
		study = new StudyBean();
		parentStudy = new StudyBean();
		studyEvents = new ArrayList();

		data = new HashMap();
		maxOrdinals = new HashMap();
		subjects = new ArrayList();
		selectedItems = new HashMap();
		selectedSEDs = new HashMap();
		groupNames = new HashMap();
		selectedSEDCRFs = new HashMap();
		itemNames = new ArrayList<Object>();
		rowValues = new ArrayList();
		eventHeaders = new ArrayList<String>();
		eventDescriptions = new HashMap<String, String>();

		hmInKeys = new HashMap();
		hBASE_EVENTSIDE = new ArrayList();
		hBASE_ITEMGROUPSIDE = new ArrayList();
		aBASE_ITEMDATAID = new ArrayList();
	}

	public ExtractBean(DataSource ds, SimpleDateFormat sdf, SimpleDateFormat long_sdf) {
		this.sdf = sdf;
		this.long_sdf = long_sdf;
		// TODO need to refactor the below
		this.ds = ds;
		study = new StudyBean();
		parentStudy = new StudyBean();
		studyEvents = new ArrayList();

		data = new HashMap();
		maxOrdinals = new HashMap();
		subjects = new ArrayList();
		selectedItems = new HashMap();
		selectedSEDs = new HashMap();
		groupNames = new HashMap();
		selectedSEDCRFs = new HashMap();
		itemNames = new ArrayList<Object>();
		rowValues = new ArrayList();
		eventHeaders = new ArrayList<String>();
		eventDescriptions = new HashMap<String, String>();

		hmInKeys = new HashMap();
		hBASE_EVENTSIDE = new ArrayList();
		hBASE_ITEMGROUPSIDE = new ArrayList();
		aBASE_ITEMDATAID = new ArrayList();

	}

	/**
	 * @return Returns the eventDescriptions.
	 */
	public HashMap getEventDescriptions() {
		return eventDescriptions;
	}

	/**
	 * @param eventDescriptions
	 *            The eventDescriptions to set.
	 */
	public void setEventDescriptions(HashMap<String, String> eventDescriptions) {
		this.eventDescriptions = eventDescriptions;
	}

	//
	// TODO place to add additional metadata, tbh
	//
	public void computeReportMetadata(ReportBean answer) {
		// ///////////////////
		// //
		// HEADER //
		// //
		// ///////////////////
		answer.nextCell("Database Export Header Metadata");
		answer.nextRow();

		answer.nextCell("Dataset Name");
		answer.nextCell(dataset.getName());
		answer.nextRow();

		answer.nextCell("Date");

		answer.nextCell(sdf.format(new Date(System.currentTimeMillis())));
		answer.nextRow();

		answer.nextCell("Protocol ID");
		answer.nextCell(getParentProtocolId());
		answer.nextRow();

		answer.nextCell("Study Name");
		answer.nextCell(getParentStudyName());
		answer.nextRow();

		String siteName = getSiteName();
		if (!siteName.equals("")) {
			answer.nextCell("Site Name");
			answer.nextCell(siteName);
			answer.nextRow();
		}

		answer.nextCell("Subjects");
		answer.nextCell(Integer.toString(getNumSubjects()));
		answer.nextRow();

		int numSEDs = getNumSEDs();
		answer.nextCell("Study Event Definitions");
		answer.nextCell(String.valueOf(numSEDs));
		answer.nextRow();

		for (int i = 1; i <= numSEDs; i++) {
			String repeating = getSEDIsRepeating(i) ? " (Repeating) " : "";
			answer.nextCell("Study Event Definition " + i + repeating);
			answer.nextCell(getSEDName(i));
			answer.nextRow();

			int numSEDCRFs = getSEDNumCRFs(i);
			for (int j = 1; j <= numSEDCRFs; j++) {
				answer.nextCell("CRF ");
				answer.nextCell(getSEDCRFName(i, j));
				answer.nextCell(getSEDCRFCode(i, j));
				answer.nextRow();
			}
		}
	}

	public void computeReportData(ReportBean answer) {
		answer.nextCell("Subject Event Item Values (Item-CRF-Ordinal)");
		answer.nextRow();

		answer.nextCell("SubjID");
		answer.nextCell("ProtocolID");

		// subject column labels
		// general order: subject info first, then group info, then event info,
		// then CRF info
		if (dataset.isShowSubjectDob()) {
			if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
				answer.nextCell("YOB");
			} else if (study.getStudyParameterConfig().getCollectDob().equals("1")) {
				answer.nextCell("DOB");
			}
		}
		if (dataset.isShowSubjectGender()) {
			answer.nextCell("Gender");
		}
		// TODO add additional labels here
		if (dataset.isShowSubjectStatus()) {
			answer.nextCell("SubjectStatus");
			eventDescriptions.put("SubjectStatus", "Subject Status");
		}

		if (dataset.isShowSubjectUniqueIdentifier() && "1".equals(showUniqueId)) {
			answer.nextCell("UniqueID");
			eventDescriptions.put("UniqueID", "Unique ID");
		}

		if (dataset.isShowSubjectSecondaryId()) {
			answer.nextCell("SecondaryID");
			eventDescriptions.put("SecondaryID", "SecondaryID");
		}

		if (dataset.isShowSubjectGroupInformation()) {
			for (int y = 0; y < studyGroupClasses.size(); y++) {
				StudyGroupClassBean studyGroupClassBean = (StudyGroupClassBean) studyGroupClasses.get(y);
				answer.nextCell(studyGroupClassBean.getName());
				eventDescriptions.put(studyGroupClassBean.getName(), studyGroupClassBean.getName());
			}
		}

		int numSEDs = getNumSEDs();
		for (int i = 1; i <= numSEDs; i++) {
			int numSamples = getSEDNumSamples(i);

			for (int j = 1; j <= numSamples; j++) {

				if (dataset.isShowEventLocation()) {
					String location = getColumnLabel(i, j, "Location", numSamples);
					String description = getColumnDescription(i, j, "Location For ", currentDef.getName(), numSamples);
					answer.nextCell(location);
					eventHeaders.add(location);
					eventDescriptions.put(location, description);
				}
				if (dataset.isShowEventStart()) {
					String start = getColumnLabel(i, j, "StartDate", numSamples);
					String description = getColumnDescription(i, j, "Start Date For ", currentDef.getName(), numSamples);
					answer.nextCell(start);
					eventHeaders.add(start);
					eventDescriptions.put(start, description);

				}
				if (dataset.isShowEventEnd()) {
					String end = getColumnLabel(i, j, "EndDate", numSamples);
					String description = getColumnDescription(i, j, "End Date For ", currentDef.getName(), numSamples);
					answer.nextCell(end);
					eventHeaders.add(end);
					eventDescriptions.put(end, description);
				}
				if (dataset.isShowEventStatus()) {
					String eventStatus = getColumnLabel(i, j, "SubjectEventStatus", numSamples);
					String description = getColumnDescription(i, j, "Event Status For ", currentDef.getName(),
							numSamples);
					answer.nextCell(eventStatus);
					eventHeaders.add(eventStatus);
					eventDescriptions.put(eventStatus, description);
				}
				if (dataset.isShowSubjectAgeAtEvent()
						&& ("1".equals(study.getStudyParameterConfig().getCollectDob()) || "2".equals(study
								.getStudyParameterConfig().getCollectDob()))) {
					String subjectAgeAtEvent = getColumnLabel(i, j, "AgeAtEvent", numSamples);
					String description = getColumnDescription(i, j, "Age At Event for ", currentDef.getName(),
							numSamples);
					answer.nextCell(subjectAgeAtEvent);
					eventHeaders.add(subjectAgeAtEvent);
					eventDescriptions.put(subjectAgeAtEvent, description);
				}
			}
		}

		for (int i = 1; i <= numSEDs; i++) {
			int numSamples = getSEDNumSamples(i);
			for (int j = 1; j <= numSamples; j++) {
				if (dataset.isShowCRFcompletionDate()) {
					String crfCompletionDate = getColumnLabel(i, j, "CompletionDate", numSamples);
					String description = getColumnDescription(i, j, "Completion Date for ", currentDef.getName(),
							numSamples);// FIXME
					answer.nextCell(crfCompletionDate);
					eventHeaders.add(crfCompletionDate);
					eventDescriptions.put(crfCompletionDate, description);
				}

				if (dataset.isShowCRFinterviewerDate()) {
					String interviewerDate = getColumnLabel(i, j, "InterviewDate", numSamples);
					String description = getColumnDescription(i, j, "Interviewed Date for ", currentDef.getName(),
							numSamples);// FIXME
					answer.nextCell(interviewerDate);
					eventHeaders.add(interviewerDate);
					eventDescriptions.put(interviewerDate, description);
				}

				if (dataset.isShowCRFinterviewerName()) {
					String interviewerName = getColumnLabel(i, j, "InterviewerName", numSamples);
					String description = getColumnDescription(i, j, "Interviewer Name for ", currentDef.getName(),
							numSamples);// FIXME
					answer.nextCell(interviewerName);
					eventHeaders.add(interviewerName);
					eventDescriptions.put(interviewerName, description);
				}

				if (dataset.isShowCRFstatus()) {
					String crfStatus = getColumnLabel(i, j, "CRFVersionStatus", numSamples);// numSamples
					String description = getColumnDescription(i, j, "Event CRF Status for ", currentDef.getName(),
							numSamples);// FIXME
					answer.nextCell(crfStatus);
					eventHeaders.add(crfStatus);
					eventDescriptions.put(crfStatus, description);
				}

				if (dataset.isShowCRFversion()) {
					String crfCompletionDate = getColumnLabel(i, j, "VersionName", numSamples);
					String description = getColumnDescription(i, j, "CRF Version Name for ", currentDef.getName(),
							numSamples);// FIXME
					answer.nextCell(crfCompletionDate);
					eventHeaders.add(crfCompletionDate);
					eventDescriptions.put(crfCompletionDate, description);
				}
			}
		}

		for (int i = 1; i <= numSEDs; i++) {
			int numSamples = getSEDNumSamples(i);

			for (int j = 1; j <= numSamples; j++) {
				int numSEDCRFs = getSEDNumCRFs(i);
				for (int k = 1; k <= numSEDCRFs; k++) {

					int numItems = getNumItems(i, k);
					for (int l = 1; l <= numItems; l++) {
						for (Iterator iter = groupNames.entrySet().iterator(); iter.hasNext();) {
							java.util.Map.Entry groupEntry = (java.util.Map.Entry) iter.next();
							String groupName = (String) groupEntry.getKey();

							logger.info("*** Found a row in groupNames: key " + groupName);
							if (inKeys(i, j, k, l, groupName)) {
								Integer groupCount = (Integer) groupEntry.getValue();
								for (int m = 1; m <= groupCount.intValue(); m++) {
									answer.nextCell(getColumnItemLabel(i, j, k, l, numSamples, m, groupName));
									DisplayItemHeaderBean dih = new DisplayItemHeaderBean();
									dih.setItemHeaderName(getColumnItemLabel(i, j, k, l, numSamples, m, groupName));
									dih.setItem(currentItem);
									itemNames.add(dih);
								}
							}
						}
					}
				}
			}
		}

		answer.nextRow();

		for (int h = 1; h <= getNumSubjects(); h++) {
			DisplayItemDataBean didb = new DisplayItemDataBean();
			String label = getSubjectStudyLabel(h);
			answer.nextCell(label);
			didb.setSubjectName(label);

			String protocolId = getParentProtocolId();
			answer.nextCell(protocolId);
			didb.setStudyLabel(protocolId);

			if (dataset.isShowSubjectDob()) {
				if (study.getStudyParameterConfig().getCollectDob().equals("2")) {
					String yob = getSubjectYearOfBirth(h);
					answer.nextCell(yob);
					didb.setSubjectDob(yob);
				} else if (study.getStudyParameterConfig().getCollectDob().equals("1")) {
					String dob = getSubjectDateOfBirth(h);
					answer.nextCell(dob);
					didb.setSubjectDob(dob);
				}
			}
			if (dataset.isShowSubjectGender()) {
				String gender = getSubjectGender(h);
				answer.nextCell(gender);
				didb.setSubjectGender(gender);
			}

			// TODO column headers above, column values here, tbh
			if (dataset.isShowSubjectStatus()) {
				String status = getSubjectStatusName(h);
				answer.nextCell(status);
				didb.setSubjectStatus(status);
			}
			if (dataset.isShowSubjectUniqueIdentifier() && "1".equals(showUniqueId)) {
				String uniqueName = getSubjectUniqueIdentifier(h);
				answer.nextCell(uniqueName);
				didb.setSubjectUniqueId(uniqueName);
			}

			if (dataset.isShowSubjectSecondaryId()) {
				String secondaryId = getSubjectSecondaryId(h);
				answer.nextCell(secondaryId);
				didb.setSubjectSecondaryId(secondaryId);
			}
			if (dataset.isShowSubjectGroupInformation()) {
				ArrayList studyGroupList = new ArrayList();
				studyGroupList = getStudyGroupMap(h);// studyGroupMap =
				// getStudyGroupMap(h);
				// logger.info("+++ picture of study group classes:
				// "+studyGroupClasses.toString());
				// logger.info("+++ picture of study group list:
				// "+studyGroupList);
				// logger.info("+++ picture of study group map:
				// "+studyGroupMap.toString());
				for (int y = 0; y < studyGroupClasses.size(); y++) {
					StudyGroupClassBean sgcBean = (StudyGroupClassBean) studyGroupClasses.get(y);
					// if the subject is in the group...
					// logger.info("iterating through keys:
					// "+sgcBean.getId());
					Iterator iter = studyGroupList.iterator();
					/*
					 * case 0 - no groups assigned - should just have a blank here
					 */
					if (!iter.hasNext()) {
						answer.nextCell("");

						didb.setGroupName(Integer.valueOf(sgcBean.getId()), "");
					}
					/*
					 * case 1 - one or more groups assigned - runs through the maps and assigns them in rows
					 */
					while (iter.hasNext()) {
						studyGroupMap = (HashMap) iter.next();

						// logger.info("+++ picture of study group map:
						// "+studyGroupMap.toString());

						if (studyGroupMap.containsKey(Integer.valueOf(sgcBean.getId()))) {
							StudyGroupBean groupBean = (StudyGroupBean) studyGroupMap.get(Integer.valueOf(sgcBean
									.getId()));
							// logger.info("found a group name in a group
							// class: "+groupBean.getName());

							answer.nextCell(groupBean.getName());

							didb.setGroupName(Integer.valueOf(sgcBean.getId()), groupBean.getName());

							break;
							// didb.setGroupName(groupBean.getName());
							// otherwise we don't enter anything...
						} else {
							answer.nextCell("");

							didb.setGroupName(Integer.valueOf(sgcBean.getId()), "");
						}// end if
					}// end while
				}// end for
			}// end if

			// sed column values
			for (int i = 1; i <= numSEDs; i++) {
				int numSamples = getSEDNumSamples(i);

				// add event-specific attributes here, tbh
				for (int j = 1; j <= numSamples; j++) {
					if (dataset.isShowEventLocation()) {
						String location = getEventLocation(h, i, j);
						answer.nextCell(location);
						didb.getEventValues().add(location);

					}
					if (dataset.isShowEventStart()) {
						String start = getEventStart(h, i, j);
						answer.nextCell(start);
						didb.getEventValues().add(start);
					}
					if (dataset.isShowEventEnd()) {
						String end = getEventEnd(h, i, j);
						answer.nextCell(end);
						didb.getEventValues().add(end);
					}
					if (dataset.isShowEventStatus()) {
						String status = getEventStatus(h, i, j);
						answer.nextCell(status);
						didb.getEventValues().add(status);
					}
					if (dataset.isShowSubjectAgeAtEvent()
							&& ("1".equals(study.getStudyParameterConfig().getCollectDob()) || "2".equals(study
									.getStudyParameterConfig().getCollectDob()))) {
						String ageAtEvent = currentSubject.getDateOfBirth() != null ? getAgeAtEvent(h, i, j) : "";
						answer.nextCell(ageAtEvent);
						didb.getEventValues().add(ageAtEvent);
					}
				}
			}

			// item-crf-ordinal column labels
			for (int i = 1; i <= numSEDs; i++) {

				int numSamples = getSEDNumSamples(i);

				for (int j = 1; j <= numSamples; j++) {

					if (dataset.isShowCRFcompletionDate()) {
						String completionDate = getCRFCompletionDate(h, i, j);
						answer.nextCell(completionDate);
						didb.getEventValues().add(completionDate);
					}

					if (dataset.isShowCRFinterviewerDate()) {
						String interviewerDate = getCRFInterviewerDate(h, i, j);
						answer.nextCell(interviewerDate);
						didb.getEventValues().add(interviewerDate);
					}

					if (dataset.isShowCRFinterviewerName()) {
						String interviewerName = getCRFInterviewerName(h, i, j);
						answer.nextCell(interviewerName);
						didb.getEventValues().add(interviewerName);

					}

					if (dataset.isShowCRFstatus()) {

						String crfStatus = getSEDCRFStatus(h, i, j);
						answer.nextCell(crfStatus);
						didb.getEventValues().add(crfStatus);
					}

					if (dataset.isShowCRFversion()) {
						String crfVersion = getSEDCRFVersionName(h, i, j);
						answer.nextCell(crfVersion);
						didb.getEventValues().add(crfVersion);

					}
				}
			}

			for (int i = 1; i <= numSEDs; i++) {
				int numSamples = getSEDNumSamples(i);
				for (int j = 1; j <= numSamples; j++) {
					int numSEDCRFs = getSEDNumCRFs(i);
					for (int k = 1; k <= numSEDCRFs; k++) {

						int numItems = getNumItems(i, k);
						for (int l = 1; l <= numItems; l++) {
							for (java.util.Iterator iter = groupNames.entrySet().iterator(); iter.hasNext();) {
								java.util.Map.Entry groupEntry = (java.util.Map.Entry) iter.next();
								String groupName = (String) groupEntry.getKey();
								if (inKeys(i, j, k, l, groupName)) {
									Integer groupCount = (Integer) groupEntry.getValue();
									for (int m = 1; m <= groupCount.intValue(); m++) {
										String data = getDataByIndex(h, i, j, k, l, m, groupName);
										answer.nextCell(data);
										didb.getItemValues().add(data);
									}
								}
							}
						}
					}
				}
			}
			rowValues.add(didb);
			answer.nextRow();
		}
	}

	public void computeReport(ReportBean answer) {
		computeReportMetadata(answer);
		answer.closeMetadata();
		computeReportData(answer);
	}

	private HashMap displayed = new HashMap();

	// keys are Strings returned by getColumnKeys, values are ArrayLists of
	// ItemBean objects in order of their display in the SED/CRF
	private HashMap sedCrfColumns = new HashMap();

	private HashMap sedCrfItemFormMetadataBeans = new HashMap();

	/**
	 * Implements the Column algorithm in "Dataset Export Algorithms" Must be called after DatasetDAO.getDatasetData();
	 */
	public void getMetadata() {
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		CRFDAO cdao = new CRFDAO(ds);
		CRFVersionDAO cvdao = new CRFVersionDAO(ds);
		ItemDAO idao = new ItemDAO(ds);
		ItemFormMetadataDAO ifmDAO = new ItemFormMetadataDAO(this.ds);
		StudyGroupDAO studygroupDAO = new StudyGroupDAO(ds);
		StudyGroupClassDAO studygroupclassDAO = new StudyGroupClassDAO(ds);
		studyGroupClasses = new ArrayList();
		studyGroupMap = new HashMap();
		studyGroupMaps = new HashMap<Integer, ArrayList>();
		sedCrfColumns = new HashMap();
		displayed = new HashMap();
		sedCrfItemFormMetadataBeans = new HashMap();

		studyEvents = seddao.findAllByStudy(study);
		ArrayList finalStudyEvents = new ArrayList();
		// set up group classes first, tbh
		// this bit of code throws an error b/c we try to access
		// currentSubject...

		if (dataset.isShowSubjectGroupInformation()) {
			studyGroupMaps = studygroupDAO.findSubjectGroupMaps(study.getId());

			logger.info("found subject group ids: " + dataset.getSubjectGroupIds().toString());
			
			for (int h = 0; h < dataset.getSubjectGroupIds().size(); h++) {
				Integer groupId = (Integer) dataset.getSubjectGroupIds().get(h);

				StudyGroupClassBean sgclass = (StudyGroupClassBean) studygroupclassDAO.findByPK(groupId.intValue());
				
				logger.info("found a studygroupclass bean: " + sgclass.getName());
				studyGroupClasses.add(sgclass);
			}
		}
		for (int i = 0; i < studyEvents.size(); i++) {
			StudyEventDefinitionBean sed = (StudyEventDefinitionBean) studyEvents.get(i);

			if (!selectedSED(sed)) {
				continue;
			}
			ArrayList CRFs = (ArrayList) cdao.findAllActiveByDefinition(sed);
			ArrayList CRFsDisplayedInThisSED = new ArrayList();

			for (int j = 0; j < CRFs.size(); j++) {
				CRFBean cb = (CRFBean) CRFs.get(j);

				if (!selectedSEDCRF(sed, cb)) {
					continue;
				} else {

					CRFsDisplayedInThisSED.add(cb);

					ArrayList CRFVersions = cvdao.findAllByCRFId(cb.getId());
					for (int k = 0; k < CRFVersions.size(); k++) {
						CRFVersionBean cvb = (CRFVersionBean) CRFVersions.get(k);

						ArrayList Items = idao.findAllItemsByVersionId(cvb.getId());
						// sort by ordinal/name
						Collections.sort(Items);
						for (int l = 0; l < Items.size(); l++) {
							ItemBean ib = (ItemBean) Items.get(l);
							if (selected(ib) && !getDisplayed(sed, cb, ib)) {
								
								ItemFormMetadataBean ifmb = ifmDAO.findByItemIdAndCRFVersionId(ib.getId(), cvb.getId());
								addColumn(sed, cb, ib);
								addItemFormMetadataBeans(sed, cb, ifmb);
								markDisplayed(sed, cb, ib);
							}
						}
					}
				}
			}

			sed.setCrfs(CRFsDisplayedInThisSED);
			finalStudyEvents.add(sed); // make the setCrfs call "stick"
		}
		this.studyEvents = finalStudyEvents;
	}

	protected boolean selected(ItemBean ib) {
		return selectedItems.containsKey(Integer.valueOf(ib.getId()));
	}

	protected boolean selectedSEDCRF(StudyEventDefinitionBean sed, CRFBean cb) {
		return selectedSEDCRFs.containsKey(sed.getId() + "_" + cb.getId());
	}

	protected boolean selectedSED(StudyEventDefinitionBean sed) {
		return selectedSEDs.containsKey(Integer.valueOf(sed.getId()));
	}

	private void markDisplayed(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
		displayed.put(getDisplayedKey(sed, cb, ib), Boolean.TRUE);
	}

	private boolean getDisplayed(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
		return displayed.containsKey(getDisplayedKey(sed, cb, ib));
	}

	private void addColumn(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
		String key = getColumnsKey(sed, cb);
		ArrayList columns = (ArrayList) sedCrfColumns.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		columns.add(ib);
		sedCrfColumns.put(key, columns);
	}

	public ArrayList getColumns(StudyEventDefinitionBean sed, CRFBean cb) {
		String key = getColumnsKey(sed, cb);
		ArrayList columns = (ArrayList) sedCrfColumns.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		return columns;
	}

	private void addItemFormMetadataBeans(StudyEventDefinitionBean sed, CRFBean cb, ItemFormMetadataBean ifmb) {
		String key = sed.getId() + "_" + cb.getId();
		ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		columns.add(ifmb);
		sedCrfItemFormMetadataBeans.put(key, columns);
	}

	public ArrayList getItemFormMetadataBeans(StudyEventDefinitionBean sed, CRFBean cb) {
		String key = sed.getId() + "_" + cb.getId();
		ArrayList columns = (ArrayList) sedCrfItemFormMetadataBeans.get(key);

		if (columns == null) {
			columns = new ArrayList();
		}

		return columns;
	}

	/**
	 * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION replaced the old one with a new function
	 */
	public void addStudySubjectData(ArrayList objs) {
		for (int i = 0; i < objs.size(); i++) {
			StudySubjectBean sub = new StudySubjectBean();
			sub = (StudySubjectBean) objs.get(i);
			subjects.add(sub);
		}
	}

	/**
	 * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION Combines the two HashMaps into eventData entries - the data is
	 *      already filtered for null values
	 */
	public void addStudyEventData() {

		// initialize
		eventData = new HashMap();

		for (int ik = 0; ik < aBASE_ITEMDATAID.size(); ik++) {
			// get the item_group side
			extractDataset_ITEMGROUPSIDE objgrp = (extractDataset_ITEMGROUPSIDE) hBASE_ITEMGROUPSIDE.get(ik);
			extractDataset_EVENTSIDE objev = (extractDataset_EVENTSIDE) hBASE_EVENTSIDE.get(ik);

			// sanity check - assume both are not null
			Integer itemdataid = (Integer) aBASE_ITEMDATAID.get(ik);
			Integer itemdataid_objgrp = objgrp.itemDataId;
			Integer itemdataid_objev = objev.itemDataId;
			if (itemdataid_objgrp.intValue() == itemdataid.intValue()
					&& itemdataid_objev.intValue() == itemdataid.intValue()) {
				// OK - add entries to the dataEvent

				// initialize
				StudyEventBean event = new StudyEventBean();
				EventCRFBean eventCRF = new EventCRFBean();

				//
				event.setName(objev.studyEventDefinitionName); // studyEventDefinitionName
				event.setDateStarted(objev.studyEventDateStart); // studyEventStart
				event.setDateEnded(objev.studyEventDateEnd); // studyEventEnd
				event.setLocation(objev.studyEventLoacation); // studyEventLocation
				event.setSampleOrdinal(objev.sampleOrdinal.intValue()); // sampleOrdinal
				event.setStudyEventDefinitionId(objev.studyEvenetDefinitionId.intValue());
				
				event.setStudySubjectId(objev.studySubjectId.intValue()); // studySubjectId
				
				event.setStartTimeFlag(objev.studyEventStartTimeFlag.booleanValue()); // se
				
				event.setEndTimeFlag(objev.studyEventEndTimeFlag.booleanValue()); // se
				event.setStatus(Status.get(objev.studyEventStatusId.intValue())); // se
				event.setSubjectEventStatus(SubjectEventStatus.get(objev.studyEventSubjectEventStatusId.intValue())); // se
				event.setId(objev.studyEventId.intValue()); 
				eventCRF.setCompletionStatusId(objgrp.eventCrfCompletionStatusId);// completionStatusId
				eventCRF.setInterviewerName(objgrp.interviewerName); // interviewerName
				eventCRF.setDateCompleted(objgrp.eventCrfDateCompleted); // dateCompleted
				eventCRF.setDateValidateCompleted(objgrp.eventCrfDateValidateCompleted); // dateValidateCompleted
				eventCRF.setStatus(Status.get(objgrp.eventCrfStatusId));
				eventCRF.setDateInterviewed(objgrp.dateInterviewed); // dateInterviewedv

				CRFVersionBean crfVersion = new CRFVersionBean();
				crfVersion.setName(objgrp.crfVersionName); // crfVersionName
				crfVersion.setStatus(Status.get(objgrp.crfVersionStatusId.intValue())); // crfVersionStatusId
				crfVersion.setStatusId(objgrp.crfVersionStatusId.intValue()); // crfVersionStatusId

				eventCRF.setCrfVersion(crfVersion);

				ArrayList events = new ArrayList();
				events.add(eventCRF);
				event.setEventCRFs(events);
				
				String key = getStudyEventDataKey(
				objev.studySubjectId.intValue(),
				objev.studyEvenetDefinitionId.intValue(),
				objev.sampleOrdinal.intValue());

				if (eventData == null) {
					eventData = new HashMap();
				}
				StudyEventBean checkEvent = (StudyEventBean) eventData.get(key);

				if (checkEvent == null) {
					eventData.put(key, event);
				} 

			} 
		}
	}

	public void addStudyEventDataOld(Integer studySubjectId, String studyEventDefinitionName,
			Integer studyEventDefinitionId, Integer sampleOrdinal, String studyEventLocation, Date studyEventStart,
			Date studyEventEnd, String crfVersionName, Integer crfVersionStatusId, Date dateInterviewed,
			String interviewerName, Date dateCompleted, Date dateValidateCompleted, Integer completionStatusId) {

		if (studySubjectId == null || studyEventDefinitionId == null || sampleOrdinal == null
				|| studyEventLocation == null || studyEventStart == null) {
			return;
		}

		if (studyEventDefinitionId.intValue() <= 0 || studySubjectId.intValue() <= 0 || sampleOrdinal.intValue() <= 0) {
			return;
		}
		StudyEventDAO sedao = new StudyEventDAO(ds);
		StudyEventBean se = (StudyEventBean) sedao.findByStudySubjectIdAndDefinitionIdAndOrdinal(studySubjectId,
				studyEventDefinitionId, sampleOrdinal);
		
		StudyEventBean event = new StudyEventBean();
		EventCRFBean eventCRF = new EventCRFBean();

		event.setName(studyEventDefinitionName);
		event.setDateStarted(studyEventStart);
		event.setDateEnded(studyEventEnd);
		event.setLocation(studyEventLocation);
		event.setSampleOrdinal(sampleOrdinal.intValue());
		event.setStudyEventDefinitionId(studyEventDefinitionId.intValue());
		event.setStudySubjectId(studySubjectId.intValue());
		event.setStartTimeFlag(se.getStartTimeFlag());
		event.setEndTimeFlag(se.getEndTimeFlag());
		event.setStatus(se.getStatus());
		event.setSubjectEventStatus(se.getSubjectEventStatus());

		event.setStage(se.getStage());
		logger.info("found stage: " + se.getStage().getName());
		event.setId(se.getId());
		eventCRF.setCompletionStatusId(completionStatusId.intValue());//
		eventCRF.setInterviewerName(interviewerName);
		eventCRF.setDateCompleted(dateCompleted);
		eventCRF.setDateValidateCompleted(dateValidateCompleted);
		eventCRF.setDateInterviewed(dateInterviewed);

		CRFVersionBean crfVersion = new CRFVersionBean();
		crfVersion.setName(crfVersionName);
		crfVersion.setStatus(Status.get(crfVersionStatusId.intValue()));
		crfVersion.setStatusId(crfVersionStatusId.intValue());

		eventCRF.setCrfVersion(crfVersion);
		ArrayList events = new ArrayList();
		events.add(eventCRF);
		event.setEventCRFs(events);
		
		String key = getStudyEventDataKey(studySubjectId.intValue(), studyEventDefinitionId.intValue(),
				sampleOrdinal.intValue());
		if (eventData == null) {
			eventData = new HashMap();
		}
		StudyEventBean checkEvent = (StudyEventBean) eventData.get(key);

		if (checkEvent == null) {
			eventData.put(key, event);
			logger.info("###just CREATED key: " + key + " event: " + event.getName() + " int.name: "
					+ eventCRF.getInterviewerName());
		} 
	}

	/**
	 * debug: takes in a event crf bean and spits out all its data. tbh
	 */
	public void debug(StudyEventBean seb) {
		java.lang.StringBuffer buf = new java.lang.StringBuffer();
		buf.append("***** ***** *****\n");

		buf.append("event crf count: " + seb.getEventCRFs().size() + " ");
		buf.append("study event bean location: " + seb.getLocation() + " ");
		buf.append("study event def id: " + seb.getStudyEventDefinitionId() + " ");
		buf.append("study event date started: " + seb.getDateStarted() + " ");
		buf.append("study event date ended: " + seb.getDateEnded() + " ");
		buf.append("study event status: " + seb.getStatus().getName() + " ");
		buf.append("***** ***** *****\n");
		logger.info(buf.toString());
		for (int i = 0; i < seb.getEventCRFs().size(); i++) {
			EventCRFBean check = (EventCRFBean) seb.getEventCRFs().get(i);

			debug(check);
		}
	}

	public void debug(EventCRFBean checkEvent) {
		java.lang.StringBuffer buf = new java.lang.StringBuffer();
		buf.append("****************\n");
		buf.append("debug of event crf bean: id " + checkEvent.getId() + " ");
		buf.append("crf int name: " + checkEvent.getInterviewerName() + " ");
		buf.append("crf version id: " + checkEvent.getCrfVersion().getId() + " ");
		buf.append("crf version name: " + checkEvent.getCrfVersion().getName() + " ");
		buf.append("interview date: " + checkEvent.getCreatedDate() + " ");
		buf.append("status: " + checkEvent.getStatus().getName() + " ");
		buf.append("crf version status: " + checkEvent.getCrfVersion().getStatus().getName() + " ");
		buf.append("completion status id: " + checkEvent.getCompletionStatusId() + " ");
		buf.append("data entry stage: " + checkEvent.getStage().getName() + " ");
		logger.info(buf.toString());
	}

	/*
	 * addGroupName -- check to see if this group name is in the system, if it is not, add it together with its ordinal
	 * If it is already in the system, look at the ordinals and find out which is bigger, then add the bigger of the two
	 * back into the data structure, tbh
	 */
	public void addGroupName(String name, Integer ordinal) {
		if (name == null) {
			return;
		}

		if (!groupNames.containsKey(name)) {
			groupNames.put(name, ordinal);
		} else {
			Integer numTimes = (Integer) groupNames.get(name);

			if (numTimes > ordinal) {
				groupNames.put(name, numTimes);
			} else {
				groupNames.put(name, ordinal);
			}
		}
	}

	/**
     *
     */
	public void addItemData() {
		// initialize
		data = new HashMap();

		for (int ik = 0; ik < aBASE_ITEMDATAID.size(); ik++) {
			// get the item_group side
			extractDataset_ITEMGROUPSIDE objgrp = (extractDataset_ITEMGROUPSIDE) hBASE_ITEMGROUPSIDE.get(ik);
			extractDataset_EVENTSIDE objev = (extractDataset_EVENTSIDE) hBASE_EVENTSIDE.get(ik);

			// sanity check - assume both are not null
			Integer itemdataid = (Integer) aBASE_ITEMDATAID.get(ik);
			Integer itemdataid_objgrp = objgrp.itemDataId;
			Integer itemdataid_objev = objev.itemDataId;
			if (itemdataid_objgrp.intValue() == itemdataid.intValue()
					&& itemdataid_objev.intValue() == itemdataid.intValue()) {

				if (!"".equals(objgrp.itemGroupName)) {
					
					String key = getDataKey(objev.studySubjectId.intValue(),
					objev.studyEvenetDefinitionId.intValue(),
					objev.sampleOrdinal.intValue(),
					objgrp.crfid.intValue(),
					objgrp.itemId.intValue(),
					objgrp.itemGroupRepeatNumber.intValue(),
					objgrp.itemGroupName);

					data.put(key, objgrp.itemValue);
					int maxOrdinal = getMaxOrdinal(objev.studyEvenetDefinitionId.intValue());
					if (maxOrdinal < objev.sampleOrdinal.intValue()) {
						setMaxOrdinal(objev.studyEvenetDefinitionId.intValue(), objev.sampleOrdinal.intValue());
					}

					selectedItems.put(objgrp.itemId, Boolean.TRUE);
					selectedSEDCRFs.put(objev.studyEvenetDefinitionId.intValue() + "_" + objgrp.crfid.intValue(), Boolean.TRUE);
					selectedSEDs.put(objev.studyEvenetDefinitionId, Boolean.TRUE);

					if (objgrp.itemGroupRepeatNumber.intValue() > getMaxItemDataBeanOrdinal()) {
						setMaxItemDataBeanOrdinal(objgrp.itemGroupRepeatNumber.intValue());
					}
				}
				addGroupName(objgrp.itemGroupName, objgrp.itemGroupRepeatNumber);
			} 
		}
	}

	/**
	 * Create a key out of a combination of variables, and then put the data in a hashmap with the
	 * key.
	 */
	public void addItemDataOld(Integer studySubjectId, Integer studyEventDefinitionId, Integer sampleOrdinal,
			Integer crfId, Integer itemId, String itemValue, Integer itemDataOrdinal, String groupName) {
		if (studyEventDefinitionId == null || studySubjectId == null || crfId == null || itemId == null
				|| sampleOrdinal == null || itemValue == null) {
			return;
		}

		if (studyEventDefinitionId.intValue() <= 0 || studySubjectId.intValue() <= 0 || crfId.intValue() <= 0
				|| itemId.intValue() <= 0 || sampleOrdinal.intValue() <= 0) {
			return;
		}

		logger.info("sample ordinal: " + sampleOrdinal.toString());
		logger.info("item data ordinal: " + itemDataOrdinal.toString());

		String key = getDataKey(studySubjectId.intValue(), studyEventDefinitionId.intValue(), sampleOrdinal.intValue(),
				crfId.intValue(), itemId.intValue(), itemDataOrdinal.intValue(), groupName);

		data.put(key, itemValue);
		logger.info("*** just put in data for " + key + " and value " + itemValue);
		int maxOrdinal = getMaxOrdinal(studyEventDefinitionId.intValue());
		if (maxOrdinal < sampleOrdinal.intValue()) {
			setMaxOrdinal(studyEventDefinitionId.intValue(), sampleOrdinal.intValue());
		}
		selectedItems.put(itemId, Boolean.TRUE);
		selectedSEDCRFs.put(studyEventDefinitionId.intValue() + "_" + crfId.intValue(), Boolean.TRUE);
		selectedSEDs.put(studyEventDefinitionId, Boolean.TRUE);

		return;
	}

	protected String getDataByIndex(int subjectInd, int sedInd, int sampleOrdinal, int crfInd, int itemInd,
			int itemOrdinal, String groupName) {
		syncSubjectIndex(subjectInd);
		syncItemIndex(sedInd, crfInd, itemInd);
		String key = getDataKey(currentSubject.getId(), currentDef.getId(), sampleOrdinal, currentCRF.getId(),
				currentItem.getId(), itemOrdinal, groupName);
		String itemValue = (String) data.get(key);

		if (itemValue == null) {
			itemValue = "";
		}

		return itemValue;
	}


	private Integer getMaxOrdinalsKey(int studySubjectId) {
		return Integer.valueOf(studySubjectId);
	}

	private int getMaxOrdinal(int studyEventDefinitionId) {
		Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
		try {
			if (maxOrdinals.containsKey(key)) {
				Integer maxOrdinal = (Integer) maxOrdinals.get(key);
				if (maxOrdinal != null) {
					return maxOrdinal.intValue();
				}
			}
		} catch (Exception e) {
		}

		return 0;
	}

	private void setMaxOrdinal(int studyEventDefinitionId, int sampleOrdinal) {
		Integer key = getMaxOrdinalsKey(studyEventDefinitionId);
		maxOrdinals.put(key, Integer.valueOf(sampleOrdinal));
	}

	public String getParentProtocolId() {
		if (!parentStudy.isActive()) {
			return study.getIdentifier();
		} else {
			return parentStudy.getIdentifier() + "_" + study.getIdentifier();
		}
	}

	public String getParentStudyName() {
		if (!parentStudy.isActive()) {
			return study.getName();
		} else {
			return parentStudy.getName();
		}
	}

	public String getParentStudySummary() {
		if (!parentStudy.isActive()) {
			return study.getSummary();
		} else {
			return parentStudy.getSummary();
		}
	}

	private String getSiteName() {
		if (parentStudy.isActive()) {
			return study.getName();
		} else {
			return "";
		}
	}

	public int getNumSubjects() {
		if (subjects != null) {
			return subjects.size();
		} else {
			return 0;
		}
	}

	protected int getNumSEDs() {
		return studyEvents.size();
	}

	public int getMaxItemDataBeanOrdinal() {
		return maxItemDataBeanOrdinal;
	}

	public void setMaxItemDataBeanOrdinal(int maxItemDataBeanOrdinal) {
		this.maxItemDataBeanOrdinal = maxItemDataBeanOrdinal;
	}

	private void syncSubjectIndex(int ind) {
		if (subjIndex != ind) {
			currentSubject = (StudySubjectBean) subjects.get(ind - 1);
			subjIndex = ind;
		}
	}

	private String getSubjectStudyLabel(int h) {
		syncSubjectIndex(h);

		return currentSubject.getLabel();
	}

	private String getSubjectDateOfBirth(int h) {
		syncSubjectIndex(h);
		Date dob = currentSubject.getDateOfBirth();
		return dob == null ? "" : sdf.format(dob);
	}

	private String getSubjectStatusName(int h) {
		syncSubjectIndex(h);
		Status status = currentSubject.getStatus();
		return status.getName();
	}

	private String getSubjectUniqueIdentifier(int h) {
		syncSubjectIndex(h);
		String uni = currentSubject.getSecondaryLabel();
		uni = currentSubject.getUniqueIdentifier();
		logger.info("+++ comparing " + uni + " vs. secondary label " + currentSubject.getSecondaryLabel());
		return uni;
	}

	private String getSubjectSecondaryId(int h) {
		syncSubjectIndex(h);
		return currentSubject.getSecondaryLabel();
	}

	private ArrayList getStudyGroupMap(int h) {
		syncSubjectIndex(h);
		Integer key = Integer.valueOf(currentSubject.getId());
		ArrayList value = (ArrayList) studyGroupMaps.get(key);
		return value != null ? value : new ArrayList();
	}

	private String getSubjectYearOfBirth(int h) {
		syncSubjectIndex(h);
		Date dob = currentSubject.getDateOfBirth();

		if (dob == null) {
			return "";
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);
		int year = cal.get(Calendar.YEAR);

		return year + "";
	}

	private String getSubjectGender(int h) {
		syncSubjectIndex(h);
		return String.valueOf(currentSubject.getGender());
	}

	private void syncSEDIndex(int ind) {
		if (sedIndex != ind) {
			currentDef = (StudyEventDefinitionBean) studyEvents.get(ind - 1);
			sedIndex = ind;
		}
	}

	private boolean getSEDIsRepeating(int ind) {
		syncSEDIndex(ind);
		return currentDef.isRepeating();
	}

	private String getSEDName(int ind) {
		syncSEDIndex(ind);
		return currentDef.getName();
	}

	protected int getSEDNumCRFs(int ind) {
		syncSEDIndex(ind);
		return currentDef.getCrfs().size();
	}

	protected String getCRFStatus(int h, int i, int j) {

		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return eventCRF != null ? eventCRF.getStatus().getName() : "";
	}

	protected String getCRFVersionName(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() == 1) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		} else {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(j - 1);
		}

		return eventCRF != null ? eventCRF.getCrfVersion().getName() : "";

	}

	protected String getCRFInterviewerDate(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return eventCRF != null && eventCRF.getDateInterviewed() != null ? sdf.format(eventCRF.getDateInterviewed())
				: "";

	}

	protected String getCRFInterviewerName(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return eventCRF != null ? eventCRF.getInterviewerName() : "";

	}

	protected String getCRFCompletionDate(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		return eventCRF.getDateValidateCompleted() == null ? sdf.format(eventCRF.getDateCompleted()) : sdf
				.format(eventCRF.getDateValidateCompleted());// need
		// to
		// be
		// fixed?

	}

	private String getSEDCRFName(int sedInd, int crfInd) {
		syncCRFIndex(sedInd, crfInd);
		return currentCRF.getName();
	}

	private String getSEDCRFVersionName(int h, int sedInd, int crfInd) {

		StudyEventBean seb = getEvent(h, sedInd, crfInd);

		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}

		String returnMe = "";
		if (seb.getEventCRFs().size() > 0) {
			logger.info("found getEventCRFs.size " + seb.getEventCRFs().size());
			for (int t = 0; t < seb.getEventCRFs().size(); t++) {
				eventCRF = (EventCRFBean) seb.getEventCRFs().get(t);
				returnMe = eventCRF.getCrfVersion().getName();
			}

		}
		logger.info("returning the following for crf version name: " + returnMe);
		return returnMe;
	}

	private void syncCRFIndex(int sedInd, int crfInd) {
		syncSEDIndex(sedInd);
		try {
			currentCRF = (CRFBean) currentDef.getCrfs().get(crfInd - 1);
		} catch (IndexOutOfBoundsException e) {
			logger.info("found exception");
			currentCRF = (CRFBean) currentDef.getCrfs().get(0);
		}

	}

	private String getSEDCRFStatus(int h, int sedInd, int crfInd) {// BADS Flag
		syncCRFIndex(sedInd, crfInd);

		StudyEventBean seb = getEvent(h, sedInd, crfInd);
		Status ecStatus = Status.AVAILABLE;
		EventCRFBean eventCRF = null;
		if (seb.getEventCRFs().size() > 0) {
			eventCRF = (EventCRFBean) seb.getEventCRFs().get(0);
		}
		String crfVersionStatus = "";
		SubjectEventStatus status = SubjectEventStatus.NOT_SCHEDULED;
		CRFVersionBean crfv = new CRFVersionBean();
		crfv.setStatus(Status.AVAILABLE);
		// modified stage so that crfVersionStatus could be the same as what it
		// shows in subject matrix - as required.
		DataEntryStage stage = DataEntryStage.INVALID;
		try {
			stage = eventCRF.getStage();
			ecStatus = eventCRF.getStatus();
			status = seb.getSubjectEventStatus();// SubjectEventStatus.get(
			crfv = eventCRF.getCrfVersion();
		} catch (NullPointerException e) {
			logger.info("exception hit, status set to not scheduled");
		}
		logger.info("event crf stage: " + stage.getName() + ", event crf status: " + ecStatus.getName() + ", STATUS: "
				+ status.getName() + " crf version: " + crfv.getStatus().getName() + " data entry stage: "
				+ stage.getName());

		if (stage.equals(DataEntryStage.INVALID) || ecStatus.equals(Status.INVALID)) {
			stage = DataEntryStage.UNCOMPLETED;
		}
		crfVersionStatus = stage.getName();
		if (status.equals(SubjectEventStatus.LOCKED) || status.equals(SubjectEventStatus.SKIPPED)
				|| status.equals(SubjectEventStatus.STOPPED)) {
			crfVersionStatus = DataEntryStage.LOCKED.getName();
		} else if (status.equals(SubjectEventStatus.INVALID)) {
			crfVersionStatus = DataEntryStage.LOCKED.getName();
		} else if (!currentCRF.getStatus().equals(Status.AVAILABLE)) {
			crfVersionStatus = DataEntryStage.LOCKED.getName();
		} else if (!crfv.getStatus().equals(Status.AVAILABLE)) {
			crfVersionStatus = DataEntryStage.LOCKED.getName();
		}

		logger.info("returning: " + crfVersionStatus);
		return crfVersionStatus;
	}

	protected int getNumItems(int sedInd, int crfInd) {
		syncCRFIndex(sedInd, crfInd);
		ArrayList items = getColumns(currentDef, currentCRF);
		return items.size();
	}

	private void syncItemIndex(int sedInd, int crfInd, int itemInd) {
		syncCRFIndex(sedInd, crfInd);

		ArrayList items = getColumns(currentDef, currentCRF);
		currentItem = (ItemBean) items.get(itemInd - 1);

	}

	private String getItemName(int sedInd, int crfInd, int itemInd) {
		syncItemIndex(sedInd, crfInd, itemInd);
		return currentItem.getName();
	}

	private String getDataKey(int studySubjectId, int studyEventDefinitionId, int sampleOrdinal, int crfId, int itemId,
			int itemOrdinal, String groupName) {
		String groupString = "";
		if (!groupName.equals(UNGROUPED)) {
			// need to remember that this is hard coded, need to place it
			// outside the code somehow, tbh
			groupString = "_" + groupName + "_" + itemOrdinal;
		}
		return studySubjectId + "_" + studyEventDefinitionId + "_" + sampleOrdinal + "_" + crfId + "_" + itemId
				+ groupString;
	}

	private String getDisplayedKey(StudyEventDefinitionBean sed, CRFBean cb, ItemBean ib) {
		return sed.getId() + "_" + cb.getId() + "_" + ib.getId();
	}

	private String getColumnsKey(StudyEventDefinitionBean sed, CRFBean cb) {
		return sed.getId() + "_" + cb.getId();
	}

	private String getStudyEventDataKey(int studySubjectId, int studyEventDefinitionId, int sampleOrdinal) {
		String key = studySubjectId + "_" + studyEventDefinitionId + "_" + sampleOrdinal;
		return key;
	}

	public static String getSEDCode(int sedInd) {
		sedInd--;
		if (sedInd > 26) {
			int digit1 = sedInd / 26;
			int digit2 = sedInd % 26;

			char letter1 = (char) ('A' + digit1);
			char letter2 = (char) ('A' + digit2);

			return "" + letter1 + letter2;
		} else {
			char letter = (char) ('A' + sedInd);

			return "" + letter;
		}
	}

	public static String getSEDCRFCode(int sedInd, int crfInd) {
		return getSEDCode(sedInd) + crfInd;
	}

	private String getSampleCode(int ordinal, int numSamples) {
		return numSamples > 1 ? "_" + ordinal : "";
	}

	private String getColumnLabel(int sedInd, int ordinal, String labelType, int numSamples) {
		return labelType + "_" + getSEDCode(sedInd) + getSampleCode(ordinal, numSamples);
	}

	private String getColumnDescription(int sedInd, int ordinal, String labelType, String defName, int numSamples) {
		return labelType + defName + "(" + getSEDCode(sedInd) + getSampleCode(ordinal, numSamples) + ")";
	}

	private String getColumnItemLabel(int sedInd, int ordinal, int crfInd, int itemInd, int numSamples,
			int itemDataOrdinal, String groupName) {
		String groupEnd = "";
		if (!groupName.equals(UNGROUPED)) {
			groupEnd = "_" + groupName + "_" + itemDataOrdinal;
		}
		return getItemName(sedInd, crfInd, itemInd) + "_" + getSEDCRFCode(sedInd, crfInd)
				+ getSampleCode(ordinal, numSamples) + groupEnd;// "_"
	}

	protected boolean inKeys(int sedInd, int sampleOrdinal, int crfInd, int itemInd, String groupName) {
		syncSEDIndex(sedInd);
		syncCRFIndex(sedInd, crfInd);
		syncItemIndex(sedInd, crfInd, itemInd);

		/**
		 * @vbc 08/06/2008 NEW EXTRACT DATA IMPLEMENTATION change it into a simple HashMap
		 * 
		 *      TODO - verify if the itemOrdinalItem is required - in the previous code is set to 1 !?!!?
		 */
		String key = currentDef.getId() + "_" + sampleOrdinal + "_" + currentCRF.getId() + "_" + currentItem.getId()
				+ "_" + groupName;
		Boolean issavedkey = (Boolean) getHmInKeys().get(key);
		if (issavedkey == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return Returns the study.
	 */
	public StudyBean getStudy() {
		return study;
	}

	/**
	 * @param study
	 *            The study to set.
	 */
	public void setStudy(StudyBean study) {
		this.study = study;
	}

	/**
	 * @return Returns the parentStudy.
	 */
	public StudyBean getParentStudy() {
		return parentStudy;
	}

	/**
	 * @param parentStudy
	 *            The parentStudy to set.
	 */
	public void setParentStudy(StudyBean parentStudy) {
		this.parentStudy = parentStudy;
	}

	/**
	 * @return Returns the format.
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            The format to set.
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	/**
	 * @return Returns the dataset.
	 */
	public DatasetBean getDataset() {
		return dataset;
	}

	/**
	 * @param dataset
	 *            The dataset to set.
	 */
	public void setDataset(DatasetBean dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return Returns the studyEvents.
	 */
	public ArrayList getStudyEvents() {
		return studyEvents;
	}

	/**
	 * The maximum over all ordinals over all study events for the provided SED.
	 * 
	 * @param i
	 *            An index into the studyEvents list for the SED whose max ordinal we want.
	 * @return The maximum number of samples for the i-th SED.
	 */
	public int getSEDNumSamples(int i) {
		syncSEDIndex(i);
		int sedId = currentDef.getId();
		return getMaxOrdinal(sedId);
	}

	/**
	 * Get the event correspodning to the provided study subject, SED and sample ordinal.
	 * 
	 * @param h
	 *            An index into the array of subjects.
	 * @param i
	 *            An index into the array of SEDs.
	 * @param j
	 *            The sample ordinal.
	 * @return The event correspodning to the provided study subject, SED and sample ordinal.
	 */
	private StudyEventBean getEvent(int h, int i, int j) {
		syncSubjectIndex(h);
		syncSEDIndex(i);

		String key = getStudyEventDataKey(currentSubject.getId(), currentDef.getId(), j);
		StudyEventBean seb = (StudyEventBean) eventData.get(key);

		if (seb == null) {
			return new StudyEventBean();
		} else {
			return seb;
		}
	}

	private String getEventLocation(int h, int i, int j) {
		return getEvent(h, i, j).getLocation();
	}

	private String getEventStart(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		Date start = seb.getDateStarted();
		if (seb.getStartTimeFlag()) {
			return start != null ? long_sdf.format(start) : "";
		} else {
			return start != null ? sdf.format(start) : "";
		}
	}

	private String getEventEnd(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		Date end = seb.getDateEnded();
		if (seb.getEndTimeFlag()) {
			return end != null ? long_sdf.format(end) : "";
		} else {
			return end != null ? sdf.format(end) : "";
		}
	}

	private String getEventStatus(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);

		return seb.getSubjectEventStatus().getName();
	}

	private String getAgeAtEvent(int h, int i, int j) {
		StudyEventBean seb = getEvent(h, i, j);
		Date startDate = seb.getDateStarted();
		startDate = seb.getDateStarted() != null ? seb.getDateStarted() : new Date();
		Date age = currentSubject.getDateOfBirth();
		String answer = "";
		if (age.before(startDate)) {
			Calendar dateOfBirth = Calendar.getInstance();
			dateOfBirth.setTime(age);
			Calendar theStartDate = Calendar.getInstance();// new
			theStartDate.setTime(startDate);
			int theAge = theStartDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
			Calendar today = Calendar.getInstance();
			// add the age to the year to see if it's happened yet
			dateOfBirth.add(Calendar.YEAR, theAge);
			// subtract one from the age if the birthday hasn't happened yet
			if (today.before(dateOfBirth)) {
				theAge--;
			}
			answer = "" + theAge;
		} else {
			// ideally should not get here, but we have an 'error' code if it
			// does, tbh
			answer = "-1";
		}
		return answer;
	}

	protected ArrayList getSubjects() {
		return this.subjects;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            The dateCreated to set.
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return Returns the itemNames.
	 */
	public ArrayList getItemNames() {
		return itemNames;
	}

	/**
	 * @param itemNames
	 *            The itemNames to set.
	 */
	public void setItemNames(ArrayList itemNames) {
		this.itemNames = itemNames;
	}

	/**
	 * @return Returns the rowValues.
	 */
	public ArrayList getRowValues() {
		return rowValues;
	}

	/**
	 * @param rowValues
	 *            The rowValues to set.
	 */
	public void setRowValues(ArrayList rowValues) {
		this.rowValues = rowValues;
	}

	/**
	 * @return Returns the eventHeaders.
	 */
	public ArrayList getEventHeaders() {
		return eventHeaders;
	}

	/**
	 * @param eventHeaders
	 *            The eventHeaders to set.
	 */
	public void setEventHeaders(ArrayList eventHeaders) {
		this.eventHeaders = eventHeaders;
	}

	public ArrayList getStudyGroupClasses() {
		return studyGroupClasses;
	}

	public void setStudyGroupClasses(ArrayList studyGroupClasses) {
		this.studyGroupClasses = studyGroupClasses;
	}

	public HashMap getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(HashMap groupNames) {
		this.groupNames = groupNames;
	}

	public String getShowUniqueId() {
		return showUniqueId;
	}

	public void setShowUniqueId(String showUniqueId) {
		this.showUniqueId = showUniqueId;
	}

	/**
	 * @return the hmInKeys
	 */
	public HashMap getHmInKeys() {
		return hmInKeys;
	}

	/**
	 * @param hmInKeys
	 *            the hmInKeys to set
	 */
	public void setHmInKeys(HashMap hmInKeys) {
		this.hmInKeys = hmInKeys;
	}

	/**
	 * This sets the values from the two querries
	 */
	public void resetEntryBASE_EVENTSIDE() {
		hBASE_EVENTSIDE = new ArrayList();

	}

	public void resetEntryBASE_ITEMGROUPSIDE() {
		hBASE_ITEMGROUPSIDE = new ArrayList();
	}

	public void resetArrayListEntryBASE_ITEMGROUPSIDE() {
		aBASE_ITEMDATAID = new ArrayList();
	}

	/**
	 * Add n entry
	 * 
	 * @param itemdataid
	 */
	public void addItemDataIdEntry(Integer itemdataid) {
		aBASE_ITEMDATAID.add(itemdataid);
	}//

	/**
	 * Add an entry
	 * 
	 * @param pitemDataId
	 * @param pitemGroupId
	 * @param pitemGroupName
	 * @param pitemDescription
	 * @param pitemName
	 * @param pitemValue
	 * @param pitemUnits
	 * @param pcrfVersionName
	 * @param pcrfVersionStatusId
	 * @param pdateInterviewed
	 * @param pinterviewerName
	 * @param peventCrfDateCompleted
	 * @param peventCrfDateValidateCompleted
	 * @param peventCrfCompletionStatusId
	 * @param pstudySubjectId
	 * @param peventCrfId
	 * @param pitemId
	 * @param pcrfVersionId
	 */
	public void addEntryBASE_ITEMGROUPSIDE(Integer pitemDataId, Integer pitemdataordinal, Integer pitemGroupId,
			String pitemGroupName, Integer pitemDatatypeId, String pitemDescription, String pitemName,
			String pitemValue, String pitemUnits, String pcrfVersionName, Integer pcrfVersionStatusId,
			Date pdateInterviewed, String pinterviewerName, Timestamp peventCrfDateCompleted,
			Timestamp peventCrfDateValidateCompleted, Integer peventCrfCompletionStatusId,
			Integer pitemGroupRepeatNumber, Integer pcrfId, Integer pstudySubjectId, Integer peventCrfId,
			Integer pitemId, Integer pcrfVersionId, Integer eventcrfStatusId) {
		extractDataset_ITEMGROUPSIDE obj = new extractDataset_ITEMGROUPSIDE();

		obj.setSQLDatasetBASE_ITEMGROUPSIDE(pitemDataId, pitemdataordinal, pitemGroupId, pitemGroupName,
				pitemDatatypeId, pitemDescription, pitemName, pitemValue, pitemUnits, pcrfVersionName,
				pcrfVersionStatusId, pdateInterviewed, pinterviewerName, peventCrfDateCompleted,
				peventCrfDateValidateCompleted, peventCrfCompletionStatusId, pitemGroupRepeatNumber, pcrfId,
				pstudySubjectId, peventCrfId, pitemId, pcrfVersionId, eventcrfStatusId);

		hBASE_ITEMGROUPSIDE.add(obj);
		// TODO - verify that the order is the same

	}// addEntryBASE_ITEMGROUPSIDE

	/**
	 * Add an entry in the HashMap
	 * 
	 * @param pitemDataId
	 * @param pstudySubjectId
	 * @param psampleOrdinal
	 * @param pstudyEvenetDefinitionId
	 * @param pstudyEventDefinitionName
	 * @param pstudyEventLoacation
	 * @param pstudyEventDateStart
	 * @param pstudyEventDateEnd
	 * @param pstudyEventStartTimeFlag
	 * @param pstudyEventEndTimeFlag
	 * @param pstudyEventStatusId
	 * @param pstudyEventSubjectEventStatusId
	 * @param pitemId
	 * @param pcrfVersionId
	 * @param peventCrfId
	 * @param pstudyEventId
	 */
	public void addEntryBASE_EVENTSIDE(Integer pitemDataId, Integer pstudySubjectId, Integer psampleOrdinal,
			Integer pstudyEvenetDefinitionId, String pstudyEventDefinitionName, String pstudyEventLoacation,
			Timestamp pstudyEventDateStart, Timestamp pstudyEventDateEnd, Boolean pstudyEventStartTimeFlag,
			Boolean pstudyEventEndTimeFlag, Integer pstudyEventStatusId, Integer pstudyEventSubjectEventStatusId,
			Integer pitemId, Integer pcrfVersionId, Integer peventCrfId, Integer pstudyEventId)

	{
		extractDataset_EVENTSIDE obj = new extractDataset_EVENTSIDE();

		obj.setSQLDatasetBASE_EVENTSIDE(pitemDataId, pstudySubjectId, psampleOrdinal, pstudyEvenetDefinitionId,
				pstudyEventDefinitionName, pstudyEventLoacation, pstudyEventDateStart, pstudyEventDateEnd,
				pstudyEventStartTimeFlag, pstudyEventEndTimeFlag, pstudyEventStatusId, pstudyEventSubjectEventStatusId,
				pitemId, pcrfVersionId, peventCrfId, pstudyEventId);

		hBASE_EVENTSIDE.add(obj);
		// TODO - verify that the order is the same

	}// addEntryBASE_EVENTSIDE

	/**
	 * This class captures the data from getSQLDatasetBASE_EVENTSIDE SELECT
	 * 
	 * itemdataid, studysubjectid, study_event.sample_ordinal, study_event.study_event_definition_id,
	 * study_event_definition.name, study_event.location, study_event.date_start, study_event.date_end,
	 * 
	 * study_event.start_time_flag study_event.end_time_flag study_event.status_id study_event.subject_event_status_id
	 * 
	 * //ids itemid, crfversionid, eventcrfid, studyeventid
	 * 
	 * This is used to merge the two BASE querries and build the eventData for ExtractBean.java
	 * 
	 */
	public class extractDataset_EVENTSIDE {
		// TODO - could be made private and then get/set

		// "primary key"
		public Integer itemDataId;

		// data
		public Integer studySubjectId;
		public Integer sampleOrdinal;
		public Integer studyEvenetDefinitionId;
		public String studyEventDefinitionName;
		public String studyEventLoacation;
		public Timestamp studyEventDateStart;
		public Timestamp studyEventDateEnd;
		public Boolean studyEventStartTimeFlag;
		public Boolean studyEventEndTimeFlag;
		public Integer studyEventStatusId;
		public Integer studyEventSubjectEventStatusId;
		public Integer itemId;
		public Integer crfVersionId;
		public Integer eventCrfId;
		public Integer studyEventId;

		public void setSQLDatasetBASE_EVENTSIDE(Integer pitemDataId, Integer pstudySubjectId, Integer psampleOrdinal,
				Integer pstudyEvenetDefinitionId, String pstudyEventDefinitionName, String pstudyEventLoacation,
				Timestamp pstudyEventDateStart, Timestamp pstudyEventDateEnd, Boolean pstudyEventStartTimeFlag,
				Boolean pstudyEventEndTimeFlag, Integer pstudyEventStatusId, Integer pstudyEventSubjectEventStatusId,
				Integer pitemId, Integer pcrfVersionId, Integer peventCrfId, Integer pstudyEventId) {
			// assigns

			// "primary key"
			itemDataId = pitemDataId;

			// data
			studySubjectId = pstudySubjectId;
			sampleOrdinal = psampleOrdinal;
			studyEvenetDefinitionId = pstudyEvenetDefinitionId;
			studyEventDefinitionName = pstudyEventDefinitionName;
			studyEventLoacation = pstudyEventLoacation;
			studyEventDateStart = pstudyEventDateStart;
			studyEventDateEnd = pstudyEventDateEnd;
			studyEventStartTimeFlag = pstudyEventStartTimeFlag;
			studyEventEndTimeFlag = pstudyEventEndTimeFlag;
			studyEventStatusId = pstudyEventStatusId;
			studyEventSubjectEventStatusId = pstudyEventSubjectEventStatusId;

			itemId = pitemId;
			crfVersionId = pcrfVersionId;
			eventCrfId = peventCrfId;
			studyEventId = pstudyEventId;

		}// set

		/**
		 * for debug
		 */
		@Override
		public String toString() {
			String ret = new String("");

			// "primary key"
			if (itemDataId == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemDataId.toString();
			}
			ret = ret + "_";

			// data

			if (studySubjectId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studySubjectId.toString();
			}
			ret = ret + "_";

			if (sampleOrdinal == null) {
				ret = ret + "null";
			} else {
				ret = ret + sampleOrdinal.toString();
			}
			ret = ret + "_";

			if (studyEvenetDefinitionId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEvenetDefinitionId.toString();
			}
			ret = ret + "_";

			if (studyEventDefinitionName == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventDefinitionName.toString();
			}
			ret = ret + "_";

			if (studyEventLoacation == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventLoacation.toString();
			}
			ret = ret + "_";

			if (studyEventDateStart == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventDateStart.toString();
			}
			ret = ret + "_";

			if (studyEventDateEnd == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventDateEnd.toString();
			}
			ret = ret + "_";

			if (studyEventStartTimeFlag == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventStartTimeFlag.toString();
			}
			ret = ret + "_";

			if (studyEventEndTimeFlag == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventEndTimeFlag.toString();
			}
			ret = ret + "_";

			if (studyEventStatusId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventStatusId.toString();
			}
			ret = ret + "_";

			if (studyEventSubjectEventStatusId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventSubjectEventStatusId.toString();
			}
			ret = ret + "_";

			if (itemId == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemId.toString();
			}
			ret = ret + "_";

			if (crfVersionId == null) {
				ret = ret + "null";
			} else {
				ret = ret + crfVersionId.toString();
			}
			ret = ret + "_";

			if (eventCrfId == null) {
				ret = ret + "null";
			} else {
				ret = ret + eventCrfId.toString();
			}
			ret = ret + "_";

			if (studyEventId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studyEventId.toString();
			}
			ret = ret + "_";

			return ret;
		}

	}// class

	/**
	 * The second part of the merge for eventData in ExtractBean
	 * 
	 * SELECT itemdataid, item_group_metadata.item_group_id , item_group.name, itemdesc, itemname, itemvalue, itemunits,
	 * crfversioname, crfversionstatusid, dateinterviewed, interviewername, eventcrfdatecompleted,
	 * eventcrfdatevalidatecompleted, eventcrfcompletionstatusid, repeat_number, crfid,
	 * 
	 * //and ids studysubjectid, eventcrfid, itemid, crfversionid
	 * 
	 */
	public class extractDataset_ITEMGROUPSIDE {

		// TODO - could be made private and then get/set

		// this is the key
		public Integer itemDataId;

		// data
		public Integer itemGroupId;
		public String itemGroupName;
		public String itemDescription;
		public String itemName;
		public String itemValue;
		public String itemUnits;
		public String crfVersionName;
		public Integer crfVersionStatusId;
		public Date dateInterviewed;
		public String interviewerName;
		public Timestamp eventCrfDateCompleted;
		public Timestamp eventCrfDateValidateCompleted;
		public Integer eventCrfCompletionStatusId;
		public Integer itemGroupRepeatNumber;
		public Integer crfid;
		public Integer eventCrfStatusId;

		// keys
		public Integer studySubjectId;
		public Integer eventCrfId;
		public Integer itemId;
		public Integer crfVersionId;

		public void setSQLDatasetBASE_ITEMGROUPSIDE(Integer pitemDataId, Integer pitemdataordinal,
				Integer pitemGroupId, String pitemGroupName, Integer pitemDatatypeId, String pitemDescription,
				String pitemName, String pitemValue, String pitemUnits, String pcrfVersionName,
				Integer pcrfVersionStatusId, Date pdateInterviewed, String pinterviewerName,
				Timestamp peventCrfDateCompleted, Timestamp peventCrfDateValidateCompleted,
				Integer peventCrfCompletionStatusId, Integer pitemGroupMetatdatrepeatNumber, Integer pcrfId,
				Integer pstudySubjectId, Integer peventCrfId, Integer pitemId, Integer pcrfVersionId,
				Integer eventcrfStatusId) {
			// assign
			itemDataId = pitemDataId;
			itemGroupId = pitemGroupId;
			itemGroupName = pitemGroupName;
			itemDescription = pitemDescription;
			itemName = pitemName;

			if (pitemDatatypeId == 9) {
				SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.getDateFormatInItemData());
				sdf.setLenient(false);
				try {
					java.util.Date date = sdf.parse(pitemValue);
					itemValue = new SimpleDateFormat("yyyy-MM-dd").format(date);
				} catch (ParseException fe) {
					itemValue = pitemValue;
					logger.info("Failed date format for: item-data-id=" + pitemDataId + " with data-type-id="
							+ pitemDatatypeId + " and item-data-value=" + pitemValue);
				}
			} else {
				itemValue = pitemValue;
			}

			itemUnits = pitemUnits;
			crfVersionName = pcrfVersionName;
			crfVersionStatusId = pcrfVersionStatusId;
			dateInterviewed = pdateInterviewed;
			interviewerName = pinterviewerName;
			eventCrfDateCompleted = peventCrfDateCompleted;
			eventCrfDateValidateCompleted = peventCrfDateValidateCompleted;
			eventCrfCompletionStatusId = peventCrfCompletionStatusId;
			eventCrfStatusId = eventcrfStatusId;

			itemGroupRepeatNumber = pitemdataordinal;

			crfid = pcrfId;

			studySubjectId = pstudySubjectId;
			eventCrfId = peventCrfId;
			itemId = pitemId;
			crfVersionId = pcrfVersionId;
		}

		/**
		 * for debug
		 */
		@Override
		public String toString() {
			String ret = new String("");

			if (itemDataId == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemDataId.toString();
			}
			ret = ret + "_";

			// data

			if (itemGroupId == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemGroupId.toString();
			}
			ret = ret + "_";

			if (itemGroupName == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemGroupName.toString();
			}
			ret = ret + "_";

			if (itemDescription == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemDescription.toString();
			}
			ret = ret + "_";

			if (itemName == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemName.toString();
			}
			ret = ret + "_";

			if (itemValue == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemValue.toString();
			}
			ret = ret + "_";

			if (itemUnits == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemUnits.toString();
			}
			ret = ret + "_";

			if (crfVersionName == null) {
				ret = ret + "null";
			} else {
				ret = ret + crfVersionName.toString();
			}
			ret = ret + "_";

			if (crfVersionStatusId == null) {
				ret = ret + "null";
			} else {
				ret = ret + crfVersionStatusId.toString();
			}
			ret = ret + "_";

			if (dateInterviewed == null) {
				ret = ret + "null";
			} else {
				ret = ret + dateInterviewed.toString();
			}
			ret = ret + "_";

			if (interviewerName == null) {
				ret = ret + "null";
			} else {
				ret = ret + interviewerName.toString();
			}
			ret = ret + "_";

			if (eventCrfDateCompleted == null) {
				ret = ret + "null";
			} else {
				ret = ret + eventCrfDateCompleted.toString();
			}
			ret = ret + "_";

			if (eventCrfDateValidateCompleted == null) {
				ret = ret + "null";
			} else {
				ret = ret + eventCrfDateValidateCompleted.toString();
			}
			ret = ret + "_";

			if (eventCrfCompletionStatusId == null) {
				ret = ret + "null";
			} else {
				ret = ret + eventCrfCompletionStatusId.toString();
			}
			ret = ret + "_";

			if (itemGroupRepeatNumber == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemGroupRepeatNumber.toString();
			}
			ret = ret + "_";

			if (crfid == null) {
				ret = ret + "null";
			} else {
				ret = ret + crfid.toString();
			}
			ret = ret + "_";

			// keys
			if (studySubjectId == null) {
				ret = ret + "null";
			} else {
				ret = ret + studySubjectId.toString();
			}
			ret = ret + "_";

			if (eventCrfId == null) {
				ret = ret + "null";
			} else {
				ret = ret + eventCrfId.toString();
			}
			ret = ret + "_";

			if (itemId == null) {
				ret = ret + "null";
			} else {
				ret = ret + itemId.toString();
			}
			ret = ret + "_";

			if (crfVersionId == null) {
				ret = ret + "null";
			} else {
				ret = ret + crfVersionId.toString();
			}
			ret = ret + "_";

			return ret;
		}

	}// class

	/**
	 * @return the hBASE_EVENTSIDE
	 */
	public ArrayList getHBASE_EVENTSIDE() {
		return hBASE_EVENTSIDE;
	}

	/**
	 * @param hbase_eventside
	 *            the hBASE_EVENTSIDE to set
	 */
	public void setHBASE_EVENTSIDE(ArrayList hbase_eventside) {
		hBASE_EVENTSIDE = hbase_eventside;
	}

	/**
	 * @return the hBASE_ITEMGROUPSIDE
	 */
	public ArrayList getHBASE_ITEMGROUPSIDE() {
		return hBASE_ITEMGROUPSIDE;
	}

	/**
	 * @param hbase_itemgroupside
	 *            the hBASE_ITEMGROUPSIDE to set
	 */
	public void setHBASE_ITEMGROUPSIDE(ArrayList hbase_itemgroupside) {
		hBASE_ITEMGROUPSIDE = hbase_itemgroupside;
	}

	/**
	 * @return the aBASE_ITEMDATAID
	 */
	public ArrayList getABASE_ITEMDATAID() {
		return aBASE_ITEMDATAID;
	}

	/**
	 * @param abase_itemdataid
	 *            the aBASE_ITEMDATAID to set
	 */
	public void setABASE_ITEMDATAID(ArrayList abase_itemdataid) {
		aBASE_ITEMDATAID = abase_itemdataid;
	}

}
