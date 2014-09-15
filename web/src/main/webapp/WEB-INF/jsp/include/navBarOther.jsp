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

<div class="taskGroup"><fmt:message key="nav_other" bundle="${resword}"/></div>
<div class="taskLeftColumn">
    <div class="taskLink"><a href="${urlPrefix}UpdateProfile"><fmt:message key="nav_update_profile" bundle="${resword}"/></a></div>
    <div class="taskLink"><a href="javascript: openDefWindow('help/iconkey.html');"> <fmt:message key="help" bundle="${resword}"/> </a></div>
    <div class="taskLink"><a href="javascript: window.location.href=('${pageContext.request.contextPath}/Contact');" > <fmt:message key="nav_support" bundle="${resword}"/> </a></div>
</div>
<div class="taskRightColumn">
    <div class="taskLink"><a href="${urlPrefix}j_spring_security_logout" onClick="clearLastAccessedObjects();"><fmt:message key="nav_log_out" bundle="${resword}"/></a></div>
    <div class="taskLink"><a href="http://www.clinovo.com/clincapture/forum/" target="_blank" > <fmt:message key="forums" bundle="${resword}"/> </a></div>
    <div class="taskLink"><a href="javascript: openDefWindow('${pageContext.request.contextPath}/help/about.jsp');" > <fmt:message key="about" bundle="${resword}"/> </a></div>
</div>
<br clear="all">
