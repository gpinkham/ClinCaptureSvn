<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<script language="JavaScript">
<!--

function selectAll() {
    if (document.cl.all.checked) {
	  for (var i=0; i <document.cl.elements.length; i++) {
		if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
			document.cl.elements[i].checked = true;
		}
	  }
	} else {
	  for (var i=0; i <document.cl.elements.length; i++) {
		if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
			document.cl.elements[i].checked = false;
		}
	  }
	}
}
function notSelectAll() {
	if (!this.checked){
		document.cl.all.checked = false;
    }

}
//-->
</script>


<jsp:include page="../include/extract-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content"></div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<c:choose>
	<c:when test="${newDataset.id>0}">
		<h1>
			<span class="first_level_header">
				<fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="view_selected_items" bundle="${resword}"/> 
				<a href="javascript:openDocWindow('help/4_7_editDataset_Help.html#step1')">
					<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
				</a>
				: <c:out value='${newDataset.name}'/>
			</span>
		</h1>
	</c:when>
	<c:otherwise>
		<h1>
			<span class="first_level_header">
				<fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="view_selected_items" bundle="${resword}"/> 
				<a href="javascript:openDocWindow('help/4_2_createDataset_Help.html#step1')">
					<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
				</a>
			</span>
		</h1>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty newDataset.itemDefCrf}">
		<p><fmt:message key="please_select_one_CRF_from_the" bundle="${restext}"/> <b><fmt:message key="left_side_info_panel" bundle="${restext}"/></b><fmt:message key="select_items_in_CRF_include_dataset" bundle="${restext}"/></p>
		<p><fmt:message key="click_event_subject_attributes_specify" bundle="${restext}"/></p>
	</c:when>
	<c:otherwise>
		<p><fmt:message key="can_view_items_selected_inclusion" bundle="${restext}"/><fmt:message key="select_all_items_inclusion_clicking" bundle="${restext}"/></p>
	</c:otherwise>
</c:choose>

<c:if test="${empty newDataset.itemDefCrf}">
	<form id="datasetForm" action="EditSelected" method="post" name="cl">
		<input type="hidden" name="all" value="1">
		<input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/>
		<input type="submit" id="btnSubmit" value="<fmt:message key="select_all_items_in_study" bundle="${resword}"/>" class="button_xlong" onClick="return confirmSubmit({message: '<fmt:message key="there_a_total_of" bundle="${resword}"><fmt:param value="${totalNumberOfStudyItems}"/></fmt:message>', height: 150, width: 500, submit: this });"/>
		<input type="button" onclick="confirmCancel('ViewDatasets');" name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/>
	</form>
	<br><br>
</c:if>

<c:if test="${!empty newDataset.itemDefCrf}">
	<form id="datasetForm" action="CreateDataset" method="post" name="cl">
	<input type="hidden" name="action" value="beginsubmit"/>
	<input type="hidden" name="crfId" value="-1">
	<input type="hidden" name="defId" value="<c:out value="${definition.id}"/>">
	<P><B><fmt:message key="show_items_this_dataset" bundle="${restext}"/></b></p>
	
	<table border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td><input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/></td>
			<td><input type="submit" id="btnSubmit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
			<td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
			<td><input type="button" onclick="confirmCancel('ViewDatasets');" name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/></td>
		</tr>
	</table>
	<br>
	
	<jsp:include page="selected.jsp"/>
	
	<table border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td><input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/></td>
			<td><input type="submit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
			<td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
			<td><input type="button" onclick="confirmCancel('ViewDatasets');" name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/></td>
		</tr>
	</table>
	</form>
</c:if>

<jsp:include page="createDatasetBoxes.jsp" flush="true">
<jsp:param name="selectStudyEvents" value="1"/>
</jsp:include>

<jsp:include page="../include/footer.jsp"/>
