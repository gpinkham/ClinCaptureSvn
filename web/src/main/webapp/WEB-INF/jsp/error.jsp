<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.akaza.openclinica.web.SQLInitServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope="session" id="passwordExpired" class="java.lang.String"/>

<c:choose>
	<c:when test="${userBean != null && userRole != null && userRole.role.name != 'invalid' && passwordExpired == 'no'}">
		<jsp:include page="include/home-header.jsp"/>


		<jsp:include page="include/sidebar.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="login-include/login-header.jsp"/>

		<jsp:include page="include/userbox-inactive.jsp"/>
		<table border="0" cellpadding=0" cellspacing="0">
			<tr><td class="sidebar" valign="top"><br><b><a href="j_spring_security_logout"><fmt:message key="logout" bundle="${restext}"/></a></b></br></td>
				<td class="content" valign="top">
	</c:otherwise>
</c:choose>

<h1>
	<span class="first_level_header">
		<fmt:message key="an_error_has_ocurred" bundle="${resword}"/>
	</span>
</h1>
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<%-- input is needed for Selenium tests --%>
<input type="hidden" id="error_page" value="true"/>

<font class="bodytext">
<fmt:message key="error_page" bundle="${resword}">
	<%--<fmt:param><%=request.getHeader("Referer")%></fmt:param>--%>
	<%-- tbh 02/2010 remove HTML/XML from the referer name --%>
	<fmt:param><%=SQLInitServlet.getField("mail.errormsg")%></fmt:param>
</fmt:message>
<br></br>
<br></br>
<input type="button" name="BTN_Back_Smart" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 

</font>
</td></tr>
</table>

<c:choose>
	<c:when test="${userBean != null && userRole != null && userRole.role.name != 'invalid' && passwordExpired == 'no'}">
		<jsp:include page="include/footer.jsp"/>
	</c:when>
	<c:otherwise>
		<jsp:include page="login-include/login-footer.jsp"/>
	</c:otherwise>
</c:choose>