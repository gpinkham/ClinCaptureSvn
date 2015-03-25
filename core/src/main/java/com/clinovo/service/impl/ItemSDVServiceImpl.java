/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.ItemSDVService;
import com.clinovo.util.CrfShortcutsAnalyzer;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * Item SDV Service implementation.
 *
 * @author Sergey Kirpichenok
 *
 */
@Service
@SuppressWarnings({"unchecked"})
public class ItemSDVServiceImpl implements ItemSDVService {

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public void processChangedCrfVersionMetadata(StudyBean currentStudy, UserAccountBean userAccountBean,
			int crfVersionId, Map<Integer, Boolean> metadata) throws Exception {
		EventCRFDAO eventCrfDao = new EventCRFDAO(dataSource);
		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		StudyEventDAO studyEventDao = new StudyEventDAO(dataSource);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		ItemFormMetadataDAO itemFormMetadataDao = new ItemFormMetadataDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);
		boolean dataChanged = false;
		boolean unSdvEventCrfBeans = false;
		for (Integer itemFormMetaId : metadata.keySet()) {
			boolean sdvRequired = metadata.get(itemFormMetaId);
			ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) itemFormMetadataDao
					.findByPK(itemFormMetaId);
			if (itemFormMetadataBean.isShowItem() && itemFormMetadataBean.isSdvRequired() != sdvRequired) {
				dataChanged = true;
				if (!itemFormMetadataBean.isSdvRequired()) {
					unSdvEventCrfBeans = true;
				}
			}
			itemFormMetadataBean.setSdvRequired(sdvRequired);
			itemFormMetadataDao.update(itemFormMetadataBean);
		}
		if (dataChanged) {
			eventDefinitionCrfDao.updateEDCThatHasItemsToSDV(crfVersionId, SourceDataVerification.PARTIALREQUIRED);
			itemDataDao.unsdvItemDataWhenCRFMetadataWasChanged(crfVersionId);
			if (unSdvEventCrfBeans) {
				eventCrfDao.unsdvEventCRFsWhenCRFMetadataWasChanged(crfVersionId, userAccountBean.getId());
			} else {
				eventCrfDao.sdvEventCRFsWhenCRFMetadataWasChangedAndAllItemsAreSDV(crfVersionId,
						userAccountBean.getId(), currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries()
								.equalsIgnoreCase(YES));
			}
			SubjectEventStatusUtil.determineSubjectEventStates(studyEventDao
					.findStudyEventsByCrfVersionAndSubjectEventStatus(crfVersionId, unSdvEventCrfBeans
							? SubjectEventStatus.SOURCE_DATA_VERIFIED
							: SubjectEventStatus.COMPLETED), userAccountBean, new DAOWrapper(new StudyDAO(dataSource),
					crfVersionDao, studyEventDao, new StudySubjectDAO(dataSource), eventCrfDao, eventDefinitionCrfDao,
					new DiscrepancyNoteDAO(dataSource)), null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String sdvItem(int itemDataId, int sectionId, int eventDefinitionCrfId, String action,
			UserAccountBean userAccountBean, CrfShortcutsAnalyzer crfShortcutsAnalyzer) throws Exception {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Map<String, Integer> deltaMap = new HashMap<String, Integer>();

		SectionDAO sectionDao = new SectionDAO(dataSource);
		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		EventCRFDAO eventCrfDao = new EventCRFDAO(dataSource);
		StudyEventDAO studyEventDao = new StudyEventDAO(dataSource);

		ItemDataBean itemDataBean = (ItemDataBean) itemDataDao.findByPK(itemDataId);
		List<SectionBean> sections = sectionDao.findAllByCRFVersionId(itemDataBean.getEventCRFId());
		EventCRFBean eventCrfBean = (EventCRFBean) eventCrfDao.findByPK(itemDataBean.getEventCRFId());

		itemDataBean.setSdv(action.equalsIgnoreCase(SDV));
		itemDataBean.setUpdater(userAccountBean);
		itemDataBean.setUpdatedDate(new Date());
		itemDataDao.update(itemDataBean);

		for (DisplayItemBean dib : itemDataDao.getListOfItemsToSDV(eventCrfBean.getId())) {
			crfShortcutsAnalyzer.prepareItemsToSDVShortcutLink(dib, eventCrfBean, eventDefinitionCrfId, sections,
					deltaMap);
			if (sectionId == dib.getMetadata().getSectionId()) {
				boolean repeating = dib.getGroupMetadata().isRepeatingGroup();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(ITEM_ID, dib.getData().getItemId());
				jsonObj.put(ITEM_DATA_ID, dib.getData().getId());
				jsonObj.put(ROW_COUNT, repeating ? dib.getData().getOrdinal() - 1 : "");
				jsonArray.put(jsonObj);
			}
		}

		String crfState = action.equalsIgnoreCase(SDV) && crfShortcutsAnalyzer.getTotalItemsToSDV() == 0
				? SDV
				: (action.equalsIgnoreCase(UN_SDV) && eventCrfBean.isSdvStatus() ? COMPLETED : "");
		if (!crfState.isEmpty()) {
			eventCrfBean.setSdvStatus(action.equalsIgnoreCase(SDV));
			eventCrfBean.setUpdater(userAccountBean);
			eventCrfBean.setUpdatedDate(new Date());
			eventCrfDao.update(eventCrfBean);

			StudyEventBean studyEventBean = (StudyEventBean) studyEventDao.findByPK(eventCrfBean.getStudyEventId());
			SubjectEventStatusUtil.determineSubjectEventState(studyEventBean, new DAOWrapper(new StudyDAO(dataSource),
					new CRFVersionDAO(dataSource), studyEventDao, new StudySubjectDAO(dataSource), eventCrfDao,
					new EventDefinitionCRFDAO(dataSource), new DiscrepancyNoteDAO(dataSource)));
			studyEventBean.setUpdater(userAccountBean);
			studyEventBean.setUpdatedDate(new Date());
			studyEventDao.update(studyEventBean);
		}

		jsonObject.put(ITEM, SDV);
		jsonObject.put(CRF, crfState);
		jsonObject.put(TOTAL_ITEMS_TO_SDV, Integer.toString(crfShortcutsAnalyzer.getTotalItemsToSDV()));
		jsonObject.put(TOTAL_SECTION_ITEMS_TO_SDV, Integer.toString(crfShortcutsAnalyzer.getSectionTotalItemsToSDV()));
		jsonObject.put(ITEM_DATA_ITEMS, jsonArray);

		return jsonObject.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetSDVForItems(ArrayList<DisplayItemBean> changedItemsList, UserAccountBean userAccountBean) {
		ItemDataDAO itemDataDao = new ItemDataDAO(dataSource);
		List<Integer> itemDataList = new ArrayList<Integer>();
		for (DisplayItemBean dib : changedItemsList) {
			if (dib.getData() != null && dib.getData().getId() > 0 && dib.getData().isSdv()) {
				itemDataList.add(dib.getData().getId());
			}
		}
		if (itemDataList.size() > 0) {
			itemDataDao.sdvItems(itemDataList, userAccountBean.getId(), false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DisplayItemBean> getListOfItemsToSDV(int eventCrfId) {
		return new ItemDataDAO(dataSource).getListOfItemsToSDV(eventCrfId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv) {
		return new ItemDataDAO(dataSource).sdvCrfItems(eventCrfId, userId, sdv);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean sdvCrfItems(int eventCrfId, int userId, boolean sdv, Connection con) {
		return new ItemDataDAO(dataSource).sdvCrfItems(eventCrfId, userId, sdv, con);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCountOfItemsToSDV(int eventCrfId) {
		return new ItemDataDAO(dataSource).getCountOfItemsToSDV(eventCrfId);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasItemsToSDV(int crfId) {
		return new ItemFormMetadataDAO(dataSource).hasItemsToSDV(crfId);
	}
}
