<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<c:set var="dateTimeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope="request" id="formMessages" class="java.util.HashMap"/>
<!-- *JSP* submit/showItemInput.jsp -->
<style type="text/css">

	.tooltip {

		width:100%;

	}

</style>

<c:set var="repeatParentId" value="${param.repeatParentId}" scope="request"/>
<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" scope="request"/>
<c:set var="functionType" value="${displayItem.metadata.responseSet.options[0].value}"/>
<c:set var="itemId" value="${displayItem.item.id}" scope="request"/>
<c:set var="itemName" value="${displayItem.item.name}" scope="request"/>
<c:set var="inputName" value="input${itemId}" scope="request"/>
<c:set var="inputVal" value="input${itemId}" scope="request"/>
<c:set var="numOfDate" value="${param.key}" scope="request"/>
<c:set var="defValue" value="${param.defaultValue}" scope="request"/>
<c:set var="respLayout" value="${param.respLayout}" scope="request"/>
<%-- What is the including JSP (e.g., doubleDataEntry)--%>
<c:set var="originJSP" value="${param.originJSP}" scope="request"/>

<c:set var="totNew" value="${displayItem.totNew}" scope="request"/>
<c:set var="totUpdated" value="${displayItem.totUpdated}" scope="request"/>
<c:set var="totRes" value="${displayItem.totRes}" scope="request"/>
<c:set var="totClosed" value="${displayItem.totClosed}" scope="request"/>
<c:set var="totNA" value="${displayItem.totNA}" scope="request"/>
<%-- A boolean request attribute set in DataEntryServlet...--%>
<c:set var="hasDataFlag" value="${hasDataFlag}" scope="request"/>
<c:set var="ddeEntered" value="${requestScope['ddeEntered']}" scope="request"/>

<c:if test="${(respLayout eq 'Horizontal' || respLayout eq 'horizontal')}">
	<c:set var="isHorizontal" value="${true}" scope="request"/>
</c:if>

<%-- text input value; the default value is not displayed if the application has data, or is
 not originating from doubleDataEntry--%>
<c:if test="${hasDataFlag == null || empty hasDataFlag}">
  <c:set var="hasDataFlag" value="${false}"/>
</c:if>

<c:choose>
  <c:when test="${section.section.processDefaultValues}"><c:set var="inputTxtValue" value="${defValue}"/></c:when>
  <c:otherwise><c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/></c:otherwise>
</c:choose>

<%-- for tab index. must start from 1, not 0--%>
<c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
<c:set var="tabNum" value="${crfTabIndex}" />

<%-- find out whether the item is involved with an error message, and if so, outline the
form element in red --%>


<c:forEach var="frmMsg" items="${formMessages}">
	<c:if test="${frmMsg.key eq inputVal}">
		<c:set var="isInError" value="${true}" />
		<c:set var="errorTxtMessage" value="${frmMsg.value}" />
		<c:set var="errorTxtMessage" value='<%= StringEscapeUtils.escapeJavaScript(pageContext.getAttribute("errorTxtMessage").toString()) %>' />
	</c:if>
</c:forEach>

<script type="text/JavaScript" language="JavaScript" src="includes/instant_onchange.js?r=${revisionNumber}"></script>

<script lang="Javascript">
	function changeImage(name) {
		turnOnIsDataChangedParamOfDN(name);
		setImageWithTitle('DataStatus_top','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');
		setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');
		$('input[name=submittedExit]').removeClass("medium_back").addClass('medium_cancel').val('<fmt:message key="cancel" bundle="${resword}"/>');
		if(typeof formChanged != "undefined"){
			formChanged = true;
		}
	}
</script>

<%-- A way to deal with the lack of 'break' out of forEach loop--%>

<c:choose>
	<c:when test="${hasShown}">
		<c:set var="exclaim" value="aka_exclaim_show"/>
		<c:set var="input" value="aka_input_show"/>

	</c:when>
	<c:otherwise>
		<c:set var="exclaim" value="aka_exclaim_error"/>
		<c:set var="input" value="aka_input_error"/>
	</c:otherwise>
</c:choose>

<c:if test="${displayItem.item.dataType.id == 13}">
	<c:set var="inputType" value="divider"/>
</c:if>

<c:if test="${displayItem.item.dataType.id == 14}">
	<c:set var="inputType" value="label"/>
</c:if>

<%-- adding here, tbh clinovo 10/18/2012 --%>
<div id="<c:out value='${itemName }'/>" style="display: ${isFSCRF ? 'inline-block' : ''}">
<%-- end addition --%>
<c:if test='${inputType=="file"}'>
	<label for="input<c:out value='${itemId}'/>"></label>
	<c:set var="pathAndName" value="${displayItem.data.value}"/>
	<c:choose>
		<c:when test="${inputTxtValue==null || empty inputTxtValue}">
			<input type="hidden" id="input<c:out value="${itemId}"/>" name="input<c:out value="${itemId}"/>" value="<c:out value="${inputTxtValue}"/>">
			<div id="div<c:out value="${itemId}"/>" name="myDiv">
				<c:choose>
					<c:when test="${isInError && !hasShown}">
						<span class="<c:out value="${exclaim}"/>">! </span>
						<input type="text" class="<c:out value="${input}"/>" id="ft<c:out value="${itemId}"/>" name="fileText<c:out value="${itemId}"/>" disabled>
					</c:when>
					<c:otherwise>
						<input type="text" id="ft<c:out value="${itemId}"/>" name="fileText<c:out value="${itemId}"/>" disabled>
					</c:otherwise>
				</c:choose>
				<input type="button" id="up<c:out value="${itemId}"/>" name="uploadFile<c:out value="${itemId}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>"
					   onClick="javascript:openFileWindow('UploadFile?submitted=no&itemId=<c:out value="${itemId}"/>'); changeImage('input${itemId}');">
				<input type="hidden" id="fa<c:out value="${itemId}"/>" name="fileAction<c:out value="${itemId}"/>" value="upload">
			</div>
		</c:when>
		<c:otherwise>
			<div id="div<c:out value="${itemId}"/>" name="myDiv">
			<c:if test="${isInError && !hasShown}">
				<span class="<c:out value="${exclaim}"/>">! </span>
			</c:if>
			<c:choose>
				<c:when test="${fn:contains(inputTxtValue, 'fileNotFound#')}">
					<c:set var="inputTxtValue" value="${fn:substringAfter(inputTxtValue,'fileNotFound#')}"/>
					<del id="a<c:out value="${itemId}"/>"><c:out value="${inputTxtValue}"/></del>
					</div><br>
					<input id="rp${itemId}" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="replace" bundle="${resword}"/>" onClick="replaceSwitch(this, '${section.eventCRF.id}','${itemId}','${inputTxtValue}','notFound');changeImage('input${itemId}');">
					<input id="rm${itemId}" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="remove" bundle="${resword}"/>" onClick="removeSwitch(this, '${section.eventCRF.id}','${itemId}','${inputTxtValue}','notFound');changeImage('input${itemId}');">
				</c:when>
				<c:otherwise>
					<c:set var="prefilename" value="${pathAndName}"/>
					<a href="DownloadAttachedFile?eventCRFId=<c:out value="${section.eventCRF.id}"/>&fileName=<c:out value="${fn:replace(prefilename,'+','%2B')}"/>" id="a<c:out value="${itemId}"/>"><c:out value="${inputTxtValue}"/></a>
					</div><br>
					<input id="rp${itemId}" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="replace" bundle="${resword}"/>" onClick="replaceSwitch(this, '${section.eventCRF.id}', '${itemId}', '${inputTxtValue}','found');changeImage('input${itemId}');">
					<input id="rm${itemId}" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="remove" bundle="${resword}"/>" onClick="removeSwitch(this, '${section.eventCRF.id}', '${itemId}', '${inputTxtValue}','found');changeImage('input${itemId}');">
				</c:otherwise>
			</c:choose>
			<input type="hidden" id="input<c:out value="${itemId}"/>" name="input<c:out value="${itemId}"/>" value="<c:out value="${inputTxtValue}"/>">
			<input type="hidden" id="fa<c:out value="${itemId}"/>" name="fileAction<c:out value="${itemId}"/>" value="noAction">
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${inputType == "instant-calculation"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<input type="hidden" id="input<c:out value="${itemId}"/>" name="input<c:out value="${itemId}"/>" value="<c:out value="${inputTxtValue}"/>" >
	<c:choose>
		<c:when test="${isInError && !hasShown}">
			<span class="<c:out value="${exclaim}"/>">! </span><input class="<c:out value="${input}"/>" id="showinput<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
				"this.className='changedField'; manualChange('input<c:out value="${itemId}"/>'); changeImage('input${itemId}');"
																	  type="text" name="showinput<c:out value="${itemId}" />" value="<c:out value="${inputTxtValue}"/>" />
		</c:when>
		<c:otherwise>
			<input id="showinput<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
					"this.className='changedField'; manualChange('input<c:out value="${itemId}"/>'); changeImage('input${itemId}');"
				   type="text" name="showinput<c:out value="${itemId}" />" value="<c:out value="${inputTxtValue}"/>" />
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${inputType == "text"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<c:choose>
		<c:when test="${isInError && !hasShown}">
			<span class="<c:out value="${exclaim}"/>">! </span>
			<input datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
				   autotabbing="" onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="text" name="input<c:out value="${itemId}" />" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
		</c:when>
		<c:otherwise>
			<input datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
				   autotabbing="" onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="text" name="input<c:out value="${itemId}" />" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
		</c:otherwise>
	</c:choose>
	<c:if test="${displayItem.item.itemDataTypeId==9 || displayItem.item.itemDataTypeId==10}"><!-- date type-->
		<ui:calendarIcon onClickSelector="getSib(this.previousSibling)" linkName="anchor${itemId}" linkId="anchor${itemId}" imageId="anchor${itemId}" checkIfShowYear="true"/>
		<c:set var="numOfDate" value="${numOfDate+1}"/>
	</c:if>
</c:if>
<c:if test='${inputType == "textarea"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<c:choose>
		<c:when test="${isInError && !hasShown}">
			<span class="<c:out value="${exclaim}"/>">! </span>
      <textarea datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
				autotabbing="" onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" name="input<c:out value="${itemId}" />" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
		</c:when>
		<c:otherwise>
      <textarea datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
				autotabbing="" onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" name="input<c:out value="${itemId}" />" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${inputType == "checkbox"}'>
	<%-- What if the defaultValue is a comma- or space-separated value for
	 multiple checkboxes or multi-select tags? --%>
	<c:set var="allChecked" value=""/>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:set var="checked" value="" />
		<c:choose>
		  <c:when test="${section.section.processDefaultValues}">
			<c:forTokens items="${inputTxtValue}" delims=","  var="_item">
			  <c:if test="${(option.text eq _item) || (option.value eq _item)}"><c:set var="checked" value="checked" /></c:if>
		    </c:forTokens>
		  </c:when>
		  <c:otherwise><c:if test="${option.selected}"><c:set var="checked" value="checked" /></c:if></c:otherwise>
		</c:choose>
		<label for="input<c:out value="${itemId}"/>"></label>
		<c:choose>
			<c:when test="${isInError && !hasShown}">
				<span class="<c:out value="${exclaim}"/>">! </span>
				<c:choose>
					<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
						<c:set var="scdPairStr" value=""/>
						<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
							<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
						</c:forEach>
						<input class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onClick="javascript:checkControlShow(this, '<c:out value="${scdPairStr}"/>');"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:when>
					<c:otherwise>
						<input class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
						<c:set var="scdPairStr" value=""/>
						<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
							<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
						</c:forEach>
						<input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onClick="javascript:checkControlShow(this, '<c:out value="${scdPairStr}"/>');"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:out value="${isChecked}"/> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:when>
					<c:otherwise>
						<input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</c:if>
<c:if test='${inputType == "radio"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:choose>
			<c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
			<c:otherwise><c:set var="checked" value="" /></c:otherwise>
		</c:choose>
		<label for="input<c:out value="${itemId}"/>"></label>
		<c:choose>
			<c:when test="${isInError && !hasShown}">
				<span class="<c:out value="${exclaim}"/>">! </span>
				<c:choose>
					<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
						<c:set var="scdPairStr" value=""/>
						<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
							<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
						</c:forEach>
						<input class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onClick="javascript:radioControlShow(this, '<c:out value="${scdPairStr}"/>');"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:when>
					<c:otherwise>
						<input class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
							   onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
						<c:set var="scdPairStr" value=""/>
						<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
							<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
						</c:forEach>
						<input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"  onClick="javascript:radioControlShow(this, '<c:out value="${scdPairStr}"/>');"
							   onChange="this.className='changedField'; javascript:destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:when>
					<c:otherwise>
						<input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}"
							   onChange="this.className='changedField'; javascript:destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</c:if>

<%-- adding some spacing to make this more readable, tbh --%>

<c:if test='${inputType == "single-select"}'>
	<label for="input${itemId}"></label>
	<c:if test="${isInError && !hasShown}">
		<span class="${exclaim}">! </span>
	</c:if>
	<c:set var="scdScript" value=""/>
	<c:set var="optionWasSelected" value="false"/>
	<c:set var="defaultValueInOptions" value="false"/>
	<c:set var="selectDefault" value="${section.section.processDefaultValues && displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}"/>
	<c:if test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
		<c:set var="scdPairStr" value=""/>
		<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
			<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
		</c:forEach>
		<c:set var="scdScript" value="selectControlShow(this, '${scdPairStr}');"/>
	</c:if>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:if test="${option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue}">
			<c:set var="defaultValueInOptions" value="true"/>
		</c:if>
	</c:forEach>
	<select class="${isInError ? 'aka_input_error' : 'formfield'}" id="input${itemId}" tabindex="${tabNum}"
			onChange="this.className='changedField'; destNonRepInstant('${itemId}', '${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}', '${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}'); ${scdScript} changeImage('input${itemId}');" name="input${itemId}">
		<c:if test="${!defaultValueInOptions}">
			<c:set var="optionWasSelected" value="${selectDefault}"/>
			<option value="" ${selectDefault ? 'selected' : ''}>${displayItem.metadata.defaultValue}</option>
		</c:if>
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
			<option value="${option.value}" ${!optionWasSelected && ((selectDefault && (option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue)) || option.selected) ? 'selected' : ''}>${option.text}</option>
		</c:forEach>
	</select>
</c:if>

<c:if test='${inputType == "multi-select"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<c:choose>
		<c:when test="${isInError && !hasShown}">
			<span class="<c:out value="${exclaim}"/>">! </span>
			<c:choose>
				<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
					<c:set var="scdPairStr" value=""/>
					<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
						<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
					</c:forEach>
					<select class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>"
					onChange="destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); javascript:selectControlShow(this, '<c:out value="${scdPairStr}"/>'); changeImage('input${itemId}');">
				</c:when>
				<c:otherwise>
					<select class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>"
					onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');">
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
					<c:set var="scdPairStr" value=""/>
					<c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
						<c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
					</c:forEach>
					<select id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>"
					onChange="destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); javascript:selectControlShow(this, '<c:out value="${scdPairStr}"/>'); this.className='changedField'; changeImage('input${itemId}');">
				</c:when>
				<c:otherwise>
					<select id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>"
					onChange="this.className='changedField'; destNonRepInstant('<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}" />'); changeImage('input${itemId}');">
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
		<c:set var="checked" value="" />
		<c:choose>
		  <c:when test="${section.section.processDefaultValues}">
		    <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
			  <c:if test="${(option.text eq _item) || (option.value eq _item)}"><c:set var="checked" value="selected" /></c:if>
			</c:forTokens>
		  </c:when>
		  <c:otherwise><c:if test="${option.selected}"><c:set var="checked" value="selected" /></c:if></c:otherwise>
		</c:choose>
		<option value="${option.value}" ${checked}>${option.text}</option>
	</c:forEach>
	</select>
</c:if>


<c:if test='${inputType == "calculation" || inputType == "group-calculation"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<input type="hidden" name="input<c:out value="${itemId}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
	<c:choose>
		<c:when test="${isInError && !hasShown}">
			<span class="<c:out value="${exclaim}"/>">! </span><input class="<c:out value="${input}"/>" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
				"this.className='changedField'; changeImage('input${itemId}');" type="text" class="disabled" disabled="disabled" name="input<c:out value="${itemId}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
		</c:when>
		<c:otherwise>
			<input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
					"this.className='changedField'; changeImage('input${itemId}');" type="text" class="disabled" disabled="disabled" name="input<c:out value="${itemId}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
		</c:otherwise>
	</c:choose>
</c:if>


</div>
<c:if test="${displayItem.metadata.required}">
	<c:choose>
		<c:when test="${isFSCRF}">
			<span class="alert">*</span>
		</c:when>
		<c:otherwise>
			<td valign="top"><span class="alert">*</span></td>
		</c:otherwise>
	</c:choose>
</c:if>
