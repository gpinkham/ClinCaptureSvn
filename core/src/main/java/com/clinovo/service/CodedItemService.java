package com.clinovo.service;

import java.util.List;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

public interface CodedItemService {
	
	List<CodedItem> findAll() throws Exception;

	CodedItem findCodedItem(int codedItemId);
	
	void deleteCodedItem(CodedItem codedItem);

	CodedItem findByItemId(int codedItemItemId);

	List<CodedItem> findCodedItemsByVerbatimTerm(String verbatimTerm);

	List<CodedItem> findCodedItemsByCodedTerm(String codedTerm);

	List<CodedItem> findCodedItemsByDictionary(String dictionary);

	List<CodedItem> findCodedItemsByStatus(CodeStatus coded);

	CodedItem saveCodedItem(CodedItem codedItem) throws Exception;

	List<CodedItem> findByEventCRF(int eventCRFId);

	List<CodedItem> findByCRFVersion(int i);

}
