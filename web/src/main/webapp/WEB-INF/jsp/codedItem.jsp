<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:choose>
    <c:when test="${fn:length(classification) eq 0}">
        <br>
        <span class="formlabel">No matching results found in dictionary <c:out value="${itemDictionary}"/></span>
    </c:when>
    <c:otherwise>
        <table id="tablepaging" class="itemsTable">
            <c:set var="counter" value="0"/>
            <c:forEach items="${classification}" var="obj">
                <c:set var="counter" value="${counter + 1}"/>
                <c:if test="${counter lt 31}">
                    <tr>
                        <td><c:out value="${counter}"/>.</td>
                        <td width=90px><fmt:message key="id" bundle="${resword}"/>:</td>
                        <td width=360px><c:out value="${obj.id}"/></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Term:</td>
                        <td>
                            <c:out value="${obj.term}"/></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Code:</td>
                        <td>
                            <div id="${itemDataId}" class="wrapText">
                                <c:out value="${obj.code}"/>
                            </div>
                        </td>
                        <td>
                            <input type="button" name="codeItemBtn" class="loginbutton" value="code" style="background-image: url(../images/violet/loginbutton_BG.gif);" onclick="saveCodedItem($(this).parent().prev(  ))" />
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Dictionary:</td>
                        <td>
                            <c:out value="${obj.dictionary}"/>
                        </td>
                        <td></td>
                    </tr>
                </c:if>
            </c:forEach>
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
