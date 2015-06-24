<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.audit_events" var="resaudit"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.AuditEventRow" />
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>

<tr valign="top">        
      <td class="table_cell_left">
		  <cc-fmt:formatDate value="${currRow.bean.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
      <td class="table_cell">
	  <c:choose>
	  <c:when test="${currRow.bean.actionMessage == '__job_fired_success' || currRow.bean.actionMessage == '__job_fired_fail'}">
		<fmt:message key="${currRow.bean.actionMessage}" bundle="${resaudit}" />&nbsp;
	  </c:when>
	  <c:otherwise>
		<c:out value="${currRow.bean.actionMessage}" escapeXml="false" />&nbsp;
	  </c:otherwise>
	  </c:choose>
	  </td>
      <td class="table_cell"><c:out value="${currRow.bean.auditTable}"/>&nbsp;</td>

      <td class="table_cell">
	  <c:choose>
	  <c:when test="${currRow.bean.reasonForChange == '__job_fired_success' || currRow.bean.reasonForChange == '__job_fired_fail'}">
		<fmt:message key="${currRow.bean.reasonForChange}" bundle="${resaudit}"/>&nbsp;
	  </c:when>
	  <c:otherwise><c:out value="${currRow.bean.reasonForChange}"/>&nbsp;</c:otherwise>
	  </c:choose>
      </td>
      <td class="table_cell"><!-- actions go here. -->
		  <c:import url="../include/showAuditEntityLink.jsp">
			<c:param name="auditTable" value="${currRow.bean.auditTable}" />
			<c:param name="entityId" value="${currRow.bean.entityId}" />
			<c:param name="rolename" value="${userRole.role.name}"/>
		</c:import>
      </td>    
</tr> 
  
