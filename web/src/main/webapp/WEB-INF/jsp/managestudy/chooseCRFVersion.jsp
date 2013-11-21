<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"     prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy_top_pages_new.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="choose_crf_instruction_key"  bundle="${resword}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr><jsp:include page="../include/sideInfo.jsp"/>
  
<h1>
	<span class="first_level_header">
		<fmt:message key="choose_CRF_version" bundle="${resword}"/>
	</span>
</h1>
<script type="text/JavaScript" language="JavaScript" src="../../includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/javascript" language="javascript">
$.noConflict();
 jQuery(document).ready(function(){
       jQuery('#selectedVersion').change(function() {
        var x = jQuery(this).val();
        // and update the hidden input's value
        var ind = jQuery(this).attr("selectedIndex") ;
		var selectedText = jQuery(this.options[ind]).text();
		jQuery('#selectedVersionName').val(selectedText);
    });
});

</script>
<form action="${pageContext.request.contextPath}/pages/managestudy/confirmCRFVersionChange" method="POST">
<input type="hidden" name="studySubjectId" value="${studySubjectId}">
<input type="hidden" name="eventDefinitionCRFId" value="${eventDefinitionCRFId}">
<input type="hidden" name="studySubjectLabel" value="${studySubjectLabel}">
<input type="hidden" name="crfversionId" value="${crfversionId}">
<input type="hidden" name="crfId" value="${crfBean.id}">
<input type="hidden" name="crfName" value="${crfName}">
<input type="hidden" name="crfVersionName" value="${crfVersionName}">
<input type="hidden" name="eventCRFId" value="${eventCRFId}">
<input type="hidden" name="eventName" value="${eventName}">
<input type="hidden" name="eventCreateDate" value="${eventCreateDate}">
<input type="hidden" name="eventOrdinal" value="${eventOrdinal}">
<input type="hidden" id="formWithStateFlag" value=""/>

<table cellpadding="2" cellspacing="2" border="0" class="dataTable" >

<!-- header table -->
<tr><td>
<fmt:message key="study_subject_ID" bundle="${resword}"/>:</td>
<td><c:out value="${studySubjectLabel}"/></td></tr>


<tr><td>
<fmt:message key="event" bundle="${resword}"/>:</td>
<td> <c:out value="${eventName}" />&nbsp;(<c:out value="${eventCreateDate}" />)</td></tr>

<c:if test="${! empty eventOrdinal}">
<tr><td><fmt:message key="occurrence_number" bundle="${resword}"/>:</td>
<td><c:out value="${eventOrdinal}" /></td></tr>
</c:if>


<!-- <tr><td>
<fmt:message key="study" bundle="${resword}"/>:</td>
<td><c:out value="${studySubjectLabel}"/></td></tr>
 -->

<tr><td>
<fmt:message key="choose_CRF_version_crf_name" bundle="${resword}"/></td>
<td>
<a href="#" onclick="window.openNewWindow('../../ViewCRF?module=admin&crfId=<c:out value="${crfBean.id}"/>' ,'','','dn')">
<c:out value="${crfName}"/></a></td></tr>

<!-- default version label here -->
<tr><td>
<fmt:message key="choose_CRF_version_current_crf_version_title" bundle="${resword}"/></td>
<td><c:out value="${crfVersionName}"/></td></tr>
<tr><td>
<!-- select new version here -->
<fmt:message key="choose_CRF_version_combo_title" bundle="${resword}"/></td>
<td>
<!--  do not delete &nbsp; around version name -->
<select name="selectedVersionId" id="selectedVersion">
<option value="-1" >-Select-</option>
<c:forEach var="version" items="${crfBean.versions}">
<c:if test="${version.id != crfversionId}">
<option value="<c:out value="${version.id}"/>">&nbsp;<c:out value="${version.name}" />&nbsp;</option>
</c:if>
</c:forEach>
</select>
<script>
  $(function() {
    if ($("#selectedVersion").val() != undefined && $("#selectedVersion").val() != "-1") {
      $("#formWithStateFlag").val("changed");
    }
  });
</script>
<input type='hidden' id='selectedVersionName' name='selectedVersionName' value='zzz'>

</td></tr>
</table>
<!-- header ends -->


<!-- crf table here -->
<br><br>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR"><div class="tablebox_center">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
<td class="table_header_row_left"><fmt:message key="version_name" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="version_oid" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="date_created" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="owner" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="default_version" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="action" bundle="${resword}"/></td>
</tr>

<!-- versions data -->

<c:forEach var="version" items="${crfBean.versions}">
<tr>
<c:if test="${version.status.id == 1}" >
<td  class="table_cell_left"  > <c:out value="${version.name}" />&nbsp;</td>
<td  class="table_cell"  > <c:out value="${version.oid}" />&nbsp;</td>
<td  class="table_cell"  > <fmt:formatDate value="${version.createdDate}" pattern="${dteFormat}"/>&nbsp;</td>
<td  class="table_cell"  > <c:out value="${crfBean.owner.name}" />&nbsp;</td>
<!-- <td  class="table_cell"  > <c:out value="${version.status.name}" />&nbsp;</td>-->
<td  class="table_cell"  style="text-align:center;" ><c:if test="${version.id == crfversionId}">X</c:if>&nbsp;</td>
<td  class="table_cell"  > &nbsp;
<a onmouseup="javascript:setImage('bt_View1','../../images/bt_View.gif');" onmousedown="javascript:setImage('bt_View1','../../images/bt_View_d.gif');"
href="#" onclick="window.location.href='../../ViewSectionDataEntry?module=admin&crfId=<c:out value="${crfBean.id}"/>&crfVersionId=<c:out value="${version.id}"/>&tabId=1&crfListPage=yes'">
<img hspace="6" border="0" title="View" alt="View" src="../../images/bt_View.gif" name="bt_View1">
</a>
<a onmouseup="javascript:setImage('bt_Metadata','../../images/bt_Metadata.gif');" onmousedown="javascript:setImage('bt_Metadata','../../images/bt_Metadata.gif');"
href="#" onclick="window.location.href='../../ViewCRFVersion?id=<c:out value="${version.id}"/>'">
<img hspace="6" border="0" title="Metadata" alt="Metadata" src="../../images/bt_Metadata.gif" name="bt_Metadata">
</a>


</td>
</c:if>

</tr>
</c:forEach>


</table>
</div></div></div></div></div></div></div></div></div>

<table border="0" cellpadding="0" cellspacing="0"><tr>
 <td VALIGN="top">
  <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
				 value="<fmt:message key="back" bundle="${resword}"/>"
				 class="button_long"
				 onClick="formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" />
 </td >
 <td VALIGN="top">
   <input type="submit" name="confirmCRFVersionSubmit" class="button_long" 
          value="<fmt:message key="continue" bundle="${resword}"/>" >
 </td >
 <td VALIGN="top">
    <input type="button" name="customCancel" id="customCancel"
           value="<fmt:message key="cancel" bundle="${resword}"/>"
           class="button_long" onClick="$('#fr_cancel_button #cancel').click();"/>
 </td>
</tr></table>
</form>
<form id="fr_cancel_button" method="get" style="display: none;">
  <input type="hidden" name="id" value="<c:out value="${studySubjectId}"/>" />
  <input type="button" name="Cancel" id="cancel"
         value="<fmt:message key="cancel" bundle="${resword}"/>"
         class="button_long" onClick="formWithStateConfirmGoTo('<fmt:message key="sure_to_cancel" bundle="${resword}"/>', '${pageContext.request.contextPath}/ViewStudySubject?id=<c:out value="${studySubjectId}"/>');" />
</form>
<jsp:include page="../include/footer.jsp"/>

