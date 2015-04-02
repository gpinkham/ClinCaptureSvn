<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div class="chart_wrapper" align="left">
	<c:forEach items="${espsDataRows}" var="row" varStatus="status">
		<div class="bar_name"><a href="ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_s_2_studySubject.status=asc&findSubjects_f_enrolledAt=${row.name}">${row.rowName}</a></div>
		<ul barnumber="bar${status.count}" class="stacked_bar">
			<c:forEach items="${row.rowValues}" var="statuses" varStatus="num">
			<c:if test="${statuses.value!=0}">
				<a href="#a" status-id="${num.count - 1}" site-oid="${row.name}">
					<li class="${statuses.key} stack">
						<div class="pop-up"></div>
						<div class="hidden" id="value">${statuses.value}</div>
					</li>
				</a>
			</c:if>
			</c:forEach>
		</ul>
		<div class="right_text" barnumber="bar${status.count}">${row.extraField}%</div>
	</c:forEach>
</div>

<form id="esps_form" class="hidden">
	<input type="text" id="esps_next_page_exists" value="${espsNextPageExists}" />
	<input type="text" id="esps_previous_page_exists" value="${espsPreviousPageExists}" />
	<input type="text" id="esps_last_element" value="${espsDisplay}" />
	<input type="text" class="status" value="<fmt:message bundle='${resword}' key='locked'/>"/>
	<input type="text" class="status" value="<fmt:message bundle='${resword}' key='removed'/>" />
	<input type="text" class="status" value="<fmt:message bundle='${resword}' key='Signed'/>" />
	<input type="text" class="status" value="<fmt:message bundle='${resword}' key='available'/>"/>
</form>

