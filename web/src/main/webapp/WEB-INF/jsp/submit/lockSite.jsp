<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy-header.jsp"/>

<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">
            <fmt:message key="confirm_lock_of_site"  bundle="${resword}"/> <c:out value="${studyBean.name}"/>. <fmt:message key="this_site_will_be_locked"  bundle="${resword}"/>
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

<jsp:useBean scope="request" id="displayEvent" class="org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>

<h1>
	<span class="first_level_header">
		<c:choose>
			<c:when test="${action eq 'lock'}">
				<fmt:message key="lockSiteStudySubjects"  bundle="${resword}"/>: ${studyBean.name}
			</c:when>
			<c:when test="${action eq 'unlock'}">
				<fmt:message key="unlockSiteStudySubjects" bundle="${resword}"/>: ${studyBean.name}
			</c:when>
			<c:otherwise> 
			</c:otherwise>
		</c:choose>
	</span>
</h1>

<div id="siteProperties" style="">

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr valign="top"><td class="table_header_column"><fmt:message key="parent_name" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${parentName}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="site_name" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.name}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>: </td><td class="table_cell">
    <c:out value="${siteToView.identifier}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="secondary_IDs" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.secondaryIdentifier}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="OID" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.oid}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.principalInvestigator}"/>
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.summary}"/>
</td></tr>

    <tr valign="top">
        <td class="table_header_column"><fmt:message key="protocol_verification" bundle="${resword}"/>:</td>
        <td class="table_cell">
            <cc-fmt:formatDate value="${siteToView.protocolDateVerification}" dateTimeZone="${userBean.userTimeZoneId}"/>
        </td>
    </tr>

    <tr valign="top">
        <td class="table_header_column"><fmt:message key="start_date" bundle="${resword}"/>:</td>
        <td class="table_cell">
            <cc-fmt:formatDate value="${siteToView.datePlannedStart}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;
        </td>
    </tr>

    <tr valign="top">
        <td class="table_header_column"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td>
        <td class="table_cell">
            <cc-fmt:formatDate value="${siteToView.datePlannedEnd}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;
        </td>
    </tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.expectedTotalEnrollment}"/>&nbsp;
</td></tr>
<tr valign="top"><td class="table_header_column"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityName}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityCity}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityState}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityZip}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityCountry}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityContactName}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityContactDegree}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityContactPhone}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${siteToView.facilityContactEmail}"/>&nbsp;
</td></tr>

<tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell">
    <c:choose>
        <c:when test="${siteToView.status.locked}">
            <strong><c:out value="${siteToView.status.name}"/></strong>
        </c:when>
        <c:otherwise>
            <c:out value="${siteToView.status.name}"/>
        </c:otherwise>
    </c:choose>
</td></tr>

<c:forEach var="config" items="${siteToView.studyParameters}">
<c:choose>
    <c:when test="${config.parameter.handle=='markImportedCRFAsCompleted'}">
        <tr valign="top">
            <td class="table_header_column"><fmt:message key="markImportedCRFAsCompleted" bundle="${resword}"/></td>
            <td class="table_cell">
                <c:choose>
                    <c:when test="${config.value.value== 'yes'}">
                        <fmt:message key="yes" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='autoScheduleEventDuringImport'}">
      <tr valign="top">
        <td class="table_header_column"><fmt:message key="autoScheduleEventDuringImport" bundle="${resword}"/></td>
        <td class="table_cell">
          <c:choose>
            <c:when test="${config.value.value== 'yes'}">
              <fmt:message key="yes" bundle="${resword}"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="no" bundle="${resword}"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='autoCreateSubjectDuringImport'}">
      <tr valign="top">
        <td class="table_header_column"><fmt:message key="autoCreateSubjectDuringImport" bundle="${resword}"/></td>
        <td class="table_cell">
          <c:choose>
            <c:when test="${config.value.value== 'yes'}">
              <fmt:message key="yes" bundle="${resword}"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="no" bundle="${resword}"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='allowSdvWithOpenQueries'}">
        <tr valign="top">
            <td class="table_header_column"><fmt:message key="allowSdvWithOpenQueries" bundle="${resword}"/></td>
            <td class="table_cell">
                <c:choose>
                    <c:when test="${config.value.value== 'yes'}">
                        <fmt:message key="yes" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='replaceExisitingDataDuringImport'}">
        <tr valign="top">
            <td class="table_header_column"><fmt:message key="replaceExisitingDataDuringImport" bundle="${resword}"/></td>
            <td class="table_cell">
                <c:choose>
                    <c:when test="${config.value.value== 'yes'}">
                        <fmt:message key="yes" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='interviewerNameRequired'}">
        <tr valign="top"><td class="table_header_column"><fmt:message key="when_entering_data" bundle="${resword}"/></td><td class="table_cell">
            <c:choose>
                <c:when test="${config.value.value== 'yes'}">
                    <fmt:message key="yes" bundle="${resword}"/>
                </c:when>
                <c:when test="${config.value.value== 'no'}">
                    <fmt:message key="no" bundle="${resword}"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="not_used" bundle="${resword}"/>
                </c:otherwise>
            </c:choose>
        </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='interviewerNameDefault'}">
        <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
            <c:choose>
                <c:when test="${config.value.value== 'blank'}">
                    <fmt:message key="blank" bundle="${resword}"/>

                </c:when>
                <c:otherwise>
                    <fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
                </c:otherwise>
            </c:choose>
        </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='interviewDateRequired'}">
        <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_required" bundle="${resword}"/></td><td class="table_cell">
            <c:choose>
                <c:when test="${config.value.value== 'yes'}">
                    <fmt:message key="yes" bundle="${resword}"/>
                </c:when>
                <c:when test="${config.value.value== 'no'}">
                    <fmt:message key="no" bundle="${resword}"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="not_used" bundle="${resword}"/>
                </c:otherwise>
            </c:choose>
        </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='interviewDateDefault'}">
        <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
            <c:choose>
                <c:when test="${config.value.value== 'blank'}">
                    <fmt:message key="blank" bundle="${resword}"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>
                </c:otherwise>
            </c:choose>
        </td>
        </tr>
    </c:when>
    <c:otherwise></c:otherwise>
</c:choose>

</c:forEach>

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>

<form action="LockSite" method="post">
    <input type="hidden" name="action" value="${action}">
    <input type="hidden" name="id" value="<c:out value="${studyBean.id}"/>">

    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
    <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit">

</form>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
