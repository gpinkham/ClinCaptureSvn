<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean scope="request" id="table" class="org.akaza.openclinica.web.bean.EntityBeanTable"/>
<c:choose>
    <c:when test="${userRole.manageStudy}">
        <c:import url="../include/managestudy-header.jsp"/>
    </c:when>
    <c:otherwise>
        <c:import url="../include/submit-header.jsp"/>
    </c:otherwise>
</c:choose>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="events_month_shown_default" bundle="${restext}"/>
            <br><br>
            <fmt:message key="subject_scheduled_no_DE_yellow" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == "startDate"}'>
        <c:set var="startDate" value="${presetValue.value}"/>
    </c:if>
    <c:if test='${presetValue.key == "endDate"}'>
        <c:set var="endDate" value="${presetValue.value}"/>
    </c:if>
    <c:if test='${presetValue.key == "definitionId"}'>
        <c:set var="definitionId" value="${presetValue.value}"/>
    </c:if>
    <c:if test='${presetValue.key == "statusId"}'>
        <c:set var="statusId" value="${presetValue.value}"/>
    </c:if>

</c:forEach>
<!-- the object inside the array is StudySubjectBean-->

<h1>
    <div class="first_level_header">
        <fmt:message key="view_all_events_in" bundle="${resword}"/> <c:out value="${study.name}"/>
        <a href="javascript:openDocWindow('help/2_5_viewEvents_Help.html')">
            <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>"
                 title="<fmt:message key="help" bundle="${resword}"/>">
        </a>
		<c:if test="${enablePrint == 'yes' and not empty allEvents}">
			<a href="javascript:openDocWindow('ViewStudyEvents?print=yes&${queryUrl}')"
			   onMouseDown="javascript:setImage('bt_Print0','images/bt_Print_d.gif');"
			   onMouseUp="javascript:setImage('bt_Print0','images/bt_Print.gif');">
				<img name="bt_Print0" src="images/bt_Print.gif" border="0"
					 alt="<fmt:message key="print" bundle="${resword}"/>">
			</a>
		</c:if>
    </div>
</h1>


<div style="width: 640px">
    <!-- These DIVs define shaded box borders -->
    <div class="box_T">
        <div class="box_L">
            <div class="box_R">
                <div class="box_B">
                    <div class="box_TL">
                        <div class="box_TR">
                            <div class="box_BL">
                                <div class="box_BR">
                                    <div class="textbox_center">
                                        <form method="POST" action="ViewStudyEvents" name="control">
                                            <jsp:include page="../include/showSubmitted.jsp"/>
                                            <table border="0" cellpadding="0" cellspacing="0">
                                                <tr valign="top"><b><fmt:message key="filter_events_by"
                                                                                 bundle="${resword}"/>:</b></tr>
                                                <tr valign="top">
                                                    <td><fmt:message key="study_event_definition"
                                                                     bundle="${resword}"/>:
                                                    </td>
                                                    <td colspan="2">
                                                        <div class="formfieldL_BG">
                                                            <c:set var="definitionId1"
                                                                   value="${definitionId}"/>
                                                            <select name="definitionId" class="formfieldL">
                                                                <option value="0">--<fmt:message key="all"
                                                                                                 bundle="${resword}"/>--
                                                                </option>
                                                                <c:forEach var="definition" items="${definitions}">
                                                                <c:choose>
                                                                <c:when test="${definitionId1 == definition.id}">
                                                                <option value="<c:out value="${definition.id}"/>"
                                                                        selected>
                                                                        <c:out value="${definition.name}"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                <option value="<c:out value="${definition.id}"/>">
                                                                        <c:out value="${definition.name}"/>
                                                                    </c:otherwise>
                                                                    </c:choose>
                                                                    </c:forEach>
                                                            </select></div>
                                                    </td>
                                                    <td>&nbsp;&nbsp;<fmt:message key="status" bundle="${resword}"/>:
                                                    </td>
                                                    <td colspan="2">
                                                        <div class="formfieldM_BG">
                                                            <c:set var="status1" value="${statusId}"/>
                                                            <select name="statusId" class="formfieldM">
                                                                <option value="0">--<fmt:message key="all"
                                                                                                 bundle="${resword}"/>--
                                                                </option>
                                                                <c:forEach var="status" items="${statuses}">
                                                                <c:choose>
                                                                <c:when test="${status1 == status.id}">
                                                                <option value="<c:out value="${status.id}"/>" selected>
                                                                        <c:out value="${status.name}"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                    <c:if test="${status.id != '2'}">
                                                                <option value="<c:out value="${status.id}"/>">
                                                                        <c:out value="${status.name}"/>
                                                                    </c:if>
                                                                    </c:otherwise>
                                                                    </c:choose>
                                                                    </c:forEach>
                                                            </select>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr valign="top">
                                                    <td><fmt:message key="date_started" bundle="${resword}"/>:</td>
                                                    <td>
                                                        <div class="formfieldS_BG">
                                                            <input type="text" name="startDate"
                                                                   value="${startDate}"
                                                                   class="formfieldS" id="startDateField"></div>
                                                        <jsp:include page="../showMessage.jsp">
                                                            <jsp:param name="key" value="startDate"/>
                                                        </jsp:include>
                                                    </td>
                                                    <td>
														<ui:calendarIcon onClickSelector="'#startDateField'"/>
                                                        (<fmt:message key="date_format" bundle="${resformat}"/>)
                                                    </td>
                                                    <td>&nbsp;&nbsp;<fmt:message key="date_ended"
                                                                                 bundle="${resword}"/>:
                                                    </td>
                                                    <td>
                                                        <div class="formfieldS_BG">
                                                            <input type="text" name="endDate"
                                                                   value="${endDate}" class="formfieldS"
                                                                   id="endDateField"></div>
                                                        <jsp:include page="../showMessage.jsp">
                                                            <jsp:param name="key" value="endDate"/>
                                                        </jsp:include>
                                                    </td>
                                                    <td>
														<ui:calendarIcon onClickSelector="'#endDateField'"/> (<fmt:message key="date_format" bundle="${resformat}"/>)
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colspan="6" align="right"><input type="submit" name="submit"
                                                                                         value="<fmt:message key="apply_filter" bundle="${resword}"/>"
                                                                                         class="button_medium"></td>
                                                </tr>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<br><br>
<c:if test="${empty allEvents}">
    <p><fmt:message key="no_events_within_parameters" bundle="${restext}"/>
</c:if>
<c:forEach var="eventView" items="${allEvents}">
    <c:choose>
        <c:when test="${userRole.manageStudy}">
            <span class="table_title_Admin floatLeft">
        </c:when>
        <c:otherwise>
            <span class="table_title_Admin floatLeft">
        </c:otherwise>
    </c:choose>
    <b> <c:out
            value="${eventView.definition.name}"/></b></span><c:if test="${userRole.studyAdministrator || userBean.name eq 'root'}"><a href="InitUpdateEventDefinition?id=${eventView.definition.id}" class="floatLeft"
    onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
    onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"><img
    name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit"
                                                                          bundle="${resword}"/>" title="<fmt:message
        key="edit" bundle="${resword}"/>" align="left" hspace="6"></a></c:if>
    <br><br>
    <c:set var="table" value="${eventView.studyEventTable}" scope="request"/>
    <c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showEventByDefinitionRow.jsp"/></c:import>
    <br>
</c:forEach>
<table>
    <td>
        <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
               value="<fmt:message key="back" bundle="${resword}"/>"
               class="button_medium medium_back"
               onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
    </td>
</table>
<DIV ID="testdiv1"
     STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<br><br>


<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
    <tr>
        <td id="sidebar_Workflow_closed" style="display: none">
            <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img
                    src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif"
                    border="0"></a>
        </td>
        <td id="sidebar_Workflow_open" style="display: all">
            <table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
                <tr>
                    <td class="workflowBox_T" valign="top">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <td class="workflow_tab">
                                    <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img
                                            src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

                                    <b><fmt:message key="workflow" bundle="${resword}"/></b>

                                </td>
                            </tr>
                        </table>
                    </td>
                    <td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
                </tr>
                <tr>
                    <td colspan="2" class="workflowbox_B">
                        <div class="box_R">
                            <div class="box_B">
                                <div class="box_BR">
                                    <div class="workflowBox_center">


                                        <!-- Workflow items -->

                                        <table border="0" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td>

                                                    <!-- These DIVs define shaded box borders -->
                                                    <div class="box_T">
                                                        <div class="box_L">
                                                            <div class="box_R">
                                                                <div class="box_B">
                                                                    <div class="box_TL">
                                                                        <div class="box_TR">
                                                                            <div class="box_BL">
                                                                                <div class="box_BR">

                                                                                    <div class="textbox_center"
                                                                                         align="center">

                                                                                        <c:choose>
                                                                                        <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                               <a href="ManageStudy"><fmt:message key="manage_study" bundle="${resworkflow}"/></a>
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                               <a href="ListStudySubjects"><fmt:message key="submit_data" bundle="${resworkflow}"/></a>
                             </c:otherwise>
                             </c:choose>




							</span>

                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                </td>
                                                <td><img src="images/arrow.gif"></td>
                                                <td>

                                                    <!-- These DIVs define shaded box borders -->
                                                    <div class="box_T">
                                                        <div class="box_L">
                                                            <div class="box_R">
                                                                <div class="box_B">
                                                                    <div class="box_TL">
                                                                        <div class="box_TR">
                                                                            <div class="box_BL">
                                                                                <div class="box_BR">

                                                                                    <div class="textbox_center"
                                                                                         align="center">

                                                                                        <c:choose>
                                                                                        <c:when test="${userRole.manageStudy}">
                               <span class="title_manage">
                             </c:when>
                             <c:otherwise>
                               <span class="title_submit">
                             </c:otherwise>
                             </c:choose>


							<fmt:message key="view_events" bundle="${resworkflow}"/>


							</span>

                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                </td>
                                            </tr>
                                        </table>


                                        <!-- end Workflow items -->

                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
