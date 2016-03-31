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

package org.akaza.openclinica.service;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * DiscrepancyNoteUtil is a convenience class for managing discrepancy notes, such as getting all notes for a study, or
 * filtering them by subject or resolution status.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class DiscrepancyNoteUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DiscrepancyNoteUtil.class);
	public static final Map<String, Integer> TYPES = new HashMap<String, Integer>();
	public static final int ANNOTATION_ID = 2;
	public static final int RFC_ID = 4;
	public static final int FAILED_VALIDATION_ID = 1;
	public static final int QUERY_ID = 3;
	public static final int NEW_ID = 1;
	public static final int UPDATED_ID = 2;
	public static final int RESOLUTION_PROPOSED_ID = 3;
	public static final int CLOSED_ID = 4;
	public static final int NA_ID = 5;

	static {
		TYPES.put("Annotation", ANNOTATION_ID);
		TYPES.put("Reason for Change", RFC_ID);
		TYPES.put("Failed Validation Check", FAILED_VALIDATION_ID);
		TYPES.put("Query", QUERY_ID);
	}

	public static final Map<String, Integer> RESOLUTION_STATUS = new HashMap<String, Integer>();

	static {
		RESOLUTION_STATUS.put("New", NEW_ID);
		RESOLUTION_STATUS.put("Updated", UPDATED_ID);
		RESOLUTION_STATUS.put("Resolution Proposed", RESOLUTION_PROPOSED_ID);
		RESOLUTION_STATUS.put("Closed", CLOSED_ID);
		RESOLUTION_STATUS.put("Not Applicable", NA_ID);
	}

	/**
	 * Get type names from the resource bundle.
	 * @param bundle ResourceBundle
	 * @return String[]
	 */
	public static String[] getTypeNames(ResourceBundle bundle) {
		return new String[]{bundle.getString("query"), bundle.getString("Failed_Validation_Check"),
				bundle.getString("reason_for_change"), bundle.getString("Annotation")};
	}

	/**
	 * Inject DNs into Study Events.
	 * @param displayStudyBeans List<DisplayStudyEventBean>
	 * @param resolutionStatusIds Set<Integer>
	 * @param dataSource DataSource
	 * @param discNoteType int
	 */
	public void injectParentDiscNotesIntoDisplayStudyEvents(List<DisplayStudyEventBean> displayStudyBeans,
															Set<Integer> resolutionStatusIds, DataSource dataSource, int discNoteType) {
		if (displayStudyBeans == null) {
			return;
		}
		// booleans representing whether this method should only get
		// DiscrepancyNoteBeans with
		// certain resolution status or discrepancyNoteTypeId number.
		boolean hasResolutionStatus = this.checkResolutionStatus(resolutionStatusIds);
		boolean hasDiscNoteType = discNoteType >= 1 && discNoteType <= RFC_ID;

		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(dataSource);

		StudyEventBean studyEventBean;
		List<EventCRFBean> eventCRFBeans;
		List<DiscrepancyNoteBean> foundDiscNotes;

		for (DisplayStudyEventBean dStudyEventBean : displayStudyBeans) {
			studyEventBean = dStudyEventBean.getStudyEvent();
			// All EventCRFs for a study event
			eventCRFBeans = eventCRFDAO.findAllByStudyEvent(studyEventBean);

			for (EventCRFBean eventCrfBean : eventCRFBeans) {
				// Find ItemData type notes associated with an event crf
				foundDiscNotes = discrepancyNoteDAO.findParentItemDataDNotesFromEventCRF(eventCrfBean);
				// filter for any specified disc note type
				if (!foundDiscNotes.isEmpty() && hasDiscNoteType) {
					// only include disc notes that have the specified disc note
					// type id
					foundDiscNotes = filterforDiscNoteType(foundDiscNotes, discNoteType);
				}
				if (!foundDiscNotes.isEmpty()) {
					if (!hasResolutionStatus) {
						studyEventBean.getDiscBeanList().addAll(foundDiscNotes);
					} else {
						// Only include disc notes with a particular resolution
						// status, specified by
						// the parameter passed to the servlet or saved in a
						// session variable
						for (DiscrepancyNoteBean discBean : foundDiscNotes) {
							for (int statusId : resolutionStatusIds) {
								if (discBean.getResolutionStatusId() == statusId) {
									studyEventBean.getDiscBeanList().add(discBean);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Create thread of parents.
	 * @param allDiscNotes List<DiscrepancyNoteBean>
	 * @param currentStudy StudyBean
	 * @param resolutionStatusIds Set<Integer>
	 * @param discNoteType int
	 * @return List<DiscrepancyNoteThread>
	 */
	public List<DiscrepancyNoteThread> createThreadsOfParents(List<DiscrepancyNoteBean> allDiscNotes, StudyBean currentStudy, Set<Integer> resolutionStatusIds, int discNoteType) {

		List<DiscrepancyNoteThread> dnThreads = new ArrayList<DiscrepancyNoteThread>();
		if (allDiscNotes == null || allDiscNotes.isEmpty()) {
			return dnThreads;
		}
		if (currentStudy == null) {
			currentStudy = new StudyBean();
		}
		for (DiscrepancyNoteBean discBean : allDiscNotes) {
			DiscrepancyNoteThread tempDNThread = new DiscrepancyNoteThread();
			tempDNThread.setStudyId(currentStudy.getId());
			tempDNThread.getLinkedNoteList().addFirst(discBean);
			int resolutionStatusId = discBean.getResolutionStatusId();
			// the thread's status id is the parent's in this case, when there
			// are no children
			tempDNThread.setLatestResolutionStatus(this.getResolutionStatusName(resolutionStatusId));
			dnThreads.add(tempDNThread);
		}
		// Do the filtering here; remove any DN threads that do not have any
		// notes
		LinkedList<DiscrepancyNoteBean> linkedList;

		if (resolutionStatusIds != null && !resolutionStatusIds.isEmpty()) {
			for (DiscrepancyNoteThread dnThread : dnThreads) {
				linkedList = new LinkedList<DiscrepancyNoteBean>();
				for (DiscrepancyNoteBean discBean : dnThread.getLinkedNoteList()) {
					for (int statusId : resolutionStatusIds) {
						if (discBean.getResolutionStatusId() == statusId) {
							linkedList.offer(discBean);
						}
					}
				}
				dnThread.setLinkedNoteList(linkedList);
			}
			dnThreads = removeEmptyDNThreads(dnThreads);
		}
		if (discNoteType >= 1 && discNoteType <= NA_ID) {

			for (DiscrepancyNoteThread dnThread : dnThreads) {
				linkedList = new LinkedList<DiscrepancyNoteBean>();
				for (DiscrepancyNoteBean discBean : dnThread.getLinkedNoteList()) {
					if (discBean.getDiscrepancyNoteTypeId() == discNoteType) {
						linkedList.offer(discBean);
					}
				}
				dnThread.setLinkedNoteList(linkedList);
			}
			dnThreads = removeEmptyDNThreads(dnThreads);
		}
		return dnThreads;
	}

	/**
	 * Remove empty DN Threads.
	 * @param allDNThreads List<DiscrepancyNoteThread>
	 * @return List<DiscrepancyNoteThread>
	 */
	public List<DiscrepancyNoteThread> removeEmptyDNThreads(List<DiscrepancyNoteThread> allDNThreads) {

		if (allDNThreads == null || allDNThreads.isEmpty()) {
			return new ArrayList<DiscrepancyNoteThread>();
		}
		List<DiscrepancyNoteThread> newList = new ArrayList<DiscrepancyNoteThread>();

		for (DiscrepancyNoteThread thread : allDNThreads) {
			if (thread.getLinkedNoteList().size() > 0) {
				newList.add(thread);
			}
		}
		return newList;
	}

	/**
	 * Check whether the contents of a list of resolution status ids are valid.
	 *
	 * @param listOfStatusIds A HashSet of resolution status ids.
	 * @return true or false, depending on whether the ids are valid.
	 */
	public boolean checkResolutionStatus(Set<Integer> listOfStatusIds) {
		if (listOfStatusIds == null) {
			return false;
		}
		for (int id : listOfStatusIds) {
			if (id >= 1 && id <= NA_ID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Filter a List of DiscrepancyNoteBeans for a particular discrepancy note type id.
	 *
	 * @param allDiscNotes A List of DiscrepancyNoteBeans prior to being filtered for a discrepancy note type id.
	 * @param discNoteType An it representing a discrepancy note type id.
	 * @return A List of DiscrepancyNoteBeans that have the specified discrepancy note type id.
	 */
	public List<DiscrepancyNoteBean> filterforDiscNoteType(List<DiscrepancyNoteBean> allDiscNotes, int discNoteType) {

		// Do not filter this List if the discNoteType isn't between 1 and 4
		if (!(discNoteType >= 1 && discNoteType <= RFC_ID)) {
			return allDiscNotes;
		}
		List<DiscrepancyNoteBean> newDiscNotes = new ArrayList<DiscrepancyNoteBean>();

		for (DiscrepancyNoteBean dnBean : allDiscNotes) {
			if (dnBean.getDiscrepancyNoteTypeId() == discNoteType) {
				newDiscNotes.add(dnBean);
			}
		}
		return newDiscNotes;
	}

	/**
	 * Get the number of DiscrepancyNoteBeans of a particular type, like "Failed Validation Check.".
	 *
	 * @param discrepancyNoteBeans A List of DiscrepancyNoteBeans.
	 * @param resStatusId          An int representing the dsicrepancy note type id.
	 * @param eventCRFId           int
	 * @return Only any DiscrepancyNoteBeans that have this type id.
	 */
	public int getDiscNoteCountByStatusEventCRFId(List<DiscrepancyNoteBean> discrepancyNoteBeans, int resStatusId,
												  int eventCRFId) {
		int typeCount = 0;
		for (DiscrepancyNoteBean dBean : discrepancyNoteBeans) {
			if (dBean.getResolutionStatusId() == resStatusId && dBean.getEventCRFId() == eventCRFId) {
				typeCount++;
			}
		}
		return typeCount;
	}

	/**
	 * Get Resolution Status Name.
	 * @param resId int
	 * @return String
	 */
	public String getResolutionStatusName(int resId) {
		for (String resName : RESOLUTION_STATUS.keySet()) {
			if (resId == RESOLUTION_STATUS.get(resName)) {
				return resName;
			}
		}
		return "";
	}

	/**
	 * Get resolution status type name.
	 * @param resTypeId int
	 * @return String
	 */
	public String getResolutionStatusTypeName(int resTypeId) {
		for (String resName : TYPES.keySet()) {
			if (resTypeId == TYPES.get(resName)) {
				return resName;
			}
		}
		return "";
	}

	/**
	 * Create Disc Notes By Event CRF.
	 * @param displayEvents List<DisplayStudyEventBean>
	 * @return Map <Integer, Map<String, Integer>>
	 */
	public Map<Integer, Map<String, Integer>> createDiscNoteMapByEventCRF(List<DisplayStudyEventBean> displayEvents) {

		Map<Integer, Map<String, Integer>> discNoteMap = new HashMap<Integer, Map<String, Integer>>();
		if (displayEvents == null || displayEvents.isEmpty()) {
			return discNoteMap;
		}
		Map<String, Integer> innerMap;
		SortedSet<Integer> allEventCRFIds = getEventCRFIdsFromDisplayEvents(displayEvents);

		for (Integer eventCRFId : allEventCRFIds) {
			innerMap = getDiscNoteCountFromDisplayEvents(displayEvents, eventCRFId);
			discNoteMap.put(eventCRFId, innerMap);
		}
		return discNoteMap;
	}

	private Map<String, Integer> getDiscNoteCountFromDisplayEvents(List<DisplayStudyEventBean> disBeans, int eventCRFId) {
		Map<String, Integer> discNoteMap = new HashMap<String, Integer>();
		if (eventCRFId == 0 || disBeans == null) {
			return discNoteMap;
		}
		List<DiscrepancyNoteBean> dnBeans;
		for (DisplayStudyEventBean eventBean : disBeans) {
			dnBeans = eventBean.getStudyEvent().getDiscBeanList();
			for (String statusName : RESOLUTION_STATUS.keySet()) {
				discNoteMap.put(statusName,
						getDiscNoteCountByStatusEventCRFId(dnBeans, RESOLUTION_STATUS.get(statusName), eventCRFId));
			}
		}
		return discNoteMap;
	}

	private SortedSet<Integer> getEventCRFIdsFromDisplayEvents(List<DisplayStudyEventBean> displayEvents) {
		SortedSet<Integer> treeSet = new TreeSet<Integer>();
		if (displayEvents == null || displayEvents.isEmpty()) {
			return treeSet;
		}
		List<DisplayEventCRFBean> displayEventCRFBeans;
		List<EventCRFBean> eventCRFBeans;

		for (DisplayStudyEventBean displayStudyEventBean : displayEvents) {
			displayEventCRFBeans = displayStudyEventBean.getDisplayEventCRFs();
			if (displayEventCRFBeans.isEmpty()) {
				eventCRFBeans = displayStudyEventBean.getStudyEvent().getEventCRFs();
				for (EventCRFBean evBean : eventCRFBeans) {
					treeSet.add(evBean.getId());
				}
				continue; // move on to the next DisplayStudyEventBean
			}
			for (DisplayEventCRFBean disBean : displayEventCRFBeans) {
				treeSet.add(disBean.getEventCRF().getId());
			}
		}

		return treeSet;
	}

	/**
	 * Get study subject id for disc note.
	 * @param discrepancyNoteBean DiscrepancyNoteBean
	 * @param dataSource DataSource
	 * @param studyId int
	 * @return int
	 */
	public int getStudySubjectIdForDiscNote(DiscrepancyNoteBean discrepancyNoteBean, DataSource dataSource, int studyId) {
		if (discrepancyNoteBean == null) {
			return 0;
		}
		StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
		List<StudySubjectBean> studySubjectBeans;

		if ("subject".equalsIgnoreCase(discrepancyNoteBean.getEntityType())) {
			studySubjectBeans = studySubjectDAO.findAllBySubjectId(discrepancyNoteBean.getEntityId());
			for (StudySubjectBean bean : studySubjectBeans) {
				if (bean.getStudyId() == studyId) {
					return bean.getId();
				} else {
					if (((StudyBean) new StudyDAO(dataSource).findByPK(bean.getStudyId())).getParentStudyId() == studyId) {
						return bean.getId();
					}
				}
			}
		} else if ("studySub".equalsIgnoreCase(discrepancyNoteBean.getEntityType())) {
			return discrepancyNoteBean.getEntityId();
		}
		return 0;
	}

	/**
	 * Create threads.
	 * @param allDiscNotes List<DiscrepancyNoteBean>
	 * @param currentStudy StudyBean
	 * @return List<DiscrepancyNoteThread>
	 */
	public List<DiscrepancyNoteThread> createThreads(List<DiscrepancyNoteBean> allDiscNotes,
													 StudyBean currentStudy) {

		List<DiscrepancyNoteThread> dnThreads = new ArrayList<DiscrepancyNoteThread>();
		if (allDiscNotes == null || allDiscNotes.isEmpty()) {
			return dnThreads;
		}
		if (currentStudy == null) {
			currentStudy = new StudyBean();
		}

		List<DiscrepancyNoteBean> childDiscBeans = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteThread tempDNThread;
		int resolutionStatusId;

		for (DiscrepancyNoteBean discBean : allDiscNotes) {
			tempDNThread = new DiscrepancyNoteThread();
			tempDNThread.setStudyId(currentStudy.getId());

			tempDNThread.getLinkedNoteList().addFirst(discBean);
			// childDiscBeans should be empty here
			if (!childDiscBeans.isEmpty()) {
				childDiscBeans.clear();
			}
			childDiscBeans = discBean.getChildren();
			Collections.sort(childDiscBeans);

			resolutionStatusId = discBean.getResolutionStatusId();
			// the thread's status id is the parent's in this case, when there
			// are no children
			tempDNThread.setLatestResolutionStatus(this.getResolutionStatusName(resolutionStatusId));

			if (!childDiscBeans.isEmpty()) {
				for (DiscrepancyNoteBean childBean : childDiscBeans) {
					tempDNThread.getLinkedNoteList().offer(childBean);
				}
			}
			dnThreads.add(tempDNThread);
		}
		return dnThreads;
	}

	/**
	 * Get all notes for subject and event.
	 * @param studySubjectBean StudySubjectBean
	 * @param currentStudy StudyBean
	 * @param sm SessionManager
	 * @return List<DiscrepancyNoteBean>
	 */
	public static List<DiscrepancyNoteBean> getAllNotesforSubjectAndEvent(StudySubjectBean studySubjectBean,
																		  StudyBean currentStudy, SessionManager sm) {
		int studyId = studySubjectBean.getStudyId();
		StudyDAO studydao = new StudyDAO(sm.getDataSource());
		StudyBean study = (StudyBean) studydao.findByPK(studyId);
		// If the study subject derives from a site, and is being viewed from a
		// parent study,
		// then the study IDs will be different. However, since each note is
		// saved with the specific
		// study ID, then its study ID may be different than the study subject's
		// ID.
		boolean subjectStudyIsCurrentStudy = studyId == currentStudy.getId();
		boolean isParentStudy = study.getParentStudyId() < 1;
		// Get any disc notes for this study event
		DiscrepancyNoteDAO discrepancyNoteDAO = new DiscrepancyNoteDAO(sm.getDataSource());
		ArrayList<DiscrepancyNoteBean> allNotesforSubjectAndEvent;
		// These methods return only parent disc notes
		if (subjectStudyIsCurrentStudy && isParentStudy) {
			allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudyAndId(currentStudy,
					studySubjectBean.getId());
		} else {
			if (!isParentStudy) {
				StudyBean stParent = (StudyBean) studydao.findByPK(study.getParentStudyId());
				allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(stParent, study,
						studySubjectBean.getId());
			} else {
				allNotesforSubjectAndEvent = discrepancyNoteDAO.findAllStudyEventByStudiesAndSubjectId(currentStudy,
						study, studySubjectBean.getId());
			}
		}
		return allNotesforSubjectAndEvent;
	}

	/**
	 * Get image file name for flag by resolution status id.
	 * @param resolutionStatusId int
	 * @return String
	 */
	public static String getImageFileNameForFlagByResolutionStatusId(int resolutionStatusId) {
		String result;
		switch (resolutionStatusId) {
			case NEW_ID:
				result = "icon_Note";
				break;
			case UPDATED_ID:
				result = "icon_flagYellow";
				break;
			case RESOLUTION_PROPOSED_ID:
				result = "icon_flagBlack";
				break;
			case CLOSED_ID:
				result = "icon_flagGreen";
				break;
			case NA_ID:
				result = "icon_flagWhite";
				break;
			default:
				result = "icon_noNote";
				break;
		}
		return result;
	}

	/**
	 * Get DN resolution status.
	 * @param existingNotes List
	 * @return int
	 */
	public static int getDiscrepancyNoteResolutionStatus(List existingNotes) {
		int resolutionStatus = 0;
		boolean hasOtherThread = false;
		for (Object obj : existingNotes) {
			DiscrepancyNoteBean note = (DiscrepancyNoteBean) obj;
			/*
			 * We would only take the resolution status of the parent note of any note thread. If there are more than
			 * one note thread, the thread with the worst resolution status will be taken.
			 */
			if (note.getParentDnId() == 0) {
				if (hasOtherThread) {
					if (resolutionStatus > note.getResolutionStatusId()) {
						resolutionStatus = note.getResolutionStatusId();
					}
				} else {
					resolutionStatus = note.getResolutionStatusId();
				}
				hasOtherThread = true;
			}
		}
		return resolutionStatus;
	}

	/**
	 * Transform RFC for FVC.
	 * @param dn DiscrepancyNoteBean
	 * @param ub UserAccountBean
	 * @param resStatusId Integer
	 * @param dndao DiscrepancyNoteDAO
	 */
	public static void transformSavedRFCToFVC(DiscrepancyNoteBean dn, UserAccountBean ub,
											  Integer resStatusId, DiscrepancyNoteDAO dndao) {
		transformSavedDNTo(dn, ub, "", "", DiscrepancyNoteType.REASON_FOR_CHANGE.getId(),
				DiscrepancyNoteType.FAILEDVAL.getId(), resStatusId, dndao);
	}

	/**
	 * Transform Annotation to FVC.
	 * @param dn DiscrepancyNoteBean
	 * @param ub UserAccountBean
	 * @param resStatusId Integer
	 * @param dndao DiscrepancyNoteDAO
	 */
	public static void transformSavedAnnotationToFVC(DiscrepancyNoteBean dn, UserAccountBean ub,
													 Integer resStatusId, DiscrepancyNoteDAO dndao) {
		transformSavedDNTo(dn, ub, "", "", DiscrepancyNoteType.ANNOTATION.getId(),
				DiscrepancyNoteType.FAILEDVAL.getId(), resStatusId, dndao);
	}

	private static void transformSavedDNTo(DiscrepancyNoteBean dn, UserAccountBean ub, String description,
										   String detailedNotes, Integer oldTypeId, Integer typeId, Integer resStatusId, DiscrepancyNoteDAO dndao) {
		if (oldTypeId != dn.getDiscrepancyNoteTypeId()) {
			return;
		}
		if (ub != null) {
			dn.setAssignedUserId(ub.getId());
		}
		if (!StringUtil.isBlank(description)) {
			dn.setDescription(description);
		}
		if (!StringUtil.isBlank(detailedNotes)) {
			dn.setDetailedNotes(detailedNotes);
		}
		if (typeId != null && typeId != 0) {
			dn.setDiscrepancyNoteTypeId(typeId);
		}
		if (resStatusId != null && resStatusId != 0) {
			dn.setResolutionStatusId(resStatusId);
		}

		DiscrepancyNoteBean parentDN = null;
		if (dn.getParentDnId() > 0) {
			parentDN = (DiscrepancyNoteBean) dndao.findByPK(dn.getParentDnId());
			if (typeId != null && typeId != 0) {
				parentDN.setDiscrepancyNoteTypeId(typeId);
			}
			if (resStatusId != null && resStatusId != 0) {
				parentDN.setResolutionStatusId(resStatusId);
			}
		}
		dndao.update(dn);
		dndao.update(parentDN);
	}

	/**
	 * Transform Annotation to FVC.
	 * @param dn DiscrepancyNoteBean
	 * @param ub UserAccountBean
	 * @param resStatusId Integer
	 */
	public static void transformAnnotationToFVC(DiscrepancyNoteBean dn, UserAccountBean ub, Integer resStatusId) {
		transformDNTo(dn, ub, "", "", DiscrepancyNoteType.ANNOTATION.getId(), DiscrepancyNoteType.FAILEDVAL.getId(),
				resStatusId);
	}

	private static DiscrepancyNoteBean transformDNTo(DiscrepancyNoteBean dn, UserAccountBean ub, String description,
													 String detailedNotes, Integer oldTypeId, Integer typeId, Integer resStatusId) {
		if (oldTypeId != dn.getDiscrepancyNoteTypeId()) {
			return dn;
		}
		if (ub != null) {
			dn.setAssignedUserId(ub.getId());
		}
		if (!StringUtil.isBlank(description)) {
			dn.setDescription(description);
		}
		if (!StringUtil.isBlank(detailedNotes)) {
			dn.setDetailedNotes(detailedNotes);
		}
		if (typeId != null && typeId != 0) {
			dn.setDiscrepancyNoteTypeId(typeId);
		}
		if (resStatusId != null && resStatusId != 0) {
			dn.setResolutionStatusId(resStatusId);
		}
		return dn;
	}

	/**
	 * Generate user account.
	 * @param studySubjectId int
	 * @param currentStudy StudyBean
	 * @param udao UserAccountDAO
	 * @param studyDAO StudyDAO
	 * @param ecb EventCRFBean
	 * @param eddao EventDefinitionCRFDAO
	 * @return static ArrayList<StudyUserRoleBean>s
	 */
	public static ArrayList<StudyUserRoleBean> generateUserAccounts(int studySubjectId, StudyBean currentStudy,
																	UserAccountDAO udao, StudyDAO studyDAO, EventCRFBean ecb, EventDefinitionCRFDAO eddao) {
		StudyBean subjectStudy = studyDAO.findByStudySubjectId(studySubjectId);
		int studyId = currentStudy.getId();
		ArrayList<StudyUserRoleBean> surbs;
		ArrayList<StudyUserRoleBean> userBeans = new ArrayList();
		if (currentStudy.getParentStudyId() > 0) {
			surbs = udao.findAllAvailableOrLockedOrRemovedUsersByStudyOrSite(studyId, currentStudy.getParentStudyId(), studySubjectId);
		} else if (subjectStudy.getParentStudyId() > 0) {
			surbs = udao.findAllAvailableOrLockedOrRemovedUsersByStudyOrSite(subjectStudy.getId(), subjectStudy.getParentStudyId(), studySubjectId);
		} else {
			surbs = udao.findAllAvailableOrLockedOrRemovedUsersByStudyOrSite(studyId, 0, studySubjectId);
		}
		
		for (StudyUserRoleBean surb : surbs){
			if (surb.getStatus().isAvailable() || ( ecb != null && surb.getUserAccountId() == ecb.getOwnerId())) {
				userBeans.add(surb);
			}
		}
		
		UserAccountBean rootUserAccount = (UserAccountBean) udao.findByPK(1);
		if (!rootUserAccount.getStatus().isLocked() && !rootUserAccount.getStatus().isDeleted()) {
			StudyUserRoleBean rootStudyUserRole = createRootUserRole(rootUserAccount, studyId);
			userBeans.add(rootStudyUserRole);
		}
		removeEvaluatorsBasedOnCrfStage(userBeans, ecb, eddao, subjectStudy);
		return userBeans;
	}

	private static void removeEvaluatorsBasedOnCrfStage(List<StudyUserRoleBean> userAccounts, EventCRFBean ecb,
														EventDefinitionCRFDAO eddao, StudyBean subjectStudy) {
		if (!shouldRemoveEvaluators(ecb, eddao, subjectStudy)) {
			return;
		}
		List<StudyUserRoleBean> evaluators = new ArrayList<StudyUserRoleBean>();
		for (StudyUserRoleBean surb : userAccounts) {
			if (surb.getRole().equals(Role.STUDY_EVALUATOR)) {
				evaluators.add(surb);
			}
		}
		userAccounts.removeAll(evaluators);
	}

	private static boolean shouldRemoveEvaluators(EventCRFBean ecb, EventDefinitionCRFDAO eddao, StudyBean subjectStudy) {
		if (ecb == null) {
			return true;
		}
		EventDefinitionCRFBean edcb;
		if (!subjectStudy.isSite()) {
			edcb = eddao.findForStudyByStudyEventIdAndCRFVersionId(ecb.getStudyEventId(),
					ecb.getCRFVersionId());
		} else {
			edcb = eddao.findForSiteByEventCrfId(ecb.getId());
			if (edcb == null) {
				edcb = eddao.findForStudyByStudyEventIdAndCRFVersionId(ecb.getStudyEventId(),
						ecb.getCRFVersionId());
			}
		}
		return !edcb.isEvaluatedCRF() || ecb.getStage().equals(DataEntryStage.INITIAL_DATA_ENTRY) || ecb.isNotStarted();
	}

	private static StudyUserRoleBean createRootUserRole(UserAccountBean rootUserAccount, int studyId) {
		StudyUserRoleBean rootUserRole = rootUserAccount.getSysAdminRole();

		rootUserRole.setUserAccountId(rootUserAccount.getId());
		rootUserRole.setRole(Role.SYSTEM_ADMINISTRATOR);
		rootUserRole.setStatus(rootUserAccount.getStatus());
		rootUserRole.setFirstName(rootUserAccount.getFirstName());
		rootUserRole.setLastName(rootUserAccount.getLastName());
		rootUserRole.setCreatedDate(rootUserAccount.getCreatedDate());
		rootUserRole.setUserName(rootUserAccount.getName());
		rootUserRole.setName(rootUserAccount.getName());
		rootUserRole.setStudyId(studyId);

		return rootUserRole;
	}

	/**
	 * Get Study Subject.
	 * @param subjectId int
	 * @param currentStudy StudyBean
	 * @param dataSource DataSource
	 * @return StudySubjectBean
	 */
	public static StudySubjectBean getStudySubject(int subjectId, StudyBean currentStudy, DataSource dataSource) {
		StudySubjectBean ssub = new StudySubjectBean();
		if (subjectId <= 0) {
			return ssub;
		}
		StudySubjectDAO ssdao = new StudySubjectDAO(dataSource);
		StudyDAO sdao = new StudyDAO(dataSource);
		ssub = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);
		if (ssub.getId() <= 0 && currentStudy.getParentStudyId() > 0) {
			ssub = ssdao.findBySubjectIdAndStudy(subjectId,
					(StudyBean) sdao.findByPK(currentStudy.getParentStudyId()));
		}
		return ssub;
	}

	/**
	 * Prepare Repeating Info Map.
	 * @param name String
	 * @param entityId int
	 * @param iddao ItemDataDAO
	 * @param ecdao EventCRFDAO
	 * @param sedao StudyEventDAO
	 * @param igmdao ItemGroupMetadataDAO
	 * @param seddao StudyEventDefinitionDAO
	 * @return Map <String, String>
	 */
	public static Map<String, String> prepareRepeatingInfoMap(String name, int entityId, ItemDataDAO iddao,
															  EventCRFDAO ecdao, StudyEventDAO sedao, ItemGroupMetadataDAO igmdao, StudyEventDefinitionDAO seddao) {
		Map<String, String> repeatingInfoMap = new HashMap<String, String>();
		try {
			if (name.equalsIgnoreCase("eventcrf")) {
				EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(entityId);
				StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
						.getStudyEventDefinitionId());
				if (sedb.isRepeating()) {
					repeatingInfoMap.put("studyEventOrdinal", "" + seb.getSampleOrdinal());
				}
			} else if (name.equalsIgnoreCase("studyevent")) {
				StudyEventBean seb = (StudyEventBean) sedao.findByPK(entityId);
				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
						.getStudyEventDefinitionId());
				if (sedb.isRepeating()) {
					repeatingInfoMap.put("studyEventOrdinal", "" + seb.getSampleOrdinal());
				}
			} else if (name.equalsIgnoreCase("itemdata")) {
				ItemDataBean idb = (ItemDataBean) iddao.findByPK(entityId);
				EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(idb.getEventCRFId());
				StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
				StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(seb
						.getStudyEventDefinitionId());
				ItemGroupMetadataBean igmb = (ItemGroupMetadataBean) igmdao.findByItemAndCrfVersion(idb.getItemId(),
						ecb.getCRFVersionId());
				if (sedb.isRepeating()) {
					repeatingInfoMap.put("studyEventOrdinal", "" + seb.getSampleOrdinal());
				}
				if (igmb.isRepeatingGroup()) {
					repeatingInfoMap.put("itemDataOrdinal", "" + idb.getOrdinal());
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return repeatingInfoMap;
	}
	
	/**
	 * Check if type of DN equals this type.
	 * @param dnb DiscrepancyNoteBean
	 * @param dnt DiscrepancyNoteType
	 * @return boolean
	 */
	public static boolean hasType(DiscrepancyNoteBean dnb, DiscrepancyNoteType dnt) {
		if (dnb.getDisType() != null && dnb.getDisType() == dnt) return true;
		if (dnb.getDiscrepancyNoteTypeId() == dnt.getId()) return true;
		return false;
	}
	
	/**
	 * Filter String to Boolean Map from Object to Boolean Map.
	 * @param map Map<Object, Boolean> 
	 * @return Map<String, Boolean>
	 */
	public static Map<String, Boolean> getStringToBooleanMap(Map<Object, Boolean> map) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		if (map == null || map.isEmpty()) return result;
		for (Object key : map.keySet()) {
			if (key instanceof String) {
				result.put((String)key, map.get(key));
			}
		}
		return result;
	}
}
