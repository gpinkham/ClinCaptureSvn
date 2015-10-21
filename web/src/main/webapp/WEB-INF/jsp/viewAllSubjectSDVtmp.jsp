<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>

<jsp:include page="include/managestudy_top_pages.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<jsp:include page="include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="design_implement_sdv" bundle="${restext}" />
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
	</td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>

<link rel="stylesheet" href="../includes/jmesa/jmesa.css?r=${revisionNumber}" type="text/css">

<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery.jmesa.js?r=${revisionNumber}"></script>

<%-- view all subjects starts here --%>
<script type="text/javascript">

    function onInvokeAction(id,action) {
        setExportToLimit(id, '');
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
    }
    jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
</script>

<h1>
	<span class="first_level_header">
		<fmt:message key="sdv_sdv_for" bundle="${resword}"/> <c:out value="${study.name}"/>
			<a href="javascript:openDocWindow('../help/3_1_SDV_Help.html')">
		<img src="../images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
	</span>
</h1>

<jsp:useBean scope='session' id='sSdvRestore' class='java.lang.String' />

<c:set var="restore" value="true"/>
<c:if test="${sSdvRestore=='false'}">
	<c:set var="restore" value="false"/>
</c:if>
<c:set var="additionalParameter" value=""/>
<c:if test="${showBackButton}">
	<c:set var="additionalParameter" value="sbb=true&"/>
</c:if>

<div id="searchFilterSDV">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="bottom" id="Tab1'">
				<div id="Tab1Selected">
				<div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h">
					<span class="tabtext"><fmt:message key="view_by_event_CRF" bundle="${resword}"/></span>
				</div></div></div>
				</div>
			</td>

			<td valign="bottom" id="Tab2'">
				<div id="Tab2NotSelected">
				<div class="tab_BG"><div class="tab_L"><div class="tab_R">
				<a class="tabtext"
					title="<fmt:message key="view_by_studysubjectID" bundle="${resword}"/>"
					href='viewSubjectAggregate?${additionalParameter}s_sdv_restore=${restore}&studyId=${studyId}'>

					<fmt:message key="view_by_studysubjectID" bundle="${resword}" />
				</a>
				</div></div></div>
				</div>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
    function prompt(formObj,crfId,element){
    	
    	formObj.action='${pageContext.request.contextPath}/pages/handleSDVRemove';
        formObj.crfId.value=crfId;
        confirmSubmit({
    		message: '<fmt:message key="uncheck_sdv" bundle="${resmessages}"/>',
    		height: 150,
    		width: 500,
    		form: formObj
    	});
        setAccessedObjected(element);
 	}
</script>

<div id="subjectSDV">
	<form name='sdvForm' action="${pageContext.request.contextPath}/pages/viewAllSubjectSDVtmp">
		<input type="hidden" name="studyId" value="${param.studyId}">
		<input type="hidden" name=imagePathPrefix value="../">

		<%--This value will be set by an onclick handler associated with an SDV button --%>
		<input type="hidden" name="crfId" value="0">

		<%-- the destination JSP page after removal or adding SDV for an eventCRF --%>
		<input type="hidden" name="redirection" value="viewAllSubjectSDVtmp">

		${sdvTableAttribute}
		<br />

		<input type="hidden" name="sbb" value="true"/>

		<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
			value="<fmt:message key="back" bundle="${resword}"/>"
			class="button_medium medium_back"
			onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

		<c:set var="sdvAllFormBTNCaption"><fmt:message key="sdv_all_checked" bundle="${resword}"/></c:set>
		<input type="submit" name="sdvAllFormSubmit" value="${sdvAllFormBTNCaption}"
			   class="${ui:getHtmlButtonCssClass(sdvAllFormBTNCaption, "")}"
			   onclick="this.form.method='POST';this.form.action='${pageContext.request.contextPath}/pages/handleSDVPost';this.form.submit();" />
	</form>
</div>
<input id="accessAttributeName" type="hidden" value="data-cc-sdvCrfId"/>
<jsp:include page="include/footer.jsp"/>
<script>window.sdvPage = true;</script>
