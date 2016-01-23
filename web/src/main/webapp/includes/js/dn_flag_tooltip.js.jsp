<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<script>
	function genToolTips(itemDataId) {
		var resStatus = [];
		var detailedNotes = [];
		var discrepancyType = [];
		var updatedDates = [];
		var i = 0;
		var title = '<fmt:message key="tooltip_title1" bundle="${resword}"/>';
		var parentDnIds = [];
		var totNotes = 0;
		var footNote = '<fmt:message key="footNote" bundle="${resword}"/>';
		var auditLog = '';
		<c:set var="discrepancyNotes" value="1"/>
		<c:forEach var="displayItemWithGroupBean" items="${section.displayItemGroups}">
			<c:choose>
				<c:when test="${displayItemWithGroupBean.inGroup}">
					<c:forEach var="displayItemGroupBean" items="${displayItemWithGroupBean.itemGroups}">
						
						<c:forEach var="itemsSection" items="${displayItemGroupBean.items}">
							if ("${itemsSection.data.id}" == itemDataId) {
								<c:set var="notesSize" value="${itemsSection.totNew}"/>
								title = "<c:out value="${itemsSection.item.name}"/>";
								<c:set  var="discrepancyNotes" value="${itemsSection.discrepancyNotes}"/>
								<c:forEach var="discrepancyNotes" items="${discrepancyNotes}">
									resStatus[i] =<c:out value="${discrepancyNotes.resolutionStatusId}"/>;
									detailedNotes[i] = "<c:out value="${discrepancyNotes.description}"/>";
									discrepancyType[i] = "<c:out value="${discrepancyNotes.disType.name}"/>";
									updatedDates[i] = "<c:out value="${discrepancyNotes.createdDate}"/>";
									parentDnIds[i] = "<c:out value="${discrepancyNotes.parentDnId}"/>";
									i++;
								</c:forEach>
								totNotes =     ${notesSize};
								if (totNotes > 0) {
									footNote = totNotes + " " + '<fmt:message key="foot_threads" bundle="${resword}"/>' + " " + '<fmt:message key="footNote_threads" bundle="${resword}"/>';
								}
								if ("${itemsSection.data.auditLog}" == "true") {
									auditLog = '<fmt:message key="audit_exist" bundle="${resword}" />';
								}
							}
						</c:forEach>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:set var="itemsSection" value="${displayItemWithGroupBean.singleItem}"/>
					if ("${itemsSection.data.id}" == itemDataId) {
						<c:set var="notesSize" value="${itemsSection.totNew}"/>
						title = "<c:out value="${itemsSection.item.name}"/>";
						<c:set  var="discrepancyNotes" value="${itemsSection.discrepancyNotes}"/>
						<c:forEach var="discrepancyNotes" items="${discrepancyNotes}">
							resStatus[i] =<c:out value="${discrepancyNotes.resolutionStatusId}"/>;
							detailedNotes[i] = "<c:out value="${discrepancyNotes.description}"/>";
							discrepancyType[i] = "<c:out value="${discrepancyNotes.disType.name}"/>";
							updatedDates[i] = "<c:out value="${discrepancyNotes.createdDate}"/>";
							parentDnIds[i] = "<c:out value="${discrepancyNotes.parentDnId}"/>";
							i++;
						</c:forEach>
						totNotes =     ${notesSize};
						if (totNotes > 0) {
							footNote = totNotes + " " + '<fmt:message key="foot_threads" bundle="${resword}"/>' + " " + '<fmt:message key="footNote_threads" bundle="${resword}"/>';
						}
						if ("${itemsSection.data.auditLog}" == "true") {
							auditLog = '<fmt:message key="audit_exist" bundle="${resword}" />';
						}
					}
				</c:otherwise>
			</c:choose>
		</c:forEach>
		var htmlgen =
				'<div class=\"tooltip\">' +
				'<table  width="95%">' +
				' <tr><td  align=\"center\" class=\"header1\">' + title +
				' </td></tr><tr></tr></table><table  style="border-collapse:collapse" cellspacing="0" cellpadding="0" width="95%" >' +
				drawRows(i, resStatus, detailedNotes, discrepancyType, updatedDates, parentDnIds) +
				'</table><table width="95%"  class="tableborder" align="left">' +
				'</table><table><tr></tr></table>' +
				'<table width="95%"><tbody><td height="30" colspan="3"><span class=\"note\">' + footNote + '</span>' +
				'</td></tr>' +
				'<tr><td align=\"center\">' + auditLog + '</td></tr>' +
				'</tbody></table></table></div>';
		return htmlgen;
	}
</script>