<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <c:import url="../include/admin-header.jsp"/>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy && module=='manage'}">
                <c:import url="../include/managestudy-header.jsp"/>
            </c:when>
            <c:otherwise>
                <c:import url="../include/submit-header.jsp"/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<!--script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script-->
<script type="text/javascript" language="javascript">
    function checkCRFLocked(ecId, url){
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                window.location = url;
            }else{
                alert(data);return false;
            }
        });
    }
    function checkCRFLockedInitial(ecId, formName){
        if(ecId==0) {formName.submit(); return;} 
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
                alert(data);
            }
        });
    }
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">
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

<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="father" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="mother" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="subjectStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="parentStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope="request" id="children" class="java.util.ArrayList"/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope="request" id="groups" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="from" class="java.lang.String"/>

<script language="JavaScript">
    function leftnavExpand(strLeftNavRowElementName, isHeader){
      var objLeftNavRowElement;

      objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
      if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
			objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
			if (isHeader) {
				objExCl = MM_findObj("excl_"+strLeftNavRowElementName);
				if(objLeftNavRowElement.display == "none"){
					objExCl.src = "images/bt_Expand.gif";
				}else{
					objExCl.src = "images/bt_Collapse.gif";
				}	
			}
        }
    }
</script>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td>
        <h1>
            <div class="first_level_header">
                <fmt:message key="view_subject_record" bundle="${resword}"/>: <c:out value="${studySub.label}"/> 
                <a>&nbsp;</a>
			</div>
        </h1>
        </td>
    </tr>
</table>

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_titla_manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_submit">
            </c:otherwise>
        </c:choose>

    </c:otherwise>
</c:choose>

	<a href="javascript:leftnavExpand('studySubjectRecord', true); leftnavExpand('dynamicGroups'); leftnavExpand('groups');">
		<img id="excl_studySubjectRecord" src="images/bt_Expand.gif" border="0"> <fmt:message key="study_subject_record" bundle="${resword}"/>
	</a>
	</div>
<div id="studySubjectRecord" style="display: none">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<c:set var="secondaryIdShow" value="${true}"/>
<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel"/>
<c:if test="${study ne null}">
    <c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}"/>
    <c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}"/>
</c:if>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<c:set var="enrollmentDateShow" value="${true}"/>
<fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel"/>
<c:if test="${study ne null}">
    <c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}"/>
    <c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}"/>
</c:if>

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td style="padding-right: 0px;" valign="top" width="800">
	<!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="tablebox_center">
			<table width="800" border="0" cellpadding="0" cellspacing="0">

		<!-- Table Actions row (pagination, search, tools) -->
				<tr>
			<!-- Table Tools/Actions cell -->
					<td class="table_actions" valign="top" align="right" height='20'>
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="table_tools">
								<table width='70'>
									<td style="white-space: nowrap;">
									<c:if test="${study.status.available}">
										<c:if test="${userRole.role.id ne 6}">
											<a href="UpdateStudySubject?id=<c:out value="${studySub.id}"/>&amp;action=show" onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
													onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');">
												<img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="Edit" title="Edit" align="left" hspace="6">
											</a>
										</c:if>
									</c:if>
									<c:choose>
										<c:when test="${showUnlockEventsButton}">
											<a href="LockStudySubject?id=${studySub.id}&action=unlock"><img src="images/bt__Unlock.png" border="0" alt="<fmt:message key="unlockStudySubject" bundle="${resword}"/>" title="<fmt:message key="unlockStudySubject" bundle="${resword}"/>" hspace="4"></a>
										</c:when>
										<c:when test="${showLockEventsButton}">
											<a href="LockStudySubject?id=${studySub.id}&action=lock"><img src="images/bt__Lock.png" border="0" alt="<fmt:message key="lockStudySubject" bundle="${resword}"/>" title="<fmt:message key="lockStudySubject" bundle="${resword}"/>" hspace="4"></a>
										</c:when>
									</c:choose>
									</td>
								</table>
							</td>
						</tr>
					</table>
					</td>

			<!-- End Table Tools/Actions cell -->
				</tr>
				
		<!-- end Table Actions row (pagination, search, tools) -->
				<tr>
					<td valign="top">

			<!-- Table Contents -->
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="table_header_column_top">${studySubjectLabel}</td>
							<td class="table_cell_top"><c:out value="${studySub.label}"/></td>
							<td class="table_header_row"><fmt:message key="person_ID" bundle="${resword}"/>
							
							<%-- DN for person ID goes here --%>
							<c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
					            <c:set var="isNew" value="${hasUniqueIDNote eq 'yes' ? 0 : 1}"/>					             
					            <c:choose>
					                <c:when test="${hasUniqueIDNote eq 'yes'}">
					                    <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier', '', event); return false;">
					                        <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="${uniqueIDNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
					                    </a>					                    
					                </c:when>
					                <c:otherwise>					                    
					                    <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier','spanAlert-uniqueIdentifier', '', event); return false;">
					                        <img id="flag_uniqueIdentifier" name="flag_uniqueIdentifier" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                  <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=uniqueIdentifier&column=unique_identifier">
					                    </a>
					                </c:otherwise>
					            </c:choose>
					        </c:if>
							</td>
    						<td class="table_cell_top">
								<c:out value="${subject.uniqueIdentifier}"/>
					      	</td>
						</tr>
						<tr>
                            <c:choose>
                                <c:when test="${secondaryIdShow}">
                                    <td class="table_header_column">${secondaryIdLabel}</td>
                                    <td class="table_cell"><c:out value="${studySub.secondaryLabel}"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td class="table_header_column">&nbsp;</td>
                                    <td class="table_cell">&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
							<c:choose>
							    <c:when test="${subjectStudy.studyParameterConfig.collectDob == '1'}">
							        <td class="table_header_row"><fmt:message key="date_of_birth" bundle="${resword}"/>							            
							            <%-- DN for DOB goes here --%>
							        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
						                <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>						
						                <c:choose>
						                    <c:when test="${hasDOBNote eq 'yes'}">
						                        <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
						                            <img id="flag_dob" name="flag_dob" src="${dOBNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
						                        </a>						                            
						                    </c:when>
						                    <c:otherwise>						                            
						                        <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
													            <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                      <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						                        </a>
						                    </c:otherwise>
						                </c:choose>
						            </c:if>
							        </td>
							        <td class="table_cell"><fmt:formatDate value="${subject.dateOfBirth}" pattern="${dteFormat}"/></td>
							    </c:when>
							    <c:when test="${subjectStudy.studyParameterConfig.collectDob == '3'}">
							        <td class="table_header_row"><fmt:message key="date_of_birth" bundle="${resword}"/>							            
							            <%-- DN for DOB goes here --%>
							        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
						                <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>
						                <c:choose>
						                    <c:when test="${hasDOBNote eq 'yes'}">
						                        <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
						                            <img id="flag_dob" name="flag_dob" src="${dOBNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
						                        </a>						                            
						                    </c:when>
						                    <c:otherwise>						                            
						                        <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
						                            <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                        <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						                        </a>
						                    </c:otherwise>
						                </c:choose>
						            </c:if>
							        </td>
							        <td class="table_cell"><fmt:message key="not_used" bundle="${resword}"/></td>
							    </c:when>
							    <c:otherwise>
							        <td class="table_header_row"><fmt:message key="year_of_birth" bundle="${resword}"/>
							            
							            <%-- DN for DOB goes here --%>
							        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
						                <c:set var="isNew" value="${hasDOBNote eq 'yes' ? 0 : 1}"/>						
						                <c:choose>
						                    <c:when test="${hasDOBNote eq 'yes'}">
						                        <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth','spanAlert-dob', '', event); return false;">
						                            <img id="flag_dob" name="flag_dob" src="${dOBNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
						                        </a>						                            
						                    </c:when>
						                    <c:otherwise>						                          
						                        <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth&new=1','spanAlert-dob', '', event); return false;">
						                            <img id="flag_dob" name="flag_dob" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                        <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=dob&column=date_of_birth">
						                        </a>
						                    </c:otherwise>
						                </c:choose>
						            </c:if>
							        </td>
							        <td class="table_cell"><c:out value="${yearOfBirth}"/>
							        </td>
							    </c:otherwise>
							</c:choose>
						</tr>
                        <tr>
							<td class="table_header_column"><fmt:message key="status" bundle="${resword}"/></td>
                    		<td class="table_cell"><c:out value="${studySub.status.name}"/></td>
                            <c:choose>
                                <c:when test="${genderShow}">
                                    <td class="table_header_row">${genderLabel}
                                        <%-- DN for Gender goes here --%>
                                        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                                            <c:set var="isNew" value="${hasGenderNote eq 'yes' ? 0 : 1}"/>
                                            <c:choose>
                                                <c:when test="${hasGenderNote eq 'yes'}">
                                                    <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=gender&column=gender','spanAlert-gender', '', event); return false;">
                                                        <img id="flag_gender" name="flag_gender" src="${genderNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=${subject.id}&writeToDB=1&name=subject&field=gender&column=gender','spanAlert-gender', '', event); return false;">
                                                        <img id="flag_gender" name="flag_gender" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                                        <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${subject.id}&name=subject&field=gender&column=gender">
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                    <td class="table_cell">
                                        <c:choose>
                                            <c:when test="${subject.gender==32}">
                                                &nbsp;
                                            </c:when>
                                            <c:when test="${subject.gender==109 ||subject.gender==77}">
                                                <fmt:message key="male" bundle="${resword}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:message key="female" bundle="${resword}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td class="table_header_row">&nbsp;</td>
                                    <td class="table_cell">&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
						</tr>
						<tr>
							<td class="table_header_column"> </td>
                    		<td class="table_cell"> </td>
                            <c:choose>
                                <c:when test="${enrollmentDateShow}">
                                    <td class="table_header_row">${enrollmentDateLabel}
                                            &nbsp;
                                                <%-- DN for enrollment date goes here --%>
                                        <c:if test="${subjectStudy.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
                                            <c:set var="isNew" value="${hasEnrollmentNote eq 'yes' ? 0 : 1}"/>
                                            <c:choose>
                                                <c:when test="${hasEnrollmentNote eq 'yes'}">
                                                    <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate', '', event); return false;">
                                                        <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="${enrollmentNote.resStatus.iconFilePath}" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" >
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?subjectId=${studySub.id}&id=${studySub.id}&writeToDB=1&name=studySub&field=enrollmentDate&column=enrollment_date','spanAlert-enrollmentDate', '', event); return false;">
                                                        <img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                                        <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&subjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date">
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                    <td class="table_cell"><fmt:formatDate value="${studySub.enrollmentDate}" pattern="${dteFormat}"/>&nbsp;
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td class="table_header_row">&nbsp;</td>
                                    <td class="table_cell">&nbsp;</td>
                                </c:otherwise>
                            </c:choose>
						</tr>
                        <tr>
							<td class="table_divider" colspan="4">&nbsp;</td>
						</tr>
               			<tr>
		                    <td class="table_header_column_top"><fmt:message key="study_name" bundle="${resword}"/></td>
		                    <td class="table_cell_top">
		                        <c:choose>
		                            <c:when test="${subjectStudy.parentStudyId>0}">
		                                <a href="ViewStudy?id=<c:out value="${parentStudy.id}"/>&amp;viewFull=yes"><c:out value="${parentStudy.name}"/></a>
		                            </c:when>
		                            <c:otherwise>
		                                <a href="ViewStudy?id=<c:out value="${subjectStudy.id}"/>&amp;viewFull=yes"><c:out value="${subjectStudy.name}"/></a>
		                            </c:otherwise>
		                        </c:choose>
		                    </td>
							<td class="table_header_row"><fmt:message key="site_name" bundle="${resword}"/></td>
		                    <td class="table_cell_top">
		                        <c:if test="${subjectStudy.parentStudyId>0}">
		                            <a href="ViewSite?id=<c:out value="${subjectStudy.id}"/>"><c:out value="${subjectStudy.name}"/></a>
		                        </c:if>&nbsp;
							</td>
						</tr>
					</table>
			<!-- End Table Contents -->
					</td>
				</tr>
			</table>
			</div>
			</div></div></div></div></div></div></div></div>

			</td>
		</tr>
	</table>
<br>
</div>

<!-- DynamicGroup table begin -->
<c:if test="${!subjDynGroupIsDefault || (subjDynGroupIsDefault && !defaultGroupNotExist)}">
  <div id="dynamicGroups" style="display:none;">
        <div style="width: 600px">
            <!-- These DIVs define shaded box borders -->
            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                <div class="tablebox_center">
                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td valign="top">

                                <!-- Table Contents -->
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="table_header_row_left"><fmt:message key="dynamic_group_class" bundle="${resword}"/></td>
                                        <td class="table_header_row"><fmt:message key="events" bundle="${resword}"/></td>
                      <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
                    </tr>
                    <tr>
                      <c:choose>
                        <c:when test="${subjDynGroupIsDefault}">
                          <c:choose>
                            <c:when test="${defaultGroupNotExist}">
                              <td class="table_cell_left">&nbsp;&nbsp;<fmt:message key="default_group" bundle="${resword}"/>&nbsp;(<fmt:message key="none" bundle="${resword}"/>)</td>
                            </c:when>
                            <c:otherwise>
                              <td class="table_cell_left">&nbsp;&nbsp;<fmt:message key="default_group" bundle="${resword}"/>&nbsp;(<c:out value="${subjDynGroup.name}"/>)</td>
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                        <c:otherwise>
                          <td class="table_cell">&nbsp;&nbsp;<c:out value="${subjDynGroup.name}"/></td>
                        </c:otherwise>
                      </c:choose>
                      <td class="table_cell"><c:out value="${studyEventDefinitionsString}"/></td>
                      <c:choose>
                        <c:when test="${subjDynGroup.status.available}">
                          <td class="table_cell aka_green_highlight"><c:out value="${subjDynGroup.status.name}"/></td>
                        </c:when>
                        <c:when test="${subjDynGroup.status.deleted}">
                          <td class="table_cell aka_red_highlight"><c:out value="${subjDynGroup.status.name}"/></td>
                        </c:when>
                        <c:otherwise>
                          <td class="table_cell"><c:out value="${subjDynGroup.status.name}"/></td>
                        </c:otherwise>
                      </c:choose>
                    </tr>
                                </table>

                                <!-- End Table Contents -->
                            </td>
                        </tr>
                    </table>
                </div>
            </div></div></div></div></div></div></div></div>
        </div>
        <br><br>
  </div>
</c:if>
<!-- DynamicGroup table end -->
<!-- Group table begin -->
<c:if test="${!empty groups}">
  <div id="groups" style="display:none;">
      <div style="width: 600px">
          <!-- These DIVs define shaded box borders -->
          <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
              <div class="tablebox_center">
                  <table border="0" cellpadding="0" cellspacing="0" width="100%">
                      <tr>
                          <td valign="top">

                              <!-- Table Contents -->
                              <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                  <tr>
                                      <td class="table_header_row_left"><fmt:message key="subject_group_class" bundle="${resword}"/></td>
                                      <td class="table_header_row"><fmt:message key="study_group" bundle="${resword}"/></td>
                                      <td class="table_header_row"><fmt:message key="notes" bundle="${resword}"/></td>
                                      <td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
                                  </tr>
                                  <c:choose>
                                      <c:when test="${!empty groups}">
                                          <c:forEach var="group" items="${groups}">
                                              <tr>
                                                  <td class="table_cell_left">&nbsp;&nbsp;<c:out value="${group.groupClassName}"/></td>
                                                  <td class="table_cell"><c:out value="${group.studyGroupName}"/></td>
                                                  <td class="table_cell"><c:out value="${group.notes}"/>&nbsp;</td>
                                                  <c:choose>
                                                    <c:when test="${group.status.available}">
                                                      <td class="table_cell aka_green_highlight"><c:out value="${group.status.name}"/></td>
                                                    </c:when>
                                                    <c:when test="${group.status.deleted}">
                                                      <td class="table_cell aka_red_highlight"><c:out value="${group.status.name}"/></td>
                                                    </c:when>
                                                    <c:otherwise>
                                                      <td class="table_cell"><c:out value="${group.status.name}"/></td>
                                                    </c:otherwise>
                                                  </c:choose>
                                              </tr>
                                          </c:forEach>
                                      </c:when>
                                      <c:otherwise>
                                          <tr>
                                              <td class="table_cell" colspan="3"><fmt:message key="currently_no_groups" bundle="${resword}"/></td>
                                          </tr>
                                      </c:otherwise>
                                  </c:choose>
                              </table>

                              <!-- End Table Contents -->
                          </td>
                      </tr>
                  </table>
              </div>
          </div></div></div></div></div></div></div></div>
      </div>
      <br><br>
  </div>
</c:if>
<!-- Group table end -->

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_titla_manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_submit">
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
	<a name="events" href="javascript:leftnavExpand('subjectEvents',true);">
		<img id="excl_subjectEvents" src="images/bt_Collapse.gif" border="0"> <fmt:message key="events" bundle="${resword}"/>
	</a>
</div>
<div id="subjectEvents">
    <c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showStudyEventRow.jsp" /></c:import>
    </br></br>
</div>

<p> 
</p> 
<table> 
	<tr>
		<td>
			<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
		</td>
		<td>
			<input id="NotesDiscrepancies" class="button_long" type="button" name="BTN_NotesDiscrepancies" value="<fmt:message key="subjects_discrepancies" bundle="${resword}"/>" onclick="window.location.href=('ViewNotes?module=<c:out value="${module}"/>&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15&listNotes_f_studySubject.label=<c:out value="${studySub.label}"/>');"/>
		</td>
		<c:if test="${userRole.role.id != 4 && userRole.role.id != 5}">
		<td> 
			<input id="ViewAuditLog" class="button_long" type="submit" name="BTN_View" value="<fmt:message key="subjects_audit_log" bundle="${resword}"/>" onclick="javascript:openDocWindow('ViewStudySubjectAuditLog?id=<c:out value="${studySub.id}"/>');"/>
		</td>
		</c:if>
		<td>
			<input class="button_long" type="submit" name="CaseBook" value="<fmt:message key="subjects_case_book" bundle="${resword}"/>" onclick="javascript:openDocWindow('PrintSubjectCaseBook?subjectId=<c:out value="${studySub.id}"/>');"/>
		</td>
	</tr>
</table> 


<!-- End Main Content Area -->



<jsp:include page="../include/footer.jsp"/>
