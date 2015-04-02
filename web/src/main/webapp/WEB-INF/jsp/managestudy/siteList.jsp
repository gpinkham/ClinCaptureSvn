<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>


<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy-header.jsp"/>
<script type="text/javascript" language="javascript">
    
	jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
    
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="study_have_sites_data_collected" bundle="${restext}"/>
			<fmt:message key="view_details_site_or_create" bundle="${restext}"/>
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
	</td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope="session" id="userRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />

<h1>
	<span class="first_level_header">
		<fmt:message key="manage_all_sites_in_study" bundle="${restext}"/> <c:out value="${study.name}"/> 
		<a href="javascript:openDocWindow('help/5_4_sites_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>

<br>
<c:import url="../include/showTable.jsp">
    <c:param name="rowURL" value="showSiteRow.jsp" />
    <c:param name="userRoleId" value="${userRole.role.id}" />
</c:import>
<br><br>

<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
<input type="button" name="BTN_Create_Site"
					value="<fmt:message key="create_site" bundle="${resword}"/>"
					class="button_medium"
					onClick="window.location.href=('CreateSubStudy');"/>
 
 <input id="accessAttributeName" type="hidden" value="data-cc-siteId">
<jsp:include page="../include/footer.jsp"/>
