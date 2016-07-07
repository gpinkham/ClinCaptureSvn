<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="/includes/js/pages/emailLog.js.jsp" />
<jsp:include page="../include/managestudy_top_pages.jsp" />
<jsp:include page="../include/sideAlert.jsp" />

<link rel="stylesheet" href="<c:url value='/includes/jmesa/jmesa.css?r=${revisionNumber}'/>" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jmesa.js?r=${revisionNumber}'/>"></script>
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery.jmesa.js?r=${revisionNumber}'/>"></script>

<script type="text/javascript">
	$(window).load(function(){
		highlightLastAccessedObject();
	});

	function onInvokeAction(id) {
		createHiddenInputFieldsForLimitAndSubmit(id);
	}
</script>

<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}" /></b>
		<div class="sidebar_tab_content">
			<fmt:message key="email_log_for" bundle="${resword}" /> ${study.name}
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}" /></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<!-- Page content start -->

<h1>
	<span class="first_level_header">
		<fmt:message key="email_log" bundle="${resword}"/>
	</span>
</h1>

<form action="${pageContext.request.contextPath}/pages/EmailLog">
	${dataTable}
</form>

<input id="accessAttributeName" type="hidden" value="data-cc-emailLog">

<br>

<div style="clear:left; float:left">
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
		   value="<fmt:message key="back" bundle="${resword}"/>"
		   class="button_medium medium_back"
		   onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
</div>

<jsp:include page="../include/footer.jsp" />
