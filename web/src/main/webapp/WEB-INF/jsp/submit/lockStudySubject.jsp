<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">
            <fmt:message key="confirm_lock_of_study_subject"  bundle="${resword}"/> <c:out value="${studySubjectBean.name}"/>. <fmt:message key="this_study_subject_will_be_locked"  bundle="${resword}"/>
        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<h1>
	<span class="first_level_header">
		<c:choose>
			<c:when test="${action eq 'lock'}">
				<fmt:message key="lockStudySubject"  bundle="${resword}"/>: ${studySubjectBean.name}
			</c:when>
			<c:when test="${action eq 'unlock'}">
				<fmt:message key="unlockStudySubject"  bundle="${resword}"/>: ${studySubjectBean.name}
			</c:when>
			<c:otherwise> </c:otherwise>
		</c:choose>
	</span>
</h1>

<div id="studySubjectRecord" style="">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<c:set var="secondaryIdShow" value="${true}"/>
<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel"/>
<c:if test="${study ne null}">
    <c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
    <c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}"/>
</c:if>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<c:set var="enrollmentDateShow" value="${true}"/>
<fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel"/>
<c:if test="${study ne null}">
    <c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}"/>
    <c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}"/>
</c:if>

<table border="0" cellpadding="0" cellspacing="0">
<tbody><tr>

<td style="padding-right: 0px;" valign="top" width="800">

<!-- These DIVs define shaded box borders -->

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<table width="800" border="0" cellpadding="0" cellspacing="0">

<!-- Table Actions row (pagination, search, tools) -->

<tbody>
<tr>
<td valign="top">

<!-- Table Contents -->

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tbody><tr>
    <td class="table_header_column_top">${studySubjectLabel}</td>
    <td class="table_cell_top"><c:out value="${studySubjectBean.label}"/></td>
    <td class="table_header_row"><fmt:message key="person_ID" bundle="${resword}"/>

        <%-- DN for person ID goes here --%>
        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
            <c:set var="isNew" value="${hasUniqueIDNote eq 'yes' ? 0 : 1}"/>

            <c:choose>
                <c:when test="${hasUniqueIDNote eq 'yes'}">
                    <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
                        <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                    </a>

                </c:when>
                <c:otherwise>

                    <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
                        <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                    </a>
                </c:otherwise>
            </c:choose>
        </c:if>
    </td>
    <td class="table_cell_top"><c:out value="${subject.uniqueIdentifier}"/>
    </td>
</tr>

<tr>
    <c:choose>
        <c:when test="${secondaryIdShow}">
            <td class="table_header_column">${secondaryIdLabel}</td>
            <td class="table_cell"><c:out value="${studySubjectBean.secondaryLabel}"/></td>
        </c:when>
        <c:otherwise>
            <td class="table_header_column">&nbsp;</td>
            <td class="table_cell">&nbsp;</td>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${subjectStudy.studyParameterConfig.collectDob == '1'}">

            <td class="table_header_row"><fmt:message key="date_of_birth" bundle="${resword}"/>

                    <%-- DN for DOB goes here --%>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>

                    <c:choose>
                        <c:when test="${hasDOBNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>

                        </c:when>
                        <c:otherwise>

                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:otherwise>
                    </c:choose>

                </c:if>
            </td>
            <td class="table_cell"><fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/></td>

        </c:when>
        <c:when test="${subjectStudy.studyParameterConfig.collectDob == '3'}">

            <td class="table_header_row"><fmt:message key="date_of_birth" bundle="${resword}"/>

                    <%-- DN for DOB goes here --%>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>

                    <c:choose>
                        <c:when test="${hasDOBNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>

                        </c:when>
                        <c:otherwise>

                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:otherwise>
                    </c:choose>

                </c:if>
            </td>
            <td class="table_cell"><fmt:message key="not_used" bundle="${resword}"/></td>

        </c:when>
        <c:otherwise>

            <td class="table_header_row"><fmt:message key="year_of_birth" bundle="${resword}"/>

                    <%-- DN for DOB goes here --%>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>

                    <c:choose>
                        <c:when test="${hasDOBNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>

                        </c:when>
                        <c:otherwise>

                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob'); return false;">
                                <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:otherwise>
                    </c:choose>

                </c:if>
            </td>
            <td class="table_cell"><c:out value="${yearOfBirth}"/>
            </td>

        </c:otherwise>
    </c:choose>

</tr>
<tr>
    <td class="table_header_column"><fmt:message key="status" bundle="${resword}"/></td>
    <td class="table_cell"><c:out value="${studySubjectBean.status.name}"/></td>

    <c:choose>
        <c:when test="${genderShow}">
            <td class="table_header_row">${genderLabel}

                    <%-- DN for Gender goes here --%>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNew" value="${hasGenderNote eq 'yes' ? 0 : 1}"/>
                    <c:choose>
                        <c:when test="${hasGenderNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${subject.id}&name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
                                <img id="flag_gender" name="flag_gender" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?subjectId=${studySubjectBean.id}&id=${subject.id}&writeToDB=1&name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
                                <img id="flag_gender" name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </td>
            <td class="table_cell">

                <c:choose>
                    <c:when test="${subject.gender==32}">
                        &nbsp;
                    </c:when>
                    <c:when test="${subject.gender==109 ||subject.gender==77}">
                        <fmt:message key="male" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="female" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>

            </td>
        </c:when>
        <c:otherwise>
            <td class="table_header_row">&nbsp;</td>
            <td class="table_cell">&nbsp;</td>
        </c:otherwise>
    </c:choose>

</tr>
<tr>
    <td class="table_header_column"> </td>
    <td class="table_cell"> </td>
    <c:choose>
        <c:when test="${enrollmentDateShow}">
            <td class="table_header_row">${enrollmentDateLabel}
                &nbsp;
                    <%-- DN for enrollment date goes here --%>
                <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                    <c:set var="isNew" value="${hasEnrollmentNote eq 'yes' ? 0 : 1}"/>
                    <c:choose>
                        <c:when test="${hasEnrollmentNote eq 'yes'}">
                            <a href="#" onClick="openDNoteWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySubjectBean.id}&id=${studySubjectBean.id}&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
                                <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_Note.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="#" onClick="openDNoteWindow('CreateDiscrepancyNote?subjectId=${studySubjectBean.id}&id=${studySubjectBean.id}&writeToDB=1&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
                                <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </td>
            <td class="table_cell"><fmt:formatDate value="${studySubjectBean.enrollmentDate}" pattern="${dteFormat}"/>&nbsp;
            </td>
        </c:when>
        <c:otherwise>
            <td class="table_header_row">&nbsp;</td>
            <td class="table_cell">&nbsp;</td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <td class="table_divider" colspan="4">&nbsp;</td>
</tr>

<tr>

    <td class="table_header_column_top"><fmt:message key="study_name" bundle="${resword}"/></td>
    <td class="table_cell_top">
        <c:choose>
            <c:when test="${subjectStudy.parentStudyId > 0}">
                <a href="ViewStudy?id=<c:out value="${parentStudy.id}"/>&amp;viewFull=yes"><c:out value="${parentStudy.name}"/></a>
            </c:when>
            <c:otherwise>
                <a href="ViewStudy?id=<c:out value="${subjectStudy.id}"/>&amp;viewFull=yes"><c:out value="${subjectStudy.name}"/></a>
            </c:otherwise>
        </c:choose>
    </td>
    <td class="table_header_row"><fmt:message key="site_name" bundle="${resword}"/></td>
    <td class="table_cell_top">
        <c:if test="${subjectStudy.parentStudyId>0}">
            <a href="ViewSite?id=<c:out value="${subjectStudy.id}"/>"><c:out value="${subjectStudy.name}"/></a>
        </c:if>&nbsp;</td>
</tr>

</tbody></table>

<!-- End Table Contents -->

</td>
</tr>
</tbody></table>


</div>

</div></div></div></div></div></div></div></div>

</td>
</tr>
</tbody></table>
<br>


</div>

<form action="LockStudySubject" method="post">
    <input type="hidden" name="action" value="${action}">
    <input type="hidden" name="id" value="<c:out value="${studySubjectBean.id}"/>">

    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
    <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">

</form>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
