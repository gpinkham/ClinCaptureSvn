<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/managestudy-header.jsp"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

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
  
  function showGroupSection() {
  
    var index = $(":select [name='groupClassTypeId'] :selected").val();
	switch (index) {
		case '4':
			$("div#groups").hide();
			$("div#definitions").show();
			$("tr#isDefaultRow").show();   
			$("tr#subjAssignmentRow").hide(); 
			break
		case '':
			$("div#definitions").hide();
			$("div#groups").hide();
			$("input#isDefault").attr("checked", "");
			$("tr#isDefaultRow").hide();   
			$("tr#subjAssignmentRow").hide(); 
			break
		default: 
			$("div#definitions").hide();
			$("div#groups").show();
			$("input#isDefault").attr("checked", "");
			$("tr#isDefaultRow").hide();   
			$("tr#subjAssignmentRow").show(); 
	}
	return true;
  }
  
  $(document).ready(function() { 
		showGroupSection();
	});

  function showMoreFields(index) {
  
	for (var j=index+1; (j<51)&&(j<(index+4)); j++){
		$("tr#row"+j).show();
	}
  }	
	
</script>
<h1><span class="title_manage"><fmt:message key="create_a_subject_group_class" bundle="${resword}"/> <a href="javascript:openDocWindow('help/4_7_subjectGroups_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<form action="CreateSubjectGroupClass" method="post">
* <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<input type="hidden" name="action" value="confirm">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="75%"> 
	<tr valign="top">
		<td width="140" class="formlabel">
			<fmt:message key="name" bundle="${resword}"/>:
		</td>
		<td>
			<div class="formfieldXL_BG">
				<input type="text" name="name" onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="<c:out value="${fields['groupClassName']}"/>" class="formfieldXL">
			</div>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include>
		</td>
		<td>*</td>
	</tr>
    
	<tr valign="top">
		<td class="formlabel">
			<fmt:message key="type" bundle="${resword}"/>:
		</td>
		<td>
			<div class="formfieldL_BG">
			<c:set var="groupClassTypeId1" value="${fields['groupClassTypeId']}"/>   
			<select name="groupClassTypeId" onChange="showGroupSection(); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" class="formfieldL">
				<option value="">--</option>
				<c:forEach var="type" items="${groupTypes}">    
				<c:choose>
					<c:when test="${groupClassTypeId1 == type.id}">   
						<option value="<c:out value="${type.id}"/>" selected><c:out value="${type.name}"/>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${type.id}"/>"><c:out value="${type.name}"/>      
					</c:otherwise>
				</c:choose> 
				</c:forEach>
			</select>
			</div>
			<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="groupClassTypeId"/></jsp:include>
		</td>
		<td>*</td>
	</tr>      
  
	<tr valign="top" id="subjAssignmentRow">
		<td class="formlabel" width="140">
			<fmt:message key="subject_assignment" bundle="${resword}"/>:
		</td>
		<td>
		<c:choose>
			<c:when test="${fields['subjectAssignment'] =='Required'}">
				<input type="radio" checked name="subjectAssignment" onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="Required"><fmt:message key="required" bundle="${resword}"/>
				<input type="radio" name="subjectAssignment" onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="Optional"><fmt:message key="optional" bundle="${resword}"/>
			</c:when>
			<c:otherwise>
				<input type="radio" name="subjectAssignment" onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="Required"><fmt:message key="required" bundle="${resword}"/>
				<input type="radio" checked name="subjectAssignment" onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="Optional"><fmt:message key="optional" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
		</td>
		
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="subjectAssignment"/></jsp:include>
		<td>*</td>
	</tr>
	<tr valign="top" id="isDefaultRow">
		<td class="formlabel" width="140">
			<fmt:message key="default" bundle="${resword}"/>
		</td>
		<td>
			<c:choose>
			<c:when test="${fields['isDefault'] =='yes'}">
				<input type="checkbox" checked id="isDefault" name="isDefault" value="yes">
			</c:when>
			<c:otherwise>
				<input type="checkbox" id="isDefault" name="isDefault" value="yes">
			</c:otherwise>
		</c:choose>
			
		</td>
		<td></td>
	</tr>
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>

<%-- Ordinary Study Group Section --%>
<div id="groups" style="display: none">

 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
 <div class="tablebox_center">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top">
			<td class="formlabel">
				<br>
				<fmt:message key="study_groups" bundle="${resword}"/>:
			</td>
			<td style="padding-top:4px;">
				
				<c:set var="count" value="0"/>
				<table border="0" cellpadding="0" cellspacing="0">  
					<br>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyGroupError"/></jsp:include>
					<tr>      
						<td>&nbsp;</td>
						<td>&nbsp;<strong><fmt:message key="name" bundle="${resword}"/></strong></td>
						<td>&nbsp;<strong><fmt:message key="description" bundle="${resword}"/></strong></td>
					</tr>   
					<c:forEach var="studyGroup" items="${studyGroups}">
					<tr>  
						<td valign="top">
							<c:out value="${count+1}"/>
							&nbsp;
						</td>  
						<td>
							<div class="formfieldS_BG">
								<input type="text" name="studyGroup<c:out value="${count}"/>" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="<c:out value="${studyGroup.name}"/>" class="formfieldS">
							</div>
						</td>  
						<td> 
							<div class="formfieldL_BG">
								<input type="text" name="studyGroupDescription<c:out value="${count}"/>" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="<c:out value="${studyGroup.description}"/>" class="formfieldL">
							</div>
						</td>
					</tr> 
					<c:set var="count" value="${count+1}"/>
					</c:forEach>
					
					<c:choose>
						<c:when test="${count < 5}">
							<c:set var="delta" value="${6-count}"/>
						</c:when>
						<c:when test="${count > 46}">
							<c:set var="delta" value="${49-count}"/>
						</c:when>
						<c:otherwise>
							<c:set var="delta" value="2"/>
						</c:otherwise>
					</c:choose>

					<c:forEach begin="${count}" end="${count+delta}">
					<tr>  
						<td valign="top">
							<c:out value="${count+1}"/>
							&nbsp;
						</td>  
						<td>
							<div class="formfieldS_BG">
								<input type="text" name="studyGroup<c:out value="${count}"/>" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="" class="formfieldS">
							</div>
						</td>  
						<td> 
							<div class="formfieldL_BG">
								<input type="text" name="studyGroupDescription<c:out value="${count}"/>" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="" class="formfieldL">
							</div>
						</td>
					</tr> 
					<c:set var="count" value="${count+1}"/>
					</c:forEach>
					
					<c:if test="${count <= 49}">
						<c:forEach begin="${count}" end="49">
						<tr style="display: none" id="row${count}">  
							<td valign="top"><c:out value="${count+1}"/>&nbsp;</td>    
							<td>
								<div class="formfieldS_BG">
									<input type="text" name="studyGroup<c:out value="${count}"/>" value="" class="formfieldS" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');">
								</div>
							</td>       
							<td> 
								<div class="formfieldL_BG">
									<input type="text" name="studyGroupDescription<c:out value="${count}"/>" onChange="showMoreFields(${count}); setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" value="<c:out value="${studyGroup.description}"/>" class="formfieldL">
								</div>
							</td>
						</tr>
						<c:set var="count" value="${count+1}"/>
						</c:forEach>   
					</c:if> 
					<br>    
				</table> 
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="studyGroupError"/></jsp:include>
			</td>
		</tr> 		
	</table>
	<br>
</div>
</div></div></div></div></div></div></div></div></div>
</div>
 
 <%-- Dynamic Group Section --%>
 <div id="definitions" style="display: none">
 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="study_events" bundle="${resword}"/>:
<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dynamicEvents"/></jsp:include>
<br></br>
	<table border="1" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top">
			<td class="table_header_row">&nbsp;</td>
			<td class="table_header_row"><fmt:message key="name" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="OID" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="description" bundle="${resword}"/></td>
			<td class="table_header_row"><fmt:message key="of_CRFs" bundle="${resword}"/></td>
		</tr>
		<c:forEach var="definition" items="${definitionsToView}" varStatus="status">
		<tr>
			<td class="table_cell_left">
			<c:choose>
				<c:when test="${definition.value}">
					<input type="checkbox" checked onchange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" 
						value="yes" name="selected<c:out value="${definition.key.id}"/>"/>&nbsp;
				</c:when>
				<c:otherwise>
					<input type="checkbox" onchange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');" 
						value="yes" name="selected<c:out value="${definition.key.id}"/>"/>&nbsp;
				</c:otherwise>
			</c:choose>
			</td>
			<td class="table_cell">
				<c:out value="${definition.key.name}"/>
			</td>
			<td class="table_cell"><c:out value="${definition.key.oid}"/></td>
			<td class="table_cell">
				<c:out value="${definition.key.description}"/>&nbsp;
			</td>
			<td class="table_cell">
				<c:out value="${definition.key.crfNum}"/>&nbsp;
			</td>
		</tr>
		</c:forEach>
	</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
</div>
<br>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
  <input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" 
					value="<fmt:message key="back" bundle="${resword}"/>" 
					class="button_medium" 
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
  <img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this CRF section." alt="Data Status" name="DataStatus_bottom">
</td>  
<td>
<input type="submit" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium">
</td>
<%-- <td><input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>--%>
</tr>
</table>  
</form>
<%--<c:import url="../include/workflow.jsp">--%>
  <%--<c:param name="module" value="manage"/>--%>
 <%--</c:import>--%>
<jsp:include page="../include/footer.jsp"/>
