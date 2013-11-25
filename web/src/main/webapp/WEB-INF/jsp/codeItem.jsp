<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="color" scope="session" value="${newThemeColor}"/>

<c:choose>
    <c:when test="${fn:length(elementList) eq 0}">
        <br>
        <span class="formlabel">No matching results found in dictionary <c:out value="${itemDictionary}"/></span>
    </c:when>
    <c:otherwise>
        <table id="tablepaging" class="itemsTable">
            <c:set var="counter" value="0"/>
            <c:forEach items="${elementList}" var="obj" varStatus="status">
                <c:if test="${(status.index % 2==0) }">
                    <c:set var="counter" value="${counter + 1}"/>
                    <c:if test="${counter lt 31}">
                        <tr>
                            <td><c:out value="${obj.itemName}"/>:</td>
                            <td/></td>
                            <td width=360px colspan="2"></td>
                            <td></td>
                        </tr>
                    </c:if>
                </c:if>
            </c:forEach>
            <tr>

            <c:set var="codeButtonColor" value="../images/button_BG.gif"/>
            <c:if test="${(color == 'violet')}">
                <c:set var="codeButtonColor" value="../images/violet/button_BG.gif"/>
            </c:if>
            <c:if test="${(color == 'green')}">
                <c:set var="codeButtonColor" value="../images/green/button_BG.gif"/>
            </c:if>
            <c:choose>
                <c:when test="${autoCoded}">
                        <input type="hidden" id="autoCode" />
                </c:when>
                <c:otherwise>
                        <td></td>
                        <td></td>
                        <td align="right">
                            <input type="button" name="codeAndAliasBtn" class="button" value="Code & Alias" style="background-image: url(<c:out value="${codeButtonColor}"/>);" onclick="codeAndAlias($(this).parent().prev())" />
                            <input type="button" name="codeItemBtn" class="button" value="Code" style="background-image: url(<c:out value="${codeButtonColor}"/>);" onclick="saveCodedItem($(this).parent().prev(  ))" />
                        </td>
                 </c:otherwise>
            </c:choose>
            </tr>
        </table>
    </c:otherwise>
</c:choose>

<div id="pageNavPosition" style="padding-top: 20px" align="center"/>

<script type="text/javascript">
    var pager = new Pager('tablepaging', 12);
    pager.init();
    pager.showPageNav('pager', 'pageNavPosition');
    pager.showPage(1);
</script>

