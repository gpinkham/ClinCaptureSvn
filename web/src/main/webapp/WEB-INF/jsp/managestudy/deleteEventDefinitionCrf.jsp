<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmr" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="resnotes"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/home-page-with-charts-header.jsp"/>

<jsp:include page="../include/sideAlert.jsp"/>
<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
				src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="choose_crf_instruction_key" bundle="${resword}"/>
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
				src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<h1>
	<span class="first_level_header">
		<fmt:message bundle="${resword}" key="delete_crf_from_definition"/>
	</span>
</h1>

<br>
<table class="table_horizontal table_shadow_bottom" width="500px">
	<tr>
		<td><fmt:message bundle="${resword}" key="CRF_name"/></td>
		<td><fmt:message bundle="${resword}" key="required"/></td>
	</tr>
	<tr>
		<td>${edc.crf.name}</td>
		<td>${edc.requiredCRF}</td>
	</tr>
</table>
<br/>
<c:if test="${!canBeDeleted}">
	<p class="alert"><fmt:message bundle="${resword}" key="data_is_present_for_this_crf"/></p>

	<c:if test="${fn:length(eventCRFs) != 0}">
		<p><fmt:message bundle="${resword}" key="data_entry_has_been_started_for_this_event_definition_crf"/></p>
		<div class="tablebox_center">
			<table class="table_horizontal table_shadow_bottom" width="500px">
				<tr>
					<td><fmt:message bundle="${resword}" key="study_subject"/></td>
					<td><fmt:message bundle="${resword}" key="study_name"/></td>
				</tr>
				<c:forEach items="${eventCRFs}" var="eventCrf">
					<tr>
						<td>${eventCrf.studySubjectName}</td>
						<td>${eventCrf.studyName}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</c:if>

	<c:if test="${fn:length(ruleSetRules) != 0}">
		<p><fmt:message bundle="${resword}" key="this_event_definition_crf_mentioned_in_rules"/></p>
		<div class="tablebox_center">
			<table class="table_horizontal table_shadow_bottom">
				<tr>
					<td style="width: 100px"><fmt:message bundle="${resword}" key="rule_oid"/></td>
					<td style="width: 200px"><fmt:message bundle="${resword}" key="rule_name"/></td>
					<td style="width: 400px"><fmt:message bundle="${resword}" key="rule_expression"/></td>
					<td style="width: 150px"><fmt:message bundle="${resword}" key="event"/></td>
					<td style="width: 300px"><fmt:message bundle="${resword}" key="test_rule_target"/></td>
				</tr>
				<c:forEach items="${ruleSetRules}" var="ruleSetRule">
					<tr>
						<td>${ruleSetRule.ruleBean.oid}</td>
						<td>${ruleSetRule.ruleBean.name}</td>
						<td>${ruleSetRule.ruleBean.expression.value}</td>
						<td>${ruleSetRule.ruleSetBean.originalTarget.targetEventOid}</td>
						<td>${ruleSetRule.ruleSetBean.originalTarget.value}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</c:if>
</c:if>

<form action="deleteEventDefinitionCRF" id="main_form">
	<input type="hidden" value="true" name="submit" id="action_input"/>
	<input type="hidden" value="${edc.id}" name="id"/>
	<input type="hidden" value="${edId}" name="edId"/>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					   value="<fmt:message key="back" bundle="${resword}"/>"
					   class="button_medium medium_back"
					   onclick="$('#action_input').attr('name', 'back'); $('#main_form').submit();"/>
			</td>
			<c:if test="${canBeDeleted}">
				<td>
					<input type="submit" name="submit_button" id="submit_button" class="button_long long_submit"
						   value="<fmt:message key="submit" bundle="${resword}"/>"
						   onclick="return confirmSubmit({message:'<fmt:message bundle="${resword}" key="confirm_delete_crf_from_definition"/>',
						   width: 500, height: 150, submit: this})">
				</td>
			</c:if>
		</tr>
	</table>
</form>

<jsp:include page="../include/footer.jsp"/>