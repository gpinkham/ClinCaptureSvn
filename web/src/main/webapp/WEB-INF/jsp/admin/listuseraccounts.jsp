<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>

<script type="text/javascript" language="javascript">
    jQuery(window).load(function() {
    	highlightLastAccessedObject(rowHighlightTypes.MULTIPLE);
    });
    
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
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
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="administer_users" bundle="${resword}"/> 
		<a href="javascript:openDocWindow('help/6_2_administerUsers_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>

<style>
    div.tablebox_center > table {
        width: 100%;
    }
</style>

<c:import url="../include/showTable.jsp">
	<c:param name="rowURL" value="showUserAccountRow.jsp" />
</c:import>
<br>
<table>
<td>
 <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
</td>
<c:if test="${userRole.id ne 10}">
<td>
	<c:set var="createUserBTNCaption"><fmt:message key="create_user" bundle="${resword}"/></c:set>
	<input id="CreateUser" type="button" name="BTN_Create" value="${createUserBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(createUserBTNCaption, "")}"
		   onclick="window.location.href=('CreateUserAccount');"/>
</td>
</c:if> 
<td>
	<c:set var="auditLoginsBTNCaption"><fmt:message key="audit_logins" bundle="${resword}"/></c:set>
	<input id="AuditLogins" type="button" name="BTN_Audit" value="${auditLoginsBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(auditLoginsBTNCaption, "")}"
		   onclick="window.location.href=('AuditUserActivity?restore=true');"/>
</td>
<c:if test="${userRole.id ne 10}">
<td>
	<c:set var="loginLockoutBTNCaption"><fmt:message key="login_lockout" bundle="${resword}"/></c:set>
	<input id="LoginLockout" type="button" name="BTN_Login" value="${loginLockoutBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(loginLockoutBTNCaption, "")}"
		   onclick="window.location.href=('Configure');"/>
</td>
</c:if>
<c:if test="${userRole.id ne 10}">
<td>
	<c:set var="passwordPoliciesBTNCaption"><fmt:message key="password_policies" bundle="${resword}"/></c:set>
	<input id="PasswordPolicies" type="button" name="BTN_Password" value="${passwordPoliciesBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(passwordPoliciesBTNCaption, "")}"
		   onclick="window.location.href=('ConfigurePasswordRequirements');"/>
</td>
</c:if>
</table>

<script>$("#contentTable td.table_header_row:last").css("width", "200px");</script>

<input id="accessAttributeName" type="hidden" value="data-cc-userId">
<jsp:include page="../include/footer.jsp"/>
