<%@page contentType="text/html;charset=UTF-8" language="java" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>ClinCapture Web Services</title>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
	<link rel="shortcut icon" href='<c:url value="/images/favicon.png"/>' />
	<link rel="stylesheet" href="<c:url value='/includes/styles.css'/>" type="text/css"/>
	<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery-1.3.2.min.js'/>"></script>
 </head>

<body>
	<div class="ws_main_wrapper" align="center">
		<div class="cc_logo"></div>
		<div class="header_line"></div>
		<div class="ws_logo"></div>
		<table>
			<tr>
				<td>
					<div class="infobox_wide">
						<div class="infobox_text">
							<h1><fmt:message key="congratulations" bundle="${resword}"/>!</h1>
							<p><fmt:message key="successfully_installed_ws" bundle="${restext}"/>.</p>
							<p>
								<fmt:message key="please_use" bundle="${restext}"/> 
								<a href="#" target="_blank"><fmt:message key="user_manual" bundle="${resword}"/></a>
								<fmt:message key="get_additional_info" bundle="${restext}"/>. 
								<fmt:message key="list_of_ws" bundle="${restext}"/> 
								<a href="#" target="_blank"><fmt:message key="here" bundle="${resword}"/>.</a>
							</p>
						</div>
					</div>
				</td>
				<td>
					<div class="infobox_narrow">
						<div class="infobox_narrow_text">
							<p><fmt:message key="links" bundle="${restext}"/>:</p>
							<ul>
								<li><a href="http://www.clinovo.com/contact"><fmt:message key="support_page" bundle="${resword}"/></a></li>
								<li><a href="http://www.clinovo.com/clincapture/forum"><fmt:message key="forum" bundle="${resword}"/></a></li>
								<li><a href="http://www.clinovo.com/clincapture/community"><fmt:message key="community" bundle="${resword}"/></a></li>
							</ul>
						</div>
					</div>
				</td>
			</tr>
		</table>
		<table class="footer_table">
			<tr>
				<td class="dotted_row"></td>
				<td rowspan="2" class="clinovo_logo"><a href="http:\\www.clincapture.com"><img src="../../images/logo_system_200x61.png"/></a></td>
			</tr>
			<tr>
				<td height="10px"></td>			</tr>
		</table>
	</div>
</body>
</html>