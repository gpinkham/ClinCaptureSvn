<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="contextPath" value="${fn:replace(pageContext.request.requestURL, fn:substringAfter(pageContext.request.requestURL, pageContext.request.contextPath), '')}" />

<script language="JavaScript">
	function changeDNFlagIconInParentWindow(){
		setImageInParentWin('flag_${updatedDiscrepancyNote.field}', '${contextPath}/${updatedDiscrepancyNote.resStatus.iconFilePath}', '${updatedDiscrepancyNote.resStatus.id}');
	}
</script>

<input type="hidden" name="responseMessage" value="${responseMessage}"/>
<c:import url="./discrepancyNote.jsp">
	<c:param name="parentId" value="${updatedDiscrepancyNote.parentDnId}"/>
	<c:param name="entityId" value="${id}"/>				
	<c:param name="entityType" value="${name}"/>				
	<c:param name="field" value="${field}"/>				
	<c:param name="column" value="${column}"/>
	<c:param name="boxId" value="box${updatedDiscrepancyNote.parentDnId}"/>
	<c:param name="typeId" value="${updatedDiscrepancyNote.discrepancyNoteTypeId}"/>
	<c:param name="isRFC" value="${isRFC}"/>
	<c:param name="isInError" value="${isInError}"/>
	<c:param name="strErrMsg" value="${strErrMsg}"/>
	<c:param name="showStatus" value="${false}"/>
</c:import> 

<jsp:include page="../include/changeTheme.jsp"/>