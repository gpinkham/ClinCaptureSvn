<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="evaluation_progress_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${evalProgPreviousYearExists}">
					<input type="button" name="BTN_Back" id="previous" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium" onClick="javascript: initEvaluationProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${evalProgNextYearExists}">
					<input type="button" name="BTN_Forward" id="next" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium" onClick="javascript: initEvaluationProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="evaluation_progress_form">
	<c:forEach items="${evaluationProgressDataRows}" var="row" varStatus="rowIndex">
		<input type="hidden" value="${row.key}"
				<c:forEach items="${row.value}" var="status">
					<c:set var="statusKey" value="${fn:toLowerCase(status.key) }" />
					stat-${fn:replace(statusKey," ", "_")}="${status.value}"
				</c:forEach>
				/>
	</c:forEach>

	<input type="text" id="evaluationProgressYear" value="${evaluationProgressYear}"/>
	<input type="text" class="currentColor" value="${newThemeColor}">
	<input type="text" id="evaluationProgressActivateLegend" value="${evaluationProgressActivateLegend}">
	<input type="text" id="evaluationStatusList" value="<fmt:message key="evaluation_completed" bundle="${resword}"/>;<fmt:message key="ready_for_evaluation" bundle="${resword}"/>"/>
</form>

<script>applyThemeForChart();</script>
