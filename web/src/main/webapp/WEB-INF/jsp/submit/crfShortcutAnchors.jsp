<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="itemId" value="${param.itemId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${param.inputName}" />
<c:set var="di" value="${inputName == 'interviewer' ? crfShortcutsAnalyzer.interviewerDisplayItemBean : (inputName == 'interviewDate' ? crfShortcutsAnalyzer.interviewDateDisplayItemBean : displayItem)}" />

<c:set var="crfShortcutAnchorsId" value="${inputName == 'interviewer' || inputName == 'interviewDate' ? inputName : displayItem.item.id}"/>

<div id="crfShortcutAnchors_${rowCount}item_${crfShortcutAnchorsId}" field="${inputName}" class="hidden">
    <c:forEach items="${di.newDn}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    <c:forEach items="${di.updatedDn}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    <c:forEach items="${di.resolutionProposedDn}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    <c:forEach items="${di.closedDn}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    <c:forEach items="${di.annotationDn}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    <c:if test="${di.data ne null && di.metadata ne null && di.data.id > 0 && section.eventCRF.stage.doubleDE_Complete && di.metadata.showItem && di.metadata.sdvRequired && (userRole.studyAdministrator || userRole.monitor)}">
        <c:forEach items="${di.itemToSDV}" var="value"><a id="${value}" rel="${itemId}" alt="${rowCount}"></a></c:forEach>
    </c:if>
</div>

