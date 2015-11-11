package com.clinovo.dao;

import com.clinovo.model.ItemRenderMetadata;
import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 * DB access layer for ItemRenderMetadata.
 */
@Repository
public class ItemRenderMetadataDAO extends AbstractDomainDao<ItemRenderMetadata> {
	@Override
	public Class<ItemRenderMetadata> domainClass() {
		return ItemRenderMetadata.class;
	}

	/**
	 * Find metadata by CRF version and Item ID.
	 * @param crfVersionId CRFVersionBean ID.
	 * @param itemId ItemBean ID.
	 * @return ItemRenderMetadata
	 */
	public ItemRenderMetadata findByCrfVersionAndItemID(int crfVersionId, int itemId) {
		String query = "from " + getDomainClassName() + " irm where irm.crfVersionId = :crfVersionId and irm.itemId = :itemId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("crfVersionId", crfVersionId);
		q.setInteger("itemId", itemId);
		return (ItemRenderMetadata) q.uniqueResult();
	}
}
