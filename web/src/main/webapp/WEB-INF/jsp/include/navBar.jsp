<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<script language="JavaScript">
        function reportBug() {
            var bugtrack = "https://www.openclinica.com/ClinCapture/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&user=";
            var user= "<c:out value="${userBean.name}"/>";
            bugtrack = bugtrack + user+ "&url=" + window.location.href;
            openDocWindow(bugtrack);
        }
        function confirmCancel(pageName){
            var confirm1 = confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');
            if(confirm1){
                window.location = pageName;
            }
        }
        function confirmCancelAction( pageName, contextPath){
            var confirm1 = confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');
            if(confirm1){
            	 var tform = document.forms["fr_cancel_button"];
            	tform.action=contextPath+"/"+pageName;
            	tform.submit();

            }
        }
        function confirmExit(pageName){
            var confirm1 = confirm('<fmt:message key="sure_to_exit" bundle="${resword}"/>');
            if(confirm1){
                window.location = pageName;
            }
        }
        function goBack(){
            var confirm1 = confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>');
            if(confirm1){
                return history.go(-1);
            }
        }
        function lockedCRFAlert(userName){
            alert('<fmt:message key="CRF_unavailable" bundle="${resword}"/>'+'\n'
                    +'          '+userName+' '+'<fmt:message key="Currently_entering_data" bundle="${resword}"/>'+'\n'
                    +'<fmt:message key="Leave_the_CRF" bundle="${resword}"/>');
            return false;
        }
        function confirmExitAction( pageName, contextPath){
            var confirm1 = confirm('<fmt:message key="sure_to_exit" bundle="${resword}"/>');
            if(confirm1){
            	 var tform = document.forms["fr_cancel_button"];
            	tform.action=contextPath+"/"+pageName;
            	tform.submit();

            }
        }
</script>

<jsp:useBean scope='session' id='tableFacadeRestore' class='java.lang.String' />
<c:set var="restore" value="true"/>
<c:if test="${tableFacadeRestore=='false'}"><c:set var="restore" value="false"/></c:if>
<c:set var="profilePage" value="${param.profilePage}"/>
<!--  If Controller Spring based append ../ to urls -->
<c:set var="urlPrefix" value=""/>
<c:set var="requestFromSpringController" value="${param.isSpringController}" />
<c:set var="requestFromDoubleSpringController" value="${param.isDoubleSpringController}" />

<c:if test="${requestFromSpringController == 'true' }">
    <c:set var="urlPrefix" value="../"/>
</c:if>

<c:if test="${requestFromDoubleSpringController == 'true' }">
    <c:set var="urlPrefix" value="../../"/>
</c:if>

<!-- Main Navigation -->
     <div class="oc_nav">
     
     <table border="0" cellpadding="0" cellspacing="0" width="100%">
     <tr>
     <td align="left">
        
            <c:choose>
                <c:when test='${study.parentStudyId > 0}'>
                    <b><a href="${urlPrefix}ViewStudy?id=${study.parentStudyId}&viewFull=yes" title="<c:out value='${study.parentStudyName}'/>" alt="<c:out value='${study.parentStudyName}'/>" ><c:out value="${study.parentStudyName}" /></a>
                    :&nbsp;<a href="${urlPrefix}ViewSite?id=${study.id}" title="<c:out value='${study.name}'/>" alt="<c:out value='${study.name}'/>"><c:out value="${study.name}" /></a></b>
                </c:when>
                <c:otherwise>
                    <b><a href="${urlPrefix}ViewStudy?id=${study.id}&viewFull=yes" title="<c:out value='${study.name}'/>" alt="<c:out value='${study.name}'/>"><c:out value="${study.name}" /></a></b>
                </c:otherwise>
            </c:choose>
            (<c:out value="${study.identifier}" />)&nbsp;&nbsp;|&nbsp;&nbsp;
            <a href="${urlPrefix}ChangeStudy"><fmt:message key="change_study_site" bundle="${resworkflow}"/></a>
      </td>
      <td align="right">
        
            <a href="${urlPrefix}UpdateProfile"><b><c:out value="${userBean.name}" /></b> (<c:out value="${userRole.role.description}" />)</a>
            <c:set var="formatLocale"><fmt:message key="locale_string" bundle="${resformat}"/></c:set>
            &nbsp;
            <c:choose>
                <c:when test="${formatLocale == null}">
                    en
                </c:when>
                <c:otherwise>
                    <img src="${urlPrefix}images/flags/${fn:toUpperCase(formatLocale)}.png" alt="${formatLocale}" class="toolbarFlag"/>
                </c:otherwise>
            </c:choose>
            &nbsp;|&nbsp;
            <a href="${urlPrefix}j_spring_security_logout"><fmt:message key="log_out" bundle="${resword}"/></a>
        
        </td>
        </tr>
        </table>
        
<!--         <br/><br style="line-height: 4px;"/> -->
        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

            <div class="navbox_center">
                <!-- Top Navigation Row -->
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td>
                            <div id="bt_Home" class="nav_bt"><div><div><div>
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td><b>
                                        <ul>
                                        <c:choose>
                                            <c:when test="${userRole.sysAdmin || userBean.techAdmin}">
                                                <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a name="codedItems" href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                            <c:when test="${userRole.studyAdministrator}">
                                                <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a name="codedItems" href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                            <c:when test="${userRole.clinicalResearchCoordinator}">
                                                <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <c:if test="${study.status.available}">
                                                    <li><a href="${urlPrefix}AddNewSubject"><fmt:message key="nav_add_subject" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                </c:if>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                            <c:when test="${userRole.investigator}">
                                                <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <c:if test="${study.status.available}">
                                                    <li><a href="${urlPrefix}AddNewSubject"><fmt:message key="nav_add_subject" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                </c:if>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                            <c:when test="${userRole.studyCoder }">
                                                <li><a href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                            <c:when test="${userRole.monitor }">
                                                <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li><a name="codedItems" href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                <li>
                                                    <a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}">
                                                        <fmt:message key="nav_sdv" bundle="${resword}"/>
                                                    </a>&nbsp;&nbsp;|&nbsp;&nbsp;
                                                </li>
                                                <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                            </c:when>
                                        </c:choose>
                                        <li id="nav_Tasks" style="position: relative; z-index: 3;">
                                            <a href="#" onmouseover="setNav('nav_Tasks');" id="nav_Tasks_link"><fmt:message key="nav_tasks" bundle="${resword}"/>
                                                <img src="${urlPrefix}images/bt_Tasks_pulldown.gif" alt="Tasks" border="0"/></a>
                                        </li>
                                        </ul></b>
                                    </td>
                                    <td align="right" style="font-weight: normal;">

                                        <fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
                                        <c:if test="${study ne null}">
                                            <c:set var="studySubjectIDLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
                                        </c:if>

                                        <form METHOD="GET" action="${urlPrefix}ListStudySubjects" onSubmit=" if (document.forms[0]['findSubjects_f_studySubject.label'].value == '${studySubjectIDLabel}') { document.forms[0]['findSubjects_f_studySubject.label'].value=''}">
                                            <input type="text" name="findSubjects_f_studySubject.label" onblur="if (this.value == '') this.value = '${studySubjectIDLabel}'" onfocus="if (this.value == '${studySubjectIDLabel}') this.value = ''" value="${studySubjectIDLabel}" class="navSearch"/>
                                            <input type="hidden" name="navBar" value="yes"/>
                                            <input type="submit" value="<fmt:message key="go" bundle="${resword}"/>"  class="navSearchButton"/>
                                        </form>

                                    </td>
                                </tr>
                            </table>
                            </div></div></div></div>
                        </td>
                    </tr>
                                    </table>
            </div>
            <!-- End shaded box border DIVs -->
        </div></div></div></div></div></div></div></div></div>


            </td>
        </tr>
    </table>
    <!-- NAVIGATION DROP-DOWN -->


<div id="nav_hide" style="position: absolute; left: 0px; top: 0px; visibility: hidden; z-index: 2; width: 100%; height: 400px;">

<a href="#" onmouseover="hideSubnavs();"><img src="${urlPrefix}images/spacer.gif" alt="" width="1000" height="400" border="0"/></a>
</div>


    </div>
    <img src="${urlPrefix}images/spacer.gif" width="596" height="1"><br>
<!-- End Main Navigation -->
<div id="subnav_Tasks" class="dropdown">
    <div class="dropdown_BG">
        <c:choose>
            <c:when test="${userBean.sysAdmin || userBean.techAdmin}">
            <div class="taskGroup"><fmt:message key="nav_submit_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ImportCRFData"><fmt:message key="nav_import_data" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <c:if test="${!study.status.frozen && !study.status.locked}">
                    <div class="taskLink"><a href="${urlPrefix}CreateNewStudyEvent"><fmt:message key="nav_schedule_event" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_monitor_and_manage_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
                <c:choose>
                    <c:when test="${study.parentStudyId > 0 && (userRole.studyAdministrator || userBean.name == 'root')}">
                    </c:when>
                    <c:otherwise>
                        <div class="taskLink"><a href="${urlPrefix}ViewRuleAssignment?read=true"><fmt:message key="nav_rules" bundle="${resword}"/></a></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="taskRightColumn">
                <c:choose>
                    <c:when test="${study.parentStudyId > 0 && (userRole.studyAdministrator || userBean.name == 'root')}">
                    </c:when>
                    <c:otherwise>
                        <div class="taskLink"><a href="${urlPrefix}ListSubjectGroupClass?read=true"><fmt:message key="nav_groups" bundle="${resword}"/></a></div>
                        <div class="taskLink"><a href="${urlPrefix}ListCRF"><fmt:message key="nav_crfs" bundle="${resword}"/></a></div>
                        <div class="taskLink"><a href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a></div>
                    </c:otherwise>
                </c:choose>
                <c:if test="${includeReporting}">
                    <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
                </c:if>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_extract_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewDatasets"><fmt:message key="datasets" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_study_setup" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewStudy?id=${study.id}&viewFull=yes"><fmt:message key="nav_view_study" bundle="${resword}"/></a></div>
                <c:choose>
                    <c:when test="${study.parentStudyId > 0}">
                    </c:when>
                    <c:otherwise>
                        <div class="taskLink"><a href="${urlPrefix}pages/studymodule"><fmt:message key="nav_build_study" bundle="${resword}"/></a></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="taskRightColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudyUser"><fmt:message key="assign_users" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_administration" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudy"><fmt:message key="nav_studies" bundle="${resword}"/></a></div>
                <c:if test="${study.parentStudyId <= 0}">
                	<div class="taskLink"><a href="${urlPrefix}ListSite"><fmt:message key="nav_sites" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ListUserAccounts"><fmt:message key="nav_users" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <c:if test="${study.parentStudyId <= 0}">
                	<div class="taskLink"><a href="${urlPrefix}ListCRF"><fmt:message key="nav_crfs" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewAllJobs"><fmt:message key="nav_jobs" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ListSubject"><fmt:message key="nav_subjects" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            </c:when>
            <c:when test="${userRole.monitor}">
            <div class="taskGroup"><fmt:message key="nav_monitor_and_manage_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a></div>
                <c:if test="${includeReporting}">
                    <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
                </c:if>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_extract_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewDatasets"><fmt:message key="datasets" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            </c:when>

            <c:when test="${userRole.clinicalResearchCoordinator }">
            <div class="taskGroup"><fmt:message key="nav_submit_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
                <c:if test="${study.status.available}">
                    <div class="taskLink"><a href="${urlPrefix}AddNewSubject"><fmt:message key="nav_add_subject" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <c:if test="${!study.status.frozen && !study.status.locked}">
                    <div class="taskLink"><a href="${urlPrefix}CreateNewStudyEvent"><fmt:message key="nav_schedule_event" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ImportCRFData"><fmt:message key="nav_import_data" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            </c:when>

            <c:when test="${userRole.investigator}">
            <div class="taskGroup"><fmt:message key="nav_submit_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
                <c:if test="${study.status.available}">
                    <div class="taskLink"><a href="${urlPrefix}AddNewSubject"><fmt:message key="nav_add_subject" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <c:if test="${!study.status.frozen && !study.status.locked}">
                    <div class="taskLink"><a href="${urlPrefix}CreateNewStudyEvent"><fmt:message key="nav_schedule_event" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ImportCRFData"><fmt:message key="nav_import_data" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_extract_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewDatasets"><fmt:message key="datasets" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            </c:when>
            <c:when test="${userRole.studyCoder}">
                <div class="taskGroup"><fmt:message key="nav_monitor_and_manage_data" bundle="${resword}"/></div>
                <div class="taskLink"><a href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a></div>
            </c:when>
            <c:when test="${userRole.studyAdministrator}">
            <div class="taskGroup"><fmt:message key="nav_submit_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}ImportCRFData"><fmt:message key="nav_import_data" bundle="${resword}"/></a></div>
            </div>
            <div class="taskRightColumn">
                <c:if test="${!study.status.frozen && !study.status.locked}">
                    <div class="taskLink"><a href="${urlPrefix}CreateNewStudyEvent"><fmt:message key="nav_schedule_event" bundle="${resword}"/></a></div>
                </c:if>
                <div class="taskLink"><a href="${urlPrefix}ViewStudyEvents"><fmt:message key="nav_view_events" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_monitor_and_manage_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}"><fmt:message key="nav_source_data_verification" bundle="${resword}"/></a></div>
                <div class="taskLink"><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a></div>
                <c:choose>
                    <c:when test="${study.parentStudyId > 0 && userRole.studyAdministrator}">
                    </c:when>
                    <c:otherwise>
                        <div class="taskLink"><a href="${urlPrefix}ViewRuleAssignment?read=true"><fmt:message key="nav_rules" bundle="${resword}"/></a></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="taskRightColumn">
            <c:choose>
                <c:when test="${study.parentStudyId > 0 && userRole.studyAdministrator}">
                </c:when>
                <c:otherwise>
                    <div class="taskLink"><a href="${urlPrefix}ListSubjectGroupClass?read=true"><fmt:message key="nav_groups" bundle="${resword}"/></a></div>
                    <div class="taskLink"><a href="${urlPrefix}ListCRF"><fmt:message key="nav_crfs" bundle="${resword}"/></a></div>
                    <div class="taskLink"><a href="${urlPrefix}pages/codedItems?study=${study.id}"><fmt:message key="code" bundle="${resword}"/></a></div>
                </c:otherwise>
            </c:choose>
            <c:if test="${includeReporting}">
                    <div class="taskLink"><a href="${urlPrefix}reports" target="_blank"><fmt:message key="reporting" bundle="${resword}"/></a></div>
            </c:if>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_extract_data" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewDatasets"><fmt:message key="datasets" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            <div class="taskGroup"><fmt:message key="nav_study_setup" bundle="${resword}"/></div>
            <div class="taskLeftColumn">
                <div class="taskLink"><a href="${urlPrefix}ViewStudy?id=${study.id}&viewFull=yes"><fmt:message key="nav_view_study" bundle="${resword}"/></a></div>
                <c:choose>
                    <c:when test="${study.parentStudyId > 0}">
                    </c:when>
                    <c:otherwise>
                        <div class="taskLink"><a href="${urlPrefix}pages/studymodule"><fmt:message key="nav_build_study" bundle="${resword}"/></a></div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="taskRightColumn">
                <div class="taskLink"><a href="${urlPrefix}ListStudyUser"><fmt:message key="assign_users" bundle="${resword}"/></a></div>
            </div>
            <br clear="all">
            </c:when>
        </c:choose>
        <div class="taskGroup"><fmt:message key="nav_other" bundle="${resword}"/></div>
        <div class="taskLeftColumn">
            <div class="taskLink"><a href="${urlPrefix}UpdateProfile"><fmt:message key="nav_update_profile" bundle="${resword}"/></a></div>
        </div>
        <div class="taskRightColumn">
            <div class="taskLink"><a href="${urlPrefix}j_spring_security_logout"><fmt:message key="nav_log_out" bundle="${resword}"/></a></div>
        </div>
        <br clear="all">
        <div></div>
    </div>
</div>
