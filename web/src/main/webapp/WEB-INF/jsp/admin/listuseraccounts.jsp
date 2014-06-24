<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
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
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
</td>
<td>
 <input id="CreateUser" class="button_medium" type="button" name="BTN_Create" value="<fmt:message key="create_user" bundle="${resword}"/>" onclick="window.location.href=('CreateUserAccount');"/>
</td>
<td>
 <input id="AuditLogins" class="button_medium" type="button" name="BTN_Audit" value="<fmt:message key="audit_logins" bundle="${resword}"/>" onclick="window.location.href=('AuditUserActivity?restore=true');"/>
</td>
<td> 
 <input id="LoginLockout" class="button_medium" type="button" name="BTN_Login" value="<fmt:message key="login_lockout" bundle="${resword}"/>" onclick="window.location.href=('Configure');"/>
</td>
<td> 
 <input id="PasswordPolicies" class="button_medium" type="button" name="BTN_Password" value="<fmt:message key="password_policies" bundle="${resword}"/>" onclick="window.location.href=('ConfigurePasswordRequirements');"/>
</td>
</table>

<script>$("#contentTable td.table_header_row:last").css("width", "200px");</script>

<input id="accessAttributeName" type="hidden" value="data-cc-userId">
<jsp:include page="../include/footer.jsp"/>
