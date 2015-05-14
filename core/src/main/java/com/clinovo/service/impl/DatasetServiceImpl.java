package com.clinovo.service.impl;

import com.clinovo.service.CRFMaskingService;
import com.clinovo.service.DatasetService;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of DatasetService interface.
 */
@Service("datasetService")
public class DatasetServiceImpl implements DatasetService {

	@Autowired
	private DataSource ds;

	@Autowired
	private CRFMaskingService maskingService;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	/**
	 * {@inheritDoc}
	 */
	public DatasetBean initialDatasetData(int datasetId) {
		return initialDatasetData(datasetId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public DatasetBean create(DatasetBean datasetBean) {
		DatasetDAO datasetDAO = new DatasetDAO(ds);
		return (DatasetBean) datasetDAO.create(datasetBean);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public DatasetBean initialDatasetData(int datasetId, UserAccountBean ub) {
		ItemDAO idao = new ItemDAO(ds);
		DatasetDAO datasetDAO = getDatasetDao();
		DatasetBean dataset = (DatasetBean) datasetDAO.findByPK(datasetId);
		List<String> excludeItems = new ArrayList<String>();
		List<String> eventsAndCrfs = new ArrayList<String>();
		if (dataset.getExcludeItems() != null && !dataset.getExcludeItems().trim().isEmpty()) {
			excludeItems = Arrays.asList(dataset.getExcludeItems().trim().split(","));
		}
		if (dataset.getSedIdAndCRFIdPairs() != null && !dataset.getSedIdAndCRFIdPairs().trim().isEmpty()) {
			eventsAndCrfs = Arrays.asList(dataset.getSedIdAndCRFIdPairs().trim().split(","));
		}
		String sql = dataset.getSQLStatement();
		sql = sql.split("study_event_definition_id in")[1];
		String[] ss = sql.split("and item_id in");
		String sedIds = ss[0];
		String[] sss = ss[1].split("and");
		String itemIds = sss[0];

		datasetDAO.setDefinitionCrfItemTypesExpected();
		logger.debug("begin to execute GetDefinitionCrfItemSql");
		ArrayList alist = datasetDAO.selectDefinitionCrfItems(sedIds, itemIds);
		for (Object anAlist : alist) {
			HashMap row = (HashMap) anAlist;

			if (!isItemFromMaskedCRFs(row, ub)) {
				ItemBean ib = (ItemBean) idao.getEntityFromHashMap(row);
				Integer defId = (Integer) row.get("sed_id");
				String defName = (String) row.get("sed_name");
				String crfName = (String) row.get("crf_name");
				Integer crfVersionId = (Integer) row.get("cv_version_id");
				String crfVersionName = (String) row.get("cv_name");
				Integer itemId = ib.getId();
				String key = defId + "_" + crfVersionId + "_" + itemId;
				if (!dataset.getItemMap().containsKey(key)) {
					ib.setDefId(defId);
					ib.setSelected(isItemSelected(key, excludeItems, eventsAndCrfs));
					ib.setDefName(defName);
					ib.setCrfName(crfName);
					ib.setDatasetItemMapKey(key);
					ItemFormMetadataBean imf = new ItemFormMetadataBean();
					imf.setCrfVersionName(crfVersionName);
					imf.setCrfVersionId(crfVersionId);
					ib.setItemMeta(imf);
					if (!dataset.getEventIds().contains(defId)) {
						dataset.getEventIds().add(defId);
					}
					dataset.getItemIds().add(itemId);
					dataset.getItemDefCrf().add(ib);
					dataset.getItemMap().put(key, ib);
				}
			} else if (itemShouldBeChecked(row, eventsAndCrfs)) {
				dataset.setContainsMaskedCRFs(true);
			}
		}
		dataset.setSubjectGroupIds(datasetDAO.getGroupIds(dataset.getId()));
		Collections.sort(dataset.getItemDefCrf(), new ItemBean.ItemBeanComparator());
		return dataset;
	}

	private boolean itemShouldBeChecked(HashMap row, List<String> eventsAndCRFs) {
		Integer defId = (Integer) row.get("sed_id");
		Integer crfVersionId = (Integer) row.get("cv_version_id");
		String keyEventCRF = defId + "_" + crfVersionId;
		return eventsAndCRFs.size() != 0 && eventsAndCRFs.contains(keyEventCRF);
	}

	private boolean isItemSelected(String key, List<String> excludeItems, List<String> eventsAndCRFs) {
		String [] arguments = key.split("_");
		String keyEventCRF = arguments[0] + "_" + arguments[1];
		return eventsAndCRFs.size() != 0 ? eventsAndCRFs.contains(keyEventCRF) && !excludeItems.contains(key) : !excludeItems.contains(key);
	}

	private boolean isItemFromMaskedCRFs(HashMap map, UserAccountBean ub) {
		if (ub != null) {
			int crfId = (Integer) map.get("crf_id");
			int sedId = (Integer) map.get("sed_id");
			EventDefinitionCRFDAO edcDao = new EventDefinitionCRFDAO(ds);
			EventDefinitionCRFBean edcBean = edcDao.findByStudyEventDefinitionIdAndCRFIdAndStudyId(sedId, crfId, ub.getActiveStudyId());
			return maskingService.isEventDefinitionCRFMasked(edcBean.getId(), ub.getId(), ub.getActiveStudyId());
		}
		return false;
	}

	private DatasetDAO getDatasetDao() {
		return new DatasetDAO(ds);
	}
}
