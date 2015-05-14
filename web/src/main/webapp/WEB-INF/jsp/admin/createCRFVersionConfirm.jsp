<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>

<c:choose>
<c:when test="${userBean.sysAdmin}">
 <c:import url="../include/admin-head-prev.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-head-prev.jsp"/>
</c:otherwise>
</c:choose>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href=
      "javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>

  <tr id="sidebar_Instructions_closed">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>

<jsp:include page="../include/sideInfo_prev.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='excelErrors' class='java.util.ArrayList'/>
<jsp:useBean scope='session' id='htmlTable' class='java.lang.String'/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

<c:choose>
<c:when test="${empty excelErrors}">
 <h1>
 	<span class="first_level_header">
 		<fmt:message key="preview_CRF" bundle="${resword}"/>
 	</span>
 </h1>
 <c:if test="${!empty warnings}">
  <p><fmt:message key="warnings" bundle="${resword}"/>:<p>
  <c:forEach var="warning" items="${warnings}">
    <p><span class="alert"><c:out value="${warning}"/></span></p>
  </c:forEach>
 </c:if>

<p>
<table border="0">
<tr>

<td>
<input type="button" name="BTN_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
                    <c:choose>
                        <c:when test="${param.crfId == 0}">
                            onClick="javascript: window.location.href = 'CreateCRFVersion';"/>
                        </c:when>
                    <c:otherwise>
                            onClick="javascript: window.location.href = 'InitCreateCRFVersion?crfId=${version.crfId}&name=${crfName}';" />
                    </c:otherwise>
                    </c:choose>
</td>  
  <td><b><form action="CreateCRFVersion?action=confirmsql&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post">
 <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit"></form></b> </td>
   </tr>
  </table>
<br/>

<jsp:include page="../managestudy/viewSectionDataPreview.jsp"/><br><br>

 <table border="0">
<tr>
<td>
 <input type="button" name="BTN_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
                    <c:choose>
                        <c:when test="${param.crfId == 0}">
                            onClick="javascript: window.location.href = 'CreateCRFVersion';"/>
                        </c:when>
                        <c:otherwise>
                            onClick="javascript: window.location.href = 'InitCreateCRFVersion?crfId=${version.crfId}&name=${crfName}';" />
                        </c:otherwise>
                    </c:choose>
</td>  
<td><b><form action="CreateCRFVersion?action=confirmsql&crfId=<c:out value=
"${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post">

 <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit"></form></b> </td>
  </tr>
  </table>

</c:when>
<c:otherwise>
<h1>
	<span class="first_level_header">
		<fmt:message key="preview_CRF_failed_invalid_data" bundle="${resword}"/>
	</span>
</h1>
<br>
<fmt:message key="there_were_several_invalid_fields" bundle="${restext}"/> 
<br>
<c:if test="${!empty excelErrors}">
<!-- <import-error>
<c:forEach var="error" items="${excelErrors}">
    ${error}<br/>
</c:forEach>
</import-error> -->
</c:if>
<c:forEach var="error" items="${excelErrors}">
<span class="alert"><c:out value="${error}"/><br></span>
</c:forEach>
<br>
<%-- <fmt:message key="click" bundle="${resword}"/> --%>
<fmt:message key="go_back_to_upload_your_corrected_spreadsheet" bundle="${restext}"/>
</br>
<input type="button" name="BTN_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
                    <c:choose>
                        <c:when test="${param.crfId == 0}">
                            onClick="javascript: window.location.href = 'CreateCRFVersion';" />
                        </c:when>
                        <c:otherwise>
                            onClick="javascript: window.location.href = 'InitCreateCRFVersion?crfId=${version.crfId}&name=${crfName}';" />
                        </c:otherwise>
                    </c:choose>

<br></br>
<%=htmlTable%>

</c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${userBean.sysAdmin}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/>
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/>
  </c:import>
  </c:otherwise>
 </c:choose>

<jsp:include page="../include/footer.jsp"/>
