<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="resexception"/>

<c:set var="sectionNum" value="0"/>
<c:forEach var="section" items="${toc.sections}">
	<c:set var="sectionNum" value="${sectionNum+1}"/>
</c:forEach>
<script type="text/javascript" language="JavaScript">
	var checkboxObject;

	var areCheckboxesValid = function() {
		var partialSave = false;
		var markComplete = false;
		$("input[name=markPartialSaved]").each(function() {
			partialSave = !partialSave ? this.checked : partialSave;
		});
		$("input[name=markComplete]").each(function() {
			markComplete = !markComplete ? this.checked : markComplete;
		});
		return !(partialSave && markComplete);
	}

	var submitCrfForm = function(button) {
		disableSubmit(true);
		var formIsValid = true;
		if (!areCheckboxesValid()) {
			formIsValid	= false;
			alertDialog({ message: "<fmt:message key="crfForm.checkboxesErrorMsg" bundle="${resexception}"/>", height: 150, width: 400 });
		}
		if (formIsValid) {
			$("form[name=crfForm]").append('<input type="hidden" name="' + $(button).attr("name") + '" value="' + $(button).val() + '">');
			$("form[name=crfForm]").submit();
		} else {
			disableSubmit(false);
		}
	}

	function disableSubmit(value) {
		$("input[type=button],input[type=submit]").attr("disabled", value);
	}

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

	function setParameterForDN(field, parameterName, value) {
		<c:if test="${dataEntryStage.isAdmin_Editing()}">
		setParameterForDNWithPath('0', field, parameterName, value, '${pageContext.request.contextPath}');
		</c:if>
	}

	var TabsNumber = ${sectionNum};
	var frameWidth = 1000;
	var tabWidth = frameWidth / TabsNumber;
	var TabsShown = TabsNumber;
	var TabLabel = new Array(TabsNumber);
	var TabFullName = new Array(TabsNumber);
	var TabSectionId = new Array(TabsNumber);

	<c:forEach var="section" items="${toc.sections}" varStatus="status">
		<c:set var="completedItems" value="${section.numItemsCompleted}"/>
		<c:if test="${toc.eventDefinitionCRF.doubleEntry}">
			<c:set var="completedItems" value="${section.numItemsNeedingValidation}"/>
		</c:if>
		<c:choose>
			<c:when test="${sectionIdToEvCRFSection[section.id] ne null && sectionIdToEvCRFSection[section.id].partialSaved}">
				<c:set var="section_icon" value="<img title='partial data entry' style='position: relative; margin-bottom: -3px' alt='partial data entry' src='images/icon_PartialDE.gif'/>"/>
			</c:when>
			<c:otherwise>
			   	<c:set var="section_icon" value=""/>
			</c:otherwise>
		</c:choose>
		TabFullName[${status.index}] = "<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";
		TabSectionId[<c:out value="${status.index}"/>] = <c:out value="${section.id}"/>;
		TabLabel[<c:out value="${status.index}"/>] = "<c:out value="${section.label}"/>" + "<span id='secNumItemsCom<c:out value="${status.index}"/>' style='font-weight: normal;'>  (<c:out value="${completedItems}"/>/<c:out value="${section.numItems}" />) ${section_icon}</span>";
	</c:forEach>

	$(window).load(function() {
		DisplaySectionTabs();
	});

	function DisplaySectionTabs() {
		var TabID = 1;
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
	};

	/********************************************************************
	 *  Functions for input type "File"
	 *********************************************************************/

	var replaceMessage = '<fmt:message key="replace" bundle="${resword}"/>';
	var cancelReplaceMessage = '<fmt:message key="cancel_replace" bundle="${resword}"/>';
	var removeMessage = '<fmt:message key="remove" bundle="${resword}"/>';
	var cancelRemoveMessage = '<fmt:message key="cancel_remove" bundle="${resword}"/>';

	function replaceSwitch(eventCRFId, itemId, filename, filePathName, status, isRepeating) {
		var id = 'rp'
		var rp = document.getElementById(id + itemId);
		var div = document.getElementById('div' + itemId);
		var a = document.getElementById('a' + itemId);
		var ft = document.getElementById('ft' + itemId);
		var up = document.getElementById('up' + itemId);
		<%-- uploadLink is different from showItemInput.jsp --%>
		var uploadLink = 'UploadFile?submitted=no&' + (isRepeating ? 'inputName=' : 'itemId=') + itemId;
		var downloadLink = 'DownloadAttachedFile?eventCRFId=' + eventCRFId + '&fileName=' + filePathName;

		if (rp.getAttribute('value') == replaceMessage) {
			if (a) {
				div.appendChild(a);
				div.removeChild(a);
			}
			if (!ft) {
				var new_ft = document.createElement('input');
				new_ft.setAttribute("id", "ft" + itemId);
				new_ft.setAttribute("type", "text");
				new_ft.setAttribute("name", "fileText" + itemId);
				new_ft.setAttribute("disabled", "disabled");
				div.appendChild(new_ft);
				var new_up = document.createElement('input');
				new_up.setAttribute("id", "up" + itemId);
				new_up.setAttribute("type", "button");
				new_up.setAttribute("name", "uploadFile" + itemId);
				new_up.setAttribute("value", '<fmt:message key="click_to_upload" bundle="${resword}"/>');
				new_up.onclick = function () {
					var itemid = itemId;
					javascript:openDocWindow(uploadLink)
				};
				div.appendChild(new_up);
			}
			var fa = document.getElementById('fa' + itemId);
			fa.setAttribute("value", "upload");
			div.appendChild(fa);
			switchStr(itemId, "rm", "value", cancelRemoveMessage, removeMessage);
		} else if (rp.getAttribute('value') == cancelReplaceMessage) {
			if (ft) {
				div.appendChild(ft);
				div.appendChild(up);
				div.removeChild(ft);
				div.removeChild(up);
			}
			if (!a) {
				if (status == 'found') {
					var new_a = document.createElement('a');
					new_a.href = downloadLink;
					new_a.setAttribute("id", "a" + itemId);
					new_a.appendChild(document.createTextNode(filename));
					div.appendChild(new_a);
				} else if (status == 'notFound') {
					var new_a = document.createElement('del');
					new_a.setAttribute("id", "a" + itemId);
					new_a.innerHTML = filename;
					div.appendChild(new_a);
				}
			}
			var fa = document.getElementById('fa' + itemId);
			fa.setAttribute("value", "noAction");
			div.appendChild(fa);
		}

		switchValue(itemId, id, replaceMessage, cancelReplaceMessage);
	}

	function removeSwitch(eventCRFId, itemId, filename, filePathName, status) {
		var id = 'rm';
		var rm = document.getElementById(id + itemId);
		var div = document.getElementById('div' + itemId);
		var a = document.getElementById('a' + itemId);
		var ft = document.getElementById('ft' + itemId);
		var up = document.getElementById('up' + itemId);
		var fa = document.getElementById('fa' + itemId);
		var input = document.getElementById(itemId);
		if (!input) {
			input = document.getElementById('input' + itemId);
		}
		var downloadLink = 'DownloadAttachedFile?eventCRFId=' + eventCRFId + '&fileName=' + filePathName;
		if (rm.getAttribute('value') == removeMessage) {
			input.setAttribute("value", "");
			if (a) {
				div.appendChild(a);
				div.removeChild(a);
			}
			if (ft) {
				div.appendChild(ft);
				div.appendChild(up);
				div.removeChild(ft);
				div.removeChild(up);
				switchStr(itemId, "rp", "value", cancelReplaceMessage, replaceMessage);
			}
			var new_a = document.createElement('del');
			new_a.setAttribute("id", "a" + itemId);
			if (navigator.appName == "Microsoft Internet Explorer") {
				new_a.style.setAttribute("color", "red");
			} else {
				new_a.setAttribute("style", "color:red");
			}
			new_a.innerHTML = filename;
			div.appendChild(new_a);
			fa.setAttribute("value", "erase");
			div.appendChild(fa);
		} else if (rm.getAttribute('value') == cancelRemoveMessage) {
			input.setAttribute("value", filename);
			if (a) {
				div.appendChild(a);
				div.removeChild(a);
				if (status == 'found') {
					var new_a = document.createElement('a');
					new_a.href = downloadLink;
					new_a.setAttribute("id", "a" + itemId);
					new_a.appendChild(document.createTextNode(filename));
					div.appendChild(new_a);
				} else if (status == 'notFound') {
					var new_del = document.createElement('del');
					new_del.setAttribute("id", "a" + itemId);
					new_del.innerHTML = filename;
					div.appendChild(new_del);
				}
			}
			fa.setAttribute("value", "noAction");
			div.appendChild(fa);
		}

		switchValue(itemId, id, removeMessage, cancelRemoveMessage);
	}

	function switchValue(itemId, id, str1, str2) {
		var attribute = 'value';
		var e = document.getElementById(id + itemId);
		if (e.getAttribute(attribute) == str1) {
			e.setAttribute(attribute, str2);
		} else if (e.getAttribute(attribute) == str2) {
			e.setAttribute(attribute, str1);
		}
	}

	function switchStr(itemId, id, attribute, str1, str2) {
		var e = document.getElementById(id + itemId);
		if (e.getAttribute(attribute) == str1) {
			e.setAttribute(attribute, str2);
		}
	}

</script>

<c:if test="${study.studyParameterConfig.autoTabbing == 'yes'}">
	<script>initAutotabbing();</script>
</c:if>