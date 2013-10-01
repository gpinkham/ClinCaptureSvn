package com.clinovo.service;

import java.util.List;

import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

public interface CodedItemService {
	
	List<CodedItem> findAll() throws Exception;

	CodedItem findCodedItem(int codedItemId);
	
	void deleteCodedItem(CodedItem codedItem);

	List<CodedItem> findByItem(int codedItemItemId);

	List<CodedItem> findCodedItemsByVerbatimTerm(String verbatimTerm);

	List<CodedItem> findCodedItemsByCodedTerm(String codedTerm);

	List<CodedItem> findCodedItemsByDictionary(String dictionary);

	List<CodedItem> findCodedItemsByStatus(CodeStatus coded);

	CodedItem saveCodedItem(CodedItem codedItem) throws Exception;

	List<CodedItem> findByEventCRF(int eventCRFId);

	List<CodedItem> findByCRFVersion(int crfVersion);
	
	List<CodedItem> findBySubject(int subject);
	
	CodedItem createCodedItem(EventCRFBean eventCRF, ItemBean itemBean) throws Exception;

	CodedItem findByItemData(int itemDataId);

}
