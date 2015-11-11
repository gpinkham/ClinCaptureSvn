<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="rowShown" value="${displayItemWithGroup.itemsRow.shown}" scope="request"/>

<c:if test="${displayItemWithGroup.itemsRow.haveHeaders}">
	<div style="width: 100%; background-color: #ececec; ${rowShown ? "" : "display: none;"}" row-number="${itemStatus.index}" class="item-header">
		<c:forEach items="${displayItemWithGroup.itemsRow.headers}" var="itemHeader">
			<div style="padding: 5px; width: 555px; display: table-cell;">
				<b>${itemHeader}</b>
			</div>
		</c:forEach>
	</div>
</c:if>

<c:if test="${displayItemWithGroup.itemsRow.haveSubHeaders}">
	<div style="width: 100%; background-color: #ececec; ${rowShown ? "" : "display: none;"}" row-number="${itemStatus.index}" class="item-subheader">
		<c:forEach items="${displayItemWithGroup.itemsRow.subHeaders}" var="subHeader">
			<div style="padding: 5px; width: 555px; display: table-cell;">
					${subHeader}
			</div>
		</c:forEach>
	</div>
</c:if>

<div style="border-bottom: 1px solid #eeeeee; width: 100%; ${rowShown ? "" : "display: none;"}" row-number="${itemStatus.index}">
	<c:forEach items="${displayItemWithGroup.itemsRow.items}" var="displayItem" varStatus="rowStatus">
		<c:set var="displayItem" value="${displayItem}" scope="request"/>
		<c:set var="itemShown" value="${displayItem.scdData.scdDisplayInfo.scdShowStatus != 2}" scope="request"/>
		<c:set var="leftTextWidth" value="${(not empty displayItem.item.itemRenderMetadata && displayItem.item.itemRenderMetadata.leftItemTextWidth != 0)
				? displayItem.item.itemRenderMetadata.leftItemTextWidth : '200'}" scope="request"/>
		<c:set var="itemWidth" value="${(not empty displayItem.item.itemRenderMetadata && displayItem.item.itemRenderMetadata.width != 0)
				? displayItem.item.itemRenderMetadata.width : '555'}" scope="request"/>
		<div style="display: table-cell; padding: 5px; min-width: ${itemWidth}px; vertical-align: top;">
			<div class="item-cell" style="position: relative; display: ${itemShown ? 'table' : 'none'}" id="t${displayItem.item.id}">
				<div style="float: left; width: ${leftTextWidth}px; min-height: 10px">
					<div style="float: left;">
						<c:out value="${displayItem.metadata.questionNumberLabel}" escapeXml="false"/> 
					</div>
					<div style="float: left;">
						<c:import url="../submit/generateLeftItemTxt.jsp">
							<c:param name="linkText" value="${displayItem.metadata.leftItemText}"/>
						</c:import>
					</div>
				</div>
				<div style="float: left; width: 350px">
					<div style="display: inline-block">
						<c:import url="../submit/showItemInput.jsp">
							<c:param name="key" value="${numOfDate}" />
							<c:param name="tabNum" value="${itemNum}"/>
							<c:param name="defaultValue" value="${displayItem.metadata.defaultValue}"/>
							<c:param name="respLayout" value="${displayItem.metadata.responseLayout}"/>
							<c:param name="originJSP" value="${originJSP}"/>
							<c:param name="isForcedRFC" value="${dataEntryStage.isAdmin_Editing() ? study.studyParameterConfig.adminForcedReasonForChange : ''}"/>
						</c:import>
					</div>
					<div style="display: inline-block">
						<c:import url="../data-entry-include/discrepancy_flag.jsp">
							<c:param name="originJSP" value="${originJSP}"/>
						</c:import>
					</div>
					<c:if test="${displayItem.item.units != ''}">
						<div style="display: inline-block">
							<c:out value="(${displayItem.item.units})" escapeXml="false"/>
						</div>
					</c:if>
					<div style="display: inline-block">
						<c:import url="../submit/generateLeftItemTxt.jsp">
							<c:param name="linkText" value="${displayItem.metadata.rightItemText}"/>
						</c:import>
					</div>
				</div>
			</div>
		</div>
	</c:forEach>
</div>