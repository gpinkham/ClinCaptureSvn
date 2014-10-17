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

import com.clinovo.service.DataEntryService;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
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
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.form.FormBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service("dataEntryService")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataEntryServiceImpl implements DataEntryService {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DynamicsMetadataService dynamicsMetadataService;

	public DisplaySectionBean getDisplayBean(boolean hasGroup, boolean includeUngroupedItems, boolean isSubmitted,
			Page servletPage, StudyBean study, EventCRFBean ecb, SectionBean sb) throws Exception {
		DisplaySectionBean section = new DisplaySectionBean();

		// Find out whether there are ungrouped items in this section
		boolean hasUngroupedItems = false;

		EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(),
				ecb.getCRFVersionId());
		int eventDefinitionCRFId = edcb.getId();

		logger.trace("eventDefinitionCRFId " + eventDefinitionCRFId);
		// Use this class to find out whether there are ungrouped items in this
		// section
		FormBeanUtil formBeanUtil = new FormBeanUtil();
		List<DisplayItemGroupBean> itemGroups = new ArrayList<DisplayItemGroupBean>();
		if (hasGroup) {
			DisplaySectionBean newDisplayBean = new DisplaySectionBean();
			if (includeUngroupedItems) {
				// Null values: this method adds null values to the
				// displayitembeans
				newDisplayBean = formBeanUtil.createDisplaySectionBWithFormGroups(sb.getId(), ecb.getCRFVersionId(),
						dataSource, eventDefinitionCRFId, ecb, dynamicsMetadataService);
			} else {
				newDisplayBean = formBeanUtil.createDisplaySectionWithItemGroups(study, sb.getId(), ecb,
						ecb.getStudyEventId(), dataSource, eventDefinitionCRFId, dynamicsMetadataService);
			}
			itemGroups = newDisplayBean.getDisplayFormGroups();
			logger.trace("found item group size: " + itemGroups.size() + " and to string: " + itemGroups.toString());
			section.setDisplayFormGroups(itemGroups);

		}

		// Find out whether any display items are *not* grouped; see issue 1689
		hasUngroupedItems = formBeanUtil.sectionHasUngroupedItems(dataSource, sb.getId(), itemGroups);

		SectionDAO sdao = new SectionDAO(dataSource);
		sb.setHasSCDItem(hasUngroupedItems ? sdao.hasSCDItem(sb.getId()) : false);

		section.setEventCRF(ecb);

		if (sb.getParentId() > 0) {
			SectionBean parent = (SectionBean) sdao.findByPK(sb.getParentId());
			sb.setParent(parent);
		}

		section.setSection(sb);

		CRFVersionDAO cvdao = new CRFVersionDAO(dataSource);
		CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
		section.setCrfVersion(cvb);

		CRFDAO cdao = new CRFDAO(dataSource);
		CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
		section.setCrf(cb);

		section.setEventDefinitionCRF(edcb);

		// setup DAO's here to avoid creating too many objects
		ItemDAO idao = new ItemDAO(dataSource);
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);

		// Use itemGroups to determine if there are any ungrouped items

		// get all the parent display item beans not in group
		logger.debug("Entering getParentDisplayItems::: Thread is? " + Thread.currentThread());
		ArrayList displayItems = getParentDisplayItems(hasGroup, sb, edcb, idao, ifmdao, iddao, ecb, hasUngroupedItems,
				Page.isDDEServletPage(servletPage));
		logger.debug("Entering getParentDisplayItems::: Done and Thread is? " + Thread.currentThread());

		logger.debug("just ran get parent display, has group " + hasGroup + " has ungrouped " + hasUngroupedItems);
		// now sort them by ordinal
		Collections.sort(displayItems);

		// now get the child DisplayItemBeans
		for (int i = 0; i < displayItems.size(); i++) {
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
			dib.setChildren(getChildrenDisplayItems(dib, edcb, ecb, servletPage));

			// TODO use the setData command here to make sure we get a value?
			// On Submition of the Admin Editing form the loadDBValue does not required
			//
			if (ecb.getStage() == DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE
					|| ecb.getStage() == DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE) {
				if (shouldLoadDBValues(dib, servletPage) && !isSubmitted) {
					dib.loadDBValue();
				}
			} else {
				if (shouldLoadDBValues(dib, servletPage)) {
					logger.trace("should load db values is true, set value");
					dib.loadDBValue();
					logger.trace("just got data loaded: " + dib.getData().getValue());
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
	 * @param dib
	 *            DisplayItemBean
	 * @param servletPage
	 *            Page
	 * @return boolean
	 */
	public boolean shouldLoadDBValues(DisplayItemBean dib, Page servletPage) {
		if (Page.ADMIN_EDIT_SERVLET.equals(servletPage)) {
			if (dib.getData().getStatus() == null) {
				return true;
			}
			if (!Status.UNAVAILABLE.equals(dib.getData().getStatus())) {
				return false;
			}
		}

		if (Page.DOUBLE_DATA_ENTRY_SERVLET.equals(servletPage)) {
			if (dib.getEventDefinitionCRF().isEvaluatedCRF()) {
				return true;
			}
			if (dib.getData().getStatus() == null || dib.getData().getStatus().equals(Status.UNAVAILABLE)) {
				return true;
			}
			if (dib.getData().getStatus().equals(Status.PENDING)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * For each single item in this section which is a parent, get a DisplayItemBean corresponding to that item. Note
	 * that an item is a parent iff its parentId == 0.
	 * 
	 * @param sb
	 *            The section whose items we are retrieving.
	 * @param hasUngroupedItems
	 *
	 * 
	 * @return An array of DisplayItemBean objects, one per parent item in the section. Note that there is no guarantee
	 *         on the ordering of the objects.
	 * @throws Exception
	 */
	private ArrayList getParentDisplayItems(boolean hasGroup, SectionBean sb, EventDefinitionCRFBean edcb,
			ItemDAO idao, ItemFormMetadataDAO ifmdao, ItemDataDAO iddao, EventCRFBean ecb, boolean hasUngroupedItems,
			boolean isDDEPage) throws Exception {
		ArrayList answer = new ArrayList();

		// DisplayItemBean objects are composed of an ItemBean, ItemDataBean and
		// ItemFormDataBean.
		// However the DAOs only provide methods to retrieve one type of bean at
		// a
		// time (per section)
		// the displayItems hashmap allows us to compose these beans into
		// DisplayItemBean objects,
		// while hitting the database only three times
		HashMap displayItems = new HashMap();

		ArrayList items = new ArrayList();
		ArrayList itemsUngrped = new ArrayList();
		if (hasGroup) {
			if (hasUngroupedItems) {
				itemsUngrped = idao.findAllUngroupedParentsBySectionId(sb.getId(), sb.getCRFVersionId());
			}
		}

		logger.trace("no item groups");
		items = idao.findAllNonRepeatingParentsBySectionId(sb.getId());
		items.addAll(itemsUngrped);
		// }
		logger.debug("items size" + items.size());
		for (int i = 0; i < items.size(); i++) {
			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(edcb);
			ItemBean ib = (ItemBean) items.get(i);
			dib.setItem(ib);
			displayItems.put(new Integer(dib.getItem().getId()), dib);
		}

		ArrayList data = iddao.findAllBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
		for (int i = 0; i < data.size(); i++) {
			ItemDataBean idb = (ItemDataBean) data.get(i);
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(idb.getItemId()));
			if (dib != null) {
				dib.setData(idb);
				displayItems.put(new Integer(idb.getItemId()), dib);
			}
		}

		ArrayList metadata = ifmdao.findAllBySectionId(sb.getId());
		for (int i = 0; i < metadata.size(); i++) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metadata.get(i);
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(ifmb.getItemId()));
			if (dib != null) {
				logger.debug("Entering thread before getting ItemMetadataService:::" + Thread.currentThread());
				boolean showItem = dynamicsMetadataService.isShown(ifmb.getItemId(), ecb, dib.getData());
				if (isDDEPage) {
					showItem = dynamicsMetadataService.hasPassedDDE(ifmb, ecb, dib.getData());
				}
				// is the above needed for children items too?
				boolean passedDDE = dynamicsMetadataService.hasPassedDDE(ifmb, ecb, dib.getData());
				if (showItem) { // we are only showing, not hiding
					logger.debug("set show item " + ifmb.getItemId() + " idb " + dib.getData().getId() + " show item "
							+ showItem + " passed dde " + passedDDE);
					ifmb.setShowItem(true);
				} else {
					logger.debug("DID NOT set show item " + ifmb.getItemId() + " idb " + dib.getData().getId()
							+ " show item " + showItem + " passed dde " + passedDDE + " value "
							+ dib.getData().getValue());
				}
				DynamicsItemFormMetadataBean dynamicsMetadataBean = dynamicsMetadataService
						.getDynamicsItemFormMetadataBean(ifmb.getItemId(), ecb, dib.getData());
				if (dynamicsMetadataBean != null) {
					ifmb.setShowItem(dynamicsMetadataBean.isShowItem());
				}
				dib.setMetadata(ifmb);
				displayItems.put(new Integer(ifmb.getItemId()), dib);
			}
		}

		Iterator hmIt = displayItems.keySet().iterator();
		while (hmIt.hasNext()) {
			Integer key = (Integer) hmIt.next();
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(key);
			answer.add(dib);
			logger.debug("*** getting with key: " + key + " display item bean with value: " + dib.getData().getValue());
		}
		logger.debug("*** test of the display items: " + displayItems.toString());

		return answer;
	}

	/**
	 * Get the DisplayItemBean objects corresponding to the items which are children of the specified parent.
	 * 
	 * @param parent
	 *            The item whose children are to be retrieved.
	 * @param isDDEPage
	 * @param shouldLoadDBValues
	 * 
	 * @return An array of DisplayItemBean objects corresponding to the items which are children of parent, and are
	 *         sorted by column number (ascending), then ordinal (ascending).
	 */
	private ArrayList getChildrenDisplayItems(DisplayItemBean parent, EventDefinitionCRFBean edcb, EventCRFBean ecb,
			Page servletPage) {
		boolean isDDEPage = Page.isDDEServletPage(servletPage);
		ArrayList answer = new ArrayList();
		int parentId = parent.getItem().getId();
		ItemDAO idao = new ItemDAO(dataSource);
		ArrayList childItemBeans = idao.findAllByParentIdAndCRFVersionId(parentId, ecb.getCRFVersionId());
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(dataSource);
		for (int i = 0; i < childItemBeans.size(); i++) {
			ItemBean child = (ItemBean) childItemBeans.get(i);
			ItemDataBean data = iddao.findByItemIdAndEventCRFId(child.getId(), ecb.getId());
			ItemFormMetadataBean metadata = ifmdao.findByItemIdAndCRFVersionId(child.getId(), ecb.getCRFVersionId());

			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(edcb);
			dib.setItem(child);
			// tbh
			if (!isDDEPage) {
				dib.setData(data);
			}
			dib.setDbData(data);
			boolean showItem = dynamicsMetadataService.isShown(metadata.getItemId(), ecb, data);
			if (isDDEPage) {
				showItem = dynamicsMetadataService.hasPassedDDE(metadata, ecb, data);
			}

			if (showItem) {
				logger.debug("set show item: " + metadata.getItemId() + " data " + data.getId());
				metadata.setShowItem(true);
			}

			dib.setMetadata(metadata);

			if (shouldLoadDBValues(dib, servletPage)) {
				logger.trace("should load db values is true, set value");
				dib.loadDBValue();
				logger.trace("just loaded the child value: " + dib.getData().getValue());
			}

			answer.add(dib);
		}

		// this is a pretty slow and memory intensive way to sort... see if we
		// can
		// have the db do this instead
		Collections.sort(answer);

		return answer;
	}

	/**
	 * Retrieve the DisplaySectionBean which will be used to display the Event CRF Section on the JSP, and also is used
	 * to controll processRequest.
	 * 
	 * @param request
	 *            TODO
	 */
	public ArrayList getAllDisplayBeans(ArrayList<SectionBean> allSectionBeans, EventCRFBean ecb, StudyBean study,
			Page servletPage) throws Exception {
		ArrayList<DisplaySectionBean> sections = new ArrayList<DisplaySectionBean>();
		SectionDAO sdao = new SectionDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);

		for (int j = 0; j < allSectionBeans.size(); j++) {

			SectionBean sb = allSectionBeans.get(j);

			DisplaySectionBean section = new DisplaySectionBean();
			section.setEventCRF(ecb);

			if (sb.getParentId() > 0) {
				SectionBean parent = (SectionBean) sdao.findByPK(sb.getParentId());
				sb.setParent(parent);
			}

			section.setSection(sb);

			CRFVersionDAO cvdao = new CRFVersionDAO(dataSource);
			CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(ecb.getCRFVersionId());
			section.setCrfVersion(cvb);

			CRFDAO cdao = new CRFDAO(dataSource);
			CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
			section.setCrf(cb);

			EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(dataSource);
			EventDefinitionCRFBean edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, ecb.getStudyEventId(),
					cvb.getId());

			section.setEventDefinitionCRF(edcb);

			// setup DAO's here to avoid creating too many objects
			ItemDAO idao = new ItemDAO(dataSource);
			ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(dataSource);
			iddao = new ItemDataDAO(dataSource);

			// get all the display item beans
			ArrayList displayItems = getParentDisplayItems(false, sb, edcb, idao, ifmdao, iddao, ecb, false,
					Page.isDDEServletPage(servletPage));

			logger.debug("222 just ran get parent display, has group " + " FALSE has ungrouped FALSE");
			// now sort them by ordinal
			Collections.sort(displayItems);

			// now get the child DisplayItemBeans
			for (int i = 0; i < displayItems.size(); i++) {
				DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
				dib.setChildren(getChildrenDisplayItems(dib, edcb, ecb, servletPage));

				if (shouldLoadDBValues(dib, servletPage)) {
					logger.trace("should load db values is true, set value");
					dib.loadDBValue();
				}

				displayItems.set(i, dib);
			}

			section.setItems(displayItems);
			sections.add(section);
		}

		return sections;
	}
}
