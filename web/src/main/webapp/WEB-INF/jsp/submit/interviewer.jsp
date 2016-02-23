<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
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
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<c:set var="interviewer" value="${toc.eventCRF.interviewerName}" />
<c:set var="interviewDate"><cc-fmt:formatDate value="${toc.eventCRF.dateInterviewed}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/></c:set>
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="originJSP" value="${param.originJSP}" />
<c:set var="writeToDB" value="${originJSP eq 'initialDataEntry' ? '0' : '1'}"/>

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
       value="${subjectStudy.studyParameterConfig.interviewerNameRequired == 'not_used' and
		subjectStudy.studyParameterConfig.interviewDateRequired == 'not_used' and
		study.studyParameterConfig.secondaryIdRequired == 'not_used' and
		study.studyParameterConfig.secondaryLabelViewable == 'false' and
		study.studyParameterConfig.genderRequired == 'false' and
		!toc.studyEventDefinition.repeating and
		study.studyParameterConfig.collectDob == '3' and
		subjectStudy.studyParameterConfig.personIdShownOnCRF == 'false'}" />

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == "interviewer"}'>
        <c:set var="interviewer" value="${presetValue.value}"/>
    </c:if>
    <c:if test='${presetValue.key == "interviewDate"}'>
        <c:set var="interviewDate" value="${presetValue.value}"/>
    </c:if>
</c:forEach>
<!-- *JSP* submit/interviewer.jsp -->

<c:if test="${study.studyParameterConfig.interviewDateRequired == 'yes' and study.studyParameterConfig.interviewDateEditable != 'true' and empty interviewDate}">
    <c:set var="interviewDate"><cc-fmt:formatDate value="${studyEvent != null and studyEvent.dateStarted != null ? studyEvent.dateStarted : currentDate}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/></c:set>
</c:if>

<table border="0" cellpadding="0" cellspacing="0" onLoad="">

<c:choose>
<c:when test="${subjectStudy.studyParameterConfig.interviewerNameRequired == 'yes'
	|| subjectStudy.studyParameterConfig.interviewDateRequired == 'yes'}">
<tr id="CRF_infobox_closed" style="display: all;">
    <td style="padding-top: 3px; padding-left: 0px;" nowrap>
        <div class="moreCrfInfoBlock">
            <div class="crfInfoBlock">
                <div class="table_title_Admin">
                    <fmt:message key="event" bundle="${resword}"/>: <c:out value="${toc.studyEventDefinition.name}" />
                    (<cc-fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>)
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
                <a href="javascript: crfShortcutsIgnoreResize(true); processCrfMoreInfo(); adjustCrfShortcutsTopPosition(); crfShortcutsIgnoreResize(false);" id="showMoreInfo" style="visibility: ${!hideAditionalInfoPanel ? 'visible' : 'hidden'};">
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
                    <fmt:message key="event" bundle="${resword}"/>: <c:out value="${toc.studyEventDefinition.name}" />
                    (<cc-fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>)
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
    <c:if test="${subjectStudy.studyParameterConfig.personIdShownOnCRF == 'true'}">
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
<tr id="preCrfShortcutsTr">
    <td class="header_crf_cell" id="itemHolderId_interviewer">
        <c:import url="../submit/crfShortcutAnchors.jsp">
            <c:param name="rowCount" value=""/>
            <c:param name="itemId" value="interviewer" />
            <c:param name="inputName" value="interviewer"/>
        </c:import>
        <c:if test="${subjectStudy.studyParameterConfig.interviewerNameRequired != 'not_used'}">
            <c:set var="showPreCrfShortcutsTr" value="true"/>
            <c:choose>
                <c:when test="${isInError_Int}">
                    <fmt:message key="interviewer_name" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span> &nbsp;
                </c:when>
                <c:otherwise>
                    <fmt:message key="interviewer_name" bundle="${resword}"/>:
                    <c:if test="${subjectStudy.studyParameterConfig.interviewerNameRequired=='yes'}"><span class="alert">*</span></c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${subjectStudy.studyParameterConfig.interviewerNameRequired != 'not_used'}">
            <c:set var="showPreCrfShortcutsTr" value="true"/>
            <c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
            <c:choose>
                <c:when test="${subjectStudy.studyParameterConfig.interviewerNameEditable=='true'}">
                    <c:choose>
                        <c:when test="${isInError_Int}">
                            <span class="aka_input_error" style="display:inline-block;height:22px;">
                                <label for="interviewer"></label><input tabindex="${crfTabIndex}" id="interviewer" type="text" name="interviewer" maxlength="${interviewerNameMaxLength}" size="15" value="<c:out value="${interviewer}" />" class="aka_input_error">
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                                <input tabindex="${crfTabIndex}" type="text" id="interviewer" name="interviewer" maxlength="${interviewerNameMaxLength}" size="15"
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
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=${writeToDB}&stSubjectId=${studySubject.id}&itemId=${itemId}&id=${InterviewerNameNote.eventCRFId}&name=${InterviewerNameNote.entityType}&field=interviewer&column=${InterviewerNameNote.column}&enterData=${enterData}&monitor=${monitor}&blank=${blank}','spanAlert-interviewDate'); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewer" name="flag_interviewer" src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a id="nameNote1" href="#" onmouseout="UnTip();" onmouseover="callTip(genToolTipFromArray('interviewNotes'));"
                           onClick="openDSNoteWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?stSubjectId=${studySubject.id}&viewData=y&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name&writeToDB=${writeToDB}&newNote=${isNewDN}','spanAlert-interviewer', event); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewer" name="flag_interviewer" src="<c:out value="${contextPath}" />/images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                            <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=${writeToDB}&stSubjectId=${studySubject.id}&itemId=${itemId}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewer&column=interviewer_name&enterData=${enterData}&monitor=${monitor}&blank=${blank}"/>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <span ID="spanAlert-interviewer" class="alert"></span>
        </c:if>
    </td>

    <td class="header_crf_cell" id="itemHolderId_interviewDate">
        <c:import url="../submit/crfShortcutAnchors.jsp">
            <c:param name="rowCount" value=""/>
            <c:param name="itemId" value="interviewDate" />
            <c:param name="inputName" value="interviewDate"/>
        </c:import>
        <c:if test="${subjectStudy.studyParameterConfig.interviewDateRequired != 'not_used'}">
            <c:set var="showPreCrfShortcutsTr" value="true"/>
            <c:choose>
                <c:when test="${isInError_Dat}">
                    <fmt:message key="interview_date" bundle="${resword}"/>: <span class="aka_exclaim_error">! </span>
                </c:when>
                <c:otherwise>
                    <fmt:message key="interview_date" bundle="${resword}"/>:
                    <c:if test="${subjectStudy.studyParameterConfig.interviewDateRequired=='yes'}">
                        <span class="alert">*</span>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${subjectStudy.studyParameterConfig.interviewDateRequired != 'not_used'}">
                <c:set var="showPreCrfShortcutsTr" value="true"/>
                <c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
                <c:choose>
                    <c:when test="${subjectStudy.studyParameterConfig.interviewDateEditable=='true'}">
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
                		</span>
						<ui:calendarIcon onClickSelector="'#interviewDate'" />
                    </c:when>
                    <c:otherwise>
                        <span class="formfieldM_BG" style="display:inline-block;height:22px;">
                        <input id="interviewDate" type="text" disabled size="15"
                               value="<c:out value="${interviewDate}" />" class="formfieldM">
                        <input type="hidden" name="interviewDate"
                               value="<c:out value="${interviewDate}" />">
                		</span>
                    </c:otherwise>
                </c:choose>
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
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=${writeToDB}&stSubjectId=${studySubject.id}&itemId=${itemId}&id=${InterviewerDateNote.eventCRFId}&name=${InterviewerDateNote.entityType}&field=interviewDate&column=${InterviewerDateNote.column}&enterData=${enterData}&monitor=${monitor}&blank=${blank}','spanAlert-interviewDate'); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewDate" name="flag_interviewDate" src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="#"  onmouseover="callTip(genToolTipFromArray('dateNotes') );" onmouseout="UnTip();"
                           onClick="openDNoteWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?stSubjectId=${studySubject.id}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed&writeToDB=${writeToDB}&newNote=${isNewDNDate}','spanAlert-interviewDate', undefined, event); return false;">
                            <img style="vertical-align: middle;" id="flag_interviewDate" name="flag_interviewDate"src="<c:out value="${contextPath}" />/images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                            <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?writeToDB=${writeToDB}&stSubjectId=${studySubject.id}&itemId=${itemId}&id=<c:out value="${toc.eventCRF.id}"/>&name=eventCrf&field=interviewDate&column=date_interviewed&enterData=${enterData}&monitor=${monitor}&blank=${blank}"/>
                        </a>
                    </c:otherwise>
                </c:choose>
                </c:if>
                <span ID="spanAlert-interviewDate" class="alert"></span>
                </c:if>
    </td>
</tr>
</table>

<c:if test="${showPreCrfShortcutsTr ne 'true'}">
    <script>
        $("#preCrfShortcutsTr").addClass("hidden");
    </script>
</c:if>

</div></div></div></div></div></div></div></div>
</td>
</tr>
</table></table>

<script>
    initCrfMoreInfo();
    cleanupCRFHeader();
    tabbingMode = "${event_def_crf_bean.tabbingMode}";
</script>
