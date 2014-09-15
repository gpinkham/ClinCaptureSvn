<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="urlPrefix" value=""/>
<c:set var="requestFromSpringController" value="${param.isSpringController}" />
<c:set var="requestFromDoubleSpringController" value="${param.isDoubleSpringController}" />

<c:if test="${requestFromSpringController == 'true' }">
    <c:set var="urlPrefix" value="../"/>
</c:if>

<c:if test="${requestFromDoubleSpringController == 'true' }">
    <c:set var="urlPrefix" value="../../"/>
</c:if>

<br clear="all">
<div class="taskGroup"><fmt:message key="nav_study_setup" bundle="${resword}"/></div>
<div class="taskLeftColumn">
    <div class="taskLink"><a href="${urlPrefix}ViewStudy?id=${study.id}&viewFull=yes"><fmt:message key="nav_view_study" bundle="${resword}"/></a></div>
    <c:if test="${userBean.name == 'root' or userRole.studyAdministrator}">
        <c:choose>
            <c:when test="${study.parentStudyId > 0}">
            </c:when>
            <c:otherwise>
                <div class="taskLink"><a href="${urlPrefix}pages/studymodule"><fmt:message key="nav_build_study" bundle="${resword}"/></a></div>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>
<div class="taskRightColumn">
    <div class="taskLink"><a href="${urlPrefix}ListStudyUser"><fmt:message key="assign_users" bundle="${resword}"/></a></div>
    <c:if test="${userBean.name == 'root' or userRole.studyAdministrator}">
        <div class="taskLink"><a href="${urlPrefix}pages/system"><fmt:message key="system" bundle="${resword}"/></a></div>
    </c:if>
</div>
