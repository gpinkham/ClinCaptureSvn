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
import com.clinovo.model.Term;

/**
 * This class is the database interface for medical coding terms. It performs the CRUD operations related to the terms.
 * 
 * <p>
 * Note that some members in this class have the potential to return null, depending on the validity of filters specified. 
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class TermDAO extends AbstractDomainDao<Term> {

	@Override
	public Class<Term> domainClass() {
		
		return Term.class;
	}
	
	/**
	 * Retrieves all the terms from the database.
	 * 
	 * @return List of all valid terms
	 */
	public List<Term> findAll() {

		String query = "from  " + this.getDomainClassName() + " order by preferredName asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<Term>) q.list();
	}
	
	/**
	 * Retrieves a term for the specified name.
	 * 
	 * @param name The name of the term to search for.
	 * 
	 * @return The term if found, otherwise null.
	 */
	public Term findByName(String name) {

		String query = "from " + getDomainClassName() + " do  where do.preferredName = :name";
		Query q = getCurrentSession().createQuery(query);
		q.setString("name", name);

		return (Term) q.uniqueResult();
	}

	/**
	 * Retrieves all the terms that belong to the specified dictionary.
	 * 
	 * @param dictionary The dictionary to which the terms belong.
	 * 
	 * @return List of terms that belong to the specified dictionary, otherwise null.
	 */
	public List<Term> findByDictionary(Dictionary dictionary) {

		Criteria criteria = this.getCurrentSession().createCriteria(getDomainClassName());
		criteria.add(Restrictions.eq("dictionary", dictionary));
		
		return criteria.list();
	}
	
	/**
	 * Deletes the given term from the database.
	 * 
	 * @param term The term to delete
	 */
	public void deleteTerm(Term term) {
		
		this.getCurrentSession().delete(term);
	}

	/**
	 * Find a coded term given a verbatim term and an external dictionary to which it belongs.
	 * 
	 * @param verbatimTerm The verbatim term matching the candidate term's preferred name
	 * @param externalDictionaryName The external dictionary this term was picked from
	 * 
	 * @return Return Term only and only if both the verbatim term and dictionary match, null otherwise.
	 */
	public Term findByTermAndExternalDictionary(String verbatimTerm, String externalDictionaryName) {
		
		String query = "from " + getDomainClassName() + " t  where t.preferredName = :preferredName and externalDictionaryName = :externalDictionaryName";
		Query q = getCurrentSession().createQuery(query);
		q.setString("preferredName", verbatimTerm);
		q.setString("externalDictionaryName", externalDictionaryName);

		return (Term) q.uniqueResult();
	}

	/**
	 * Find all terms that were created using the specified external dictionary (from the specified external dictionary)
	 * 
	 * @param externalDictionaryName The external dictionary to filter on.
	 * 
	 * @return List of terms that were created from the external dictionary
	 */
	public List<Term> findByExternalDictionary(String externalDictionaryName) {
		
		String query = "from " + getDomainClassName() + " t  where t.externalDictionaryName = :externalDictionaryName";
		Query q = getCurrentSession().createQuery(query);
		q.setString("externalDictionaryName", externalDictionaryName);

		return (List<Term>) q.list();
	}
}
