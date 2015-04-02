<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
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

<h1>
	<span class="first_level_header">
		<fmt:message key="view_a_subject_group_class" bundle="${resword}"/>
	</span>
</h1>


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

<c:choose>
	<c:when test="${group.groupClassTypeId == 4}">
		</br>
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
		</br>
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

<p>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
</p>

 <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/> 
 </c:import>
 
<jsp:include page="../include/footer.jsp"/>