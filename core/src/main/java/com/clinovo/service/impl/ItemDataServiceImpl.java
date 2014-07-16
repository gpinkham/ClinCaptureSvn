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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import com.clinovo.model.CodedItem;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.ItemDataService;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

@Service("itemDataService")
public class ItemDataServiceImpl implements ItemDataService {

	@Autowired private DataSource dataSource;

	@Autowired private CodedItemService codedItemService;

	private ItemDataDAO itemDataDAO;

	private DataSource getDataSource() {
		return dataSource;
	}

	private CodedItemService getCodedItemService() {
		return codedItemService;
	}

	private ItemDataDAO getItemDataDAO() {

		if (itemDataDAO == null) {
			return itemDataDAO = new ItemDataDAO(getDataSource());
		} else {
			return itemDataDAO;
		}
	}

	public void removeItemDataByEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		List<ItemDataBean> itemDataList = getItemDataDAO().findAllByEventCRFId(eventCRF.getId());

		for (ItemDataBean item : itemDataList) {

			if (!item.getStatus().isDeleted()) {
				item.setStatus(Status.AUTO_DELETED);
				item.setUpdater(updater);
				item.setUpdatedDate(new Date());
				getItemDataDAO().update(item);
			}

			CodedItem codedItem = getCodedItemService().findCodedItem(item.getId());

			if (codedItem != null) {
				codedItem.setStatus(com.clinovo.model.Status.CodeStatus.REMOVED.toString());
				getCodedItemService().saveCodedItem(codedItem);
			}
		}
	}

	public void restoreItemDataByEventCRF(EventCRFBean eventCRF, UserAccountBean updater) throws Exception {

		List<ItemDataBean> itemDataList = getItemDataDAO().findAllByEventCRFId(eventCRF.getId());

		for (ItemDataBean item : itemDataList) {

			if (item.getStatus().equals(Status.AUTO_DELETED)) {
				item.setStatus(Status.AVAILABLE);
				item.setUpdater(updater);
				item.setUpdatedDate(new Date());
				getItemDataDAO().update(item);
			}

			CodedItem codedItem = getCodedItemService().findCodedItem(item.getId());
			if (codedItem != null) {

				if (codedItem.getHttpPath() == null || codedItem.getHttpPath().isEmpty()) {
					codedItem.setStatus(com.clinovo.model.Status.CodeStatus.NOT_CODED.toString());
				} else {
					codedItem.setStatus(com.clinovo.model.Status.CodeStatus.CODED.toString());
				}

				getCodedItemService().saveCodedItem(codedItem);
			}
		}
	}
}
