<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:if test="${displayItemWithGroup.singleItem.metadata.columnNumber <= 1}">

	<c:if test="${(not empty lastItemWasSingle) && lastItemWasSingle}">
		</tr></table></td></tr>
	</c:if>

	<c:set var="lastItemWasSingle" value="true" scope="request"/>
	<c:set var="numOfTr" value="${numOfTr+1}" scope="request"/>

	<c:if test="${!empty displayItemWithGroup.singleItem.metadata.header}">
		<jsp:include page="../data-entry-include/simple_item_header.jsp"/>
	</c:if>

	<c:if test="${!empty displayItemWithGroup.singleItem.metadata.subHeader}">
		<jsp:include page="../data-entry-include/simple_item_subheader.jsp"/>
	</c:if>

	<c:set var="rowDisplay" value="${displayItemWithGroup.singleItem.scdData.scdDisplayInfo.rowDisplayStatus}" scope="request"/>
	<c:set var="rowSCDShowIDStr" value="${displayItemWithGroup.singleItem.scdData.scdDisplayInfo.rowSCDShowIDStr}" scope="request"/>
	<input type="hidden" id="rowSCDShowIDs${numOfTr}" value="${rowSCDShowIDStr}" />
	<c:choose>
		<c:when test="${rowDisplay == 0}">
			<tr>
		</c:when>
		<c:when test="${rowDisplay == 1}">
			<tr id="tr${numOfTr}">
		</c:when>
		<c:otherwise>
			<tr id="tr${numOfTr}" style="display:none">
		</c:otherwise>
	</c:choose>
	<c:if test="${displayItemWithGroup.singleItem.item.dataType.id ne 13 and displayItemWithGroup.singleItem.item.dataType.id ne 14}">
		<td class="table_cell_left">
		<table border="0" class="itemHolderClass" id="itemHolderId_input${displayItemWithGroup.singleItem.item.id}">
		<c:set var="prevItemHolderId" value="${displayItemWithGroup.singleItem.item.id}" scope="request"/>
		<tr>
	</c:if>
</c:if>

<c:choose>
	<c:when test="${cdisplay > 0}">
		<c:set var="scdShowStatus" value="${displayItemWithGroup.singleItem.scdData.scdDisplayInfo.scdShowStatus}" scope="request"/>
		<c:set var="cdId" value="${displayItemWithGroup.singleItem.item.id}" scope="request"/>
		<input type="hidden" id="col${cdId}" value="${numOfTr}"/>
		<c:choose>
			<c:when test="${scdShowStatus == 1}">
				<td valign="top" id="t${cdId}">
			</c:when>
			<c:when test="${scdShowStatus == 2}">
				<td valign="top" id="t${cdId}" style="display:none">
			</c:when>
			<c:otherwise>
				<td valign="top">
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<td valign="top">
	</c:otherwise>
</c:choose>

<c:set var="isItemShown" value="false"/>
<c:forEach var="formMsg" items="${formMessages}">
	<c:set var="inputValue">input<c:out value="${displayItemWithGroup.singleItem.item.id}"/></c:set>
	<c:if test="${formMsg.key eq inputValue}">
		<c:set var="isItemShown" value="true"/>
	</c:if>
</c:forEach>

<c:choose>
	<c:when test="${isItemShown && hasShown}">
		<table border="0" cellspacing="0" cellpadding="1" class='aka_group_show <c:if test="${prevItemHolderId != displayItemWithGroup.singleItem.item.id}"> itemHolderClass" id="itemHolderId_input${displayItemWithGroup.singleItem.item.id}"<c:set var="prevItemHolderId" value="${displayItemWithGroup.singleItem.item.id}"/></c:if>'>
		<tr>
	</c:when>
	<c:otherwise>
		<table border="0" cellspacing="0" cellpadding="1" <c:if test="${prevItemHolderId != displayItemWithGroup.singleItem.item.id}">class="itemHolderClass" id="itemHolderId_input${displayItemWithGroup.singleItem.item.id}"<c:set var="prevItemHolderId" value="${displayItemWithGroup.singleItem.item.id}"/></c:if>>
		<tr>
	</c:otherwise>
</c:choose>

<c:if test="${displayItemWithGroup.singleItem.item.dataType.id ne 13 and displayItemWithGroup.singleItem.item.dataType.id ne 14}">
	<td valign="top" class="aka_ques_block">
		<c:out value="${displayItemWithGroup.singleItem.metadata.questionNumberLabel}" escapeXml="false"/>
	</td>
	<td valign="top" class="aka_text_block">
		<c:import url="../submit/generateLeftItemTxt.jsp">
			<c:param name="itemId" value="${displayItemWithGroup.singleItem.item.id}"/>
			<c:param name="inputType" value="${displayItemWithGroup.singleItem.metadata.responseSet.responseType.name}"/>
			<c:param name="function" value="${displayItemWithGroup.singleItem.metadata.responseSet.options[0].value}"/>
			<c:param name="linkText" value="${displayItemWithGroup.singleItem.metadata.leftItemText}"/>
			<c:param name="side" value="left"/>
		</c:import>
	</td>
	<td valign="top" nowrap="nowrap">
		<c:set var="displayItem" scope="request" value="${displayItemWithGroup.singleItem}" />
		<c:import url="../submit/showItemInput.jsp">
			<c:param name="key" value="${numOfDate}" />
			<c:param name="tabNum" value="${itemNum}"/>
			<c:param name="defaultValue" value="${displayItemWithGroup.singleItem.metadata.defaultValue}"/>
			<c:param name="respLayout" value="${displayItemWithGroup.singleItem.metadata.responseLayout}"/>
			<c:param name="originJSP" value="${originJSP}"/>
		<c:param name="isForcedRFC" value="${dataEntryStage.isAdmin_Editing() ? study.studyParameterConfig.adminForcedReasonForChange : ''}"/>
	</c:import>
</td>
<td>
	<c:import url="../data-entry-include/discrepancy_flag.jsp">
		<c:param name="originJSP" value="${originJSP}"/>
	</c:import>
</td>
<c:if test='${displayItemWithGroup.singleItem.item.units != ""}'>
	<td valign="top">
		<c:out value="(${displayItemWithGroup.singleItem.item.units})" escapeXml="false"/>
		</td>
	</c:if>
	<td valign="top">
		<c:import url="../submit/generateLeftItemTxt.jsp">
			<c:param name="itemId" value="${displayItemWithGroup.singleItem.item.id}"/>
			<c:param name="inputType" value="${displayItemWithGroup.singleItem.metadata.responseSet.responseType.name}"/>
			<c:param name="function" value="${displayItemWithGroup.singleItem.metadata.responseSet.options[0].value}"/>
			<c:param name="linkText" value="${displayItemWithGroup.singleItem.metadata.rightItemText}"/>
			<c:param name="side" value="right"/>
		</c:import>
	</td>
	</tr>
	</table>
	</td>
</c:if>

<c:if test="${displayItemWithGroup.singleItem.numChildren > 0}">
	<jsp:include page="../data-entry-include/child_item_render.jsp"/>
</c:if>

<c:if test="${itemStatus.last}">
	</tr></table></td></tr>
</c:if>