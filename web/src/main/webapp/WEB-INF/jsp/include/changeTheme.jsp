<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="color" scope="session" value="${newThemeColor}"/>
<c:if test="${color == 'violet' || color == 'green'}">
    <script>
        $(function() {
            var s, styles = document.getElementsByTagName('style');
            var i = styles.length;
            while (i--) {
                s = styles[i];
                if (s.className == 'hideStuff') {
                    s.parentNode.removeChild(s);
                    break;
                }
            }
        });
    </script>
</c:if>
<c:choose>
    <c:when test="${color == 'violet'}">
        <script language="JavaScript" type="text/JavaScript">
            $('a').css('color', '#AA62C6');
            $('a').removeAttr('onmousedown');
            $('a').removeAttr('onmouseup');
            $('H3').css('color', '#AA62C6');
            $('H1').css('color', '#AA62C6');

            $(".nav_bt").find('a').css('color', '#FFFFFF');
            $('.oc_nav').find('.nav_bt').css('background-color', "#AA62C6");
            if ($('#bt_Home td, #bt_Home li').css('background-color') != 'transparent') {
                $('#bt_Home td, #bt_Home li').css('background-color', "#AA62C6");
            }
            $('input.navSearch:text').css('background-color', "#dbc4e4");
            $('.table_cell_noborder').css('color', "#AA62C6");
            $('.table_cell_top').css('color', "#AA62C6");
            $('.table_header_column_top').css('color', "#AA62C6");
            $('.table_title_manage').css('color', "#AA62C6");
            $('.table_title_Admin').css('color', "#AA62C6");
            $('.title_manage').css('color', '#AA62C6');
            $('.table_horizontal tr:first-child td').css('color', '#AA62C6');
            $('.table_vertical tr td:first-child').css('color', '#AA62C6');

            $("img[src*='bt_']").each(
                    function () {
                        var newSrc = $(this).attr('src');
                        var violetCheck = "/violet/";
                        var transparent = "Transparent";
                        if (newSrc.indexOf(violetCheck) == -1
                                && newSrc.indexOf(transparent) == -1) {
                            newSrc = newSrc.replace('images/', 'images/violet/');
                            $(this).attr('src', newSrc);
                        }
                    });

            $("img[src*='arrow_']").each(
                    function () {
                        var newSrc = $(this).attr('src');
                        var violetCheck = "/violet/";
                        var defaultArrow = "_dis";
                        if (newSrc.indexOf(violetCheck) == -1
                                && newSrc.indexOf(defaultArrow) == -1) {
                            newSrc = newSrc.replace('images/', 'images/violet/');
                            $(this).attr('src', newSrc);
                        }
                    });

            $('.jmesa').find("tr[class*='header']").find('td').css(
                    'background-color', '#AA62C6');
            $('.sidebar_tab_content').find('span').css('color', '#AA62C6');

            $('.tab_R_h').find('span').css('color', '#AA62C6');
            $('.jmesa').find("tr[class*='header']").find('td').css('color',
                    '#FFFFFF');
            $('.aka_revised_content').find('span.title_submit').css('color',
                    '#D4A718');

            $("img[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).attr('src', newSrc);
            });

            $("input[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/icon_SignedBlue.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/create_new.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/icon_NDs.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).attr('src', newSrc);
            });

            $("input.button_medium").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).css('background-image', newSrc);
            });

            $("input.button").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).css('background-image', newSrc);
            });

            $("input.button_long").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).css('background-image', newSrc);
            });

            $("input.button_xlong").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).css('background-image', newSrc);
            });

            $("button.button_remove").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/violet/');
                $(this).css('background-image', newSrc);
            });

            $('input.navSearchButton:submit').css('background-image',
                    'url(images/violet/bt_navSearch.gif)');
            $('input.button_search:submit').css('background-image',
                    'url(images/violet/button_Search_BG.gif)');
            $('input.button_search').css('background-image',
                    'url(images/violet/button_Search_BG.gif)');
            $('.sidebar').css('color', '#AA62C6');

            $("img[src*='images/code_blue.png']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/violet/') >= 0) return;
                newSrc = newSrc.replace('images/code_blue.png', 'images/violet/code_violet.png');
                $(this).attr('src', newSrc);
            });
        </script>
        <input id="themeColorCode" type="hidden" value="#AA62C6" />
    </c:when>
    <c:when test="${color == 'green'}">
        <script language="JavaScript" type="text/JavaScript">
            $('a').css('color', '#75b894');
            $('a').removeAttr('onmousedown');
            $('a').removeAttr('onmouseup');
            $('H3').css('color', '#75b894');
            $('H1').css('color', '#75b894');

            $(".nav_bt").find('a').css('color', '#FFFFFF');
            $('.oc_nav').find('.nav_bt').css("background-color", "#75b894");
            $('#bt_Home td, #bt_Home li').css("background-color", "#75b894");
            $('input.navSearch:text').css('background-color', "#c9e3d5");
            $('.table_cell_noborder').css('color', "#75b894");
            $('.table_cell_top').css('color', "#75b894");
            $('.table_header_column_top').css('color', "#75b894");
            $('.table_title_manage').css('color', "#75b894");
            $('.table_title_Admin').css('color', "#75b894");
            $('.title_manage').css('color', '#75b894');
            $('.table_horizontal tr:first-child td').css('color', '#75b894');
            $('.table_vertical tr td:first-child').css('color', '#75b894');

            $("img[src*='bt_']").each(function () {
                var newSrc = $(this).attr('src');
                var greenCheck = "/green/";
                var transparent = "Transparent";
                if (newSrc.indexOf(greenCheck) == -1 && newSrc.indexOf(transparent) == -1) {
                    newSrc = newSrc.replace('images/', 'images/green/');
                    $(this).attr('src', newSrc);
                }
            });

            $("img[src*='arrow_']").each(function () {
                var newSrc = $(this).attr('src');
                var greenCheck = "/green/";
                var defaultArrow = "_dis";
                if (newSrc.indexOf(greenCheck) == -1 && newSrc.indexOf(defaultArrow) == -1) {
                    newSrc = newSrc.replace('images/', 'images/green/');
                    $(this).attr('src', newSrc);
                }
            });

            $('.jmesa').find("tr[class*='header']").find('td').css('background-color', '#75b894');
            $('.sidebar_tab_content').find('span').css('color', '#75b894');

            $('.tab_R_h').find('span').css('color', '#75b894');
            $('.jmesa').find("tr[class*='header']").find('td').css('color', '#FFFFFF');
            $('.aka_revised_content').find('span.title_submit').css('color', '#D4A718');

            $("img[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).attr('src', newSrc);
            });

            $("input[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/icon_SignedBlue.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/create_new.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).attr('src', newSrc);
            });

            $("img[src*='images/icon_NDs.gif']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).attr('src', newSrc);
            });

            $("input.button_medium").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).css('background-image', newSrc);
            });

            $("input.button").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).css('background-image', newSrc);
            });

            $("input.button_long").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).css('background-image', newSrc);
            });

            $("input.button_xlong").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).css('background-image', newSrc);
            });

            $("button.button_remove").each(function () {
                var newSrc = $(this).css('background-image');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/', 'images/green/');
                $(this).css('background-image', newSrc);
            });

            $('input.navSearchButton:submit').css('background-image', 'url(images/green/bt_navSearch.gif)');
            $('input.button_search:submit').css('background-image', 'url(images/green/button_Search_BG.gif)');
            $('input.button_search').css('background-image', 'url(images/green/button_Search_BG.gif)');
            $('.sidebar').css('color', '#75b894');

            $("img[src*='images/code_blue.png']").each(function () {
                var newSrc = $(this).attr('src');
                if (newSrc.indexOf('/green/') >= 0) return;
                newSrc = newSrc.replace('images/code_blue.png', 'images/green/code_green.png');
                $(this).attr('src', newSrc);
            });
        </script>
        <input id="themeColorCode" type="hidden" value="#75b894" />
    </c:when>
    <c:otherwise>
        <input id="themeColorCode" type="hidden" value="#5B91C9" />
    </c:otherwise>
</c:choose>


