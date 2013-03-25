<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<%@page import="org.akaza.openclinica.web.SQLInitServlet"%>


<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 

<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "http://dev.openclinica.org/ClinCapture/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>
<!-- Breadcrumbs -->

	<div class="breadcrumbs">
		
	&nbsp;

	
	</div>

<!-- End Breadcrumbs -->

				</td>

<!-- Help and ClinCapture Feedback Buttons -->
				<td valign="top">
					&nbsp;
				</td>

<!-- end Help and ClinCapture Feedback Buttons -->

	<td valign="top" align="right">


<!-- User Box -->


<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
	<tr>
		<td class="sidebar" valign="top">

<!-- Sidebar Contents -->


<div ID="login">
    <form action="<c:url value='/j_spring_security_check'/>" method="post">
        <h1><fmt:message key="login" bundle="${resword}"/></h1>
        <b><fmt:message key="user_name" bundle="${resword}"/>:</b>
        <div class="loginbox_BG"><input type="text" name="j_username" class="loginbox"/></div>

        <b><fmt:message key="password" bundle="${resword}"/></b>
        <div class="loginbox_BG"><input type="password" name="j_password" class="loginbox" /></div>

        <input type="submit" name="submit" value="<fmt:message key='login' bundle='${resword}'/>" class="loginbutton" />
    </form>
	
	<!--<div ID="login">
	<form name="myform" action="j_security_check" focus="j_username" method=POST>
	<h1><fmt:message key="login" bundle="${resword}"/></h1>

	<b><fmt:message key="user_name" bundle="${resword}"/>:</b>
	<div class="loginbox_BG">
	<input type="text" name="j_username" class="loginbox">
	</div>

	<b><fmt:message key="password" bundle="${resword}"/></b>
	<div class="loginbox_BG">
	<input type="password" name="j_password"  class="loginbox">
	</div>

	
	<input type="submit" value="<fmt:message key="login" bundle="${resword}"/>" class="loginbutton">
   --></form>
	
<%--
	<br><br>
	<a href="RequestAccount"><fmt:message key="request_an_account" bundle="${resword}"/></a>
	<br><br>
	<a href="RequestPassword"><fmt:message key="forgot_password" bundle="${resword}"/></a>
--%>
	</div>


<!-- End Sidebar Contents -->

				<br><img src="<tags:imagesLink value="spacer.gif"/>" width="120" height="1">

				</td>
				<td class="content" valign="top">
