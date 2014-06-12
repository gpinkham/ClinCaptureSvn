<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

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
					<input type="button" name="BTN_Back" id="previous" value="Previous" class="button_medium" onClick="javascript: initSdvProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${sdvNextYearExists}">
					<input type="button" name="BTN_Forvard" id="next" value="Next" class="button_medium" onClick="javascript: initSdvProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="sdv_progress">
	<input type="text" value="${sdvProgressYear}" id="sdvProgressYear" />
	<input type="text" class="currentColor" value="${newThemeColor}">
</form>
<input type="hidden" value="${study.id}" id="sdvWStudyId">