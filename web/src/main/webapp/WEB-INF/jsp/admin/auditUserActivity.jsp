<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>


<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<jsp:include page="../include/admin-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>


<link rel="stylesheet" href="includes/jmesa/jmesa.css?r=${revisionNumber}" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js?r=${revisionNumber}"></script>

<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('userLogins') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/AuditUserActivity?'+ parameterString;
    }
    jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
</script>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='crf' class='org.akaza.openclinica.bean.admin.CRFBean'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="audit_logins" bundle="${resword}"/>
	</span>
</h1>

<jsp:useBean id="now" class="java.util.Date" />
<P><I><fmt:message key="local_time_info" bundle="${resword}"/></I></P>
<div id="auditUserLoginDiv">
    <form  action="${pageContext.request.contextPath}/AuditUserActivity">
        <input type="hidden" name="module" value="admin">
        <input type="hidden" name="crfId" value="${crf.id}">
        ${auditUserLoginHtml}
    </form>
</div>


<br>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

<input id="accessAttributeName" type="hidden" value="data-cc-auditUserId">
<jsp:include page="../include/footer.jsp"/>
