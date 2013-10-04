package com.clinovo.service.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.CodedItemDAO;
import com.clinovo.exception.CodeException;
import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;
import com.clinovo.service.CodedItemService;

@Service
@Transactional
@SuppressWarnings("rawtypes")
public class CodedItemServiceImpl implements CodedItemService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CodedItemDAO codeItemDAO;

	public List<CodedItem> findAll() throws Exception {

		return codeItemDAO.findAll();
	}

	public CodedItem findById(int codedItemId) {
		return codeItemDAO.findById(codedItemId);
	}

	public CodedItem findCodedItem(int codedItemId) {
		return codeItemDAO.findById(codedItemId);
	}

	public List<CodedItem> findCodedItemsByVerbatimTerm(String verbatimTerm) {
		return codeItemDAO.findByVerbatimTerm(verbatimTerm);
	}

	public List<CodedItem> findCodedItemsByCodedTerm(String codedTerm) {
		return codeItemDAO.findByCodedTerm(codedTerm);
	}

	public List<CodedItem> findCodedItemsByDictionary(String dictionary) {
		return codeItemDAO.findByDictionary(dictionary);
	}

	public List<CodedItem> findCodedItemsByStatus(CodeStatus status) {
		return codeItemDAO.findByStatus(status);
	}

	public List<CodedItem> findByItem(int codedItemItemId) {
		return codeItemDAO.findByItemId(codedItemItemId);
	}
	
	public List<CodedItem> findByEventCRF(int eventCRFId) {
		return codeItemDAO.findByEventCRF(eventCRFId);
	}

	public List<CodedItem> findByCRFVersion(int crfVersionId) {
		return codeItemDAO.findByCRFVersion(crfVersionId);
	}
	
	public List<CodedItem> findBySubject(int subject) {
		return codeItemDAO.findBySubject(subject);
	}

	public CodedItem saveCodedItem(CodedItem codedItem) throws Exception {

		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		UserAccountDAO userDAO = new UserAccountDAO(dataSource);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserAccountBean loggedInUser = (UserAccountBean) userDAO.findByUserName(authentication.getName());
		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByItemIdAndEventCRFId(codedItem.getItemId(),
				codedItem.getEventCrfId());

		if (itemData.getId() > 0) {

			itemData.setUpdater(loggedInUser);
			itemData.setUpdatedDate(new Date());
			itemData.setValue(codedItem.getCodedTerm());

			// persist
			itemDataDAO.updateValue(itemData);
			
		} else {

			throw new CodeException("ItemData for this Item not found. Has the CRF been completed?");
		}

		return codeItemDAO.saveOrUpdate(codedItem);
	}

	public void deleteCodedItem(CodedItem codedItem) {
		codeItemDAO.deleteCodedItem(codedItem);
	}

	public CodedItem createCodedItem(EventCRFBean eventCRF, ItemBean item, ItemDataBean itemData) throws Exception {

		CodedItem cItem = new CodedItem();

		ItemDAO itemDAO = new ItemDAO(dataSource);
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO itemMetaDAO = new ItemFormMetadataDAO(dataSource);

		if (item.getDataType().equals(ItemDataType.CODE)) {

			ItemFormMetadataBean meta = itemMetaDAO.findByItemIdAndCRFVersionId(item.getId(),
					eventCRF.getCRFVersionId());
			ItemBean refItem = (ItemBean) itemDAO.findByNameAndCRFVersionId(meta.getCodeRef(),
					eventCRF.getCRFVersionId());
			
			// We use the ordinal to cater for repeat items
			ItemDataBean data = itemDataDAO.findByItemIdAndEventCRFIdAndOrdinal(refItem.getId(), eventCRF.getId(), itemData.getOrdinal());

			// Now the item
			if (data.getValue() != null && !data.getValue().isEmpty()) {

				cItem.setItemId(item.getId());
				cItem.setItemDataId(data.getId());
				cItem.setEventCrfId(eventCRF.getId());
				cItem.setVerbatimTerm(data.getValue());
				cItem.setSubjectId(eventCRF.getStudySubjectId());
				cItem.setCrfVersionId(eventCRF.getCRFVersionId());

				cItem = saveCodedItem(cItem);
			}
		}

		return cItem;
	}

	public CodedItem findByItemData(int itemDataId) {
		return codeItemDAO.findByItemData(itemDataId);
	}
}
