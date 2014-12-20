<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm"%>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat" />
	
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" />
    <title>ClinCapture <fmt:message key="view_data_entry" bundle="${resword}" /></title>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />

    <link rel="stylesheet" href="${contextPath}/includes/styles.css" type="text/css" media="screen">
    <link rel="stylesheet" href="${contextPath}/includes/print.css" type="text/css" media="print">
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/global_functions_javascript.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/Tabs.js"></script>
    <script type="text/javascript" language="JavaScript" src="${contextPath}/includes/repetition-model/repetition-model.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/prototype.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/scriptaculous.js?load=effects"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/effects.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="${contextPath}/includes/jmesa/jquery-ui.min.js"></script>
    <ui:calendar/>
    <ui:theme/>
    <script type="text/javascript" language="JavaScript" src="${contextPath}/includes/jmesa/jquery.blockUI.js"></script>
</head>
<body class="aka_bodywidth">
<script language="JavaScript" type="text/javascript">

    function disableElements() {
        jQuery("table > tbody  tr").attr("repeat", "0");
        jQuery("table > tbody  button").attr("disabled", "true");
        jQuery("table > tbody  input").attr("disabled", "disabled");
        jQuery("table > tbody a").not('[tabindex]').removeAttr("onlick");
        jQuery("table > tbody .tablebox_center select").attr("disabled", "disabled");
        jQuery("table > tbody .tablebox_center textarea").attr("disabled", "disabled");
        jQuery("table > tbody .tablebox_center button").attr("disabled", "disabled");
    }

    function calcCenterOfElement(element) {
        var center = {
            x: element.offset().left + element.width() / 2,
            y: element.offset().top + element.height() / 2
        }
        return center;
    }

    function calcOffset(target, bullet) {
        var offset = {
            delta_x: target.x - bullet.x,
            delta_y: target.y - bullet.y
        }
        return offset;
    }

    function annotateAllRadioButtons() {
        $('input[id*=input][type=radio]').each(annotateElement);
        return;
    }

    function annotateAllCheckBoxes() {
        $('input[id*=input][type=checkbox]').each(annotateElement);
        return;
    }

    function annotateElement(i, element) {
        var aDivId = 'a_div_' + element.id;
        var annotationTextDiv = $(element).prev();
        var offset = calcOffset(calcCenterOfElement($(element)), calcCenterOfElement(annotationTextDiv));
        annotationTextDiv.css({
            'left': annotationTextDiv.offset().left + offset.delta_x + 'px',
            'top': annotationTextDiv.offset().top + offset.delta_y + 'px',
            'display': '',
            'visibility': 'visible'
        });
        return;
    }

    $(window).load(function () {
        deleteHideStuff();
        annotateAllRadioButtons();
        annotateAllCheckBoxes();
        disableElements();
    })
</script>

<c:set var="prevItemHolderId" value="0"/>

<div id="centralContainer" style="padding-left: 3em; margin-top: 1em; background-color: white; color: black;">
<table width="75%">
	<tr>
		<td>
			<h1>
				<span class="first_level_header"> 
					<b> <c:out value="${toc.crf.name}" /> 
						<c:out value="${toc.crfVersion.name}" /> 
						<img src="images/icon_NotStarted.gif" alt="<fmt:message key="not_started" bundle="${resword}"/>" title="<fmt:message key="not_started" bundle="${resword}"/>">
					</b> 
					&nbsp;&nbsp;
				</span>
			</h1>
		</td>
	</tr>
</table>

<input type="button" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" value="<fmt:message key="exit" bundle="${resword}"/>" class="button" />
	
<br/> <br> 
<c:set var="sectionNum" value="${fn:length(toc.sections)}"/> 
	 
<!-- section tabs here -->
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td class="annotated_crfOID">
		<span id="crfVerFormOID" class="annotated_crfOID"><c:out value="${crfVerFormOID}"/>&nbsp;</span>
		<c:forEach var="sed" items="${studyEventDefs}" varStatus="myStatus">
		<c:if test='${myStatus.first}'>
			(
		</c:if>
		<span id="studyEventDefs" class="annotated_eventOID"><c:out value="${sed.oid}"/>
			<c:if test='${!myStatus.last}'>
				,&nbsp;
			</c:if>
		</span>
		<c:if test='${myStatus.last}'>
			)	
		</c:if>
		</c:forEach>
	</td>
</tr>
<tr>

<script type="text/JavaScript" language="JavaScript">

    // Total number of tabs (one for each CRF)
    var TabsNumber = <c:out value="${sectionNum}"/>;

    var frameWidth = 1000;
    var tabWidth = frameWidth/TabsNumber;

    // Number of tabs to display at a time  o
    var TabsShown = TabsNumber; /* was 3; */

    // Labels to display on each tab (name of CRF)
    var TabLabel = new Array(TabsNumber)
    var TabFullName = new Array(TabsNumber)
    var TabSectionId = new Array(TabsNumber)

    <c:set var="eventDefinitionCRFDoubleEntryMode" value="${toc.eventDefinitionCRF.doubleEntry}"/>
    <c:set var="showCustomMSG" value="${toc.eventCRF.stage.id < 3}"/>
    <c:choose>
        <c:when test="${eventDefinitionCRFDoubleEntryMode && showCustomMSG}">
            <c:set var="markCRFMethodName" value="displayMessageFromCheckbox(this, true)"/>
        </c:when>
        <c:otherwise>
            <c:set var="markCRFMethodName" value="displayMessageFromCheckbox(this, undefined)"/>
        </c:otherwise>
    </c:choose>

    <c:set var="count" value="0"/>
    <c:forEach var="section" items="${toc.sections}">
    <c:set var="completedItems" value="${section.numItemsCompleted}"/>
    <c:if test="${section.numItemsCompleted == 0 && toc.eventDefinitionCRF.doubleEntry}">
    <c:set var="completedItems" value="${section.numItemsNeedingValidation}"/>
    </c:if>

    <c:set var="cwParam" value=""/>
    <c:if test="${justCloseWindow}">
        <c:set var="cwParam" value="cw=1&"/>
    </c:if>

    TabFullName[<c:out value="${count}"/>] = "<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";

    TabSectionId[<c:out value="${count}"/>] = <c:out value="${section.id}"/>;

    TabLabel[<c:out value="${count}"/>] = "<c:out value="${section.label}"/> " + "<span id='secNumItemsCom<c:out value="${count}"/>' style='font-weight: normal;'>(<c:out value="${completedItems}"/>/<c:out value="${section.numItems}" />)</span>";

    <c:set var="count" value="${count+1}"/>
    </c:forEach>
    DisplaySectionTabs();

    function DisplaySectionTabs() {
        TabID = 1;

        while (TabID <= TabsNumber)
        {
            sectionId = TabSectionId[TabID - 1];
        <c:choose>
        <c:when test="${studySubject != null && studySubject.id>0}">
            url = "${contextPath}/ViewSectionDataEntry?${cwParam}eventCRFId=" + <c:out value="${EventCRFBean.id}"/> + "&crfVersionId=${section.crfVersion.id}&sectionId=" + sectionId + "&tabId=" + TabID + "&eventDefinitionCRFId=${eventDefinitionCRFId}&annotated=1";

        </c:when>
        <c:otherwise>
            url = "${contextPath}/ViewSectionDataEntry?${cwParam}crfVersionId=" + <c:out value="${section.crfVersion.id}"/> + "&sectionId=" + sectionId + "&eventCRFId=" + <c:out value="${EventCRFBean.id}"/> + "&tabId=" + TabID+"&eventDefinitionCRFId=${eventDefinitionCRFId}&annotated=1";

        </c:otherwise>
        </c:choose>
            currTabID = <c:out value="${tabId}"/>;
            document.write('<td nowrap style="display:inline-block; overflow:hidden; max-width: ' + tabWidth + 'px" class="crfHeaderTabs" valign="bottom" id="Tab' + TabID + '">');
            if (TabID != currTabID) {
                document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
                document.write('<a class="tabtext" title="' + TabFullName[(TabID - 1)] + '" href=' + url + '>' + TabLabel[(TabID - 1)] + '</a></div></div></div></div>');
                document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>');
                document.write('</td>');
            }
            else {
                document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">');
                document.write('<span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>');
                document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>');
                document.write('</td>');
            }

            TabID++;

        }

        reverseRowsOrder();

    }

    function reverseRowsOrder() {
        TabID=1;
        var c = 0;
        var p = 0;
        var offsets = new Array();
        var rows = new Array();

        while (TabID<=TabsNumber) {
            var tab = document.getElementById("Tab" + TabID);
            if (offsets.length == 0 || tab.offsetTop != offsets[offsets.length - 1]) {
                c = 0;
                rows[p++] = new Array();
                offsets[offsets.length] = tab.offsetTop;
            }
            rows[p-1][c++] = tab;
            TabID++;
        }

        for (var i = rows.length - 1 ; i >= 0; i--) {
            var trId = 'tr_' + i;
            document.write('<tr id="' + trId + '">');
            for (var j = 0; j <= rows[i].length - 1; j++) {
                var td = rows[i][j];
                document.getElementById(trId).innerHTML = document.getElementById(trId).innerHTML + td.outerHTML;
                td.outerHTML = "";
            }
            document.write('</tr>');
        }
    }
</script>

	</tr>
</table> 
	
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<div style="width: 100%">
				<!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B">
			<div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="tablebox_center">
				<table border="0" cellpadding="0" cellspacing="0">
					<c:set var="itemNum" value="${0}" />
					<c:set var="numOfTr" value="0" />
					<c:if test='${section.section.title != ""}'>
						<tr class="aka_stripes">
							<td class="aka_header_border">
								<b><fmt:message	key="title" bundle="${resword}" />:&nbsp;<c:out value="${section.section.title}" escapeXml="false" /></b>
							</td>
						</tr>
					</c:if>
					<c:if test='${section.section.subtitle != ""}'>
						<tr class="aka_stripes">
							<td class="aka_header_border">
								<fmt:message key="subtitle" bundle="${resword}" />:&nbsp;<c:out	value="${section.section.subtitle}"	escapeXml="false" />
							</td>
						</tr>
					</c:if>
					<c:if test='${section.section.instructions != ""}'>
						<tr class="aka_stripes">
							<td class="aka_header_border">
								<fmt:message key="instructions" bundle="${resword}" />:&nbsp;<c:out value="${section.section.instructions}"	escapeXml="false" />
							</td>
						</tr>
					</c:if>
					<c:forEach var="displayItem" items="${section.displayItemGroups}" varStatus="itemStatus">
						<c:choose>
						<c:when test="${displayItem.inGroup == true}">
							<c:set var="sectionBorders" value="${section.section.borders}" />
							<tr>
								<td class="annotated_groupOID">
									<span class="annotated_groupOID">${displayItem.itemGroup.itemGroupBean.oid}</span>
								</td>
							</tr>
							<tr>
								<td>
									<c:set var="uniqueId" value="0" />
									<c:set var="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
									<c:set var="totalColsPlusSubcols" value="0" /> 
									<c:set var="questionNumber" value="" /> 
									<c:if test="${!(repeatParentId eq 'Ungrouped')}">
										<%-- implement group header--%>
										<c:if test="${!(displayItem.itemGroup.groupMetaBean.header eq '')}">
											<div class="aka_group_header">
												<strong>
													<c:out value="${displayItem.itemGroup.groupMetaBean.header}" escapeXml="false" />
												</strong>
											</div>
										</c:if>
										<table border="0" cellspacing="0" cellpadding="0" class="aka_form_table" width="100%">
										<thead>
											<tr>
												<%-- if there are horizontal checkboxes or radios anywhere in the group...--%>
												<c:set var="isHorizontal" scope="request" value="${false}" />
												<c:forEach var="theItem" items="${displayItem.itemGroup.items}">
													<c:set var="questionNumber" value="${theItem.metadata.questionNumberLabel}" />
													<%-- We have to add a second row of headers if the response_layout property is horizontal for checkboxes. --%>
													<c:set var="isHorizontalCellLevel" scope="request" value="${false}" />
													<c:if test="${theItem.metadata.responseLayout eq 'horizontal' || theItem.metadata.responseLayout eq 'Horizontal'}">
														<c:set var="isHorizontal" scope="request" value="${true}" />
														<c:set var="isHorizontalCellLevel" scope="request" value="${true}" />
														<c:set var="optionsLen" value="${fn:length(theItem.metadata.responseSet.options)}" />
													</c:if>
													<%-- compute total columns value for the add button row colspan attribute--%>
													<c:choose>
													<c:when	test="${isHorizontalCellLevel && sectionBorders == 1 && (theItem.metadata.responseSet.responseType.name eq 'checkbox' || theItem.metadata.responseSet.responseType.name eq 'radio')}">
														<th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
															<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
													</c:when>
													<c:when	test="${isHorizontalCellLevel && (theItem.metadata.responseSet.responseType.name eq 'checkbox' || theItem.metadata.responseSet.responseType.name eq 'radio')}">
														<th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders">
														<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
													</c:when>
													<c:when test="${sectionBorders == 1}">
														<th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
														<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
													</c:when>
													<c:otherwise>
														<th class="aka_headerBackground aka_padding_large aka_cellBorders">
															<c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
													</c:otherwise>
													</c:choose>
													<c:if test="${!(empty questionNumber)}">
															<span style="margin-right: 1em"><c:out value="${questionNumber}" escapeXml="false" /></span>
													</c:if>
													<c:choose>
													<c:when test="${theItem.metadata.header == ''}">
														<c:out value="${theItem.metadata.leftItemText}" escapeXml="false" />
													</c:when>
													<c:otherwise>
														<c:out value="${theItem.metadata.header}" escapeXml="false" />
													</c:otherwise>
													</c:choose>
														</th>
												</c:forEach>
												<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
													<c:choose>
													<c:when test="${sectionBorders == 1}">
														<th class="aka_headerBackground aka_padding_large aka_cellBorders_dark" />
													</c:when>
													<c:otherwise>
														<th class="aka_headerBackground aka_padding_large aka_cellBorders" />
													</c:otherwise>
													</c:choose>
												</c:if>
											</tr>
											<c:if test="${isHorizontal}">
												<%-- create another row --%>
												<tr>
													<c:forEach var="theItem" items="${displayItem.itemGroup.items}">
														<c:set var="isHorizontalCellLevel" scope="request" value="${false}" />
														<c:if test="${theItem.metadata.responseLayout eq 'horizontal' || theItem.metadata.responseLayout eq 'Horizontal'}">
															<c:set var="isHorizontalCellLevel" scope="request" value="${true}" />
														</c:if>
														<c:choose>
														<c:when test="${isHorizontalCellLevel && sectionBorders == 1 && (theItem.metadata.responseSet.responseType.name eq 'checkbox' || theItem.metadata.responseSet.responseType.name eq 'radio')}">
															<c:forEach var="respOpt" items="${theItem.metadata.responseSet.options}">
																<th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
																	<c:out value="${respOpt.text}" />
																</th>
															</c:forEach>
														</c:when>
														<c:when test="${isHorizontalCellLevel && (theItem.metadata.responseSet.responseType.name eq 'checkbox' || theItem.metadata.responseSet.responseType.name eq 'radio')}">
															<c:forEach var="respOpt" items="${theItem.metadata.responseSet.options}">
																<th class="aka_headerBackground aka_padding_large aka_cellBorders">
																	<c:out value="${respOpt.text}" />
																</th>
															</c:forEach>
														</c:when>
														<c:when test="${sectionBorders == 1}">
															<th class="aka_headerBackground aka_padding_large aka_cellBorders_dark" />
														</c:when>
														<c:otherwise>
															<th class="aka_headerBackground aka_padding_large aka_cellBorders" />
														</c:otherwise>
														</c:choose>
													</c:forEach>
														<th/>
												</tr>
											</c:if>
										</thead>

										<tbody>
											<c:set var="uniqueId" value="${0}" />
											<c:set var="repeatRowCount" value="0" />
											<c:set var="repeatRowCount" value="${fn:length(displayItem.itemGroups)}" />
											<!-- there are data posted already -->
											<!-- repeating rows in an item group  start-->
											<c:forEach var="bodyItemGroup" items="${displayItem.itemGroups}" varStatus="status">
												<c:set var="columnNum" value="1" />
												<!-- hasError is set to true when validation error happens-->
												<c:choose>
												<c:when test="${status.last && !status.first}">
												<!-- for the last but not the first row and only row, we need to use [] so the repetition javascript can copy it to create new row-->
													<tr id="<c:out value="${repeatParentId}"/>" repeat="template">
														<c:forEach var="bodyItem" items="${bodyItemGroup.items}">
															<c:set var="itemNum" value="${itemNum + 1}" />
															<c:set var="isHorizontalCellLevel" scope="request" value="${false}" />
															<c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' || bodyItem.metadata.responseLayout eq 'Horizontal'}">
																<c:set var="isHorizontalCellLevel" scope="request" value="${true}" />
															</c:if>
															<c:choose>
															<c:when test="${isHorizontalCellLevel && sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' || bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
																<%-- For horizontal checkboxes, radio buttons--%>
																<c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
																	<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders_dark">
																		<c:set var="displayItem" scope="request" value="${bodyItem}" />
																		<c:set var="responseOptionBean" scope="request" value="${respOption}" />
																		<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																			<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																			<c:param name="rowCount" value="${uniqueId}" />
																			<c:param name="isLast" value="${false}" />
																			<c:param name="tabNum" value="${itemNum}" />
																			<c:param name="isHorizontal" value="${isHorizontalCellLevel}" />
																			<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																			<c:param name="isLocked" value="${isLocked}" />
																		</c:import>
																	</td>
																</c:forEach>
															</c:when>
															<c:when test="${isHorizontalCellLevel && (bodyItem.metadata.responseSet.responseType.name eq 'radio' || bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
															<%-- For horizontal checkboxes, radio buttons--%>
																<c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
																	<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders">
																		<c:set var="displayItem" scope="request" value="${bodyItem}" />
																		<c:set var="responseOptionBean" scope="request" value="${respOption}" />
																		<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																			<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																			<c:param name="rowCount" value="${uniqueId}" />
																			<c:param name="isLast" value="${false}" />
																			<c:param name="tabNum" value="${itemNum}" />
																			<c:param name="isHorizontal" value="${isHorizontalCellLevel}" />
																			<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																			<c:param name="isLocked" value="${isLocked}" />
																		</c:import>
																	</td>
																</c:forEach>
															</c:when>
															<c:when test="${sectionBorders == 1}">
																<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders_dark">
																	<c:set var="displayItem" scope="request" value="${bodyItem}" />
																	<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																		<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																		<c:param name="rowCount" value="${uniqueId}" />
																		<c:param name="isLast" value="${false}" />
																		<c:param name="tabNum" value="${itemNum}" />
																		<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																		<c:param name="isLocked" value="${isLocked}" />
																	</c:import>
																</td>
															</c:when>
															<%-- could be a radio or checkbox that is not horizontal --%>
															<c:otherwise>
																<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders">
																	<c:set var="displayItem" scope="request" value="${bodyItem}" />
																	<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																		<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																		<c:param name="rowCount" value="${uniqueId}" />
																		<c:param name="isLast" value="${false}" />
																		<c:param name="tabNum" value="${itemNum}" />
																		<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																		
																		<c:param name="isLocked" value="${isLocked}" />
																	</c:import>
																</td>
															</c:otherwise>
															</c:choose>
															<c:set var="columnNum" value="${columnNum+1}" />
														</c:forEach>
														<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
															<c:choose>
															<c:when test="${sectionBorders == 1}">
																<td class="aka_padding_norm aka_cellBorders_dark">
																	<input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
																	<button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
																</td>
															</c:when>
															<c:otherwise>
																<td class="aka_padding_norm aka_cellBorders">
																	<input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
																	<button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
																</td>
															</c:otherwise>
															</c:choose>
														</c:if>
													</tr>
												</c:when>
												<c:otherwise>
													<!--  not the last row -->
													<tr repeat="0">
														<c:set var="columnNum" value="1" />
														<c:forEach var="bodyItem" items="${bodyItemGroup.items}">
															<c:set var="itemNum" value="${itemNum + 1}" />
															<c:set var="isHorizontalCellLevel" scope="request" value="${false}" />
															<c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' || bodyItem.metadata.responseLayout eq 'Horizontal'}">
																<c:set var="isHorizontalCellLevel" scope="request" value="${true}" />
															</c:if>
															<c:choose>
															<c:when test="${isHorizontalCellLevel && sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' || bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
																<%-- For horizontal checkboxes, radio buttons--%>
																<c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
																	<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders_dark">
																		<c:set var="displayItem" scope="request" value="${bodyItem}" />
																		<c:set var="responseOptionBean" scope="request" value="${respOption}" />
																		<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																			<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																			<c:param name="rowCount" value="${uniqueId}" />
																			<c:param name="isLast" value="${false}" />
																			<c:param name="tabNum" value="${itemNum}" />
																			<c:param name="isHorizontal" value="${isHorizontalCellLevel}" />
																			<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																			<c:param name="isLocked" value="${isLocked}" />
																		</c:import>
																	</td>
																</c:forEach>
															</c:when>
															<c:when test="${isHorizontalCellLevel && (bodyItem.metadata.responseSet.responseType.name eq 'radio' || bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
																<%-- For horizontal checkboxes, radio buttons--%>
																<c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
																	<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders">
																		<c:set var="displayItem" scope="request" value="${bodyItem}" />
																		<c:set var="responseOptionBean" scope="request" value="${respOption}" />
																		<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																			<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																			<c:param name="rowCount" value="${uniqueId}" />
																			<c:param name="isLast" value="${false}" />
																			<c:param name="tabNum" value="${itemNum}" />
																			<c:param name="isHorizontal" value="${isHorizontalCellLevel}" />
																			<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																			<c:param name="isLocked" value="${isLocked}" />
																		</c:import>
																	</td>
																</c:forEach>
															</c:when>
															<c:when test="${sectionBorders == 1}">
																<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders_dark">
																	<c:set var="displayItem" scope="request" value="${bodyItem}" /> 
																	<c:import url="../submit/showAnnotatedGroupItemInput.jsp"> 
																	<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																		<c:param name="rowCount" value="${uniqueId}" />
																		<c:param name="isLast" value="${false}" />
																		<c:param name="tabNum" value="${itemNum}" />
																		<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																		<c:param name="isLocked" value="${isLocked}" />
																	</c:import>
																</td>
															</c:when>
															<%-- could be a radio or checkbox that is not horizontal --%>
															<c:otherwise>
																<td id="itemHolderId_${uniqueId}input${bodyItem.item.id}" class="itemHolderClass aka_padding_norm aka_cellBorders">
																	<c:set var="displayItem" scope="request" value="${bodyItem}" /> 
																	<c:import url="../submit/showAnnotatedGroupItemInput.jsp">
																		<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}" />
																		<c:param name="rowCount" value="${uniqueId}" />
																		<c:param name="isLast" value="${false}" />
																		<c:param name="tabNum" value="${itemNum}" />
																		<c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}" />
																		<c:param name="isLocked" value="${isLocked}" />
																	</c:import>
																</td>
															</c:otherwise>
															</c:choose>
															<c:set var="columnNum" value="${columnNum+1}" />
														</c:forEach>
														<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
															<c:choose>
															<c:when test="${sectionBorders == 1}">
																<td class="aka_padding_norm aka_cellBorders_dark">
																	<%-- check for manual in the input name; if rowCount > 0 then manual will be in the name --%>
                                                                    <c:choose>
																	<c:when test="${uniqueId ==0}">
																		<input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
																	</c:when>
																	<c:otherwise>
																		<input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
																	</c:otherwise>
																	</c:choose>
																	<button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
																</td>
															</c:when>
															<c:otherwise>
																<td class="aka_padding_norm aka_cellBorders">
																	<%-- check for manual in the input name; if rowCount > 0 then manual will be in the name --%>
                                                                    <c:choose>
																	<c:when test="${uniqueId ==0}"> 
																		<input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
																	</c:when>
																	<c:otherwise>
																		<input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
																	</c:otherwise>
																	</c:choose>
																	<button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove"></button>
																</td>
															</c:otherwise>
															</c:choose>
														</c:if>
													</tr>
												</c:otherwise>
												</c:choose>
												<c:set var="uniqueId" value="${uniqueId +1}" />
												<!-- repeating rows in an item group end -->
											</c:forEach>
											<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
												<tr>
													<c:choose>
													<c:when test="${sectionBorders == 1}">
														<%-- Add 1 to the totalColsPlusSubcols variable to accomodate the cell containing the remove button--%>
														<td class="aka_padding_norm aka_cellBorders_dark" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
															<button stype="add" type="button" template="<c:out value="${repeatParentId}"/>" class="button_search">
																<fmt:message key="add" bundle="${resword}" />
															</button>
														</td>
													</c:when>
													<c:otherwise>
														<td class="aka_padding_norm aka_cellBorders" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
															<button stype="add" type="button" template="<c:out value="${repeatParentId}"/>" class="button_search">
																<fmt:message key="add" bundle="${resword}" />
															</button>
														</td>
													</c:otherwise>
													</c:choose>
												</tr>
											</c:if>
										</tbody>
										</table>
									</c:if>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
						<%-- SHOW THE PARENT FIRST --%>
							<c:if test="${displayItem.singleItem.metadata.parentId == 0}">
							<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->
								<c:if test="${displayItem.singleItem.metadata.columnNumber <=1}">
									<c:if test="${numOfTr > 0 }">
												</td>
											</tr>
										</table>
									</c:if>
									<c:set var="numOfTr" value="${numOfTr+1}" />
									<c:if test="${!empty displayItem.singleItem.metadata.header}">
										<tr class="aka_stripes">
											<td class="table_cell_left aka_stripes">
												<b><c:out value="${displayItem.singleItem.metadata.header}" escapeXml="false" /></b>
											</td>
										</tr>
									</c:if>
									<c:if test="${!empty displayItem.singleItem.metadata.subHeader}">
										<tr class="aka_stripes">
											<td class="table_cell_left">
												<c:out value="${displayItem.singleItem.metadata.subHeader}"	escapeXml="false" />
											</td>
										</tr>
									</c:if>
									<tr>
										<td class="table_cell_left">
											<table border="0" class="itemHolderClass" id="itemHolderId_input${displayItem.singleItem.item.id}">
												<c:set var="prevItemHolderId" value="${displayItem.singleItem.item.id}"/>
												<tr>
													<td valign="top">
								</c:if> 
								<c:if test="${displayItem.singleItem.metadata.columnNumber >1}">
									<td valign="top">
								</c:if>
								<table border="0" <c:if test="${prevItemHolderId != displayItem.singleItem.item.id}">class="itemHolderClass" id="itemHolderId_input${displayItem.singleItem.item.id}"<c:set var="prevItemHolderId" value="${displayItem.singleItem.item.id}"/></c:if>>
									<tr>
										<td valign="top" class="aka_ques_block"></td>
										<c:if test="${displayItem.singleItem.metadata.textFromLeftItemText==''}">
											<td valign="top" class="aka_text_block">
												&nbsp;
											</td>
										</c:if>
										<td class="annotated_itemOID">
											<span class="annotated_itemOID">
												<c:choose>
													<c:when test="${study.studyParameterConfig.annotatedCrfSasItemNames == 'yes'}">
														${sasItemNamesMap[displayItem.singleItem.item.name]}
													</c:when>
													<c:otherwise>
														${displayItem.singleItem.item.name}
													</c:otherwise>
												</c:choose>
											</span>
										</td>
									</tr>
									<tr>
										<td valign="top" class="aka_ques_block">
											<c:out value="${displayItem.singleItem.metadata.questionNumberLabel}" escapeXml="false" />
										</td>
										<td valign="top" class="aka_text_block">
											<c:out value="${displayItem.singleItem.metadata.leftItemText}" escapeXml="false" />
										</td>
										<td valign="top" nowrap="nowrap">
											<%-- display the HTML input tag --%> 
											<c:set var="displayItem" scope="request" value="${displayItem.singleItem}" /> 
											<c:import url="../submit/showAnnotatedItemInput.jsp">
												<c:param name="tabNum" value="${itemNum}" />
												<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}"/>
												<c:param name="defaultValue" value="${displayItem.singleItem.metadata.defaultValue}" />
												<c:param name="respLayout" value="${displayItem.singleItem.metadata.responseLayout}" />
												<c:param name="isLocked" value="${isLocked}" />
												<c:param name="isLast" value="${false}"/>
											</c:import>
										</td>
										<c:if test='${displayItem.singleItem.item.units != ""}'>
											<td valign="top"><c:out value="(${displayItem.singleItem.item.units})" escapeXml="false" /></td>
										</c:if>
										<td valign="top"><c:out value="${displayItem.singleItem.metadata.rightItemText}" escapeXml="false" /></td>
									</tr>
								</table>
								</td>
								<c:if test="${itemStatus.last}">
												</tr>
											</table>
										</td>
									</tr>
								</c:if>

								<c:if test="${displayItem.singleItem.numChildren > 0}">
									<tr>
									<%-- NOW SHOW THE CHILDREN --%>
										<td class="table_cell">
											<table border="0">
												<c:set var="notFirstRow" value="${0}" />
												<c:forEach var="childItem" items="${displayItem.singleItem.children}">
													<c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
													<c:if test="${currColumn == 1}">
														<c:if test="${notFirstRow != 0}">
															</tr>
														</c:if>
														<tr>
														<c:set var="notFirstRow" value="${1}" />
														<td valign="top">&nbsp;</td>
													</c:if>
			<%-- this for loop "fills in" columns left blank e.g., if the first childItem has column number 2, and the next one has column number 5,
			then we need to insert one blank column before the first childItem, and two blank columns between the second and third children --%>
													<c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
														<td valign="top">&nbsp;</td>
													</c:forEach>
													<td valign="top">
														<table border="0" class="itemHolderClass" id="itemHolderId_input${childItem.item.id}">
															<c:set var="prevItemHolderId" value="${childItem.item.id}"/>
															<tr>
																<td valign="top" class="aka_ques_block">
																	<c:out	value="${childItem.metadata.questionNumberLabel}" escapeXml="false" />
																</td>
																<td valign="top" class="aka_text_block">
																	<c:out value="${childItem.metadata.leftItemText}" escapeXml="false" />
																</td>
																<td valign="top" nowrap="nowrap">
									<%-- display the HTML input tag --%> 
																	<c:set var="itemNum" value="${itemNum + 1}" /> 
																	<c:set var="displayItem" scope="request" value="${childItem}" /> 
																	<c:import url="../submit/showAnnotatedItemInput.jsp">
																		<c:param name="tabNum" value="${itemNum}" />
																		<c:param name="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}"/>
																		<c:param name="defaultValue" value="${childItem.metadata.defaultValue}" />
																		<c:param name="respLayout" value="${childItem.metadata.responseLayout}" />
																		<c:param name="isLocked" value="${isLocked}" />
																		<c:param name="isLast" value="${false}"/>
																	</c:import>
																</td>
																<c:if test='${childItem.item.units != ""}'>
																	<td valign="top"><c:out value="(${childItem.item.units})" escapeXml="false" /></td>
																</c:if>
																<td valign="top"><c:out value="${childItem.metadata.rightItemText}" escapeXml="false" /></td>
															</tr>
													<%--BWP: try this--%>
															<tr>
																<td valign="top" colspan="4" style="text-align: right">
																	<c:import url="../showMessage.jsp">
																		<c:param name="key" value="input${childItem.item.id}" />
																	</c:import>
																</td>
															</tr>
														</table>
													</td>
												</c:forEach>
												</tr>
											</table>
										</td>
									</tr>
								</c:if>
							</c:if>
						</c:otherwise>
						</c:choose>
						<c:set var="itemNum" value="${itemNum + 1}" />
					</c:forEach>
				
			</div>
			</div></div></div></div>
			</div></div></div></div>
			</div>
		</td>
	</tr>
</table> <!-- End Table Contents -->

</div>

</body>
<jsp:include page="../include/changeTheme.jsp" />
</html>
