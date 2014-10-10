<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>

<%
String key = request.getParameter("key");
String message="";
if (formMessages.get(key)!=null) {
	ArrayList messages = (ArrayList) formMessages.get(key);
	if (messages !=null) {
	  for (int messagecount = 0; messagecount < messages.size(); messagecount++) {
		message = (String) messages.get(messagecount);
	  }
    }
}
%>
<span ID="spanAlert-<%=key%>" class="alert"><%=message%></span>
<%
if (! message.equals("")) { %>
<br/>
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<br/>
<c:if test="${crfAutoUploadMode}">
<!-- <import-error>
    <%=message%><br/>
</import-error> -->
</c:if>
 <%
 }
%>
