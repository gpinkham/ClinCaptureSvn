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
package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;

/**
 * This class is the database interface for coded items. It performs the CRUD operations related to coded items.
 * 
 * <p>
 * Note that some members in this class have the potential to return null, depending on the validity of filters specified. 
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class CodedItemDAO extends AbstractDomainDao<CodedItem> {

	@Override
	public Class<CodedItem> domainClass() {
		return CodedItem.class;
	}

	/**
	 * Retrieves all the coded items from the database.
	 * 
	 * @return List of all valid coded items
	 */
	public List<CodedItem> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by itemId asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<CodedItem>) q.list();
	}

	/**
	 * Retrieves coded items that have been coded with the same dictionary
	 *
	 * @param dictionary The dictionary name to filter on.
	 *
	 * @return List of coded items that were coded with the specified dictionary
	 */
	public List<CodedItem> findByDictionary(String dictionary) {

		String query = "from " + getDomainClassName() + " do  where do.dictionary = :dictionary";
		Query q = getCurrentSession().createQuery(query);
		q.setString("dictionary", dictionary);

		return q.list();
	}

	/**
	 * Retrieves coded items that have similar status.
	 *
	 * @param status The status to filter on.
	 *
	 * @return List of coded items that have the specified status
	 */
	public List<CodedItem> findByStatus(CodeStatus status) {

		String query = "from " + getDomainClassName() + " do  where do.status = :status";
		Query q = getCurrentSession().createQuery(query);
		q.setString("status", String.valueOf(status));

		return q.list();
	}

	/**
	 * Retrieves all the coded items that belong to a specified item.
	 *
	 * @param codedItemItemId The item id to filter on.
	 *
	 * @return List of coded items that belong to the specified item.
	 */
	public CodedItem findByItemId(int codedItemItemId) {

		String query = "from " + getDomainClassName() + " do  where do.itemId = :itemId";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("itemId", codedItemItemId);

        return (CodedItem) q.uniqueResult();
	}

	/**
	 * Retrieves all the coded items that belong to the specified event crf.
	 *
	 * @param eventCRFId The event crf id to filter on.
	 *
	 * @return List of coded items that belong to the specified event.
	 */
	public List<CodedItem> findByEventCRF(int eventCRFId) {

		String query = "from " + getDomainClassName() + " do  where do.eventCrfId = :eventCrfId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("eventCrfId", eventCRFId);

		return q.list();
	}

	/**
	 * Retrieves all the coded items that belong to the specified crf version.
	 *
	 * @param crfVersionId The crf version id to filter on.
	 *
	 * @return List of coded items that belong to the specified crf version.
	 */
	public List<CodedItem> findByCRFVersion(int crfVersionId) {

		String query = "from " + getDomainClassName() + " do  where do.crfVersionId = :crfVersionId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("crfVersionId", crfVersionId);

		return q.list();
	}

	/**
	 * Retrieves all the coded items that belong to the specified subject.
	 *
	 * @param subjectId The subject id to filter on.
	 *
	 * @return List of coded items that belong to the specified subject.
	 */
	public List<CodedItem> findBySubject(int subjectId) {

		String query = "from " + getDomainClassName() + " do  where do.subjectId = :subjectId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("subjectId", subjectId);

		return q.list();
	}

	/**
	 * Retrieves the coded item that belong to the specified study. 
	 * 
	 * @param studyId The study id to filter on.
	 * 
	 * @return List of coded items belonging to the specified study.
	 */
	public List<CodedItem> findByStudy(int studyId) {
		
		String query = "from " + getDomainClassName() + " do  where do.studyId = :studyId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);

		return q.list();
	}
	
	/**
	 * Retrieves the coded item that belong to the scope (study/site). 
	 * 
	 * @param studyId The study id to filter on.
	 * @param siteId The site id to filter on
	 * 
	 * @return List of coded items belonging to the specified scope.
	 */
	public List<CodedItem> findByStudyAndSite(int studyId, int siteId) {
		
		String query = "from " + getDomainClassName() + " do  where do.studyId = :studyId and siteId = :siteId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);
		q.setInteger("siteId", siteId);

		return q.list();
	}
	
	/**
	 * Deletes the given coded item from the database.
	 * 
	 * @param codedItem The coded item to delete
	 */
	public void deleteCodedItem(CodedItem codedItem) {
		
		this.getCurrentSession().delete(codedItem);
	}
}
