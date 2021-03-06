<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage" />
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="../include/managestudy_top_pages.jsp" />

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jspfunctions.js?r=${revisionNumber}'/>"></script>

<jsp:include page="../include/sideAlert.jsp" />

<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a> 
		<b><fmt:message key="instructions" bundle="${restext}" /></b>

		<div class="sidebar_tab_content"> 
			<fmt:message key="system_properties_confirm_instruction" bundle="${restext}" />
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

<div class="sysProps">
	<form:form id="systemForm" method="post" commandName="systemCommand">
        <form:hidden path="formWithStateFlag"/>
		<span class="first_level_header"><fmt:message key="confirmSystemProperties" bundle="${resword}" /></span>
		<br/>

		<div class="groups">
			<table cellspacing="0" cellpadding="0" border="0" id="systemConfirmationTable">
				<tr>
					<th class="table_header_row"><fmt:message
							key="systemPropertyTable.th.class" bundle="${resword}" /></th>
					<th class="table_header_row"><fmt:message
							key="systemPropertyTable.th.subclass" bundle="${resword}" /></th>
					<th class="table_header_row"><fmt:message
							key="systemPropertyTable.th.property" bundle="${resword}" /></th>
					<th class="table_header_row"><fmt:message
							key="systemPropertyTable.th.value" bundle="${resword}" /></th>
				</tr>
				<c:forEach items="${systemCommand.systemPropertyGroups}" var="grp" varStatus="groupStatus">
					<c:set var="grp" value="${grp}" scope="request" />
					<c:set var="groupStatus" value="${groupStatus}" scope="request" />
					<tr>
						<c:set var="subGroupsRowspan" value="${fn:length(grp.subGroups)}" />
						<c:choose>
							<c:when test="${subGroupsRowspan > 0}">
								<c:set var="subGroupsRowspan" value="${0}" />
								<c:forEach items="${grp.subGroups}" var="subGrp" varStatus="subGroupStatus">
									<c:set var="subGroupsRowspan"
											value="${subGroupsRowspan + fn:length(subGrp.systemProperties)}" />
								</c:forEach>
								<td rowspan="${subGroupsRowspan}">
									<fmt:message key="systemProperty.${grp.group.name}.name" bundle="${resword}" />
								</td>
								<c:forEach items="${grp.subGroups}" var="subGrp" varStatus="subGroupStatus">
									<td rowspan="${fn:length(subGrp.systemProperties)}">
										<fmt:message key="systemProperty.${subGrp.group.name}.name" bundle="${resword}" />
									</td>
									<c:set var="subGrp" value="${subGrp}" scope="request" />
									<c:set var="subGroupStatus" value="${subGroupStatus}" scope="request" />
									<%@include file="systemConfirmProperty.jsp"%>
									</tr>
									<c:if test="${subGroupStatus.index < subGroupStatus.count - 1}">
										<tr>
									</c:if>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<td rowspan="${fn:length(grp.systemProperties)}">
									<fmt:message key="systemProperty.${grp.group.name}.name" bundle="${resword}" />
								</td>
								
								<td rowspan="${fn:length(grp.systemProperties)}">&nbsp;</td>
								<c:set var="subGrp" value="${null}" scope="request" />
								<c:set var="subGroupStatus" value="${null}" scope="request" />
								<%@include file="systemConfirmProperty.jsp"%>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class="buttons">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<input type="submit" name="back" id="back" value="<fmt:message key="back" bundle="${resword}"/>"
								class="button_medium medium_back" />
					</td>
					
					<td>
						<input type="submit" name="confirm" id="confirm" value="<fmt:message key="submit" bundle="${resword}"/>"
								class="button_medium medium_submit">
					</td>
					
					<td>
						<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>"
								class="button_medium medium_cancel" 
								onClick="confirmCancel('${defaultURL}')" />
					</td>
				</tr>
			</table>
		</div>
	</form:form>
</div>

<jsp:include page="../include/footer.jsp" />