<%@ page import="org.akaza.openclinica.bean.core.Role" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean scope="session" id="userRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />
<jsp:useBean scope='request' id='eventId' class='java.lang.String'/>
<c:set var="eventId" value="${eventId}"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="showDDEColumn" value="false"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<script type="text/javascript" language="javascript">
    function checkCRFLocked(ecId, url){
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                window.location = url;
            }else{
            	alertDialog({ message:data, height: 150, width: 500 });
            }
        });
    }

    function checkCRFLockedInitial(ecId, formName){
        if (ecId==0) { formName.submit(); return;}
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
            	alertDialog({ message:data, height: 150, width: 500 });
            }
        });
    }
</script>

<c:set var="hideCol1" value="true"/>
<c:set var="hideCol2" value="false"/>
<c:set var="hideCol3" value="true"/>
<c:set var="hideCol4" value="true"/>
<c:set var="hideCol5" value="true"/>
<c:set var="hideCol6" value="true"/>
<c:set var="hideCol7" value="true"/>
<c:set var="hideCol8" value="true"/>

<c:set var="insertBlankCell" value="false"/>

<!--#start-->
<!-- *JSP* submit/crfListForStudyEvent.jsp -->
<table class="crfListTable" border="0" cellpadding="0" cellspacing="0">
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
                <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and userRole.id ne 6 and userRole.role.id ne 9 and not study.status.frozen and not study.status.locked}">
                    <c:set var="hideCol1" value="false"/>
                    <a href="UpdateStudySubject?id=${studySubject.id}&action=show"><img src="images/bt_Edit.gif" border="0" align="left" alt="<fmt:message key="edit_study_subject" bundle="${resword}"/>" title="<fmt:message key="edit_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <a href="ViewStudySubject?id=${studySubject.id}"><img src="images/bt_View.gif" border="0" align="left" alt="<fmt:message key="view_subject_record" bundle="${resword}"/>" title="<fmt:message key="view_subject_record" bundle="${resword}"/>" hspace="4"/></a>

            <c:choose>
                <c:when test="${userRole.id ne 4 and userRole.id ne 5 and userRole.id ne 6 and userRole.role.id ne 9 and not study.status.frozen and not study.status.locked and not studySubject.status.signed}">
                    <c:set var="insertBlankCell" value="true"/>
                    <c:choose>
                        <c:when test="${not studySubject.status.deleted}">
                            <c:set var="hideCol3" value="false"/>
                            <a href="RemoveStudySubject?action=confirm&id=${studySubject.id}&subjectId=${studySubject.subjectId}&studyId=${studyId}"><img src="images/bt_Remove.gif" border="0" align="left" alt="<fmt:message key="remove_study_subject" bundle="${resword}"/>" title="<fmt:message key="remove_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                        </c:when>
                        <c:otherwise>
                            <c:set var="hideCol3" value="false"/>
                            <a href="RestoreStudySubject?action=confirm&id=${studySubject.id}&subjectId=${studySubject.subjectId}&studyId=${studyId}"><img src="images/bt_Restore.gif" border="0" align="left" alt="<fmt:message key="restore_study_subject" bundle="${resword}"/>" title="<fmt:message key="restore_study_subject" bundle="${resword}"/>" hspace="4"/></a>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>

            <c:choose>
                <c:when test="${showSubjectSDVButton and not studySubject.status.deleted and not study.status.frozen and not study.status.locked and (userRole.id eq 9 or userRole.role.id eq 6 or userRole.id eq 2 or userRole.id eq 1) and not study.status.frozen and not study.status.locked and ((currentStudy.studyParameterConfig.allowSdvWithOpenQueries eq 'yes') or (currentStudy.studyParameterConfig.allowSdvWithOpenQueries eq 'no' and subjectFlagColor eq null))}">
                    <c:set var="hideCol5" value="false"/>
                    <a class="sdvLink" href="pages/viewSubjectAggregate?sbb=true&studyId=${studyId}&studySubjectId=&theStudySubjectId=0&redirection=viewSubjectAggregate&maxRows=15&showMoreLink=true&s_sdv_tr_=true&s_sdv_p_=1&s_sdv_mr_=15&s_sdv_f_studySubjectId=${studySubject.label}" style="color: #666;"><img src="images/icon_DoubleCheck_Action.gif" border="0" align="left" alt="<fmt:message key="perform_sdv" bundle="${resword}"/>" title="<fmt:message key="perform_sdv" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${showSubjectSignButton and (studyEvent.subjectEventStatus.id eq 1 or studyEvent.subjectEventStatus.id eq 4 or studyEvent.subjectEventStatus.id eq 8 or studyEvent.subjectEventStatus.id eq 9) and userRole.id eq 4 and not study.status.locked and not study.status.pending}">
                    <c:set var="hideCol6" value="false"/>
                    <a href="SignStudySubject?id=${studySubject.id}"><img src="images/icon_SignedBlue.gif" border="0" align="left" alt="<fmt:message key="sign_subject" bundle="${resword}"/>" title="<fmt:message key="sign_subject" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and (userRole.id eq 2 or userRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <c:set var="hideCol7" value="false"/>
                    <a href="ReassignStudySubject?id=${studySubject.id}"><img src="images/bt_Reassign.gif" border="0" align="left" alt="<fmt:message key="reassign" bundle="${resword}"/>" title="<fmt:message key="reassign" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>
			<c:choose>
             	<c:when test="${subjectFlagColor eq 'yellow'}">
                    <c:set var="hideCol8" value="false"/>
               		<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed">
                 		   <img src="images/icon_flagYellow.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            	</c:when>
            	<c:when test="${subjectFlagColor eq 'red'}">
                    <c:set var="hideCol8" value="false"/>
               		<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=New">
                 		   <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            	</c:when>
            </c:choose>
        </td>
    </tr>

    <tr>
        <td class="table_header_row popupHeaderBlock" class="table_header_row" colspan="4" align="left">
            <fmt:message key="event" bundle="${resword}"/>: <b>${studyEvent.studyEventDefinition.name}</b>
        </td>
        <td class="table_header_row" align="center" style="vertical-align: middle;">
        <c:set var="removedTitle" value="Removed"/>
        <c:choose>
            <c:when test="${studyEvent.subjectEventStatus.id eq 10}">
                <img src="${sesIconUrl}" alt="${studyEvent.subjectEventStatus.name}" title="<c:out value="${removedTitle}"/>" /></td>
            </c:when>
            <c:otherwise>
                <img src="${sesIconUrl}" alt="${studyEvent.subjectEventStatus.name}" title="${studyEvent.subjectEventStatus.name}"/></td>
            </c:otherwise>
        </c:choose>
        <td class="table_header_row" style="white-space: nowrap;">

            <c:choose>
                <c:when test="${not(studyEvent.subjectEventStatus.id eq 7 and (userRole.id eq 4 or userRole.id eq 5 or userRole.id eq 6 or userRole.id eq 7 or userRole.role.id eq 9)) and not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and userRole.id ne 6 and userRole.role.id ne 9 and not study.status.frozen and not study.status.locked}">
                    <c:set var="hideCol1" value="false"/>
                    <a href="UpdateStudyEvent?event_id=${studyEvent.id}&ss_id=${studySubject.id}"><img src="images/bt_Edit.gif" border="0" align="left" alt="<fmt:message key="edit_study_event" bundle="${resword}"/>" title="<fmt:message key="edit_study_event" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <a href="EnterDataForStudyEvent?eventId=${studyEvent.id}"><img src="images/bt_View.gif" border="0" align="left" alt="<fmt:message key="view_study_event" bundle="${resword}"/>" title="<fmt:message key="view_study_event" bundle="${resword}"/>" hspace="4"/></a>

            <c:choose>
                <c:when test="${not studySubject.status.deleted and userRole.id ne 4 and userRole.id ne 5 and userRole.id ne 6 and userRole.role.id ne 9 and not study.status.frozen and not study.status.locked}">
                    <c:choose>
                        <c:when test="${studyEvent.subjectEventStatus.id ne 10}">
                            <c:set var="hideCol3" value="false"/>
                            <a href="RemoveStudyEvent?action=confirm&id=${studyEvent.id}&studySubId=${studySubject.id}"><img src="images/bt_Remove.gif" border="0" align="left" alt="<fmt:message key="remove_study_event" bundle="${resword}"/>" title="<fmt:message key="remove_study_event" bundle="${resword}"/>" hspace="4"/></a>
                        </c:when>
                        <c:otherwise>
                            <c:set var="hideCol3" value="false"/>
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
                <c:when test="${showSDVButton and not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and (userRole.id eq 9 or userRole.role.id eq 6 or userRole.id eq 2 or userRole.id eq 1) and not study.status.frozen and not study.status.locked}">
                    <c:set var="hideCol5" value="false"/>
                    <a class="sdvLink" href="pages/viewAllSubjectSDVtmp?sbb=true&studyId=${studyId}&imagePathPrefix=..%2F&crfId=0&redirection=viewAllSubjectSDVtmp&maxRows=15&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15&sdv_f_studySubjectId=${studySubject.label}&sdv_f_eventName=${studyEvent.studyEventDefinition.name}" style="color: #666;"><img src="images/icon_DoubleCheck_Action.gif" border="0" align="left" alt="<fmt:message key="perform_sdv" bundle="${resword}"/>" title="<fmt:message key="perform_sdv" bundle="${resword}"/>" hspace="4"/></a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${showSignButton and (studyEvent.subjectEventStatus.id eq 4 or studyEvent.subjectEventStatus.id eq 9) and userRole.id eq 4 and not study.status.locked and not study.status.pending}">
                    <c:set var="hideCol6" value="false"/>
                    <c:set var="signUrl" value="UpdateStudyEvent?action=submit&event_id=${studyEvent.id}&ss_id=${studySubject.id}&changeDate=&startDate="/>
	                    <c:if test="${study.studyParameterConfig.startDateTimeRequired != 'not_used'}">
                            <fmt:formatDate value="${studyEvent.dateStarted}" pattern="${dteFormat}" var="dateStart"/>
                            <c:set var="signUrl" value="${signUrl}${dateStart}" />
	                     </c:if>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'yes' && study.studyParameterConfig.startDateTimeRequired != 'not_used'}">
                            <c:set var="signUrl" value="${signUrl}&startHour=" />
                            <fmt:formatDate value="${studyEvent.dateStarted}" pattern="hh" var="startHour"/>
                            <c:set var="signUrl" value="${signUrl}${startHour}"/>
                            <c:set var="signUrl" value="${signUrl}&startMinute="/>
                            <fmt:formatDate value="${studyEvent.dateStarted}" pattern="mm" var="startMinutes"/>
                            <c:set var="signUrl" value="${signUrl}${startMinutes}" />
                            <c:set var="signUrl" value="${signUrl}&startHalf=" />
                            <fmt:formatDate value="${studyEvent.dateStarted}" pattern="a" var="startHalf"/>
                            <c:set var="signUrl" value="${signUrl}${startHalf}" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="signUrl" value="${signUrl}&startHour=-1&startMinute=-1&startHalf="/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="signUrl" value="${signUrl}&endDate=" />
		                <c:if test="${study.studyParameterConfig.endDateTimeRequired != 'not_used'}">
                           <fmt:formatDate value="${studyEvent.dateEnded}" pattern="${dteFormat}" var="dateEnd"/>
                            <c:set var="signUrl" value="${signUrl}${dateEnd}" />
	                     </c:if>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'yes' && study.studyParameterConfig.endDateTimeRequired != 'not_used'}}">
                            <c:set var="signUrl" value="${signUrl}&endHour=" />
                            <fmt:formatDate value="${studyEvent.dateEnded}" pattern="hh" var="endHour"/>
                            <c:set var="signUrl" value="${signUrl}${endHour}"/>
                            <c:set var="signUrl" value="${signUrl}&endMinute=" />
                            <fmt:formatDate value="${studyEvent.dateEnded}" pattern="mm" var="endMinutes"/>
                            <c:set var="signUrl" value="${signUrl}${endMinutes}"/>
                            <c:set var="signUrl" value="${signUrl}&endHalf=" />
                            <fmt:formatDate value="${studyEvent.dateEnded}" pattern="a" var="endHalf"/>
                            <c:set var="signUrl" value="${signUrl}${endHalf}"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="signUrl" value="${signUrl}&endHour=-1&endMinute=-1&endHalf="/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="signUrl" value="${signUrl}&statusId=8&Submit=Submit+Changes"/>
                    <a href="${signUrl}">
                    <img src="images/icon_SignedBlue.gif" border="0" align="left" alt="<fmt:message key="sign" bundle="${resword}"/>" title="<fmt:message key="sign" bundle="${resword}"/>" hspace="4"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>
                </c:otherwise>
            </c:choose>

            <img src="images/bt_Transparent.gif" border="0" align="left" hspace="4"/>

            <!-- View DNs icons -->
            <c:choose>
             	<c:when test="${eventFlagColor eq 'yellow'}">
                    <c:set var="hideCol8" value="false"/>
               		<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_eventName=${studyEventName}&listNotes_f_studySubject.label=${studySubject.label}">
                 		   <img src="images/icon_flagYellow.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            	</c:when>
            	<c:when test="${eventFlagColor eq 'red'}">
                    <c:set var="hideCol8" value="false"/>
               		<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_discrepancyNoteBean.resolutionStatus=New&listNotes_f_eventName=${studyEventName}&listNotes_f_studySubject.label=${studySubject.label}">
                 		   <img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            	</c:when>
            </c:choose>
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
        <td class="table_header_row_left" style="width: auto; min-width: 170px;"><fmt:message key="CRF_name" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: auto;"><fmt:message key="version" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: auto;"><fmt:message key="initial_data_entry" bundle="${resword}"/></td>
        <td class="table_header_row_left ddeColumnHeader" style="width: auto;"><fmt:message key="validation" bundle="${resword}"/></td>
        <td class="table_header_row_left" style="width: auto;">&nbsp;<%--fmt:message key="status" bundle="${resword}"/--%></td>
        <td class="table_header_row_left crfListTableActions" style="width: auto;">&nbsp;<%--fmt:message key="actions" bundle="${resword}"/--%></td>
    </tr>
    <c:set var="rowCount" value="${0}" />

    <c:forEach var="dedc" items="${fullCrfList}">
        <c:choose>
            <c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean'}">
                        <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${studyEvent.id}&subjectId=${studySubject.subjectId}&eventCRFId=${dedc.eventCRF.id}&exitTo=EnterDataForStudyEvent?eventId=${eventId}" />
                            <tr valign="top">
                                <td class="table_cell_left"><c:out value="${dedc.edc.crf.name}" />
                                    <c:if test="${dedc.edc.requiredCRF}"><span style="color: orange">*</span>
                                    </c:if>
                                    <c:if test="${(dedc.edc.sourceDataVerification.code eq 1 or dedc.edc.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}">
                                        <img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/>
                                    </c:if>
                                </td>
                                <td class="table_cell_left">
                                    <form name="startForm${studyEvent.id}${dedc.edc.crf.id}" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
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
                                            <c:set var="crfVersionInputId" value="crfVersionId${dedc.eventCRF.crfVersion.id}"/>
                                          </c:when>
                                          <c:otherwise>
                                            <input type="hidden" id="crfVersionId${defaultVersionId}" name="crfVersionId" value="<c:out value="${defaultVersionId}"/>">
                                            <c:set var="crfVersionInputId" value="crfVersionId${defaultVersionId}"/>
                                          </c:otherwise>
                                        </c:choose>

                                        <c:choose>
                                            <c:when test="${versionCount<=1}">
                                                <c:choose>
                                                  <c:when test="${dedc.status.locked}">
                                                    ${dedc.eventCRF.crfVersion.name}
                                                    <script>$('#${crfVersionInputId}').val('${dedc.eventCRF.crfVersion.id}')</script>
                                                  </c:when>
                                                  <c:otherwise>
                                                    <c:forEach var="version" items="${dedc.edc.versions}">
                                                      <c:out value="${version.name}"/>
                                                    </c:forEach>
                                                  </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:when test="${dedc.eventCRF.notStarted || dedc.eventCRF.id == 0}">
                                                <select name="versionId<c:out value="${dedc.edc.crf.id}"/>" onchange="javascript:changeQuery<c:out value="${dedc.edc.crf.id}"/>();">

                                                    <c:forEach var="version" items="${dedc.edc.versions}">

                                                        <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />

                                                        <c:choose>
                                                            <c:when test="${dedc.edc.defaultVersionId == version.id}">
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
                                                        var qer = document.startForm${studyEvent.id}${dedc.edc.crf.id}.versionId<c:out value="${dedc.edc.crf.id}"/>.value;
                                                        document.startForm${studyEvent.id}${dedc.edc.crf.id}.crfVersionId.value=qer;
                                                    }
                                                </SCRIPT>
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${dedc.eventCRF.crfVersion.name}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>

                                <td class="table_cell_left">
                                    <c:if test="${dedc.eventCRF != null && !dedc.eventCRF.notStarted && dedc.eventCRF.owner != null}">
                                        ${dedc.eventCRF.owner.name}
                                    </c:if>
                                    &nbsp;
                                </td>

                                <td class="table_cell_left ddeColumn">
                                    <c:if test="${!dedc.edc.doubleEntry}">
                                        n/a
                                    </c:if>
                                    &nbsp;
                                </td>

                                <c:choose>
                                    <c:when test="${dedc.status.locked || studyEvent.subjectEventStatus.locked}">
                                        <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;">
                                            <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
                                        </td>
                                    </c:when>

                                    <c:when test="${!studySubject.status.deleted}">
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
                                        <c:when test="${dedc.status.locked || studyEvent.subjectEventStatus.locked}">
                                            <%--<c:when test="${dedc.status.name=='locked'}">--%>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:when>
                                        <c:when test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and !studySubject.status.deleted && (study.status.available) && !studyEvent.status.deleted && userRole.role.id ne 6 and userRole.role.id ne 9}">
                                            <c:set var="hideCol1" value="false"/>
                                            <ui:dataEntryLink object="${dedc}" rowCount="${rowCount}" actionQueryTail="${studyEvent.id}${dedc.edc.crf.id}" onClickFunction="checkCRFLockedInitial"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:otherwise>
                                    </c:choose>

									<ui:viewDataEntryLink object="${dedc}" onClick="viewCrfByVersion('${dedc.edc.id}', '${studySubject.id}', $('#${crfVersionInputId}').val(), '${eventId}', 1);"/>

                                    <c:if test="${not empty crfNDsMap[dedc.edc.crf.id]}">
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                    		<c:choose>
             									<c:when test="${crfNDsMap[dec.eventCRF.crf.id] eq 'yellow'}">
                                                    <c:set var="hideCol8" value="false"/>
               										<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_crfName=${dedc.edc.crf.name}">
               										<img src="images/icon_flagYellow.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            									</c:when>
            									<c:when test="${crfNDsMap[dec.eventCRF.crf.id] eq 'red'}">
                                                    <c:set var="hideCol8" value="false"/>
               										<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&&listNotes_f_discrepancyNoteBean.resolutionStatus=New&listNotes_f_crfName=${dedc.edc.crf.name}">
                 		   							<img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            									</c:when>
            								</c:choose>
                                    </c:if>
                                </td>
                            </tr>

                        <c:set var="rowCount" value="${rowCount + 1}" />

                <!-- end of for each for dedc, uncompleted event crfs, started CRFs below -->
                </c:when>
                    <c:when test="${dedc.class.name eq 'org.akaza.openclinica.bean.submit.DisplayEventCRFBean'}">
                        <c:set var="dec" value="${dedc}"/>

                        <tr>
                            <td class="table_cell_left"><c:out value="${dec.eventCRF.crf.name}" /> <c:if test="${dec.eventDefinitionCRF.requiredCRF}"><span style="color: orange">*</span></c:if> <c:if test="${(dec.eventDefinitionCRF.sourceDataVerification.code eq 1 or dec.eventDefinitionCRF.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}"><img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/></c:if></td>
                            <td class="table_cell_left"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>
                            <td class="table_cell_left"><c:out value="${dec.eventCRF.owner.name}" />&nbsp;</td>
                            <td class="table_cell_left ddeColumn">
                                <c:choose>
                                    <c:when test="${!dec.eventDefinitionCRF.doubleEntry && !dec.eventDefinitionCRF.evaluatedCRF}">
                                        n/a
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="showDDEColumn" value="true"/>
                                        <c:choose>
                                            <c:when test="${dec.stage.doubleDE || dec.stage.doubleDE_Complete || dec.stage.admin_Editing || dec.stage.locked}">
                                                <c:out value="${dec.eventCRF.doubleDataOwner.name}" />&nbsp;
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
                                        <c:set var="ddeTitleCode" value="double_data_entry"/>
                                        <c:if test="${dec.eventDefinitionCRF.evaluatedCRF && !dec.eventDefinitionCRF.doubleEntry}"><c:set var="ddeTitleCode" value="evaluation"/></c:if>
                                        <img src="images/icon_DDE.gif" alt="<fmt:message key="${ddeTitleCode}" bundle="${resword}"/>" title="<fmt:message key="${ddeTitleCode}" bundle="${resword}"/>">
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
                                <div class="<c:if test='${dec.eventDefinitionCRF.hideCrf and study.parentStudyId > 0}'>hidden</c:if>">

                                <c:set var="allowDataEntry" value="${(study.status.available && dec.continueInitialDataEntryPermitted) || (study.status.available && (dec.startDoubleDataEntryPermitted || dec.continueDoubleDataEntryPermitted)) || ((study.status.available || study.status.frozen) && dec.performAdministrativeEditingPermitted)}" />

                                <c:set var="crfSpacersCount" value="0"/>
                                <c:choose>
                                    <c:when test='${!allowDataEntry && dec.stage.name == "invalid" }'>
                                        <c:set var="hideCol2" value="false"/>
                                        <c:set var="crfSpacersCount" value="5"/>
                                        <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
										<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}" name="bt_Print${rowCount}"/>

                                        <c:if test="${userRole.id ne 4 && userRole.id ne 5 and userRole.id ne 6 and userRole.role.id ne 9 and (!studySubject.status.deleted) && (study.status.available)}">
                                            <c:set var="hideCol3" value="false"/>
                                            <c:set var="hideCol4" value="false"/>
                                            <c:set var="crfSpacersCount" value="4"/>
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

                                    <c:when test='${!allowDataEntry}'>
                                        <c:set var="hideCol2" value="false"/>
                                        <c:set var="crfSpacersCount" value="5"/>
                                        <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
										<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="enterDataWasInserted" value="false"/>
                                        <c:if test="${not studySubject.status.deleted and studyEvent.subjectEventStatus.id ne 10 and !studySubject.status.deleted && userRole.role.id ne 6 and userRole.role.id ne 9}">
                                            <c:set var="hideCol1" value="false"/>
                                            <c:set var="enterDataWasInserted" value="true"/>
                                            <ui:dataEntryLink object="${dec}" actionQueryTail="?eventCRFId=${dec.eventCRF.id}"/>
                                        </c:if>

                                        <c:if test="${enterDataWasInserted eq 'false'}">
                                            <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                        </c:if>

                                        <c:set var="hideCol2" value="false"/>
                                        <c:set var="crfSpacersCount" value="5"/>
										<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}" name="bt_Pring${rowCount}"/>
                                        <c:if test="${(userRole.studyDirector || userBean.sysAdmin) && (study.status.available)}">
                                            <c:set var="hideCol3" value="false"/>
                                            <c:set var="crfSpacersCount" value="4"/>
                                            <a href="RemoveEventCRF?action=confirm&id=<c:out value="${dec.eventCRF.id}"/>&studySubId=<c:out value="${studySubject.id}"/>"
                                               onMouseDown="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                                               onMouseUp="javascript:setImage('bt_Remove<c:out value="${rowCount}"/>','images/bt_Remove.gif');"
                                              ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>

                                        <c:if test="${userBean.sysAdmin && (study.status.available)}">
                                            <c:set var="hideCol4" value="false"/>
                                            <c:set var="crfSpacersCount" value="3"/>
                                            <a href="DeleteEventCRF?action=confirm&ssId=<c:out value="${studySubject.id}"/>&ecId=<c:out value="${dec.eventCRF.id}"/>"
                                               onMouseDown="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                                               onMouseUp="javascript:setImage('bt_Delete<c:out value="${rowCount}"/>','images/bt_Delete.gif');"
                                              ><img name="bt_Remove<c:out value="${rowCount}"/>" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>

                                        <c:if test="${dec.eventCRF.id > 0 && !dec.eventCRF.notStarted && !dec.locked && !dec.stage.locked && (userRole.sysAdmin || userRole.studyAdministrator) && (study.status.available || study.status.pending) && !(studyEvent.subjectEventStatus.removed || studyEvent.subjectEventStatus.locked || studyEvent.subjectEventStatus.stopped || studyEvent.subjectEventStatus.skipped) && dec.eventDefinitionCRF.versions != null && fn:length(dec.eventDefinitionCRF.versions) > 1}">
                                            <c:set var="hideCol5" value="false"/>
                                            <c:set var="crfSpacersCount" value="2"/>
                                            <a href="pages/managestudy/chooseCRFVersion?crfId=${dec.eventCRF.crf.id}&crfName=${dec.eventCRF.crf.name}&crfversionId=${dec.eventCRF.crfVersion.id}&crfVersionName=${dec.eventCRF.crfVersion.name}&studySubjectLabel=${studySubject.label}&studySubjectId=${studySubject.id}&eventCRFId=${dec.eventCRF.id}&eventDefinitionCRFId=${dec.eventDefinitionCRF.id}"
                                            onMouseDown="javascript:setImage('bt_Reassign','images/bt_Reassign_d.gif');"
                                            onMouseUp="javascript:setImage('bt_Reassign','images/bt_Reassign.gif');">
                                            <img name="Reassign" src="images/bt_Reassign.gif" border="0" alt="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" title="<fmt:message key="reassign_crf_version" bundle="${resword}"/>" align="left" hspace="4"></a>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${not empty crfNDsMap[dec.eventCRF.crf.id]}">
                                    <c:forEach begin="1" end="${crfSpacersCount}">
                                        <img src="images/bt_Transparent.gif" class="crfBlankCellImg" border="0" align="left" hspace="4"/>
                                    </c:forEach>
                                    <c:choose>
             							<c:when test="${crfNDsMap[dec.eventCRF.crf.id] eq 'yellow'}">
                                            <c:set var="hideCol8" value="false"/>
               								<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&listNotes_f_discrepancyNoteBean.resolutionStatus=Not+Closed&listNotes_f_crfName=${dec.eventCRF.crf.name}&listNotes_f_eventName=${dec.eventCRF.name}">
                 		   					<img src="images/icon_flagYellow.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            							</c:when>
            							<c:when test="${crfNDsMap[dec.eventCRF.crf.id] eq 'red'}">
                                            <c:set var="hideCol8" value="false"/>
               								<a href="ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=${studySubject.label}&listNotes_f_discrepancyNoteBean.resolutionStatus=New&listNotes_f_crfName=${dec.eventCRF.crf.name}&listNotes_f_eventName=${dec.eventCRF.name}">
                 		   					<img src="images/icon_Note.gif" border="0" align="left" alt="<fmt:message key="view_all_discrepancy_notes_in" bundle="${resword}"/>" title="<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>" hspace="4"/></a>
            							</c:when>
            						</c:choose>
                            	</c:if>
                                </div>
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

<input type="hidden" id="hideCol1" style="display: none;" value="${hideCol1}"/>
<input type="hidden" id="hideCol2" style="display: none;" value="${hideCol2}"/>
<input type="hidden" id="hideCol3" style="display: none;" value="${hideCol3}"/>
<input type="hidden" id="hideCol4" style="display: none;" value="${hideCol4}"/>
<input type="hidden" id="hideCol5" style="display: none;" value="${hideCol5}"/>
<input type="hidden" id="hideCol6" style="display: none;" value="${hideCol6}"/>
<input type="hidden" id="hideCol7" style="display: none;" value="${hideCol7}"/>
<input type="hidden" id="hideCol8" style="display: none;" value="${hideCol8}"/>

<input type="hidden" id="popupTotalColumns" style="display: none;" value="8"/>

<c:if test="${showDDEColumn ne \"true\"}">
    <script>
        jQuery(".popupHeaderBlock").attr("colSpan", "3");
        jQuery(".ddeColumn").remove();//.css("display", "none");
        jQuery(".ddeColumnHeader").remove();//.css("display", "none");
    </script>
</c:if>

<jsp:include page="../include/changeTheme.jsp"/>
