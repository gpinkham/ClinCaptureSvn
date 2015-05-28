<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:choose>
	<c:when test="${userBean.sysAdmin}">
		<c:import url="../include/admin-header.jsp"/>
	</c:when>
	<c:otherwise>
		<c:import url="../include/managestudy-header.jsp"/>
	</c:otherwise>
</c:choose>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->

<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope='request' id='eventCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='versionToRemove' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_removal_of_CRF_version" bundle="${resword}"/>
	</span>
</h1>

<p><fmt:message key="you_choose_to_remove_the_following_CRF_version" bundle="${restext}"/>:</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center_admin">
	<table  class="table_vertical">
		<tr>
			<td><fmt:message key="name" bundle="${resword}"/>:</td>
			<td> <c:out value="${versionToRemove.name}"/></td>
		</tr>
		<tr>
			<td><fmt:message key="description" bundle="${resword}"/>:</td>
			<td> <c:out value="${versionToRemove.description}"/></td>
		</tr>
	</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/>

<span class="table_title_Admin">
	<fmt:message key="associated_event_CRFs" bundle="${resword}"/>
</span>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center_admin">
	<table class="table_horizontal">
		<tr>
			<td><fmt:message key="SE_ID" bundle="${resword}"/></td>
			<td><fmt:message key="date_interviewed" bundle="${resword}"/></td>
			<td><fmt:message key="status" bundle="${resword}"/></td>
		</tr>
		<c:forEach var="eventCRF" items="${eventCRFs}">
		<tr>
			<td><c:out value="${eventCRF.studyEventId}"/></td>
			<td>
				<cc-fmt:formatDate value="${eventCRF.dateInterviewed}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
			</td>
			<td><c:out value="${eventCRF.status.name}"/></td>
		</tr>
		</c:forEach>
	</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/>

<form action='RemoveCRFVersion?action=submit&id=<c:out value="${versionToRemove.id}"/>' method="POST">
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
						value="<fmt:message key="back" bundle="${resword}"/>"
						class="button_medium medium_back"
						onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
	<input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<fmt:message key="if_you_remove_this_CRF_version" bundle="${restext}"/>", height: 150, width: 500, submit: this });'>
	<input type = "hidden" name = "confirmPagePassed" value = "true" />
</form>

<c:choose>
	<c:when test="${userBean.sysAdmin}">
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
