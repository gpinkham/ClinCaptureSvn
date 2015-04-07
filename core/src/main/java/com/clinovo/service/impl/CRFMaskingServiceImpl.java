package com.clinovo.service.impl;

import com.clinovo.dao.CRFMaskingDAO;
import com.clinovo.model.CRFMask;
import com.clinovo.service.CRFMaskingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Masking Service implementation.
 */
@Transactional
@Service("crfMaskingService")
public class CRFMaskingServiceImpl implements CRFMaskingService {

	@Autowired
	private CRFMaskingDAO maskingDAO;

	public List<CRFMask> findAll() {
		return maskingDAO.findAll();
	}

	public List<CRFMask> findByUserId(int id) {
		return maskingDAO.findByUserId(id);
	}

	public void saveCRFMask(CRFMask mask) {
		maskingDAO.saveOrUpdate(mask);
	}

	public CRFMask findByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId) {
		return maskingDAO.findByUserIdSiteIdAndCRFId(userId, siteId, crfId);
	}

	public void delete(CRFMask mask) {
		maskingDAO.delete(mask);
	}

	public boolean isEventDefinitionCRFMasked(int crfId, int userId, int studyId) {
		return findByUserIdSiteIdAndCRFId(userId, studyId, crfId) != null;
	}
}
