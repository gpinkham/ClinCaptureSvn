		jQuery.ajax({
		    url: '../HelpThemeServlet',
		    type: 'GET',
		    dataType: 'text',
		    success: function (response2) {
		        var themeColor = response2;
		        var colorHex;
		        var colorPath;
		        var colorCheck;
		        if (themeColor == 'violet') {
		            colorHex = '#AA62C6';
		            colorPath = 'images/violet/';
		            colorCheck = '/violet/';
		        }
		        if (themeColor == 'green') {
		            colorHex = '#75b894';
		            colorPath = 'images/green/';
		            colorCheck = '/green/';
		        }

		        if (themeColor != null && colorPath != null) {
		            $('.icon_right').css('color', colorHex);
		            $('a').css('color', colorHex);
		            $('H1').css('color', colorHex);
		            $("input").each(function () {
		                var newSrc = $(this).css('background-image');
		                newSrc = newSrc.replace('images/', colorPath);
		                $(this).css('background-image', newSrc);
		            });
		            $("img[src*='bt_']").each(
		                function () {
		                    var newSrc = $(this).attr('src');
		                    var transparent = "Transparent";
		                    if (newSrc.indexOf(colorCheck) == -1 && newSrc.indexOf(transparent) == -1) {
		                        newSrc = newSrc.replace('images/', colorPath);
		                        $(this).attr('src', newSrc);
		                    }
		                });

		            $("img[src*='arrow_']").each(
		                function () {
		                    var newSrc = $(this).attr('src');
		                    var defaultArrow = "_dis";
		                    if (newSrc.indexOf(colorCheck) == -1 && newSrc.indexOf(defaultArrow) == -1) {
		                        newSrc = newSrc.replace('images/', colorPath);
		                        $(this).attr('src', newSrc);
		                    }
		                });

		            $("img[src*='images/create_new.gif']").each(function () {
		                var newSrc = $(this).attr('src');
		                newSrc = newSrc.replace('images/', colorPath);
		                $(this).attr('src', newSrc);
		            });
					
					$("img[src*='images/popup_Help.gif']").each(function () {
		                var newSrc = $(this).attr('src');
		                newSrc = newSrc.replace('images/', colorPath);
		                $(this).attr('src', newSrc);
		            });
		        }

		    }
		});