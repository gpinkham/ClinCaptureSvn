<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tr>
	<c:choose>
		<c:when test="${!statusShown }">
			<td class="table_cell_br"><b><fmt:message key="status"
					bundle="${resword}" /></b></td>
			<td class="table_cell_br"><c:out value="${studySub.status.name}" /></td>
			<c:set var="statusShown" value="${true}" />
		</c:when>
		<c:when test="${genderShow && !genderShown}">
			<c:set var="headerClass" value="table_header_column" />
			<%@include file="showGenderCells.jsp"%>
			<c:set var="genderShown" value="${true}" />
		</c:when>
		<c:when test="${enrollmentDateShow && !enrollmentDateShown}">
			<c:set var="headerClass" value="table_header_column" />
			<%@include file="showEnrollmentDateCells.jsp"%>
			<c:set var="enrollmentDateShown" value="${true}" />
		</c:when>
		<c:otherwise>
			<td class="table_header_column">&nbsp;</td>
			<td class="table_cell">&nbsp;</td>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${!statusShown }">
			<td class="table_cell_br"><b><fmt:message key="status"
					bundle="${resword}" /></b></td>
			<td class="table_cell_br"><c:out value="${studySub.status.name}" /></td>
			<c:set var="statusShown" value="${true}" />
		</c:when>
		<c:when test="${genderShow && !genderShown}">
			<c:set var="headerClass" value="${tableHeaderRow}" />
			<%@include file="showGenderCells.jsp"%>
			<c:set var="genderShown" value="${true}" />
		</c:when>
		<c:when test="${enrollmentDateShow && !enrollmentDateShown}">
			<c:set var="headerClass" value="${tableHeaderRow}" />
			<%@include file="showEnrollmentDateCells.jsp"%>
			<c:set var="enrollmentDateShown" value="${true}" />
		</c:when>
		<c:otherwise>
			<td class="${tableHeaderRow}">&nbsp;</td>
			<td class="table_cell">&nbsp;</td>
		</c:otherwise>
	</c:choose>
</tr>
