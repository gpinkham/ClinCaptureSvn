<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy_top_pages.jsp" />

<script language="JavaScript">
	<c:import url="../../../includes/js/pages/edit_user_accout.js?r=${revisionNumber}" />
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='presetValues' class='java.util.HashMap' />
<jsp:useBean scope='request' id='userTypes' class='java.util.ArrayList' />

<c:set var="firstName" value="" />
<c:set var="lastName" value="" />
<c:set var="email" value="" />
<c:set var="phone" value="" />
<c:set var="institutionalAffiliation" value="" />
<c:set var="userTypeId" value="${0}" />
<c:set var="resetPassword" value="${0}" />
<c:set var="displayPwd" value="no" />

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == "firstName"}'>
        <c:set var="firstName" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "lastName"}'>
        <c:set var="lastName" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "email"}'>
        <c:set var="email" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "phone"}'>
      <c:set var="phone" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "institutionalAffiliation"}'>
        <c:set var="institutionalAffiliation" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "userType"}'>
        <c:set var="userTypeId" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "resetPassword"}'>
        <c:set var="resetPassword" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "displayPwd"}'>
        <c:set var="displayPwd" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "runWebServices"}'>
        <c:set var="runWebServices" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == "notifyPassword"}'>
        <c:set var="notifyPassword" value="${presetValue.value}" />
    </c:if>
	<c:if test='${presetValue.key == "userTimeZoneID"}'>
		<c:set var="userTimeZoneID" value="${presetValue.value}"/>
	</c:if>
</c:forEach>
<h1>
	<span class="first_level_header">
		<fmt:message key="edit_a_user_account" bundle="${resword}"/>
	</span>
</h1>

<form action="EditUserAccount" id="edit_user" method="post" onsubmit="return isPhoneNumberValid('phone', '<fmt:message key="invalid_phone_number_format" bundle="${resword}"/>');">
<jsp:include page="../include/showSubmitted.jsp" />
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="userId" /></jsp:include>
<jsp:include page="../include/showHiddenInput.jsp"><jsp:param name="fieldName" value="stepNum" /></jsp:include>

<div style="width: 400px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">

<!-- Table Contents -->
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td class="table_header_column_top"><fmt:message key="user_name" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<c:out value="${userName}"/>
					</div>
				</td>
				<td>*</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td class="table_header_column_top"><fmt:message key="first_name" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="firstName" value="<c:out value="${firstName}"/>" size="20"
						       class="formfieldM"/>
					</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="firstName"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr valign="top">
	<td class="table_header_column"><fmt:message key="last_name" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="lastName" value="<c:out value="${lastName}"/>" size="20"
						       class="formfieldM"/>
					</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
				<td colspan="2"><jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="lastName"/>
					</jsp:include>
			</tr>
		</table>
	</td>
</tr>


<tr valign="top">
	<td class="table_header_column"><fmt:message key="email" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="email" value="<c:out value="${email}"/>" size="20"
						       class="formfieldM"/>
					</div>
				</td>
				<td>(<fmt:message key="username@institution" bundle="${resword}"/>) *</td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="email"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr valign="top">
	<td class="table_header_column"><fmt:message key="phone" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" id="phone" name="phone" maxlength="20"
						       onchange="javascript:changeIcon();" value="<c:out value="${phone}"/>" size="20"
						       class="formfieldM"/>
					</div>
				</td>
				<td><span style="white-space: nowrap;">(<fmt:message key="phone_number_format_ex"
				                                                     bundle="${resword}"/>)</span><br/>&nbsp;
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="phone"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr valign="top">
	<td class="table_header_column"><fmt:message key="institutional_affiliation" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<input type="text" name="institutionalAffiliation"
						       value="<c:out value="${institutionalAffiliation}"/>" size="20"
						       class="formfieldM"/>
					</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="institutionalAffiliation"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>

	<tr valign="top">
		<td class="table_header_column"><fmt:message key="time_zone" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<div class="formfieldXL_BG">
							<select id="userTimeZoneID" name="userTimeZoneID" class="formfieldXL">
								<c:forEach var="timeZoneID" items="${timeZoneIDsSorted}">
									<option value='<c:out value="${timeZoneID.key}" />' <c:if test="${timeZoneID.key == userTimeZoneID}">selected</c:if>>
										<c:out value="${timeZoneID.value}"/>
									</option>
								</c:forEach>
							</select>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>

<tr valign="top">
	<td class="table_header_column"><fmt:message key="user_type" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<div class="formfieldM_BG">
						<c:if test="${editedUser.name eq 'root'}"><input type="hidden" id="userTypeInput"
						                                                 name="userType" value=""/></c:if>
						<select
								<c:if test="${editedUser.name ne 'root'}">name="userType"</c:if>
								<c:if test="${editedUser.name eq 'root'}">disabled="disabled"</c:if>
								class="formfieldM">
							<c:forEach var="type" items="${userTypes}">
								<c:choose>
									<c:when test="${userTypeId == type.id}">
										<option value='<c:out value="${type.id}" />' selected><c:out
												value="${type.name}"/></option>
										<c:set var="currentTypeId" value="${userTypeId}"/>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${type.id}" />'><c:out
												value="${type.name}"/></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<c:if test="${editedUser.name eq 'root'}">
							<script>$("#userTypeInput").val("${currentTypeId}")</script>
						</c:if>
					</div>
				</td>
				<td>*</td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="userType"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>

<tr valign="top">
	<td class="table_header_column"><fmt:message key="can_run_web_services" bundle="${resword}"/>:</td>
	<td valign="top">
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
					<input type="checkbox" name="runWebServices" id="runWebServices" value="1"
					       <c:if test="${runWebServices != 0}">checked</c:if>
							>
				</td>
				<td></td>
			</tr>
			<tr>
				<td colspan="2">
					<jsp:include page="../showMessage.jsp">
						<jsp:param name="key" value="runWebServices"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td colspan=2 class="table_header_column">
		<input type="checkbox" name="resetPassword" id="resetPassword" value="1"
		<c:if test="${resetPassword != 0}">
		       checked
		</c:if>
		       onclick="javascript:a()">
		<fmt:message key="reset_password" bundle="${resword}"/>
	</td>
</tr>
<tr>
	<td colspan="2">
		<c:choose>
			<c:when test="${displayPwd == 'no'}">
				<c:if test="${notifyPassword eq 'email'}">
					<input type="radio" name="displayPwd" id="displayPwd0" value="no"
					       disabled="true"><fmt:message key="send_user_password_via_email" bundle="${resword}"/>
				</c:if>
				<input type="radio" checked name="displayPwd" id="displayPwd1" value="yes"
				       disabled="true"><fmt:message key="show_user_password_to_admin" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<c:if test="${notifyPassword eq 'email'}">
					<input type="radio" name="displayPwd" id="displayPwd0" value="no"
					       disabled="true"><fmt:message key="send_user_password_via_email" bundle="${resword}"/>
				</c:if>
				<input type="radio" checked name="displayPwd" id="displayPwd1" value="yes"
				       disabled="true"><fmt:message key="show_user_password_to_admin" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<input type="button" value="<fmt:message key="back" bundle="${resword}"/>"
			       title="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50"
			       onclick="back_checkEditUserFormState();"/>
			<input type="submit" name="continue" value="<fmt:message key="continue" bundle="${resword}"/>"
			       class="button_medium medium_continue"/>
			<c:if test="${userBean.sysAdmin && editedUser.name != 'root' && isSiteLevelUser}">
				<input type="button" value="<fmt:message key="crfs_masking" bundle="${resword}"/>" class="button_medium"
				       onclick="redirectRequestToMaskingPage();"/>
			</c:if>
			<br/>
		</td>
	</tr>
</table>
</form>

<div style="display:none">
	<input type="hidden" value="${notifyPassword eq 'email'}" id="notifyPassword"/>
	<input type="hidden" value='<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>' id="you_have_unsaved_data2"/>
	<input type="hidden" value="${navigationURL}" id="navigationURL"/>
	<input type="hidden" value="${defaultURL}" id="defaultURL"/>
	<input type="hidden" value='<fmt:message key="sure_to_cancel" bundle="${resword}"/>' id="sure_to_cancel"/>
</div>

<jsp:include page="../include/footer.jsp"/>
