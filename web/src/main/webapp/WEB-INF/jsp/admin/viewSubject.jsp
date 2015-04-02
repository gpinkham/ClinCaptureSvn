<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/admin-header.jsp"/>


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

<jsp:useBean scope='request' id='subject' class='org.akaza.openclinica.bean.submit.SubjectBean'/>
<jsp:useBean scope='request' id='studySubs' class='java.util.ArrayList'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="view_subject_details" bundle="${resword}"/>
	</span>
</h1>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="tablebox_center">
			
			<table border="0" cellpadding="0" cellspacing="0">
				<c:if test="${study.studyParameterConfig.subjectPersonIdRequired != 'not used'}">
				<tr valign="top">
					<td class="table_header_column_top">
						<fmt:message key="person_ID" bundle="${resword}" />:</td>
					<td class="table_cell_top">
						<c:out value="${subject.uniqueIdentifier}" />&nbsp;</td>
				</tr>
				</c:if>
			
				<c:set var="genderShow" value="${true}" />
				<fmt:message key="gender" bundle="${resword}" var="genderLabel" />
				<c:if test="${study ne null}">
					<c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}" />
					<c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}" /></c:if>
			
				<c:if test="${genderShow}">
				<tr valign="top">
					<td class="table_header_column">${genderLabel}:</td>
					<td class="table_cell">
						<c:out value="${subject.gender}" />
					</td>
				</tr>
				</c:if>
			
				<tr valign="top">
					<td class="table_header_column">
						<fmt:message key="date_of_birth" bundle="${resword}" />:</td>
					<td class="table_cell">
						<fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}" />
					</td>
				</tr>
			
				<tr valign="top">
					<td class="table_header_column">
						<fmt:message key="date_created" bundle="${resword}" />:</td>
					<td class="table_cell">
						<fmt:formatDate value="${subject.createdDate}" pattern="${dteFormat}" />
					</td>
				</tr>
			</table>
			
			</div>
			</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>
<br/><br/>

<div class="table_title_Admin"> <fmt:message key="associated_study_subjects" bundle="${resword}"/></div>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table border="0" cellpadding="0" cellspacing="0">
	<tr valign="top">
		<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel" />
		<c:if test="${study ne null}">
			<c:set var="studySubjectIdLabel" value="${study.studyParameterConfig.studySubjectIdLabel}" /></c:if>

		<td class="table_header_row_left">${studySubjectIdLabel}</td>
		<c:set var="secondaryIdShow" value="${true}" />
		<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel" />

		<c:if test="${study ne null}">
			<c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}" />
			<c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}" /></c:if>
		<c:if test="${secondaryIdShow}">
			<td class="table_header_row">${secondaryIdLabel}</td>
		</c:if>

		<td class="table_header_row">
			<fmt:message key="study_ID" bundle="${resword}" />
		</td>
		<c:set var="enrollmentDateShow" value="${true}" />
		<fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel" />

		<c:if test="${study ne null}">
			<c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}" />
			<c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}" /></c:if>

		<c:if test="${enrollmentDateShow}">
			<td class="table_header_row">${enrollmentDateLabel}</td>
		</c:if>

		<td class="table_header_row">
			<fmt:message key="date_created" bundle="${resword}" />
		</td>

		<td class="table_header_row">
			<fmt:message key="created_by" bundle="${resword}" />
		</td>

		<td class="table_header_row">
			<fmt:message key="status" bundle="${resword}" />
		</td>
	</tr>

	<c:forEach var="studySub" items="${studySubs}">
	<tr valign="top">
		<td class="table_cell_left">
			<c:out value="${studySub.label}" />
		</td>

		<c:if test="${secondaryIdShow}">
		<td class="table_cell">
			<c:out value="${studySub.secondaryLabel}" />&nbsp;</td>
		</c:if>

		<td class="table_cell">
			<c:out value="${studySub.studyId}" />
		</td>

		<c:if test="${enrollmentDateShow}">
		<td class="table_cell">
			<fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}" />
		</td>
		</c:if>

		<td class="table_cell">
			<fmt:formatDate value="${studySub.createdDate}" pattern="${dteFormat}" />
		</td>

		<td class="table_cell">
			<c:out value="${studySub.owner.name}" />
		</td>

		<td class="table_cell">
			<c:out value="${studySub.status.name}" />
		</td>
	</tr>
	</c:forEach>
</table>

</div>
</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>
<br>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
  <c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
 </c:import>
<jsp:include page="../include/footer.jsp"/>
