<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/extract-header.jsp"/>

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
		<fmt:message key="list_shows_accesible_click_icons" bundle="${restext}"/>


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
<jsp:useBean scope="request" id="datasets" class="java.util.ArrayList"/>


<h1>
	<span class="first_level_header">
		<fmt:message key="datasets_for" bundle="${resword}"/>: <c:out value="${study.name}" /> 
		<a href="javascript:openDocWindow('help/4_1_viewDatasets_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showDatasetRow.jsp" /></c:import>

<br>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
	   value="<fmt:message key="back" bundle="${resword}"/>"
	   class="button_medium medium_back"
	   onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

<c:set var="createDatasetBTNCaption"><fmt:message key="create_dataset" bundle="${resword}"/></c:set>
<input type="button" name="BTN_Create" id="CreateDataset" value="${createDatasetBTNCaption}"
	   class="${ui:getHtmlButtonCssClass(createDatasetBTNCaption, "")}"
	   onclick="window.location.href=('CreateDataset');"/>


<input id="accessAttributeName" type="hidden" value="data-cc-datasetId">
<jsp:include page="../include/footer.jsp"/>
