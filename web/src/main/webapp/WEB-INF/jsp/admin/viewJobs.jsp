<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/admin-header.jsp"/>

<script type="text/javascript" language="javascript">
    jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
    
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="scheduled_export_data_jobs" bundle="${resword}"/>&nbsp;
		<a href="javascript:openDocWindow('help/6.4.1_exportJobs_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>

<p></p>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I>
	<fmt:message key="job_is_set_to_run_on_local_time" bundle="${resword}"/> <cc-fmt:formatDate value="${now}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>.
</I></P>

<c:import url="../include/showTable.jsp">
	<c:param name="rowURL" value="showJobRow.jsp" />
</c:import>

<br><br>

<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

<c:set var="createExportJobBTNCaption"><fmt:message key="create_export_job" bundle="${resword}"/></c:set>
<input type="button" name="createExportJobBTN" value="${createExportJobBTNCaption}"
	   class="${ui:getHtmlButtonCssClass(createExportJobBTNCaption, "")}"
	   onClick="window.location.href='CreateJobExport'"/>
           
<br><br>

<input id="accessAttributeName" type="hidden" value="data-cc-exportJobId">
<jsp:include page="../include/footer.jsp"/>
