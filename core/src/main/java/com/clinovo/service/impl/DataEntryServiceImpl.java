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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.clinovo.enums.CurrentDataEntryStage;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.DataEntryService;

/**
 * Data Entry service.
 */
@Service("dataEntryService")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataEntryServiceImpl implements DataEntryService {

	public static final String INPUT_EVENT_CRF = "event";
	public static final String SECTION_BEAN = "section_bean";

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	@Autowired
	private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;

	private DynamicsMetadataService dynamicsMetadataService;

	/**
	 * {@inheritDoc}
	 */
	public DisplaySectionBean getDisplayBean(boolean hasGroup, boolean isSubmitted,
			Page servletPage, HttpServletRequest request) throws Exception {

		DisplaySectionBean section = new DisplaySectionBean();
		StudyBean study = (StudyBean) request.getSession().getAttribute("study");
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		EventDefinitionCRFBean edcb = getEventDefinitionCRFFromSubjectsSite(ecb);
		FormBeanUtil formBeanUtil = new FormBeanUtil();
		List<DisplayItemGroupBean> itemGroups = new ArrayList<DisplayItemGroupBean>();

		if (hasGroup) {
			DisplaySectionBean newDisplayBean;
			newDisplayBean = formBeanUtil.createDisplaySectionWithItemGroups(study, sb.getId(), ecb,
					ecb.getStudyEventId(), dataSource, edcb.getId(), getDynamicsMetadataService());
			itemGroups = newDisplayBean.getDisplayFormGroups();
			section.setDisplayFormGroups(itemGroups);
		}
		boolean hasUnGroupedItems = formBeanUtil.sectionHasUngroupedItems(dataSource, sb.getId(), itemGroups);
		SectionDAO sdao = new SectionDAO(dataSource);
		sb.setHasSCDItem(hasUnGroupedItems && sdao.hasSCDItem(sb.getId()));
		section.setEventCRF(ecb);

		if (sb.getParentId() > 0) {
			SectionBean parent = (SectionBean) sdao.findByPK(sb.getParentId());
			sb.setParent(parent);
		}
		section.setSection(sb);
		CRFVersionBean cvb = getCRFVersionFromEventCRF(ecb);
		section.setCrfVersion(cvb);
		CRFBean cb = getCrfFromCrfVersion(cvb);
		section.setCrf(cb);
		section.setEventDefinitionCRF(edcb);
		ArrayList displayItems = getParentDisplayItems(hasGroup, sb, edcb, ecb, hasUnGroupedItems,
				Page.isDDEServletPage(servletPage));
		Collections.sort(displayItems);

		for (int i = 0; i < displayItems.size(); i++) {
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
			dib.setChildren(getChildrenDisplayItems(dib, edcb, ecb, servletPage));

			if (ecb.getStage() == DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE
					|| ecb.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				if (shouldLoadDBValues(dib, servletPage) && !isSubmitted) {
					dib.loadDBValue();
				}
			} else {
				if (shouldLoadDBValues(dib, servletPage)) {
					dib.loadDBValue();
				}
			}
			displayItems.set(i, dib);
		}
		section.setItems(displayItems);
		return section;
	}

	/**
	 * Method that checks that DB data should be displayed.
	 * 
	 * @param dib DisplayItemBean
	 * @param servletPage Page
	 * @return boolean
	 */
	public boolean shouldLoadDBValues(DisplayItemBean dib, Page servletPage) {
		if (Page.DOUBLE_DATA_ENTRY_SERVLET.equals(servletPage)) {
			if (dib.getEventDefinitionCRF().isEvaluatedCRF()) {
				return true;
			}
			if (dib.getData().getStatus() != null && dib.getData().getStatus().equals(Status.PENDING)) {
				return false;
			}
		}
		return true;
	}

	public boolean shouldLoadDBValues(DisplayItemBean dib, CurrentDataEntryStage dataEntryStage) {
		if (dataEntryStage == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
			if (dib.getEventDefinitionCRF().isEvaluatedCRF()) {
				return true;
			}
			if (dib.getData().getStatus() != null && dib.getData().getStatus().equals(Status.PENDING)) {
				return false;
			}
		}
		return true;
	}

	private ArrayList getParentDisplayItems(boolean hasGroup, SectionBean sb, EventDefinitionCRFBean edcb,
			EventCRFBean ecb, boolean hasUngroupedItems, boolean isDDEPage) throws Exception {

		ItemDAO idao = new ItemDAO(dataSource);
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ArrayList answer = new ArrayList();
		HashMap displayItems = new HashMap();
		ArrayList items;
		ArrayList itemsUngrped = new ArrayList();
		if (hasGroup) {
			if (hasUngroupedItems) {
				itemsUngrped = idao.findAllUngroupedParentsBySectionId(sb.getId(), sb.getCRFVersionId());
			}
		}
		items = idao.findAllNonRepeatingParentsBySectionId(sb.getId());
		items.addAll(itemsUngrped);

		for (Object item : items) {
			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(edcb);
			ItemBean ib = (ItemBean) item;
			dib.setItem(ib);
			displayItems.put(dib.getItem().getId(), dib);
		}
		ArrayList data = iddao.findAllBySectionIdAndEventCRFId(sb.getId(), ecb.getId());

		for (Object aData : data) {
			ItemDataBean idb = (ItemDataBean) aData;
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(idb.getItemId()));
			if (dib != null) {
				dib.setData(idb);
				displayItems.put(idb.getItemId(), dib);
			}
		}
		ArrayList metadata = ifmdao.findAllBySectionId(sb.getId());

		for (Object aMetadata : metadata) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) aMetadata;
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(ifmb.getItemId()));
			if (dib != null) {
				boolean showItem = getDynamicsMetadataService().isShown(ifmb.getItemId(), ecb, dib.getData());
				if (isDDEPage) {
					showItem = getDynamicsMetadataService().hasPassedDDE(ifmb, ecb, dib.getData());
				}
				if (showItem) {
					ifmb.setShowItem(true);
				}
				DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsMetadataService()
						.getDynamicsItemFormMetadataBean(ifmb.getItemId(), ecb, dib.getData());
				if (dynamicsMetadataBean != null) {
					ifmb.setShowItem(dynamicsMetadataBean.isShowItem());
				}
				dib.setMetadata(ifmb);
				displayItems.put(ifmb.getItemId(), dib);
			}
		}
		for (Object o : displayItems.keySet()) {
			Integer key = (Integer) o;
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(key);
			answer.add(dib);
		}
		return answer;
	}

	private ArrayList getChildrenDisplayItems(DisplayItemBean parent, EventDefinitionCRFBean edcb, EventCRFBean ecb,
			Page servletPage) {
		boolean isDDEPage = Page.isDDEServletPage(servletPage);
		ArrayList answer = new ArrayList();
		int parentId = parent.getItem().getId();
		ItemDAO idao = new ItemDAO(dataSource);
		ArrayList childItemBeans = idao.findAllByParentIdAndCRFVersionId(parentId, ecb.getCRFVersionId());
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(dataSource);

		for (Object childItemBean : childItemBeans) {
			ItemBean child = (ItemBean) childItemBean;
			ItemDataBean data = iddao.findByItemIdAndEventCRFId(child.getId(), ecb.getId());
			ItemFormMetadataBean metadata = ifmdao.findByItemIdAndCRFVersionId(child.getId(), ecb.getCRFVersionId());

			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(edcb);
			dib.setItem(child);

			if (!isDDEPage) {
				dib.setData(data);
			}
			dib.setDbData(data);
			boolean showItem = getDynamicsMetadataService().isShown(metadata.getItemId(), ecb, data);

			if (isDDEPage) {
				showItem = getDynamicsMetadataService().hasPassedDDE(metadata, ecb, data);
			}
			if (showItem) {
				metadata.setShowItem(true);
			}
			dib.setMetadata(metadata);

			if (shouldLoadDBValues(dib, servletPage)) {
				dib.loadDBValue();
			}
			answer.add(dib);
		}
		Collections.sort(answer);
		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList getAllDisplayBeans(ArrayList<SectionBean> allSectionBeans, EventCRFBean ecb, StudyBean study,
			Page servletPage) throws Exception {
		ArrayList<DisplaySectionBean> sections = new ArrayList<DisplaySectionBean>();
		SectionDAO sdao = new SectionDAO(dataSource);

		for (SectionBean sb : allSectionBeans) {
			DisplaySectionBean section = new DisplaySectionBean();
			section.setEventCRF(ecb);

			if (sb.getParentId() > 0) {
				sb.setParent((SectionBean) sdao.findByPK(sb.getParentId()));
			}
			section.setSection(sb);
			CRFVersionBean cvb = getCRFVersionFromEventCRF(ecb);
			section.setCrfVersion(cvb);
			CRFBean cb = getCrfFromCrfVersion(cvb);
			section.setCrf(cb);

			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(dataSource);
			EventDefinitionCRFBean edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(),
					cvb.getId());
			section.setEventDefinitionCRF(edcb);

			ArrayList displayItems = getParentDisplayItems(false, sb, edcb, ecb, false,
					Page.isDDEServletPage(servletPage));
			Collections.sort(displayItems);

			for (int i = 0; i < displayItems.size(); i++) {
				DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
				dib.setChildren(getChildrenDisplayItems(dib, edcb, ecb, servletPage));
				if (shouldLoadDBValues(dib, servletPage)) {
					dib.loadDBValue();
				}
				displayItems.set(i, dib);
			}
			section.setItems(displayItems);
			sections.add(section);
		}
		return sections;
	}

	private DynamicsMetadataService getDynamicsMetadataService() {
		if (dynamicsMetadataService == null) {
			dynamicsMetadataService = new DynamicsMetadataService(dynamicsItemFormMetadataDao, dynamicsItemGroupMetadataDao, dataSource);
		}
		return dynamicsMetadataService;
	}

	private EventDefinitionCRFBean getEventDefinitionCRFFromSubjectsSite(EventCRFBean eventCRFBean) {
		StudyDAO studydao = new StudyDAO(dataSource);
		StudySubjectDAO subjectDAO = new StudySubjectDAO(dataSource);
		EventDefinitionCRFDAO definitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		StudySubjectBean subject = (StudySubjectBean) subjectDAO.findByPK(eventCRFBean.getStudySubjectId());
		StudyBean subjectStudy = (StudyBean) studydao.findByPK((subject).getStudyId());
		return definitionCRFDAO.findByStudyEventIdAndCRFVersionId(subjectStudy, eventCRFBean.getStudyEventId(), eventCRFBean.getCRFVersionId());
	}

	private CRFVersionBean getCRFVersionFromEventCRF(EventCRFBean eventCRFBean) {
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		return  (CRFVersionBean) crfVersionDAO.findByPK(eventCRFBean.getCRFVersionId());
	}

	private CRFBean getCrfFromCrfVersion(CRFVersionBean crfVersionBean) {
		CRFDAO crfdao = new CRFDAO(dataSource);
		return  (CRFBean) crfdao.findByPK(crfVersionBean.getCrfId());
	}
}
