<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/admin-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<script type="text/javascript">
    function changeFlag(){
        objFlag = MM_findObj('pageIsChanged');
        objFlag.value='true';
    }
    function sendUrl() {
        document.getElementById('changeRoles').value = 'true';
        document.forms[1].submit();
    }
</script>
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

<c:forEach var="currRole" items="${roles}" varStatus="status">
    <c:set var="rolesCount" value="${status.count}" />
</c:forEach>
<c:choose>
    <c:when test="${isStudyLevelUser}">
        <c:set var="inclRoleCode1" value="2" />
        <c:set var="inclRoleCode2" value="6" />
        <c:set var="inclRoleCode3" value="7" />
    </c:when>
    <c:otherwise>
        <c:set var="inclRoleCode1" value="4" />
        <c:set var="inclRoleCode2" value="5" />
    </c:otherwise>
</c:choose>

<h1>
	<span class="first_level_header">
		<fmt:message key="set_user_role" bundle="${resword}"/>
	</span>
</h1>

<p><fmt:message key="choose_a_study_from_the_following_study" bundle="${resword}"/></p>
<form action="SetUserRole" method="post">
    <input type="hidden" name="action" value="submit">
    <input type="hidden" name="userId" value="<c:out value="${user.id}"/>">
    <input type="hidden" name="name" value="<c:out value="${user.name}"/>">
    <input type="hidden" id="changeRoles" name="changeRoles">

    <div style="width: 600px">
        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

            <div class="tablebox_center">
                <table border="0" cellpadding="0" cellspacing="0" >
                    <c:choose>
                        <c:when test="${fn:length(studies) eq 0}">
                            <tr><td width="40px"></td><td style="padding-top: 7px;"><fmt:message key="first_name" bundle="${resword}"/>: <c:out value="${user.firstName}"/></td></tr>
                            <tr><td></td><td style="padding-top: 4px;"><fmt:message key="last_name" bundle="${resword}"/>: <c:out value="${user.lastName}"/></td></tr>
                            <tr><td></td><td class="formlabel" style="text-align:right;"><p><b><fmt:message key="all_available_roles" bundle="${resword}"/></b></p></td></tr>
                        </c:when>
                        <c:otherwise>
                            <tr><td width="40px"></td><td class="formlabel"  style="padding-top: 7px;"><fmt:message key="first_name" bundle="${resword}"/>:</td><td  style="padding-top: 7px;"><c:out value="${user.firstName}"/></td></tr>
                            <tr><td></td><td class="formlabel"><fmt:message key="last_name" bundle="${resword}"/>:</td><td><c:out value="${user.lastName}"/></td></tr>
                            <tr><td></td><td class="formlabel"><fmt:message key="study_name" bundle="${resword}"/>:</td>
                                <td onchange="javascript:changeIcon();"><div class="formfieldXL_BG">
                                    <c:choose>
                                        <c:when test="${withoutRoles}">
                                            <select name="studyId" class="formfieldXL" onchange="sendUrl();">
                                                <option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>
                                                <c:forEach var="study" items="${studies}">
                                                    <c:set var="selectedFlag" value="${selectedStudy.id == study.id ? 'selected' : ''}"/>
                                                    <c:choose>
                                                        <c:when test="${study.parentStudyId > 0}">
                                                            <option value='<c:out value="${study.id}" />' ${selectedFlag}>&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${study.name}" /></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <option value='<c:out value="${study.id}" />' ${selectedFlag}><c:out value="${study.name}" /></option>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </select>
                                        </c:when>
                                        <c:otherwise>
                                            <select name="studyId" class="formfieldXL" onchange="changeFlag();">
                                                <c:forEach var="userStudy" items="${studies}" varStatus="status">
                                                    <c:choose>
                                                        <c:when test="${isStudyLevelUser}">
                                                            <option value="<c:out value="${userStudy.id}"/>"><c:out value="${userStudy.name}"/></option>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${userStudy.parentStudyId == 0}">
                                                                    <optgroup label="<c:out value="${userStudy.name}"/>" >
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <option value="<c:out value="${userStudy.id}"/>">&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${userStudy.name}"/></option>
                                                                    <c:if test="${status.last || studies[status.index + 1].parentStudyId == 0}">
                                                                        </optgroup>
                                                                    </c:if>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </select>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                </td>
                            </tr>
                            <tr><td></td><td class="formlabel"><fmt:message key="study_user_role" bundle="${resword}"/>:</td>
                                <td onchange="javascript:changeIcon();">
                                	<div class="formfieldXL_BG">
                                    	<c:set var="role1" value="${uRole.role}"/>
                                        <select name="roleId" class="formfieldXL" onchange="javascript:'changeFlag();'">
                                            <c:if test="${not withoutRoles || selectedStudy ne null}">
                                                <c:forEach var="currRole" items="${roles}">
                                                    <c:if test="${currRole.key == inclRoleCode1 || currRole.key == inclRoleCode2 || currRole.key == inclRoleCode3}">
                                                        <option value='<c:out value="${currRole.key}" />' <c:if test="${role1.id == currRole.key}">selected</c:if>><c:out value="${currRole.value}" /></option>
                                                    </c:if>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                	</div>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>

                </table>
            </div>
        </div></div></div></div></div></div></div></div>

    </div>
    <input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage"
           value="<fmt:message key="back" bundle="${resword}"/>"
           class="button_medium"
           onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
    <c:if test="${fn:length(studies) gt 0}">
        <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
    </c:if>
    <input type="hidden" id="pageIsChanged" name="pageIsChanged" value="${pageIsChanged}">
    <img src="images/icon_UnchangedData.gif" style="visibility:hidden" alt="Data Status" name="DataStatus_bottom">
</form>

<c:import url="../include/workflow.jsp">
    <c:param name="module" value="admin"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
