<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/managestudy_top_pages.jsp"/>

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jspfunctions.js?r=${revisionNumber}'/>"></script>

<jsp:include page="../include/sideAlert.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
        </a>
        <b>
            <fmt:message key="instructions" bundle="${restext}" />
        </b>
        <div class="sidebar_tab_content">
            <fmt:message key="you_are_completely_deleting_a_crf_version" bundle="${restext}"/>
        </div>
    </td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10">
        </a>
        <b>
            <fmt:message key="instructions" bundle="${restext}" />
        </b>
    </td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<h1>
	<span class="first_level_header">
        <fmt:message key="version" bundle="${resword}" var="crfVersionTitle"/>
		<fmt:message key="confirm_deletion_of_crf_version" bundle="${resword}"/>: ${crfBean.name} ${fn:toLowerCase(crfVersionTitle)} ${crfVersionBean.name}
	</span>
</h1>

<p>
    <fmt:message key="this_operation_will_permanently_delete_this_crf_version" bundle="${restext}"/>
</p>

<div style="width:40%">
    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="tablebox_center">
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                            <tr valign="top">
                                                <td class="table_header_column_top">
                                                    <fmt:message key="name" bundle="${resword}"/>:
                                                </td>
                                                <td class="table_cell_top">
                                                    ${crfBean.name} ${fn:toLowerCase(crfVersionTitle)} ${crfVersionBean.name}
                                                </td>
                                            </tr>
                                            <tr valign="top">
                                                <td class="table_header_column">
                                                    <fmt:message key="description" bundle="${resword}"/>:
                                                </td>
                                                <td class="table_cell">
                                                    <c:out value="${crfVersionBean.description}"/>
                                                </td>
                                            </tr>
                                            <tr valign="top">
                                                <td class="table_header_column">
                                                    <fmt:message key="rule_oid" bundle="${resword}"/>:
                                                </td>
                                                <td class="table_cell">
                                                    <c:out value="${crfVersionBean.oid}"/>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<br/>

<span class="table_title_Admin">
    <fmt:message key="discrepancy_notes_affected" bundle="${resword}"/>
</span>

    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="tablebox_center" align="center">
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%" style="table-layout:fixed;">
                                            <tr valign="top">
                                                <td class="table_header_row_left">
                                                    <fmt:message key="study_subject_ID" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="type" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="resolution_status" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="description" bundle="${resword}"/>
                                                </td>
                                            </tr>
                                            <c:if test="${fn:length(crfDiscrepancyNotes) gt 0}">
                                                <c:forEach var="discrepancyNote" items="${crfDiscrepancyNotes}">
                                                    <tr>
                                                        <td class="table_cell">
                                                            <c:out value="${discrepancyNote.studySub.label}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${discrepancyNote.disType.name}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${discrepancyNote.resStatus.name}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${discrepancyNote.description}"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:if>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

<br/>

<span class="table_title_Admin">
    <fmt:message key="rules_affected" bundle="${resword}"/>
</span>

<div>
    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="tablebox_center" align="center">
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                            <tr valign="top">
                                                <td class="table_header_row_left">
                                                    <fmt:message key="view_rule_assignment_rule_oid" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="name" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="description" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="rule_expression" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="view_rule_assignment_target" bundle="${resword}"/>
                                                </td>
                                            </tr>
                                            <c:if test="${fn:length(ruleSetBeanList) gt 0}">
                                                <c:forEach var="ruleSetBean" items="${ruleSetBeanList}">
                                                    <c:forEach var="ruleSetRule" items="${ruleSetBean.ruleSetRules}">
                                                        <tr>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSetRule.ruleBean.oid}"/>
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSetRule.ruleBean.name}"/>
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSetRule.ruleBean.description}"/>
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSetRule.ruleBean.expression.value}"/>
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSetBean.originalTarget.value}"/>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:forEach>
                                            </c:if>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<br/>

<span class="table_title_Admin">
    <fmt:message key="crf_data_affected" bundle="${resword}"/>
</span>

<div style="width:50%">
    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="tablebox_center" align="center">
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                            <tr valign="top">
                                                <td class="table_header_row_left">
                                                    <fmt:message key="study_subject_ID" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="event_name" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="created_date" bundle="${resword}"/>
                                                </td>
                                                <td class="table_header_row">
                                                    <fmt:message key="last_updated_date" bundle="${resword}"/>
                                                </td>
                                            </tr>
                                            <c:if test="${fn:length(eventCRFBeanList) gt 0}">
                                                <c:forEach var="eventCRFBean" items="${eventCRFBeanList}">
                                                    <tr>
                                                        <td class="table_cell">
                                                            <c:out value="${eventCRFBean.studySubjectName}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${eventCRFBean.eventName}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${eventCRFBean.createdDate}"/>
                                                        </td>
                                                        <td class="table_cell">
                                                            <c:out value="${eventCRFBean.updatedDate}"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:if>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<br/>

<span class="table_title_Admin">
    <fmt:message key="associated_event_definitions" bundle="${resword}"/>
</span>

<div style="width:50%">
    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="tablebox_center">
                                        <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                            <tr valign="top">
                                                <td class="table_header_row_left"><fmt:message key="study_event" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="CRF_status" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="study_name" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="date_created" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="owner" bundle="${resword}"/></td>
                                            </tr>
                                            <c:if test="${fn:length(eventDefinitionListFull) gt 0}">
                                                <c:forEach var="eventDefinition" items="${eventDefinitionListFull}">
                                                    <tr valign="top">
                                                        <td class="table_cell_left"><c:out value="${eventDefinition.name}"/></td>
                                                        <c:choose>
                                                            <c:when test="${eventDefinition.status.name eq 'available'}">
                                                                <td class="table_cell" style="color: #009966"><c:out value="${eventDefinition.status.name}"/></td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <td class="table_cell" style="color: #ff0000"><c:out value="${eventDefinition.status.name}"/></td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <td class="table_cell"><c:out value="${eventDefinition.studyName}"/></td>
                                                        <td class="table_cell"><c:out value="${eventDefinition.createdDate}"/></td>
                                                        <td class="table_cell"><c:out value="${eventDefinition.owner.name}"/></td>
                                                    </tr>
                                                </c:forEach>
                                            </c:if>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<form:form method="post">
    <table>
        <tr>
            <td>
                <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
                       value="<fmt:message key="back" bundle="${resword}"/>"
                       class="button_medium medium_back"
                       onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
            </td>
            <c:if test="${fn:length(reassignedCrfVersionOid) eq 0 and fn:length(eventCRFBeanList) eq 0
                        and fn:length(crfDiscrepancyNotes) eq 0 and fn:length(eventDefinitionListAvailable) eq 0
                        and fn:length(ruleSetBeanList) eq 0}">
                <td>
                    <input type="submit" name="confirm" value="Submit" class="button_medium" onclick="doSubmit();">
                </td>
            </c:if>
        </tr>
    </table>
</form:form>

<jsp:include page="../include/footer.jsp"/>
