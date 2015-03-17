<%@ page import="org.akaza.openclinica.bean.submit.EventCRFBean" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<link rel="shortcut icon" type="image/x-icon" href="<c:out value="${contextPath}" />/images/favicon.ico">

<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>
<jsp:useBean scope='request' id='exitTo' class='java.lang.String'/>
<jsp:useBean scope='request' id='nameNotes' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='intrvDates' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='existingNameNotes' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='existingIntrvDateNotes' class='java.util.ArrayList'/>

<script type="text/javascript" src="<c:out value="${contextPath}" />/includes/wz_tooltip/wz_tooltip.js"></script>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<c:set var="interviewer" value="${toc.eventCRF.interviewerName}" />
<c:set var="interviewDate" value="${toc.eventCRF.dateInterviewed}" />
<c:set var="itemId" value="${displayItem.item.id}" />

<style type="text/css">
    .tooltip {
        width:100%;
    }
</style>

<script type="text/javascript" language="javascript">

    window.expandCrfInfo = '${empty expandCrfInfo ? false : expandCrfInfo}';

    //If someone closes the browser on data entry stage, the following request should be
    //sent to the server to make this CRF available for data entry.
    var closing = true;
    function clsWin() {
        if (closing) {
            $.post("CheckCRFLocked?userId=<c:out value="${userBean.id}"/>&exitTo=<c:out value="${exitTo}" />", function (data) {
                return;
            });
        }
    }
    $(document).ready(function () {
        $("a").click(function (event) {
            closing = false;
        });
        $("input").click(function (event) {
            closing = false;
        });
        $("select").click(function (event) {
            closing = false;
        });
    });

    function genToolTipFromArray(flag) {
        var resStatus = new Array();
        var detailedNotes = new Array();
        var discrepancyType = new Array();
        var updatedDates = new Array();
        var parentDnids = new Array();
        var totNotes = 0;
        var footNote = '<fmt:message key="footNote" bundle="${resword}"/>';
        var i = 0;
        var discNotes = new Array();
        var title = '<fmt:message key="tooltip_title1" bundle="${resword}"/>';
        if (flag == 'interviewNotes') {
            <c:forEach var="discrepancyNoteBeans" items="${nameNotes}">
            resStatus[i] =<c:out value="${discrepancyNoteBeans.resolutionStatusId}"/>;
            detailedNotes[i] = '<c:out value="${discrepancyNoteBeans.description}"/>';
            discrepancyType[i] = '<c:out value="${discrepancyNoteBeans.disType.name}"/>';
            updatedDates[i] = '<c:out value="${discrepancyNoteBeans.createdDate}"/>';
            parentDnids[i] = '<c:out value="${discrepancyNoteBeans.parentDnId}"/>';
            i++;
            </c:forEach>
            title = '<fmt:message key="tooltip_name_title" bundle="${resword}"/>';
            totNotes = ${fn:length(existingNameNotes)};
            if (totNotes > 0) {
                footNote = totNotes + " " + '<fmt:message key="foot_threads" bundle="${resword}"/>' + " " + '<fmt:message key="footNote_threads" bundle="${resword}"/>';
            }
        }
        else if (flag == 'dateNotes') {
            <c:forEach var="discrepancyNoteBeans" items="${intrvDates}">
            resStatus[i] =<c:out value="${discrepancyNoteBeans.resolutionStatusId}"/>;
            detailedNotes[i] = '<c:out value="${discrepancyNoteBeans.description}"/>';
            discrepancyType[i] = '<c:out value="${discrepancyNoteBeans.disType.name}"/>';
            updatedDates[i] = '<c:out value="${discrepancyNoteBeans.createdDate}"/>';
            parentDnids[i] = '<c:out value="${discrepancyNoteBeans.parentDnId}"/>';
            i++;
            </c:forEach>
            title = '<fmt:message key="tooltip_name_title" bundle="${resword}"/>';
            totNotes = ${fn:length(existingIntrvDateNotes)};
            if (totNotes > 0) {
                footNote = totNotes + " " + '<fmt:message key="foot_threads" bundle="${resword}"/>' + " " + '<fmt:message key="footNote_threads" bundle="${resword}"/>';
            }
        }

        var htmlgen = '<div class=\"tooltip\">' +
                '<table  width="250">' +
                ' <tr><td  align=\"center\" class=\"header1\">' + title +
                ' </td></tr><tr></tr></table><table  style="border-collapse:collapse" cellspacing="0" cellpadding="0" width="225" >' +
                drawRows(i, resStatus, detailedNotes, discrepancyType, updatedDates, parentDnids) +
                '</table><table width="250"  class="tableborder" align="left">' +
                '</table><table><tr></tr></table>' +
                '<table width="200"><tbody><td height="50" colspan="3">' +
                '<span class=\"note\">' + footNote + '</span>' +
                '</td></tr></tbody></table></table></div>';
        return htmlgen;
    }

    function drawRows(i, resStatus, detailedNotes, discrepancyType, updatedDates, parentDnIds) {
        var row = '';
        var noteType = '';
        for (var x = 0; x < i; x++) {
            if (resStatus[x] == '1') {
                if (parentDnIds[x] == '0') {
                    row += '<tr> <td class=\"label\"></td><td colspan = "3" class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
                else {
                    row += '<tr> <td class=\"label\"><img src="images/icon_Note.gif" width="16" height="13" alt="Note"></td>' + '<td  width="180" align="left" class=\"label\" nowrap>&nbsp;<fmt:message key="open" bundle="${resword}"/>: &nbsp;' + discrepancyType[x] + '&nbsp;' + updatedDates[x] + '</td></tr><tr><td class=\"borderlabel\"></td><td class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
            } else if (resStatus[x] == '2') {
                if (parentDnIds[x] == '0') {
                    row += '<tr> <td class=\"label\"></td><td colspan = "3" class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
                else {
                    row += '<tr > <td  class=\"label\"><img src="images/icon_flagYellow.gif" width="16" height="13" alt="Note"></td>' + '<td width="180"  align="left" class=\"label\" nowrap>&nbsp;<fmt:message key="updated" bundle="${resword}"/>: &nbsp;' + discrepancyType[x] + '&nbsp;' + updatedDates[x] + '</td></tr><tr><td class=\"borderlabel\"></td><td  class=\"borderlabel\" nowrap>&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
            } else if (resStatus[x] == '3') {
                if (parentDnIds[x] == '0') {
                    row += '<tr> <td class=\"label\"></td><td colspan = "3" class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
                else {
                    row += '<tr> <td class=\"label\"><img src="images/icon_flagBlack.gif" width="16" height="13" alt="Note"></td>' + '<td  width="180"  align="left" class=\"label\" nowrap>&nbsp;<fmt:message key="resolved" bundle="${resword}"/>: &nbsp;' + discrepancyType[x] + '&nbsp;' + updatedDates[x] + '</td></tr><tr><td class=\"borderlabel\"></td><td  class=\"borderlabel\" nowrap>&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
            }
            else if (resStatus[x] == '4') {
                if (parentDnIds[x] == '0') {
                    row += '<tr> <td class=\"label\"></td><td colspan = "3" class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
                else {
                    row += '<tr> <td  class=\"label\"><img src="images/icon_flagGreen.gif" width="16" height="13" alt="Note"></td>' + '<td  width="180" align="left" class=\"label\" nowrap>&nbsp;<fmt:message key="closed" bundle="${resword}"/>: &nbsp;' + discrepancyType[x] + '&nbsp;' + updatedDates[x] + '</td></tr><tr><td class=\"borderlabel\"></td><td class=\"borderlabel\" nowrap>&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
            }
            else if (resStatus[x] == '5') {
                if (parentDnIds[x] == '0') {
                    row += '<tr> <td class=\"label\"></td><td colspan = "3" class=\"borderlabel\" nowrap >&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                } else {
                    row += '<tr> <td width="16"  class=\"label\"><img src="images/icon_flagWhite.gif" width="16" height="13" alt="Note"></td>' + '<td width="180"  align="left" class=\"label\" nowrap>&nbsp; <fmt:message key="not_applicable" bundle="${resword}"/>: &nbsp;' + discrepancyType[x] + '&nbsp;' + updatedDates[x] + '</td></tr><tr><td class=\"borderlabel\"></td><td class=\"borderlabel\" nowrap>&nbsp;' + detailedNotes[x].substring(0, 60) + '...</td></tr>';
                }
            }
        }
        return row;
    }

    function callTip(html) {
        Tip(html, BGCOLOR, '#FFFFE5', BORDERCOLOR, '');
    }

</script>

<c:set var="hideAditionalInfoPanel"
       value="${study.studyParameterConfig.interviewerNameRequired == 'not_used' and
		study.studyParameterConfig.interviewDateRequired == 'not_used' and
		study.studyParameterConfig.secondaryIdRequired == 'not_used' and
		study.studyParameterConfig.secondaryLabelViewable == 'false' and
		study.studyParameterConfig.genderRequired == 'false' and
		!toc.studyEventDefinition.repeating and
		study.studyParameterConfig.collectDob == '3' and
		study.studyParameterConfig.personIdShownOnCRF == 'false'}" />

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == "interviewer"}'>
        <c:set var="interviewer" value="${presetValue.value}"/>
    </c:if>
    <c:if test='${presetValue.key == "interviewDate"}'>
        <c:set var="interviewDate" value="${presetValue.value}"/>
    </c:if>
</c:forEach>
<!-- *JSP* submit/interviewer.jsp -->

<table border="0" cellpadding="0" cellspacing="0" onLoad="">

<c:choose>
<c:when test="${study.studyParameterConfig.interviewerNameRequired == 'yes' || study.studyParameterConfig.interviewDateRequired == 'yes'}">
<tr id="CRF_infobox_closed" style="display: all;">
    <td style="padding-top: 3px; padding-left: 0px;" nowrap>
        <div class="moreCrfInfoBlock">
            <div class="crfInfoBlock">
                <div class="table_title_Admin">
                    <fmt:message key="event" bundle="${resword}"/>: </b><c:out value="${toc.studyEventDefinition.name}" />
                    (<fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dteFormat}"/>)
                    &emsp;
	                <c:if test="${not empty siteTitle}">
                        <fmt:message key="site" bundle="${resword}"/>:
                        <c:out value="${siteTitle}"/>
                    </c:if>
                </div>
                <div class="table_title_Admin">
                    <fmt:message key="study" bundle="${resword}"/>: ${studyTitle}
                </div>
            </div>
            <div class="crfInfoDiv">
                <a href="javascript: processCrfMoreInfo();" id="showMoreInfo" style="visibility: ${!hideAditionalInfoPanel ? 'visible' : 'hidden'};">
                    <img id="moreInfoExpandedImg" src="<c:out value="${contextPath}" />/images/sidebar_expand.gif" align="left" border="0" style="margin-right: 5px;">
                    <img id="moreInfoCollapsedImg" src="<c:out value="${contextPath}" />/images/sidebar_collapse.gif" align="left" border="0" style="margin-right: 5px;">
                    <b><fmt:message key="More_info" bundle="${resword}"/></b>
                </a>
            </div>
        </div>
        </div>
    </td>
</tr>
<tr id="CRF_infobox_open">
<td style="padding-top: 15px;">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
    <td valign="bottom">
        <table border="0" cellpadding="0" cellspacing="0" width="100">
            <tr>
                <td nowrap>
                    <div class="tab_BG_h">
                        <div class="tab_R_h" style="padding-right: 40px;">
                            <div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
                                <b><fmt:message key="CRF_info" bundle="${resword}"/></b>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</c:when>
<c:otherwise>
<tr id="CRF_infobox_closed">
    <td style="padding-top: 3px; padding-left: 0px;" nowrap>
        <div class="moreCrfInfoBlock">
            <div class="crfInfoBlock">
                <div class="table_title_Admin">
                    <fmt:message key="event" bundle="${resword}"/>: </b><c:out value="${toc.studyEventDefinition.name}" />
                    (<fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dteFormat}"/>)
                    &emsp;
	                <c:if test="${not empty siteTitle}">
                        <fmt:message key="site" bundle="${resword}"/>:
                        <c:out value="${siteTitle}"/>
                    </c:if>
                </div>
                <div class="table_title_Admin">
                    <fmt:message key="study" bundle="${resword}"/>: ${studyTitle}
                </div>
            </div>
            <div class="crfInfoBlock">
                <div class="crfInfoDiv">
                    <a href="javascript: processCrfMoreInfo();" id="showMoreInfo" style="visibility: ${!hideAditionalInfoPanel ? 'visible' : 'hidden'};">
                        <img id="moreInfoExpandedImg" src="<c:out value="${contextPath}" />/images/sidebar_expand.gif" align="left" border="0" style="margin-right: 5px;">
                        <img id="moreInfoCollapsedImg" src="<c:out value="${contextPath}" />/images/sidebar_collapse.gif" align="left" border="0" style="margin-right: 5px;">
                        <b><fmt:message key="More_info" bundle="${resword}"/></b>
                    </a>
                </div>
            </div>
        </div>
    </td>
</tr>
<tr id="CRF_infobox_open">
<td style="padding-top: 15px;">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
    <td valign="bottom">
        <table border="0" cellpadding="0" cellspacing="0" width="100">
            <tr>
                <td nowrap>
                    <div class="tab_BG_h">
                        <div class="tab_R_h" style="padding-right: 40px;">
                            <div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
                                <b><fmt:message key="CRF_info" bundle="${resword}"/></b>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
</c:otherwise>
</c:choose>
<tr>
<td valign="top">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<c:set var="secondaryIdShow" value="${true}"/>
<c:set var="secondaryLabelViewable" value="${true}"/>
<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel"/>
<c:if test="${study ne null}">
    <c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
    <c:set var="secondaryLabelViewable" value="${study.studyParameterConfig.secondaryLabelViewable == 'true'}"/>
    <c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}"/>
</c:if>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<table border="0" cellpadding="0" cellspacing="1" width="600px" id="ecrfBoxInfo">
<tr id="ecrfParameters">
    <c:if test="${secondaryLabelViewable and secondaryIdShow}">
        <td class="header_crf_cell">
            <b>${secondaryIdLabel}:</b>
            <span id=ssIdLabel><c:out value="${studySubject.secondaryLabel}"/></span>
        </td>
    </c:if>
    <c:if test="${genderShow}">
        <td class="header_crf_cell">
            <span><b>${genderLabel}:</b></span>
            <c:choose>
                <c:when test="${subject.gender==109}"><fmt:message key="M" bundle="${resword}"/></c:when>
                <c:when test="${subject.gender==102}"><fmt:message key="F" bundle="${resword}"/></c:when>
                <c:otherwise>
                    <c:out value="${subject.gender}"/>
                </c:otherwise>
            </c:choose>
        </td>
    </c:if>
    <c:if test="${toc.studyEventDefinition.repeating}">
        <td class="header_crf_cell">
            <b><fmt:message key="occurrence_number" bundle="${resword}"/>:</b>
            <c:out value="${toc.studyEvent.sampleOrdinal}"/>
        </td>
    </c:if>

    <c:if test="${study.studyParameterConfig.collectDob != '3'}">
    <td class="header_crf_cell">
        <b><fmt:message key="age_at_enrollment" bundle="${resword}"/>:</b>
        <c:out value="${age}"/>
    </td>
    <td class="header_crf_cell">
        <c:choose>
            <c:when test="${study.studyParameterConfig.collectDob =='1'}">
                <b><fmt:message key="date_of_birth" bundle="${resword}"/>:</b>
            </c:when>
            <c:otherwise>
                <b><fmt:message key="year_of_birth" bundle="${resword}"/>:</b>
            </c:otherwise>
        </c:choose>
        </c:if>
        <c:if test="${study.studyParameterConfig.collectDob != '3'}">

        <c:choose>
            <c:when test="${study.studyParameterConfig.collectDob == '2' && subject.dateOfBirth.year != null}">
                ${subject.dateOfBirth.year + 1900}
            </c:when>
            <c:otherwise>
                <fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/>
            </c:otherwise>
        </c:choose>
    </td>
    </c:if>
    <c:if test="${study.studyParameterConfig.personIdShownOnCRF == 'true'}">
        <td class="header_crf_cell">
            <b><fmt:message key="person_ID" bundle="${resword}"/>:</b>
            <c:out value="${subject.uniqueIdentifier}"/>
        </td>
    </c:if>
</tr>
<c:forEach var="frmMsg" items="${formMessages}">
    <c:if test="${frmMsg.key eq 'interviewer'}">
        <c:set var="isInError_Int" value="${true}"/>
    </c:if>
    <c:if test="${frmMsg.key eq 'interviewDate'}">
        <c:set var="isInError_Dat" value="${true}"/>
    </c:if>
</c:forEach>
<tr id="preDnShortcutsTr">
    <td class="header_crf_cell" id="itemHolderId_interviewer">
        <c:import url="../submit/dnShortcutAnchors.jsp">
            <c:param name="rowCount" value=""/>
            <c:param name="itemId" value="interviewer" />
            <c:param name="inputName" value="interviewer"/>
        </c:import>
        <c:if test="${study.studyParameterConfig.interviewerNameRequired != 'not_used'}">
            <c:set var="showPreDnShortcutsTr" value="true"/>
            <c:choose>
                <c:when test="${isInError_Int}">
                    <fmt:message key="interviewer_name" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span> &nbsp;
                </c:when>
                <c:otherwise>
                    <fmt:message key="interviewer_name" bundle="${resword}"/>:
                    <c:if test="${study.studyParameterConfig.interviewerNameRequired=='yes'}">*</c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${study.studyParameterConfig.interviewerNameRequired != 'not_used'}">
            <c:set var="showPreDnShortcutsTr" value="true"/>
            <c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
            <c:choose>
                <c:when test="${study.studyParameterConfig.interviewerNameEditable=='true'}">
                    <c:choose>
                        <c:when test="${isInError_Int}">
                            <span class="aka_input_error" style="display:inline-block;height:22px;">
                                <label for="interviewer"></label><input tabindex="${crfTabIndex}" id="interviewer" type="text" name="interviewer" size="15" value="<c:out value="${interviewer}" />" class="aka_input_error">
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                                <input tabindex="${crfTabIndex}" type="text" id="interviewer" name="interviewer" size="15"
                                       value="<c:out value="${interviewer}" />" class="formfieldM">
                            </span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                        <input type="text" disabled size="15"
                               value="<c:out value="${interviewer}" />" class="formfieldM">
                        <input type="hidden" id="interviewer" name="interviewer"
                               value="<c:out value="${interviewer}" />">
                    </span>
                </c:otherwise>
            </c:choose>
            <c:set var="isNewDN" value="${hasNameNote eq 'yes' ? 0 : 1}"/>
            <c:if test="${study.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                <c:choose>
                    <c:when test="${nameNoteResStatus == 0}">
                        <c:set var="imageFileName" value="icon_noNote" />
                    </c:when>
                    <c:when test="${nameNoteResStatus == 1}">
                        <c:set var="imageFileName" value="icon_Note" />
                    </c:when>
                    <c:when test="${nameNoteResStatus == 2}">
                        <c:set var="imageFileName" value="icon_flagYellow" />
                    </c:when>
                    <c:when test="${nameNoteResStatus == 3}">
                        <c:set var="imageFileName" value="icon_flagBlack" />
                    </c:when>
                    <c:when test="${nameNoteResStatus == 4}">
                        <c:set var="imageFileName" value="icon_flagGreen" />
                    </c:when>
                    <c:when test="${nameNoteResStatus == 5}">
                        <c:set var="imageFileName" value="icon_flagWhite" />
                    </c:when>
                    <c:otherwise>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${hasNameNote eq 'yes'}">
                        <a href="#" id="nameNote1" onmouseout="UnTip();"onmouseover="callTip(genToolTipFromArray('interviewNotes'));"
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySubject.id}&itemId=${itemId}&id=${InterviewerNameNote.eventCRFId}&name=${InterviewerNameNote.entityType}&field=interviewer&column=${InterviewerNameNote.column}&enterData=${enterData}&monitor=${monitor}&blank=${blank}','spanAlert-interviewDate'); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewer" name="flag_interviewer" src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a id="nameNote1" href="#" onmouseout="UnTip();" onmouseover="callTip(genToolTipFromArray('interviewNotes'));"
                           onClick="openDSNoteWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?stSubjectId=${studySubject.id}&viewData=y&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name&writeToDB=1&new=${isNewDN}','spanAlert-interviewer', event); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewer" name="flag_interviewer" src="<c:out value="${contextPath}" />/images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                            <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySubject.id}&itemId=${itemId}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name&enterData=${enterData}&monitor=${monitor}&blank=${blank}"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <span ID="spanAlert-interviewer" class="alert"></span>
        </c:if>
    </td>

    <td class="header_crf_cell" id="itemHolderId_interviewDate">
        <c:import url="../submit/dnShortcutAnchors.jsp">
            <c:param name="rowCount" value=""/>
            <c:param name="itemId" value="interviewDate" />
            <c:param name="inputName" value="interviewDate"/>
        </c:import>
        <c:if test="${study.studyParameterConfig.interviewDateRequired != 'not_used'}">
            <c:set var="showPreDnShortcutsTr" value="true"/>
            <c:choose>
                <c:when test="${isInError_Dat}">
                    <fmt:message key="interview_date" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span>
                </c:when>
                <c:otherwise>
                    <fmt:message key="interview_date" bundle="${resword}"/>:
                    <c:if test="${study.studyParameterConfig.interviewDateRequired=='yes'}">
                        *
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${study.studyParameterConfig.interviewDateRequired != 'not_used'}">
                <c:set var="showPreDnShortcutsTr" value="true"/>
                <c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
                <c:choose>
                    <c:when test="${study.studyParameterConfig.interviewDateEditable=='true'}">
                        <c:choose>
                            <c:when test="${isInError_Dat}">
                                <span class="aka_input_error" style="display:inline-block;height:22px;">
                                <label for="interviewDate"></label>
                                <input tabindex="${crfTabIndex}" id="interviewDate" type="text" name="interviewDate" size="15"
                                       value="<c:out value="${interviewDate}" />" class="aka_input_error">
                            </c:when>
                            <c:otherwise>
                                <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                                <input tabindex="${crfTabIndex}" id="interviewDate" type="text" name="interviewDate" size="15"
                                       value="<c:out value="${interviewDate}" />" class="formfieldM">
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                        <input id="interviewDate" type="text" disabled size="15"
                               value="<c:out value="${interviewDate}" />" class="formfieldM">
                        <input type="hidden" name="interviewDate"
                               value="<c:out value="${interviewDate}" />">
                    </c:otherwise>
                </c:choose>
                </span>
                <a href="#!" onclick="$('#interviewDate').datepicker({ dateFormat: '<fmt:message key='date_format_calender' bundle='${resformat}'/>', showOn: 'none'}).datepicker('show');">
                    <img style="vertical-align: middle;" src="<c:out value="${contextPath}" />/images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0"/>
                </a>
                <c:if test="${study.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNewDNDate" value="${hasDateNote eq 'yes' ? 0 : 1}"/>
                <c:choose>
                <c:when test="${IntrvDateNoteResStatus == 0}">
                    <c:set var="imageFileName" value="icon_noNote" />
                </c:when>
                <c:when test="${IntrvDateNoteResStatus == 1}">
                    <c:set var="imageFileName" value="icon_Note" />
                </c:when>
                <c:when test="${IntrvDateNoteResStatus == 2}">
                    <c:set var="imageFileName" value="icon_flagYellow" />
                </c:when>
                <c:when test="${IntrvDateNoteResStatus == 3}">
                    <c:set var="imageFileName" value="icon_flagBlack" />
                </c:when>
                <c:when test="${IntrvDateNoteResStatus == 4}">
                    <c:set var="imageFileName" value="icon_flagGreen" />
                </c:when>
                <c:when test="${IntrvDateNoteResStatus == 5}">
                    <c:set var="imageFileName" value="icon_flagWhite" />
                </c:when>
                <c:otherwise>
                </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${hasDateNote eq 'yes'}">
                        <a href="#"  onmouseover="callTip(genToolTipFromArray('dateNotes') );" onmouseout="UnTip();"
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySubject.id}&itemId=${itemId}&id=${InterviewerDateNote.eventCRFId}&name=${InterviewerDateNote.entityType}&field=interviewDate&column=${InterviewerDateNote.column}&enterData=${enterData}&monitor=${monitor}&blank=${blank}','spanAlert-interviewDate'); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewDate" name="flag_interviewDate" src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="#"  onmouseover="callTip(genToolTipFromArray('dateNotes') );" onmouseout="UnTip();"
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?stSubjectId=${studySubject.id}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed&writeToDB=1&new=${isNewDNDate}','spanAlert-interviewDate', undefined, event); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewDate" name="flag_interviewDate"src="<c:out value="${contextPath}" />/images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                            <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySubject.id}&itemId=${itemId}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed&enterData=${enterData}&monitor=${monitor}&blank=${blank}"/>
                        </a>
                    </c:otherwise>
                </c:choose>
                </c:if>
                <span ID="spanAlert-interviewDate" class="alert"></span>
                </c:if>
    </td>
</tr>
</table>

<c:if test="${showPreDnShortcutsTr ne 'true'}">
    <script>
        $("#preDnShortcutsTr").addClass("hidden");
    </script>
</c:if>

</div></div></div></div></div></div></div></div>
</td>
</tr>
</table></table>

<script>tabbingMode = "${event_def_crf_bean.tabbingMode}";</script>
<c:set var="tdPercentWidth" value="16.7%"/>
<c:set var="dnShortcutsSpan" value="${6}"/>
<c:set var="dnShortcutsWidth" value="${110}"/>
<c:if test="${crfShortcutsAnalyzer.totalResolutionProposed == 0}">
    <c:set var="tdPercentWidth" value="20%"/>
    <c:set var="dnShortcutsSpan" value="${dnShortcutsSpan - 1}"/>
</c:if>
<span class="hidden" id="dnShortcutsSpan">${dnShortcutsSpan}</span>
<span class="hidden" id="dnShortcutsWidth">${dnShortcutsWidth}</span>
<span class="hidden" id="dnShortcutsAllowSdvWithOpenQueries">${study.studyParameterConfig.allowSdvWithOpenQueries}</span>
<span class="hidden" id="userIsAbleToSDVItems">${crfShortcutsAnalyzer.userIsAbleToSDVItems}</span>
<table id="dnShortcutsTable" border="0" cellspacing="0" cellpadding="0" style="cursor: default;position: absolute;left: 0px;top: 0px;" class="notSelectable">
    <tr>
        <td>
            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR"><div class="tablebox_center">
                <table id="dnShortcutsSubTable" border="0" cellspacing="0" cellpadding="0" width="${dnShortcutsSpan * dnShortcutsWidth}px">
                    <tr>
                        <td colspan="${dnShortcutsSpan}" valign="top" class="table_cell_left_header" style="padding-left: 6px; padding-right: 6px;">
                            <b><fmt:message key="crf_shortcuts_header" bundle="${resword}"/>:</b>
                            <a onclick="processPushpin($(this));" type="image" id="pushpin" class="ui-icon ui-icon-bullet" title="<fmt:message key="unlock" bundle="${resword}"/>" unlocktitle="<fmt:message key="unlock" bundle="${resword}"/>" locktitle="<fmt:message key="lock" bundle="${resword}"/>">&nbsp;</a>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#CC0000;text-align: center;"><fmt:message key="openDn" bundle="${resword}"/></td>
                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#D4A718;text-align: center;"><fmt:message key="updatedDn" bundle="${resword}"/></td>
                        <c:if test="${crfShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="resolved" bundle="${resword}"/></td></c:if>
                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#7CB98F;text-align: center;"><fmt:message key="closedDn" bundle="${resword}"/></td>
                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="annotations" bundle="${resword}"/></td>
                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="itemsToSDV" bundle="${resword}"/></td>
                    </tr>
                    <tr>
                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#CC0000;"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalNew}" nextdnlink="${crfShortcutsAnalyzer.nextNewDnLink}" onclick="highlightFieldForDNShortcutAnchor(0, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalNew">&nbsp;${crfShortcutsAnalyzer.totalNew}&nbsp;</div></a></td>
                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#D4A718;"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalUpdated}" nextdnlink="${crfShortcutsAnalyzer.nextUpdatedDnLink}" onclick="highlightFieldForDNShortcutAnchor(1, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalUpdated">&nbsp;${crfShortcutsAnalyzer.totalUpdated}&nbsp;</div></a></td>
                        <c:if test="${crfShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black;"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalResolutionProposed}" nextdnlink="${crfShortcutsAnalyzer.nextResolutionProposedLink}" onclick="highlightFieldForDNShortcutAnchor(2, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalResolutionProposed">&nbsp;${crfShortcutsAnalyzer.totalResolutionProposed}&nbsp;</div></a></td></c:if>
                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#7CB98F;"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalClosed}" nextdnlink="${crfShortcutsAnalyzer.nextClosedDnLink}" onclick="highlightFieldForDNShortcutAnchor(3, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalClosed">&nbsp;${crfShortcutsAnalyzer.totalClosed}&nbsp;</div></a></td>
                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalAnnotations}" nextdnlink="${crfShortcutsAnalyzer.nextAnnotationLink}" onclick="highlightFieldForDNShortcutAnchor(4, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalAnnotations">&nbsp;${crfShortcutsAnalyzer.totalAnnotations}&nbsp;</div></a></td>
                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black"><a class="dnShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalItemsToSDV}" nextdnlink="${crfShortcutsAnalyzer.nextItemToSDVLink}" onclick="highlightFieldForDNShortcutAnchor(5, this);"><div style="width: 100%; text-align: center;" id="dnShortcutTotalItemsToSDV">&nbsp;${crfShortcutsAnalyzer.totalItemsToSDV}&nbsp;</div></a></td>
                    </tr>
                </table>
            </div></div></div></div></div></div></div></div></div>
            <c:if test="${crfShortcutsAnalyzer eq null || (crfShortcutsAnalyzer.totalNew == 0 && crfShortcutsAnalyzer.totalUpdated == 0 && crfShortcutsAnalyzer.totalResolutionProposed == 0 && crfShortcutsAnalyzer.totalClosed == 0 && crfShortcutsAnalyzer.totalAnnotations == 0 && crfShortcutsAnalyzer.totalItemsToSDV == 0)}">
                <script>
                    $("#dnShortcutsTable").addClass("hidden");
                </script>
            </c:if>
        </td></tr>
</table>

<script>
    $("#dnShortcutsTable").css("top", dnShortcutsTableDefTop + "px");
    $("#dnShortcutsTable").css("left", dnShortcutsTableDefLeft + "px");
    initCrfMoreInfo();
    var dnShortcutInterval;
    var dnShortcutFunction = function() {
        try {
            var end = document.location.href.length;
            var start = document.location.href.indexOf("#");
            if (start > 0) {
                var dnShortcutId = document.location.href.substring(start, end);
                if (dnShortcutId == "#" || $(dnShortcutId).length == 0) {
                    clearInterval(dnShortcutInterval);
                } else {
                    var positionTop = parseInt($(dnShortcutId).position().top);
                    var browserClientHeight = getBrowserClientHeight();
                    if (positionTop >= 0) {
                        if (positionTop > browserClientHeight) {
                            document.location.href = document.location.href;
                        }
                        highlightFirstFieldForDNShortcutAnchors(dnShortcutId.replace("#", ""));
                        clearInterval(dnShortcutInterval);
                    }
                }
            } else {
                clearInterval(dnShortcutInterval);
            }
        } catch (e) {
            console.log("Error: " + e);
        }
    }
    $(document).ready(function () {
        dnShortcutInterval = setInterval(dnShortcutFunction, 1);
    });
    window.updateCRFHeader = function(field, itemId, rowCount, resolutionStatusId) {
        gfAddOverlay();
        var parametersHolder = {
            contextPath: "<%=request.getContextPath()%>",
            servletPath: document.location.pathname.replace("<%=request.getContextPath()%>", ""),
            restfulUrl: document.location.pathname.toString().indexOf("/ClinicalData/html/view") >= 0,
            tabId: "<%=request.getAttribute("tabId") == null ? request.getParameter("tabId") : request.getAttribute("tabId")%>",
            sectionId: "<%=request.getAttribute("sectionId") == null ? request.getParameter("sectionId") : request.getAttribute("sectionId")%>",
            itemId: parseInt(itemId),
            field: field,
            rowCount: rowCount,
            resolutionStatusId: parseInt(resolutionStatusId),
            eventCRFId: parseInt("${eventCRF.id}"),
            eventDefinitionCRFId: parseInt("<%=request.getParameter("eventDefinitionCRFId") == null ? request.getAttribute("eventDefinitionCRFId") : request.getParameter("eventDefinitionCRFId")%>"),
            studyEventId: parseInt("<%=request.getParameter("studyEventId") == null ? request.getAttribute("studyEventId") : request.getParameter("studyEventId")%>"),
            subjectId: parseInt("<%=request.getParameter("subjectId") == null ? request.getAttribute("subjectId") : request.getParameter("subjectId")%>"),
            action: "<%=request.getParameter("action") == null ? request.getAttribute("action") : request.getParameter("action")%>",
            exitTo: "<%=request.getParameter("exitTo") == null ? request.getAttribute("exitTo") : request.getParameter("exitTo")%>",
            crfVersionId: parseInt("<%=request.getParameter("crfVersionId") == null ? request.getAttribute("crfVersionId") : request.getParameter("crfVersionId")%>")
        }
        if (parametersHolder.exitTo.toLowerCase() == "null") {
            parametersHolder.exitTo = "";
        }
        resetHighlightedFieldsForDNShortcutAnchors();
        updateCRFHeaderFunction(parametersHolder);
    };

    $(document).ready(function () {
        var currentTr = $('<tr>');
        var table = $('#ecrfBoxInfo');
        $('#ecrfParameters td').each(function(index) {
            currentTr.append(this);
            if (index % 2) {
                table.append(currentTr);
                currentTr = $('<tr>');
            } else {
                table.append(currentTr);
            }
        });
        table.append($('#preDnShortcutsTr'));
    });
</script>
