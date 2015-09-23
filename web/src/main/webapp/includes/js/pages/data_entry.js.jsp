<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="sectionNum" value="0"/>
<c:forEach var="section" items="${toc.sections}">
	<c:set var="sectionNum" value="${sectionNum+1}"/>
</c:forEach>
<script type="text/javascript" language="JavaScript">
	var checkboxObject;

	$(document).ready(function(){
		$("#CRF_infobox_closed").css("display","block");
		$("#CRF_infobox_open").css("display","none");
	});

	<c:if test="${dataEntryStage.isInitialDE()}">
	var formChanged;

	$(document).ready(function () {
		formChanged = false;
	});

	$(document).ready(function () {
		checkRandomizationCRF();
	});
	</c:if>

	function getSib(theSibling){
		var sib;
		do {
			sib  = theSibling.previousSibling;
			if(sib.nodeType != 1){
				theSibling = sib;
			}
		} while(!(sib.nodeType == 1));

		return sib;
	}

	function getFocused(f){
		var v = document.getElementById(f);
		v.focus();
	}

	function disableSubmit() {
		var srh = document.getElementById('srh');
		var srl = document.getElementById('srl');
		var seh = document.getElementById('seh');
		var sel = document.getElementById('sel');
		if (srh != undefined)
			srh.disabled = true;
		if (srl != undefined)
			srl.disabled = true;
		if (seh != undefined)
			seh.disabled = true;
		if (sel != undefined)
			sel.disabled = true;
	}

	function setParameterForDN(field, parameterName, value) {
		<c:if test="${dataEntryStage.isAdmin_Editing()}">
		setParameterForDNWithPath('0', field, parameterName, value, '${pageContext.request.contextPath}');
		</c:if>
	}

	var TabsNumber = <c:out value="${sectionNum}"/>;

	var frameWidth = 1000;
	var tabWidth = frameWidth / TabsNumber;

	// Number of tabs to display at a time
	var TabsShown = TabsNumber;
	// Labels to display on each tab (name of CRF)
	var TabLabel = new Array(TabsNumber);
	var TabFullName = new Array(TabsNumber);
	var TabSectionId = new Array(TabsNumber);

	<c:set var="eventDefinitionCRFDoubleEntryMode" value="${toc.eventDefinitionCRF.doubleEntry}"/>
	<c:choose>
	<c:when test="${eventDefinitionCRFDoubleEntryMode}">
	<c:set var="markCRFMethodName" value="displayMessageFromCheckbox(this, true)"/>
	</c:when>
	<c:otherwise>
	<c:set var="markCRFMethodName" value="displayMessageFromCheckbox(this, undefined)"/>
	</c:otherwise>
	</c:choose>

	<c:set value="0" var="count"/>
	<c:forEach var="section" items="${toc.sections}">
	<c:set var="completedItems" value="${section.numItemsCompleted}"/>
	<c:if test="${toc.eventDefinitionCRF.doubleEntry}">
	<c:set var="completedItems" value="${section.numItemsNeedingValidation}"/>
	</c:if>
	TabFullName[${count}] = "<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";
	TabSectionId[<c:out value="${count}"/>] = <c:out value="${section.id}"/>;
	TabLabel[<c:out value="${count}"/>] = "<c:out value="${section.label}"/>" + "<span id='secNumItemsCom<c:out value="${count}"/>' style='font-weight: normal;'>  (<c:out value="${completedItems}"/>/<c:out value="${section.numItems}" />)</span>";
	<c:set value="${count+1}" var="count"/>
	</c:forEach>

	$(window).load(function() {
		DisplaySectionTabs();
	});

	function DisplaySectionTabs() {
		var TabID = 1
		$(".sectionsContainer td").remove();
		while (TabID <= TabsNumber) {
			sectionId = TabSectionId[TabID - 1];
			url = "${formAction}?eventCRFId=" + <c:out value="${section.eventCRF.id}"/> +"&sectionId=" + sectionId + "&tabId=" + TabID + "${justCloseWindow ? '&cw=1' : ''}";
			currTabID = <c:out value="${tabId}"/>;
			var sectionContent = '<td nowrap style="display:inline-block;" class="crfHeaderTabs" valign="bottom" id="Tab' + TabID + '">';
			if (TabID != currTabID) {
				sectionContent += '<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">'
				+ '<a class="tabtext" style="color: ' + theme.mainColor + '" title="' + TabFullName[(TabID - 1)] + '" href=' + url + ' onclick="return checkSectionStatus(this, ' + "'<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>'" + ');">' + TabLabel[(TabID - 1)] + '</a></div></div></div></div>'
				+ '<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>'
				+ '</td>';
			}
			else {
				sectionContent += '<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">'
				+ '<span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>'
				+ '<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h">'
				+ '<div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID - 1)] + '</span></div></div></div></div>'
				+ '</td>';
			}
			$(".sectionsContainer").append(sectionContent);
			TabID++;
		}
	}

	window.onbeforeunload = function(){
		if (window.opener) {
			window.opener.location.reload(true);
		}
	}
</script>

<c:if test="${study.studyParameterConfig.autoTabbing == 'yes'}">
	<script>initAutotabbing();</script>
</c:if>