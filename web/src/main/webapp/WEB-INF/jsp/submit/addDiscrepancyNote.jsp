<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>

<html>    
<head>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<title><fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="add_discrepancy_note" bundle="${resword}"/></title>
<link rel="icon" href="<c:url value='${faviconUrl}'/>" />
<link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />
<link rel="stylesheet" href="<c:out value="${contextPath}" />/includes/styles.css?r=${revisionNumber}" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>

<link rel="stylesheet" href="includes/jmesa/jmesa.css?r=${revisionNumber}" type="text/css">
<link rel="stylesheet" href="includes/jquery-ui.css"  type="text/css"/>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>

<script language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>

<style type="text/css">
.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}
</style>

<ui:theme/>

<script language="JavaScript">
function leftnavExpand(strLeftNavRowElementName){

    var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
        objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
    }
}

function hide(strLeftNavRowElementName){

    var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
        objLeftNavRowElement.display = "none";
    }
}

// typeId -  discrepancyNoteTypeId
// filter1 - whichResStatus

function setStatus(typeId,filter1,nw,ud,rs,cl,na) {
	objtr1=document.getElementById('res1');
	objtr2=document.getElementById('resStatusId');

	if (typeId == 2|| typeId ==4) {//annotation or reason for change
  		objtr2.disabled = true;
  		objtr2.options.length = 0;
  		objtr2.options[0]=new Option(na, '5');
	} else {
		objtr2.disabled = false;
		objtr2.options.length = 0;
		objtr2.options[0]=new Option(nw, '1');
		if(filter1=="22" || (filter1=="2" && typeId==1)) {
//	  		objtr2.options[1]=new Option(rs, '3');   //ClinCapture #42 DN worlflow simplification
		} else if(filter1=="1") {
			objtr2.options[1]=new Option(ud,'2');
			objtr2.options[2]=new Option(cl,'4');
		} else {
			objtr2.options[1]=new Option(ud,'2');
//			objtr2.options[2]=new Option(rs,'3');  //ClinCapture #42 DN worlflow simplification
			objtr2.options[2]=new Option(cl,'4');
		}
	}
}

function setResStatus(resStatusId, destinationUserId) {
	objtr1=document.getElementById('resStatusId');
	objtr2=document.getElementById('userAccountId');
	objtr4=document.getElementById('typeId');

	if (resStatusId == 3 || resStatusId == 4) { //Resolutiuon proposed or Closed
		// objtr2.disabled = false;
		objtr2.value = destinationUserId;
		// disable?
		objtr2.disabled = false;
		objtr3.removeAttribute('disabled');
		//objtr3.value = destinationUserId;
		// disable?
		//objtr3.disabled = false;
	}

	if (resStatusId == 5 && objtr4.value == 4) { // Not applicable AND Reason for Change
		objtr1.disabled = true;
	}
}

function setElements(typeId,user1,user2,filter1,nw,ud,rs,cl,na,isRFC) {
	setStatus(typeId,filter1,nw,ud,rs,cl,na);
	if(typeId == 3) {//query
		showElement(user1);
		showElement(user2);
		showElement('input');
		switchOnElement('inputDescription');
		hideElement('select');
		switchOffElement('selectDescription');

	} else {
		hideElement(user1);
		hideElement(user2);
		if (isRFC) {
			hideElement('input');
			switchOffElement('inputDescription');
			showElement('select');
			switchOnElement('selectDescription');
		} else {
			showElement('input');
			switchOnElement('inputDescription');
		}
	}
}

function sendFormDataViaAjax() {
	var aForm = $('form#noteForm');
	$( "div#divWithData").hide();
    $( "div#ajax-loader").show();
	$.ajax({
        type: aForm.attr('method'),
        url: aForm.attr('action'),
        data: aForm.serialize(),
        success: function (data) {
			function showDiv() {
				$("div#divWithData").html('');
				$("div#divWithData").append(data);
				$("select[id*=typeId]").change();
				$("div#ajax-loader").hide();
				$("div#divWithData").show();
			};

			if (data.indexOf('Save Done') > -1) {
				showDiv();
				changeDNFlagIconInParentWindow();
				window.close();
			} else if (data.indexOf('Show pop-up') > -1) {
				showDiv();
				changeDNFlagIconInParentWindow();
				showMessageForDN();
			} else if (data.indexOf('Error in data') > -1) {
				showDiv();
			} else {
				alertDialog({ message: "No response from server", height: 150, width: 500 });
			}
        }
    });
}

function displayMessageInBox(showOkButton, showCancelButton, headerOfMessageBox, message, notShowAgainMessage, themeColor){
    if ($("#confirmation").length == 0) {
		$("body").append(
            "<div id=\"confirmation\" class=\"message_box_confirmation\" style=\"display: none;\" title=\"" + headerOfMessageBox + "\">" +
                "<div style=\"clear: both; text-align: justify;\">"+ message +"</div>"+
                "<div style=\"clear: both; padding: 6px;\"><input type=\"checkbox\" id=\"ignoreBoxMSG\"/>" + notShowAgainMessage + "</div>" +
                "<div style=\"clear: both;\">" +
                    (showOkButton == true? "<input type=\"button\" value=\"OK\" class=\"button_medium\" onclick=\"clickOkInMessageBox();\" style=\"float: left;\">" : "") +
                    (showCancelButton == true? "<input type=\"button\" value=\"Cancel\" class=\"button_medium\" onclick=\"clickCancelInMessageBox();\" style=\"float: left; margin-left: 6px;\">" : "") +
                "</div>" +
            "</div>");

        $("#confirmation").dialog({
            autoOpen : false,
            modal : true,
            height: 180,
            width: 450,
			close: function(event, ui) {
				window.close();
				}
			});

        $("#confirmation #ignoreBoxMSG").unbind("change").bind("change", function() {
			setCookie("ignoreBoxMSG31", $(this).attr("checked") ? "yes" : "no", 1000);
        });

		if (themeColor == 'violet') {
			$('input.button_medium').not(".medium_back, .medium_cancel, .medium_continue, .medium_submit").css('background-image', 'url(images/violet/button_medium_BG.gif)');
			$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#AA62C6');
		}

		if (themeColor == 'green') {
			$('input.button_medium').not(".medium_back, .medium_cancel, .medium_continue, .medium_submit").css('background-image', 'url(images/green/button_medium_BG.gif)');
			$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#75b894');
		}

		if (themeColor == 'darkBlue') {
			$('input.button_medium').not(".medium_back, .medium_cancel, .medium_continue, .medium_submit").css('background-image', 'url(images/darkBlue/button_medium_BG.gif)');
			$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#2C6CAF');
		}

		if (getCookie("ignoreBoxMSG31") == "yes") {
			clickOkInMessageBox();
		} else {
			$("#confirmation #ignoreBoxMSG").attr('checked', false);
			$("#confirmation").dialog("open");
        }
    }
}

function clickOkInMessageBox(){
    $('#confirmation').dialog('close');
	window.close();
}

function showMessageForDN(){
    displayMessageInBox(true, false, '<fmt:message key="this_note_is_associated_with_data" bundle="${respage}"/>', '${popupMessage}', '<fmt:message key="do_not_show_this_message_anymore" bundle="${respage}"/>', '${newThemeColor}');
}

$(document).ready(function() {
	$("select[id*=typeId]").change();
	var aForm = $('form#noteForm');
	aForm.submit(function (e) {
		e.preventDefault();
		sendFormDataViaAjax();
	});
})

</script>
</head>
<body style="margin: 0px 12px 0px 12px;" onload="javascript:setStatus('<c:out value="${discrepancyNote.discrepancyNoteTypeId}"/>','<c:out value="${whichResStatus}"/>','<fmt:message key="New" bundle="${resterm}"/>','<fmt:message key="Updated" bundle="${resterm}"/>','<fmt:message key="Resolution_Proposed" bundle="${resterm}"/>','<fmt:message key="Closed" bundle="${resterm}"/>','<fmt:message key="Not_Applicable" bundle="${resterm}"/>');">
<%-- needs to run at first to possibly gray out the drop down, tbh 02/2010--%><!-- *JSP* submit/addDiscrepancyNote.jsp -->
<div style="float: left;">
	<h1>
		<span class="first_level_header">			<c:out value="${entityName}"/>: <fmt:message key="add_discrepancy_note" bundle="${resword}"/>
		</span>
	</h1>
</div><div style="float: right;">
	<a href="#" onclick="javascript:window.close();">
		<img name="close_box" alt="<fmt:message key="Close_Box" bundle="${resword}"/>" src="images/bt_Remove.gif" class="icon_dnBox">
	</a></div>
<div style="clear:both;"></div> 
<div class="alert">
<c:forEach var="message" items="${pageMessages}"> <c:out value="${message}" escapeXml="false"/>
</c:forEach>
</div>         
<form id="noteForm" method="POST" action="CreateDiscrepancyNote"><jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="name" value="<c:out value="${discrepancyNote.entityType}"/>">
<input type="hidden" name="column" value="<c:out value="${discrepancyNote.column}"/>">
<input type="hidden" name="itemId" value="<c:out value="${discrepancyNote.itemId}"/>"><input type="hidden" name="parentId" value="<c:out value="${discrepancyNote.parentDnId}"/>">
<input type="hidden" name="id" value="<c:out value="${discrepancyNote.entityId}"/>">
<input type="hidden" name="subjectId" value="<c:out value="${param.subjectId}"/>">
<input type="hidden" name="field" value="<c:out value="${discrepancyNote.field}"/>"><input type="hidden" name="writeToDB" value="<c:out value="${writeToDB}" />">
<input type="hidden" name="monitor" value="<c:out value="${monitor}" />">
<input type="hidden" name="newNote" value="<c:out value="${newNote}" />">
<input type="hidden" name="enterData" value="<c:out value="${enterData}" />">
<input type="hidden" name="eventCRFId" value="<c:out value="${eventCRFId}"/>">
<input type="hidden" name="errorFlag" value="<c:out value="${errorFlag}"/>">
<input type="hidden" name="isRFC" value="<c:out value="${isRFC}"/>">
<input type="hidden" name="originJSP" value="<c:out value="${originJSP}"/>">

<c:set var="name" value="${discrepancyNote.entityType}"/>
<!-- Entity box -->
<table border="0" cellpadding="0" cellspacing="0" style="float:left;">
	<tr><td valign="bottom">
	<table border="0" cellpadding="0" cellspacing="0">
    	<tr><td nowrap style="padding-right: 20px;">
            <div class="tab_BG_h"><div class="tab_R_h" style="padding-right: 0px;"><div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
	        <b><c:choose>
	            <c:when test="${entityName != '' && entityName != null }">
	                  "<c:out value="${entityName}"/>"
	            </c:when>
	            <c:otherwise>
	                <%-- nothing here; if entityName is blank --%>
	            </c:otherwise>
	        </c:choose>
			<fmt:message key="Properties" bundle="${resword}"/>:</b>
			</div></div></div>
		</td></tr>
    </table>
    </td></tr>
    <tr><td valign="top">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		<div class="textbox_center">
		<table border="0" cellpadding="0" cellspacing="0">
        <tr>   
	        <td class="table_cell_noborder"><fmt:message key="subject" bundle="${resword}"/>:&nbsp;&nbsp;</td>
	        <td class="table_cell_noborder"><b><c:out value="${discrepancyNote.subjectName}"/></b></td>
	        <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="event" bundle="${resword}"/>:&nbsp;&nbsp;</td>
	        <td class="table_cell_noborder">
				<b>
				<c:choose>
                    <c:when test="${discrepancyNote.eventName != null && discrepancyNote.eventName != ''}">
                        <c:out value="${discrepancyNote.eventName}"/>
                    </c:when>
                    <c:otherwise>
						<fmt:message key='N/A' bundle='${resword}'/>
                    </c:otherwise>
                </c:choose>
				</b>
			</td>
    	</tr>
        <tr>
            <td class="table_cell_noborder"><fmt:message key="event_date" bundle="${resword}"/>:&nbsp;&nbsp;</td>
            <td class="table_cell_noborder">
                <b><c:choose>
                    <c:when test="${discrepancyNote.eventStart == null}">
                        <fmt:message key="N/A" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
						<cc-fmt:formatDate value="${discrepancyNote.eventStart}" dateTimeZone="${userBean.userTimeZoneId}"/>
                    </c:otherwise>
                </c:choose></b>
            </td>
            <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="CRF" bundle="${resword}"/>:&nbsp;&nbsp;</td>
            <td class="table_cell_noborder"><b><c:out value="${discrepancyNote.crfName == '' ? 'N/A' : discrepancyNote.crfName}"/></b></td>
        </tr>
        <tr>
        	<td class="table_cell_noborder"><fmt:message key="Current_Value" bundle="${resword}"/>:&nbsp;&nbsp;</td>
            <td class="table_cell_noborder"><b><c:out value="${entityValue}"/></b></td>
            <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="More" bundle="${resword}"/>:&nbsp;&nbsp;</td>
            <td class="table_cell_noborder">
            <c:choose>
            <c:when test="${name eq 'itemData' || name eq 'ItemData'}">
                <a href="javascript: openDocWindow('ViewItemDetail?itemId=${item.id}')">
                <fmt:message key="Data_Dictionary" bundle="${resword}"/></a>
            </c:when>
            <c:otherwise>
                <b><fmt:message key="N/A" bundle="${resword}"/></b>
            </c:otherwise>
            </c:choose>
            </td>
        </tr>
        </table>
        </div>
        </div></div></div></div></div></div></div>
	</td></tr>
</table>

<div style="clear:both;"></div> 
<h3 class="title_manage"><fmt:message key="add_note" bundle="${resword}"/></h3>

<!-- dn box -->
<div id="ajax-loader" style="width: 580; height: 200; display: none;" align="center"><img src="images/ajax-loader-blue.gif"/></div>
<div id="divWithData" style="width: 580;">	
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<br>
<div class="textbox_center">

	<div class="dnBoxCol1 dnBoxText"><fmt:message key="description" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText">
		<c:if test="${isRFC}">
			<div class="formfieldL_BG" id="select" style="display:none; float: left;" >
				<select name="description" id="selectDescription"  class="formFieldL formfieldLSelect" disabled>
					<c:forEach var="rfcTerm" items="${dDescriptionsMap['dnRFCDescriptions']}">
						<option value="${rfcTerm.name}"><c:out value="${rfcTerm.name}"/>
					</c:forEach>
					<option value="Other"><fmt:message key="other" bundle="${resword}"/>
				</select>
			</div>
		</c:if>
			<div id="input" style="display:none; float: left;"> 
				<div class="formfieldXL_BG" >
					<input type="text" name="description" id="inputDescription" disabled value="<c:out value="${discrepancyNote.description}"/>" class="formfieldXL">
				</div>
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
			</div>
			<span class="alert">*</span>
	</div>
	
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="detailed_note" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText">
		<div class="formtextareaXL4_BG">
	  		<textarea name="detailedDes" rows="4" cols="50" class="formtextareaXL4 textarea_fixed_size"><c:out value="${discrepancyNote.detailedNotes}"/></textarea>
		</div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="detailedDes"/></jsp:include>
	</div>

<c:if test="${not showStatus}"> <div style="display:none;">  </c:if>
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="type" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG" style="float: left;">
		<c:choose>
		<c:when test="${parentId > 0}">
			<input type="hidden" name="typeId" value="${param.typeId}"/>
			<select name="pTypeId" id="pTypeId" class="formfieldL formFieldLSelect" disabled>
				<option value="<c:out value="${param.typeId}"/>" selected><c:out value="${param.typeName}"/>
			</select>
		</c:when>
		<c:otherwise>
			<c:set var="typeId1" value="${discrepancyNote.discrepancyNoteTypeId}"/>
			<select name="typeId" id="typeId" class="formfieldL formFieldLSelect"
                    onchange ="javascript:setElements(this.options[selectedIndex].value, 'user1', 'user2',
                            '<c:out value="${whichResStatus}"/>',
                            '<fmt:message key="New" bundle="${resterm}"/>',
                            '<fmt:message key="Updated" bundle="${resterm}"/>',
                            '<fmt:message key="Resolution_Proposed" bundle="${resterm}"/>',
                            '<fmt:message key="Closed" bundle="${resterm}"/>',
                            '<fmt:message key="Not_Applicable" bundle="${resterm}"/>', ${isRFC});">

                <c:forEach var="type" items="${discrepancyTypes}">
				<c:choose>
				<c:when test="${typeId1 == type.id}">
				 	<c:choose>
				    <c:when test="${study.status.frozen && (type.id==2 or type.id==4)}">
						<option value="<c:out value="${type.id}"/>" disabled="true" selected /><c:out value="${type.name}"/>
				    </c:when>
				    <c:otherwise>
				   		<option value="<c:out value="${type.id}"/>" selected ><c:out value="${type.name}"/>
				    </c:otherwise>
				    </c:choose>
				 </c:when>
				 <c:otherwise>
					<c:choose>
					<c:when test="${study.status.frozen && (type.id==2 || type.id==4)}">
						<option value="<c:out value="${type.id}"/>" disabled="true"><c:out value="${type.name}"/>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${type.id}"/>"><c:out value="${type.name}"/>
					</c:otherwise>
					</c:choose>
				 </c:otherwise>
				</c:choose>
				</c:forEach>
			</select>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="typeId"/></jsp:include>
		</c:otherwise>
		</c:choose>
	</div><span class="alert">*</span></div>
		
	<span id="res1${parentId}" style="display: none;">
		<div class="dnBoxCol1 dnBoxText"><fmt:message key="Set_to_Status" bundle="${resword}"/>:</div>
		<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG" style="float: left;">
			<c:set var="resStatusIdl" value="${discrepancyNote.resolutionStatusId}"/>
		    <select name="resStatusId" id="resStatusId" class="formfieldL formFieldLSelect">
				<c:set var="resStatuses" value="${resolutionStatuses}"/>
				<c:forEach var="status" items="${resStatuses}">
					<c:choose>
					<c:when test="${resStatusIdl == status.id}">
					   <option value="<c:out value="${status.id}"/>" selected ><c:out value="${status.name}"/>
					</c:when>
					<c:otherwise>
					   <option value="<c:out value="${status.id}"/>" ><c:out value="${status.name}"/>
					</c:otherwise>
					</c:choose>
				</c:forEach>
			</select></div>
		    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="resStatusId"/></jsp:include>
		<span class="alert">*</span></div>
	</span>

	<c:choose>
	<c:when test="${(parent == null || parent.id ==0 || unlock == 1) && autoView == 0}">
    	<span id="user1" style="display:none">
	</c:when>
	<c:otherwise>
		<span id="user1" style="display:block">
  	</c:otherwise>
	</c:choose>
		<div class="dnBoxCol1 dnBoxText"><fmt:message key="assign_to_user" bundle="${resword}"/>:</div>
		<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG">
			<c:choose>
			<c:when test='${not empty discrepancyNote.assignedUserId and not (discrepancyNote.assignedUserId eq 0)}'>
				<c:set var="userAccountId1" value="${discrepancyNote.assignedUserId}"/>
			</c:when>
            <c:when test='${(not empty eventCrfOwnerId) and (eventCrfOwnerId > 0)}'>
                <c:set var="userAccountId1" value="${eventCrfOwnerId}"/>
            </c:when>
			<c:otherwise>
				<c:set var="userAccountId1" value="${userBean.id}"/>
			</c:otherwise>
			</c:choose>
			<select name="userAccountId" id="userAccountId" class="formfieldL formFieldLSelect" >
				<option value="0">
		  		<c:forEach var="user" items="${userAccounts}">
		   		<c:choose>		     	<c:when test="${userAccountId1 == user.userAccountId}">
		       		<option value="<c:out value="${user.userAccountId}"/>" selected><c:out value="${user.lastName}"/>, <c:out value="${user.firstName}"/> (<c:out value="${user.userName}"/>)
		     	</c:when>
		     	<c:otherwise>
		       		<option value="<c:out value="${user.userAccountId}"/>"><c:out value="${user.lastName}"/>, <c:out value="${user.firstName}"/> (<c:out value="${user.userName}"/>)
		     	</c:otherwise>
		   		</c:choose>
		 		</c:forEach>
			</select>
			</div>
		  	<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="userAccountId"/></jsp:include>
		</div>
	</span>
		
	<c:choose>
	<c:when test="${(parent == null || parent.id ==0 || unlock == 1) && autoView == 0}">
		<span id="user2" style="display:none">
	</c:when>
	<c:otherwise>
		<span id="user2" style="display:block">
	</c:otherwise>
	</c:choose>
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="email_assigned_user" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText">
		<c:choose>
        <c:when test="${writeToDB eq '1'}">
            <input name="sendEmail" value="1" type="checkbox"/> 
        </c:when>
        <c:otherwise>
            <input name="sendEmail" value="1" type="checkbox" disabled title="<fmt:message key="email_service_for_DN_will_be_available_after_initial_data_saving" bundle="${resword}"/>"/>    
        </c:otherwise>
		</c:choose>
	</div>
	</span>
	<c:if test="${not showStatus}"> </div>  </c:if>

    <c:set var= "noteEntityType" value="${discrepancyNote.entityType}"/>
	<c:if test="${enterData == '1' || canMonitor == '1' || noteEntityType != 'itemData' }">
        <div align="right">
		<c:choose>
            <c:when test="${writeToDB eq '1'}">
                <input type="button" name="SubmitExit" onclick="javascript: $('#typeId option').removeAttr('disabled'); sendFormDataViaAjax();" value="<fmt:message key="submit_close" bundle="${resword}"/>" class="button_medium">
            </c:when>
            <c:otherwise>
                <input type="button" name="Submit" onclick="javascript: sendFormDataViaAjax();" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit">
            </c:otherwise>
        </c:choose>
		</div>
    </c:if>
</div>
</div></div></div></div></div></div></div>
</div>

</form>
</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>
