<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="cc-fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
	<c:choose>
		<c:when test="${displayItem.discrepancyNoteStatus == 0}">
			<c:set var="imageFileName" value="icon_noNote" />
		</c:when>
		<c:when test="${displayItem.discrepancyNoteStatus == 1 || displayItem.discrepancyNoteStatus == 6}">
			<c:set var="imageFileName" value="icon_Note" />
		</c:when>
		<c:when test="${displayItem.discrepancyNoteStatus == 2}">			<c:set var="imageFileName" value="icon_flagYellow" />
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
		<c:param name="rowCount" value=""/>
		<c:param name="itemId" value="${itemId}" />
		<c:param name="inputName" value="${inputName}"/>
	</c:import>

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

	<c:choose>
		<c:when test="${displayItem.item.dataType.id eq 13 or displayItem.item.dataType.id eq 14}"></c:when>
		<c:when test="${displayItem.numDiscrepancyNotes > 0}">
			<a tabindex="<c:out value="${tabNum + 1000}"/>" href="#"   onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
			   onClick="openDNWindow('ViewDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&groupLabel=<c:out value="${displayItem.metadata.groupLabel}"/>&sectionId=<c:out value="${displayItem.metadata.sectionId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}" />&column=value&enterData=1&originJSP=<c:out value="${param.originJSP}"/>&writeToDB=1&','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>'); return false;">
				<img id="flag_<c:out value="${inputName}" />" name="flag_<c:out value="${inputName}" />"
					 src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
			</a>
		</c:when>
		<c:otherwise>
			<c:set var="eventName" value="${toc.studyEventDefinition.name}"/>
			<c:set var="eventDate">
				<cc-fmt:formatDate value="${toc.studyEvent.dateStarted}" pattern="${dateTimeFormat}"/>
			</c:set>
			<c:set var="crfName" value="${toc.crf.name} ${toc.crfVersion.name}"/>
			<a tabindex="<c:out value="${tabNum + 1000}"/>" href="#" onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
			   onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&groupLabel=<c:out value="${displayItem.metadata.groupLabel}"/>&sectionId=<c:out value="${displayItem.metadata.sectionId}"/>&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}" />&column=value&enterData=1&eventName=${eventName}&eventDate=${eventDate}&crfName=${crfName}&originJSP=<c:out value="${param.originJSP}"/>&writeToDB=<c:out value="${writeToDB}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
				<img id="flag_<c:out value="${inputName}" />" name="flag_<c:out value="${inputName}" />"
					 src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
				<input type="hidden" value="ViewDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&groupLabel=<c:out value="${displayItem.metadata.groupLabel}"/>&sectionId=<c:out value="${displayItem.metadata.sectionId}"/>&id=<c:out value="${dataId}"/>&name=itemData&field=<c:out value="${inputName}" />&column=value&enterData=1&originJSP=<c:out value="${param.originJSP}"/>&writeToDB=<c:out value="${writeToDB}"/>"/>
			</a>
		</c:otherwise>
	</c:choose>

	<c:import url="../submit/itemSDV.jsp">
		<c:param name="rowCount" value=""/>
		<c:param name="itemId" value="${itemId}" />
		<c:param name="inputName" value="${inputName}"/>
	</c:import>
</c:if>