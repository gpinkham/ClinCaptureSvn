<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>


<jsp:include page="include/managestudy_top_pages.jsp"/>

<script type="text/javascript" language="javascript">
    jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
    
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="cancel_data_export_info" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>
<link rel="stylesheet" href="../includes/jmesa/jmesa.css?r=${revisionNumber}" type="text/css">
<!--script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.3.2.min.js"></script-->
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery.jmesa.js?r=${revisionNumber}"></script>
<script type="text/javascript">
    function onInvokeAction(id,action) {
        setExportToLimit(id, '');
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        //location.href = '${pageContext.request.contextPath}/ViewCRF?module=manage&crfId=' + '${crf.id}&' + parameterString;
    }
</script>

<h1>
	<span class="first_level_header">
		<fmt:message key="currently_executing_data_export_jobs" bundle="${resword}"/>
	</span>
</h1>


<script type="text/javascript">
    function prompt(formObj,theStudySubjectId){
    	
    	formObj.action='${pageContext.request.contextPath}/pages/unSdvStudySubject';
        formObj.theStudySubjectId.value=theStudySubjectId;
        
        confirmSubmit({
        	message: "<fmt:message key="uncheck_sdv" bundle="${resmessages}"/>",
        	height: 150, 
        	width: 500,
        	form: formObj
        });
    }
</script>
<div id="subjectSDV">
    <form name='scheduledJobsForm' action="${pageContext.request.contextPath}/pages/listCurrentScheduledJobs">
        <%--<fmt:message key="select_all_on_page" bundle="${resword}"/> <input type=checkbox name='checkSDVAll' onclick='selectAllChecks(this.form)'/>
        <br />--%>
        <input type="hidden" name="studyId" value="${param.studyId}">
        
        <%--This value will be set by an onclick handler associated with an SDV button --%>
        <input type="hidden" name="theJobName" value="0">
            <input type="hidden" name="theJobGroupName" value="0">
			<input type="hidden" name="theTriggerGroupName" value="0">
            <input type="hidden" name="theTriggerName" value="0">
        <%-- the destination JSP page after removal or adding SDV for an eventCRF --%>
        <input type="hidden" name="redirection" value="listCurrentScheduledJobs">

  ${scheduledTableAttribute}
        <br />
       
        <%--<input type="submit" name="sdvAllFormCancel" class="button_medium" value="Cancel" onclick="this.form.action='${pageContext.request.contextPath}/pages/viewSubjectAggregate';this.form.submit();"/>
    </form>--%>
    <script type="text/javascript">hideCols('s_sdv',[2,3,4])</script>

</div>
	<br>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
<input id="accessAttributeName" type="hidden" value="data-cc-runningJobId">
<jsp:include page="include/footer.jsp"/>
</body>
<script language="JavaScript">
jQuery('#footerInnerTable1').attr('width','1100px');
</script>
</html>