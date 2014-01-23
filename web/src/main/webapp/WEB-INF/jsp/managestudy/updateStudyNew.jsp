<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.admin" var="resadmin"/>

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
	function leftnavExpand(strLeftNavRowElementName){
		var objLeftNavRowElement;
		var objExCl;
        objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
		if (objLeftNavRowElement != null) {
			if (objLeftNavRowElement.style) { 
				objLeftNavRowElement = objLeftNavRowElement.style; 
			}
			objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
		}
		objExCl = MM_findObj("excl_"+strLeftNavRowElementName);
		if (objExCl != null) {
			if(objLeftNavRowElement.display == "none"){
				objExCl.src = "images/${newThemeColor}/bt_Expand.gif";
			}else{
				objExCl.src = "images/${newThemeColor}/bt_Collapse.gif";
			}
		}
	}
	
	function leftnavExpandExt(strLeftNavRowElementName){
		$("div[id="+strLeftNavRowElementName+"]").each(function() {
			leftnavExpand(strLeftNavRowElementName+'_3');
			leftnavExpand(strLeftNavRowElementName+'_4');
		});
	}
	
	function showMoreFields(index, name) {
		switch (name) {
			case 'RFC':
				var rowId = 'dnRFCDescriptionRow';
				break;
			case 'updateDescriptions':
				var rowId = 'dnUpdateDescriptionRow';
				break;
			case 'closeDescriptions':
				var rowId = 'dnCloseDescriptionRow';
				break;
			}
		for (var j=index+1; (j<26)&&(j<(index+3)); j++){
			$("tr#"+rowId+"_a"+j).show();
			$("tr#"+rowId+"_b"+j).show();
			$("tr#"+rowId+"_c"+j).show();
		}
	}	
	
	$(document).ready(function() { 
		var sections = new Array(3, 4, 5, 6, '6_1', '6_2', '6_3', '7');
		for (var j=1; j < sections.length; j++){
			if ($("div#section" + sections[j] + " span.alert").text() != '') {
				leftnavExpand("section" + sections[j]);
			}	
		}
	})	

 </script>

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

<br><br>

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
<div id="section1" style="display: ">
<div style="width: 600px">
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
					<div class="formfieldXL_BG">
						<input type="text" name="uniqueProId" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.identifier}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td>
				<td width="10%">
					*
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<b><fmt:message key="brief_title" bundle="${resword}"/></b>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="name" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.name}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
				</td>
				<td>
					*
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<b><fmt:message key="official_title" bundle="${resword}"/></b>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="officialTitle" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.officialTitle}"/>" class="formfieldXL">
					</div>
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
					<div class="formfieldXL_BG">
						<c:set var="dis" value="${parentStudy.name!='' && !parentStudy.status.available}"/>
						<c:set var="status1" value="${studyToView.status.id}"/>
						<select class="formfieldXL" name="statusId" disabled="true">
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
					</div>
				</td>
				<td>
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
					<div class="formtextareaXL4_BG">
						<textarea class="formtextareaXL4" name="secondProId" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.secondaryIdentifier}"/></textarea>
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
				</td>
				<td>&nbsp;</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="principal_investigator" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="prinInvestigator" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.principalInvestigator}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td><td>*</td></tr>

			<tr valign="top">
				<td class="formlabel">
					<a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary'); return false;">
						<fmt:message key="brief_summary" bundle="${resword}"/>:
					</a>
				</td>
				<td>
					<div class="formtextareaXL4_BG">
						<textarea class="formtextareaXL4" name="description" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.summary}"/></textarea>
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
				</td>
				<td>
					*
				</td>
			</tr>

            <tr valign="top">
				<td class="formlabel">
					<fmt:message key="detailed_description" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formtextareaXL4_BG">
						<textarea class="formtextareaXL4" name="protocolDescription" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.protocolDescription}"/></textarea>
					</div>
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
					<div class="formfieldXL_BG">
						<input type="text" name="sponsor" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.sponsor}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="sponsor"/></jsp:include>
				</td>
				<td>
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
					<div class="formtextareaXL4_BG">
						<textarea class="formtextareaXL4" name="collaborators" onchange="javascript:changeIcon()" rows="4" cols="50"><c:out value="${studyToView.collaborators}"/></textarea>
					</div>
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
					<div class="formfieldXL_BG">
						<select name="phase" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
				<td>
					*
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="protocol_type" bundle="${resword}"/>:
				</td>
				<td>
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
					<div class="formfieldXL_BG">
						<input type="text" name="protocolDateVerification" onchange="javascript:changeIcon()" value="<c:out value="${protocolDateVerification}"/>" class="formfieldM" id="protocolDateVerificationField">
					</div>
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="protocolDateVerification"/>
					</jsp:include>
				</td>
				<td>
					<A HREF="#" >
						<img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="protocolDateVerificationTrigger"/>
						<script type="text/javascript">
							Calendar.setup({inputField  : "protocolDateVerificationField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "protocolDateVerificationTrigger" });
						</script>
					</a>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="study_start_date" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="startDate" onchange="javascript:changeIcon()" value="<c:out value="${startDate}" />" class="formfieldXL" id="startDateField">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include>
				</td>
				<td>
					<A HREF="#" >
						<img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="startDateTrigger"/>
						<script type="text/javascript">
							Calendar.setup({inputField  : "startDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "startDateTrigger" });
						</script>
					</a>
					*
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="study_completion_date" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="endDate" onchange="javascript:changeIcon()" value="<c:out value="${endDate}" />" class="formfieldXL" id="endDateField">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include>
				</td>
				<td>
					<A HREF="#">
						<img src="images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" id="endDateTrigger"/>
						<script type="text/javascript">
							Calendar.setup({inputField  : "endDateField", ifFormat    : "<fmt:message key="date_format_calender" bundle="${resformat}"/>", button      : "endDateTrigger" });
						</script>
					</a>
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
					<div class="formfieldXL_BG">
						<select name="purpose" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
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
					<div class="formfieldXL_BG">
						<select name="allocation" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
				<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntMasking" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntMasking'); return false;">
					<fmt:message key="masking" bundle="${resword}"/></a>:
				</td>
				<td>
					<c:set var="masking1" value="${studyToView.masking}"/>
					<div class="formfieldXL_BG">
						<select name="masking" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>
			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="control" bundle="${resword}"/>:
				</td>
				<td>
					<c:set var="control1" value="${studyToView.control}"/>
					<div class="formfieldXL_BG">
						<select name="control" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
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
					<div class="formfieldXL_BG">
						<select name="assignment" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>
			<tr valign="top">
				<td class="formlabel">
					<a href="http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#IntEndpoints'); return false;">
						<fmt:message key="study_classification" bundle="${resword}"/></a>:</td><td>
				<%-- was endpoint, tbh --%>
					<c:set var="endpoint1" value="${studyToView.endpoint}"/>
					<div class="formfieldXL_BG">
						<select name="endpoint" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>
		</c:if>
		<c:if test="${isInterventional==0}">
             <!-- End From Update Page 3 -->
             <!-- condition for isInterventional should be applied -->
             <!-- From Update Page 4 -->
			<tr valign="bottom">
				<td class="formlabel">
					<fmt:message key="purpose" bundle="${resword}"/>:
				</td>
				<td>
					<c:set var="purpose1" value="${studyToView.purpose}"/>
					<div class="formfieldXL_BG">
						<select name="purpose" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="purpose"/></jsp:include>
				</td>
				<td valign="top">
					*
				</td>
			</tr>

			<tr valign="bottom">
				<td class="formlabel">
					<fmt:message key="duration" bundle="${resword}"/>:
				</td>
				<td>
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
					<div class="formfieldXL_BG">
						<select name="selection" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
				<td>&nbsp;</td>
			</tr>

			<tr valign="bottom">
				<td class="formlabel">
					<fmt:message key="timing" bundle="${resword}"/>
				</td>
				<td>
					<c:set var="timing1" value="${studyToView.timing}"/>
					<div class="formfieldXL_BG">
						<select name="timing" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
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
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">
		<table border="0" cellpadding="0">
			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="conditions" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="conditions" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.conditions}"/>" class="formfieldXL">
					</div>
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
					<div class="formtextareaXL4_BG">
						<textarea name="keywords" onchange="javascript:changeIcon()" rows="4" cols="50"  class="formtextareaXL4"><c:out value="${studyToView.keywords}"/></textarea>
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="keywords"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="eligibility_criteria" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formtextareaXL4_BG">
						<textarea name="eligibility" onchange="javascript:changeIcon()" rows="4" cols="50" class="formtextareaXL4"><c:out value="${studyToView.eligibility}"/></textarea>
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="eligibility"/></jsp:include>
				</td>
			</tr>

		<c:if test="${genderShow}">
			<tr valign="top">
				<td class="formlabel">${genderLabel}:</td>
				<td>
					<div class="formfieldXL_BG">
						<select name="gender" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>
		</c:if>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="minimum_age" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="ageMin" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.ageMin}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMin"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="maximum_age" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="ageMax" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.ageMax}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="ageMax"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="healthy_volunteers_accepted" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<select name="healthyVolunteerAccepted" class="formfieldXL" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="expected_total_enrollment" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="expectedTotalEnrollment" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.expectedTotalEnrollment}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="expectedTotalEnrollment"/></jsp:include>
				</td>
				<td>*</td>
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
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
    <div class="textbox_center">
		<table border="0" cellpadding="5">
			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_name" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facName" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityName}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facName"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_city" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facCity" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityCity}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCity"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_state_province" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facState" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityState}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facState"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="postal_code" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facZip" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityZip}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facZip"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_country" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facCountry" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityCountry}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facCountry"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_contact_name" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facConName" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactName}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConName"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_contact_degree" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facConDegree" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactDegree}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConDegree"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_contact_phone" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facConPhone" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactPhone}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="facConPhone"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="facility_contact_email" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="facConEmail" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.facilityContactEmail}"/>" class="formfieldXL">
					</div>
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
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">
		<table border="0" cellpadding="0">
			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="MEDLINE_identifier" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="medlineIdentifier" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.medlineIdentifier}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="medlineIdentifier"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="results_reference" bundle="${resword}"/>:
				</td>
				<td>
					<div class="formfieldS_BG">
						<select name="resultsReference" class="formfieldS" onchange="javascript:changeIcon()">
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
					</div>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="URL_reference" bundle="${resword}"/>
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="url" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.url}"/>" class="formfieldXL">
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="url"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel">
					<fmt:message key="URL_description" bundle="${resword}"/>
				</td>
				<td>
					<div class="formfieldXL_BG">
						<input type="text" name="urlDescription" onchange="javascript:changeIcon()" value="<c:out value="${studyToView.urlDescription}"/>" class="formfieldXL">
					</div>
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

		<div style="width: 600px">
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
										<div class="formfieldXL_BG">
											<input type="text" name="updateName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL">
										</div>
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
										<c:choose>
											<c:when test="${term.visibilityLevel == 'Site'}">
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" checked value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:when>
											<c:when test="${term.visibilityLevel == 'Study'}">
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" checked value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:when>
											<c:otherwise>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="updateVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:otherwise>
										</c:choose>
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
										<div class="formfieldXL_BG">
											<input type="text" name="updateName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'updateDescriptions');" value="" class="formfieldXL">
										</div>
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
											<div class="formfieldXL_BG">
												<input type="text" name="updateName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'updateDescriptions');" value="" class="formfieldXL">
											</div>
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

		<div style="width: 600px">
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
										<div class="formfieldXL_BG">
											<input type="text" name="closeName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL">
										</div>
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
										<c:choose>
											<c:when test="${term.visibilityLevel == 'Site'}">
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" checked value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:when>
											<c:when test="${term.visibilityLevel == 'Study'}">
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" checked value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:when>
											<c:otherwise>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
												<input type="radio" onchange="javascript:changeIcon()" name="closeVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
											</c:otherwise>
										</c:choose>
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
										<div class="formfieldXL_BG">
											<input type="text" name="closeName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'closeDescriptions');" value="" class="formfieldXL">
										</div>
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
											<div class="formfieldXL_BG">
												<input type="text" name="closeName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'closeDescriptions');" value="" class="formfieldXL">
											</div>
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

			<div style="width: 600px">
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
											<div class="formfieldXL_BG">
												<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon()" value="${term.name}" class="formfieldXL">
											</div>
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
											<c:choose>
												<c:when test="${term.visibilityLevel == 'Site'}">
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" checked value="2"><fmt:message key="site_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
												</c:when>
												<c:when test="${term.visibilityLevel == 'Study'}">
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" checked value="1"><fmt:message key="study_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="0"><fmt:message key="both" bundle="${resword}"/>
												</c:when>
												<c:otherwise>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="2"><fmt:message key="site_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" value="1"><fmt:message key="study_level" bundle="${resword}"/>
													<input type="radio" onchange="javascript:changeIcon()" name="dnRFCVisibilityLevel${count}" checked value="0"><fmt:message key="both" bundle="${resword}"/>
												</c:otherwise>
											</c:choose>
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
											<div class="formfieldXL_BG">
												<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'RFC');" value="" class="formfieldXL">
											</div>
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
												<div class="formfieldXL_BG">
													<input type="text" name="dnRFCName${count}" onchange="javascript:changeIcon();" onfocus="javascript:showMoreFields(${count},'RFC');" value="" class="formfieldXL">
												</div>
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
	</div>
<!-- end of section f -->

<a href="javascript:leftnavExpand('section7');">
	<img id="excl_section7" src="images/bt_Expand.gif" border="0"> <span class="table_title_Admin">
    <fmt:message key="study_parameter_configuration" bundle="${resword}"/></span></a>
<div id="section7" style="display:none ">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr valign="top">
			<td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/>:</td><td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.collectDob == '1'}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.collectDob == '2'}">
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>

		<tr valign="top">
			<td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.discrepancyManagement == 'false'}">
					<input type="radio" onchange="javascript:changeIcon()" name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" checked name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio"  onchange="javascript:changeIcon()" name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>

		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="subject_person_ID_required" bundle="${resword}"/>:
			</td>
			<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'required'}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>

		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="show_person_id_on_crf_header" bundle="${resword}"/>:
			</td>
			<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.personIdShownOnCRF == 'true'}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="personIdShownOnCRF" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="personIdShownOnCRF" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="personIdShownOnCRF" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="personIdShownOnCRF" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>

		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="how_to_generate_the_study_subject_ID" bundle="${resword}"/>:
			</td>
			<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'manual'}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
   
		<tr>
			<td>
				&nbsp;
			</td>
		</tr>
		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/>
			</td>
			<td>
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewerNameRequired == 'yes'}">checked </c:if> name="interviewerNameRequired" value="yes"> <fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewerNameRequired== 'no'}">checked</c:if> name="interviewerNameRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewerNameRequired== 'not_used'}">checked</c:if> name="interviewerNameRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
			</td>
		</tr>

		<tr valign="top">
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

		<tr valign="top">
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
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewDateRequired== 'yes'}"> checked </c:if> name="interviewDateRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewDateRequired== 'no'}"> checked </c:if> name="interviewDateRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.interviewDateRequired== 'not_used'}"> checked </c:if> name="interviewDateRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
			</td>
		</tr>

		<tr valign="top">
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

		<tr valign="top">
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

	<tr valign="top"><td class="formlabel"><fmt:message key="secondary_label_viewable" bundle="${resword}"/></td><td>
		<c:choose>
			<c:when test="${studyToView.studyParameterConfig.secondaryLabelViewable== 'true'}">
				<input type="radio" onchange="javascript:changeIcon()" checked name="secondaryLabelViewable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" name="secondaryLabelViewable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" onchange="javascript:changeIcon()" name="secondaryLabelViewable" value="true"><fmt:message key="yes" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" checked name="secondaryLabelViewable" value="false"><fmt:message key="no" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="forced_reason_for_change" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.adminForcedReasonForChange== 'true'}">
					<input type="radio" onchange="javascript:changeIcon()" checked name="adminForcedReasonForChange" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" name="adminForcedReasonForChange" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" name="adminForcedReasonForChange" value="true"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" checked name="adminForcedReasonForChange" value="false"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

  <tr valign="top">
    <td class="formlabel">
      <fmt:message key="allowSdvWithOpenQueries" bundle="${resword}"/>
    </td>
    <td>
      <c:choose>
        <c:when test="${studyToView.studyParameterConfig.allowSdvWithOpenQueries == 'yes'}">
          <input type="radio" checked name="allowSdvWithOpenQueries" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
          <input type="radio" name="allowSdvWithOpenQueries" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
        </c:when>
        <c:otherwise>
          <input type="radio" name="allowSdvWithOpenQueries" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
          <input type="radio" checked name="allowSdvWithOpenQueries" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="event_location_required" bundle="${resword}"/>
		</td>
		<td>
            <input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.eventLocationRequired== 'required'}"> checked </c:if> name="eventLocationRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
            <input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.eventLocationRequired== 'optional'}"> checked </c:if> name="eventLocationRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
            <input type="radio" onchange="javascript:changeIcon()" <c:if test="${studyToView.studyParameterConfig.eventLocationRequired== 'not_used'}"> checked </c:if> name="eventLocationRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
		</td>
	</tr>

  <%-- clinovo - start (ticket #11) --%>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr valign="top" style="border: 1px solid black;width: 100%;">
		<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
			<fmt:message key="subjectForm" bundle="${resword}"/>:
		</td>
		<td style=" border-top: 1px solid black; text-align: left;">
			&nbsp;
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
			<fmt:message key="secondaryIdLabel" bundle="${resword}"/>
		</td>
		<td>
			<input type="text" name="secondaryIdLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.secondaryIdLabel}" maxlength="255" size="35">
			<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryIdLabel"/></jsp:include>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="secondaryIDRequired" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.secondaryIdRequired == 'yes'}">
					<input type="radio" checked name="secondaryIdRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="secondaryIdRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="secondaryIdRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.secondaryIdRequired == 'no'}">
					<input type="radio" name="secondaryIdRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" checked name="secondaryIdRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="secondaryIdRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" name="secondaryIdRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="secondaryIdRequired" onchange="javascript:changeIcon()" value="no"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" checked name="secondaryIdRequired" onchange="javascript:changeIcon()" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr valign="top">
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
			<fmt:message key="dateOfEnrollmentForStudyRequired" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'yes'}">
					<input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'no'}">
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="dateOfEnrollmentForStudyRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="genderLabel" bundle="${resword}"/>
		</td>
		<td>
			<input type="text" name="genderLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.genderLabel}" maxlength="255" size="35">
			<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="genderLabel"/></jsp:include>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="gender_required2" bundle="${resword}"/>:
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.genderRequired == 'false'}">
					<input type="radio" name="genderRequired" value="true" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="genderRequired" value="false" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" checked name="genderRequired" value="true" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" name="genderRequired" value="false" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>

	<tr valign="top" style="border: 1px solid black;width: 100%;">
		<td class="formlabel" style="border-top: 1px solid black;text-align: left;">
			<fmt:message key="studyEventForm" bundle="${resword}"/>:
		</td><td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>
	</tr>

	<tr valign="top">
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
			<fmt:message key="endDateTimeLabel" bundle="${resword}"/>
		</td>
		<td>
			<input type="text" name="endDateTimeLabel" onchange="javascript:changeIcon()" value="${studyToView.studyParameterConfig.endDateTimeLabel}" maxlength="255" size="35">
			<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDateTimeLabel"/></jsp:include>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="startDateTimeRequired" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.startDateTimeRequired == 'yes'}">
					<input type="radio" checked name="startDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="startDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="startDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.startDateTimeRequired == 'no'}">
					<input type="radio" name="startDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" checked name="startDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="startDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" name="startDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="startDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" checked name="startDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="endDateTimeRequired" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.endDateTimeRequired == 'yes'}">
					<input type="radio" checked name="endDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="endDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="endDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:when test="${studyToView.studyParameterConfig.endDateTimeRequired == 'no'}">
					<input type="radio" name="endDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" checked name="endDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" name="endDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" name="endDateTimeRequired" value="yes" onchange="javascript:changeIcon()"><fmt:message key="required" bundle="${resword}"/>
					<input type="radio" name="endDateTimeRequired" value="no" onchange="javascript:changeIcon()"><fmt:message key="optional" bundle="${resword}"/>
					<input type="radio" checked name="endDateTimeRequired" value="not_used" onchange="javascript:changeIcon()"><fmt:message key="not_used" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="useStartTime" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
				<c:when test="${studyToView.studyParameterConfig.useStartTime == 'yes'}">
					<input type="radio" checked name="useStartTime" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" name="useStartTime" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" name="useStartTime" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
					<input type="radio" checked name="useStartTime" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

		<tr valign="top">
			<td class="formlabel">
				<fmt:message key="useEndTime" bundle="${resword}"/>
			</td>
			<td>
				<c:choose>
					<c:when test="${studyToView.studyParameterConfig.useEndTime == 'yes'}">
						<input type="radio" checked name="useEndTime" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
						<input type="radio" name="useEndTime" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
					</c:when>
					<c:otherwise>
						<input type="radio" name="useEndTime" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
						<input type="radio" checked name="useEndTime" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>

		<tr>
			<td>&nbsp;</td>
		</tr>

    <tr valign="top" style="border: 1px solid black;width: 100%;">
      <td class="formlabel" style="border-top: 1px solid black;text-align: left;">
        <fmt:message key="dataImport" bundle="${resword}"/>:
      </td><td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>
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
                <fmt:message key="defaultBioontologyURL" bundle="${resword}"/>:
            </td>
            <td>
                <input onchange="javascript:changeIcon()" id="bioontologyURL" name="defaultBioontologyURL" value="${bioontologyURL}"/>
            </td>
        </tr>

        <tr>
            <td>&nbsp;</td>
        </tr>

        <tr valign="top">
            <td class="formlabel">
                <fmt:message key="medicalCodingApiKey" bundle="${resword}"/>:
            </td>
            <td>
                <input onchange="javascript:changeIcon()" id="medicalCodingApiKey" name="medicalCodingApiKey" value="${medicalCodingApiKey}"/>
            </td>
        </tr>

        <tr>
            <td>&nbsp;</td>
        </tr>

        <tr valign="top">
		    <td class="formlabel">
		        <fmt:message key="autoCodeDictionaryName" bundle="${resword}"/>:
		    </td>
		    <td>
		        <input type="text" name="autoCodeDictionaryName" value="${studyToView.studyParameterConfig.autoCodeDictionaryName}" maxlength="255" size="35">
		        <br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="autoCodeDictionaryName"/></jsp:include>
		    </td>
		</tr>
        <tr><td>&nbsp;</td></tr>

        <tr valign="top">
            <td class="formlabel">
                <fmt:message key="allowCodingVerification" bundle="${resword}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyToView.studyParameterConfig.allowCodingVerification == 'yes'}">
                        <input type="radio" checked name="allowCodingVerification" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                        <input type="radio" name="allowCodingVerification" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="radio" name="allowCodingVerification" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                        <input type="radio" checked name="allowCodingVerification" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

		<tr>
			<td>&nbsp;</td>
		</tr>

		<tr valign="top">
            <td class="formlabel">
                <fmt:message key="medicalCodingApprovalNeeded" bundle="${resword}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${studyToView.studyParameterConfig.medicalCodingApprovalNeeded == 'yes'}">
                        <input type="radio" checked name="medicalCodingApprovalNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                        <input type="radio" name="medicalCodingApprovalNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <input type="radio" name="medicalCodingApprovalNeeded" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
                        <input type="radio" checked name="medicalCodingApprovalNeeded" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

        <tr>
            <td>&nbsp;</td>
        </tr>

        <tr valign="top">
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
					class="button_medium" 
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>

</td>
<td>
 <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
</td>
<td>
 <img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
</td>
</tr>
</table>

</form>

<br>
<br>

<br>
 <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>