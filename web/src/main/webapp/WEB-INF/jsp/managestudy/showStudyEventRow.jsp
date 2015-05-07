<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean scope="session" id="userRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean"/>
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.DisplayStudyEventRow"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>

<tr>
<td class="table_cell_left"><c:out value="${currRow.bean.studyEvent.studyEventDefinition.name}"/>
    <c:if test="${currRow.bean.studyEvent.studyEventDefinition.repeating}">
        (<c:out value="${currRow.bean.studyEvent.sampleOrdinal}"/>)
    </c:if>
</td>
<td class="table_cell"><fmt:formatDate value="${currRow.bean.studyEvent.dateStarted}" pattern="${dteFormat}"/>
</td>

<td class="table_cell"><c:out value="${currRow.bean.studyEvent.location}"/></td>
<td class="table_cell" width="30"><c:out value="${currRow.bean.studyEvent.subjectEventStatus.name}"/></td>
<td class="table_cell">
    <table border="0" cellpadding="0" cellspacing="0">
        <tr class="innerTable">
            <td>
                <a href="EnterDataForStudyEvent?eventId=<c:out value="${currRow.bean.studyEvent.id}"/>"
                   onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                   onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
                   data-cc-subjectStudyEventId="${currRow.bean.studyEvent.id}"
                   onclick="setAccessedObjected(this)"><img
                        name="bt_View1" src="images/bt_View.gif" border="0"
                        alt="<fmt:message key="view" bundle="${resword}"/>"
                        title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>

        <c:if test="${(!studySub.status.deleted && !currRow.bean.studyEvent.status.deleted && currRow.bean.studyEvent.editable) && (study.status.available) and userRole.id ne 6 and userRole.id ne 9}">
            <tr class="innerTable">
                <td>
                    <a href="UpdateStudyEvent?event_id=<c:out value="${currRow.bean.studyEvent.id}"/>&ss_id=<c:out value="${studySub.id}"/>"
                       onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
                       onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
                       onclick="setAccessedObjected(this)"><img
                            name="bt_Edit1" src="images/bt_Edit.gif" border="0"
                            alt="<fmt:message key="edit" bundle="${resword}"/>"
                            title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
        </c:if>

        <tr class="innerTable">
            <td>
                <c:choose>
                <c:when test="${!currRow.bean.studyEvent.status.deleted}">
                <c:if test="${userRole.manageStudy && study.status.available}">
                <a href="RemoveStudyEvent?action=confirm&id=<c:out value="${currRow.bean.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
                   onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                   onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                   onclick="setAccessedObjected(this)"><img
                        name="bt_Remove1" src="images/bt_Remove.gif" border="0"
                        alt="<fmt:message key="remove" bundle="${resword}"/>"
                        title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
                </c:if>
                </c:when>
                <c:otherwise>
                <c:if test="${(userRole.manageStudy && !studySub.status.deleted) && (study.status.available)}">
                <a href="RestoreStudyEvent?action=confirm&id=<c:out value="${currRow.bean.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
                   onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
                   onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
                   onclick="setAccessedObjected(this)"><img
                        name="bt_Restore3" src="images/bt_Restore.gif" border="0"
                        alt="<fmt:message key="restore" bundle="${resword}"/>"
                        title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
                </c:if>
                </c:otherwise>
                </c:choose>

                <c:if test="${currRow.bean.studyEvent.subjectEventStatus.scheduled && userRole.manageStudy && study.status.available}">
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr class="innerTable">
            <td>
                <a href="DeleteStudyEvent?action=confirm&id=<c:out value="${currRow.bean.studyEvent.id}"/>&studySubId=<c:out value="${studySub.id}"/>"
                   onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                   onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                   onclick="setAccessedObjected(this)"><img
                        name="bt_Remove1" src="images/bt_Delete.gif" border="0"
                        alt="<fmt:message key="delete" bundle="${resword}"/>"
                        title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"></a>
            </td>
        </tr>
        </c:if>


        </td>
        </tr>
    </table>
</td>
<td class="table_cell">

<c:choose>

<c:when test="${empty currRow.bean.uncompletedCRFs && empty currRow.bean.displayEventCRFs}">
    <fmt:message key="no_CRFs" bundle="${resword}"/>
</c:when>

<c:otherwise>

<table border="0" cellpadding="0" cellspacing="0" width="100%">

<c:forEach var="dedc" items="${currRow.bean.fullCrfList}">
<c:choose>
<c:when test="${dedc['class'].name eq 'org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean'}">

    <c:set var="getQuery"
           value="eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}&eventCRFId=${dedc.eventCRF.id}&exitTo=ViewStudySubject?id=${studySub.id}"/>
    <form name="startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">

        <tr valign="top" class="innerTable">
            <td class="table_cell_border" width="180px"><c:out value="${dedc.edc.crf.name}"/>
                <c:if test="${dedc.edc.requiredCRF}">
                    <span style="color: orange">*</span>
                </c:if>
                <c:if test="${(dedc.edc.sourceDataVerification.code eq 1 or dedc.edc.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}">
                    <img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/>
                </c:if>
            </td>
            <td class="table_cell_border" width="60px">

                <c:set var="versionCount" value="0"/>
                <c:set var="firstVersionId" value="0"/>
                <c:set var="versionOid" value="*"/>
                <c:forEach var="version" items="${dedc.edc.versions}">
                    <c:if test="${versionCount == 0}">
                        <c:set var="versionOid" value="${version.oid}"/>
                        <c:set var="firstVersionId" value="${version.id}"/>
                    </c:if>
                    <c:set var="versionCount" value="${versionCount+1}"/>
                </c:forEach>

                <c:set var="dynamicCrfVersionId" value="crfVersionId${firstVersionId}"/>
                <c:choose>
                    <c:when test="${dedc.eventCRF.notStarted && dedc.eventCRF.id == 0}">
                        <input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId"
                               value="<c:out value="${firstVersionId}"/>">
                        <c:set var="dynamicCrfVersionId" value="crfVersionId${firstVersionId}"/>
                    </c:when>
                    <c:when test="${versionCount > 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
                        <input type="hidden" id="crfVersionId${dedc.eventCRF.crfVersion.id}" name="crfVersionId"
                               value="<c:out value="${dedc.eventCRF.crfVersion.id}"/>">
                        <c:set var="dynamicCrfVersionId" value="crfVersionId${dedc.eventCRF.crfVersion.id}"/>
                    </c:when>
                    <c:when test="${versionCount == 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
                        <input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId"
                               value="<c:out value="${firstVersionId}"/>">
                        <c:set var="dynamicCrfVersionId" value="crfVersionId${firstVersionId}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" id="crfVersionId${defaultVersionId}" name="crfVersionId"
                               value="<c:out value="${defaultVersionId}"/>">
                        <c:set var="dynamicCrfVersionId" value="crfVersionId${defaultVersionId}"/>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${versionCount<=1}">
                        <c:choose>
                            <c:when test="${dedc.status.locked}">
                                ${dedc.eventCRF.crfVersion.name}
                                <script>$('#${dynamicCrfVersionId}').val('${dedc.eventCRF.crfVersion.id}')</script>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="version" items="${dedc.edc.versions}">
                                    <c:out value="${version.name}"/>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:when>

                    <c:when test="${dedc.eventCRF.notStarted || dedc.eventCRF.id == 0}">
                        <select name="versionId<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>"
                                onchange="javascript:changeQuery
                                    <c:out value="${currRow.bean.studyEvent.id}"/>
                                    <c:out value="${dedc.edc.crf.id}"/>();">
                            <c:forEach var="version" items="${dedc.edc.versions}">
                                <c:set var="getQuery"
                                       value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}"/>
                                <c:choose>
                                    <c:when test="${(dedc.edc.defaultVersionId == version.id && dedc.eventCRF.id == 0) || (dedc.eventCRF.CRFVersionId == version.id && dedc.eventCRF.notStarted)}">
                                        <script>$('#${dynamicCrfVersionId}').val('${version.id}');</script>
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
                            </c:forEach>
                        </select>

                        <SCRIPT LANGUAGE="JavaScript">
                            function changeQuery<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>() {
                                var qer = document.startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.versionId<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.value;
                                document.startForm<c:out value="${currRow.bean.studyEvent.id}"/><c:out value="${dedc.edc.crf.id}"/>.crfVersionId.value=qer;
                            }
                        </SCRIPT>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${dedc.eventCRF.crfVersion.name}"/>
                    </c:otherwise>
                </c:choose>
            </td>

            <c:choose>
                <c:when test="${dedc.status.locked}">
                    <td class="table_cell" bgcolor="#F5F5F5" align="center" width="20">
                        <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>"
                             title="<fmt:message key="locked" bundle="${resword}"/>">
                    </td>
                </c:when>

                <c:when test="${!studySubject.status.deleted}">
                    <c:choose>
                        <c:when test="${dedc.eventCRF.id > 0 and !dedc.eventCRF.notStarted}">
                            <td class="table_cell" bgcolor="#F5F5F5" align="center" width="20"><img
                                    src="images/icon_InitialDE.gif"
                                    alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>"
                                    title="<fmt:message key="initial_data_entry" bundle="${resword}"/>"></td>
                        </c:when>
                        <c:otherwise>
                            <td class="table_cell" bgcolor="#F5F5F5" align="center" width="20"><img
                                    src="images/icon_NotStarted.gif"
                                    alt="<fmt:message key="not_started" bundle="${resword}"/>"
                                    title="<fmt:message key="not_started" bundle="${resword}"/>"></td>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <td class="table_cell" bgcolor="#F5F5F5" align="center" width="20">
                        <img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>"
                             title="<fmt:message key="invalid" bundle="${resword}"/>">
                    </td>
                </c:otherwise>
            </c:choose>
            <td class="table_cell_border" width="110">&nbsp;&nbsp;</td>
            <td class="table_cell_border">
                <table cellspacing="0" cellpadding="0" border="0">
                    <tr class="innerTable">
                        <td>
                            <c:choose>
                                <c:when test="${!dedc.status.locked && !currRow.bean.studyEvent.subjectEventStatus.locked && !studySub.status.deleted && study.status.available && !currRow.bean.studyEvent.status.deleted && userRole.role.id ne 6 and userRole.role.id ne 9}">
                                    <ui:dataEntryLink object="${dedc}" rowCount="${rowCount}" actionQueryTail="${currRow.bean.studyEvent.id}${dedc.edc.crf.id}" onClickFunction="setAccessedObjected(this); checkCRFLockedInitial"  imgAlign="right" imgHSpace="6"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="images/bt_Transparent.gif" border="0" hspace="6">
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
							<ui:viewDataEntryLink hspace="6" object="${dedc}" onClick="setAccessedObjected(this); viewCrfByVersion('${dedc.edc.id}', '${studySub.id}', $('#${dynamicCrfVersionId}').val(), '${currRow.bean.studyEvent.id}', 1, 'ViewStudySubject?id=${studySub.id}');"/>
                        </td>

                        <td>
							<ui:printEventCRFLink crfVersionOid="${versionOid}" dedc="${dedc}"/>
                        </td>
                        <c:if test="${dedc.eventCRF.id == 0 and dedc.eventCRF.notStarted && (userRole.id == 9 || userRole.id == 6 || userRole.id == 4 || userRole.id == 2  || userRole.id == 1)}">
                            <td><img src="images/bt_Transparent.gif" border="0" hspace="6"></td>
                            <td><img src="images/bt_Transparent.gif" border="0" hspace="6"></td>
                            <td><img src="images/bt_Transparent.gif" border="0" hspace="6"></td>
                        </c:if>

                    </tr>
                </table>
            </td>
        </tr>
    </form>

</c:when>
<c:when test="${dedc['class'].name eq 'org.akaza.openclinica.bean.submit.DisplayEventCRFBean'}">
    <c:set var="dec" value="${dedc}"/>
    <c:set var="allowDataEntry" value="${(study.status.available && dec.continueInitialDataEntryPermitted) || (study.status.available && (dec.startDoubleDataEntryPermitted || dec.continueDoubleDataEntryPermitted)) || ((study.status.available || study.status.frozen) && dec.performAdministrativeEditingPermitted)}" />
    <tr class="innerTable">
        <td class="table_cell_border" width="180px"><c:out value="${dec.eventCRF.crf.name}"/> <c:if test="${dec.eventDefinitionCRF.requiredCRF}"><span style="color: orange">*</span></c:if> <c:if
                test="${(dec.eventDefinitionCRF.sourceDataVerification.code eq 1 or dec.eventDefinitionCRF.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}">
            <img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/></c:if></td>
        <td class="table_cell_border" width="60px"><c:out value="${dec.eventCRF.crfVersion.name}"/></td>
        <td class="table_cell_border" bgcolor="#F5F5F5" align="center" width="20">
            <c:choose>
                <c:when test="${dec.stage.initialDE}">
                    <img src="images/icon_InitialDE.gif"
                         alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>"
                         title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
                </c:when>
                <c:when test="${dec.stage.initialDE_Complete}">
                    <img src="images/icon_InitialDEcomplete.gif"
                         alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>"
                         title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
                </c:when>
                <c:when test="${dec.stage.doubleDE}">
                    <c:set var="ddeTitleCode" value="double_data_entry"/>
                    <c:if test="${dec.eventDefinitionCRF.evaluatedCRF && !dec.eventDefinitionCRF.doubleEntry}"><c:set var="ddeTitleCode" value="evaluation"/></c:if>
                    <img src="images/icon_DDE.gif" alt="<fmt:message key="${ddeTitleCode}" bundle="${resword}"/>" title="<fmt:message key="${ddeTitleCode}" bundle="${resword}"/>">
                </c:when>
                <c:when test="${dec.stage.doubleDE_Complete}">
                    <c:choose>
                        <c:when test="${currRow.bean.studyEvent.subjectEventStatus.signed}">
                            <img src="images/icon_Signed.gif"
                                 alt="<fmt:message key="subjectEventSigned" bundle="${resword}"/>"
                                 title="<fmt:message key="subjectEventSigned" bundle="${resword}"/>">
                        </c:when>
                        <c:when test="${dec.eventCRF.sdvStatus}">
                            <img src="images/icon_DoubleCheck.gif"
                                 alt="<fmt:message key="sourceDataVerified" bundle="${resword}"/>"
                                 title="<fmt:message key="sourceDataVerified" bundle="${resword}"/>">
                        </c:when>
                        <c:otherwise>
                            <img src="images/icon_DEcomplete.gif"
                                 alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>"
                                 title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${dec.stage.admin_Editing}">
                    <img src="images/icon_AdminEdit.gif"
                         alt="<fmt:message key="administrative_editing" bundle="${resword}"/>"
                         title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
                </c:when>
                <c:when test="${dec.stage.locked || dec.eventCRF.status.locked || dec.locked}">
                    <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>"
                         title="<fmt:message key="locked" bundle="${resword}"/>">
                </c:when>
                <c:otherwise>
                    <img src="images/icon_Invalid.gif" alt="<fmt:message key="removed" bundle="${resword}"/>"
                         title="<fmt:message key="removed" bundle="${resword}"/>">
                </c:otherwise>
            </c:choose>

        </td>
        <td class="table_cell_border" width="110">
            <c:if test="${dec.eventCRF.updatedDate != null}">
                <fmt:formatDate value="${dec.eventCRF.updatedDate}" pattern="${dteFormat}"/><br>
            </c:if>
            <c:choose>
                <c:when test="${dec.eventCRF.updater.name == null}">
                    (<c:out value="${dec.eventCRF.owner.name}"/>)
                </c:when>
                <c:otherwise>
                    (<c:out value="${dec.eventCRF.updater.name}"/>)
                </c:otherwise>
            </c:choose>
        </td>

        <td class="table_cell_border">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr valign="top" class="innerTable <c:if test='${dec.eventDefinitionCRF.hideCrf and study.parentStudyId > 0}'>hidden</c:if>">
                    <td>
                        <c:choose>
                            <c:when test="${!dec.eventCRF.status.deleted && !dec.eventCRF.status.locked && study.status.available && !currRow.bean.studyEvent.status.deleted && userRole.role.id ne 6 && userRole.role.id ne 9}">
                                <ui:dataEntryLink object="${dec}" actionQueryTail="?eventCRFId=${dec.eventCRF.id}&exitTo=ViewStudySubject?id=${studySub.id}" imgAlign="left" imgHSpace="6" onClickFunction="setAccessedObjected(this); checkCRFLocked"/>
                                <%-- locked status here --%>
                                <c:if test="${dec.locked || dec.eventCRF.status.locked || dec.stage.locked || currRow.bean.studyEvent.subjectEventStatus.locked}">
                                    <img name="itemForSpace" src="images/bt_EnterData.gif" border="0"
                                         style="visibility:hidden" align="left" hspace="6">
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <img src="images/bt_Transparent.gif" border="0" hspace="6">
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
						<ui:viewDataEntryLink object="${dec}" queryTail="&studySubjectId=${studySub.id}&exitTo=ViewStudySubject?id=${studySub.id}" onClick="setAccessedObjected(this)" hspace="6" />
                    </td>
                    <td>
						<ui:printEventCRFLink studyOid="${parentStudyOid}" subjectOid="${studySub.oid}" studyEvent="${currRow.bean.studyEvent}" dec="${dec}"/>
                    </td>

                    <c:choose>
                        <c:when test="${!dec.eventCRF.status.deleted}">

                            <c:if test="${userRole.manageStudy && study.status.available && (!dec.locked) && (!dec.stage.locked)}">
                                <td>
									<ui:removeEventCRFLink object="${dec}" subjectId="${studySub.id}" onClick="setAccessedObjected(this)" hspace="6"/>
                                </td>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${userRole.id ne 4 and userRole.id ne 5 and userRole.id ne 6 and userRole.id ne 9 && (!studySub.status.deleted) && (study.status.available)}">
                                <td>
									<ui:restoreEventCRFLink object="${dec}" subjectId="${studySub.id}" hspace="6" onClick="setAccessedObjected(this)"/>
                                </td>
                            </c:if>
                            <c:if test="${userRole.manageStudy && dec.eventCRF.status.deleted && (!dec.locked) && (!dec.stage.locked) && userRole.id ne 2 && userRole.id ne 1}">
                                <td>
                                    <img src="images/bt_Transparent.gif" border="0" hspace="6">
                                </td>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${!dec.eventCRF.status.deleted && (userRole.id == 6 or userRole.id == 9)}">
                        <td>
                            <img src="images/bt_Transparent.gif" border="0" hspace="6">
                        </td>
                    </c:if>
                    <c:if test="${userRole.id ne 4 and userRole.id ne 5 and userRole.id ne 6 and userRole.id ne 9 and (!studySub.status.deleted) && (study.status.available) && (!dec.locked) && (!dec.stage.locked)}">
                        <td>
							<ui:deleteEventCRFLink object="${dec}" subjectId="${studySub.id}" onClick="setAccessedObjected(this)"/>
                        </td>
                    </c:if>
                    <c:if test="${dec.stage.invalid && (userRole.id == 1 || userRole.id == 2) && (studySub.status.deleted)}">
                        <td>
                            <img src="images/bt_Transparent.gif" border="0" hspace="6">
                        </td>
                        <td>
                            <img src="images/bt_Transparent.gif" border="0" hspace="6">
                        </td>
                    </c:if>
                    <c:if test="${(studySub.status.deleted) && !dec.stage.invalid && (userRole.id == 1 || userRole.id == 2)}">
                        <td>
                            <img src="images/bt_Transparent.gif" border="0" hspace="6">
                        </td>
                    </c:if>
                    <c:choose>
                        <c:when test="${allowDataEntry && dec.eventCRF.id > 0 && !dec.eventCRF.notStarted && !dec.locked && !dec.stage.locked && (userRole.sysAdmin || userRole.studyAdministrator) && (study.status.available || study.status.pending) && !(currRow.bean.studyEvent.subjectEventStatus.removed || currRow.bean.studyEvent.subjectEventStatus.locked || currRow.bean.studyEvent.subjectEventStatus.stopped || currRow.bean.studyEvent.subjectEventStatus.skipped) && dec.eventDefinitionCRF.versions != null && fn:length(dec.eventDefinitionCRF.versions) > 1}">
                            <td>
								<ui:choseCRFVersionIcon dec="${dec}" subjectBean="${studySub}" onClick="setAccessedObjected(this)"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td width="150px">
                                <img src="images/bt_Transparent.gif" border="0" hspace="6">
                                <c:if test="${userRole.id == 9 or userRole.id == 6 or userRole.id == 2 or userRole.id == 1}">
                                    <img src="images/bt_Transparent.gif" border="0" hspace="6">
                                    <img src="images/bt_Transparent.gif" border="0" hspace="6">
                                </c:if>
                            </td>
                        </c:otherwise>
                    </c:choose>

                </tr>
            </table>
        </td>
    </tr>
</c:when>
</c:choose>
</c:forEach>
</table>
</c:otherwise>
</c:choose>
</td>

</tr>
