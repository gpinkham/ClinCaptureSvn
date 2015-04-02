<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="color" scope="session" value="${newThemeColor}"/>
<c:if test="${itemDictionary == 'WHOD' || itemDictionary == 'MedDRA'}">
    <c:set var="displayHttp" value="style=display:none"/>
</c:if>

<c:choose>
    <c:when test="${fn:length(codedElementList) eq 0}">
        <div id="emptyResult">
            <br>
            <span class="formlabel">No matching results found in dictionary <c:out value="${itemDictionary}"/></span>
            <br>
        </div>
        <input type="hidden" id="notCoded" />
    </c:when>
    <c:otherwise>
        <table id="tablepaging_result" class="itemsTable">
            <c:set var="counter" value="0"/>
            <c:forEach items="${codedElementList}" var="obj">
                <c:set var="counter" value="${counter + 1}"/>
                <tbody>
                <tr <c:out value="${displayHttp}"/>>
                    <td  id="<fmt:message key='http' bundle="${resword}"/>">
                        <fmt:message key='http' bundle="${resword}"/>:
                    </td>
                    <c:set var="hyperlinkColor" value="#789EC5"/>
                    <c:if test="${(color == 'violet')}">
                        <c:set var="hyperlinkColor" value="#aa62c6"/>
                    </c:if>
                    <c:if test="${(color == 'green')}">
                        <c:set var="hyperlinkColor" value="#75b894"/>
                    </c:if>
                    <c:if test="${(color == 'darkBlue')}">
                        <c:set var="hyperlinkColor" value="#2c6caf"/>
                    </c:if>
                    <td>
                        <c:set var="objectHttpPath" value="${fn:replace(obj.httpPath, '#', '%23')}"/>
                        <a target="_blank" style="color:<c:out value="${hyperlinkColor}"/>"
                           href="${bioontologyUrl}/ontologies/<c:out value="${fn:toUpperCase(fn:replace(itemDictionary, ' ', ''))}"/>?p=classes&conceptid=<c:out value="${objectHttpPath}"/>">
                            <c:out value="${obj.httpPath}"/>
                        </a>
                    </td>
                    <td width="360px" colspan="2"></td>
                    <td></td>
                </tr>
                <c:forEach items="${obj.classificationElement}" var="classElement" varStatus="status">
                    <c:choose>
                        <c:when test="${itemDictionary == 'WHOD' and status.last}">
                            <tr style="font-weight:bold;">
                        </c:when>
                        <c:otherwise>
                            <tr>
                        </c:otherwise>
                    </c:choose>
                        <td id="<c:out value="${classElement.elementName}"/>" style="white-space: nowrap;">
                            <c:set var="classElementName"><c:out value="${fn:toLowerCase(classElement.elementName)}"/></c:set>
                            <fmt:message key='${classElementName}' bundle="${resword}"/>:
                        </td>
                            <td width="360px"><c:out value="${classElement.codeName}"/>
                        </td>
                        <td colspan="2"></td>
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
                        <c:if test="${(color == 'darkBlue')}">
                            <c:set var="codeButtonColor" value="../images/darkBlue/button_BG.gif"/>
                        </c:if>

                        <c:choose>
                            <c:when test="${autoCoded}">
                                <td colspan="2"></td>
                                <td></td>
                                <td>
                                    <input type="hidden" id="autoCode"/>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td></td>

                                <c:choose>
                                    <c:when test="${itemDictionary == 'WHOD' || itemDictionary == 'MedDRA'}">
                                        <td>
                                            <c:if test="${configuredDictionaryIsAvailable}">
                                                <input type="button" id="<c:out value="${counter}"/>" name="codeAndAliasBtn"
                                                       class="button" value="Code & Alias" style="background-image: url(<c:out
                                                        value="${codeButtonColor}"/>); float: right;"
                                                       onclick="codeAndAlias($(this))"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <input type="button" id="<c:out value="${counter}"/>" name="codeItemBtn"
                                                   class="button" value="Code"
                                                   style="background-image: url(<c:out value="${codeButtonColor}"/>);"
                                                   onclick="saveCodedItem($(this))"/>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <c:if test="${configuredDictionaryIsAvailable}">
                                                <input type="button" id="<c:out value="${counter}"/>" name="codeAndAliasBtn"
                                                       class="button" value="Code & Alias" style="background-image: url(<c:out
                                                        value="${codeButtonColor}"/>); float: right; visibility:hidden;"
                                                       onclick="codeAndAlias($(this))"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <input type="button" id="<c:out value="${counter}"/>" name="codeItemBtn"
                                                   class="button" value="Code" style="background-image: url(<c:out
                                                    value="${codeButtonColor}"/>); visibility:hidden;"
                                                   onclick="saveCodedItem($(this))"/>
                                        </td>
                                        <td>
                                            <input type="button" id="<c:out value="${counter}"/>" name="getInfoItemBtn"
                                                   class="button" value="Get full info"
                                                   style="background-image: url(<c:out value="${codeButtonColor}"/>);"
                                                   onclick="codeItemFields($(this))"/>
                                        </td>
                                    </c:otherwise>
                                </c:choose>


                            </c:otherwise>
                        </c:choose>
                    </tr>
                </tbody>
            </c:forEach>
        </table>
        <c:if test="${fn:length(codedElementList) > 0}">
            <input type="hidden" id="response" />
        </c:if>
    </c:otherwise>
</c:choose>

<c:if test="${!autoCoded && fn:length(codedElementList) > 0}">
    <div id="pageNavPosition" style="padding-top: 20px" align="center"/>
    <input type="hidden" name="<c:out value="${itemDictionary}"/>" id="dictionary"/>
</c:if>

<script type="text/javascript">

    var dictionary = $("#dictionary").attr('name');
    var rowsToDisplay = 6;

    var pager = new Pager('tablepaging_result', rowsToDisplay);
    pager.init();
    pager.showPageNav('pager', 'pageNavPosition');
    pager.showPage(1);
</script>
