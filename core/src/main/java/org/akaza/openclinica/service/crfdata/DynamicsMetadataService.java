/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.service.crfdata;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.akaza.openclinica.domain.crfdata.DynamicsItemGroupMetadataBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.managestudy.DiscrepancyNoteService;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.util.DAOWrapper;
import com.clinovo.util.EventCRFUtil;
import com.clinovo.util.SubjectEventStatusUtil;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class DynamicsMetadataService implements MetadataServiceInterface {
	public static final int FOUR = 4;
	public static final int THREE = 3;
	public static final int TWO = 2;
	public static final int ONE = 1;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private static final String ESCAPED_SEPERATOR = "\\.";
	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;
	private DataSource ds;
	private ExpressionService expressionService;
	private DiscrepancyNoteService discrepancyNoteService;

	/**
	 * DynamicsMetadataService constructor.
	 *
	 * @param dynamicsItemFormMetadataDao
	 *            DynamicsItemFormMetadataDao
	 * @param dynamicsItemGroupMetadataDao
	 *            DynamicsItemGroupMetadataDao
	 * @param ds
	 *            DataSource
	 */
	public DynamicsMetadataService(DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao,
			DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao, DataSource ds) {
		this.ds = ds;
		this.expressionService = new ExpressionService(ds);
		this.discrepancyNoteService = new DiscrepancyNoteService(ds);
		this.dynamicsItemFormMetadataDao = dynamicsItemFormMetadataDao;
		this.dynamicsItemGroupMetadataDao = dynamicsItemGroupMetadataDao;
	}

	public boolean hide(Object metadataBean, EventCRFBean eventCrfBean) {
		ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
		itemFormMetadataBean.setShowItem(false);
		DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(itemFormMetadataBean,
				eventCrfBean);
		dynamicsMetadataBean.setShowItem(false);
		getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		return true;
	}

	public boolean isShown(Object metadataBean, EventCRFBean eventCrfBean) {
		ItemFormMetadataBean itemFormMetadataBean = (ItemFormMetadataBean) metadataBean;
		DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean,
				eventCrfBean, null);
		if (dynamicsMetadataBean != null) {
			return dynamicsMetadataBean.isShowItem();
		} else {
			logger.debug("did not find a row in the db for " + itemFormMetadataBean.getId());
			return false;
		}
	}

	public boolean hasPassedDDE(ItemFormMetadataBean itemFormMetadataBean, EventCRFBean eventCrfBean,
			ItemDataBean itemDataBean) {
		DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(
				itemFormMetadataBean, eventCrfBean, itemDataBean); // findByItemDataBean(itemDataBean);
		if (dynamicsMetadataBean == null) {
			return false;
		}
		return dynamicsMetadataBean.getPassedDde() > 0;
	}

	public boolean isShown(Integer itemId, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
		ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemId,
				eventCrfBean.getCRFVersionId());
		DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean,
				eventCrfBean, itemDataBean);
		return dynamicsMetadataBean != null && dynamicsMetadataBean.isShowItem();
	}

	public DynamicsItemFormMetadataBean getDynamicsItemFormMetadataBean(Integer itemId, EventCRFBean eventCrfBean,
			ItemDataBean itemDataBean) {
		DynamicsItemFormMetadataBean dynamicsMetadataBean;
		ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemId,
				eventCrfBean.getCRFVersionId());
		dynamicsMetadataBean = getDynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean, itemDataBean);
		return dynamicsMetadataBean;
	}

	public boolean isGroupShown(int metadataId, EventCRFBean eventCrfBean) throws OpenClinicaException {
		ItemGroupMetadataBean itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByPK(
				metadataId);
		DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataBean(itemGroupMetadataBean,
				eventCrfBean);
		if (dynamicsMetadataBean != null) {
			return dynamicsMetadataBean.isShowGroup();
		} else {
			return false;
		}

	}

	public boolean hasGroupPassedDDE(int metadataId, int eventCrfBeanId) throws OpenClinicaException {
		ItemGroupMetadataBean itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByPK(
				metadataId);
		DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataBean(itemGroupMetadataBean,
				eventCrfBeanId);
		if (dynamicsMetadataBean == null) {
			return false;
		}
		return dynamicsMetadataBean.getPassedDde() > 0;
	}

	private DynamicsItemFormMetadataBean getDynamicsItemFormMetadataBean(ItemFormMetadataBean metadataBean,
			EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
		ItemFormMetadataBean itemFormMetadataBean = metadataBean;
		DynamicsItemFormMetadataBean dynamicsMetadataBean = null;

		dynamicsMetadataBean = getDynamicsItemFormMetadataDao().findByMetadataBean(itemFormMetadataBean, eventCrfBean,
				itemDataBean);

		return dynamicsMetadataBean;

	}

	private DynamicsItemGroupMetadataBean getDynamicsItemGroupMetadataBean(ItemGroupMetadataBean metadataBean,
			EventCRFBean eventCrfBean) {

		DynamicsItemGroupMetadataBean dynamicsMetadataBean = getDynamicsItemGroupMetadataDao().findByMetadataBean(
				metadataBean, eventCrfBean);
		logger.debug(" returning " + metadataBean.getId() + " " + metadataBean.getItemGroupId() + " "
				+ eventCrfBean.getId());
		return dynamicsMetadataBean;

	}

	private DynamicsItemGroupMetadataBean getDynamicsItemGroupMetadataBean(ItemGroupMetadataBean metadataBean,
			int eventCrfBeanId) {

		DynamicsItemGroupMetadataBean dynamicsMetadataBean = null;
		dynamicsMetadataBean = getDynamicsItemGroupMetadataDao().findByMetadataBean(metadataBean, eventCrfBeanId);
		return dynamicsMetadataBean;

	}

	public boolean showItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
		ItemFormMetadataBean itemFormMetadataBean = metadataBean;
		itemFormMetadataBean.setShowItem(true);
		DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(itemFormMetadataBean,
				eventCrfBean);
		dynamicsMetadataBean.setItemDataId(itemDataBean.getId());
		dynamicsMetadataBean.setShowItem(true);
		dynamicsMetadataBean.setPassedDde(0);
		getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		logger.debug("just touched ifmb id " + metadataBean.getId() + " ecb id " + eventCrfBean.getId() + " item id "
				+ metadataBean.getItemId() + " itemdata id " + itemDataBean.getId());
		return true;
	}

	public boolean hideNewItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
		metadataBean.setShowItem(false);
		DynamicsItemFormMetadataBean dynamicsMetadataBean = new DynamicsItemFormMetadataBean(metadataBean, eventCrfBean);
		dynamicsMetadataBean.setItemDataId(itemDataBean.getId());
		dynamicsMetadataBean.setShowItem(false);
		getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		return true;
	}

	public boolean hideItem(ItemFormMetadataBean metadataBean, EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {
		ItemFormMetadataBean itemFormMetadataBean = metadataBean;
		DynamicsItemFormMetadataBean dynamicsMetadataBean = this.getDynamicsItemFormMetadataDao().findByItemDataBean(
				itemDataBean);
		dynamicsMetadataBean = dynamicsMetadataBean != null && dynamicsMetadataBean.getId() > 0
				? dynamicsMetadataBean
				: new DynamicsItemFormMetadataBean(itemFormMetadataBean, eventCrfBean);
		dynamicsMetadataBean.setShowItem(false);
		getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		return true;
	}

	public boolean showGroup(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean) {

		ItemGroupMetadataBean itemGroupMetadataBean = metadataBean;
		itemGroupMetadataBean.setShowGroup(true);
		DynamicsItemGroupMetadataBean dynamicsMetadataBean = new DynamicsItemGroupMetadataBean(itemGroupMetadataBean,
				eventCrfBean);
		dynamicsMetadataBean.setPassedDde(0);
		getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		return true;
	}

	public boolean hideGroup(ItemGroupMetadataBean metadataBean, EventCRFBean eventCrfBean) {

		ItemGroupMetadataBean itemGroupMetadataBean = metadataBean;
		itemGroupMetadataBean.setShowGroup(false);
		DynamicsItemGroupMetadataBean dynamicsMetadataBean = new DynamicsItemGroupMetadataBean(itemGroupMetadataBean,
				eventCrfBean);
		dynamicsMetadataBean.setPassedDde(0);
		getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsMetadataBean);
		return true;
	}

	public void show(Integer itemDataId, List<PropertyBean> properties, RuleSetBean ruleSet) {
		ItemDataBean itemDataBeanA = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
		EventCRFBean eventCrfBeanA = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBeanA.getEventCRFId());
		for (PropertyBean propertyBean : properties) {
			String oid = propertyBean.getOid();
			ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
			// OID is an item
			if (itemOrItemGroup.getItemBean() != null) {
				ItemDataBean oidBasedItemData = getItemData(itemOrItemGroup.getItemBean(), eventCrfBeanA,
						itemDataBeanA.getOrdinal());
				ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(
						itemOrItemGroup.getItemBean().getId(), eventCrfBeanA.getCRFVersionId());
				DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(
						itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
				if (dynamicsMetadataBean == null) {
					showItem(itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
				} else if (dynamicsMetadataBean != null && !dynamicsMetadataBean.isShowItem()) {
					dynamicsMetadataBean.setShowItem(true);
					getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
				}
			} else {
				// OID is a group
				logger.debug("found item group id 1 " + oid);
				ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
				ArrayList sectionBeans = getSectionDAO().findAllByCRFVersionId(eventCrfBeanA.getCRFVersionId());
				for (int i = 0; i < sectionBeans.size(); i++) {
					SectionBean sectionBean = (SectionBean) sectionBeans.get(i);
					// System.out.println("found section " + sectionBean.getId());
					List<ItemGroupMetadataBean> itemGroupMetadataBeans = getItemGroupMetadataDAO()
							.findMetaByGroupAndSection(itemGroupBean.getId(), eventCrfBeanA.getCRFVersionId(),
									sectionBean.getId());
					for (ItemGroupMetadataBean itemGroupMetadataBean : itemGroupMetadataBeans) {
						if (itemGroupMetadataBean.getItemGroupId() == itemGroupBean.getId()) {
							// System.out.println("found item group id 2 " + oid);
							DynamicsItemGroupMetadataBean dynamicsGroupBean = getDynamicsItemGroupMetadataBean(
									itemGroupMetadataBean, eventCrfBeanA);
							if (dynamicsGroupBean == null) {
								showGroup(itemGroupMetadataBean, eventCrfBeanA);
							} else if (dynamicsGroupBean != null && !dynamicsGroupBean.isShowGroup()) {
								dynamicsGroupBean.setShowGroup(true);
								getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean);
							}
						}
					}
				}
			}
		}
	}

	public void hide(Integer itemDataId, List<PropertyBean> properties) {
		ItemDataBean itemDataBean = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
		EventCRFBean eventCrfBean = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBean.getEventCRFId());
		for (PropertyBean propertyBean : properties) {
			String oid = propertyBean.getOid();
			ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
			// OID is an item
			if (itemOrItemGroup.getItemBean() != null) {
				ItemDataBean oidBasedItemData = getItemData(itemOrItemGroup.getItemBean(), eventCrfBean,
						itemDataBean.getOrdinal());
				ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(
						itemOrItemGroup.getItemBean().getId(), eventCrfBean.getCRFVersionId());
				DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(
						itemFormMetadataBean, eventCrfBean, oidBasedItemData);
				if (dynamicsMetadataBean == null && oidBasedItemData.getValue().equals("")) {
					showItem(itemFormMetadataBean, eventCrfBean, oidBasedItemData);
				} else if (dynamicsMetadataBean != null && dynamicsMetadataBean.isShowItem()
						&& oidBasedItemData.getValue().equals("")) {
					dynamicsMetadataBean.setShowItem(false);
					getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean);
				}
			}
		}
	}

	private Boolean isGroupRepeating(ItemGroupMetadataBean itemGroupMetadataBean) {
		return itemGroupMetadataBean.getRepeatNum() > 1 || itemGroupMetadataBean.getRepeatMax() > 1;
	}

	private ItemDataBean oneToIndexedMany(ItemBean itemBeanB, ItemGroupBean itemGroupBeanB,
			ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub, int index) {

		ItemDataBean theOidBasedItemData = null;
		int size = getItemDataDAO().getGroupSize(itemBeanB.getId(), eventCrfBeanB.getId());
		int maxOrdinal = getItemDataDAO().getMaxOrdinalForGroupByItemAndEventCrf(itemBeanB, eventCrfBeanB);
		if (size > 0 && size >= index) {
			List<ItemDataBean> theItemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(),
					itemBeanB.getId());
			theOidBasedItemData = theItemDataBeans.get(index - 1);
		} else {
			List<ItemBean> items = getItemDAO().findAllItemsByGroupId(itemGroupBeanB.getId(),
					eventCrfBeanB.getCRFVersionId());
			int number = itemGroupMetadataBeanB.getRepeatNum() > index
					? itemGroupMetadataBeanB.getRepeatNum()
					: index <= itemGroupMetadataBeanB.getRepeatMax() ? index : 0;
			for (int ordinal = 1 + maxOrdinal; ordinal <= number + maxOrdinal - size; ordinal++) {
				for (ItemBean itemBeanX : items) {
					ItemDataBean oidBasedItemData = getItemData(itemBeanX, eventCrfBeanB, ordinal);
					if (oidBasedItemData.getId() == 0) {
						oidBasedItemData = createItemData(oidBasedItemData, itemBeanX, ordinal, eventCrfBeanB, ub);
					}
				}
			}
			List<ItemDataBean> theItemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(),
					itemBeanB.getId());
			theOidBasedItemData = theItemDataBeans.get(index - 1);
		}
		return theOidBasedItemData;
	}

	private ItemDataBean oneToEndMany(ItemBean itemBeanB, ItemGroupBean itemGroupBeanB,
			ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub) {

		ItemDataBean theOidBasedItemData = null;
		int maxOrdinal = getItemDataDAO().getMaxOrdinalForGroupByItemAndEventCrf(itemBeanB, eventCrfBeanB);
		List<ItemBean> items = getItemDAO().findAllItemsByGroupId(itemGroupBeanB.getId(),
				eventCrfBeanB.getCRFVersionId());
		if (1 + maxOrdinal > itemGroupMetadataBeanB.getRepeatMax()) {
			logger.debug("Cannot add new repeat of this group because it has reached MaxRepeat.");
		} else {
			for (ItemBean itemBeanX : items) {
				ItemDataBean oidBasedItemData = getItemData(itemBeanX, eventCrfBeanB, 1 + maxOrdinal);
				if (oidBasedItemData.getId() == 0) {
					oidBasedItemData = createItemData(oidBasedItemData, itemBeanX, 1 + maxOrdinal, eventCrfBeanB, ub);
				}
			}
		}
		List<ItemDataBean> theItemDataBeans = getItemDataDAO().findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(),
				itemBeanB.getId());
		theOidBasedItemData = theItemDataBeans.get(theItemDataBeans.size() - 1);
		return theOidBasedItemData;
	}

	private List<ItemDataBean> oneToMany(ItemBean itemBeanB, ItemGroupBean itemGroupBeanB,
			ItemGroupMetadataBean itemGroupMetadataBeanB, EventCRFBean eventCrfBeanB, UserAccountBean ub) {

		List<ItemDataBean> itemDataBeans = new ArrayList<ItemDataBean>();
		Integer size = getItemDataDAO().getGroupSize(itemBeanB.getId(), eventCrfBeanB.getId());
		int maxOrdinal = getItemDataDAO().getMaxOrdinalForGroupByItemAndEventCrf(itemBeanB, eventCrfBeanB);
		if (size > 0 || maxOrdinal > 0) {
			itemDataBeans.addAll(getItemDataDAO()
					.findAllByEventCRFIdAndItemId(eventCrfBeanB.getId(), itemBeanB.getId()));
		} else {
			List<ItemBean> items = getItemDAO().findAllItemsByGroupId(itemGroupBeanB.getId(),
					eventCrfBeanB.getCRFVersionId());
			for (int ordinal = 1 + maxOrdinal; ordinal <= itemGroupMetadataBeanB.getRepeatNum() + maxOrdinal; ordinal++) {
				for (ItemBean itemBeanX : items) {
					ItemDataBean oidBasedItemData = getItemData(itemBeanX, eventCrfBeanB, ordinal);
					if (oidBasedItemData.getId() == 0) {
						oidBasedItemData = createItemData(oidBasedItemData, itemBeanX, ordinal, eventCrfBeanB, ub);
					}
					if (itemBeanX.getId() == itemBeanB.getId()) {
						itemDataBeans.add(oidBasedItemData);
					}
				}
			}
		}
		return itemDataBeans;
	}

	private ItemDataBean oneToOne(ItemBean itemBeanB, ItemGroupMetadataBean itemGroupMetadataBeanB,
			EventCRFBean eventCrfBeanB, UserAccountBean ub, Integer ordinal) {
		ordinal = ordinal == null ? 1 : ordinal;
		itemGroupMetadataBeanB.getRepeatNum();
		ItemDataBean oidBasedItemData = getItemData(itemBeanB, eventCrfBeanB, ordinal);
		if (oidBasedItemData.getId() == 0) {
			oidBasedItemData = createItemData(oidBasedItemData, itemBeanB, ordinal, eventCrfBeanB, ub);
		}
		return oidBasedItemData;
	}

	private ItemDataBean createItemData(ItemDataBean oidBasedItemData, ItemBean itemBeanB, int ordinal,
			EventCRFBean eventCrfBeanA, UserAccountBean ub) {
		oidBasedItemData.setItemId(itemBeanB.getId());
		oidBasedItemData.setEventCRFId(eventCrfBeanA.getId());
		oidBasedItemData.setStatus(Status.AVAILABLE);
		oidBasedItemData.setOwner(ub);
		oidBasedItemData.setOrdinal(ordinal);
		oidBasedItemData = (ItemDataBean) getItemDataDAO().create(oidBasedItemData);
		return oidBasedItemData;
	}

	private String getValue(PropertyBean property, RuleSetBean ruleSet, EventCRFBean eventCrfBean) {
		String value = null;
		if (property.getValue() != null && property.getValue().length() > 0) {
			logger.info("Value from property value is : {}", value);
			value = property.getValue();
		}
		if (property.getValueExpression() == null) {
			logger.info("There is no ValueExpression for property =" + property.getOid());
		} else {
			String expression = getExpressionService().constructFullExpressionFromPartial(
					property.getValueExpression().getValue(), ruleSet.getTarget().getValue());
			if (expression != null) {
				ItemBean itemBean = getExpressionService().getItemBeanFromExpression(expression);
				String itemGroupBOrdinal = getExpressionService().getGroupOrdninalCurated(expression);
				ItemDataBean itemData = getItemDataDAO().findByItemIdAndEventCRFIdAndOrdinal(itemBean.getId(),
						eventCrfBean.getId(), itemGroupBOrdinal == "" ? 1 : Integer.valueOf(itemGroupBOrdinal));
				if (itemData.getId() == 0) {
					logger.info("Cannot get Value for ExpressionValue {}", expression);
				} else {
					value = itemData.getValue();
					logger.info("Value from ExpressionValue '{}'  is : {}", expression, value);
				}
			}
		}
		return value;

	}

	public void insert(Integer sourceItemDataId, List<PropertyBean> properties, UserAccountBean ub,
			RuleSetBean ruleSet, Connection con) {

		ItemDataBean sourceItemDataBean = (ItemDataBean) getItemDataDAO().findByPK(sourceItemDataId);
		EventCRFBean sourceEventCrfBean = (EventCRFBean) getEventCRFDAO().findByPK(sourceItemDataBean.getEventCRFId());
		StudyEventBean sourceStudyEventBean = (StudyEventBean) getStudyEventDAO().findByPK(
				sourceEventCrfBean.getStudyEventId());
		StudyDAO sdao = getStudyDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudyBean subjectStudy = sdao.findByStudySubjectId(sourceStudyEventBean.getStudySubjectId());
		StudySubjectBean studySubject = ssdao.findBySubjectIdAndStudy(sourceStudyEventBean.getStudySubjectId(),
				subjectStudy);

		for (PropertyBean propertyBean : properties) {
			String expression = getExpressionService().constructFullExpressionFromPartial(propertyBean.getOid(),
					ruleSet.getTarget().getValue());
			ItemBean destinationItemBean = getExpressionService().getItemBeanFromExpression(expression);

			String studyEventDefinitionOid = getExpressionService().getStudyEventDefenitionOid(expression);
			StudyEventDefinitionBean destinationStudyEventDefinitionBean = studyEventDefinitionOid == null
					? null
					: getStudyEventDefinitionDAO().findByOid(studyEventDefinitionOid);

			StudyEventBean destinationStudyEventBean;
			if (destinationStudyEventDefinitionBean != null) {
				destinationStudyEventBean = (StudyEventBean) getStudyEventDAO()
						.findByStudySubjectIdAndDefinitionIdAndOrdinal(sourceEventCrfBean.getStudySubjectId(),
								destinationStudyEventDefinitionBean.getId(), sourceStudyEventBean.getSampleOrdinal());
				// event not scheduled
				if (destinationStudyEventBean.getId() == 0) {
					StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
					subjectStudy = subjectStudy.getParentStudyId() > 0 ? (StudyBean) sdao.findByPK(subjectStudy
							.getParentStudyId()) : subjectStudy;
					boolean allowScheduleEvent = spvdao
							.findByHandleAndStudy(subjectStudy.getId(), "allowRulesAutoScheduling").getValue()
							.equalsIgnoreCase("yes");
					if (allowScheduleEvent) {
						destinationStudyEventBean = scheduleEvent(sourceStudyEventBean, destinationStudyEventBean,
								destinationStudyEventDefinitionBean, subjectStudy);
					} else {
						return;
					}
				}
			} else {
				destinationStudyEventBean = sourceStudyEventBean;
			}

			Boolean isItemInSameForm = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(
					destinationItemBean.getId(), sourceEventCrfBean.getCRFVersionId()).getId() != 0
					&& sourceStudyEventBean.equals(destinationStudyEventBean);
			// Item Does not below to same form
			if (!isItemInSameForm) {
				List<EventCRFBean> eventCrfs = getEventCRFDAO().findAllByStudyEventAndCrfOrCrfVersionOid(
						destinationStudyEventBean, getExpressionService().getCrfOid(expression));
				if (eventCrfs.size() == 0) {
					createNewEventCRF(propertyBean, subjectStudy, sourceEventCrfBean, destinationStudyEventBean,
							sourceItemDataBean, destinationItemBean, expression, ruleSet, ub, con);
				} else {
					updateDestinationEventCRF(studySubject, propertyBean, subjectStudy, sourceEventCrfBean,
							eventCrfs.get(0), destinationStudyEventBean, sourceItemDataBean, destinationItemBean,
							expression, ruleSet, ub, con);
				}
			} else {
				updateSourceEventCRF(studySubject, propertyBean, subjectStudy, sourceEventCrfBean,
						destinationStudyEventBean, sourceItemDataBean, destinationItemBean, expression, ruleSet, ub,
						con);
			}
		}
	}

	private StudyEventBean scheduleEvent(StudyEventBean studyEventBeanA, StudyEventBean studyEventBeanB,
			StudyEventDefinitionBean studyEventDefinitionBeanB, StudyBean studyBean) {
		StudyEventDAO sed = getStudyEventDAO();
		StudySubjectDAO ssdao = getStudySubjectDAO();
		StudySubjectBean ssbean = ssdao.findBySubjectIdAndStudy(studyEventBeanA.getStudySubjectId(), studyBean);

		studyEventBeanB.setStudyEventDefinitionId(studyEventDefinitionBeanB.getId());
		studyEventBeanB.setStudySubjectId(studyEventBeanA.getStudySubjectId());

		studyEventBeanB.setStartTimeFlag(studyEventBeanA.getStartTimeFlag());
		studyEventBeanB.setEndTimeFlag(studyEventBeanA.getEndTimeFlag());
		studyEventBeanB.setDateStarted(new Date());
		studyEventBeanB.setUpdatedDate(new Date());
		studyEventBeanB.setOwner(studyEventBeanA.getOwner());
		studyEventBeanB.setStatus(Status.AVAILABLE);
		studyEventBeanB.setLocation(studyEventBeanA.getLocation());
		studyEventBeanB.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
		studyEventBeanB.setSampleOrdinal(sed.getMaxSampleOrdinal(studyEventDefinitionBeanB, ssbean) + 1);

		return (StudyEventBean) sed.create(studyEventBeanB);
	}

	private void createNewEventCRF(PropertyBean propertyBean, StudyBean subjectStudy, EventCRFBean sourceEventCrfBean,
			StudyEventBean destinationStudyEventBean, ItemDataBean sourceItemDataBean, ItemBean destinationItemBean,
			String expression, RuleSetBean ruleSet, UserAccountBean ub, Connection con) {

		StudyEventDAO studyEventDAO = getStudyEventDAO();
		CRFVersionDAO crfVersionDAO = getCRFVersionDAO();
		CRFVersionBean destinationCrfVersion = getExpressionService().getCRFVersionFromExpression(expression);
		CRFBean crf = getExpressionService().getCRFFromExpression(expression);
		int destinationCrfVersionId = 0;
		EventDefinitionCRFBean destinationEventDefinitionCRFBean = getEventDefinitionCRFDAO()
				.findByStudyEventDefinitionIdAndCRFId(subjectStudy,
						destinationStudyEventBean.getStudyEventDefinitionId(), crf.getId());
		if (destinationEventDefinitionCRFBean.isActive()) {
			destinationCrfVersionId = destinationCrfVersion != null
					? destinationCrfVersion.getId()
					: destinationEventDefinitionCRFBean.getDefaultVersionId();
		}
		SubjectEventStatus destinationStudyEventStatus = destinationStudyEventBean.getSubjectEventStatus();
		destinationCrfVersion = (CRFVersionBean) crfVersionDAO.findByPK(destinationCrfVersionId);
		boolean isDestinationEventDefinitionCRFBeanAvailable = destinationEventDefinitionCRFBean.isActive()
				&& destinationEventDefinitionCRFBean.getStatus().isAvailable();
		boolean isDestinationCrfVersionAvailable = destinationCrfVersion.isActive()
				&& destinationCrfVersion.getStatus().isAvailable();
		boolean isDestinationStudyEventBeanAvailable = !(destinationStudyEventStatus.isRemoved()
				|| destinationStudyEventStatus.isLocked() || destinationStudyEventStatus.isStopped() || destinationStudyEventStatus
				.isSkipped());
		boolean isAnotherVersionStarted = isAnotherVersionStarted(destinationStudyEventBean, expression);

		if (isDestinationEventDefinitionCRFBeanAvailable && isDestinationCrfVersionAvailable
				&& isDestinationStudyEventBeanAvailable && !isAnotherVersionStarted) {

			EventCRFBean destinationEventCrfBean = new EventCRFBean();
			destinationEventCrfBean.setStudyEventId(destinationStudyEventBean.getId());
			destinationEventCrfBean.setCRFVersionId(destinationCrfVersionId);
			destinationEventCrfBean.setCompletionStatusId(1);
			destinationEventCrfBean.setStatus(Status.AVAILABLE);
			destinationEventCrfBean.setStatusId(Status.AVAILABLE.getId());
			destinationEventCrfBean.setCreatedDate(new Date());
			destinationEventCrfBean.setStudySubjectId(destinationStudyEventBean.getStudySubjectId());
			destinationEventCrfBean.setOldStatus(Status.AVAILABLE);
			destinationEventCrfBean.setNotStarted(false);
			destinationEventCrfBean.setOwner(ub);
			destinationEventCrfBean = (EventCRFBean) getEventCRFDAO().create(destinationEventCrfBean);

			SubjectEventStatusUtil.determineSubjectEventState(destinationStudyEventBean, new DAOWrapper(ds));
			studyEventDAO.update(destinationStudyEventBean);

			insertValueIntoTheDestinationItem(propertyBean, sourceEventCrfBean, destinationEventCrfBean,
					destinationItemBean, sourceItemDataBean, expression, ruleSet, ub, subjectStudy, con, false);
		}
	}

	private boolean isAnotherVersionStarted(StudyEventBean destinationStudyEventBean, String expression) {

		List<EventCRFBean> eventCRFBeanList = getEventCRFDAO().findAllByStudyEvent(destinationStudyEventBean);
		List<CRFBean> startedCRFBeanList = new ArrayList<CRFBean>();

		for (EventCRFBean eventCRFBean : eventCRFBeanList) {
			if (eventCRFBean.isNotStarted()) {
				getEventCRFDAO().delete(eventCRFBean.getId());
			} else {
				startedCRFBeanList.add(getCRFDAO().findByVersionId(eventCRFBean.getCRFVersionId()));
			}
		}

		CRFVersionBean crfVersionBean = getCRFVersionDAO().findByOid(getExpressionService().getCrfOid(expression));
		if (crfVersionBean != null) {
			CRFBean destinationCrfBean = getCRFDAO().findByVersionId(crfVersionBean.getId());
			if (startedCRFBeanList.contains(destinationCrfBean)) {
				return true;
			}
		}
		return false;
	}

	private void updateDestinationEventCRF(StudySubjectBean studySubject, PropertyBean propertyBean,
			StudyBean subjectStudy, EventCRFBean sourceEventCrfBean, EventCRFBean destinationEventCrfBean,
			StudyEventBean destinationStudyEventBean, ItemDataBean sourceItemDataBean, ItemBean destinationItemBean,
			String expression, RuleSetBean ruleSet, UserAccountBean ub, Connection con) {

		StudyEventDAO studyEventDAO = getStudyEventDAO();
		boolean createReasonForChangeIfNeeded = false;
		Status destinationEventCRFStatus = getEventCRFStatus(studySubject, subjectStudy, destinationEventCrfBean,
				destinationStudyEventBean, expression);
		boolean isDestinationEventCRFLocked = destinationEventCRFStatus.isDeleted()
				|| destinationEventCRFStatus.isLocked();

		if (!isDestinationEventCRFLocked) {

			if (destinationEventCRFStatus.isNotStarted()) {
				destinationEventCrfBean.setNotStarted(false);
			} else if (destinationEventCRFStatus.isCompleted()) {
				createReasonForChangeIfNeeded = true;
			} else if (destinationEventCRFStatus.isSDVed()) {
				destinationEventCrfBean.setSdvStatus(false);
				createReasonForChangeIfNeeded = true;
			} else if (destinationEventCRFStatus.isSigned()) {
				destinationEventCrfBean.setSdvStatus(false);
				destinationEventCrfBean.setElectronicSignatureStatus(false);
				createReasonForChangeIfNeeded = true;
				if (destinationStudyEventBean.getSubjectEventStatus().isSigned()) {
					destinationStudyEventBean.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
				}
			}

			boolean isAllowedToInsertDataIntoDestinationEventCRF = insertValueIntoTheDestinationItem(propertyBean,
					sourceEventCrfBean, destinationEventCrfBean, destinationItemBean, sourceItemDataBean, expression,
					ruleSet, ub, subjectStudy, con, createReasonForChangeIfNeeded);

			if (isAllowedToInsertDataIntoDestinationEventCRF) {
				getEventCRFDAO().update(destinationEventCrfBean);
				SubjectEventStatusUtil.determineSubjectEventState(destinationStudyEventBean, new DAOWrapper(ds));
				studyEventDAO.update(destinationStudyEventBean);
			}
		}
	}

	private void updateSourceEventCRF(StudySubjectBean studySubject, PropertyBean propertyBean, StudyBean subjectStudy,
			EventCRFBean sourceEventCrfBean, StudyEventBean sourceStudyEventBean, ItemDataBean sourceItemDataBean,
			ItemBean destinationItemBean, String expression, RuleSetBean ruleSet, UserAccountBean ub, Connection con) {

		Status destinationEventCRFStatus = getEventCRFStatus(studySubject, subjectStudy, sourceEventCrfBean,
				sourceStudyEventBean, expression);
		boolean createReasonForChangeIfNeeded = destinationEventCRFStatus.isCompleted()
				|| destinationEventCRFStatus.isSDVed() || destinationEventCRFStatus.isSigned();
		insertValueIntoTheDestinationItem(propertyBean, sourceEventCrfBean, sourceEventCrfBean, destinationItemBean,
				sourceItemDataBean, expression, ruleSet, ub, subjectStudy, con, createReasonForChangeIfNeeded);
	}

	private Status getEventCRFStatus(StudySubjectBean studySubject, StudyBean subjectStudy,
			EventCRFBean sourceEventCrfBean, StudyEventBean sourceStudyEventBean, String expression) {

		EventDefinitionCRFDAO eventDefCRFDAO = getEventDefinitionCRFDAO();
		CRFBean crf = getExpressionService().getCRFFromExpression(expression);
		EventDefinitionCRFBean destinationEventDefinitionCRFBean = eventDefCRFDAO.findByStudyEventDefinitionIdAndCRFId(
				subjectStudy, sourceStudyEventBean.getStudyEventDefinitionId(), crf.getId());
		return EventCRFUtil.getEventCRFCurrentStatus(studySubject, sourceStudyEventBean,
				destinationEventDefinitionCRFBean, sourceEventCrfBean, getCRFVersionDAO(), eventDefCRFDAO);
	}

	private List<ItemDataBean> getItemDataBeansToUpdate(EventCRFBean sourceEventCrfBean,
			EventCRFBean destinationEventCrfBean, ItemDataBean sourceItemDataBean, ItemBean destinationItemBean,
			String expression, RuleSetBean ruleSet, UserAccountBean ub) {

		List<ItemDataBean> destinationItemDataBeans = new ArrayList<ItemDataBean>();
		ItemGroupMetadataBean sourceItemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO()
				.findByItemAndCrfVersion(sourceItemDataBean.getItemId(), sourceEventCrfBean.getCRFVersionId());
		Boolean isGroupARepeating = isGroupRepeating(sourceItemGroupMetadataBean);
		String itemGroupAOrdinal = getExpressionService().getGroupOrdninalCurated(ruleSet.getTarget().getValue());
		ItemGroupBean destinationItemGroupBean = getExpressionService().getItemGroupExpression(expression);
		ItemGroupMetadataBean destinationItemGroupMetadataBean;
		Boolean isGroupBRepeating;
		String itemGroupBOrdinal;

		destinationItemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDAO().findByItemAndCrfVersion(
				destinationItemBean.getId(), destinationEventCrfBean.getCRFVersionId());
		isGroupBRepeating = isGroupRepeating(destinationItemGroupMetadataBean);
		itemGroupBOrdinal = getExpressionService().getGroupOrdninalCurated(expression);

		// If A and B are both non repeating groups
		if (!isGroupARepeating && !isGroupBRepeating) {
			ItemDataBean oidBasedItemData = oneToOne(destinationItemBean, destinationItemGroupMetadataBean,
					destinationEventCrfBean, ub, 1);
			destinationItemDataBeans.add(oidBasedItemData);
		}
		// If A is not repeating group & B is a repeating group with no index selected
		if (!isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
			destinationItemDataBeans = oneToMany(destinationItemBean, destinationItemGroupBean,
					destinationItemGroupMetadataBean, destinationEventCrfBean, ub);
		}
		// If A is not repeating group & B is a repeating group with index selected
		if (!isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
			ItemDataBean oidBasedItemData = oneToIndexedMany(destinationItemBean, destinationItemGroupBean,
					destinationItemGroupMetadataBean, destinationEventCrfBean, ub, Integer.valueOf(itemGroupBOrdinal));
			destinationItemDataBeans.add(oidBasedItemData);
		}
		// If A is repeating/ non repeating group & B is a repeating group with index selected as END
		if (isGroupBRepeating && itemGroupBOrdinal.equals("END")) {
			ItemDataBean oidBasedItemData = oneToEndMany(destinationItemBean, destinationItemGroupBean,
					destinationItemGroupMetadataBean, destinationEventCrfBean, ub);
			destinationItemDataBeans.add(oidBasedItemData);
		}
		// If A is repeating group with index & B is a repeating group with index selected
		if (isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("") && !itemGroupBOrdinal.equals("END")) {
			ItemDataBean oidBasedItemData = oneToIndexedMany(destinationItemBean, destinationItemGroupBean,
					destinationItemGroupMetadataBean, destinationEventCrfBean, ub, Integer.valueOf(itemGroupBOrdinal));
			destinationItemDataBeans.add(oidBasedItemData);
		}
		// If A is repeating group with index & B is a repeating group with no index selected
		if (isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
			ItemDataBean oidBasedItemData = oneToIndexedMany(destinationItemBean, destinationItemGroupBean,
					destinationItemGroupMetadataBean, destinationEventCrfBean, ub, Integer.valueOf(itemGroupAOrdinal));
			destinationItemDataBeans.add(oidBasedItemData);
		}
		return destinationItemDataBeans;
	}

	private boolean insertValueIntoTheDestinationItem(PropertyBean propertyBean, EventCRFBean sourceEventCrfBean,
			EventCRFBean destinationEventCrfBean, ItemBean destinationItemBean, ItemDataBean sourceItemDataBean,
			String expression, RuleSetBean ruleSet, UserAccountBean ub, StudyBean subjectStudy, Connection con,
			boolean createReasonForChangeIfNeeded) {

		boolean isAllowedToInsertDataIntoDestinationEventCRF = false;
		String valueToInsert = getValue(propertyBean, ruleSet, sourceEventCrfBean);
		List<ItemDataBean> destinationItemDataBeans = getItemDataBeansToUpdate(sourceEventCrfBean,
				destinationEventCrfBean, sourceItemDataBean, destinationItemBean, expression, ruleSet, ub);
		for (ItemDataBean destinationItemDataBean : destinationItemDataBeans) {
			if (!destinationItemDataBean.getValue().equals(valueToInsert)) {
				isAllowedToInsertDataIntoDestinationEventCRF = true;
				destinationItemDataBean.setValue(valueToInsert);
				getItemDataDAO().updateValue(destinationItemDataBean, "yyyy-MM-dd", con);
				if (createReasonForChangeIfNeeded) {
					generateRFCsForDestinationItemDataBean(destinationItemBean, destinationItemDataBean, ub,
							subjectStudy);
				}
			}
		}
		return isAllowedToInsertDataIntoDestinationEventCRF;
	}

	private void generateRFCsForDestinationItemDataBean(ItemBean destinationItemBean,
			ItemDataBean destinationItemDataBean, UserAccountBean assignedUser, StudyBean subjectStudy) {

		DisplayItemBean displayItem = new DisplayItemBean();
		displayItem.setItem(destinationItemBean);
		displayItem.setData(destinationItemDataBean);
		displayItem.setDbData(destinationItemDataBean);
		String noteDescription = ResourceBundleProvider.getResNotes("data_auto_inserted_by_rule");
		String detailedDescription = "";

		DiscrepancyNoteBean rfc = discrepancyNoteService.createRFC(displayItem, destinationItemBean.getName(),
				assignedUser, noteDescription, detailedDescription);
		FormDiscrepancyNotes formDNs = new FormDiscrepancyNotes();
		formDNs.addNote(rfc.getField(), rfc);
		discrepancyNoteService.saveFieldNotes(destinationItemBean.getName(), formDNs, destinationItemDataBean.getId(),
				DiscrepancyNoteService.DN_ITEM_DATA_ENTITY_TYPE, subjectStudy);
	}

	private ItemDataBean getItemData(ItemBean itemBean, EventCRFBean eventCrfBean, Integer ordinal) {
		return getItemDataDAO().findByItemIdAndEventCRFIdAndOrdinal(itemBean.getId(), eventCrfBean.getId(), ordinal);

	}

	public void hideNew(Integer itemDataId, List<PropertyBean> properties, UserAccountBean ub, RuleSetBean ruleSet,
			Connection con) {
		ItemDataBean itemDataBeanA = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
		EventCRFBean eventCrfBeanA = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBeanA.getEventCRFId());
		ItemGroupMetadataBean itemGroupMetadataBeanA = (ItemGroupMetadataBean) getItemGroupMetadataDAO()
				.findByItemAndCrfVersion(itemDataBeanA.getItemId(), eventCrfBeanA.getCRFVersionId());
		Boolean isGroupARepeating = isGroupRepeating(itemGroupMetadataBeanA);
		String itemGroupAOrdinal = getExpressionService().getGroupOrdninalCurated(ruleSet.getTarget().getValue());

		for (PropertyBean propertyBean : properties) {
			String oid = propertyBean.getOid();
			ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
			// OID is an item
			if (itemOrItemGroup.getItemBean() != null) {
				String expression = getExpressionService().constructFullExpressionFromPartial(propertyBean.getOid(),
						ruleSet.getTarget().getValue());
				ItemBean itemBeanB = getExpressionService().getItemBeanFromExpression(expression);
				ItemGroupBean itemGroupBeanB = getExpressionService().getItemGroupExpression(expression);
				EventCRFBean eventCrfBeanB = eventCrfBeanA;
				ItemGroupMetadataBean itemGroupMetadataBeanB = (ItemGroupMetadataBean) getItemGroupMetadataDAO()
						.findByItemAndCrfVersion(itemBeanB.getId(), eventCrfBeanB.getCRFVersionId());
				Boolean isGroupBRepeating = isGroupRepeating(itemGroupMetadataBeanB);
				String itemGroupBOrdinal = getExpressionService().getGroupOrdninalCurated(expression);

				List<ItemDataBean> itemDataBeans = new ArrayList<ItemDataBean>();
				// If A and B are both non repeating groups
				if (!isGroupARepeating && !isGroupBRepeating) {
					ItemDataBean oidBasedItemData = oneToOne(itemBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub, 1);
					itemDataBeans.add(oidBasedItemData);

				}
				// If A is not repeating group & B is a repeating group with no index selected
				if (!isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
					List<ItemDataBean> oidBasedItemDatas = oneToMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub);
					itemDataBeans.addAll(oidBasedItemDatas);
				}
				// If A is not repeating group & B is a repeating group with index selected
				if (!isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupBOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}
				// If A is repeating group with index & B is a repeating group with index selected
				if (isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupBOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}
				// If A is repeating group with index & B is a repeating group with no index selected
				if (isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupAOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}

				for (ItemDataBean oidBasedItemData : itemDataBeans) {
					ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(
							itemOrItemGroup.getItemBean().getId(), eventCrfBeanA.getCRFVersionId());
					DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(
							itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
					if (dynamicsMetadataBean == null) {
						hideNewItem(itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
					} else if (dynamicsMetadataBean != null && dynamicsMetadataBean.isShowItem()) {
						// tbh #5287: add an additional check here to see if it should be hidden
						dynamicsMetadataBean.setShowItem(false);
						getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean, con);
					}
				}
			} else {
				// ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
				// below taken from showNew and reversed, tbh 07/2010
				logger.debug("found item group id 1 " + oid);
				ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
				ArrayList sectionBeans = getSectionDAO().findAllByCRFVersionId(eventCrfBeanA.getCRFVersionId());
				for (int i = 0; i < sectionBeans.size(); i++) {
					SectionBean sectionBean = (SectionBean) sectionBeans.get(i);
					// System.out.println("found section " + sectionBean.getId());
					List<ItemGroupMetadataBean> itemGroupMetadataBeans = getItemGroupMetadataDAO()
							.findMetaByGroupAndSection(itemGroupBean.getId(), eventCrfBeanA.getCRFVersionId(),
									sectionBean.getId());
					for (ItemGroupMetadataBean itemGroupMetadataBean : itemGroupMetadataBeans) {
						if (itemGroupMetadataBean.getItemGroupId() == itemGroupBean.getId()) {
							// System.out.println("found item group id 2 " + oid);
							DynamicsItemGroupMetadataBean dynamicsGroupBean = getDynamicsItemGroupMetadataBean(
									itemGroupMetadataBean, eventCrfBeanA);
							if (dynamicsGroupBean == null) {
								hideGroup(itemGroupMetadataBean, eventCrfBeanA);
							} else if (dynamicsGroupBean != null && !dynamicsGroupBean.isShowGroup()) {
								dynamicsGroupBean.setShowGroup(false);
								getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean, con);
								// TODO is below required in hide?
							} else if (eventCrfBeanA.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
								dynamicsGroupBean.setPassedDde(1); // setVersion(1); // version 1 = passed DDE
								getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean, con);
							}
						}
					}
				}
			}
		}
	}

	public void showNew(Integer itemDataId, List<PropertyBean> properties, UserAccountBean ub, RuleSetBean ruleSet,
			Connection con) {
		ItemDataBean itemDataBeanA = (ItemDataBean) getItemDataDAO().findByPK(itemDataId);
		EventCRFBean eventCrfBeanA = (EventCRFBean) getEventCRFDAO().findByPK(itemDataBeanA.getEventCRFId());
		ItemGroupMetadataBean itemGroupMetadataBeanA = (ItemGroupMetadataBean) getItemGroupMetadataDAO()
				.findByItemAndCrfVersion(itemDataBeanA.getItemId(), eventCrfBeanA.getCRFVersionId());
		Boolean isGroupARepeating = isGroupRepeating(itemGroupMetadataBeanA);
		String itemGroupAOrdinal = getExpressionService().getGroupOrdninalCurated(ruleSet.getTarget().getValue());

		for (PropertyBean propertyBean : properties) {
			String oid = propertyBean.getOid();
			ItemOrItemGroupHolder itemOrItemGroup = getItemOrItemGroup(oid);
			// OID is an item
			if (itemOrItemGroup.getItemBean() != null) {
				String expression = getExpressionService().constructFullExpressionFromPartial(propertyBean.getOid(),
						ruleSet.getTarget().getValue());
				ItemBean itemBeanB = getExpressionService().getItemBeanFromExpression(expression);
				ItemGroupBean itemGroupBeanB = getExpressionService().getItemGroupExpression(expression);
				EventCRFBean eventCrfBeanB = eventCrfBeanA;
				ItemGroupMetadataBean itemGroupMetadataBeanB = (ItemGroupMetadataBean) getItemGroupMetadataDAO()
						.findByItemAndCrfVersion(itemBeanB.getId(), eventCrfBeanB.getCRFVersionId());
				Boolean isGroupBRepeating = isGroupRepeating(itemGroupMetadataBeanB);
				String itemGroupBOrdinal = getExpressionService().getGroupOrdninalCurated(expression);

				List<ItemDataBean> itemDataBeans = new ArrayList<ItemDataBean>();
				// If A and B are both non repeating groups
				if (!isGroupARepeating && !isGroupBRepeating) {
					ItemDataBean oidBasedItemData = oneToOne(itemBeanB, itemGroupMetadataBeanB, eventCrfBeanB, ub, 1);
					itemDataBeans.add(oidBasedItemData);

				}
				// If A is not repeating group & B is a repeating group with no index selected
				if (!isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
					List<ItemDataBean> oidBasedItemDatas = oneToMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub);
					itemDataBeans.addAll(oidBasedItemDatas);
				}
				// If A is not repeating group & B is a repeating group with index selected
				if (!isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupBOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}
				// If A is repeating group with index & B is a repeating group with index selected
				if (isGroupARepeating && isGroupBRepeating && !itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupBOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}
				// If A is repeating group with index & B is a repeating group with no index selected
				if (isGroupARepeating && isGroupBRepeating && itemGroupBOrdinal.equals("")) {
					ItemDataBean oidBasedItemData = oneToIndexedMany(itemBeanB, itemGroupBeanB, itemGroupMetadataBeanB,
							eventCrfBeanB, ub, Integer.valueOf(itemGroupAOrdinal));
					itemDataBeans.add(oidBasedItemData);
				}
				logger.debug("** found item data beans: " + itemDataBeans.toString());
				for (ItemDataBean oidBasedItemData : itemDataBeans) {
					ItemFormMetadataBean itemFormMetadataBean = getItemFormMetadataDAO().findByItemIdAndCRFVersionId(
							itemBeanB.getId(), eventCrfBeanB.getCRFVersionId());
					DynamicsItemFormMetadataBean dynamicsMetadataBean = getDynamicsItemFormMetadataBean(
							itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
					if (dynamicsMetadataBean == null) {
						showItem(itemFormMetadataBean, eventCrfBeanA, oidBasedItemData);
						// itemsAlreadyShown.add(new Integer(oidBasedItemData.getId()));
					} else if (dynamicsMetadataBean != null && !dynamicsMetadataBean.isShowItem()) {
						dynamicsMetadataBean.setShowItem(true);
						getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean, con);
						// itemsAlreadyShown.add(new Integer(oidBasedItemData.getId()));
					} else if (eventCrfBeanA.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
						logger.debug("hit DDE here: idb " + oidBasedItemData.getId());
						// need a guard clause to guarantee DDE
						// if we get there, it means that we've hit DDE and the bean exists
						dynamicsMetadataBean.setPassedDde(1); // setVersion(1);// version 1 = passed DDE
						getDynamicsItemFormMetadataDao().saveOrUpdate(dynamicsMetadataBean, con);
					}
				}

			} else {
				// OID is a group
				logger.debug("found item group id 1 " + oid);
				ItemGroupBean itemGroupBean = itemOrItemGroup.getItemGroupBean();
				ArrayList sectionBeans = getSectionDAO().findAllByCRFVersionId(eventCrfBeanA.getCRFVersionId());
				for (int i = 0; i < sectionBeans.size(); i++) {
					SectionBean sectionBean = (SectionBean) sectionBeans.get(i);
					// System.out.println("found section " + sectionBean.getId());
					List<ItemGroupMetadataBean> itemGroupMetadataBeans = getItemGroupMetadataDAO()
							.findMetaByGroupAndSection(itemGroupBean.getId(), eventCrfBeanA.getCRFVersionId(),
									sectionBean.getId());
					for (ItemGroupMetadataBean itemGroupMetadataBean : itemGroupMetadataBeans) {
						if (itemGroupMetadataBean.getItemGroupId() == itemGroupBean.getId()) {
							// System.out.println("found item group id 2 " + oid);
							DynamicsItemGroupMetadataBean dynamicsGroupBean = getDynamicsItemGroupMetadataBean(
									itemGroupMetadataBean, eventCrfBeanA);
							if (dynamicsGroupBean == null) {
								showGroup(itemGroupMetadataBean, eventCrfBeanA);
							} else if (dynamicsGroupBean != null && !dynamicsGroupBean.isShowGroup()) {
								dynamicsGroupBean.setShowGroup(true);
								getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean, con);
							} else if (eventCrfBeanA.getStage().equals(DataEntryStage.DOUBLE_DATA_ENTRY)) {
								dynamicsGroupBean.setPassedDde(1); // setVersion(1); // version 1 = passed DDE
								getDynamicsItemGroupMetadataDao().saveOrUpdate(dynamicsGroupBean, con);
							}
						}
					}
				}
			}
		}
	}

	private ItemOrItemGroupHolder getItemOrItemGroup(String oid) {
		int igPos = 0;
		ItemGroupBean itemGroup;
		String[] theOid = oid.split(ESCAPED_SEPERATOR);
		switch (theOid.length) {
			case FOUR :
				igPos++;
			case THREE :
				igPos++;
			case TWO :
				itemGroup = getItemGroupDAO().findByOid(theOid[igPos].trim());
				if (itemGroup != null) {
					ItemBean item = getItemDAO().findItemByGroupIdandItemOid(itemGroup.getId(),
							theOid[igPos + 1].trim());
					if (item != null) {
						return new ItemOrItemGroupHolder(item, itemGroup);
					}
				}
			case ONE :
				itemGroup = getItemGroupDAO().findByOid(theOid[igPos]);
				if (itemGroup != null) {
					return new ItemOrItemGroupHolder(null, itemGroup);
				}

				List<ItemBean> items = getItemDAO().findByOid(theOid[igPos]);
				ItemBean item = items.size() > 0 ? items.get(0) : null;
				if (item != null) {
					return new ItemOrItemGroupHolder(item, null);
				}
			default :
				return new ItemOrItemGroupHolder(null, null);

		}
	}

	public void updateGroupDynamicsInSection(List<DisplayItemWithGroupBean> displayItemWithGroups, int sectionId,
			EventCRFBean eventCrfBean) {
		for (DisplayItemWithGroupBean itemWithGroup : displayItemWithGroups) {
			if (itemWithGroup.isInGroup()) {
				updateDynShowGroupInSection(itemWithGroup.getItemGroup(), eventCrfBean);
				updateGroupDynItemsInSection(itemWithGroup, sectionId, eventCrfBean.getCRFVersionId(),
						eventCrfBean.getId());
			}
		}
	}

	public void updateDynShowGroupInSection(DisplayItemGroupBean itemGroup, EventCRFBean eventCrfBean) {
		DynamicsItemGroupMetadataBean dgm = dynamicsItemGroupMetadataDao.findByMetadataBean(
				itemGroup.getGroupMetaBean(), eventCrfBean);
		if (dgm != null && dgm.getId() > 0) {
			itemGroup.getGroupMetaBean().setShowGroup(dgm.isShowGroup());
		}
	}

	public void updateGroupDynItemsInSection(DisplayItemWithGroupBean itemWithGroup, int sectionId, int crfVersionId,
			int eventCrfId) {
		DisplayItemGroupBean digb = itemWithGroup.getItemGroup();
		int groupId = digb.getItemGroupBean().getId();
		List<Integer> itemIds = this.dynamicsItemFormMetadataDao.findItemIdsForAGroupInSection(groupId, sectionId,
				crfVersionId, eventCrfId);
		if (itemIds != null && itemIds.size() > 0) {
			List<Integer> showItemIds = this.dynamicsItemFormMetadataDao.findShowItemIdsForAGroupInSection(groupId,
					sectionId, crfVersionId, eventCrfId);
			this.updateItemGroupInASection(digb, itemIds, showItemIds);
			this.updateGroupDynItemsInASection(itemWithGroup, showItemIds, groupId, sectionId, crfVersionId, eventCrfId);
		}
	}

	private void updateItemGroupInASection(DisplayItemGroupBean itemGroup, List<Integer> itemIds,
			List<Integer> showItemIds) {
		ArrayList<DisplayItemBean> dibs = (ArrayList<DisplayItemBean>) itemGroup.getItems();
		for (DisplayItemBean dib : dibs) {
			ItemFormMetadataBean meta = dib.getMetadata();
			if (showItemIds != null && showItemIds.contains(dib.getItem().getId())) {
				meta.setShowItem(true);
			} else if (itemIds.contains(dib.getItem().getId())) {
				dib.getMetadata().setShowItem(false);
			}
		}
	}

	private void updateGroupDynItemsInASection(DisplayItemWithGroupBean itemWithGroup, List<Integer> showItemIds,
			int groupId, int sectionId, int crfVersionId, int eventCrfId) {
		List<DisplayItemGroupBean> digbs = itemWithGroup.getItemGroups();
		List<Integer> showDataIds = this.dynamicsItemFormMetadataDao.findShowItemDataIdsForAGroupInSection(groupId,
				sectionId, crfVersionId, eventCrfId);
		List<Integer> hideDataIds = this.dynamicsItemFormMetadataDao.findHideItemDataIdsForAGroupInSection(groupId,
				sectionId, crfVersionId, eventCrfId);
		for (int n = 0; n < digbs.size(); ++n) {
			DisplayItemGroupBean dg = digbs.get(n);
			ArrayList<DisplayItemBean> items = (ArrayList<DisplayItemBean>) dg.getItems();
			for (int m = 0; m < items.size(); ++m) {
				DisplayItemBean dib = items.get(m);
				ItemFormMetadataBean meta = dib.getMetadata();
				dib.setBlankDwelt(false);
				if (hideDataIds != null && hideDataIds.contains(dib.getData().getId())) {
					meta.setShowItem(false);
				}
				if (showDataIds != null && showDataIds.contains(dib.getData().getId())) {
					meta.setShowItem(true);
				}
				if (!meta.isShowItem() && showItemIds != null && showItemIds.contains(dib.getItem().getId())) {
					dib.setBlankDwelt(true);
				}
			}
		}
	}

	public Boolean hasShowingDynGroupInSection(int sectionId, int crfVersionId, int eventCrfId) {
		return dynamicsItemGroupMetadataDao.hasShowingInSection(sectionId, crfVersionId, eventCrfId);
	}

	public Boolean hasShowingDynItemInSection(int sectionId, int crfVersionId, int eventCrfId) {
		return dynamicsItemFormMetadataDao.hasShowingInSection(sectionId, crfVersionId, eventCrfId);
	}

	public DynamicsItemFormMetadataDao getDynamicsItemFormMetadataDao() {
		return dynamicsItemFormMetadataDao;
	}

	public DynamicsItemGroupMetadataDao getDynamicsItemGroupMetadataDao() {
		return dynamicsItemGroupMetadataDao;
	}

	public void setDynamicsItemGroupMetadataDao(DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao) {
		this.dynamicsItemGroupMetadataDao = dynamicsItemGroupMetadataDao;
	}

	public void setDynamicsItemFormMetadataDao(DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao) {
		this.dynamicsItemFormMetadataDao = dynamicsItemFormMetadataDao;
	}

	public EventCRFDAO getEventCRFDAO() {
		return new EventCRFDAO(ds);
	}

	public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
		return new DiscrepancyNoteDAO(ds);
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(ds);
	}

	public CRFVersionDAO getCRFVersionDAO() {
		return new CRFVersionDAO(ds);
	}

	public CRFDAO getCRFDAO() {
		return new CRFDAO(ds);
	}

	public StudySubjectDAO getStudySubjectDAO() {
		return new StudySubjectDAO(ds);
	}

	public StudyDAO getStudyDAO() {
		return new StudyDAO(ds);
	}

	public ItemDataDAO getItemDataDAO() {
		return new ItemDataDAO(ds);
	}

	public ItemDAO getItemDAO() {
		return new ItemDAO(ds);
	}

	public ItemGroupDAO getItemGroupDAO() {
		return new ItemGroupDAO(ds);
	}

	public SectionDAO getSectionDAO() {
		return new SectionDAO(ds);
	}

	public ItemFormMetadataDAO getItemFormMetadataDAO() {
		return new ItemFormMetadataDAO(ds);
	}

	public ItemGroupMetadataDAO getItemGroupMetadataDAO() {
		return new ItemGroupMetadataDAO(ds);
	}

	public StudyEventDAO getStudyEventDAO() {
		return new StudyEventDAO(ds);
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(ds);
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	class ItemOrItemGroupHolder {

		private ItemBean itemBean;
		private ItemGroupBean itemGroupBean;

		public ItemOrItemGroupHolder(ItemBean itemBean, ItemGroupBean itemGroupBean) {
			this.itemBean = itemBean;
			this.itemGroupBean = itemGroupBean;
		}

		public ItemBean getItemBean() {
			return itemBean;
		}

		public void setItemBean(ItemBean itemBean) {
			this.itemBean = itemBean;
		}

		public ItemGroupBean getItemGroupBean() {
			return itemGroupBean;
		}

		public void setItemGroupBean(ItemGroupBean itemGroupBean) {
			this.itemGroupBean = itemGroupBean;
		}

	}

}
