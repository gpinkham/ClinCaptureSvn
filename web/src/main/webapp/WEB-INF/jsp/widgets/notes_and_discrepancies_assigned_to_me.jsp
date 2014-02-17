<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
$(document).ready(function($) {
	setTimeout(function() { initNdsAssignedToMeWidget(); }, 400);
});
</script>
<div class="dns_assigned_to_me" align="center">
	<h2><fmt:message key="nds_assigned_to_me_widget_header" bundle="${resword}"/></h2>
	<div class="chart_wrapper" align="left">
		<ul id="stacked_bar">
			<a href="ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=<c:out value='${userBean.name}' />&listNotes_f_discrepancyNoteBean.resolutionStatus=<fmt:message key='New' bundle='${resterm}'/>"><li id="stack" class="new">
					<div id="pop-up"></div>				
			</li></a>
			<a href="ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=<c:out value='${userBean.name}' />&listNotes_f_discrepancyNoteBean.resolutionStatus=<fmt:message key='Updated' bundle='${resterm}'/>"><li id="stack" class="updated">
					<div id="pop-up"></div>			
			</li></a>
			<a href="ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=<c:out value='${userBean.name}' />&listNotes_f_discrepancyNoteBean.resolutionStatus=<fmt:message key='Resolution_Proposed' bundle='${resterm}'/>"><li id="stack" class="resolution_proposed">
					<div id="pop-up"></div>				
			</li></a>
			<a href="ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=<c:out value='${userBean.name}' />&listNotes_f_discrepancyNoteBean.resolutionStatus=<fmt:message key='Closed' bundle='${resterm}'/>"><li id="stack" class="closed">
					<div id="pop-up"></div>				
			</li></a>			
		</ul>
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
			<td><div class="new sign">&nbsp</div> - <fmt:message key="new" bundle="${resword}"/></td>
			<td><div class="updated sign">&nbsp</div> - <fmt:message key="updated" bundle="${resword}"/></td>
		</tr>
		<tr align="left">
			<td><div class="resolution_proposed sign">&nbsp</div> - <fmt:message key="Resolution_Proposed" bundle="${resword}"/></td>
			<td><div class="closed sign">&nbsp</div> - <fmt:message key="closed" bundle="${resword}"/></td>
		</tr>
	</table>	
</div>
