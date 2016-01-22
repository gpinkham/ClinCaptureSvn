<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/home-page-with-charts-header.jsp"/>
<jsp:include page="../include/sideAlert.jsp"/>

<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="confirm_deletion_of_study_event_definition_header"  bundle="${resword}"/>.
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>


<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_deletion_of_study_event_definition_header" bundle="${resword}"/>
	</span>
</h1>

<p class="alert"><fmt:message key="confirm_deletion_of_study_event_definition_sub_header" bundle="${resword}"/><br/><br/></p>

<table class="table_vertical table_shadow_bottom">
	<tr>
		<td width="500px"><fmt:message bundle="${resword}" key="name"/></td>
		<td width="150px">${event.name}</td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="description"/></td>
		<td>${event.description}</td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="repeating"/></td>
		<td><fmt:message bundle="${resword}" key="${event.repeating}"/></td>
	</tr>
	<tr>
		<td><fmt:message bundle="${resword}" key="type"/></td>
		<td><fmt:message key="${event.type}" bundle="${resword}"/></td>
	</tr>
</table>

<c:if test="${fn:length(eventDefinitionCRFs) != 0 and fn:length(studyEventBeans) == 0}">
	<p class="alert"><br/><fmt:message bundle="${resword}" key="confirm_deletion_of_study_event_definition_msg1"/></p>
</c:if>
<c:if test="${fn:length(eventDefinitionCRFs) != 0 and fn:length(studyEventBeans) != 0}">
	<p class="alert"><br/><fmt:message bundle="${resword}" key="confirm_deletion_of_study_event_definition_msg2"/></p>
</c:if>

<c:if test="${fn:length(eventDefinitionCRFs) != 0}">
	<h1>
		<fmt:message bundle="${resword}" key="associated_event_definition_crfs"/>
	</h1>
	<table class="table_horizontal table_shadow_bottom">
		<tr>
			<td width="500px"><fmt:message bundle="${resword}" key="CRF_name"/></td>
			<td width="150px"><fmt:message bundle="${resword}" key="status"/></td>
		</tr>
		<c:forEach items="${eventDefinitionCRFs}" var="eventCRF">
			<tr>
				<td>
					${eventCRF.crf.name}
				</td>
				<td>
					<c:choose>
						<c:when test="${eventCRF.status.id == 1}">
							<c:set var="statusColor" value="aka_green_highlight"/>
						</c:when>
						<c:otherwise>
							<c:set var="statusColor" value="aka_red_highlight"/>
						</c:otherwise>
					</c:choose>
					<span class="${statusColor}">${eventCRF.status.name}</span>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>

<c:if test="${fn:length(studyEventBeans) != 0}">
	<h1>
		<fmt:message bundle="${resword}" key="associated_subjects_study_events"/>
	</h1>
	<table class="table_horizontal table_shadow_bottom">
		<tr>
			<td width="500px"><fmt:message bundle="${resword}" key="subject"/></td>
			<td width="150px"><fmt:message bundle="${resword}" key="status"/></td>
		</tr>
		<c:forEach items="${studyEventBeans}" var="studyEvent">
			<tr>
				<td>
						${studyEvent.studySubject.label}
				</td>
				<td>
					<c:choose>
						<c:when test="${studyEvent.status.id == 1}">
							<c:set var="statusColor" value="aka_green_highlight"/>
						</c:when>
						<c:otherwise>
							<c:set var="statusColor" value="aka_red_highlight"/>
						</c:otherwise>
					</c:choose>
					<span class="${statusColor}"> ${studyEvent.status.name}</span>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>


<form action="deleteEventDefinition" method="POST">
	<input type="hidden" name="id" value="${eventId}"/>
	<input type="hidden" name="confirm" value="true"/>
	<br>
	<table>
		<tr>
			<td>
				<input type="button" name="BTN_Back_Smart"
					   id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>"
					   class="button_medium medium_back"
					   onClick="goBackSmart('${navigationURL}', '${defaultURL}');"/>
			</td>
			<c:if test="${fn:length(eventDefinitionCRFs) == 0 && fn:length(studyEventBeans) == 0}">
				<td>
					<input type="submit" name="submit_button"
						   id="submit_button" value="<fmt:message key="submit" bundle="${resword}"/>"
						   class="button_medium medium_submit"
						   onClick="return confirmSubmit({message:'<fmt:message bundle="${resword}" key="confirm_delete_study_event_definition"/>',
								   width: 500, height: 150, submit: this})"/>
				</td>
			</c:if>
		</tr>
	</table>
</form>

<jsp:include page="../include/footer.jsp"/>

