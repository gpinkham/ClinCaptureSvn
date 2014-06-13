<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<!-- Function that runs ajax request, and returns widgets chart -->
<script>
$(document).ready(function($) {
	setTimeout(function() { initNdsPerCrf("init"); }, 700);
});
</script>

<div class="nds_per_crf" align="center">
	<h2><fmt:message key="nds_per_crf_widget_header" bundle="${resword}"/></h2>

	<!-- This part will be shown only when widget is in the toolbar -->
	<div class="description">
		<p><fmt:message key="nds_per_crf_widget_description" bundle="${resword}"/></p>
	</div>

	<!-- This content will be shown when widget is in the layout -->
	<div class="tc_content nds_per_crf_container"></div>
</div>