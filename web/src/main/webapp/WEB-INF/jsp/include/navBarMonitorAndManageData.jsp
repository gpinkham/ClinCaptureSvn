<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="urlPrefix" value=""/>
<c:set var="requestFromSpringController" value="${param.isSpringController}" />
<c:set var="requestFromDoubleSpringController" value="${param.isDoubleSpringController}" />

<c:if test="${requestFromSpringController == 'true' }">
    <c:set var="urlPrefix" value="../"/>
</c:if>

<c:if test="${requestFromDoubleSpringController == 'true' }">
    <c:set var="urlPrefix" value="../../"/>
</c:if>

<c:if test="${!userRole.studyCoder and !userRole.studyMonitor and !userRole.studyEvaluator and !userRole.siteMonitor}">
    <br clear="all">
</c:if>
<div class="taskGroup"><fmt:message key="nav_monitor_and_manage_data" bundle="${resword}"/></div>
<c:choose>
    <c:when test="${userRole.studyEvaluator}">
        <c:choose>
            <c:when test="${evaluationEnabled eq true}">
                <div class="taskLeftColumn">
                        <div class="taskLink"><a href="${urlPrefix}pages/crfEvaluation"><fmt:message key="evaluated_crf" bundle="${resword}"/></a></div>
                </div>
                <c:if test="${study.studyParameterConfig.discrepancyManagement == 'true'}">
                    <div class="taskRightColumn">
                        <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <c:if test="${study.studyParameterConfig.discrepancyManagement == 'true'}">
                    <div class="taskLeftColumn">
                        <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${userRole.studyCoder}">
        <c:choose>
            <c:when test="${study.studyParameterConfig.allowCodingVerification eq 'yes'}">
                <div class="taskLeftColumn">
                    <div class="taskLink"><a href="${urlPrefix}pages/codedItems"><fmt:message key="code" bundle="${resword}"/></a></div>
                </div>
                <div class="taskRightColumn">
                    <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${userRole.investigator || userRole.clinicalResearchCoordinator}">
        <div class="taskLeftColumn">
            <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
            <c:if test="${includeReporting}">
                <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
            </c:if>
        </div>
        <div class="taskRightColumn">
            <c:choose>
                <c:when test="${includeReporting}">
                    <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
                </c:when>
                <c:otherwise>
                    <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:when test="${userRole.studyMonitor}">
        <div class="taskLeftColumn">
            <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
        </div>
        <div class="taskRightColumn">
            <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
            <c:if test="${includeReporting}">
                <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
            </c:if>
        </div>
    </c:when>
    <c:when test="${userRole.siteMonitor}">
        <div class="taskLeftColumn">
            <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
        </div>
        <div class="taskRightColumn">
            <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
            <c:if test="${includeReporting}">
                <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
            </c:if>
        </div>
    </c:when>
    <c:otherwise>
        <c:set var="showCrfEvaluation" value="${true}"/>
        <c:set var="countOfElementsOnTheRightSide" value="${(includeReporting ? 1 : 0) + (study.parentStudyId > 0 ? 0 : (study.studyParameterConfig.allowCodingVerification eq 'yes' ? 2 : 1))}"/>
        <div class="taskLeftColumn">
            <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
            <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
            <c:if test="${study.parentStudyId == 0}">
                <div class="taskLink"><a href="${urlPrefix}ViewRuleAssignment"><fmt:message key="nav_rules" bundle="${resword}"/></a></div>
                <c:if test="${countOfElementsOnTheRightSide >= 3}">
                    <c:set var="showCrfEvaluation" value="${false}"/>
                    <div class="taskLink"><a href="${urlPrefix}ListSubjectGroupClass?read=true"><fmt:message key="nav_groups" bundle="${resword}"/></a></div>
                </c:if>
            </c:if>
            <c:if test="${showCrfEvaluation && (userBean.name == 'root' || userRole.studyAdministrator) && evaluationEnabled == true}">
                <div class="taskLink"><a href="${urlPrefix}pages/crfEvaluation"><fmt:message key="evaluated_crf" bundle="${resword}"/></a></div>
            </c:if>
        </div>
        <div class="taskRightColumn">
            <c:if test="${!showCrfEvaluation && (userBean.name == 'root' || userRole.studyAdministrator) && evaluationEnabled == true}">
                <div class="taskLink"><a href="${urlPrefix}pages/crfEvaluation"><fmt:message key="evaluated_crf" bundle="${resword}"/></a></div>
            </c:if>
            <c:if test="${study.parentStudyId == 0}">
                <c:if test="${countOfElementsOnTheRightSide < 3}">
                    <div class="taskLink"><a href="${urlPrefix}ListSubjectGroupClass?read=true"><fmt:message key="nav_groups" bundle="${resword}"/></a></div>
                </c:if>
                <c:if test="${!userRole.investigator and !userRole.clinicalResearchCoordinator}">
                    <div class="taskLink"><a href="${urlPrefix}ListCRF"><fmt:message key="nav_crfs" bundle="${resword}"/></a></div>
                </c:if>
            </c:if>
            <c:if test="${study.studyParameterConfig.allowCodingVerification eq 'yes'}">
                <div class="taskLink"><a href="${urlPrefix}pages/codedItems"><fmt:message key="code" bundle="${resword}"/></a></div>
            </c:if>
            <c:if test="${includeReporting}">
                <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>