package com.clinovo.validation;

import com.clinovo.enums.CurrentDataEntryStage;
import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DataEntryUtil;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.RuleValidator;
import org.akaza.openclinica.control.form.ScoreItemValidator;
import org.akaza.openclinica.control.form.Validation;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.hibernate.DynamicsItemFormMetadataDao;
import org.akaza.openclinica.dao.hibernate.DynamicsItemGroupMetadataDao;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Display Item Bean Validator.
 */
@Component("displayItemValidator")
public final class DisplayItemBeanValidator {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DynamicsItemFormMetadataDao dynamicsItemFormMetadataDao;
	@Autowired
	private DynamicsItemGroupMetadataDao dynamicsItemGroupMetadataDao;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private DynamicsMetadataService dynamicsMetadataService;

	public static final String EVENT_DEF_CRF_BEAN = "event_def_crf_bean";
	public static final String INPUT_EVENT_CRF = "event";
	public static final String COUNT_VALIDATE = "countValidate";
	public static final int INT_3800 = 3800;
	public static final int INT_255 = 255;

	/**
	 *
	 * @param v DiscrepancyValidator
	 * @param dib DisplayItemBean
	 * @param inputName String
	 * @param request HttpServletRequest
	 * @return DisplayItemBean
	 */
	public DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName,
													  HttpServletRequest request) {

		Locale locale = LocaleResolver.getLocale(request);
		CurrentDataEntryStage dataEntryStage = DataEntryUtil.getDataEntryStageFromRequest(request);

		if (dataEntryStage == CurrentDataEntryStage.ADMINISTRATIVE_DATA_ENTRY
				|| dataEntryStage == CurrentDataEntryStage.INITIAL_DATA_ENTRY
				|| dataEntryStage == CurrentDataEntryStage.VIEW_DATA_ENTRY) {
			ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

			if (StringUtil.isBlank(inputName) && dataEntryStage != CurrentDataEntryStage.VIEW_DATA_ENTRY) {
				dib = DataEntryUtil.loadFormValue(dib, request);
			}
			if (rt.equals(ResponseType.TEXT)
					|| rt.equals(ResponseType.TEXTAREA)
					|| rt.equals(ResponseType.FILE)) {
				dib = validateDisplayItemBeanText(v, dib, inputName, request);
			} else if (rt.equals(ResponseType.RADIO)
					|| rt.equals(ResponseType.SELECT)) {
				dib = validateDisplayItemBeanCV(v, dib, inputName, true);
			} else if (rt.equals(ResponseType.CHECKBOX)
					|| rt.equals(ResponseType.SELECTMULTI)) {
				dib = validateDisplayItemBeanCV(v, dib, inputName, false);
			}
		} else if (DataEntryUtil.getDataEntryStageFromRequest(request) == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
			ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();
			boolean isSingleItem = false;
			if (StringUtil.isBlank(inputName)) {
				inputName = DataEntryUtil.getInputName(dib);
				isSingleItem = true;
			}
			EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
			int keyId = ecb.getId();
			HttpSession session = request.getSession();
			Integer validationCount = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);
			ItemDataBean valueToCompareTmp = dib.getData();
			ItemDataBean valueToCompare = copyItemDataBean(valueToCompareTmp);

			if (!isSingleItem) {
				valueToCompare = dib.getDbData();
			}

			if (isSingleItem) {
				dib = DataEntryUtil.loadFormValue(dib, request);
			}
			DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
			boolean showOriginalItem = dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, valueToCompare);
			boolean showItem = dib.getMetadata().isShowItem();
			if (!showItem && dib.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
				showItem = true;
			}
			boolean showDuplicateItem = dynamicsMetadataService.hasPassedDDE(dib.getMetadata(), ecb, valueToCompare);

			if (showOriginalItem && showDuplicateItem || showItem) {
				if (rt.equals(ResponseType.TEXT) || rt.equals(ResponseType.TEXTAREA) || rt.equals(ResponseType.FILE)) {
					dib = validateDisplayItemBeanText(v, dib, inputName, request);
					if (validationCount == null || validationCount == 0) {
						v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
						v.setErrorMessage(messageSource.getMessage("value_you_specified", null, locale)
								+ " " + valueToCompare.getValue() + " "
								+ messageSource.getMessage("from_initial_data_entry", null, locale));
					}
				} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
						|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
					dib = validateDisplayItemBeanCV(v, dib, inputName, true);

					if (validationCount == null || validationCount == 0) {
						v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
						String errorValue = valueToCompare.getValue();
						ArrayList options = dib.getMetadata().getResponseSet().getOptions();

						for (Object option : options) {
							ResponseOptionBean rob = (ResponseOptionBean) option;
							if (rob.getValue().equals(errorValue)) {
								errorValue = rob.getText();
							}
						}
						v.setErrorMessage(messageSource.getMessage("value_you_specified", null, locale)
								+ " " + errorValue + " "
								+ messageSource.getMessage("from_initial_data_entry", null, locale));
					}
				} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
						|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
					dib = validateDisplayItemBeanCV(v, dib, inputName, false);

					if (validationCount == null || validationCount == 0) {
						v.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, true);
						String errorValue = valueToCompare.getValue();
						String errorTexts = "";

						ArrayList options = dib.getMetadata().getResponseSet().getOptions();

						for (int u = 0; u < options.size(); u++) {
							ResponseOptionBean rob = (ResponseOptionBean) options.get(u);
							if (errorValue.contains(rob.getValue())) {
								errorTexts = errorTexts + rob.getText();
								if (u < options.size() - 1) {
									errorTexts = errorTexts + ", ";
								}
							}
						}
						v.setErrorMessage(messageSource.getMessage("value_you_specified", null, locale)
								+ " " + errorTexts + " "
								+ messageSource.getMessage("from_initial_data_entry", null, locale));
					}
				}
			}
		}
		return dib;
	}

	/**
	 * Perform validation on a item which has a TEXT or TEXTAREA response type. If the item has a null value, it's
	 * automatically validated. Otherwise, it's checked against its data type.
	 *
	 * @param v       The Validator to add validations to.
	 * @param dib     The DisplayItemBean to validate.
	 * @param request HttpServletRequest
	 * @param inputName String
	 * @return The DisplayItemBean which is validated.
	 */
	public DisplayItemBean validateDisplayItemBeanText(DiscrepancyValidator v, DisplayItemBean dib,
														  String inputName, HttpServletRequest request) {

		FormProcessor fp = new FormProcessor(request);
		EventDefinitionCRFBean edcb = (EventDefinitionCRFBean) request.getAttribute(EVENT_DEF_CRF_BEAN);
		if (StringUtil.isBlank(inputName)) {
			inputName = DataEntryUtil.getInputName(dib);
		}
		ItemBean ib = dib.getItem();
		ItemFormMetadataBean ibMeta = dib.getMetadata();
		ItemDataType idt = ib.getDataType();
		ItemDataBean idb = dib.getData();

		boolean isNull = false;
		ArrayList nullValues = edcb.getNullValuesList();
		for (Object nullValue : nullValues) {
			NullValue nv = (NullValue) nullValue;
			if (nv.getName().equals(fp.getString(inputName))) {
				isNull = true;
			}
		}
		if (!isNull) {
			if (StringUtil.isBlank(idb.getValue())) {
				if (ibMeta.isRequired() && ibMeta.isShowItem()) {
					v.addValidation(inputName, Validator.IS_REQUIRED);
				}
			} else {
				if (idt.equals(ItemDataType.ST)) {
					if (ibMeta.getResponseSet().getResponseType() == org.akaza.openclinica.bean.core.ResponseType.TEXTAREA) {
						v.addValidation(inputName, Validator.LENGTH_NUMERIC_COMPARISON,
								NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_3800);
					} else {
						v.addValidation(inputName, Validator.LENGTH_NUMERIC_COMPARISON,
								NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, INT_255);
					}
				} else if (idt.equals(ItemDataType.INTEGER)) {
					v.addValidation(inputName, Validator.IS_AN_INTEGER);
					v.alwaysExecuteLastValidation(inputName);
				} else if (idt.equals(ItemDataType.REAL)) {
					v.addValidation(inputName, Validator.IS_A_FLOAT);
					v.alwaysExecuteLastValidation(inputName);
				} else if (idt.equals(ItemDataType.SET)) {
					v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata()
							.getResponseSet());
				} else if (idt.equals(ItemDataType.DATE)) {
					v.addValidation(inputName, Validator.IS_A_DATE);
					v.alwaysExecuteLastValidation(inputName);
				} else if (idt.equals(ItemDataType.PDATE)) {
					v.addValidation(inputName, Validator.IS_PARTIAL_DATE);
					v.alwaysExecuteLastValidation(inputName);
				}
				if (ibMeta.getWidthDecimal().length() > 0) {
					ArrayList<String> params = new ArrayList<String>();
					params.add(0, idt.getName());
					params.add(1, ibMeta.getWidthDecimal());
					v.addValidation(inputName, Validator.IS_VALID_WIDTH_DECIMAL, params);
					v.alwaysExecuteLastValidation(inputName);
				}
				validateRegexpAndFunctions(v, dib, inputName);
			}
		}
		return dib;
	}

	/**
	 *
	 * @param dib DisplayItemBean
	 * @param inputName String
	 * @param rv RuleValidator
	 * @param groupOrdinalPLusItemOid groupOrdinalPLusItemOid
	 * @param fireRuleValidation fireRuleValidation
	 * @param messages ArrayList<String>
	 * @param request HttpServletRequest
	 * @return DisplayItemBean
	 */
	public DisplayItemBean validateDisplayItemBean(DisplayItemBean dib, String inputName,
													  RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid,
													  Boolean fireRuleValidation,
													  ArrayList<String> messages, HttpServletRequest request) {

		CurrentDataEntryStage dataEntryStage = DataEntryUtil.getDataEntryStageFromRequest(request);

		if (dataEntryStage == CurrentDataEntryStage.ADMINISTRATIVE_DATA_ENTRY || dataEntryStage == CurrentDataEntryStage.INITIAL_DATA_ENTRY) {
			if (StringUtil.isBlank(inputName)) {
				dib = DataEntryUtil.loadFormValue(dib, request);
			}
		} else if (dataEntryStage == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
			if (StringUtil.isBlank(inputName)) {
				inputName = DataEntryUtil.getInputName(dib);
				dib = DataEntryUtil.loadFormValue(dib, request);
			}
		}
		if (dataEntryStage != CurrentDataEntryStage.VIEW_DATA_ENTRY && (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation)) {
			messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
			dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
		}
		return dib;
	}

	/**
	 *
	 * @param v DiscrepancyValidator
	 * @param digbs List<DisplayItemGroupBean>
	 * @param formGroups List<DisplayItemGroupBean>
	 * @param request HttpServletRequest
	 * @return List<DisplayItemGroupBean>
	 */
	public List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v,
																	  List<DisplayItemGroupBean> digbs,
																	  List<DisplayItemGroupBean> formGroups,
																	  HttpServletRequest request) {
		CurrentDataEntryStage dataEntryStage = DataEntryUtil.getDataEntryStageFromRequest(request);

		if (dataEntryStage == CurrentDataEntryStage.INITIAL_DATA_ENTRY || dataEntryStage == CurrentDataEntryStage.ADMINISTRATIVE_DATA_ENTRY) {
			for (DisplayItemGroupBean displayGroup : formGroups) {
				List<DisplayItemBean> items = displayGroup.getItems();
				for (DisplayItemBean displayItem : items) {
					String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(),
							displayItem, !displayGroup.isAuto());
					validateDisplayItemBean(v, displayItem, inputName, request);
				}
			}
		} else if (dataEntryStage == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
			EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
			int keyId = ecb.getId();
			HttpSession session = request.getSession();
			Integer validationCount = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);
			String inputName = "";
			for (int i = 0; i < formGroups.size(); i++) {
				DisplayItemGroupBean displayGroup = formGroups.get(i);

				List<DisplayItemBean> items = displayGroup.getItems();
				for (DisplayItemBean displayItem : items) {
					inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(),
							displayItem, !displayGroup.isAuto());
					validateDisplayItemBean(v, displayItem, inputName, request);
				}

				if (validationCount == null || validationCount == 0) {
					if (i == 0 && formGroups.size() != digbs.size()) {
						v.addValidation(inputName + "group", Validator.DIFFERENT_NUMBER_OF_GROUPS_IN_DDE);
						v.setErrorMessage(messageSource.getMessage("additional_values_that_were_not_present_at_initial",
								null, LocaleResolver.getLocale()) + inputName);
					}
				}
			}
		}
		return formGroups;
	}


	/**
	 * Customized validation for item input.
	 *
	 * @param v         DiscrepancyValidator
	 * @param dib       DisplayItemBean
	 * @param inputName String
	 */
	private void validateRegexpAndFunctions(DiscrepancyValidator v, DisplayItemBean dib, String inputName) {
		String customValidationString = dib.getMetadata().getRegexp();
		if (!StringUtil.isBlank(customValidationString)) {
			Validation customValidation = null;
			if (customValidationString.startsWith("func:")) {
				try {
					customValidation = Validator.processCRFValidationFunction(customValidationString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (customValidationString.startsWith("regexp:")) {
				try {
					customValidation = Validator.processCRFValidationRegex(customValidationString);
				} catch (Exception e) {
					logger.error("Error has occurred.", e);
				}
			}
			if (customValidation != null) {
				customValidation.setErrorMessage(dib.getMetadata().getRegexpErrorMsg());
				v.addValidation(inputName, customValidation);
			}
		}
	}

	/**
	 *
	 * @param digbs List<DisplayItemGroupBean>
	 * @param formGroups List<DisplayItemGroupBean>
	 * @param rv RuleValidator
	 * @param groupOrdinalPLusItemOid HashMap<String, ArrayList<String>>
	 * @param request HttpServletRequest
	 * @return List<DisplayItemGroupBean>
	 */
	public List<DisplayItemGroupBean> validateDisplayItemGroupBean(List<DisplayItemGroupBean> digbs,
																	  List<DisplayItemGroupBean> formGroups,
																	  RuleValidator rv,
																	  HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid,
																	  HttpServletRequest request) {
		CurrentDataEntryStage dataEntryStage = DataEntryUtil.getDataEntryStageFromRequest(request);
		if (dataEntryStage != CurrentDataEntryStage.VIEW_DATA_ENTRY) {
			if (dataEntryStage == CurrentDataEntryStage.ADMINISTRATIVE_DATA_ENTRY
					|| dataEntryStage == CurrentDataEntryStage.INITIAL_DATA_ENTRY) {
				for (DisplayItemGroupBean displayGroup : formGroups) {
					List<DisplayItemBean> items = displayGroup.getItems();
					int order = displayGroup.getOrdinal();
					for (DisplayItemBean displayItem : items) {
						String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(), displayItem, !displayGroup.isAuto());
						if (groupOrdinalPLusItemOid.containsKey(displayItem.getItem().getOid())
								|| groupOrdinalPLusItemOid.containsKey(String.valueOf(order + 1)
								+ displayItem.getItem().getOid())) {
							validateDisplayItemBean(displayItem, inputName, rv, groupOrdinalPLusItemOid, true,
									groupOrdinalPLusItemOid.get(String.valueOf(order + 1) + displayItem.getItem().getOid()),
									request);
						} else {
							validateDisplayItemBean(displayItem, inputName, rv, groupOrdinalPLusItemOid, false, null,
									request);
						}
					}
				}
			} else if (dataEntryStage == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
				EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

				for (DisplayItemGroupBean displayGroup : formGroups) {
					List<DisplayItemBean> items = displayGroup.getItems();
					int order = displayGroup.getOrdinal();

					for (DisplayItemBean displayItem : items) {
						String inputName = DataEntryUtil.getGroupItemInputName(displayGroup, displayGroup.getFormInputOrdinal(),
								displayItem, !displayGroup.isAuto());

						if (displayItem.getMetadata().isShowItem()
								|| getDynamicsMetadataService().isShown(displayItem.getItem().getId(), ecb,
								displayItem.getData())) {
							if (groupOrdinalPLusItemOid.containsKey(displayItem.getItem().getOid())
									|| groupOrdinalPLusItemOid.containsKey(String.valueOf(order + 1)
									+ displayItem.getItem().getOid())) {
								validateDisplayItemBean(displayItem, inputName, rv, groupOrdinalPLusItemOid,
										true, groupOrdinalPLusItemOid.get(String.valueOf(order + 1) + displayItem.getItem().getOid()),
										request);
							} else {
								validateDisplayItemBean(displayItem, inputName, rv, groupOrdinalPLusItemOid, false, null,
										request);
							}
						} else {
							System.out.println("OUT : " + String.valueOf(order + 1) + displayItem.getItem().getOid());
						}
					}
				}
			}
			return formGroups;
		}
		return digbs;
	}

	/**
	 *
	 * @param sv ScoreItemValidator
	 * @param dib DisplayItemBean
	 * @param inputName String
	 * @param request HttpServletRequest
	 * @return DisplayItemBean
	 */
	public DisplayItemBean validateCalcTypeDisplayItemBean(ScoreItemValidator sv, DisplayItemBean dib,
															  String inputName, HttpServletRequest request) {

		CurrentDataEntryStage dataEntryStage = DataEntryUtil.getDataEntryStageFromRequest(request);
		if (dataEntryStage == CurrentDataEntryStage.DOUBLE_DATA_ENTRY) {
			ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();
			Locale locale = LocaleResolver.getLocale(request);
			ItemDataDAO iddao = new ItemDataDAO(dataSource);
			boolean isSingleItem = false;
			if (StringUtil.isBlank(inputName)) {
				inputName = DataEntryUtil.getInputName(dib);
				isSingleItem = true;
			}
			EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);

			ItemDataBean valueToCompare = new ItemDataBean();
			if (isSingleItem) {
				int idId = dib.getData().getId();
				if (idId > 0) {
					valueToCompare = (ItemDataBean) iddao.findByPK(idId);
				}
			} else {
				valueToCompare = dib.getDbData();
			}
			DynamicsMetadataService dynamicsMetadataService = getDynamicsMetadataService();
			if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
					|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
				boolean showOriginalItem = dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, valueToCompare);
				boolean showItem = dib.getMetadata().isShowItem();
				boolean showDuplicateItem = dynamicsMetadataService.hasPassedDDE(dib.getMetadata(), ecb, valueToCompare);

				if (showOriginalItem && showDuplicateItem || showItem) {
					dib = validateDisplayItemBeanText(sv, dib, inputName, request);
				}
				if (showOriginalItem && showDuplicateItem || showItem) {
					sv.addValidation(inputName, Validator.MATCHES_INITIAL_DATA_ENTRY_VALUE, valueToCompare, false);
					sv.setErrorMessage(messageSource.getMessage("value_you_specified", null, locale) + " " + valueToCompare.getValue() + " "
							+ messageSource.getMessage("from_initial_data_entry", null, locale));
				}
			}
		} else {
			dib = validateDisplayItemBeanText(sv, dib, inputName, request);
		}
		return dib;
	}

	private DisplayItemBean validateDisplayItemBeanCV(DiscrepancyValidator v, DisplayItemBean dib,
														   String inputName, boolean isSingle) {
		if (StringUtil.isBlank(inputName)) {
			inputName = DataEntryUtil.getInputName(dib);
		}
		ItemFormMetadataBean ibMeta = dib.getMetadata();
		ItemDataBean idb = dib.getData();
		if (StringUtil.isBlank(idb.getValue())) {
			if (ibMeta.isRequired() && ibMeta.isShowItem()) {
				v.addValidation(inputName, Validator.IS_REQUIRED);
			}
		} else {
			if (isSingle) {
				v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata().getResponseSet());
			} else {
				v.addValidation(inputName, Validator.IN_RESPONSE_SET, dib.getMetadata().getResponseSet());
			}
		}
		validateRegexpAndFunctions(v, dib, inputName);
		return dib;
	}

	private DynamicsMetadataService getDynamicsMetadataService() {
		if (dynamicsMetadataService == null) {
			dynamicsMetadataService = new DynamicsMetadataService(dynamicsItemFormMetadataDao, dynamicsItemGroupMetadataDao, dataSource);
		}
		return dynamicsMetadataService;
	}

	private ItemDataBean copyItemDataBean(ItemDataBean src) {
		ItemDataBean result = new ItemDataBean();
		result.setEventCRFId(src.getEventCRFId());
		result.setItemId(src.getItemId());
		result.setValue(src.getValue());
		result.setOrdinal(src.getOrdinal());
		result.setSelected(src.isSelected());
		result.setAuditLog(src.isAuditLog());
		result.setCreatedDate(src.getCreatedDate());
		result.setUpdatedDate(src.getUpdatedDate());
		result.setOwner(src.getOwner());
		result.setUpdater(src.getUpdater());
		result.setStatus(src.getStatus());

		return result;
	}

	private DisplayItemBean validateDisplayItemBeanSingleCV(RuleValidator v, DisplayItemBean dib, String inputName,
															  ArrayList<String> messages) {
		if (StringUtil.isBlank(inputName)) {
			inputName = DataEntryUtil.getInputName(dib);
		}
		ItemFormMetadataBean ibMeta = dib.getMetadata();
		ItemDataBean idb = dib.getData();
		if (StringUtil.isBlank(idb.getValue())) {
			if (ibMeta.isRequired() && ibMeta.isShowItem()) {
				v.addValidation(inputName, Validator.IS_REQUIRED);
			}
			v.addValidation(inputName, Validator.IS_AN_RULE, messages);
		} else {
			v.addValidation(inputName, Validator.IS_AN_RULE, messages);
		}
		return dib;
	}
}
