<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:if test="${(not empty lastItemWasSingle) && lastItemWasSingle}">
	</tr></table></td></tr>
</c:if>

<c:set var="lastItemWasSingle" value="false" scope="request" />
<c:set var="currPage" value="${displayItemWithGroup.pageNumberLabel}" scope="request" />
<tr>
	<c:set var="isSectionShown" value="false" scope="request"/>
	<c:forEach var="formMsg" items="${formMessages}">
		<c:if test="${formMsg.key eq displayItemWithGroup.itemGroup.itemGroupBean.oid}">
			<c:set var="isSectionShown" value="true" scope="request"/>
		</c:if>
	</c:forEach>
	<td class="${(hasShown && isSectionShown) ? 'aka_group_show' : ''}">
		<c:set var="uniqueId" value="0" scope="request"/>
		<c:set var="repeatParentId" value="${displayItemWithGroup.itemGroup.itemGroupBean.oid}" scope="request"/>
		<c:set var="repeatNumber" value="${displayItemWithGroup.itemGroup.groupMetaBean.repeatNum}" scope="request"/>
		<c:set var="repeatNumber" value="${(groupHasData or (isFirstTimeOnSection == section.section.id)) ? 0 : repeatNumber - 1}" scope="request"/>
		<c:set var="repeatMax" value="${displayItemWithGroup.itemGroup.groupMetaBean.repeatMax}" scope="request"/>
		<c:set var="totalColsPlusSubcols" value="0" scope="request"/>
		<c:set var="questionNumber" value="" scope="request"/>
		<c:if test="${! (repeatParentId eq 'Ungrouped')}">
			<c:if test="${! (displayItemWithGroup.itemGroup.groupMetaBean.header eq '')}">
				<div class="aka_group_header">
					<strong><c:out value="${displayItemWithGroup.itemGroup.groupMetaBean.header}" escapeXml="false"/></strong>
				</div>
			</c:if>
			<c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
			<table border="0" cellspacing="0" cellpadding="0" class="aka_form_table repeatingGroupTable" width="100%">
				<thead>
				<jsp:include page="../data-entry-include/repeating_group_header.jsp"/>
				</thead>
				<tbody>
				<c:set var="uniqueId" value="${0}" scope="request"/>
				<c:set var="repeatRowCount" value="0" scope="request"/>
				<c:set var="dbItemGroupsSize" value="${fn:length(displayItemWithGroup.dbItemGroups)}" scope="request"/>
				<c:set var="itemGroupsSize" value="${fn:length(displayItemWithGroup.itemGroups)}" scope="request"/>
				<c:forEach var="bodyItemGroup" items="${displayItemWithGroup.itemGroups}">
					<c:set var="repeatRowCount" value="${repeatRowCount+1}" scope="request"/>
				</c:forEach>
				<!-- there are data posted already -->
				<c:set var="tabbingMode" value="${event_def_crf_bean.tabbingMode}" scope="request"/>
				<c:set var="itemNum" value="${crfTabIndex}" scope="request"/>

				<c:forEach var="bodyItemGroup" items="${displayItemWithGroup.itemGroups}"  varStatus="status">
					<c:set var="bodyItemGroup" value="${bodyItemGroup}" scope="request"/>
					<c:set var="columnNum"  value="1" scope="request"/>
					<!-- hasError is set to true when validation error happens-->
					<c:set var="savedIntoDB" value="false" scope="request"/>
					<c:if test="${status.index <= dbItemGroupsSize - 1}">
						<c:set var="savedIntoDB" value="true" scope="request"/>
					</c:if>

					<tr repeat="${uniqueId}" class="repeatingTableRow">
						<jsp:include page="../data-entry-include/repeating_group_row.jsp"/>
					</tr>

					<c:if test="${status.last}">
						<tr id="<c:out value="${repeatParentId}"/>" class="repeatingTableRow" repeat="template" repeat-start="${repeatNumber}" repeat-max="<c:out value="${repeatMax}"/>" >
							<jsp:include page="../data-entry-include/repeating_groups_template_row.jsp"/>
						</tr>
						<c:if test='${tabbingMode eq "topToBottom" && itemGroupsSize ne null && itemGroupsSize > 0}'>
							<c:set var="crfTabIndex" value="${crfTabIndex + fn:length(displayItemWithGroup.itemGroups[0].items) - 1}" scope="request"/>
						</c:if>
					</c:if>

					<c:set var="uniqueId" value="${uniqueId +1}" scope="request"/>
				</c:forEach>

				<c:if test="${displayItemWithGroup.itemGroup.groupMetaBean.repeatingGroup}">
					<tr>
						<td class="aka_padding_norm aka_cellBorders" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
							<button stype="add" type="button" template="<c:out value="${repeatParentId}"/>" class="button_search"><fmt:message key="add" bundle="${resword}"/></button></td>
					</tr>
				</c:if>
				</tbody>
			</table>
		</c:if>
	</td>
</tr>