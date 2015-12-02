<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="itemId" value="${param.itemId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${param.inputName}" />

<c:if test="${displayItem.data.id > 0 && section.eventCRF.stage.doubleDE_Complete && displayItem.metadata.showItem && displayItem.edcItemMetadata.sdvRequired() && (userRole.studyAdministrator || userRole.monitor)}">
    <c:set var="sdvItemLinkAdditionalClass" value="${study.studyParameterConfig.allowSdvWithOpenQueries == 'no' && !eventCrfDoesNotHaveOutstandingDNs ? 'hidden' : ''}"/>
    <a class="sdvItemLink ${sdvItemLinkAdditionalClass}">
        <fmt:message key="sdv_item" bundle="${resword}" var="sdvItemTitle"/>
        <fmt:message key="data_entry_complete" bundle="${resword}" var="dataEntryCompleteTitle"/>
        <fmt:message key="sourceDataVerified" bundle="${resword}" var="sourceDataVerifiedTitle"/>
        <c:set var="notSDVedTooltipFunctions" value="onmouseover=\"callTip('${sdvItemTitle}')\" onmouseout=\"UnTip()\""/>
        <c:set var="sdvFunction" value="onclick=\"itemLevelSDV('${pageContext.request.contextPath}', '${displayItem.data.id}', '${section.section.id}', '${section.eventDefinitionCRF.id}', 'images/item_sdved.png', 'images/icon_DoubleCheck.gif', '${sourceDataVerifiedTitle}', '${sourceDataVerifiedTitle}');\""/>
        <c:set var="unSDVFunction" value="onclick=\"itemLevelUnSDV('${pageContext.request.contextPath}', '${displayItem.data.id}', '${section.section.id}', '${section.eventDefinitionCRF.id}', 'images/item_sdv.png', 'images/icon_DEcomplete.gif', '${sdvItemTitle}', '${dataEntryCompleteTitle}');\""/>
        <div id="sdvFunction_${displayItem.data.id}" class="hidden">${sdvFunction}</div>
        <div id="unSDVFunction_${displayItem.data.id}" class="hidden">${unSDVFunction}</div>
        <c:choose>
            <c:when test="${displayItem.data.sdv || section.eventCRF.sdvStatus}">
                <img itemId="${itemId}" inputName="${inputName}" rowCount="${rowCount}" class="sdvItem ${sdvItemAdditionalClass}" id="sdv_itemData_${displayItem.data.id}" src="images/item_sdved.png" border="0" ${unSDVFunction} onmouseover="callTip('${sourceDataVerifiedTitle}')" onmouseout="UnTip()">
            </c:when>
            <c:otherwise>
                <img itemId="${itemId}" inputName="${inputName}" rowCount="${rowCount}" class="sdvItem " id="sdv_itemData_${displayItem.data.id}" src="images/item_sdv.png" border="0" ${sdvFunction} ${notSDVedTooltipFunctions}>
            </c:otherwise>
        </c:choose>
    </a>
</c:if>
