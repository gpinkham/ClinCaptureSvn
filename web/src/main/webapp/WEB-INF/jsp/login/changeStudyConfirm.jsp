<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/home-header.jsp"/>
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

<jsp:useBean scope="session" id="studyWithRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<jsp:useBean scope="request" id="studyId" type="java.lang.Integer"/>

<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_changing_curr_study_site" bundle="${restext}"/>
	</span>
</h1>

<form action="ChangeStudy" method="post">
	<input type="hidden" name="action" value="submit">
	<input type="hidden" name="studyId" value="<c:out value="${studyId}"/>">
	<p>
		<c:choose>
			<c:when test="${studyWithRole.parentStudyId > 0}">
				<fmt:message key="choosing_switch_to_site" bundle="${restext}"/> <c:out value="${parentStudyName}"/>: <c:out value="${studyWithRole.studyName}"/>			
			</c:when>
			<c:otherwise>
				<fmt:message key="choosing_switch_to_study" bundle="${restext}"/> <c:out value="${studyWithRole.studyName}"/> 
			</c:otherwise>
		</c:choose>
		<fmt:message key="with_a_role_of" bundle="${restext}"/> <c:out value="${studyWithRole.role.description}"/>. 
		<fmt:message key="the_study_site_status_is" bundle="${restext}"/> <c:out value="${currentStudy.status.name}"/>
	</p><br>

	<input type="button" name="BTN_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: history.go(-1);"/>
	<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
</form>
<jsp:include page="../include/footer.jsp"/>
