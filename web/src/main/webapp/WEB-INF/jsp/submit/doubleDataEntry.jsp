<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm" %>
<%@ taglib uri="com.akazaresearch.viewtags" prefix="view" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>ClinCapture <fmt:message key="double_data_entry" bundle="${resword}"/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    
    <link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css" media="screen">
    <link rel="stylesheet" href="includes/print.css?r=${revisionNumber}" type="text/css" media="print">
    <link href="includes/jquery-ui.css" rel="stylesheet" type="text/css"/> 
    <script type="text/javascript" language="JavaScript">
        //this has been declared here so that it is accessible from other functions in the global_functions_javascript.js
        var checkboxObject;
    </script>
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js?r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
     <!-- Added for the new Calender -->

    <ui:calendar/>
    <!-- End -->
    <!-- *JSP* submit/doubleDataEntry.jsp -->
    <script type="text/javascript" language="JavaScript" src="includes/repetition-model/repetition-model.js?r=${revisionNumber}"></script>    <script type="text/JavaScript" language="JavaScript" src="includes/prototype.js?r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js?load=effects&r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/effects.js?r=${revisionNumber}"></script>
    <ui:theme/>
</head>
<body class="aka_bodywidth" onload="document.getElementById('CRF_infobox_closed').style.display='block';document.getElementById('CRF_infobox_open').style.display='none';" onunload="javascript:clsWin();"><c:if test='${popUpURL != ""}'>
    <script>executeWhenDOMIsReady("openDNoteWindow('${popUpURL}');");</script></c:if>
<div id="centralContainer" style=
  "padding-left:3em; margin-top:1em;background-color: white; color:black;">
<%-- set button text depending on whether or not the user is confirming values --%>
<c:choose>
    <c:when test="${section.checkInputs}">        <c:set var="buttonAction"><fmt:message key="save" bundle="${resword}"/></c:set>
        <c:set var="checkInputsValue" value="1" />
    </c:when>
    <c:otherwise>        <c:set var="buttonAction" value="Confirm values" />
        <c:set var="checkInputsValue" value="0" />
    </c:otherwise>
</c:choose>
<c:set var="save_and_next_button_caption" scope="request"><fmt:message key="save_and_next" bundle="${resword}"/></c:set>
<c:set var="save_and_next_button_class" scope="request" value="${view:getHtmlButtonCssClass(save_and_next_button_caption)}" />
<table width="75%"><tr><td>
<span class="first_level_header"> <b id="crfNameId"> <c:out value="${toc.crf.name}" /> <c:out value="${toc.crfVersion.name}" />
         <c:choose>            <c:when test="${eventCRF.stage.initialDE}">
                <img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>"
                     title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
            </c:when>            <c:when
              test="${eventCRF.stage.initialDE_Complete}">
                <img src="images/icon_InitialDEcomplete.gif"
                     alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>"                     title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
            </c:when>
            <c:when test="${eventCRF.stage.doubleDE}">
                <img src="images/icon_DDE.gif" alt="<fmt:message key="double_data_entry" bundle="${resword}"/>"                     title="<fmt:message key="double_data_entry" bundle="${resword}"/>">
            </c:when>
            <c:when test="${eventCRF.stage.doubleDE_Complete}">
                <c:choose>
                    <c:when test="${studyEvent.subjectEventStatus.signed}">
                        <img src="images/icon_Signed.gif" alt="<fmt:message key="subjectEventSigned" bundle="${resword}"/>" title="<fmt:message key="subjectEventSigned" bundle="${resword}"/>">
                    </c:when>
                    <c:when test="${eventCRF.sdvStatus}">
                        <img src="images/icon_DoubleCheck.gif" alt="<fmt:message key="sourceDataVerified" bundle="${resword}"/>" title="<fmt:message key="sourceDataVerified" bundle="${resword}"/>">
                    </c:when>
                    <c:otherwise>
                        <img src="images/icon_DEcomplete.gif" alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${eventCRF.stage.admin_Editing}">
                <img src="images/icon_AdminEdit.gif"
                     alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
            </c:when>
            <c:when test="${eventCRF.stage.locked}">
                <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
            </c:when>
            <c:when test="${eventCRF.status.signed}">
                <img src="images/icon_Signed.gif" alt="<fmt:message key="signed" bundle="${resword}"/>" title="<fmt:message key="signed" bundle="${resword}"/>">
            </c:when>
            <c:when test="${eventCRF.stage.invalid}">
                <img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="invalid" bundle="${resword}"/>">
            </c:when>
            <c:otherwise>
                <img src="images/icon_NotStarted.gif" alt="<fmt:message key="not_started" bundle="${resword}"/>" title="<fmt:message key="not_started" bundle="${resword}"/>">
            </c:otherwise>
         </c:choose></b>&nbsp;&nbsp;</span></td>
    </tr>
    <tr>
        <td>
            <span class="first_level_header">
                <fmt:message key="subject_ID" bundle="${resword}"/>: <c:out value="${studySubject.label}"/>&nbsp;&nbsp;
            </span>
        </td>
    </tr>
</table>

<form id="mainForm" name="crfForm" method="POST" action="DoubleDataEntry">
<c:if test="${justCloseWindow}"><input type="hidden" name="cw" value="1" /></c:if><input type="hidden" name="eventCRFId" value="<c:out value="${section.eventCRF.id}"/>" />
<input type="hidden" name="sectionId" value="<c:out value="${section.section.id}"/>" />
<input type="hidden" name="checkInputs" value="<c:out value="${checkInputsValue}"/>" />
<input type="hidden" name="tabId" value="<c:out value="${tabId}"/>" />
<input type="hidden" name="occurenceNumber" value="<c:out value="${studyEvent.sampleOrdinal}"/>" />
<%-- We have to feed this value to the method giveFirstElementFocus()--%>
<input id="formFirstField" type="hidden" name="formFirstField" value="${requestScope['formFirstField']}" />
<input type="hidden" name="currentUserRole" value="<c:out value="${userRole.role.name}"/>" />
<c:if test="${study.parentStudyId > 0}">
	<!-- Site information -->
	<input type="hidden" name="siteUniqId" value="${study.identifier}"/>
	<input type="hidden" name="siteName" value="${study.name}"/>
	<input type="hidden" name="sitePI" value="${study.principalInvestigator}"/>	
	<!-- /Site information -->
	<!-- Facility information -->
	<input type="hidden" name="facilityName" value="${study.facilityName}"/>
	<input type="hidden" name="facilityCity" value="${study.facilityCity}"/>
	<input type="hidden" name="facilityState" value="${study.facilityState}"/>
	<input type="hidden" name="facilityZip" value="${study.facilityZip}"/>
	<input type="hidden" name="facilityContactPhone" value="${study.facilityContactPhone}"/>
	<input type="hidden" name="facilityCountry" value="${study.facilityCountry}"/>
	<input type="hidden" name="facilityContactEmail" value="${study.facilityContactEmail}"/>	
	<!-- /Facility information -->
	<!-- Interviewer information -->
	<input type="hidden" name="userName" value="${userBean.firstName}"/>
	<input type="hidden" name="userLastName" value="${userBean.lastName}"/>
	<input type="hidden" name="userEmail" value="${userBean.email}"/>
	<input type="hidden" name="userPhone" value="${userBean.phone}"/>
	<!-- /Interviewer information -->
	<!-- Randomization statistics for current site.
		This section provides info on number of study subjects in a site,
		already assigned to each of active dynamic group classes-->
	<c:forEach var="counter" items="${subjectsNumberAssignedToEachDynamicGroupMap}" varStatus="status">
		<input type="hidden" id="dynamicGroupClass${status.index + 1}" group_class_name="${counter.key}"
			   subjects_counter="${counter.value}"/>
	</c:forEach>
	<!-- /Randomization statistics for current site -->
</c:if>
<!-- Messages -->
<input type="hidden" name="randDataEntryStepMessage" value='<fmt:message key="randomization_des_message" bundle="${restext}"/>'/>
<!-- /Messages -->

<script type="text/javascript" language="JavaScript">
    // <![CDATA[
    function getSib(theSibling){
        var sib;
        do {
            sib  = theSibling.previousSibling;
            if(sib.nodeType != 1){
                theSibling = sib;
            }
        } while(! (sib.nodeType == 1))

        return sib;
    }

    // ]]>
</script>
<script language="javascript">
    function getFocused(f){
        var v = document.getElementById(f);
        v.focus();
    }
</script>

<c:set var="crfTabIndex" value="${0}" scope="request"/>

<c:import url="interviewer.jsp">
  <c:param name="hasNameNote" value="${hasNameNote}"/>
  <c:param name="hasDateNote" value="${hasDateNote}"/>
</c:import>
<!--<br><br>-->
<br />
<%--I don't think we need this segment to accompany the existing error messages:--%>
<%-- need to put this back, otherwise, error msg from 'mark complete' cannot show--%>

<c:set var="sectionNum" value="0"/>
<c:forEach var="section" items="${toc.sections}">
    <c:set var="sectionNum" value="${sectionNum+1}"/>
</c:forEach>

<div id="box" class="dialog" style="display:none;">
<span id="mbm">
    <fmt:message key="crf_data_entry_password_required" bundle="${restext}"/>
</span><br>
    <div style="text-align:center; width:100%;">
        <input align="center" type="password" name="password" id="passwordId"/>
        <input type="button" onclick="hm('box');requestSignatureFromCheckbox(document.getElementById('passwordId').value, checkboxObject);" value="OK"/>
    </div>
</div>

<!-- section tabs here -->
<table id="crfSectionTabsTable" border="0" cellpadding="0" cellspacing="0" style="${!(crfShortcutsAnalyzer eq null || (crfShortcutsAnalyzer.totalNew == 0 && crfShortcutsAnalyzer.totalUpdated == 0 && crfShortcutsAnalyzer.totalResolutionProposed == 0 && crfShortcutsAnalyzer.totalClosed == 0 && crfShortcutsAnalyzer.totalAnnotations == 0 && crfShortcutsAnalyzer.totalItemsToSDV == 0)) ? 'padding-top: 80px;' : 'padding-top: 0px;'}">
<tr><td>
    <c:if test="${!empty pageMessages}">
        <div class="alert">
            <c:forEach var="message" items="${pageMessages}">
                <c:out value="${message}" escapeXml="false"/>
                <br><br>
            </c:forEach>
        </div>
    </c:if>
    <c:if test="${! empty formMessages or !empty warningMessages}">
        <!-- initial position for data entry error messages; we'll
        improve the style as well -->
        <div id="errorMessagesContainer"  style="word-wrap: break-word !important; width:600px;margin-bottom: 20px;" class="aka_err_message">
            <ul>
                <c:forEach var="message" items="${warningMessages}">
                    <c:forEach items="${message.value}" var="value">
                        <li style="color: #868686">
                            <span style="text-decoration: underline"><strong>
                                <label onclick="getFocused('<c:out value="${message.key}"/>');"><c:out value="${value}"/></label>
                            </strong></span>
                        </li>
                    </c:forEach>
                </c:forEach>
            </ul>
            <ul>
                <c:forEach var="message" items="${formMessages}">
                    <c:choose>
                        <c:when test="${hasShown}">
                            <c:forEach items="${message.value}" var="value">
                                <li style="color:  #006633"><span style="text-decoration: underline"><strong>
                                    <label onclick="getFocused('<c:out value="${message.key}"/>');"><c:out value="${value}"/></label>
                                </strong></span></li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${message.value}" var="value">
                                <c:choose>
                                    <c:when test="${Hardrules}">
                                        <li style="color: #ff0000">
                                    </c:when>
                                    <c:otherwise>
                                        <li style="color: #E46E16">
                                    </c:otherwise>
                                </c:choose>
                           <span style="text-decoration: underline"><strong>
                               <label onclick="getFocused('<c:out value="${message.key}"/>');"><c:out value="${value}"/></label>
                           </strong></span></li>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </ul>
        </div>
    </c:if><%-- error messages are not null --%>
</td></tr>
<tr>
<!--
<td align="right" valign="middle" style="padding-left: 12px; display: none" id="TabsBack">
    <a href="javascript:TabsBack()"><img src="images/arrow_back.gif" border="0" style="margin-top:10px"></a></td>
<td align="right" style="padding-left: 12px" id="TabsBackDis">
    <img src="images/arrow_back_dis.gif" border="0"/></td>
-->

<script type="text/JavaScript" language="JavaScript">
window.onload = initmb;

// Total number of tabs (one for each CRF)
var TabsNumber = <c:out value="${sectionNum}"/>;

var frameWidth = 1000;
var tabWidth = frameWidth/TabsNumber;

// Number of tabs to display at a time
var TabsShown = TabsNumber; /* was 3; */

// Labels to display on each tab (name of CRF)
var TabLabel = new Array(TabsNumber)
var TabFullName = new Array(TabsNumber)
var TabSectionId = new Array(TabsNumber)

<c:set var="markCRFMethodName" value="displayMessageFromCheckbox(this, undefined)"/>

<c:set var="count" value="0"/>
<c:forEach var="section" items="${toc.sections}">
TabFullName[<c:out value="${count}"/>]="<c:out value="${section.label}"/> (<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)";

TabSectionId[<c:out value="${count}"/>]= <c:out value="${section.id}"/>;

TabLabel[<c:out value="${count}"/>]="<c:out value="${section.label}"/> " + "<span id='secNumItemsCom<c:out value="${count}"/>' style='font-weight: normal;'>(<c:out value="${section.numItemsCompleted}"/>/<c:out value="${section.numItems}" />)</span>";

<c:set var="count" value="${count+1}"/>
</c:forEach>

DisplaySectionTabs();

//selectTabs(${tabId},${sectionNum},'crfHeaderTabs');

function DisplaySectionTabs()
{
    TabID=1;

    while (TabID<=TabsNumber)

    {
        sectionId = TabSectionId[TabID-1];
        url = "DoubleDataEntry?eventCRFId=" + <c:out value="${section.eventCRF.id}"/> + "&sectionId=" + sectionId + "&tabId=" + TabID + "${justCloseWindow ? '&cw=1' : ''}";
        currTabID = <c:out value="${tabId}"/>;

        document.write('<td nowrap style="display:inline-block;" class="crfHeaderTabs" valign="bottom" id="Tab' + TabID + '">');

        if (TabID != currTabID) {
            document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
            document.write('<a class="tabtext" title="' + TabFullName[(TabID-1)] + '" href=' + url + ' onclick="return checkSectionStatus(this);">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
            document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
            document.write('</td>');
        }
        else {
            document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">');
            document.write('<span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
            document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
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
            if ($.browser.msie) {
                $(trId).html(document.getElementById(trId).innerHTML + td.outerHTML);
            } else {
                document.getElementById(trId).innerHTML = document.getElementById(trId).innerHTML + td.outerHTML;
                td.outerHTML = "";
            }
        }
        document.write('</tr>');
    }
}

function gotoLink() {

	objImage=document.getElementById('status_top');
	
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
    	
		var OptionIndex=document.crfForm.sectionName.selectedIndex;
		confirmDialog({
			message: '<fmt:message key="you_have_unsaved_data" bundle="${resword}"/>',
			height: 150,
			width: 500,
			pageName: document.crfForm.sectionName.options[OptionIndex].value
		});
    }
}

function pageWidth() {return window.innerWidth != null? window.innerWidth: document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth:document.body != null? document.body.clientWidth:null;}
function pageHeight() {return window.innerHeight != null? window.innerHeight: document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight:document.body != null? document.body.clientHeight:null;}
function posLeft() {return typeof window.pageXOffset != 'undefined' ? window.pageXOffset:document.documentElement && document.documentElement.scrollLeft? document.documentElement.scrollLeft:document.body.scrollLeft? document.body.scrollLeft:0;}
function posTop() {return typeof window.pageYOffset != 'undefined' ? window.pageYOffset:document.documentElement && document.documentElement.scrollTop? document.documentElement.scrollTop: document.body.scrollTop?document.body.scrollTop:0;}
function scrollFix(){var obol=document.getElementById('ol');obol.style.top=posTop()+'px';obol.style.left=posLeft()+'px'}
function sizeFix(){var obol=document.getElementById('ol');obol.style.height=pageHeight()+'px';obol.style.width=pageWidth()+'px';}
function kp(e){ky=e?e.which:event.keyCode;if(ky==88||ky==120)hm();return false}
function inf(h){tag=document.getElementsByTagName('select');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('iframe');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('object');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;}
function sm(obl, chkbox, wd, ht){if(chkbox.checked==false){checkboxObject=chkbox;return;} checkboxObject=chkbox;  var h='hidden';var b='block';var p='px';var obol=document.getElementById('ol'); var obbxd = document.getElementById('mbd');obbxd.innerHTML = $("#" + obl)[0].innerHTML;obol.style.height=pageHeight()+p;obol.style.width=pageWidth()+p;obol.style.top=posTop()+p;obol.style.left=posLeft()+p;obol.style.display=b;var tp=posTop()+((pageHeight()-ht)/2)-12;var lt=posLeft()+((pageWidth()-wd)/2)-12;var obbx=document.getElementById('mbox');obbx.style.top=(tp<0?0:tp)+p;obbx.style.left=(lt<0?0:lt)+p;obbx.style.width=wd+p;obbx.style.height=ht+p;inf(h);obbx.style.display=b;return false;}
function hm(){var v='visible';var n='none';document.getElementById('ol').style.display=n;document.getElementById('mbox').style.display=n;inf(v);document.onkeypress=''}
function initmb(){var ab='absolute';var n='none';var obody=document.getElementsByTagName('body')[0];var frag=document.createDocumentFragment();var obol=document.createElement('div');obol.setAttribute('id','ol');obol.style.display=n;obol.style.position=ab;obol.style.top=0;obol.style.left=0;obol.style.zIndex=998;obol.style.width='100%';frag.appendChild(obol);var obbx=document.createElement('div');obbx.setAttribute('id','mbox');obbx.style.display=n;obbx.style.position=ab;obbx.style.zIndex=999;var obl=document.createElement('span');obbx.appendChild(obl);var obbxd=document.createElement('div');obbxd.setAttribute('id','mbd');obl.appendChild(obbxd);frag.insertBefore(obbx,obol.nextSibling);obody.insertBefore(frag,obody.firstChild);
    window.onscroll = scrollFix; window.onresize = sizeFix;
}

function setParameterForDN(field, parameterName, value) {
  setParameterForDNWithPath(field, parameterName, value, '${pageContext.request.contextPath}');
};
</script>
</tr>
</table>
<%-- the below line is ABSOLUTELY NECESSARY FOR VALIDATION ON ANY PAGE --%>
<input type="hidden" name="submitted" value="1" />

<script type="text/javascript" language="JavaScript">
    
    function checkSectionStatus(aLink) {

        objImage=document.getElementById('status_top');
        if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        	confirmDialog({
        		message: '<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>',
        		height: 150,
        		width: 500,
        		aLink: aLink
        	});
        	return false
        }
        return true
    }
    
	function checkEntryStatus(strImageName, submit) {
    	
        objImage = MM_findObj(strImageName);
        if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        	confirmSubmit({
        		message: '<fmt:message key="you_have_unsaved_data_exit" bundle="${resword}"/>',
        		height: 150,
        		width: 500,
        		submit: submit
        	});
        	return false
        }
    	
        return true;
    } 

    function disableSubmit() {
		var srh = document.getElementById('srh');
		var srl = document.getElementById('srl');
		var seh = document.getElementById('seh');
		var sel = document.getElementById('sel');
        if (srh != undefined) srh.disabled = true;
        if (srl != undefined) srl.disabled = true;
        if (seh != undefined) seh.disabled = true;
        if (sel != undefined) sel.disabled = true;
	}
</script>

<c:set var="stage" value="${param.stage}"/>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<div style="width:100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B">
<div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<c:set var="currPage" value="" />
<c:set var="curCategory" value="" />

<!--   include return to top table-->
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0">
<c:set var="displayItemNum" value="${0}" />
<c:set var="numOfTr" value="0"/>
<c:set var="numOfDate" value="1"/>
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
<c:set var="repeatCount" value="1"/>
<c:forEach var="displayItem" items="${section.displayItemGroups}" varStatus="itemStatus">
<c:if test="${displayItem.itemGroup.groupMetaBean.showGroup}">
<c:if test="${displayItemNum ==0}">
    <!-- always show the button and page above the first item-->
    <!-- to handle the case of no pageNumLabel for all the items-->
    <%--  BWP: corrected "column span="2" "--%>
    <tr class="aka_stripes">
            <%--  <td class="aka_header_border" colspan="2">--%>
        <td class="aka_header_border" colspan="2">
            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
                <tr>

                    <td valign="bottom" nowrap="nowrap" style="padding-right: 50px">

                        <a name="top"><fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItem.pageNumberLabel}" escapeXml="false"/></a>
                    </td>
                    <td align="right" valign="bottom">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <c:choose>
                                    <c:when test="${stage !='adminEdit' && section.lastSection}">
                                        <c:choose>
                                            <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                            <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="sm('box', this, 730,100);">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                            <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="${markCRFMethodName}">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <td colspan="2">&nbsp;</td>
                                    </c:otherwise>
                                </c:choose>

                                <td><input type="submit" id="srh" name="submittedResume" value="<fmt:message key="save" bundle="${resword}"/>" class=
                                  "button_medium" onclick="disableSubmit(); this.form.submit();"/></td>
                                <c:if test="${section.lastSection and hideSaveAndNextButton eq null }">
			                     	<td><input type="submit" id="snl" name="saveAndNext" value="${save_and_next_button_caption}" class=
			           			  		"${save_and_next_button_class}" onclick="disableSubmit(); this.form.submit();"/></td>
			                    </c:if>
                                <td>
                                    <input type="submit" id="seh" name="submittedExit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium" onClick="return checkEntryStatus('DataStatus_top', this);" />
                                </td>

                                <c:choose>
                                    <c:when test="${! empty formMessages}">
                                        <td valign="bottom"><img name="DataStatus_top" id="status_top" alt="<fmt:message key="data_status" bundle="${resword}"/>" title="<fmt:message key="changed_not_saved" bundle="${restext}"/>" src="images/icon_UnsavedData.gif"></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td valign="bottom"><img name="DataStatus_top" id="status_top" alt="<fmt:message key="data_status" bundle="${resword}"/>" title="<fmt:message key="not_changed_data" bundle="${restext}"/>" src="images/icon_UnchangedData.gif"></td>
                                    </c:otherwise>
                                </c:choose>

                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</c:if>

<c:if test="${currPage != displayItem.pageNumberLabel && displayItemNum >0}">
    <!-- show page number and buttons -->
    <%--  BWP: corrected "column span="2" "--%>
    <tr class="aka_stripes">
        <td class="aka_header_border" colspan="2">
            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
                <tr>

                    <td valign="bottom" nowrap="nowrap" style="padding-right: 50px">
                        <fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItem.pageNumberLabel}" escapeXml="false"/>
                    </td>
                    <td align="left" valign="bottom">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <c:choose>
                                    <c:when test="${stage !='adminEdit' && section.lastSection}">
                                        <c:choose>
                                            <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                            <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="sm('box', this, 730,100);">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                            <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="${markCRFMethodName}">
                                                </td>
                                                <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <td colspan="2">&nbsp;</td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <!-- end of page number and buttons-->

</c:if>

<c:choose>
<c:when test="${displayItem.inGroup == true}">

<c:if test="${(not empty lastItemWasSingle) && lastItemWasSingle}">
</tr>
</table>
</td>
</tr>
</c:if>

<c:set var="lastItemWasSingle" value="false" />

<c:set var="currPage" value="${displayItem.pageNumberLabel}" />

<tr>
<c:set var="uniqueId" value="0"/>
<c:set var="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.name}"/>
    <%-- the section borders property value --%>
<c:set var="sectionBorders" value="${section.section.borders}" />
<!--<c:set var="repeatNumber" value="${displayItem.itemGroup.groupMetaBean.repeatNum}"/>-->
<!--  was just <td>, tbh 05/03/2010 -->
<%-- place where we need to highlight sections,
	one of the error keys should equal the item group's OID --%>
<c:set var="isSectionShown" value="false"/>
<c:forEach var="formMsg" items="${formMessages}">
	<c:if test="${formMsg.key eq displayItem.itemGroup.itemGroupBean.oid}">
		<c:set var="isSectionShown" value="true"/>
	</c:if>
</c:forEach>
<c:choose>
	<c:when test="${hasShown && isSectionShown}">
        <td class="aka_group_show">
	</c:when>
	<c:otherwise>
		<td>
	</c:otherwise>
</c:choose>
<!-- there are already item data for an item group, repeat number just be 1-->
<c:set var="repeatNumber" value="0"/>


<c:set var="repeatMax" value="${displayItem.itemGroup.groupMetaBean.repeatMax}"/>
<c:set var="totalColsPlusSubcols" value="0" />
<c:set var="questionNumber" value=""/>
<c:if test="${! (repeatParentId eq 'Ungrouped')}">

<c:set var="repeatParentId" value="${displayItem.itemGroup.itemGroupBean.oid}"/>
<c:if test="${! (displayItem.itemGroup.groupMetaBean.header eq '')}">
    <div class="aka_group_header">
        <strong><c:out value="${displayItem.itemGroup.groupMetaBean.header}"/></strong>
    </div>
</c:if>
<c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
<table border="0" cellspacing="0" cellpadding="0" class="aka_form_table repeatingGroupTable" width="100%">
<thead>
<tr>
        <%-- if there are horizontal checkboxes or radios anywhere in the group...--%>
    <c:set var="isHorizontal" scope="request" value="${false}"/>
    <c:forEach var="thItem" items="${displayItem.itemGroup.items}">
        <c:set var="questionNumber" value="${thItem.metadata.questionNumberLabel}"/>
        <%-- We have to add a second row of headers if the response_layout property is
     horizontal for checkboxes. --%>
        <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
        <c:if test="${thItem.metadata.responseLayout eq 'horizontal' ||
      thItem.metadata.responseLayout eq 'Horizontal'}">
            <c:set var="isHorizontal" scope="request" value="${true}"/>
            <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
            <c:set var="optionsLen" value="0"/>
            <c:forEach var="optn" items="${thItem.metadata.responseSet.options}">
                <c:set var="optionsLen" value="${optionsLen+1}"/>
            </c:forEach>
        </c:if>
        <c:choose>
            <c:when test="${isHorizontalCellLevel && sectionBorders == 1 &&
        (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                <th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
            </c:when>
            <c:when test="${isHorizontalCellLevel &&
        (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                <th colspan="<c:out value='${optionsLen}'/>" class="aka_headerBackground aka_padding_large aka_cellBorders">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + optionsLen}" />
            </c:when>
            <c:when test="${sectionBorders == 1}">
                <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
            </c:when>
            <c:otherwise>
                <th class="aka_headerBackground aka_padding_large aka_cellBorders">
                <%-- compute total columns value for the add button row colspan attribute--%>
                <c:set var="totalColsPlusSubcols" value="${totalColsPlusSubcols + 1}" />
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${thItem.metadata.header == ''}">
                <c:if test="${! (empty questionNumber)}">
                    <span style="margin-right:1em"><c:out value="${questionNumber}"/></span></c:if><c:out value="${thItem.metadata.leftItemText}" escapeXml="false"/>
            </c:when>
            <c:otherwise>
                <c:if test="${! (empty questionNumber)}">
                    <span style="margin-right:1em"><c:out value="${questionNumber}"/></span></c:if><c:out value="${thItem.metadata.header}" escapeXml="false"/>
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
        <c:forEach var="thItem" items="${displayItem.itemGroup.items}">
        <c:if test="${thItem.metadata.showItem}">
            <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
            <c:if test="${thItem.metadata.responseLayout eq 'horizontal' ||
      thItem.metadata.responseLayout eq 'Horizontal'}">
                <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
            </c:if>
            <c:choose>
                <c:when test="${isHorizontalCellLevel && sectionBorders == 1 &&
                    (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                    <c:forEach var="respOpt" items="${thItem.metadata.responseSet.options}">
                        <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark">
                            <c:out value="${respOpt.text}" /></th>
                    </c:forEach>
                </c:when>
                <c:when test="${isHorizontalCellLevel &&
                    (thItem.metadata.responseSet.responseType.name eq 'checkbox' ||
              thItem.metadata.responseSet.responseType.name eq 'radio')}">
                    <c:forEach var="respOpt" items="${thItem.metadata.responseSet.options}">
                        <th class="aka_headerBackground aka_padding_large aka_cellBorders">
                            <c:out value="${respOpt.text}" /></th>
                    </c:forEach>
                </c:when>
                <c:when test="${sectionBorders == 1}">
                    <th class="aka_headerBackground aka_padding_large aka_cellBorders_dark"/>
                </c:when>
                <c:otherwise>
                    <th class="aka_headerBackground aka_padding_large aka_cellBorders"/>
                </c:otherwise>
            </c:choose>
        </c:if>
        </c:forEach>
        <th />
    </tr>
</c:if>
</thead>

<tbody>

<c:set var="uniqueId" value="${0}"/>
<c:set var="dbItemGroupsSize" value="${fn:length(displayItem.dbItemGroups)}"/>
<c:set var="itemGroupsSize" value="${fn:length(displayItem.itemGroups)}"/>
<c:set var="tabbingMode" value="${event_def_crf_bean.tabbingMode}"/>
<c:set var="itemNum" value="${crfTabIndex}"/>

<c:forEach var="bodyItemGroup" items="${displayItem.itemGroups}"  varStatus="status">
<c:set var="columnNum"  value="1"/>

<c:set var="savedIntoDB" value="false"/>
<c:if test="${status.index <= dbItemGroupsSize - 1}">
    <c:set var="savedIntoDB" value="true"/>
</c:if>

<tr repeat="${uniqueId}" class="repeatingTableRow">
<c:set var="columnNum"  value="1"/>
<c:forEach var="bodyItem" items="${bodyItemGroup.items}" varStatus="bodyItemStatus">
<c:choose>
    <c:when test='${tabbingMode eq "leftToRight"}'>
        <c:set var="itemNum" value="${crfTabIndex}"/>
    </c:when>
    <c:otherwise>
        <c:set var="itemNum" value="${crfTabIndex + bodyItemStatus.index}"/>
    </c:otherwise>
</c:choose>
<c:choose>
<c:when test="${bodyItem.metadata.showItem}">
	<%-- adding more highlighting here, tbh 05/2010 --%>
	<c:set var="isItemShown" value="false"/>
		<c:forEach var="formMsg" items="${formMessages}">

			<c:set var="inputValue"><c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>input<c:out value="${bodyItem.item.id}"/></c:set>
			<c:if test="${formMsg.key eq inputValue}">
				<c:set var="isItemShown" value="true"/>
			</c:if>
		</c:forEach>
        <c:choose>
        	<c:when test="${isItemShown && hasShown}">
            	<c:set var="extraClass" value="aka_group_show"/>
            </c:when>
            <c:otherwise>
				<c:set var="extraClass" value=" "/>
            	<%-- do nothing here ? --%>
            </c:otherwise>
        </c:choose>
    <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
    <c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' ||
      bodyItem.metadata.responseLayout eq 'Horizontal'}">
        <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
    </c:if>
    <c:choose>
        <c:when test="${isHorizontalCellLevel &&
            sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
            <%-- For horizontal checkboxes, radio buttons--%>
            <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                <td class="itemHolderClass aka_padding_norm aka_cellBorders_dark <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${false}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="doubleDataEntry"/>
                    </c:import>
                </td>
            </c:forEach>
        </c:when>
        <c:when test="${isHorizontalCellLevel &&
           (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
            <%-- For horizontal checkboxes, radio buttons--%>
            <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                <td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
                    <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${false}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="doubleDataEntry"/>
                    </c:import>
                </td>
            </c:forEach>
        </c:when>
        <c:when test="${sectionBorders == 1}">
            <td class="itemHolderClass aka_padding_norm aka_cellBorders_dark <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                <c:set var="displayItem" scope="request" value="${bodyItem}" />
				<!-- text goes here -->
				<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.leftItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${false}"/>
						<c:param name="side" value="left"/>
					</c:import>
                <c:import url="../submit/showGroupItemInput.jsp">
                    <c:param name="repeatParentId" value="${repeatParentId}"/>
                    <c:param name="rowCount" value="${uniqueId}"/>
                    <c:param name="key" value="${numOfDate}" />
                    <c:param name="isLast" value="${false}"/>
                    <c:param name="tabNum" value="${itemNum}"/>
                    <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                    <c:param name="originJSP" value="doubleDataEntry"/>
                </c:import>
				<!-- text goes here -->
				<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.rightItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${false}"/>
						<c:param name="side" value="right"/>
					</c:import>
            </td>
        </c:when>
        <%-- could be a radio or checkbox that is not horizontal --%>
        <c:otherwise>
            <td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                <c:set var="displayItem" scope="request" value="${bodyItem}" />
				<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.leftItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${false}"/>
						<c:param name="side" value="left"/>
					</c:import>
                <c:import url="../submit/showGroupItemInput.jsp">
                    <c:param name="repeatParentId" value="${repeatParentId}"/>
                    <c:param name="rowCount" value="${uniqueId}"/>
                    <c:param name="key" value="${numOfDate}" />
                    <c:param name="isLast" value="${false}"/>
                    <c:param name="tabNum" value="${itemNum}"/>
                    <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                    <c:param name="originJSP" value="doubleDataEntry"/>
                </c:import>
				<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.rightItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${false}"/>
						<c:param name="side" value="right"/>
					</c:import>
            </td>
        </c:otherwise>
    </c:choose>
    <c:set var="columnNum" value="${columnNum+1}"/>
    </c:when>
    <c:when test="${bodyItem.blankDwelt}"><td class="aka_padding_norm aka_cellBorders"></c:when>
    </c:choose>
</c:forEach>
<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
    <c:choose>
        <c:when test="${sectionBorders == 1}">
            <td class="aka_padding_norm aka_cellBorders_dark">
                    <%-- check for manual in the input name; if rowCount > 0 then manual
               will be in the name --%>
                <c:choose>
                    <c:when test="${uniqueId ==0}">
                        <input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
                    </c:otherwise>
                </c:choose>
                <button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove" rel="${savedIntoDB}"></button>
            </td>
        </c:when>
        <c:otherwise>
            <td class="aka_padding_norm aka_cellBorders">
                    <%-- check for manual in the input name; if rowCount > 0 then manual
               will be in the name --%>
                <c:choose>
                    <c:when test="${uniqueId ==0}">
                        <input type="hidden" name="<c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>.newRow" value="yes">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="<c:out value="${repeatParentId}"/>_manual<c:out value="${uniqueId}"/>.newRow" value="yes">
                    </c:otherwise>
                </c:choose>
                <button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove" rel="${savedIntoDB}"></button>
            </td>

        </c:otherwise>
    </c:choose>
</c:if>
</tr>
<c:if test="${status.last}">
<!-- for the last but not the only first row, we need to use [] so the repetition javascript can copy it to create new row-->
<tr id="<c:out value="${repeatParentId}"/>" class="repeatingTableRow" repeat="template" repeat-start="<c:out value="${repeatNumber}"/>" repeat-max="<c:out value="${repeatMax}"/>">

    <c:forEach var="bodyItem" items="${bodyItemGroup.items}" varStatus="bodyItemState">
    <c:choose>
        <c:when test='${tabbingMode eq "leftToRight"}'>
            <c:set var="itemNum" value="${crfTabIndex}"/>
        </c:when>
        <c:otherwise>
            <c:set var="itemNum" value="${crfTabIndex + bodyItemState.index}"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
    <c:when test="${bodyItem.metadata.showItem}">
	<%-- set up highlighting here, tbh 05/2010 --%>
		<c:set var="isItemShown" value="false"/>

		<c:forEach var="formMsg" items="${formMessages}">

			<c:set var="inputValue"><c:out value="${repeatParentId}"/>_<c:out value="${uniqueId}"/>input<c:out value="${bodyItem.item.id}"/></c:set>
			<c:if test="${formMsg.key eq inputValue}">
				<c:set var="isItemShown" value="true"/>
				<%-- PASS! <c:out value="${inputValue}"/> --%>
			</c:if>

		</c:forEach>

        <c:choose>
			<c:when test="${isItemShown && hasShown}">
                 <c:set var="extraClass" value="aka_group_show"/>
            </c:when>
            <c:otherwise>
				<c:set var="extraClass" value=" "/>
                <%-- do nothing here ? --%>
            </c:otherwise>
        </c:choose>
        <c:set var="isHorizontalCellLevel" scope="request" value="${false}"/>
        <c:if test="${bodyItem.metadata.responseLayout eq 'horizontal' ||
      bodyItem.metadata.responseLayout eq 'Horizontal'}">
            <c:set var="isHorizontalCellLevel" scope="request" value="${true}"/>
        </c:if>
        <c:choose>
            <c:when test="${isHorizontalCellLevel && sectionBorders == 1 && (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
                <%-- For horizontal checkboxes, radio buttons--%>
                <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                    <td class="itemHolderClass aka_padding_norm aka_cellBorders_dark <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                        <c:set var="displayItem" scope="request" value="${bodyItem}" />
                        <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                        <c:import url="../submit/showGroupItemInput.jsp">
                            <c:param name="repeatParentId" value="${repeatParentId}"/>
                            <c:param name="rowCount" value="${uniqueId}"/>
                            <c:param name="key" value="${numOfDate}" />
                            <c:param name="isLast" value="${true}"/>
							<c:param name="isTemplateRow" value="${true}"/>
                            <c:param name="tabNum" value="${itemNum}"/>
                            <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                            <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                            <c:param name="originJSP" value="doubleDataEntry"/>
                        </c:import>
                    </td>
                </c:forEach>
            </c:when>
            <c:when test="${isHorizontalCellLevel &&
                (bodyItem.metadata.responseSet.responseType.name eq 'radio' ||
           bodyItem.metadata.responseSet.responseType.name eq 'checkbox')}">
                <%-- For horizontal checkboxes, radio buttons--%>
                <c:forEach var="respOption" items="${bodyItem.metadata.responseSet.options}">
                    <td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                        <c:set var="displayItem" scope="request" value="${bodyItem}" />
                        <c:set var="responseOptionBean" scope="request" value="${respOption}" />
                        <c:import url="../submit/showGroupItemInput.jsp">
                            <c:param name="repeatParentId" value="${repeatParentId}"/>
                            <c:param name="rowCount" value="${uniqueId}"/>
                            <c:param name="key" value="${numOfDate}" />
                            <c:param name="isLast" value="${true}"/>
							<c:param name="isTemplateRow" value="${true}"/>
                            <c:param name="tabNum" value="${itemNum}"/>
                            <c:param name="isHorizontal" value="${isHorizontalCellLevel}"/>
                            <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                            <c:param name="originJSP" value="doubleDataEntry"/>
                        </c:import>
                    </td>
                </c:forEach>
            </c:when>
            <c:when test="${sectionBorders == 1}">
                <td class="itemHolderClass aka_padding_norm aka_cellBorders_dark <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
					<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.leftItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${true}"/>
						<c:param name="side" value="left"/>
					</c:import>
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${true}"/>
						<c:param name="isTemplateRow" value="${true}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="doubleDataEntry"/>
                    </c:import>
					<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.rightItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${true}"/>
						<c:param name="side" value="right"/>
					</c:import>
                </td>
            </c:when>
            <%-- could be a radio or checkbox that is not horizontal --%>
            <c:otherwise>
                <td class="itemHolderClass aka_padding_norm aka_cellBorders <c:out value="${extraClass}"/>" id="itemHolderId_${uniqueId}input${bodyItem.item.id}">
				<!-- link text here -->
                    <c:set var="displayItem" scope="request" value="${bodyItem}" />
					<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.leftItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${true}"/>
						<c:param name="side" value="left"/>
					</c:import>
                    <c:import url="../submit/showGroupItemInput.jsp">
                        <c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
                        <c:param name="key" value="${numOfDate}" />
                        <c:param name="isLast" value="${true}"/>
						<c:param name="isTemplateRow" value="${true}"/>
                        <c:param name="tabNum" value="${itemNum}"/>
                        <c:param name="defaultValue" value="${bodyItem.metadata.defaultValue}"/>
                        <c:param name="originJSP" value="doubleDataEntry"/>
                    </c:import>
					<!-- link text here -->
					<c:import url="../submit/generateGroupItemTxt.jsp">
						<c:param name="itemId" value="${bodyItem.item.id}"/>
						<c:param name="inputType" value="${bodyItem.metadata.responseSet.responseType.name}"/>
						<c:param name="function" value="${bodyItem.metadata.responseSet.options[0].value}"/>
						<c:param name="linkText" value="${bodyItem.metadata.rightItemText}"/>
						<c:param name="repeatParentId" value="${repeatParentId}"/>
                        <c:param name="rowCount" value="${uniqueId}"/>
						<c:param name="isLast" value="${true}"/>
						<c:param name="side" value="right"/>
					</c:import>
                </td>
            </c:otherwise>
        </c:choose>
        <c:set var="columnNum" value="${columnNum+1}"/>
        </c:when>
        <c:when test="${bodyItem.blankDwelt}"><td class="aka_padding_norm aka_cellBorders"></c:when>
    	</c:choose>
    </c:forEach>
    <c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
        <c:choose>
            <c:when test="${sectionBorders == 1}">
                <td class="aka_padding_norm aka_cellBorders_dark">
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
                    <button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove" rel="${savedIntoDB}"></button>
                </td>
            </c:when>

            <c:otherwise>
                <td class="aka_padding_norm aka_cellBorders">
                    <input type="hidden" name="<c:out value="${repeatParentId}"/>_[<c:out value="${repeatParentId}"/>].newRow" value="yes" />
                    <button stype="remove" type="button" template="<c:out value="${repeatParentId}"/>" class="button_remove" rel="${savedIntoDB}"></button>
                </td>
            </c:otherwise>
        </c:choose>
    </c:if>
</tr>

<c:if test='${tabbingMode eq "topToBottom" && itemGroupsSize ne null && itemGroupsSize > 0}'>
    <c:set var="crfTabIndex" value="${crfTabIndex + fn:length(displayItem.itemGroups[0].items) - 1}" scope="request"/>
</c:if>
</c:if>

<c:set var="uniqueId" value="${uniqueId +1}"/>
</c:forEach>
<c:if test="${displayItem.itemGroup.groupMetaBean.repeatingGroup}">
      <tr>
        <c:choose>
            <c:when test="${sectionBorders == 1}">
                <%-- Add 1 to the totalColsPlusSubcols variable to accommodate the cell
                containing the remove button--%>
                <td class="aka_padding_norm aka_cellBorders_dark" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
                    <button stype="add" type="button" template="<c:out value="${repeatParentId}"/>" class="button_search"><fmt:message key="add" bundle="${resword}"/></button></td>
            </c:when>
            <c:otherwise>
                <td class="aka_padding_norm aka_cellBorders" colspan="<c:out value="${totalColsPlusSubcols + 1}"/>">
                    <button stype="add" type="button" template="<c:out value="${repeatParentId}"/>" class="button_search"><fmt:message key="add" bundle="${resword}"/></button></td>
            </c:otherwise>
        </c:choose>
    </tr>
</c:if>
</tbody>

</table>
<%--test for itemgroup named Ungrouped --%>
</c:if>
</td></tr>


</c:when>
<c:otherwise>


<c:set var="currPage" value="${displayItem.singleItem.metadata.pageNumberLabel}" />

<c:set var="cdisplay" value="${displayItem.singleItem.scdData.scdItemMetadataBean.id}"/>
<c:if test="${displayItem.singleItem.metadata.showItem || cdisplay>0}">
    <%-- SHOW THE PARENT FIRST --%>
<c:if test="${displayItem.singleItem.metadata.parentId == 0}">

<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->

<c:if test="${displayItem.singleItem.metadata.columnNumber <=1}">

<c:if test="${(not empty lastItemWasSingle) && lastItemWasSingle}">
</tr>
</table>
</td>
</tr>
</c:if>

<c:set var="lastItemWasSingle" value="true" />

<c:set var="numOfTr" value="${numOfTr+1}"/>
<c:if test="${!empty displayItem.singleItem.metadata.header}">
	<c:choose>
	<c:when test="${cdisplay>0}">
		<c:set var="scdShowStatus" value="${displayItem.singleItem.scdData.scdDisplayInfo.scdShowStatus}"/>
		<c:set var="cdId" value="${displayItem.singleItem.item.id}"/>
		<c:choose>
		<c:when test="${scdShowStatus == 1}">	<%-- 'SHOW_CHANGABLE' --%>
    		<tr class="aka_stripes" id="<c:out value="hd${cdId}"/>">
		</c:when>
		<c:when test="${scdShowStatus == 2}">	<%-- 'HIDE_CHANGABLE' --%>
			<tr class="aka_stripes" id="<c:out value="hd${cdId}"/>" style="display:none">
		</c:when>
		<c:otherwise>
    		<tr class="aka_stripes">
		</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
    	<tr class="aka_stripes">
	</c:otherwise>
	</c:choose>
            <%--<td class="table_cell_left" bgcolor="#F5F5F5">--%>
        <td class="table_cell_left aka_stripes"><b><c:out value=
          "${displayItem.singleItem.metadata.header}" escapeXml="false"/></b></td>
    </tr>
</c:if>
<c:if test="${!empty displayItem.singleItem.metadata.subHeader}">
	<c:choose>
	<c:when test="${cdisplay>0}">
		<c:set var="scdShowStatus" value="${displayItem.singleItem.scdData.scdDisplayInfo.scdShowStatus}"/>
		<c:set var="cdId" value="${displayItem.singleItem.item.id}"/>
		<c:choose>
		<c:when test="${scdShowStatus == 1}">	<%-- 'SHOW_CHANGABLE' --%>
    		<tr class="aka_stripes" id="<c:out value="sub${cdId}"/>">
		</c:when>
		<c:when test="${scdShowStatus == 2}">	<%-- 'HIDE_CHANGABLE' --%>
			<tr class="aka_stripes" id="<c:out value="sub${cdId}"/>" style="display:none">
		</c:when>
		<c:otherwise>
    		<tr class="aka_stripes">
		</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
    	<tr class="aka_stripes">
	</c:otherwise>
	</c:choose>
        <td class="table_cell_left"><c:out value="${displayItem.singleItem.metadata.subHeader}" escapeXml=
          "false"/></td>
    </tr>
</c:if>

<c:set var="rowDisplay" value="${displayItem.singleItem.scdData.scdDisplayInfo.rowDisplayStatus}"/>
<c:set var="rowSCDShowIDStr" value="${displayItem.singleItem.scdData.scdDisplayInfo.rowSCDShowIDStr}"/>
<input type="hidden" id="rowSCDShowIDs${numOfTr}" value="${rowSCDShowIDStr}" />
<c:choose>
<c:when test="${rowDisplay == 0}">	<%-- 'SHOW_UNCHANGABLE' --%>
	<tr>
</c:when>
<c:when test="${rowDisplay == 1}">	<%-- 'SHOW_CHANGABLE' --%>
	<tr id="tr${numOfTr}">
</c:when>
<c:otherwise>
	<tr id="tr${numOfTr}" style="display:none">
</c:otherwise>
</c:choose>
    <td class="itemHolderClass table_cell_left" id="itemHolderId_input${displayItem.singleItem.item.id}">
        <c:set var="prevItemHolderId" value="${displayItem.singleItem.item.id}"/>
        <table border="0">
            <tr>
                </c:if>

                		<c:choose>
						<c:when test="${cdisplay > 0}">
							<c:set var="scdShowStatus" value="${displayItem.singleItem.scdData.scdDisplayInfo.scdShowStatus}"/>
							<c:set var="cdId" value="${displayItem.singleItem.item.id}"/>
							<input type="hidden" id="col${cdId}" value="${numOfTr}"/>
							<c:choose>
							<c:when test="${scdShowStatus == 1}">	<%-- 'SHOW_CHANGABLE' --%>
		                		<td valign="top" id="t${cdId}">
					    	</c:when>
					    	<c:when test="${scdShowStatus == 2}">	<%-- 'HIDE_CHANGABLE' --%>
		                		<td valign="top" id="t${cdId}" style="display:none">
		                	</c:when>
		                	<c:otherwise>
		    					<td valign="top">
		                	</c:otherwise>
		                	</c:choose>
		                </c:when>
		                <c:otherwise>
		                	<td valign="top">
		                </c:otherwise>
		                </c:choose>


						<c:set var="isItemShown" value="false"/>
						<c:forEach var="formMsg" items="${formMessages}">
							<c:set var="inputValue">input<c:out value="${displayItem.singleItem.item.id}"/></c:set>
							<c:if test="${formMsg.key eq inputValue}">
								<c:set var="isItemShown" value="true"/>
							</c:if>
						</c:forEach>
                        <c:choose>
                    		<c:when test="${isItemShown && hasShown}">
								<table border="0" cellspacing="0" cellpadding="1" class='aka_group_show <c:if test="${prevItemHolderId != displayItem.singleItem.item.id}"> itemHolderClass" id="itemHolderId_input${displayItem.singleItem.item.id}"<c:set var="prevItemHolderId" value="${displayItem.singleItem.item.id}"/></c:if>'>
                    			<tr>
                    		</c:when>
                    		<c:otherwise>
								<table border="0" cellspacing="0" cellpadding="1" <c:if test="${prevItemHolderId != displayItem.singleItem.item.id}">class="itemHolderClass" id="itemHolderId_input${displayItem.singleItem.item.id}"<c:set var="prevItemHolderId" value="${displayItem.singleItem.item.id}"/></c:if>>
                    			<tr>
                    		</c:otherwise>
                    	</c:choose>
                            <td valign="top" class="aka_ques_block"><c:out value="${displayItem.singleItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
                            <!--
                            <td valign="top" class="aka_text_block"><c:out value="${displayItem.singleItem.metadata.leftItemText}" escapeXml="false"/></td>
                            -->
							<td valign="top" class="aka_text_block">
								<c:import url="../submit/generateLeftItemTxt.jsp">
										<c:param name="itemId" value="${displayItem.singleItem.item.id}"/>
										<c:param name="inputType" value="${displayItem.singleItem.metadata.responseSet.responseType.name}"/>
										<c:param name="function" value="${displayItem.singleItem.metadata.responseSet.options[0].value}"/>
										<c:param name="linkText" value="${displayItem.singleItem.metadata.leftItemText}"/>
										<c:param name="side" value="left"/>
								</c:import>
							</td>

                            <td valign="top" nowrap="nowrap">
                                    <%-- display the HTML input tag --%>
                                <c:set var="displayItem" scope="request" value="${displayItem.singleItem}" />
                                <c:import url="../submit/showItemInput.jsp">
                                    <c:param name="key" value="${numOfDate}" />
                                    <c:param name="tabNum" value="${itemNum}"/>
                                    <%-- add default value from the crf --%>
                                    <c:param name="defaultValue" value="${displayItem.singleItem.metadata.defaultValue}"/>
                                    <c:param name="respLayout" value="${displayItem.singleItem.metadata.responseLayout}"/>
                                    <c:param name="originJSP" value="doubleDataEntry"/>
                                </c:import>

                            </td>
                            <c:if test='${displayItem.singleItem.item.units != ""}'>
                                <td valign="top">
                                    <c:out value="(${displayItem.singleItem.item.units})" escapeXml="false"/>
                                </td>
                            </c:if>
                            <td valign="top">
                            	<!--<c:out value="${displayItem.singleItem.metadata.rightItemText}" escapeXml="false" />-->
								<c:import url="../submit/generateLeftItemTxt.jsp">
										<c:param name="itemId" value="${displayItem.singleItem.item.id}"/>
										<c:param name="inputType" value="${displayItem.singleItem.metadata.responseSet.responseType.name}"/>
										<c:param name="function" value="${displayItem.singleItem.metadata.responseSet.options[0].value}"/>
										<c:param name="linkText" value="${displayItem.singleItem.metadata.rightItemText}"/>
										<c:param name="side" value="right"/>
								</c:import>
							</td>
                        </tr>
                            <%--try this, displaying error messages in their own row--%>
                            <%--We won't need this if the error messages are not embedded in the form:
                            <tr>
                              <td valign="top" colspan="4" style="text-align:right">
                                <c:import url="../showMessage.jsp"><c:param name="key" value=
                                  "input${displayItem.singleItem.item.id}" /></c:import> </td>
                            </tr>--%>
                    </table>
                </td>
 

<c:if test="${displayItem.singleItem.numChildren > 0}">
	</tr>
    <tr>
            <%-- indentation --%>
        <!--<td class="table_cell">&nbsp;</td>-->
            <%-- NOW SHOW THE CHILDREN --%>

        <td class="table_cell">
            <table border="0">
                <c:set var="notFirstRow" value="${0}" />
                <c:forEach var="childItem" items="${displayItem.singleItem.children}">
                <c:set var="ccdisplay" value="${childItem.scdData.scdItemMetadataBean.id}"/>
				<c:if test="${childItem.metadata.showItem || ccdisplay>0}">

                <c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
                <c:if test="${currColumn == 1}">
                <c:if test="${notFirstRow != 0}">
                    </tr>
                </c:if>
                <c:choose>
				<c:when test="${ccdisplay > 0}">
					<c:set var="scdShowStatus" value="${childItem.scdData.scdDisplayInfo.scdShowStatus}"/>
					<c:set var="cdId" value="${childItem.item.id}"/>
					<c:choose>
					<c:when test="${scdShowStatus == 1}"> <%-- 'SHOW_CHANGABLE' --%>
                		<tr id="t${cdId}">
			    	</c:when>
			    	<c:when test="${scdShowStatus == 2}">	<%-- 'HIDE_CHANGABLE' --%>
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
                    <c:set var="notFirstRow" value="${1}" />
                        <%-- indentation --%>
                    <td valign="top">&nbsp;</td>
                    </c:if>
                        <%--
                          this for loop "fills in" columns left blank
                          e.g., if the first childItem has column number 2, and the next one has column number 5,
                          then we need to insert one blank column before the first childItem, and two blank columns between the second and third children
                        --%>
                    <c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
                        <td valign="top">&nbsp;</td>
                    </c:forEach>

                    <td valign="top">
                        <table border="0" class="itemHolderClass" id="itemHolderId_input${childItem.item.id}">
                            <tr>


                                <td valign="top" class="aka_ques_block"><c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
                                <td valign="top" class="aka_text_block">
                                		<!--<c:out value="${childItem.metadata.leftItemText}" escapeXml="false"/>-->

									<c:import url="../submit/generateLeftItemTxt.jsp">
										<c:param name="itemId" value="${childItem.item.id}"/>
										<c:param name="inputType" value="${childItem.metadata.responseSet.responseType.name}"/>
										<c:param name="function" value="${childItem.metadata.responseSet.options[0].value}"/>
										<c:param name="linkText" value="${childItem.metadata.leftItemText}"/>
										<c:param name="side" value="left"/>
									</c:import>
								</td>
                                <td valign="top" nowrap="nowrap">

                                        <%-- display the HTML input tag --%>
                                    <c:set var="displayItem" scope="request" value="${childItem}" />
                                    <c:import url="../submit/showItemInput.jsp" >
                                        <c:param name="key" value="${numOfDate}" />
                                        <c:param name="tabNum" value="${itemNum}"/>
                                        <c:param name="defaultValue" value="${childItem.metadata.defaultValue}"/>
                                        <c:param name="respLayout" value="${childItem.metadata.responseLayout}"/>
                                        <c:param name="originJSP" value="doubleDataEntry"/>
                                    </c:import>
                                        <%--	<br />--%><%--<c:import url="../showMessage.jsp"><c:param name="key" value="input${childItem.item.id}" /></c:import>--%>
                                </td>
                                <c:if test='${childItem.item.units != ""}'>
                                    <td valign="top"> <c:out value="(${childItem.item.units})" escapeXml="false"/> </td>
                                </c:if>
                                <td valign="top">
                                	<!--<c:out value="${childItem.metadata.rightItemText}" escapeXml="false"/>-->
									<c:import url="../submit/generateLeftItemTxt.jsp">
										<c:param name="itemId" value="${childItem.item.id}"/>
										<c:param name="inputType" value="${childItem.metadata.responseSet.responseType.name}"/>
										<c:param name="function" value="${childItem.metadata.responseSet.options[0].value}"/>
										<c:param name="linkText" value="${childItem.metadata.rightItemText}"/>
										<c:param name="side" value="right"/>
									</c:import>
								 </td>
                            </tr>
                                <%--BWP: try this--%>
                            <tr>
                                <td valign="top" colspan="4" style="text-align:right">
                                    <c:import url="../showMessage.jsp"><c:param name="key" value=
                                      "input${childItem.item.id}" /></c:import> </td>
                            </tr>
                        </table>
                    </td>
                    </c:if>
                    </c:forEach>
                </tr>
            </table>
        </td>
</c:if>

<c:if test="${itemStatus.last}">
</tr>
</table>
</td>
</tr>
</c:if>

</c:if>
</c:if>
</c:otherwise>
</c:choose>

<c:set var="displayItemNum" value="${displayItemNum + 1}" />
</c:if>
</c:forEach>
</table>

<%-- END IF NEW TABLE --%>

<!--   return to top table:
possibly, stick the upcoming section as a new row in the above table, because it sometimes displays beneath this
table-->

<table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 6px;">
    <!--   style="padding-right: 50px"-->
    <tr>
        <td valign="bottom" nowrap="nowrap">
            <a href="#top">&nbsp;&nbsp;<fmt:message key="return_to_top" bundle="${resword}"/></a>
        </td>
        <td align="right" valign="bottom">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <c:choose>
                        <c:when test="${stage !='adminEdit' && section.lastSection}">
                            <c:choose>
                                <c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
                                    <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="sm('box', this, 730,100);">
                                    </td>
                                    <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                </c:when>
                                <c:otherwise>
                                    <td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
                                                                <c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="${markCRFMethodName}">
                                    </td>
                                    <td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <td colspan="2">&nbsp;</td>
                        </c:otherwise>
                    </c:choose>
                    <td><input type="submit" id="srl" name="submittedResume" value="<fmt:message key="save" bundle="${resword}"/>" class=
                      "button_medium" onclick="disableSubmit(); this.form.submit();"/></td>
                    <c:if test="${section.lastSection and hideSaveAndNextButton eq null }">
                   		<td><input type="submit" id="snl" name="saveAndNext" value="${save_and_next_button_caption}" class=
           			  		"${save_and_next_button_class}" onclick="disableSubmit(); this.form.submit();"/></td>
                    </c:if>
                    <td>
                        <input type="submit" id="sel" name="submittedExit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium" onClick="return checkEntryStatus('DataStatus_bottom', this);" />
                    </td>

                    <c:choose>
                        <c:when test="${! empty formMessages}">
                            <td valign="bottom"><img name="DataStatus_bottom" alt="<fmt:message key="data_status" bundle="${resword}"/>" title="<fmt:message key="changed_not_saved" bundle="${restext}"/>" src="images/icon_UnsavedData.gif">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td valign="bottom"><img name="DataStatus_bottom" alt="<fmt:message key="data_status" bundle="${resword}"/>" title="<fmt:message key="not_changed_data" bundle="${restext}"/>" src="images/icon_UnchangedData.gif">&nbsp;</td>
                        </c:otherwise>
                    </c:choose>



                </tr>
            </table>
        </td>
    </tr>
</table>

<!-- End Table Contents -->

</div>
</div></div></div></div></div></div></div></div>
</div>
</form>
</div>
<div id="testdiv1" style=
  "position:absolute;visibility:hidden;background-color:white"></div>
</div>
<c:if test="${study.studyParameterConfig.autoTabbing == 'yes'}">
    <script>initAutotabbing();</script>
</c:if>
<jsp:include page="../include/changeTheme.jsp"/>
<script>
    window.onbeforeunload = function(){
        if (window.opener) {
            window.opener.location.reload(true);
        }
    }
</script>
</body>
</html>
