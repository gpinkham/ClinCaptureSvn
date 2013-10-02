<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${displayItemBean ne null && displayItemBean.dbData ne null}">
    <c:set var="rowCount" value=""/>
    <c:if test="${displayItemBean.dbData.ordinal >= 0}">
        <c:set var="rowCount" value="${displayItemBean.dbData.ordinal}"/>
    </c:if>
    <c:if test="${displayItemBean.firstNewDn}"><a id="firstNewDn" rel="${displayItemBean.dbData.itemId}" alt="${rowCount}"></a></c:if>
    <c:if test="${displayItemBean.firstUpdatedDn}"><a id="firstUpdatedDn" rel="${displayItemBean.dbData.itemId}" alt="${rowCount}"></a></c:if>
    <c:if test="${displayItemBean.firstResolutionProposed}"><a id="firstResolutionProposed" rel="${displayItemBean.dbData.itemId}" alt="${rowCount}"></a></c:if>
    <c:if test="${displayItemBean.firstClosedDn}"><a id="firstClosedDn" rel="${displayItemBean.dbData.itemId}" alt="${rowCount}"></a></c:if>
    <c:if test="${displayItemBean.firstAnnotation}"><a id="firstAnnotation" rel="${displayItemBean.dbData.itemId}" alt="${rowCount}"></a></c:if>
</c:if>
