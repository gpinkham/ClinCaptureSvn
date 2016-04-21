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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.Dictionary;
/**
 * This class is the database interface for medical coding dictionaries. It performs the CRUD operations related to coded items.
 * 
 * <p>
 * Note that some members in this class have the potential to return null, depending on the validity of filters specified. 
 *
 */
@Repository
public class DictionaryDAO extends AbstractDomainDao<Dictionary> {

	@Override
	public Class<Dictionary> domainClass() {
		return Dictionary.class;
	}
	
	/**
	 * Retrieves all the medical dictionaries from the database.
	 * 
	 * @return List of all valid dictionaries
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionary> findAll() {
		
		String query = "from  " + this.getDomainClassName() + " order by name asc";
		Query q = this.getCurrentSession().createQuery(query);
		
		return (List<Dictionary>) q.list();
	}

	/**
	 * Retrieves a medical coding dictionary for a specified name.
	 * 
	 * @param dictionaryName The name of the medical coding dictionary to search for.
	 * 
	 * @return The medical coding dictionary if found, otherwise null.
	 */
	public Dictionary findByName(String dictionaryName) {
		
		String query = "from " + getDomainClassName() + " do  where do.name = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", dictionaryName);
		
		return (Dictionary) q.uniqueResult();
	}

	/**
	 * Deletes the given dictionary from the database.
	 * 
	 * @param dictionary The dictionary to delete
	 */
	public void deleteDictionary(Dictionary dictionary) {
		this.getCurrentSession().delete(dictionary);
	}

	public Dictionary findByStudy(int study) {
		
		String query = "from " + getDomainClassName() + " do  where do.study = :study";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("study", study);
		
		return (Dictionary) q.uniqueResult();
	}
}
