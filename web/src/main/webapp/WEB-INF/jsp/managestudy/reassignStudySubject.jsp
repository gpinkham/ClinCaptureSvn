<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">
			<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
				<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
			</a>
			<b><fmt:message key="instructions" bundle="${restext}"/></b>
			<div class="sidebar_tab_content"></div>
		</td>
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">
			<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
				<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
			</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<!-- org.akaza.openclinica.bean.core.AuditableEntityBean -->

<h1>
	<span class="first_level_header">
		<fmt:message key="reassign_study_subject" bundle="${resworkflow}"/> 
	</span>
</h1>
<p><fmt:message key="you_choose_to_reassign_the_following_subject2" bundle="${restext}"/></p>
<form action="ReassignStudySubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
 
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL">
<div class="box_BR">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
<c:if test="${study ne null}">
     <c:set var="studySubjectIDLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="table_header_column">${studySubjectIDLabel}</td>
		<td class="table_cell"><c:out value="${studySub.label}"/></td>
	</tr>
 	<c:if test="${subjectStudy.studyParameterConfig.subjectPersonIdRequired != 'not_used'}">
		<tr>
			<td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/></td>
			<td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td>
		</tr>
	</c:if>
	<c:if test="${genderShow}">
		<tr>
			<td class="table_header_column">${genderLabel}</td>
			<td class="table_cell"><c:out value="${subject.gender}"/></td>
		</tr>
	</c:if>
	<tr>
   		<td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/></td>
   		<td class="table_cell">
			<cc-fmt:formatDate value="${subject.createdDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
		</td>
	</tr>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<p><strong><fmt:message key="please_choose_a_study_in_the_following_list2" bundle="${restext}"/></strong></P>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<c:choose> 	 
					<c:when test="${displayStudy.parent.id==studySub.studyId }">           
						<tr>
							<td class="table_cell">
								<input type="radio" checked name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> <fmt:message key="currently_in" bundle="${restext}"/></b>         	 
								&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
						</tr> 
					</c:when> 	 
					<c:otherwise>          
						<c:if test="${displayStudy.status.available}">
							<tr>
								<td class="table_cell">
									<input type="radio" name="studyId" onchange="javascript:changeIcon();" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> </b>
									&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
							</tr> 
						</c:if>
						<c:if test="${displayStudy.status.locked}">
							<tr>
								<td class="table_cell">
									<input type="radio" disabled="true" name="studyId" onchange="javascript:changeIcon();" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> </b>
									&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
							</tr> 
						</c:if>
					</c:otherwise>
					</c:choose>      
					<c:forEach var="child" items="${displayStudy.children}">
						<tr>
							<td class="table_cell">
								<c:choose> 	 
								<c:when test="${child.id==studySub.studyId }">      
									&nbsp;&nbsp;<img src="images/bullet.gif">
									<input type="radio" checked name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/> <fmt:message key="currently_in" bundle="${restext}"/>
									&nbsp;&nbsp;&nbsp;&nbsp;
								</c:when> 	 
								<c:otherwise>          
									<c:if test="${child.status.available}"> 
										&nbsp;&nbsp;<img src="images/bullet.gif">
										<input type="radio" name="studyId" onchange="javascript:changeIcon();" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/>
										&nbsp;&nbsp;&nbsp;&nbsp;
									</c:if>
									<c:if test="${child.status.locked}">
										&nbsp;&nbsp;<img src="images/bullet.gif">
										<input type="radio" disabled="true" name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/>
										&nbsp;&nbsp;&nbsp;&nbsp;
									</c:if>
								</c:otherwise>
								</c:choose>       
							</td>
						</tr>
					</c:forEach>
				</table>
				<br>
			</div></div></div></div></div></div></div>
			</div>
		</td>
	</tr>
</table>
<p>
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
		value="<fmt:message key="back" bundle="${resword}"/>"
		class="button_medium medium_back"
		onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" />
    <input type="submit" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium medium_continue">
</p>
<c:choose>
	<c:when test="${isDataChanged}">      
		<img src="images/icon_UnsavedData.gif" style="visibility:hidden" alt="Data Status" name="DataStatus_bottom">
	</c:when> 	 
	<c:otherwise>          
		<img src="images/icon_UnchangedData.gif" style="visibility:hidden" alt="Data Status" name="DataStatus_bottom">
	</c:otherwise>
</c:choose>

</form>
<br><br>

<jsp:include page="../include/footer.jsp"/>
