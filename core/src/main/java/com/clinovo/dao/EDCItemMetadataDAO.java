package com.clinovo.dao;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Event definition item metadata DAO.
 */
@Repository
@SuppressWarnings("unchecked")
public class EDCItemMetadataDAO extends AbstractDomainDao<EDCItemMetadata> {

	@Override
	public Class<EDCItemMetadata> domainClass() {
		return EDCItemMetadata.class;
	}

	/**
	 * Find all EDC Item Metadata by event definition crf id.
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @param crfVersionId int
	 * @return List<EDCItemMetadata>
	 */
	public List<EDCItemMetadata> findAllByEventDefinitionCRFAndVersion(EventDefinitionCRFBean eventDefinitionCRFBean,
																	   int crfVersionId) {
		String query = "from  " + this.getDomainClassName() + " edcim where edcim.eventDefinitionCrfId = :edcId and edcim.crfVersionId = :crfVersionId";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("edcId", eventDefinitionCRFBean.getParentId() == 0 ? eventDefinitionCRFBean.getId()
				: eventDefinitionCRFBean.getParentId());
		q.setInteger("crfVersionId", crfVersionId);
		return (List<EDCItemMetadata>) q.list();
	}

	/**
	 * Find EDCItemMetadata by Event Definition CRF ID, CRF Version ID and Item ID.
	 * @param crfVersionId int
	 * @param edcId int
	 * @param itemId int
	 * @return EDCItemMetadata
	 */
	public EDCItemMetadata findByCRFVersionIDEventDefinitionCRFIdAndItemId(int crfVersionId, int edcId, int itemId) {
		String query = "from  " + this.getDomainClassName() + " edcim where edcim.eventDefinitionCrfId = :edcId"
				+ " and edcim.crfVersionId = :crfVersionId and edcim.itemId = :itemId";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("crfVersionId", crfVersionId);
		q.setInteger("edcId", edcId);
		q.setInteger("itemId", itemId);
		return (EDCItemMetadata) q.uniqueResult();
	}

	/**
	 * Find all EDC Item Metadata by event definition crf id.
	 * @param edcId int
	 * @return List<EDCItemMetadata>
	 */
	public List<EDCItemMetadata> findAllByEventDefinitionCRFId(int edcId) {
		String query = "from  " + this.getDomainClassName() + " edcim where edcim.eventDefinitionCrfId = :edcId";
		Query q = this.getCurrentSession().createQuery(query);
		q.setInteger("edcId", edcId);
		return (List<EDCItemMetadata>) q.list();
	}
}
