<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="pagemessage"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="paramPath" value="systemPropertyGroups['${groupStatus.index}']"/>
<c:if test="${subGrp ne null}">
  <c:set var="grp" value="${subGrp}"/>
  <c:set var="paramPath" value="systemPropertyGroups['${groupStatus.index}'].subGroups['${subGroupStatus.index}']"/>
</c:if>

<form:hidden path="${paramPath}.group.id"/>
<form:hidden path="${paramPath}.group.name"/>
<form:hidden path="${paramPath}.group.orderId"/>
<form:hidden path="${paramPath}.group.parentId"/>
<form:hidden path="${paramPath}.group.version"/>
<form:hidden id="state_group_id_${grp.group.id}" path="${paramPath}.opened"/>

<div class="group"><img id="img_group_id_${grp.group.id}"
                        src="../images/${themeColor}${grp.opened ? "bt_Collapse" : "bt_Expand"}.gif" border="0"
                        class="mousePointer" onclick="changeGroupState('${grp.group.id}');"/><span
    class="table_title_Admin mousePointer" onclick="changeGroupState('${grp.group.id}');"><fmt:message
    key="systemProperty.${grp.group.name}.name" bundle="${resword}"/></span></div>
