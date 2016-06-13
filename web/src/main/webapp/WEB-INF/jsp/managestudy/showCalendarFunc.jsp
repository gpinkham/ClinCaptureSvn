<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<html>
<head>
    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>	<title>
	<fmt:message key="calendared_events_parametrs" bundle="${resword}"/> : <c:out value="${studyName}"/>
	</title>
    <ui:theme/>
</head>
<body>
<h1>
	<span class="first_level_header">		<fmt:message key="calendared_events_parametrs" bundle="${resword}"/>: <c:out value="${studyName}"/>
	</span>
</h1>
<table border="0" cellpadding="0" cellspacing="0" width="700" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
     <tr>
        <td class="table_header_column_top" style="color: #789EC5" align="center" width="150px"><b><fmt:message key="event_name" bundle="${resword}"/></b></td>
		<td class="table_header_column_top" style="color: #789EC5" align="center" width="100px"><b><fmt:message key="event_type" bundle="${resword}"/></b></td>		<td class="table_header_column_top" style="color: #789EC5" align="center" width="50px"><b><fmt:message key="reference" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5" align="center" width="100px"><b><fmt:message key="day_min" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5" align="center" width="100px"><b><fmt:message key="day_max" bundle="${resword}"/></b></td>
        <td class="table_header_column_top" style="color: #789EC5" align="center" width="100px"><b><fmt:message key="day_schedule" bundle="${resword}"/></b></td>        <td class="table_header_column_top" style="color: #789EC5" align="center" width="100px"><b><fmt:message key="day_email" bundle="${resword}"/></b></td>
    </tr>
	<c:forEach var="event" items="${requestScope['events']}">
      <tr>            <td class="table_header_column"> <c:out value="${event.name}"/></td>
			<td class="table_header_column">
			 <c:choose>
			 <c:when test="${event.type == 'calendared_visit'}">			 	<fmt:message key="calendared_visit" bundle="${resword}"/>
			 </c:when>
			 <c:when test="${event.type == 'unscheduled'}">
			 	<fmt:message key="unscheduled" bundle="${resword}"/>			 </c:when>
			 <c:when test="${event.type == 'common'}">
			 	<fmt:message key="common" bundle="${resword}"/>
			 </c:when>			 <c:otherwise>
			 	<fmt:message key="scheduled" bundle="${resword}"/>
			 </c:otherwise>
			 </c:choose>
			</td>
			<td class="table_header_column" align="center"> 
			 <c:choose>
				<c:when test="${event.referenceVisit == 'true'}">
					<fmt:message key="yes" bundle="${resword}"/>
				</c:when>
				<c:when test="${event.referenceVisit == 'false' && event.type == 'calendared_visit'}">
					<fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			 </c:choose>
			</td>
			<td class="table_header_column" align="center">
			  <c:choose>
				<c:when test="${event.referenceVisit == 'false' && event.type == 'calendared_visit'}">
					<c:out value="${event.minDay}"/>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			  </c:choose>
			</td>
			<td class="table_header_column" align="center">
			  <c:choose>
				<c:when test="${event.referenceVisit == 'false' && event.type == 'calendared_visit'}">
					<c:out value="${event.maxDay}"/>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			  </c:choose>
			</td>
			<td class="table_header_column" align="center">
			  <c:choose>
				<c:when test="${event.referenceVisit == 'false' && event.type == 'calendared_visit'}">
					<c:out value="${event.scheduleDay}"/>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			  </c:choose>
			</td>
			<td class="table_header_column" align="center">
			  <c:choose>
				<c:when test="${event.referenceVisit == 'false' && event.type == 'calendared_visit'}">
					<c:out value="${event.emailDay}"/>
				</c:when>
				<c:otherwise>
					&nbsp;
				</c:otherwise>
			  </c:choose>
			</td>
      </tr>
    </c:forEach>
</table>
<br>
&nbsp<input id="CloaseViewStudySubjectAuditWindow" class="button_medium" type="submit" onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>" name="BTN_Close_Window"/>
</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>