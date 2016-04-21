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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.TermDAO;
import com.clinovo.exception.CodeException;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;
import com.clinovo.service.TermService;

@Transactional
@Service("termService")
public class TermServiceImpl implements TermService {
	
	@Autowired
	private TermDAO termDAO;
	
	public List<Term> findAll() {
		return termDAO.findAll();
	}
	
	public Term findTerm(int termId) {
		return termDAO.findById(termId);
	}

	public Term findTerm(String preferredName) {
		return termDAO.findByName(preferredName);
	}

	public List<Term> findTerm(Dictionary dictionary) {
		return termDAO.findByDictionary(dictionary);
	}

    public Term findByTermAndExternalDictionary(String prefTerm, String externalDictionaryName) {
        return termDAO.findByTermAndExternalDictionary(prefTerm, externalDictionaryName);
    }

	public Term findByAliasAndExternalDictionary(String localAlias, String externalDictionaryName) {
		return termDAO.findByAliasAndExternalDictionary(localAlias, externalDictionaryName);
	}
	
	public List<Term> findByExternalDictionary(String externalDictionaryName) {
		return termDAO.findByExternalDictionary(externalDictionaryName);
	}

	public Term findByNonUniqueTermAndExternalDictionary(String localAlias, String dictionary) {

		List<Term> terms = termDAO.findAll();

		for (Term term : terms) {
			
			if (term.getLocalAlias().equalsIgnoreCase(localAlias)
					&& term.getExternalDictionaryName().equalsIgnoreCase(dictionary)) {
				
				return term;
			}
		}

		return null;
	}
	
	public Term saveTerm(Term term) throws CodeException {

		if (doesTermExist(term)) {

			throw new CodeException("Term already exists in the dictionary");

		}

		return termDAO.saveOrUpdate(term);
	}

	public void deleteTerm(Term term) {
		termDAO.deleteTerm(term);
	}
	
	private boolean doesTermExist(Term term) {

		List<Term> terms = findAll();

		for (Term x : terms) {

			if (x.getPreferredName().equals(term.getPreferredName())
					&& x.getDictionary().equals(term.getDictionary())
                    && x.getLocalAlias().equals(term.getLocalAlias())
                    && x.getExternalDictionaryName().equals(term.getExternalDictionaryName())) {

				return true;
			}
		}

		return false;
	}

}
