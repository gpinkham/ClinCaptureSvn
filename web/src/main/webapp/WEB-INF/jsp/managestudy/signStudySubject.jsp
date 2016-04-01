<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>


<jsp:useBean id="date" class="java.util.Date" />
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_time_format_short" bundle="${resformat}"/></c:set>


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

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<script type="text/JavaScript" language="JavaScript">
    function myCancel() {

        cancelButton=document.getElementById('cancel');
        if ( cancelButton != null) {
        	confirmDialog({ 
        		message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
        		height: 150,
        		width: 500,
        		redirectLink: 'ListStudySubjects'
        		});      
         	return false;
       	}
        return true;

    }
</script>

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
<jsp:useBean scope="request" id="parentStudy" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope="request" id="groups" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="from" class="java.lang.String"/>

<script language="JavaScript">
    <!--
    function leftnavExpand(strLeftNavRowElementName){

        var objLeftNavRowElement;

        objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
        if (objLeftNavRowElement != null) {
            if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
            objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
        }
    }

    //-->
</script>

<h1>
	<div class=first_level_header>
		<fmt:message key="sign_subject" bundle="${resword}"/>&nbsp;<c:out value="${studySub.label}"/>
	</div>
</h1>

<p><fmt:message key="sure_to_sign_subject" bundle="${resword}"/></p>

<p><fmt:message key="sure_to_sign_subject1" bundle="${resword}"/></p>

<b><fmt:message key="user_full_name" bundle="${resword}"/>: <c:out value="${userBean.firstName}"/>&nbsp;<c:out value="${userBean.lastName}"/>
    <br/>
    <fmt:message key="date_time" bundle="${resword}"/>: <fmt:formatDate value="${date}" type="both" pattern="${dteFormat}" timeStyle="long"/>
    <br/>
    <fmt:message key="sure_to_sign_subject2" bundle="${resword}"/>
    <br/>
    <fmt:message key="role" bundle="${resword}"/>: <c:out value="${userRole.role.description}"/></b>
<br><br>
<form action="SignStudySubject" method="post">
    <input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
    <input type="hidden" name="action" value="confirm">
    <div style="width: 250px">
        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

            <table border="0" cellpadding="0" cellspacing="0">
                <tr valign="top"><td colspan="2">&nbsp;&nbsp;</td></tr>
                <tr valign="top"><td class="formlabel"><fmt:message key="user_name" bundle="${resword}"/>:</td>
                    <td>
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr><td>
                                <div class="formfieldM_BG"><input type="text"  onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');" name="j_user" autocomplete="off" class="formfieldM"></div>
                            </td><td>&nbsp;</td>
                            </tr>
                        </table>
                    </td></tr>
                <tr valign="top"><td class="formlabel"><fmt:message key="password" bundle="${resword}"/>:</td>
                    <td>
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr><td>
                                <div class="formfieldM_BG"><input type="password" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');" name="j_pass"  autocomplete="off" class="formfieldM"></div>
                            </td><td>&nbsp;</td>
                            </tr>
                        </table>
                    </td></tr>

                <tr valign="top"><td colspan="2">&nbsp;&nbsp;</td></tr>
            </table>
        </div>
        </div></div></div></div></div></div></div></div>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
		
	<input type="submit" name="Submit" value="<fmt:message key="sign_casebook" bundle="${resword}"/>" class="button_medium">
	<input type="button" onClick="checkGoToEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>','<%=request.getContextPath()%>/ViewStudySubject?id=${studySub.id}');"  value="<fmt:message key="view_subject_record2" bundle="${resword}"/>"  class="button_xlong"/>
    <img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this CRF section." alt="Data Status" name="DataStatus_bottom">
	</form>
<br>

<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_title_Manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_Submit">
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
 
<c:choose>
    <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
        <div class="table_title_Admin">
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${userRole.manageStudy}">
                <div class="table_title_manage">
            </c:when>
            <c:otherwise>
                <div class="table_title_submit">
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>	

<%-- Subject discrepancy note table--%>

<div id="subjDiscNoteDiv" class="subjDiscNoteDiv" style="display:none">
    <table class="subjDiscNoteTable" cellpadding="0" cellspacing="0">
        <thead>
            <th class="table_header_row_left">Event Name</th>
            <th class="table_header_row">CRF Name</th>
            <th class="table_header_row">New</th>
            <th class="table_header_row">Updated</th>
            <th class="table_header_row">Resolution Proposed</th>
            <th class="table_header_row">Closed</th>
            <th class="table_header_row">Not Applicable</th>
            <th class="table_header_row">Actions</th>
        </thead>
        <tbody>
            <c:set var="hasEvents" value="${! (empty displayStudyEvents)}" />
            <c:set var="hasEventCRFs" value="${false}" />
            <c:forEach var="displayStudyEventBean" items="${displayStudyEvents}">
             <c:if test="${!(empty displayStudyEventBean.displayEventCRFs)}">
                 <c:set var="hasEventCRFs" value="${true}" />
             </c:if>
            </c:forEach>

            <c:choose>
                <c:when test="${(! hasEvents) || (! hasEventCRFs)}">
                    <tr>
                    <td class="table_cell_left"><fmt:message key="there_are_no_rows_because_no_events" bundle="${resword}"/></td>
                    </tr>
                </c:when>
                <c:otherwise>

                    <c:forEach var="displayStudyEventBean" items="${displayStudyEvents}">
                        <c:forEach var="displayEventCRFBean" items="${displayStudyEventBean.displayEventCRFs}">
                            <c:set var="discNoteMap" value="${discNoteByEventCRFid[displayEventCRFBean.eventCRF.id]}"/>

                            <tr>
                                <td class="table_cell_left">
                                        ${displayStudyEventBean.studyEvent.studyEventDefinition.name}</td>
                                <td class="table_cell">${displayEventCRFBean.eventCRF.crf.name}</td>
                                <td class="table_cell">
                                    <c:set var="discNoteCount" value="${discNoteMap['New'] + discNoteMap['New (DCF)']}"/>
                                    <c:if test="${discNoteCount > 0}">
                                        <img
                                          name="icon_Note" src="images/icon_Note.gif" border="0"                                          alt="<fmt:message key="Open" bundle="${resterm}"/>" title="<fmt:message key="Open" bundle="${resterm}"/>" align="left"/>
                                        (${discNoteCount})
                                        <c:set var="discNoteCount" value="${0}"/>
                                    </c:if>
                                </td><%-- new --%>
                                <td class="table_cell">
                                    <c:set var="discNoteCount" value="${discNoteMap['Updated']}"/>
                                    <c:if test="${discNoteCount > 0}">
                                        <img
                                          name="icon_Note" src="images/icon_flagYellow.gif" border="0"
                                          alt="<fmt:message key="Updated" bundle="${resterm}"/>" title="<fmt:message key="Updated" bundle="${resterm}"/>" align="left"/>
                                        (${discNoteCount})
                                        <c:set var="discNoteCount" value="${0}"/>
                                    </c:if>
                                </td><%-- updated --%>
                                <td class="table_cell">
                                    <c:set var="discNoteCount" value="${discNoteMap['Resolution Proposed']}"/>
                                    <c:if test="${discNoteCount > 0}">
                                        <img
                                          name="icon_Note" src="images/icon_flagBlack.gif" border="0"
                                          alt="<fmt:message key="Resolved" bundle="${resterm}"/>" title="<fmt:message key="Resolved" bundle="${resterm}"/>" align="left"/>
                                        (${discNoteCount})
                                        <c:set var="discNoteCount" value="${0}"/>
                                    </c:if>
                                </td><%-- Resolution Proposed --%>
                                <td class="table_cell">
                                    <c:set var="discNoteCount" value="${discNoteMap['Closed']}"/>
                                    <c:if test="${discNoteCount > 0}">
                                        <img
                                          name="icon_Note" src="images/icon_flagGreen.gif" border="0"
                                          alt="<fmt:message key="Closed" bundle="${resterm}"/>" title="<fmt:message key="Closed" bundle="${resterm}"/>" align="left"/>
                                        (${discNoteCount})
                                        <c:set var="discNoteCount" value="${0}"/>
                                    </c:if>
                                </td><%-- closed --%>
                                <td class="table_cell">
                                    <c:set var="discNoteCount" value="${discNoteMap['Not Applicable']}"/>
                                    <c:if test="${discNoteCount > 0}">
                                        <img
                                          name="icon_Note" src="images/icon_flagWhite.gif" border="0"
                                          alt="<fmt:message key="Not_Applicable" bundle="${resterm}"/>" title="<fmt:message key="Not_Applicable" bundle="${resterm}"/>" align="left"/>
                                        (${discNoteCount})
                                        <c:set var="discNoteCount" value="${0}"/>
                                    </c:if>
                                </td><%-- N/A --%>
                                <td class="table_cell">
                                    <a onmouseup="javascript:setImage('bt_View1','images/bt_View.gif');" onmousedown="javascript:setImage('bt_View1','images/bt_View_d.gif');" href="EnterDataForStudyEvent?eventId=${displayStudyEventBean.studyEvent.id}">
                                        <img hspace="6" border="0" align="left" title="View" alt="View" src="images/bt_View.gif" name="bt_View1"/>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<div style="width: 250px">
	
<div style="width: 250px">

	<c:choose>
		<c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
			<div class="table_title_Admin">
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${userRole.manageStudy}">
					<div class="table_title_Manage">
				</c:when>
				<c:otherwise>
					<div class="table_title_Submit">
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</div>
	<c:choose>
		<c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin && module=='admin'}">
			<div class="table_title_Admin">
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${userRole.manageStudy}">
					<div class="table_title_manage">
				</c:when>
				<c:otherwise>
					<div class="table_title_submit">
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

<!-- End Main Content Area -->


<c:import url="../include/workflow.jsp">
    <c:param name="module" value="manage"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>