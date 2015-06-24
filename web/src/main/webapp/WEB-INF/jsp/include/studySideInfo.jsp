<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:choose>
    <c:when test="${study.status.name != 'removed' && study.status.name != 'auto-removed'}">
    <c:url var="viewStudy" value="/ViewStudy"/>
	<c:url var="viewSite" value="/ViewSite"/>
    <c:choose>
    <c:when test="${study.parentStudyId>0}">
    <b><fmt:message key="study" bundle="${resword}"/>:</b>&nbsp;
     <a href="${viewStudy}?id=<c:out value="${study.parentStudyId}"/>&viewFull=yes"><c:out value="${study.parentStudyName}"/></a>
     <br><br>
    <b><fmt:message key="site" bundle="${resword}"/>:</b>&nbsp;
     <a href="${viewSite}?id=<c:out value="${study.id}"/>">
    </c:when>
    <c:otherwise>
    <b><fmt:message key="study" bundle="${resword}"/>:</b>&nbsp;
     <a href="${viewStudy}?id=<c:out value="${study.id}"/>&viewFull=yes">
    </c:otherwise>
    </c:choose>
    <c:out value="${study.name}"/></a>

    <br><br>

    <fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
    <c:if test="${study ne null}">
        <c:set var="studySubjectIDLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
    </c:if>
    <c:if test="${studySubject != null}">
    <b><a href="ViewStudySubject?id=<c:out value="${studySubject.id}"/>">${studySubjectIDLabel}</a>:</b>&nbsp; <c:out value="${studySubject.label}"/>

    <br><br>
    </c:if>

    <b><fmt:message key="start_date" bundle="${resword}"/>:</b>&nbsp;
     <c:choose>
      <c:when test="${study.datePlannedStart != null}">
       <cc-fmt:formatDate value="${study.datePlannedStart}" dateTimeZone="${userBean.userTimeZoneId}" />
      </c:when>
      <c:otherwise>
       <fmt:message key="na" bundle="${resword}"/>
     </c:otherwise>
     </c:choose>
    <br><br>

    <b><fmt:message key="end_date" bundle="${resword}"/>:</b>&nbsp; 
    <c:choose>
      <c:when test="${study.datePlannedEnd != null}">
       <cc-fmt:formatDate value="${study.datePlannedEnd}" dateTimeZone="${userBean.userTimeZoneId}"/>
      </c:when>
      <c:otherwise>
       <fmt:message key="na" bundle="${resword}"/>
      </c:otherwise>
    </c:choose>
    <br><br>

    <b><fmt:message key="pi" bundle="${resword}"/>:</b>&nbsp; <c:out value="${study.principalInvestigator}"/>

    <br><br>

    <b><fmt:message key="protocol_verification" bundle="${resword}"/>:</b>&nbsp; 
    <cc-fmt:formatDate value="${study.protocolDateVerification}" dateTimeZone="${userBean.userTimeZoneId}"/>
    

    </c:when>
   <c:otherwise>
   Your last active study/site was <c:out value="${study.name}"/>, but it has been deleted.
   </c:otherwise>
   </c:choose>