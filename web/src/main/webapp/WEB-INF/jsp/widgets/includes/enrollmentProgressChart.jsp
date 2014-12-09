<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="enrollment_progress_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${epPreviousYearExists}">
					<input type="button" name="BTN_Back" id="previous" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium" onClick="javascript: initEnrollmentProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${epNextYearExists}">
					<input type="button" name="BTN_Forvard" id="next" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium" onClick="javascript: initEnrollmentProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="enrollment_progress">
<c:forEach items="${epDataRows}" var="row" varStatus="rowIndex">
	<input type="hidden" value="${row.key}" 
		<c:forEach items="${row.value}" var="status">
			stat-${status.key.name}="${status.value}"
		</c:forEach>
	/>
</c:forEach>
	<input type="text" class="status" value="<fmt:message bundle='${resterm}' key='locked'/>" />
	<input type="text" class="status" value="<fmt:message bundle='${resterm}' key='signed'/>" />
	<input type="text" class="status" value="<fmt:message bundle='${resterm}' key='removed'/>" />
	<input type="text" class="status" value="<fmt:message bundle='${resterm}' key='available'/>" />
	<input type="text" value="${epYear}" id="epYear" />
	<input type="text" class="currentColor" value="${newThemeColor}">
</form>
<input type="hidden" value="${study.id}" id="epStudyId">

<script>applyThemeForChart();</script>
