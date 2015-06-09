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

<br clear="all">
<div class="taskGroup"><fmt:message key="nav_extract_data" bundle="${resword}"/></div>
<div class="taskLeftColumn">
    <div class="taskLink"><a href="${urlPrefix}ViewDatasets"><fmt:message key="datasets" bundle="${resword}"/></a></div>
</div>
<div class="taskRightColumn">
    <div class="taskLink"><a href="${urlPrefix}pages/casebooks"><fmt:message key="casebooks" bundle="${resword}"/></a></div>
</div>