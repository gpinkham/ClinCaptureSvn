<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/managestudy-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">		  
   		
    <fmt:message key="please_select_users_in_the_following_table"  bundle="${resword}"/>
   		
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

<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope="request" id="roles" class="java.util.ArrayList"/>
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
       	confirmDialog({ 
       		message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
       		height: 150,
       		width: 500,
       		redirectLink: 'ListStudyUser'
       		});      
        	return false;
    }
    return true; 
  }
   //-->
</script>
<h1>
	<span class="first_level_header">
    <c:choose>
        <c:when test="${study.parentStudyId > 0}">
            <fmt:message key="assign_users_to_current_site" bundle="${resword}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="assign_users_to_current_study" bundle="${resword}"/>
        </c:otherwise>
    </c:choose>
    <c:out value="${study.name}"/>    
	</span>
</h1>

<c:choose>
    <c:when test="${study.parentStudyId > 0}">
        <fmt:message key="assign_site_user_note" bundle="${resword}"/>
        <a href="${pageContext.request.contextPath}/ChangeStudy"><fmt:message key="that_study" bundle="${resword}"/></a>
    </c:when>
    <c:otherwise>
        <fmt:message key="assign_study_user_note" bundle="${resword}"/>
        <a href="${pageContext.request.contextPath}/ChangeStudy"><fmt:message key="that_site" bundle="${resword}"/></a>
    </c:otherwise>
</c:choose>

<br><br>

<form name="userForm" action="AssignUserToStudy" method="post">
<input type="hidden" name="action" value="submit">
 <c:set var="count" value="0"/>
<c:import url="../include/showTableForStudyUserList.jsp">
<c:param name="rowURL" value="showStudyUserRow.jsp" />
<c:param name="outerFormName" value="userForm" />
</c:import>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
	<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" 
					value="<fmt:message key="back" bundle="${resword}"/>" 
					class="button_medium medium_back" 
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
</td>
<td>
<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit">
</td>
<td>
    <c:set var="changeStudySiteBTNCaption"><fmt:message key="change_study_site" bundle="${resword}"/></c:set>
	<input type="button" name="BTN_Change" id="ChangeStudySite" value="${changeStudySiteBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(changeStudySiteBTNCaption, "")}"
		   onClick="javascript:window.location.href=('ChangeStudy');"/>
</td>

<td>
  <img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
</td>
</tr></table>
</form>
<br><br>

<jsp:include page="../include/footer.jsp"/>
