<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/submit-header.jsp"/>

<!-- *JSP* submit/addNewSubject.jsp -->
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="fill_to_add_click_help" bundle="${restext}"/>
		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean" />
<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<jsp:useBean scope="request" id="groups" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="fathers" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="mothers" class="java.util.ArrayList" />

<c:set var="uniqueIdentifier" value="" />
<c:set var="chosenGender" value="" />
<c:set var="label" value="" />
<c:set var="secondaryLabel" value="" />
<c:set var="enrollmentDate" value="" />
<c:set var="dob" value="" />
<c:set var="yob" value="" />
<c:set var="groupId" value="${0}" />
<c:set var="fatherId" value="${0}" />
<c:set var="motherId" value="${0}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "uniqueIdentifier"}'>
		<c:set var="uniqueIdentifier" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "gender"}'>
		<c:set var="chosenGender" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "label"}'>
		<c:set var="label" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "secondaryLabel"}'>
		<c:set var="secondaryLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "enrollmentDate"}'>
		<c:set var="enrollmentDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dob"}'>
		<c:set var="dob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "yob"}'>
		<c:set var="yob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "group"}'>
		<c:set var="groupId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mother"}'>
		<c:set var="motherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "father"}'>
		<c:set var="fatherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "selectedDynGroupClassId"}'>
		<c:set var="selectedDynGroupClassId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "defaultDynGroupClassId"}'>
		<c:set var="defaultDynGroupClassId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "defaultDynGroupClassName"}'>
		<c:set var="defaultDynGroupClassName" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<h1>
	<span class="first_level_header">
	<c:out value="${study.name}" />:
	    <fmt:message key="add_subject" bundle="${resword}"/>
	    <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step1')">
	        <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
	</span>
</h1>
<!--
<c:if test="${study.genetic && (!empty mothers) || (!empty fathers)}">
<p class="text">Indicate the subject's parents, if applicable.
</c:if>
-->
<p class="text">
<br/><fmt:message key="field_required" bundle="${resword}"/></P>
<form action="AddNewSubject" method="post">
<input type="hidden" id="formWithStateFlag" value=""/>
<input type="hidden" id="openFirstCrf" name="openFirstCrf" value="false"/>
<jsp:include page="../include/showSubmitted.jsp" />

<script type="text/JavaScript" language="JavaScript">
  function showDynamicEventsSection(defaultDynGroupClassId) {
  
    var index = $(":select [name='dynamicGroupClassId'] :selected").val();
	switch (index) {
		case '0':
			$("tr[id^='dynamicGroupId']").hide();   
			if (defaultDynGroupClassId != 0){
				$("tr#defaultDynGroupName").show();
				$("tr[id='dynamicGroupId"+defaultDynGroupClassId+"']").show();
			} else {
				$("tr#defaultDynGroupName").show();
			}
			break
		default: 
			$("tr#defaultDynGroupName").hide();
			$("tr[id^='dynamicGroupId']").hide();   
			$("tr[id='dynamicGroupId"+index+"']").show();
	}
	return true;
  }
  
  $(document).ready(function() { 
		showDynamicEventsSection(${defaultDynGroupClassId});
	});
</script>

<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="5">
	<tr valign="top">
		<td class="formlabel">${study.studyParameterConfig.studySubjectIdLabel}:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
					<c:choose>
					 <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
					  <input onfocus="this.select()" type="text" value="<c:out value="${label}"/>" size="45" class="formfieldXL" disabled>
					  <input type="hidden" name="label" value="<c:out value="${label}"/>">
					 </c:when>
					 <c:otherwise>
					   <input onfocus="this.select()" type="text" name="label" value="<c:out value="${label}"/>" size="50" class="formfieldXL">
					 </c:otherwise>
					</c:choose>
					</div></td>
					<td valign="top">*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<c:choose>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
					</div></td>
					<td valign="top">* <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
					<a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier'); return false;">
					<img name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='copyFromSSID'}">
		<script>
			$(document).ready(function() {
				initFieldCopying("[name=label]","[name=uniqueIdentifier]");
			});
		</script>
		<input type="hidden" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>">
	</c:when>
	<c:otherwise>
	  <input type="hidden" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>">
	</c:otherwise>
	</c:choose>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.secondaryIdRequired =='not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
	<tr valign="top" style="${additionalStyle}">
	  	<td class="formlabel">${study.studyParameterConfig.secondaryIdLabel}</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldXL_BG">
						<input onfocus="this.select()" type="text" name="secondaryLabel" value="<c:out value="${secondaryLabel}"/>" size="50" class="formfieldXL">
					</div></td>
					<td>
                        <c:if test="${study.studyParameterConfig.secondaryIdRequired == 'yes'}">
                            <span class="formlabel">*</span>
                        </c:if>
					</td>
				</tr>
				<tr>
					<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryLabel"/></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

    <c:set var="additionalStyle" value=""/>
    <c:if test="${study.studyParameterConfig.dateOfEnrollmentForStudyRequired =='not_used'}"><c:set var="additionalStyle" value="display: none;"/></c:if>
	<tr valign="top" style="${additionalStyle}">
		<td class="formlabel">
            <c:if test="${study.parentStudyId == 0}">
                ${study.studyParameterConfig.dateOfEnrollmentForStudyLabel} '<c:out value="${study.name}" /> ' :
            </c:if>
            <c:if test="${study.parentStudyId > 0}">
                ${study.studyParameterConfig.dateOfEnrollmentForStudyLabel} '<c:out value="${study.parentStudyName}" /> ' :
            </c:if>

        </td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
                        <!--layer-background-color:white;-->
                        <div id="testdiv1" style="position:absolute;visibility:hidden;z-index:8;background-color:white;"></div>

                        <div class="formfieldM_BG">
						    <input onfocus="this.select()" type="text" name="enrollmentDate" size="15" value="<c:out value="${enrollmentDate}" />" class="formfieldM" id="enrollmentDateField" />
                        </div>
					</td>
					<td valign="top">
                            <c:if test="${study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'yes'}">
                                <span class="formlabel">*</span>
                            </c:if>
						<ui:calendarIcon onClickSelector="'#enrollmentDateField'"/>
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
					  <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate'); return false;">
					    <img name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
					  </a>
					</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>

	<tr valign="top">
        <c:if test="${study.studyParameterConfig.genderRequired != 'false'}">
            <td class="formlabel">${study.studyParameterConfig.genderLabel}:</td>
            <td valign="top">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td valign="top"><div class="formfieldS_BG">
                                <select name="gender" class="formfieldS">
                                    <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
                                    <c:choose>
                                        <c:when test="${!empty chosenGender}">
                                            <c:choose>
                                                <c:when test='${chosenGender == "m"}'>
                                                    <option value="m" selected><fmt:message key="male" bundle="${resword}"/></option>
                                                    <option value="f"><fmt:message key="female" bundle="${resword}"/></option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="m"><fmt:message key="male" bundle="${resword}"/></option>
                                                    <option value="f" selected><fmt:message key="female" bundle="${resword}"/></option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="m"><fmt:message key="male" bundle="${resword}"/></option>
                                            <option value="f"><fmt:message key="female" bundle="${resword}"/></option>
                                        </c:otherwise>
                                    </c:choose>
                                </select>
                        </td>
                        <td align="left">
                            <c:choose>
                            <c:when test="${study.studyParameterConfig.genderRequired != 'false'}">
                               <span class="formlabel">*</span>
                            </c:when>
                            </c:choose>
                            <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                                <a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=gender&column=gender','spanAlert-gender'); return false;">
                                <img name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a>
                            </c:if>
                        </td>
                    </tr>

                    <tr>
                    <td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include></td>
                    </tr>

                </table>
            </td>
            </td>
        </c:if>
    </tr>

	<c:choose>
	<c:when test="${study.studyParameterConfig.collectDob == '1'}">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
                        <div class="formfieldM_BG">
						    <input onfocus="this.select()" type="text" name="dob" size="15" value="<c:out value="${dob}" />" class="formfieldM" id="dobField" />
                        </div>
					</td>
					<td valign="top">
                      &nbsp;&nbsp;*&nbsp;&nbsp;
						<ui:calendarIcon onClickSelector="'#dobField'"/>
                    </td>
					<td valign="top">
					<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=dob&column=date_of_birth','spanAlert-dob'); return false;">
                        &nbsp;<img name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dob"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
	</c:when>
	<c:when test="${study.studyParameterConfig.collectDob == '2'}">
	<tr valign="top">
		<td class="formlabel"><fmt:message key="year_of_birth" bundle="${resword}"/>:</td>
	  	<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
                        <div class="formfieldM_BG">
						    <input onfocus="this.select()" type="text" name="yob" size="15" value="<c:out value="${yob}" />" class="formfieldM" />
                        </div>
					</td>
					<td valign="top">(<fmt:message key="date_format_year" bundle="${resformat}"/>) * <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}"><a href="#" onClick="openDSNoteWindow('CreateDiscrepancyNote?name=subject&field=yob&column=date_of_birth','spanAlert-yob'); return false;">
					<img name="flag_yob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"></a></c:if></td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="yob"/></jsp:include></td>
				</tr>
			</table>
	  	</td>
	</tr>
  </c:when>
  <c:otherwise>
    <input type="hidden" name="dob" value="" />
  </c:otherwise>
 </c:choose>

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>

<c:if test="${(!empty groups)||(!empty dynamicGroups)}">
<br>
<div style="width: 550px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">
  <c:if test="${!empty dynamicGroups}">
  <tr valign="top">
	<td class="formlabel"><fmt:message key="dynamic_group_class" bundle="${resword}"/>:</td>
	<td class="table_cell">
	<table border="0" cellpadding="0">
		<tr><td>&nbsp;</td></tr>
		<tr valign="top">
			<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
			<div class="formfieldM_BG">
				<c:set var="selectable" value="${study.studyParameterConfig.allowDynamicGroupsManagement == 'yes' ? '' : 'disabled'}"/>
				<select name="dynamicGroupClassId" class="formfieldM" onChange="showDynamicEventsSection(${defaultDynGroupClassId});" ${selectable}>
					<option value="0"><fmt:message key="default_group" bundle="${resword}"/></option>
					<c:forEach var="dynGroup" items="${dynamicGroups}">
					<c:if test="${dynGroup.id != defaultDynGroupClassId}">
					<c:choose>
						<c:when test="${dynGroup.id == selectedDynGroupClassId}">
							<option value="<c:out value="${dynGroup.id}" />" selected><c:out value="${dynGroup.name}"/></option>
						</c:when>
						<c:otherwise>
							<option value="<c:out value="${dynGroup.id}"/>"><c:out value="${dynGroup.name}"/></option>
						</c:otherwise>
					</c:choose>
					</c:if>
					</c:forEach>
				</select>
			</div>
			</td>
		</tr>
		<tr style="display: none" id="defaultDynGroupName">
			<td>&nbsp;<fmt:message key="name" bundle="${resword}"/>:&nbsp;</td>
			<c:choose>
				<c:when test="${defaultDynGroupClassName != ''}">
					<td>&nbsp;&nbsp;<c:out value="${defaultDynGroupClassName}"/></td>
				</c:when>
				<c:otherwise>
					<td>&nbsp;<fmt:message key="none" bundle="${resword}"/>&nbsp;</td>
				</c:otherwise>
			</c:choose>
		</tr>
		<c:forEach var="dynGroup" items="${dynamicGroups}">
		<tr style="display: none" id="dynamicGroupId${dynGroup.id}">
			<td>&nbsp;<fmt:message key="events" bundle="${resword}"/>:&nbsp;</td>
			<td>
				<table width="75%" cellspacing="0" cellpadding="0" border="1">
				<c:forEach var="studyEvDef" items="${dynGroup.eventDefinitions}">
					<tr>
						<td class="table_cell">&nbsp;&nbsp;<c:out value="${studyEvDef.name}"/></td>
					</tr>
				</c:forEach>
				</table>
			</td>
		</tr>	
		</c:forEach>
		<tr><td>&nbsp;</td></tr>
	</table>
	</td>
  </tr>
  </c:if>
  <c:if test="${!empty groups}">
  <tr valign="top">
	<td class="formlabel"><fmt:message key="subject_group_class" bundle="${resword}"/>: </td>
	<td class="table_cell">
		<c:set var="count" value="0"/>
		<table border="0" cellpadding="0">
			<tr><td>&nbsp;</td></tr>
			<c:forEach var="group" items="${groups}">
			<tr valign="top">
				<td><b>&nbsp;<c:out value="${group.name}"/>&nbsp;</b></td>
				<td><div class="formfieldM_BG">
					<select name="studyGroupId<c:out value="${count}"/>" class="formfieldM">
						<option value="0">--</option>
						<c:forEach var="sg" items="${group.studyGroups}">
							<c:choose>
								<c:when test="${group.studyGroupId == sg.id}">
									<option value="<c:out value="${sg.id}" />" selected><c:out value="${sg.name}"/></option>
								</c:when>
								<c:otherwise>
									<option value="<c:out value="${sg.id}"/>"><c:out value="${sg.name}"/></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></div>
					<c:import url="../showMessage.jsp"><c:param name="key" value="studyGroupId${count}" /></c:import>
				</td>
	    	<c:if test="${group.subjectAssignment=='Required'}">
				<td align="left">*</td>
	    	</c:if>
	    	</tr>
	    	<tr valign="top">
				<td>&nbsp;<fmt:message key="notes" bundle="${resword}"/>:&nbsp;</td>
				<td>
					<div class="formfieldXL_BG"><input onfocus="this.select()" type="text" class="formfieldXL" name="notes<c:out value="${count}"/>"  value="<c:out value="${group.groupNotes}"/>"></div>
					<c:import url="../showMessage.jsp"><c:param name="key" value="notes${count}" /></c:import>
				</td>
			</tr>
			<c:set var="count" value="${count+1}"/>
			</c:forEach>
		</table>
	</td>
  </tr>
  </c:if>

</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
</c:if>
<br>
<table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td><input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" style="float: left; cursor: pointer;"/></td>
        <td><input type="submit" name="submitDone" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" style="float: left; cursor: pointer; margin-left: 10px;"></td>
        <td><input type="button" name="StartDataEntry" value="<fmt:message key="start_data_entry" bundle="${resword}"/>" class="button_medium" onClick="$('#openFirstCrf').val('true'); $('input[name=submitDone]').click();" style="float: left; cursor: pointer;"/></td>
        <td><input type="submit" name="submitEvent" value="<fmt:message key="schedule_events" bundle="${restext}"/>" class="button_medium" style="float: left; cursor: pointer; margin-left: 10px;"></td>
        <td><input type="submit" name="submitEnroll" value="<fmt:message key="add_next_subject" bundle="${restext}"/>" class="button_medium" style="float: left; cursor: pointer; margin-left: 10px;"></td>
        <td><input type="button" onclick="confirmCancel('ListStudySubjects');"  name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium medium_cancel" style="float: left; cursor: pointer; margin-left: 10px;"/></td>
    </tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;z-index:10;background-color:white;layer-background-color:white;"></DIV>

<br>

<c:import url="instructionsSetupStudyEvent.jsp">
	<c:param name="currStep" value="enroll" />
</c:import>

<jsp:include page="../include/footer.jsp"/>
