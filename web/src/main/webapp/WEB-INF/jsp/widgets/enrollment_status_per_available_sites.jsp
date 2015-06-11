<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
	<c:import url="../../includes/js/widgets/w_enrollment_status_per_available_sites.js?r=${revisionNumber}" />
</script>

<div class="espas" align="center">
	<h2><fmt:message key="enrollment_status_pas_widget_header" bundle="${resword}"/></h2>
	<div id="espas_container">
	</div>
	<table class="captions">
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
	<table class="signs">
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="available sign"></div> - <fmt:message bundle="${resword}" key="available" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="removed sign"></div> - <fmt:message bundle="${resword}" key="removed" /></td>
		</tr>
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="locked sign"></div> - <fmt:message bundle="${resword}" key="locked" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="signed sign"></div> - <fmt:message bundle="${resword}" key="subjectEventSigned" /></td>
		</tr>
	</table>
	<table>
		<tr>
			<td align="left">
				<input type="button" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium back" onClick="initESPASWidget('back');"/>
			</td>
			<td align="right">
				<input type="button" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium forward" onClick="initESPASWidget('forward');"/>
			</td>
		</tr>
	</table>
</div>

