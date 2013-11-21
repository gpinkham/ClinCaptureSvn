<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/managestudy-header.jsp"/>
 
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<script type="text/JavaScript" language="JavaScript">
  
 function moveDown(index) {
	
	$("tr#content"+index).insertAfter($("tr#content"+(index+1)));
    //$("div#content"+(index+1)).insertAfter($("td#order"+index));
	
	$("tr#content"+(index+1)).attr('id', 'content_'+(index+1)); //to change two variables we need third
	$("tr#content"+index).attr('id', 'content'+(index+1));
	$("tr#content_"+(index+1)).attr('id', 'content'+index);
	
	$("input[value='"+(index+1)+"']").attr('value', '_'+(index+1)); //to change two variables we need third
	$("input[value='"+index+"']").attr('value', (index+1));
	$("input[value='_"+(index+1)+"']").attr('value', index);
  } 
  
  function moveUp(index) {
	moveDown(index-1);
  } 
  
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListSubjectGroupClass";
       return true;
      } else {
        return false;
       }
     }
     return true;    
   
  }
  
</script>
<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_a_subject_group_class" bundle="${resword}"/>
	</span>
</h1>

<form action="CreateSubjectGroupClass" method="post">
<br>
<input type="hidden" name="action" value="submit">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
   
	<tr valign="top">
		<td width="30%" class="table_header_column">
			<fmt:message key="name" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<c:out value="${group.name}"/>
		</td>
	</tr>
	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="type" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<c:out value="${group.groupClassTypeName}"/>
		</td>
	</tr> 
 
<c:choose>
	<c:when test="${group.groupClassTypeId == 4}">
	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="default" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<c:out value="${group.default}"/> 
		</td>
	</tr>  
	</c:when>
	<c:otherwise>
	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="subject_assignment" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<c:out value="${group.subjectAssignment}"/>
		</td>
	</tr>  
	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="study_groups" bundle="${resword}"/>:
		</td>
		<td class="table_cell">  
		<c:forEach var="studyGroup" items="${studyGroups}">
			<c:out value="${studyGroup.name}"/>&nbsp;&nbsp;<c:out value="${studyGroup.description}"/>
			<br>   
		</c:forEach>  
		</td> 
	</tr>  
	</c:otherwise>
</c:choose>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<c:if test="${group.groupClassTypeId == 4}">
	</br>
	<div id="events" class="table_title_manage">
		<fmt:message key="study_events" bundle="${resword}"/>:
	</div>
	<div style="width: 70%">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td width="45px">
				<table width="100%" >
					<tr valign="top">  
						<td class="table_header_row_left">
							<fmt:message key="order" bundle="${resword}"/>
						</td>
					</tr>
					<c:forEach var="item" items="${listOfDefinitions}" varStatus="status">
					<tr valign="top">  
						<td class="table_cell_left" id="order${status.count}">
						<c:choose>
							<c:when test="${status.first}">
								<c:choose>
									<c:when test="${fn:length(listOfDefinitions) > 1}">
										&nbsp;<img src="images/bt_sort_descending.gif" border="0" onClick="javascript: moveDown(${status.count});" alt="<fmt:message key="move_down" bundle="${resword}"/>" title="<fmt:message key="move_down" bundle="${resword}"/>" />
									</c:when>
									<c:otherwise>
										&nbsp;
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${status.last}">
								&nbsp;<img src="images/bt_sort_ascending.gif" onClick="javascript: moveUp(${status.count});" alt="<fmt:message key="move_up" bundle="${resword}"/>" title="<fmt:message key="move_up" bundle="${resword}"/>" border="0"/>
							</c:when>
							<c:otherwise>
								&nbsp;<img src="images/bt_sort_ascending.gif" onClick="javascript: moveUp(${status.count});" alt="<fmt:message key="move_up" bundle="${resword}"/>" title="<fmt:message key="move_up" bundle="${resword}"/>" border="0" />
								&nbsp;<img src="images/bt_sort_descending.gif" onClick="javascript: moveDown(${status.count});" alt="<fmt:message key="move_down" bundle="${resword}"/>" title="<fmt:message key="move_down" bundle="${resword}"/>" border="0" />
							</c:otherwise>
						</c:choose>
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
			<td>
				<table width="100%" >
					<tr valign="top">
						<td class="table_header_row">
							<fmt:message key="event_name" bundle="${resword}"/>
						</td>
						<td class="table_header_row">
							<fmt:message key="OID" bundle="${resword}"/>
						</td>
					</tr>
					<c:forEach var="item" items="${listOfDefinitions}" varStatus="status">
					<tr id="content${status.count}">			
						<td class="table_cell">
							<c:out value="${item.name}"/>
							<input type="hidden" name="event${item.id}" value="${status.count}">
						</td>
						<td class="table_cell">
							<c:out value="${item.oid}"/>
						</td>
					</tr>	
					</c:forEach>
				</table>
			</td>
		</tr>
		</table>
	</div>
	</div></div></div></div></div></div></div></div>
	</div>
</c:if>   

</br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
 <input id="GoBackToSubjectList" class="button_medium" type="button" name="BTN_Back" title="<fmt:message key="back" bundle="${resword}"/>" value="<fmt:message key="back" bundle="${resword}"/>" onclick="javascript: return checkGoToEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>','CreateSubjectGroupClass?action=back');"/></td>
<td>
<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
</td>
<td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>
</tr>
</table>
</form>
<jsp:include page="../include/footer.jsp"/>