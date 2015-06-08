<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div class="chart_wrapper" align="left">
	<c:forEach items="${rows}" var="row" varStatus="status">
		<div class="bar_name">${row.rowName}</div>
		<ul barnumber="bar${status.count}" class="stacked_bar">
			<c:forEach items="${row.rowValues}" var="statuses" varStatus="num">
				<c:if test="${statuses.value!=0}">
					<li class="${statuses.key} stack">
						<div class="pop-up"></div>
						<div class="hidden" id="value">${statuses.value}</div>
					</li>
				</c:if>
			</c:forEach>
		</ul>
		<div class="right_text" barnumber="bar${status.count}">${row.extraField}%</div>
	</c:forEach>
</div>

<form id="espas_form" class="hidden">
	<input type="hidden" class="show_next" value="${showNext}" />
	<input type="hidden" class="show_back" value="${showBack}" />
	<input type="hidden" class="first_row" value="${firstRowNum}" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='Signed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='removed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='locked'/>"/>
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='available'/>"/>
</form>

