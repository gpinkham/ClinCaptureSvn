<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" style="position: relative; min-height: 100%;">

<head>
	<link rel="icon" href="<c:url value='/images/favicon.ico'/>"/>
	<link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>"/>
	<title>ClinCapture</title>
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
	<link rel="stylesheet" href="<c:url value='/includes/styles.css?r=${revisionNumber}'/>" type="text/css"/>
	<link rel="stylesheet" href="<c:url value='/includes/NewLoginStyles.css?r=${revisionNumber}'/>" type="text/css"/>
	<script type="text/JavaScript" language="JavaScript"
			src="<c:url value='/includes/jmesa/jquery-1.3.2.min.js'/>"></script>
	<script type="text/javascript" language="JavaScript"
			src="<c:url value='/includes/jmesa/jquery.blockUI.js?r=${revisionNumber}'/>"></script>
	<script type="text/JavaScript" language="JavaScript"
			src="<c:url value='/includes/global_functions_javascript.js?r=${revisionNumber}'/>"></script>
	<script type="text/JavaScript" language="JavaScript"
			src="<c:url value='/includes/theme.js?r=${revisionNumber}'/>"></script>
</head>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<body class="login_BG" onLoad="document.getElementById('username').focus();" style="margin-bottom: 170px;">
<div class="login_BG">
	<center>

		<!-- ClinCapture logo -->
		<%
			String ua = request.getHeader("User-Agent");
			String temp = "";
			String iev = "";
			if (ua != null && ua.indexOf("MSIE") != -1) {
				temp = ua.substring(ua.indexOf("MSIE"), ua.length());
				iev = temp.substring(4, temp.indexOf(";"));
				iev = iev.trim();
			}
			if (iev.length() > 1 && Double.valueOf(iev) < 7) {
		%>
		<div ID="OClogoIE6">&nbsp;</div>
		<%} else {%>
		<div ID="OClogo">&nbsp;</div>
		<%}%>
		<!-- end ClinCapture logo -->

		<span class='dbTitle'> <jsp:include page="../login-include/login-dbtitle.jsp"/> </span> <br><br><br>

		<table border="0" cellpadding="0" cellspacing="0" class="loginBoxes">
			<tr>
				<td class="loginBox_T">&nbsp;</td>
			</tr>
			<tr>
				<td class="loginBox" align="center">
					<div ID="loginBox" align="center">
						<!-- Login box contents -->
						<jsp:include page="../login-include/login-box.jsp"/>
						<!-- End Login box contents -->
					</div>
				</td>
			</tr>
		</table>
	</center>
</div>


<div id="requestPasswordForm" style="display:none;">
	<c:import url="requestPasswordPop.jsp">
	</c:import>
</div>

<!-- End Main Content Area -->
<jsp:include page="../login-include/login-footer.jsp"/>

</body>
</html>
