<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:choose>
    <c:when test="${userBean.sysAdmin && module=='admin'}">
        <c:import url="../include/admin-header.jsp"/>
    </c:when>
    <c:otherwise>
        <c:import url="../include/managestudy-header.jsp"/>
    </c:otherwise>
</c:choose>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- *JSP* submit/viewExecutedRules.jsp -->
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">
            <fmt:message key="rule_execute_rule_bottom_message" bundle="${resword}"/>
        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<script language="JavaScript" type="text/JavaScript">

    function showOrHideSubjects(item, count, showLink, hideLink) {
        if ($(item).text() == showLink) {
            $("#subjects_" + count).css('display', '');
            $(item).text(hideLink);

        } else {
            $("#subjects_" + count).css('display', 'none');
            $(item).text(showLink);
        }
    }

</script>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='ruleSet' class='org.akaza.openclinica.domain.rule.RuleSetBean'/>
<jsp:useBean scope='request' id='ruleSetResult' class='java.util.ArrayList'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="rule_execute_rule_title" bundle="${resword}"/>
	</span>
</h1>
<table>
    <tr>
        <td>
            <div>
                <div class="box_T">
                    <div class="box_L">
                        <div class="box_R">
                            <div class="box_B">
                                <div class="box_TL">
                                    <div class="box_TR">
                                        <div class="box_BL">
                                            <div class="box_BR">
                                                <div class="tablebox_center">
                                                    <table border="0" cellpadding="0" cellspacing="0" width="600px">
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="view_executed_rules_target_oid" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSet.target.value}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="rule_study_event_definition" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSet.studyEventDefinitionNameWithOID}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="CRF_name" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSet.crfWithVersionNameWithOid}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message
                                                                    key="rule_group_label" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${ruleSet.groupLabelWithOid}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="rule_item_name" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <a href="javascript: openDocWindow('ViewItemDetail?itemId=${ruleSet.itemId}')"><c:out value="${ruleSet.itemNameWithOid}"/></a>
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
        </td>
    </tr>
</table>
<br>
<c:if test="${fn:length(ruleSetResult) > 0}">
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
                                                <td class="table_header_row_left"><fmt:message key="rule_name" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="view_executed_rules_oid" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="rule_expression" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="rule_execute_on" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="rule_action_type" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="rule_action_summary" bundle="${resword}"/></td>
                                                <td class="table_header_row"><fmt:message key="action" bundle="${resword}"/></td>
                                            </tr>
                                            <c:forEach var="result" items="${ruleSetResult}" varStatus="status">
                                                <tr valign="top">
                                                    <td class="table_cell_left"><c:out value="${result.ruleName}"/></td>
                                                    <td class="table_cell"><c:out value="${result.ruleOid}"/></td>
                                                    <td class="table_cell rule-table-expression-td"><c:out value="${result.expression}"/></td>
                                                    <td class="table_cell"><c:out value="${result.executeOn}"/></td>
                                                    <td class="table_cell"><c:out value="${result.actionType}"/></td>
                                                    <td class="table_cell rule-table-action-summary-td"><c:out value="${result.actionSummary}"/></td>
                                                    <td class="table_cell" width="15%">
                                                        <a id="a${status.count}" style="cursor: pointer; cursor: hand;" onClick="showOrHideSubjects(this, ${status.count},'<fmt:message key="rule_show_subjects" bundle="${resword}"/>','<fmt:message key="rule_hide_subjects" bundle="${resword}"/>')"><fmt:message key="rule_show_subjects" bundle="${resword}"/></a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
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
    <c:forEach var="result" items="${ruleSetResult}" varStatus="status">
        <div id="subjects_${status.count}" style="display:none">
            <div style="width:600px">
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
                                                            <td class="table_header_row"><fmt:message key="nav_study_subject_id" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="date_created" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="rule_status" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="site_id" bundle="${resword}"/></td>
                                                        </tr>
                                                        <c:forEach var="subject" items="${result.subjects}">
                                                            <tr valign="top">
                                                                <c:forTokens items="${subject}" delims="," var="subjectObj">
                                                                    <td class="table_cell">
                                                                        <c:out value="${subjectObj}"/>
                                                                    </td>
                                                                </c:forTokens>
                                                            </tr>
                                                        </c:forEach>
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
        </div>
    </c:forEach>
</c:if>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium"
                   onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
        </td>
        <c:if test="${fn:length(ruleSetResult) > 0}">
            <td>
                <input type="button" name="Submit" id="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium"
                       onClick="window.location.href='RunRuleSet?ruleSetId=${ruleSet.id}&dryRun=no';"/></td>
            </td>
        </c:if>
    </tr>
</table>

<c:choose>
    <c:when test="${userBean.sysAdmin && module=='admin'}">
        <c:import url="../include/workflow.jsp">
            <c:param name="module" value="admin"/>
        </c:import>
    </c:when>
    <c:otherwise>
        <c:import url="../include/workflow.jsp">
            <c:param name="module" value="manage"/>
        </c:import>
    </c:otherwise>
</c:choose>
<jsp:include page="../include/footer.jsp"/>