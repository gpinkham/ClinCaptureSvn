<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<!-- Function that runs ajax request, and returns widgets chart -->
<script>
	<c:import url="../../includes/js/widgets/w_enrollment_progress.js?r=${revisionNumber}" />
</script>

<div class="enrollment_progress" align="center">
	<h2><fmt:message key="enrollment_progress_widget_header" bundle="${resword}"/></h2>

	<!-- This part will be shown only when widget is in the toolbar -->
	<div class="description">
		<p><fmt:message key="enrollment_progress_widget_description" bundle="${resword}"/></p>
	</div>

	<!-- This content will be shown when widget is in the layout -->
	<div class="tc_content enrollment_progress_container"></div>
</div>
