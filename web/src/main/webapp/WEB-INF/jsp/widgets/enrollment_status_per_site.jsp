<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
	<c:import url="../../includes/js/widgets/w_enrollment_status_per_site.js" />
</script>

<div class="enrollment_status_per_site" align="center">
	<h2><fmt:message key="enrollment_status_ps_widget_header" bundle="${resword}"/></h2>
	<div id="enrollment_status_per_site_container">
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
				<div class="signed sign"></div> - <fmt:message bundle="${resword}" key="subjectEventSigned" /></td>		
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="locked sign"></div> - <fmt:message bundle="${resword}" key="locked" /></td>
		</tr>
	</table>
	<table>
		<tr>
			<td align="left">
				<input type="button" name="BTN_Back" id="esps_previous" value="Previous" class="button_medium" onClick="javascript: initEnrollStatusPerSiteWidget('goBack');"/>
			</td>
			<td align="right">
				<input type="button" name="BTN_Forvard" id="esps_next" value="Next" class="button_medium" onClick="javascript: initEnrollStatusPerSiteWidget('goForward');"/>
			</td>
		</tr> 
	</table>
	<div class="hint"><fmt:message bundle="${restext}" key="widget_enrollment_per_site_hint"/></div>
</div>

