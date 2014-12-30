<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<!-- *JSP* submit/showAnnotatedGroupItemInput.jsp -->

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="isLast" value="${param.isLast}" />
<c:set var="isFirst" value="${param.isFirst}" />
<c:set var="repeatParentId" value="${param.repeatParentId}" />
<c:set var="rowCount" value="${param.rowCount}" />
<c:set var="inputName" value="${repeatParentId}_[${repeatParentId}]input${itemId}" />
<c:set var="parsedInputName" value="${repeatParentId}_${rowCount}input${itemId}" />
<c:set var="isHorizontal" value="${param.isHorizontal}" />
<c:set var="defValue" value="${param.defaultValue}" />

<c:if test="${!isLast && rowCount == 0}">
    <c:set var="inputName" value="${repeatParentId}_${rowCount}input${itemId}" />
</c:if>

<c:if test="${!isLast && rowCount > 0}">
    <c:set var="inputName" value="${repeatParentId}_manual${rowCount}input${itemId}" />
    <c:set var="parsedInputName" value="${repeatParentId}_manual${rowCount}input${itemId}" />
</c:if>

<c:set var="respLayout" value="${displayItem.metadata.responseLayout}" />

<c:choose>
<c:when test="${empty displayItem.metadata.responseSet.value}">
	<c:set var="inputTxtValue" value="${defValue}"/>
</c:when>
<c:otherwise>
	<c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/>
</c:otherwise>
</c:choose>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="annotated_itemOID">
			<span class="annotated_itemOID">
				<c:choose>
					<c:when test="${study.studyParameterConfig.annotatedCrfSasItemNames == 'yes'}">
						${sasItemNamesMap[displayItem.item.name]}
					</c:when>
					<c:otherwise>
						${displayItem.item.name}
					</c:otherwise>
				</c:choose>
			</span>
		</td>
	</tr>


	<tr>
		<td class="hidden_cell">
			<c:if test='${inputType=="file"}'>				<label for="${inputName}"></label>
				<input type="text" id="ft${itemId}" name="fileText${itemId}" value="">
				<input type="button" id="up${inputName}" name="uploadFile${inputName}" value="<fmt:message key="click_to_upload" bundle="${resword}"/>">
			</c:if>

			<c:if test='${inputType == "instant-calculation"}'>
				<label for="${inputName}"></label>
				<input id="${inputName}" type="text" name="${inputName}" value="<c:out value="${inputTxtValue}"/>" />
			</c:if>

			<c:if test='${inputType == "text"}'>
				<label for="${inputName}"></label>
				<input id="${inputName}" type="text" name="${inputName}" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
				<c:if test="${displayItem.item.itemDataTypeId==9 || displayItem.item.itemDataTypeId==10}">
					<img src="<c:out value="${contextPath}" />/images/bt_Calendar.gif" alt="<fmt:message key="show_calendar" bundle="${resword}"/>" title="<fmt:message key="show_calendar" bundle="${resword}"/>" border="0"/>
				</c:if>
			</c:if>

			<c:if test='${inputType == "textarea"}'>
				<label for="${inputName}"></label>
				<textarea id="${inputName}" name="${inputName}" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
			</c:if>

			<c:if test='${inputType == "checkbox"}'>
				<c:if test='${!isHorizontal}'>
					<c:forEach var="option" items="${displayItem.metadata.responseSet.options}" varStatus="myIndex">
						<c:if test='${myIndex.first or (!myIndex.first and isHorizontal)}'>
							<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
						</c:if>
						<label for="${inputName}"></label>
						<div class="annotation_text" id="a_div_${inputName}_${myIndex.count}">
							<span class="annotation_text">
								<c:out value="${option.value}"/>
							</span>
						</div>
						<input class="annotated_item" id="${inputName}_${myIndex.count}" type="checkbox" name="${inputName}" value="<c:out value="${option.value}" />"/> 
						<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
						<c:out value="${option.text}"/> 
						<c:if test="${!isHorizontal}">
							<br/>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test='${isHorizontal}'>
				<%-- Only have one of these per checkbox button--%>
					<label for="<c:out value="${inputName}"/>"></label>
					<div class="annotation_text" id="a_div_${inputName}">
						<span class="annotation_text">
							<c:out value="${responseOptionBean.value}"/>
						</span>
					</div>
					<input id="${inputName}" type="checkbox" name="${inputName}" value="${responseOptionBean.value}"/>
				</c:if>
			</c:if>

			<c:if test='${inputType == "radio"}'>
				<c:if test='${!isHorizontal}'>
					<c:forEach var="option" items="${displayItem.metadata.responseSet.options}" varStatus="myIndex">
						<c:if test='${myIndex.first or (!myIndex.first and isHorizontal)}'>
							<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
						</c:if>
						<label for="${inputName}"></label>
						<div class="annotation_text" id="a_div_${inputName}_${myIndex.count}">
							<span class="annotation_text">
								<c:out value="${option.value}"/>
							</span>
						</div>
						<input class="annotated_item" id="${inputName}_${myIndex.count}" type="radio" name="${inputName}" value="<c:out value="${option.value}" />"/> 
						<c:forEach var="i" begin="4" end="${fn:length(option.value)}" step="2">&nbsp;</c:forEach>
						<c:out value="${option.text}"/> 
						<c:if test="${!isHorizontal}">
							<br/>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test='${isHorizontal}'>
				<%-- Only have one of these per radio button--%>
					<label for="<c:out value="${inputName}"/>"></label>
					<div class="annotation_text" id="a_div_${inputName}">
						<span class="annotation_text">
							<c:out value="${responseOptionBean.value}"/>
						</span>
					</div>
					<input id="${inputName}" type="radio" name="${inputName}" value="${responseOptionBean.value}"/>
				</c:if>
			</c:if>

			<c:if test='${inputType == "single-select"}'>
				<label for="${inputName}"></label>
				<table border="0" cellpadding="0" cellspacing="0" id="${inputName}" style="margin: 5px;">
					<tr>
						<td class="hidden_cell">&nbsp;</td>
						<td class="single_select_cell_first">
							<select id="${inputName}" name="${inputName}" style="width: 100%;"></select>
						</td>
						<c:if test="${displayItem.metadata.required}">
							<c:set var="asteriskIsShown" value="true"/>
							<td class="hidden_cell">
								<span class="alert">&nbsp;*</span>
							</td>
						</c:if>
					</tr>
					<c:if test="${displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}">
						<tr>
							<td class="hidden_cell">
								<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
									<c:if test="${displayItem.metadata.defaultValue == option.text}">
										<c:choose>
										<c:when test="${empty option.value}">		
											<span id="span_${inputName}" class="annotation_text">''&nbsp;</span>
										</c:when>
										<c:otherwise>
											<span id="span_${inputName}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
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
									<span id="span_${inputName}" class="annotation_text">''&nbsp;</span>
								</c:when>
								<c:otherwise>
									<span id="span_${inputName}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
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
				<label for="${inputName}"></label>
				<table border="0" cellpadding="0" cellspacing="0" style="margin: 5px">
					<tr>
						<td class="hidden_cell">
							<table border="0" cellpadding="0" cellspacing="0">
								<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
									<tr>
										<td class="hidden_cell_with_padding">
											<span id="span_${inputName}" class="annotation_text"><c:out value="${option.value}"/>&nbsp;</span>
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td class="hidden_cell">
							<select id="${inputName}" multiple name="${inputName}" size="${fn:length(displayItem.metadata.responseSet.options)}">
								<c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
									<option value="<c:out value="${option.value}"/>">
										<c:out value="${option.text}"/>
									</option>
								</c:forEach>
							</select>
						</td>
						<c:if test="${displayItem.metadata.required}">
							<c:set var="asteriskIsShown" value="true"/>
							<td class="hidden_cell" style="vertical-align: top;">
								<span class="alert">&nbsp;*</span>
							</td>
						</c:if>
					</tr>
				</table>
			</c:if>

			<c:if test='${inputType == "calculation" || inputType == "group-calculation"}'>
				<input type="hidden" name="input<c:out value="${itemId}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
				<label for="${inputName}"></label>
				<input id="${inputName}" type="text" class="disabled" disabled="disabled" name="${inputName}" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
			</c:if>

			<c:if test="${displayItem.metadata.required and asteriskIsShown != 'true'}">
				<span class="alert">*</span>
			</c:if>

			<c:if test="${(inputType == 'text'|| inputType == 'textarea' || inputType == 'multi-select' || inputType == 'single-select' || inputType == 'calculation') and (!(displayItem.item.units eq ''))}">
				(<c:out value="${displayItem.item.units}"/>)
			</c:if>
			
			<c:if test="${(inputType == 'radio'|| inputType == 'checkbox') and (!isHorizontal) and (!(displayItem.item.units eq ''))}">
				(<c:out value="${displayItem.item.units}"/>)
			</c:if>
		</td>
	</tr>
</table>