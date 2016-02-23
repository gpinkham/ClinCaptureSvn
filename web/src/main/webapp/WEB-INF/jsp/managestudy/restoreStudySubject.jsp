<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/managestudy-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
         <fmt:message key="confirm_restoration_of_this_subject_to_study"  bundle="${resword}"/> <c:out value="${subjectStudy.name}"/>. <fmt:message key="the_subject_and_all_data_associated_with_it_in" bundle="${resword}"/> 
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
		<fmt:message key="restore_subject_to_study" bundle="${resword}"/>
	</span>
</h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
<c:if test="${subjectStudy ne null}">
   <c:set var="studySubjectIDLabel" value="${subjectStudy.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">

	<tr valign="top">
		<td class="table_header_column">${studySubjectIDLabel}:</td>
		<td class="table_cell"><c:out value="${studySub.label}"/></td>
	</tr>

	<c:if test="${subjectStudy.studyParameterConfig.subjectPersonIdRequired != 'not used'}">
		<tr valign="top">
			<td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
        	<td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td>
		</tr>
	</c:if>


  <c:set var="genderShow" value="${true}"/>
  <fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
  <c:if test="${subjectStudy ne null}">
      <c:set var="genderShow" value="${!(subjectStudy.studyParameterConfig.genderRequired == 'false')}"/>
      <c:set var="genderLabel" value="${subjectStudy.studyParameterConfig.genderLabel}"/>
  </c:if>
  <c:if test="${genderShow}">
      <tr valign="top">
          <td class="table_header_column">${genderLabel}:</td>
          <td class="table_cell"><c:out value="${subject.gender}"/></td>
      </tr>
  </c:if>

  <%-- <tr valign="top"><td class="table_header_column"><fmt:message key="label" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.label}"/></td></tr> --%>

  <c:set var="secondaryIdShow" value="${true}"/>
  <fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel"/>
  <c:if test="${subjectStudy ne null}">
      <c:set var="secondaryIdShow" value="${!(subjectStudy.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
      <c:set var="secondaryIdLabel" value="${subjectStudy.studyParameterConfig.secondaryIdLabel}"/>
  </c:if>
  <c:if test="${secondaryIdShow}">
      <tr valign="top">
          <td class="table_header_column">${secondaryIdLabel}:</td>
          <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/></td>
      </tr>
  </c:if>

  <c:set var="enrollmentDateShow" value="${true}"/>
  <fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel"/>
  <c:if test="${subjectStudy ne null}">
      <c:set var="enrollmentDateShow" value="${!(subjectStudy.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}"/>
      <c:set var="enrollmentDateLabel" value="${subjectStudy.studyParameterConfig.dateOfEnrollmentForStudyLabel}"/>
  </c:if>
  <c:if test="${enrollmentDateShow}">
      <tr valign="top">
          <td class="table_header_column">${enrollmentDateLabel}:</td>
          <td class="table_cell">
              <cc-fmt:formatDate value="${studySub.enrollmentDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
          </td>
      </tr>
  </c:if>

  <tr valign="top"><td class="table_header_column"><fmt:message key="created_by" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.owner.name}"/></td></tr>
  <tr valign="top">
	  <td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td>
	  <td class="table_cell">
		  <cc-fmt:formatDate value="${studySub.createdDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
  </tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${studySub.updater.name}"/>&nbsp;
  </td></tr>
  <tr valign="top">
	  <td class="table_header_column"><fmt:message key="date_updated" bundle="${resword}"/>:</td>
	  <td class="table_cell">
		  <cc-fmt:formatDate value="${studySub.updatedDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
		  &nbsp;
	  </td>
  </tr>
 </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <c:if test="${!empty events}">
 <span class="table_title_manage"><fmt:message key="subject_events" bundle="${resword}"/> </span>
 <div style="width: 600px">
   <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

   <div class="tablebox_center">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td class="table_header_row"><fmt:message key="last_update" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="event" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="start_date" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="end_date" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="location" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="update_by" bundle="${resword}"/></td>
        <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
      </tr>
   
       <c:forEach var="displayEvents" items="${events}">
       <tr>
           <td class="table_cell">
			   <cc-fmt:formatDate value="${displayEvents.studyEvent.updatedDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
		   </td>
           <td class="table_cell"><c:out value="${displayEvents.studyEvent.studyEventDefinition.name}"/>
           <c:if test="${displayEvents.studyEvent.studyEventDefinition.repeating}">
               (<c:out value="${displayEvents.studyEvent.sampleOrdinal}"/>)
           </c:if>
           </td>
           <td class="table_cell">
			   <cc-fmt:formatDate value="${displayEvents.studyEvent.dateStarted}" dateTimeZone="${userBean.userTimeZoneId}"/>
		   </td>
           <td class="table_cell">
			   <cc-fmt:formatDate value="${displayEvents.studyEvent.dateEnded}" dateTimeZone="${userBean.userTimeZoneId}"/>
		   </td>
           <td class="table_cell"><c:out value="${displayEvents.studyEvent.location}"/></td>
           <td class="table_cell"><c:out value="${displayEvents.studyEvent.updater.name}"/></td>
           <td class="table_cell"><c:out value="${displayEvents.studyEvent.status.name}"/></td>
        </tr>
        </c:forEach>
   </table>  
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
   </c:if>  
   
   <c:choose>
    <c:when test="${!empty events}">
     <form action='RestoreStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
       <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
       <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<fmt:message key="this_subject_has_data_from_events_restore" bundle="${resword}"/>", height: 150, width: 500, submit: this });'>
     </form>   
    </c:when>
    <c:otherwise>
     <form action='RestoreStudySubject?action=submit&id=<c:out value="${studySub.id}"/>&subjectId=<c:out value="${studySub.subjectId}"/>&studyId=<c:out value="${studySub.studyId}"/>' method="POST">
       <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
       <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<fmt:message key="are_you_sure_you_want_to_restore_it" bundle="${resword}"/>", height: 150, width: 500, submit: this });'>
     </form> 
     
    </c:otherwise>
   </c:choose>  
 

<jsp:include page="../include/footer.jsp"/>
