<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="study_have_sites_data_collected" bundle="${restext}"/>
        <fmt:message key="view_details_site_or_create" bundle="${restext}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope="session" id="userRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />

<h1><span class="title_manage">
<fmt:message key="manage_all_sites_in_study" bundle="${restext}"/> <c:out value="${study.name}"/> <a href="javascript:openDocWindow('help/5_4_sites_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<%-- <div class="homebox_bullets">
 <c:choose>
   <c:when test="${study.parentStudyId>0}">
	 <a href="ViewSite?id=<c:out value="${study.id}"/>">
   </c:when>
   <c:otherwise>
	 <a href="ViewStudy?id=<c:out value="${study.id}"/>">
   </c:otherwise>
</c:choose>
<fmt:message key="view_current_study_details" bundle="${restext}"/></a>
</div>--%>
<%-- 
   <c:if test="${!study.status.locked}">
    <div class="homebox_bullets"><a href="CreateSubStudy"><fmt:message key="create_new_site" bundle="${resworkflow}"/></a></div>
   </c:if>
   --%>
<p></p>
<c:import url="../include/showTable.jsp">
    <c:param name="rowURL" value="showSiteRow.jsp" />
    <c:param name="userRoleId" value="${userRole.role.id}" />
</c:import>
<br><br>


<%-- <div class="homebox_bullets"><a href="pages/studymodule"><fmt:message key="go_back_build_study_page" bundle="${resword}"/></a></div>--%>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

 <c:choose>
   <c:when test="${study.parentStudyId>0}">
         <input id="ViewStudyDetails" class="button_medium" title='<fmt:message key="view_current_study_details" bundle="${restext}"/>' type="button" name="BTN_ViewStudyDetails" value="<fmt:message key="view_study" bundle="${resword}"/>" onclick="window.location.href=('ViewStudy?id=<c:out value="${study.id}"/>');"/>
   </c:when>
   <c:otherwise>
	 	 <input id="ViewStudyDetails" class="button_medium" title='<fmt:message key="view_current_study_details" bundle="${restext}"/>' type="button" name="BTN_ViewStudyDetails" value="<fmt:message key="view_study" bundle="${resword}"/>" onclick="window.location.href=('ViewStudy?id=<c:out value="${study.id}"/>');"/>
   </c:otherwise>
</c:choose>
</a>




<jsp:include page="../include/footer.jsp"/>
