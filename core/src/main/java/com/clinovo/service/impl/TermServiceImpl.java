package com.clinovo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.dao.TermDAO;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Term;
import com.clinovo.service.TermService;

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

	public Term findTermByCode(String code) {
		return termDAO.findByCode(code);
	}

	public List<Term> findTerm(Dictionary dictionary) {
		return termDAO.findByDictionary(dictionary);
	}

	public Term saveTerm(Term term) {
		return termDAO.saveOrUpdate(term);
	}

	public void deleteTerm(Term term) {
		termDAO.deleteTerm(term);
	}

}
