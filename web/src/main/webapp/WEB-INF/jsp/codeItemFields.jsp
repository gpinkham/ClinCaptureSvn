<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:forEach items="${codedElement.classificationElement}" var="classElement">
    <tr>
        <td id="<c:out value="${classElement.elementName}"/>">
            <c:set var="classElementName"><c:out value="${fn:toLowerCase(classElement.elementName)}"/></c:set>
            <fmt:message key='${classElementName}' bundle="${resword}"/>:
        </td>
        <td>
            <c:out value="${classElement.codeName}"/>
        </td>
        <td width=360px colspan="2"></td>
        <td></td>
    </tr>
</c:forEach>
