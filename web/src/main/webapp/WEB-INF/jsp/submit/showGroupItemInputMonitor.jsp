<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope="request" id="responseOptionBean" class="org.akaza.openclinica.bean.submit.ResponseOptionBean" />
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>
<!-- *JSP* submit/showGroupItemInputMonitor.jsp -->
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="numOfDate" value="${param.key}" />
<c:set var="isLast" value="${param.isLast}" />
<c:set var="isFirst" value="${param.isFirst}" />
<c:set var="repeatParentId" value="${param.repeatParentId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${repeatParentId}_[${repeatParentId}]input${itemId}" />
<c:set var="parsedInputName" value="${repeatParentId}_${rowCount}input${itemId}" />
<c:set var="isHorizontal" value="${param.isHorizontal}" />
<c:set var="defValue" value="${param.defaultValue}" />
<c:set var="totNew" value="${displayItem.totNew}"/>
<c:set var="totUpdated" value="${displayItem.totUpdated}"/>
<c:set var="totRes" value="${displayItem.totRes}"/>
<c:set var="totClosed" value="${displayItem.totClosed}"/>
<c:set var="totNA" value="${displayItem.totNA}"/>
<%-- What is the including JSP (e.g., doubleDataEntry)--%>
<c:set var="originJSP" value="${param.originJSP}" />
<c:set var="hasDataFlag" value="${hasDataFlag}" />
<c:set var="ddeEntered" value="${requestScope['ddeEntered']}" />
<!-- for the rows in model, input name processed by back-end servlet, needs to change them back to the name got from form, so we can show error frame around the input -->
<c:set var="orderForDN" value="${rowCount}" />
<c:set var="errorInputName" value="${repeatParentId}_${rowCount}input${itemId}" />

<c:if test="${!isLast}">
    <c:set var="inputName" value="${repeatParentId}_${rowCount}input${itemId}" />
</c:if>

<c:set var="isLocked" value="${param.isLocked}" />

<!--  is a data's value is blank, so monitor can enter discrepancy note -->
<c:set var="isBlank" value="0" />

<%-- for tab index. must start from 1, not 0--%>
<c:set var="tabNum" value="${param.tabNum}" />

<c:set var="respLayout" value="${displayItem.metadata.responseLayout}" />

 <c:if test="${empty displayItem.data.value}">
        <c:set var="isBlank" value="1" />
 </c:if>

<%-- text input value--%>
<c:choose>
  <c:when test="${section.section.processDefaultValues}"><c:set var="inputTxtValue" value="${defValue}"/></c:when>
  <c:otherwise><c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/></c:otherwise>
</c:choose>

<c:forEach var="frmMsg" items="${formMessages}">
  <c:if test="${frmMsg.key eq errorInputName}">
    <c:set var="isInError" value="${true}" />
    <c:set var="errorTxtMessage" value="${frmMsg.value}" />
    <c:set var="errorTxtMessage" value='<%= StringEscapeUtils.escapeJavaScript(pageContext.getAttribute("errorTxtMessage").toString()) %>' />
  </c:if>
</c:forEach>

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
	<c:choose>
	<c:when test="${empty displayItem.data.value}">
		<input type="text" id="ft<c:out value="${inputName}"/>" name="fileText<c:out value="${inputName}"/>" value="">
		<input type="button" id="up<c:out value="${inputName}"/>" name="uploadFile<c:out value="${inputName}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>">
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${fn:contains(inputTxtValue, 'fileNotFound#')}">
			<del><c:out value="${fn:substringAfter(inputTxtValue,'fileNotFound#')}"/></del>
		</c:when>
		<c:otherwise>
			<c:set var="filename" value="${displayItem.data.value}"/>
			<c:set var="sep" value="\\"/>
            <c:set var="sep2" value="\\\\"/>
			<a href="DownloadAttachedFile?eventCRFId=<c:out value="${section.eventCRF.id}"/>&fileName=${fn:replace(fn:replace(filename,'+','%2B'),sep,sep2)}" id="a<c:out value="${itemId}"/>"><c:out value="${inputTxtValue}"/></a>
		</c:otherwise>
		</c:choose>
	</c:otherwise>
	</c:choose>
</c:if>
<c:if test='${inputType == "instant-calculation"}'>
    <%-- add for error messages --%>
    <label for="<c:out value="${inputName}"/>"></label>
    <c:choose>
        <c:when test="${isInError}">
            <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
        </c:when>
        <c:otherwise>
            <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
                    "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test='${inputType == "text"}'>
  <%-- <c:out value="txt item"/> --%>
  <%-- add for error messages --%>
  <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" value="<c:out value="${inputTxtValue}"/>" />
    </c:when>
    <c:otherwise>
      <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
        "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="<c:out value="${inputName}"/>" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
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
      <span class="aka_exclaim_error">! </span><textarea class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:when>
    <c:otherwise>
      <textarea id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="<c:out value="${inputName}"/>" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test='${inputType == "checkbox"}'>
  <c:if test="${! isHorizontal}">
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
      <label for="<c:out value="${inputName}"/>"></label>
      <c:choose>
        <c:when test="${isInError}">
          <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:when>
        <c:otherwise>
          <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <c:set var="checked" value="" />
    <c:choose>
      <c:when test="${section.section.processDefaultValues}">
        <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
          <c:if test="${(responseOptionBean.text eq _item) || (responseOptionBean.value eq _item)}"><c:set var="checked" value="checked" /></c:if>
        </c:forTokens>
      </c:when>
      <c:otherwise><c:if test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:if></c:otherwise>
    </c:choose>
    <label for="<c:out value="${inputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
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
          <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');} radioButtonOnClick(event);" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /><c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:when>
        <c:otherwise>
          <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'vertical');} radioButtonOnClick(event);" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:if test="${! isHorizontal}"><c:out value="${option.text}" /></c:if> <br/>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </c:if>
  <c:if test="${isHorizontal}">
    <c:choose>
      <c:when test="${responseOptionBean.selected}"><c:set var="checked" value="checked" /></c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <label for="<c:out value="${inputName}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');} radioButtonOnClick(event);" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
      </c:when>
      <c:otherwise>
        <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onclick="if(detectIEWindows(navigator.userAgent)){this.checked=true; unCheckSiblings(this,'horizontal');} radioButtonOnClick(event);" onChange="if(! detectIEWindows(navigator.userAgent)){this.className='changedField';} javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" onmouseup="radioButtonOnMouseUp(event);" type="radio" name="<c:out value="${inputName}"/>" value="<c:out value="${responseOptionBean.value}" />" <c:out value="${checked}"/> />
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
  <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><select class="aka_input_error" id="<c:out value="${inputName}"/>" multiple  tabindex=
      "${tabNum}" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
    </c:when>
    <c:otherwise>
      <select tabbed rowcount="${rowCount}" id="<c:out value="${inputName}"/>" multiple  tabindex=
      "${tabNum}" name="<c:out value="${inputName}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
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
	<input type="hidden" name="input<c:out value="${itemId}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
 <label for="<c:out value="${inputName}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
    </c:when>
    <c:otherwise>
      <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
        "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
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
    <c:when test="${displayItem.discrepancyNoteStatus == 1 || displayItem.discrepancyNoteStatus == 6}">
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

  <c:import url="../submit/crfShortcutAnchors.jsp">
      <c:param name="itemId" value="${itemId}" />
      <c:param name="rowCount" value="${rowCount}"/>
      <c:param name="inputName" value="${inputName}"/>
  </c:import>

  <c:choose>
    <c:when test="${displayItem.item.dataType.id eq 13 or displayItem.item.dataType.id eq 14}"></c:when>
    <c:when test="${displayItem.numDiscrepancyNotes > 0}">
        <a class="dnLink"
           tabindex="<c:out value="${tabNum + 1000}"/>" href="#" onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
           onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&errorFlag=<c:out value="${errorFlag}"/>&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
            <img id="flag_<c:out value="${inputName}"/>" name="flag_<c:out value="${inputName}" />"
                 src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
            <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&errorFlag=<c:out value="${errorFlag}"/>&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>"/>
        </a>

    </c:when>
    <c:otherwise>
    <c:set var="notLocked" value="no"/>
     <c:if test="${(isLocked eq notLocked) && (displayItem.data.id > 0)}">
      <c:set var="imageFileName" value="icon_noNote" />

         <a class="dnLink"
            tabindex="<c:out value="${tabNum + 1000}"/>" href="#"  onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
            onClick="openDNWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&groupOid=<c:out value="${repeatParentId}"/>&sectionId=<c:out value="${displayItem.metadata.sectionId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&errorFlag=<c:out value="${errorFlag}"/>&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
             <img id="flag_<c:out value="${inputName}"/>" name="flag_<c:out value="${inputName}"/>"
                  src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
             <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?eventCRFId=<c:out value="${section.eventCRF.id}"/>&stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&errorFlag=<c:out value="${errorFlag}"/>&isLocked=<c:out value="${isLocked}"/>&order=<c:out value="${orderForDN}"/>"/>
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
