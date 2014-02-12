<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:useBean scope='request' id='whichResStatus' class='java.lang.String' />
<jsp:useBean scope='session' id='boxDNMap'  class="java.util.HashMap"/>
<jsp:useBean scope='session' id='boxToShow'  class="java.lang.String"/>

<c:set var="contextPath" value="${fn:replace(pageContext.request.requestURL, fn:substringAfter(pageContext.request.requestURL, pageContext.request.contextPath), '')}" />

<script language="JavaScript">
	$(document).ready(function() {
		$("select[id*=typeId]").change();
		var aForm = $('[name=oneDNForm_0]');
		aForm.submit(function (e) {
			e.preventDefault(); 
			sendFormDataViaAjax(); 
		});
	})
	
	function changeDNFlagIconInParentWindow(){
		setImageInParentWin('flag_${updatedDiscrepancyNote.field}', '${contextPath}/${updatedDiscrepancyNote.resStatus.iconFilePath}', '${updatedDiscrepancyNote.resStatus.id}');
	}
</script>
<input type="hidden" name="responseMessage" value="${responseMessage}"/>
<c:import url="./discrepancyNote.jsp">
	<c:param name="parentId" value="0"/>
	<c:param name="entityId" value="${id}"/>				
	<c:param name="entityType" value="${name}"/>				
	<c:param name="field" value="${field}"/>				
	<c:param name="column" value="${column}"/>
	<c:param name="boxId" value="box${0}New"/>
	<c:param name="isRFC" value="${isRFC}"/>
	<c:param name="isInError" value="${isInError}"/>
	<c:param name="strErrMsg" value="${strErrMsg}"/>
	<c:param name="showStatus" value="${false}"/>
</c:import> 

<div style="clear:both;"></div>
<jsp:include page="../include/changeTheme.jsp"/>