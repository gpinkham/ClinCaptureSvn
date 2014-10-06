<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm"%>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat" />
<c:set var="dteFormat">
	<fmt:message key="date_format_string" bundle="${resformat}" />
</c:set>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
    <script type="text/javascript" language="JavaScript" src="includes/repetition-model/repetition-model.js"></script>
    <link rel="stylesheet" href="includes/styles.css" type="text/css">
    <link rel="stylesheet" href="includes/print_crf.css" type="text/css">

    <style>
        .infotab {
            border-top: 1px #CCCCCC solid;
            border-left: 1px #CCCCCC solid
        }

        .infotab tr td {
            border-right: 1px #000000 solid;
            border-bottom: 1px #000000 solid;
            padding: 2px 4px 2px 4px
        }

        .infotab tr td h1 {
            color: #000000
        }

        thead {
            display: table-header-group;
        }
    </style>
    <ui:theme/>
</head>
<c:set var="eventName" value="true" scope="request" />
<c:set var="InternetE" value="${isInternetExplorer}" scope="request" />
<jsp:useBean scope="request" id="section"
	class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="studyEvent"
	class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="sedCrfBeans"
	class="java.util.LinkedHashMap" />
<!-- Clinovo ticket #134 start -->
<script type="text/JavaScript" language="JavaScript"
	src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript"
	src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript"
	src="includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript">
$(document).ready(function() {
	$('.tableDiv').find('span').each(function() {
    	var $this = $(this);
		var t = $this.text();
    	$this.html(t.replace('&lt;','<').replace('&gt;', '>'));
	});
	
	$('.toplevel').find('.aka_headerBackground.aka_padding_large.aka_cellBorders.aka_font_general').each(function() {
    var $this = $(this);
	var t = $this.text();
    $this.html(t.replace('&lt;','<').replace('&gt;', '>'));
	});

	$("*").find("#tableDiv").css('margin-bottom', '10px'); 
	$("*").find("#toplevel1").css('margin-bottom', '20px'); 

	var elsarea = document.getElementsByTagName ('textarea');
	for ( var i = 0; i < elsarea.length ; i ++ ) {
		elsarea[i].setAttribute('disabled', 'true'); 
	}
	var els = document.getElementsByTagName ('input');
	for ( var i = 0; i < els.length ; i ++ ) {
 		if ( els[i].type == 'text' || els[i].type == 'radio' || els[i].type == 'checkbox' || els[i].type == 'button') els[i].setAttribute('disabled', 'true'); 
	}
 });
</script>
<!-- end -->
<c:if test="${(InternetE != 'true')}">
	<script>
	$(document).ready(function() {
	$(".aka_form_table").attr('class', 'tableDiv');
	});
	</script>
</c:if>
<body>

	<div
		style="border: 0px #000000 solid; padding-left: 0px; width: 820px; margin: 5px">
		<c:forEach var="item" items="${sedCrfBeans}" varStatus="status">
			<div style="page-break-after: always">

				<fmt:message key="study_subject_ID" bundle="${resword}"
					var="studySubjectLabel" />
				<c:if test="${study ne null}">
					<c:set var="studySubjectLabel"
						value="${study.studyParameterConfig.studySubjectIdLabel}" />
				</c:if>

				<c:set var="genderShow" value="${true}" />
				<fmt:message key="gender" bundle="${resword}" var="genderLabel" />
				<c:if test="${study ne null}">
					<c:set var="genderShow"
						value="${!(study.studyParameterConfig.genderRequired == 'false')}" />
					<c:set var="genderLabel"
						value="${study.studyParameterConfig.genderLabel}" />
				</c:if>

				<table border=0 align="center" width="100%">

					<tbody>
						<tr>
							<td width="100%"><c:forEach var="printCrfBean"
									items="${item.value}" varStatus="status">
									<div
										style="padding-left: 10px; padding-bottom: 20px; padding-top: 10px; page-break-after: always">
										<br>
										<h1>
											<c:out value="${printCrfBean.crfBean.name}" />
											v.
											<c:out value="${printCrfBean.crfVersionBean.name}" />
										</h1>
										<c:set var="eventName"
											value="${printCrfBean.studyEventBean.studyEventDefinition.name}"
											scope="request" />
										<table border="0" cellpadding="0" cellspacing="0" width="650"
											style="border-style: solid; margin-left: 0%; border-width: 1px; border-color: #CCCCCC;">
											<tr>
												<td class="table_cell_noborder" style="color: #789EC5"><b>${studySubjectLabel}:</b><br></td>
												<td class="table_cell_noborder" style="color: #789EC5"><c:out
														value="${studySubject.label}" /><br></td>
												<c:choose>
													<c:when
														test="${sedCrfBeans.studyParameterConfig.personIdShownOnCRF == 'true'}">
														<td class="table_cell_top" style="color: #789EC5"><b><fmt:message
																	key="person_ID" bundle="${resword}" />:</b><br></td>
														<td class="table_cell_noborder" style="color: #789EC5"><c:out
																value="${subject.uniqueIdentifier}" /><br></td>

													</c:when>
													<c:otherwise>
														<td class="table_cell_top" style="color: #789EC5"><b><fmt:message
																	key="person_ID" bundle="${resword}" />:</b><br></td>
														<td class="table_cell_noborder" style="color: #789EC5"><fmt:message
																key="N/A" bundle="${resword}" /></td>

													</c:otherwise>
												</c:choose>
											</tr>
											<tr>
												<td class="table_cell_noborder" style="color: #789EC5"><b><fmt:message
															key="study_site" bundle="${resword}" />:</b><br></td>
												<td class="table_cell_noborder" style="color: #789EC5"><c:out
														value="${studyTitle}" /><br></td>
												<td class="table_cell_top" style="color: #789EC5"><b><fmt:message
															key="age" bundle="${resword}" />:</b><br></td>
												<td class="table_cell_noborder" style="color: #789EC5"><c:choose>
														<c:when test="${age!=''}">
															<c:out value="${age}" />
														</c:when>
														<c:otherwise>
															<fmt:message key="N/A" bundle="${resword}" />
														</c:otherwise>
													</c:choose><br></td>
											</tr>

											<tr>
												<td class="table_cell_noborder" style="color: #789EC5"><b><fmt:message
															key="event" bundle="${resword}" />:</b></td>
												<td class="table_cell_noborder" style="color: #789EC5"><c:out
														value="${eventName}" /> (<fmt:formatDate
														value="${studyEvent.dateStarted}" pattern="${dteFormat}" />)</td>
												<td class="table_cell_top" style="color: #789EC5"><b><fmt:message
															key="date_of_birth" bundle="${resword}" />:</b><br></td>
												<td class="table_cell_noborder" style="color: #789EC5"><fmt:formatDate
														value="${subject.dateOfBirth}" pattern="${dteFormat}" /><br></td>
											</tr>
											<tr>
												<td class="table_cell_noborder" style="color: #789EC5"><b><fmt:message
															key="interviewer" bundle="${resword}" />:</b></td>
												<td class="table_cell_noborder" style="color: #789EC5">
														<c:choose>
															<c:when test="${not empty EventCRFBean.interviewerName}">
																<c:out value="${EventCRFBean.interviewerName}" />
															</c:when>
															<c:otherwise>
																<fmt:message key="N/A" bundle="${resword}" />
															</c:otherwise>
														</c:choose>
														<c:if test="${not empty EventCRFBean.dateInterviewed}">
															(<fmt:formatDate value="${EventCRFBean.dateInterviewed}" pattern="${dteFormat}" />)
														</c:if>
												</td>
												<c:choose>
													<c:when test="${genderShow}">
														<td class="table_cell_top" style="color: #789EC5"><b>${genderLabel}:</b></td>
														<td class="table_cell_noborder" style="color: #789EC5">
															<c:choose>
																<c:when test="${subject.gender==109}">
																	<fmt:message key="M" bundle="${resword}" />
																</c:when>
																<c:when test="${subject.gender==102}">
																	<fmt:message key="F" bundle="${resword}" />
																</c:when>
																<c:otherwise>
																	<c:out value="${subject.gender}" />
																</c:otherwise>
															</c:choose>
														</td>
													</c:when>
													<c:otherwise>
														<td class="table_cell_top" style="color: #789EC5">&nbsp;</td>
														<td class="table_cell_noborder" style="color: #789EC5">&nbsp;</td>
													</c:otherwise>
												</c:choose>
											</tr>
										</table>
										<br>
										<c:set var="dataInvolved" value="${dataInvolved}" scope="request" />
										<c:choose>
											<c:when test="${printCrfBean.grouped}">
												<c:set var="listOfDisplaySectionBeans"
													value="${printCrfBean.displaySectionBeans}" scope="request" />
												<c:set var="crfVersionBean"
													value="${printCrfBean.crfVersionBean}" scope="request" />
												<c:set var="crfBean" value="${printCrfBean.crfBean}"
													scope="request" />
												<c:set var="EventCRFBean"
													value="${printCrfBean.eventCrfBean}" scope="request" />
												<aka_frm:print_tabletag>true</aka_frm:print_tabletag>
											</c:when>

											<c:when test="${!printCrfBean.grouped}">
												<c:set var="allSections" value="${printCrfBean.allSections}"
													scope="request" />
												<c:set var="section"
													value="${printCrfBean.displaySectionBean}" scope="request" />
												<c:set var="annotations"
													value="${printCrfBean.eventCrfBean.annotations}"
													scope="request" />
												<c:set var="crfBean" value="${printCrfBean.crfBean}"
													scope="request" />
												<c:set var="allCrfPrint" value="true" scope="request" />
												<jsp:include
													page="../managestudy/showAllSectionWithoutCommentsPrint.jsp" />
											</c:when>
										</c:choose>
									</div>

								</c:forEach></td>
						</tr>
					</tbody>
				</table>
			</div>
		</c:forEach>
	</div>
</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>
