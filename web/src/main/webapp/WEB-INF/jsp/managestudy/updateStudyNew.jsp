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
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<jsp:useBean scope ="request" id="studyPhaseMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="studyTypes" class="java.util.ArrayList"/>

<jsp:useBean scope ="request" id="interPurposeMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="allocationMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="maskingMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="controlMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="assignmentMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="endpointMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="interTypeMap" class="java.util.HashMap"/>
<jsp:useBean scope ="request" id="interventions" class="java.util.ArrayList"/>
<jsp:useBean scope ="request" id="interventionError" class="java.lang.String"/>

<jsp:useBean scope="request" id="obserPurposeMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="durationMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="selectionMap" class ="java.util.HashMap"/>
<jsp:useBean scope="request" id="timingMap" class ="java.util.HashMap"/>
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
<c:set var="protocolDateVerification" value="" />
<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "protocolDateVerification"}'>
		<c:set var="protocolDateVerification" value="${presetValue.value}" />
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
		<input type="text" name="uniqueProId" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.identifier}"/>" class="formfieldXL bw2 h15">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td>
	<td width="10%" class="alert">
		*
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<b><fmt:message key="brief_title" bundle="${resword}"/></b>:
	</td>
	<td>
		<input type="text" name="name" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.name}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
	</td>
	<td class="alert">
		*
	</td>
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
		<fmt:message key="principal_investigator" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="prinInvestigator" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.principalInvestigator}"/>" class="formfieldXL h15 bw2">
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include>
	</td>
	<td class="alert">*</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary'); return false;">
			<fmt:message key="brief_summary" bundle="${resword}"/>:
		</a>
	</td>
	<td>
		<textarea class="formtextareaXL4 bw1 maxw250" name="description" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.summary}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
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
		<textarea class="formtextareaXL4 bw1 maxw250" name="protocolDescription" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.protocolDescription}"/></textarea>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolDescription"/></jsp:include>
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

<!-- section B-->
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="study_phase" bundle="${resword}"/>:
	</td>
	<td>
		<c:set var="phase1" value="${studyToView.phase}"/>
		<select name="phase" class="formfieldXL h20 bw1 w250" onchange="javascript:changeIcon()">
			<c:forEach var="phase" items="${studyPhaseMap}">
			<c:set var="phasekey">
				<fmt:message key="${phase.key}" bundle="${resadmin}"/>
			</c:set>
			<c:choose>
			<c:when test="${phase1 == phasekey}">
			<option value="<c:out value="${phase.key}"/>" selected><c:out value="${phase.value}"/>
				</c:when>
				<c:otherwise>
			<option value="<c:out value="${phase.key}"/>"><c:out value="${phase.value}"/>
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
				<input type="radio" onchange="javascript:changeIcon()" checked name="protocolType" value="observational" disabled><fmt:message key="observational" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" checked name="protocolType" value="interventional" disabled><fmt:message key="interventional" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
	<td>&nbsp;</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<a href="http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#VerificationDate'); return false;"><fmt:message key="protocol_verification" bundle="${resword}"/>:</a>
	</td>
	<td>
		<input type="text" name="protocolDateVerification" onchange="javascript:changeIcon()" value="<c:out value="${protocolDateVerification}"/>" class="formfieldXL bw2 h15" id="protocolDateVerificationField">
		<br>
		<jsp:include page="../showMessage.jsp">
			<jsp:param name="key" value="protocolDateVerification"/>
		</jsp:include>
	</td>
	<td>
		<ui:calendarIcon onClickSelector="'#protocolDateVerificationField'"/>
	</td>
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

<!-- From Update Page 3 -->

<c:if test='${isInterventional==1}'>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntPurpose" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntPurpose'); return false;">
				<fmt:message key="purpose" bundle="${resword}"/></a>:
		</td>
		<td>
			<c:set var="purpose1" value="${studyToView.purpose}"/>
			<select name="purpose" class="formfieldXL bw1 h20 w250" onchange="javascript:changeIcon()">
				<c:forEach var="purpose" items="${interPurposeMap}">
				<c:set var="purposekey">
					<fmt:message key="${purpose.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${purpose1 == purposekey}">
				<option value="<c:out value="${purpose.key}"/>" selected><c:out value="${purpose.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${purposekey}"/>"><c:out value="${purpose.value}"/>
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
			<c:set var="allocation1" value="${studyToView.allocation}"/>
			<select name="allocation" class="formfieldXL w250 h20 bw1" onchange="javascript:changeIcon()">
				<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
				<c:forEach var="allocation" items="${allocationMap}">
				<c:set var="allocationkey">
					<fmt:message key="${allocation.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${allocation1 == allocationkey}">
				<option value="<c:out value="${allocation.key}"/>" selected><c:out value="${allocation.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${allocation.key}"/>"><c:out value="${allocation.value}"/>
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
			<c:set var="masking1" value="${studyToView.masking}"/>
			<select name="masking" class="formfieldXL w250 h20 bw1" onchange="javascript:changeIcon()">
				<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
				<c:forEach var="masking" items="${maskingMap}">
				<c:set var="maskingkey">
					<fmt:message key="${masking.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${masking1 == maskingkey}">
				<option value="<c:out value="${masking.key}"/>" selected><c:out value="${masking.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${masking.key}"/>"><c:out value="${masking.value}"/>
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
			<c:set var="control1" value="${studyToView.control}"/>
			<select name="control" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
				<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
				<c:forEach var="control" items="${controlMap}">
				<c:set var="controlkey">
					<fmt:message key="${control.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${control1 == controlkey}">
				<option value="<c:out value="${control.key}"/>" selected><c:out value="${control.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${control.key}"/>"><c:out value="${control.value}"/>
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
				<%-- was assignment, tbh --%>
			<c:set var="assignment1" value="${studyToView.assignment}"/>
			<select name="assignment" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
				<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
				<c:forEach var="assignment" items="${assignmentMap}">
				<c:set var="assignmentkey">
					<fmt:message key="${assignment.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${assignment1 == assignmentkey}">
				<option value="<c:out value="${assignment.key}"/>" selected><c:out value="${assignment.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${assignment.key}"/>"><c:out value="${assignment.value}"/>
					</c:otherwise>
					</c:choose>
					</c:forEach>
			</select>
		</td>
	</tr>
	<tr valign="top">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints'); return false;">
				<fmt:message key="study_classification" bundle="${resword}"/></a>:</td><td>
			<%-- was endpoint, tbh --%>
		<c:set var="endpoint1" value="${studyToView.endpoint}"/>
		<select name="endpoint" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
			<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
			<c:forEach var="endpoint" items="${endpointMap}">
			<c:set var="endpointkey">
				<fmt:message key="${endpoint.key}" bundle="${resadmin}"/>
			</c:set>
			<c:choose>
			<c:when test="${endpoint1 == endpointkey}">
			<option value="<c:out value="${endpoint.key}"/>" selected><c:out value="${endpoint.value}"/>
				</c:when>
				<c:otherwise>
			<option value="<c:out value="${endpoint.key}"/>"><c:out value="${endpoint.value}"/>
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
			<c:set var="purpose1" value="${studyToView.purpose}"/>
			<select name="purpose" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
				<c:forEach var="purpose" items="${obserPurposeMap}">
				<c:set var="purposekey">
					<fmt:message key="${purpose.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${purpose1 == purposekey}">
				<option value="<c:out value="${purpose.key}"/>" selected><c:out value="${purpose.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${purpose.key}"/>"><c:out value="${purpose.value}"/>
					</c:otherwise>
					</c:choose>
					</c:forEach>
			</select>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include>
		</td>
		<td valign="top" class="alert">
			*
		</td>
	</tr>

	<tr valign="bottom">
		<td class="formlabel">
			<fmt:message key="duration" bundle="${resword}"/>:
		</td>
		<td class="pb10">
			<c:set var="longitudinal">
				<fmt:message key="longitudinal" bundle="${resadmin}"/>
			</c:set>
			<c:choose>
				<c:when test="${studyToView.duration ==longitudinal}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="duration" value="longitudinal"><fmt:message key="longitudinal" bundle="${resword}"/>:
					<input type="radio" onchange="javascript:changeIcon()" name="duration" value="cross-sectional"><fmt:message key="cross_sectional" bundle="${resword}"/>:
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="duration" value="longitudinal"><fmt:message key="longitudinal" bundle="${resword}"/>:
					<input type="radio" onchange="javascript:changeIcon()" checked name="duration" value="cross-sectional"><fmt:message key="cross_sectional" bundle="${resword}"/>:
				</c:otherwise>
			</c:choose>
		</td>
		<td>&nbsp;</td>
	</tr>

	<tr valign="bottom">
		<td class="formlabel">
			<a href="http://prsinfo.clinicaltrials.gov/definitions.html#EligibilitySamplingMethod" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#EligibilitySamplingMethod'); return false;">
				<fmt:message key="selection" bundle="${resword}"/></a>
		</td>
		<td>
			<c:set var="selection1" value="${studyToView.selection}"/>
			<select name="selection" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
				<c:forEach var="selection" items="${selectionMap}">
				<c:set var="selectionkey">
					<fmt:message key="${selection.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${selection1 == selectionkey}">
				<option value="<c:out value="${selection.key}"/>" selected><c:out value="${selection.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${selection.key}"/>"><c:out value="${selection.value}"/>
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
			<c:set var="timing1" value="${studyToView.timing}"/>
			<select name="timing" class="formfieldXL bw1 w250 h20" onchange="javascript:changeIcon()">
				<c:forEach var="timing" items="${timingMap}">
				<c:set var="timingkey">
					<fmt:message key="${timing.key}" bundle="${resadmin}"/>
				</c:set>
				<c:choose>
				<c:when test="${timing1 == timingkey}">
				<option value="<c:out value="${timing.key}"/>" selected><c:out value="${timing.value}"/>
					</c:when>
					<c:otherwise>
				<option value="<c:out value="${timing.key}"/>"><c:out value="${timing.value}"/>
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
<br>

<a href="javascript:leftnavExpand('section3');">
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

					<tr valign="top">
						<td class="formlabel">
							<fmt:message key="expected_total_enrollment" bundle="${resword}"/>:
						</td>
						<td>
							<input type="text" name="expectedTotalEnrollment" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.expectedTotalEnrollment}"/>" class="formfieldXL bw2 h15">
							<br>
							<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="expectedTotalEnrollment"/></jsp:include>
						</td>
						<td class="alert">*</td>
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


<!-- Features Parameters section -->

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

	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="crf_annotation" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.crfAnnotation == 'yes'}">
					<input type="radio" checked name="crfAnnotation" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="crfAnnotation" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="crfAnnotation" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="crfAnnotation" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="dynamic_group" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.dynamicGroup == 'yes'}">
					<input type="radio" checked name="dynamicGroup" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="dynamicGroup" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="dynamicGroup" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="dynamicGroup" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="calendared_visits" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.calendaredVisits == 'yes'}">
					<input type="radio" checked name="calendaredVisits" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="calendaredVisits" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="calendaredVisits" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="calendaredVisits" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="interactive_dashboards" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.interactiveDashboards == 'yes'}">
					<input type="radio" checked name="interactiveDashboards" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="interactiveDashboards" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="interactiveDashboards" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="interactiveDashboards" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="item_level_sdv" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.itemLevelSDV == 'yes'}">
					<input type="radio" checked name="itemLevelSDV" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="itemLevelSDV" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="itemLevelSDV" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="itemLevelSDV" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="subject_casebook_in_pdf" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.subjectCasebookInPDF == 'yes'}">
					<input type="radio" checked name="subjectCasebookInPDF" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="subjectCasebookInPDF" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="subjectCasebookInPDF" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="subjectCasebookInPDF" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="crfs_masking" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.crfMasking == 'yes'}">
					<input type="radio" checked name="crfMasking" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="crfMasking" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="crfMasking" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="crfMasking" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="sas_extracts" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.sasExtracts == 'yes'}">
					<input type="radio" checked name="sasExtracts" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="sasExtracts" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="sasExtracts" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="sasExtracts" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="study_evaluator" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.studyEvaluator == 'yes'}">
					<input type="radio" checked name="studyEvaluator" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="studyEvaluator" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="studyEvaluator" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="studyEvaluator" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="randomization_cap" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.randomization == 'yes'}">
					<input type="radio" checked name="randomization" value="yes" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="randomization"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="randomization" value="no" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="randomization"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="randomization" value="yes" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="randomization"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="randomization" value="no" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="randomization"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr valign="top">
	    <td class="formlabel">
	        <fmt:message key="medical_coding" bundle="${resword}"/>?
	    </td>
	    <td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.medicalCoding == 'yes'}">
					<input type="radio" checked name="medicalCoding" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" ${mode} name="medicalCoding" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" ${mode} name="medicalCoding" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="medicalCoding" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

</c:if>

<!-- /Features Parameters section -->

<tr valign="top" style="border: 1px solid black;width: 100%;">
    <td class="formlabel" style="border-top: 1px solid black;text-align: left; width:170px">
        <fmt:message key="studyParameters" bundle="${resword}"/>:
    </td>
    <td style=" border-top: 1px solid black; text-align: left;">
        &nbsp;
    </td>
</tr>
	
<tr valign="top">
	<td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/></td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.discrepancyManagement == 'true' ? "checked" : ""}
		       name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.discrepancyManagement == 'false' ? "checked" : ""}
		       name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="forced_reason_for_change" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.adminForcedReasonForChange == 'true' ? "checked" : ""}
		       name="adminForcedReasonForChange" value="true"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.adminForcedReasonForChange == 'false' ? "checked" : ""}
		       name="adminForcedReasonForChange" value="false"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="allowSdvWithOpenQueries" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowSdvWithOpenQueries == 'yes' ? "checked" : ""}
		       name="allowSdvWithOpenQueries" value="yes"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowSdvWithOpenQueries == 'no' ? "checked" : ""}
		       name="allowSdvWithOpenQueries" value="no"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="allowDynamicGroupsManagement" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowDynamicGroupsManagement == 'yes' ? "checked" : ""}
		       name="allowDynamicGroupsManagement" value="yes"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowDynamicGroupsManagement == 'yes' ? "" : "checked"}
		       name=allowDynamicGroupsManagement value="no"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="dcf_allow_discrepancy_correction_forms" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowDiscrepancyCorrectionForms == 'yes' ? "checked" : ""}
		       name="allowDiscrepancyCorrectionForms" value="yes"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.allowDiscrepancyCorrectionForms == 'yes' ? "" : "checked"}
		       name=allowDiscrepancyCorrectionForms value="no"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="instanceTupe">
	<td class="formlabel"><fmt:message key="systemProperty.instanceType.label" bundle="${resword}"/>:</td>
	<td>
		<select class="formfieldS single_select_property instanceType" name="instanceType" onchange="javascript:changeIcon(); updateInstanceType();">
			<option value="development" ${studyToView.studyParameterConfig.instanceType == 'development' ? 'selected' : ''}> <fmt:message key="systemProperty.instanceType.development.radioLabel" bundle="${resword}"/> </option>
			<option value="training" ${studyToView.studyParameterConfig.instanceType == 'training' ? 'selected' : ''}> <fmt:message key="systemProperty.instanceType.training.radioLabel" bundle="${resword}"/> </option>
			<option value="production" ${studyToView.studyParameterConfig.instanceType == 'production' ? 'selected' : ''}> <fmt:message key="systemProperty.instanceType.production.radioLabel" bundle="${resword}"/> </option>
			<option value="other" ${studyToView.studyParameterConfig.instanceType != 'production' && studyToView.studyParameterConfig.instanceType != 'training' && studyToView.studyParameterConfig.instanceType != 'development' ? 'selected' : ''}> <fmt:message key="other" bundle="${resword}"/> </option>
		</select>
		<input type="text" name="instanceTypeNotUsed" value="${studyToView.studyParameterConfig.instanceType}" onchange="javascript:changeIcon(); updateInstanceType();" maxlength="20" style="display: none" class="w150 instanceTypeText"/>
		<br>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="instanceType"/></jsp:include>
		<script>
			$(window).load(function() {
				updateInstanceType();
			});
		</script>
	</td>
</tr>
<!-- Study parameters section end -->
<tr>
	<td>&nbsp;</td>
</tr>
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="subjectParameters" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">
		&nbsp;
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="how_to_generate_the_study_subject_ID" bundle="${resword}"/>:
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.subjectIdGeneration == 'manual' ? "checked" : ""}
		       name="subjectIdGeneration" value="manual" onclick="hideUnhideStudyParamRow(this);"
		       data-cc-action="hide" data-row-class="autoGeneratedFormat">
		<fmt:message key="manual_entry" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.subjectIdGeneration == 'auto editable' ? "checked" : ""}
		       name="subjectIdGeneration" value="auto editable" onclick="hideUnhideStudyParamRow(this);"
		       data-cc-action="show" data-row-class="autoGeneratedFormat">
		<fmt:message key="auto_generated_and_editable" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.subjectIdGeneration == 'auto non-editable' ? "checked" : ""}
		       name="subjectIdGeneration" value="auto non-editable" onclick="hideUnhideStudyParamRow(this);"
		       data-cc-action="show" data-row-class="autoGeneratedFormat">
		<fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
	</td>
</tr>


<tr valign="top" class="autoGeneratedFormat">
	<td class="formlabel"><fmt:message key="auto_generated_prefix" bundle="${resword}"/>:</td>
	<td>
		<select class="formfieldXS single_select_property" onchange="javascript:changeIcon(); updateGeneratedSubjectId();" style="width: 110px;" id="generatedIdPrefix">
			<option value="0" ${studyToView.studyParameterConfig.autoGeneratedPrefix == '' ? 'selected' : ''}>
				<fmt:message bundle="${resword}" key="none"/>
			</option>
			<option value="1" ${studyToView.studyParameterConfig.autoGeneratedPrefix == 'SiteID' ? 'selected' : ''}>
				<fmt:message bundle="${resword}" key="site_id"/>
			</option>
			<option value="2" ${studyToView.studyParameterConfig.autoGeneratedPrefix != '' and studyToView.studyParameterConfig.autoGeneratedPrefix != 'SiteID' ? 'selected' : ''}>
				<fmt:message bundle="${resword}" key="custom"/>
			</option>
		</select>
		<input type="text" value="${studyToView.studyParameterConfig.autoGeneratedPrefix}" name="autoGeneratedPrefix" onchange="javascript:changeIcon(); updateGeneratedSubjectId();" maxlength="25"/>
		<br/>
		<span id="autoGeneratedPrefixMessage" class="alert"><fmt:message bundle="${reserrors}" key="auto_generated_subject_id_prefix_message"/></span>
	</td>
</tr>

<tr valign="top" class="autoGeneratedFormat">
	<td class="formlabel"><fmt:message key="auto_generated_separator" bundle="${resword}"/>:</td>
	<td>
		<select class="formfieldXS single_select_property" onchange="javascript:changeIcon(); updateGeneratedSubjectId();" style="width: 110px;" id="generatedIdSeparator">
			<option value="0" ${studyToView.studyParameterConfig.autoGeneratedSeparator == '' ? 'selected' : ''}>
				<fmt:message bundle="${resword}" key="none"/>
			</option>
			<option value="1" ${studyToView.studyParameterConfig.autoGeneratedSeparator != '' ? 'selected' : ''}>
				<fmt:message bundle="${resword}" key="custom"/>
			</option>
		</select>
		<input type="text" value="${studyToView.studyParameterConfig.autoGeneratedSeparator}" name="autoGeneratedSeparator" onchange="javascript:changeIcon(); updateGeneratedSubjectId();" maxlength="2" style="width: 20px"/>
		<br>
		<span id="autoGeneratedSeparatorMessage" class="alert"><fmt:message bundle="${reserrors}" key="auto_generated_subject_id_separator_message"/><span>
	</td>
</tr>

<tr valign="top" class="autoGeneratedFormat">
	<td class="formlabel"><fmt:message key="auto_generated_suffix" bundle="${resword}"/>:</td>
	<td>
		<select class="formfieldXS single_select_property" name="autoGeneratedSuffix" onchange="javascript:changeIcon(); updateGeneratedSubjectId();">
			<option value="3" ${studyToView.studyParameterConfig.autoGeneratedSuffix == '3' ? 'selected' : ''}> 3 </option>
			<option value="4" ${studyToView.studyParameterConfig.autoGeneratedSuffix == '4' ? 'selected' : ''}> 4 </option>
			<option value="5" ${studyToView.studyParameterConfig.autoGeneratedSuffix == '5' ? 'selected' : ''}> 5 </option>
			<option value="6" ${studyToView.studyParameterConfig.autoGeneratedSuffix == '6' ? 'selected' : ''}> 6 </option>
		</select>
	</td>
</tr>

<script>
	$(window).load(function() {
		updateGeneratedSubjectId();
	});
</script>

<tr valign="top" class="autoGeneratedFormat">
	<td class="formlabel"><fmt:message key="subject_ID" bundle="${resword}"/>:</td>
	<td>
		<span id="generated_id"></span>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="studySubjectIdLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="studySubjectIdLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.studySubjectIdLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubjectIdLabel"/></jsp:include>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="secondaryIDRequired" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" name="secondaryIdRequired"
		${studyToView.studyParameterConfig.secondaryIdRequired == 'yes' ? "checked" : ""}
		       value="yes" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="secondaryId">
		<fmt:message key="required" bundle="${resword}"/>

		<input type="radio" name="secondaryIdRequired"
		${studyToView.studyParameterConfig.secondaryIdRequired == 'no' ? "checked" : ""}
		       value="no" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="secondaryId">
		<fmt:message key="optional" bundle="${resword}"/>
		<input type="radio" name="secondaryIdRequired"
		${studyToView.studyParameterConfig.secondaryIdRequired == 'not_used' ? "checked" : ""}
		       value="not_used" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="secondaryId"">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr id="secondaryIdRow" valign="top" class="secondaryId">
	<td class="formlabel">
		<fmt:message key="secondaryIdLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="secondaryIdLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.secondaryIdLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryIdLabel"/></jsp:include>
	</td>
</tr>
<tr id="secondaryViewableRow" valign="top" class="secondaryId">
	<td class="formlabel"><fmt:message key="secondary_label_viewable" bundle="${resword}"/></td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.secondaryLabelViewable== 'true' ? "checked" : ""}
		       name="secondaryLabelViewable" value="true"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.secondaryLabelViewable== 'false' ? "checked" : ""}
		       name="secondaryLabelViewable" value="false"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="dateOfEnrollmentForStudyRequired" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" name="dateOfEnrollmentForStudyRequired"
		${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'yes' ? "checked" : ""}
		       value="yes" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="dateOfEnrollment">
		<fmt:message key="required" bundle="${resword}"/>
		<input type="radio" name="dateOfEnrollmentForStudyRequired"
		${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'no' ? "checked" : ""}
		       value="no" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="dateOfEnrollment">
		<fmt:message key="optional" bundle="${resword}"/>
		<input type="radio" name="dateOfEnrollmentForStudyRequired"
		${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used' ? "checked" : ""}
		       value="not_used" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="dateOfEnrollment">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="dateOfEnrollment">
	<td class="formlabel">
		<fmt:message key="dateOfEnrollmentForStudyLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="dateOfEnrollmentForStudyLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.dateOfEnrollmentForStudyLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dateOfEnrollmentForStudyLabel"/></jsp:include>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="gender_required2" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" name="genderRequired"
		${studyToView.studyParameterConfig.genderRequired == 'true' ? "checked" : ""}
		       value="true" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="gender">
		<fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" name="genderRequired"
		${studyToView.studyParameterConfig.genderRequired == 'false' ? "checked" : ""}
		       value="false" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="gender">
		<fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="gender">
	<td class="formlabel">
		<fmt:message key="genderLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="genderLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.genderLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="genderLabel"/></jsp:include>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/></td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.collectDob == '1' ? "checked" : ""}
		       name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.collectDob == '2' ? "checked" : ""}
		       name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.collectDob == '3' ? "checked" : ""}
		       name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="subject_person_ID_required" bundle="${resword}"/>
	</td>
	<td>
		<c:set var="not_used" value="not used"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="personId"
		${studyToView.studyParameterConfig.subjectPersonIdRequired == 'required' ? "checked" : ""}
		       name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="personId"
		${studyToView.studyParameterConfig.subjectPersonIdRequired == 'optional' ? "checked" : ""}
		       name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="personId"
		${studyToView.studyParameterConfig.subjectPersonIdRequired == 'copyFromSSID' ? "checked" : ""}
		       name="subjectPersonIdRequired" value="copyFromSSID"><fmt:message key="copy_from_ssid" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="personId"
		${studyToView.studyParameterConfig.subjectPersonIdRequired == not_used ? "checked" : ""}
		       name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>

<tr valign="top" class="personId">
	<td class="formlabel">
		<fmt:message key="show_person_id_on_crf_header" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.personIdShownOnCRF == 'true' ? "checked" : ""}
		       name="personIdShownOnCRF" value="true"><fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.personIdShownOnCRF == 'false' ? "checked" : ""}
		       name="personIdShownOnCRF" value="false"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>


<tr>
	<td>&nbsp;</td>
</tr>
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="eventParameters" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">
		&nbsp;
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="event_location_required" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.eventLocationRequired== 'required' ? "checked" : ""}
		       name="eventLocationRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.eventLocationRequired== 'optional' ? "checked" : ""}
		       name="eventLocationRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon()"
		${studyToView.studyParameterConfig.eventLocationRequired== 'not_used' ? "checked" : ""}
		       name="eventLocationRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="startDateTimeRequired" bundle="${resword}"/>
	</td>
	<td>
		<input class="some-class" type="radio" name="startDateTimeRequired" value="yes"
		${studyToView.studyParameterConfig.startDateTimeRequired == 'yes' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="startDate">
		<fmt:message key="required" bundle="${resword}"/>
		<input type="radio" name="startDateTimeRequired" value="no"
		${studyToView.studyParameterConfig.startDateTimeRequired == 'no' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="startDate">
		<fmt:message key="optional" bundle="${resword}"/>
		<input type="radio" name="startDateTimeRequired" value="not_used"
		${studyToView.studyParameterConfig.startDateTimeRequired == 'not_used' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="startDate">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="startDate">
	<td class="formlabel">
		<fmt:message key="useStartTime" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" checked name="useStartTime" value="yes"
		${studyToView.studyParameterConfig.useStartTime == 'yes' ? "checked" : ""}
		       onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" name="useStartTime" value="no"
		${studyToView.studyParameterConfig.useStartTime == 'no' ? "checked" : ""}
		       onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="startDate">
	<td class="formlabel">
		<fmt:message key="startDateTimeLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="startDateTimeLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.startDateTimeLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDateTimeLabel"/></jsp:include>
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="endDateTimeRequired" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" checked name="endDateTimeRequired" value="yes"
		${studyToView.studyParameterConfig.endDateTimeRequired == 'yes' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="stopDate">
		<fmt:message key="required" bundle="${resword}"/>

		<input type="radio" name="endDateTimeRequired" value="no"
		${studyToView.studyParameterConfig.endDateTimeRequired == 'no' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="stopDate">
		<fmt:message key="optional" bundle="${resword}"/>

		<input type="radio" name="endDateTimeRequired" value="not_used"
		${studyToView.studyParameterConfig.endDateTimeRequired == 'not_used' ? "checked" : ""}
		       onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="stopDate">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="stopDate">
	<td class="formlabel">
		<fmt:message key="useEndTime" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" checked name="useEndTime" value="yes"
		${studyToView.studyParameterConfig.useEndTime == 'yes' ? "checked" : ""}
		       onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>

		<input type="radio" name="useEndTime" value="no"
		${studyToView.studyParameterConfig.useEndTime == 'no' ? "checked" : ""}
		       onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="stopDate">
	<td class="formlabel">
		<fmt:message key="endDateTimeLabel" bundle="${resword}"/>
	</td>
	<td>
		<input type="text" name="endDateTimeLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.endDateTimeLabel}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDateTimeLabel"/></jsp:include>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>

<!-- Data Entry Parameters section -->
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="dataEntryParameters" bundle="${resword}"/>:
	</td><td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewer"
		${studyToView.studyParameterConfig.interviewerNameRequired == 'yes' ? "checked" : ""}
		       name="interviewerNameRequired" value="yes">
		<fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewer"
		${studyToView.studyParameterConfig.interviewerNameRequired == 'no' ? "checked" : ""}
		       name="interviewerNameRequired" value="no">
		<fmt:message key="no" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="interviewer"
		${studyToView.studyParameterConfig.interviewerNameRequired == 'not_used' ? "checked" : ""}
		       name="interviewerNameRequired" value="not_used">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>

<tr valign="top" class="interviewer">
	<td class="formlabel">
		<fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.interviewerNameDefault== 'blank'}">
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewerNameDefault" value="pre-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewerNameDefault" value="re-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top" class="interviewer">
	<td class="formlabel">
		<fmt:message key="interviewer_name_editable" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.interviewerNameEditable== 'true'}">
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="interviewer_date_required" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewDate"
		${studyToView.studyParameterConfig.interviewDateRequired == 'yes' ? "checked" : ""}
		       name="interviewDateRequired" value="yes">
		<fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewDate"
		${studyToView.studyParameterConfig.interviewDateRequired == 'no' ? "checked" : ""}
		       name="interviewDateRequired" value="no">
		<fmt:message key="no" bundle="${resword}"/>
		<input type="radio" onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="interviewDate"
		${studyToView.studyParameterConfig.interviewDateRequired == 'not_used' ? "checked" : ""}
		       name="interviewDateRequired" value="not_used">
		<fmt:message key="not_used" bundle="${resword}"/>
	</td>
</tr>

<tr valign="top" class="interviewDate">
	<td class="formlabel">
		<fmt:message key="interviewer_date_default_as_blank" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.interviewDateDefault== 'blank'}">
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewDateDefault" value="pre-populated">
				<fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewDateDefault" value="re-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top" class="interviewDate">
	<td class="formlabel">
		<fmt:message key="interviewer_date_editable" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.interviewDateEditable== 'true'}">
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" checked name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
    <td class="formlabel">
        <fmt:message key="useAutoTabbing" bundle="${resword}"/>
    </td>
    <td>
        <input type="radio" onchange="javascript:changeIcon()"
        ${studyToView.studyParameterConfig.autoTabbing == 'yes' ? "checked" : ""}
               name="autoTabbing" value="yes"><fmt:message key="yes" bundle="${resword}"/>

        <input type="radio" onchange="javascript:changeIcon()"
        ${studyToView.studyParameterConfig.autoTabbing == 'no' ? "checked" : ""}
               name="autoTabbing" value="no"><fmt:message key="no" bundle="${resword}"/>
    </td>
</tr>
<!-- /Data Entry Parameters section -->

<!-- CRFs Parameters section -->
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="crfs_parameters" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">
		&nbsp;
	</td>
</tr>
<tr valign="top">
	<td class="formlabel">
		<fmt:message key="systemProperty.allowCrfEvaluation.label" bundle="${resword}"/>?
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.allowCrfEvaluation == 'yes'}">
				<input type="radio" checked name="allowCrfEvaluation" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="allowCrfEvaluation" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="allowCrfEvaluation" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="allowCrfEvaluation" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="systemProperty.evaluateWithContext.label" bundle="${resword}"/>?
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.evaluateWithContext == 'yes'}">
				<input type="radio" checked name="evaluateWithContext" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="evaluateWithContext" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="evaluateWithContext" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="evaluateWithContext" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="sas_name_annotation" bundle="${resword}"/>?
	</td>
	<td>
		<input type="radio" ${studyToView.studyParameterConfig.annotatedCrfSasItemNames == 'yes' ? 'checked' : ''} name="annotatedCrfSasItemNames" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" ${studyToView.studyParameterConfig.annotatedCrfSasItemNames == 'no' ? 'checked' : ''} name="annotatedCrfSasItemNames" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="show_years_in_calendar" bundle="${resword}"/>?
	</td>
	<td>
		<input type="radio" ${studyToView.studyParameterConfig.showYearsInCalendar == 'yes' ? 'checked' : ''} name="showYearsInCalendar" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" ${studyToView.studyParameterConfig.showYearsInCalendar == 'no' ? 'checked' : ''} name="showYearsInCalendar" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<!-- / CRFs Parameters section -->

<!-- Randomization Parameters section -->
<tr class="randomization">
	<td>&nbsp;</td>
</tr>
<tr  class="randomization" valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="randomization_parameters" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">
		&nbsp;
	</td>
</tr>

<tr class="randomization" valign="top">
	<td class="formlabel">
		<fmt:message key="assign_randomization_parameters_to" bundle="${resword}"/>:
	</td>
	<td>
		<input type="radio" name="assignRandomizationResultTo" ${studyToView.studyParameterConfig.assignRandomizationResultTo == 'dngroup' ? "checked" : ""} value="dngroup" onchange="javascript:changeIcon()">
		<fmt:message key="systemProperty.assignRandomizationResultTo.dngroup.radioLabel" bundle="${resword}"/>

		<input type="radio" name="assignRandomizationResultTo" ${studyToView.studyParameterConfig.assignRandomizationResultTo == 'ssid' ? "checked" : ""} value="ssid" onchange="javascript:changeIcon()">
		<fmt:message key="systemProperty.assignRandomizationResultTo.ssid.radioLabel" bundle="${resword}"/>

		<input type="radio" name="assignRandomizationResultTo" ${studyToView.studyParameterConfig.assignRandomizationResultTo == 'none' ? "checked" : ""} value="none" onchange="javascript:changeIcon()">
		<fmt:message key="systemProperty.assignRandomizationResultTo.none.radioLabel" bundle="${resword}"/>
	</td>
</tr>
<tr class="randomization" valign="top">
	<td class="formlabel">
		<fmt:message key="systemProperty.randomizationTrialId.label" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="randomizationTrialId" value="${studyToView.studyParameterConfig.randomizationTrialId}" maxlength="255" size="35">
	</td>
</tr>
<tr class="randomization" valign="top">
	<td class="formlabel">
		<fmt:message key="systemProperty.randomizationEnviroment.label" bundle="${resword}"/>:
	</td>
	<td>
		<input type="radio" name="randomizationEnviroment" ${studyToView.studyParameterConfig.randomizationEnviroment == 'test' ? "checked" : ""} value="test" onchange="javascript:changeIcon()">
		<fmt:message key="systemProperty.randomizationEnviroment.test.radioLabel" bundle="${resword}"/>

		<input type="radio" name="randomizationEnviroment" ${studyToView.studyParameterConfig.randomizationEnviroment == 'prod' ? "checked" : ""} value="prod" onchange="javascript:changeIcon()">
		<fmt:message key="systemProperty.randomizationEnviroment.prod.radioLabel" bundle="${resword}"/>
	</td>
</tr>
<!-- /Randomization Parameters section -->

<!-- Import Parameters section -->
<tr>
	<td>&nbsp;</td>
</tr>
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="dataImport" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="markImportedCRFAsCompleted" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.markImportedCRFAsCompleted == 'yes'}">
				<input type="radio" checked name="markImportedCRFAsCompleted" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="markImportedCRFAsCompleted" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="markImportedCRFAsCompleted" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="markImportedCRFAsCompleted" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="autoScheduleEventDuringImport" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.autoScheduleEventDuringImport == 'yes'}">
				<input type="radio" checked name="autoScheduleEventDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="autoScheduleEventDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="autoScheduleEventDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="autoScheduleEventDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="autoCreateSubjectDuringImport" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.autoCreateSubjectDuringImport == 'yes'}">
				<input type="radio" checked name="autoCreateSubjectDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="autoCreateSubjectDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="autoCreateSubjectDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="autoCreateSubjectDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="replaceExisitingDataDuringImport" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.replaceExisitingDataDuringImport == 'yes'}">
				<input type="radio" checked name="replaceExisitingDataDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="replaceExisitingDataDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="replaceExisitingDataDuringImport" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="replaceExisitingDataDuringImport" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<!-- /Import Parameters section -->

<!-- Coding Parameters section -->
<tr>
	<td>&nbsp;</td>
</tr>
<tr valign="top" style="border: 1px solid black;width: 100%;">
	<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
		<fmt:message key="medical_coding" bundle="${resword}"/>:
	</td>
	<td style=" border-top: 1px solid black; text-align: left;">
		&nbsp;
	</td>
</tr>

<tr valign="top">
	<td class="formlabel">
		<fmt:message key="allowCodingVerification" bundle="${resword}"/>
	</td>
	<td>
		<input type="radio" name="allowCodingVerification" ${studyToView.studyParameterConfig.allowCodingVerification == 'yes' ? "checked" : ""} value="yes" onchange="javascript:changeIcon()"
		       onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="medicalCoding">
		<fmt:message key="yes" bundle="${resword}"/>
		<input type="radio" name="allowCodingVerification" ${studyToView.studyParameterConfig.allowCodingVerification == 'no' ? "checked" : ""} value="no" onchange="javascript:changeIcon()"
		       onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="medicalCoding">
		<fmt:message key="no" bundle="${resword}"/>
	</td>
</tr>
<tr valign="top" class="medicalCoding">
	<td class="formlabel">
		<fmt:message key="medicalCodingApprovalNeeded" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.medicalCodingApprovalNeeded == 'yes'}">
				<input type="radio" disabled="disabled" name="medicalCodingApprovalNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" disabled="disabled" checked name="medicalCodingApprovalNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" disabled="disabled" name="medicalCodingApprovalNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" disabled="disabled" checked name="medicalCodingApprovalNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr valign="top" class="medicalCoding">
	<td class="formlabel">
		<fmt:message key="medicalCodingContextNeeded" bundle="${resword}"/>
	</td>
	<td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.medicalCodingContextNeeded == 'yes'}">
				<input type="radio" checked name="medicalCodingContextNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" name="medicalCodingContextNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="medicalCodingContextNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" checked name="medicalCodingContextNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr valign="top" class="medicalCoding">
	<td class="formlabel">
		<fmt:message key="autoCodeDictionaryName" bundle="${resword}"/>:
	</td>
	<td>
		<input type="text" name="autoCodeDictionaryName" value="${studyToView.studyParameterConfig.autoCodeDictionaryName}" maxlength="255" size="35">
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="autoCodeDictionaryName"/></jsp:include>
	</td>
</tr>
<!-- /Coding Parameters section -->

<tr>
	<td>&nbsp;</td>
</tr>

<tr valign="top" style="border: 1px solid black;width: 100%;">
    <td class="formlabel" style="border-top: 1px solid black;text-align: left; width:170px">
        <fmt:message key="rule_rules" bundle="${resword}"/>:
    </td>
    <td style=" border-top: 1px solid black; text-align: left;">
        &nbsp;
    </td>
</tr>
<tr valign="top">
    <td class="formlabel">
        <fmt:message key="allowRulesAutoScheduling" bundle="${resword}"/>?
    </td>
    <td>
        <c:choose>
            <c:when test="${studyToView.studyParameterConfig.allowRulesAutoScheduling == 'yes'}">
                <input type="radio" checked name="allowRulesAutoScheduling" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                <input type="radio" name="allowRulesAutoScheduling" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
            </c:when>
            <c:otherwise>
                <input type="radio" name="allowRulesAutoScheduling" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                <input type="radio" checked name="allowRulesAutoScheduling" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <td>&nbsp;</td>
</tr>

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
