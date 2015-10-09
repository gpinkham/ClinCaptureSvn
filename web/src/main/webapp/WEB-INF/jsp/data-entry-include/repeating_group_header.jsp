<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:if test="${!(displayItemWithGroup.itemGroup.groupMetaBean.header eq '')}">
	<tr>
		<th colspan="${displayItemWithGroup.columnsShown}" class="aka_headerBackground aka_padding_large aka_cellBorders">
			<c:out value="${displayItemWithGroup.itemGroup.groupMetaBean.header}" escapeXml="false"/>
		</th>
	</tr>
</c:if>


<tr>
	<%-- if there are horizontal checkboxes or radios anywhere in the group...--%>
	<c:set var="isHorizontal" scope="request" value="${false}"/>
	<c:forEach var="thItem" items="${displayItemWithGroup.itemGroup.items}">
		<c:set var="metadata" value="${thItem.metadata}"/>
		<c:if test="${metadata.showItem}">
			<c:set var="questionNumber" value="${metadata.questionNumberLabel}"/>
			<c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
			<c:if test="${fn:toLowerCase(metadata.responseLayout) eq 'horizontal'}">
				<c:set var="isHorizontal" scope="request" value="${true}"/>
				<c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
				<c:set var="optionsLen" value="0"/>
				<c:forEach var="optn" items="${metadata.responseSet.options}">
					<c:set var="optionsLen" value="${optionsLen+1}"/>
				</c:forEach>
			</c:if>
			<c:choose>
				<c:when test="${isHorizontalCellLevel &&
						(metadata.responseSet.responseType.name eq 'checkbox' ||
							metadata.responseSet.responseType.name eq 'radio')}">
					<th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders">
					<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" scope="request"/>
				</c:when>
				<c:otherwise>
					<th class="aka_headerBackground aka_padding_large aka_cellBorders">
					<%-- compute total columns value for the add button row colspan attribute--%>
					<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" scope="request"/>
				</c:otherwise>
			</c:choose>

			<c:if test="${! (empty questionNumber)}">
				<span style="margin-right:1em"><c:out value="${questionNumber}" escapeXml="false"/></span>
			</c:if>
			<c:out value="${metadata.header eq '' ? metadata.leftItemText : metadata.header}" escapeXml="false"/>
			</th>
		</c:if>
	</c:forEach>
	<c:if test="${displayItemWithGroup.itemGroup.groupMetaBean.repeatingGroup}">
		<th class="aka_headerBackground aka_padding_large aka_cellBorders" />
	</c:if>
</tr>
<c:if test="${isHorizontal}">
	<%-- create second row for hodizontal items --%>
	<tr>
		<c:forEach var="thItem" items="${displayItemWithGroup.itemGroup.items}">
			<c:set var="metadata" value="${thItem.metadata}"/>
			<c:set var="responseSet" value="${metadata.responseSet}"/>
			<c:if test="${metadata.showItem}">
				<c:set var="isHorizontalCellLevel" scope="request" value="${fn:toLowerCase(metadata.responseLayout) eq 'horizontal'}"/>
				<c:choose>
					<c:when test="${isHorizontalCellLevel && (responseSet.responseType.name eq 'checkbox' || responseSet.responseType.name eq 'radio')}">
						<c:forEach var="respOpt" items="${responseSet.options}">
							<th class="aka_headerBackground aka_padding_large aka_cellBorders">
								<c:out value="${respOpt.text}" />
							</th>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<th class="aka_headerBackground aka_padding_large aka_cellBorders"></th>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</tr>
</c:if>