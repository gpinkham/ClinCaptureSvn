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

import org.akaza.openclinica.bean.managestudy.StudyBean;

import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;

/**
 * Service contract specification for the dictionary service to the DAO.
 *
 */
public interface DictionaryService {
	
	/**
	 * Retrieves all dictionaries.
	 * 
	 * @return List of dictionaries
	 */
	public List<Dictionary> findAll();
	
	/**
	 * Retrieves the custom dictionary configured for the specified study.
	 * 
	 * @param study The study for which to extract the custom dictionary for.
	 * 
	 * @return The custom dictionary, if it has been configured.
	 */
	public Dictionary findByStudy(int study);
	
	/**
	 * Retrieve a dictionary given a valid id.
	 * 
	 * @param dictionaryId Id of the dictionary to retrieve.
	 * 
	 * @return Dictionary if it exists, null otherwise
	 */
	public Dictionary findDictionary(int dictionaryId);

	/**
	 * Retrieve a dictionary given a valid name.
	 * 
	 * @param dictionaryName Name of the dictionary to retrieve.
	 * 
	 * @return Dictionary if it exists, null otherwise
	 */
	public Dictionary findDictionary(String dictionaryName);

	/**
	 * Persists a valid dictionary to storage.
	 * 
	 * @param dictionary Dictionary to persist.
	 * 
	 * @return Persisted dictionary, null incase it was invalid
	 */
	public Dictionary saveDictionary(Dictionary dictionary);

	/**
	 * Deletes a specified dictionary from storage.
	 * 
	 * @param dictionary Dictionary to delete
	 */
	public void deleteDictionary(Dictionary dictionary);

	/**
	 * Creates a custom dictionary for a particular study.
	 * 
	 * @param dictionaryName The name for the custom dictionary.
	 * @param study The study to which the custom dictionary is bound
	 * 
	 * @return The created custom dictionary
	 * 
	 * @throws CodeException If the dictionary exists
	 */
	public Dictionary createDictionary(String dictionaryName, StudyBean study) throws CodeException;


}
