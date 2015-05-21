package com.clinovo.service;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;

/**
 * Service to work with datasets.
 */
public interface DatasetService {

	/**
	 * Create DatasetBean.
	 * @param datasetBean DatasetBean
	 * @return EntityBean
	 */
	DatasetBean create(DatasetBean datasetBean);

	/**
	* Initialize itemMap, itemIds, itemDefCrf and groupIds for a DatasetBean.
	* @param datasetId int
	* @param ub UserAccountBean
	* @return DatasetBean
	*/
	DatasetBean initialDatasetData(int datasetId, UserAccountBean ub);

	/**
	 * Initialize itemMap, itemIds, itemDefCrf and groupIds for a DatasetBean.
	 * @param datasetId int
	 * @return DatasetBean
	 */
	DatasetBean initialDatasetData(int datasetId);
}
