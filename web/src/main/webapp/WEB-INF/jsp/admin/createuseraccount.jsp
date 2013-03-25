<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>


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
<jsp:useBean scope='request' id='studies' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='roles' class='java.util.HashMap'/>
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:forEach var="currRole" items="${roles}" varStatus="status">
    <c:set var="rolesCount" value="${status.count}" />
</c:forEach>
<c:choose>
    <c:when test="${rolesCount > 3}">
        <c:set var="inclRoleCode1" value="2" />
        <c:set var="inclRoleCode2" value="6" />
    </c:when>
    <c:otherwise>
        <c:set var="inclRoleCode1" value="4" />
        <c:set var="inclRoleCode2" value="5" />
    </c:otherwise>
</c:choose>

<c:set var="userName" value="" />
<c:set var="firstName" value="" />
<c:set var="lastName" value="" />
<c:set var="email" value="" />
<c:set var="institutionalAffiliation" value="" />
<c:set var="activeStudyId" value="${0}" />
<c:set var="roleId" value="${0}" />
<c:set var="userTypeId" value="${2}" />
<c:set var="displayPwd" value="no" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "userName"}'>
		<c:set var="userName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "firstName"}'>
		<c:set var="firstName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastName"}'>
		<c:set var="lastName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "email"}'>
		<c:set var="email" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "institutionalAffiliation"}'>
		<c:set var="institutionalAffiliation" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "activeStudy"}'>
		<c:set var="activeStudyId" value="${presetValue.value}" />
	</c:if>
    <c:if test="${activeStudyId == 0}">
        <c:set var="activeStudyId" value="${activeStudy}"/>
    </c:if>
    <c:if test='${presetValue.key == "role"}'>
		<c:set var="roleId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "type"}'>
		<c:set var="userTypeId" value="${presetValue.value}" />
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
</c:forEach>

<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {

    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListUserAccounts";
       return true;
      } else {
        return false;
       }
     }
     return true;

  }
  function checkGoToEntryStatusMod1(Message, Adress) {
	    closing = false;        
		objFlag = MM_findObj('pageIsChanged');
	    if (objFlag != null && objFlag.value=='true') {
	        return confirmGoTo(Message, Adress);
	    } else {
	        window.location.href=(Adress);
	    }
	    return true;
	}
  function changeFlag(){
	  objFlag = MM_findObj('pageIsChanged');
	  objFlag.value='true';
  }
   //-->
 function sendUrl() {
	objFlag = MM_findObj('pageIsChanged');
	objFlag.value='true';
    document.getElementById('changeRoles').value = 'true';
    document.forms[1].submit();
 }
</script>

<h1><span class="title_manage"><fmt:message key="create_a_user_account" bundle="${resword}"/></span></h1>

<fmt:message key="field_required" bundle="${resword}"/>
<form action="CreateUserAccount" method="post">
<jsp:include page="../include/showSubmitted.jsp" />

<%
java.lang.String fieldName;
java.lang.String fieldValue;
int selectedValue;
%>
<div style="width: 450px">

<!-- These DIVs define shaded box borders -->

	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">


		<!-- Table Contents -->
<input type="hidden" id="changeRoles" name="changeRoles">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
	<tr valign="top">
		<td class="formlabel"><fmt:message key="username2" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" id="userName" onchange="javascript:changeFlag();" name="userName" value="<c:out value="${userName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="userName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel"><fmt:message key="first_name" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" id="firstName" name="firstName" onchange="javascript:changeFlag();" value="<c:out value="${firstName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
		<td class="formlabel"><fmt:message key="last_name" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
					<input type="text" id="lastName" name="lastName" onchange="javascript:changeFlag();" value="<c:out value="${lastName}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastName" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>


	<tr valign="top">
		<td class="formlabel"><fmt:message key="email" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" id="email" name="email" onchange="javascript:changeFlag();" value="<c:out value="${email}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>(<fmt:message key="username@institution" bundle="${resword}"/>) *</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="email" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>


	<tr valign="top">
		<td class="formlabel"><fmt:message key="institutional_affiliation" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<input type="text" id="institutionalAffiliation" name="institutionalAffiliation" onchange="javascript:changeFlag();" value="<c:out value="${institutionalAffiliation}"/>" size="20" class="formfieldM" />
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="institutionalAffiliation" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="active_study" bundle="${resword}"/>:</td>
<!-- EDIT !! -->
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
                        <div class="formfieldXL_BG">
                        <select name="activeStudy" id="activeStudy" class="formfieldXL" onchange="sendUrl();">
							<option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>

                            <c:forEach var="study" items="${studies}">
								<c:choose>
									<c:when test="${activeStudy == study.id}">
										<c:choose>
										<c:when test="${study.parentStudyId>0}">
											<option value='<c:out value="${study.id}" />' selected>&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${study.name}" /></option>
										</c:when>
										<c:otherwise>
											<option value='<c:out value="${study.id}" />' selected><c:out value="${study.name}" /></option>
										</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
										<c:when test="${study.parentStudyId>0}">
											<option value='<c:out value="${study.id}" />'>&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${study.name}" /></option>
										</c:when>
										<c:otherwise>
											<option value='<c:out value="${study.id}" />'><c:out value="${study.name}" /></option>
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div>
                    </td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="activeStudy" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="role" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="role" id="role" onchange="javascript:changeFlag();" class="formfieldM">
							<option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>
							<c:forEach var="currRole" items="${roles}">
                                <c:if test="${currRole.key == inclRoleCode1 || currRole.key == inclRoleCode2}">
                                    <option value='<c:out value="${currRole.key}" />' <c:if test="${roleId == currRole.key}">selected</c:if>><c:out value="${currRole.value}" /></option>
                                </c:if>
							</c:forEach>
						</select>
					</div></td>
					<td>*</td>
				</tr>
				<tr>
					<td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="role" /></jsp:include></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
	  	<td class="formlabel"><fmt:message key="user_type" bundle="${resword}"/>:</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"><div class="formfieldM_BG">
						<select name="type" id="type" onchange="javascript:changeFlag();" class="formfieldM">
						<c:forEach var="currType" items="${types}">
								<c:choose>
									<c:when test="${userTypeId == currType.id}">
										<option value='<c:out value="${currType.id}" />' selected><c:out value="${currType.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${currType.id}" />'><c:out value="${currType.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div></td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr valign="top">
        <td class="formlabel"><fmt:message key="can_run_web_services" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top">
                        <br><input type="checkbox" name="runWebServices" onchange="javascript:changeFlag();" id="runWebServices" value="1"
                            <c:if test="${runWebServices != 0}">checked</c:if>
                        >
                    </td>
                    <td> </td>
                </tr>
                <tr>
                    <td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="runWebServices" /></jsp:include></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr valign="top">
	  <td class="formlabel"><fmt:message key="user_password_generated" bundle="${resword}"/>:</td>
	  	<td>
	  	<c:choose>
         <c:when test="${notifyPassword eq 'email'}">
            <input type="radio" id="displayPwd0" checked name="displayPwd" onchange="javascript:changeFlag();" value="no"><fmt:message key="send_user_password_via_email" bundle="${resword}"/>
            <br><input type="radio" id="displayPwd1" name="displayPwd" onchange="javascript:changeFlag();" value="yes"><fmt:message key="show_user_password_to_admin" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
         
            <br><input type="radio" checked id="displayPwd1" checked name="displayPwd" onchange="javascript:changeFlag();" value="yes"><fmt:message key="show_user_password_to_admin" bundle="${resword}"/>
         </c:otherwise>
       </c:choose>
      </td>
	</tr>
</table>
	</div>

	</div></div></div></div></div></div></div></div>

	</div>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
  <input type="button" name="BTN_Back" id="GoToPreviousPage" title="<fmt:message key="go_back_to_subject_matrix" bundle="${resword}"/>" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: return checkGoToEntryStatusMod1('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>','ListUserAccounts');"/>
  <input type="hidden" id="pageIsChanged" name="pageIsChanged" value="${pageIsChanged}">
</td>
<td>
  <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium" />
</td>
</tr>
</table>
</form>
<jsp:include page="../include/footer.jsp"/>
