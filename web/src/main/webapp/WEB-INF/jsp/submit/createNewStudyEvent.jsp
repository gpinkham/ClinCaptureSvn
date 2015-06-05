<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/submit-header.jsp"/>

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
<jsp:include page="../include/sideInfo.jsp"/>

<c:set var="location" value="" />
<c:set var="requestStudySubjectFalse" value="no" />

<!-- TODO: HOW TO DEAL WITH PRESET VALUES THAT AREN'T STRINGS? -->
<!-- TODO: CAN I USE PUBLIC STATIC MEMBERS HERE? -->
<!-- *JSP* submit/createNewStudyEvent.jsp -->
<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "location"}'>
		<c:set var="location" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinition"}'>
		<c:set var="chosenDefinition" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studySubject"}'>
		<c:set var="chosenStudySubject" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studySubjectLabel"}'>
		<c:set var="chosenSubjectLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "requestStudySubject"}'>
		<c:set var="requestStudySubject" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "locationScheduled0"}'>
		<c:set var="locationScheduled0" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "locationScheduled1"}'>
		<c:set var="locationScheduled1" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "locationScheduled2"}'>
		<c:set var="locationScheduled2" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "locationScheduled3"}'>
		<c:set var="locationScheduled3" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinitionScheduled0"}'>
		<c:set var="chosenDefinitionScheduled0" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinitionScheduled1"}'>
		<c:set var="chosenDefinitionScheduled1" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinitionScheduled2"}'>
		<c:set var="chosenDefinitionScheduled2" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinitionScheduled3"}'>
		<c:set var="chosenDefinitionScheduled3" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "display0"}'>
		<c:set var="display0" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "display1"}'>
		<c:set var="display1" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "display2"}'>
		<c:set var="display2" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "display3"}'>
		<c:set var="display3" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1>
	<span class="first_level_header">
		<c:choose>
			<c:when test="${requestStudySubject == requestStudySubjectFalse}">
				<fmt:message key="schedule_study_event_for" bundle="${resword}"/><b> <c:out value="${chosenStudySubject.name}" /></b>
				<a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2')">
				<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
			</c:when>
			<c:otherwise>	
				<fmt:message key="schedule_study_event_for" bundle="${resword}"/>
				<a href="javascript:openDocWindow('help/2_4_scheduleEvent_Help.html')">
				<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
			</c:otherwise>
		</c:choose>
	</span>
</h1>

<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<script type="text/JavaScript" language="JavaScript">
  <!--
function myCancel() {

    cancelButton=document.getElementById('cancel');
    if (cancelButton != null) {
    	confirmDialog({ 
    		message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
    		height: 150,
    		width: 500,
    		redirectLink: 'ListStudySubjects'
    		});      
     	return false;
     }
     return true;
  }
  
function setDNFlag(strImageName,strParentWinImageFullPath, resolutionStatusId) {
    var objImage;
    objImage = MM_findObj(strImageName);
    if (objImage != null) {
        objImage.src = strParentWinImageFullPath;
    }
}
   //-->
</script>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:forEach var="updatedDiscrepancyNote" items="${notSavedDNs}">
	<script type="text/JavaScript" language="JavaScript">	
		$(document).ready(function() {
			setDNFlag('flag_${updatedDiscrepancyNote.field}', '${contextPath}/${updatedDiscrepancyNote.resStatus.iconFilePath}');
		});
	</script>
</c:forEach>

<P><span class="alert">* </span><fmt:message key="indicates_required_field" bundle="${resword}"/></P>
<form action="CreateNewStudyEvent" method="post">
<input type="hidden" id="openFirstCrf" name="openFirstCrf" value="false"/>
<input type="hidden" id="formWithStateFlag" value=""/>
<jsp:include page="../include/showSubmitted.jsp" />

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<div style="width: 600px">

<!-- These DIVs define shaded box borders -->
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
	<tr>
		<td class="formlabel">${studySubjectLabel}:</td>
		<td valign="top">
			<c:choose>
				<c:when test="${requestStudySubject == requestStudySubjectFalse}">
					<b><c:out value="${chosenStudySubject.name}" /></b>
					<input type="hidden" name="studySubject" value="<c:out value="${chosenStudySubject.id}" />" />
					<input type="hidden" name="studySubjectLabel" value="<c:out value="${chosenStudySubject.name}" />" />
					<input type="hidden" name="requestStudySubject" value="<c:out value="${requestStudySubject}" />" />
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubject"/></jsp:include>
				</c:when>
				<c:otherwise>
					<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top">
						<div class="formfieldXL_BG">
						<!-- added/removed tbh 11/09/2009 -->
						<input type="text" class="formfieldXL" size="50" value="<c:out value="${chosenSubjectLabel}"/>" name="studySubjectLabel" />
				
							<%-- <select name="studySubject" class="formfieldXL">
							<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
							<c:forEach var="subject" items="${subjects}">
								<c:choose>
									<c:when test="${chosenStudySubject.id == subject.id}">
										<option value="<c:out value="${subject.id}" />" selected><c:out value="${subject.name}"/></option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${subject.id}" />"><c:out value="${subject.name}"/></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							</select> --%>
						</div>
						</td>
						<td class="alert">*</td>
					</tr>
					<tr>
						<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubject"/></jsp:include></td>
					</tr>
					</table>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>

	<tr>
	  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinition" class="formfieldXL" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitions}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinition.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td class="alert">*</td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinition"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.startDateTimeLabel}:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useStartTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
				    <c:param name="prefix" value="start"/>
				    <c:param name="count" value="1"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
				</c:import>

				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=start&column=date_start','spanAlert-start', '', event); return false;">
							<img name="flag_start" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=start&column=date_start">						
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="start"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr valign="top" style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.endDateTimeLabel}:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useEndTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
				    <c:param name="prefix" value="end"/>
				    <c:param name="count" value="2"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
				</c:import>

				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=end&column=date_end','spanAlert-end', '', event); return false;">
							<img name="flag_end" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=end&column=date_end">	
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="end" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
    <tr>
        <td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
          <td valign="top">
              <table border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td valign="top">
                <div class="formfieldXL_BG">
                    <input type="text" name="location" value="<c:out value="${location}"/>" size="50" class="formfieldXL">
                </div>
                </td>
                <td>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=location&column=location','spanAlert-location', '', event); return false;">
							<img name="flag_location" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=location&column=location">
						</a>
					</c:if>
				</td>
            </tr>
            <tr>
                <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="location"/></jsp:include></td>
            </tr>
            </table>
        </td>
    </tr>
    </c:if>
    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'not_used'}">
        <input type="hidden" name="location" value="">
    </c:if>
</table>

</div>

</div></div></div></div></div></div></div></div>
</div> </div><br>

<a class="scheduleLink" href="javascript:leftnavExpand_ext('schedule0', true, '${newThemeColor}');">
    <img id="excl_schedule0" src="images/bt_Expand.gif" border="0"> <fmt:message key="schedule_another_event" bundle="${resword}"/></a>

<div id="schedule0" style="display: <c:out value="${display0}"/>">
<div style="width: 100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
 <tr>
  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  	<td valign="top">
    	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinitionScheduled0" class="formfieldXL" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitionsScheduled}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinitionScheduled0.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td class="alert">*</td>
			</tr>
			<tr>
			 <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinitionScheduled0"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="locationScheduled0" value="<c:out value="${locationScheduled0}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>
                    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'required'}"><span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled0&column=location','spanAlert-locationScheduled0', '', event); return false;">
							<img name="flag_locationScheduled0" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled0&column=location">
						</a>							
					</c:if>
                </td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="locationScheduled0"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    </c:if>
    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'not_used'}">
        <input type="hidden" name="location" value="">
    </c:if>
    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.startDateTimeLabel}:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useStartTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
                <c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="startScheduled0"/>
                    <c:param name="count" value="3"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled0&column=date_start','spanAlert-startScheduled0', '', event); return false;">
							<img name="flag_startScheduled0" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled0&column=date_start">
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startScheduled0"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr valign="top" style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.endDateTimeLabel}:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useEndTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="endScheduled0"/>
                    <c:param name="count" value="4"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled0&column=date_end','spanAlert-endScheduled0', '', event); return false;">
							<img name="flag_endScheduled0" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled0&column=date_end">	
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endScheduled0" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>

</div>
<br>
</div>

<a class="scheduleLink" href="javascript:leftnavExpand_ext('schedule1', true, '${newThemeColor}');">
    <img id="excl_schedule1" src="images/bt_Expand.gif" border="0"> <fmt:message key="schedule_another_event" bundle="${resword}"/> </a></div>

<div id="schedule1" style="display: <c:out value="${display1}"/>">
<div style="width: 100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
 <tr>
  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  	<td valign="top">
    	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinitionScheduled1" class="formfieldXL" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitionsScheduled}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinitionScheduled1.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td class="alert">*</td>
			</tr>
			<tr>
			 <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinitionScheduled1"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="locationScheduled1" value="<c:out value="${locationScheduled1}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>
                    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'required'}"><span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled1&column=location','spanAlert-locationScheduled1', '', event); return false;">
							<img name="flag_locationScheduled1" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled1&column=location">
						</a>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="locationScheduled1"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    </c:if>
    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'not_used'}">
        <input type="hidden" name="location" value="">
    </c:if>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.startDateTimeLabel}:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useStartTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="startScheduled1"/>
                    <c:param name="count" value="3"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled1&column=date_start','spanAlert-startScheduled1', '', event); return false;">
							<img name="flag_startScheduled1" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled1&column=date_start">
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startScheduled1"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr valign="top" style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.endDateTimeLabel}:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useEndTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
                <c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="endScheduled1"/>
                    <c:param name="count" value="4"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled1&column=date_start','spanAlert-endScheduled1', '', event); return false;">
							<img name="flag_endScheduled1" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled1&column=date_end">	
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endScheduled1" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>

</div>
<br>
</div>


<a class="scheduleLink" href="javascript:leftnavExpand_ext('schedule2', true, '${newThemeColor}');">
    <img id="excl_schedule2" src="images/bt_Expand.gif" border="0"> <fmt:message key="schedule_another_event" bundle="${resword}"/> </a></div>

<div id="schedule2" style="display:<c:out value="${display2}"/>">
<div style="width: 100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
 <tr>
  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  	<td valign="top">
    	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinitionScheduled2" class="formfieldXL" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitionsScheduled}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinitionScheduled2.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td class="alert">*</td>
			</tr>
			<tr>
			 <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinitionScheduled2"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="locationScheduled2" value="<c:out value="${locationScheduled2}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>
                    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'required'}"><span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled2&column=location','spanAlert-locationScheduled2', '', event); return false;">
							<img name="flag_locationScheduled2" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled2&column=location">
						</a>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="locationScheduled2"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    </c:if>
    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'not_used'}">
        <input type="hidden" name="location" value="">
    </c:if>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.startDateTimeLabel}:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useStartTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
                <c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="startScheduled2"/>
                    <c:param name="count" value="3"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled2&column=date_start','spanAlert-startScheduled2', '', event); return false;">
							<img name="flag_startScheduled2" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled0&column=date_start">
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startScheduled2"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr valign="top" style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.endDateTimeLabel}:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useEndTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="endScheduled2"/>
                    <c:param name="count" value="4"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled2&column=date_end','spanAlert-endScheduled2', '', event); return false;">
							<img name="flag_endScheduled2" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled2&column=date_end">	
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endScheduled2" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>

</div>
<br>
</div>


<a class="scheduleLink" href="javascript:leftnavExpand_ext('schedule3', true, '${newThemeColor}');">
    <img id="excl_schedule3" src="images/bt_Expand.gif" border="0"> <fmt:message key="schedule_another_event" bundle="${resword}"/> </a></div>

<div id="schedule3" style="display:<c:out value="${display3}"/>">
<div style="width: 100%">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">

<table border="0" cellpadding="3" cellspacing="0">
 <tr>
  	<td class="formlabel"><fmt:message key="study_event_definition" bundle="${resword}"/>:</td>
  	<td valign="top">
    	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<select name="studyEventDefinitionScheduled3" class="formfieldXL" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
						<option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
						<c:forEach var="definition" items="${eventDefinitionsScheduled}">
							<c:choose>
								<c:when test="${definition.repeating}">
									<c:set var="repeating">
									 (<fmt:message key="repeating" bundle="${resword}"/>)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="repeating">
									(<fmt:message key="non_repeating" bundle="${resword}"/>)
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${chosenDefinitionScheduled3.id == definition.id}">
									<option value="<c:out value="${definition.id}" />" selected><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${definition.id}"/>"><c:out value="${definition.name}"/> <c:out value="${repeating}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
				</td>
				<td class="alert">*</td>
			</tr>
			<tr>
			 <td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyEventDefinitionScheduled3"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
	<tr>
		<td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<div class="formfieldXL_BG">
					<input type="text" name="locationScheduled3" value="<c:out value="${locationScheduled3}"/>" size="50" class="formfieldXL">
				</div>
				</td>
				<td>
                    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'required'}"><span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled3&column=location','spanAlert-locationScheduled3', '', event); return false;">
							<img name="flag_locationScheduled3" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=locationScheduled3&column=location">
						</a>
					</c:if>
				</td>
			</tr>
			<tr>
				<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="locationScheduled3"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>
    </c:if>
    <c:if test="${study.studyParameterConfig.eventLocationRequired == 'not_used'}">
        <input type="hidden" name="location" value="">
    </c:if>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.startDateTimeLabel}:</td>
	  	<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useStartTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="startScheduled3"/>
                    <c:param name="count" value="3"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useStartTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.startDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled3&column=date_start','spanAlert-startScheduled3', '', event); return false;">
							<img name="flag_startScheduled3" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=startScheduled3&column=date_start">
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startScheduled3"/></jsp:include></td>
			</tr>
			</table>
		</td>
	</tr>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
    <tr valign="top" style="${additionalStyle}">
		<td class="formlabel">${study.studyParameterConfig.endDateTimeLabel}:</td>
		<td valign="top">
		  	<table border="0" cellpadding="0" cellspacing="0">
			<tr>
                <c:set var="timeAdditionalStyle" value=""/>
                <c:if test="${study.studyParameterConfig.useEndTime == 'no'}"><c:set var="timeAdditionalStyle" value="display: none;"/></c:if>
				<c:import url="../include/showDateTimeInput2.jsp">
                    <c:param name="prefix" value="endScheduled3"/>
                    <c:param name="count" value="4"/>
                    <c:param name="timeAdditionalStyle" value="${timeAdditionalStyle}"/>
                </c:import>
				<td>
                    <c:choose>
                        <c:when test="${study.studyParameterConfig.useEndTime == 'no'}">(<fmt:message key="date_format_string" bundle="${resformat}"/>)</c:when>
                        <c:otherwise>(<fmt:message key="date_time_format" bundle="${resformat}"/>)</c:otherwise>
                    </c:choose>
                    <c:if test="${study.studyParameterConfig.endDateTimeRequired == 'yes'}"> <span class="alert">*</span></c:if>
                    <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
						<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled3&column=date_end','spanAlert-endScheduled3', '', event); return false;">
							<img name="flag_endScheduled3" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
							<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${chosenStudySubject.id}&name=studyEvent&field=endScheduled3&column=date_end">
						</a>
					</c:if>
                </td>
			</tr>
			<tr>
				<td colspan="7">
					<fmt:message key="leave_this_field_blank_if_not_applicable" bundle="${restext}"/><br/>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endScheduled3" /></jsp:include>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>

</div>

</div></div></div></div></div></div></div></div>

</div>
</div><br>

<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <input type="submit" name="Schedule" value="<fmt:message key="schedule_event" bundle="${resword}"/>" class="button_medium"/>
            <input type="button" name="StartDataEntry" value="<fmt:message key="start_data_entry" bundle="${resword}"/>" class="button_medium" onClick="$('#openFirstCrf').val('true'); $('input[name=Schedule]').click();"/>
            <input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium medium_cancel" onClick="formWithStateGoBackSmart('<fmt:message key="sure_to_cancel" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" "/>
        </td>
    </tr>
</table>
</form>

<script>
    jQuery("img[class='showCalendar']").each(function() {
        var jsCode = jQuery(this).attr("rel");
        eval(jsCode);
    });
</script>

<jsp:include page="../include/footer.jsp"/>