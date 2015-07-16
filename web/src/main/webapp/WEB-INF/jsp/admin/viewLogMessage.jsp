<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<jsp:include page="../include/admin-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content"></div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp"/>

<h1>
	<span class="first_level_header">
		<fmt:message key="view_log" bundle="${resword}"/>: <c:out value="${filename}"/>
	</span>
</h1>



<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="tablebox_center">
		<p class="pl10">&nbsp;<c:out value="${logmsg}" escapeXml="false"/></p>
	</div>
</div></div></div></div></div></div></div></div>

<br/>

<table>
	<tr>
		<td>
			<input type="button" name="BTN_Back_Smart" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
		</td>
		<td>
			<input type="button" value='<fmt:message key="view_all_export_data_jobs" bundle="${resword}"/>' class="button_long" onclick="window.location.href='ViewJob'"/>
		</td>
		<td>
			<input type="button" value='<fmt:message key="view_all_import_data_jobs" bundle="${resword}"/>' class="button_long" onclick="window.location.href='ViewImportJob'"/>
		</td>
	</tr>
</table>

<br/>

<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean id="now" class="java.util.Date" />
<p><i><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</i></p>

 <jsp:include page="../include/footer.jsp"/>