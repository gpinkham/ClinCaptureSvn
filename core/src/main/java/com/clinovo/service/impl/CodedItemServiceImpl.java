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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOÃ¢â‚¬â„¢S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.clinovo.exception.CodeException;
import com.clinovo.model.CodedItemElement;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.CodedItemDAO;
import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;
import com.clinovo.service.CodedItemService;

@Service
@Transactional
public class CodedItemServiceImpl implements CodedItemService {

	private ItemDataDAO itemDataDAO;
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	private CodedItemDAO codeItemDAO;


	public List<CodedItem> findAll() {

		return codeItemDAO.findAll();
	}

	public List<CodedItem> findByStudy(int studyId) {
		
		List<CodedItem> codedItems = codeItemDAO.findByStudy(studyId);
		
		return retrieveAvailableItems(codedItems);
	}
	
	public List<CodedItem> findByStudyAndSite(int studyId, int siteId) {
		
		List<CodedItem> codedItems = codeItemDAO.findByStudyAndSite(studyId, siteId);
		
		return retrieveAvailableItems(codedItems);
	}

	public CodedItem findById(int codedItemId) {
		return codeItemDAO.findById(codedItemId);
	}

	public CodedItem findCodedItem(int codedItemId) {
		return codeItemDAO.findByItemId(codedItemId);
	}

	public List<CodedItem> findCodedItemsByDictionary(String dictionary) {
		return codeItemDAO.findByDictionary(dictionary);
	}

	public List<CodedItem> findCodedItemsByStatus(CodeStatus status) {
		return codeItemDAO.findByStatus(status);
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

	public CodedItem createCodedItem(EventCRFBean eventCRF, ItemBean item, ItemDataBean itemData, StudyBean study) throws Exception {

		CodedItem cItem = new CodedItem();

		ItemDAO itemDAO = new ItemDAO(dataSource);
		ItemFormMetadataDAO itemMetaDAO = new ItemFormMetadataDAO(dataSource);
        ItemFormMetadataBean meta = itemMetaDAO.findByItemIdAndCRFVersionId(item.getId(),
                eventCRF.getCRFVersionId());
		if (currentDictionaryIsValid(meta.getCodeRef()) && !itemData.getValue().isEmpty()) {

            if (study.isSite(study.getParentStudyId())) {

                cItem.setSiteId(study.getId());
                cItem.setStudyId(study.getParentStudyId());

            } else {

                cItem.setStudyId(study.getId());
            }

            cItem.setItemId(itemData.getId());
            cItem.setEventCrfId(eventCRF.getId());
            cItem.setDictionary(meta.getCodeRef());
            cItem.setSubjectId(eventCRF.getStudySubjectId());
            cItem.setCrfVersionId(eventCRF.getCRFVersionId());
            cItem.setPreferredTerm(itemData.getValue());

            codeItemDAO.saveOrUpdate(cItem);
            return cItem;

        } else if (item.getDataType().equals(ItemDataType.CODE)) {

            ItemBean refItem = (ItemBean) itemDAO.findByNameAndCRFVersionId(meta.getCodeRef(),
                    eventCRF.getCRFVersionId());
            ItemDataBean refItemData = getItemDataDAO().findByItemIdAndEventCRFIdAndOrdinal(refItem.getId(), eventCRF.getId(), itemData.getOrdinal());
            CodedItem codedItem = codeItemDAO.findByItemId(refItemData.getId());

            if(codedItem != null) {

            	CodedItemElement codedItemElement = new CodedItemElement(itemData.getId(), StringUtils.substringAfter(item.getName(), "_"));

                codedItem.addCodedItemElements(codedItemElement);

                codeItemDAO.saveOrUpdate(codedItem);

                return codedItem;
            }
        }

		return cItem;
	}

    public CodedItem saveCodedItem(CodedItem codedItem) throws Exception {

        for(CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {
            ItemDataBean itemData = (ItemDataBean) getItemDataDAO().findByPK(codedItemElement.getItemDataId());
           
            if (itemData.getId() > 0) {

                itemData.setUpdatedDate(new Date());
                itemData.setValue(codedItemElement.getItemCode());

                // persist
                getItemDataDAO().updateValue(itemData);

            } else {

                throw new CodeException("ItemData for this Item not found. Has the CRF been completed?");
            }
        }

        resetEventAndCrfStatus(codedItem);

        return codeItemDAO.saveOrUpdate(codedItem);
    }

	public void deleteCodedItem(CodedItem codedItem) {
		codeItemDAO.deleteCodedItem(codedItem);
	}
	
	private ItemDataDAO getItemDataDAO() {
		
		if(itemDataDAO == null) {
			itemDataDAO = new ItemDataDAO(dataSource);
		}
		
		return itemDataDAO;
	}

	public void removeByCRFVersion(int versionId) {

		List<CodedItem> codedItems = findByCRFVersion(versionId);

		for (CodedItem item : codedItems) {

			item.setStatus(String.valueOf(CodeStatus.REMOVED));
			codeItemDAO.saveOrUpdate(item);
		}
	}
	
	public void deleteByCRFVersion(int versionId) {
		
		List<CodedItem> codedItems = findByCRFVersion(versionId);

		for (CodedItem item : codedItems) {
			codeItemDAO.deleteCodedItem(item);
		}
	}

	public void restoreByCRFVersion(int versionId) {
		
		List<CodedItem> codedItems = findByCRFVersion(versionId);

		for (CodedItem item : codedItems) {

            if(item.getId() > 0) {

				item.setStatus(String.valueOf(CodeStatus.CODED));
				
			} else {
				
				item.setStatus(String.valueOf(CodeStatus.NOT_CODED));
			}
			
			codeItemDAO.saveOrUpdate(item);
		}
		
	}
	
	private List<CodedItem> retrieveAvailableItems(List<CodedItem> items) {
		
		List<CodedItem> validItems = new ArrayList<CodedItem>();

		for (CodedItem item : items) {
			if (!item.getStatus().equals(String.valueOf(CodeStatus.REMOVED))) {
				validItems.add(item);
			}
		}

		return validItems;
	}


    private boolean currentDictionaryIsValid(String dictionaryName) {
        if (dictionaryName.equalsIgnoreCase("icd_10") ||
                dictionaryName.equalsIgnoreCase("icd_9cm") ||
                dictionaryName.equalsIgnoreCase("whod") ||
                dictionaryName.equalsIgnoreCase("meddra")) {
            return true;
        } else {
            return false;
        }
    }

    private void resetEventAndCrfStatus(CodedItem codedItem) {

        EventCRFDAO eventEcrfDAO = new EventCRFDAO(dataSource);
        StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);

        EventCRFBean eventCRFBean = (EventCRFBean) eventEcrfDAO.findByPK(codedItem.getEventCrfId());
        StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventCRFBean.getStudyEventId());

        if (studyEventBean.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED) || eventCRFBean.isSdvStatus()) {

            eventCRFBean.setStage(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE);
            eventCRFBean.setSdvStatus(false);
            eventEcrfDAO.update(eventCRFBean);

            studyEventBean.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
            studyEventBean.setUpdatedDate(new Date());
            studyEventDAO.update(studyEventBean);
        }
    }
}
