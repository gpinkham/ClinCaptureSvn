package org.akaza.openclinica.dao.hibernate;

import java.util.ArrayList;

import org.akaza.openclinica.domain.datamap.StudyEvent;
import org.akaza.openclinica.domain.datamap.StudySubject;

public class StudySubjectDao extends AbstractDomainDao<StudySubject> {

	@Override
	public Class<StudySubject> domainClass() {
		return StudySubject.class;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public ArrayList<StudyEvent> fetchListSEs(String id) {
		String query = " from StudyEvent se where se.studySubject.ocOid = :id order by se.studyEventDefinition.ordinal,se.sampleOrdinal";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("id", id.toString());

		return (ArrayList<StudyEvent>) q.list();
	}
}