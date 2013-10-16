<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/admin-header.jsp"/>

<jsp:include page="../include/sideAlert.jsp"/>

<c:set var="dictionaries">MedDRA,ICD9,ICD10</c:set>
<c:set var="selectedDictionary" value="${studyToView.studyParameterConfig.defaultMedicalCodingDictionary}"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
        <fmt:message key="enter_the_study_and_protocol" bundle="${resword}"/>
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

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope ="request" id="studyPhaseMap" class="java.util.HashMap"/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<script type="text/JavaScript" language="JavaScript">
  <!--
<%-- function myCancel() {

    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListStudy";
       return true;
      } else {
        return false;
       }
     }
     return true;

  }--%>
   //-->
</script>
<h1><span class="title_manage"><fmt:message key="create_a_new_study_continue" bundle="${resword}"/></span></h1>

<span class="title_Admin"><p><b><fmt:message key="section_f_study_parameter_configuration" bundle="${resword}"/> </b></p></span>
<P>* <fmt:message key="indicates_required_field" bundle="${resword}"/></P>

<form action="CreateStudy" method="post">
<input type="hidden" name="action" value="confirm">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">

  <tr valign="top"><td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/>:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '1'}">
    <input type="radio" checked name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
     <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
   <c:when test="${newStudy.studyParameterConfig.collectDob == '2'}">
    <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
     <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
    <input type="radio" checked name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.discrepancyManagement == 'false'}">
    <input type="radio" name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" checked name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>
  <tr><td>&nbsp;</td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="subject_person_ID_required" bundle="${resword}"/>:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'required'}">
    <input type="radio" checked name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
    <c:when test="${newStudy.studyParameterConfig.subjectPersonIdRequired == 'optional'}">
    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
    <input type="radio" checked name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
    <input type="radio" name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
    <input type="radio" name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
    <input type="radio" checked name="subjectPersonIdRequired" value="not used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="show_person_id_on_crf_header" bundle="${resword}"/>:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.personIdShownOnCRF == 'true'}">
    <input type="radio" checked name="personIdShownOnCRF" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="personIdShownOnCRF" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="personIdShownOnCRF" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="personIdShownOnCRF" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:otherwise>
  </c:choose>
  </td>
  </tr>

   <tr valign="top"><td class="formlabel"><fmt:message key="how_to_generate_the_study_subject_ID" bundle="${resword}"/>:</td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'manual'}">
    <input type="radio" checked name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
   </c:when>
    <c:when test="${newStudy.studyParameterConfig.subjectIdGeneration == 'auto editable'}">
    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
    <input type="radio" checked name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
    <input type="radio" name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
    <input type="radio" name="subjectIdGeneration" value="auto editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
    <input type="radio" checked name="subjectIdGeneration" value="auto non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

   <tr><td>&nbsp;</td></tr>
   <tr valign="top"><td class="formlabel"><fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/></td><td>
   <c:choose>
       <c:when test="${newStudy.studyParameterConfig.interviewerNameRequired== 'yes'}">
        <input type="radio" checked name="interviewerNameRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
        <input type="radio" name="interviewerNameRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
        <input type="radio" name="interviewerNameRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
       </c:when>
       <c:when test="${newStudy.studyParameterConfig.interviewerNameRequired== 'no'}">
           <input type="radio" name="interviewerNameRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
           <input type="radio" checked name="interviewerNameRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
           <input type="radio" name="interviewerNameRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
       </c:when>
       <c:otherwise>
        <input type="radio" name="interviewerNameRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
        <input type="radio" name="interviewerNameRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
        <input type="radio" checked name="interviewerNameRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
       </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewerNameDefault== 'blank'}">
    <input type="radio" checked name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
    <input type="radio" name="interviewerNameDefault" value="pre-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="interviewerNameDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
    <input type="radio" checked name="interviewerNameDefault" value="re-populated"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewerNameEditable== 'true'}">
    <input type="radio" checked name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_required" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateRequired== 'yes'}">
    <input type="radio" checked name="interviewDateRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="interviewDateRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
    <input type="radio" name="interviewDateRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
   <c:when test="${newStudy.studyParameterConfig.interviewDateRequired== 'no'}">
    <input type="radio" name="interviewDateRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="interviewDateRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
    <input type="radio" name="interviewDateRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="interviewDateRequired" value="yes"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="interviewDateRequired" value="no"><fmt:message key="no" bundle="${resword}"/>
    <input type="radio" checked name="interviewDateRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_default_as_blank" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateDefault== 'blank'}">
    <input type="radio" checked name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
    <input type="radio" name="interviewDateDefault" value="pre-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="interviewDateDefault" value="blank"><fmt:message key="blank" bundle="${resword}"/>
    <input type="radio" checked name="interviewDateDefault" value="re-populated"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="interviewer_date_editable" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.interviewDateEditable== 'true'}">
    <input type="radio" checked name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>


  <tr valign="top"><td class="formlabel"><fmt:message key="secondary_label_viewable" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${studyToView.studyParameterConfig.secondaryLabelViewable== 'true'}">
    <input type="radio" checked name="secondaryLabelViewable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="secondaryLabelViewable" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="secondaryLabelViewable" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="secondaryLabelViewable" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <%-- adding forced reason for change here, tbh --%>

  <tr valign="top"><td class="formlabel"><fmt:message key="forced_reason_for_change" bundle="${resword}"/></td><td>
   <c:choose>
   <c:when test="${newStudy.studyParameterConfig.adminForcedReasonForChange== 'true'}">
    <input type="radio" checked name="adminForcedReasonForChange" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="adminForcedReasonForChange" value="false"><fmt:message key="no" bundle="${resword}"/>

   </c:when>
   <c:otherwise>
    <input type="radio" name="adminForcedReasonForChange" value="true"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="adminForcedReasonForChange" value="false"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td>
  </tr>

  <tr>
      <td>&nbsp;</td>
  </tr>
  <tr valign="top" style="border: 1px solid black;width: 100%;">
      <td class="formlabel" style="border-top: 1px solid black;text-align: left;">
          <fmt:message key="subjectForm" bundle="${resword}"/>:
      </td><td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="studySubjectIdLabel" bundle="${resword}"/>
      </td>
      <td>
          <input type="text" name="studySubjectIdLabel" value="${studyToView.studyParameterConfig.studySubjectIdLabel}" maxlength="255" size="35">
          <br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studySubjectIdLabel"/></jsp:include>
      </td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="secondaryIdLabel" bundle="${resword}"/>
      </td>
      <td>
          <input type="text" name="secondaryIdLabel" value="${studyToView.studyParameterConfig.secondaryIdLabel}" maxlength="255" size="35">
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
                  <input type="radio" checked name="secondaryIdRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="secondaryIdRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="secondaryIdRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>
              <c:when test="${studyToView.studyParameterConfig.secondaryIdRequired == 'no'}">
                  <input type="radio" name="secondaryIdRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" checked name="secondaryIdRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="secondaryIdRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>

              <c:otherwise>
                  <input type="radio" name="secondaryIdRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="secondaryIdRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" checked name="secondaryIdRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:otherwise>
          </c:choose>
      </td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="dateOfEnrollmentForStudyLabel" bundle="${resword}"/>
      </td>
      <td>
          <input type="text" name="dateOfEnrollmentForStudyLabel" value="${studyToView.studyParameterConfig.dateOfEnrollmentForStudyLabel}" maxlength="255" size="35">
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
                  <input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>
              <c:when test="${studyToView.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'no'}">
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>

              <c:otherwise>
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="dateOfEnrollmentForStudyRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" checked name="dateOfEnrollmentForStudyRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:otherwise>
          </c:choose>
      </td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="genderLabel" bundle="${resword}"/>
      </td>
      <td>
          <input type="text" name="genderLabel" value="${studyToView.studyParameterConfig.genderLabel}" maxlength="255" size="35">
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
                  <input type="radio" name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" checked name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
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
          <input type="text" name="startDateTimeLabel" value="${studyToView.studyParameterConfig.startDateTimeLabel}" maxlength="255" size="35">
          <br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDateTimeLabel"/></jsp:include>
      </td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="endDateTimeLabel" bundle="${resword}"/>
      </td>
      <td>
          <input type="text" name="endDateTimeLabel" value="${studyToView.studyParameterConfig.endDateTimeLabel}" maxlength="255" size="35">
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
                  <input type="radio" checked name="startDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="startDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="startDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>
              <c:when test="${studyToView.studyParameterConfig.startDateTimeRequired == 'no'}">
                  <input type="radio" name="startDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" checked name="startDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="startDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>

              <c:otherwise>
                  <input type="radio" name="startDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="startDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" checked name="startDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

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
                  <input type="radio" checked name="endDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="endDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="endDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>
              <c:when test="${studyToView.studyParameterConfig.endDateTimeRequired == 'no'}">
                  <input type="radio" name="endDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" checked name="endDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" name="endDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

              </c:when>

              <c:otherwise>
                  <input type="radio" name="endDateTimeRequired" value="yes"><fmt:message key="required" bundle="${resword}"/>
                  <input type="radio" name="endDateTimeRequired" value="no"><fmt:message key="optional" bundle="${resword}"/>
                  <input type="radio" checked name="endDateTimeRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>

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
                  <input type="radio" checked name="useStartTime" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="useStartTime" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="useStartTime" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="useStartTime" value="no"><fmt:message key="no" bundle="${resword}"/>
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
                  <input type="radio" checked name="useEndTime" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="useEndTime" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="useEndTime" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="useEndTime" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:otherwise>
          </c:choose>
      </td>
  </tr>

  <tr>
      <td>&nbsp;</td>
  </tr>
  <tr valign="top" style="border: 1px solid black;width: 100%;">
      <td class="formlabel" style="border-top: 1px solid black;text-align: left;">
          &nbsp;
      </td><td style=" border-top: 1px solid black; text-align: left;">&nbsp;</td>

  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="markImportedCRFAsCompleted" bundle="${resword}"/>
      </td>
      <td>
          <c:choose>
              <c:when test="${studyToView.studyParameterConfig.markImportedCRFAsCompleted == 'yes'}">
                  <input type="radio" checked name="markImportedCRFAsCompleted" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="markImportedCRFAsCompleted" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="markImportedCRFAsCompleted" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="markImportedCRFAsCompleted" value="no"><fmt:message key="no" bundle="${resword}"/>
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
                  <input type="radio" checked name="allowSdvWithOpenQueries" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="allowSdvWithOpenQueries" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="allowSdvWithOpenQueries" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="allowSdvWithOpenQueries" value="no"><fmt:message key="no" bundle="${resword}"/>
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
                  <input type="radio" checked name="replaceExisitingDataDuringImport" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="replaceExisitingDataDuringImport" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="replaceExisitingDataDuringImport" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="replaceExisitingDataDuringImport" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:otherwise>
          </c:choose>
      </td>
  </tr>

  <tr valign="top">
    <td class="formlabel">
        <fmt:message key="defaultMedicalCodingDictionary" bundle="${resword}"/>
    </td>
    <td>
      <c:choose>
        <c:when test="${studyToView.studyParameterConfig.defaultMedicalCodingDictionary == ''}">
          <select id="dictionaries" name="defaultMedicalCodingDictionary">
            <option value="0"></option>
            <c:forTokens items="${dictionaries}" delims="," var="dictionary">
              <option value="${dictionary}">${dictionary}</option>
            </c:forTokens>
          </select>
          </c:when>
          <c:otherwise>
            <select id="dictionaries" name="defaultMedicalCodingDictionary">
                <option value="0"></option>
                <c:forTokens items="${dictionaries}" delims="," var="dictionary">
                  <option value="${dictionary}" ${dictionary == selectedDictionary ? 'selected' : ''}>${dictionary}</option>
              </c:forTokens>
            </select>
          </c:otherwise>
        </c:choose>
      </td>
  </tr>

  <tr valign="top">
    <td class="formlabel">
        <fmt:message key="autoCodeDictionaryName" bundle="${resword}"/>:
    </td>
    <td>
        <input type="text" name="autoCodeDictionaryName" value="${studyToView.studyParameterConfig.autoCodeDictionaryName}" maxlength="255" size="35">
        <c:set var="autoCodeDictionaryName" value="${studyToView.studyParameterConfig.autoCodeDictionaryName}"/>
        <jsp:include page="../showMessage.jsp">
          <jsp:param name="key" value="autoCodeDictionaryName"/>
        </jsp:include>
    </td>
  </tr>

  <tr valign="top">
      <td class="formlabel">
          <fmt:message key="allowCodingVerification" bundle="${resword}"/>
      </td>
      <td>
          <c:choose>
              <c:when test="${studyToView.studyParameterConfig.allowCodingVerification == 'yes'}">
                  <input type="radio" checked name="allowCodingVerification" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" name="allowCodingVerification" value="no"><fmt:message key="no" bundle="${resword}"/>
              </c:when>
              <c:otherwise>
                  <input type="radio" name="allowCodingVerification" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                  <input type="radio" checked name="allowCodingVerification" value="no"><fmt:message key="no" bundle="${resword}"/>
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
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
 <input type="submit" name="Submit" value="<fmt:message key="confirm_study" bundle="${resword}"/>" class="button_long">
</td>
<td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="myCancel('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');"/></td>
</tr>
</table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b><fmt:message key="workflow" bundle="${resword}"/></b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
		</tr>
		<tr>
			<td colspan="2" class="workflowbox_B">
			<div class="box_R"><div class="box_B"><div class="box_BR">
				<div class="workflowBox_center">


		<!-- Workflow items -->

				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">

							<fmt:message key="study_title_description" bundle="${resword}"/><br><br>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">


							<fmt:message key="status_and_design" bundle="${resword}"/>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">

					        <fmt:message key="conditions_and_eligibility" bundle="${resword}"/><br><br>

							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">


							 <fmt:message key="specify_facility" bundle="${resword}"/>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">


							 <fmt:message key="other_related_information" bundle="${resword}"/>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">


							 <b><fmt:message key="study_parameter_configuration" bundle="${resword}"/></b>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_admin">


							 <fmt:message key="confirm_and_submit_study" bundle="${resword}"/>


							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>

					</tr>
				</table>


		<!-- end Workflow items -->

				</div>
			</div></div></div>
			</td>
		</tr>
	</table>
	</td>
   </tr>
</table>

<!-- END WORKFLOW BOX -->

<jsp:include page="../include/footer.jsp"/>
