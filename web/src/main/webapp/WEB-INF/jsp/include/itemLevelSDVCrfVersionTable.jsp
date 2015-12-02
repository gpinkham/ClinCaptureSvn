<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:forEach var="table" items="${sdvMetadataBySection}" varStatus="sectionStatus">
	</br>
	<div class="table_title_Manage">
		<a href="javascript:leftnavExpand('section${sectionStatus.count}');">
			<img id="excl_sectionProperties" src="../images/bt_Collapse.gif" border="0" /> <fmt:message bundle="${resword}" key="_section"/>: ${table.key.name}
		</a>
	</div>
	<div id="section${sectionStatus.count}">
		<div class="table_shadow_bottom">
			<table width="100%" class="table_horizontal">
				<tr style="background-color:#F5F5F5">
					<td style="width:100px"><fmt:message bundle="${resword}" key="item_name"/></td>
					<td style="width:150px"><fmt:message bundle="${resword}" key="description"/></td>
					<td style="width:300px"><fmt:message bundle="${resword}" key="left_item_text"/></td>
					<td style="width:200px"><fmt:message bundle="${resword}" key="right_item_text"/></td>
					<td style="width:100px"><fmt:message bundle="${resword}" key="is_shown"/></td>
					<td style="width:100px"><fmt:message bundle="${resword}" key="sdv_required"/></td>
				</tr>
				<c:forEach var="row" items="${table.value}" varStatus="rowStatus">
					<tr>
						<td><a href="javascript: openDocWindow('../ViewItemDetail?itemId=${row.itemBean.id}')">${row.itemBean.name}</a></td>
						<td>${row.itemBean.description}</td>
						<td>${row.itemFormMetadataBean.leftItemText}</td>
						<td>${row.itemFormMetadataBean.rightItemText}</td>
						<td>${row.itemFormMetadataBean.showItem}</td>
						<td>
							<c:set value="${row.itemFormMetadataBean.showItem}" var="showItem"/>
							<c:set value="${row.edcItemMetadata.sdvRequired()}" var="required" />
							<c:set value="${showItem ? '' : 'disabled'}" var="disabled" />
							<input type="radio" ${required ? 'checked' : ''} ${disabled} value="1" name="${showItem ? 'sdv_requirement_s' : ''}${table.key.id}_i${row.itemBean.id}" onchange="javascript:changeIcon()" /> <fmt:message bundle="${resword}" key="yes"/>
							<input type="radio" ${required ? '' : 'checked'} ${disabled} value="0" name="${showItem ? 'sdv_requirement_s' : ''}${table.key.id}_i${row.itemBean.id}" onchange="javascript:changeIcon()" /> <fmt:message bundle="${resword}" key="no"/>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</c:forEach>
<input type="hidden" value="${crfVersionId}" id="currentVersion">

<jsp:include page="../include/changeTheme.jsp"/>