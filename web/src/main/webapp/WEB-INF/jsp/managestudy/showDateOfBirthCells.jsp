<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<c:choose>
	<c:when test="${subjectStudy.studyParameterConfig.collectDob == '1'}">
		<td class="${headerClass }"><fmt:message key="date_of_birth"
				bundle="${resword}" /> <%-- DN for DOB goes here --%> <c:if
				test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
				<c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}" />
				<c:choose>
					<c:when test="${hasDOBNote eq 'yes'}">
						<a href="#"
							onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob"
							src="${dOBNote.resStatus.iconFilePath}" border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
						</a>
					</c:when>
					<c:otherwise>
						<a href="#"
							onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif"
							border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden"
							value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						</a>
					</c:otherwise>
				</c:choose>
			</c:if></td>
		<td class="table_cell"><fmt:formatDate
				value="${subject.dateOfBirth}" pattern="${dteFormat}" /></td>
	</c:when>
	<c:when test="${subjectStudy.studyParameterConfig.collectDob == '3'}">
		<td class="${headerClass }"><fmt:message key="date_of_birth"
				bundle="${resword}" /> <%-- DN for DOB goes here --%> <c:if
				test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
				<c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}" />
				<c:choose>
					<c:when test="${hasDOBNote eq 'yes'}">
						<a href="#"
							onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob"
							src="${dOBNote.resStatus.iconFilePath}" border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
						</a>
					</c:when>
					<c:otherwise>
						<a href="#"
							onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif"
							border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden"
							value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						</a>
					</c:otherwise>
				</c:choose>
			</c:if></td>
		<td class="table_cell"><fmt:message key="not_used"
				bundle="${resword}" /></td>
	</c:when>
	<c:otherwise>
		<td class="${headerClass }"><fmt:message key="year_of_birth"
				bundle="${resword}" /> <%-- DN for DOB goes here --%> <c:if
				test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
				<c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}" />
				<c:choose>
					<c:when test="${hasDOBNote eq 'yes'}">
						<a href="#"
							onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob"
							src="${dOBNote.resStatus.iconFilePath}" border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
						</a>
					</c:when>
					<c:otherwise>
						<a href="#"
							onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
							<img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif"
							border="0"
							alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
							title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden"
							value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						</a>
					</c:otherwise>
				</c:choose>
			</c:if></td>
		<td class="table_cell"><c:out value="${yearOfBirth}" /></td>
	</c:otherwise>
</c:choose>
