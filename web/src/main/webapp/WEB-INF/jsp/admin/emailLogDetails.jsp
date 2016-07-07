<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="/includes/js/pages/emailLog.js.jsp" />
<jsp:include page="../include/managestudy_top_pages.jsp" />
<jsp:include page="../include/sideAlert.jsp" />

<link rel="stylesheet" href="<c:url value='/includes/jmesa/jmesa.css?r=${revisionNumber}'/>" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jmesa.js?r=${revisionNumber}'/>"></script>
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery.jmesa.js?r=${revisionNumber}'/>"></script>

<jsp:include page="../include/sideInfo.jsp" />

<script type="text/javascript">
	function onInvokeAction(id) {
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
</script>

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

<br/>

<h3>
	<fmt:message bundle="${resword}" key="resend_attempts"/>
</h3>

<form action="${pageContext.request.contextPath}/pages/EmailLogDetails">
	<input type="hidden" name="id" value="${logEntry.id}"/>
	${dataTable}
</form>

<br/>

<div>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
		   value="<fmt:message key="back" bundle="${resword}"/>"
		   class="button_medium medium_back"
		   onClick="goBackSmart('${navigationURL}', '${defaultURL}');"/>
	<input type="button" name="resend_email_button" id="resend_email_button"
		   value="<fmt:message key="resend" bundle="${resword}"/>"
		   class="button_medium"
		   onClick="resendEmail(${logEntry.id}, ${sentFromAdminEmail})"/>
</div>

<jsp:include page="../include/footer.jsp" />
