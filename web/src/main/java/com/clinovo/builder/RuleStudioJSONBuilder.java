package com.clinovo.builder;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionRunBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build Rule JSON object for Rules Studio.
 */
@SuppressWarnings("unchecked")
public class RuleStudioJSONBuilder {

	private DataSource dataSource;
	private String targetEventOID = "";
	private String targetCRFOID = "";
	private String targetVersionOID = "";

	public static final String ITEM_PREFIX = "I_";
	public static final int ITEM_OID_WITHOUT_EVENT_LENGTH = 3;
	public static final int ITEM_OID_WITH_EVENT_LENGTH = 4;

	/**
	 * Public constructor with the DataSource.
	 *
	 * @param dataSource DataSource.
	 */
	public RuleStudioJSONBuilder(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Build JSON rule object.
	 *
	 * @param rule        RuleBean.
	 * @param ruleSetRule RuleSetRuleBean.
	 * @return JSONObject.
	 * @throws Exception in case if error will occur during JSON generation.
	 */
	public JSONObject buildRule(RuleBean rule, RuleSetRuleBean ruleSetRule) throws Exception {
		JSONObject jsonRule = new JSONObject();
		JSONArray ruleActions = buildJSONActions(ruleSetRule, jsonRule);
		// Rule run
		RuleActionRunBean ruleActionRun = rule.getRuleSetRules().get(0).getActions().get(0).getRuleActionRun();
		jsonRule.put("di", ruleActionRun.getImportDataEntry());
		jsonRule.put("dde", ruleActionRun.getDoubleDataEntry());
		jsonRule.put("ide", ruleActionRun.getInitialDataEntry());
		jsonRule.put("ae", ruleActionRun.getAdministrativeDataEntry());
		// Targets
		ExpressionBean originalTarget = rule.getRuleSetRules().get(0).getRuleSetBean().getOriginalTarget();
		JSONArray targetsJSONArray = buildJSONTargets(originalTarget);
		// Rule properties
		jsonRule.put("targets", targetsJSONArray);
		jsonRule.put("oid", rule.getOid());
		jsonRule.put("actions", ruleActions);
		if (!rule.getDescription().isEmpty()) {
			jsonRule.put("name", rule.getDescription());
		} else {
			jsonRule.put("name", rule.getName());
		}
		jsonRule.put("expression", rule.getExpression().getValue());
		return jsonRule;
	}

	/**
	 * Build JSONArray of Study Events in a JSON for a specific Study.
	 *
	 * @param study StudyBean.
	 * @return JSONArray.
	 * @throws Exception in case if error will occur during JSON generation.
	 */
	public JSONArray buildEventsArray(StudyBean study) throws Exception {
		JSONArray events = new JSONArray();
		StudyEventDefinitionDAO eventDAO = new StudyEventDefinitionDAO(dataSource);
		List<StudyEventDefinitionBean> studyEvents = eventDAO.findAllByStudy(study.getId());
		for (StudyEventDefinitionBean evt : studyEvents) {
			if (!evt.getStatus().equals(Status.DELETED)) {
				JSONObject obj = new JSONObject();
				obj.put("id", evt.getId());
				obj.put("oid", evt.getOid());
				obj.put("name", evt.getName());
				obj.put("ordinal", evt.getOrdinal());
				obj.put("description", evt.getDescription());
				events.put(obj);
			}
		}
		return events;
	}

	/**
	 * Get JSONArray of Event Definition CRFs for a specific event.
	 *
	 * @param event StudyEventDefinitionBean
	 * @param study StudyBean
	 * @return JSONArray
	 * @throws Exception in case if error will occur during JSON generation.
	 */
	public JSONArray buildCRFsArray(StudyEventDefinitionBean event, StudyBean study)
			throws Exception {
		JSONArray crfs = new JSONArray();
		CRFDAO crfDAO = new CRFDAO(dataSource);
		EventDefinitionCRFDAO eCrfDAO = new EventDefinitionCRFDAO(dataSource);
		List<EventDefinitionCRFBean> eventCRFs = (List<EventDefinitionCRFBean>) eCrfDAO
				.findAllActiveByEventDefinitionId(study, event.getId());
		for (EventDefinitionCRFBean crf : eventCRFs) {
			JSONObject obj = new JSONObject();
			CRFBean cf = (CRFBean) crfDAO.findByPK(crf.getCrfId());
			if (!cf.getStatus().equals(Status.DELETED)) {
				obj.put("id", cf.getId());
				obj.put("oid", cf.getOid());
				obj.put("name", cf.getName());
				obj.put("version", cf.getVersionNumber());
				crfs.put(obj);
			}
		}
		return crfs;
	}

	/**
	 * Get JSONArray of the Versions for a specific CRF.
	 *
	 * @param crf CRFBean
	 * @return JSONArray
	 * @throws Exception in case if error will occur during JSON generation.
	 */
	public JSONArray buildVersionsArray(CRFBean crf) throws Exception {
		JSONArray versions = new JSONArray();
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		List<CRFVersionBean> vers = (List<CRFVersionBean>) crfVersionDAO.findAllActiveByCRF(crf.getId());
		for (CRFVersionBean ver : vers) {
			if (!ver.getStatus().equals(Status.DELETED)) {
				JSONObject obj = new JSONObject();
				obj.put("id", ver.getId());
				obj.put("oid", ver.getOid());
				obj.put("name", ver.getName());
				versions.put(obj);
			}
		}
		return versions;
	}

	/**
	 * Build Array of items for CRF Version.
	 *
	 * @param crfVersion CRFVersionBean
	 * @param study StudyBean
	 * @return JSONArray JSONArray.
	 * @throws Exception in case if there will be an error while JSON generation.
	 */
	public JSONArray buildItemsArray(CRFVersionBean crfVersion, StudyBean study) throws Exception {
		JSONArray items = new JSONArray();
		ItemDAO crfDAO = new ItemDAO(dataSource);
		ItemGroupDAO itemGroupDAO = new ItemGroupDAO(dataSource);
		ItemGroupMetadataDAO itemGroupMetaDAO = new ItemGroupMetadataDAO(dataSource);
		ItemFormMetadataDAO itemFormMetaDataDAO = new ItemFormMetadataDAO(dataSource);
		List<ItemBean> crfItems = (List<ItemBean>) crfDAO.findAllItemsByVersionId(crfVersion.getId());
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		List<CRFVersionBean> availableVersions = (List<CRFVersionBean>) crfVersionDAO.findAllActiveByCRF(crfVersion.getCrfId());
		boolean multipleVersions = availableVersions.size() > 1;
		StudyEventDefinitionDAO eventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		List<StudyEventDefinitionBean> availableEvens = eventDefinitionDAO.findAllActiveByStudyIdAndCRFId(crfVersion.getCrfId(), study.getId());
		boolean multipleEvents = availableEvens.size() > 1;

		for (ItemBean item : crfItems) {
			JSONObject obj = new JSONObject();
			// Item group
			ItemGroupBean itemGroup = itemGroupDAO.findByItemAndCRFVersion(item, crfVersion);
			// Form meta data
			ItemFormMetadataBean itemFormMetaData = itemFormMetaDataDAO.findByItemIdAndCRFVersionId(item.getId(),
					crfVersion.getId());
			obj.put("id", item.getId());
			obj.put("oid", item.getOid());
			obj.put("name", item.getName());
			obj.put("group", itemGroup.getOid());
			obj.put("type", item.getDataType().getName());
			obj.put("description", item.getDescription());
			obj.put("ordinal", itemFormMetaData.getOrdinal());
			obj.put("m_versions", Boolean.toString(multipleVersions));
			obj.put("m_events", Boolean.toString(multipleEvents));
			// Repeat item?
			ItemGroupMetadataBean itemGroupMeta = (ItemGroupMetadataBean) itemGroupMetaDAO.findByItemAndCrfVersion(
					item.getId(), crfVersion.getId());
			if (itemGroupMeta.isRepeatingGroup()) {
				obj.put("repeat", true);
			}
			items.put(obj);
		}
		return items;
	}

	/**
	 * Get data about all items in the Target, Expression, Destination of the rule.
	 *
	 * @param user        UserAccountBean
	 * @param rule        RuleBean
	 * @param ruleSetRule RuleSetRuleBean
	 * @return JSONObject
	 * @throws Exception in case if there will be an error while JSON generation.
	 */
	public JSONArray getCascadeRuleData(UserAccountBean user, RuleBean rule, RuleSetRuleBean ruleSetRule) throws Exception {

		StudyBean currentStudy = (StudyBean) getStudyDao().findByPK(rule.getStudyId());
		JSONArray studies = buildStudiesArray(user);
		// parse rule target.
		ExpressionBean target = rule.getRuleSetRules().get(0).getRuleSetBean().getOriginalTarget();
		cascadeParse(currentStudy, studies, target.getValue(), target);
		// parse destinations if rule type is Insert or Hide.
		List<RuleActionBean> actions = ruleSetRule.getActions();
		for (RuleActionBean action : actions) {
			ActionType actionType = action.getActionType();

			if (actionType.equals(ActionType.INSERT)) {
				InsertActionBean insertAction = (InsertActionBean) action;
				for (PropertyBean property : insertAction.getProperties()) {
					if (property.getValue() == null && property.getValueExpression() != null) {
						cascadeParse(currentStudy, studies, property.getValueExpression().getValue(), null);
					}
					if (property.getOid() != null) {
						cascadeParse(currentStudy, studies, property.getOid(), null);
					}
				}
			} else if (actionType.equals(ActionType.HIDE)) {
				HideActionBean hideAction = (HideActionBean) action;
				for (PropertyBean property : hideAction.getProperties()) {
					if (property.getOid() != null && !property.getOid().isEmpty()) {
						cascadeParse(currentStudy, studies, property.getOid(), null);
					}
				}
			}
		}
		// parse expression
		String expression = rule.getExpression().getValue();
		String[] expressionItems = expression.replaceAll(ExpressionService.RULE_EXPRESSION_OPERANDS_SPLIT, "").split(" ");
		for (String item : expressionItems) {
			if (item != null && !item.isEmpty() && item.contains(ITEM_PREFIX)) {
				cascadeParse(currentStudy, studies, item, null);
			}
		}
		return studies;
	}

	/**
	 * Build array of the Studies in the JSON format.
	 *
	 * @param user UserAccountBean.
	 * @return JSONArray
	 * @throws JSONException in case if error will occurs.
	 */
	public JSONArray buildStudiesArray(UserAccountBean user) throws JSONException {
		JSONArray studies = new JSONArray();
		StudyDAO studyDAO = new StudyDAO(dataSource);
		List<StudyBean> availableStudies = studyDAO.findAllActiveStudiesWhereUserHasRole(user.getName());
		for (StudyBean studyBean : availableStudies) {
			if (!studyBean.isSite(studyBean.getParentStudyId())) {
				JSONObject study = convertStudyToJSON(studyBean);
				studies.put(study);
			}
		}
		return studies;
	}

	private JSONObject convertStudyToJSON(StudyBean studyBean) throws JSONException {
		JSONObject study = new JSONObject();
		study.put("id", studyBean.getId());
		study.put("oid", studyBean.getOid());
		study.put("name", studyBean.getName());
		study.put("description", studyBean.getSummary());
		study.put("identifier", studyBean.getIdentifier());
		return study;
	}

	/**
	 * ExpressionBean should be entered only in case if rule's target is parsed. In all other cases it should be null.
	 * @param study StudyBean
	 * @param studies JSONArray
	 * @param item String
	 * @param expression ExpressionBean
	 * @return JSONArray
	 * @throws Exception in case if some error will be thrown.
	 */
	public JSONArray cascadeParse(StudyBean study, JSONArray studies, String item, ExpressionBean expression) throws Exception {
		String eventOID = "";
		String [] itemElements = item.split("\\.");

		if (itemElements.length > ITEM_OID_WITHOUT_EVENT_LENGTH) {
			eventOID = itemElements[0];
		} else if (expression == null && !targetEventOID.isEmpty()) {
			eventOID = targetEventOID;
		} else if (expression != null && expression.getTargetEventOid() != null) {
			eventOID = expression.getTargetEventOid();
		}
		String formOID;

		if (itemElements.length >= ITEM_OID_WITHOUT_EVENT_LENGTH) {
			formOID = getItemVersion(item);
		} else if (expression == null && !targetCRFOID.isEmpty()) {
			formOID = targetCRFOID;
		} else {
			throw new Exception("CRF OID was not found in the rule target.");
		}
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		CRFVersionBean crfVersionBean;

		if (expression == null && !targetVersionOID.isEmpty() && itemElements.length < ITEM_OID_WITHOUT_EVENT_LENGTH) {
			crfVersionBean = crfVersionDAO.findByOid(targetVersionOID);
		} else {
			crfVersionBean = crfVersionDAO.findByOid(formOID);
		}
		CRFDAO crfDao = new CRFDAO(dataSource);
		CRFBean crfBean;

		if (crfVersionBean != null) {
			crfBean = (CRFBean) crfDao.findByPK(crfVersionBean.getCrfId());
		} else {
			crfBean = crfDao.findByOid(formOID);
			if (expression != null && expression.getTargetVersionOid() != null) {
				crfVersionBean = crfVersionDAO.findByOid(expression.getTargetVersionOid());
			}
		}
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean eventBean;

		if (!eventOID.isEmpty()) {
			eventBean = studyEventDefinitionDAO.findByOid(eventOID);
		} else {
			List<StudyEventDefinitionBean> studyEvents = studyEventDefinitionDAO
					.findAllActiveByStudyIdAndCRFId(crfBean.getId(), study.getId());
			if (studyEvents.isEmpty()) {
				throw new Exception("No active events found with one of the CRFs that are used in this rule");
			}
			eventBean = studyEvents.get(0);
		}

		String itemOID = itemElements[itemElements.length - 1];
		ItemDAO itemDAO = new ItemDAO(dataSource);
		List<ItemBean> itemBeans = itemDAO.findByOid(itemOID);

		if (itemBeans.isEmpty()) {
			throw new Exception("One of the items in this rule was not found in the CRF version to which it's binded.");
		}
		ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(dataSource);

		if (crfVersionBean != null) {
			ItemFormMetadataBean itemFormMetadataBean = itemFormMetadataDAO
					.findByItemIdAndCRFVersionId(itemBeans.get(0).getId(), crfVersionBean.getId());
			if (itemFormMetadataBean.getId() == 0) {
				throw new Exception("One of the items in this rule was not found in the CRF version to which it's binded.");
			}
		} else {
			List<CRFVersionBean> versionBeans = (List<CRFVersionBean>) crfVersionDAO.findAllActiveByCRF(crfBean.getId());
			if (versionBeans.isEmpty()) {
				throw new Exception("No active CRFs Versions were found for one of the CRFs that are used in this rule.");
			}
			for (CRFVersionBean versionBean : versionBeans) {
				ItemFormMetadataBean itemFormMetadataBean = itemFormMetadataDAO
						.findByItemIdAndCRFVersionId(itemBeans.get(0).getId(), versionBean.getId());
				if (itemFormMetadataBean.getId() != 0) {
					crfVersionBean = versionBean;
				}
			}
			if (crfVersionBean == null) {
				throw new Exception("One of the items in this rule was not found in the CRF version to which it's binded.");
			}
		}

		JSONArray existingEventsArray = getEntitiesFromArrayByOID(studies, study.getOid(), "events");
		JSONArray events;
		if (existingEventsArray == null) {
			events = buildEventsArray(study);
		} else {
			events = existingEventsArray;
		}
		JSONArray existingCRFsArray = getEntitiesFromArrayByOID(events, eventBean.getOid(), "crfs");
		JSONArray crfs;
		if (existingCRFsArray == null) {
			crfs = buildCRFsArray(eventBean, study);
		} else {
			crfs = existingCRFsArray;
		}
		JSONArray existingVersionsArray = getEntitiesFromArrayByOID(crfs, crfBean.getOid(), "versions");
		JSONArray versions;
		if (existingVersionsArray == null) {
			versions = buildVersionsArray(crfBean);
		} else {
			versions = existingVersionsArray;
		}
		JSONArray existingItemsArray = getEntitiesFromArrayByOID(versions, crfVersionBean.getOid(), "items");
		JSONArray items;
		if (existingItemsArray == null) {
			items = buildItemsArray(crfVersionBean, study);
		} else {
			items = existingItemsArray;
		}

		putFieldToAnArrayElementByOID(versions, items, crfVersionBean.getOid(), "items");
		putFieldToAnArrayElementByOID(crfs, versions, crfBean.getOid(), "versions");
		putFieldToAnArrayElementByOID(events, crfs, eventBean.getOid(), "crfs");
		putFieldToAnArrayElementByOID(studies, events, study.getOid(), "events");
		// if it's target.
		if (expression != null) {
			targetEventOID = eventBean.getOid();
			targetCRFOID = crfBean.getOid();
			targetVersionOID = crfVersionBean.getOid();
		}
		return studies;
	}

	/**
	 * Get StudyDao.
	 * @return StudyDao.
	 */
	public StudyDAO getStudyDao() {
		return new StudyDAO(dataSource);
	}

	private JSONArray getEntitiesFromArrayByOID(JSONArray array, String oid, String key) throws JSONException {
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			if (object.getString("oid").equals(oid)) {
				if (object.has(key)) {
					return object.getJSONArray(key);
				} else {
					return null;
				}
			}
		}
		return null;
	}

	private void putFieldToAnArrayElementByOID(JSONArray targetArray, JSONArray insertedArray,
											   String oid, String fieldName) throws JSONException {
		for (int i = 0; i < targetArray.length(); i++) {
			JSONObject object = targetArray.getJSONObject(i);
			if (object.getString("oid").equals(oid)) {
				object.put(fieldName, insertedArray);
				targetArray.put(i, object);
			}
		}
	}

	private JSONArray buildJSONActions(RuleSetRuleBean ruleSetRule, JSONObject jsonRule) throws JSONException {
		JSONObject jsonAction = new JSONObject();
		List<RuleActionBean> actions = ruleSetRule.getActions();
		jsonRule.put("evaluates", actions.get(0).getExpressionEvaluatesTo());

		ActionType actionType = actions.get(0).getActionType();
		jsonAction.put("type", actionType);
		jsonAction.put("select", actions.get(0).getExpressionEvaluatesTo());
		if (actionType.equals(ActionType.EMAIL)) {
			EmailActionBean emailAction = (EmailActionBean) actions.get(0);
			jsonAction.put("to", emailAction.getTo());
			jsonAction.put("body", emailAction.getMessage());
		} else if (actionType.equals(ActionType.FILE_DISCREPANCY_NOTE)) {
			DiscrepancyNoteActionBean discrepancyAction = (DiscrepancyNoteActionBean) actions.get(0);
			jsonAction.put("type", "discrepancy");
			jsonAction.put("message", discrepancyAction.getMessage());
		} else if (actionType.equals(ActionType.INSERT)) {
			InsertActionBean insertAction = (InsertActionBean) actions.get(0);
			jsonAction.put("type", "insert");
			jsonAction.put("message", insertAction.getCuratedMessage());
			// Destinations
			JSONArray destinations = new JSONArray();
			for (int x = 0; x < insertAction.getProperties().size(); x++) {
				JSONObject dest = new JSONObject();
				PropertyBean bean = insertAction.getProperties().get(x);
				dest.put("id", x + 1);
				dest.put("oid", bean.getOid());
				if (bean.getValue() == null && bean.getValueExpression() != null) {
					dest.put("item", true);
					dest.put("value", bean.getValueExpression().getValue());
				} else {
					dest.put("value", bean.getValue());
				}
				destinations.put(dest);
			}
			jsonAction.put("destinations", destinations);
		} else if (actionType.equals(ActionType.SHOW) || actionType.equals(ActionType.HIDE)) {
			jsonAction.put("type", "showHide");
			for (RuleActionBean action : actions) {
				if (action.getActionType().equals(ActionType.SHOW)) {
					ShowActionBean showAction = (ShowActionBean) action;
					jsonAction.put("type", "showHide");
					jsonAction.put("message", showAction.getMessage());
					jsonAction.put("show", showAction.getExpressionEvaluatesTo());
					jsonRule.put("evaluates", showAction.getExpressionEvaluatesTo());
				} else {
					HideActionBean hideAction = (HideActionBean) action;
					jsonAction.put("hide", hideAction.getExpressionEvaluatesTo());
					JSONArray destinations = new JSONArray();
					for (int d = 0; d < hideAction.getProperties().size(); d++) {
						destinations.put(hideAction.getProperties().get(d).getOid());
					}
					jsonAction.put("destinations", destinations);
				}
			}
		}
		JSONArray ruleActions = new JSONArray();
		ruleActions.put(jsonAction);
		return ruleActions;
	}

	private JSONArray buildJSONTargets(ExpressionBean target) throws JSONException {
		JSONArray targetsJSONArray = new JSONArray();
		JSONObject jsonTarget = new JSONObject();

		String targetPath = target.getValue();
		String name = targetPath.replaceAll("\\[\\w+\\]", "").trim();
		jsonTarget.put("name", name);
		if (targetPath.split("\\.").length > ITEM_OID_WITHOUT_EVENT_LENGTH) {
			jsonTarget.put("evt", getItemEvent(targetPath, jsonTarget));
		}
		if (isVersionified(targetPath)) {
			jsonTarget.put("versionify", true);
			jsonTarget.put("version", getItemVersion(targetPath));
		}
		if (getItemLine(targetPath, jsonTarget) != null) {
			jsonTarget.put("line", getItemLine(targetPath, jsonTarget));
		}
		if (target.getTargetEventOid() != null) {
			jsonTarget.put("target-event-oid", target.getTargetEventOid());
		}
		if (target.getTargetVersionOid() != null) {
			jsonTarget.put("target-version-oid", target.getTargetVersionOid());
		}
		jsonTarget.put("crf", getItemVersion(targetPath));
		jsonTarget.put("group", getItemGroup(targetPath));
		targetsJSONArray.put(jsonTarget);
		return targetsJSONArray;
	}

	private boolean isVersionified(String targetPath) {
		String crf = "";
		String[] preds = targetPath.split("\\.");
		if (preds.length == ITEM_OID_WITHOUT_EVENT_LENGTH) {
			crf = preds[0];
		} else if (preds.length > ITEM_OID_WITHOUT_EVENT_LENGTH) {
			crf = preds[preds.length - ITEM_OID_WITHOUT_EVENT_LENGTH];
		}
		Pattern pattern = Pattern.compile("V\\d+$");
		Matcher matcher = pattern.matcher(crf);
		return matcher.find();
	}

	private String getItemVersion(String targetPath) {
		String[] preds = targetPath.split("\\.");
		if (preds.length == ITEM_OID_WITHOUT_EVENT_LENGTH) {
			return preds[0];
		} else if (preds.length > ITEM_OID_WITHOUT_EVENT_LENGTH) {
			return preds[preds.length - ITEM_OID_WITHOUT_EVENT_LENGTH];
		} else {
			return targetPath;
		}
	}

	private String getItemGroup(String targetPath) throws JSONException {
		targetPath = targetPath.replaceAll("\\[\\w+\\]", "").trim();
		String[] preds = targetPath.split("\\.");
		if (preds.length == 2) {
			return preds[0];
		} else if (preds.length > 2) {
			return preds[preds.length - 2];
		} else {
			return targetPath;
		}
	}

	private String getItemEvent(String targetPath, JSONObject tar) throws JSONException {
		String[] preds = targetPath.split("\\.");
		if (preds.length == ITEM_OID_WITH_EVENT_LENGTH) {
			tar.put("eventify", true);
			return preds[0];
		} else {
			return targetPath;
		}
	}

	private String getItemLine(String targetPath, JSONObject tar) throws JSONException {
		Pattern pattern = Pattern.compile("(?<=\\[)(\\w+)(?=\\])");
		Matcher matcher = pattern.matcher(targetPath);
		if (matcher.find()) {
			tar.put("linefy", true);
			return matcher.group(0);
		}
		return null;
	}
}
