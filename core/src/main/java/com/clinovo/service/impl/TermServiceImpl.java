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
					&& x.getDictionary().equals(term.getDictionary())) {

				return true;
			}
		}

		return false;
	}

}
