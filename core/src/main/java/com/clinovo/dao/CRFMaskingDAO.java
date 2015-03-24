package com.clinovo.dao;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CRF Masking DAO class.
 */
@Repository
public class CRFMaskingDAO extends AbstractDomainDao<CRFMask> {

	@Override
	public Class<CRFMask> domainClass() {
		return CRFMask.class;
	}

	/**
	 * Retrieves all the masks from the database.
	 *
	 * @return List of all masks
	 */
	public List<CRFMask> findAll() {
		String query = "from  " + this.getDomainClassName();
		Query q = this.getCurrentSession().createQuery(query);
		return (List<CRFMask>) q.list();
	}

	/**
	 * Retrieves CRFs Masks for specific user.
	 *
	 * @param id The ID of widget to filter on.
	 * @return Widget selected by id
	 */
	public List<CRFMask> findByUserId(int id) {
		String query = "from " + getDomainClassName() + " cm where cm.userId = :id";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("id", id);
		return (List<CRFMask>) q.list();
	}

	/**
	 * Find CRF Mask by user ID, site ID, crf ID.
	 *
	 * @param userId int
	 * @param siteId int
	 * @param crfId  int
	 * @return CRF Mask
	 */
	public CRFMask findByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId) {
		String query = "from " + getDomainClassName()
				+ " cm where cm.userId = :userId and "
				+ "cm.eventDefinitionCrfId = :crfId and cm.studyId = :siteId";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("userId", userId);
		q.setInteger("crfId", crfId);
		q.setInteger("siteId", siteId);
		return (CRFMask) q.uniqueResult();
	}

	/**
	 * Delete CRF Mask.
	 *
	 * @param mask CRF Mask to be deleted
	 */
	public void delete(CRFMask mask) {
		getCurrentSession().delete(mask);
	}
}



