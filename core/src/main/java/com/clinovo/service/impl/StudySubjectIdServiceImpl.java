package com.clinovo.service.impl;

import com.clinovo.dao.StudySubjectIdDAO;
import com.clinovo.service.StudySubjectIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudySubjectIdServiceImpl implements StudySubjectIdService {

	@Autowired
	private StudySubjectIdDAO studySubjectIdDAO;

	public String getNextStudySubjectId(String studyIdentifier) {
		return studySubjectIdDAO.getNextStudySubjectId(studyIdentifier);
	}
}
