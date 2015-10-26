var getTheme = function(themeColor) {
    var theme;
    try {
        theme = eval(themeColor + 'Theme');
    } catch (e) {
        theme = eval('blueTheme');
    }
    return theme;
}

var deleteHideStuff = function() {
    var s, styles = document.getElementsByTagName('style');
    var i = styles.length;
    while (i--) {
        s = styles[i];
        if (s.className == 'hideStuff') {
            s.parentNode.removeChild(s);
            break;
        }
    }
}

var helpPageApplyTheme = function() {
    jQuery.ajax({
        url: '../HelpThemeServlet',
        type: 'GET',
        dataType: 'text',
        success: function (response2) {
            var theme = getTheme(response2);
            $('.icon_right').css('color', theme.mainColor);
            $('a').css('color', theme.mainColor);
            $('H1').css('color', theme.mainColor);
            if (theme.name != 'blue') {
            	$("input.button_medium").not(".medium_back, .medium_cancel, .medium_continue, .medium_submit").each(function () { 
                    var newSrc = $(this).css('background-image');
                    if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
                    newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                    $(this).css('background-image', newSrc);
                });
                
            	$("input.button_long").not(".long_back, .long_cancel, .long_continue").each(function () {
                    var newSrc = $(this).css('background-image');
                    if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
                    newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                    $(this).css('background-image', newSrc);
                });
                
                $("img[src*='bt_']").each(
                    function () {
                        var newSrc = $(this).attr('src');
                        var transparent = "Transparent";
                        if (newSrc.indexOf('/' + theme.name + '/') == -1 && newSrc.indexOf(transparent) == -1) {
                            newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                            $(this).attr('src', newSrc);
                        }
                    });

                $("img[src*='arrow_']").each(
                    function () {
                        var newSrc = $(this).attr('src');
                        var defaultArrow = "_dis";
                        if (newSrc.indexOf('/' + theme.name + '/') == -1 && newSrc.indexOf(defaultArrow) == -1) {
                            newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                            $(this).attr('src', newSrc);
                        }
                    });

                $("img[src*='images/create_new.gif']").each(function () {
                    var newSrc = $(this).attr('src');
                    newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                    $(this).attr('src', newSrc);
                });

                $("img[src*='images/popup_Help.gif']").each(function () {
                    var newSrc = $(this).attr('src');
                    newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
                    $(this).attr('src', newSrc);
                });
            }
        }
    });
}

var loginPageApplyTheme = function(themeColor) {
    var theme = getTheme(themeColor);
    jQuery('a').css('color', theme.mainColor);
    jQuery('H1').css('color', theme.mainColor);
    if (theme.name != 'blue') {
        jQuery("input").not(".cancel").each(function() {
            var newSrc = jQuery(this).css('background-image');
            if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
            newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
            jQuery(this).css('background-image', newSrc);
        });
    }
}

var applyCommonStylesForTheme = function(theme) {
    $('a').css('color', theme.mainColor);
    $('a').removeAttr('onmousedown');
    $('a').removeAttr('onmouseup');
    $('H3').css('color', theme.mainColor);
    $('H1').css('color', theme.mainColor);

    $('input.navSearch:text').css('background-color', theme.navSearchColor);
    $('.table_cell_noborder').css('color', theme.mainColor);
    $('.header_crf_cell').css('color', theme.mainColor);
    $('.table_cell_top').css('color', theme.mainColor);
    $('.table_cell_br_colored').css('color', theme.mainColor);
    $('.table_header_column_top').css('color', theme.mainColor);
    $('.table_title_manage').css('color', theme.mainColor);
    $('.table_title_Admin').css('color', theme.mainColor);
    $('.title_manage').css('color', theme.mainColor);
    $('.table_horizontal tr:first-child td').css('color', theme.mainColor);
    $('.table_vertical tr td:first-child').css('color', theme.mainColor);

    $(".nav_bt").find('a').css('color', theme.navBtCollor);
    $('.oc_nav').find('.nav_bt').css('background-color', theme.mainColor);

    $("img[src*='bt_']").each(function () {
        var newSrc = $(this).attr('src');
        var check = "/" + theme.name + "/";
        var transparent = "Transparent";
        if (newSrc.indexOf(check) == -1 && newSrc.indexOf(transparent) == -1) {
            newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
            $(this).attr('src', newSrc);
        }
    });

    $("img[src*='arrow_']").each(function () {
        var newSrc = $(this).attr('src');
        var check = "/" + theme.name + "/";
        var defaultArrow = "_dis";
        if (newSrc.indexOf(check) == -1 && newSrc.indexOf(defaultArrow) == -1) {
            newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
            $(this).attr('src', newSrc);
        }
    });

    $('.jmesa').find("tr[class*='header']").find('td').css('background-color', theme.mainColor);
    $('.sidebar_tab_content').find('span').css('color', theme.mainColor);

    $('.tab_R_h').find('span').css('color', theme.mainColor);
    $('.jmesa').find("tr[class*='header']").find('td').css('color', theme.jmesaTdHeaderColor);
    $('.aka_revised_content').find('span.title_submit').css('color', theme.headerColor);

    $("img[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).attr('src', newSrc);
    });

    $("input[src*='images/icon_DoubleCheck_Action.gif']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).attr('src', newSrc);
    });

    $("img[src*='images/icon_SignedBlue.gif']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).attr('src', newSrc);
    });

    $("img[src*='images/create_new.gif']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).attr('src', newSrc);
    });

    $("img[src*='images/icon_NDs.gif']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).attr('src', newSrc);
    });

    $("input.button_medium").not(".medium_back, .medium_cancel, .medium_continue, .medium_submit").each(function () {
        var newSrc = $(this).css('background-image');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).css('background-image', newSrc);
    });

    $("input.button").not(".cancel, .submit").each(function () {
        var newSrc = $(this).css('background-image');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).css('background-image', newSrc);
    });

    $("input.button_long").not(".long_back, .long_cancel, .long_continue, .long_submit").each(function () {
        var newSrc = $(this).css('background-image');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).css('background-image', newSrc);
    });

    $("input.button_xlong").not(".xlong_submit").each(function () {
        var newSrc = $(this).css('background-image');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).css('background-image', newSrc);
    });

    $("button.button_remove").each(function () {
        var newSrc = $(this).css('background-image');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/', 'images/' + theme.name + '/');
        $(this).css('background-image', newSrc);
    });

    $('button.button_add').css('background-image', 'url(images/' + theme.name + '/create_new.gif)');
    $('.sidebar').css('color', theme.mainColor);

    $("img[src*='images/code_blue.png']").each(function () {
        var newSrc = $(this).attr('src');
        if (newSrc.indexOf('/' + theme.name + '/') >= 0) return;
        newSrc = newSrc.replace('images/code_blue.png', 'images/' + theme.name + '/code_' + theme.name + '.png');
        $(this).attr('src', newSrc);
    });

    $('.aka_revised_content').find('h1 span').css('color', theme.headerColor);
    $('.aka_revised_content').find('h3 span').css('color', theme.headerColor);
    $('.first_level_header').css('color', theme.headerColor);
    $('.table_title_Manage').css('color', theme.headerColor);
}

var blueTheme = {
    name: 'blue',
    navBtCollor: '',
    mainColor: '#5B91C9',
    navSearchColor: '',
    headerColor: '#D4A718',
    jmesaTdHeaderColor: '',
    applyCommonStyles: function () { },
    applyCustomStyles: function () { }
}

var darkBlueTheme = {
    name: 'darkBlue',
    mainColor: '#2C6CAF',
    navBtCollor: '#FFFFFF',
    navSearchColor: '#DBC4E4',
    headerColor: '#FF7C00',
    jmesaTdHeaderColor: '#FFFFFF',
    applyCommonStyles: function () {
        applyCommonStylesForTheme(this);
    },
    applyCustomStyles: function () {
        var elements = $('#bt_Home td, #bt_Home li');
        if (elements.css('background-color') != 'transparent') {
            elements.css('background-color', this.mainColor);
        }
    }
}

var greenTheme = {
    name: 'green',
    mainColor: '#75B894',
    navBtCollor: '#FFFFFF',
    navSearchColor: '#C9E3D5',
    headerColor: '#D4A718',
    jmesaTdHeaderColor: '#FFFFFF',
    applyCommonStyles: function () {
        applyCommonStylesForTheme(this);
    },
    applyCustomStyles: function () {
        $('#bt_Home td, #bt_Home li').css("background-color", this.mainColor);
    }
}

var violetTheme = {
    name: 'violet',
    mainColor: '#AA62C6',
    navBtCollor: '#FFFFFF',
    navSearchColor: '#DBC4E4',
    headerColor: '#D4A718',
    jmesaTdHeaderColor: '#FFFFFF',
    applyCommonStyles: function () {
        applyCommonStylesForTheme(this);
    },
    applyCustomStyles: function () {
        var elements = $('#bt_Home td, #bt_Home li');
        if (elements.css('background-color') != 'transparent') {
            elements.css('background-color', this.mainColor);
        }
    }
}

var applyThemeForChart = function() {
    try {
        if (theme) {
            theme.applyCommonStyles();
            theme.applyCustomStyles();
        }
    } catch (e) {}
}

if (location.href.toString().toLowerCase().indexOf('/login/') > 0) {
    var colorUrl = '../../HelpThemeServlet';
    jQuery.ajax({
        url : colorUrl,
        type : 'GET',
        dataType : 'text',
        success : function(response2) {
            loginPageApplyTheme(response2);
        }

    });
} else if (location.href.toString().toLowerCase().indexOf('/help/') > 0) {
    var colorUrl = '../HelpThemeServlet';
    jQuery.ajax({
        url: colorUrl,
        type: 'GET',
        dataType: 'text',
        success: function (response2) {
            helpPageApplyTheme(response2);
        }
    });
}
