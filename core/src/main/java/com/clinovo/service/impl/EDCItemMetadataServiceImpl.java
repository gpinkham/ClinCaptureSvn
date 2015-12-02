package com.clinovo.service.impl;

import com.clinovo.dao.EDCItemMetadataDAO;
import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.EDCItemMetadataService;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * Event Definition CRF Item Metadata service implementation.
 */
@Transactional
@Service("edcItemMetadataService")
public class EDCItemMetadataServiceImpl implements EDCItemMetadataService {

	@Autowired
	private EDCItemMetadataDAO edcItemMetadataDAO;

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public List<EDCItemMetadata> findAllByEventDefinitionCRFAndVersion(EventDefinitionCRFBean eventDefinitionCRFBean, int crfVersionId) {
		return edcItemMetadataDAO.findAllByEventDefinitionCRFAndVersion(eventDefinitionCRFBean, crfVersionId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EDCItemMetadata> findAllByEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean) {
		return edcItemMetadataDAO.findAllByEventDefinitionCRFId(eventDefinitionCRFBean.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public List<EDCItemMetadata> findAllByEventCRFId(int eventCRFId) {
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCRFDAO.findForSiteByEventCrfId(eventCRFId);
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCRFId);
		return edcItemMetadataDAO.findAllByEventDefinitionCRFAndVersion(eventDefinitionCRFBean,
				eventCRFBean.getCRFVersionId());
	}

	/**
	 * {@inheritDoc}
	 */
	public EDCItemMetadata findByEventCRFAndItemID(int eventCRFId, int itemId) {
		if (eventCRFId == 0) {
			return new EDCItemMetadata();
		}
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCRFDAO.findForSiteByEventCrfId(eventCRFId);
		EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
		EventCRFBean eventCRFBean = (EventCRFBean) eventCRFDAO.findByPK(eventCRFId);
		int edcId = eventDefinitionCRFBean.getParentId() > 0 ? eventDefinitionCRFBean.getParentId() : eventDefinitionCRFBean.getId();
		return edcItemMetadataDAO.findByCRFVersionIDEventDefinitionCRFIdAndItemId(eventCRFBean.getCRFVersionId(), edcId, itemId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EDCItemMetadata findByCRFVersionIDEventDefinitionCRFIDAndItemID(int crfVersion, int edcId, int itemId) {
		return edcItemMetadataDAO.findByCRFVersionIDEventDefinitionCRFIdAndItemId(crfVersion, edcId, itemId);
	}

	/**
	 * {@inheritDoc}
	 */
	public EDCItemMetadata saveOrUpdate(EDCItemMetadata edcItemMetadata) {
		return edcItemMetadataDAO.saveOrUpdate(edcItemMetadata);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateSDVRequiredByEventDefinitionCRF(EventDefinitionCRFBean edcBean, boolean sdvRequired) {
		List<EDCItemMetadata> edcItemMetadataList = findAllByEventDefinitionCRF(edcBean);
		for (EDCItemMetadata edcItemMetadata : edcItemMetadataList) {
			edcItemMetadata.setSdvRequired(sdvRequired ? "1" : "0");
			edcItemMetadataDAO.saveOrUpdate(edcItemMetadata);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public EDCItemMetadata findById(int id) {
		return edcItemMetadataDAO.findById(id);
	}
}
