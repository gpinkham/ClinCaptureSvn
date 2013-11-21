<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:import url="../include/admin-header.jsp"/>


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


<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectToRemove' class='org.akaza.openclinica.bean.submit.SubjectBean'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_removal_of_subject" bundle="${resword}"/>
	</span>
</h1>

<div class="table_title_Admin"><fmt:message key="you_choose_to_remove_the_following_subject" bundle="${restext}"/>:</div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top" ><td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${subjectToRemove.uniqueIdentifier}"/>
   </td></tr>

  <c:set var="genderShow" value="${true}"/>
  <fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
  <c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
  </c:if>
  <c:if test="${genderShow}">
      <tr valign="top">
        <td class="table_header_column">${genderLabel}:</td>
        <td class="table_cell"><c:out value="${subjectToRemove.gender}"/></td>
      </tr>
  </c:if>

  <tr valign="top"><td class="table_header_column"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${subjectToRemove.dateOfBirth}" pattern="${dteFormat}"/>
  </td>
  <tr valign="top"><td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/>:</td><td class="table_cell">
  <fmt:formatDate value="${subjectToRemove.createdDate}" pattern="${dteFormat}"/>
  </td>
  </tr>
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br/><br/>
<div class="table_title_Admin"> <fmt:message key="associated_study_subjects" bundle="${resword}"/> </div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">

    <fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
    <c:if test="${study ne null}">
        <c:set var="studySubjectIdLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
    </c:if>
    <td class="table_header_column_top">${studySubjectIdLabel}</td>

    <c:set var="secondaryIdShow" value="${true}"/>
    <fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel"/>
    <c:if test="${study ne null}">
         <c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
         <c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}"/>
    </c:if>
    <c:if test="${secondaryIdShow}">
        <td class="table_header_column_top">${secondaryIdLabel}</td>
    </c:if>

    <td class="table_header_column_top"><fmt:message key="study_record_ID" bundle="${resword}"/></td>

    <c:set var="enrollmentDateShow" value="${true}"/>
    <fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel"/>
    <c:if test="${study ne null}">
        <c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}"/>
        <c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}"/>
    </c:if>
    <c:if test="${enrollmentDateShow}">
        <td class="table_header_column_top">${enrollmentDateLabel}</td>
    </c:if>

    <td class="table_header_column_top"><fmt:message key="date_created" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="created_by" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
    </tr>
  <c:forEach var="studySub" items="${studySubs}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${studySub.label}"/></td>
    <c:if test="${secondaryIdShow}">
        <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/>&nbsp;</td>
    </c:if>
    <td class="table_cell"><c:out value="${studySub.studyId}"/></td>
    <c:if test="${enrollmentDateShow}">
        <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}"/></td>
    </c:if>
    <td class="table_cell"><fmt:formatDate value="${studySub.createdDate}" pattern="${dteFormat}"/></td>
    <td class="table_cell"><c:out value="${studySub.owner.name}"/></td>
    <td class="table_cell"><c:out value="${studySub.status.name}"/></td>
    </tr>
 </c:forEach>
</table>
</div>

</div></div></div></div></div></div></div></div>
</div>
<br><br>
<div class="table_title_Admin"><fmt:message key="associated_study_events" bundle="${resword}"/></div>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
 <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="record_ID" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="location" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="date_started" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="created_by" bundle="${resword}"/></td>
    <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
    </tr>
  <c:forEach var="event" items="${events}">
    <tr valign="top">
    <td class="table_cell"><c:out value="${event.id}"/></td>
    <td class="table_cell"><c:out value="${event.location}"/>&nbsp;</td>
    <td class="table_cell"><fmt:formatDate value="${event.createdDate}" pattern="${dteFormat}"/></td>
    <td class="table_cell"><c:out value="${event.owner.name}"/></td>
    <td class="table_cell"><c:out value="${event.status.name}"/></td>
    </tr>
 </c:forEach>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<form action='RemoveSubject?action=submit&id=<c:out value="${subjectToRemove.id}"/>' method="POST">
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
<input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium" onClick='return confirm("<fmt:message key="if_you_remove_this_subject" bundle="${restext}"/>");'>
</form>
<br><br>

<jsp:include page="../include/footer.jsp"/>
