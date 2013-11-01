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
package com.clinovo.service;

import java.util.List;

import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;

/**
 * Service contract specification for the Term service to the DAO.
 *
 */
public interface TermService {

	/**
	 * Retrieves all terms.
	 * 
	 * @return List of terms
	 */
	List<Term> findAll();
	
	/**
	 * Retrieve a term given a valid id.
	 * 
	 * @param termId Id of the term to retrieve.
	 * 
	 * @return Term if it exists, null otherwise
	 */
	Term findTerm(int termId);

	/**
	 * Retrieve a term given a valid preferredName.
	 * 
	 * @param preferredName preferredName of the term to retrieve.
	 * 
	 * @return Term if it exists, null otherwise
	 */
	Term findTerm(String preferredName);

	/**
	 * Retrieve a term given a valid code.
	 * 
	 * @param code code of the term to retrieve.
	 * 
	 * @return Term if it exists, null otherwise
	 */
	Term findTermByCode(String code);

	/**
	 * Retrieves all the terms that belong to the specified dictionary
	 * 
	 * @param dictionary The dictionary to filter on.
	 * 
	 * @return List of terms
	 */
	List<Term> findTerm(Dictionary dictionary);
	
	/**
	 * Find a coded term given a verbatim term and an external dictionary to which it belongs.
	 * 
	 * @param verbatimTerm The verbatim term matching the candidate term's preferred name
	 * @param externalDictionaryName The external dictionary this term was picked from
	 * 
	 * @return Return Term only and only if both the verbatim term and dictionary match, null otherwise.
	 */
	Term findByTermAndExternalDictionary(String verbatimTerm, String externalDictionaryName);
	
	/**
	 * Find at least a coded term given a verbatim term and an external dictionary to which it belongs - this method is case insensitive.
	 * 
	 * @param verbatimTerm The verbatim term matching the candidate term's preferred name
	 * @param externalDictionaryName The external dictionary this term was picked from
	 * 
	 * @return Return Term if there is one that matches (ignoring case), otherwise, null -
	 */
	Term findByNonUniqueTermAndExternalDictionary(String verbatimTerm, String externalDictionaryName);

	/**
	 * Find all terms that were created using the specified external dictionary.
	 * 
	 * @param externalDictionaryName External dictionary name to filter on
	 * @return List of terms if found
	 */
	List<Term> findByExternalDictionary(String externalDictionaryName);
	
	/**
	 * Persist the given term to the database.
	 * 
	 * @param term Term to persist.
	 * 
	 * @return The persisted term if valid, otherwise null.
	 * @throws CodeException If the term already exists in storage
	 */
	Term saveTerm(Term term) throws CodeException;

	/**
	 * Delete the specified term from the database.
	 * 
	 * @param term The term to delete.
	 */
	void deleteTerm(Term term);

}
