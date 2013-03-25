<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>


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

<h1><span class="title_manage"><fmt:message key="administer_users" bundle="${resword}"/> <a href="javascript:openDocWindow('help/6_2_administerUsers_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>
<%-- 
<div class="homebox_bullets"><a href="CreateUserAccount"><fmt:message key="create_a_new_user" bundle="${resword}"/></a></div><br/>
<div class="homebox_bullets"><a href="AuditUserActivity?restore=true"><fmt:message key="audit_user_activity" bundle="${resword}"/></a></div><br/>
<div class="homebox_bullets"><a href="Configure"><fmt:message key="lock_out_configuration" bundle="${resword}"/></a></div><br/>
<div class="homebox_bullets"><a href="ConfigurePasswordRequirements"><fmt:message key="configure_password_requirements" bundle="${resword}"/></a></div>
<p></p>
--%>

<c:import url="../include/showTable.jsp">
	<c:param name="rowURL" value="showUserAccountRow.jsp" />
</c:import>
<br>
<table>
<%--td>
 <input id="GoBackToHome" class="button_medium" type="button" name="BTN_Back" title="<fmt:message key="go_back_home" bundle="${resword}"/>" value="<fmt:message key="back" bundle="${resword}"/>" onclick="window.location.href=('MainMenu');"/>
</td--%>
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

<jsp:include page="../include/footer.jsp"/>
