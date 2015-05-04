<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
	<c:import url="../../includes/js/widgets/w_events_completion.js?r=${revisionNumber}" />
</script>

<div class="events_completion" align="center">
	<h2><fmt:message key="events_completion_widget_header" bundle="${resword}"/></h2>
	<div id="events_completion_container">
	</div>
	<table class="captions_percents">
		<tr>
			<td>0%</td>
			<td>10%</td>
			<td>20%</td>
			<td>30%</td>
			<td>40%</td>
			<td>50%</td>
			<td>60%</td>
			<td>70%</td>
			<td>80%</td>
			<td>90%</td>
			<td>100%</td>
		</tr>
	</table>
	<table class="signs">
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="scheduled sign"></div> - <fmt:message bundle="${resword}" key="scheduled" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="data_entry_started sign"></div> - <fmt:message bundle="${resword}" key="data_entry_started" /></td>
		</tr>
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="completed sign"></div> - <fmt:message bundle="${resword}" key="completed" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="signed sign"></div> - <fmt:message bundle="${resword}" key="subjectEventSigned" /></td>
		</tr>
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="locked sign"></div> - <fmt:message bundle="${resword}" key="locked" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="skipped sign"></div> - <fmt:message bundle="${resword}" key="skipped" /></td>
		</tr>
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="stopped sign"></div> - <fmt:message bundle="${resword}" key="stopped" /></td>
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="sdved sign"></div> - <fmt:message bundle="${resword}" key="SDV_complete" /></td>
		</tr>
		<tr align="left">
			<td>
				<div class="popup_legend_medium"><p></p></div>
				<div class="not_scheduled sign"></div> - <fmt:message bundle="${resword}" key="notScheduled" /></td>
		</tr>
	</table>
	<table>
		<tr>
			<td align="left">
				<input type="button" name="BTN_Back" id="previous" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium" onClick="javascript: initEventsCompletionWidget('goBack');"/>
			</td>
			<td align="right">
				<input type="button" name="BTN_Forvard" id="next" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium" onClick="javascript: initEventsCompletionWidget('goForward');"/>
			</td>
		</tr> 
	</table>
</div>
