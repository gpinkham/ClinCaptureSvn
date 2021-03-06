<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/admin-header.jsp"/>
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

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
		<fmt:message key="studies_are_indicated_in_bold" bundle="${restext}"/>
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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="administer_studies" bundle="${resword}"/> 
		<a href="javascript:openDocWindow('help/6_1_administerStudies_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>

<%--div class="homebox_bullets"><a href="CreateStudy"><fmt:message key="create_a_new_study" bundle="${resword}"/></a></div--%>
<p>
<fmt:message key="studies_are_indicated_in_bold" bundle="${restext}"/>
</p>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showStudyRow.jsp" /></c:import>
<br>
	<%-- <a id="haCreateStudy" href="CreateStudy" style="display: none;"><fmt:message key="create_a_new_study" bundle="${resword}"/></a> --%>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

		<input type="button" onclick="location.href = '${pageContext.request.contextPath}/CreateStudy'"  name="create_study" value="<fmt:message key="create_study" bundle="${resword}"/>" class="button_medium"/>

<br><br>

<input id="accessAttributeName" type="hidden" value="data-cc-studyId">
<jsp:include page="../include/footer.jsp"/>
