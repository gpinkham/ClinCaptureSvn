<%@ page import="org.akaza.openclinica.bean.core.Role" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean'/>
<jsp:useBean scope="session" id="currentRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />
<jsp:useBean scope='request' id='eventId' class='java.lang.String'/>
<c:set var="eventId" value="${eventId}"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="showDDEColumn" value="false"/>

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
        if (ecId==0) { formName.submit(); return;}
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
                alert(data);
            }
        });
    }
</script>

<c:set var="hideCrfBlankCell" value="true"/>
<c:set var="insertBlankCell" value="false"/>

<!--#start-->
<!-- *JSP* submit/crfListForStudyEvent.jsp -->
<table class="crfListTable" border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td class="table_header_row popupHeaderBlock" class="table_header_row" colspan="4" align="left">&nbsp;</td>
        <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
        <td class="table_header_row">
            <div style="float: left;"><fmt:message key="actions" bundle="${resword}"/></div>
            <c:if test="${!studyEvent.studyEventDefinition.repeating}">
                <div style="float: right; margin-top: -2px; padding-right: 4px;">
                    <a href="#" onclick="closePopup(); hideAllTooltips();">X</a>
                </div>
            </c:if>
        </td>
    </tr>
    <tr>
        <td class="table_header_row popupHeaderBlock" class="table_header_row" colspan="4" align="left">
            <fmt:message key="subject" bundle="${resword}"/>: <b>${studySubject.label}</b>
        </td>
        <td class="table_header_row">&nbsp;</td>

        <!-- Subject Controls section -->
        <td class="table_header_row" style="white-space: nowrap;">
            <c:choose>
                <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and currentRole.id ne 6 and not study.status.frozen and not study.status.locked}">
                    <a href="UpdateStudySubject?id=${studySubject.id}&action=show"><img src="images/bt_Edit.gif" border="0" align="left" alt="<fmt:message key="edit_study_subject" bundle="${resword}"/>" title="<fmt:message key="edit_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <a href="ViewStudySubject?id=${studySubject.id}"><img src="images/bt_View.gif" border="0" align="left" alt="<fmt:message key="view_subject_record" bundle="${resword}"/>" title="<fmt:message key="view_subject_record" bundle="${resword}"/>" hspace="4"/></a>

            <c:choose>
                <c:when test="${currentRole.id ne 4 and currentRole.id ne 5 and currentRole.id ne 6 and not study.status.frozen and not study.status.locked}">
                    <c:set var="insertBlankCell" value="true"/>
                    <c:choose>
                        <c:when test="${not studySubject.status.deleted}">
                            <a href="RemoveStudySubject?action=confirm&id=${studySubject.id}&subjectId=${studySubject.id}&studyId=${studyId}"><img src="images/bt_Remove.gif" border="0" align="left" alt="<fmt:message key="remove_study_subject" bundle="${resword}"/>" title="<fmt:message key="remove_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                        </c:when>
                        <c:otherwise>
                            <a href="RestoreStudySubject?action=confirm&id=${studySubject.id}&subjectId=${studySubject.id}&studyId=${studyId}"><img src="images/bt_Restore.gif" border="0" align="left" alt="<fmt:message key="restore_study_subject" bundle="${resword}"/>" title="<fmt:message key="restore_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>

            <c:choose>
                <c:when test="${showSubjectSDVButton and not studySubject.status.deleted and not study.status.frozen and not study.status.locked and (currentRole.id eq 6 or currentRole.id eq 2 or currentRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <a class="sdvLink" href="pages/viewSubjectAggregate?sbb=true&studyId=${studyId}&studySubjectId=&theStudySubjectId=0&redirection=viewSubjectAggregate&maxRows=15&showMoreLink=true&s_sdv_tr_=true&s_sdv_p_=1&s_sdv_mr_=15&s_sdv_f_studySubjectId=${studySubject.label}" style="color: #666;"><img src="images/icon_DoubleCheck_Action.gif" border="0" align="left" alt="<fmt:message key="perform_sdv" bundle="${resword}"/>" title="<fmt:message key="perform_sdv" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${showSubjectSignButton and (studyEvent.subjectEventStatus.id eq 4 or studyEvent.subjectEventStatus.id eq 9) and (currentRole.id eq 4 or currentRole.id eq 1) and not study.status.frozen and not study.status.locked} ">
                    <a href="SignStudySubject?id=${studySubject.id}"><img src="images/icon_SignedBlue.gif" border="0" align="left" alt="<fmt:message key="sign_subject" bundle="${resword}"/>" title="<fmt:message key="sign_subject" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and (currentRole.id eq 2 or currentRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <a href="ReassignStudySubject?id=${studySubject.id}"><img src="images/bt_Reassign.gif" border="0" align="left" alt="<fmt:message key="reassign" bundle="${resword}"/>" title="<fmt:message key="reassign" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:if test="${subjectHasNDs eq true}">
                <a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed">
                    <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            </c:if>
        </td>
    </tr>

    <tr>
        <td class="table_header_row popupHeaderBlock" class="table_header_row" colspan="4" align="left">
            <fmt:message key="event" bundle="${resword}"/>: <b>${studyEvent.studyEventDefinition.name}</b>
        </td>
        <td class="table_header_row" align="center" style="vertical-align: middle;">
        <c:set var="removedTitle" value="Removed"/>
        <c:choose>
            <c:when test="${studyEvent.subjectEventStatus.name eq 'deleted'}">
                <img src="${sesIconUrl}" alt="${studyEvent.subjectEventStatus.name}" title="<c:out value="${removedTitle}"/>" /></td>
            </c:when>
            <c:otherwise>
                <img src="${sesIconUrl}" alt="${studyEvent.subjectEventStatus.name}" title="${studyEvent.subjectEventStatus.name}"/></td>
            </c:otherwise>
        </c:choose>
        <td class="table_header_row" style="white-space: nowrap;">

            <c:choose>
                <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and currentRole.id ne 6 and not study.status.frozen and not study.status.locked}">
                    <a href="UpdateStudyEvent?event_id=${studyEvent.id}&ss_id=${studySubject.id}"><img src="images/bt_Edit.gif" border="0" align="left" alt="<fmt:message key="edit_study_event" bundle="${resword}"/>" title="<fmt:message key="edit_study_event" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <a href="EnterDataForStudyEvent?eventId=${studyEvent.id}"><img src="images/bt_View.gif" border="0" align="left" alt="<fmt:message key="view_study_event" bundle="${resword}"/>" title="<fmt:message key="view_study_event" bundle="${resword}"/>" hspace="4"/></a>

            <c:choose>
                <c:when test="${not studySubject.status.deleted and currentRole.id ne 4 and currentRole.id ne 5 and currentRole.id ne 6 and not study.status.frozen and not study.status.locked}">
                    <c:choose>
                        <c:when test="${studyEvent.subjectEventStatus.id ne 10}">
                            <a href="RemoveStudyEvent?action=confirm&id=${studyEvent.id}&studySubId=${studySubject.id}"><img src="images/bt_Remove.gif" border="0" align="left" alt="<fmt:message key="remove_study_event" bundle="${resword}"/>" title="<fmt:message key="remove_study_event" bundle="${resword}"/>" hspace="4"/></a>
                        </c:when>
                        <c:otherwise>
                            <a href="RestoreStudyEvent?action=confirm&id=${studyEvent.id}&studySubId=${studySubject.id}"><img src="images/bt_Restore.gif" border="0" align="left" alt="<fmt:message key="restore_study_event" bundle="${resword}"/>" title="<fmt:message key="restore_study_event" bundle="${resword}"/>" hspace="4"/></a>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>

            <c:choose>
                <c:when test="${showSDVButton and not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and (currentRole.id eq 6 or currentRole.id eq 2 or currentRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <a class="sdvLink" href="pages/viewAllSubjectSDVtmp?sbb=true&studyId=${studyId}&imagePathPrefix=..%2F&crfId=0&redirection=viewAllSubjectSDVtmp&maxRows=15&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15&sdv_f_studySubjectId=${studySubject.label}&sdv_f_eventName=${studyEvent.studyEventDefinition.name}" style="color: #666;"><img src="images/icon_DoubleCheck_Action.gif" border="0" align="left" alt="<fmt:message key="perform_sdv" bundle="${resword}"/>" title="<fmt:message key="perform_sdv" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${showSignButton and (studyEvent.subjectEventStatus.id eq 4 or studyEvent.subjectEventStatus.id eq 9) and (currentRole.id eq 4 or currentRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <a href="UpdateStudyEvent?action=submit&event_id=${studyEvent.id}&ss_id=${studySubject.id}&changeDate=&startDate=20-Jan-2012&startHour=-1&startMinute=-1&startHalf=&endDate=&endHour=-1&endMinute=-1&endHalf=&statusId=8&Submit=Submit+Changes"><img src="images/icon_SignedBlue.gif" border="0" align="left" alt="<fmt:message key="sign" bundle="${resword}"/>" title="<fmt:message key="sign" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>

            <!-- View DNs icons -->
            <c:if test="${eventHasNDs eq true}">
                    <a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_eventName=${studyEventName}&listNotes_f_studySubject.label=${studySubject.label}">
                        <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            </c:if>
        </td>
    </tr>
    <c:choose>
    <c:when test="${empty uncompletedEventDefinitionCRFs && empty displayEventCRFs}">
        <tr>
            <td class="table_cell_left"><fmt:message key="there_are_no_CRF" bundle="${resword}"/></td>
        </tr>
    </c:when>

    <c:otherwise>
    <tr>
        <td class="table_header_row_left" style="width: auto;"><fmt:message key="CRF_name" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: 40px;"><fmt:message key="version" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: 100px;"><fmt:message key="initial_data_entry" bundle="${resword}"/></td>
        <td class="table_header_row_left ddeColumnHeader" style="width: 100px;"><fmt:message key="validation" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: 50px;">&nbsp;<%--fmt:message key="status" bundle="${resword}"/--%></td>
        <td class="table_header_row_left crfListTableActions" style="width: 60px;">&nbsp;<%--fmt:message key="actions" bundle="${resword}"/--%></td>
    </tr>
    <c:set var="rowCount" value="${0}" />

    <c:forEach var="dedc" items="${fullCrfList}">
        <c:choose>
            <c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean'}">
                <c:choose>
                    <c:when test="${dedc.status.name=='locked'}">
                    &nbsp;
                    </c:when>
                    <c:otherwise>
                        <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${studyEvent.id}&subjectId=${studySubject.subjectId}&eventCRFId=${dedc.eventCRF.id}&exitTo=EnterDataForStudyEvent?eventId=${eventId}" />
                            <tr valign="top">
                                <td class="table_cell_left"><c:out value="${dedc.edc.crf.name}" />
                                    <c:if test="${dedc.edc.requiredCRF}"><span style="color: orange">*</span>
                                    </c:if> 
                                    <c:if test="${dedc.edc.sourceDataVerification.code eq 1 or dedc.edc.sourceDataVerification.code eq 2}">
                                        <img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/>
                                    </c:if>
                                </td>
                                <td class="table_cell_left">
                                    <form name="startForm<c:out value="${dedc.edc.crf.id}"/>" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
                                        <c:choose>
                                            <c:when test="${dedc.eventCRF.id > 0}">
                                                <!-- found an event crf id -->
                                                <input type="hidden" name="crfVersionId" value="<c:out value="${dedc.eventCRF.CRFVersionId}"/>">
                                            </c:when>
                                            <c:otherwise>
                                                <!-- did not find an event crf id -->
                                                <input type="hidden" name="crfVersionId" value="<c:out value="${dedc.edc.defaultVersionId}"/>">
                                            </c:otherwise>
                                        </c:choose>
                                            <c:set var="versionCount" value="0"/>
                                        <c:forEach var="version" items="${dedc.edc.versions}">
                                            <c:set var="versionCount" value="${versionCount+1}"/>
                                        </c:forEach>
                                        <c:choose>
                                            <c:when test="${versionCount<=1}">

                                                <c:forEach var="version" items="${dedc.edc.versions}">
                                                    <c:out value="${version.name}"/>
                                                </c:forEach>

                                            </c:when>
                                            <c:when test="${dedc.eventCRF.id == 0}">

                                                <select name="versionId<c:out value="${dedc.edc.crf.id}"/>" onchange="javascript:changeQuery<c:out value="${dedc.edc.crf.id}"/>();">

                                                    <c:forEach var="version" items="${dedc.edc.versions}">

                                                        <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />

                                                        <c:choose>
                                                            <c:when test="${dedc.edc.defaultVersionId==version.id}">
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

                                <td class="table_cell_left ddeColumn">
                                    <c:if test="${dedc.eventCRF != null && !dedc.eventCRF.notStarted && dedc.eventCRF.owner != null}">
                                        ${dedc.eventCRF.owner.name}
                                    </c:if>
                                    &nbsp;
                                </td>

                                <td class="table_cell_left">
                                    <c:if test="${!dedc.edc.doubleEntry}">
                                        n/a
                                    </c:if>
                                    &nbsp;
                                </td>

                                <c:choose>

                                    <c:when test="${studyEvent.subjectEventStatus.name=='locked'}">
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
                                                <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;">
                                                    <img src="images/icon_NotStarted.gif" alt="<fmt:message key="not_started" bundle="${resword}"/>" title="<fmt:message key="not_started" bundle="${resword}"/>">
                                                </td>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>

                                    <c:otherwise>
                                        <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;"><img src="images/icon_Invalid.gif" alt="<fmt:message key="removed" bundle="${resword}"/>" title="<fmt:message key="removed" bundle="${resword}"/>"></td>
                                    </c:otherwise>

                                </c:choose>

                                <td class="table_cell_left" style="vertical-align: middle;">
                                    <c:choose>
                                        <c:when test="${studyEvent.subjectEventStatus.name=='locked'}">
                                            <%--<c:when test="${dedc.status.name=='locked'}">--%>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:when>
                                        <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and studySubject.status.name != 'removed'&& studySubject.status.name != 'auto-removed' && study.status.available && !studyEvent.status.deleted && userRole.role.id ne 6}">
                                                <c:set var="hideCrfBlankCell" value="false"/>
                                                <a href="#" onclick="checkCRFLockedInitial('<c:out value="${dedc.eventCRF.id}"/>',document.startForm<c:out value="${dedc.edc.crf.id}"/>);"
                                                   onMouseDown="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData_d.gif');"
                                                   onMouseUp="javascript:setImage('bt_EnterData<c:out value="${rowCount}"/>','images/bt_EnterData.gif');">
                                                   <img name="bt_EnterData<c:out value="${rowCount}"/>" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="enter_data" bundle="${resword}"/>" title="<fmt:message key="enter_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dedc.edc.id}"/>&crfVersionId=<c:out value="${dedc.edc.defaultVersionId}"/>&studySubjectId=<c:out value="${studySubject.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
                                           onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                                           onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img
                                           name="bt_View1" align="left" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_default" bundle="${resword}"/>" title="<fmt:message key="view_default" bundle="${resword}"/>" hspace="4"></a>


                                    </form>
                                    <c:if test="${crfNDsMap[dedc.edc.crf.id] eq true}">
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_crfName=${dedc.edc.crf.name}">
                                                <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
                                    </c:if>
                                </td>
                            </tr>

                        <c:set var="rowCount" value="${rowCount + 1}" />
                    </c:otherwise>
                </c:choose>

                <!-- end of for each for dedc, uncompleted event crfs, started CRFs below -->
                </c:when>
                    <c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.submit.DisplayEventCRFBean'}">
                        <c:set var="dec" value="${dedc}"/>

                        <tr>
                            <td class="table_cell_left"><c:out value="${dec.eventCRF.crf.name}" /> <c:if test="${dec.eventDefinitionCRF.requiredCRF}"><span style="color: orange">*</span></c:if> <c:if test="${dec.eventDefinitionCRF.sourceDataVerification.code eq 1 or dec.eventDefinitionCRF.sourceDataVerification.code eq 2}"><img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/></c:if></td>
                            <td class="table_cell_left"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>

                            <td class="table_cell_left"><c:out value="${dec.eventCRF.owner.name}" />&nbsp;</td>
                            <td class="table_cell_left ddeColumn">
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

                            <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;">

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
                                        <img src="images/icon_Invalid.gif" alt="<fmt:message key="removed" bundle="${resword}"/>" title="<fmt:message key="removed" bundle="${resword}"/>">
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td class="table_cell_left" style="vertical-align: middle;">
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

                                <c:set var="crfSpacersCount" value="0"/>
                                <c:choose>
                                    <c:when test='${actionQuery == "" && dec.stage.name =="invalid" }'>
                                        <c:set var="crfSpacersCount" value="5"/>
                                        <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        <a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
                                           onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
                                           onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
                                          ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>" align="left" hspace="4"></a>

                                        <c:if test="${currentRole.id ne 4 and currentRole.id ne 5 and (studySubject.status.name != 'removed' && studySubject.status.name != 'auto-removed') && (study.status.available)}">
                                            <c:set var="crfSpacersCount" value="4"/>
                                            <a href="RestoreEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
                                               onMouseDown="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
                                               onMouseUp="javascript:setImage('bt_Restore<c:out value="${rowCount}"/>','images/bt_Restore.gif');"
                                              ><img name="bt_Restore<c:out value="${rowCount}"/>" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>
                                    </c:when>

                                    <c:when test='${actionQuery == ""}'>
                                        <c:set var="crfSpacersCount" value="5"/>
                                        <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        <a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
                                           onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                                           onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
                                          ><img name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="4"></a>

                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="enterDataWasInserted" value="false"/>
                                        <c:if test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and studySubject.status.name != 'removed'&& studySubject.status.name != 'auto-removed' && userRole.role.id ne 6}">
                                            <c:if test="${dec.continueInitialDataEntryPermitted}">
                                                <c:set var="hideCrfBlankCell" value="false"/>
                                                <c:set var="enterDataWasInserted" value="true"/>
                                                <a href="#"
                                                onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                                                onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                                                onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                                                <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="continue_entering_data" bundle="${resword}"/>" title="<fmt:message key="continue_entering_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                                            </c:if>
                                            <c:if test="${dec.startDoubleDataEntryPermitted}">
                                                <c:set var="hideCrfBlankCell" value="false"/>
                                                <c:set var="enterDataWasInserted" value="true"/>
                                                <a href="#"
                                                onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                                                onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                                                onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                                                <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="begin_double_data_entry" bundle="${resword}"/>" title="<fmt:message key="begin_double_data_entry" bundle="${resword}"/>" align="left" hspace="4"></a>
                                            </c:if>
                                            <c:if test="${dec.continueDoubleDataEntryPermitted}">
                                                <c:set var="hideCrfBlankCell" value="false"/>
                                                <c:set var="enterDataWasInserted" value="true"/>
                                                <a href="#"
                                                onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                                                onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                                                onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                                                <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="continue_entering_data" bundle="${resword}"/>" title="<fmt:message key="continue_entering_data" bundle="${resword}"/>" align="left" hspace="4"></a>
                                            </c:if>
                                            <c:if test="${dec.performAdministrativeEditingPermitted}">
                                                <c:set var="hideCrfBlankCell" value="false"/>
                                                <c:set var="enterDataWasInserted" value="true"/>
                                                <a href="#"
                                                onMouseDown="javascript:setImage('bt_EnterData1','images/bt_EnterData_d.gif');"
                                                onMouseUp="javascript:setImage('bt_EnterData1','images/bt_EnterData.gif');"
                                                onclick="checkCRFLocked('<c:out value="${dec.eventCRF.id}"/>', '<c:out value="${actionQuery}"/>');">
                                                <img name="bt_EnterData1" src="images/bt_EnterData.gif" border="0" alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>" align="left" hspace="4">
                                                </a>
                                            </c:if>
                                        </c:if>

                                        <c:if test="${enterDataWasInserted eq 'false'}">
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:if>

                                        <c:set var="crfSpacersCount" value="5"/>
                                        <a href="ViewSectionDataEntry?eventDefinitionCRFId=<c:out value="${dec.eventDefinitionCRF.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>&tabId=1&eventId=<c:out value="${eventId}"/>"
                                           onMouseDown="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
                                           onMouseUp="javascript:setImage('bt_View<c:out value="${rowCount}"/>','images/bt_View.gif');"
                                          ><img name="bt_Print<c:out value="${rowCount}"/>" src="images/bt_View.gif" border="0" alt="<fmt:message key="view_data" bundle="${resword}"/>" title="<fmt:message key="view_data" bundle="${resword}"/>" align="left" hspace="4"></a>

                                        <c:if test="${(userRole.studyDirector || userBean.sysAdmin) && (study.status.available)}">
                                            <c:set var="crfSpacersCount" value="4"/>
                                            <a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
                                               onMouseDown="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                                               onMouseUp="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                                              ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>

                                        <c:if test="${userBean.sysAdmin && (study.status.available)}">
                                            <c:set var="crfSpacersCount" value="3"/>
                                            <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
                                               onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                                               onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                                              ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                                <c:if test="${crfNDsMap[dec.eventCRF.crf.id] eq true}">
                                        <c:forEach begin="1" end="${crfSpacersCount}">
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:forEach>
                                        <a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_crfName=${dec.eventCRF.crf.name}&listNotes_f_eventName=${dec.eventCRF.name}">
                                            <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
                                </c:if>
                            </td>
                        </tr>
                        <c:set var="rowCount" value="${rowCount + 1}" />
                    </c:when>
                </c:choose>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</table>
<!--#end-->

<c:if test="${showDDEColumn ne \"true\"}">
    <script>
        jQuery(".popupHeaderBlock").attr("colSpan", "3");
        jQuery(".ddeColumn").remove();//.css("display", "none");
        jQuery(".ddeColumnHeader").remove();//.css("display", "none");
    </script>
</c:if>

<input type="hidden" class="hideCrfBlankCell"  value="${hideCrfBlankCell}"/>
<jsp:include page="../include/changeTheme.jsp"/>