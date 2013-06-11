<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>	
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<fmt:message key="instructions" bundle="${resword}"/>

		<div class="sidebar_tab_content">
       
        <fmt:message key="A_study_event_definition_describes_a_type_of_study_event"  bundle="${resword}"/> <fmt:message key="please_consult_the_ODM"  bundle="${resword}"/> 
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
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListEventDefinition";
       return true;
      } else {
        return false;
       }
     }
     return true;       
  }
   //-->
</script>
<script type="text/JavaScript" language="JavaScript">
$(document).ready(function() {
	$(".showHide").css('display', 'none');
	$('tr[name="repeating"]').css('display', 'none');
	
	$('select[name="type"]').change(function() {
		if($(this).find(":selected").val() == '') {
			$('tr[name="repeating"]').hide();
			$(".showHide").fadeOut('medium');
			$('input[name*="Day"]').attr('value','').attr('readonly','');
			$('.showHide input[type="checkbox"]').attr('checked', false);
			$('input[name="emailUser"]').attr('value','').attr('readonly','');
		}
		if($(this).find(":selected").val() == 'calendared_visit') {
			$(".showHide").fadeIn('medium');
			$('tr[name="repeating"]').fadeOut('medium');
		} else if($(this).find(":selected").val() != '') {
			$('tr[name="repeating"]').fadeIn('medium');
			$(".showHide").fadeOut('medium');
			$('input[name*="Day"]').attr('value','').attr('readonly','');
			$('.showHide input[type="checkbox"]').attr('checked', false);
			$('input[name="emailUser"]').attr('value','').attr('readonly','');
		}
	})

	$('input[name="isReference"]').click(function() {
		if ($(this).is(':checked')) {
			$('input[name*="Day"]').attr('value','0').attr('readonly','true');
			$('input[name="emailUser"]').attr('value','').attr('readonly','true');
			$("tr[id^='email']").fadeOut('medium');

		} else {
			$('input[name*="Day"]').attr('value','').attr('readonly','');
			$('input[name="emailUser"]').attr('readonly','');
			$("tr[id^='email']").fadeIn('medium');

		}
	});
	$('select[name="type"]').each(function() {
		if($(this).find(":selected").val() == 'calendared_visit') {
			$('tr[name="repeating"]').hide();
			$(".showHide").css('display', '');
		} else if($(this).find(":selected").val() == '') {
			$('tr[name="repeating"]').hide();
			$(".showHide").fadeOut('medium');
			$('input[name*="Day"]').attr('value','').attr('readonly','');
			$('.showHide input[type="checkbox"]').attr('checked', false);
			$('input[name="emailUser"]').attr('value','').attr('readonly','');
		} else {
			$('tr[name="repeating"]').fadeIn('medium');
		}
	});
	$('input[name="isReference"]').each(function() {
		if ($(this).is(':checked')) {
			$('tr[name="repeating"]').hide();
			$('input[name*="Day"]').attr('value','0').attr('readonly','true');
			$('input[name="emailUser"]').attr('value','').attr('readonly','true');
			$("tr[id^='email']").hide();
		}
	});
});
</script>
<h1><span class="title_manage">
<fmt:message key="create_SED_for"  bundle="${resword}"/> <c:out value="${study.name}"/>
</span></h1>
	<ol>
    	<fmt:message key="list_create_SED_for"  bundle="${resword}"/>
	</ol>
	<br>

* <fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<form action="DefineStudyEvent" method="post">
<input type="hidden" name="actionName" value="next">
<input type="hidden" name="pageNum" value="1">
<div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">
   <tr valign="top"><td class="formlabel"><fmt:message key="name" bundle="${resword}"/>:</td><td><table><tr><td>
 <div class="formfieldXL_BG"><input type="text" name="name" onchange="javascript:changeIcon();" value="<c:out value="${definition.name}"/>" class="formfieldXL"></div>
  </td><td class="formlabel">*</td><td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr></table></td></tr>
  <tr valign="top"><td class="formlabel"><fmt:message key="description" bundle="${resword}"/>:</td><td>  
  <div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="description" onchange="javascript:changeIcon();" onchange="javascript:changeIcon();" rows="4" cols="50"><c:out value="${definition.description}"/></textarea>
  </div>  
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
  </td></tr>
 
 <tr valign="top" name="repeating"><td class="formlabel"><fmt:message key="repeating" bundle="${resword}"/>:</td><td>
  <c:choose>
   <c:when test="${definition.repeating == true}">
    <input type="radio" checked name="repeating" onchange="javascript:changeIcon();" value="1"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" name="repeating" onchange="javascript:changeIcon();" value="0"><fmt:message key="no" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <input type="radio" name="repeating" onchange="javascript:changeIcon();" value="1"><fmt:message key="yes" bundle="${resword}"/>
    <input type="radio" checked name="repeating" onchange="javascript:changeIcon();" value="0"><fmt:message key="no" bundle="${resword}"/>
   </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="type" bundle="${resword}"/>:</td><td><table><tr><td>
    <div class="formfieldXL_BG"><select name="type" onchange="javascript:changeIcon();" class="formfieldXL">        
       <c:choose>
        <c:when test="${definition.type == 'common'}">          
         <option value="scheduled"><fmt:message key="scheduled" bundle="${resword}"/>
         <option value="unscheduled"><fmt:message key="unscheduled" bundle="${resword}"/>
         <option value="common" selected><fmt:message key="common" bundle="${resword}"/>
         <option value="calendared_visit"><fmt:message key="calendared_visit" bundle="${resword}"/>
		 <option value=''>
        </c:when>        
        <c:when test="${definition.type == 'unscheduled'}">       
         <option value="scheduled"><fmt:message key="scheduled" bundle="${resword}"/>
         <option value="unscheduled" selected><fmt:message key="unscheduled" bundle="${resword}"/>
         <option value="common"><fmt:message key="common" bundle="${resword}"/>
         <option value="calendared_visit"><fmt:message key="calendared_visit" bundle="${resword}"/>
		 <option value=''>
        </c:when>
        <c:when test="${definition.type == 'calendared_visit'}">       
         <option value="scheduled"><fmt:message key="scheduled" bundle="${resword}"/>
         <option value="unscheduled"><fmt:message key="unscheduled" bundle="${resword}"/>
         <option value="common"><fmt:message key="common" bundle="${resword}"/>
         <option value="calendared_visit" selected><fmt:message key="calendared_visit" bundle="${resword}"/>
		 <option value=''>
        </c:when>
		<c:when test="${definition.type == 'scheduled'}">
         <option value="scheduled" selected><fmt:message key="scheduled" bundle="${resword}"/>
         <option value="unscheduled"><fmt:message key="unscheduled" bundle="${resword}"/>
         <option value="common"><fmt:message key="common" bundle="${resword}"/>
         <option value="calendared_visit" ><fmt:message key="calendared_visit" bundle="${resword}"/>
		 <option value=''>		
		</c:when> 
        <c:otherwise>        
         <option value="scheduled"><fmt:message key="scheduled" bundle="${resword}"/>
         <option value="unscheduled"><fmt:message key="unscheduled" bundle="${resword}"/>
         <option value="common"><fmt:message key="common" bundle="${resword}"/>
         <option value="calendared_visit"><fmt:message key="calendared_visit" bundle="${resword}"/>
		 <option value='' selected>
        </c:otherwise>
       </c:choose>       
    </select></div>
   </td><td class="formlabel">*</td><td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="type"/></jsp:include></td></tr></table></td></tr>
  
  <tr valign="top"><td class="formlabel"><fmt:message key="category" bundle="${resword}"/>:</td><td>  
   <div class="formfieldXL_BG"><input type="text" name="category" onchange="javascript:changeIcon();" value="<c:out value="${definition.category}"/>" class="formfieldXL"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="category"/></jsp:include>
  </td></tr>
<!-- Clinovo ticket #134 start -->
  <tr valign="top" class='showHide'>
  		<td style="white-space: nowrap;"><fmt:message key="reference_event" bundle="${resword}"/>:</td><td>
  		<c:choose>
  		<c:when test="${isReference == 'true'}">
  		<input type="checkbox" value="true" checked name="isReference"/>
  		</c:when>
  		<c:otherwise>
  		<input type="checkbox" value="true" name="isReference"/>
  		</c:otherwise>
  		</c:choose>
  		<c:choose>
  		<c:when test="${referenceVisitAlredyExist == 'true'}">
  		<span class="alert"><fmt:message key="reference_visit_already_exist" bundle="${resword}"/></span>
  		</c:when>
  		</c:choose></td>
  </tr>
  <tr valign="top" class='showHide'>
  		<td class="formlabel" style="padding-top:10px"><fmt:message key="day_schedule" bundle="${resword}"/>:</td><td><table width=480px><tr><td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${schDay}"/>" type="text" size="3" name="schDay"/></div>
		</td><td style="padding-bottom:20px">*</td><td width="250px"><fmt:message key="after_the_reference_visit" bundle="${resword}"/></td><td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="schDay"/></jsp:include></td></tr></table></td>
  </tr>
   <tr valign="top" class='showHide'>
  		<td class="formlabel" style="padding-top:15px"><fmt:message key="day_max" bundle="${resword}"/>:</td><td><table width=480px><tr><td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${maxDay}"/>" type="text" size="3" name="maxDay"/></div>
		</td><td style="padding-bottom:20px">*</td><td width="250px"><fmt:message key="the_maximum_day_that_an_event" bundle="${resword}"/></td><td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="maxDay"/></jsp:include></td></tr></table></td>
    </tr>
      <tr valign="top" class='showHide'>
  		<td class="formlabel" style="padding-top:15px"><fmt:message key="day_min" bundle="${resword}"/>:</td><td><table width=480px><tr><td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${minDay}"/>" type="text" size="3" name="minDay"/></div>
		</td><td style="padding-bottom:20px">*</td><td width="250px"><fmt:message key="the_minimum_day_that_an_event" bundle="${resword}"/></td><td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="minDay"/></jsp:include></td></tr></table></td>
    </tr>
	<tr valign="top" class='showHide'>
  		<td class="formlabel" style="padding-top:15px"><fmt:message key="day_email" bundle="${resword}"/>:</td><td><table width=480px><tr><td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${emailDay}"/>" type="text" size="3" name="emailDay"/></div>
		</td><td style="padding-bottom:20px">*</td><td width="250px"><fmt:message key="the_day_a_reminder_email_is" bundle="${resword}"/></td><td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="emailDay"/></jsp:include></td></tr></table></td>
    </tr>

   <tr valign="top" class='showHide' id='email'>
  		<td class="formlabel"><fmt:message key="user_name" bundle="${resword}"/>:</td><td><table><tr><td><div class="formfieldXL_BG" ><input class="formfieldXL" value="<c:out value="${emailUser}"/>" type="text" size="3" name="emailUser"/></div><td style="padding-bottom:20px">*</td></td></tr></table></td></tr>
		<tr class='showHide' id='email'><td>&nbsp</td><td><fmt:message key="use_only_a_valid_user_name" bundle="${resword}"/></tr>
		<tr class='showHide' id='email'><td>&nbsp</td><td width="250px"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="emailUser"/></jsp:include></td></tr>

 <!--  Clinovo end -->
  
  <!-- end -->
 

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<table border="0" cellpadding="0" cellspacing="0">
<tr>

<td>
 <%-- <input type="button" name="BTN_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: return checkGoBackEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>');"/> --%>
	<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" 
					value="<fmt:message key="back" bundle="${resword}"/>" 
					class="button_medium" 
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
</td>
<td>
  <input type="submit" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium">
</td>
<td>
  <img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
</td>
</tr></table>
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
						
							<b><fmt:message key="enter_definition_name_and_description" bundle="${resword}"/><br><br></b>
									
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
				             <fmt:message key="add_CRFs_to_definition" bundle="${resword}"/><br><br>
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
				             <fmt:message key="edit_properties_for_each_CRF" bundle="${resword}"/><br><br>
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
				             <fmt:message key="confirm_and_submit_definition" bundle="${resword}"/><br><br>
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
