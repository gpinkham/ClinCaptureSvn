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

        confirmSubmit({
            message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
            height: 150,
            width: 500,
            pageName: pageName
        });
    }

    function confirmCancelAction( pageName, contextPath){

        var tform = document.forms["fr_cancel_button"];
        tform.action=contextPath+"/"+pageName;
        confirmSubmit({
            message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
            height: 150,
            width: 500,
            form: tform
        });
    }

    function confirmExit(pageName){

        confirmSubmit({
            message: '<fmt:message key="sure_to_exit" bundle="${resword}"/>',
            height: 150,
            width: 500,
            pageName: pageName
        });
    }

    function goBack(){

        confirmSubmit({
            message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
            height: 150,
            width: 500,
            goBack: true
        });
    }
    function lockedCRFAlert(userName){

        alertDialog({
            message: message,
            height: 150,
            width: 500
        });
        return false;
    }

    function confirmExitAction( pageName, contextPath){

        var tform = document.forms["fr_cancel_button"];
        tform.action=contextPath+"/"+pageName;
        confirmSubmit({
            message: '<fmt:message key="sure_to_exit" bundle="${resword}"/>',
            height: 150,
            width: 500,
            form: tform
        });
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
                <a href="${urlPrefix}j_spring_security_logout" onClick="clearLastAccessedObjects();"><fmt:message key="log_out" bundle="${resword}"/></a>

            </td>
        </tr>
    </table>

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

                                                <c:when test="${userBean.name == 'root'}">
                                                    <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                </c:when>

                                                <c:when test="${userRole.studyAdministrator}">
                                                    <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}StudyAuditLog"><fmt:message key="nav_study_audit_log" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                </c:when>

                                                <c:when test="${userRole.monitor }">
                                                    <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li><a href="${urlPrefix}ListStudySubjects"><fmt:message key="nav_subject_matrix" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <li>
                                                        <a href="${urlPrefix}pages/viewAllSubjectSDVtmp?sdv_restore=${restore}&studyId=${study.id}">
                                                            <fmt:message key="nav_sdv" bundle="${resword}"/>
                                                        </a>&nbsp;&nbsp;|&nbsp;&nbsp;
                                                    </li>
                                                    <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
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

                                                <c:when test="${userRole.studyCoder}">
                                                    <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <c:if test="${study.studyParameterConfig.allowCodingVerification eq 'yes'}">
                                                        <li><a href="${urlPrefix}pages/codedItems"><fmt:message key="code" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    </c:if>
                                                    <li><a href="${urlPrefix}ViewNotes?module=submit"><fmt:message key="nav_notes_and_discrepancies" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                </c:when>

                                                <c:when test="${userRole.studyEvaluator}">
                                                    <li><a href="${urlPrefix}MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    <c:if test="${evaluationEnabled eq true}">
                                                        <li><a href="${urlPrefix}pages/crfEvaluation"><fmt:message key="evaluate" bundle="${resword}"/></a>&nbsp;&nbsp;|&nbsp;&nbsp;</li>
                                                    </c:if>
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
    </div></div></div></div></div></div></div></div>

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

            <c:when test="${userBean.name == 'root'}">
                <c:import url="/WEB-INF/jsp/include/navBarSubmitData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarMonitorAndManageData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarExtractData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarStudySetup.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarAdministration.jsp"/>
            </c:when>

            <c:when test="${userRole.studyAdministrator}">
                <c:import url="/WEB-INF/jsp/include/navBarSubmitData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarMonitorAndManageData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarExtractData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarStudySetup.jsp"/>
                <c:if test="${userBean.sysAdmin}"><c:import url="/WEB-INF/jsp/include/navBarAdministration.jsp"/></c:if>
            </c:when>

            <c:when test="${userRole.studyMonitor}">
                <c:import url="/WEB-INF/jsp/include/navBarMonitorAndManageData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarExtractData.jsp"/>
                <c:if test="${userBean.sysAdmin}"><c:import url="/WEB-INF/jsp/include/navBarAdministration.jsp"/></c:if>
            </c:when>

            <c:when test="${userRole.clinicalResearchCoordinator}">
                <c:import url="/WEB-INF/jsp/include/navBarSubmitData.jsp"/>
                <c:if test="${userBean.sysAdmin}"><c:import url="/WEB-INF/jsp/include/navBarAdministration.jsp"/></c:if>
            </c:when>

            <c:when test="${userRole.investigator}">
                <c:import url="/WEB-INF/jsp/include/navBarSubmitData.jsp"/>
                <c:import url="/WEB-INF/jsp/include/navBarExtractData.jsp"/>
                <c:if test="${userBean.sysAdmin}"><c:import url="/WEB-INF/jsp/include/navBarAdministration.jsp"/></c:if>
            </c:when>

            <c:when test="${userRole.studyCoder}">
                <c:import url="/WEB-INF/jsp/include/navBarMonitorAndManageData.jsp"/>
            </c:when>

            <c:when test="${userRole.studyEvaluator}">
                <c:import url="/WEB-INF/jsp/include/navBarMonitorAndManageData.jsp"/>
            </c:when>

        </c:choose>

        <c:import url="/WEB-INF/jsp/include/navBarOther.jsp"/>

    </div>
</div>