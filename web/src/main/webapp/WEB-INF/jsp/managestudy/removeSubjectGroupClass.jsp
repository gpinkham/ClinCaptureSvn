<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
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

<c:choose>
	<c:when test="${group.groupClassTypeId == 4}">
		<c:set var="pageHeader"><fmt:message key="confirm_to_remove_the_dynamic_group_class"  bundle="${resword}"/></c:set>
		<c:set var="pageDescription"><fmt:message key="confirm_removal_of_this_dynamic_group_class"  bundle="${resword}"/></c:set>
		<c:set var="allertMessage"><fmt:message key="if_you_remove_this_dynamic_group_class"  bundle="${resword}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="pageHeader"><fmt:message key="confirm_to_remove_the_subject_group_class"  bundle="${resword}"/></c:set>
		<c:set var="pageDescription"><fmt:message key="confirm_removal_of_this_subject_group_class"  bundle="${resword}"/></c:set>
		<c:set var="allertMessage"><fmt:message key="if_you_remove_this_subject_group_class"  bundle="${resword}"/></c:set>
	</c:otherwise>
</c:choose>

<h1>
	<span class="first_level_header">
		<c:out value="${pageHeader}"/>
	</span>
</h1>
<p><c:out value="${pageDescription}"/> <c:out value="${study.name}"/>.</p>

<div style="width: 600px">
<table class="table_horizontal table_shadow_bottom" border="0" cellpadding="0" cellspacing="0" width="100%">
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
					<c:out value="${group['default']}"/>
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
<br>

<c:choose>
	<c:when test="${group.groupClassTypeId == 4}">
		<div class="table_title_manage"><fmt:message key="study_events" bundle="${resword}"/>:</div>
		<div id="definitions">
		<div style="width: 600px">
			<table class="table_horizontal table_shadow_bottom" border="0" cellpadding="0" cellspacing="0" width="100%">
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
		</div>
	</c:when>
	<c:otherwise>
		<div class="table_title_manage"><fmt:message key="study_group_and_associated_subjects" bundle="${resword}"/>:</div>
		<div style="width: 600px">
		<table class="table_horizontal table_shadow_bottom" border="0" cellpadding="0" cellspacing="0" width="100%">   
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
	</c:otherwise>
</c:choose>
<br><br>
 <form action='RemoveSubjectGroupClass?action=submit&id=<c:out value="${group.id}"/>' method="POST">
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
    <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<c:out value="${allertMessage}"/>", height: 150, width: 500, submit: this });'/>
 </form> 
<br><br>
<jsp:include page="../include/footer.jsp"/>