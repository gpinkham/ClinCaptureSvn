<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="paramPath" value="systemPropertyGroups['${groupStatus.index}']"/>
<c:if test="${subGrp ne null}">
  <c:set var="grp" value="${subGrp}"/>
  <c:set var="paramPath" value="systemPropertyGroups['${groupStatus.index}'].subGroups['${subGroupStatus.index}']"/>
</c:if>

<c:forEach items="${grp.systemProperties}" var="systemProperty" varStatus="spStatus">

  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].id"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].name"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].valueType"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].required"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].orderId"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].type"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].typeValues"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].size"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].showDescription"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].showNote"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].groupId"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].crc"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].investigator"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].monitor"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].admin"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].root"/>
  <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].version"/>

  <c:if test="${systemProperty.showDescription}">
    <div class="propertyDescription">
      <p><fmt:message key="systemProperty.${systemProperty.name}.description" bundle="${resword}"/></p>
    </div>
  </c:if>
  <div class="propertyInputHolder">
    <div class="propertyLabel"><span class="table_title_Admin"><fmt:message
        key="systemProperty.${systemProperty.name}.label" bundle="${resword}"/>:</span></div>
    <div class="propertyInput">
      <c:set var="access" value="READ"/>
      <c:if test="${userRole.role.id eq 1}">
        <c:set var="access" value="${systemProperty.root}"/>
      </c:if>
      <c:if test="${userRole.role.id eq 2}">
        <c:set var="access" value="${systemProperty.admin}"/>
      </c:if>
      <c:if test="${userRole.role.id eq 6}">
        <c:set var="access" value="${systemProperty.monitor}"/>
      </c:if>
      <c:if test="${userRole.role.id eq 4}">
        <c:set var="access" value="${systemProperty.investigator}"/>
      </c:if>
      <c:if test="${userRole.role.id eq 5}">
        <c:set var="access" value="${systemProperty.crc}"/>
      </c:if>
      <c:choose>
        <c:when test="${systemProperty.type eq 'DYNAMIC_INPUT'}">
          <form:hidden size="${systemProperty.size}" path="${paramPath}.systemProperties['${spStatus.index}'].value"/>
          <input type="text" size="${systemProperty.size}" value="${systemProperty.value}" class="formfieldXL"
                 disabled="true"/>
        </c:when>
        <c:when test="${systemProperty.type eq 'PASSWORD'}">
          <form:password size="${systemProperty.size}" value="${systemProperty.value}"
                         path="${paramPath}.systemProperties['${spStatus.index}'].value" class="formfieldXL"
                         cssErrorClass="inputError" disabled="${access ne 'WRITE' ? 'true' : 'false'}"/>
        </c:when>
        <c:when test="${systemProperty.type eq 'TEXT'}">
          <form:input size="${systemProperty.size}" value="${systemProperty.value}"
                      path="${paramPath}.systemProperties['${spStatus.index}'].value" class="formfieldXL"
                      cssErrorClass="inputError" disabled="${access ne 'WRITE' ? 'true' : 'false'}"/>
        </c:when>
        <c:when test="${systemProperty.type eq 'RADIO' or systemProperty.type eq 'DYNAMIC_RADIO'}">
          <c:forEach items="${fn:split(systemProperty.typeValues, ',')}" var="propertyValue">
            <input type="radio" ${systemProperty.value eq propertyValue ? "checked" : ""} value="${propertyValue}"
                   name="${paramPath}.systemProperties['${spStatus.index}'].value"
                   class="formfieldXL" ${access ne 'WRITE' or systemProperty.type eq 'DYNAMIC_RADIO' ? 'disabled="true"' : ''}/>

            <div class="radioLabel"><fmt:message key="systemProperty.${systemProperty.name}.${propertyValue}.radioLabel"
                                                 bundle="${resword}"/></div>
          </c:forEach>
        </c:when>
        <c:when test="${systemProperty.type eq 'FILE'}">
          <form:hidden path="${paramPath}.systemProperties['${spStatus.index}'].value"/>
          <input type="file" name="logoFile" id="logoFile" ${access ne 'WRITE' ? 'disabled="true"' : ''}/>
          <br/><img src="<c:url value='${systemProperty.value}'/>"/>
        </c:when>
        <c:when test="${systemProperty.type eq 'COMBOBOX'}">
            <form:select multiple="false" style="width: ${systemProperty.size}px;" path="${paramPath}.systemProperties['${spStatus.index}'].value" class="formfieldXL" cssErrorClass="inputError" disabled="${access ne 'WRITE' ? 'true' : 'false'}">
                <c:forEach items="${fn:split(systemProperty.typeValues, ',')}" var="itemValue">
                    <spring:message code="${systemProperty.name}.${itemValue}.translation" var="itemLabel" text="${itemValue}"/>
                    <option value="${itemValue}" ${itemValue == systemProperty.value ? "selected" : ""}>${itemLabel}</option>
                </c:forEach>
            </form:select>
        </c:when>
      </c:choose>
    </div>
    <c:if test="${systemProperty.required and access eq 'WRITE' and systemProperty.type ne 'DYNAMIC_INPUT' and systemProperty.type ne 'DYNAMIC_RADIO'}">
      <div class="propertyRequired">*</div>
    </c:if>
    <c:if test="${systemProperty.showMeasurements}">
      <div class="propertyMeasurements"><fmt:message key="systemProperty.${systemProperty.name}.measurements"
                                                     bundle="${resword}"/></div>
    </c:if>
  </div>
  <br class="clear"/>

</c:forEach>
