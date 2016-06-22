<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="../include/managestudy_top_pages.jsp" />
<jsp:include page="../include/sideAlert.jsp" />

<jsp:include page="../include/sideInfo.jsp" />

<!-- Page content start -->

<h1>
	<span class="first_level_header">
		<fmt:message key="email_log_details" bundle="${resword}"/>
	</span>
</h1>

<table class="table_vertical table_shadow_bottom">
	<tr>
		<td><fmt:message bundle="${resword}" key="action"/>:</td>
		<td style="width: 400px"><fmt:message bundle="${resword}" key="email_action.${logEntry.action}"/></td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="recipient"/>:</td>
		<td>${logEntry.recipient}</td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="date_sent"/>:</td>
		<td><cc-fmt:formatDate value="${logEntry.dateSent}" dateTimeZone="${userBean.userTimeZoneId}"/></td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="sent_by"/>:</td>
		<td>${logEntry.senderAccount.name}</td>
	</tr>
	<tr>
		<c:set var="status_class" value="${logEntry.wasSent == 'TRUE' ? 'aka_green_highlight' : 'aka_red_highlight'}"/>
		<td><fmt:message bundle="${resword}" key="status"/>:</td>
		<td class="${status_class}"><fmt:message bundle="${resword}" key="email_status.${logEntry.wasSent}"/></td>
	</tr>
	<c:if test="${logEntry.wasSent == 'FALSE'}">
		<tr>
			<td><fmt:message bundle="${resword}" key="error"/></td>
			<td>${logEntry.error}</td>
		</tr>
	</c:if>
</table>

<br>

<div style="clear:left; float:left">
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
		   value="<fmt:message key="back" bundle="${resword}"/>"
		   class="button_medium medium_back"
		   onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
</div>

<jsp:include page="../include/footer.jsp" />
