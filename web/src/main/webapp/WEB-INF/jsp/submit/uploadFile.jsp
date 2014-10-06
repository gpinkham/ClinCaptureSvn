<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);
%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<jsp:useBean scope='session' id='pageMessage' class='java.lang.String'/>
<jsp:useBean scope='session' id='mayProcessUploading' class='java.lang.String'/>
<jsp:useBean scope='request' id='fileItemId' class='java.lang.String'/>
<jsp:useBean scope='request' id='fileName' class='java.lang.String'/>
<jsp:useBean scope='request' id='inputName' class='java.lang.String'/>
<jsp:useBean scope='request' id='attachedFilePath' class='java.lang.String'/>
<jsp:useBean scope='request' id='uploadFileStatus' class='java.lang.String'/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<html>
 <head>
 	<c:set var="contextPath" value="${pageContext.request.contextPath}" />

	<title>
		<fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="file_upload" bundle="${resword}"/>
	</title>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
	<link rel="stylesheet" href="<c:out value="${contextPath}" />/includes/styles.css" type="text/css">
	<link rel="stylesheet" href="includes/jquery-ui.css"  type="text/css"/>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>
 	<script>
		function processUpload(itemId,fileName,isGroupItem) {
			var a = fileName;
			var b = 'ft' + itemId;
			var c = 'input' + itemId;
			if(isGroupItem == 'true' && itemId.indexOf("input") > 0 ) {
				c = itemId;
			}
			var bb = window.opener.document.getElementById('ft'+itemId);
			bb.setAttribute("value", a);
			window.opener.document.crfForm.elements[b].value = a;
			window.opener.document.crfForm.elements[c].value = a;
		}
	
		function cleanFile() {
			var f = document.getElementById("file");
			f.value = "";
			window.close();
		}
	</script>
     <ui:theme/>
 </head>

<body>
<!-- *JSP* submit/uploadFile.jsp --><div style="float: left; padding-left: 15px;">
	<h1>
		<span class="first_level_header">
			<fmt:message key="file_upload" bundle="${resword}"/>		</span>
	</h1>
</div>
<div style="float: right; padding-right: 20px;">	<a href="#" onclick="javascript:window.close();">
		<img name="close_box" alt="<fmt:message key="Close_Box" bundle="${resword}"/>" src="images/bt_Remove.gif" class="icon_dnBox">
	</a>
</div><div style="clear:both;"></div> 
<div class="alert" style="padding-left: 15px;">
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/></c:forEach>
</div> 
<div id="divWithData" style="padding-left: 15px; padding-right: 15px;">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">	<div class="uploadFileBoxText">
		<form name="uploadForm" action="UploadFile" method="post" enctype="multipart/form-data">
			<input type="hidden" name="itemId" value="${fileItemId}">
			<input type="hidden" name="inputName" value="${inputName}">			<c:choose>
				<c:when test="${mayProcessUploading=='true'}">
					<c:choose>
						<c:when test="${uploadFileStatus=='successed'}">
							<c:choose>
							<c:when test="${inputName == null || inputName == ''}">
								<script type="text/javascript">
									processUpload('<c:out value="${fileItemId}"/>','<c:out value="${fileName}"/>','false');
								</script>
							</c:when>
							<c:otherwise>
								<script type="text/javascript">
									processUpload('<c:out value="${inputName}"/>','<c:out value="${fileName}"/>','true');
								</script>
							</c:otherwise>
							</c:choose>
							<p>
								<fmt:message key="select_close_window_button" bundle="${restext}"/>
							</p>
							<p>
								<input type="button" name="close" value="<fmt:message key="close_window" bundle="${resword}"/>" onClick="javascript:window.close();" class="button_long">
							</p>
						</c:when>
						<c:otherwise>
							<p>
								<fmt:message key="upload_note" bundle="${restext}"/>
							</p>
							<p>
								<fmt:message key="select_cancel_upload_button" bundle="${restext}"/>
							</p>
							<input id="file" type="file" name="browse" size="60">
							<p>
								<input type="submit" name="upload" value="<fmt:message key="upload_file" bundle="${resword}"/>" class="button_long" onclick="return checkFileUpload('file', '<fmt:message key="select_a_file_to_upload" bundle="${restext}"/>');" >
								<input type="button" name="cancel" value="<fmt:message key="cancel_upload" bundle="${resword}"/>" onClick="cleanFile()" class="button_long">
							</p>
							<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<p>
						<fmt:message key="uploading_not_process_because_permission" bundle="${restext}"/>
					</p>
				</c:otherwise>
			</c:choose>
		</form>
	</div>
</div></div></div></div></div></div></div>
</div>
<jsp:include page="../include/changeTheme.jsp"/>
</body>
</html>