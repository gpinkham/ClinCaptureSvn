<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/managestudy_top_pages_new.jsp"/>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="confirm_crf_instruction_key"  bundle="${resword}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr><jsp:include page="../include/sideInfo.jsp"/>
  
  
  
<h1><span class="title_manage">
<fmt:message key="confirm_CRF_version" bundle="${resword}"/>
<!--
 <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/?')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
 -->
</span></h1>



<table cellpadding="2" cellspacing="2" border="0" >




<!-- header table -->
<!-- subject lable here -->
<tr><td>
<fmt:message key="study_subject_ID" bundle="${resword}"/>: &nbsp;</td>
<td><c:out value="${studySubjectLabel}"/></td></tr>



<tr><td>
<fmt:message key="event" bundle="${resword}"/>:</td>
<td> <c:out value="${eventName}" />&nbsp;(<c:out value="${eventCreateDate}" />)</td></tr>

<c:if test="${! empty eventOrdinal}">
<tr><td><fmt:message key="occurrence_number" bundle="${resword}"/>:</td>
<td><c:out value="${eventOrdinal}" /></td></tr>
</c:if>     

<!--  <tr><td>
<fmt:message key="study" bundle="${resword}"/>:</td>
<td><c:out value="${studySubjectLabel}"/></td></tr>-->

<!-- CRF name  here -->
<tr><td>
<fmt:message key="choose_CRF_version_crf_name" bundle="${resword}"/>:</td>
<td><a href="#" onclick="window.openNewWindow('${pageContext.request.contextPath}/ViewCRF?module=admin&crfId=<c:out value="${crfId}"/>' ,'','','dn')">
<c:out value="${crfName}"/></a></td></tr>
<tr><td>


<!-- default version label here -->
<tr><td>
<fmt:message key="choose_CRF_version_current_crf_version_title" bundle="${resword}"/>:</td>
<td>
<a href="#" onclick="window.openNewWindow('../../ViewSectionDataEntry?module=admin&crfId=<c:out value="${crfId}"/>&crfVersionId=<c:out value="${crfversionId}"/>&tabId=1&crfListPage=yes','','','')">
<c:out value="${crfVersionName}"/>
</a>
</td></tr>

<tr><td>
<fmt:message key="confirm_CRF_version_new_version" bundle="${resword}"/>: </td>
<td>
<a href="#" onclick="window.openNewWindow('../../ViewSectionDataEntry?module=admin&crfId=<c:out value="${crfId}"/>&crfVersionId=<c:out value="${selectedVersionId}"/>&tabId=1&crfListPage=yes','','','')">
<c:out value="${selectedVersionName}"/>
</a>
</td></tr>
</table>
<!-- header ends -->

<br><br>
<fmt:message key="confirm_crf_version_table_comment" bundle="${resword}"/>

<br><br>
<!-- crf table here -->

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR"><div class="tablebox_center">

<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr><td>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
<td class="table_header_row_left" style="" colspan="3"><fmt:message key="confirm_CRF_version_current_version" bundle="${resword}"/>: '
<c:out value="${crfVersionName}"/>'</td></tr>
<tr>
<td class="table_header_row_left"><fmt:message key="confirm_CRF_version_field_name" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="confirm_CRF_version_field_oid" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="confirm_CRF_version_field_value" bundle="${resword}"/></td>
</tr>
<c:forEach items="${rows}" var="row" varStatus="rowLoop" begin="0" step="1">

<tr>
<td  class="table_cell_left" > ${row[0]}&nbsp;</td>
<td  class="table_cell"  > 
<c:choose>
<c:when test="${empty row[1]}" >
&nbsp;
</c:when>
<c:otherwise>
<a href="javascript: openDocWindow('../../ViewItemDetail?itemId=${row[2]}')"> ${row[1]} </a>
</c:otherwise>
</c:choose>
</td>



<td  class="table_cell"  > ${row[3]}&nbsp;</td>
</tr>
</c:forEach>
</table></td>
<td>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
<td class="table_header_row" style=""  colspan="3"><fmt:message key="confirm_CRF_version_new_version" bundle="${resword}"/>: '
<c:out value="${selectedVersionName}"/>'</td>
</tr>
<tr>
<td class="table_header_row"><fmt:message key="confirm_CRF_version_field_name" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="confirm_CRF_version_field_oid" bundle="${resword}"/></td>
<td class="table_header_row"><fmt:message key="confirm_CRF_version_field_value" bundle="${resword}"/></td>
</tr>

<c:forEach items="${rows}" var="row" varStatus="rowLoop" begin="0" step="1">

<tr>
<td  class="table_cell"  > ${row[4]}&nbsp;</td>
<td  class="table_cell"  > 
<c:choose>
<c:when test="${empty row[5]}" >
&nbsp;
</c:when>
<c:otherwise>
<a href="javascript: openDocWindow('../../ViewItemDetail?itemId=${row[6]}')"> ${row[5]} </a>
</c:otherwise>
</c:choose>
</td>
<td  class="table_cell"  > ${row[7]}&nbsp;</td>
</tr>
</c:forEach>
</table>
</td></tr></table>
</div></div></div></div></div></div></div></div></div>

<table border="0" colspan="2"><tr><td>
<form method="POST" action="${pageContext.request.contextPath}/pages/managestudy/changeCRFVersion" >
<input type="hidden" name="newCRFVersionId" value="${selectedVersionId}">

<input type="hidden" name="studySubjectId" value="${studySubjectId}">
<input type="hidden" name="eventDefinitionCRFId" value="${eventDefinitionCRFId}">
<input type="hidden" name="studySubjectLabel" value="${studySubjectLabel}">
<input type="hidden" name="crfversionId" value="${crfversionId}">
<input type="hidden" name="crfId" value="${crfId}">
<input type="hidden" name="crfName" value="${crfName}">
<input type="hidden" name="crfVersionName" value="${crfVersionName}">
<input type="hidden" name="eventCRFId" value="${eventCRFId}">
<input type="hidden" name="eventName" value="${eventName}">
<input type="hidden" name="eventCreateDate" value="${eventCreateDate}">
<input type="hidden" name="eventOrdinal" value="${eventOrdinal}">

<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
       value="<fmt:message key="back" bundle="${resword}"/>"
       class="button_long"
       onClick="history.go(-1);" />

<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_long">
</form></td><td>

<form id="fr_cancel_button" method="get">
<input type="hidden" name="id" value="<c:out value="${studySubjectId}"/>" />
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_long" onClick="confirmGoTo('<fmt:message key="sure_to_cancel" bundle="${resword}"/>', '${pageContext.request.contextPath}/ViewStudySubject?id=<c:out value="${studySubjectId}"/>');" >


</form>
</td></tr></table>


<jsp:include page="../include/footer.jsp"/>