<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<html>   
<head>
<c:set var="contextPath" value="${fn:replace(pageContext.request.requestURL, fn:substringAfter(pageContext.request.requestURL, pageContext.request.contextPath), '')}" />
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
<link rel="stylesheet" href="<c:out value="${contextPath}" />/includes/styles.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>

<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<link rel="stylesheet" href="includes/jquery-ui.css"  type="text/css"/>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>

<script language="JavaScript" src="includes/CalendarPopup.js"></script>

<style type="text/css">  
.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}
</style>

<script language="JavaScript">

// typeId -  discrepancyNoteTypeId
// filter1 - whichResStatus

function setStatus(typeId,filter1,nw,ud,rs,cl,na) {
	objtr1=document.getElementById('res1');
	objtr2=document.getElementById('resStatusId');

	if (typeId == 2|| typeId ==4) {//annotation or reason for change
  		objtr2.disabled = true;
  		objtr2.options.length = 0;
  		objtr2.options[0]=new Option(na, '5');
	} else {
		objtr2.disabled = false;
		objtr2.options.length = 0;
		objtr2.options[0]=new Option(nw, '1');
		if(filter1=="22" || (filter1=="2" && typeId==1)) {
//	  		objtr2.options[1]=new Option(rs, '3');   //ClinCapture #42 DN worlflow simplification
		} else if(filter1=="1") {
			objtr2.options[1]=new Option(ud,'2');
			objtr2.options[2]=new Option(cl,'4');
		} else {
			objtr2.options[1]=new Option(ud,'2');
//			objtr2.options[2]=new Option(rs,'3');  //ClinCapture #42 DN worlflow simplification
			objtr2.options[2]=new Option(cl,'4');
		}
	}
}
		
function setElements(typeId,user1,user2,filter1,nw,ud,rs,cl,na,isRFC) {
	setStatus(typeId,filter1,nw,ud,rs,cl,na);
	if(typeId == 3) {//query
		showElement(user1);
		showElement(user2);	
		showElement('input');
		switchOnElement('inputDescription');
		hideElement('select');
		switchOffElement('selectDescription');
		
	} else {
		hideElement(user1);
		hideElement(user2);
		if (isRFC) {
			hideElement('input'); 
			switchOffElement('inputDescription');
			showElement('select');
			switchOnElement('selectDescription');
		} else {
			showElement('input');
			switchOnElement('inputDescription');
		}
	}
}

function changeDNFlagIconInParentWindow(){
    setImageInParentWin('flag_${updatedDiscrepancyNote.field}', '${contextPath}/${updatedDiscrepancyNote.resStatus.iconFilePath}', '${updatedDiscrepancyNote.resStatus.id}');
}

$(document).ready(function() {
		$( "select[id*=typeId]" ).change();
});

</script> 
</head>
<body style="margin: 0px 12px 0px 12px;" onload="javascript:setStatus('<c:out value="${discrepancyNote.discrepancyNoteTypeId}"/>','<c:out value="${whichResStatus}"/>','<fmt:message key="New" bundle="${resterm}"/>','<fmt:message key="Updated" bundle="${resterm}"/>','<fmt:message key="Resolution_Proposed" bundle="${resterm}"/>','<fmt:message key="Closed" bundle="${resterm}"/>','<fmt:message key="Not_Applicable" bundle="${resterm}"/>');">

<!-- dn box -->
<input type="hidden" name="responseMessage" value="${responseMessage}"/>
	
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">

	<div class="dnBoxCol1 dnBoxText"><fmt:message key="description" bundle="${resword}"/>:<span class="alert">*</span></div>
	<div class="dnBoxCol2 dnBoxText">
		<c:if test="${isRFC}">
			<div class="formfieldL_BG" id="select" style="display:none" >
				<select name="description" id="selectDescription" class="formFieldL" disabled>
					<c:forEach var="rfcTerm" items="${dDescriptionsMap['dnRFCDescriptions']}">
						<option value="${rfcTerm.name}"><c:out value="${rfcTerm.name}"/>
					</c:forEach>
					<option value="Other"><fmt:message key="other" bundle="${resword}"/>
				</select>
			</div>
		</c:if>
			<div id="input" style="display:none"> 
				<div class="formfieldXL_BG" >
					<input type="text" name="description" id="inputDescription" disabled value="<c:out value="${discrepancyNote.description}"/>" class="formfieldXL">
				</div>
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description${parentId}"/></jsp:include>
			</div>
	</div>
	
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="detailed_note" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText">
		<div class="formtextareaXL4_BG">
	  		<textarea name="detailedDes" rows="4" cols="50" class="formtextareaXL4 textarea_fixed_size"><c:out value="${discrepancyNote.detailedNotes}"/></textarea>
		</div>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="detailedDes${parentId}"/></jsp:include>
	</div>

<c:if test="${not showStatus}"> <div style="display:none;">  </c:if>
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="type" bundle="${resword}"/>:<span class="alert">*</span></div>
	<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG">
		<c:choose>
		<c:when test="${parentId > 0}">
			<input type="hidden" name="typeId" value="${param.typeId}"/>
			<select name="pTypeId" id="pTypeId" class="formfieldL" disabled>
				<option value="<c:out value="${param.typeId}"/>" selected><c:out value="${param.typeName}"/>
			</select>
		</c:when>
		<c:otherwise>
			<c:set var="typeId1" value="${discrepancyNote.discrepancyNoteTypeId}"/>
			<select name="typeId" id="typeId" class="formfieldL"
                    onchange ="javascript:setElements(this.options[selectedIndex].value, 'user1', 'user2',
                            '<c:out value="${whichResStatus}"/>',
                            '<fmt:message key="New" bundle="${resterm}"/>',
                            '<fmt:message key="Updated" bundle="${resterm}"/>',
                            '<fmt:message key="Resolution_Proposed" bundle="${resterm}"/>',
                            '<fmt:message key="Closed" bundle="${resterm}"/>',
                            '<fmt:message key="Not_Applicable" bundle="${resterm}"/>', ${isRFC});">

                <c:forEach var="type" items="${discrepancyTypes}">
				<c:choose>
				<c:when test="${typeId1 == type.id}">
				 	<c:choose>
				    <c:when test="${study.status.frozen && (type.id==2 or type.id==4)}">
						<option value="<c:out value="${type.id}"/>" disabled="true" selected /><c:out value="${type.name}"/>
				    </c:when>
				    <c:otherwise>
				   		<option value="<c:out value="${type.id}"/>" selected ><c:out value="${type.name}"/>
				    </c:otherwise>
				    </c:choose>
				 </c:when>
				 <c:otherwise>
					<c:choose>
					<c:when test="${study.status.frozen && (type.id==2 || type.id==4)}">
						<option value="<c:out value="${type.id}"/>" disabled="true"><c:out value="${type.name}"/>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${type.id}"/>"><c:out value="${type.name}"/>
					</c:otherwise>
					</c:choose>
				 </c:otherwise>
				</c:choose>
				</c:forEach>
			</select>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="typeId"/></jsp:include>
		</c:otherwise>
		</c:choose>
	</div></div>
		
	<span id="res1${parentId}" style="display: none;">
		<div class="dnBoxCol1 dnBoxText"><fmt:message key="Set_to_Status" bundle="${resword}"/>:<span class="alert">*</span></div>
		<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG">
			<c:set var="resStatusIdl" value="${discrepancyNote.resolutionStatusId}"/>
		    <select name="resStatusId" id="resStatusId" class="formfieldL">
				<c:set var="resStatuses" value="${resolutionStatuses}"/>
				<c:forEach var="status" items="${resStatuses}">
					<c:choose>
					<c:when test="${resStatusIdl == status.id}">
					   <option value="<c:out value="${status.id}"/>" selected ><c:out value="${status.name}"/>
					</c:when>
					<c:otherwise>
					   <option value="<c:out value="${status.id}"/>" ><c:out value="${status.name}"/>
					</c:otherwise>
					</c:choose>
				</c:forEach>
			</select></div>
		    <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="resStatusId"/></jsp:include>
		</div>
	</span>

	<c:choose>
	<c:when test="${(parent == null || parent.id ==0 || unlock == 1) && autoView == 0}">
    	<span id="user1" style="display:none">
	</c:when>
	<c:otherwise>
		<span id="user1" style="display:block">
  	</c:otherwise>
	</c:choose>
		<div class="dnBoxCol1 dnBoxText"><fmt:message key="assign_to_user" bundle="${resword}"/>:</div>
		<div class="dnBoxCol2 dnBoxText"><div class="formfieldL_BG">
			<c:choose>
			<c:when test='${not empty discrepancyNote.assignedUserId and not (discrepancyNote.assignedUserId eq 0)}'>
				<c:set var="userAccountId1" value="${discrepancyNote.assignedUserId}"/>
			</c:when>
            <c:when test='${not empty eventCrfOwnerId}'>
                <c:set var="userAccountId1" value="${eventCrfOwnerId}"/>
            </c:when>
			<c:otherwise>
				<c:set var="userAccountId1" value="0"/>
			</c:otherwise>
			</c:choose>
			<select name="userAccountId" id="userAccountId" class="formfieldL" >
				<option value="0">
		  		<c:forEach var="user" items="${userAccounts}">
		   		<c:choose>
		     	<c:when test="${userAccountId1 == user.userAccountId}">
		       		<option value="<c:out value="${user.userAccountId}"/>" selected><c:out value="${user.lastName}"/>, <c:out value="${user.firstName}"/> (<c:out value="${user.userName}"/>)
		     	</c:when>
		     	<c:otherwise>
		       		<option value="<c:out value="${user.userAccountId}"/>"><c:out value="${user.lastName}"/>, <c:out value="${user.firstName}"/> (<c:out value="${user.userName}"/>)
		     	</c:otherwise>
		   		</c:choose>
		 		</c:forEach>
			</select>
			</div>
		  	<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="userAccountId"/></jsp:include>
		</div>
	</span>
		
	<c:choose>
	<c:when test="${(parent == null || parent.id ==0 || unlock == 1) && autoView == 0}">
		<span id="user2" style="display:none">
	</c:when>
	<c:otherwise>
		<span id="user2" style="display:block">
	</c:otherwise>
	</c:choose>
	<div class="dnBoxCol1 dnBoxText"><fmt:message key="email_assigned_user" bundle="${resword}"/>:</div>
	<div class="dnBoxCol2 dnBoxText"><input name="sendEmail" value="1" type="checkbox"/></div>
	</span>
<c:if test="${not showStatus}"> </div>  </c:if>

    <c:set var= "noteEntityType" value="${discrepancyNote.entityType}"/>
	<c:if test="${enterData == '1' || canMonitor == '1' || noteEntityType != 'itemData' }">
        <c:choose>
            <c:when test="${writeToDB eq '1'}">
                <div class="dnBoxCol2"><input type="button" name="SubmitExit" onclick="javascript: $('#typeId option').removeAttr('disabled');sendFormDataViaAjax();" value="<fmt:message key="submit_close" bundle="${resword}"/>" class="button_medium" onclick="javascript:setValue('close<c:out value="${parentId}"/>','true');"></div>
            </c:when>
            <c:otherwise>
                <div class="dnBoxCol2"><input type="button" name="Submit" onclick="javascript: sendFormDataViaAjax();" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium"></div>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>
</div></div></div></div></div></div></div>

</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>