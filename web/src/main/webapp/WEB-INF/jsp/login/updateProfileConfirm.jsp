<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<jsp:include page="../include/home-header.jsp"/>


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
<jsp:useBean scope="session" id="userBean11" class="org.akaza.openclinica.bean.login.UserAccountBean"/>
<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_user_profile_updates" bundle="${resword}"/>
	</span>
</h1>


<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">

  <tr valign="top"><td class="table_header_column"><fmt:message key="first_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.firstName}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="last_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.lastName}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="email" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.email}"/></td></tr>
	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="time_zone" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<c:out value="${userBean1.userTimeZoneId}"/>
		</td>
	</tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="institutional_affiliation" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.institutionalAffiliation}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="default_active_study" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${newActiveStudy.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="password_challenge_question" bundle="${resword}"/>:</td><td class="table_cell">
      <c:out value="${userBean1.passwdChallengeQuestion}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="password_challenge_answer" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.passwdChallengeAnswer}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="phone" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean1.phone}"/></td></tr>

  </td></tr>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
<table><tr><td>
<form action="UpdateProfile" method="post">
 <input type="hidden" name="action" value="back">
 <input type="submit" name="back" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium">
</form></td>
<td><form action="UpdateProfile?action=submit" method="post">
 <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium"></form>
 <!-- 
 <td>
 <form action="UpdateProfile" method="post">
 <input type="hidden" name="action" value="back">
 <input type="submit" name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/></td></form> -->

</table>

<jsp:include page="../include/footer.jsp"/>
