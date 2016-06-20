<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<script>
	/* ============================================
	 * When page is opened this function will gather all messages
	 * that should be displayed, and add them to messagesSource.
	 * ============================================= */
	function getMessagesValues() {
		messageSource.texts.compareOrCalculate = '<fmt:message bundle="${resword}" key="compare_or_calculate"/>';
		messageSource.texts.compareCalculate = '<fmt:message bundle="${resword}" key="compare_calculate"/>';
		messageSource.texts.groupOrData = '<fmt:message bundle="${resword}" key="group_or_data"/>';
		messageSource.texts.groupData = '<fmt:message bundle="${resword}" key="group_data"/>';
		messageSource.texts.condition = '<fmt:message bundle="${resword}" key="condition"/>';
		messageSource.texts.description = '<fmt:message bundle="${resword}" key="description"/>';
		messageSource.texts.nameText = '<fmt:message bundle="${resword}" key="name"/>';
		messageSource.texts.dataType = '<fmt:message bundle="${resword}" key="data_type"/>';
		messageSource.texts.identifier = '<fmt:message bundle="${resword}" key="identifier"/>';
		messageSource.texts.numberText = '<fmt:message bundle="${resword}" key="number"/>';
		messageSource.texts.dataText = '<fmt:message bundle="${resword}" key="data"/>';
		messageSource.texts.selectDate = '<fmt:message bundle="${resword}" key="selectDate"/>';
		messageSource.texts.changingStudy = '<fmt:message bundle="${resword}" key="changingStudy"/>';
		messageSource.texts.cancel = '<fmt:message bundle="${resword}" key="cancel"/>';
		messageSource.texts.clear = '<fmt:message bundle="${resword}" key="clear"/>';

		messageSource.terms.and = '<fmt:message bundle="${resword}" key="AND"/>';
		messageSource.terms.or = '<fmt:message bundle="${resword}" key="OR"/>';
		messageSource.terms.not = '<fmt:message bundle="${resword}" key="NOT"/>';

		messageSource.tooltips.deleteButton = '<fmt:message bundle="${resword}" key="rs.tooltip.deleteButton"/>';
		messageSource.tooltips.targetTip = '<fmt:message bundle="${resword}" key="rs.tooltip.target"/>';
		messageSource.tooltips.ruleName = '<fmt:message bundle="${resword}" key="rs.tooltip.ruleName"/>';
		messageSource.tooltips.leftTip = '<fmt:message bundle="${resword}" key="rs.tooltip.left"/>';
		messageSource.tooltips.rightTip = '<fmt:message bundle="${resword}" key="rs.tooltip.right"/>';
		messageSource.tooltips.and = '<fmt:message bundle="${resword}" key="rs.tooltip.and"/>';
		messageSource.tooltips.or = '<fmt:message bundle="${resword}" key="rs.tooltip.or"/>';
		messageSource.tooltips.eqTip = '<fmt:message bundle="${resword}" key="rs.tooltip.eq"/>';
		messageSource.tooltips.neq = '<fmt:message bundle="${resword}" key="rs.tooltip.neq"/>';
		messageSource.tooltips.ltTip = '<fmt:message bundle="${resword}" key="rs.tooltip.lt"/>';
		messageSource.tooltips.gtTip = '<fmt:message bundle="${resword}" key="rs.tooltip.gt"/>';
		messageSource.tooltips.lte = '<fmt:message bundle="${resword}" key="rs.tooltip.lte"/>';
		messageSource.tooltips.gte = '<fmt:message bundle="${resword}" key="rs.tooltip.gte"/>';
		messageSource.tooltips.ct = '<fmt:message bundle="${resword}" key="rs.tooltip.ct"/>';
		messageSource.tooltips.nct = '<fmt:message bundle="${resword}" key="rs.tooltip.nct"/>';
		messageSource.tooltips.plus = '<fmt:message bundle="${resword}" key="rs.tooltip.plus"/>';
		messageSource.tooltips.minus = '<fmt:message bundle="${resword}" key="rs.tooltip.minus"/>';
		messageSource.tooltips.divide = '<fmt:message bundle="${resword}" key="rs.tooltip.divide"/>';
		messageSource.tooltips.mult = '<fmt:message bundle="${resword}" key="rs.tooltip.mult"/>';
		messageSource.tooltips.numberTip = '<fmt:message bundle="${resword}" key="rs.tooltip.number"/>';
		messageSource.tooltips.emptyTip = '<fmt:message bundle="${resword}" key="rs.tooltip.empty"/>';
		messageSource.tooltips.dateTip = '<fmt:message bundle="${resword}" key="rs.tooltip.date"/>';
		messageSource.tooltips.textTip = '<fmt:message bundle="${resword}" key="rs.tooltip.text"/>';
		messageSource.tooltips.studiesLink = '<fmt:message bundle="${resword}" key="rs.tooltip.studiesLink"/>';
		messageSource.tooltips.currentDateTip = '<fmt:message bundle="${resword}" key="rs.tooltip.currentDate"/>';
		messageSource.tooltips.eventsLink = '<fmt:message bundle="${resword}" key="rs.tooltip.eventsLink"/>';
		messageSource.tooltips.crfsLink = '<fmt:message bundle="${resword}" key="rs.tooltip.crfsLink"/>';
		messageSource.tooltips.versionsLink = '<fmt:message bundle="${resword}" key="rs.tooltip.versionsLink"/>';
		messageSource.tooltips.itemsLink = '<fmt:message bundle="${resword}" key="rs.tooltip.itemsLink"/>';
		messageSource.tooltips.dottedBorder = '<fmt:message bundle="${resword}" key="rs.tooltip.dottedBorder"/>';
		messageSource.tooltips.item = '<fmt:message bundle="${resword}" key="rs.tooltip.item"/>';
		messageSource.tooltips.eventify = '<fmt:message bundle="${resword}" key="rs.tooltip.eventify"/>';
		messageSource.tooltips.versionifyTip = '<fmt:message bundle="${resword}" key="rs.tooltip.versionify"/>';
		messageSource.tooltips.linefy = '<fmt:message bundle="${resword}" key="rs.tooltip.linefy"/>';
		messageSource.tooltips.opt = '<fmt:message bundle="${resword}" key="rs.tooltip.opt"/>';
		messageSource.tooltips.removePopUp = '<fmt:message bundle="${resword}" key="rs.tooltip.removePopUp"/>';
		messageSource.tooltips.addPopUp = '<fmt:message bundle="${resword}" key="rs.tooltip.addPopUp"/>';

		messageSource.messages.clearExpression = '<fmt:message bundle="${resword}" key="rs.message.clearExpression"/>';
		messageSource.messages.showHideEvaluation = '<fmt:message bundle="${resword}" key="rs.message.showHideEvaluation"/>';
		messageSource.messages.serverIsNotAvailable = '<fmt:message bundle="${resword}" key="rs.message.serverIsNotAvailable"/>';
		messageSource.messages.enterNumber = '<fmt:message bundle="${resword}" key="rs.message.enterNumber"/>';
		messageSource.messages.expressionWillBeLost = '<fmt:message bundle="${resword}" key="rs.message.expressionWillBeLost"/>';
		messageSource.messages.selectAnAction = '<fmt:message bundle="${resword}" key="rs.message.selectAnAction"/>';
		messageSource.messages.selectWhenRun = '<fmt:message bundle="${resword}" key="rs.message.selectWhenRun"/>';
		messageSource.messages.selectEvaluate = '<fmt:message bundle="${resword}" key="rs.message.selectEvaluate"/>';
		messageSource.messages.specifyTarget = '<fmt:message bundle="${resword}" key="rs.message.specifyTarget"/>';
		messageSource.messages.specifyDescription = '<fmt:message bundle="${resword}" key="rs.message.specifyDescription"/>';
		messageSource.messages.specifyDiscrepancyText = '<fmt:message bundle="${resword}" key="rs.message.specifyDiscrepancyText"/>';
		messageSource.messages.invalidDiscrepancyText = '<fmt:message bundle="${resword}" key="rs.message.invalidDiscrepancyText"/>';
		messageSource.messages.invalidEmailText = '<fmt:message bundle="${resword}" key="rs.message.invalidEmailText"/>';
		messageSource.messages.specifyEmailMessage = '<fmt:message bundle="${resword}" key="rs.message.specifyEmailMessage"/>';
		messageSource.messages.invalidEmail = '<fmt:message bundle="${resword}" key="rs.message.invalidEmail"/>';
		messageSource.messages.selectItemsToInsert = '<fmt:message bundle="${resword}" key="rs.message.selectItemsToInsert"/>';
		messageSource.messages.selectItemsToShowHide = '<fmt:message bundle="${resword}" key="rs.message.selectItemsToShowHide"/>';
		messageSource.messages.invalidExpression = '<fmt:message bundle="${resword}" key="rs.message.invalidExpression"/>';
		messageSource.messages.invalidItemsSelected = '<fmt:message bundle="${resword}" key="rs.message.invalidItemsSelected"/>';
		messageSource.messages.checkLog = '<fmt:message bundle="${resword}" key="rs.message.checkLog"/>';
		messageSource.messages.selectEvent = '<fmt:message bundle="${resword}" key="rs.message.selectEvent"/>';
		messageSource.messages.selectCRF = '<fmt:message bundle="${resword}" key="rs.message.selectCRF"/>';
		messageSource.messages.selectVersion = '<fmt:message bundle="${resword}" key="rs.message.selectVersion"/>';
		messageSource.messages.descriptionLength = '<fmt:message bundle="${resword}" key="rs.message.descriptionLength"/>';
		messageSource.messages.insertValueLength = '<fmt:message bundle="${resword}" key="rs.message.insertValueLength"/>';

		messageSource.validations.initialDe = '<fmt:message bundle="${resword}" key="initial_data_entry"/>';
		messageSource.validations.administrativeDe = '<fmt:message bundle="${resword}" key="administrative_editing"/>';
		messageSource.validations.doubleDe = '<fmt:message bundle="${resword}" key="double_data_entry"/>';
		messageSource.validations.importDe = '<fmt:message bundle="${resword}" key="data_import"/>';
		messageSource.validations.discrepancyAction = '<fmt:message bundle="${resword}" key="rs.validation.discrepancyAction"/>';
		messageSource.validations.emailAction = '<fmt:message bundle="${resword}" key="rs.validation.emailAction"/>';
		messageSource.validations.insertAction = '<fmt:message bundle="${resword}" key="rs.validation.insertAction"/>';
		messageSource.validations.showHideAction = '<fmt:message bundle="${resword}" key="rs.validation.showHideAction"/>';
		messageSource.validations.ruleFailure = '<fmt:message bundle="${resword}" key="rs.validation.failure"/>';
		messageSource.validations.ruleValid = '<fmt:message bundle="${resword}" key="rs.validation.valid"/>';
		messageSource.validations.ruleInvalid = '<fmt:message bundle="${resword}" key="rs.validation.invalid"/>';
		messageSource.validations.missingEventsData = '<fmt:message bundle="${resword}" key="rs.validation.missingEventsData"/>';
		messageSource.validations.missingCRFsData = '<fmt:message bundle="${resword}" key="rs.validation.missingCRFsData"/>';
		messageSource.validations.missingVersionsData = '<fmt:message bundle="${resword}" key="rs.validation.missingVersionsData"/>';
		messageSource.validations.missingItemsData = '<fmt:message bundle="${resword}" key="rs.validation.missingItemsData"/>';
		messageSource.validations.missingSessionStudies = '<fmt:message bundle="${resword}" key="rs.validation.missingSessionStudies"/>';
		messageSource.validations.missingDestination = '<fmt:message bundle="${resword}" key="rs.validation.missingDestination"/>';

		messageSource.dataType.st = '<fmt:message bundle="${resword}" key="data_type_st"/>';
		messageSource.dataType.int = '<fmt:message bundle="${resword}" key="data_type_int"/>';
		messageSource.dataType.real = '<fmt:message bundle="${resword}" key="data_type_real"/>';
		messageSource.dataType.date = '<fmt:message bundle="${resword}" key="data_type_date"/>';
		messageSource.dataType.file = '<fmt:message bundle="${resword}" key="data_type_file"/>';
		messageSource.dataType.pdate = '<fmt:message bundle="${resword}" key="data_type_pdate"/>';
		messageSource.dataType.code = '<fmt:message bundle="${resword}" key="data_type_code"/>';

		localStorage.setItem("messageSource", JSON.stringify(messageSource));
	}
</script>