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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.Date;
import java.util.List;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.DictionaryDAO;
import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Status.DictionaryType;
import com.clinovo.service.DictionaryService;

@Transactional
@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {

	@Autowired DictionaryDAO dictionaryDAO;
	
	public List<Dictionary> findAll() {
		return dictionaryDAO.findAll();
	}
	
	public Dictionary findDictionary(int dictionaryId) {
		return dictionaryDAO.findById(dictionaryId);
	}

	public Dictionary findDictionary(String dictionaryName) {
		return dictionaryDAO.findByName(dictionaryName);
	}
	
	public Dictionary findByStudy(int study) {
		return dictionaryDAO.findByStudy(study);
	}

	public Dictionary saveDictionary(Dictionary dictionary) {
		return dictionaryDAO.saveOrUpdate(dictionary);
	}

	public void deleteDictionary(Dictionary dictionary) {
		dictionaryDAO.deleteDictionary(dictionary);
	}

	public Dictionary createDictionary(String dictionaryName, StudyBean study) throws CodeException {
		
		// check if dictionary with similar name exists
		if(doesDictionaryExist(dictionaryName)) {
			
			throw new CodeException("A dictionary with a similar name exists");
			
		} else {
			
			Dictionary dictionary = new Dictionary();
			
			dictionary.setStudy(study.getId());
			dictionary.setName(dictionaryName);
			dictionary.setDateCreated(new Date());
			dictionary.setType(DictionaryType.CUSTOM.ordinal());
			dictionary.setDescription("Automatically created by the system to support auto-coding");
			
			return saveDictionary(dictionary);
		}
		
	}

	private boolean doesDictionaryExist(String dictionaryName) {
		
		List<Dictionary> dictionaries = dictionaryDAO.findAll();
		
		for(Dictionary dictionary : dictionaries) {
			
			if(dictionary.getName().equals(dictionaryName)) {
				return true;
			}
		}
		
		return false;
	}
}
