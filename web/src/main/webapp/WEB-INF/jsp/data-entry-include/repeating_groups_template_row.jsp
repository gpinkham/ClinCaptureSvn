<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="isButtonRemShow" value="true"/>
<c:forEach var="bodyItem" items="${bodyItemGroup.items}" varStatus="bodyItemState">
	<c:set var="itemNum" value="${tabbingMode eq 'leftToRight' ? crfTabIndex + bodyItemState.index : crfTabIndex}" scope="request"/>
	<c:choose>
		<c:when test="${bodyItem.metadata.showItem}">
			<c:set var="isItemShown" value="false"  scope="request"/>
			<c:forEach var="formMsg" items="${formMessages}">
				<c:set var="inputValue" value="${repeatParentId}_${uniqueId}input${bodyItem.item.id}"  scope="request"/>
				<c:if test="${formMsg.key eq inputValue}">
					<c:set var="isItemShown" value="true"  scope="request"/>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${isItemShown && hasShown}">
					<c:set var="extraClass" value="aka_group_show" scope="request"/>
				</c:when>
				<c:when test="${bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
								bodyItem.metadata.responseSet.responseType.name eq 'checkbox'}">
					<c:set var="extraClass" value="align_left"  scope="request"/>
				</c:when>
				<c:otherwise>
					<c:set var="extraClass" value=" " scope="request"/>
				</c:otherwise>
			</c:choose>
			<c:set var="isHorizontalCellLevel" scope="request" value="${fn:toLowerCase(bodyItem.metadata.responseLayout) eq 'horizontal'}"/>
			<c:choose>
				<c:when test="${isHorizontalCellLevel &&
						(bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
						bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
					<%-- For horizontal checkboxes, radio buttons--%>
					<c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
						<td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
							<c:set var="displayItem" scope="request" value="${bodyItem}" />
							<c:set var="responseOptionBean" scope="request" value="${respOption}" />
							<c:import url="../submit/showGroupItemInput.jsp">
								<c:param name="repeatParentId" value="${displayItemWithGroup.itemGroup.itemGroupBean.oid}"/>
								<c:param name="rowCount" value="${uniqueId}"/>
								<c:param name="key" value="${numOfDate}" />
								<c:param name="isLast" value="${true}"/>
								<c:param name="isTemplateRow" value="${true}"/>
								<c:param name="tabNum" value="${itemNum}"/>
								<c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
								<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
								<c:param name="originJSP" value="${originJSP}"/>
								<c:param name="isForcedRFC" value="${dataEntryStage.isAdmin_Editing() ? study.studyParameterConfig.adminForcedReasonForChange : ''}"/>
							</c:import>
						</td>
					</c:forEach>
				</c:when>
				<%-- could be a radio or checkbox that is not horizontal --%>
				<c:otherwise>
					<td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
						<c:set var="displayItem" scope="request" value="${bodyItem}" />
						<c:import url="../submit/showGroupItemInput.jsp">
							<c:param name="repeatParentId" value="${displayItemWithGroup.itemGroup.itemGroupBean.oid}"/>
							<c:param name="rowCount" value="${uniqueId}"/>
							<c:param name="key" value="${numOfDate}" />
							<c:param name="isLast" value="${true}"/>
							<c:param name="isTemplateRow" value="${true}"/>
							<c:param name="tabNum" value="${itemNum}"/>
							<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
							<c:param name="originJSP" value="${originJSP}"/>
							<c:param name="isForcedRFC" value="${dataEntryStage.isAdmin_Editing() ? study.studyParameterConfig.adminForcedReasonForChange : ''}"/>
						</c:import>
					</td>
				</c:otherwise>
			</c:choose>
			<c:set var="columnNum" value="${columnNum+1}" scope="request"/>
		</c:when>
		<c:when test="${bodyItem.blankDwelt}">
			<td class="aka_padding_norm aka_cellBorders"></td>
		</c:when>
	</c:choose>
</c:forEach>
<c:if test="${displayItemWithGroup.itemGroup.groupMetaBean.repeatingGroup}">
	<td class="aka_padding_norm aka_cellBorders">
		<input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
		<button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove" rel="${savedIntoDB}"></button>
	</td>
</c:if>