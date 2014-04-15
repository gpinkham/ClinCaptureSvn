<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="exceptions" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="../include/managestudy_top_pages.jsp" />

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jspfunctions.js'/>"></script>

<jsp:include page="../include/sideAlert.jsp" />

<c:set var="color" scope="session" value="${newThemeColor}" />
<c:if test="${(color == 'violet') || (color == 'green')}">
	<style class="hideStuff" type="text/css">body {display:none;}</style>
</c:if>

<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a> 
		<b><fmt:message key="instructions" bundle="${restext}" /></b>
		<div class="sidebar_tab_content">
			<fmt:message key="system_properties_instruction" bundle="${restext}" />
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a> 
		<b><fmt:message key="instructions" bundle="${restext}" /></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<form:form id="systemForm" method="post" commandName="systemCommand" enctype="multipart/form-data">
    <form:hidden path="formWithStateFlag"/>
	<div class="sysProps">
		<span class="first_level_header"><fmt:message key="systemPropertiesHeader" bundle="${resword}" /></span><br/>
        <spring:bind path="systemCommand.*">
            <c:if test="${not empty status.errorMessages || systemCommandError ne null}">
                <div class="systemErrors">
                    <c:if test="${systemCommandError ne null}">
                        <span class="systemError"><fmt:message key="${systemCommandError}" bundle="${exceptions}" /></span><br/>
                    </c:if>
                    <form:errors path="*" cssClass="systemError" />
                </div>
            </c:if>
        </spring:bind>
        <div class="groups">
            <c:forEach items="${systemCommand.systemPropertyGroups}" var="grp" varStatus="groupStatus">
                <c:set var="grp" value="${grp}" scope="request" />
                <c:set var="subGrp" value="${null}" scope="request" />
                <c:set var="groupStatus" value="${groupStatus}" scope="request" />
                <c:set var="subGroupStatus" value="${null}" scope="request" />
                <%@include file="systemGroup.jsp"%>
                <div id="div_group_id_${grp.group.id}" class="propertyHolder ${grp.opened ? "" : "hidden"}">
                    <%@include file="systemProperty.jsp"%>
                    <div id="div_sub_group_id_${grp.group.id}" class="subGroup ${grp.opened ? "" : "hidden"}">
                        <c:forEach items="${grp.subGroups}" var="subGrp" varStatus="subGroupStatus">
                            <c:set var="subGrp" value="${subGrp}" scope="request" />
                            <c:set var="subGroupStatus" value="${subGroupStatus}" scope="request" />
                            <%@include file="systemGroup.jsp"%>
                            <div id="div_group_id_${subGrp.group.id}" class="propertyHolder ${subGrp.opened ? "" : "hidden"}">
                                <%@include file="systemProperty.jsp"%>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="buttons">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td>
                        <input type="button" name="BTN_Back" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium"
                               onClick="formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" />
                    </td>
                    <td>
                        <input type="button" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium" onClick="doSubmit();">
                    </td>
                </tr>
            </table>
        </div>
	</div>
</form:form>

<jsp:include page="../include/footer.jsp" />
