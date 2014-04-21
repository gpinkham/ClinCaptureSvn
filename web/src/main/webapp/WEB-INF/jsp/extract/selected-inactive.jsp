<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/> 

<span class="table_title_extract"><fmt:message key="subject_attributes" bundle="${resword}"/></span>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table class="table_horizontal" width="100%">
	<tr>
		<td style="width:165px">
			<fmt:message key="subject_status" bundle="${resword}" />
		</td>

		<td style="width:130px">
			<fmt:message key="subject_unique_ID" bundle="${resword}" />
		</td>

		<c:set var="secondaryIdShow" value="${true}" />
		<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel" />

		<c:if test="${study ne null}">
			<c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}" />
			<c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}" /></c:if>

		<c:if test="${secondaryIdShow}">
			<td>${secondaryIdLabel}</td>
		</c:if>

		<td class="table_header_column_top">
			<c:choose>
				<c:when test="${study.studyParameterConfig.collectDob != '2'}">
					<fmt:message key="date_of_birth" bundle="${resword}" />
				</c:when>
				<c:otherwise>
					<fmt:message key="year_of_birth" bundle="${resword}" />
				</c:otherwise>
			</c:choose>
		</td>

		<c:set var="genderShow" value="${true}" />
		<fmt:message key="gender" bundle="${resword}" var="genderLabel" />

		<c:if test="${study ne null}">
			<c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}" />
			<c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}" /></c:if>

		<c:if test="${genderShow}">
			<td>${genderLabel}</td>
		</c:if>
	</tr>

	<tr>
		<td>
			<c:choose>
				<c:when test="${newDataset.showSubjectStatus}">
					<input type="checkbox" checked name="subj_status" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="subj_status" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showSubjectUniqueIdentifier}">
					<input type="checkbox" checked name="unique_identifier" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="unique_identifier" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<c:if test="${secondaryIdShow}">
			<td>
				<c:choose>
					<c:when test="${newDataset.showSubjectSecondaryId}">
						<input type="checkbox" checked name="subj_secondary_id" value="yes" disabled="disabled">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="subj_secondary_id" value="yes" disabled="disabled">
					</c:otherwise>
				</c:choose>
			</td>
		</c:if>

		<td>
			<c:choose>
				<c:when test="${newDataset.showSubjectDob}">
					<input type="checkbox" checked name="dob" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="dob" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<c:if test="${genderShow}">
			<td>
				<c:choose>
					<c:when test="${newDataset.showSubjectGender}">
						<input type="checkbox" checked name="gender" value="yes" disabled="disabled">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="gender" value="yes" disabled="disabled">
					</c:otherwise>
				</c:choose>
			</td>
		</c:if>
	</tr>
</table>

</div>
</div></div></div></div></div></div></div></div>
</div>

<span class="table_title_extract"><fmt:message key="event_attributes" bundle="${resword}"/></span>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table class="table_horizontal" width="100%">
	<tr>
		<td style="width:165px">
			<fmt:message key="event_location" bundle="${resword}" />
		</td>

		<td style="width:130px">
			<fmt:message key="start_date" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="end_date" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="status" bundle="${resword}" />
		</td>

		<c:if test="${subjectAgeAtEvent == 1}">
			<td>
				<fmt:message key="subject_age_at_event" bundle="${resword}" />
			</td>
		</c:if>
	</tr>

	<tr>
		<td>
			<c:choose>
				<c:when test="${newDataset.showEventLocation}">
					<input type="checkbox" checked name="location" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="location" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showEventStart}">
					<input type="checkbox" checked name="start" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="start" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showEventEnd}">
					<input type="checkbox" checked name="end" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="end" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showEventStatus}">
					<input type="checkbox" checked name="event_status" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="event_status" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<c:if test="${subjectAgeAtEvent == 1}">
			<td>
				<c:choose>
					<c:when test="${newDataset.showSubjectAgeAtEvent}">
						<input type="checkbox" checked name="age_at_event" value="yes" disabled="disabled">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="age_at_event" value="yes" disabled="disabled">
					</c:otherwise>
				</c:choose>
			</td>
		</c:if>
	</tr>
</table>

</div>
</div></div></div></div></div></div></div></div>
</div>

<span class="table_title_extract"><fmt:message key="CRF_attributes" bundle="${resword}"/></span>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table class="table_horizontal" width="100%">
	<tr>
		<td style="width:165px">
			<fmt:message key="CRF_version" bundle="${resword}" />
		</td>

		<td style="width:130px">
			<fmt:message key="interviewer_name" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="interview_date" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="CRF_status" bundle="${resword}" />
		</td>
	</tr>

	<tr>
		<td>
			<c:choose>
				<c:when test="${newDataset.showCRFversion}">
					<input type="checkbox" checked name="crf_version" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="crf_version" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showCRFinterviewerName}">
					<input type="checkbox" checked name="interviewer" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="interviewer" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showCRFinterviewerDate}">
					<input type="checkbox" checked name="interviewer_date" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="interviewer_date" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>

		<td>
			<c:choose>
				<c:when test="${newDataset.showCRFstatus}">
					<input type="checkbox" checked name="crf_status" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="crf_status" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
</table>

</div>
</div></div></div></div></div></div></div></div>
</div>

<span class="table_title_extract"><fmt:message key="subject_group_attributes" bundle="${resword}"/></span>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table class="table_horizontal" width="100%">
	<tr>
		<td style="width:25px"></td>
		<td style="width:125px">
			<fmt:message key="subject_group_name" bundle="${resword}" />
		</td>
		<td style="width:130px">
			<fmt:message key="subject_group_type" bundle="${resword}" />
		</td>
		<td>
			<fmt:message key="status" bundle="${resword}" />
		</td>
		<td>
			<fmt:message key="subject_assignment" bundle="${resword}" />
		</td>
	</tr>

	<c:forEach var="sgclass" items="${newDataset.allSelectedGroups}">
		<tr>
			<c:choose>
				<c:when test="${sgclass.selected}">
					<td>
						<input type=checkbox checked name="groupSelected<c:out value=" ${sgclass.id} "/>" value="yes" disabled="disabled">
				</c:when>
				<c:otherwise>
					<td>
						<input type=checkbox name="groupSelected<c:out value=" ${sgclass.id} "/>" value="yes" disabled="disabled">
				</c:otherwise>
			</c:choose>

			<td>
				<c:out value="${sgclass.name}" />
			</td>

			<td>
				<c:out value="${sgclass.groupClassTypeName}" />
			</td>

			<td>
				<c:out value="${sgclass.status.name}" />
			</td>

			<td>
				<c:out value="${sgclass.subjectAssignment}" />
			</td>
		</tr>
	</c:forEach>
</table>

</div>
</div></div></div></div></div></div></div></div>
</div>

<br>
<span class="table_title_extract"><fmt:message key="CRF_data" bundle="${resword}"/></span>

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center" align="center">

<table class="table_horizontal" width="100%">
	<tr>
		<td style="width:25px">&nbsp;</td>

		<td style="width:125px">
			<fmt:message key="name" bundle="${resword}" />
		</td>

		<td style="width:130px">
			<fmt:message key="description" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="event" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="CRF" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="version2" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="data_type" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="units" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="response_label" bundle="${resword}" />
		</td>

		<td>
			<fmt:message key="PHI" bundle="${resword}" />
		</td>
	</tr>

	<c:set var="count" value="0" />

	<c:forEach var='item' items='${newDataset.itemDefCrf}'>
		<tr>
			<td>
				<c:choose>
					<c:when test="${item.selected}">
						<input type="checkbox" name="itemSelected<c:out value=" ${count} "/>" checked value="yes" disabled="disabled">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="itemSelected<c:out value=" ${count} "/>" value="yes" disabled="disabled">
					</c:otherwise>
				</c:choose>
			</td>

			<td> <a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value='${item.id}'/>&itemName=<c:out value='${item.name}'/>');">
				<c:out value="${item.name}"/></a>
			</td>

			<td>
				<c:out value="${item.description}" />&nbsp;</td>

			<td>
				<input type="hidden" name="itemDefName<c:out value=" ${count} "/>" value="<c:out value=" ${item.defName} "/>">
				<c:out value="${item.defName}" />&nbsp;</td>

			<td>
				<input type="hidden" name="itemCrfName<c:out value=" ${count} "/>" value="<c:out value=" ${item.crfName} "/>">
				<c:out value="${item.crfName}" />&nbsp;</td>

			<td>
				<c:choose>
					<c:when test="${fn:length(item.itemMetas) eq 0}">${item.itemMeta.crfVersionName}</c:when>
					<c:otherwise>
						<c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
							<c:choose>
								<c:when test="${status.last}">
									<c:out value="${meta.crfVersionName}" /></c:when>
								<c:otherwise>
									<c:out value="${meta.crfVersionName}" />,
									<br>
								</c:otherwise>
							</c:choose>
						</c:forEach>&nbsp;</c:otherwise>
				</c:choose>
			</td>

			<td>
				<c:out value="${item.dataType.name}" />&nbsp;</td>

			<td>
				<c:out value="${item.units}" />&nbsp;</td>

			<td>
				<c:choose>
					<c:when test="${fn:length(item.itemMetas) eq 0}">${item.itemMeta.responseSet.label}</c:when>
					<c:otherwise>
						<c:forEach var="meta" items="${item.itemMetas}" varStatus="status">
							<c:choose>
								<c:when test="${status.last}">
									<c:out value="${meta.responseSet.label}" /></c:when>
								<c:otherwise>
									<c:out value="${meta.responseSet.label}" />,
									<br>
								</c:otherwise>
							</c:choose>
						</c:forEach>&nbsp;</c:otherwise>
				</c:choose>
			</td>

			<td>
				<c:choose>
					<c:when test="${item.phiStatus}">
						<fmt:message key="yes" bundle="${resword}" />
					</c:when>
					<c:otherwise>
						<fmt:message key="no" bundle="${resword}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:set var="count" value="${count+1}" />
	</c:forEach>
</table>
</div>

</div></div></div></div></div></div></div></div>
