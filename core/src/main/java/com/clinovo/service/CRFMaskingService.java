package com.clinovo.service;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;

import java.util.List;

/**
 * CRF Masking service interface.
 */
public interface CRFMaskingService {

	/**
	 * Find all CRF Masks.
	 *
	 * @return List of CRF Masks
	 */
	List<CRFMask> findAll();

	/**
	 * Find all CRF Masks by user ID.
	 *
	 * @param id User Account ID
	 * @return List of CRF Masks
	 */
	List<CRFMask> findByUserId(int id);

	/**
	 * Save CRF Mask.
	 *
	 * @param mask CRF Mask to be saved
	 */
	void saveCRFMask(CRFMask mask);

	/**
	 * Find CRF Mask by user ID, site ID, crf ID.
	 *
	 * @param userId UserAccountBean ID
	 * @param siteId StudyBean ID
	 * @param crfId  EventDefinitionCRF ID
	 * @return CRF Mask
	 */
	CRFMask findByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId);

	/**
	 * Find Active CRF Mask by UserAccountBean ID, StudyBean ID and EventDefinitionCRF ID.
	 *
	 * @param userId UserAccountBeanID
	 * @param siteId StudyBeanID
	 * @param crfId EventDefinitionCRFID
	 * @return CRF Mask
	 */
	CRFMask findActiveByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId);

	/**
	 * Delete CRF Mask.
	 *
	 * @param mask CRF Mask to be deleted
	 */
	void delete(CRFMask mask);

	/**
	 * Check if current eventCRF is masked.
	 *
	 * @param crfId EventDefinitionCRF ID
	 * @param userId UserAccountBean ID
	 * @param studyId StudyBean ID
	 * @return boolean
	 */
	boolean isEventDefinitionCRFMasked(int crfId, int userId, int studyId);

	/**
	 * This method will remove rows in masking table is user role will be set as Investigator,
	 * and restore rows if old role was Investigator.
	 *
	 * @param oldRole role that was updated.
	 * @param newRole new role that was set.
	 * @param study site on which Masks should be restored or removed.
	 * @param userId user for which Masks should be restored or removed.
	 */
	void updateMasksOnUserRoleUpdate(Role oldRole, Role newRole, StudyBean study, int userId);
}
