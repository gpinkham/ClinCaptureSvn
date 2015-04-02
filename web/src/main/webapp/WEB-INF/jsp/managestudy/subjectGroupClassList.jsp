<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<script type="text/JavaScript" language="JavaScript">
  
 function moveDown(index) {

	$("tr#content"+index).insertAfter($("tr#content"+(index+1)));
	
	$("tr#content"+(index+1)).attr('id', 'content_'+(index+1)); //to change two variables we need third
	$("tr#content"+index).attr('id', 'content'+(index+1));
	$("tr#content_"+(index+1)).attr('id', 'content'+index);
	
	$("input[value='"+(index+1)+"']").attr('value', '_'+(index+1)); //to change two variables we need third
	$("input[value='"+index+"']").attr('value', (index+1));
	$("input[value='_"+(index+1)+"']").attr('value', index);
	
	$("td#order"+index).insertAfter($("td#order"+(index+1)));
	$("td#order"+(index+1)).insertBefore($("input[name^='dynamicGroup'][value="+(index+1)+"]").parent());
  } 
  
  function moveUp(index) {
	moveDown(index-1);
  }  
  
  function leftnavExpandExt(strLeftNavRowElementName, isHeader){
      var objLeftNavRowElement;

      objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
      if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
			objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
			if (isHeader) {
				objExCl = MM_findObj("excl_"+strLeftNavRowElementName);
				if(objLeftNavRowElement.display == "none"){
					objExCl.src = "images/bt_Expand.gif";
				}else{
					objExCl.src = "images/bt_Collapse.gif";
				}	
			}
        }
    }
  jQuery(window).load(function(){

  	highlightLastAccessedObject();
  });
</script>


<!-- then instructions-->
	<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		<fmt:message key="List_of_all_subject_group_classes_with_their_subject"  bundle="${resword}"/> <fmt:message key="select_any_group_class_for_details_on_it_records"  bundle="${resword}"/>
		<br><br>
        <fmt:message key="the_list_of_subject_group_classes_in_the_current_study"  bundle="${resword}"/>

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
	</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.bean.EntityBeanTable'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="manage_all_groups_in_study" bundle="${restext}"/> <c:out value="${study.name}"/>
		<a href="javascript:openDocWindow('help/3_4_subjectGroups_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>">
		</a>
	</span>
</h1>

<p></p>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showSubjectGroupClassRow.jsp" /></c:import>
<br>

<form action="ListSubjectGroupClass?${pageContext.request.queryString}&action=submit_order" method="post">
<c:if test="${fn:length(availableDynGroups) > 0}">
	<a href="javascript:leftnavExpandExt('dynGroupsOrder', true);">
		<img id="excl_dynGroupsOrder" src="images/bt_Collapse.gif" border="0"> 
		<fmt:message key="order_of_dynamic_groups" bundle="${resword}"/>
	</a>
	<br/></br>
	<div id="dynGroupsOrder" style="width: 65%;">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<table width="90%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<table width="100%" >
						<tr valign="top">
							<td class="table_header_row_left" width="40px">
								<fmt:message key="order" bundle="${resword}"/>
							</td>
							<td class="table_header_row">
								<fmt:message key="dynamic_group_name" bundle="${resword}"/>
							</td>
							<td class="table_header_row">
								<fmt:message key="events" bundle="${resword}"/>
							</td>
						</tr>
						<c:forEach var="dynGroup" items="${availableDynGroups}" varStatus="status">
						<c:choose>
							<c:when test="${dynGroup.default}">
								<tr id="content${status.count}">	
									<c:set var="defaultGroupExists" value="${true}"/>
									<td class="table_cell_left" id="order${status.count}">
										<c:choose>
											<c:when test="${study.parentStudyId != 0}">
												&nbsp;${status.count}.
											</c:when>
											<c:otherwise>
												&nbsp;
											</c:otherwise>
										</c:choose>
									</td>
									<td class="table_cell">
										<c:out value="${dynGroup.name}"/>
										(<fmt:message key="default" bundle="${resword}"/>)
									</td>
									<td class="table_cell">
										<c:choose>
											<c:when test="${fn:length(dynGroupClassIdToEventsNames[dynGroup.id]) eq 0}">
												<i><fmt:message key="all_events_in_group_removed" bundle="${restext}"/></i>
											</c:when>
											<c:otherwise>
												<c:out value="${dynGroupClassIdToEventsNames[dynGroup.id]}"/>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr id="content${status.count}">	
									<td class="table_cell_left" id="order${status.count}">
									<c:choose>
										<c:when test="${study.parentStudyId != 0}">
											&nbsp;${status.count}.
										</c:when>
										<c:when test="${status.first}">
											<c:choose>
												<c:when test="${fn:length(availableDynGroups) > 1}">
													&nbsp;<img src="images/bt_sort_descending.gif" border="0" onClick="javascript: moveDown(${status.count});" alt="<fmt:message key="move_down" bundle="${resword}"/>" title="<fmt:message key="move_down" bundle="${resword}"/>" />
												</c:when>
												<c:otherwise>
													&nbsp;
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:when test="${status.last && defaultGroupExists && fn:length(availableDynGroups) == 2}">
											&nbsp;
										</c:when>
										<c:when test="${status.last}">
											&nbsp;<img src="images/bt_sort_ascending.gif" onClick="javascript: moveUp(${status.count});" alt="<fmt:message key="move_up" bundle="${resword}"/>" title="<fmt:message key="move_up" bundle="${resword}"/>" border="0"/>
										</c:when>
										<c:when test="${defaultGroupExists && status.count == 2}">
											&nbsp;<img src="images/bt_sort_descending.gif" onClick="javascript: moveDown(${status.count});" alt="<fmt:message key="move_down" bundle="${resword}"/>" title="<fmt:message key="move_down" bundle="${resword}"/>" border="0" />
										</c:when>
										<c:otherwise>
											&nbsp;<img src="images/bt_sort_ascending.gif" onClick="javascript: moveUp(${status.count});" alt="<fmt:message key="move_up" bundle="${resword}"/>" title="<fmt:message key="move_up" bundle="${resword}"/>" border="0" />
											&nbsp;<img src="images/bt_sort_descending.gif" onClick="javascript: moveDown(${status.count});" alt="<fmt:message key="move_down" bundle="${resword}"/>" title="<fmt:message key="move_down" bundle="${resword}"/>" border="0" />
										</c:otherwise>
									</c:choose>
									</td>
									<td class="table_cell">
										<c:out value="${dynGroup.name}"/>
										<input type="hidden" name="dynamicGroup${dynGroup.id}" value="${status.count}">
									</td>
									<td class="table_cell">
										<c:choose>
											<c:when test="${fn:length(dynGroupClassIdToEventsNames[dynGroup.id]) eq 0}">
												<i><fmt:message key="all_events_in_group_removed" bundle="${restext}"/></i>
											</c:when>
											<c:otherwise>
												<c:out value="${dynGroupClassIdToEventsNames[dynGroup.id]}"/>
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
						</c:forEach>
					</table>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="right">
			<c:if test="${fn:length(availableDynGroups) > 1 && !(defaultGroupExists && fn:length(availableDynGroups) == 2) && (study.parentStudyId == 0)}">
				<input type="submit" name="BTN_Change_Group_Order" value="<fmt:message key="submit" bundle="${resword}"/>" class="button"/>
			</c:if>  
		</td>
	</tr>
	</table>		
	</div>
	</div></div></div></div></div></div></div></div>
	</div>
</c:if>   
</form>
<br> 
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
    <c:if test="${study.parentStudyId == 0}">
    	<input type="button" onclick="window.location.href=('CreateSubjectGroupClass');" name="BTN_Group" value="<fmt:message key="create_group" bundle="${resword}"/>" class="button_medium"/>
   	</c:if>      
 <input id="accessAttributeName" type="hidden" value="data-cc-groupId">
<jsp:include page="../include/footer.jsp"/>