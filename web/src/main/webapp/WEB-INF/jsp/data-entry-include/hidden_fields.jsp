<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:if test="${justCloseWindow}"><input type="hidden" name="cw" value="1" /></c:if>
<input type="hidden" name="eventCRFId" value="<c:out value="${section.eventCRF.id}"/>" />
<input type="hidden" name="sectionId" value="<c:out value="${section.section.id}"/>" />
<input type="hidden" name="checkInputs" value="<c:out value="${checkInputsValue}"/>" />
<input type="hidden" name="tabId" value="<c:out value="${tabId}"/>" />
<input type="hidden" name="occurenceNumber" value="<c:out value="${studyEvent.sampleOrdinal}"/>" />
<input id="formFirstField" type="hidden" name="formFirstField" value="${requestScope['formFirstField']}" />
<input id="hasPopUp" type="hidden" name="hasPopUp" value="${requestScope['hasPopUp']}" />
<input type="hidden" name="currentUserRole" value="<c:out value="${userRole.role.name}"/>" />
<%-- We have to feed this value to the method giveFirstElementFocus()--%>
<input type="hidden" name="crfVersionId" value="<c:out value="${section.eventCRF.CRFVersionId}"/>" />
<c:if test="${dataEntryStage.isInitialDE()}">
<input type="hidden" name="isFirstTimeOnSection" value="${section.section.id}" />
</c:if>
<input type="hidden" id="currentUser" value="${userBean.name}">
<!-- For randomization -->
<input type="hidden" name="studySubjectId" value="${studySubject.id}">
<input type="hidden" name="crfId" value="<c:out value="${section.crf.id}"/>" />
<input type="hidden" name="assignRandomizationResultTo" value="${study.studyParameterConfig.assignRandomizationResultTo}"/>
<input type="hidden" name="randomizationMessage" value="<fmt:message key="randomization_successful_message" bundle="${restext}"/>"/>
<input type="hidden" name="randomizationEnabled" value="${study.studyParameterConfig.randomization}"/>
<c:choose>
	<c:when test="${assignRandomizationResultTo eq 'ssid'}">
		<input type="hidden" name="subjectLabel" value="${subject.uniqueIdentifier}" />
		<input type="hidden" name="personIdMissing" value="<fmt:message key="person_id_should_be_specifyed" bundle="${restext}"/>"/>
	</c:when>
	<c:otherwise>
		<input type="hidden" name="subjectLabel" value="${studySubject.label}" />
	</c:otherwise>
</c:choose>

<c:if test="${study.parentStudyId > 0}">
	<!-- Site information -->
	<input type="hidden" name="siteUniqId" value="${study.identifier}"/>
	<input type="hidden" name="siteName" value="${study.name}"/>
	<input type="hidden" name="sitePI" value="${study.principalInvestigator}"/>
	<!-- /Site information -->
	<!-- Facility information -->
	<input type="hidden" name="facilityName" value="${study.facilityName}"/>
	<input type="hidden" name="facilityCity" value="${study.facilityCity}"/>
	<input type="hidden" name="facilityState" value="${study.facilityState}"/>
	<input type="hidden" name="facilityZip" value="${study.facilityZip}"/>
	<input type="hidden" name="facilityContactPhone" value="${study.facilityContactPhone}"/>
	<input type="hidden" name="facilityCountry" value="${study.facilityCountry}"/>
	<input type="hidden" name="facilityContactEmail" value="${study.facilityContactEmail}"/>
	<!-- /Facility information -->
	<!-- Interviewer information -->
	<input type="hidden" name="userName" value="${userBean.firstName}"/>
	<input type="hidden" name="userLastName" value="${userBean.lastName}"/>
	<input type="hidden" name="userEmail" value="${userBean.email}"/>
	<input type="hidden" name="userPhone" value="${userBean.phone}"/>
	<!-- /Interviewer information -->
	<!-- Randomization statistics for current site.
	This section provides info on number of study subjects in a site,
	already assigned to each of active dynamic group classes-->
	<c:forEach var="counter" items="${subjectsNumberAssignedToEachDynamicGroupMap}" varStatus="status">
		<input type="hidden" id="dynamicGroupClass${status.index + 1}" group_class_name="${counter.key}"
			   subjects_counter="${counter.value}"/>
	</c:forEach>
	<!-- /Randomization statistics for current site -->
</c:if>
<!-- Messages -->
<input type="hidden" name="randDataEntryStepMessage" value='<fmt:message key="randomization_des_message" bundle="${restext}"/>'/>
<!-- /Messages -->
