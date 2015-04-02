<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>
<html> 
<head>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <title><fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="view_discrepancy_note" bundle="${resword}"/></title>
    <link rel="stylesheet" href="includes/styles.css" type="text/css">

    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
    <script language="JavaScript">
        function scrollToElement(id) {
            alert(id);
            alert(document.getElementById(id).offsetTop);
            window.scrollTo(0,(document.getElementById(id)).offsetTop);
        }

        function findPosition(obj) {
            var curtop = 0;
            if (obj.offsetParent) {
                do {
                    curtop += obj.offsetTop;
                } while (obj = obj.offsetParent);
            return [curtop];
            }
        }
		
		function sendFormDataViaAjax(formId) {
			var aForm = $('form#oneDNForm_'+formId);
			$( "div#divWithData_"+formId).hide();
			$( "div#ajax-loader_"+formId).show();
			$.ajax({
				type: aForm.attr('method'),
				url: aForm.attr('action'),
				data: aForm.serialize(),
				success: function (data) {
					function showDiv() {
						var divWithData = $("div#divWithData_"+formId);
						divWithData.html('');
						divWithData.append(data);
						$("div#ajax-loader_"+formId).hide();
						divWithData.show();
					};
					if (data.indexOf('Save Done') > -1) {
						showDiv();
						changeDNFlagIconInParentWindow();
						window.close();
					} else if (data.indexOf('Error in data') > -1) {
						showDiv();
					} else {
						alert('No response from server');
					}
				}
			});
		}
		
		$(document).ready(function() {
			var beginNewThreadLink = $("a#a0");0
			if (beginNewThreadLink && ${isRFC}) {
				eval(beginNewThreadLink.attr('href'));
			}
		})
    </script>

    <style type="text/css">

        .popup_BG { background-image: url(images/main_BG.gif);
            background-repeat: repeat-x;
            background-position: top;
            background-color: #FFFFFF;
        }

        .table_cell_left { padding-left: 8px; padding-right: 8px; }
    </style>
    <ui:theme/>
</head>

<body class="popup_BG" style="margin: 0px 12px 0px 12px;" 	onload="window.scrollTo(0,'<c:out value="${y}"/>');javascript:setStatusWithId('<c:out value="${typeID0}"/>','0','<c:out value="${whichResStatus}"/>','<fmt:message key="New" bundle="${resterm}"/>','<fmt:message key="Updated" bundle="${resterm}"/>','<fmt:message key="Resolution_Proposed" bundle="${resterm}"/>','<fmt:message key="Closed" bundle="${resterm}"/>','<fmt:message key="Not_Applicable" bundle="${resterm}"/>');refreshSource('<c:out value="${refresh}"/>', '/ViewNotes?');">

<c:if test="${updatedDiscrepancyNote ne null}">
  <script type="text/javascript" language="javascript">			setImageInParentWin('flag_<c:out value="${updatedDiscrepancyNote.field}"/>', '${contextPath}<c:out value="${updatedDiscrepancyNote.resStatus.iconFilePath}"/>', '${updatedDiscrepancyNote.resStatus.id}');
  </script>
</c:if>
<!-- Alert Box -->
<!-- *JSP* submit/viewDiscrepancyNote.jsp -->
<!-- These DIVs define shaded box borders -->
<%-- <jsp:include page="../include/alertbox.jsp"/> --%>

<!-- End Alert Box -->
<div style="float: left;">
	<h1>
		<span class="first_level_header">			<c:out value="${entityName}"/><c:if test="${itemDataOrdinal ne null}">(#${itemDataOrdinal})</c:if>: <fmt:message key="view_discrepancy_notes" bundle="${resword}"/>
		</span>
	</h1>
</div><div style="float: right;"><p>
</p></div>
<br clear="all">

<!-- Entity box -->
<table border="0" cellpadding="0" cellspacing="0" style="float:left;">
    <tr>
        <td valign="bottom">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td nowrap style="padding-right: 20px;">
                        <div class="tab_BG_h"><div class="tab_R_h" style="padding-right: 0px;"><div class="tab_L_h" style="padding: 3px 11px 0px 6px; text-align: left;">
						<b><c:choose>
                            <c:when test="${entityName != '' && entityName != null }">
                                  "<c:out value="${entityName}"/><c:if test="${itemDataOrdinal ne null}">(#${itemDataOrdinal})</c:if>"
                            </c:when>
                            <c:otherwise>
                                <%-- nothing here; if entityName is blank --%>
                            </c:otherwise>
                        </c:choose>
                        <fmt:message key="Properties" bundle="${resword}"/>:</b>
                        </div></div></div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
	<td valign="top">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
            <table border="0" cellpadding="0" cellspacing="0">
            	<tr>
                    <td class="table_cell_noborder"><fmt:message key="subject" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder"><b><c:out value="${noteSubject.label}"/></b></td>
                    <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="event" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder">
                        <b><c:choose>
                            <c:when test="${studyEvent.name != null && studyEvent.name != ''}">
                                <c:out value="${studyEvent.name}"/><c:if test="${studyEventOrdinal ne null}">(x${studyEventOrdinal})</c:if>
                            </c:when>
                            <c:otherwise>
								<fmt:message key='N/A' bundle='${resword}'/>
                            </c:otherwise>
                        </c:choose></b>
                    </td>
            	</tr>
            	<tr>
                    <td class="table_cell_noborder"><fmt:message key="event_date" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder">
                        
						<b><c:choose>
                            <c:when test="${studyEvent.dateStarted != null}">
                                <fmt:formatDate value="${studyEvent.dateStarted}" pattern="${dteFormat}"/>&nbsp;
                            </c:when>
                            <c:otherwise>
								<fmt:message key='N/A' bundle='${resword}'/>
                            </c:otherwise>
                        </c:choose></b>
                    </td>
                    <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="CRF" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder">
                        <b><c:choose>
                            <c:when test="${crf.name != null && crf.name != ''}">
                                <c:out value="${crf.name}"/>
                            </c:when>
                            <c:otherwise>
								<fmt:message key='N/A' bundle='${resword}'/>
                            </c:otherwise>
                        </c:choose></b>
                    </td>
            	</tr> 
            	<tr>
                    <td class="table_cell_noborder"><fmt:message key="Current_Value" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder">
						<b>
						<c:choose>
							<c:when test="${entityValue != null && entityValue != ''}">
								<c:out value="${entityValue}"/>
							</c:when>
							<c:otherwise>
								<fmt:message key='N/A' bundle='${resword}'/>
							</c:otherwise>
						</c:choose>
						&nbsp;
						</b>
					</td>
                    <td class="table_cell_noborder" style="padding-left: 40px;"><fmt:message key="More" bundle="${resword}"/>:&nbsp;&nbsp;</td>
                    <td class="table_cell_noborder">
					<c:choose>
						<c:when test="${name eq 'itemData' || name eq 'ItemData'}">
							<a href="javascript: openDocWindow('ViewItemDetail?itemId=${item.id}')">
							<fmt:message key="Data_Dictionary" bundle="${resword}"/></a>
						</c:when>
						<c:otherwise>
							<b><fmt:message key="N/A" bundle="${resword}"/></b>
						</c:otherwise>
					</c:choose>
                    </td>  
                </tr>
                <c:if test="${name eq 'itemData' ||name eq 'ItemData'}">
	                <tr>
    	                <td class="table_cell_noborder">
    	                <td class="table_cell_noborder">
    	                <td class="table_cell_noborder">
    	                <td class="table_cell_noborder">
	            	</tr>
            	</c:if>
			</table>
			</div>
        </div></div></div></div></div></div></div>
    </td>
    </tr>
 
</table>


<div style="clear:both;"></div>
<h3 class="title_manage"><fmt:message key="note_details" bundle="${resword}"/></h3>

<div class="alert">    
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/><br><br>
</c:forEach>
</div>

<c:set var="count" value="${1}"/>
<!-- Thread Heading -->
<c:forEach var="note" items="${discrepancyNotes}">

    <c:if test="${note.value.saved==false}">
    	<c:set var="disableBeginNewThread" value="${true}"/>
    </c:if>   
    
    <table border="0" cellpadding="0" cellspacing="0">
        <tbody>
            <tr>
                <td>
                    <!-- These DIVs define shaded box borders -->
                    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                        <div class="tablebox_center">
                            <table border="0" cellpadding="0" cellspacing="0" width="600">
                                <tr class="aka_stripes">
                                    <td class="aka_header_border" colspan="4">
                                        <div style="float: left; font-size: 15px;">

                                            <!-- expand/collapse button -->

                                            <a href="javascript:leftnavExpand('thread<c:out value="${count}"/>');leftnavExpand('thread<c:out value="${count}"/>_expand');leftnavExpand('thread<c:out value="${count}"/>_collapse');">
                                                <div id="thread<c:out value="${count}"/>_collapse" style="width: 8px; height: 8px; border-color: #789EC5; border-width: 1px; border-style: solid; line-height: 6px; text-align: center; float: left; margin: 3px 6px 0px 0px; ">-</div>
                                                <div id="thread<c:out value="${count}"/>_expand" style="width: 8px; height: 8px; border-color: #789EC5; border-width: 1px; border-style: solid; font-size: 10px; line-height: 6px; text-align: center; float: left; margin: 3px 6px 0px 0px; display: none;">+</div>
                                            </a>

                                            <!-- Thread title -->
                                            <b><c:out value="${note.value.description}"/>
                                                <c:if test="${note.value.saved==false}">
                                                    <span class="alert">[<fmt:message key="not_saved" bundle="${resword}"/>]</span>
                                                </c:if>
                                            </b>
                                        </div>
                                        <div style="float: right; padding-left: 30px;">
                                            <fmt:message key="Last_updated" bundle="${resword}"/>: <b><fmt:formatDate value="${note.value.lastDateUpdated}" pattern="${dteFormat}"/> by <c:out value="${note.value.owner.name}"/></b><br>
                                            <fmt:message key="Assigned_to" bundle="${resword}"/>:&nbsp;&nbsp;  <b> <c:out value="${note.value.assignedUser.firstName}"/> <c:out value="${note.value.assignedUser.lastName}"/> (<c:out value ="${note.value.assignedUser.name}"/>)
                                        </div>
                                    </td>
                                </tr>
                                <tr class="aka_stripes">
                                    <td class="aka_header_border" width="25%"><fmt:message key="ID" bundle="${resword}"/>: <b><c:out value="${note.value.id}"/></b></td>
                                    <td class="aka_header_border" width="25%"><fmt:message key="type" bundle="${resword}"/>: <b><c:out value="${note.value.disType.name}"/></b></td>
                                    <td class="aka_header_border" width="25%" >
                                        Current Status: <b><c:out value="${note.value.resStatus.name}"/></b>
                                        <input type="hidden" value="${note.value.resStatus.id}" id="dn_status_${note.value.id}">
                                    </td>
                                    <td class="aka_header_border" width="25%"><fmt:message key="of_notes" bundle="${resword}"/>: <b><c:out value="${note.value.numChildren}" /></b></td>
                                </tr>
                            </table>
                            <table border="0" cellpadding="0" cellspacing="0" width="600" id="thread<c:out value="${count}"/>">

                                <c:forEach var="child" items="${note.value.children}" varStatus="status">

                                    <c:set var="addNote" value="true"/>

                                    <c:if test="${userRole.studyCoder}">

                                        <c:set var="belongs" value="${child.owner.id == userBean.id || child.assignedUserId == userBean.id}"/>
                                        <c:if test="${belongs == false}">
                                            <c:set var="addNote" value="false"/>
                                        </c:if>

                                    </c:if>

                                    <tr>
                                        <td class="table_cell_left" colspan="2" bgcolor="#f5f5f5" width="50%" valign="top"><b><c:out value="${child.description}"/></b></td>
                                        <td class="table_cell" bgcolor="#f5f5f5" align="left" width="25%" valign="top" nowrap><fmt:message key="status" bundle="${resword}"/>: <c:out value="${child.resStatus.name}"/></td>
                                        <td class="table_cell" bgcolor="#f5f5f5" width="25%" align="right" valign="top" nowrap>
                                            <fmt:formatDate value="${child.createdDate}" pattern="${dteFormat}"/> by <c:out value="${child.owner.name}"/><br>
                                            <c:if test="${child.assignedUserId > 0}">
                                                <fmt:message key="Assigned_to" bundle="${resword}"/>: <c:out value="${child.assignedUser.firstName}"/> <c:out value="${child.assignedUser.lastName}"/> (<c:out value ="${child.assignedUser.name}"/>)
                                            </c:if>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="table_cell_left" colspan="4">
                                            <c:out value="${child.detailedNotes}"/>
                                        </td>
                                    </tr>

                                    <c:if test="${!status.last}">
                                        <!-- Spacer row -->
                                        <tr>
                                            <td class="table_header_row_left" colspan="4"  style="border-top-width: 1px; border-top-color: #CCCCCC; font-size: 1px; line-height: 4px; height: 6px; padding: 0px;">&nbsp;</td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:set var="showDNBox" value="n"/>
                                <c:if test="${!study.status.locked}">
                                    <tr>
                                        <td class="table_cell_left" colspan="4" align="right">
                                            <c:if test="${(note.value.id>0 && note.value.resStatus.id != 5) && !(note.value.resStatus.id == 4)}">
                                                <c:set var="sindex" value="0"/>
                                                <c:forEach var="status" items="${resolutionStatuses}">
                                                    <c:choose>
                                                    <c:when test="${status.id == 2}">
                                                        <c:if test="${addNote == true}">
                                                            <input class="button_medium" type="button" id="resStatus${status.id}${note.value.id}" value="<fmt:message key="update_note" bundle="${resterm}"/>" onclick="javascript:boxShowWithDefault('<c:out value="${note.value.id}"/>','<c:out value="${sindex}"/>','<c:out value="${status.id}"/>','<c:out value="${status.name}"/>'); showAnotherDescriptions(2, ${note.value.id});/*scrollToElement('<c:out value="submitBtn${note.value.id}"/>');*/"/>
                                                        </c:if>
                                                    </c:when>
                                                    <c:when test="${status.id == 4}">
                                                        <c:if test="${addNote == true}">
                                                            <input class="button_medium" type="button" id="resStatus${status.id}${note.value.id}" value="<fmt:message key="close_note" bundle="${resterm}"/>" onclick="javascript:boxShowWithDefault('<c:out value="${note.value.id}"/>','<c:out value="${sindex}"/>','<c:out value="${status.id}"/>','<c:out value="${status.name}"/>'); showAnotherDescriptions(4, ${note.value.id});"/>
                                                        </c:if>
                                                    </c:when>
                                                    </c:choose>
                                                    <c:set var="sindex" value="${sindex+1}"/>
                                                </c:forEach>
                                                <br>
                                                <c:set var="showDNBox" value="y"/>
                                            </c:if>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="table_cell_left" id="msg${note.value.id}">
                                        <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="newChildAdded${note.value.id}"/></jsp:include>  
                                        </td>
                                    </tr>
                                </c:if>
                        </table>
                    </div>
                </div></div></div></div></div></div></div></div>
            </td>
        </tr>
        <tr>
            <td>        
        		<c:if test="${showDNBox eq 'y'}">
					<c:forEach var="child" items="${note.value.children}">
						<c:if test="${child.owner.id != userBean.id}">
							<c:set var="previousDNUpdaterId" value="${child.owner.id}"/>
						</c:if>
					</c:forEach>
					<div id="ajax-loader_${note.value.id}" style="width: 580; height: 270; display: none;" align="center"><img src="images/ajax-loader-blue.gif"/></div>
					<div id="divWithData_${note.value.id}">
						<c:import url="./discrepancyNote.jsp">
							<c:param name="formCounter" value="${count}"/>
							<c:param name="parentId" value="${note.value.id}"/>
							<c:param name="entityId" value="${id}"/>                
							<c:param name="entityType" value="${name}"/>                
							<c:param name="field" value="${field}"/>                
							<c:param name="column" value="${column}"/>
							<c:param name="boxId" value="box${note.value.id}"/>
							<c:param name="typeId" value="${note.value.discrepancyNoteTypeId}"/>
							<c:param name="typeName" value="${note.value.disType.name}"/>
							<c:param name="previousDNUpdaterId" value="${previousDNUpdaterId}"/>
						</c:import>
					</div>
        		</c:if>
			</td>
        </tr>
    </tbody>
</table>
<c:set var="count" value="${count+1}"/>
</c:forEach>

<c:if test="${!study.status.locked}">
	<div style="clear:both;"></div>
	<c:choose>
    <c:when test="${id == 0 || disableBeginNewThread}">
    	<p id="p">
			<b><fmt:message key="begin_new_thread" bundle="${resword}"/></b>
		</p>
    </c:when>
    <c:otherwise>
		<p id="p">
			<a href="javascript:showOnly('box<c:out value="${0}"/>');removeText('a0','<b><fmt:message key="begin_new_thread" bundle="${resword}"/></b>');" id="a0"><b><fmt:message key="begin_new_thread" bundle="${resword}"/></b></a>
		</p>
	</c:otherwise>
	</c:choose>
	<div id="ajax-loader_0" style="width: 580; height: 270; display: none;" align="center"><img src="images/ajax-loader-blue.gif"/></div>
	<div id="divWithData_0">
		<c:import url="./discrepancyNote.jsp">
			<c:param name="parentId" value="0"/>
			<c:param name="entityId" value="${id}"/>				
			<c:param name="entityType" value="${name}"/>				
			<c:param name="field" value="${field}"/>				
			<c:param name="column" value="${column}"/>
			<c:param name="boxId" value="box${0}"/>
			<c:param name="isRFC" value="${isRFC}"/>
			<c:param name="isInError" value="${isInError}"/>
			<c:param name="strErrMsg" value="${strErrMsg}"/>
			<c:param name="previousDNUpdaterId" value="${0}"/>
		</c:import> 
	</div>
</c:if>  
 
<div style="clear:both;"></div>
<div id="audit">
<h3 class="title_manage"><fmt:message key="Audit_History" bundle="${resword}"/></h3>
<c:import url="../admin/auditItem.jsp">
	<c:param name="entityCreatedDate" value="${entityCreatedDate}"/>
</c:import>
</div>
<div style="clear:both;"></div>
</body>

<input type="button" name="BTN_Close" id="CloseWindow" value="<fmt:message key="close_window" bundle="${resword}"/>" class="button_medium" onClick="javascript:window.close();"/>
<jsp:include page="../include/changeTheme.jsp"/>
</html>