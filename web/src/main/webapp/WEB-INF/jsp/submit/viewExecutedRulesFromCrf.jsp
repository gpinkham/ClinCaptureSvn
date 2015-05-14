<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<link rel="stylesheet" href="includes/jmesa/jmesa.css?r=${revisionNumber}" type="text/css">
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:choose>
    <c:when test="${userBean.sysAdmin && module=='admin'}">
        <c:import url="../include/admin-header.jsp"/>
    </c:when>
    <c:otherwise>
        <c:import url="../include/managestudy-header.jsp"/>
    </c:otherwise>
</c:choose>

<!-- *JSP* submit/viewExecutedRulesFromCrf.jsp -->
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">
        </div>
    </td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
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
<jsp:useBean scope='request' id='result' class='java.util.HashMap'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="rule_execute_crf_rule_title" bundle="${resword}"/>
	</span>
</h1>

<c:set var="count" value="0"/>

<c:choose>
    <c:when test="${fn:length(result) == 0}">
        <p><fmt:message key="there_are_no_rules" bundle="${resword}"/><br>
            <fmt:message key="there_are_no_rules1" bundle="${resword}"/><a href="${pageContext.request.contextPath}/ViewRuleAssignment">&nbsp;<fmt:message key="here" bundle="${resword}"/></a>
        </p>
    </c:when>
    <c:otherwise>
        <c:forEach items="${result}" varStatus="status">
            <div style="width: 600px">
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
                                                            <td class="table_header_column_top"><fmt:message
                                                                    key="rule_crf_version" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${status.current.key.crfVersion}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="rule_name" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${status.current.key.ruleName}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="rule_result" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:out value="${status.current.key.result}"/>
                                                            </td>
                                                        </tr>
                                                        <tr valign="top">
                                                            <td class="table_header_column"><fmt:message key="rule_actions" bundle="${resword}"/>:
                                                            </td>
                                                            <td class="table_cell">
                                                                <c:forEach items="${status.current.key.actions}" var="action">
                                                                    <c:out value="${action.actionType}"/> : <c:out
                                                                        value="${action.summary}"/><br>
                                                                </c:forEach>
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
            <br>
            <c:choose>
                <c:when test="${userBean.sysAdmin && module=='admin'}">
                    <span class="table_title_Admin">
                </c:when>
                <c:otherwise>
                    <span class="table_title_Manage">
                </c:otherwise>
            </c:choose>
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
                                                            <td class="table_header_row_left"><fmt:message key="rule_study_event_definition" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="rule_group_label" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="rule_item_name" bundle="${resword}"/></td>
                                                            <td class="table_header_row"><fmt:message key="action"  bundle="${resword}"/></td>
                                                        </tr>
                                                        <c:forEach items="${status.current.value}" varStatus="status1">
                                                            <c:set var="count" value="${count+1}"/>
                                                            <tr valign="top">
                                                                <td class="table_cell_left"><c:out value="${status1.current.key.studyEventDefinitionName}"/></td>
                                                                <td class="table_cell">${status1.current.key.itemGroupName}</td>
                                                                <td class="table_cell">${status1.current.key.itemName}</td>
                                                                <td class="table_cell">
                                                                    <a id="a${count}" style="cursor: pointer; cursor: hand;" onClick="showOrHideSubjects(this, ${count},'<fmt:message key="rule_show_subjects" bundle="${resword}"/>','<fmt:message key="rule_hide_subjects" bundle="${resword}"/>')"><fmt:message key="rule_show_subjects" bundle="${resword}"/></a>
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
            </div>
            <c:forEach items="${status.current.value}" varStatus="status2">
                <div style="display:none" id="subjects_${count}">
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

                                                                <c:forEach var="subject" items="${status2.current.value}">
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
        </c:forEach>
        <p><fmt:message key="rule_execute_rule_bottom_message1" bundle="${resword}"/><br>
            <fmt:message key="rule_execute_rule_bottom_message2" bundle="${resword}"/></p>
    </c:otherwise>
</c:choose>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
        </td>
        <c:if test="${fn:length(result) > 0}">
            <td>
                <input type="button" name="Submit" id="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_long long_submit" onClick="window.location.href='RunRule?${submitLinkParams}';"/>
            </td>
        </c:if>
        <td>
            <input type="button" name="cancel" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium" onclick=window.location.href='ListCRF'>
        </td>
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