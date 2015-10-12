package com.clinovo.dao;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.EventCRFSectionBean;

@Repository
@SuppressWarnings("unchecked")
public class EventCRFSectionDAO  extends AbstractDomainDao<EventCRFSectionBean> {

	@Override
	public Class<EventCRFSectionBean> domainClass() {
		return EventCRFSectionBean.class;
	}

	public EventCRFSectionBean findByEventCRFIdAndSectionId(int eventCRFId,
			int sectionId) {
		String query = "from " + getDomainClassName()
				+ " ecs where ecs.eventCRFId = :eventCRFId and "
				+ "ecs.sectionId = :sectionId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("eventCRFId", eventCRFId);
		q.setInteger("sectionId", sectionId);
		return q.uniqueResult() == null? new EventCRFSectionBean() : (EventCRFSectionBean) q.uniqueResult();
	}

	public void deleteEventCRFSection(EventCRFSectionBean eventCRFSectionBean) {
		this.getCurrentSession().delete(eventCRFSectionBean);
	}
	
	public List<EventCRFSectionBean> findAllPartiallySavedByEventCRFId(int eventCRFId) {
		String query = "from " + getDomainClassName()
				+ " ecs where ecs.eventCRFId = :eventCRFId and ecs.partialSaved = 'true'";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("eventCRFId", eventCRFId);
		return q.list() == null? new ArrayList<EventCRFSectionBean>() : q.list();
	}
}
