<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage" />
<ui:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="exceptions" />
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="../include/managestudy_top_pages.jsp" />

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jspfunctions.js'/>"></script>

<jsp:include page="../include/sideAlert.jsp" />
<link rel="stylesheet" href="/includes/styles.css" type="text/css">
<link rel="stylesheet" href="<c:url value='/includes/jmesa/jmesa.css'/>" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jmesa.js'/>"></script>
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery.jmesa.js'/>"></script>

<script type="text/javascript">

    $(window).load(function(){
        highlightLastAccessedObject();
    });

    function onInvokeAction(id) {
        createHiddenInputFieldsForLimitAndSubmit(id);
    }

    function checkCRFLocked(ecId, url){
        jQuery.post("../CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                openDocWindow(url);
            }else{
                alertDialog({ message:data, height: 150, width: 500 });
            }
        });
    }

</script>

<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
        </a>
        <b><fmt:message key="instructions" bundle="${restext}" /></b>
        <div class="sidebar_tab_content">
            <fmt:message key="crf_evaluation_instruction" bundle="${restext}" />
        </div>
    </td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10">
        </a>
        <b><fmt:message key="instructions" bundle="${restext}" /></b>
    </td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<h1>
	<span class="first_level_header">
		<fmt:message key="crf_evaluation" bundle="${resword}"/>
    	<a href="javascript:openDocWindow('../help/8_0_crfEvaluation_Help.html')">
            <img src="../images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>">
        </a>
	</span>
</h1>

${summaryTable}
<br>
<form action="${pageContext.request.contextPath}/pages/crfEvaluation" style="clear:left; float:left;">
    ${crfEvaluationTable}
</form>

<br>

<div style="clear:left; float:left">
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
           value="<fmt:message key="back" bundle="${resword}"/>"
           class="button_medium"
           onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
</div>

<input id="accessAttributeName" type="hidden" value="data-cc-crfEvaluationId">

<jsp:include page="../include/footer.jsp" />
