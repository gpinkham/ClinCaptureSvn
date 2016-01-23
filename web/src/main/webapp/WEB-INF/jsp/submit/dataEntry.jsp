<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="com.akazaresearch.viewtags" prefix="view" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean id="dataEntryStage" scope="request" type="org.akaza.openclinica.bean.core.DataEntryStage"/>
<c:set var="isFSCRF" value="${toc.crf.source == 'formstudio'}" scope="request"/>

<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>ClinCapture <fmt:message key="initial_data_entry" bundle="${resword}"/></title>
	<link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
	<link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />

	<link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css" media="screen">
	<link rel="stylesheet" href="includes/print.css?r=${revisionNumber}" type="text/css" media="print">
	<link href="includes/jquery-ui.css" rel="stylesheet" type="text/css"/>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
	<script type="text/javascript" language="JavaScript" src="includes/repetition-model/repetition-model.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/prototype.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js?load=effects&r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/effects.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/js/session_lifetime.js?r=${revisionNumber}"></script>

	<ui:calendar/>
	<ui:theme/>

	<c:import url="/includes/js/conditional_show_hide.js.jsp" />
	<c:import url="/includes/js/pages/data_entry.js.jsp" />
	<c:import url="/includes/js/dn_flag_tooltip.js.jsp" />
	<c:import url="/includes/js/dialogs.js.jsp" />
</head>
<body class="${isFSCRF ? 'p10' : 'aka_bodywidth'}" id="${isFSCRF ? '' : 'centralContainer'}" onunload="clsWin();" >

<c:if test='${popUpURL != ""}'>
	<script>executeWhenDOMIsReady("openDNoteWindow('${popUpURL}');");</script>
</c:if>

<c:choose>
	<c:when test="${toc.eventDefinitionCRF.doubleEntry}">
		<c:set var="markCRFMethodName" scope="request" value="displayMessageFromCheckbox(this, 'markPartialSaved', true)"/>
	</c:when>
	<c:otherwise>
		<c:set var="markCRFMethodName" scope="request" value="displayMessageFromCheckbox(this, 'markPartialSaved', undefined)"/>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${section.checkInputs}">
		<c:set var="buttonAction" scope="request"><fmt:message key="save" bundle="${resword}"/></c:set>
		<c:set var="checkInputsValue" value="1" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="buttonAction" value="Confirm values" scope="request"/>
		<c:set var="checkInputsValue" value="0" scope="request"/>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${dataEntryStage.isInitialDE()}">
		<c:set var="formAction" value="InitialDataEntry" scope="request" />
		<c:set var="originJSP" value="initialDataEntry" scope="request" />
	</c:when>
	<c:when test="${dataEntryStage.isAdmin_Editing()}">
		<c:set var="formAction" value="AdministrativeEditing" scope="request" />
		<c:set var="originJSP" value="administrativeEditing" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="formAction" value="DoubleDataEntry" scope="request" />
		<c:set var="originJSP" value="doubleDataEntry" scope="request" />
	</c:otherwise>
</c:choose>

<c:set var="save_and_next_button_caption" scope="request"><fmt:message key='save_and_next' bundle='${resword}'/></c:set>
<c:set var="submitClassType" value="submit" scope="request"/>
<c:set var="fromResolveDiscrepancyPage" value="${fromResolveDiscrepancy ne null and fromResolveDiscrepancy eq 'true'}" scope="request"/>


<table width="75%">
	<tr>
		<td><span class="first_level_header">
			<b id="crfNameId">
				${toc.crf.name} ${toc.crfVersion.name}
				<ui:displayEventCRFStatusIcon studySubject="${studySubject}" studyEvent="${studyEvent}"
						eventDefinitionCRF="${toc.eventDefinitionCRF}" eventCrf="${eventCRF}"/>
			</b>
		</span></td>
	</tr>
	<tr>
		<td><span class="first_level_header">
           <fmt:message key="subject_ID" bundle="${resword}"/>: <c:out value="${studySubject.label}"/>
		</span></td>
	</tr>
</table>

<form id="mainForm" name="crfForm" method="post" action="${formAction}">
	<c:import url="../data-entry-include/hidden_fields.jsp"/>
	<c:import url="interviewer.jsp">
		<c:param name="hasNameNote" value="${hasNameNote}"/>
		<c:param name="hasDateNote" value="${hasDateNote}"/>
	</c:import>
	<br />

	<c:if test="${!dataEntryStage.isAdmin_Editing()}">
		<c:import url="../data-entry-include/request_password_block.jsp"/>
	</c:if>

	<!-- section tabs here -->
	<table id="crfSectionTabsTable" border="0" cellpadding="0" cellspacing="0" style="${!(crfShortcutsAnalyzer eq null || (crfShortcutsAnalyzer.totalNew == 0 && crfShortcutsAnalyzer.totalUpdated == 0 && crfShortcutsAnalyzer.totalResolutionProposed == 0 && crfShortcutsAnalyzer.totalClosed == 0 && crfShortcutsAnalyzer.totalAnnotations == 0 && crfShortcutsAnalyzer.totalItemsToSDV == 0)) ? 'padding-top: 80px;' : 'padding-top: 0px;'}">
		<tr><td>
			<c:import url="../data-entry-include/error_messages.jsp"/>
		</td></tr>
		<tr class="sectionsContainer">
			<td></td>
		</tr>
	</table>

	<input type="hidden" name="submitted" value="1" />
	<c:set var="stage" value="${param.stage}" scope="request"/>


	<div class="table_shadow_bottom" style="display: table; ${isFSCRF ? 'min-width: 100%;' : ''}">
		<c:set var="currPage" value="" scope="request" />
		<c:set var="curCategory" value="" scope="request"/>
		<c:set var="displayItemNum" value="${0}" scope="request"/>
		<c:set var="numOfTr" value="0" scope="request"/>
		<c:set var="numOfDate" value="1" scope="request"/>
		<c:set var="repeatCount" value="1" scope="request"/>
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
			<c:if test='${section.section.title != ""}'>
				<tr class="aka_stripes">
					<td class="aka_header_border"><b><fmt:message key="title" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.title}" escapeXml="false"/></b> </td>
				</tr>
			</c:if>

			<c:if test='${section.section.subtitle != ""}'>
				<tr class="aka_stripes">
					<td class="aka_header_border"><fmt:message key="subtitle" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.subtitle}" escapeXml="false"/> </td>
				</tr>
			</c:if>

			<c:if test='${section.section.instructions != ""}'>
				<tr class="aka_stripes">
					<td class="aka_header_border"><fmt:message key="instructions" bundle="${resword}"/>:&nbsp;<c:out value="${section.section.instructions}" escapeXml="false"/> </td>
				</tr>
			</c:if>

			<c:forEach var="displayItemWithGroup" items="${section.displayItemGroups}" varStatus="itemStatus">
				<c:set var="itemStatus" value="${itemStatus}" scope="request"/>
				<c:set var="displayItemWithGroup" value="${displayItemWithGroup}" scope="request"/>
				<c:if test="${displayItemWithGroup.itemGroup.groupMetaBean.showGroup}">
					<c:if test="${displayItemNum ==0}">
						<tr class="aka_stripes">
							<td class="aka_header_border" colspan="2">
								<table border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 6px;">
									<tr>
										<td valign="bottom" nowrap="nowrap" style="padding-right: 50px">
											<a name="top"><fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItemWithGroup.pageNumberLabel}" escapeXml="false"/></a>
										</td>
										<td align="right" valign="bottom">
											<jsp:include page="../data-entry-include/form_actions.jsp">
												<jsp:param name="isUpper" value="true"/>
											</jsp:include>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</c:if>

					<c:if test="${currPage != displayItemWithGroup.pageNumberLabel && displayItemNum >0}">
						<tr class="aka_stripes">
							<td class="aka_header_border" colspan="2">
								<c:import url="../data-entry-include/page_number.jsp"/>
							</td>
						</tr>
					</c:if>

					<c:choose>
						<c:when test="${displayItemWithGroup.inGroup == true}">
							<jsp:include page="../data-entry-include/repeating_group_render.jsp"/>
						</c:when>
						<c:otherwise>
							<c:set var="currPage" value="${displayItemWithGroup.singleItem.metadata.pageNumberLabel}" scope="request"/>
							<c:set var="cdisplay" value="${displayItemWithGroup.singleItem.scdData.scdItemMetadataBean.id}" scope="request"/>
							<c:if test="${displayItemWithGroup.singleItem.metadata.showItem || cdisplay>0}">

								<c:choose>
									<c:when test="${isFSCRF}">
										<tr>
											<td>
												<jsp:include page="../data-entry-include/fs_item_render.jsp"/>
											</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:if test="${displayItemWithGroup.singleItem.metadata.parentId == 0}">
											<jsp:include page="../data-entry-include/simple_item_render.jsp"/>
										</c:if>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:otherwise>
					</c:choose>

					<c:set var="displayItemNum" value="${displayItemNum + 1}" scope="request"/>
				</c:if>
			</c:forEach>
		</table>

		<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
			<tr>
				<td valign="bottom" nowrap="nowrap">
					<a href="#top">&nbsp;&nbsp;<fmt:message key="return_to_top" bundle="${resword}"/></a>
				</td>
				<td align="right" valign="bottom">
					<jsp:include page="../data-entry-include/form_actions.jsp">
						<jsp:param name="isUpper" value="false"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>

</form>

<jsp:include page="../include/changeTheme.jsp"/>

</body>
</html>