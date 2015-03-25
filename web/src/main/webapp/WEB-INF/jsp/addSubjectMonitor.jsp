<%--
  User: Hamid
  Date: November 25, 2010
  A copy of addNewSubjectExpressNew.jsp that would be used by Investigators Home Page.
--%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<jsp:useBean scope="request" id="label" class="java.lang.String"/>

<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean" />
<jsp:useBean scope="request" id="pageMessages" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<jsp:useBean scope="request" id="groups" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="fathers" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="mothers" class="java.util.ArrayList" />

<c:set var="uniqueIdentifier" value="" />
<c:set var="chosenGender" value="" />
<c:set var="label" value="" />
<c:set var="secondaryLabel" value="" />
<c:set var="enrollmentDate" value="" />
<c:set var="startDate" value=""/>
<c:set var="dob" value="" />
<c:set var="yob" value="" />
<c:set var="groupId" value="${0}" />
<c:set var="fatherId" value="${0}" />
<c:set var="motherId" value="${0}" />
<c:set var="studyEventDefinition" value=""/>
<c:set var="location" value=""/>

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "uniqueIdentifier"}'>
		<c:set var="uniqueIdentifier" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "gender"}'>
		<c:set var="chosenGender" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "label"}'>
		<c:set var="label" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "secondaryLabel"}'>
		<c:set var="secondaryLabel" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "enrollmentDate"}'>
		<c:set var="enrollmentDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dob"}'>
		<c:set var="dob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "yob"}'>
		<c:set var="yob" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "group"}'>
		<c:set var="groupId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mother"}'>
		<c:set var="motherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "father"}'>
		<c:set var="fatherId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "studyEventDefinition"}'>
		<c:set var="studyEventDefinition" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "location"}'>
		<c:set var="location" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<form name="subjectForm" action="AddNewSubject" method="post">
<input type="hidden" name="subjectOverlay" value="true">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectIDLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<div style="width: 500px; height: 550px; overflow: scroll; background:#FFFFFF;">
<table border="0" cellpadding="0" >
    <tr style="height:10px;">
        <td width="35%"><h3><fmt:message key="add_new_subject" bundle="${resword}"/></h3></td>
        <td >&nbsp;</td>
    </tr>
    <tr valign="top">
        <td class="formlabel">
            <jsp:include page="include/showSubmitted.jsp" />
            <input type="hidden" name="addWithEvent" value="1"/>
            ${studySubjectIDLabel}:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top"><div class="formfieldXL_BG">
                    <c:choose>
                     <c:when test="${study.studyParameterConfig.subjectIdGeneration =='auto non-editable'}">
                      <input onfocus="this.select()" type="text" value="<c:out value="${label}"/>" size="45" class="formfield" disabled>
                      <input type="hidden" name="label" value="<c:out value="${label}"/>">
                     </c:when>
                     <c:otherwise>
                       <input onfocus="this.select()" type="text" name="label" value="<c:out value="${label}"/>" size="50" class="formfieldXL">
                     </c:otherwise>
                    </c:choose>
                    </div></td>
                    <td>*</td>
                </tr>
                <tr>
                    <td><jsp:include page="showMessage.jsp"><jsp:param name="key" value="label"/></jsp:include></td>
                </tr>
                
            </table>
        </td>
    </tr>
    <c:choose>
    <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='required'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top"><div class="formfieldXL_BG">
                        <input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
                    </div></td>
                    <td>*</td>
                </tr>
                <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
            </table>
        </td>
    </tr>
    </c:when>
    <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='optional'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top"><div class="formfieldXL_BG">
                        <input onfocus="this.select()" type="text" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>" size="50" class="formfieldXL">
                    </div></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="uniqueIdentifier"/></jsp:include></td>
                </tr>
            </table>
        </td>
    </tr>
    </c:when>
    <c:when test="${study.studyParameterConfig.subjectPersonIdRequired =='copyFromSSID'}">
    	<script>
			$(document).ready(function() {
				initFieldCopying("[name=label]","[name=uniqueIdentifier]");
			});
		</script>
	    &nbsp;<input type="hidden" name="uniqueIdentifier" value="<fmt:message key="person_ID" bundle="${resword}"/>" size="12" tabindex="<c:out value="${tabCount}"/>" class="formfield">
    </c:when>
    <c:otherwise>
      <input type="hidden" name="uniqueIdentifier" value="<c:out value="${uniqueIdentifier}"/>">
    </c:otherwise>
    </c:choose>

    <c:set var="enrollmentDateShow" value="${true}"/>
    <fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel"/>
    <c:if test="${study ne null}">
        <c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}"/>
        <c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}"/>
    </c:if>
    <c:if test="${enrollmentDateShow}">
        <tr valign="top">
            <td class="formlabel">
                ${enrollmentDateLabel}:
            </td>
            <td valign="top">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td valign="top">
                            <!--layer-background-color:white;-->
                            <div class="formfieldM_BG">
                                <input onfocus="this.select()" type="text" name="enrollmentDate" size="15" value="<c:out value="${enrollmentDate}" />" class="formfieldM" id="enrollmentDateField" />
                            </div>
                        </td>
                        <td>
							<ui:calendarIcon onClickSelector="'#enrollmentDateField'" imageId="enrollmentDateTrigger" />
                            *
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="enrollmentDate"/></jsp:include></td>
                    </tr>
                </table>
            </td>
        </tr>
    </c:if>

    <c:if test="${study.studyParameterConfig.genderRequired != 'false'}">
        <tr valign="top">
            <td class="formlabel">${study.studyParameterConfig.genderLabel}:</td>
            <td valign="top">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td valign="top">
                            <div class="formfieldS_BG">
                                <select name="gender" class="formfieldS">
                                    <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
                                    <c:choose>
                                        <c:when test="${!empty chosenGender}">
                                            <c:choose>
                                                <c:when test='${chosenGender == "m"}'>
                                                    <option value="m" selected><fmt:message key="male" bundle="${resword}"/></option>
                                                    <option value="f"><fmt:message key="female" bundle="${resword}"/></option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="m"><fmt:message key="male" bundle="${resword}"/></option>
                                                    <option value="f" selected><fmt:message key="female" bundle="${resword}"/></option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="m"><fmt:message key="male" bundle="${resword}"/></option>
                                            <option value="f"><fmt:message key="female" bundle="${resword}"/></option>
                                        </c:otherwise>
                                    </c:choose>
                                </select>
                            </div>
                        </td>
                        <td align="left">
                            <c:choose>
                            <c:when test="${study.studyParameterConfig.genderRequired != 'false'}">
                               <span class="formlabel">*</span>
                            </c:when>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </c:if>

    <tr>
        <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="gender"/></jsp:include></td>
    </tr>


    <c:choose>
    <c:when test="${study.studyParameterConfig.collectDob == '1'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top">
                        <div class="formfieldM_BG">
                            <input onfocus="this.select()" type="text" name="dob" size="15" value="<c:out value="${dob}" />" class="formfieldM" id="dobField" />
                        </div>
                    </td>
                    <td>
						<ui:calendarIcon onClickSelector="'#dobField'" imageId="dobTrigger"/>
                    </td>
                    <td>* </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="dob"/></jsp:include></td>
    </tr>

    </c:when>
    <c:when test="${study.studyParameterConfig.collectDob == '2'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="year_of_birth" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top">
                        <div class="formfieldM_BG">
                            <input onfocus="this.select()" type="text" name="yob" size="15" value="<c:out value="${yob}" />" class="formfieldM" />
                        </div>
                    </td>
                    <td>(<fmt:message key="date_format_year" bundle="${resformat}"/>) *</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="yob"/></jsp:include></td>
    </tr>

  </c:when>
  <c:otherwise>
    <input type="hidden" name="dob" value="" />
  </c:otherwise>
 </c:choose>
<c:if test="${(!empty studyGroupClasses)}">
    <tr valign="top">
      <td class="formlabel"><fmt:message key="subject_group_class" bundle="${resword}"/>:
      <td class="table_cell">
      <c:set var="count" value="0"/>
      <table border="0" cellpadding="0">
        <c:forEach var="group" items="${studyGroupClasses}">
        <tr valign="top">
         <td><b><c:out value="${group.name}"/></b></td>
         <td><div class="formfieldM_BG">
             <select name="studyGroupId<c:out value="${count}"/>" class="formfieldM">
                 <option value=""><c:out value="${group.name}"/>:</option>
                  <c:forEach var="studyGroup" items="${group.studyGroups}">
                    <option value="<c:out value="${studyGroup.id}"/>"><c:out value="${studyGroup.name}"/></option>
                  </c:forEach>
              </select></div>
             <c:import url="showMessage.jsp"><c:param name="key" value="studyGroupId${count}" /></c:import>

              </td>
              <c:if test="${group.subjectAssignment=='Required'}">
                <td align="left">*</td>
              </c:if>
              </tr>
             <c:set var="count" value="${count+1}"/>
        </c:forEach>
        </table>
      </td>
    </tr>
</c:if>

    <tr valign="top">
        <td class="formlabel"><fmt:message key="SED_2" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr><td>
                    <div class="formfieldS_BG">
                        <select name="studyEventDefinition" class="formfieldS">
                            <option value="">-<fmt:message key="select" bundle="${resword}"/>-</option>
                            <c:forEach var="event" items="${allDefsArray}">
                                <option <c:if test="${studyEventDefinition == event.id}">SELECTED</c:if> value="<c:out value="${event.id}"/>"><c:out value="${event.name}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    </td>
                    <td><span class="formlabel">*</span></td>
                </tr>
                <tr>
                    <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="studyEventDefinition"/></jsp:include></td>
                </tr>

            </table>
        </td>
    </tr>

    <tr valign="top">
        <td class="formlabel">
            <fmt:message key="start_date" bundle="${resword}"/>:
        </td>
          <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top">
                        <div class="formfieldM_BG">
                            <input type="text" name="startDate" size="15" value="<c:out value="${startDate}" />" class="formfieldM" id="enrollmentDateField2" />
                        </div>
                    </td>
                    <td>
						<ui:calendarIcon onClickSelector="'#enrollmentDateField2'" imageId="enrollmentDateTrigger2"/>
                        *
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include></td>
                </tr>

            </table>
          </td>
    </tr>
    <c:choose>
    <c:when test="${study.studyParameterConfig.eventLocationRequired == 'required'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top"><div class="formfieldXL_BG">
                       <input type="text" name="location"size="50" value="<c:out value="${location}"/>" class="formfieldXL">
                    </div></td>
                    <td>*</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td colspan="2"><jsp:include page="showMessage.jsp"><jsp:param name="key" value="location"/></jsp:include></td>
    </tr>

    </c:when>
    <c:when test="${study.studyParameterConfig.eventLocationRequired == 'optional'}">
    <tr valign="top">
        <td class="formlabel"><fmt:message key="location" bundle="${resword}"/>:</td>
        <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td valign="top"><div class="formfieldXL_BG">
                       <input type="text" name="location"size="50" class="formfieldXL">
                    </div></td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
    </c:when>
    <c:otherwise>
        <input type="hidden" name="location" value=""/>
    </c:otherwise>
    </c:choose>
    <tr>
        <td colspan="2" align="center">
        <input type="submit" name="addSubject" value="<fmt:message key="add2" bundle="${resword}"/>" class="button" />
        &nbsp;
        <input type="button" id="cancel" name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>" class="button"/>

        <div id="dvForCalander" style="width:1px; height:1px;"></div>
    </td>
    </tr>

</table>

</div>

</form>
