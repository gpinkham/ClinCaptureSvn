<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<!-- *JSP* submit/showAnnotatedItemInput.jsp -->

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="inputName" value="input${itemId}" />
<c:set var="defValue" value="${param.defaultValue}" />
<c:set var="respLayout" value="${param.respLayout}" />
<c:set var="isBlank" value="0" />

<c:if test="${empty displayItem.data.value}">
        <c:set var="isBlank" value="1" />
</c:if>

<c:if test="${(respLayout eq 'Horizontal' || respLayout eq 'horizontal')}">
	<c:set var="isHorizontal" value="${true}" />
</c:if>

<c:choose>
	<c:when test="${empty displayItem.metadata.responseSet.value}">
		<c:set var="inputTxtValue" value="${defValue}"/>
	</c:when>
	<c:otherwise>
		<c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/>
	</c:otherwise>
</c:choose>

<c:if test='${inputType=="file"}'>
	<label for="input${itemId}"></label>
	<input type="text" id="ft${itemId}" name="fileText${itemId}" value="">
	<input type="button" id="up${inputName}" name="uploadFile${inputName}" value="<fmt:message key="click_to_upload" bundle="${resword}"/>">
</c:if>

<c:if test='${inputType == "instant-calculation"}'>
    <label for="input${itemId}"></label>
	<input id="input${itemId}" type="text" name="input<c:out value="${itemId}"/>" value="<c:out value="${inputTxtValue}"/>" />
</c:if>

<c:if test='${inputType == "text"}'>
	<label for="input${itemId}"></label>
	<input id="input${itemId}" type="text" name="input<c:out value="${itemId}" />" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
	<c:if test="${displayItem.item.itemDataTypeId==9 || displayItem.item.itemDataTypeId==10}">
		<img src="<c:out value="${contextPath}" />/images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0" ID="anchor${itemId}"/>
	</c:if>
</c:if>

<c:if test='${inputType == "textarea"}'>
	<label for="input${itemId}"></label>
	<textarea id="input${itemId}" name="input<c:out value="${itemId}"/>" rows="5" cols="40">
		<c:out value="${inputTxtValue}"/>
	</textarea>
</c:if>

<c:if test='${inputType == "checkbox"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}" varStatus="myIndex">
		<c:if test='${myIndex.first or (!myIndex.first and isHorizontal)}'>
			<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
		</c:if>
		<label for="input${itemId}"></label>
		<div class="annotation_text" id="a_div_input${itemId}_${myIndex.count}">
			<span class="annotation_text"><c:out value="${option.value}"/></span>
		</div>
		<input class="annotated_item" id="input${itemId}_${myIndex.count}" type="checkbox" name="input${itemId}" value="<c:out value="${option.value}" />"/> 
		<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
		<c:out value="${option.text}"/> 
		<c:if test="${!isHorizontal}">
			<br/>
		</c:if>
	</c:forEach>
</c:if>

<c:if test='${inputType == "radio"}'>
	<c:forEach var="option" items="${displayItem.metadata.responseSet.options}" varStatus="myIndex">
		<c:if test='${myIndex.first or (!myIndex.first and isHorizontal)}'>
			<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
		</c:if>
		<label for="input${itemId}"></label>
		<div class="annotation_text" id="a_div_input${itemId}_${myIndex.count}">
			<span class="annotation_text">
				<c:out value="${option.value}"/>
			</span>
		</div>
		<input class="annotated_item" id="input${itemId}_${myIndex.count}" type="radio" name="input${itemId}" value="<c:out value="${option.value}" />"/> 
		<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
		<c:out value="${option.text}"/> 
		<c:if test="${!isHorizontal}">
			<br/>
		</c:if>
	</c:forEach>
</c:if>

<c:if test='${inputType == "single-select"}'>
	<label for="input${itemId}"></label>
	<table border="0" cellpadding="0" cellspacing="0" id="input${itemId}">
		<tr>
			<td class="hidden_cell">&nbsp;</td>
			<td class="single_select_cell_first">
				<select id="input${itemId}" name="input${itemId}" style="width: 100%;"></select>
			</td>
		</tr>
		<c:if test="${displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}">
			<tr>
				<td class="hidden_cell">
					<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
						<c:if test="${displayItem.metadata.defaultValue == option.text}">
							<c:choose>
							<c:when test="${empty option.value}">		
								<span id="span_input${itemId}" class="annotation_text">''&nbsp;</span>
							</c:when>
							<c:otherwise>
								<span id="span_input${itemId}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
							</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
				</td>
				<td class="single_select_cell">
					<c:out value="${displayItem.metadata.defaultValue}"/>&nbsp;
				</td>
			</tr>
		</c:if>
		<c:forEach var="option" items="${displayItem.metadata.responseSet.options}" varStatus="myIndex">
			<c:choose>
			<c:when test="${myIndex.last}">		
				<c:set var="className" value="single_select_cell_last"/>
			</c:when>
			<c:otherwise>
				<c:set var="className" value="single_select_cell"/>
			</c:otherwise>
			</c:choose>
			
			<tr>
				<td class="hidden_cell">
					<c:choose>
					<c:when test="${empty option.value}">		
						<span id="span_input${itemId}" class="annotation_text">''&nbsp;</span>
					</c:when>
					<c:otherwise>
						<span id="span_input${itemId}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
					</c:otherwise>
					</c:choose>
				</td>
				<td class="${className}">
					<c:out value="${option.text}"/>&nbsp;
				</td>
			</tr>
		</c:forEach>
    </table>
</c:if>

<c:if test='${inputType == "multi-select"}'>
	<label for="input${itemId}"></label>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="hidden_cell">
				<table border="0" cellpadding="0" cellspacing="0">
					<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
						<tr>
							<td class="hidden_cell_with_padding">
								<span id="span_input${itemId}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
			<td class="hidden_cell">
				<select id="input${itemId}" multiple name="input${itemId}" size="${fn:length(displayItem.metadata.responseSet.options)}">
					<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
						<option value="<c:out value="${option.value}"/>">
							<c:out value="${option.text}"/>
						</option>
					</c:forEach>
				</select>
			</td>
		</tr>
	</table>
</c:if>

<c:if test='${inputType == "calculation" || inputType == "group-calculation"}'>
	<label for="${inputName}"></label>
	<input type="hidden" name="${inputName}" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
	<input id="${inputName}" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
</c:if>
<c:if test="${displayItem.metadata.required}">
	<td valign="top"><span class="alert">*</span></td>
</c:if>