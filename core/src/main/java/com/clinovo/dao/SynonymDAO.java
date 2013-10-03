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
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.clinovo.model.Dictionary;
import com.clinovo.model.Synonym;

/**
 * This class is the database interface for medical coding term synonyms. It performs the CRUD operations related to the synonyms.
 * 
 * <p>
 * Note that some members in this class have the potential to return null, depending on the validity of filters specified. 
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class SynonymDAO extends AbstractDomainDao<Synonym> {

	@Override
	public Class<Synonym> domainClass() {
		return Synonym.class;
	}
	
	/**
	 * Retrieves all the synonyms from the database.
	 * 
	 * @return List of all valid synonyms
	 */
	public List<Synonym> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by name asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<Synonym>) q.list();
	}

	/**
	 * Retrieves a synonym for the specified name.
	 * 
	 * @param synonymName The name of the synonym to search for.
	 * 
	 * @return The synonym if found, otherwise null.
	 */
	public Synonym findByName(String synonymName) {

		String query = "from " + getDomainClassName() + " do  where do.name = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", synonymName);

		return (Synonym) q.uniqueResult();
	}

	/**
	 * Retrieves a synonym for the specified code.
	 * 
	 * @param code The code of the synonym to search for.
	 * 
	 * @return The synonym if found, otherwise null.
	 */
	public Synonym findByCode(String code) {

		String query = "from " + getDomainClassName() + " do  where do.code = :code";
		Query q = getCurrentSession().createQuery(query);
		q.setString("code", code);

		return (Synonym) q.uniqueResult();
	}

	/**
	 * Retrieves all the synonyms that belong to the specified dictionary.
	 * 
	 * @param dictionary The dictionary to which the synonyms belong.
	 * 
	 * @return List of synonyms that belong to the specified dictionary, otherwise null.
	 */
	public List<Synonym> findByDictionary(Dictionary dictionary) {

		Criteria criteria = this.getCurrentSession().createCriteria(getDomainClassName());
		criteria.add(Restrictions.eq("dictionary", dictionary));
		
		return criteria.list();
	}

	/**
	 * Deletes the given synonym from the database.
	 * 
	 * @param synon The synonym to delete
	 */
	public void deleteSynonym(Synonym synon) {
		
		this.getCurrentSession().delete(synon);
	}
}
