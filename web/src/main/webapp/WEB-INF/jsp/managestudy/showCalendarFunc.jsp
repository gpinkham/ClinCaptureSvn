<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
</head>

<h1><span class="title_manage">
<fmt:message key="calendared_events_parametrs" bundle="${resword}"/> : <c:out value="${studyName}"/>
<body>
</span></h1>
<table border="0" cellpadding="0" cellspacing="0" width="650" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">

     <tr>
        <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="event_name" bundle="${resword}"/></b></td>
		<td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="event_type" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="day_min" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="day_max" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="day_schedule" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5"><b><fmt:message key="day_email" bundle="${resword}"/></b></td>
    </tr>
	<c:forEach var="event" items="${requestScope['events']}">
      <tr>
            <td class="table_header_column"> <c:out value="${event.name}"/></td>
			<td class="table_header_column"><c:out value="${event.type}"/></td>
			<td class="table_header_column"><c:out value="${event.minDay}"/></td>
			<td class="table_header_column"><c:out value="${event.maxDay}"/></td>
			<td class="table_header_column"><c:out value="${event.scheduleDay}"/></td>
			<td class="table_header_column"><c:out value="${event.emailDay}"/></td>
            </td>
      </tr>
    </c:forEach>
</table>
<br>
&nbsp<input id="CloaseViewStudySubjectAuditWindow" class="button_medium" type="submit" onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>" name="BTN_Close_Window"/>
</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>