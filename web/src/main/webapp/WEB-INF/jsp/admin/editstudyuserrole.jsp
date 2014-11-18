<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
        </a>
        <b><fmt:message key="instructions" bundle="${resword}"/></b>
        <div class="sidebar_tab_content"></div>
    </td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
            <img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
        <b><fmt:message key="instructions" bundle="${resword}"/></b>
    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='studyUserRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean'/>
<jsp:useBean scope='request' id='userName' class='java.lang.String'/>
<jsp:useBean scope='request' id='chosenRoleId' type='java.lang.Integer' />
<jsp:useBean scope="request" id="roles" class="java.util.LinkedHashMap"/>

<c:forEach var="currRole" items="${roles}" varStatus="status">
    <c:set var="rolesCount" value="${status.count}"/>
</c:forEach>
<c:choose>
    <c:when test="${isThisStudy}">
        <c:set var="inclRoleCode1" value="2"/>
        <c:set var="inclRoleCode2" value="6"/>
        <c:set var="inclRoleCode3" value="7"/>
        <c:set var="inclRoleCode_evaluator" value="8"/>
    </c:when>
    <c:otherwise>
        <c:set var="inclRoleCode1" value="4"/>
        <c:set var="inclRoleCode2" value="5"/>
        <c:set var="inclRoleCode3" value="9"/>
    </c:otherwise>
</c:choose>

<h1>
	<span class="first_level_header">
		<fmt:message key="modify_role_for" bundle="${restext}">
            <fmt:param value="${userName}"/>
            <fmt:param value="${studyUserRole.studyName}"/>
        </fmt:message>
	</span>
</h1>

<form action="EditStudyUserRole" method="post">
<jsp:include page="../include/showSubmitted.jsp" />
<input type="hidden" name="studyId" value="<c:out value="${studyUserRole.studyId}" />" />
<input type="hidden" name="userName" value="<c:out value="${userName}" />" />

    <div style="width: 400px">
        <div class="box_T">
            <div class="box_L">
                <div class="box_R">
                    <div class="box_B">
                        <div class="box_TL">
                            <div class="box_TR">
                                <div class="box_BL">
                                    <div class="box_BR">
                                        <div class="tablebox_center">
                                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                                <tr>
                                                    <td class="table_header_column_top"><fmt:message key="username2" bundle="${resword}"/>:
                                                    </td>
                                                    <td class="table_cell_top"><b><c:out value="${userName}"/></b></td>
                                                </tr>
                                                <tr>
                                                    <td class="table_header_column"><fmt:message key="study" bundle="${resword}"/>:
                                                    </td>
                                                    <td class="table_cell">
                                                        <b><c:out value="${studyUserRole.studyName}"/></b>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="table_header_column"><fmt:message key="role" bundle="${resword}"/>:
                                                    </td>
                                                    <td valign="top">
                                                        <table border="0" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td valign="top">
                                                                    <div class="formfieldM_BG">
                                                                        <select name="role" class="formfieldM" onchange="javascript:changeIcon();">
                                                                            <option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>
                                                                            <c:forEach var="role" items="${roles}">
                                                                                <c:if test="${role.key == inclRoleCode1 || role.key == inclRoleCode2 || role.key == inclRoleCode3 || role.key == inclRoleCode_evaluator}">
                                                                                    <option value="<c:out value="${role.key}"/>" <c:if test="${chosenRoleId == role.key}">selected</c:if> >
                                                                                        <c:out value="${role.value}"/>
                                                                                    </option>
                                                                                </c:if>
                                                                            </c:forEach>
                                                                        </select>
                                                                    </div>
                                                                </td>
                                                                <td>&nbsp;</td>
                                                            </tr>
                                                            <tr>
                                                                <td colspan="2">
                                                                    <jsp:include page="../showMessage.jsp">
                                                                        <jsp:param name="key" value="role"/>
                                                                    </jsp:include>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>"
           class="button_medium"
           onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
    <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
    <img src="images/icon_UnchangedData.gif" style="visibility:hidden" alt="Data Status" name="DataStatus_bottom">
</form>
<c:import url="../include/workflow.jsp">
    <c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
