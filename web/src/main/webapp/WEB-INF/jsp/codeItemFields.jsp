<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:forEach items="${codedElement.classificationElement}" var="classElement">
    <tr>
        <td>
            <c:out value="${classElement.elementName}"/>:
        </td>
        <td>
            <c:out value="${classElement.codeName}"/>
        </td>
        <td width=360px colspan="2"></td>
        <td></td>
    </tr>
</c:forEach>
