package org.akaza.openclinica.service.extract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.clinovo.service.CRFMaskingService;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.odmbeans.AuditLogBean;
import org.akaza.openclinica.bean.odmbeans.AuditLogsBean;
import org.akaza.openclinica.bean.odmbeans.ChildNoteBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.odmbeans.DiscrepancyNotesBean;
import org.akaza.openclinica.bean.odmbeans.ElementRefBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportFormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportStudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ExportSubjectDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectGroupDataBean;
import org.akaza.openclinica.dao.hibernate.AuditLogEventDao;
import org.akaza.openclinica.dao.hibernate.StudyDao;
import org.akaza.openclinica.dao.hibernate.StudyEventDefinitionDao;
import org.akaza.openclinica.dao.hibernate.StudySubjectDao;
import org.akaza.openclinica.dao.hibernate.UserAccountDao;
import org.akaza.openclinica.domain.EventCRFStatus;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.datamap.AuditLogEvent;
import org.akaza.openclinica.domain.datamap.CrfBean;
import org.akaza.openclinica.domain.datamap.DiscrepancyNote;
import org.akaza.openclinica.domain.datamap.DnEventCrfMap;
import org.akaza.openclinica.domain.datamap.DnItemDataMap;
import org.akaza.openclinica.domain.datamap.DnStudyEventMap;
import org.akaza.openclinica.domain.datamap.DnStudySubjectMap;
import org.akaza.openclinica.domain.datamap.DnSubjectMap;
import org.akaza.openclinica.domain.datamap.EventCrf;
import org.akaza.openclinica.domain.datamap.EventDefinitionCrf;
import org.akaza.openclinica.domain.datamap.ItemData;
import org.akaza.openclinica.domain.datamap.ItemGroupMetadata;
import org.akaza.openclinica.domain.datamap.Study;
import org.akaza.openclinica.domain.datamap.StudyEvent;
import org.akaza.openclinica.domain.datamap.StudyEventDefinition;
import org.akaza.openclinica.domain.datamap.StudySubject;
import org.akaza.openclinica.domain.datamap.SubjectEventStatus;
import org.akaza.openclinica.domain.datamap.SubjectGroupMap;
import org.akaza.openclinica.domain.user.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generate CDISC-ODM clinical data without data set.
 */
@Transactional
@Service("generateClinicalDataService")
public class GenerateClinicalDataServiceImpl implements GenerateClinicalDataService {

	protected static final Logger LOGGER = LoggerFactory.getLogger("org.akaza.openclinica.service.extract.GenerateClinicalDataServiceImpl");
	protected static final String DELIMITER = ",";
	private static final String GROUPOID_ORDINAL_DELIM = ":";
	private static final String INDICATE_ALL = "*";
	private static final String OPEN_ORDINAL_DELIMITER = "[";
	private static final String CLOSE_ORDINAL_DELIMITER = "]";
	private static final Object STATUS = "Status";
	private static final Object STUDY_EVENT = "study_event";
	private static final Object SUBJECT_GROUP_MAP = "subject_group_map";
	private static boolean isActiveRoleAtSite = true;
	private static final int DEFAULT_USER_ID = 0;

	@Autowired
	private StudyDao studyDao;

	@Autowired
	private StudySubjectDao studySubjectDao;

	@Autowired
	private StudyEventDefinitionDao studyEventDefDao;

	@Autowired
	private CRFMaskingService maskingService;

	@Autowired
	private UserAccountDao userAccountDao;

	@Autowired
	private AuditLogEventDao auditEventDAO;

	private boolean collectDns = true;
	private boolean collectAudits = true;
	private Locale locale;

	/**
	 * Get clinical data in ODM format.
	 *
	 * @param studyOID OID of the study.
	 * @return LinkedHashMap of String and OdmClinicalDataBean
	 */
	public LinkedHashMap<String, OdmClinicalDataBean> getClinicalData(String studyOID) {
		LinkedHashMap<String, OdmClinicalDataBean> hm = new LinkedHashMap<String, OdmClinicalDataBean>();
		Study study = new Study();
		study.setOcOid(studyOID);
		study = getStudyDao().findByColumnName(studyOID, "ocOid");
		List<StudySubject> studySubjs = study.getStudySubjects();
		if (study.getStudies().size() < 1) {
			hm.put(studyOID, constructClinicalData(study, studySubjs, DEFAULT_USER_ID));
		} else {
			hm.put(studyOID, constructClinicalData(study, studySubjs, DEFAULT_USER_ID));
			for (Study s : study.getStudies()) {
				hm.put(s.getOcOid(), constructClinicalData(s, s.getStudySubjects(), DEFAULT_USER_ID));
			}
		}

		return hm;
	}

	private List<StudySubject> listStudySubjects(String studySubjectOID) {
		ArrayList<StudySubject> studySubjs = new ArrayList<StudySubject>();
		StudySubject studySubj = studySubjectDao.findByColumnName(studySubjectOID, "ocOid");

		studySubjs.add(studySubj);
		return studySubjs;
	}


	/**
	 * Get clinical data for a specific user (without masked CRFs).
	 *
	 * @param studyOID        String
	 * @param studySubjectOID String
	 * @param userId          UserAccountBean or null
	 * @return OdmClinicalDataBean
	 */
	public OdmClinicalDataBean getClinicalData(String studyOID, String studySubjectOID, int userId) {
		Study study = getStudyDao().findByColumnName(studyOID, "ocOid");
		return constructClinicalData(study, listStudySubjects(studySubjectOID), userId);
	}

	public StudyDao getStudyDao() {
		return studyDao;
	}

	private OdmClinicalDataBean constructClinicalData(Study study, List<StudySubject> studySubjs, int userId) {
		return constructClinicalDataStudy(studySubjs, study, null, null, userId);
	}

	private OdmClinicalDataBean constructClinicalDataStudy(List<StudySubject> studySubjs, Study study, List<StudyEvent> studyEvents, String formVersionOID, int userId) {
		OdmClinicalDataBean odmClinicalDataBean = new OdmClinicalDataBean();
		ExportSubjectDataBean expSubjectBean;
		List<ExportSubjectDataBean> exportSubjDataBeanList = new ArrayList<ExportSubjectDataBean>();
		for (StudySubject studySubj : studySubjs) {
			if (studyEvents == null) {
				studyEvents = studySubjectDao.fetchListSEs(studySubj.getOcOid());
				if (userId != 0) {
					maskingService.removeMaskedCRFsFromStudyEvents(studyEvents, userId);
				}
				expSubjectBean = setExportSubjectDataBean(studySubj, study, studyEvents, formVersionOID);
			} else {
				expSubjectBean = setExportSubjectDataBean(studySubj, study, studyEvents, formVersionOID);
			}
			exportSubjDataBeanList.add(expSubjectBean);

			odmClinicalDataBean.setExportSubjectData(exportSubjDataBeanList);
			odmClinicalDataBean.setStudyOID(study.getOcOid());
		}
		return odmClinicalDataBean;
	}


	@SuppressWarnings("unchecked")
	private ExportSubjectDataBean setExportSubjectDataBean(
			StudySubject studySubj, Study study, List<StudyEvent> studyEvents, String formVersionOID) {
		ExportSubjectDataBean exportSubjectDataBean = new ExportSubjectDataBean();

		if (subjectBelongsToStudy(study, studySubj)) {
			if (studySubj.getSubject().getDateOfBirth() != null) {
				exportSubjectDataBean.setDateOfBirth(studySubj.getSubject().getDateOfBirth());
			}
			exportSubjectDataBean.setSubjectGender(studySubj.getSubject().getGender() + "");

			for (SubjectGroupMap subjGrpMap : studySubj.getSubjectGroupMaps()) {
				SubjectGroupDataBean subjGrpDataBean = new SubjectGroupDataBean();
				subjGrpDataBean.setStudyGroupClassId("SGC_" + subjGrpMap.getStudyGroupClass().getStudyGroupClassId());
				subjGrpDataBean.setStudyGroupClassName(subjGrpMap.getStudyGroup().getStudyGroupClass().getName());
				subjGrpDataBean.setStudyGroupName(subjGrpMap.getStudyGroup().getName());
				exportSubjectDataBean.getSubjectGroupData().add(subjGrpDataBean);
			}
			exportSubjectDataBean.setStudySubjectId(studySubj.getLabel());

			if (studySubj.getSubject().getUniqueIdentifier() != null) {
				exportSubjectDataBean.setUniqueIdentifier(studySubj.getSubject().getUniqueIdentifier());
			}
			exportSubjectDataBean.setSecondaryId(studySubj.getSecondaryLabel());
			exportSubjectDataBean.setStatus(studySubj.getStatus().toString());

			if (isCollectAudits()) {
				exportSubjectDataBean.setAuditLogs(fetchAuditLogs(studySubj.getStudySubjectId(), "study_subject", studySubj.getOcOid(), null));
			}
			AuditLogsBean subjectGroupMapLogs = fetchAuditLogs(studySubj.getStudySubjectId(), "subject_group_map", studySubj.getOcOid(), null);
			AuditLogsBean subjectLogs = fetchAuditLogs(studySubj.getSubject().getSubjectId(), "subject", studySubj.getOcOid(), null);
			exportSubjectDataBean.getAuditLogs().getAuditLogs().addAll(subjectGroupMapLogs.getAuditLogs());
			exportSubjectDataBean.getAuditLogs().getAuditLogs().addAll(subjectLogs.getAuditLogs());
			Collections.sort(exportSubjectDataBean.getAuditLogs().getAuditLogs());

			if (isCollectDns()) {
				exportSubjectDataBean.setDiscrepancyNotes(fetchDiscrepancyNotes(studySubj));
			}
			exportSubjectDataBean.setExportStudyEventData(setExportStudyEventDataBean(studySubj, studyEvents, formVersionOID));
			exportSubjectDataBean.setSubjectOID(studySubj.getOcOid());
			exportSubjectDataBean.setEnrollmentDate(studySubj.getEnrollmentDate());
		}
		return exportSubjectDataBean;
	}

	private boolean subjectBelongsToStudy(Study study, StudySubject studySubj) {
		boolean subjectBelongs = false;
		if (studySubj.getStudy().getOcOid().equals(study.getOcOid())) {
			subjectBelongs = true;
		} else {
			if (studySubj.getStudy().getStudy().getOcOid().equals(study.getOcOid())) {
				subjectBelongs = true;
			}
		}
		return subjectBelongs;
	}

	private ArrayList<ExportStudyEventDataBean> setExportStudyEventDataBean(
			StudySubject ss, List<StudyEvent> studyEvents, String formVersionOID) {
		ArrayList<ExportStudyEventDataBean> al = new ArrayList<ExportStudyEventDataBean>();

		for (StudyEvent se : studyEvents) {
			if (se != null) {
				ExportStudyEventDataBean expSEBean = new ExportStudyEventDataBean();
				expSEBean.setLocation(se.getLocation());

				expSEBean.setStartDate(se.getDateStart());
				expSEBean.setStartTimeFlag(se.getStartTimeFlag());
				expSEBean.setEndDate(se.getDateEnd());
				expSEBean.setEndTimeFlag(se.getEndTimeFlag());
				expSEBean.setStudyEventOID(se.getStudyEventDefinition().getOcOid());
				expSEBean.setStudyEventRepeatKey(se.getSampleOrdinal().toString());
				if (se.getStudySubject().getSubject().getDateOfBirth() != null && se.getDateStart() != null) {
					expSEBean.setAgeAtEvent(Utils.getAge(se.getStudySubject().getSubject().getDateOfBirth(), se.getDateStart()));
				}
				expSEBean.setStatus(fetchStudyEventStatus(se.getSubjectEventStatusId()));
				if (collectAudits) {
					expSEBean.setAuditLogs(fetchAuditLogs(se.getStudyEventId(), "study_event", se.getStudyEventDefinition().getOcOid(), null));
				}
				if (collectDns) {
					expSEBean.setDiscrepancyNotes(fetchDiscrepancyNotes(se));
				}
				expSEBean.setExportFormData(getFormDataForClinicalStudy(ss, se, formVersionOID));
				expSEBean.setStudyEventDefinition(se.getStudyEventDefinition());
				al.add(expSEBean);
			}
		}
		return al;
	}

	private ArrayList<ExportFormDataBean> getFormDataForClinicalStudy(
			StudySubject ss, StudyEvent se, String formVersionOID) {
		List<ExportFormDataBean> formDataBean = new ArrayList<ExportFormDataBean>();
		boolean formCheck = true;
		if (formVersionOID != null) {
			formCheck = false;
		}
		boolean hiddenCrfCheckPassed;
		List<CrfBean> hiddenCrfs = new ArrayList<CrfBean>();

		for (EventCrf ecrf : se.getEventCrfs()) {
			List<EventDefinitionCrf> seds = se.getStudyEventDefinition().getEventDefinitionCrfs();
			hiddenCrfCheckPassed = true;

			if (isActiveRoleAtSite) {
				Integer parentStudyId;
				if (ss.getStudy() != null) {
					parentStudyId = ss.getStudy().getStudy().getStudyId();
					hiddenCrfs = listOfHiddenCrfs(ss.getStudy().getStudyId(), parentStudyId, seds);
				}
				if (hiddenCrfs.contains(ecrf.getCrfVersion().getCrf())) {
					hiddenCrfCheckPassed = false;
				}
			}
			if (hiddenCrfCheckPassed) {
				if (!formCheck) {
					formCheck = ecrf.getCrfVersion().getOcOid().equals(formVersionOID);
				}
				if (formCheck) {
					ExportFormDataBean dataBean = new ExportFormDataBean();
					dataBean.setItemGroupData(fetchItemData(ecrf));
					dataBean.setFormOID(ecrf.getCrfVersion().getOcOid());
					dataBean.setShowStatus(true);
					if (ecrf.getDateInterviewed() != null) {
						dataBean.setInterviewDate(ecrf.getDateInterviewed());
					}
					if (ecrf.getInterviewerName() != null) {
						dataBean.setInterviewerName(ecrf.getInterviewerName());
					}
					dataBean.setStatus(fetchEventCRFStatus(ecrf));
					if (ecrf.getCrfVersion().getName() != null) {
						dataBean.setCrfVersion(ecrf.getCrfVersion().getName());
					}
					if (collectAudits) {
						dataBean.setAuditLogs(fetchAuditLogs(ecrf.getEventCrfId(), "event_crf", ecrf.getCrfVersion().getOcOid(), null));
					}
					if (collectDns) {
						dataBean.setDiscrepancyNotes(fetchDiscrepancyNotes(ecrf));
					}
					formDataBean.add(dataBean);
					if (formVersionOID != null) {
						formCheck = false;
					}
				}
			}
		}
		return (ArrayList<ExportFormDataBean>) formDataBean;
	}

	private List<CrfBean> listOfHiddenCrfs(Integer siteId, Integer parentStudyId, List<EventDefinitionCrf> seds) {
		List<CrfBean> hiddenCrfs = new ArrayList<CrfBean>();
		LOGGER.info("The study subject is at the site/study" + siteId);

		for (EventDefinitionCrf eventDefCrf : seds) {
			if (eventDefCrf.getHideCrf() && (eventDefCrf.getStudy().getStudyId() == siteId || eventDefCrf.getParentId().equals(siteId) || parentStudyId == eventDefCrf.getStudy().getStudyId() || parentStudyId.equals(eventDefCrf.getParentId()))) {
				hiddenCrfs.add(eventDefCrf.getCrf());
			}
		}
		return hiddenCrfs;
	}

	private String fetchEventCRFStatus(EventCrf ecrf) {
		String stage = null;
		Status status = ecrf.getStatus();

		if (ecrf.getEventCrfId() <= 0 || status.getCode() <= 0) {
			stage = EventCRFStatus.UNCOMPLETED.getI18nDescription(getLocale());
		}
		if (status.getCode().equals(EventCRFStatus.PARTIAL_DATA_ENTRY.getCode())) {
			stage = EventCRFStatus.PARTIAL_DATA_ENTRY.getI18nDescription(getLocale());
		}
		if (status.equals(Status.AVAILABLE)) {
			stage = EventCRFStatus.INITIAL_DATA_ENTRY.getI18nDescription(getLocale());
		}
		if (status.equals(Status.PENDING)) {
			if (ecrf.getValidatorId() != 0) {
				stage = EventCRFStatus.DOUBLE_DATA_ENTRY.getI18nDescription(getLocale());
			} else {
				stage = EventCRFStatus.INITIAL_DATA_ENTRY_COMPLETE.getI18nDescription(getLocale());
			}
		}
		if (status.equals(Status.UNAVAILABLE)) {
			stage = EventCRFStatus.DOUBLE_DATA_ENTRY_COMPLETE.getI18nDescription(getLocale());
		}
		if (status.equals(Status.LOCKED)) {
			stage = EventCRFStatus.LOCKED.getI18nDescription(getLocale());
		}
		if (status.equals(Status.DELETED)) {
			stage = EventCRFStatus.INVALID.getI18nDescription(getLocale());
		}
		if (status.equals(Status.AUTO_DELETED)) {
			stage = EventCRFStatus.INVALID.getI18nDescription(getLocale());
		}
		return stage;
	}

	private ArrayList<ImportItemGroupDataBean> fetchItemData(EventCrf eventCrf) {
		String groupOID, itemOID;
		String itemValue;
		String itemDataValue;
		HashMap<String, ArrayList<String>> oidMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, List<ItemData>> oidDNAuditMap = new HashMap<String, List<ItemData>>();
		List<ItemData> itds = eventCrf.getItemDatas();

		for (ItemData itemData : itds) {
			List<ItemGroupMetadata> igmetadatas = itemData.getItem().getItemGroupMetadatas();
			for (ItemGroupMetadata igGrpMetadata : igmetadatas) {
				groupOID = igGrpMetadata.getItemGroup().getOcOid();
				if (!oidMap.containsKey(groupOID)) {
					String groupOIDOrdnl;
					ArrayList<String> itemsValues;
					ArrayList<ItemData> itemDatas;
					List<ItemGroupMetadata> allItemsInAGroup = igGrpMetadata
							.getItemGroup().getItemGroupMetadatas();

					for (ItemGroupMetadata itemGrpMetada : allItemsInAGroup) {
						itemOID = itemGrpMetada.getItem().getOcOid();
						itemsValues = new ArrayList<String>();
						itemDataValue = fetchItemDataValue(itemData);
						itemDatas = new ArrayList<ItemData>();
						itemValue = itemOID + DELIMITER + itemDataValue;
						itemsValues.add(itemValue);
						groupOIDOrdnl = groupOID + GROUPOID_ORDINAL_DELIM
								+ itemData.getOrdinal();

						if (itemData.getItem().getOcOid().equals(itemOID)) {
							if (oidMap.containsKey(groupOIDOrdnl)) {
								ArrayList<String> itemgrps = oidMap
										.get(groupOIDOrdnl);
								List<ItemData> itemDataTemps = oidDNAuditMap.get(groupOIDOrdnl);
								if (!itemgrps.contains(itemValue)) {
									itemgrps.add(itemValue);
									oidMap.remove(groupOIDOrdnl);
									itemDataTemps.add(itemData);
									oidDNAuditMap.remove(groupOIDOrdnl);
								}
								oidMap.put(groupOIDOrdnl, itemgrps);
								oidDNAuditMap.put(groupOIDOrdnl, itemDataTemps);

							} else {
								oidMap.put(groupOIDOrdnl, itemsValues);
								itemDatas.add(itemData);
								oidDNAuditMap.put(groupOIDOrdnl, itemDatas);
							}
						}
					}
				}
			}
		}
		return populateImportItemGrpBean(oidMap, oidDNAuditMap);
	}

	private String fetchItemDataValue(ItemData itemData) {
		return itemData.getValue();

	}

	private ArrayList<ImportItemGroupDataBean> populateImportItemGrpBean(
			HashMap<String, ArrayList<String>> oidMap, HashMap<String, List<ItemData>> oidDNAuditMap) {
		Set<String> keysGrpOIDs = oidMap.keySet();
		ArrayList<ImportItemGroupDataBean> iigDataBean = new ArrayList<ImportItemGroupDataBean>();
		ImportItemGroupDataBean importItemGrpDataBean;
		new ImportItemGroupDataBean();
		for (String grpOID : keysGrpOIDs) {
			ArrayList<String> vals = oidMap.get(grpOID);
			importItemGrpDataBean = new ImportItemGroupDataBean();
			int groupIdx = grpOID.indexOf(GROUPOID_ORDINAL_DELIM);
			if (groupIdx != -1) {
				importItemGrpDataBean.setItemGroupOID(grpOID.substring(0,
						groupIdx));
				importItemGrpDataBean.setItemGroupRepeatKey(grpOID.substring(
						groupIdx + 1, grpOID.length()));
				ArrayList<ImportItemDataBean> iiDList = new ArrayList<ImportItemDataBean>();

				for (String value : vals) {
					ImportItemDataBean iiDataBean = new ImportItemDataBean();
					int index = value.indexOf(DELIMITER);
					if (!value.trim().equalsIgnoreCase(DELIMITER)) {
						iiDataBean.setItemOID(value.substring(0, index));
						iiDataBean.setValue(value.substring(index + 1, value.length()));
						if (isCollectAudits() || isCollectDns()) {
							iiDataBean = fetchItemDataAuditValue(oidDNAuditMap.get(grpOID), iiDataBean);
						}
						iiDList.add(iiDataBean);
					}
				}
				importItemGrpDataBean.setItemData(iiDList);
				iigDataBean.add(importItemGrpDataBean);
			}
		}
		return iigDataBean;
	}

	private ImportItemDataBean fetchItemDataAuditValue(List<ItemData> list,
													   ImportItemDataBean iiDataBean) {
		for (ItemData id : list) {
			if (id.getItem().getOcOid().equals(iiDataBean.getItemOID())) {
				if (isCollectAudits()) {
					iiDataBean.setAuditLogs(fetchAuditLogs(id.getItemDataId(), "item_data", iiDataBean.getItemOID(), null));
				}
				if (isCollectDns()) {
					iiDataBean.setDiscrepancyNotes(fetchDiscrepancyNotes(id));
				}
				return iiDataBean;
			}
		}
		return iiDataBean;
	}

	private DiscrepancyNotesBean fetchDiscrepancyNotes(ItemData itemData) {
		List<DnItemDataMap> dnItemDataMaps = itemData.getDnItemDataMaps();
		DiscrepancyNotesBean dnNotesBean = new DiscrepancyNotesBean();
		dnNotesBean.setEntityID(itemData.getItem().getOcOid());
		if (isCollectDns()) {
			ArrayList<DiscrepancyNoteBean> dnNotes = new ArrayList<DiscrepancyNoteBean>();
			for (DnItemDataMap dnItemDataMap : dnItemDataMaps) {
				DiscrepancyNote dn = dnItemDataMap.getDiscrepancyNote();
				fillDNObject(dnNotes, dn, null);
			}
			dnNotesBean.setAuditLogs(dnNotes);
		}
		return dnNotesBean;

	}

	private DiscrepancyNotesBean fetchDiscrepancyNotes(EventCrf eventCrf) {
		LOGGER.info("Fetching the discrepancy notes..");
		List<DnEventCrfMap> dnEventCrfMaps = eventCrf.getDnEventCrfMaps();
		DiscrepancyNotesBean dnNotesBean = new DiscrepancyNotesBean();
		dnNotesBean.setEntityID(eventCrf.getCrfVersion().getOcOid());
		ArrayList<DiscrepancyNoteBean> dnNotes = new ArrayList<DiscrepancyNoteBean>();
		for (DnEventCrfMap dnItemDataMap : dnEventCrfMaps) {
			DiscrepancyNote dn = dnItemDataMap.getDiscrepancyNote();
			fillDNObject(dnNotes, dn, dnItemDataMap.getDnEventCrfMapId().getColumnName());
		}
		dnNotesBean.setAuditLogs(dnNotes);
		return dnNotesBean;

	}

	private DiscrepancyNotesBean fetchDiscrepancyNotes(StudySubject studySubj) {
		List<DnStudySubjectMap> dnMaps = studySubj.getDnStudySubjectMaps();

		DiscrepancyNotesBean dnNotesBean = new DiscrepancyNotesBean();
		dnNotesBean.setEntityID(studySubj.getOcOid());
		ArrayList<DiscrepancyNoteBean> dnNotes = new ArrayList<DiscrepancyNoteBean>();

		for (DnStudySubjectMap dnMap : dnMaps) {
			DiscrepancyNote dn = dnMap.getDiscrepancyNote();
			fillDNObject(dnNotes, dn, dnMap.getDnStudySubjectMapId().getColumnName());
		}
		dnNotesBean.setAuditLogs(dnNotes);
		List<DnSubjectMap> dnSubjMaps = studySubj.getSubject().getDnSubjectMaps();
		ArrayList<DiscrepancyNoteBean> dnSubjs = new ArrayList<DiscrepancyNoteBean>();

		for (DnSubjectMap dnMap : dnSubjMaps) {
			DiscrepancyNote dn = dnMap.getDiscrepancyNote();
			fillDNObject(dnSubjs, dn, dnMap.getDnSubjectMapId().getColumnName());
		}

		for (DiscrepancyNoteBean dnSubjMap : dnSubjs) {
			dnNotesBean.getDiscrepancyNotes().add(dnSubjMap);
		}
		return dnNotesBean;
	}

	private DiscrepancyNotesBean fetchDiscrepancyNotes(StudyEvent studyEvent) {
		List<DnStudyEventMap> dnMaps = studyEvent.getDnStudyEventMaps();
		DiscrepancyNotesBean dnNotesBean = new DiscrepancyNotesBean();
		dnNotesBean.setEntityID(studyEvent.getStudyEventDefinition().getOcOid());
		ArrayList<DiscrepancyNoteBean> dnNotes = new ArrayList<DiscrepancyNoteBean>();
		for (DnStudyEventMap dnMap : dnMaps) {
			DiscrepancyNote dn = dnMap.getDiscrepancyNote();
			fillDNObject(dnNotes, dn, dnMap.getDnStudyEventMapId().getColumnName());
		}
		dnNotesBean.setAuditLogs(dnNotes);
		return dnNotesBean;
	}

	private void fillDNObject(ArrayList<DiscrepancyNoteBean> dnNotes,
							  DiscrepancyNote dn, String columnName) {

		if (dn.getParentDiscrepancyNote() == null) {
			DiscrepancyNoteBean dnNoteBean = new DiscrepancyNoteBean();
			dnNoteBean.setStatus(dn.getResolutionStatus().getName());
			dnNoteBean.setNoteType(dn.getEntityType());
			dnNoteBean.setOid("DN_" + dn.getDiscrepancyNoteId());
			dnNoteBean.setNoteType(dn.getDiscrepancyNoteType().getName());
			dnNoteBean.setDateUpdated(dn.getDateCreated());
			dnNoteBean.setEntityName(columnName);

			for (DiscrepancyNote childDN : dn.getChildDiscrepancyNotes()) {
				ChildNoteBean childNoteBean = new ChildNoteBean();
				childNoteBean.setOid("CDN_" + childDN.getDiscrepancyNoteId());
				ElementRefBean userRef = new ElementRefBean();
				childNoteBean.setDescription(childDN.getDescription());
				childNoteBean.setStatus(childDN.getResolutionStatus().getName());
				childNoteBean.setDetailedNote(childDN.getDetailedNotes());
				childNoteBean.setDateCreated(childDN.getDateCreated());

				if (childDN.getUserAccountByOwnerId() != null) {
					childNoteBean.setOwnerUserName(childDN.getUserAccountByOwnerId().getUserName());
					childNoteBean.setOwnerFirstName(childDN.getUserAccountByOwnerId().getFirstName());
					childNoteBean.setOwnerLastName(childDN.getUserAccountByOwnerId().getLastName());
				}
				if (childDN.getUserAccount() != null) {
					userRef.setElementDefOID("USR_" + childDN.getUserAccount().getUserId());
					userRef.setUserName(childDN.getUserAccount().getUserName());
					userRef.setFullName(childDN.getUserAccount().getFirstName() + " " + childDN.getUserAccount().getLastName());
				} else {
					userRef.setElementDefOID("");
					userRef.setUserName("");
					userRef.setFullName("");
				}
				childNoteBean.setUserRef(userRef);
				dnNoteBean.getChildNotes().add(childNoteBean);
			}
			dnNoteBean.setNumberOfChildNotes(dnNoteBean.getChildNotes().size());

			if (!dnNotes.contains(dnNoteBean)) {
				dnNotes.add(dnNoteBean);
			}
		}
	}

	private AuditLogsBean fetchAuditLogs(int entityID,
										 String itemDataAuditTable, String entityValue, String anotherAuditLog) {
		AuditLogsBean auditLogsBean = new AuditLogsBean();

		if (isCollectAudits()) {
			AuditLogEvent auditLog = new AuditLogEvent();
			auditLog.setEntityId(entityID);
			auditLog.setAuditTable(itemDataAuditTable);
			auditLogsBean.setEntityID(entityValue);
			ArrayList<AuditLogEvent> auditLogEvent = (auditEventDAO.findByParam(auditLog, anotherAuditLog));

			auditLogsBean = fetchODMAuditBean(auditLogEvent, auditLogsBean);
		}
		return auditLogsBean;
	}

	private AuditLogsBean fetchODMAuditBean(ArrayList<AuditLogEvent> auditLogEvents, AuditLogsBean auditLogsBean) {

		for (AuditLogEvent auditLogEvent : auditLogEvents) {
			AuditLogBean auditBean = new AuditLogBean();
			auditBean.setOid("AL_" + auditLogEvent.getAuditId());
			auditBean.setDatetimeStamp(auditLogEvent.getAuditDate());
			if (auditLogEvent.getEntityName() != null && auditLogEvent.getEntityName().equals(STATUS)) {
				if (auditLogEvent.getAuditTable().equals(STUDY_EVENT)) {
					auditBean.setNewValue(fetchStudyEventStatus(Integer.valueOf(auditLogEvent.getNewValue())));
					auditBean.setOldValue(fetchStudyEventStatus(Integer.valueOf(auditLogEvent.getOldValue())));
				} else if (auditLogEvent.getAuditTable().equals(SUBJECT_GROUP_MAP)) {
					auditBean.setNewValue(auditLogEvent.getNewValue());
					auditBean.setOldValue(auditLogEvent.getOldValue());
				} else {
					auditBean.setNewValue(Status.getByCode(Integer.valueOf(auditLogEvent.getNewValue())).getI18nDescription(getLocale()));
					auditBean.setOldValue(Status.getByCode(Integer.valueOf(auditLogEvent.getOldValue())).getI18nDescription(getLocale()));
				}
			} else {
				auditBean.setNewValue(auditLogEvent.getNewValue() == null ? "" : auditLogEvent.getNewValue());
				auditBean.setOldValue(auditLogEvent.getOldValue() == null ? "" : auditLogEvent.getOldValue());
			}
			auditBean.setReasonForChange(auditLogEvent.getReasonForChange() == null ? "" : auditLogEvent.getReasonForChange());
			String auditEventTypeName = auditLogEvent.getAuditLogEventType().getName();
			auditEventTypeName = auditEventTypeName.replace(' ', '_');
			auditEventTypeName = auditEventTypeName.substring(0, 1).toLowerCase() + auditEventTypeName.substring(1);
			auditLogEvent.getAuditLogEventType().setName(auditEventTypeName);
			auditBean.setType(auditLogEvent.getAuditLogEventType().getI18nName(locale));
			auditBean.setValueType(auditLogEvent.getEntityName() == null ? "" : auditLogEvent.getEntityName());

			if (auditLogEvent.getUserAccount() != null && auditLogEvent.getUserAccount().getUserId() != 0) {
				auditBean.setUserId("USR_" + auditLogEvent.getUserAccount().getUserId());
				auditBean.setUserName(auditLogEvent.getUserAccount().getUserName());
				auditBean.setName(auditLogEvent.getUserAccount().getFirstName() + " " + auditLogEvent.getUserAccount().getLastName());
			} else {
				auditBean.setUserId("");
				auditBean.setUserName("");
				auditBean.setName("");
			}
			auditLogsBean.getAuditLogs().add(auditBean);
		}
		return auditLogsBean;
	}

	private String fetchStudyEventStatus(Integer valueOf) {
		return SubjectEventStatus.getByCode(valueOf).getI18nDescription(getLocale());
	}

	/**
	 * This is a generic method where the control enters first. Regardless what URL is being used. Depending upon the combination of URL parameters, further course is determined.
	 *
	 * @param studyOID        String
	 * @param studySubjectOID String
	 * @param studyEventOID   String
	 * @param formVersionOID  String
	 * @param collectDNs      Boolean
	 * @param collectAudit    Boolean
	 * @param locale          Locale;
	 * @param userId          int
	 * @return LinkedHashMap <String, OdmClinicalDataBean>
	 */
	public LinkedHashMap<String, OdmClinicalDataBean> getClinicalData(String studyOID, String studySubjectOID,
																	  String studyEventOID, String formVersionOID,
																	  Boolean collectDNs, Boolean collectAudit, Locale locale, int userId) {
		setLocale(locale);
		setCollectDns(collectDNs);
		setCollectAudits(collectAudit);
		LinkedHashMap<String, OdmClinicalDataBean> clinicalDataHash = new LinkedHashMap<String, OdmClinicalDataBean>();
		UserAccount userAccount = userAccountDao.findByColumnName(userId, "userId");
		LOGGER.debug("Entering the URL with " + studyOID + ":" + studySubjectOID + ":" + studyEventOID + ":" + formVersionOID + ":DNS:" + collectDNs + ":Audits:" + collectAudit);
		LOGGER.info("Determining the generic paramters...");
		isActiveRoleAtSite = userAccount.getActiveStudy().getStudy() != null;

		if (!studySubjectOID.equals(INDICATE_ALL)) {
			StudySubject ss = studySubjectDao.findByColumnName(studySubjectOID, "ocOid");
			studyOID = ss.getStudy().getOcOid();
		}
		if (studyEventOID.equals(INDICATE_ALL) && formVersionOID.equals(INDICATE_ALL) && !studySubjectOID.equals(INDICATE_ALL) && !studyOID.equals(INDICATE_ALL)) {
			LOGGER.info("Adding all the study events,formevents as it is a *");
			LOGGER.info("study subject is not all and so is study");
			clinicalDataHash.put(studyOID, getClinicalData(studyOID, studySubjectOID, userId));
			return clinicalDataHash;
		} else if (studyEventOID.equals(INDICATE_ALL) && formVersionOID.equals(INDICATE_ALL) && studySubjectOID.equals(INDICATE_ALL) && !studyOID.equals(INDICATE_ALL)) {
			LOGGER.info("At the study level.. study event,study subject and forms are *");
			return getClinicalData(studyOID);
		} else if (!studyEventOID.equals(INDICATE_ALL) && !studySubjectOID.equals(INDICATE_ALL) && !studyOID.equals(INDICATE_ALL) && formVersionOID.equals(INDICATE_ALL)) {
			LOGGER.info("Obtaining the form version specific");
			clinicalDataHash.put(studyOID, getClinicalDatas(studyOID, studySubjectOID, studyEventOID, null));
			return clinicalDataHash;
		} else if (!studyEventOID.equals(INDICATE_ALL) && !studySubjectOID.equals(INDICATE_ALL) && !studyOID.equals(INDICATE_ALL) && !formVersionOID.equals(INDICATE_ALL)) {
			clinicalDataHash.put(studyOID, getClinicalDatas(studyOID, studySubjectOID, studyEventOID, formVersionOID));
			return clinicalDataHash;
		}

		return null;
	}

	private OdmClinicalDataBean getClinicalDatas(String studyOID,
												 String studySubjectOID, String studyEventOID, String formVersionOID) {
		int seOrdinal = 0;
		String temp = studyEventOID;
		List<StudyEvent> studyEvents;
		Study study = getStudyDao().findByColumnName(studyOID, "ocOid");
		List<StudySubject> ss = listStudySubjects(studySubjectOID);
		int idx = studyEventOID.indexOf(OPEN_ORDINAL_DELIMITER);
		LOGGER.info("study event oridinal is.." + idx);
		if (idx > 0) {
			studyEventOID = studyEventOID.substring(0, idx);
			seOrdinal = new Integer(temp.substring(idx + 1, temp.indexOf(CLOSE_ORDINAL_DELIMITER)));
		}
		StudyEventDefinition sed = studyEventDefDao.findByColumnName(studyEventOID, "ocOid");
		LOGGER.info("study event ordinal.." + seOrdinal);
		if (seOrdinal > 0) {
			studyEvents = fetchSE(seOrdinal, sed.getStudyEvents(), studySubjectOID);
		} else {
			studyEvents = fetchSE(sed.getStudyEvents(), studySubjectOID);
		}
		return constructClinicalDataStudy(ss, study, studyEvents, formVersionOID, DEFAULT_USER_ID);
	}


	private List<StudyEvent> fetchSE(int seOrdinal, List<StudyEvent> studyEvents, String ssOID) {
		List<StudyEvent> sEs = new ArrayList<StudyEvent>();
		LOGGER.debug("fetching all the study events");
		for (StudyEvent se : studyEvents) {
			if (se.getSampleOrdinal() == seOrdinal && se.getStudySubject().getOcOid().equals(ssOID)) {
				sEs.add(se);
			}
		}
		return sEs;
	}

	private List<StudyEvent> fetchSE(List<StudyEvent> studyEvents, String ssOID) {
		List<StudyEvent> sEs = new ArrayList<StudyEvent>();
		for (StudyEvent se : studyEvents) {
			if (se.getStudySubject().getOcOid().equals(ssOID)) {
				sEs.add(se);
			}
		}
		return sEs;
	}

	public boolean isCollectDns() {
		return collectDns;
	}

	public void setCollectDns(boolean collectDns) {
		this.collectDns = collectDns;
	}

	public boolean isCollectAudits() {
		return collectAudits;
	}

	public void setCollectAudits(boolean collectAudits) {
		this.collectAudits = collectAudits;
	}

	private void setLocale(Locale locale) {
		this.locale = locale;
	}

	private Locale getLocale() {
		return locale;
	}
}
