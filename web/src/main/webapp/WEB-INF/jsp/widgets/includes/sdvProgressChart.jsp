<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<form id="sdvMonthForm">
<c:forEach items="${sdvValuesByMonth}" var="month" varStatus="sdvIndex">
	<input class="hidden" month="${month.key}" value="${month.value}" available="${sdvAvailableECRFs[sdvIndex.index]}"/>
</c:forEach>
</form>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="sdv_progress_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${sdvPreviousYearExists}">
					<input type="button" name="BTN_Back" id="previous" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium" onClick="javascript: initSdvProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${sdvNextYearExists}">
					<input type="button" name="BTN_Forvard" id="next" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium" onClick="javascript: initSdvProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="sdv_progress">
	<input type="text" value="${sdvProgressYear}" id="sdvProgressYear" />
	<input type="text" class="currentColor" value="${newThemeColor}">
	<input type="hidden" class="status sdved" value="<fmt:message bundle='${resword}' key='w_status_sdved'/>" />
	<input type="hidden" class="status available_for_sdv" value="<fmt:message bundle='${resword}' key='w_status_available_for_sdv'/>" />
	<input type="hidden" class="status_sdved_jmesa_filter" value="<fmt:message bundle='${resword}' key='complete'/>" />
	<input type="hidden" class="status_available_for_sdv_jmesa_filter" value="<fmt:message bundle='${resword}' key='not_done'/>" />
	<input type="hidden" id="sdvProgressActivateLegend" value="${sdvProgressActivateLegend}">
</form>
<input type="hidden" value="${study.id}" id="sdvWStudyId">

<script>applyThemeForChart();</script>
