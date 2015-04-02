<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="exceptions"/>

<jsp:useBean scope='request' id='pageMessages' class='java.util.ArrayList'/>
<%--<jsp:useBean scope='request' id='message' class='java.lang.String'/>--%>

<c:if test="${crfAutoUploadMode && !empty pageMessages}">
<!-- <import-error>
<c:forEach var="message" items="${pageMessages}">
    ${message}<br/>
</c:forEach>
</import-error> -->
</c:if>

<c:if test="${!empty pageMessages}">
<div class="alert">    
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/> 
 <br><br>
</c:forEach>
</div>
</c:if>

<c:if test="${param.message == 'authentication_failed'}">
<div class="alert">
    <fmt:message key="no_have_correct_privilege_current_study" bundle="${resmessages}"/>
    <fmt:message key="change_study_contact_sysadmin" bundle="${resmessages}"/>
 <br><br>
</div>
</c:if>

<c:if test="${param.message == 'system_no_permission'}">
  <div class="alert">
    <fmt:message key="system_no_permission" bundle="${exceptions}"/>
    <br><br>
  </div>
</c:if>
