/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.MeasurementUnitOidGenerator;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.admin.SpreadSheetItemUtil;
import org.akaza.openclinica.control.form.spreadsheet.OnChangeSheetValidator;
import org.akaza.openclinica.control.form.spreadsheet.SheetValidationContainer;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.enums.OperationType;
import com.clinovo.lib.crf.producer.ErrorMessageProducer;
import com.clinovo.lib.crf.service.ImportCrfService;

/**
 * BaseCrfBuilder.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseCrfBuilder implements CrfBuilder {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseCrfBuilder.class);

	public static final int INT_5 = 5;
	public static final int INT_1000 = 1000;

	public static final String UNGROUPED = "Ungrouped";
	public static final String GROUP_LAYOUT = "GROUP_LAYOUT";
	public static final String WIDTH_DECIMAL = "width_decimal";

	// import service
	private ImportCrfService importCrfService;

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
	private String currentVariable;
	private ItemBeanExt currentItem;
	private String currentHeader = "";
	private SectionBean currentSection;
	private StringBuffer currentMessage;
	private ItemGroupBean currentItemGroup;
	private StringBuffer currentScoreValidatorErrorsBuffer;

	// assistants
	private int sectionOrdinal;
	private int itemMetadataOrdinal;
	private int groupMetadataOrdinal;
	private boolean hasWidthDecimalColumn;
	private List codingRefItemNames = new ArrayList();
	private Set<String> existingUnits = new TreeSet<String>();
	private Map<String, SectionBean> sectionLabelMap = new HashMap<String, SectionBean>();
	private Map<String, ItemBeanExt> itemNameToItemMap = new HashMap<String, ItemBeanExt>();
	private Map<String, ItemGroupBean> itemGroupLabelMap = new HashMap<String, ItemGroupBean>();
	private Map<String, ItemFormMetadataBean> itemNameToMetaMap = new HashMap<String, ItemFormMetadataBean>();
	private Map<String, ItemGroupMetadataBean> itemGroupLabelToMetaMap = new HashMap<String, ItemGroupMetadataBean>();

	// resource bundles
	private ResourceBundle pageMessagesResourceBundle;

	// validation assistants
	private List refItemNames = new ArrayList();
	private SheetValidationContainer sheetContainer;
	private OnChangeSheetValidator instantValidator;
	private List<String> resPairs = new ArrayList<String>();
	private List<String> resNames = new ArrayList<String>();
	private List<String> itemNames = new ArrayList<String>();
	private List<String> groupNames = new ArrayList<String>();
	private List<String> sectionNames = new ArrayList<String>();
	private Map<String, String> labelWithType = new HashMap<String, String>();
	private Map<String, String> optionsTextMap = new HashMap<String, String>();
	private Map<String, String> optionsValuesMap = new HashMap<String, String>();
	private Map<String, String[]> controlValues = new HashMap<String, String[]>();
	private Map<String, String[]> labelWithOptionsText = new HashMap<String, String[]>();
	private Map<String, String[]> labelWithOptionsValues = new HashMap<String, String[]>();
	private List<SpreadSheetItemUtil> spreadSheetItems = new ArrayList<SpreadSheetItemUtil>();

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
	 * @param pageMessagesResourceBundle
	 *            ResourceBundle
	 * @param importCrfService
	 *            ImportCrfService
	 */
	public BaseCrfBuilder(UserAccountBean owner, StudyBean studyBean, DataSource dataSource, Locale locale,
			ResourceBundle pageMessagesResourceBundle, ImportCrfService importCrfService) {
		this.owner = owner;
		this.locale = locale;
		groupNames.add(UNGROUPED);
		this.studyBean = studyBean;
		this.dataSource = dataSource;
		this.importCrfService = importCrfService;
		sheetContainer = new SheetValidationContainer();
		this.pageMessagesResourceBundle = pageMessagesResourceBundle;
		instantValidator = new OnChangeSheetValidator(sheetContainer, pageMessagesResourceBundle);
	}

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

	public String getCurrentHeader() {
		return currentHeader;
	}

	public void setCurrentHeader(String currentHeader) {
		this.currentHeader = currentHeader;
	}

	public UserAccountBean getOwner() {
		return owner;
	}

	public ItemGroupBean getDefaultItemGroupBean() {
		return defaultItemGroupBean;
	}

	public void setDefaultItemGroupBean(ItemGroupBean defaultItemGroupBean) {
		this.defaultItemGroupBean = defaultItemGroupBean;
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

	public String getCurrentVariable() {
		return currentVariable;
	}

	public void setCurrentVariable(String currentVariable) {
		this.currentVariable = currentVariable;
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

	public Map<String, SectionBean> getSectionLabelMap() {
		return sectionLabelMap;
	}

	public Map<String, ItemGroupBean> getItemGroupLabelMap() {
		return itemGroupLabelMap;
	}

	public Map<String, ItemGroupMetadataBean> getItemGroupLabelToMetaMap() {
		return itemGroupLabelToMetaMap;
	}

	public Map<String, ItemBeanExt> getItemNameToItemMap() {
		return itemNameToItemMap;
	}

	public List getCodingRefItemNames() {
		return codingRefItemNames;
	}

	/**
	 * Returns true if sheet has width decimal column.
	 * 
	 * @return boolean
	 */
	public boolean hasWidthDecimalColumn() {
		return hasWidthDecimalColumn;
	}

	public void setHasWidthDecimalColumn(boolean hasWidthDecimalColumn) {
		this.hasWidthDecimalColumn = hasWidthDecimalColumn;
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

	public ResourceBundle getPageMessagesResourceBundle() {
		return pageMessagesResourceBundle;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public List getRefItemNames() {
		return refItemNames;
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

	public List<SpreadSheetItemUtil> getSpreadSheetItems() {
		return spreadSheetItems;
	}

	public Map<String, String> getOptionsTextMap() {
		return optionsTextMap;
	}

	public Map<String, String> getOptionsValuesMap() {
		return optionsValuesMap;
	}

	/**
	 * Returns last SpreadSheetItemUtil.
	 * 
	 * @return SpreadSheetItemUtil
	 */
	public SpreadSheetItemUtil getLastSpreadSheetItem() {
		return spreadSheetItems.get(spreadSheetItems.size() - 1);
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
	 * Returns cell value.
	 * 
	 * @param cell
	 *            Cell
	 * @return String
	 */
	public String getValue(Cell cell) {
		String val;
		int cellType;
		if (cell == null) {
			cellType = Cell.CELL_TYPE_BLANK;
		} else {
			cellType = cell.getCellType();
		}

		switch (cellType) {
			case Cell.CELL_TYPE_BLANK :
				val = "";
				break;
			case Cell.CELL_TYPE_NUMERIC :
				val = cell.getNumericCellValue() + "";
				double dphi = cell.getNumericCellValue();
				if ((dphi - (int) dphi) * INT_1000 == 0) {
					val = (int) dphi + "";
				}
				break;
			case Cell.CELL_TYPE_STRING :
				val = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN :
				boolean val2 = cell.getBooleanCellValue();
				if (val2) {
					val = "true";
				} else {
					val = "false";
				}
				break;
			default :
				val = "";
		}
		return val.trim();
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
	 * Returns ErrorMessageProducer.
	 *
	 * @return ErrorMessageProducer
	 */
	public abstract ErrorMessageProducer getErrorMessageProducer();

	/**
	 * {@inheritDoc}
	 */
	public void build() throws Exception {
		importCrfService.importNewCrf(this);
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

	/**
	 * {@inheritDoc}
	 */
	public CRFVersionBean save() {
		Connection connection = null;
		try {
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

			// create crf & crf version
			if (crfBean.getId() > 0) {
				crfBean = (CRFBean) crfDao.findByPK(crfBean.getId());
			} else {
				crfDao.create(crfBean, connection);
			}
			crfVersionBean.setCrfId(crfBean.getId());
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
					String unitOid = measurementUnitOidGenerator.generateOid(itemBean.getUnits());
					itemDao.createMeasurementUnit(unitOid, itemBean.getUnits(), connection);
					existingUnits.add(itemBean.getUnits());
				}

				// create item meta
				itemBean.getItemMeta().setItemId(itemBean.getId());
				if (itemBean.getParentItemBean() != null) {
					itemBean.getItemMeta().setParentId(itemBean.getParentItemBean().getId());
					itemBean.getItemMeta().setParentLabel(itemBean.getParentItemBean().getName());
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
		return crfVersionBean;
	}
}
