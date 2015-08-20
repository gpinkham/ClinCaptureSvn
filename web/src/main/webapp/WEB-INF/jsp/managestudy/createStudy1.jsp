<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.admin" var="resadmin"/>

<jsp:include page="../include/admin-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
        <fmt:message key="enter_the_study_and_protocol" bundle="${resword}"/>
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

<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope="request" id="facRecruitStatusMap" class="java.util.HashMap"/>

<h1>
	<span class="first_level_header">
		<fmt:message key="create_a_new_study" bundle="${resword}"/>
	</span>
</h1>

<p>
<fmt:message key="ClinicalTrials.gov" bundle="${restext}"/>
</p>
<span class="title_Admin"><p><b><fmt:message key="section_a_study_description" bundle="${resword}"/></b></p></span>
<P><span class="alert">*</span> <fmt:message key="indicates_required_field" bundle="${resword}"/></P>
<form action="CreateStudy" method="post">
<input type="hidden" name="action" value="next">
<input type="hidden" name="pageNum" value="1">

 <div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">

	<tr valign="top">
		<td style="color: rgb(170, 98, 198);">Features:</td>
		<td class="formlabel">
	        <fmt:message key="crf_annotation" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="crfAnnotation" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="crfAnnotation" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="dynamic_group" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="dynamicGroup" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="dynamicGroup" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>				
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="calendared_visits" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="calendaredVisits" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="calendaredVisits" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="interactive_dashboards" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="interactiveDashboards" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="interactiveDashboards" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="item_level_sdv" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="itemLevelSDV" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="itemLevelSDV" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="subject_casebook_in_pdf" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="subjectCasebookInPDF" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="subjectCasebookInPDF" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="crfs_masking" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="crfMasking" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="crfMasking" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="sas_extracts" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="sasExtracts" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="sasExtracts" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="study_evaluator" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="studyEvaluator" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="studyEvaluator" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="randomization_cap" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" name="randomization" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" checked name="randomization" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	<tr valign="top">
		<td></td>
	    <td class="formlabel">
	        <fmt:message key="medical_coding" bundle="${resword}"/>?
	    </td>
	    <td>
			<input type="radio" checked name="medicalCoding" value="yes" onchange="javascript:changeIcon()"><fmt:message key="yes" bundle="${resword}"/>
			<input type="radio" name="medicalCoding" value="no" onchange="javascript:changeIcon()"><fmt:message key="no" bundle="${resword}"/>
		</td>
	</tr>
	
</table>
  </div>
  </div></div></div></div></div></div></div></div>
   </div>
	
	
 <div style="width: 600px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">	
		
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;"><b><fmt:message key="unique_protocol_ID" bundle="${resword}"/></b>:</a></td><td><div class="formfieldXL_BG">
  <input type="text" name="uniqueProId" value="<c:out value="${newStudy.identifier}"/>" class="formfieldXL" onchange="javascript:changeIcon();"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="uniqueProId"/></jsp:include></td><td class="formlabel alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefTitle" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefTitle'); return false;"><b><fmt:message key="brief_title" bundle="${resword}"/></b></a>:</td><td><div class="formfieldXL_BG">
  <input type="text" name="name" value="<c:out value="${newStudy.name}"/>" class="formfieldXL" onchange="javascript:changeIcon();"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td><td class="formlabel alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#OfficialTitle" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#OfficialTitle'); return false;"><b><fmt:message key="official_title" bundle="${resword}"/></b></a>:</td><td><div class="formfieldXL_BG">
  <input type="text" name="officialTitle" value="<c:out value="${newStudy.officialTitle}"/>" class="formfieldXL" onchange="javascript:changeIcon();"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="officialTitle"/></jsp:include>
  </td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds'); return false;"><b><fmt:message key="secondary_IDs" bundle="${resword}"/></b>:</a><br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td>
  <td><div class="formtextareaXL4_BG">
   <textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50" onchange="javascript:changeIcon();"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondProId"/></jsp:include>
  </td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td><div class="formfieldXL_BG">
  <input type="text" name="prinInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>" class="formfieldXL" onchange="javascript:changeIcon();"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="prinInvestigator"/></jsp:include></td><td class="formlabel alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#StudyType" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#StudyType'); return false;"><fmt:message key="protocol_type" bundle="${resword}"/></a>:</td><td>
   <c:set var="type1" value="observational"/>
   <c:choose>
    <c:when test="${newStudy.protocolTypeKey == type1}">
      <input type="radio" name="protocolType" value="interventional" onchange="javascript:changeIcon();"><fmt:message key="interventional" bundle="${resword}"/>
      <input type="radio" checked name="protocolType" value="observational" onchange="javascript:changeIcon();"><fmt:message key="observational" bundle="${resadmin}"/>
    </c:when>
    <c:otherwise>
      <input type="radio" checked name="protocolType" value="interventional" onchange="javascript:changeIcon();"><fmt:message key="interventional" bundle="${resword}"/>
      <input type="radio" name="protocolType" value="observational" onchange="javascript:changeIcon();"><fmt:message key="observational" bundle="${resadmin}"/>
    </c:otherwise>
   </c:choose>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolType"/></jsp:include></td><td class="alert">*</td></tr>


  </table>
  </div>
  </div></div></div></div></div></div></div></div>
   </div>

  <div style="width: 600px">
  <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

  <div class="textbox_center">
  <table border="0" cellpadding="0" cellspacing="0">
  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#BriefSummary'); return false;"><fmt:message key="brief_summary" bundle="${resword}"/></a>:</td><td><div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="description" rows="4" cols="50" maxlength="2000" onchange="javascript:changeIcon();"><c:out value="${newStudy.summary}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include></td><td class="formlabel alert">*</td></tr>

   <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#DetailedDescription" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#DetailedDescription'); return false;"><fmt:message key="detailed_description" bundle="${resword}"/></a>:</td><td>
   <div class="formtextareaXL4_BG"><textarea class="formtextareaXL4" name="protocolDescription" rows="4" cols="50" onchange="javascript:changeIcon();"><c:out value="${newStudy.protocolDescription}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolDescription"/></jsp:include>
  </td></tr>

  </table>
  </div>
  </div></div></div></div></div></div></div></div>
  </div>

  <div style="width: 600px">

  <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

  <div class="textbox_center">
  <table border="0" cellpadding="0" cellspacing="0">

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#LeadSponsor" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#LeadSponsor'); return false;"><fmt:message key="sponsor" bundle="${resword}"/></a>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="sponsor" value="<c:out value="${newStudy.sponsor}"/>" class="formfieldXL" onchange="javascript:changeIcon();"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="sponsor"/></jsp:include></td><td class="formlabel alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="collaborators" bundle="${resword}"/>:<br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td><td>
  <div class="formtextareaXL4_BG">
  <textarea class="formtextareaXL4" name="collaborators" rows="4" cols="50" onchange="javascript:changeIcon();"><c:out value="${newStudy.collaborators}"/></textarea></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="collaborators"/></jsp:include>
  </td></tr>

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>

    <div style="width: 600px">
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

    <div class="textbox_center">
    <table border="0" cellpadding="0" cellspacing="0">
        <tr><fmt:message key="select_user_for_edit_update" bundle="${restext}"/> </tr>
        <br>
        <br>
        <tr valign="top"><td class="formlabel">Select User :</td><td>
         <div class="formfieldL_BG">
          <select name="selectedUser" class="formfieldL" onchange="javascript:changeIcon();">1
          <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
          <c:forEach var="user" items="${users}">
              <option value="<c:out value="${user.id}"/>"> <c:out value="${user.name}"/>
                  (<c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/>)
              </option>
           </c:forEach>
         </select></div>
         </td></tr>

    </table>
    </div>
    </div></div></div></div></div></div></div></div>
    </div>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
	</td>
	<td>
	 	<input type="submit" id="Submit" name="Save" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" />
	</td>
	<td>
		<img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
		 <c:if test="${pageIsChanged ne null && pageIsChanged eq true}">
		   <script>
		     $("img[name=DataStatus_bottom]").attr("src", "images/icon_UnsavedData.gif");
		   </script>
		 </c:if>
	</td>
</tr>

</table>

</form>
<br><br>

<jsp:include page="../include/footer.jsp"/>
