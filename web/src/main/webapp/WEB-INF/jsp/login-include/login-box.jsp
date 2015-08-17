<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div ID="login" align="left">
	<form action="<c:url value='/j_spring_security_check'/>" method="post" autocomplete='off'>
		<input type="hidden" name="domain_name" value=""/>
		<input type="password" style="display:none"/>

		<h1><fmt:message key="login" bundle="${resword}"/></h1>
		<b><fmt:message key="user_name" bundle="${resword}"/></b>

		<div class="formfieldM_BG">
			<input type="text" id="username" name="j_username" class="formfieldM">
		</div>
		<b><fmt:message key="password" bundle="${resword}"/></b>

		<div class="formfieldM_BG">
			<input type="password" id="j_password" name="j_password" class="formfieldM">
		</div>
		<input type="submit" name="submit" value="<fmt:message key='login_button' bundle='${resword}'/>"
			   class="loginbutton"/>

		<div style="display:inline; position:absolute;"><a style="" href="#" id="requestPassword"> <fmt:message
				key="forgot_password" bundle="${resword}"/></a></div>

		<div style="display: none"  id="exit"><a href="${pageContext.request.contextPath}/MainMenu"> <fmt:message
				key="exit" bundle="${resword}"/></a></div>
	</form>
	<br/>
	<jsp:include page="../login-include/login-alertbox.jsp"/>
</div>

<script type="text/javascript">
	$(document).ready(function () {

		$('input[name=domain_name]').val(document.location.host);

		$('#requestPassword').click(function () {
			$.blockUI({message: $('#requestPasswordForm'), css: {left: "200px", top: "180px"}});
		});

		$('#cancel').click(function () {
			$.unblockUI();
			return false;
		});
	});
</script>