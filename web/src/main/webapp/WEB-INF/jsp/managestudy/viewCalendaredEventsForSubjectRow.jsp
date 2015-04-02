<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.CalendarEventRow" />
<tr>
    <td class="table_header_column" align="center"><b><c:out value="${currRow.bean.eventName}"/></b></td>
    <c:choose>
        <c:when test="${currRow.bean.referenceVisit == 'Yes'}">
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <c:choose>
                <c:when test="${currRow.bean.referenceVisit == 'Yes'}">
                    <td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
                </c:when>
                <c:otherwise>
                    <td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
                </c:otherwise>
            </c:choose>
            <td class="table_header_column" align="center"><b>–</br></b></td>
        </c:when>
        <c:when test="${currRow.bean.referenceVisit == 'No' && empty currRow.bean.eventsReferenceVisit}">
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <td class="table_header_column" align="center"><b>–</br></b></td>
            <c:choose>
                <c:when test="${currRow.bean.referenceVisit == 'Yes'}">
                    <td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
                </c:when>
                <c:otherwise>
                    <td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
                </c:otherwise>
            </c:choose>
            <td class="table_header_column" align="center"><b>RVs not found</br></b></td>
        </c:when>
        <c:otherwise>
            <td class="table_header_column" align="center" width="180px"><b><fmt:formatDate value="${currRow.bean.dateMin}" dateStyle="medium"/> – <fmt:formatDate value="${currRow.bean.dateMax}" dateStyle="medium"/></b></td>
            <td class="table_header_column" align="center"><b><fmt:formatDate value="${currRow.bean.dateSchedule}" dateStyle="medium"/></br></b></td>
            <td class="table_header_column" align="center"><b><fmt:formatDate value="${currRow.bean.dateEmail}" dateStyle="medium"/></br></b></td>
            <c:choose>
                <c:when test="${currRow.bean.referenceVisit == 'Yes'}">
                    <td class="table_header_column" align="center"><b><fmt:message key="yes" bundle="${resword}"/></br></b></td>
                </c:when>
                <c:otherwise>
                    <td class="table_header_column" align="center"><b><fmt:message key="no" bundle="${resword}"/></br></b></td>
                </c:otherwise>
            </c:choose>
            <td class="table_header_column" align="center"><b><c:out value="${currRow.bean.eventsReferenceVisit}"/></br></b></td>
        </c:otherwise>
    </c:choose>
</tr>