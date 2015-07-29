<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise>
</c:choose>

<script type="text/javascript" language="javascript">
function confirmRemove(){
	
	confirmDialog({ 
		message: "<fmt:message key="rule_if_you_remove_this_all" bundle="${resword}"/>",
		height: 150, 
		width: 500, 
		redirectLink: 'UpdateRuleSetRule?action=remove&ruleSetId=<c:out value="${ruleSet.id}"/>&source=ViewRuleSet'
	});
}
</script>
<!-- *JSP* submit/viewRules.jsp -->
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

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='ruleSet' class='org.akaza.openclinica.domain.rule.RuleSetBean'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="rule_manage_rule_title" bundle="${resword}"/> <c:out value="${ruleSet.itemName}"/>
	</span>
</h1>
<table><tr><td>
<div>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_target_oid" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.target.value}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="rule_study_event_definition" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.studyEventDefinitionNameWithOID}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="CRF_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.crfWithVersionNameWithOid}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_group_label" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${ruleSet.groupLabelWithOid}"/>
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="rule_item_name" bundle="${resword}"/>:</td><td class="table_cell">
  <a href="javascript: openDocWindow('ViewItemDetail?itemId=${ruleSet.itemId}')"><c:out value="${ruleSet.itemNameWithOid}"/></a>
  </td></tr>

</table>
</div>
</div></div></div></div></div></div></div></div>

</div></td></tr></table>
<br>
<div>
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_row_left"><fmt:message key="rule_name" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="rule_oid" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="rule_expression" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="rule_status" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="rule_execute_on" bundle="${resword}"/></td>
    <!-- <td class="table_header_row"><fmt:message key="rule_action_type" bundle="${resword}"/></td>  -->
    <td class="table_header_row"><fmt:message key="rule_action_summary" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="action" bundle="${resword}"/></td>
    </tr>
  <c:forEach var ="ruleSetRule" items="${ruleSetRuleBeans}">
    <c:choose>
     <c:when test="${ruleSetRule.status.name eq 'AVAILABLE'}">
        <c:set var="className" value="aka_green_highlight"/>
    </c:when>
    <c:otherwise>
     <c:set var="className" value="aka_red_highlight"/>
    </c:otherwise>
    </c:choose>
    <c:set var="count" value="0"/>
    <c:forEach items="${ruleSetRule.allActionsWithEvaluatesToAsKey}" varStatus="status">
      <c:set  var="count" value="${count + fn:length(status.current.value) +1}" />
    </c:forEach>

    <tr valign="top">
    <td rowspan="${count +1}" class="table_cell_left"><c:out value="${ruleSetRule.ruleBean.name}"/></td>
    <td rowspan="${count +1}" class="table_cell"><c:out value="${ruleSetRule.ruleBean.oid}"/></td>
    <td rowspan="${count +1}" class="table_cell rule-table-expression-td"><c:out value="${ruleSetRule.ruleBean.expression.value}"/></td>
    <td rowspan="${count +1}" class="table_cell <c:out value='${className}'/>" style="font-weight: normal;">${ruleSetRule.status.description}</td>
    <td class="table_cell"></td>
    <td class="table_cell">&nbsp;</td>
    <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
      <tr>
      <%--<c:choose>--%>
      <%--<c:when test="${ ruleSet.status.name eq 'AVAILABLE'  }">--%>
      <c:if test="${ ruleSetRule.status.name eq 'AVAILABLE'  }">
      <td><a href="RunRuleSet?ruleSetId=<c:out value="${ruleSet.id}"/>&ruleId=<c:out value="${ruleSetRule.ruleBean.id}"/>"
      onMouseDown="javascript:setImage('bt_View1','images/bt_ExexuteRules.gif');"
      onMouseUp="javascript:setImage('bt_View1','images/bt_ExexuteRules.gif');"><img
      name="bt_View1" src="images/bt_ExexuteRules.gif" border="0" alt="<fmt:message key="rule_run" bundle="${resword}"/>" title="<fmt:message key="rule_run" bundle="${resword}"/>" align="left" hspace="6"></a>
      </td>
      <td><a href="UpdateRuleSetRule?action=remove&ruleSetRuleId=<c:out value="${ruleSetRule.id}"/>&ruleSetId=<c:out value="${ruleSet.id}"/>&source=ViewRuleSet"
      onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
      onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
      onClick='return confirmDialog({ message: "<fmt:message key="rule_if_you_remove_this" bundle="${resword}"/>", height: 150, width: 500, aLink: this });'><img
      name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
      </td>
      </c:if>
      <c:if test="${ ruleSetRule.status.name eq 'DELETED' }">
      <td>
      <a href="UpdateRuleSetRule?action=restore&ruleSetRuleId=<c:out value="${ruleSetRule.id}"/>&ruleSetId=<c:out value="${ruleSet.id}"/>&source=ViewRuleSet"
      onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
      onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
      onClick='return confirmDialog({ message: "<fmt:message key="rule_if_you_restore_this" bundle="${resword}"/>", height: 150, width: 500, aLink: this });'><img
      name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
      </td>
      </c:if>
      <td>
      <a onmouseup="javascript:setImage('bt_run','images/bt_EnterData.gif');" 
         onmousedown="javascript:setImage('bt_run','images/bt_EnterData_d.gif');" 
         href="TestRule?ruleSetRuleId=<c:out value='${ruleSetRule.id}'/>&ruleSetId=<c:out value="${ruleSet.id}"/>"><img align="left" hspace="6" border="0" title="Test" alt="Test" src="images/bt_Reassign_d.gif" name="Test"></a>
      
      </td>

<!--       <td>

      <a onmouseup="javascript:setImage('bt_run','images/bt_EnterData.gif');" 
         onmousedown="javascript:setImage('bt_run','images/bt_EnterData_d.gif');" 
         href="${sessionScope.ruleDesignerUrl}&path=ViewRuleSet?ruleSetId=${ruleSet.id}"><img align="left" hspace="6" border="0" title="Rule Designer" alt="Rule Designer" src="images/bt_EnterData.gif" name="Test"></a>
      
      </td> -->
      <%--</c:when>--%>
      <%--<c:otherwise>--%>
      <%--</c:otherwise>--%>
      <%--</c:choose>--%>
      </tr></table></td>

    <c:forEach items="${ruleSetRule.allActionsWithEvaluatesToAsKey}" varStatus="status">
    <tr valign="top">
        <td rowspan="${fn:length(status.current.value) +1 }" class="table_cell"><c:out value="${status.current.key}"/></td>
        
    </tr>
        <c:forEach items="${status.current.value}" var="val">
        <tr valign="top">
            <td class="table_cell">
            <table class="action-summary-table">
                <c:forEach items="${val.propertiesForDisplay}" var="mapEntry" varStatus="status">
                <tr valign="top">
                    <td ><i><fmt:message key="${mapEntry.key}" bundle="${resword}" /></i></td>
                    <td ><c:out value="${mapEntry.value}"/></td>
                </tr>
                </c:forEach>
                
                <c:set var="runon" value=""/>
		        <c:if test="${val.ruleActionRun.initialDataEntry eq 'true'}"><c:set var="runon"><fmt:message key="IDE_comma" bundle="${resword}" /></c:set></c:if>
		        <c:if test="${val.ruleActionRun.doubleDataEntry eq 'true'}"><c:set var="runon"><c:out value="${runon}"/> <fmt:message key="DDE_comma" bundle="${resword}" /></c:set></c:if>
		        <c:if test="${val.ruleActionRun.administrativeDataEntry eq 'true'}"><c:set var="runon"><c:out value="${runon}"/> <fmt:message key="ADE_comma" bundle="${resword}" /></c:set></c:if>
		        <c:if test="${val.ruleActionRun.batch eq 'true'}"><c:set var="runon"><c:out value="${runon}"/> <fmt:message key="batch_comma" bundle="${resword}"/></c:set></c:if>
		        <c:if test="${fn:length(runon)>0}">
			        <tr valign="top">
		        		<td><i><fmt:message key="run_on_colon" bundle="${resword}" /></i></td>
	        			<td><c:out value="${fn:substring(runon,0,fn:length(runon)-1)}"/></td>
		        	</tr>
		        </c:if>
		        
		        <c:if test="${val.actionType.code!=1 && val.actionType.code !=2 && fn:length(val.properties)>0}">
		        	<c:set var="props" value=""/>
		    		<c:forEach items="${val.properties}" var="prop" varStatus="status">
		    			<c:set var="props"><c:out value="${props}"/> <c:out value="${prop.oid}"/>,</c:set>
		    		</c:forEach>
		        	<c:if test="${fn:length(props)>0}">
		    			<tr valign="top">
	                		<td ><i><fmt:message key="dest_prop_colon" bundle="${resword}" /></i></td>
                			<td ><c:out value="${fn:substring(props,0,fn:length(props)-1)}"/></td>
                		</tr>
                	</c:if>
                </c:if>
            </table>
            </td>
            <td class="table_cell">&nbsp;</td>
        </tr>
        </c:forEach>
    </c:forEach>

    <tr><td class="table_divider" colspan="7">&nbsp;</td></tr>


 </c:forEach>

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>

	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
<input type="button" value="<fmt:message key="view_rules_run_all" bundle="${resword}"/>" 
title="<fmt:message key="view_rules_run_all_tip" bundle="${resword}"/>" class="button_medium" onclick="javascript:window.location.href='RunRuleSet?ruleSetId=<c:out value="${ruleSet.id}"/>'"/>
<input type="button" value="<fmt:message key="view_rules_remove_all" bundle="${resword}"/>" 
title="<fmt:message key="view_rules_remove_all_tip" bundle="${resword}"/>" class="button_medium" onclick="confirmRemove();" />
<input type="button" value="<fmt:message key="download" bundle="${resword}"/>" 
title="<fmt:message key="view_rules_get_xml_tip" bundle="${resword}"/>" class="button_medium" onclick="javascript:window.location.href='DownloadRuleSetXml?ruleSetRuleIds=<c:out value="${validRuleSetRuleIds}"/>'"/>
<input type="button" value="<fmt:message key="view_rules_audit" bundle="${resword}"/>" 
title="<fmt:message key="view_rules_audit_tip" bundle="${resword}"/>" class="button_medium" onclick="javascript:window.location.href='ViewRuleSetAudit?ruleSetId=<c:out value="${ruleSet.id}"/>'"/>


<c:choose>
  <c:when test="${userBean.sysAdmin && module=='admin'}">
  <c:import url="../include/workflow.jsp">
   <c:param name="module" value="admin"/>
  </c:import>
 </c:when>
  <c:otherwise>
   <c:import url="../include/workflow.jsp">
   <c:param name="module" value="manage"/>
  </c:import>
  </c:otherwise>
 </c:choose>
<jsp:include page="../include/footer.jsp"/>
