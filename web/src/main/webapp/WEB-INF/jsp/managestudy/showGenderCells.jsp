<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<td class="${headerClass }">${genderLabel}
    <%-- DN for Gender goes here --%>
    <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
        <c:set var="isNew" value="${hasGenderNote eq 'yes' ? 0 : 1}"/>
        <c:choose>
            <c:when test="${hasGenderNote eq 'yes'}">
                <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=gender&column=gender','spanAlert-gender', '', event); return false;">
                    <img id="flag_gender" name="flag_gender" src="${genderNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                </a>
            </c:when>
            <c:otherwise>
                <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=${subject.id}&writeToDB=1&name=subject&field=gender&column=gender','spanAlert-gender', '', event); return false;">
                    <img id="flag_gender" name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                    <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=gender&column=gender">
                </a>
            </c:otherwise>
        </c:choose>
    </c:if>
</td>
<td class="table_cell">
    <c:choose>
        <c:when test="${subject.gender==32}">
            &nbsp;
        </c:when>
        <c:when test="${subject.gender==109 ||subject.gender==77}">
            <fmt:message key="male" bundle="${resword}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="female" bundle="${resword}"/>
        </c:otherwise>
    </c:choose>
</td>