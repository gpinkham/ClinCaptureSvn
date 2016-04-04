<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.admin" var="resadmin"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="reserrors"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<c:set var="bioontologyURL" value="${studyToView.studyParameterConfig.defaultBioontologyURL}"/>
<c:set var="medicalCodingApiKey" value="${studyToView.studyParameterConfig.medicalCodingApiKey}"/>

<c:choose>
	<c:when test="${userRole.role.id > 3}">
		<jsp:include page="../include/home-header.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="../include/admin-header.jsp"/>
	</c:otherwise>
</c:choose>

<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<jsp:useBean scope ="request" id="studyTypes" class="java.util.ArrayList"/>

<jsp:useBean scope="request" id="isInterventional" class ="java.lang.String"/>
<jsp:useBean scope="request" id="studyId" class ="java.lang.String"/>

<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript: leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope='request' id='parentStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='studyToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='sitesToView' class='java.util.ArrayList'/>

<script language="JavaScript">
	<c:import url="../../../includes/js/pages/update_study.js?r=${revisionNumber}" />
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<h1>
	<span class="first_level_header">
		<fmt:message key="update_study_details" bundle="${resword}"/> <c:out value="${studyToView.name}"/>
	</span>
</h1>

<c:set var="startDate" value="" />
<c:set var="endDate" value="" />
<c:set var="approvalDate" value="" />
<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "approvalDate"}'>
		<c:set var="approvalDate" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<br>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
	<c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
	<c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<br>
<form action="UpdateStudyNew" method="post">
<input type=hidden name="action" value="submit">
<input type=hidden name="studyId" value="<c:out value="${studyId}"/>">
<a href="javascript:leftnavExpand('section1');">
	<img id="excl_section1" src="images/bt_Collapse.gif" border="0"> <span class="table_title_Admin">
    <fmt:message key="study_description_status" bundle="${resword}"/>  </span></a>
<div id="section1" style="">
<div style="width: 670px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="450">
<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;">
			<b><fmt:message key="unique_protocol_ID" bundle="${resword}"/></b>:
		</a>
	</td>
	<td>
		<input type="text" name="protocolId" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.identifier}"/>" class="formfieldXL bw2 h15">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolId"/></jsp:include></td>
	<td width="10%" class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<b><fmt:message key="study_name" bundle="${resword}"/></b>:
	</td>
	<td>
		<input type="text" name="studyName" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.name}"/>" class="formfieldXL h15 bw2" ${studyToView.origin eq 'studio' ? 'maxlength="20"' : 'maxlength="100"'}>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyName"/></jsp:include>
	</td>
	<td class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<b><fmt:message key="brief_title" bundle="${resword}"/></b>:
	</td>
	<td>
		<input type="text" name="briefTitle" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.briefTitle}"/>" class="formfieldXL h15 bw2" maxlength="100">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="briefTitle"/></jsp:include>
	</td>
	<td class="alert">&nbsp;</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<b><fmt:message key="official_title" bundle="${resword}"/></b>:
	</td>
	<td>
		<input type="text" name="officialTitle" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.officialTitle}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="officialTitle"/></jsp:include>
	</td>
	<td>
		&nbsp;
	</td>
</tr>

<c:choose>
	<c:when test="${studyToView.parentStudyId == 0}">
		<c:set var="key" value="study_system_status"/>
	</c:when>
	<c:otherwise>
		<c:set var="key" value="site_system_status"/>
	</c:otherwise>
</c:choose>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="${key}" bundle="${resword}"/>:
	</td>
	<td>
		<c:set var="dis" value="${parentStudy.name!='' && !parentStudy.status.available}"/>
		<c:set var="status1" value="${studyToView.status.id}"/>
		<select class="formfieldXL bw1 h20 w250" name="statusId" disabled="true">
			<c:forEach var="status" items="${statuses}">
			<c:choose>
			<c:when test="${status1 == status.id}">
			<option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
				</c:when>
				<c:otherwise>
			<option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>
				</c:otherwise>
				</c:choose>
				</c:forEach>
		</select>
		<input type=hidden name="status" value="${status1}">
	</td>
	<td class="alert">
		*
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds'); return false;"><b><fmt:message key="secondary_IDs" bundle="${resword}"/></b>:</a>
		<br>
		(<fmt:message key="separate_by_commas" bundle="${resword}"/>)
	</td>
	<td>
		<textarea class="formtextareaXL4 bw1 maxw250" name="secondProId" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.secondaryIdentifier}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
	</td>
	<td>&nbsp;</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary'); return false;">
			<fmt:message key="brief_summary" bundle="${resword}"/>:
		</a>
	</td>
	<td>
		<textarea class="formtextareaXL4 bw1 maxw250" name="summary" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.summary}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="summary"/></jsp:include>
	</td>
	<td class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="detailed_description" bundle="${resword}"/>:
	</td>
	<td>
		<textarea class="formtextareaXL4 bw1 maxw250" name="description" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.protocolDescription}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
	</td>
	<td>
		&nbsp;
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="sponsor" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="sponsor" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.sponsor}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="sponsor"/></jsp:include>
	</td>
	<td class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="collaborators" bundle="${resword}"/>:
		<br>
		(<fmt:message key="separate_by_commas" bundle="${resword}"/>)
	</td>
	<td>
		<textarea class="formtextareaXL4 bw1 maxw250" name="collaborators" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.collaborators}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="collaborators"/></jsp:include>
	</td>
	<td>
		&nbsp;
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="principal_investigator" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="principalInvestigator" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.principalInvestigator}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="principalInvestigator"/></jsp:include>
	</td>
	<td class="alert">*</td>
</tr>

<!-- section B-->
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="study_type" bundle="${resword}"/>:
	</td>
	<td>
		<c:set var="phase1" value="${studyToView.phaseKey}"/>
		<select name="phase" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
			<c:forEach var="phase" items="${currentMapHolder.studyPhaseList}">
				<c:choose>
					<c:when test="${phase1 == phase.value}">
						<option value="${phase.id}" selected><fmt:message key="${phase.code}" bundle="${resadmin}"/></option>
					</c:when>
					<c:otherwise>
						<option value="${phase.id}"><fmt:message key="${phase.code}" bundle="${resadmin}"/></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</td>
	<td class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="protocol_type" bundle="${resword}"/>:
	</td>
	<td class="pb10">
		<c:set var="type1" value="observational"/>
		<c:choose>
			<c:when test="${studyToView.protocolTypeKey == type1}">
				<input type="hidden" name="protocolType" value="1"/>
				<input type="radio" onchange="javascript:changeIcon()" checked value="observational" disabled><fmt:message key="observational" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="protocolType" value="0"/>
				<input type="radio" onchange="javascript:changeIcon()" checked value="interventional" disabled><fmt:message key="interventional" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
	<td>&nbsp;</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="expected_total_enrollment" bundle="${resword}"/>:
	</td>
	<td class="pb10">
		<input type="text" name="totalEnrollment" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.expectedTotalEnrollment}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="totalEnrollment"/></jsp:include>
	</td>
	<td class="alert">*</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="study_start_date" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="startDate" onchange="javascript:changeIcon()" value="<c:out value="${startDate}" />" class="formfieldXL  bw2 h15" id="startDateField">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>
	</td>
	<td>
		<ui:calendarIcon onClickSelector="'#startDateField'"/>
		<span class="alert">*</span>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="study_completion_date" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="endDate" onchange="javascript:changeIcon()" value="<c:out value="${endDate}" />" class="formfieldXL h15 bw2" id="endDateField">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include>
	</td>
	<td>
		<ui:calendarIcon onClickSelector="'#endDateField'"/>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate'); return false;"><fmt:message key="protocol_verification" bundle="${resword}"/>:</a>
	</td>
	<td>
		<input type="text" name="approvalDate" onchange="javascript:changeIcon()" value="${approvalDate}" class="formfieldXL bw2 h15" id="protocolDateVerificationField">
		<br>
		<jsp:include page="../showMessage.jsp">
			<jsp:param name="key" value="approvalDate"/>
		</jsp:include>
	</td>
	<td>
		<ui:calendarIcon onClickSelector="'#protocolDateVerificationField'"/>
	</td>
</tr>

<!-- From Update Page 3 -->

<c:if test='${isInterventional==1}'>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntPurpose" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntPurpose'); return false;">
				<fmt:message key="purpose" bundle="${resword}"/></a>:
		</td>
		<td>
			<c:set var="purpose1" value="${studyToView.purposeKey}"/>
			<select name="purpose" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="purpose" items="${currentMapHolder.interPurposeList}">
					<c:choose>
						<c:when test="${purpose1 == purpose.value}">
							<option value="${purpose.id}" selected><fmt:message key="${purpose.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${purpose.id}"><fmt:message key="${purpose.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<br>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntAllocation" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntAllocation'); return false;">
				<fmt:message key="allocation" bundle="${resword}"/></a>:
		</td>
		<td>
			<c:set var="allocation1" value="${studyToView.allocationKey}"/>
			<select name="allocation" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="allocation" items="${currentMapHolder.allocationList}">
					<c:choose>
						<c:when test="${allocation1 == allocation.value}">
							<option value="${allocation.id}" selected><fmt:message key="${allocation.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${allocation.id}"><fmt:message key="${allocation.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntMasking" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntMasking'); return false;">
				<fmt:message key="masking" bundle="${resword}"/></a>:
		</td>
		<td>
			<c:set var="masking1" value="${studyToView.maskingKey}"/>
			<select name="masking" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="masking" items="${currentMapHolder.maskingList}">
					<c:choose>
						<c:when test="${masking1 == masking.value}">
							<option value="${masking.id}" selected><fmt:message key="${masking.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${masking.id}"><fmt:message key="${masking.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="control" bundle="${resword}"/>:
		</td>
		<td>
			<c:set var="control1" value="${studyToView.controlKey}"/>
			<select name="control" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="control" items="${currentMapHolder.controlList}">
					<c:choose>
						<c:when test="${control1 == control.value}">
							<option value="${control.id}" selected><fmt:message key="${control.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${control.id}"><fmt:message key="${control.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntDesign" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntDesign'); return false;">
				<fmt:message key="intervention_model" bundle="${resword}"/></a>:
		</td>
		<td>
			<c:set var="assignment1" value="${studyToView.assignmentKey}"/>
			<select name="assignment" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="assignment" items="${currentMapHolder.assignmentList}">
					<c:choose>
						<c:when test="${assignment1 == assignment.value}">
							<option value="${assignment.id}" selected><fmt:message key="${assignment.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${assignment.id}"><fmt:message key="${assignment.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints'); return false;">
				<fmt:message key="study_classification" bundle="${resword}"/></a>:</td>
		<td>
			<c:set var="endpoint1" value="${studyToView.endpointKey}"/>
			<select name="endPoint" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="endpoint" items="${currentMapHolder.endPointList}">
					<c:choose>
						<c:when test="${endpoint1 == endpoint.value}">
							<option value="${endpoint.id}" selected><fmt:message key="${endpoint.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${endpoint.id}"><fmt:message key="${endpoint.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
	</td>
	</tr>
</c:if>
<!-- End From Update Page 3 -->

<!-- condition for isInterventional should be applied -->
<!-- From Update Page 4 -->
<c:if test="${isInterventional==0}">
	<tr valign="bottom">
		<td class="formlabel">
			<fmt:message key="purpose" bundle="${resword}"/>:
		</td>
		<td>
			<c:set var="purpose1" value="${studyToView.purposeKey}"/>
			<select name="purpose" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="purpose" items="${currentMapHolder.obserPurposeList}">
					<c:choose>
						<c:when test="${purpose1 == purpose.value}">
							<option value="${purpose.id}" selected><fmt:message key="${purpose.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${purpose.id}"><fmt:message key="${purpose.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include>
		</td>
		<td>&nbsp;</td>
	</tr>

	<tr valign="bottom">
		<td class="formlabel">
			<fmt:message key="duration" bundle="${resword}"/>
		</td>
		<td>
			<c:set var="duration1" value="${studyToView.durationKey}"/>
			<select name="duration" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="duration" items="${currentMapHolder.durationList}">
					<c:choose>
						<c:when test="${duration1 == duration.value}">
							<option value="${duration.id}" selected><fmt:message key="${duration.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${duration.id}"><fmt:message key="${duration.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
		<td>&nbsp;</td>
	</tr>

	<tr valign="bottom">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#EligibilitySamplingMethod" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#EligibilitySamplingMethod'); return false;">
				<fmt:message key="selection" bundle="${resword}"/></a>
		</td>
		<td>
			<c:set var="selection1" value="${studyToView.selectionKey}"/>
			<select name="selection" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="selection" items="${currentMapHolder.selectionList}">
					<c:choose>
						<c:when test="${selection1 == selection.value}">
							<option value="${selection.id}" selected><fmt:message key="${selection.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${selection.id}"><fmt:message key="${selection.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
		<td>&nbsp;</td>
	</tr>

	<tr valign="bottom">
		<td class="formlabel">
			<fmt:message key="timing" bundle="${resword}"/>
		</td>
		<td>
			<c:set var="timing1" value="${studyToView.timingKey}"/>
			<select name="timing" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
				<c:forEach var="timing" items="${currentMapHolder.timingList}">
					<c:choose>
						<c:when test="${timing1 == timing.value}">
							<option value="${timing.id}" selected><fmt:message key="${timing.code}" bundle="${resadmin}"/></option>
						</c:when>
						<c:otherwise>
							<option value="${timing.id}"><fmt:message key="${timing.code}" bundle="${resadmin}"/></option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
		<td>&nbsp;</td>
	</tr>
	<!-- End of the condition -->
	<!-- End From Update Page 4 -->
</c:if>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>

<div style="font-family: Tahoma, Arial, Helvetica, Sans-Serif;font-size:17px;">
	<fmt:message key="expand_each_section" bundle="${restext}"/>
</div>

<br class="hidden">
<a href="javascript:leftnavExpand('section3');" class="hidden">
	<img id="excl_section3" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">
         <fmt:message key="conditions_and_eligibility" bundle="${resword}"/></span></a>
<div id="section3" style="display:none ">
	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="0">
					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="conditions" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="conditions" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.conditions}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="conditions"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="keywords" bundle="${resword}"/>:
							<br>
							(<fmt:message key="separate_by_commas" bundle="${resword}"/>)
						</td>
						<td>
							<textarea name="keywords" onchange="javascript:changeIcon()" rows="4" cols="50"  class="formtextareaXL4 bw1 maxw250"><c:out value="${studyToView.keywords}"/></textarea>
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="keywords"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="eligibility_criteria" bundle="${resword}"/>:
						</td>
						<td>
							<textarea name="eligibility" onchange="javascript:changeIcon()" rows="4" cols="50" class="formtextareaXL4 bw1 maxw250"><c:out value="${studyToView.eligibility}"/></textarea>
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="eligibility"/></jsp:include>
						</td>
					</tr>

					<c:if test="${genderShow}">
						<tr valign="top">
							<td class="formlabel">${genderLabel}:</td>
							<td>
								<select name="gender" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
									<c:set var="female">
										<fmt:message key="female" bundle="${resword}"/>
									</c:set>
									<c:set var="male">
										<fmt:message key="male" bundle="${resword}"/>
									</c:set>
									<c:choose>
										<c:when test="${studyToView.gender == female}">
											<option value="both"><fmt:message key="both" bundle="${resword}"/></option>
											<option value="male"><fmt:message key="male" bundle="${resword}"/></option>
											<option value="female" selected><fmt:message key="female" bundle="${resword}"/></option>
										</c:when>
										<c:when test="${studyToView.gender == male}">
											<option value="both"><fmt:message key="both" bundle="${resword}"/></option>
											<option value="male" selected><fmt:message key="male" bundle="${resword}"/></option>
											<option value="female"><fmt:message key="female" bundle="${resword}"/></option>
										</c:when>
										<c:otherwise>
											<option value="both" selected><c:out value="${studyToView.gender}"/></option>
											<option value="male"><fmt:message key="male" bundle="${resword}"/></option>
											<option value="female"><fmt:message key="female" bundle="${resword}"/></option>
										</c:otherwise>
									</c:choose>
								</select>
							</td>
						</tr>
					</c:if>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="minimum_age" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="ageMin" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.ageMin}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMin"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="maximum_age" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="ageMax" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.ageMax}"/>" class="formfieldXL bw2 h15">
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMax"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="healthy_volunteers_accepted" bundle="${resword}"/>:
						</td>
						<td>
							<select name="healthyVolunteerAccepted" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
								<c:choose>
									<c:when test="${studyToView.healthyVolunteerAccepted == true}">
										<option value="1" selected><fmt:message key="yes" bundle="${resword}"/></option>
										<option value="0"><fmt:message key="no" bundle="${resword}"/></option>
									</c:when>
									<c:otherwise>
										<option value="1"><fmt:message key="yes" bundle="${resword}"/></option>
										<option value="0" selected><fmt:message key="no" bundle="${resword}"/></option>
									</c:otherwise>
								</c:choose>
							</select>
						</td>
					</tr>
				</table>
			</div>
		</div></div></div></div></div></div></div></div>
	</div>
</div>
<br>

<a href="javascript:leftnavExpand('section4');">
	<img id="excl_section4" src="images/bt_Expand.gif" border="0">
    <span class="table_title_Admin">
        <fmt:message key="facility_information" bundle="${resword}"/></span></a>
<div id="section4" style="display:none ">
	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="5">
					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_name" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facName" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityName}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facName"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_city" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facCity" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityCity}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCity"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_state_province" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facState" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityState}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facState"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="postal_code" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facZip" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityZip}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facZip"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_country" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facCountry" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityCountry}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCountry"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_contact_name" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facConName" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactName}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConName"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_contact_degree" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facConDegree" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactDegree}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConDegree"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_contact_phone" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facConPhone" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactPhone}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConPhone"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="facility_contact_email" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="facConEmail" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactEmail}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConEmail"/></jsp:include>
						</td>
					</tr>
				</table>
			</div>
		</div></div></div></div></div></div></div></div>
	</div>
</div>
<br>

<a href="javascript:leftnavExpand('section5');">
	<img id="excl_section5" src="images/bt_Expand.gif" border="0">
	<span class="table_title_Admin">
           <fmt:message key="related_infomation" bundle="${resword}"/>
	</span>
</a>
<div id="section5" style="display:none ">
	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="0">
					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="MEDLINE_identifier" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="medlineIdentifier" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.medlineIdentifier}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="medlineIdentifier"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="results_reference" bundle="${resword}"/>:
						</td>
						<td>
							<select name="resultsReference" class="formfieldS bw1 h20" onchange="javascript:changeIcon()">
								<c:choose>
									<c:when test="${studyToView.resultsReference == true}">
										<option value="1" selected><fmt:message key="yes" bundle="${resword}"/></option>
										<option value="0"><fmt:message key="no" bundle="${resword}"/></option>
									</c:when>
									<c:otherwise>
										<option value="1"><fmt:message key="yes" bundle="${resword}"/></option>
										<option value="0" selected><fmt:message key="no" bundle="${resword}"/></option>
									</c:otherwise>
								</c:choose>
							</select>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="URL_reference" bundle="${resword}"/>
						</td>
						<td>
							<input type="text" name="url" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.url}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="url"/></jsp:include>
						</td>
					</tr>

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="URL_description" bundle="${resword}"/>
						</td>
						<td>
							<input type="text" name="urlDescription" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.urlDescription}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="urlDescription"/></jsp:include>
						</td>
					</tr>
				</table>
			</div>
		</div></div></div></div></div></div></div></div>
	</div>
</div>

<br>
<a href="javascript:leftnavExpand('section6');">
	<img id="excl_section6" src="images/bt_Expand.gif" border="0">
	<span class="table_title_Admin">
           <fmt:message key="notes_and_discrepancies_descriptions" bundle="${resword}"/>
	</span>
</a>
<br>
<div id="section6" style="display:none">

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="javascript:leftnavExpand('section6_1');">
	<img id="excl_section6_1" src="images/bt_Expand.gif" border="0">
		<span class="table_title_Admin">
			<fmt:message key="update_discrepancies_descriptions" bundle="${resword}"/>
		</span>
</a>
<br>

<div id="section6_1" name="" style="display:none" name="update_descriptions">

	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="0">
					<tr valign="top"><td class="formlabel"><fmt:message key="reason_for_update_descriptions" bundle="${resword}"/></td>
						<td>
							<table>
								<c:set var="count" value="0"/>
								<c:forEach var="term" items="${dDescriptionsMap['dnUpdateDescriptions']}">
									<tr>
										<td>
											<input type="text" name="updateName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr>
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateDescriptionError${count}"/></jsp:include></p>
										</td>
									</tr>
									<tr>
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:

											<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" ${term.visibilityLevel == 'Site' ? 'checked' : ''} value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" ${term.visibilityLevel == 'Study' ? 'checked' : ''} value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" ${term.visibilityLevel == 'Study and Site' ? 'checked' : ''} value="0"><fmt:message key="both" bundle="${resword}"/>
											<input type=hidden name="updateDescriptionId${count}" value="${term.id}">
											<c:set var="count" value="${count+1}"/>
											<br></br>
										</td>
									</tr>
								</c:forEach>
								<c:choose>
									<c:when test="${count < 3}">
										<c:set var="delta" value="${2-count}"/>
									</c:when>
									<c:when test="${count > 21}">
										<c:set var="delta" value="${24-count}"/>
									</c:when>
									<c:otherwise>
										<c:set var="delta" value="1"/>
									</c:otherwise>
								</c:choose>
								<c:forEach begin="${count}" end="${count+delta}">
									<tr id="dnUpdateDescriptionRow_a${count}">
										<td>
											<input type="text" name="updateName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'updateDescriptions');" value="" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr id="dnUpdateDescriptionRow_b${count}">
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateDescriptionError${count}"/></jsp:include></p>
										</td>
									</tr>
									<tr id="dnUpdateDescriptionRow_c${count}">
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:
											<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
											<br></br>
										</td>
									</tr>
									<c:set var="count" value="${count+1}"/>
								</c:forEach>
								<c:if test="${count <= 24}">
									<c:forEach begin="${count}" end="24">
										<tr style="display: none" id="dnUpdateDescriptionRow_a${count}">
											<td>
												<input type="text" name="updateName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'updateDescriptions');" value="" class="formfieldXL bw2 h15">
											</td>
										</tr>
										<tr style="display: none" id="dnUpdateDescriptionRow_b${count}">
											<td>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateName${count}"/></jsp:include></p>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="updateDescriptionError${count}"/></jsp:include></p>
											</td>
										</tr>
										<tr style="display: none" id="dnUpdateDescriptionRow_c${count}">
											<td>
												<fmt:message key="visibility" bundle="${resword}"/>:
												<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="updateVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
												<br></br>
											</td>
										</tr>
										<c:set var="count" value="${count+1}"/>
									</c:forEach>
								</c:if>
							</table>
						</td>
					</tr>
				</table>
			</div></div></div></div></div></div></div></div></div>
	</div>
</div>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="javascript:leftnavExpand('section6_2');">
	<img id="excl_section6_2" src="images/bt_Expand.gif" border="0">
		<span class="table_title_Admin">
			<fmt:message key="close_discrepancies_descriptions" bundle="${resword}"/>
		</span>
</a>
<br>

<div id="section6_2" style="display:none" name="close_descriptions">

	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="0">
					<tr valign="top"><td class="formlabel"><fmt:message key="reason_for_close_descriptions" bundle="${resword}"/></td>
						<td>
							<table>
								<c:set var="count" value="0"/>
								<c:forEach var="term" items="${dDescriptionsMap['dnCloseDescriptions']}">
									<tr>
										<td>
											<input type="text" name="closeName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr>
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeDescriptionError${count}"/></jsp:include></p>
										</td>
									</tr>
									<tr>
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:
											<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" ${term.visibilityLevel == 'Site' ? 'checked' : ''} value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" ${term.visibilityLevel == 'Study' ? 'checked' : ''} value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" ${term.visibilityLevel == 'Study and Site' ? 'checked' : ''} value="0"><fmt:message key="both" bundle="${resword}"/>

											<input type=hidden name="closeDescriptionId${count}" value="${term.id}">
											<c:set var="count" value="${count+1}"/>
											<br></br>
										</td>
									</tr>
								</c:forEach>
								<c:choose>
									<c:when test="${count < 3}">
										<c:set var="delta" value="${2-count}"/>
									</c:when>
									<c:when test="${count > 21}">
										<c:set var="delta" value="${24-count}"/>
									</c:when>
									<c:otherwise>
										<c:set var="delta" value="1"/>
									</c:otherwise>
								</c:choose>
								<c:forEach begin="${count}" end="${count+delta}">
									<tr id="dnCloseDescriptionRow_a${count}">
										<td>
											<input type="text" name="closeName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'closeDescriptions');" value="" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr id="dnCloseDescriptionRow_b${count}">
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeDescriptionError${count}"/></jsp:include></p>											</td>
										</td>
									</tr>
									<tr id="dnCloseDescriptionRow_c${count}">
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:
											<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
											<br></br>
										</td>
									</tr>
									<c:set var="count" value="${count+1}"/>
								</c:forEach>
								<c:if test="${count <= 24}">
									<c:forEach begin="${count}" end="24">
										<tr style="display: none" id="dnCloseDescriptionRow_a${count}">
											<td>
												<input type="text" name="closeName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'closeDescriptions');" value="" class="formfieldXL bw2 h15">
											</td>
										</tr>
										<tr style="display: none" id="dnCloseDescriptionRow_b${count}">
											<td>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeName${count}"/></jsp:include></p>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="closeDescriptionError${count}"/></jsp:include></p>												</td>
											</td>
										</tr>
										<tr style="display: none" id="dnCloseDescriptionRow_c${count}">
											<td>
												<fmt:message key="visibility" bundle="${resword}"/>:
												<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="closeVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
												<br></br>
											</td>
										</tr>
										<c:set var="count" value="${count+1}"/>
									</c:forEach>
								</c:if>
							</table>
						</td>
					</tr>
				</table>
			</div></div></div></div></div></div></div></div></div>
	</div>
</div>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="javascript:leftnavExpand('section6_3');">
	<img id="excl_section6_3" src="images/bt_Expand.gif" border="0">
		<span class="table_title_Admin">
           <fmt:message key="reason_for_change_descriptions" bundle="${resword}"/>
		</span>
</a>

<div id="section6_3" style="display:none" name="reasons_for_change_desciptions">

	<div style="width: 670px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0" cellpadding="0">
					<tr valign="top"><td class="formlabel"><fmt:message key="reason_for_change_descriptions" bundle="${resword}"/></td>
						<td>
							<table>
								<c:set var="count" value="0"/>
								<c:forEach var="term" items="${dDescriptionsMap['dnRFCDescriptions']}">
									<tr>
										<td>
											<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr>
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCDescriptionError${count}"/></jsp:include></p>
										</td>
									</tr>
									<tr>
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:

											<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" ${term.visibilityLevel == 'Site'} value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" ${term.visibilityLevel == 'Study'} value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}"  ${term.visibilityLevel == 'Study and Site'}value="0"><fmt:message key="both" bundle="${resword}"/>

											<input type=hidden name="dnRFCDescriptionId${count}" value="${term.id}">
											<c:set var="count" value="${count+1}"/>
											<br></br>
										</td>
									</tr>
								</c:forEach>
								<c:choose>
									<c:when test="${count < 3}">
										<c:set var="delta" value="${2-count}"/>
									</c:when>
									<c:when test="${count > 21}">
										<c:set var="delta" value="${24-count}"/>
									</c:when>
									<c:otherwise>
										<c:set var="delta" value="1"/>
									</c:otherwise>
								</c:choose>
								<c:forEach begin="${count}" end="${count+delta}">
									<tr id="dnRFCDescriptionRow_a${count}">
										<td>
											<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'RFC');" value="" class="formfieldXL bw2 h15">
										</td>
									</tr>
									<tr id="dnRFCDescriptionRow_b${count}">
										<td>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCName${count}"/></jsp:include></p>
											<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCDescriptionError${count}"/></jsp:include></p>
										</td>
									</tr>
									<tr id="dnRFCDescriptionRow_c${count}">
										<td>
											<fmt:message key="visibility" bundle="${resword}"/>:
											<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
											<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
											<br></br>
										</td>
									</tr>
									<c:set var="count" value="${count+1}"/>
								</c:forEach>
								<c:if test="${count <= 24}">
									<c:forEach begin="${count}" end="24">
										<tr style="display: none" id="dnRFCDescriptionRow_a${count}">
											<td>
												<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'RFC');" value="" class="formfieldXL bw2 h15">
											</td>
										</tr>
										<tr style="display: none" id="dnRFCDescriptionRow_b${count}">
											<td>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCName${count}"/></jsp:include></p>
												<p><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dnRFCDescriptionError${count}"/></jsp:include></p>
											</td>
										</tr>
										<tr style="display: none" id="dnRFCDescriptionRow_c${count}">
											<td>
												<fmt:message key="visibility" bundle="${resword}"/>:
												<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon();" name="dnRFCVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
												<br/><br/>
											</td>
										</tr>
										<c:set var="count" value="${count+1}"/>
									</c:forEach>
								</c:if>
							</table>
						</td>
					</tr>
				</table>
			</div></div></div></div></div></div></div></div></div>
	</div>
</div>
</div>
<!-- end of section f -->

<a href="javascript:leftnavExpand('section7');">
	<img id="excl_section7" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">
    <fmt:message key="study_parameter_configuration" bundle="${resword}"/></span></a>
<div id="section7" style="display:none ">
<div style="width: 670px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table border="0" cellpadding="4" cellspacing="0" id="study_param_config">

<c:if test="${userRole.id == 1 || userRole.id == 2}">
	<c:set var="mode" value="${userRole.id == 2 ? 'disabled' : '' }"/>
	
	<tr valign="top" style="width: 100%;">
		<td class="formlabel" style="text-align: left;">
			<fmt:message key="features" bundle="${resword}"/>:
		</td>
		<td style="text-align: left;">
			&nbsp;
		</td>
	</tr>

	<c:forEach items="${studyFeatures}" var="studyFeature" varStatus="studyFeaturesStatus">
		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="${studyFeature.code}" bundle="${resword}"/>?
			</td>
			<td>
				<c:choose>
					<c:when test="${studyToView.studyParameterConfig[studyFeature.name] == 'yes'}">
						<input type="radio" checked name="${studyFeature.name}" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
						<input type="radio" ${mode} name="${studyFeature.name}" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
					</c:when>
					<c:otherwise>
						<input type="radio" ${mode} name="${studyFeature.name}" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
						<input type="radio" checked name="${studyFeature.name}" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</c:forEach>

</c:if>

<span id="autoGeneratedPrefixErrorMessage" class="hidden"><fmt:message bundle="${reserrors}" key="auto_generated_subject_id_prefix_message"/></span>
<span id="autoGeneratedSeparatorErrorMessage" class="hidden"><fmt:message bundle="${reserrors}" key="auto_generated_subject_id_separator_message"/></span>

<c:forEach items="${studyConfigurationParameters}" var="studyConfigurationParameter" varStatus="studyConfigurationParameterStatus">
	<c:choose>
		<c:when test="${studyConfigurationParameter.skip}"></c:when>
		<c:when test="${studyConfigurationParameter.type == 'DYNAMIC_LABEL'}">
			<tr valign="top" class="${studyConfigurationParameter.rowClassName}">
				<td class="formlabel"><fmt:message key="${studyConfigurationParameter.code}" bundle="${resword}"/>:</td>
				<td>
					<span id="${studyConfigurationParameter.name}" objectsSelector="${studyConfigurationParameter.objectsSelector}"></span>
					<script>buildDynamicLabel('${studyConfigurationParameter.name}');</script>
				</td>
			</tr>
		</c:when>
		<c:when test="${studyConfigurationParameter.type == 'GROUP'}">
			<c:if test="${studyConfigurationParameterStatus.index > 0}">
				<tr>
					<td>&nbsp;</td>
				</tr>
			</c:if>
			<tr valign="top" style="border: 1px solid black;width: 100%;">
				<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
					<fmt:message key="${studyConfigurationParameter.code}" bundle="${resword}"/>:
				</td>
				<td style="border-top: 1px solid black; text-align: left;">
					&nbsp;
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr valign="top" class="${studyConfigurationParameter.rowClassName}">
				<td class="formlabel"><fmt:message key="${studyConfigurationParameter.code}" bundle="${resword}"/></td>
				<td>
					<c:set var="jsFunction" value=""/>
					<c:set var="selectedOption" value=""/>
					<c:set var="jsFunctionOnLoad" value=""/>
					<c:set var="dynamicLabelBuilder" value=""/>
					<c:choose>
						<c:when test="${studyConfigurationParameter.type == 'TEXT'}">
							<input type="text" name="${studyConfigurationParameter.name}" onchange="changeIcon()" value="${studyToView.studyParameterConfig[studyConfigurationParameter.name]}" maxlength="${studyConfigurationParameter.maxLength}" size="35">
							<br/>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="${studyConfigurationParameter.name}"/></jsp:include>
						</c:when>
						<c:when test="${studyConfigurationParameter.type == 'RADIO'}">
							<c:if test="${studyConfigurationParameter.dependentRowsClassName ne null and studyConfigurationParameter.hideDependentRowsIfSelectedValueIs ne null}">
								<c:set var="jsFunction" value="hideStudyParameterRowsForValue($('#studyConfigurationParameter-${studyConfigurationParameterStatus.index}:checked'), '${studyConfigurationParameter.dependentRowsClassName}', '${studyConfigurationParameter.hideDependentRowsIfSelectedValueIs}');"/>
								<c:set var="jsFunctionOnLoad" value="hideStudyParameterRowsForValue($('#studyConfigurationParameter-${studyConfigurationParameterStatus.index}:checked'), '${studyConfigurationParameter.dependentRowsClassName}', '${studyConfigurationParameter.hideDependentRowsIfSelectedValueIs}');"/>
							</c:if>
							<c:set var="additionalAttribute" value="${studyConfigurationParameter.disabled ? 'disabled' : ''}"/>
							<c:forEach items="${studyConfigurationParameter.values}" var="studyConfigurationParameterValue" varStatus="studyConfigurationParameterValueStatus">
								<input ${additionalAttribute} type="radio" id="studyConfigurationParameter-${studyConfigurationParameterStatus.index}" onchange="changeIcon()" onclick="${jsFunction}"
									${studyToView.studyParameterConfig[studyConfigurationParameter.name] == studyConfigurationParameterValue ? "checked" : ""}
									   name="${studyConfigurationParameter.name}" value="${studyConfigurationParameterValue}"><fmt:message key="${studyConfigurationParameter.valueCodes[studyConfigurationParameterValueStatus.index]}" bundle="${resword}"/>
							</c:forEach>
						</c:when>
						<c:when test="${studyConfigurationParameter.type == 'SELECT'}">
							<c:if test="${studyConfigurationParameter.additionalStudyConfigurationParameter ne null and studyConfigurationParameter.showAdditionalStudyConfigurationParameterIfSelectedValueIs ne null}">
								<c:set var="jsFunction" value="additionalStudyParameterHandler(${studyConfigurationParameterStatus.index}, '${studyConfigurationParameter.showAdditionalStudyConfigurationParameterIfSelectedValueIs}', true);"/>
								<c:set var="jsFunctionOnLoad" value="additionalStudyParameterHandler(${studyConfigurationParameterStatus.index}, '${studyConfigurationParameter.showAdditionalStudyConfigurationParameterIfSelectedValueIs}');"/>
							</c:if>
							<c:if test="${studyConfigurationParameter.dynamicLabelId ne null}">
								<c:set var="dynamicLabelBuilder" value="buildDynamicLabel('${studyConfigurationParameter.dynamicLabelId}');"/>
							</c:if>
							<select id="studyConfigurationParameter-${studyConfigurationParameterStatus.index}" processorMode="${studyConfigurationParameter.processorMode}" class="formfieldS single_select_property" name="${studyConfigurationParameter.ignoreName ? '' : studyConfigurationParameter.name}" onchange="changeIcon(); ${jsFunction} ${dynamicLabelBuilder}">
								<c:forEach items="${studyConfigurationParameter.values}" var="studyConfigurationParameterValue" varStatus="studyConfigurationParameterValueStatus">
									<c:set var="additionalAttribute" value=""/>
									<c:if test="${studyToView.studyParameterConfig[studyConfigurationParameter.name] == studyConfigurationParameterValue}">
										<c:set var="additionalAttribute" value="selected"/>
										<c:set var="selectedOption" value="${studyConfigurationParameterValue}"/>
									</c:if>
									<option value="${studyConfigurationParameterValue}" ${additionalAttribute}> <fmt:message key="${studyConfigurationParameter.valueCodes[studyConfigurationParameterValueStatus.index]}" bundle="${resword}"/> </option>
								</c:forEach>
							</select>
							<c:if test="${empty selectedOption and studyConfigurationParameter.selectValueIfThereIsNothingIsSelected ne null}">
								<c:set var="selectedOption" value="${studyConfigurationParameter.selectValueIfThereIsNothingIsSelected}"/>
								<script>$("#studyConfigurationParameter-${studyConfigurationParameterStatus.index} option[value=${studyConfigurationParameter.selectValueIfThereIsNothingIsSelected}]").attr("selected", true);</script>
							</c:if>
							<c:set var="additionalStudyConfigurationParameterClass" value="hidden"/>
							<c:if test="${selectedOption == studyConfigurationParameter.showAdditionalStudyConfigurationParameterIfSelectedValueIs}">
								<c:set var="additionalStudyConfigurationParameterClass" value=""/>
							</c:if>
							<c:if test="${studyConfigurationParameter.additionalStudyConfigurationParameter ne null && studyConfigurationParameter.additionalStudyConfigurationParameter.type == 'TEXT'}">
								<input type="text" id="additionalStudyConfigurationParameter-${studyConfigurationParameterStatus.index}" name="${studyConfigurationParameter.additionalStudyConfigurationParameter.name}" value="${studyToView.studyParameterConfig[studyConfigurationParameter.name]}" onchange="changeIcon(); ${dynamicLabelBuilder}" maxlength="${studyConfigurationParameter.additionalStudyConfigurationParameter.maxLength}" class="w150 ${additionalStudyConfigurationParameterClass}"/>
								<br>
								<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="${studyConfigurationParameter.additionalStudyConfigurationParameter.name}"/></jsp:include>
							</c:if>
						</c:when>
					</c:choose>
					<script>
						$(window).load(function() {
							${jsFunctionOnLoad}
						});
					</script>
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
	<c:if test="${studyConfigurationParameterStatus.last}">
		<tr>
			<td>&nbsp;</td>
		</tr>
	</c:if>
</c:forEach>

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
</div>
<br>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage"
			       value="<fmt:message key="back" bundle="${resword}"/>"
			       class="button_medium medium_back"
			       onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
		</td>
		<td>
			<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit">
		</td>
		<td>
			<img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
		</td>
	</tr>
</table>
</form>

<br><br><br>
<c:import url="../include/workflow.jsp">
	<c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
