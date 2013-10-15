<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="include/managestudy_top_pages.jsp"/>
<!-- should be extract study? -->
<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<jsp:include page="include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

        <div class="sidebar_tab_content">

            <fmt:message key="extract_instructions" bundle="${restext}"/>

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${restext}"/></b>

    </td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>

<div id="startBox" class="box_T">
<div class="box_L">
<div class="box_R">
<div class="box_B">
<div class="box_TL">
<div class="box_TR">
<div class="box_BL">
<div class="box_BR">
<div class="textbox_center">
<%-- TODO I18N --%>
<P><fmt:message key="extract_is_running" bundle="${restext}"/></P>
<%--Clinovo Ticket #113 Task 27b --%>
<table>
	<tr>
		<td>
			<input type="button" onclick="javascript:window.location.href='../ViewDatasets'"  name="exit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium"/>
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

<jsp:include page="include/footer.jsp"/>