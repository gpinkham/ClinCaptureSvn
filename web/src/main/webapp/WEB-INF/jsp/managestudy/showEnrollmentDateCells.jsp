<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<td class="${headerClass }">${enrollmentDateLabel}
        &nbsp;
            <%-- DN for enrollment date goes here --%>
    <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
        <c:set var="isNew" value="${hasEnrollmentNote eq 'yes' ? 0 : 1}"/>
        <c:choose>
            <c:when test="${hasEnrollmentNote eq 'yes'}">
                <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate', '', event); return false;">
                    <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="${enrollmentNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                </a>
            </c:when>
            <c:otherwise>
                <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${studySub.id}&id=${studySub.id}&writeToDB=1&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate', '', event); return false;">
                    <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                    <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date">
                </a>
            </c:otherwise>
        </c:choose>
    </c:if>
</td>
<td class="table_cell">
	<fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}"/>&nbsp;
</td>