/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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

/*
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.service.rule.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.ExpressionTreeHelper;
import org.akaza.openclinica.util.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides rule expression services.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpressionService {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String SEPARATOR = ".";
	public static final String ESCAPED_SEPARATOR = "\\.";
	public static final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN = "[A-Z_0-9]+|[A-Z_0-9]+\\[(ALL|[1-9]\\d*)\\]$";
	public static final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL = "[A-Z_0-9]+|[A-Z_0-9]+\\[[1-9]\\d*\\]$";
	public static final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL = "[A-Z_0-9]+\\[(END|ALL|[1-9]\\d*)\\]$";
	public static final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_END = "[A-Z_0-9]+|[A-Z_0-9]+\\[(END|ALL|[1-9]\\d*)\\]$";
	public static final String RULE_EXPRESSION_OPERANDS_SPLIT = "eq|ne|ct|nct|gt|gte|lt|lte|\\*|-|/|\\+|\\(|\\)";
	public static final String PRE = "[A-Z_0-9]+\\[";
	public static final String POST = "\\]";
	public static final String CRF_OID_OR_ITEM_DATA_PATTERN = "[A-Z_0-9]+";
	public static final String BRACKETS_AND_CONTENTS = "\\[(END|ALL|[1-9]\\d*)\\]";
	public static final String ALL_IN_BRACKETS = "ALL";
	public static final String OPENNIG_BRACKET = "[";
	public static final String CLOSING_BRACKET = "]";
	private static final int FOUR = 4;
	private static final int THREE = 3;
	public static final int TWO = 2;
	public static final int ONE = 1;
	public static final int ZERO = 0;

	private DataSource ds;
	private Pattern[] pattern;
	private Pattern[] rulePattern;
	private Pattern[] ruleActionPattern;
	private ExpressionObjectWrapper expressionWrapper;

	private boolean enableCaching = true;
	private Map<String, CRFBean> crfOidMap = new HashMap<String, CRFBean>();
	private Map<Integer, CRFBean> crfIdMap = new HashMap<Integer, CRFBean>();
	private Map<Integer, ItemBean> itemIdMap = new HashMap<Integer, ItemBean>();
	private HashMap<String, ItemBean> itemOidMap = new HashMap<String, ItemBean>();
	private Map<String, CRFVersionBean> crfVersionOidMap = new HashMap<String, CRFVersionBean>();
	private Map<Integer, StudyEventBean> studyEventIdMap = new HashMap<Integer, StudyEventBean>();
	private Map<String, List<ItemBean>> itemOidToItemListMap = new HashMap<String, List<ItemBean>>();
	private Map<Integer, EventCRFBean> eventCrfIdMap = new HashMap<Integer, EventCRFBean>();
	private Map<String, EventDefinitionCRFBean> eventDefinitionCrfOidMap = new HashMap<String, EventDefinitionCRFBean>();
	private Map<String, Boolean> isItemGroupRepeatingBasedOnCrfVersionMap = new HashMap<String, Boolean>();
	private Map<Integer, ItemGroupBean> itemGroupIdMap = new HashMap<Integer, ItemGroupBean>();
	private Map<String, ItemGroupBean> itemGroupOidMap = new HashMap<String, ItemGroupBean>();
	private Map<String, StudyEventDefinitionBean> studyEventDefinitionOidMap = new HashMap<String, StudyEventDefinitionBean>();
	private Map<String, Boolean> isItemGroupRepeatingBasedOnAllCrfVersionsMap = new HashMap<String, Boolean>();
	private Map<String, List<ItemDataBean>> seIdItemOidItemGroupOidToItemDataMap = new HashMap<String, List<ItemDataBean>>();
	private Map<String, StudyEventBean> sedOidCrfOidSedOrdinalSsIdToStudyEventMap = new HashMap<String, StudyEventBean>();
	private Map<String, EventDefinitionCRFBean> sIdsedIdCrfIdToEventDefinitionMap = new HashMap<String, EventDefinitionCRFBean>();
	private Map<String, ItemGroupMetadataBean> itemOidCrfVersionIdToItemGroupMetadataMap = new HashMap<String, ItemGroupMetadataBean>();

	/**
	 * Method that stores object in custom cache.
	 * 
	 * @param map
	 *            Map
	 * @param key
	 *            Object
	 * @param value
	 *            Object
	 */
	private void cache(Map map, Object key, Object value) {
		if (enableCaching && key != null && value != null) {
			map.put(key, value);
		}
	}

	/**
	 * Method clears custom caches.
	 */
	public void clearItemDataCache() {
		seIdItemOidItemGroupOidToItemDataMap.clear();
	}

	/**
	 * Instantiates ExpressionService object with DataSource.
	 * 
	 * @param ds
	 *            DataSource to be used
	 */
	public ExpressionService(DataSource ds) {
		init(ds, null);
	}

	/**
	 * Instantiates ExpressionService object with an ExpressionObjectWrapper.
	 * 
	 * @param expressionWrapper
	 *            ExpressionObjectWrapper to be used
	 */
	public ExpressionService(ExpressionObjectWrapper expressionWrapper) {
		init(expressionWrapper.getDs(), expressionWrapper);
	}

	private void init(DataSource ds, ExpressionObjectWrapper expressionWrapper) {
		pattern = new Pattern[FOUR];
		pattern[THREE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN); // STUDY_EVENT_DEFINITION_OID +
		// ordinal
		pattern[TWO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // CRF_OID or CRF_VERSION_OID
		pattern[ONE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN); // ITEM_GROUP_DATA_OID + ordinal
		pattern[ZERO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // ITEM_DATA_OID

		// [ALL] ordinals are not accepted in Rule Expressions
		rulePattern = new Pattern[FOUR];
		rulePattern[THREE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL); // STUDY_EVENT_DEFINITION_OID+
																									// ordinal
		rulePattern[TWO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // CRF_OID or CRF_VERSION_OID
		rulePattern[ONE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL); // ITEM_GROUP_DATA_OID
																									// +
																									// ordinal
		rulePattern[ZERO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // ITEM_DATA_OID

		// [END] support added
		ruleActionPattern = new Pattern[FOUR];
		ruleActionPattern[THREE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN); // STUDY_EVENT_DEFINITION_OID
																									// + ordinal
		ruleActionPattern[TWO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // CRF_OID or CRF_VERSION_OID
		ruleActionPattern[ONE] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_END); // ITEM_GROUP_DATA_OID
																											// + ordinal
		ruleActionPattern[ZERO] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // ITEM_DATA_OID

		this.ds = ds;
		this.expressionWrapper = expressionWrapper;
	}

	/**
	 * Checks if expression is valid.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @return returns true if exrpession is valid and false if expression is not valid
	 */
	public boolean ruleSetExpressionChecker(String expression) {
		if (checkSyntax(expression)) {
			isExpressionValid(expression);
		} else {
			throw new OpenClinicaSystemException("OCRERR_0032");
		}
		return true;
	}

	/**
	 * Checks if value of itemBean is of type date.
	 * 
	 * @param itemBean
	 *            ItemBean object to check
	 * @param value
	 *            Value to check
	 * @return Returns final value
	 */
	public String ifValueIsDate(ItemBean itemBean, String value) {
		String theFinalValue = value;

		if (value != null && itemBean.getDataType() == ItemDataType.DATE) {
			value = Utils.convertedItemDateValue(value,
					ResourceBundleProvider.getFormatBundle().getString("date_format_string"), "MM/dd/yyyy",
					ResourceBundleProvider.getLocale());
			theFinalValue = ExpressionTreeHelper.isValidDateMMddyyyy(value);
		}
		return theFinalValue;
	}

	private String getValueFromDb(String expression, List<ItemDataBean> itemData, Map<Integer, ItemBean> itemBeans) {
		if (isExpressionPartial(expression)) {
			throw new OpenClinicaSystemException("getValueFromDb:We cannot get the Value of a PARTIAL expression : "
					+ expression);
		}
		try {
			Integer index = getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer
					.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;
			ItemDataBean itemDataBean = itemData.get(index);
			String value = itemData.get(index).getValue();
			if (itemBeans.containsKey(itemDataBean.getItemId())) {
				value = ifValueIsDate(itemBeans.get(itemDataBean.getItemId()), value);
			}
			return value;
		} catch (NullPointerException npe) {
			logger.error("NullPointerException was thrown ");
			return "";
		} catch (IndexOutOfBoundsException ioobe) {
			logger.error("IndexOutOfBoundsException was thrown ");
			return "";
		}

	}

	/**
	 * Gets value from database.
	 * 
	 * @param expression
	 *            Expression to check
	 * @return Returns value from database
	 */
	public String getValueFromDbb(String expression) {
		if (isExpressionPartial(expression)) {
			throw new OpenClinicaSystemException("getValueFromDb:We cannot get the Value of a PARTIAL expression : "
					+ expression);
		}
		try {
			// Get the studyEventId from RuleSet Target so we can know which
			// StudySubject we are dealing with.
			String ruleSetExpression = expressionWrapper.getRuleSet().getTarget().getValue();
			String ruleSetExpressionStudyEventId = getStudyEventDefinitionOidOrdinalFromExpression(ruleSetExpression);

			StudyEventBean studyEvent = studyEventIdMap.get(Integer.parseInt(ruleSetExpressionStudyEventId));
			if (studyEvent == null) {
				studyEvent = (StudyEventBean) getStudyEventDao().findByPK(
						Integer.valueOf(ruleSetExpressionStudyEventId));
				cache(studyEventIdMap, Integer.parseInt(ruleSetExpressionStudyEventId), studyEvent);
			}

			// Prepare Method arguments
			String studyEventDefinitionOid = getStudyEventDefinitionOidFromExpression(expression);
			String crfOrCrfVersionOid = getCrfOidFromExpression(expression);
			String studyEventDefinitionOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
			studyEventDefinitionOrdinal = studyEventDefinitionOrdinal.equals("") ? "1" : studyEventDefinitionOrdinal;
			String studySubjectId = String.valueOf(studyEvent.getStudySubjectId());

			logger.debug(
					"ruleSet studyEventId  {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEventDefinitionOrdinal {} ,studySubjectId {}",
					studyEvent.getId(), studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal,
					studySubjectId);

			String key = studyEventDefinitionOid.concat("_").concat(crfOrCrfVersionOid).concat("_")
					.concat(studyEventDefinitionOrdinal).concat("_").concat(studySubjectId);
			StudyEventBean studyEventofThisExpression = sedOidCrfOidSedOrdinalSsIdToStudyEventMap.get(key);
			if (studyEventofThisExpression == null) {
				studyEventofThisExpression = getStudyEventDao().findAllByStudyEventDefinitionAndCrfOidsAndOrdinal(
						studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal, studySubjectId);
				cache(sedOidCrfOidSedOrdinalSsIdToStudyEventMap, key, studyEventofThisExpression);
			}

			logger.debug("studyEvent : {} , itemOid {} , itemGroupOid {}", studyEventofThisExpression.getId(),
					getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));

			key = Integer.toString(studyEventofThisExpression.getId()).concat("_")
					.concat(getItemGroupOidFromExpression(expression)).concat("_")
					.concat(getItemOidFromExpression(expression));
			List<ItemDataBean> itemData = seIdItemOidItemGroupOidToItemDataMap.get(key);
			if (itemData == null) {
				itemData = getItemDataDao().findByStudyEventAndOids(studyEventofThisExpression.getId(),
						getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));
				cache(seIdItemOidItemGroupOidToItemDataMap, key, itemData);
			}

			expression = fixGroupOrdinal(expression, ruleSetExpression, itemData, expressionWrapper.getEventCrf());

			Integer index = getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer
					.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;

			ItemDataBean itemDataBean = itemData.get(index);
			ItemBean itemBean = itemIdMap.get(itemDataBean.getItemId());
			if (itemBean == null) {
				itemBean = (ItemBean) getItemDao().findByPK(itemDataBean.getItemId());
				cache(itemIdMap, itemDataBean.getItemId(), itemBean);
			}
			String value = itemData.get(index).getValue();
			value = ifValueIsDate(itemBean, value);

			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets ItemDataBean for passed expression from database.
	 * 
	 * @param expression
	 *            Expression whose ItemDataBean is to be returned
	 * @return Returns ItemDataBean for passed expression
	 */
	public ItemDataBean getItemDataBeanFromDb(String expression) {
		if (isExpressionPartial(expression)) {
			throw new OpenClinicaSystemException(
					"getItemDataBeanFromDb:We cannot get the ItemData of a PARTIAL expression : " + expression);
		}
		String studyEventId = getStudyEventDefinitionOidOrdinalFromExpression(expression);
		Integer index = getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer
				.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;
		String key = studyEventId.concat("_").concat(getItemGroupOidFromExpression(expression)).concat("_")
				.concat(getItemOidFromExpression(expression));
		List<ItemDataBean> itemData = seIdItemOidItemGroupOidToItemDataMap.get(key);
		if (itemData == null) {
			itemData = getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventId),
					getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));
			cache(seIdItemOidItemGroupOidToItemDataMap, key, itemData);
		}
		return itemData.size() > index ? itemData.get(index) : null;
	}

	/**
	 * Gets expression value from form.
	 * 
	 * @param expression
	 *            Expression whose value is to be returned.
	 * @return Returns expression's value from form.
	 */
	public String getValueFromForm(String expression) {
		String result = null;
		HashMap<String, String> formValues = expressionWrapper.getItemsAndTheirValues();
		if (formValues != null && !formValues.isEmpty()) {
			String withGroup = getItemGroupPLusItem(expression);
			String withoutGroup = getItemOidFromExpression(expression);
			result = formValues.containsKey(withGroup) ? formValues.get(withGroup) : formValues
					.containsKey(withoutGroup) ? formValues.get(withoutGroup) : null;
		} else {
			logger.warn("The HashMap that stores form values was null, Better this be a Bulk operation");
		}
		return result;
	}

	/**
	 * Gets expression value from form for the stored ItemBeans.
	 * 
	 * @param expression
	 *            Expression to be evaluated.
	 * @param itemBeans
	 *            Map that stores ItemBeans
	 * @return Returns expression value
	 */
	public String getValueFromForm(String expression, Map<String, ItemBean> itemBeans) {
		if (itemBeans == null) {
			logger.info("The Map that stores ItemBeans is null. Item Date value cannot be processed.");
		}
		String result = null;
		HashMap<String, String> formValues = expressionWrapper.getItemsAndTheirValues();
		if (formValues != null && !formValues.isEmpty()) {
			String withGroup = getItemGroupPLusItem(expression);
			String withoutGroup = getItemOidFromExpression(expression);
			result = formValues.containsKey(withGroup) ? formValues.get(withGroup) : formValues
					.containsKey(withoutGroup) ? formValues.get(withoutGroup) : null;
			if (itemBeans != null) {
				ItemBean itemBean = itemBeans.containsKey(withGroup) ? itemBeans.get(withGroup) : itemBeans
						.containsKey(withoutGroup) ? itemBeans.get(withoutGroup) : null;
				result = ifValueIsDate(itemBean, result);
			}
		} else {
			logger.warn("The HashMap that stores form values was null, Better this be a Bulk operation");
		}
		return result;
	}

	/**
	 * Evaluates expression.
	 * 
	 * @param expression
	 *            Expression to be evaluated.
	 * @return The value of the expression.
	 */
	public String evaluateExpression(String expression) {
		String value = null;
		if (expressionWrapper.getRuleSet() != null) {
			if (isExpressionPartial(expression)) {
				String fullExpression = constructFullExpressionIfPartialProvided(expression, expressionWrapper
						.getRuleSet().getTarget().getValue());
				List<ItemDataBean> itemDatas = getItemDatas(fullExpression);
				Map<Integer, ItemBean> itemBeansI = new HashMap<Integer, ItemBean>();
				if (itemOidMap != null) {
					for (ItemBean item : itemOidMap.values()) {
						itemBeansI.put(item.getId(), item);
					}
				}
				fullExpression = fixGroupOrdinal(fullExpression, expressionWrapper.getRuleSet().getTarget().getValue(),
						itemDatas, expressionWrapper.getEventCrf());
				if (checkSyntax(fullExpression)) {
					String valueFromForm;
					if (itemOidMap == null) {
						valueFromForm = getValueFromForm(fullExpression);
					} else {
						valueFromForm = getValueFromForm(fullExpression, itemOidMap);
					}
					String valueFromDb = getValueFromDb(fullExpression, itemDatas, itemBeansI);
					logger.debug("valueFromForm : {} , valueFromDb : {}", valueFromForm, valueFromDb);
					if (valueFromForm == null && valueFromDb == null) {
						throw new OpenClinicaSystemException("OCRERR_0017", new Object[]{fullExpression,
								expressionWrapper.getRuleSet().getTarget().getValue()});
					}
					value = valueFromForm == null ? valueFromDb : valueFromForm;
				}
			} else {
				// So Expression is not Partial
				if (checkSyntax(expression)) {
					String valueFromDb = getValueFromDbb(expression);
					if (valueFromDb == null) {
						throw new OpenClinicaSystemException("OCRERR_0018", new Object[]{expression});
					}
					logger.debug("valueFromDb : {}", valueFromDb);
					value = valueFromDb;
				}
			}
		}
		return value;
	}

	private List<ItemDataBean> getItemDatas(String expression) {
		String studyEventId = getStudyEventDefinitionOidOrdinalFromExpression(expression);
		String key = studyEventId.concat("_").concat(getItemGroupOidFromExpression(expression)).concat("_")
				.concat(getItemOidFromExpression(expression));
		List<ItemDataBean> itemDataList = seIdItemOidItemGroupOidToItemDataMap.get(key);
		if (itemDataList == null) {
			itemDataList = getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventId),
					getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));
			cache(seIdItemOidItemGroupOidToItemDataMap, key, itemDataList);
		}
		return itemDataList;
	}

	private String fixGroupOrdinal(String ruleExpression, String targetExpression, List<ItemDataBean> itemData,
			EventCRFBean eventCrf) {

		String returnedRuleExpression = ruleExpression;
		if (getItemGroupOid(ruleExpression).equals(getItemGroupOid(targetExpression))) {
			if (getGroupOrdninalCurated(ruleExpression).equals("")
					&& !getGroupOrdninalCurated(targetExpression).equals("")) {
				returnedRuleExpression = replaceGroupOidOrdinalInExpression(ruleExpression,
						Integer.valueOf(getGroupOrdninalCurated(targetExpression)));
			}
		} else {
			EventCRFBean theEventCrfBean;
			if (eventCrf != null) {
				theEventCrfBean = eventCrf;
			} else if (!itemData.isEmpty()) {
				theEventCrfBean = eventCrfIdMap.get(itemData.get(0).getEventCRFId());
				if (theEventCrfBean == null) {
					theEventCrfBean = (EventCRFBean) getEventCRFDao().findByPK(itemData.get(0).getEventCRFId());
					cache(eventCrfIdMap, itemData.get(0).getEventCRFId(), theEventCrfBean);
				}
			} else {
				return returnedRuleExpression;
			}

			String itemOid = getItemOid(ruleExpression);
			String key = itemOid.concat("_").concat(Integer.toString(theEventCrfBean.getCRFVersionId()));
			ItemGroupMetadataBean itemGroupMetadataBean = itemOidCrfVersionIdToItemGroupMetadataMap.get(key);
			if (itemGroupMetadataBean == null) {
				Integer itemId = itemData.isEmpty() ? (getItemDao().findByOid(itemOid).get(0)).getId() : itemData
						.get(0).getItemId();
				itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDao().findByItemAndCrfVersion(
						itemId, theEventCrfBean.getCRFVersionId());
				cache(itemOidCrfVersionIdToItemGroupMetadataMap, key, itemGroupMetadataBean);
			}

			if (isGroupRepeating(itemGroupMetadataBean) && getGroupOrdninalCurated(ruleExpression).equals("")) {
				returnedRuleExpression = replaceGroupOidOrdinalInExpression(ruleExpression,
						Integer.valueOf(getGroupOrdninalCurated(targetExpression)));
			}

		}
		return returnedRuleExpression;
	}

	private Boolean isGroupRepeating(ItemGroupMetadataBean itemGroupMetadataBean) {
		return itemGroupMetadataBean.getRepeatNum() > 1 || itemGroupMetadataBean.getRepeatMax() > 1;
	}

	/**
	 * Checks whether Insert ACtion Expression is valid.
	 * 
	 * @param expression
	 *            Expression to be checked
	 * @param ruleSet
	 *            RuleSetBean containing rule
	 * @param allowedLength
	 *            Maximum length of expression allowed
	 * @return Returns true if expression is valid, false otherwise
	 */
	public boolean isInsertActionExpressionValid(String expression, RuleSetBean ruleSet, Integer allowedLength) {
		boolean result = false;
		boolean isRuleExpressionValid;

		Integer k = getExpressionSize(expression);
		if (k > allowedLength) {
			return false;
		}

		if (ruleSet != null) {
			String fullExpression = constructFullExpressionFromPartial(expression, ruleSet.getTarget().getValue());
			isRuleExpressionValid = checkInsertActionExpressionSyntax(fullExpression);

			if (isRuleExpressionValid) {
				isExpressionValid(fullExpression);
				result = true;
			}

		}
		return result;
	}

	/**
	 * Checks if expression is valid.
	 * 
	 * @param expression
	 *            Expression to be checked
	 * @param ruleSet
	 *            RuleSetBean containing rule
	 * @param allowedLength
	 *            Maximum length of expression allowed
	 * @return Returns true if expression is valid, false otherwise
	 */
	public boolean isExpressionValid(String expression, RuleSetBean ruleSet, Integer allowedLength) {
		boolean result = false;
		boolean isRuleExpressionValid;

		Integer k = getExpressionSize(expression);
		if (k > allowedLength) {
			return false;
		}

		if (ruleSet != null) {
			String fullExpression = constructFullExpressionFromPartial(expression, ruleSet.getTarget().getValue());
			isRuleExpressionValid = checkSyntax(fullExpression);

			if (isRuleExpressionValid) {
				isExpressionValid(fullExpression);
				result = true;
			}

		}
		return result;
	}

	/**
	 * Checks validity of rule expression.
	 * 
	 * @param expression
	 *            Expression to be checked
	 * @param optimiseRuleValidator
	 *            Specifies whether check should be optimized or not
	 * @return Returns true if expression is valid, false otherwise
	 */
	public boolean ruleExpressionChecker(String expression, Boolean optimiseRuleValidator) {
		boolean result = false;
		boolean isRuleExpressionValid;
		if (expressionWrapper.getRuleSet() != null) {
			if (isExpressionPartial(expressionWrapper.getRuleSet().getTarget().getValue())) {
				return true;
			}
			String fullExpression = constructFullExpressionFromPartial(expression, expressionWrapper.getRuleSet()
					.getTarget().getValue());

			if (isExpressionPartial(expression)) {
				isRuleExpressionValid = checkSyntax(fullExpression);
			} else {
				isRuleExpressionValid = checkRuleExpressionSyntax(fullExpression);
			}

			if (isRuleExpressionValid) {
				isExpressionValidWithOptimiseRuleValidator(fullExpression, optimiseRuleValidator);
				result = true;
			}

			String targetGroupOid = getItemGroupOid(expressionWrapper.getRuleSet().getTarget().getValue());
			String ruleGroupOid = getItemGroupOid(fullExpression);
			CRFVersionBean targetCrfVersion = getCRFVersionFromExpression(expressionWrapper.getRuleSet().getTarget()
					.getValue());
			CRFVersionBean ruleCrfVersion = getCRFVersionFromExpression(fullExpression);
			Boolean isTargetGroupRepeating = targetCrfVersion == null ? getItemGroupDao()
					.isItemGroupRepeatingBasedOnAllCrfVersions(targetGroupOid) : getItemGroupDao()
					.isItemGroupRepeatingBasedOnCrfVersion(targetGroupOid, targetCrfVersion.getId());
			Boolean isRuleGroupRepeating = ruleCrfVersion == null ? getItemGroupDao()
					.isItemGroupRepeatingBasedOnAllCrfVersions(ruleGroupOid) : getItemGroupDao()
					.isItemGroupRepeatingBasedOnCrfVersion(ruleGroupOid, ruleCrfVersion.getId());
			if (!isTargetGroupRepeating && isRuleGroupRepeating) {
				String ordinal = getItemGroupOidOrdinalFromExpression(fullExpression);
				if (ordinal.equals("") || ordinal.equals("ALL")) {
					result = false;
				}
			}

		} else {
			if (checkSyntax(expression) && getItemBeanFromExpression(expression) != null) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Gets size of expression.
	 * 
	 * @param expression
	 *            Expression whose size is to be determined.
	 * @return Returns size of expression
	 */
	public Integer getExpressionSize(String expression) {
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		return splitExpression.length;
	}

	/**
	 * Checks if expression is partial.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @return Returns true if expression is partial, false otherwise.
	 */
	public Boolean isExpressionPartial(String expression) {
		return !(expression.split(ESCAPED_SEPARATOR).length == FOUR);
	}

	/**
	 * Constructs a full expression from the partial expression provided.
	 * 
	 * @param expression
	 *            Partial expression to be used.
	 * @param ruleSetTargetExpression
	 *            Expression of rule target
	 * @return Returns full expression constructed.
	 */
	public String constructFullExpressionIfPartialProvided(String expression, String ruleSetTargetExpression) {
		if (expression == null) {
			logger.info("expression is null.");
			return null;
		} else {
			String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
			switch (splitExpression.length) {
				case ONE :
					return deContextualizeExpression(THREE, expression, ruleSetTargetExpression);
				case TWO :
					return deContextualizeExpression(TWO, expression, ruleSetTargetExpression);
				case THREE :
					return deContextualizeExpression(ONE, expression, ruleSetTargetExpression);
				case FOUR :
					return expression;
				default :
					throw new OpenClinicaSystemException(
							"Full Expression cannot be constructed from provided expression : " + expression);
			}
		}
	}

	/**
	 * Constructs a full expression from the partial expression provided.
	 * 
	 * @param expression
	 *            Partial expression to be used.
	 * @param crfVersion
	 *            CRFVersionBean containing expression item
	 * @param studyEventDefinition
	 *            StudyEventDefinitionBean containing rule CRF
	 * @return Returns full expression constructed.
	 */
	public String constructFullExpressionIfPartialProvided(String expression, CRFVersionBean crfVersion,
			StudyEventDefinitionBean studyEventDefinition) {
		String resultingExpression = expression;
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		if (splitExpression.length == ONE) {
			ItemBean itemBean = getItemBeanFromExpression(expression);
			String key = itemBean.getOid().concat("_").concat(Integer.toString(crfVersion.getId()));
			ItemGroupMetadataBean itemGroupMetadataBean = itemOidCrfVersionIdToItemGroupMetadataMap.get(key);
			if (itemGroupMetadataBean == null) {
				itemGroupMetadataBean = (ItemGroupMetadataBean) getItemGroupMetadataDao().findByItemAndCrfVersion(
						getItemBeanFromExpression(expression).getId(), crfVersion.getId());
				cache(itemOidCrfVersionIdToItemGroupMetadataMap, key, itemGroupMetadataBean);
			}
			ItemGroupBean itemGroup = itemGroupIdMap.get(itemGroupMetadataBean.getItemGroupId());
			if (itemGroup == null) {
				itemGroup = (ItemGroupBean) getItemGroupDao().findByPK(itemGroupMetadataBean.getItemGroupId());
				cache(itemGroupIdMap, itemGroupMetadataBean.getItemGroupId(), itemGroup);
			}
			resultingExpression = studyEventDefinition.getOid() + SEPARATOR + crfVersion.getOid() + SEPARATOR
					+ itemGroup.getOid() + SEPARATOR + expression;
		}
		if (splitExpression.length == TWO) {
			resultingExpression = studyEventDefinition.getOid() + SEPARATOR + crfVersion.getOid() + SEPARATOR
					+ expression;
		}
		if (splitExpression.length == THREE) {
			resultingExpression = studyEventDefinition.getOid() + SEPARATOR + expression;
		}
		return resultingExpression;
	}

	/**
	 * Constructs full expression from partial expression.
	 * 
	 * @param expression
	 *            Partial expression to be used
	 * @param ruleSetTargetExpression
	 *            Expression of rule target
	 * @return Returns constructed expression
	 */
	public String constructFullExpressionFromPartial(String expression, String ruleSetTargetExpression) {
		if (expression == null) {
			logger.info("expression is null.");
			return null;
		} else {
			String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
			switch (splitExpression.length) {
				case ONE :
					return deContextualizeExpression(THREE, expression, ruleSetTargetExpression);
				case TWO :
					return deContextualizeExpression(TWO, expression, ruleSetTargetExpression);
				case THREE :
					return deContextualizeExpression(ONE, expression, ruleSetTargetExpression);
				case FOUR :
					return expression;
				default :
					throw new OpenClinicaSystemException(
							"Full Expression cannot be constructed from provided expression : " + expression);
			}
		}
	}

	private String deContextualizeExpression(int j, String ruleExpression, String ruleSetTargetExpression) {
		String[] splitRuleSetExpression = ruleSetTargetExpression.split(ESCAPED_SEPARATOR);
		String buildExpression = "";

		for (int i = ZERO; i < j; i++) {
			buildExpression = buildExpression + splitRuleSetExpression[i] + SEPARATOR;
		}
		return buildExpression + ruleExpression;
	}

	private String getItemOidFromExpression(String expression) {
		return getOidFromExpression(expression, ZERO, ZERO);
	}

	private String getItemGroupOidFromExpression(String expression) {
		return getOidFromExpression(expression, ONE, ONE).replaceAll(BRACKETS_AND_CONTENTS, "");
	}

	private String getItemGroupOidWithOrdinalFromExpression(String expression) {
		return getOidFromExpression(expression, ONE, ONE);
	}

	private String getItemGroupOidOrdinalFromExpression(String expression) {
		String itemGroupOid = getOidFromExpression(expression, ONE, ONE);
		String itemGroupOidOrdinal = "";
		if (itemGroupOid.matches(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL)) {
			itemGroupOidOrdinal = itemGroupOid.trim().replaceAll(PRE, "").trim().replaceAll(POST, "");
		}
		return itemGroupOidOrdinal;
	}

	private String getItemGroupPLusItem(String expression) {
		return getItemGroupOidWithOrdinalFromExpression(expression) + SEPARATOR + getItemOidFromExpression(expression);
	}

	private String getCrfOidFromExpression(String expression) {
		return getOidFromExpression(expression, TWO, TWO);
	}

	private String getStudyEventDefinitionOidFromExpression(String expression) {
		return getOidFromExpression(expression, THREE, THREE).replaceAll(BRACKETS_AND_CONTENTS, "");
	}

	private String getStudyEventDefinitionOidWithOrdinalFromExpression(String expression) {
		return getOidFromExpression(expression, THREE, THREE);
	}

	/**
	 * Gets item group name and ordinal from expression in the format ITEM_GROUP_NAME [ORDINAL].
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return Returns item group name and ordinal in the said format.
	 */
	public String getItemGroupNameAndOrdinal(String expression) {
		return getItemGroupExpression(expression).getName() + " " + OPENNIG_BRACKET
				+ getItemGroupOidOrdinalFromExpression(expression) + CLOSING_BRACKET;
	}

	/**
	 * Gets study event definition oid from expression.
	 * 
	 * @param expression
	 *            Expresssion to be used.
	 * @return Returns study event definition oid ordinal
	 */
	public String getStudyEventDefinitionOidOrdinalFromExpression(String expression) {
		String studyEventDefinitionOid = getOidFromExpression(expression, THREE, THREE);
		String studyEventDefinitionOidOrdinal = "";
		if (studyEventDefinitionOid.matches(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL)) {
			studyEventDefinitionOidOrdinal = studyEventDefinitionOid.trim().replaceAll(PRE, "").trim()
					.replaceAll(POST, "");
		}
		return studyEventDefinitionOidOrdinal;
	}

	/**
	 * Use this method to create 1ItemOID or ItemOID Used in Data Entry Rule Execution.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return GroupOrdinal + ItemOID
	 */
	public String getGroupOrdninalConcatWithItemOid(String expression) {
		String ordinal = getGroupOrdninalCurated(expression);
		logger.debug(" orginigal expression {} , post getGroupOrdninalConcatWithItemOid : {} ", expression, ordinal
				+ getItemOidFromExpression(expression));
		return ordinal + getItemOidFromExpression(expression);
	}

	/**
	 * Get item group oid with ordinal and item oid.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return Returns item group oid with ordinal and item oid
	 */
	public String getGroupOidWithItemOid(String expression) {
		return getItemGroupOidWithOrdinalFromExpression(expression) + SEPARATOR + getItemOidFromExpression(expression);
	}

	/**
	 * Gets item oid from expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return item oid
	 */
	public String getItemOid(String expression) {
		return getItemOidFromExpression(expression);
	}

	/**
	 * Gets item group oid from expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return item group oid
	 */
	public String getItemGroupOid(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < 2) {
			return null;
		}
		return getItemGroupOidFromExpression(expression);
	}

	/**
	 * Gets CRF oid from exrpession.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return crf oid
	 */
	public String getCrfOid(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < THREE) {
			return null;
		}
		return getCrfOidFromExpression(expression);
	}

	/**
	 * Gets study event definition oid from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return study event definition oid
	 */
	public String getStudyEventDefenitionOid(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < FOUR) {
			return null;
		}
		return getStudyEventDefinitionOidFromExpression(expression);
	}

	/**
	 * Get group ordinal curated from expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return group ordinal curated
	 */
	public String getGroupOrdninalCurated(String expression) {
		String originalOrdinal = getItemGroupOidOrdinalFromExpression(expression);
		return originalOrdinal.equals(ALL_IN_BRACKETS) ? "" : originalOrdinal;
	}

	/**
	 * Gets study event definition ordinal curated from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return study event definition ordinal curated
	 */
	public String getStudyEventDefinitionOrdninalCurated(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < FOUR) {
			return "";
		}
		String originalOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
		return originalOrdinal.equals(ALL_IN_BRACKETS) ? "" : originalOrdinal;
	}

	/**
	 * Gets study event definition ordinal curated from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return study event definition ordinal curated
	 */
	public String getStudyEventDefenitionOrdninalCurated(String expression) {
		String originalOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
		String ordinal;
		if (originalOrdinal.equals(ALL_IN_BRACKETS)) {
			throw new OpenClinicaSystemException("ALL not supported in the following instance");
		} else if (originalOrdinal.equals("")) {
			ordinal = "1";
		} else {
			ordinal = originalOrdinal;
		}
		return ordinal;
	}

	/**
	 * Gets concatenation of group oid with item oid from expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return GroupOid.ItemOid
	 */
	public String getGroupOidConcatWithItemOid(String expression) {
		String result = getItemGroupOidFromExpression(expression) + SEPARATOR + getItemOidFromExpression(expression);
		logger.debug("getGroupOidConcatWithItemOid returns : {} ", result);
		return result;
	}

	/**
	 * Gets group oid ordinal from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return GroupOid[Ordinal]
	 */
	public String getGroupOidOrdinal(String expression) {
		String result = this.getItemGroupOidWithOrdinalFromExpression(expression);
		logger.debug("getGroupOidOrdinal returns : {} ", result);
		return result;
	}

	/**
	 * Replaces group oid ordinal in expression.
	 * 
	 * @param expression
	 *            Expression to be modified
	 * @param ordinal
	 *            Ordinal to be used
	 * @return new expression with ordinal replaced
	 */
	public String replaceGroupOidOrdinalInExpression(String expression, Integer ordinal) {
		String replacement = getStudyEventDefinitionOidWithOrdinalFromExpression(expression) + SEPARATOR
				+ getCrfOidFromExpression(expression) + SEPARATOR;
		if (ordinal == null) {
			replacement += getItemGroupOidFromExpression(expression) + SEPARATOR + getItemOidFromExpression(expression);
		} else {
			replacement += getItemGroupOidFromExpression(expression) + OPENNIG_BRACKET + ordinal + CLOSING_BRACKET
					+ SEPARATOR + getItemOidFromExpression(expression);
		}
		logger.debug("Original Expression : {} , Rewritten as {} .", expression, replacement);
		return replacement;
	}

	/**
	 * Replaces crf oid in expression.
	 * 
	 * @param expression
	 *            Expression to be modified.
	 * @param replacementCrfOid
	 *            new crf oid to be inserted in expression
	 * @return new expressio after replacement
	 */
	public String replaceCRFOidInExpression(String expression, String replacementCrfOid) {
		if (expression.split(ESCAPED_SEPARATOR).length < FOUR) {
			if (expression.split(ESCAPED_SEPARATOR).length == THREE) {
				return replacementCrfOid + SEPARATOR + getItemGroupOidWithOrdinalFromExpression(expression) + SEPARATOR
						+ getItemOidFromExpression(expression);
			}
			return expression;
		}
		return getStudyEventDefinitionOidWithOrdinalFromExpression(expression) + SEPARATOR + replacementCrfOid
				+ SEPARATOR + getItemGroupOidWithOrdinalFromExpression(expression) + SEPARATOR
				+ getItemOidFromExpression(expression);
	}

	/**
	 * Gets custom expression used to create view.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @param sampleOrdinal
	 *            Sample ordinal to be used
	 * @return custom expression
	 */
	public String getCustomExpressionUsedToCreateView(String expression, int sampleOrdinal) {
		return getStudyEventDefenitionOid(expression) + OPENNIG_BRACKET + sampleOrdinal + CLOSING_BRACKET + SEPARATOR
				+ "XXX" + SEPARATOR + getGroupOidWithItemOid(expression);
	}

	/**
	 * Replaces study event definition oid in expression.
	 * 
	 * @param expression
	 *            Expression to be modified.
	 * @param replacement
	 *            to be inserted
	 * @return modified expression
	 */
	public String replaceStudyEventDefinitionOIDWith(String expression, String replacement) {
		replacement = getStudyEventDefinitionOidFromExpression(expression) + OPENNIG_BRACKET + replacement
				+ CLOSING_BRACKET;
		String studyEventDefinitionOID = getStudyEventDefinitionOidWithOrdinalFromExpression(expression);
		return expression.replace(studyEventDefinitionOID, replacement);
	}

	/**
	 * Gets oid from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @param patternIndex
	 *            Index of pattern to be used.
	 * @param expressionIndex
	 *            Index of expression.
	 * @return oid from expression
	 */
	private String getOidFromExpression(String expression, int patternIndex, int expressionIndex) {
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		if (!match(splitExpression[splitExpression.length - 1 - expressionIndex], pattern[patternIndex])) {
			if (!match(splitExpression[splitExpression.length - 1 - expressionIndex], ruleActionPattern[patternIndex])) {
				throw new OpenClinicaSystemException("OCRERR_0019", new String[]{expression});
			}
		}
		return splitExpression[splitExpression.length - 1 - expressionIndex];
	}

	/**
	 * Gets ItemBean object from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return ItemBean object from expression
	 */
	public ItemBean getItemBeanFromExpression(String expression) {
		String itemOid = getItemOidFromExpression(expression);
		List<ItemBean> itemList = itemOidToItemListMap.get(itemOid);
		if (itemList == null) {
			itemList = getItemDao().findByOid(itemOid);
			cache(itemOidToItemListMap, itemOid, itemList);
		}
		return itemList.size() > 0 ? itemList.get(0) : null;
	}

	/**
	 * Gets StudyEventDefinitionBean object from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return StudyEventDefinitionBean object from expression
	 */
	public StudyEventDefinitionBean getStudyEventDefinitionFromExpression(String expression) {
		return expression.split(ESCAPED_SEPARATOR).length == FOUR ? getStudyEventDefinitionFromExpression(expression,
				expressionWrapper.getStudyBean()) : null;
	}

	/**
	 * Gets StudyEventDefinitionBean object from expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @param study
	 *            Study owning StudyEventDefinitionBean
	 * @return StudyEventDefinitionBean object from expression
	 */
	public StudyEventDefinitionBean getStudyEventDefinitionFromExpression(String expression, StudyBean study) {
		String studyEventDefinitionKey = getStudyEventDefinitionOidFromExpression(expression);
		logger.debug("Expression : {} , Study Event Definition OID {} , Study Bean {} ", expression,
				studyEventDefinitionKey, study.getId());
		StudyEventDefinitionBean studyEventDefinition = studyEventDefinitionOidMap.get(studyEventDefinitionKey);
		if (studyEventDefinition == null) {
			studyEventDefinition = getStudyEventDefinitionDao().findByOid(studyEventDefinitionKey);
			cache(studyEventDefinitionOidMap, studyEventDefinitionKey, studyEventDefinition);
		}
		return studyEventDefinition;
	}

	/**
	 * Gets ItemGroupBean object from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return ItemGroupBean object
	 */
	public ItemGroupBean getItemGroupExpression(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < TWO) {
			return null;
		}
		String itemGroupKey = getItemGroupOidFromExpression(expression);
		logger.debug("Expression : {} , ItemGroup OID : {} " + expression, itemGroupKey);
		ItemGroupBean itemGroup = itemGroupOidMap.get(itemGroupKey);
		if (itemGroup == null) {
			itemGroup = getItemGroupDao().findByOid(itemGroupKey);
			cache(itemGroupOidMap, itemGroupKey, itemGroup);
		}
		return itemGroup;
	}

	/**
	 * Gets ItemBean from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @param itemGroup
	 *            ItemGroupBean the item belongs to
	 * @return ItemBean object
	 */
	public ItemBean getItemExpression(String expression, ItemGroupBean itemGroup) {
		String itemKey = getItemOidFromExpression(expression);
		logger.debug("Expression : {} , Item OID : {}", expression, itemKey);
		ItemBean item = itemOidMap.get(itemKey);
		if (item == null) {
			item = getItemDao().findItemByGroupIdAndItemOid(itemGroup.getId(), itemKey);
			cache(itemOidMap, itemKey, item);
		}
		return item;
	}

	/**
	 * Gets ItemBean from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return ItemBean object
	 */
	public ItemBean getItemFromExpression(String expression) {
		String itemKey = getItemOidFromExpression(expression);
		logger.debug("Expression : {} , Item OID : {}", expression, itemKey);
		ItemBean item = itemOidMap.get(itemKey);
		if (item == null) {
			List<ItemBean> persistentItems = getItemDao().findByOid(itemKey);
			item = persistentItems.size() > 0 ? persistentItems.get(0) : null;
			cache(itemOidMap, itemKey, item);
		}
		return item;
	}

	/**
	 * Gets CRFBean from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return CRFBean
	 */
	public CRFBean getCRFFromExpression(String expression) {

		if (expression.split(ESCAPED_SEPARATOR).length < THREE) {
			return null;
		}
		CRFBean crf;
		String crfOid = getCrfOidFromExpression(expression);
		logger.info("Expression : " + expression);
		logger.info("Expression : " + crfOid);
		CRFVersionBean crfVersion = crfVersionOidMap.get(crfOid);
		if (crfVersion == null) {
			crfVersion = getCrfVersionDao().findByOid(crfOid);
			cache(crfVersionOidMap, crfOid, crfVersion);
		}
		if (crfVersion != null) {
			int crfId = crfVersion.getCrfId();
			crf = crfIdMap.get(crfId);
			if (crf == null) {
				crf = (CRFBean) getCrfDao().findByPK(crfId);
				cache(crfIdMap, crfId, crf);
			}
		} else {
			crf = crfOidMap.get(crfOid);
			if (crf == null) {
				crf = getCrfDao().findByOid(crfOid);
				cache(crfOidMap, crfOid, crf);
			}
		}
		return crf;
	}

	/**
	 * Gets CRFVersionBean from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used
	 * @return CRFVersionBean object
	 */
	public CRFVersionBean getCRFVersionFromExpression(String expression) {
		CRFVersionBean crfVersionBean;
		logger.info("Expression : " + expression);
		if (expression.split(ESCAPED_SEPARATOR).length < THREE) {
			return null;
		} else {
			String crfVersionOid = getCrfOidFromExpression(expression);
			crfVersionBean = crfVersionOidMap.get(crfVersionOid);
			if (crfVersionBean == null) {
				crfVersionBean = getCrfVersionDao().findByOid(crfVersionOid);
				cache(crfVersionOidMap, crfVersionOid, crfVersionBean);
			}
		}
		return crfVersionBean;
	}

	/**
	 * Given a Complete Expression check business logic validity of each component. Will throw
	 * OpenClinicaSystemException with correct explanation. This might allow immediate communication of message to user.
	 * 
	 * @param expression
	 *            Expression to be used
	 */
	public void isExpressionValid(String expression) {
		isExpressionValidWithOptimiseRuleValidator(expression, false);
	}

	/**
	 * Checks if expression is valid.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @param optimiseRuleValidator
	 *            Specifies if optimised rule validator should be used or not.
	 */
	public void isExpressionValidWithOptimiseRuleValidator(String expression, Boolean optimiseRuleValidator) {
		int length = expression.split(ESCAPED_SEPARATOR).length;
		CRFBean crf;
		ItemBean item;
		ItemGroupBean itemGroup = null;

		if (length > ZERO) {
			item = getItemFromExpression(expression);
			if (item == null) {
				throw new OpenClinicaSystemException("OCRERR_0023");
			}
		}
		if (!optimiseRuleValidator) {
			if (length > ONE) {
				String itemGroupOid = getItemGroupOidFromExpression(expression);
				itemGroup = itemGroupOidMap.get(itemGroupOid);
				if (itemGroup == null) {
					itemGroup = getItemGroupDao().findByOid(itemGroupOid);
					cache(itemGroupOidMap, itemGroupOid, itemGroup);
				}
				if (itemGroup == null) {
					throw new OpenClinicaSystemException("OCRERR_0022");
				}
			}

			if (length > TWO) {
				crf = getCRFFromExpression(expression);
				if (crf == null || itemGroup == null || crf.getId() != itemGroup.getCrfId()) {
					throw new OpenClinicaSystemException("OCRERR_0033");
				}
			}

			if (length > THREE) {
				StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionFromExpression(expression);
				crf = getCRFFromExpression(expression);
				if (studyEventDefinition == null || crf == null) {
					throw new OpenClinicaSystemException("OCRERR_0034");
				}

				String key = Integer.toString(this.expressionWrapper.getStudyBean().getId()).concat("_")
						.concat(Integer.toString(studyEventDefinition.getId())).concat("_")
						.concat(Integer.toString(crf.getId()));
				EventDefinitionCRFBean eventDefinitionCrf = sIdsedIdCrfIdToEventDefinitionMap.get(key);
				if (eventDefinitionCrf == null) {
					eventDefinitionCrf = getEventDefinitionCRFDao().findByStudyEventDefinitionIdAndCRFId(
							this.expressionWrapper.getStudyBean(), studyEventDefinition.getId(), crf.getId());
					cache(sIdsedIdCrfIdToEventDefinitionMap, key, eventDefinitionCrf);
				}
				if (eventDefinitionCrf == null || eventDefinitionCrf.getId() == 0) {
					throw new OpenClinicaSystemException("OCRERR_0034");
				}
			}
		}
	}

	/**
	 * Gets EventDefinitionCRFBean from passed expression.
	 * 
	 * @param expression
	 *            Expression to be used.
	 * @return EventDefinitionCRFBean object
	 */
	public EventDefinitionCRFBean getEventDefinitionCRF(String expression) {
		if (expression.split(ESCAPED_SEPARATOR).length < FOUR) {
			return null;
		}
		StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionFromExpression(expression);
		CRFBean crf = getCRFFromExpression(expression);

		if (studyEventDefinition == null || crf == null) {
			throw new OpenClinicaSystemException("OCRERR_0020");
		}

		String key = Integer.toString(this.expressionWrapper.getStudyBean().getId()).concat("_")
				.concat(Integer.toString(studyEventDefinition.getId())).concat("_")
				.concat(Integer.toString(crf.getId()));
		EventDefinitionCRFBean eventDefinitionCrf = sIdsedIdCrfIdToEventDefinitionMap.get(key);
		if (eventDefinitionCrf == null) {
			eventDefinitionCrf = getEventDefinitionCRFDao().findByStudyEventDefinitionIdAndCRFId(
					this.expressionWrapper.getStudyBean(), studyEventDefinition.getId(), crf.getId());
		}
		return eventDefinitionCrf;
	}

	/**
	 * Checks the validity of Item or Item Group Oid in Crf.
	 * 
	 * @param oid
	 *            Oid whose validity is to be checked.
	 * @param ruleSet
	 *            RuleSetBean containing crf to be checked.
	 * @return true if valid, false otherwise
	 */
	public String checkValidityOfItemOrItemGroupOidInCrf(String oid, RuleSetBean ruleSet) {
		int pos = 0;
		oid = oid.trim();
		String[] theOid = oid.split(ESCAPED_SEPARATOR);
		switch (theOid.length) {
			case FOUR :
				String edcOid = theOid[pos];
				EventDefinitionCRFBean eventDefinitionCRFBean = eventDefinitionCrfOidMap.get(edcOid);
				if (eventDefinitionCRFBean == null) {
					eventDefinitionCRFBean = getEventDefinitionCRFDao().findByOid(edcOid);
					cache(eventDefinitionCrfOidMap, edcOid, eventDefinitionCRFBean);
				}
				if (eventDefinitionCRFBean != null) {
					return oid;
				}
				pos++;
			case THREE :
				String crfOid = theOid[pos];
				CRFBean crfBean = crfOidMap.get(crfOid);
				if (crfBean == null) {
					crfBean = getCrfDao().findByOid(crfOid);
					cache(crfOidMap, crfOid, crfBean);
				}
				CRFVersionBean crfVersionBean = crfVersionOidMap.get(crfOid);
				if (crfVersionBean == null) {
					crfVersionBean = getCrfVersionDao().findByOid(crfOid);
					cache(crfVersionOidMap, crfOid, crfVersionBean);
				}
				if (crfBean != null && crfVersionBean != null) {
					return oid;
				}
				pos++;
			case TWO :
				String itemGroupOid = theOid[pos];
				ItemGroupBean itemGroup = itemGroupOidMap.get(itemGroupOid);
				if (itemGroup == null) {
					itemGroup = getItemGroupDao().findByOid(itemGroupOid);
					cache(itemGroupOidMap, itemGroupOid, itemGroup);
				}
				boolean isItemGroupBePartOfCrfOrNull = ruleSet.getCrfId() == null
						|| itemGroup.getCrfId().equals(ruleSet.getCrfId());
				if (itemGroup == null || !isItemGroupBePartOfCrfOrNull) {
					return oid;
				} else if (ruleSet.getCrfId() != null && !itemGroup.getCrfId().equals(ruleSet.getCrfId())) {
					return oid;
				}
				pos++;
			case ONE :
				String itemOid = theOid[pos];
				List<ItemBean> itemList = itemOidToItemListMap.get(itemOid);
				if (itemList == null) {
					itemList = getItemDao().findByOid(itemOid);
					cache(itemOidToItemListMap, itemOid, itemList);
				}
				if (itemList.size() == 0) {
					return oid;
				}
			default :
				break;
		}
		return "OK";
	}

	/**
	 * Check syntax of expression.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @return true if syntax is correct, false otherwise.
	 */
	public boolean checkSyntax(String expression) {
		if (expression.startsWith(SEPARATOR) || expression.endsWith(SEPARATOR)) {
			return false;
		}
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		int patternIndex = 0;
		for (int i = splitExpression.length - 1; i >= 0; i--) {
			if (!match(splitExpression[i], pattern[patternIndex++])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks insert action syntax of passed expression.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @return true if syntax is valid, false otherwise.
	 */
	public boolean checkInsertActionExpressionSyntax(String expression) {
		if (expression.startsWith(SEPARATOR) || expression.endsWith(SEPARATOR)) {
			return false;
		}
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		int patternIndex = 0;
		for (int i = splitExpression.length - 1; i >= 0; i--) {
			if (!match(splitExpression[i], ruleActionPattern[patternIndex++])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks rule expression syntax.
	 * 
	 * @param expression
	 *            Expression to be checked.
	 * @return true if syntax is correct, false otherwise
	 */
	public boolean checkRuleExpressionSyntax(String expression) {
		if (expression.startsWith(SEPARATOR) || expression.endsWith(SEPARATOR)) {
			return false;
		}
		String[] splitExpression = expression.split(ESCAPED_SEPARATOR);
		int patternIndex = 0;
		for (int i = splitExpression.length - 1; i >= 0; i--) {
			if (!match(splitExpression[i], rulePattern[patternIndex++])) {
				return false;
			}
		}
		return true;
	}

	private boolean match(String input, Pattern pattern) {
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	private ItemDAO getItemDao() {
		return new ItemDAO(ds);
	}

	private ItemDataDAO getItemDataDao() {
		return new ItemDataDAO(ds);
	}

	private CRFVersionDAO getCrfVersionDao() {
		return new CRFVersionDAO(ds);
	}

	private CRFDAO getCrfDao() {
		return new CRFDAO(ds);
	}

	private ItemGroupDAO getItemGroupDao() {
		return new ItemGroupDAO(ds);
	}

	private ItemGroupMetadataDAO getItemGroupMetadataDao() {
		return new ItemGroupMetadataDAO(ds);
	}

	private EventDefinitionCRFDAO getEventDefinitionCRFDao() {
		return new EventDefinitionCRFDAO(ds);
	}

	private StudyEventDefinitionDAO getStudyEventDefinitionDao() {
		return new StudyEventDefinitionDAO(ds);
	}

	private StudyEventDAO getStudyEventDao() {
		return new StudyEventDAO(ds);
	}

	private EventCRFDAO getEventCRFDao() {
		return new EventCRFDAO(ds);
	}

	/**
	 * Sets ExpressionObjectWrapper field.
	 * 
	 * @param expressionWrapper
	 *            ExpressionObjectWrapper object to be set
	 */
	public void setExpressionWrapper(ExpressionObjectWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
	}

	/**
	 * Method returns ExpressionObjectWrapper.
	 * 
	 * @return ExpressionObjectWrapper
	 */
	public ExpressionObjectWrapper getExpressionWrapper() {
		return expressionWrapper;
	}

	/**
	 * Prepares rule expression for parsing and evaluation by generating expressions for each group where repeating
	 * groups are applicable. Returns passed expression in list if not applicable.
	 * 
	 * @param expression
	 *            Rule expression string to be prepared
	 * @param ruleSet
	 *            RuleSetBean object to
	 * @return returns list of prepared expressions
	 */
	public List<String> prepareRuleExpression(String expression, RuleSetBean ruleSet) {
		List<String> expressions = new ArrayList<String>();
		List<String> expressionOCVariables = getExpressionOCVariables(expression, ruleSet);
		String newExpression;
		try {
			if (getExpressionCrfOids(expressionOCVariables).size() < 2) {
				return listifyString(expression);
			}
			List<String> crfOrCrfVersionOidsWithRepeatingGrps = getCrfOidsWithRepeatingGroup(expressionOCVariables,
					ruleSet);
			if (crfOrCrfVersionOidsWithRepeatingGrps.size() == 0) {
				return listifyString(expression);
			}
			for (String crfOrCrfVersionOid : crfOrCrfVersionOidsWithRepeatingGrps) {
				List<String> crfExpressionVars = getExpressionVariablesByCrfOid(expressionOCVariables,
						crfOrCrfVersionOid);
				for (String expressionVar : crfExpressionVars) {
					List<ItemDataBean> itemData = getItemDataForRepeatingGroupCRFs(expressionVar, ruleSet);
					if (itemData != null) {
						for (int i = 1; i <= itemData.size(); i++) {
							newExpression = insertGroupOrdinal(expressionVar, i);
							expressions.add(expression.replaceAll(expressionVar, newExpression));
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return listifyString(expression);
		}
		return expressions.size() > 0 ? expressions : listifyString(expression);
	}

	/**
	 * Gets list of expression variables that belong to specified crfOid.
	 * 
	 * @param expressionOCVariables
	 *            List<String>
	 * @param crfOid
	 *            String
	 * @return List<String>
	 */
	private List<String> getExpressionVariablesByCrfOid(List<String> expressionOCVariables, String crfOid) {
		List<String> crfExpressionVars = new ArrayList<String>();
		for (String var : expressionOCVariables) {
			if (var.contains(crfOid) && !StringValidator.itemExitsInList(var, crfExpressionVars)) {
				crfExpressionVars.add(var.trim());
			}
		}
		return crfExpressionVars;
	}

	/**
	 * Generates list of crf or crf version oids with repeating groups from expression. The target crf/version oid is
	 * not part of this list
	 * 
	 * @param expressionOCVariables
	 *            List<String>
	 * @param ruleSet
	 *            RuleSetBean
	 * @return List<String>
	 */
	private List<String> getCrfOidsWithRepeatingGroup(List<String> expressionOCVariables, RuleSetBean ruleSet) {
		List<String> crfOidsWithRepeatingGrps = new ArrayList<String>();

		String ruleGroupOid;
		String targetCrfOid;
		String crfOid;
		CRFVersionBean ruleCrfVersion;
		Boolean isRuleGroupRepeating;
		// Get target crf version
		CRFVersionBean targetCrfVersion = getCRFVersionFromExpression(ruleSet.getTarget().getValue());
		if (targetCrfVersion != null) {
			targetCrfOid = targetCrfVersion.getOid();
		} else {
			CRFBean crf = getCRFFromExpression(ruleSet.getTarget().getValue());
			targetCrfOid = crf.getOid();
		}

		for (String expr : expressionOCVariables) {
			crfOid = getCrfOid(expr);
			if (targetCrfOid.equals(crfOid)) {
				continue;
			}
			ruleGroupOid = getItemGroupOid(expr);
			ruleCrfVersion = getCRFVersionFromExpression(expr);
			if (ruleCrfVersion == null) {
				isRuleGroupRepeating = isItemGroupRepeatingBasedOnAllCrfVersionsMap.get(ruleGroupOid);
				if (isRuleGroupRepeating == null) {
					isRuleGroupRepeating = getItemGroupDao().isItemGroupRepeatingBasedOnAllCrfVersions(ruleGroupOid);
					cache(isItemGroupRepeatingBasedOnAllCrfVersionsMap, ruleGroupOid, isRuleGroupRepeating);
				}
			} else {
				String key = ruleGroupOid.concat("_").concat(Integer.toString(ruleCrfVersion.getId()));
				isRuleGroupRepeating = isItemGroupRepeatingBasedOnCrfVersionMap.get(key);
				if (isRuleGroupRepeating == null) {
					isRuleGroupRepeating = getItemGroupDao().isItemGroupRepeatingBasedOnCrfVersion(ruleGroupOid,
							ruleCrfVersion.getId());
					cache(isItemGroupRepeatingBasedOnCrfVersionMap, key, isRuleGroupRepeating);
				}
			}
			if (isRuleGroupRepeating && !StringValidator.itemExitsInList(crfOid, crfOidsWithRepeatingGrps)) {
				crfOidsWithRepeatingGrps.add(crfOid);
			}
		}
		return crfOidsWithRepeatingGrps;
	}

	/**
	 * Puts string in List<String> object and return list.
	 * 
	 * @param string
	 *            string
	 * @return List<String>
	 */
	private List<String> listifyString(String string) {
		List<String> strings = new ArrayList<String>();
		strings.add(string);
		return strings;
	}

	/**
	 * Extracts crf or crf version oids from rule expression and returns them distinctly in a list.
	 * 
	 * @param expressionOCVariables
	 *            List<String>
	 * @return List<String>
	 */
	List<String> getExpressionCrfOids(List<String> expressionOCVariables) {
		String crfOid;
		List<String> crfOids = new ArrayList<String>();
		for (String expr : expressionOCVariables) {
			if (!isPossibleOCVariable(expr)) {
				continue;
			}
			crfOid = getCrfOid(expr);
			if (!StringValidator.itemExitsInList(crfOid, crfOids)) {
				crfOids.add(crfOid);
			}

		}
		return crfOids;
	}

	/**
	 * Extracts OC variables from expression and returns them in list.
	 * 
	 * @param expression
	 *            String
	 * @param ruleSet
	 *            RuleSetBean
	 * @return List<String>
	 */
	private List<String> getExpressionOCVariables(String expression, RuleSetBean ruleSet) {
		String[] expressionParts = expression.split(RULE_EXPRESSION_OPERANDS_SPLIT);
		String fullExpression;
		List<String> expressionOCVariables = new ArrayList<String>();
		for (String expr : expressionParts) {
			if (!isPossibleOCVariable(expr)) {
				continue;
			}
			try {
				fullExpression = constructFullExpressionFromPartial(expr, ruleSet.getTarget().getValue());
				expressionOCVariables.add(fullExpression.trim());
			} catch (OpenClinicaSystemException e) {
				// Full expression cannot be constructed
				logger.error(e.getMessage());
			}
		}
		return expressionOCVariables;
	}

	/**
	 * Check if var is a possbible OpenClinicaVariable.
	 * 
	 * @param var
	 *            String
	 * @return boolean
	 */
	private boolean isPossibleOCVariable(String var) {
		var = var.trim();
		return !"".equals(var) && !var.equals("_CURRENT_DATE") && !var.equals("_SUBJECT_DOB")
				&& !var.equals("_SUBJECT_ENROLLMENT");
	}

	/**
	 * Gets subject's saved data for CRFs with repeating groups.
	 * 
	 * @param expression
	 *            String
	 * @param ruleSet
	 *            RuleSetBean
	 * @return List<ItemDataBean>
	 */
	private List<ItemDataBean> getItemDataForRepeatingGroupCRFs(String expression, RuleSetBean ruleSet) {
		try {
			// Get the studyEventId from RuleSet Target so we can know which
			// StudySubject we are dealing with.
			String targetExpression = ruleSet.getTarget().getValue();
			String targetExpressionStudyEventId = getStudyEventDefinitionOidOrdinalFromExpression(targetExpression);

			StudyEventBean studyEvent = studyEventIdMap.get(Integer.parseInt(targetExpressionStudyEventId));
			if (studyEvent == null) {
				studyEvent = (StudyEventBean) getStudyEventDao()
						.findByPK(Integer.valueOf(targetExpressionStudyEventId));
				cache(studyEventIdMap, Integer.parseInt(targetExpressionStudyEventId), studyEvent);
			}

			// Prepare Method arguments
			String studyEventDefinitionOid = getStudyEventDefinitionOidFromExpression(expression);
			String crfOrCrfVersionOid = getCrfOidFromExpression(expression);
			String studyEventDefinitionOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
			studyEventDefinitionOrdinal = studyEventDefinitionOrdinal.equals("") ? "1" : studyEventDefinitionOrdinal;
			String studySubjectId = String.valueOf(studyEvent.getStudySubjectId());

			logger.debug(
					"ruleSet studyEventId  {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEventDefinitionOrdinal {} ,studySubjectId {}",
					studyEvent.getId(), studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal,
					studySubjectId);

			String key = studyEventDefinitionOid.concat("_").concat(crfOrCrfVersionOid).concat("_")
					.concat(studyEventDefinitionOrdinal).concat("_").concat(studySubjectId);
			StudyEventBean studyEventofThisExpression = sedOidCrfOidSedOrdinalSsIdToStudyEventMap.get(key);
			if (studyEventofThisExpression == null) {
				studyEventofThisExpression = getStudyEventDao().findAllByStudyEventDefinitionAndCrfOidsAndOrdinal(
						studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal, studySubjectId);
				cache(sedOidCrfOidSedOrdinalSsIdToStudyEventMap, key, studyEventofThisExpression);
			}

			logger.debug("studyEvent : {} , itemOid {} , itemGroupOid {}", studyEventofThisExpression.getId(),
					getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));

			key = Integer.toString(studyEventofThisExpression.getId()).concat("_")
					.concat(getItemGroupOidFromExpression(expression)).concat("_")
					.concat(getItemOidFromExpression(expression));
			List<ItemDataBean> itemData = seIdItemOidItemGroupOidToItemDataMap.get(key);
			if (itemData == null) {
				itemData = getItemDataDao().findByStudyEventAndOids(studyEventofThisExpression.getId(),
						getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression));
				cache(seIdItemOidItemGroupOidToItemDataMap, key, itemData);
			}
			return itemData;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Inserts group ordinal into expression.
	 * 
	 * @param expression
	 *            => expression to be modified
	 * @param i
	 *            => group ordinal number to be inserted
	 * @return String
	 */
	String insertGroupOrdinal(String expression, int i) {
		// If expression contains "UNGROUPED" do nothing
		if (expression.contains("UNGROUPED")) {
			return expression;
		}
		// If expression already has group ordinal, do nothing
		String groupOrdinal = getItemGroupOidOrdinalFromExpression(expression);
		if (!groupOrdinal.equals("")) {
			return expression;
		}
		// Insert group ordinal and return expression.
		int lastSeparatorIndex = expression.lastIndexOf(SEPARATOR);
		String firstPart = expression.substring(0, lastSeparatorIndex);
		String lastPart = expression.substring(lastSeparatorIndex);
		return firstPart + OPENNIG_BRACKET + i + CLOSING_BRACKET + lastPart;
	}
}
