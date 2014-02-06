<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.exceptions" var="exceptions"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy_top_pages.jsp"/>

<jsp:include page="../include/sideAlert.jsp"/>

<c:set var="color" scope="session" value="${newThemeColor}"/>
<c:if test="${(color == 'violet') || (color == 'green')}">
    <script>
        document.write('<style class="hideStuff" ' + 'type="text/css">body {display:none;}<\/style>');
    </script>
</c:if>

<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right"
                hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="study_module_instruction" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
                src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<div>
    <span class="first_level_header"><fmt:message key="error.controllerErrorMsg" bundle="${exceptions}"/></span>
</div>

<jsp:include page="../include/footer.jsp"/>