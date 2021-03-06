<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>

<jsp:useBean scope="request" id="itemAudits" class="java.util.ArrayList"/>

<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<html>
<head>
    <link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
    	
</head>

	<body>
	<c:choose>
	<c:when test="${fn:length(itemAudits)>0}">
	<table border="0"><tr><td width="20">&nbsp;</td><td>
	
		<table border="0" cellpadding="0" cellspacing="0" width="550" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
		  <tr>
		      <td class="table_cell"><b><fmt:message key="audit_event" bundle="${resword}"/></b></td>
		      <td class="table_cell"><b><fmt:message key="local_date_time" bundle="${resword}"/></b></td>
		      <td class="table_cell"><b><fmt:message key="user" bundle="${resword}"/></b></td>
		      <td class="table_cell"><b><fmt:message key="value_type" bundle="${resword}"/></b></td>
		      <td class="table_cell"><b><fmt:message key="old" bundle="${resword}"/></b></td>
		      <td class="table_cell"><b><fmt:message key="new" bundle="${resword}"/></b></td>
		  </tr>
		  <c:forEach var="audit" items="${itemAudits}">
		  <tr>
		      <td class="table_cell"><c:out value="${audit.auditEventTypeName}"/>&nbsp;</td>
		      <td class="table_cell"><cc-fmt:formatDate value="${audit.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;</td>
		      <td class="table_cell"><c:out value="${audit.userName}"/>&nbsp;</td>
		      <td class="table_cell"><c:out value="${audit.entityName}"/><c:if test="${itemDataOrdinal ne null}">(#${itemDataOrdinal})</c:if>&nbsp;</td>
		      <td class="table_cell"><c:out value="${audit.oldValue}"/>&nbsp;</td>
		      <td class="table_cell"><c:out value="${audit.newValue}"/>&nbsp;</td>
		  </tr>
		  </c:forEach>
		  </table>
	  </td>
	  </tr>
	  </table>
	  <tr>
	  	<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<fmt:message key="item_initially_entered_on" bundle="${respage}"/> <c:out value="${param.entityCreatedDate}"/>.)</p>
	  </tr>
	  </c:when>
	  <c:otherwise>
	  <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<fmt:message key="no_changed_made_since_item_initially_entered_on" bundle="${respage}"/> <c:out value="${param.entityCreatedDate}"/>.)</p>
	  </c:otherwise>
	  </c:choose>
	
	</body>
</html>
