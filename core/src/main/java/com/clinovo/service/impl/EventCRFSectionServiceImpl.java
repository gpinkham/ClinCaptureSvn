package com.clinovo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.dao.EventCRFSectionDAO;
import com.clinovo.model.EventCRFSectionBean;
import com.clinovo.service.EventCRFSectionService;

@Transactional
@Service("eventCRFSectionService")
public class EventCRFSectionServiceImpl implements EventCRFSectionService{

	@Autowired EventCRFSectionDAO eventCRFSectionDAO;
	
	public EventCRFSectionBean findById(int id) {
		return eventCRFSectionDAO.findById(id);
	}
	
	public EventCRFSectionBean findByEventCRFIdAndSectionId(int eventCRFId, int sectionId) {
		return eventCRFSectionDAO.findByEventCRFIdAndSectionId(eventCRFId, sectionId);
	}
	
	public EventCRFSectionBean saveEventCRFSectionBean(EventCRFSectionBean eventCRFSectionBean) {
		return eventCRFSectionDAO.saveOrUpdate(eventCRFSectionBean);
	}

	public void deleteEventCRFSectionBean(EventCRFSectionBean eventCRFSectionBean) {
		eventCRFSectionDAO.deleteEventCRFSection(eventCRFSectionBean);
	}

	public List<EventCRFSectionBean> findAllByEventCRFId(int eventCRFId) {
		return eventCRFSectionDAO.findAllByEventCRFId(eventCRFId);
	}

	public Map<Integer, EventCRFSectionBean> getSectionIdToEvCRFSectionMap(int eventCRFId) {
		Map<Integer, EventCRFSectionBean> result = new HashMap<Integer, EventCRFSectionBean>();
		List<EventCRFSectionBean> list = findAllByEventCRFId(eventCRFId);
		for (EventCRFSectionBean ecsb: list) {
			result.put(ecsb.getSectionId(), ecsb);
		}
		
		return result;
	}
}
