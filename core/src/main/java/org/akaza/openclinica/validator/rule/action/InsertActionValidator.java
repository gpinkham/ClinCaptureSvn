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

package org.akaza.openclinica.validator.rule.action;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.logic.expressionTree.ExpressionTreeHelper;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class InsertActionValidator implements Validator {

	DataSource dataSource;
	ExpressionService expressionService;

	public static class InsertActionHolder {
		public RuleSetBean ruleSetBean;
		public EventDefinitionCRFBean eventDefinitionCRFBean;
		public Object obj;

		public InsertActionHolder(RuleSetBean ruleSetBean, EventDefinitionCRFBean eventDefinitionCRFBean, Object obj) {
			this.obj = obj;
			this.ruleSetBean = ruleSetBean;
			this.eventDefinitionCRFBean = eventDefinitionCRFBean;
		}
	}

	public InsertActionValidator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * This Validator validates just Person instances
	 */
	public boolean supports(Class clazz) {
		return InsertActionBean.class.equals(clazz);
	}

	public void validateOidInPropertyBean(RuleSetBean ruleSetBean, PropertyBean propertyBean, Errors e, String p) {
		if (getExpressionService().isExpressionPartial(ruleSetBean.getTarget().getValue())) {
			if (getExpressionService().getExpressionSize(propertyBean.getOid()) > 4) {
				e.rejectValue(p + "oid", "oid.invalid", "OID: " + propertyBean.getOid() + " is Invalid.");
			}
			try {
				getExpressionService().isExpressionValid(propertyBean.getOid());
			} catch (OpenClinicaSystemException ose) {
				e.rejectValue(p + "oid", "oid.invalid", "OID: " + propertyBean.getOid() + " is Invalid.");
			}
			// Use OID in destinationProperty to get CRF
			CRFBean destinationPropertyOidCrf = getExpressionService().getCRFFromExpression(propertyBean.getOid());
			if (destinationPropertyOidCrf == null) {
				ItemBean item = getExpressionService().getItemBeanFromExpression(propertyBean.getOid());
				destinationPropertyOidCrf = getCrfDAO().findByItemOid(item.getOid());
			}
			// Use Target get CRF
			CRFBean targetCrf = getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue());
			if (targetCrf == null) {
				ItemBean item = getExpressionService().getItemBeanFromExpression(ruleSetBean.getTarget().getValue());
				targetCrf = getCrfDAO().findByItemOid(item.getOid());

			}
			// Get All event definitions the selected CRF belongs to
			List<StudyEventDefinitionBean> destinationPropertyStudyEventDefinitions = getStudyEventDefinitionDAO()
					.findAllByCrf(destinationPropertyOidCrf);
			List<StudyEventDefinitionBean> targetStudyEventDefinitions = getStudyEventDefinitionDAO().findAllByCrf(
					targetCrf);
			Collection intersection = CollectionUtils.intersection(destinationPropertyStudyEventDefinitions,
					targetStudyEventDefinitions);
			if (intersection.size() == 0) {
				e.rejectValue(p + "oid", "oid.invalid", "OID: " + propertyBean.getOid() + " is Invalid.");
			}
		} else {
			String expression = getExpressionService().constructFullExpressionFromPartial(propertyBean.getOid(),
					ruleSetBean.getTarget().getValue());
			ItemBean item = getExpressionService().getItemBeanFromExpression(expression);

			if (!(getExpressionService().isInsertActionExpressionValid(propertyBean.getOid(), ruleSetBean, 3) || getExpressionService()
					.isInsertActionExpressionValid(propertyBean.getOid(), ruleSetBean, 4)) || item == null) {
				e.rejectValue(p + "oid", "oid.invalid", "OID: " + propertyBean.getOid() + " is Invalid.");
			}
		}
	}

	public void validateValueExpressionInPropertyBean(RuleSetBean ruleSetBean, PropertyBean propertyBean, Errors e,
			String p) {
		if (getExpressionService().isExpressionPartial(ruleSetBean.getTarget().getValue())) {
			if (getExpressionService().getExpressionSize(propertyBean.getValueExpression().getValue()) > 2) {
				e.rejectValue(p + "valueExpression", "valueExpression.invalid",
						"Value provided for ValueExpression is Invalid");
			}
			try {
				getExpressionService().isExpressionValid(propertyBean.getValueExpression().getValue());
			} catch (OpenClinicaSystemException ose) {
				e.rejectValue(p + "valueExpression", "valueExpression.invalid",
						"Value provided for ValueExpression is Invalid");
			}
			// Use ValueExression in destinationProperty to get CRF
			ItemBean item = getExpressionService().getItemBeanFromExpression(
					propertyBean.getValueExpression().getValue());
			CRFBean destinationPropertyValueExpressionCrf = getCrfDAO().findByItemOid(item.getOid());
			// Use Target to get CRF
			CRFBean targetCrf = getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue());
			if (targetCrf == null) {
				ItemBean targetItem = getExpressionService().getItemBeanFromExpression(
						ruleSetBean.getTarget().getValue());
				targetCrf = getCrfDAO().findByItemOid(targetItem.getOid());

			}
			if (destinationPropertyValueExpressionCrf.getId() != targetCrf.getId()) {
				e.rejectValue(p + "valueExpression", "valueExpression.invalid",
						"Value provided for ValueExpression is Invalid");
			}
		} else {
			String valueExpression = getExpressionService().constructFullExpressionFromPartial(
					propertyBean.getValueExpression().getValue(), ruleSetBean.getTarget().getValue());
			ItemBean item = getExpressionService().getItemBeanFromExpression(valueExpression);
			if (!getExpressionService().isExpressionValid(propertyBean.getValueExpression().getValue(), ruleSetBean, 2)
					|| item == null) {
				e.rejectValue(p + "valueExpression", "valueExpression.invalid",
						"Value provided for ValueExpression is Invalid");
			}
		}
	}

	public void validate(Object obj, Errors e) {
		RuleSetBean ruleSetBean = ((InsertActionHolder) obj).ruleSetBean;
		InsertActionBean insertActionBean = (InsertActionBean) ((InsertActionHolder) obj).obj;
		EventDefinitionCRFBean eventDefinitionCRFBean = ((InsertActionHolder) obj).eventDefinitionCRFBean;
		for (int i = 0; i < insertActionBean.getProperties().size(); i++) {
			String p = "properties[" + i + "].";
			PropertyBean propertyBean = insertActionBean.getProperties().get(i);
			ValidationUtils.rejectIfEmpty(e, p + "oid", "oid.empty");

			validateOidInPropertyBean(ruleSetBean, propertyBean, e, p);

			if (propertyBean.getValueExpression() != null && propertyBean.getValueExpression().getValue() != null
					&& propertyBean.getValueExpression().getValue().length() != 0) {

				String expressionContextName = propertyBean.getValueExpression().getContextName();
				Context context = expressionContextName != null ? Context.getByName(expressionContextName)
						: Context.OC_RULES_V1;
				propertyBean.getValueExpression().setContext(context);

				validateValueExpressionInPropertyBean(ruleSetBean, propertyBean, e, p);
			} else {
				if (propertyBean.getValue() == null || propertyBean.getValue().length() > 0) {
					ValidationUtils.rejectIfEmpty(e, p + "value", "value.empty");
				} else {
					checkValidity(eventDefinitionCRFBean,
							getExpressionService().getItemBeanFromExpression(propertyBean.getOid()),
							propertyBean.getValue(), p, e);
				}
			}
		}
	}

	private void checkValidity(EventDefinitionCRFBean eventDefinitionCRFBean, ItemBean itemBean, String value,
			String index, Errors e) {
		Boolean result = false;
		List<ItemFormMetadataBean> itemFormMetadataBeans = getItemFormMetadataDAO().findAllByItemId(itemBean.getId());
		for (ItemFormMetadataBean itemFormMetadataBean : itemFormMetadataBeans) {

			if (itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.RADIO)
					|| itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.SELECT)) {
				if (matchValueWithOptions(value, itemFormMetadataBean.getResponseSet().getOptions()) != null) {
					result = true;
					break;
				}
			}

			// TODO: check Null Value logic based on not event definition crf being selected
			if (itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.CHECKBOX)
					|| itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.SELECTMULTI)) {
				if (eventDefinitionCRFBean == null) {
					result = true;
					break;
				}
				if (matchValueWithManyOptions(eventDefinitionCRFBean, value, itemFormMetadataBean.getResponseSet()
						.getOptions()) != null) {
					result = true;
					break;
				}
			}

			// TODO: check Null Value logic based on not event definition crf being selected
			if (itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.TEXT)
					|| itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.TEXTAREA)) {
				if (eventDefinitionCRFBean == null) {
					result = true;
					break;
				} else if (checkValidityBasedonNullValues(eventDefinitionCRFBean, value)) {
					result = true;
					break;
				} else {
					int errorCount = e.getErrorCount();
					checkValidityBasedOnDataType(itemBean, value, index, e);
					if (e.getErrorCount() == errorCount) {
						result = true;
						break;
					}
				}
			}

			if (itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.CALCULATION)
					|| itemFormMetadataBean.getResponseSet().getResponseType().equals(ResponseType.GROUP_CALCULATION)) {
				result = false;
				break;

			}

		}
		if (!result) {
			e.rejectValue(index + "value", "value.invalid");
		}
	}

	private String matchValueWithOptions(String value, List<ResponseOptionBean> options) {
		String returnedValue = null;
		if (!options.isEmpty()) {
			for (ResponseOptionBean responseOptionBean : options) {
				if (responseOptionBean.getValue().equals(value)) {
					returnedValue = responseOptionBean.getValue();
					break;
				}
			}
		}
		return returnedValue;
	}

	private String matchValueWithManyOptions(EventDefinitionCRFBean eventDefinitionCRFBean, String value,
			List<ResponseOptionBean> options) {
		String entireOptions = "";
		String[] simValues = value.split(",");
		boolean checkComplete;

		if (!options.isEmpty()) {
			for (ResponseOptionBean responseOptionBean : options) {
				entireOptions += responseOptionBean.getValue();
			}
			// remove spaces, since they are causing problems:
			entireOptions = entireOptions.replace(" ", "");

			ArrayList nullValues = eventDefinitionCRFBean.getNullValuesList();

			for (Object nullValue : nullValues) {
				NullValue nullValueTerm = (NullValue) nullValue;
				entireOptions += nullValueTerm.getName();
			}

			for (String sim : simValues) {
				sim = sim.replace(" ", "");
				checkComplete = entireOptions.contains(sim);// Pattern.matches(entireOptions,sim);
				if (!checkComplete) {
					return null;
				}
			}
		}
		return value;
	}

	private Boolean checkValidityBasedonNullValues(EventDefinitionCRFBean eventDefinitionCRFBean, String value) {
		return eventDefinitionCRFBean.getNullValuesList().contains(NullValue.getByName(value));
	}

	private void checkValidityBasedOnDataType(ItemBean itemBean, String value, String index, Errors e) {
		switch (itemBean.getItemDataTypeId()) {
		case 6: { // ItemDataType.INTEGER
			try {
				Integer.valueOf(value);
			} catch (NumberFormatException nfe) {
				e.rejectValue(index + "value", "value.invalid.integer");
			}
			break;
		}
		case 7: { // ItemDataType.REAL
			try {
				Float.valueOf(value);
			} catch (NumberFormatException nfe) {
				e.rejectValue(index + "value", "value.invalid.float");
			}
			break;
		}
		case 9: { // ItemDataType.DATE
			if (!ExpressionTreeHelper.isDateyyyyMMdd(value)) {
				e.rejectValue(index + "value", "value.invalid.date");
			}
			break;
		}
		case 10: { // ItemDataType.PDATE
			try {
				Float.valueOf(value);
			} catch (NumberFormatException nfe) {
				e.rejectValue(index + "value", "value.invalid.float");
			}
			break;
		}
		case 11: { // ItemDataType.FILE
			e.rejectValue(index + "value", "value.notSupported.file");
			break;
		}

		default:
			break;
		}

	}

	public ItemDAO getItemDAO() {
		return new ItemDAO(dataSource);
	}

	public StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}

	public CRFDAO getCrfDAO() {
		return new CRFDAO(dataSource);
	}

	public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	public ItemFormMetadataDAO getItemFormMetadataDAO() {
		return new ItemFormMetadataDAO(dataSource);
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
}
