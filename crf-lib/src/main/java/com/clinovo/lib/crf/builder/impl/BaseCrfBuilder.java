/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.builder.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.extract.SasNameValidator;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.oid.MeasurementUnitOidGenerator;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.spreadsheet.OnChangeSheetValidator;
import org.akaza.openclinica.control.form.spreadsheet.SheetValidationContainer;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.enums.OperationType;
import com.clinovo.lib.crf.producer.ErrorMessageProducer;
import com.clinovo.lib.crf.service.ImportCrfService;
import com.clinovo.service.ItemRenderMetadataService;

/**
 * BaseCrfBuilder.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseCrfBuilder implements CrfBuilder {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseCrfBuilder.class);

	public static final int INT_1000 = 1000;

	public static final String UNGROUPED = "Ungrouped";
	public static final String GROUP_LAYOUT = "GROUP_LAYOUT";
	public static final String WIDTH_DECIMAL = "width_decimal";

	// import service
	private ImportCrfService importCrfService;

	private ItemRenderMetadataService itemRenderMetadataService;

	// data source
	private DataSource dataSource;

	// operation type
	private OperationType operationType;

	// beans
	private Locale locale;
	private StudyBean studyBean;
	private UserAccountBean owner;
	private boolean isRepeating = false;
	private CRFBean crfBean = new CRFBean();
	private ItemGroupBean defaultItemGroupBean;
	private List<ItemBean> items = new ArrayList<ItemBean>();
	private CRFVersionBean crfVersionBean = new CRFVersionBean();
	private List<SectionBean> sections = new ArrayList<SectionBean>();
	private List<ItemGroupBean> itemGroups = new ArrayList<ItemGroupBean>();

	// current beans
	private ItemBeanExt currentItem;
	private SectionBean currentSection;
	private StringBuffer currentMessage;
	private ItemGroupBean currentItemGroup;
	private StringBuffer currentScoreValidatorErrorsBuffer;

	// assistants
	private int sectionOrdinal;
	private int itemMetadataOrdinal;
	private int groupMetadataOrdinal;
	private List codingRefItemNames = new ArrayList();
	private Set<String> existingUnits = new TreeSet<String>();
	private List<String> sysItemNames = new ArrayList<String>();
	private Map<String, SectionBean> sectionLabelMap = new HashMap<String, SectionBean>();
	private Map<String, ItemBeanExt> itemNameToItemMap = new HashMap<String, ItemBeanExt>();
	private Map<String, ItemGroupBean> itemGroupLabelMap = new HashMap<String, ItemGroupBean>();
	private Map<String, ItemFormMetadataBean> itemNameToMetaMap = new HashMap<String, ItemFormMetadataBean>();
	private Map<String, ItemGroupMetadataBean> itemGroupLabelToMetaMap = new HashMap<String, ItemGroupMetadataBean>();
	private Map<String, List<ItemBeanExt>> parentNameToChildrenMap = new HashMap<String, List<ItemBeanExt>>();

	// message source
	private MessageSource messageSource;

	// validation assistants
	private SheetValidationContainer sheetContainer;
	private OnChangeSheetValidator instantValidator;
	private List<String> resPairs = new ArrayList<String>();
	private List<String> resNames = new ArrayList<String>();
	private List<String> itemNames = new ArrayList<String>();
	private List<String> groupNames = new ArrayList<String>();
	private List<String> sectionNames = new ArrayList<String>();
	private Map<String, String> labelWithType = new HashMap<String, String>();
	private Map<String, String> optionsTextMap = new HashMap<String, String>();
	private Map<String, String> groupSectionMap = new HashMap<String, String>();
	private Map<String, String> optionsValuesMap = new HashMap<String, String>();
	private Map<String, String[]> controlValues = new HashMap<String, String[]>();
	private Map<String, String[]> labelWithOptionsText = new HashMap<String, String[]>();
	private Map<String, String[]> labelWithOptionsValues = new HashMap<String, String[]>();
	private List<ItemGroupCrvVersionUtil> itemGroupCrfRecords = new ArrayList<ItemGroupCrvVersionUtil>();

	// error
	private Map<String, String> errorsMap = new HashMap<String, String>();
	private List<String> errorsList = new ArrayList<String>();

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            UserAccountBean
	 * @param studyBean
	 *            StudyBean
	 * @param dataSource
	 *            DataSource
	 * @param locale
	 *            Locale
	 * @param messageSource
	 *            MessageSource
	 * @param importCrfService
	 *            ImportCrfService
	 * @param metadataService
	 *            ItemRenderMetadataService
	 */
	public BaseCrfBuilder(UserAccountBean owner, StudyBean studyBean, DataSource dataSource, Locale locale,
			MessageSource messageSource, ImportCrfService importCrfService, ItemRenderMetadataService metadataService) {
		this.owner = owner;
		this.locale = locale;
		groupNames.add(UNGROUPED);
		this.studyBean = studyBean;
		this.dataSource = dataSource;
		this.messageSource = messageSource;
		this.importCrfService = importCrfService;
		this.itemRenderMetadataService = metadataService;
		sheetContainer = new SheetValidationContainer();
		instantValidator = new OnChangeSheetValidator(sheetContainer, ResourceBundleProvider.getPageMessagesBundle());
	}

	/**
	 * Sets crf source.
	 */
	protected abstract void setCrfSource();

	/**
	 * Returns ErrorMessageProducer.
	 *
	 * @return ErrorMessageProducer
	 */
	public abstract ErrorMessageProducer getErrorMessageProducer();

	/**
	 * Reformats strings correctly considering the \\, and \,.
	 *
	 * @param value
	 *            String
	 * @return String
	 */
	public String reformat(String value) {
		return value.replace("\\\\,", "##").replace("\\,", "##").replace("##", "\\,");
	}

	/**
	 * Splits strings correctly.
	 * 
	 * @param value
	 *            String
	 * @param splitter
	 *            String
	 * @return String[]
	 */
	public String[] split(String value, String splitter) {
		String[] values = value.replace("\\,", "##").split(splitter);
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].replace("##", "\\,");
		}
		return values;
	}

	public StudyBean getStudyBean() {
		return studyBean;
	}

	public UserAccountBean getOwner() {
		return owner;
	}

	public ItemGroupBean getDefaultItemGroupBean() {
		return defaultItemGroupBean;
	}

	/**
	 * Sets default item group. And sets min & max rows.
	 *
	 * @param defaultItemGroupBean
	 *            ItemGroupBean
	 */
	public void setDefaultItemGroupBean(ItemGroupBean defaultItemGroupBean) {
		this.defaultItemGroupBean = defaultItemGroupBean;
		this.defaultItemGroupBean.setMeta(new ItemGroupMetadataBean());
		this.defaultItemGroupBean.getMeta().setRepeatingGroup(false);
		this.defaultItemGroupBean.getMeta().setRepeatMax(1);
		this.defaultItemGroupBean.getMeta().setRepeatNum(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public CRFBean getCrfBean() {
		return crfBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public CRFVersionBean getCrfVersionBean() {
		return crfVersionBean;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<SectionBean> getSections() {
		return sections;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ItemGroupBean> getItemGroups() {
		return itemGroups;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ItemBean> getItems() {
		return items;
	}

	public boolean isRepeating() {
		return isRepeating;
	}

	public void setIsRepeating(boolean isRepeating) {
		this.isRepeating = isRepeating;
	}

	public SectionBean getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(SectionBean section) {
		this.currentSection = section;
	}

	public ItemBeanExt getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(ItemBeanExt currentItem) {
		this.currentItem = currentItem;
	}

	public ItemGroupBean getCurrentItemGroup() {
		return currentItemGroup;
	}

	public void setCurrentItemGroup(ItemGroupBean currentItemGroup) {
		this.currentItemGroup = currentItemGroup;
	}

	public StringBuffer getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(StringBuffer currentMessage) {
		this.currentMessage = currentMessage;
	}

	public int getNextSectionOrdinal() {
		return ++sectionOrdinal;
	}

	public int getNextItemMetadataOrdinal() {
		return ++itemMetadataOrdinal;
	}

	public int getNextGroupMetadataOrdinal() {
		return ++groupMetadataOrdinal;
	}

	public Map<String, ItemFormMetadataBean> getItemNameToMetaMap() {
		return itemNameToMetaMap;
	}

	public List<String> getSysItemNames() {
		return sysItemNames;
	}

	public Map<String, SectionBean> getSectionLabelMap() {
		return sectionLabelMap;
	}

	public Map<String, ItemGroupBean> getItemGroupLabelMap() {
		return itemGroupLabelMap;
	}

	public Map<String, ItemGroupMetadataBean> getItemGroupLabelToMetaMap() {
		return itemGroupLabelToMetaMap;
	}

	public Map<String, List<ItemBeanExt>> getParentNameToChildrenMap() {
		return parentNameToChildrenMap;
	}

	public Map<String, ItemBeanExt> getItemNameToItemMap() {
		return itemNameToItemMap;
	}

	public List getCodingRefItemNames() {
		return codingRefItemNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHtmlTable() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getErrorsList() {
		return errorsList;
	}

	public Map<String, String> getErrorsMap() {
		return errorsMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Map> createCrfMetaObject() {
		return new HashMap<String, Map>();
	}

	public Locale getLocale() {
		return locale;
	}

	public List<String> getSectionNames() {
		return sectionNames;
	}

	public List<String> getGroupNames() {
		return groupNames;
	}

	public List<String> getItemNames() {
		return itemNames;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * Returns message.
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getMessage(String code) {
		return getMessage(code, null);
	}

	/**
	 * Returns message.
	 * 
	 * @param code
	 *            String
	 * @param args
	 *            Object[]
	 * @return String
	 */
	public String getMessage(String code, Object[] args) {
		return messageSource.getMessage(code, args, locale);
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public Map<String, String> getGroupSectionMap() {
		return groupSectionMap;
	}

	public List<ItemGroupCrvVersionUtil> getItemGroupCrfRecords() {
		return itemGroupCrfRecords;
	}

	public Map<String, String[]> getLabelWithOptionsValues() {
		return labelWithOptionsValues;
	}

	public Map<String, String[]> getLabelWithOptionsText() {
		return labelWithOptionsText;
	}

	public Map<String, String> getLabelWithType() {
		return labelWithType;
	}

	public Map<String, String[]> getControlValues() {
		return controlValues;
	}

	public Map<String, String> getOptionsTextMap() {
		return optionsTextMap;
	}

	public Map<String, String> getOptionsValuesMap() {
		return optionsValuesMap;
	}

	public List<String> getResPairs() {
		return resPairs;
	}

	public List<String> getResNames() {
		return resNames;
	}

	public OnChangeSheetValidator getInstantValidator() {
		return instantValidator;
	}

	public SheetValidationContainer getSheetContainer() {
		return sheetContainer;
	}

	public StringBuffer getCurrentScoreValidatorErrorsBuffer() {
		return currentScoreValidatorErrorsBuffer;
	}

	public void setCurrentScoreValidatorErrorsBuffer(StringBuffer currentScoreValidatorErrorsBuffer) {
		this.currentScoreValidatorErrorsBuffer = currentScoreValidatorErrorsBuffer;
	}

	/**
	 * Returns true if current object is ExcelCrfBuilder.
	 * 
	 * @return boolean
	 */
	public boolean isExcelCrfBuilder() {
		return this instanceof ExcelCrfBuilder;
	}

	/**
	 * Returns true if current object is JsonCrfBuilder.
	 * 
	 * @return boolean
	 */
	public boolean isJsonCrfBuilder() {
		return this instanceof JsonCrfBuilder;
	}

	/**
	 * Returns DataSource.
	 *
	 * @return DataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Prepares itemGroupCrfRecords collection.
	 */
	public void prepareItemGroupCrfRecords() {
		// all items with group / version info from db
		itemGroupCrfRecords = new ItemDAO(dataSource)
				.findAllWithItemGroupCRFVersionMetadataByCRFId(getCrfBean().getName());
	}

	/**
	 * Returns type of operation.
	 *
	 * @return OperationType
	 */
	public OperationType getOperationType() {
		return operationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void build() throws Exception {
		importCrfService.importNewCrf(this);
		setCrfSource();
	}

	/**
	 * {@inheritDoc}
	 */
	public void build(int crfId) throws Exception {
		importCrfService.importNewCrfVersion(this, crfId);
		setCrfSource();
	}

	private Map<String, ItemGroupBean> existingGroupsMap(ItemGroupDAO itemGroupDao) {
		// prepare already existing groups map
		Map<String, ItemGroupBean> existingGroupsMap = new HashMap<String, ItemGroupBean>();
		if (operationType == OperationType.IMPORT_NEW_CRF_VERSION) {
			List<ItemGroupBean> existingItemGroupBeans = itemGroupDao.findAllActiveByCrf(crfBean);
			for (ItemGroupBean existingItemGroupBean : existingItemGroupBeans) {
				existingGroupsMap.put(existingItemGroupBean.getName(), existingItemGroupBean);
			}
		}
		return existingGroupsMap;
	}

	private Map<String, ItemBean> existingItemsMap(ItemDAO itemDao) {
		// prepare already existing items map
		Map<String, ItemBean> existingItemsMap = new HashMap<String, ItemBean>();
		if (operationType == OperationType.IMPORT_NEW_CRF_VERSION) {
			List<ItemBean> existingItemBeans = itemDao.findAllActiveByCRF(crfBean);
			for (ItemBean existingItemBean : existingItemBeans) {
				existingItemsMap.put(existingItemBean.getName(), existingItemBean);
			}
		}
		return existingItemsMap;
	}

	private void createItemRenderMeta() {
		try {
			for (ItemBean item : items) {
				ItemBeanExt itemBean = (ItemBeanExt) item;
				// create item render meta
				if (itemBean.getItemRenderMetadata() != null && (itemBean.getItemRenderMetadata().getWidth() != 0
						|| itemBean.getItemRenderMetadata().getLeftItemTextWidth() != 0)) {
					itemBean.getItemRenderMetadata().setItemId(itemBean.getId());
					itemBean.getItemRenderMetadata().setCrfVersionId(crfVersionBean.getId());
					itemRenderMetadataService.save(itemBean.getItemRenderMetadata());
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public CRFVersionBean save() {
		Connection connection = null;
		boolean operationSuccessful = false;
		SasNameValidator sasNameValidator = new SasNameValidator();
		try {
			List<String> unitOidList = new ArrayList<String>();
			List<String> itemOidList = new ArrayList<String>();
			List<String> itemGroupOidList = new ArrayList<String>();
			Map<String, ItemFormMetadataBean> itemNameToMetaMap = new HashMap<String, ItemFormMetadataBean>();

			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			CRFDAO crfDao = new CRFDAO(dataSource);
			ItemDAO itemDao = new ItemDAO(dataSource);
			SectionDAO sectionDao = new SectionDAO(dataSource);
			ItemGroupDAO itemGroupDao = new ItemGroupDAO(dataSource);
			CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
			ItemFormMetadataDAO itemFormMetadataDao = new ItemFormMetadataDAO(dataSource);
			ItemGroupMetadataDAO itemGroupMetadataDao = new ItemGroupMetadataDAO(dataSource);
			MeasurementUnitOidGenerator measurementUnitOidGenerator = new MeasurementUnitOidGenerator();
			measurementUnitOidGenerator.setDataSource(dataSource);

			// prepare already existing items map & existing groups map
			Map<String, ItemBean> existingItemsMap = existingItemsMap(itemDao);
			Map<String, ItemGroupBean> existingGroupsMap = existingGroupsMap(itemGroupDao);

			processSCDForChildItems();

			// create crf & crf version
			if (crfBean.getId() > 0) {
				crfBean = (CRFBean) crfDao.findByPK(crfBean.getId());
			} else {
				crfDao.create(crfBean, connection);
			}
			crfVersionBean.setCrfId(crfBean.getId());
			crfVersionBean.setCrfName(crfBean.getName());
			crfVersionDao.create(crfVersionBean, connection);

			// create sections
			for (SectionBean sectionBean : sections) {
				sectionBean.setCRFVersionId(crfVersionBean.getId());
				sectionDao.create(sectionBean, connection);
			}

			// create groups
			for (ItemGroupBean itemGroupBean : itemGroups) {
				if (!existingGroupsMap.keySet().contains(itemGroupBean.getName())) {
					itemGroupBean.setCrfId(crfVersionBean.getCrfId());
					itemGroupBean.setOid(itemGroupDao.getValidOid(itemGroupBean, crfBean.getName(),
							itemGroupBean.getName(), (ArrayList) itemGroupOidList));
					itemGroupOidList.add(itemGroupBean.getOid());
					itemGroupDao.create(itemGroupBean, connection);
				} else {
					itemGroupBean.setId(existingGroupsMap.get(itemGroupBean.getName()).getId());
				}
			}

			// create items, meta & versioning maps
			for (ItemBean item : items) {
				ItemBeanExt itemBean = (ItemBeanExt) item;
				// create or update item
				if (!existingItemsMap.keySet().contains(itemBean.getName())) {
					itemBean.setSasName(sasNameValidator.getValidName(itemBean.getName()));
					itemBean.setOid(itemDao.getValidOid(itemBean, crfBean.getName(), itemBean.getName(),
							(ArrayList) itemOidList));
					itemDao.create(itemBean, connection);
					itemOidList.add(itemBean.getOid());
				} else {
					ItemBean oldItemBean = existingItemsMap.get(itemBean.getName());
					oldItemBean.setDescription(itemBean.getDescription());
					oldItemBean.setPhiStatus(itemBean.isPhiStatus());
					itemOidList.add(oldItemBean.getOid());
					itemBean.setId(oldItemBean.getId());
					if (owner.getId() == oldItemBean.getOwnerId()) {
						if (!crfVersionDao.hasItemData(oldItemBean.getId())) {
							oldItemBean.setUnits(itemBean.getUnits());
							oldItemBean.setItemDataTypeId(itemBean.getItemDataTypeId());
						} else
							if (oldItemBean.getItemDataTypeId() == ItemDataType.DATE.getId()
									&& itemBean.getItemDataTypeId() == ItemDataType.PDATE.getId()) {
							oldItemBean.setItemDataTypeId(itemBean.getItemDataTypeId());
						}
					}
					itemDao.update(oldItemBean, connection);
				}

				// create versioning map
				itemDao.createVersioningMap(itemBean.getId(), crfVersionBean.getId(), connection);

				// create response set
				Integer responseSetId = itemDao.findExistingResponseSetId(crfVersionBean.getId(),
						itemBean.getResponseSet(), connection);
				if (responseSetId == null) {
					itemDao.createResponseSet(crfVersionBean.getId(), itemBean.getResponseSet(), connection);
				} else {
					itemBean.getResponseSet().setResponseSetId(responseSetId);
				}

				// create measurement unit
				if (itemBean.getUnits() != null && itemBean.getUnits().length() > 0
						&& !existingUnits.contains(itemBean.getUnits())
						&& !itemDao.doesMeasurementUnitExist(itemBean.getUnits(), connection)) {
					String unitOid = itemDao.getValidUnitOid(measurementUnitOidGenerator, itemBean.getUnits(),
							unitOidList);
					itemDao.createMeasurementUnit(unitOid, itemBean.getUnits(), connection);
					existingUnits.add(itemBean.getUnits());
					unitOidList.add(unitOid);
				}

				// create item meta
				itemBean.getItemMeta().setItemId(itemBean.getId());
				if (itemBean.getParentItemBean() != null) {
					itemBean.getItemMeta().setParentId(0);
					itemBean.getItemMeta().setParentLabel("");
					itemBean.getItemMeta().setColumnNumber(1);
					itemBean.getItemMeta().setPseudoChild(true);
				}
				itemBean.getItemMeta().setSectionId(itemBean.getSectionBean().getId());
				itemBean.getItemMeta().setCrfVersionId(crfVersionBean.getId());
				itemBean.getItemMeta().setResponseSetId(itemBean.getResponseSet().getResponseSetId());
				itemFormMetadataDao.create(itemBean.getItemMeta(), connection);
				itemNameToMetaMap.put(itemBean.getName(), itemBean.getItemMeta());

				// create group meta
				itemBean.getItemGroupBean().getMeta().setItemGroupId(itemBean.getItemGroupBean().getId());
				itemBean.getItemGroupBean().getMeta().setCrfVersionId(crfVersionBean.getId());
				itemBean.getItemGroupBean().getMeta().setItemId(itemBean.getId());
				itemBean.getItemGroupBean().getMeta().setOrdinal(getNextGroupMetadataOrdinal());
				itemGroupMetadataDao.create(itemBean.getItemGroupBean().getMeta(), connection);

				// create simple conditional display
				if (itemBean.getSimpleConditionalDisplayBean() != null) {
					itemDao.createSCDItemMeta(
							itemNameToMetaMap.get(itemBean.getName()).getId(), itemNameToMetaMap
									.get(itemBean.getSimpleConditionalDisplayBean().getControlItemName()).getId(),
							itemBean.getSimpleConditionalDisplayBean(), connection);
				}
			}

			connection.commit();
			operationSuccessful = true;
		} catch (Exception e) {
			LOGGER.error("Error has occurred.", e);
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		}
		// create item render meta
		if (operationSuccessful) {
			createItemRenderMeta();
		}
		return crfVersionBean;
	}

	/**
	 * Puts current item bean into the parentNameToChildrenMap, if it has a parent.
	 */
	public void addCurrentItemToChildrenMap() {

		ItemBeanExt currentItem = getCurrentItem();
		if (currentItem != null && currentItem.getParentItemBean() != null
				&& StringUtils.isNotEmpty(currentItem.getParentItemBean().getName())) {

			String parentItemName = currentItem.getParentItemBean().getName();
			if (parentNameToChildrenMap.get(parentItemName) == null) {
				List<ItemBeanExt> children = new ArrayList<ItemBeanExt>();
				children.add(currentItem);
				parentNameToChildrenMap.put(parentItemName, children);
			} else {
				parentNameToChildrenMap.get(parentItemName).add(currentItem);
			}
		}
	}

	private void processSCDForChildItems() {

		for (Map.Entry<String, List<ItemBeanExt>> entry : parentNameToChildrenMap.entrySet()) {
			String parentItemName = entry.getKey();
			List<ItemBeanExt> itemBeanExtList = entry.getValue();
			ItemBeanExt parentItem = itemNameToItemMap.get(parentItemName);
			if (parentItem.hasSCD()) {
				boolean copyParentSCDToChildItems = true;
				for (ItemBeanExt childItem : itemBeanExtList) {
					if (childItem.hasSCD()) {
						copyParentSCDToChildItems = false;
						break;
					}
				}
				if (copyParentSCDToChildItems) {
					SimpleConditionalDisplayBean parentSCDBean = parentItem.getSimpleConditionalDisplayBean();
					for (ItemBeanExt childItem : itemBeanExtList) {
						childItem.setSimpleConditionalDisplayBean(new SimpleConditionalDisplayBean(parentSCDBean));
						childItem.getItemMeta().setShowItem(false);
					}
				}
			}
		}
	}
}
