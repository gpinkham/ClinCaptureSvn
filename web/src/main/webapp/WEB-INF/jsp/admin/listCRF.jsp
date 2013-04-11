<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

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
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="CRF_library_shows_all_CRFs" bundle="${restext}"/>

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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
	<h1><span class="title_manage"><fmt:message key="administer_CRFs2" bundle="${resworkflow}"/>
        <a href="javascript:openDocWindow('help/3_5_viewCRF_Help.html')">
            <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
</span></h1>
</c:when>
<c:otherwise>
	<h1><span class="title_manage"><fmt:message key="manage_CRFs2" bundle="${resworkflow}"/>
        <a href="javascript:openDocWindow('help/3_5_viewCRF_Help.html')">
		<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
</span></h1>
</c:otherwise>
</c:choose>

<!-- <p><fmt:message key="can_download_blank_CRF_excel" bundle="${restext}"/> <a href="DownloadVersionSpreadSheet?template=1"><b><fmt:message key="here" bundle="${resword}"/></b></a>.</p> -->
<%--
<p><fmt:message key="also_download_set_example_CRFs" bundle="${restext}"/> <a href="http://www.openclinica.org/entities/entity_details.php?eid=151" target="_blank"><fmt:message key="here" bundle="${resword}"/></a>.</p>
--%>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showCRFRow.jsp" /></c:import>
<br>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
<input type="button" style="position:relative;" name="<fmt:message key="create_CRF" bundle="${resword}"/>" value="<fmt:message key="create_CRF" bundle="${resword}"/>" class="button_long" onClick="window.location.href='CreateCRFVersion?module=<c:out value="${module}"/>'" />


<jsp:include page="../include/footer.jsp"/>
