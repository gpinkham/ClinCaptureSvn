package com.clinovo.service;

import com.clinovo.model.CRFMask;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;

import java.util.ArrayList;
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
	 * @param eventDefinitionCRFid EventDefinitionCRF ID
	 * @param userId UserAccountBean ID
	 * @param studyId StudyBean ID
	 * @return boolean
	 */
	boolean isEventDefinitionCRFMasked(int eventDefinitionCRFid, int userId, int studyId);

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

	/**
	 * This method will remove all masked dedc Beans from the list.
	 *
	 * @param dedcBeans list of DisplayEventDefinitionCRFBean CRF beans.
	 * @param user UserAccountBean.
	 * @return List<DisplayEventDefinitionCRFBean>
	 */
	ArrayList<DisplayEventDefinitionCRFBean> removeMaskedDisplayEventDefinitionCRFBeans(List<DisplayEventDefinitionCRFBean> dedcBeans, UserAccountBean user);

	/**
	 * This method will remove all masked dedc Beans from the list.
	 *
	 * @param decBeans list of DisplayEventCRFBean CRF beans.
	 * @param user UserAccountBean.
	 * @return List<DisplayEventCRFBean>
	 */
	ArrayList<DisplayEventCRFBean> removeMaskedDisplayEventCRFBeans(List<DisplayEventCRFBean> decBeans, UserAccountBean user);

	/**
	 * This method will return all masks in the Study Event Definition.
	 * @param userId UserAccountBean Id.
	 * @param eventDefinitionId StudyEventDefinitionBean id.
	 * @param studyId int
	 * @return List of CRFMasks.
	 */
	List<CRFMask> findAllActiveByUserStudyAndEventDefinitionIds(int userId, int eventDefinitionId, int studyId);

	/**
	 * Returns first event with at least one not masked StudyEventDefinitionBean.
	 * @param sedList List<StudyEventDefinitionBean>
	 * @param userId int
	 * @param studyId int
	 * @return first StudyEventDefinitionBean with at least one not masked StudyEventDefinitionBean
	 */
	StudyEventDefinitionBean returnFirstNotMaskedEvent(List<StudyEventDefinitionBean> sedList, int userId, int studyId);
}
