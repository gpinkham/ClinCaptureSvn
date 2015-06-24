<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/admin-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->

<%-- TODO wrap into one page with createExportJob.jsp, tbh--%>
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
<jsp:useBean scope='request' id='jobName' class='java.lang.String'/>
<jsp:useBean scope='request' id='jobDesc' class='java.lang.String'/>

<jsp:useBean scope='request' id='periodToRun' class='java.lang.String'/>
<jsp:useBean scope='request' id='tab' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc12' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13oc' class='java.lang.String'/>
<jsp:useBean scope='request' id='spss' class='java.lang.String'/>
<jsp:useBean scope='request' id='contactEmail' class='java.lang.String'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="update_scheduled_job" bundle="${resword}"/>
	</span>
</h1>
<p>

<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I>
	<fmt:message key="job_is_set_to_run_on_local_time" bundle="${resword}"/> <cc-fmt:formatDate value="${now}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>.
</I></P>

<form action="UpdateJobExport" method="post">
<input type="hidden" name="action" value="confirmall" />
<input type="hidden" name="tname" value="<c:out value="${jobName}"/>" />

<table>
<tr>
		<td class="text" width="280px"><b><fmt:message key="job_name" bundle="${resword}"/>:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobName"/></jsp:include></td>
		<td class="text">
			<input type="text" name="jobName" size="30" value="<c:out value="${jobName}"/>" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"/> <span class="alert">*</span>
		</td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="description" bundle="${resword}"/>:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include></td>
		<td class="text"><input type="text" name="jobDesc" size="60" value="<c:out value="${jobDesc}"/>" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"/> <span class="alert">*</span>
		</td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="please_pick_a_dataset_to_export" bundle="${resword}"/>:</b><br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsId"/></jsp:include></td>
		<td class="text"><select name="dsId" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
			<c:forEach var="dataset" items="${datasets}">
				<option value="<c:out value="${dataset.id}"/>"
					<c:if test="${dsId == dataset.id}">
						selected
					</c:if>
				><c:out value="${dataset.name}" /></option>
			</c:forEach>
		</select> <span class="alert">*</span></td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="period_to_run" bundle="${resword}"/>:</b>
		&nbsp; <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="periodToRun"/></jsp:include></td>
		<td class="text">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td class="text"><fmt:message key="daily" bundle="${resword}"/></td>
		<td class="text"><input type="radio" name="periodToRun" value="daily" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"
			<c:if test="${periodToRun == 'daily'}">
				checked 
			</c:if>
			/> <span class="alert">*</span></td>
		</tr>
		<tr>
			<td class="text"><fmt:message key="weekly" bundle="${resword}"/></td>
			<td class="text"><input type="radio" name="periodToRun" value="weekly" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"
			<c:if test="${periodToRun == 'weekly'}">
				checked
			</c:if>
			/></td>
		</tr>
		<tr>
			<td class="text"><fmt:message key="monthly" bundle="${resword}"/></td>
			<td class="text"><input type="radio" name="periodToRun" value="monthly" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"
			<c:if test="${periodToRun == 'monthly'}">
				checked
			</c:if>
			/>
		</td>
		</tr>
		</table>
		</td>
	</tr>


	<tr>
		<td class="text"><b><fmt:message key="start_date_time" bundle="${resword}"/>:</b></td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="job"/><c:param name="count" value="1"/></c:import>
					<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) </td>
				</tr>
				<tr>
					<td	colspan="7">
						<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDate"/></jsp:include>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td class="text"><b><fmt:message key="file_formats" bundle="${resword}"/>:</b>
		&nbsp;<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="tab"/></jsp:include>
		</td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
            <c:forEach var="extract" items="${extractProperties}" varStatus="loopCounter">
            <tr>
                <td class="text">
                    <c:choose>
                        <c:when test="${fn:startsWith(extract.filedescription, '&')==true}">
                            <fmt:message key="${fn:substringAfter(extract.filedescription, '&')}" bundle="${restext}"/>&nbsp;
                        </c:when>
                        <c:otherwise>
                            <c:out value="${extract.filedescription}"/>&nbsp;
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="text"><input type="radio" name="formatId" value="<c:out value="${extract.id}"/>" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"
                    <c:if test="${formatId == extract.id}">
						checked
					</c:if>
				/></td>
				<c:if test="${loopCounter.count == 1}">
					<td class="alert">*</td>
				</c:if>
			</tr>
            </c:forEach>    
			</table>
		</td>
	</tr>



	<tr>
		<td class="text"><b><fmt:message key="contact_email" bundle="${resword}"/>:</b><br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include></td>
		<td class="text"><input type="text" name="contactEmail" size="60" value="<c:out value="${contactEmail}"/>" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"/>
		</td>
	</tr>
</table>

<table>
	<tr>
		<td align="left">
			<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>" 
					class="button_medium medium_back" 
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
					
		</td>
		<td>
			<input type="submit" name="btnSubmit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit"/>
		</td>
		<td>
			<img src="images/icon_UnchangedData.gif" style="visibility:hidden"
			   title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
		</td>
	</tr>
</table>
</form>

<jsp:include page="../include/footer.jsp"/>
