<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:if test="${subGrp ne null}">
  <c:set var="grp" value="${subGrp}"/>
</c:if>

<c:choose>
  <c:when test="${fn:length(grp.systemProperties) eq 0}">
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </c:when>
  <c:otherwise>
    <c:forEach items="${grp.systemProperties}" var="systemProperty" varStatus="spStatus">
      <td><fmt:message key="systemProperty.${systemProperty.name}.label" bundle="${resword}"/></td>
      <c:choose>
        <c:when test="${systemProperty.type eq 'PASSWORD'}">
          <td>********************</td>
        </c:when>
        <c:when test="${systemProperty.type eq 'TEXT' || systemProperty.type eq 'DYNAMIC_INPUT'}">
          <td>${systemProperty.value}</td>
        </c:when>
        <c:when test="${systemProperty.type eq 'RADIO' || systemProperty.type eq 'DYNAMIC_RADIO'}">
          <td><fmt:message key="systemProperty.${systemProperty.name}.${systemProperty.value}.radioLabel"
                           bundle="${resword}"/></td>
        </c:when>
        <c:when test="${systemProperty.type eq 'FILE'}">
          <td><img
              src="<c:url value='${systemCommand.newLogoUrl ne null and not empty systemCommand.newLogoUrl ? systemCommand.newLogoUrl : systemProperty.value}'/>"/>
          </td>
        </c:when>
        <c:otherwise>
          <td>&nbsp;</td>
        </c:otherwise>
      </c:choose>
      </tr>
      <c:if test="${spStatus.index < spStatus.count - 1}"><tr></c:if>
    </c:forEach>
  </c:otherwise>
</c:choose>