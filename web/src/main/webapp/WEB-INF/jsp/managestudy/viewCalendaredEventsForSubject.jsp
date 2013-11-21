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
    <style media="screen" type="text/css">
        .table_header_row, .table_header_row_left {
            text-align: center !important;
            border-width: 1 1 0px 0px !important;
            padding: 3px !important;
            border-color: #CCCCCC
        }
    </style>
</head>
<body>
<h1>
	<span class="first_level_header">
		<fmt:message key="calendared_events_parametrs" bundle="${resword}"/> : <c:out value="${subjectLabel}"/>
	</span>
</h1>
<table style="border:none">
    <tr><td class="table_header_column_top" align="right"><fmt:message key="current_date" bundle="${resword}"/>:</td>
        <td class="table_header_column_top" align="left"><fmt:formatDate value="${currentDate}" dateStyle="medium"/></td>
    </tr>
</table>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="viewCalendaredEventsForSubjectRow.jsp" /></c:import>

&nbsp<input id="CloaseViewStudySubjectAuditWindow" class="button_medium" type="submit" onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>" name="BTN_Close_Window"/>

<jsp:include page="../include/changeTheme.jsp"/>
</body>
</html>