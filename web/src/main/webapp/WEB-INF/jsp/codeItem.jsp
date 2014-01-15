<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="color" scope="session" value="${newThemeColor}"/>

<c:choose>
    <c:when test="${fn:length(codedElementList) eq 0}">
        <div id="emptyResult">
            <br>
            <span class="formlabel">No matching results found in dictionary <c:out value="${itemDictionary}"/>
        </div>
    </c:when>
    <c:otherwise>
        <table id="tablepaging" class="itemsTable">
            <c:set var="counter" value="0"/>
            <c:forEach items="${codedElementList}" var="obj">
                <c:set var="counter" value="${counter + 1}"/>
                <tr>
                    <td>HTTP:</td>
                    <td><c:out value="${obj.httpPath}"/></td>
                    <td width=360px colspan="2"></td>
                    <td></td>
                </tr>
                <c:forEach items="${obj.classificationElement}" var="classElement">
                    <tr>
                        <td>
                            <c:if test="${(classElement.elementName == 'PT' || classElement.elementName == 'EXT')}">
                                <div id=<c:out value="${counter}"/> name="verbTermMark"/>
                            </c:if>
                            <c:out value="${classElement.elementName}"/>:
                        </td>
                        <td><c:out value="${classElement.codeName}"/></td>
                        <td width=360px colspan="2"></td>
                        <td></td>
                    </tr>
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
                            <td colspan="2"></td>
                            <td></td>
                            <td>
                                <input type="button" id="<c:out value="${counter}"/>" name="codeAndAliasBtn" class="button" value="Code & Alias" style="background-image: url(<c:out value="${codeButtonColor}"/>);" onclick="codeAndAlias($(this))" />
                            </td>
                            <td>
                                <input type="button" id="<c:out value="${counter}"/>" name="codeItemBtn" class="button" value="Code" style="background-image: url(<c:out value="${codeButtonColor}"/>);" onclick="saveCodedItem($(this))" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<div id="pageNavPosition" style="padding-top: 20px" align="center"/>
<input type="hidden" name="<c:out value="${itemDictionary}"/>" id="dictionary"/>

<script type="text/javascript">

    var dictionary = $("#dictionary").attr('name');
    var rowsToDisplay;

    if(dictionary == "MedDRA") {

        rowsToDisplay = 14;
    } else if (dictionary == "ICD 10" || dictionary == "ICD 9CM") {

        rowsToDisplay = 15;
    } else {

        rowsToDisplay = 10;
    }

    var pager = new Pager('tablepaging', rowsToDisplay);
    pager.init();
    pager.showPageNav('pager', 'pageNavPosition');
    pager.showPage(1);
</script>