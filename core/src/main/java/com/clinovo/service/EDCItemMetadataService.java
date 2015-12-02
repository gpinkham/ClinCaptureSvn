package com.clinovo.service;

import com.clinovo.model.EDCItemMetadata;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;

import java.util.List;

/**
 * Event Definition CRF Item Metadata service.
 */
public interface EDCItemMetadataService {

	/**
	 * Find all EDC Item Metadata by event definition crf id.
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @param crfVersionId int
	 * @return List<EDCItemMetadata>
	 */
	List<EDCItemMetadata> findAllByEventDefinitionCRFAndVersion(EventDefinitionCRFBean eventDefinitionCRFBean,
																	   int crfVersionId);

	/**
	 * Find all EDC Item Metadata by event definition crf id.
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @return List<EDCItemMetadata>
	 */
	List<EDCItemMetadata> findAllByEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean);

	/**
	 * Find all EDC Item Metadata by event crf id.
	 * @param eventCRFId int
	 * @return List<EDCItemMetadata>
	 */
	List<EDCItemMetadata> findAllByEventCRFId(int eventCRFId);

	/**
	 * Find EDCItemMetadata by Event CRF ID and Item ID.
	 * @param eventCRFId int
	 * @param itemId int
	 * @return EDCItemMetadata
	 */
	EDCItemMetadata findByEventCRFAndItemID(int eventCRFId, int itemId);

	/**
	 * Find EDCItemMetadata by CRF Version ID, Event Definition CRF ID and Item ID.
	 * @param crfVersion int
	 * @param edcId int
	 * @param itemId int
	 * @return EDCItemMetadata
	 */
	EDCItemMetadata findByCRFVersionIDEventDefinitionCRFIDAndItemID(int crfVersion, int edcId, int itemId);

	/**
	 * Save or Update.
	 * @param edcItemMetadata EDCItemMetadata
	 * @return EDCItemMetadata
	 */
	EDCItemMetadata saveOrUpdate(EDCItemMetadata edcItemMetadata);

	/**
	 * Update SDV required for all items by Event Definition CRF Bean.
	 * @param edcBean EventDefinitionCRFBean
	 * @param sdvRequired boolean
	 */
	void updateSDVRequiredByEventDefinitionCRF(EventDefinitionCRFBean edcBean, boolean sdvRequired);

	/**
	 * Find EDCItemMetadataById.
	 * @param id int
	 * @return EDCItemMetadata
	 */
	EDCItemMetadata findById(int id);
}
