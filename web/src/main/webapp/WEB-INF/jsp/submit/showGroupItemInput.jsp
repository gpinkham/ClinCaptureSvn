    <%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<c:set var="dateTimeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
 
<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope="request" id="responseOptionBean" class="org.akaza.openclinica.bean.submit.ResponseOptionBean" />
<jsp:useBean scope="request" id="formMessages" class="java.util.HashMap"/>
<script type="text/JavaScript" language="JavaScript" src="includes/instant_onchange.js?r=${revisionNumber}"></script>
<!-- *JSP* submit/showGroupItemInput.jsp -->
<%-- Some javascript functions for handling file data type -- ywang Dec.,2008 --%>
<script lang="Javascript">
function changeImage(name) {
	turnOnIsDataChangedParamOfDN(name);
	setImageWithTitle('DataStatus_top','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>'); 
	setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', '<fmt:message key="changed_not_saved" bundle="${restext}"/>');
	$('input[name=submittedExit]').removeClass("medium_back").addClass('medium_cancel').val('<fmt:message key="cancel" bundle="${resword}"/>');
    if(typeof formChanged != "undefined") {
		formChanged = true;
	}
}
</script>

<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="functionType" value="${displayItem.metadata.responseSet.options[0].value}"/>
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="respLayout" value="${displayItem.metadata.responseLayout}" />
<c:set var="numOfDate" value="${param.key}" />
<c:set var="isLast" value="${param.isLast}" />
<c:set var="isFirst" value="${param.isFirst}" />
<c:set var="repeatParentId" value="${param.repeatParentId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${repeatParentId}_[${repeatParentId}]input${itemId}" />
<c:set var="parsedInputName" value="${repeatParentId}_${rowCount}input${itemId}" />
<c:set var="isHorizontal" value="${param.isHorizontal}" />
<c:set var="defValue" value="${param.defaultValue}" />

<%-- What is the including JSP (e.g., doubleDataEntry)--%>
<c:set var="originJSP" value="${param.originJSP}" />
<c:set var="hasDataFlag" value="${hasDataFlag}" />
<c:set var="ddeEntered" value="${requestScope['ddeEntered']}" />
<!-- for the rows in model, input name processed by back-end servlet, needs to change them back to the name got from form, so we can show error frame around the input -->
<c:set var="isTemplateRow" value="${param.isTemplateRow}" />
<c:set var="orderForDN" value="${rowCount}" />
<c:set var="errorInputName" value="${repeatParentId}_${rowCount}input${itemId}" />

<c:if test="${rowCount > 0}">
    <c:set var="errorInputName" value="${repeatParentId}_manual${rowCount}input${itemId}" />
</c:if>

<c:if test="${!isLast && rowCount == 0}">
    <c:set var="inputName" value="${repeatParentId}_${rowCount}input${itemId}" />
</c:if>

<c:if test="${!isLast && rowCount > 0}">
    <c:set var="inputName" value="${repeatParentId}_manual${rowCount}input${itemId}" />
    <c:set var="parsedInputName" value="${repeatParentId}_manual${rowCount}input${itemId}" />
</c:if>

<c:set var="isLocked" value="${param.isLocked}" />

<!--  is a data's value is blank, so monitor can enter discrepancy note -->
<c:set var="isBlank" value="0" />

<%-- for tab index. must start from 1, not 0--%>

<c:set var="tabNum" value="${param.tabNum}" />

 <c:if test="${empty displayItem.data.value}">
        <c:set var="isBlank" value="1" />
 </c:if>

<%-- text input value--%>
<c:choose>
  <c:when test="${isTemplateRow || section.section.processDefaultValues}"><c:set var="inputTxtValue" value="${defValue}"/></c:when>
  <c:otherwise><c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/></c:otherwise>
</c:choose>

<c:forEach var="frmMsg" items="${formMessages}">
  <c:if test="${frmMsg.key eq errorInputName}">
    <c:set var="isInError" value="${true}" />
    <c:set var="errorTxtMessage" value="${frmMsg.value}" />
    <c:set var="errorTxtMessage" value='<%= StringEscapeUtils.escapeJavaScript(pageContext.getAttribute("errorTxtMessage").toString()) %>' />
  </c:if>
</c:forEach>

<c:if test="${isTemplateRow == true}">
 <c:set var="isInError" value="${false}" />
 </c:if>

 <c:if test="${isInError}">
      <c:set var="errorFlag" value="1"/><!--  use in discrepancy note-->
 </c:if>

<c:if test="${displayItem.item.dataType.id == 13}">
	<c:set var="inputType" value="divider"/>
	<%-- we don't support dividers inside repeating groups --%>
</c:if>

<c:if test="${displayItem.item.dataType.id == 14}">
	<c:set var="inputType" value="label"/>
	<div class="tableLabelHolder">
		<select class="tableLabelSelect hidden">
			<c:forEach items="${displayItem.tableLabels}" var="tableLabel">
				<option>${tableLabel}</option>
			</c:forEach>
		</select>
	</div>
</c:if>

<c:if test='${inputType=="file"}'>
	<label for="<c:out value="${inputName}"/>"></label>
	<c:set var="pathAndName" value="${displayItem.data.value}"/>
	<c:choose>
	<c:when test="${inputTxtValue==null || empty inputTxtValue}">
		<div id="div<c:out value="${inputName}"/>" name="myDiv">
		<c:choose>
    	<c:when test="${isInError}">
      		<span class="aka_exclaim_error">! </span><input class="aka_input_error" type="text" id="ft<c:out value="${inputName}"/>" name="fileText<c:out value="${inputName}"/>" disabled class="disabled">
		</c:when>
		<c:otherwise>
			<input type="text" id="ft<c:out value="${inputName}"/>" name="fileText<c:out value="${inputName}"/>" disabled class="disabled">
		</c:otherwise>
		</c:choose>
			<input type="button" id="up<c:out value="${inputName}"/>" name="uploadFile<c:out value="${inputName}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>"
				   onClick="javascript:openFileWindow('UploadFile?submitted=no&itemId=<c:out value="${itemId}"/>&inputName=<c:out value="${inputName}"/>'); changeImage('${inputName}');">
			<input type="hidden" id="fa<c:out value="${inputName}"/>" name="fileAction<c:out value="${inputName}"/>" value="upload">
		</div>
		<input type="hidden" id="<c:out value="${inputName}"/>" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>">
	</c:when>
	<c:otherwise>
		<div id="div<c:out value="${inputName}"/>" name="myDiv">
		<c:if test="${isInError}">
      		<span class="<c:out value="aka_exclaim_error"/>">! </span>
      	</c:if>
		<c:choose>
		<c:when test="${fn:contains(inputTxtValue, 'fileNotFound#')}">
			<c:set var="inputTxtValue" value="${fn:substringAfter(inputTxtValue,'fileNotFound#')}"/>
			<del id="a<c:out value="${inputName}"/>"><c:out value="${inputTxtValue}"/></del>
			<input type="text" id="hidft<c:out value="${inputName}"/>" name="fileText<c:out value="${inputName}"/>" class="hidden">
			<input type="button" id="hidup<c:out value="${inputName}"/>" name="uploadFile<c:out value="${inputName}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>"
				   onClick="javascript:openFileWindow('UploadFile?submitted=no&itemId=<c:out value="${itemId}"/>&inputName=<c:out value="${inputName}"/>')" class="hidden">
		</div><br>
		<input id="rp<c:out value="${inputName}"/>" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="replace" bundle="${resword}"/>" onClick="replaceSwitch(this, '${section.eventCRF.id}', '${inputName}', '${inputTxtValue}', 'notFound', true); changeImage('${inputName}');">
		<input id="rm<c:out value="${inputName}"/>" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="remove" bundle="${resword}"/>" onClick="removeSwitch(this, '${section.eventCRF.id}','${inputName}', '${inputTxtValue}', 'notFound'); changeImage('${inputName}');">
		</c:when>
		<c:otherwise>
			<c:set var="prefilename" value="${displayItem.data.value}"/>
			<a href="DownloadAttachedFile?eventCRFId=<c:out value="${section.eventCRF.id}"/>&fileName=<c:out value="${fn:replace(prefilename,'+','%2B')}"/>" id="a<c:out value="${inputName}"/>"><c:out value="${inputTxtValue}"/></a>
			<input type="text" id="hidft<c:out value="${inputName}"/>" name="fileText<c:out value="${inputName}"/>" class="hidden">
			<input type="button" id="hidup<c:out value="${inputName}"/>" name="uploadFile<c:out value="${inputName}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>"
				   onClick="javascript:openFileWindow('UploadFile?submitted=no&itemId=<c:out value="${itemId}"/>&inputName=<c:out value="${inputName}"/>')" class="hidden">
		</div><br>
		<input id="rp<c:out value="${inputName}"/>" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="replace" bundle="${resword}"/>" onClick="replaceSwitch(this, '${section.eventCRF.id}','${inputName}', '${inputTxtValue}', 'found', true); changeImage('${inputName}');">
		<input id="rm<c:out value="${inputName}"/>" filePathName="${fn:replace(pathAndName,'+','%2B')}" type="button" value="<fmt:message key="remove" bundle="${resword}"/>" onClick="removeSwitch(this, '${section.eventCRF.id}','${inputName}', '${inputTxtValue}', 'found'); changeImage('${inputName}');">
		</c:otherwise>
		</c:choose>
		<input type="hidden" id="<c:out value="${inputName}"/>" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>">
		<input type="hidden" id="fa<c:out value="${inputName}"/>" name="fileAction<c:out value="${inputName}"/>" value="noAction">
	</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${inputType == "instant-calculation"}'>
    <label for="<c:out value="${inputName}"/>"></label>
    <input type="hidden" id="<c:out value="${inputName}"/>" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" >
    <c:choose>
        <c:when test="${isInError && !hasShown}">
            <span class="aka_exclaim_error">! </span><input class="aka_input_error"  id="show<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
                "this.className='changedField'; manualChange('<c:out value="${inputName}"/>'); changeImage('${inputName}');"
                                                            type="text" name="show<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
        </c:when>
        <c:otherwise>
            <input id="show<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
                    "this.className='changedField'; manualChange('<c:out value="${inputName}"/>'); changeImage('${inputName}');"
                   type="text" name="show<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test='${inputType == "text"}'>
  <%-- <c:out value="txt item"/> --%>
  <%-- add for error messages --%>
  <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span>
      <input datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
             autotabbing="" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" type="text" name="<c:out value="${inputName}"/>" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" default="${inputTxtValue}"/>
    </c:when>
    <c:otherwise>
      <input datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
             autotabbing="" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" type="text" name="<c:out value="${inputName}"/>" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" default="${inputTxtValue}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${displayItem.item.itemDataTypeId==9 || displayItem.item.itemDataTypeId==10}"><!-- date type-->
	  <ui:calendarIcon onClickSelector="getSib(this.previousSibling)" linkName="anchor${inputName}" linkId="anchor${inputName}" checkIfShowYear="true"/>
    <c:set var="numOfDate" value="${numOfDate+1}"/>
  </c:if>
</c:if>
<c:if test='${inputType == "textarea"}'>
  <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span>
      <textarea datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
                autotabbing="" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" name="<c:out value="${inputName}"/>" rows="5" cols="40" default="${inputTxtValue}"><c:out value="${inputTxtValue}"/></textarea>
    </c:when>
    <c:otherwise>
      <textarea datatype="${displayItem.item.dataType.name}" maxlength="${displayItem.maxLength}" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
                autotabbing="" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />');  changeImage('${inputName}');" name="<c:out value="${inputName}"/>" rows="5" cols="40" default="${inputTxtValue}"><c:out value="${inputTxtValue}"/></textarea>
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test='${inputType == "checkbox"}'>
  <c:if test="${! isHorizontal}">
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
      <c:set var="checked" value="" />
      <c:set var="needsToBeChecked" value=""/>
      <c:choose>
        <c:when test="${isTemplateRow || section.section.processDefaultValues}">
          <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
            <c:if test="${(option.text eq _item) || (option.value eq _item)}"><c:set var="checked" value="checked" /></c:if>
          </c:forTokens>
          <c:set var="needsToBeChecked" value="${!(empty checked) ? 'needsToBeChecked' : ''}"/>
        </c:when>
        <c:otherwise><c:if test="${option.selected}"><c:set var="checked" value="checked" /></c:if></c:otherwise>
      </c:choose>
      <label for="<c:out value="${inputName}"/>"></label>
      <c:choose>
        <c:when test="${isInError}">
          <span class="aka_exclaim_error">! </span>
          <input ${needsToBeChecked} class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
                 onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); setImage('DataStatus_top','images/icon_UnsavedData.gif'); changeImage('${inputName}');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:when>
        <c:otherwise>
          <input ${needsToBeChecked} id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
                 onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); setImage('DataStatus_top','images/icon_UnsavedData.gif'); changeImage('${inputName}');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <c:set var="checked" value="" />
    <c:set var="needsToBeChecked" value=""/>
    <c:choose>
      <c:when test="${isTemplateRow || section.section.processDefaultValues}">
        <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
          <c:if test="${(responseOptionBean.text eq _item) || (responseOptionBean.value eq _item)}"><c:set var="checked" value="checked" /></c:if>
        </c:forTokens>
        <c:set var="needsToBeChecked" value="${!(empty checked) ? 'needsToBeChecked' : ''}"/>
      </c:when>
      <c:otherwise><c:if test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:if></c:otherwise>
    </c:choose>
    <c:set var="needsToBeChecked" value="${isTemplateRow && !(empty checked) ? 'needsToBeChecked' : ''}"/>
    <label for="<c:out value="${inputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span>
        <input ${needsToBeChecked} class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
               onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); setImage('DataStatus_top','images/icon_UnsavedData.gif'); changeImage('${inputName}');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input ${needsToBeChecked} id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
               onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); setImage('DataStatus_top','images/icon_UnsavedData.gif'); changeImage('${inputName}');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:otherwise>
    </c:choose>

  </c:if>
</c:if>
<c:if test='${inputType == "radio"}'>
  <c:if test="${! isHorizontal}">
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
      <c:choose>
        <c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
        <c:otherwise><c:set var="checked" value="" /></c:otherwise>
      </c:choose>
      <label for="<c:out value="${inputName}"/>"></label>
      <c:choose>
        <c:when test="${isInError}">
          <!-- this.className='changedField';-->
          <span class="aka_exclaim_error">! </span>
          <input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');} radioButtonOnClick(event);"
                 onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /><c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:when>
        <c:otherwise>
          <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}"
                 onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');} radioButtonOnClick(event);" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <c:choose>
      <c:when test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <%-- Only have one of these per radio button--%>
    <label for="<c:out value="${inputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span>
        <input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');} radioButtonOnClick(event);"
               onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');} radioButtonOnClick(event);"
               onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';}; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:otherwise>
    </c:choose>
  </c:if>
</c:if>

<c:if test='${inputType == "single-select"}'>
    <label for="${inputName}"></label>
    <c:if test="${isInError}">
        <span class="aka_exclaim_error">! </span>
    </c:if>
    <c:set var="optionWasSelected" value="false"/>
    <c:set var="defaultValueInOptions" value="false"/>
    <c:set var="selectDefault" value="${(isTemplateRow || section.section.processDefaultValues) && displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}"/>
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
        <c:if test="${option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue}">
            <c:set var="defaultValueInOptions" value="true"/>
        </c:if>
    </c:forEach>
    <select id="${inputName}" tabindex="${tabNum}" name="${inputName}"
            class="${isInError ? 'aka_input_error' : 'formfield'}"
            onChange="this.className='changedField'; sameRepGrpInstant('${inputName}', '${itemId}',
                    '${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}',
                    '${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}');
                    changeImage('${inputName}');">
        <c:if test="${!defaultValueInOptions and displayItem.metadata.defaultValue != null and displayItem.metadata.defaultValue != ''}">
            <c:set var="optionWasSelected" value="${selectDefault}"/>
            <option value="" ${selectDefault ? 'selected' : ''}>${displayItem.metadata.defaultValue}</option>
        </c:if>
        <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
            <option value="${option.value}" ${!optionWasSelected && ((selectDefault && (option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue)) || (option.selected && !isTemplateRow)) ? 'selected' : ''}>${option.text}</option>
        </c:forEach>
    </select>
</c:if>


<c:if test='${inputType == "multi-select"}'>
  <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><select class="aka_input_error" id="<c:out value="${inputName}"/>" multiple  tabindex=
      "${tabNum}" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');">
    </c:when>
    <c:otherwise>
      <select id="<c:out value="${inputName}"/>" multiple  tabindex=
      "${tabNum}" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; sameRepGrpInstant('<c:out value="${inputName}"/>', '<c:out value="${itemId}"/>', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStr}" />', '<c:out value="${displayItem.instantFrontStrGroup.sameRepGrpFrontStr.frontStrDelimiter.code}" />'); changeImage('${inputName}');">
    </c:otherwise>
  </c:choose>
  <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
    <c:set var="checked" value="" />
    <c:choose>
      <c:when test="${isTemplateRow || section.section.processDefaultValues}">
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
	<input type="hidden" name="input<c:out value="${itemId}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
	<label for="<c:out value="${inputName}"/>"></label>
	<c:choose>
		<c:when test="${isInError}">
			<span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; changeImage('${inputName}');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
		</c:when>
		<c:otherwise>
			<input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
				"this.className='changedField'; changeImage('${inputName}');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
    	</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${displayItem.metadata.required}">
  <span class="alert">*</span>
</c:if>
<c:if test="${study.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
    <c:choose>
    <c:when test="${displayItem.discrepancyNoteStatus == 0}">
        <c:set var="imageFileName" value="icon_noNote" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 1}">
        <c:set var="imageFileName" value="icon_Note" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 2}">
        <c:set var="imageFileName" value="icon_flagYellow" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 3}">
        <c:set var="imageFileName" value="icon_flagBlack" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 4}">
        <c:set var="imageFileName" value="icon_flagGreen" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 5}">
        <c:set var="imageFileName" value="icon_flagWhite" />
    </c:when>
    <c:otherwise>
    </c:otherwise>
  </c:choose>

	<c:choose>
		<c:when test="${originJSP eq 'initialDataEntry'}">
			<c:set var="writeToDB" value="0"/>
			<c:set var="dataId" value="0"/>
		</c:when>
		<c:otherwise>
			<c:set var="writeToDB" value="1"/>
			<c:set var="dataId" value="${displayItem.data.id}"/>
		</c:otherwise>
	</c:choose>

    <c:import url="../submit/crfShortcutAnchors.jsp">
        <c:param name="itemId" value="${itemId}" />
        <c:param name="rowCount" value="${rowCount}"/>
        <c:param name="inputName" value="${inputName}"/>
    </c:import>

	<c:choose>
		<c:when test="${displayItem.item.dataType.id eq 13 or displayItem.item.dataType.id eq 14}"></c:when>
		<c:when test="${displayItem.numDiscrepancyNotes > 0}">
			<a class="dnLink"
						tabindex="<c:out value="${tabNum + 1000}"/>" href="#"   onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip();"
						onClick="openDNoteWindow('ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>&originJSP=<c:out value="${param.originJSP}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
				<img id="flag_<c:out value="${inputName}"/>" name="flag_<c:out value="${inputName}" />"
						src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
				<input type="hidden" value="ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>&originJSP=<c:out value="${param.originJSP}"/>"/>
			</a>
		</c:when>
		<c:otherwise>
			<c:if test="${(isLocked == null) || (isLocked eq 'no')}">
				<c:set var="imageFileName" value="icon_noNote" />
				<c:set var="eventName" value="${toc.studyEventDefinition.name}"/>
				<c:set var="eventDate">
					<cc-fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dateTimeFormat}"/>
				</c:set>
				<c:set var="crfName" value="${toc.crf.name} ${toc.crfVersion.name}"/>
				<a class="dnLink"
							tabindex="<c:out value="${tabNum + 1000}"/>" href="#"  onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip();"
							onClick="openDNWindow('CreateDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&groupOid=<c:out value="${repeatParentId}"/>&sectionId=<c:out value="${displayItem.metadata.sectionId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=${writeToDB}&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>&eventName=${eventName}&eventDate=${eventDate}&crfName=${crfName}&originJSP=<c:out value="${param.originJSP}"/>&enterData=1','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
					<img id="flag_<c:out value="${inputName}"/>" name="flag_<c:out value="${inputName}"/>"
							src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
					<input type="hidden" value="ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${dataId}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>&originJSP=<c:out value="${param.originJSP}"/>&writeToDB=${writeToDB}"/>
				</a>
			</c:if>
		</c:otherwise>
	</c:choose>

	<c:import url="../submit/itemSDV.jsp">
        <c:param name="itemId" value="${itemId}" />
        <c:param name="rowCount" value="${rowCount}"/>
        <c:param name="inputName" value="${inputName}"/>
    </c:import>

</c:if>

<c:if test='${inputType == "text"|| inputType == "textarea" ||
inputType == "multi-select" || inputType == "single-select" ||
inputType == "calculation" }'>
  <c:if test="${! (displayItem.item.units eq '')}">
    (<c:out value="${displayItem.item.units}"/>)
  </c:if>
</c:if>
<c:if test='${inputType == "radio"|| inputType == "checkbox"}'>
  <c:if test="${! isHorizontal}">
    <c:if test="${! (displayItem.item.units eq '')}">
      (<c:out value="${displayItem.item.units}"/>)
    </c:if>
  </c:if>
</c:if>
