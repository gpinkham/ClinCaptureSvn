package com.clinovo.service.impl;

import com.clinovo.enums.CurrentDataEntryStage;
import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.DataEntryService;
import com.clinovo.service.DisplayItemService;
import com.clinovo.service.EDCItemMetadataService;
import com.clinovo.util.DataEntryUtil;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.view.form.FormBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DisplayItemService Implementation.
 */
@Service("displayItemService")
@SuppressWarnings({"rawtypes", "unchecked"})
public class DisplayItemServiceImpl implements DisplayItemService {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	@Autowired
	private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;
	@Autowired
	private DataEntryService dataEntryService;
	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	private DynamicsMetadataService dynamicsMetadataService;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String EVENT_DEF_CRF_BEAN = "event_def_crf_bean";
	public static final String SECTION_BEAN = "section_bean";
	public static final String INPUT_EVENT_CRF = "event";
	public static final String GROUP_HAS_DATA = "groupHasData";
	public static final String HAS_DATA_FLAG = "hasDataFlag";

	/**
	 * {@inheritDoc}
	 */
	public List<DisplayItemGroupBean> loadFormValueForItemGroup(DisplayItemGroupBean displayItemGroupBean,
																List<DisplayItemGroupBean> dataBaseGroups,
																List<DisplayItemGroupBean> formGroups,
																HttpServletRequest request) {
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		int eventDefCRFId = eventDefinitionCRFBean.getId();
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		int repeatMax = displayItemGroupBean.getGroupMetaBean().getRepeatMax();
		FormProcessor fp = new FormProcessor(request);
		ItemDAO idao = new ItemDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO metaDao = new ItemFormMetadataDAO(dataSource);
		List<ItemBean> itBeans = idao.findAllItemsByGroupId(displayItemGroupBean.getItemGroupBean().getId(), sb.getCRFVersionId());
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

		FormBeanUtil formBeanUtil = new FormBeanUtil();
		List<String> nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(eventDefCRFId, dataSource);

		Map<Integer, List<ItemDataBean>> itemDataCache = FormBeanUtil.getItemDataCache(sb.getId(), ecb.getId(), iddao,
				false);
		Map<Integer, ItemFormMetadataBean> itemFormMetadataCache = FormBeanUtil.getItemFormMetadataCache(
				ecb.getCRFVersionId(), metaDao);

		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		ItemGroupBean igb = displayItemGroupBean.getItemGroupBean();

		for (int i = 0; i < repeatMax; i++) {
			if (!DataEntryUtil.rowPresentInRequest(fp, igb.getOid(), i)) {
				continue;
			}
			DisplayItemGroupBean formGroup = new DisplayItemGroupBean();
			List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, itemDataCache,
					itemFormMetadataCache, ecb, sb.getId(), nullValuesList, dynamicsMetadataService);
			dibs = processInputForGroupItem(fp, dibs, i, displayItemGroupBean);
			formGroup.setItems(dibs);
			formGroup.setItemGroupBean(displayItemGroupBean.getItemGroupBean());
			formGroup.setGroupMetaBean(runDynamicsCheck(displayItemGroupBean.getGroupMetaBean(), request));
			formGroup.setFormInputOrdinal(i);
			formGroup.setOrdinal(i);
			formGroup.setInputId(igb.getOid() + "_" + i);
			formGroups.add(formGroup);
		}
		Collections.sort(formGroups);
		return setEditFlagsAndPopulateGroupItemsWithData(dataBaseGroups, formGroups);
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadItemsWithGroupRows(DisplayItemWithGroupBean itemWithGroup, SectionBean sb,
										  EventDefinitionCRFBean edcb, EventCRFBean ecb, HttpServletRequest request) {
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		ItemDAO idao = new ItemDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO metaDao = new ItemFormMetadataDAO(dataSource);
		FormBeanUtil formBeanUtil = new FormBeanUtil();
		List<String> nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(edcb.getId(), dataSource);
		ArrayList data = iddao.findAllActiveBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
		DisplayItemGroupBean itemGroup = itemWithGroup.getItemGroup();
		DisplayItemBean firstItem = getDisplayItemBeanForFirstItemDataBean(itemGroup.getItems(), data);

		itemWithGroup.setPageNumberLabel(firstItem.getMetadata().getPageNumberLabel());

		itemWithGroup.setItemGroup(itemGroup);
		itemWithGroup.setInGroup(true);
		itemWithGroup.setOrdinal(itemGroup.getGroupMetaBean().getOrdinal());

		Map<Integer, List<ItemDataBean>> itemDataCache = FormBeanUtil.getItemDataCache(data, false);
		Map<Integer, ItemFormMetadataBean> itemFormMetadataCache = FormBeanUtil.getItemFormMetadataCache(
				ecb.getCRFVersionId(), metaDao);

		List<ItemBean> itBeans = idao.findAllItemsByGroupId(itemGroup.getItemGroupBean().getId(), sb.getCRFVersionId());

		boolean hasData = false;
		int checkAllColumns = 0;
		for (Object aData : data) {
			ItemDataBean idb = (ItemDataBean) aData;

			logger.debug("check all columns: " + checkAllColumns);
			if (idb.getItemId() == firstItem.getItem().getId()) {
				hasData = true;
				logger.debug("set has data to --TRUE--");
				checkAllColumns = 0;
				DisplayItemGroupBean digb = new DisplayItemGroupBean();
				List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, itemDataCache,
						itemFormMetadataCache, ecb, sb.getId(), edcb, idb.getOrdinal(), dynamicsMetadataService);

				digb.setItems(dibs);
				logger.trace("set with dibs list of : " + dibs.size());
				digb.setGroupMetaBean(runDynamicsCheck(itemGroup.getGroupMetaBean(), request));
				digb.setItemGroupBean(itemGroup.getItemGroupBean());
				itemWithGroup.getItemGroups().add(digb);
				itemWithGroup.getDbItemGroups().add(digb);
			}
		}
		List<DisplayItemGroupBean> groupRows = itemWithGroup.getItemGroups();

		if (hasData) {
			for (DisplayItemGroupBean displayGroup : groupRows) {
				for (DisplayItemBean dib : displayGroup.getItems()) {
					for (Object aData : data) {
						ItemDataBean idb = (ItemDataBean) aData;
						if (idb.getItemId() == dib.getItem().getId() && !idb.isSelected()) {
							idb.setSelected(true);
							dib.setData(idb);
							if (dataEntryService.shouldLoadDBValues(dib, getDataEntryStage(request))) {
								dib.loadDBValue();
							}
							break;
						}
					}
				}
			}
		} else {
			DisplayItemGroupBean digb2 = new DisplayItemGroupBean();
			List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, itemDataCache,
					itemFormMetadataCache, ecb, sb.getId(), nullValuesList, dynamicsMetadataService);
			digb2.setItems(dibs);
			digb2.setEditFlag("initial");
			digb2.setGroupMetaBean(itemGroup.getGroupMetaBean());
			digb2.setItemGroupBean(itemGroup.getItemGroupBean());
			itemWithGroup.getItemGroups().add(digb2);
			itemWithGroup.getDbItemGroups().add(digb2);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DisplayItemWithGroupBean> createItemWithGroups(DisplaySectionBean dsb, boolean hasItemGroup,
																  int eventCRFDefId, HttpServletRequest request) {
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		HttpSession session = request.getSession();
		List<DisplayItemWithGroupBean> displayItemWithGroups = new ArrayList<DisplayItemWithGroupBean>();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		ItemDAO idao = new ItemDAO(dataSource);
		ItemDataDAO iddao = new ItemDataDAO(dataSource);
		ItemFormMetadataDAO metaDao = new ItemFormMetadataDAO(dataSource);
		FormBeanUtil formBeanUtil = new FormBeanUtil();
		List<String> nullValuesList = new ArrayList<String>();
		SectionBean sb = (SectionBean) request.getAttribute(SECTION_BEAN);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		nullValuesList = formBeanUtil.getNullValuesByEventCRFDefId(eventCRFDefId, dataSource);
		ArrayList items = dsb.getItems();

		for (Object item1 : items) {
			DisplayItemBean item = (DisplayItemBean) item1;
			DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
			EDCItemMetadata edcItemMetadata = edcItemMetadataService.findByEventCRFAndItemID(dsb.getEventCRF().getId(), item.getItem().getId());
			item.setEdcItemMetadata(edcItemMetadata);
			newOne.setSingleItem(DataEntryUtil.runDynamicsItemCheck(getDynamicsMetadataService(), item, ecb));
			newOne.setOrdinal(item.getMetadata().getOrdinal());
			newOne.setInGroup(false);
			newOne.setPageNumberLabel(item.getMetadata().getPageNumberLabel());
			displayItemWithGroups.add(newOne);
		}

		if (hasItemGroup) {
			List<ItemDataBean> data = iddao.findAllActiveBySectionIdAndEventCRFId(sb.getId(), ecb.getId());
			Map<Integer, List<ItemDataBean>> itemDataCache = FormBeanUtil.getItemDataCache(data, false);
			Map<Integer, ItemFormMetadataBean> itemFormMetadataCache = FormBeanUtil.getItemFormMetadataCache(
					ecb.getCRFVersionId(), metaDao);
			if (data != null && data.size() > 0) {
				session.setAttribute(HAS_DATA_FLAG, true);
			}

			for (DisplayItemGroupBean itemGroup : dsb.getDisplayFormGroups()) {
				logger.debug("found one itemGroup");
				DisplayItemWithGroupBean newOne = new DisplayItemWithGroupBean();
				DisplayItemBean firstItem = getDisplayItemBeanForFirstItemDataBean(itemGroup.getItems(), data);

				newOne.setPageNumberLabel(firstItem.getMetadata().getPageNumberLabel());
				newOne.setItemGroup(itemGroup);
				newOne.setInGroup(true);
				newOne.setOrdinal(itemGroup.getGroupMetaBean().getOrdinal());

				List<ItemBean> itBeans = idao.findAllItemsByGroupId(itemGroup.getItemGroupBean().getId(),
						sb.getCRFVersionId());

				boolean hasData = false;
				for (ItemDataBean aData : data) {
					ItemDataBean idb = (ItemDataBean) aData;
					if (idb.getItemId() == firstItem.getItem().getId()) {
						hasData = true;
						DisplayItemGroupBean digb = new DisplayItemGroupBean();
						List<DisplayItemBean> dibs = FormBeanUtil
								.getDisplayBeansFromItems(itBeans, itemDataCache, itemFormMetadataCache, ecb,
										sb.getId(), edcb, idb.getOrdinal(), dynamicsMetadataService);
						digb.setItems(dibs);
						digb.setGroupMetaBean(runDynamicsCheck(itemGroup.getGroupMetaBean(), request));
						digb.setItemGroupBean(itemGroup.getItemGroupBean());
						newOne.getItemGroups().add(digb);
						newOne.getDbItemGroups().add(digb);
					}
				}
				List<DisplayItemGroupBean> groupRows = newOne.getItemGroups();
				if (hasData) {
					session.setAttribute(GROUP_HAS_DATA, Boolean.TRUE);
					for (DisplayItemGroupBean displayGroup : groupRows) {
						for (DisplayItemBean dib : displayGroup.getItems()) {
							for (ItemDataBean aData : data) {
								ItemDataBean idb = aData;
								if (idb.getItemId() == dib.getItem().getId()
										&& idb.getOrdinal() == dib.getData().getOrdinal() && !idb.isSelected()) {
									idb.setSelected(true);
									dib.setData(idb);
									if (dataEntryService.shouldLoadDBValues(dib, getDataEntryStage(request))) {
										dib.loadDBValue();
									}
									break;
								}
							}
						}
					}
				} else {
					session.setAttribute(GROUP_HAS_DATA, Boolean.FALSE);
					DisplayItemGroupBean digb2 = new DisplayItemGroupBean();
					List<DisplayItemBean> dibs = FormBeanUtil.getDisplayBeansFromItems(itBeans, itemDataCache,
							itemFormMetadataCache, ecb, sb.getId(), nullValuesList, dynamicsMetadataService);
					digb2.setItems(dibs);
					logger.trace("set with nullValuesList of : " + nullValuesList);
					digb2.setEditFlag("initial");
					digb2.setGroupMetaBean(itemGroup.getGroupMetaBean());
					digb2.setItemGroupBean(itemGroup.getItemGroupBean());
					newOne.getItemGroups().add(digb2);
					newOne.getDbItemGroups().add(digb2);
				}
				displayItemWithGroups.add(newOne);
			}
		}
		Collections.sort(displayItemWithGroups);
		return displayItemWithGroups;
	}

	private List<DisplayItemGroupBean> setEditFlagsAndPopulateGroupItemsWithData(List<DisplayItemGroupBean> dbGroups,
																				  List<DisplayItemGroupBean> formGroups) {
		int previous = -1;

		for (DisplayItemGroupBean formItemGroup : formGroups) {
			if (formItemGroup.getOrdinal() == previous) {
				formItemGroup.setEditFlag("edit");
				formItemGroup.setOrdinal(previous + 1);
			}
			if (formItemGroup.getOrdinal() > dbGroups.size() - 1) {
				formItemGroup.setEditFlag("add");
			} else {
				for (int i = 0; i < dbGroups.size(); i++) {
					DisplayItemGroupBean dbItemGroup = dbGroups.get(i);
					if (formItemGroup.getOrdinal() == i) {
						if ("initial".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
							formItemGroup.setEditFlag("add");
						} else {
							dbItemGroup.setEditFlag("edit");
							for (DisplayItemBean dib : dbItemGroup.getItems()) {
								ItemDataBean data = dib.getData();
								for (DisplayItemBean formDib : formItemGroup.getItems()) {
									if (formDib.getItem().getId() == dib.getItem().getId()) {
										formDib.getData().setId(data.getId());
										formDib.setDbData(dib.getData());
										break;
									}
								}
							}
							formItemGroup.setEditFlag("edit");
						}
						break;
					}
				}
			}
			previous = formItemGroup.getOrdinal();
		}
		for (DisplayItemGroupBean dbItemGroup : dbGroups) {
			if (!"edit".equalsIgnoreCase(dbItemGroup.getEditFlag())
					&& !"initial".equalsIgnoreCase(dbItemGroup.getEditFlag())) {
				if (dbItemGroup.getGroupMetaBean().isShowGroup()) {
					dbItemGroup.setEditFlag("remove");
				}
			}
		}
		for (int j = 0; j < formGroups.size(); j++) {
			DisplayItemGroupBean formGroup = formGroups.get(j);
			formGroup.setIndex(j);
		}
		return formGroups;
	}

	private DynamicsMetadataService getDynamicsMetadataService() {
		if (dynamicsMetadataService == null) {
			dynamicsMetadataService = new DynamicsMetadataService(dynamicsItemFormMetadataDao,
					dynamicsItemGroupMetadataDao, dataSource);
		}
		return dynamicsMetadataService;
	}

	private ItemGroupMetadataBean runDynamicsCheck(ItemGroupMetadataBean metadataBean, HttpServletRequest request) {
		DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
		EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		try {
			if (!metadataBean.isShowGroup()) {
				boolean showGroup = dynamicsMetadataService.isGroupShown(metadataBean.getId(), ecb);
				if (request.getAttribute("currentDataEntryStage") == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
					showGroup = dynamicsMetadataService.hasGroupPassedDDE(metadataBean.getId(), ecb.getId());
				}
				metadataBean.setShowGroup(showGroup);
			}
		} catch (OpenClinicaException oce) {
			logger.debug("throws an OCE for " + metadataBean.getId());
		}
		return metadataBean;
	}

	private List<DisplayItemBean> processInputForGroupItem(FormProcessor fp, List<DisplayItemBean> dibs, int i,
														   DisplayItemGroupBean digb) {
		for (DisplayItemBean displayItem : dibs) {
			String inputName = DataEntryUtil.getGroupItemInputName(digb, i, displayItem);
			ResponseType rt = displayItem.getMetadata().getResponseSet().getResponseType();
			if (rt.equals(ResponseType.CHECKBOX) || rt.equals(ResponseType.SELECTMULTI)) {
				ArrayList valueArray = fp.getStringArray(inputName);
				displayItem.loadFormValue(valueArray);
			} else {
				displayItem.loadFormValue(fp.getString(inputName));
				if (rt.equals(ResponseType.SELECT)) {
					ensureSelectedOption(displayItem);
				}
			}
		}
		return dibs;
	}

	private void ensureSelectedOption(DisplayItemBean displayItemBean) {
		if (displayItemBean == null || displayItemBean.getData() == null) {
			return;
		}
		ItemDataBean itemDataBean = displayItemBean.getData();
		String dataValue = itemDataBean.getValue();
		if ("".equalsIgnoreCase(dataValue)) {
			return;
		}

		ResponseSetBean responseSetBean = displayItemBean.getMetadata().getResponseSet();
		if (responseSetBean == null) {
			return;
		}
		List<ResponseOptionBean> responseOptionBeans = responseSetBean.getOptions();
		String tempVal;
		for (ResponseOptionBean responseOptionBean : responseOptionBeans) {
			tempVal = responseOptionBean.getValue();
			if (tempVal != null && tempVal.equalsIgnoreCase(dataValue)) {
				responseOptionBean.setSelected(true);
			}
		}
	}

	private DisplayItemBean getDisplayItemBeanForFirstItemDataBean(List<DisplayItemBean> items,
																   List<ItemDataBean> dataItems) {
		for (DisplayItemBean displayItemBean : items) {
			for (ItemDataBean itemDataBean : dataItems) {
				if (displayItemBean.getItem().getId() == itemDataBean.getItemId()) {
					return displayItemBean;
				}
			}
		}
		return items.get(0);
	}

	private CurrentDataEntryStage getDataEntryStage(HttpServletRequest request) {
		return (CurrentDataEntryStage) request.getAttribute("currentDataEntryStage");
	}
}
