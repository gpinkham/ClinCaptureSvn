<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

</tr>
<tr>
	<td class="table_cell">
		<table border="0">
			<c:set var="notFirstRow" value="${0}"/>
			<c:forEach var="childItem" items="${displayItemWithGroup.singleItem.children}">
			<c:set var="ccdisplay" value="${childItem.scdData.scdItemMetadataBean.id}"/>
			<c:if test="${childItem.metadata.showItem || ccdisplay>0}">
			<c:set var="currColumn" value="${childItem.metadata.columnNumber}"/>
			<c:if test="${currColumn == 1}">
			<c:if test="${notFirstRow != 0}">
				</tr>
			</c:if>
			<c:choose>
			<c:when test="${ccdisplay > 0}">
			<c:set var="scdShowStatus" value="${childItem.scdData.scdDisplayInfo.scdShowStatus}"/>
			<c:set var="cdId" value="${childItem.item.id}"/>
			<c:choose>
			<c:when test="${scdShowStatus == 1}">
			<tr id="t${cdId}">
				</c:when>
				<c:when test="${scdShowStatus == 2}">
			<tr id="t${cdId}" style="display:none">
				</c:when>
				<c:otherwise>
			<tr>
				</c:otherwise>
				</c:choose>
				</c:when>
				<c:otherwise>
			<tr>
				</c:otherwise>
				</c:choose>
				<c:set var="notFirstRow" value="${1}"/>
				<td valign="top">&nbsp;</td>
				</c:if>
				<c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
					<td valign="top">&nbsp;</td>
				</c:forEach>
				<td valign="top">
					<table border="0" class="itemHolderClass" id="itemHolderId_input${childItem.item.id}">
						<tr>
							<td valign="top" class="aka_ques_block">
								<c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/>
							</td>
							<td valign="top" class="aka_text_block">
								<c:import url="../submit/generateLeftItemTxt.jsp">
									<c:param name="itemId" value="${childItem.item.id}"/>
									<c:param name="inputType"
											 value="${childItem.metadata.responseSet.responseType.name}"/>
									<c:param name="function"
											 value="${childItem.metadata.responseSet.options[0].value}"/>
									<c:param name="linkText" value="${childItem.metadata.leftItemText}"/>
									<c:param name="side" value="left"/>
								</c:import>
							</td>
							<td valign="top" nowrap="nowrap">
								<c:set var="displayItem" scope="request" value="${childItem}"/>
								<c:import url="../submit/showItemInput.jsp">
									<c:param name="key" value="${numOfDate}"/>
									<c:param name="tabNum" value="${itemNum}"/>
									<c:param name="defaultValue" value="${childItem.metadata.defaultValue}"/>
									<c:param name="respLayout" value="${childItem.metadata.responseLayout}"/>
									<c:param name="originJSP" value="${originJSP}"/>
									<c:param name="isForcedRFC"
											 value="${dataEntryStage.isAdmin_Editing() ? study.studyParameterConfig.adminForcedReasonForChange : ''}"/>
								</c:import>
							</td>
							<td>
								<c:import url="../data-entry-include/discrepancy_flag.jsp"/>
							</td>
							<c:if test='${childItem.item.units != ""}'>
								<td valign="top"><c:out value="(${childItem.item.units})" escapeXml="false"/></td>
							</c:if>
							<td valign="top">
								<c:import url="../submit/generateLeftItemTxt.jsp">
									<c:param name="itemId" value="${childItem.item.id}"/>
									<c:param name="inputType"
											 value="${childItem.metadata.responseSet.responseType.name}"/>
									<c:param name="function"
											 value="${childItem.metadata.responseSet.options[0].value}"/>
									<c:param name="linkText" value="${childItem.metadata.rightItemText}"/>
									<c:param name="side" value="right"/>
								</c:import>
							</td>
						</tr>
					</table>
				</td>
				</c:if>
				</c:forEach>
			</tr>
		</table>
	</td>