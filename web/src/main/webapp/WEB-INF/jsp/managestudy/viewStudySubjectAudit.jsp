<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.audit_events" var="resaudit"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<html>
<head>
	<link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
	<link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
	<link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
	<ui:theme/>
</head>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>

<body style="padding-left: 20px">
<a name="root"></a>

<form action="ExportExcelStudySubjectAuditLog">
	<input type="hidden" value="<c:out value="${id}"/>" name="id"/><br>
	<input type="submit" value="Export to Excel" class="button_xlong"/><br>
</form>


<h1>
	<span class="first_level_header">
		<fmt:message key="study_subject" bundle="${resword}"/>: ${studySub.label} <fmt:message key="audit_logs" bundle="${resword}"/>
	</span>
</h1>

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<fmt:message key="secondary_subject_ID" bundle="${resword}" var="secondaryIdLabel"/>
<c:if test="${study ne null}">
	<c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>
<c:set var="secondaryIdShow" value="${true}"/>
<c:if test="${study ne null}">
	<c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
	<c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}"/>
</c:if>

<%-- Subject Summary --%>
<table width="650px" class="table_horizontal table_shadow_bottom">
	<tr>
		<td><b>${studySubjectLabel}</b></td>
		<c:if test="${secondaryIdShow}">
			<td><b>${secondaryIdLabel}</b></td>
		</c:if>
		<td><b><fmt:message key="date_of_birth" bundle="${resword}"/></b></td>
		<c:if test="${subjectStudy.studyParameterConfig.subjectPersonIdRequired != 'not used'}">
			<td><b><fmt:message key="person_ID" bundle="${resword}"/></b></td>
		</c:if>
		<td><b><fmt:message key="created_by" bundle="${resword}"/></b></td>
		<td><b><fmt:message key="status" bundle="${resword}"/></b></td>
	</tr>
	<tr>
		<td><c:out value="${studySub.label}"/></td>
		<c:if test="${secondaryIdShow}">
			<td><c:out value="${studySub.secondaryLabel}"/>&nbsp</td>
		</c:if>
		<td><fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/>&nbsp</td>
		<c:if test="${subjectStudy.studyParameterConfig.subjectPersonIdRequired != 'not used'}">
			<td><c:out value="${subject.uniqueIdentifier}"/>&nbsp;</td>
		</c:if>
		<td><c:out value="${studySub.owner.name}"/>&nbsp;</td>
		<td><c:out value="${studySub.status.name}"/></td>
	</tr>
</table>
<br/>

<%-- Subject Audit Events --%>
<a href="javascript:leftnavExpand('subject_audit');">
	<img id="excl_subject_audit" src="images/bt_Collapse.gif" border="0">
	<span class="table_title_Admin">
		<fmt:message key="subject_audit" bundle="${resword}"/>
	</span>
</a>

<div id="subject_audit">
	<table width="650px" class="table_horizontal table_shadow_bottom">
		<tr>
			<td><fmt:message key="audit_event" bundle="${resword}"/></td>
			<td><fmt:message key="local_date_time" bundle="${resword}"/></td>
			<td><fmt:message key="user" bundle="${resword}"/></td>
			<td><fmt:message key="value_type" bundle="${resword}"/></td>
			<td><fmt:message key="old" bundle="${resword}"/></td>
			<td><fmt:message key="new" bundle="${resword}"/></td>
		</tr>
		<c:forEach var="studySubjectAudit" items="${studySubjectAudits}">
			<tr>
				<td><b><fmt:message key="${studySubjectAudit.auditEventTypeName}" bundle="${resaudit}"/></b></td>
				<td>
					<cc-fmt:formatDate value="${studySubjectAudit.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;
				</td>
				<td><c:out value="${studySubjectAudit.userName}"/>&nbsp;</td>
				<td><c:out value="${studySubjectAudit.entityName}"/>&nbsp;</td>
				<td><c:out value="${studySubjectAudit.oldValue}"/>&nbsp;</td>
				<td><c:out value="${studySubjectAudit.newValue}"/>&nbsp;</td>
			</tr>
		</c:forEach>
	</table>
	<br>

	<a href="javascript:leftnavExpand('randomization_audit');">
		<img id="excl_randomization_audit" src="images/bt_Collapse.gif" border="0">
		<span class="table_title_Admin">
			<fmt:message key="randomization_audit" bundle="${resword}"/>
		</span>
	</a>

	<div id="randomization_audit">
		<table width="900px" class="table_horizontal table_shadow_bottom">
			<tr>
				<td><fmt:message key="audit_event" bundle="${resword}"/></td>
				<td><fmt:message key="local_date_time" bundle="${resword}"/></td>
				<td><fmt:message key="user" bundle="${resword}"/></td>
				<td><fmt:message key="event_description" bundle="${resword}"/></td>
			</tr>
			<c:forEach var="randomizationAudit" items="${randomizationAudits}">
				<tr>
					<td><b><fmt:message bundle="${resword}" key="randomization.call.result.${randomizationAudit.success == 1 ? 'success' : 'error'}"/></b></td>
					<td><cc-fmt:formatDate value="${randomizationAudit.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/></td>
					<td>${randomizationAudit.userName}</td>
					<td width="600px">
						<div style="margin-bottom: 10px">
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="systemProperty.randomizationAuthenticationUrl.label" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.authenticationUrl}</span>
						</div>
						<div style="margin-bottom: 10px">
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="systemProperty.randomizationUrl.label" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.randomizationUrl}</span>
						</div>
						<div style="margin-bottom: 10px">
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="systemProperty.randomizationTrialId.label" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.trialId}</span>
						</div>
						<div style="margin-bottom: 10px">
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="site_id" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.siteName}</span>
						</div>
						<div style="margin-bottom: 10px">
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="stratificationVariables" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.strataVariables}</span>
						</div>
						<div>
							<span style="display: inline-block; width: 200px; vertical-align: top"><b><fmt:message key="randomization_result" bundle="${resword}"/>:</b></span>
							<span style="display: inline-block; width: 380px; word-break: break-all;">${randomizationAudit.response}</span>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<br>

	<a href="javascript:leftnavExpand('events_audit');">
		<img id="excl_events_audit" src="images/bt_Collapse.gif" border="0">
		<span class="table_title_Admin">
			<fmt:message key="study_events_audit" bundle="${resword}"/>
		</span>
	</a>

	<div id="events_audit">
		<table width="650px" class="table_horizontal table_shadow_bottom">
			<tr>
				<td><b><fmt:message key="study_events" bundle="${resword}"/></b><br></td>
				<td><b><fmt:message key="location" bundle="${resword}"/></b><br></td>
				<td><b><fmt:message key="date" bundle="${resword}"/></b><br></td>
				<td><b><fmt:message key="occurrence_number" bundle="${resword}"/></b><br></td>
			</tr>
			<c:forEach var="event" items="${events}">
				<tr>
					<td>
						<a href="#${event.studyEventDefinition.name}${event.sampleOrdinal}">${event.studyEventDefinition.name}</a>
					</td>
					<td>${event.location}</td>
					<td>
						<c:choose>
							<c:when test="${event.startTimeFlag=='false'}">
								<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
							</c:when>
							<c:otherwise>
								<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
							</c:otherwise>
						</c:choose>
					</td>
					<td><c:out value="${event.sampleOrdinal}"/>&nbsp;</td>
				</tr>
			</c:forEach>
		</table>
		<br>

		<%-- Study Event Summary --%>
		<c:forEach var="event" items="${events}" varStatus="event_status">
			<a href="javascript:leftnavExpand('event_${event_status.index}');" name="${event.studyEventDefinition.name}${event.sampleOrdinal}" class="pl10">
				<img id="excl_event_${event_status.index}" src="images/bt_Collapse.gif" border="0">
				<span class="table_title_Admin">
					<fmt:message key="event" bundle="${resword}"/>: ${event.studyEventDefinition.name}
				</span>
			</a>

			<div id="event_${event_status.index}" class="pl10">
				<table width="650px"  class="table_vertical table_shadow_bottom">
					<tr>
						<td><fmt:message key="name" bundle="${resword}"/></td>
						<td>${event.studyEventDefinition.name}</td>
					</tr>
					<tr>
						<td><c:out value="Location"/></td>
						<td>${event.location}</td>
					</tr>
					<tr>
						<td><c:out value="Start Date"/></td>
						<td>
							<c:choose>
								<c:when test="${event.startTimeFlag=='false'}">
									<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}"
													   dateTimeZone="${userBean.userTimeZoneId}"/>
								</c:when>
								<c:otherwise>
									<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dtetmeFormat}"
													   dateTimeZone="${userBean.userTimeZoneId}"/>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td><c:out value="Status"/></td>
						<td>${event.subjectEventStatus.name}</td>
					</tr>
					<tr>
						<td><fmt:message key="occurrence_number" bundle="${resword}"/></td>
						<td>${event.sampleOrdinal}</td>
					</tr>
				</table>
				<br/>
				<%--Audit Events for Study Event --%>
				<table width="650px" class="table_horizontal table_shadow_bottom">
					<tr>
						<td><b><fmt:message key="audit_event" bundle="${resword}"/></b></td>
						<td><b><fmt:message key="local_date_time" bundle="${resword}"/></b></td>
						<td><b><fmt:message key="user" bundle="${resword}"/></b></td>
						<td><b><fmt:message key="value_type" bundle="${resword}"/></b></td>
						<td><b><fmt:message key="old" bundle="${resword}"/></b></td>
						<td><b><fmt:message key="new" bundle="${resword}"/></b></td>
					</tr>
					<c:forEach var="studyEventAudit" items="${studyEventAudits}">
						<c:if test="${studyEventAudit.entityId==event.id}">
							<tr>
								<td>
									<b><fmt:message key="${studyEventAudit.auditEventTypeName}" bundle="${resaudit}"/></b>
								</td>
								<td>
									<cc-fmt:formatDate value="${studyEventAudit.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
								</td>
								<td>${studyEventAudit.userName}</td>
								<td>${studyEventAudit.entityName}</td>
								<td>
									<c:choose>
										<c:when test="${studyEventAudit.oldValue eq '0'}"><fmt:message key="invalid" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '1'}"><fmt:message key="scheduled" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '2'}"><fmt:message key="not_scheduled" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '3'}"><fmt:message key="data_entry_started" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '4'}"><fmt:message key="completed" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '5'}"><fmt:message key="stopped" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '6'}"><fmt:message key="skipped" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '7'}"><fmt:message key="locked" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '8'}"><fmt:message key="signed" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '9'}"><fmt:message key="source_data_verified" bundle="${resterm}"/></c:when>
										<c:when test="${studyEventAudit.oldValue eq '10'}"><fmt:message key="deleted" bundle="${resterm}"/></c:when>
										<c:otherwise><c:out value="${studyEventAudit.oldValue}"/></c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${studyEventAudit.newValue eq '5'}">
											<fmt:message key="removed" bundle="${resterm}"/>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${studyEventAudit.newValue eq '0'}"><fmt:message key="invalid" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '1'}"><fmt:message key="scheduled" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '2'}"><fmt:message key="not_scheduled" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '3'}"><fmt:message key="data_entry_started" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '4'}"><fmt:message key="completed" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '6'}"><fmt:message key="skipped" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '7'}"><fmt:message key="locked" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '8'}"><fmt:message key="signed" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '9'}"><fmt:message key="source_data_verified" bundle="${resterm}"/></c:when>
												<c:when test="${studyEventAudit.newValue eq '10'}"><fmt:message key="deleted" bundle="${resterm}"/></c:when>
												<c:otherwise><c:out value="${studyEventAudit.newValue}"/></c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
				<br/>
				<%-- Event CRFs Audit Events --%>
				<a href="javascript:leftnavExpand('event_${event_status.index}_crfs_audit');" class="pl10">
					<img id="excl_event_${event_status.index}_crfs_audit" src="images/bt_Collapse.gif" border="0">
					<span class="table_title_Admin">
						<fmt:message key="event_crfs_audit" bundle="${resword}"/>
					</span>
				</a>
				<br/>

				<div id="event_${event_status.index}_crfs_audit" class="pl20">
					<a href="javascript:leftnavExpand('event_${event_status.index}_deleted_crfs_audit');">
						<img id="excl_event_${event_status.index}_deleted_crfs_audit" src="images/bt_Collapse.gif" border="0">
						<span class="table_title_Admin">
							<fmt:message key="deleted_event_crfs" bundle="${resword}"/>
						</span>
					</a>

					<div id="event_${event_status.index}_deleted_crfs_audit">
						<table width="650px" class="table_horizontal table_shadow_bottom">
							<tr>
								<td><fmt:message key="name" bundle="${resword}"/></td>
								<td><fmt:message key="version" bundle="${resword}"/></td>
								<td><fmt:message key="deleted_by" bundle="${resword}"/></td>
								<td><fmt:message key="delete_date" bundle="${resword}"/></td>
							</tr>
							<c:forEach var="deletedEventCRF" items="${allDeletedEventCRFs}">
								<c:if test="${deletedEventCRF.studyEventId == event.id}">
									<tr>
										<td>${deletedEventCRF.crfName}</td>
										<td>${deletedEventCRF.crfVersion}</td>
										<td>${deletedEventCRF.deletedBy}</td>
										<td>
											<cc-fmt:formatDate value="${deletedEventCRF.deletedDate}" pattern="${dteFormat}"
															   dateTimeZone="${userBean.userTimeZoneId}"/>
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</table>
					</div>

					<%-- Event CRFs for this Study Event --%>
					<c:forEach var="eventCRF" items="${event.eventCRFs}" varStatus="ec_status">
						<br>
						<a href="javascript:leftnavExpand('e_${event_status.index}_c_${ec_status.index}_audit');">
							<img id="excl_e_${event_status.index}_c_${ec_status.index}_audit" src="images/bt_Collapse.gif" border="0">
							<span class="table_title_Admin">
								<fmt:message key="event_crf" bundle="${resword}"/>: ${eventCRF.crf.name}
							</span>
						</a>

						<div id="e_${event_status.index}_c_${ec_status.index}_audit">
							<table width="650px" class="table_horizontal table_shadow_bottom">
								<tr>
									<td><fmt:message key="name" bundle="${resword}"/></td>
									<td><fmt:message key="version" bundle="${resword}"/></td>
									<c:if test="${subjectStudy.studyParameterConfig.interviewDateRequired != 'not_used'}">
										<td><fmt:message key="date_interviewed" bundle="${resword}"/></td>
									</c:if>
									<c:if test="${subjectStudy.studyParameterConfig.interviewerNameRequired != 'not_used'}">
										<td><fmt:message key="interviewer_name" bundle="${resword}"/></td>
									</c:if>
									<td><fmt:message key="owner" bundle="${resword}"/></td>
								</tr>
								<tr>
									<td>${eventCRF.crf.name}</td>
									<td>${eventCRF.crfVersion.name}</td>
									<c:if test="${subjectStudy.studyParameterConfig.interviewDateRequired != 'not_used'}">
										<td>
											<cc-fmt:formatDate value="${eventCRF.dateInterviewed}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
										</td>
									</c:if>
									<c:if test="${subjectStudy.studyParameterConfig.interviewerNameRequired != 'not_used'}">
										<td>${eventCRF.interviewerName}</td>
									</c:if>
									<td>${eventCRF.owner.name}</td>
								</tr>
							</table>
							<br>

							<%-- Item Data Audit Events --%>
							<a href="javascript:leftnavExpand('e_${event_status.index}_c_${ec_status.index}_item_data_audit');" class="pl10">
								<img id="excl_e_${event_status.index}_c_${ec_status.index}_item_data_audit" src="images/bt_Collapse.gif" border="0">
								<span class="table_title_Admin">
									<fmt:message key="item_data_audit" bundle="${resword}"/>
								</span>
							</a>

							<div id="e_${event_status.index}_c_${ec_status.index}_item_data_audit" class="pl10">
								<table width="1000px" class="table_horizontal table_shadow_bottom">
									<tr>
										<td><fmt:message key="audit_event" bundle="${resword}"/></td>
										<td><fmt:message key="local_date_time" bundle="${resword}"/></td>
										<td><fmt:message key="user" bundle="${resword}"/></td>
										<td><fmt:message key="value_type" bundle="${resword}"/></td>
										<td><fmt:message key="old" bundle="${resword}"/></td>
										<td><fmt:message key="new" bundle="${resword}"/></td>
									</tr>
									<c:forEach var="eventCRFAudit" items="${eventCRFAudits}">
										<c:if test="${eventCRFAudit.eventCRFId==eventCRF.id}">
											<tr>
												<td><b><fmt:message key="${eventCRFAudit.auditEventTypeName}" bundle="${resaudit}"/></b></td>
												<td>
													<cc-fmt:formatDate value="${eventCRFAudit.auditDate}" pattern="${dtetmeFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
												</td>
												<td>${eventCRFAudit.userName}</td>
												<td>
													<c:choose>
														<c:when test="${eventCRFAudit.itemId != 0}">
															<a href="javascript: openDocWindow('ViewItemDetail?itemId=${eventCRFAudit.itemId}')"
															   title="${eventCRFAudit.itemDescription}">
																	${eventCRFAudit.entityName} (${eventCRFAudit.ordinal})
															</a>
														</c:when>
														<c:otherwise>
															${eventCRFAudit.entityName} (${eventCRFAudit.ordinal})
														</c:otherwise>
													</c:choose>
												</td>
												<td style="word-break: break-all">
													<c:choose>
														<c:when test='${eventCRFAudit.auditEventTypeId == 12 or eventCRFAudit.entityName eq "Status"}'>
															<c:if test="${eventCRFAudit.oldValue eq '0'}"><fmt:message key="invalid" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '1'}"><fmt:message key="available" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '2'}"><fmt:message key="unavailable" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '3'}"><fmt:message key="private" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '4'}"><fmt:message key="pending" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '5'}"><fmt:message key="removed" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '6'}"><fmt:message key="locked" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.oldValue eq '7'}"><fmt:message key="auto-removed" bundle="${resterm}"/></c:if>
														</c:when>
														<c:when test='${eventCRFAudit.auditEventTypeId == 32}' >
															<c:choose>
																<c:when test="${eventCRFAudit.oldValue eq '1'}">TRUE</c:when>
																<c:when test="${eventCRFAudit.oldValue eq '0'}">FALSE</c:when>
																<c:otherwise><c:out value="${eventCRFAudit.oldValue}"/></c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${eventCRFAudit.itemDataTypeId == 11}">
																	<c:set var="path" value="${eventCRFAudit.oldValue}"/>
																	<c:set var="sep" value="\\"/>
																	<c:set var="sep2" value="\\\\"/>
																	<a href="DownloadAttachedFile?eventCRFId=${eventCRFAudit.eventCRFId}&fileName=${fn:replace(fn:replace(path,'+','%2B'),sep,sep2)}">${eventCRFAudit.oldValue}</a>
																</c:when>
																<c:otherwise>
																	${eventCRFAudit.oldValue}</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</td>
												<td style="word-break: break-all">
													<c:choose>
														<c:when test='${eventCRFAudit.auditEventTypeId == 12 or eventCRFAudit.entityName eq "Status"}'>
															<c:if test="${eventCRFAudit.newValue eq '0'}"><fmt:message key="invalid" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '1'}"><fmt:message key="available" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '2'}"><fmt:message key="completed" bundle="${resword}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '3'}"><fmt:message key="private" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '4'}"><fmt:message key="pending" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '5'}"><fmt:message key="removed" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '6'}"><fmt:message key="locked" bundle="${resterm}"/></c:if>
															<c:if test="${eventCRFAudit.newValue eq '7'}"><fmt:message key="auto-removed" bundle="${resterm}"/></c:if>
														</c:when>
														<c:when test='${eventCRFAudit.auditEventTypeId == 32}' >
															<c:choose>
																<c:when test="${eventCRFAudit.newValue eq '1'}">TRUE</c:when>
																<c:when test="${eventCRFAudit.newValue eq '0'}">FALSE</c:when>
																<c:otherwise><c:out value="${eventCRFAudit.newValue}"/></c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${eventCRFAudit.itemDataTypeId == 11}">
																	<c:set var="path" value="${eventCRFAudit.newValue}"/>
																	<c:set var="sep" value="\\"/>
																	<c:set var="sep2" value="\\\\"/>
																	<a href="DownloadAttachedFile?eventCRFId=${eventCRFAudit.eventCRFId}&fileName=${fn:replace(fn:replace(path,'+','%2B'),sep,sep2)}">${eventCRFAudit.newValue}</a>
																</c:when>
																<c:otherwise>${eventCRFAudit.newValue}</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</td>
											</tr>
										</c:if>
									</c:forEach>
								</table>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			<br/>
			<input id="CloaseViewStudySubjectAuditWindow" class="button_medium" type="submit" onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>" name="BTN_Close_Window"/>
			<a href="#root"><fmt:message key="return_to_top" bundle="${resword}"/></a>
			<br/><br/>
		</c:forEach>
	</div>
</div>
<jsp:include page="../include/changeTheme.jsp"/>
</body>
