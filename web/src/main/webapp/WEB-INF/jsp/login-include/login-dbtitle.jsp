<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${instanceType eq 'development'}"><span class="dbTitle">Development DataBase</span></c:when>
    <c:when test="${instanceType eq 'training'}"><span class="dbTitle">Training DataBase</span></c:when>
</c:choose>
