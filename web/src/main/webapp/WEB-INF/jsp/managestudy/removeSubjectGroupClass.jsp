<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
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

<h1><span class="title_manage"><fmt:message key="confirm_to_remove_a_subject_group_class"  bundle="${resword}"/></span></h1>
<p><fmt:message key="confirm_deletion_of_this_subject_group_class"  bundle="${resword}"/> <c:out value="${study.name}"/>.</p>

<!-- These DIVs define shaded box borders -->

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr valign="top">
		<td class="table_header_column">
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
			<c:out value="${group.groupClassTypeName}"/>&nbsp;
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
		</c:otherwise>
	</c:choose>
</table>
</div>
</div></div></div></div></div></div></div></div>
</div>
<br>

<c:choose>
	<c:when test="${group.groupClassTypeId == 4}">
		<div class="table_title_manage"><fmt:message key="study_events" bundle="${resword}"/>:</div>
		<div id="definitions">
		<div style="width: 600px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

		<div class="tablebox_center">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr valign="top">
					<td class="table_header_row_left">&nbsp;</td>
					<td class="table_header_row"><fmt:message key="name" bundle="${resword}"/></td>
					<td class="table_header_row"><fmt:message key="OID" bundle="${resword}"/></td>
					<td class="table_header_row"><fmt:message key="description" bundle="${resword}"/></td>
					<td class="table_header_row"><fmt:message key="of_CRFs" bundle="${resword}"/></td>
					<td class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
				</tr>
				<c:forEach var="definition" items="${orderedDefinitions}" varStatus="status">
				<tr>
					<td class="table_cell_left">
						<c:out value="${status.count}"/>
					</td>
					<td class="table_cell">
						<c:out value="${definition.name}"/>
					</td>
					<td class="table_cell"><c:out value="${definition.oid}"/></td>
					<td class="table_cell">
						<c:out value="${definition.description}"/>&nbsp;
					</td>
					<td class="table_cell">
						<c:out value="${definition.crfNum}"/>
					</td>
					<c:choose>
						<c:when test="${definition.status.available}">
							<td class="table_cell"><c:out value="${definition.status.name}"/></td>	
						</c:when>
						<c:otherwise>
							<td class="table_cell aka_red_highlight"><c:out value="${definition.status.name}"/></td>	
						</c:otherwise>
					</c:choose>
				</tr>
				</c:forEach>
			</table>
		</div>
		</div></div></div></div></div></div></div></div>
		</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="table_title_manage"><fmt:message key="study_group_and_associated_subjects" bundle="${resword}"/>:</div>
		<div style="width: 600px">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		<div class="tablebox_center">

		<table border="0" cellpadding="0" cellspacing="0" width="100%">   
			<tr valign="top">
				<td class="table_header_row"><fmt:message key="name" bundle="${resword}"/></td>
				<td class="table_header_row"><fmt:message key="description" bundle="${resword}"/></td>
				<td class="table_header_row"><fmt:message key="subjects" bundle="${resword}"/></td> 
			</tr>    
			<c:forEach var="studyGroup" items="${studyGroups}">   
			<tr valign="top">
				<td class="table_cell">  
					<c:out value="${studyGroup.name}"/>  
				</td>
				<td class="table_cell">  
					<c:out value="${studyGroup.description}"/>&nbsp;  
				</td>
				<td class="table_cell">  
				<c:forEach var="subjectMap" items="${studyGroup.subjectMaps}">       
					<c:out value="${subjectMap.subjectLabel}"/>
					<br>      
				</c:forEach>
				&nbsp;
				</td>
			</tr>      
			</c:forEach>  
		</table>
		</div>
		</div></div></div></div></div></div></div></div>
		</div>
	</c:otherwise>
</c:choose>

 <form action='RemoveSubjectGroupClass?action=submit&id=<c:out value="${group.id}"/>' method="POST">
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
    <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium" onClick='return confirm("<fmt:message key="if_you_remove_this_subject_group_class" bundle="${resword}"/>");'>
 </form> 
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr>
		<td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
	</td>
	<td id="sidebar_Workflow_open" style="display: all">
	<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
		<tr>
			<td class="workflowBox_T" valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="workflow_tab">
					<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

					<b><fmt:message key="workflow" bundle="${resword}"/></b>

					</td>
				</tr>
			</table>
			</td>
			<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
		</tr>
		<tr>
			<td colspan="2" class="workflowbox_B">
			<div class="box_R"><div class="box_B"><div class="box_BR">
				<div class="workflowBox_center">

		<!-- Workflow items -->

				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	
							<div class="textbox_center" align="center">
	
							<span class="title_manage">
				
							<fmt:message key="manage_study" bundle="${resword}"/>	
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">
				
							<fmt:message key="manage_groups" bundle="${resword}"/>
					
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
						<td><img src="images/arrow.gif"></td>
						<td>

				<!-- These DIVs define shaded box borders -->
						<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

							<div class="textbox_center" align="center">

							<span class="title_manage">				
					
							<b><fmt:message key="remove_subject_group_class" bundle="${resword}"/></b>					
				
							</span>

							</div>
						</div></div></div></div></div></div></div></div>

						</td>
					</tr>
				</table>

		<!-- end Workflow items -->

				</div>
			</div></div></div>
			</td>
		</tr>
	</table>			
	</td>
   </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>