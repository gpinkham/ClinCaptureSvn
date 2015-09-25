package com.clinovo.validator;

import com.clinovo.model.CodedItem;
import com.clinovo.model.CodedItemElement;
import com.clinovo.service.CodedItemService;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Coded Term Validator.
 */
public final class CodedTermValidator {

	private DataSource dataSource;
	private CodedItemService codedItemService;

	/**
	 * Constructor for coded term validator.
	 * @param dataSource DataSource
	 * @param codedItemService CodedItemService
	 */
	public CodedTermValidator(DataSource dataSource, CodedItemService codedItemService) {
		this.dataSource = dataSource;
		this.codedItemService = codedItemService;
	}

	/**
	 * Validate coded term.
	 * @param changedItemsList ArrayList of DisplayItemBeans
	 * @param item DisplayItemBean
	 * @param ecrfBean EventCRFBean
	 * @throws Exception in case if error will be thrown while coding item saving.
	 */
	public void validateCodedTerm(ArrayList<DisplayItemBean> changedItemsList, DisplayItemBean item,
									 EventCRFBean ecrfBean) throws Exception {
		ItemFormMetadataDAO itemMetaDAO = new ItemFormMetadataDAO(dataSource);
		ItemDAO itemDAO = new ItemDAO(dataSource);
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		StudyDAO studyDAO = new StudyDAO(dataSource);

		ItemFormMetadataBean meta = itemMetaDAO.findByItemIdAndCRFVersionId(item.getItem().getId(),
				ecrfBean.getCRFVersionId());
		ItemBean refItem = (ItemBean) itemDAO.findByNameAndCRFVersionId(meta.getCodeRef(), ecrfBean.getCRFVersionId());
		ItemDataBean refItemData = itemDataDAO.findByItemIdAndEventCRFIdAndOrdinal(refItem.getId(), ecrfBean.getId(), item
				.getData().getOrdinal());

		if (refItemData.getId() > 0) {
			for (DisplayItemBean displayItemBean : changedItemsList) {
				CodedItem codedItem = (CodedItem) codedItemService.findCodedItem(refItemData.getId());
				if (codedItem != null && codedItem.getId() > 0) {
					CodedItemElement gradeElement = codedItem.getCodedItemElementByItemName("GR");
					ItemDataBean gradeItemData = (ItemDataBean) itemDataDAO.findByPK(gradeElement.getItemDataId());
					if (refItemData.getId() == displayItemBean.getData().getId()
							&& !refItemData.getValue().equalsIgnoreCase(displayItemBean.getData().getValue())) {
						codedItem.setStatus("NOT_CODED");
						codedItem.setHttpPath("");
						codedItem.setPreferredTerm(displayItemBean.getData().getValue());
						for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {
							if (!codedItemElement.getItemName().equals("GR")) {
								codedItemElement.setItemCode("");
							}
						}
						if (displayItemBean.getData().getValue().isEmpty()) {
							codedItemService.deleteCodedItem(codedItem);
						} else {
							codedItemService.saveCodedItem(codedItem);
						}
						item.getData().setValue("");
					} else if (gradeItemData.getId() == displayItemBean.getData().getId()
							&& displayItemBean.getData().getId() != 0) {
						ItemDataBean refItemDataBean = itemDataDAO.findByItemIdAndEventCRFIdAndOrdinal(refItem.getId(),
								ecrfBean.getId(), item.getData().getOrdinal());
						for (DisplayItemBean changedItem : changedItemsList) {
							if (refItemDataBean.getId() == changedItem.getData().getId()
									&& !refItemDataBean.getValue().equalsIgnoreCase(changedItem.getData().getValue())) {
								refItemDataBean = changedItem.getData();
								break;
							}
						}
						codedItem.setPreferredTerm(refItemDataBean.getValue());
						codedItem.setStatus("NOT_CODED");
						codedItem.setHttpPath("");
						for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {
							if (codedItemElement.getItemName().equals("GR")) {
								codedItemElement.setItemCode(displayItemBean.getData().getValue());
							} else {
								codedItemElement.setItemCode("");
							}
						}
						codedItemService.saveCodedItem(codedItem);
						item.getData().setValue("");
					} else if (gradeElement.getItemDataId() < 0 && codedItem.getDictionary().equals("CTCAE")) {
						StudyBean study = (StudyBean) studyDAO.findByPK(
								codedItem.getSiteId() > 0 ? codedItem.getSiteId() : codedItem.getStudyId());
						codedItemService.createCodedItem(ecrfBean, displayItemBean.getItem(),
								displayItemBean.getData(), study);
						item.getData().setValue("");
					}
				}
			}
		}
	}
}
