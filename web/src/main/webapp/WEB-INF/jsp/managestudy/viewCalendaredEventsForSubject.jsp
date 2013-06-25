<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<html>
<head>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="stylesheet" href="includes/styles.css" type="text/css">
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
	<c:set var="color" scope="session" value="${newThemeColor}" />
	<c:if test="${(color == 'violet') || (color == 'green')}">
		<script>
			document.write('<style class="hideStuff" ' + 'type="text/css">body {display:none;}<\/style>');
		</script>
	</c:if>
	<title>
	<fmt:message key="calendared_events_parametrs" bundle="${resword}"/> : <c:out value="${subjectLabel}"/>
	</title>
</head>
<body>
<h1><span class="title_manage">
<fmt:message key="calendared_events_parametrs" bundle="${resword}"/> : <c:out value="${subjectLabel}"/>
</span></h1>
<table style="border:none">
<tr><td class="table_header_column_top" align="right"><fmt:message key="current_date" bundle="${resword}"/>:</td>
	<td class="table_header_column_top" align="left"><fmt:formatDate value="${currentDate}" dateStyle="medium"/></td>
</tr>
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="800" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
	<tr>
        <td class="table_header_column_top" align="center"><b><fmt:message key="calendared_event_name" bundle="${resword}"/></b></td>
		<td class="table_header_column_top" align="center"><b><fmt:message key="min_max_date_range" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" align="center"><b><fmt:message key="schedule_date" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" align="center"><b><fmt:message key="user_email_date" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" align="center"><b><fmt:message key="is_reference_event" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" align="center"><b><fmt:message key="reference_visit_for_event" bundle="${resword}"/></b></td>
    </tr>
		<c:forEach var="event" items="${requestScope['events']}">
	<tr>
        <td class="table_header_column" align="center"><b><c:out value="${event.eventName}"/></b></td>
	 <c:choose>
	 <c:when test="${event.referenceVisit == 'true'}">
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<c:choose>
					<c:when test="${event.referenceVisit == 'true'}">
						<td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
					</c:when>
					<c:otherwise>
						<td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
					</c:otherwise>
				</c:choose>
				<td class="table_header_column" align="center"><b>–</br></b></td>
	</c:when>
	<c:when test="${event.referenceVisit == 'false' && empty event.eventsReferenceVisit}">
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<td class="table_header_column" align="center"><b>–</br></b></td>
				<c:choose>
					<c:when test="${event.referenceVisit == 'true'}">
						<td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
					</c:when>
					<c:otherwise>
						<td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
					</c:otherwise>
				</c:choose>
				<td class="table_header_column" align="center"><b>RVs not found</br></b></td>
	</c:when>
	<c:otherwise>
		 <td class="table_header_column" align="center" width="180px"><b><fmt:formatDate value="${event.dateMin}" dateStyle="medium"/> – <fmt:formatDate value="${event.dateMax}" dateStyle="medium"/></b>
		 		
		 </td>
		 <td class="table_header_column" align="center"><b><fmt:formatDate value="${event.dateSchedule}" dateStyle="medium"/></br></b></td>
		 <td class="table_header_column" align="center"><b><fmt:formatDate value="${event.dateEmail}" dateStyle="medium"/></br></b></td>
 		<c:choose>
					<c:when test="${event.referenceVisit == 'true'}">
						<td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
					</c:when>
					<c:otherwise>
						<td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
					</c:otherwise>
				</c:choose>
 		 <td class="table_header_column" align="center"><b><c:out value="${event.eventsReferenceVisit}"/></br></b></td>
	</c:otherwise>
	</c:choose>
	</tr>
	</c:forEach>
</table>
<br>
&nbsp<input id="CloaseViewStudySubjectAuditWindow" class="button_medium" type="submit" onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>" name="BTN_Close_Window"/>

<jsp:include page="../include/changeTheme.jsp"/>
</body>
</html>