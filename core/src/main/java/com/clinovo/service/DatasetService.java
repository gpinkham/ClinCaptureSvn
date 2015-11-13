package com.clinovo.service;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;

/**
 * Service to work with datasets.
 */
public interface DatasetService {

	/**
	 * Removes dataset.
	 * 
	 * @param datasetBean
	 *            DatasetBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeDataset(DatasetBean datasetBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores dataset.
	 *
	 * @param datasetBean
	 *            DatasetBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreDataset(DatasetBean datasetBean, UserAccountBean updater) throws Exception;

	/**
	 * Locks dataset.
	 *
	 * @param datasetBean
	 *            DatasetBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void lockDataset(DatasetBean datasetBean, UserAccountBean updater) throws Exception;

	/**
	 * Unlocks dataset.
	 *
	 * @param datasetBean
	 *            DatasetBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void unlockDataset(DatasetBean datasetBean, UserAccountBean updater) throws Exception;

	/**
	 * Removes datasets.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void removeDatasets(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores datasets.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void restoreDatasets(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Locks datasets.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void lockDatasets(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Unlocks datasets.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @param updater
	 *            UserAccountBean
	 * @throws Exception
	 *             an Exception
	 */
	void unlockDatasets(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Create DatasetBean.
	 * 
	 * @param datasetBean
	 *            DatasetBean
	 * @return EntityBean
	 */
	DatasetBean create(DatasetBean datasetBean);

	/**
	 * Initialize itemMap, itemIds, itemDefCrf and groupIds for a DatasetBean.
	 * 
	 * @param datasetId
	 *            int
	 * @param ub
	 *            UserAccountBean
	 * @return DatasetBean
	 */
	DatasetBean initialDatasetData(int datasetId, UserAccountBean ub);
}
