/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.model.CodedItem;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.ItemDataService;

/**
 * ItemDataServiceImpl.
 */
@Service("itemDataService")
@SuppressWarnings("unchecked")
public class ItemDataServiceImpl implements ItemDataService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CodedItemService codedItemService;

	public ItemDataDAO getItemDataDAO() {
		return new ItemDataDAO(dataSource);
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return new DiscrepancyNoteDAO(dataSource);
	}

	private void disableItemDataBeans(ItemDataBean itemDataBean, UserAccountBean updater, Status status)
			throws Exception {
		if (!itemDataBean.getStatus().isInvalid() && !itemDataBean.getStatus().isDeleted()
				&& !itemDataBean.getStatus().isLocked()) {
			itemDataBean.setOldStatus(itemDataBean.getStatus());
		}
		itemDataBean.setStatus(status);
		itemDataBean.setUpdater(updater);
		getItemDataDAO().update(itemDataBean);
		CodedItem codedItem = codedItemService.findCodedItem(itemDataBean.getId());
		if (codedItem != null) {
			codedItem.setStatus(status.isDeleted()
					? com.clinovo.model.Status.CodeStatus.REMOVED.toString()
					: com.clinovo.model.Status.CodeStatus.LOCKED.toString());
			codedItemService.saveCodedItem(codedItem, false);
		}
	}

	private void enableItemDataBeans(ItemDataBean itemDataBean, UserAccountBean updater, Status status)
			throws Exception {
		if (!itemDataBean.getOldStatus().isInvalid() && !itemDataBean.getOldStatus().isDeleted()
				&& !itemDataBean.getOldStatus().isLocked()) {
			itemDataBean.setStatus(itemDataBean.getOldStatus());
		} else {
			itemDataBean.setStatus(status);
		}
		itemDataBean.setUpdater(updater);
		getItemDataDAO().update(itemDataBean);
		CodedItem codedItem = codedItemService.findCodedItem(itemDataBean.getId());
		if (codedItem != null) {
			if (itemDataBean.getStatus().isDeleted() || itemDataBean.getStatus().isLocked()) {
				codedItem.setStatus(itemDataBean.getStatus().isDeleted()
						? com.clinovo.model.Status.CodeStatus.REMOVED.toString()
						: com.clinovo.model.Status.CodeStatus.LOCKED.toString());
			} else {
				if (codedItem.getHttpPath() == null || codedItem.getHttpPath().isEmpty()) {
					codedItem.setStatus(com.clinovo.model.Status.CodeStatus.NOT_CODED.toString());
				} else {
					codedItem.setStatus(com.clinovo.model.Status.CodeStatus.CODED.toString());
				}
			}
			codedItemService.saveCodedItem(codedItem, false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteItemData(EventCRFBean eventCrf, UserAccountBean updater) throws Exception {
		ItemDataDAO iddao = getItemDataDAO();
		DiscrepancyNoteDAO dnDao = getDiscrepancyNoteDAO();

		List<ItemDataBean> itemDataBeanList = iddao.findAllByEventCRFId(eventCrf.getId());
		for (ItemDataBean itemDataBean : itemDataBeanList) {
			CodedItem codedItem = codedItemService.findCodedItem(itemDataBean.getId());
			List<DiscrepancyNoteBean> discrepancyList = dnDao.findExistingNotesForItemData(itemDataBean.getId());

			iddao.deleteDnMap(itemDataBean.getId());

			for (DiscrepancyNoteBean noteBean : discrepancyList) {
				dnDao.deleteNotes(noteBean.getId());
			}

			itemDataBean.setUpdater(updater);
			iddao.updateUser(itemDataBean);
			iddao.delete(itemDataBean.getId());

			if (codedItem != null) {
				codedItemService.deleteCodedItem(codedItem);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateItemDataStates(EventCRFBean eventCrf, UserAccountBean updater) throws Exception {
		List<ItemDataBean> itemDataList = getItemDataDAO().findAllByEventCRFId(eventCrf.getId());
		for (ItemDataBean itemDataBean : itemDataList) {
			if (eventCrf.getStatus().isDeleted() || eventCrf.getStatus().isLocked()) {
				disableItemDataBeans(itemDataBean, updater, eventCrf.getStatus());
			} else {
				enableItemDataBeans(itemDataBean, updater, eventCrf.getStatus());
			}
		}
	}
}
