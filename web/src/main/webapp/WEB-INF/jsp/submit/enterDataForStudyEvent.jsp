<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='request' id='eventId' class='java.lang.String'/>
<c:set var="eventId" value="${eventId}"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="showDDEColumn" value="false"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/submit-header.jsp"/>

<!--script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script-->
<!-- *JSP* submit/enterDataForStudyEvent.jsp -->
<script type="text/javascript" language="javascript">
    function checkCRFLocked(ecId, url){
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                window.location = url;
            }else{
                alert(data);
            }
        });
    }
    function checkCRFLockedInitial(ecId, formName){
        if(ecId==0) {formName.submit(); return;} 
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
                alert(data);
            }
        });
    }
</script>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
        <b><fmt:message key="instructions" bundle="${resword}"/></b>
    </td>
</tr>
<jsp:include page="../include/eventOverviewSideInfo.jsp"/>

<jsp:useBean scope="request" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="studySubject" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean" />
<jsp:useBean scope="request" id="uncompletedEventDefinitionCRFs" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="displayEventCRFs" class="java.util.ArrayList" />

<h1><span class="title_manage"><fmt:message key="view_event" bundle="${resword}"/>:<c:out value="${studyEvent.studyEventDefinition.name}" />
<fmt:message key="for_subject" bundle="${resword}"/> <c:out value="${studySubject.label}"/>

<%-- <h1><span class="title_manage"><fmt:message key="enter_or_validate_data" bundle="${resword}"/><c:out value="${studyEvent.studyEventDefinition.name}" />--%>
 <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2a')">
 <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a> </span></h1>



<a name="global"><a href="javascript:leftnavExpand('globalRecord');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img
  name="ExpandGroup5" src="images/bt_Expand.gif" border="0"></a></a></div>

<div id="globalRecord">
<div style="width: 350px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<div class="tablebox_center">

    <table border="0" cellpadding="0" cellspacing="0" width="335">


        <tr>
            <td valign="top">

                <!-- Table Contents -->

                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                       <td class="table_header_column_top">${studySubjectLabel}</td>
                       <td class="table_cell_top"><c:out value="${studySubject.label}"/>
                        <span style="float:right">
                         <a href="ViewStudySubject?id=<c:out value="${studySubject.id}"/>"
                   onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                   onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img
                   name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>"></a>

                        </span>
                       </td>
                    </tr>
                    <tr>
                        <td class="table_header_column"><fmt:message key="SE" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.studyEventDefinition.name}"/>&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="table_header_column"><fmt:message key="location" bundle="${resword}"/></td>
                        <td class="table_cell">
                            <c:set var="eventLocation" value="${studyEvent.location}"/>
                            <c:if test="${studyEvent.location eq ''}">
                                <c:set var="eventLocation" value="N/A"/>
                            </c:if>
                            <span style="float:left">
                                <c:out value="${eventLocation}"/>
                            </span>
                            <%-- CreateDiscrepancyNote?id=392&name=studyEvent&field=location&column=location&strErrMsg=--%>
                            <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                                <c:set var="isNew" value="${hasLocationNote eq 'yes' ? 0 : 1}"/>
                                <c:choose>
                                    <c:when test="${hasLocationNote eq 'yes'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg','spanAlert-location'); return false;">
                                     <img id="flag_location" name="flag_location" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:when test="${hasLocationNote eq 'no'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg','spanAlert-location'); return false;">
                                     <img id="flag_location" name="flag_location" src="images/icon_flagGreen.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:otherwise>
                                       <c:if test="${!study.status.locked}">
                                        <span style="float:right">
                                        <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg=','spanAlert-location'); return false;">
                                        <img id="flag_location" name="flag_location" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                        </a></span>
                                       </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </c:if></td>
                    </tr>
                    <%-- adding oid here, tbh 06/2008 --%>
                    <tr>
                        <td class="table_header_column"><fmt:message key="study_subject_oid" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studySubject.oid}"/></td>
                    </tr>
                    <tr>
                        <td class="table_divider" colspan="2">&nbsp;</td>
                    </tr>
                    <c:if test="${isStartDateUsed eq true}">
                    <tr>
                        <td class="table_header_column">${startDateLabel}</td>
                        <td class="table_cell"><span style="float:left"><fmt:formatDate value="${studyEvent.dateStarted}" pattern="${dteFormat}"/></span>
                         <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                                <c:set var="isNew" value="${hasStartDateNote eq 'yes' ? 0 : 1}"/>
                                <c:choose>
                                    <c:when test="${hasStartDateNote eq 'yes'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=start_date&column=start_date&strErrMsg','spanAlert-start_date'); return false;">
                                     <img id="flag_start_date" name="flag_start_date" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:when test="${hasStartDateNote eq 'no'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=start_date&column=start_date&strErrMsg','spanAlert-start_date'); return false;">
                                     <img id="flag_start_date" name="flag_start_date" src="images/icon_flagGreen.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:otherwise>
                                       <c:if test="${!study.status.locked}">
                                        <span style="float:right">
                                        <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=start_date&column=start_date&strErrMsg=','spanAlert-start_date'); return false;">
                                            <img id="flag_start_date" name="flag_start_date" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                        </a></span>
                                       </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${isEndDateUsed eq true}">
                    <tr>
                        <td class="table_header_column">${endDateLabel}</td>
                        <td class="table_cell"><span style="float:left"><fmt:formatDate value="${studyEvent.dateEnded}" pattern="${dteFormat}"/></span>

                         <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                                <c:set var="isNew" value="${hasEndDateNote eq 'yes' ? 0 : 1}"/>
                                <c:choose>
                                    <c:when test="${hasEndDateNote eq 'yes'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=end_date&column=end_date&strErrMsg','spanAlert-end_date'); return false;">
                                     <img id="flag_end_date" name="flag_end_date" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:when test="${hasEndDateNote eq 'no'}">
                                     <span style="float:right"><a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=end_date&column=end_date&strErrMsg','spanAlert-end_date'); return false;">
                                     <img id="flag_end_date" name="flag_end_date" src="images/icon_flagGreen.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                     </a>
                                     </span>
                                    </c:when>
                                    <c:otherwise>
                                      <c:if test="${!study.status.locked}">
                                        <span style="float:right">
                                        <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&subjectId=${studySubject.id}&name=studyEvent&field=end_date&column=end_date&strErrMsg=','spanAlert-end_date'); return false;">
                                        <img id="flag_end_date" name="flag_end_date" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" hspace="4" >
                                        </a></span>
                                      </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </td>
                    </tr>
                    </c:if>
                    <tr>
                        <td class="table_header_column"><fmt:message key="subject_event_status" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.subjectEventStatus.name}"/></td>
                    </tr>
                    <tr>
                        <td class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.updater.name}"/> (<fmt:formatDate value="${studyEvent.updatedDate}" pattern="${dteFormat}"/>)</td>
                    </tr>

                </table>

                <!-- End Table Contents -->

            </td>
        </tr>
    </table>
</div>

</div></div></div></div></div></div></div></div>
</div>

</div>

<p><div class="table_title_submit"><fmt:message key="CRFs_in_this_study_event" bundle="${resword}"/>:</div>

<div style="width: 650px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<c:choose>
<c:when test="${empty uncompletedEventDefinitionCRFs && empty displayEventCRFs}">
    <tr>
        <td class="table_cell_left"><fmt:message key="there_are_no_CRF" bundle="${resword}"/></td>
    </tr>
</c:when>

<c:otherwise>
<tr>
    <td align="center" class="table_header_row_left"><fmt:message key="CRF_name" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="version" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="initial_data_entry" bundle="${resword}"/></td>
    <td align="center" class="table_header_row ddeColumnHeader"><fmt:message key="validation" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="actions" bundle="${resword}"/></td>
</tr>
<c:set var="rowCount" value="${0}" />

<c:forEach var="dedc" items="${fullCrfList}">
<c:choose>
<c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean'}">

<c:choose>
<c:when test="${dedc.status.name=='locked'}">
</c:when>
<c:otherwise>
<c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${studyEvent.id}&subjectId=${studySubject.subjectId}&eventCRFId=${dedc.eventCRF.id}&exitTo=EnterDataForStudyEvent?eventId=${eventId}" />
<form name="startForm<c:out value="${dedc.edc.crf.id}"/>" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
<tr valign="top">
	<td class="table_cell_left">
		<c:out value="${dedc.edc.crf.name}" /> 
		<c:if test="${dedc.edc.requiredCRF}">
			<span style="color: orange">*</span>
		</c:if> 
		<c:if test="${dedc.edc.sourceDataVerification.code eq 1 or dedc.edc.sourceDataVerification.code eq 2}">
			<img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/>
		</c:if>
	</td>
	<td class="table_cell">
		<c:set var="versionCount" value="0"/>
		<c:set var="firstVersionId" value="0"/>
		<c:forEach var="version" items="${dedc.edc.versions}">
			<c:if test="${versionCount == 0}">
				<c:set var="firstVersionId" value="${version.id}"/>
			</c:if>
			<c:set var="versionCount" value="${versionCount+1}"/>
        </c:forEach>

    <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
		<c:choose>
			<c:when test="${dedc.eventCRF.notStarted && dedc.eventCRF.id == 0}">
				<input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId" value="<c:out value="${firstVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
			</c:when>
			<c:when test="${versionCount > 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
				<input type="hidden" id="crfVersionId${dedc.eventCRF.crfVersion.id}" name="crfVersionId" value="<c:out value="${dedc.eventCRF.crfVersion.id}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${dedc.eventCRF.crfVersion.id}"/>
			</c:when>
			<c:when test="${versionCount == 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
				<input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId" value="<c:out value="${firstVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
			</c:when>
			<c:otherwise>
				<input type="hidden" id="crfVersionId${defaultVersionId}" name="crfVersionId" value="<c:out value="${defaultVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${defaultVersionId}"/>
			</c:otherwise>
		</c:choose>
        
        <c:choose>
        <c:when test="${versionCount<=1}">
			<c:forEach var="version" items="${dedc.edc.versions}">
				<c:out value="${version.name}"/>
			</c:forEach>
        </c:when>
        <c:when test="${dedc.eventCRF.notStarted || dedc.eventCRF.id == 0}">
        <select name="versionId<c:out value="${dedc.edc.crf.id}"/>" onchange="javascript:changeQuery<c:out value="${dedc.edc.crf.id}"/>();">
            <c:forEach var="version" items="${dedc.edc.versions}">
                <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />
                <c:choose>
                    <c:when test="${(dedc.edc.defaultVersionId == version.id && dedc.eventCRF.id == 0) || (dedc.eventCRF.CRFVersionId == version.id && dedc.eventCRF.notStarted)}">
                        <script>$('#${crfVersionInputId}').val('${version.id}');</script>
                        <option value="<c:out value="${version.id}"/>" selected>
                            <c:out value="${version.name}"/>
                        </option>
                    </c:when>
                    <c:otherwise>
                        <option value="<c:out value="${version.id}"/>">
                            <c:out value="${version.name}"/>
                        </option>
                    </c:otherwise>
                </c:choose>
            </c:forEach><%-- end versions --%>
        </select>
        <SCRIPT LANGUAGE="JavaScript">
            function changeQuery<c:out value="${dedc.edc.crf.id}"/>() {
                var qer = document.startForm<c:out value="${dedc.edc.crf.id}"/>.versionId<c:out value="${dedc.edc.crf.id}"/>.value;
                document.startForm<c:out value="${dedc.edc.crf.id}"/>.crfVersionId.value=qer;
            }
        </SCRIPT>
        </c:when>
        <c:otherwise>
            <c:out value="${dedc.eventCRF.crfVersion.name}"/>
        </c:otherwise>
        </c:choose>
</td>

<c:choose>
    <c:when test="${studyEvent.subjectEventStatus.name=='locked'}">
        <%--<c:when test="${dedc.status.name=='locked'}">--%>
        <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;">
            <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
        </td>
    </c:when>

    <c:when test="${studySubject.status.name != 'removed'&& studySubject.status.name != 'auto-removed'}">
        <c:choose>
            <c:when test="${dedc.eventCRF.id>0 and !dedc.eventCRF.notStarted}">
                <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;"><img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry" bundle="${resword}"/>"></td>
            </c:when>
            <c:otherwise>
                <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;"><img src="images/icon_NotStarted.gif" alt="<fmt:message key="not_started" bundle="${resword}"/>" title="<fmt:message key="not_started" bundle="${resword}"/>"></td>
            </c:otherwise>
        </c:choose>
    </c:when>

    <c:otherwise>
        <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;"><img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="removed" bundle="${resword}"/>"></td>
    </c:otherwise>

</c:choose>

<td class="table_cell ddeColumn">
    <c:if test="${dedc.eventCRF != null && !dedc.eventCRF.notStarted && dedc.eventCRF.owner != null}">
        ${dedc.eventCRF.owner.name}
    </c:if>
    &nbsp;</td>

<td class="table_cell">&nbsp;</td>

<td class="table_cell" style="vertical-align: middle; width:200px;">
            <c:choose>

                <c:when test="${studyEvent.subjectEventStatus.name=='locked'}">
                    <%--<c:when test="${dedc.status.name=='locked'}">--%>
					<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4"/>
                </c:when>

                <c:when test="${studySubject.status.name != 'removed'&& studySubject.status.name != 'auto-removed' && study.status.available && !studyEvent.status.deleted && userRole.role.id ne 6}">
                        <a href="#" onclick="checkCRFLockedInitial('<c:out value="${dedc.eventCRF.id}"/>',document.startForm<c:out value="${dedc.edc.crf.id}"/>);"
                           onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
                           onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');">
                           <img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="enter_data" bundle="${resword}"/>" title="<fmt:message key="enter_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                </c:when>

                <c:otherwise>
					<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
				</c:otherwise>
            </c:choose>
                <a href="#" onclick="viewCrfByVersion('${dedc.edc.id}', '${studySubject.id}', $('#${crfVersionInputId}').val(), '${eventId}', 1);"
                   onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                   onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');">
                       <img name="bt_View1" align="left" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_default" bundle="${resword}"/>" title="<fmt:message key="view_default" bundle="${resword}"/>" align="left" hspace="4"></a>
            <a href="javascript:openDocWindow('PrintCRF?id=<c:out value="${dedc.edc.defaultVersionId}"/>')"
               onMouseDown="javascript:setImage('bt_Print1','images/bt_Print_d.gif');"
               onMouseUp="javascript:setImage('bt_Print1','images/bt_Print.gif');"><img
              name="bt_Print1" align="left" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print_default" bundle="${resword}"/>" title="<fmt:message key="print_default" bundle="${resword}"/>" align="left" hspace="4"></a>
        
        <!-- added clinovo 12/2012 #121 -->
        	<c:if test="${ 
        (study.status.available || study.status.pending)  && 
        (userBean.sysAdmin || (userRole.studyDirector || userRole.studyAdministrator))
        && !(studyEvent.subjectEventStatus.locked || studyEvent.subjectEventStatus.skipped)
        && (dedc.eventCRF.id>0 and !dedc.eventCRF.notStarted)}">
		<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
		<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
    	<a href="pages/managestudy/chooseCRFVersion?crfId=<c:out value="${dedc.eventCRF.crf.id}" />&crfName=<c:out value="${dedc.eventCRF.crf.name}" />&crfversionId=<c:out value="${dedc.eventCRF.crfVersion.id}" />&crfVersionName=<c:out value="${dedc.eventCRF.crfVersion.name}" />&studySubjectLabel=<c:out value="${studySubject.label}"/>&studySubjectId=<c:out value="${studySubject.id}"/>&eventCRFId=<c:out value="${dedc.eventCRF.id}"/>&eventDefinitionCRFId=<c:out value="${dedc.edc.id}" />"
   			onMouseDown="javascript:setImage('bt_Reassign','images/bt_Reassign_d.gif');"
   			onMouseUp="javascript:setImage('bt_Reassign','images/bt_Reassign.gif');"><img
      		name="Reassign" src="images/bt_Reassign.gif" border="0" alt="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" title="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" align="left" hspace="4"></a>
            </c:if>
</td>

</tr>
</form>

<c:set var="rowCount" value="${rowCount + 1}" />
</c:otherwise>
</c:choose>

<!-- end of for each for dedc, uncompleted event crfs, started CRFs below -->
</c:when>
<c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.submit.DisplayEventCRFBean'}">
<c:set var="dec" value="${dedc}"/>

<tr>
<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" /> <c:if test="${dec.eventDefinitionCRF.requiredCRF}"><span style="color: orange">*</span></c:if> <c:if test="${dec.eventDefinitionCRF.sourceDataVerification.code eq 1 or dec.eventDefinitionCRF.sourceDataVerification.code eq 2}"><img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/></c:if></td>
<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>
<td class="table_cell" bgcolor="#F5F5F5" align="center">

    <c:choose>
        <c:when test="${dec.eventCRF.notStarted}">
            <img src="images/icon_NotStarted.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
        </c:when>
        <c:when test="${dec.stage.initialDE}">
            <img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
        </c:when>
        <c:when test="${dec.stage.initialDE_Complete}">
            <img src="images/icon_InitialDEcomplete.gif" alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
        </c:when>
        <c:when test="${dec.stage.doubleDE}">
            <img src="images/icon_DDE.gif" alt="<fmt:message key="double_data_entry" bundle="${resword}"/>" title="<fmt:message key="double_data_entry" bundle="${resword}"/>">
        </c:when>
        <c:when test="${dec.stage.doubleDE_Complete}">
            <c:choose>
                <c:when test="${studyEvent.subjectEventStatus.signed}">
                    <img src="images/icon_Signed.gif" alt="<fmt:message key="subjectEventSigned" bundle="${resword}"/>" title="<fmt:message key="subjectEventSigned" bundle="${resword}"/>">
                </c:when>
                <c:when test="${dec.eventCRF.sdvStatus}">
                    <img src="images/icon_DoubleCheck.gif" alt="<fmt:message key="sourceDataVerified" bundle="${resword}"/>" title="<fmt:message key="sourceDataVerified" bundle="${resword}"/>">
                </c:when>
                <c:otherwise>
                    <img src="images/icon_DEcomplete.gif" alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
                </c:otherwise>
            </c:choose>
        </c:when>

        <c:when test="${dec.stage.admin_Editing}">
            <img src="images/icon_AdminEdit.gif" alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
        </c:when>

        <c:when test="${dec.stage.locked}">
            <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
        </c:when>

        <c:otherwise>
            <img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="removed" bundle="${resword}"/>">
        </c:otherwise>
    </c:choose>
</td>
<td class="table_cell"><c:out value="${dec.eventCRF.owner.name}" />&nbsp;</td>
<td class="table_cell ddeColumn">
    <c:choose>
        <c:when test="${!dec.eventDefinitionCRF.doubleEntry}">
            n/a
        </c:when>
        <c:otherwise>
            <c:set var="showDDEColumn" value="true"/>
            <c:choose>
                <c:when test="${dec.stage.doubleDE || dec.stage.doubleDE_Complete || dec.stage.admin_Editing || dec.stage.locked}">
                    <c:out value="${dec.eventCRF.updater.name}" />&nbsp;
                </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>

</td>
<td class="table_cell" style="width:215px;text-align:center;">
    <c:set var="actionQuery" value="" />
    <c:if test="${study.status.available}">
        <c:if test="${dec.continueInitialDataEntryPermitted}">
            <c:set var="actionQuery" value="InitialDataEntry?eventCRFId=${dec.eventCRF.id}" />
        </c:if>

        <c:if test="${dec.startDoubleDataEntryPermitted}">
            <c:set var="actionQuery" value="DoubleDataEntry?eventCRFId=${dec.eventCRF.id}" />
        </c:if>

        <c:if test="${dec.continueDoubleDataEntryPermitted}">
            <c:set var="actionQuery" value="DoubleDataEntry?eventCRFId=${dec.eventCRF.id}" />
        </c:if>

        <c:if test="${dec.performAdministrativeEditingPermitted}">
            <c:set var="actionQuery" value="AdministrativeEditing?eventCRFId=${dec.eventCRF.id}" />
        </c:if>
    </c:if>
    <c:choose>
        <c:when test='${actionQuery == "" && dec.stage.name == "invalid" }'>
			<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
            <a href="ViewSectionDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}"/>&eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
               onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
               onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
              ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>" align="left" hspace="4"></a>

            <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
               onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
               onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
              ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>" title="<fmt:message key="print" bundle="${resword}"/>" align="left" hspace="4"></a>

            <c:if test="${currentRole.id ne 4 and currentRole.id ne 5 and (studySubject.status.name != 'removed' && studySubject.status.name != 'auto-removed') && (study.status.available)}">
                <a href="RestoreEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
                   onMouseDown="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
                   onMouseUp="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
                  ><img name="bt_Restore<c:out value="${rowCount}"/>" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="4"></a>
                <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
                   onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                   onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                  ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="4"></a>
            </c:if>

        </c:when>

        <c:when test='${actionQuery == ""}'>
			<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
            <a href="ViewSectionDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}"/>&eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
               onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
               onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
              ><img name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="4"></a>

            <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
               onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
               onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
              ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>" title="<fmt:message key="print" bundle="${resword}"/>" align="left" hspace="4"></a>
            <%-- added above 112007, tbh --%>
        </c:when>
        <c:otherwise>
			<c:choose>
            <c:when test="${studySubject.status.name != 'removed'&& studySubject.status.name != 'auto-removed' && userRole.role.id ne 6}">
                <c:if test="${dec.continueInitialDataEntryPermitted}">
                <a href="#"
                    onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                    onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                    onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                    <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="continue_entering_data" bundle="${resword}"/>" title="<fmt:message key="continue_entering_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                </c:if>
                <c:if test="${dec.startDoubleDataEntryPermitted}">
                 <a href="#"
                    onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                    onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                    onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                    <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="begin_double_data_entry" bundle="${resword}"/>" title="<fmt:message key="begin_double_data_entry" bundle="${resword}"/>" align="left" hspace="4"></a>
                </c:if>
                <c:if test="${dec.continueDoubleDataEntryPermitted}">
                  <a href="#"
                    onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                    onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                    onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                    <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="continue_entering_data" bundle="${resword}"/>" title="<fmt:message key="continue_entering_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                </c:if>
                <c:if test="${dec.performAdministrativeEditingPermitted}">
                 <a href="#"
                    onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                    onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                    onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                    <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>" align="left" hspace="4">
                    </a>
                </c:if>
                <c:if test="${dec.locked || dec.eventCRF.status.locked || dec.stage.locked || currRow.bean.studyEvent.subjectEventStatus.locked}">
				<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
                </c:if>
            </c:when>
			<c:otherwise>
				<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
			</c:otherwise>
			</c:choose>

            <a href="ViewSectionDataEntry?eventCRFId=<c:out value="${dec.eventCRF.id}"/>&eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
               onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
               onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
              ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>" align="left" hspace="4"></a>

            <a href="javascript:openDocWindow('PrintDataEntry?ecId=<c:out value="${dec.eventCRF.id}"/>')"
               onMouseDown="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
               onMouseUp="javascript:setImage('bt_Print<c:out value="${rowCount}"/>','images/bt_Print.gif');"
              ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_Print.gif" border="0" alt="<fmt:message key="print" bundle="${resword}"/>" title="<fmt:message key="print" bundle="${resword}"/>" align="left" hspace="4"></a>
			<c:if test="${(userRole.studyDirector || userBean.sysAdmin) && (study.status.available)}">
                <a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
                   onMouseDown="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                   onMouseUp="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                  ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="4"></a>
            </c:if>

            <c:if test="${userBean.sysAdmin && (study.status.available)}">
                <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
                   onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                   onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                  ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="4"></a>
            </c:if>
            <!--  reasign crf version -->
          	<!-- added clinovo #121 12/2012 --> 	
 			<c:if test="${(userBean.sysAdmin || (userRole.studyDirector || userRole.studyAdministrator)) &&
 				(study.status.available || study.status.pending) 
 				&& !(studyEvent.subjectEventStatus.locked || studyEvent.subjectEventStatus.skipped)}">
   
  			  <a href="pages/managestudy/chooseCRFVersion?crfId=<c:out value="${dec.eventCRF.crf.id}" />&crfName=<c:out value="${dec.eventCRF.crf.name}" />&crfversionId=<c:out value="${dec.eventCRF.crfVersion.id}" />&crfVersionName=<c:out value="${dec.eventCRF.crfVersion.name}" />&studySubjectLabel=<c:out value="${studySubject.label}"/>&studySubjectId=<c:out value="${studySubject.id}"/>&eventCRFId=<c:out value="${dec.eventCRF.id}"/>&eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>"
   				onMouseDown="javascript:setImage('bt_Reassign','images/bt_Reassign_d.gif');"
   				onMouseUp="javascript:setImage('bt_Reassign','images/bt_Reassign.gif');"><img
      			name="Reassign" src="images/bt_Reassign.gif" border="0" alt="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" title="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" align="left" hspace="4"></a>
   			
   			</c:if>

        </c:otherwise>
    </c:choose>
</td>
</tr>
<c:set var="rowCount" value="${rowCount + 1}" />
</c:when>
</c:choose>
</c:forEach>

</c:otherwise>
</c:choose>
</table>

<!-- End Table Contents -->

</div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<form method="POST" action="ViewStudySubject">
    <input type="hidden" name="id" value="<c:out value="${studySubject.id}"/>" />
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
    <input type="button" name="BTN_BackToSV" id="GoToSV" value="<fmt:message key="view_subject_record2" bundle="${resword}"/>" class="button_long" onClick="window.location.href = 'ViewStudySubject?id=${studySubject.id}';"/>
    <input type="button" name="BTN_Schedule" id="ScheduleEvent" value="<fmt:message key="schedule_event" bundle="${resword}"/>" class="button_long" onClick="javascript: window.location.href=('CreateNewStudyEvent?studySubjectId=<c:out value="${studySubject.id}"/>&studyEventDefinition=<c:out value="${studyEvent.studyEventDefinition.id}"/>');">
</form>
<br>

<c:import url="instructionsEnterData.jsp">
    <c:param name="currStep" value="eventOverview" />
</c:import>

<c:if test="${showDDEColumn ne \"true\"}">
    <script>
        jQuery(".ddeColumn").css("display", "none");
        jQuery(".ddeColumnHeader").css("display", "none");
    </script>
</c:if>

<jsp:include page="../include/footer.jsp"/>