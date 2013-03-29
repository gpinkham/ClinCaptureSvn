<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
	
	
	<c:set var="color" scope="session" value="${newThemeColor}"/>
	<c:if test="${(color == 'violet')}">
	<script language="JavaScript" type="text/JavaScript">
		
		if(window.$jq == null) {
		var jjq = jQuery;
		}
		else {
			var jjq = $jq;
		}
		
		window.onload = function() {
		setTimeout(
			function(){
				var s, styles = document.getElementsByTagName('style');
				var i = styles.length;
				while (i--) {
				s = styles[i];
				if (s.className == 'hideStuff') {
					s.parentNode.removeChild(s);
					return;
					}
				}
			}, 300);
		}
		
		jjq('a').css('color','#AA62C6');
		jjq('a').removeAttr('onmousedown');
		jjq('a').removeAttr('onmouseup');
		jjq('H3').css('color', '#AA62C6');
		jjq('H1').css('color', '#AA62C6');
		
		jjq(".nav_bt").find('a:link').css('color','#FFFFFF');
		jjq('.oc_nav').find('.nav_bt').css("background-color", "#AA62C6");
		jjq('#bt_Home td, #bt_Home li').css("background-color", "#AA62C6");
		jjq('input.navSearch:text').css('background-color', "#dbc4e4");
		jjq('.table_cell_noborder').css('color', "#AA62C6");
		jjq('.table_cell_top').css('color', "#AA62C6");
		jjq('.table_header_column_top').css('color', "#AA62C6");
		jjq('.table_title_manage').css('color', "#AA62C6");
		jjq('.title_manage').css('color', '#AA62C6');
		
		jjq("img[src*='bt_']").each(function() {
			var newSrc = jjq(this).attr('src');
			var violetCheck = "/violet/";
				if(newSrc.indexOf(violetCheck) == -1){
				newSrc = newSrc.replace('images/','images/violet/');
				jjq(this).attr('src', newSrc);
			} 
		});
		
		jjq("img[src*='arrow_']").each(function() {
			var newSrc = jjq(this).attr('src');
			var violetCheck = "/violet/";
			var defaultArrow = "_dis";
				if(newSrc.indexOf(violetCheck) == -1 && newSrc.indexOf(defaultArrow) == -1){
				newSrc = newSrc.replace('images/','images/violet/');
				jjq(this).attr('src', newSrc);
			} 
		});
		
		jjq('.jmesa').find("tr[class*='header']").find('td').css('background-color','#AA62C6');
		jjq('.sidebar_tab_content').find('span').css('color', '#AA62C6');

		jjq('.tab_R_h').find('span').css('color', '#AA62C6');
		jjq('.jmesa').find("tr[class*='header']").find('td').css('color','#FFFFFF');
		jjq('.aka_revised_content').find('span.title_submit').css('color','#D4A718');

		jjq("img[src*='images/icon_DoubleCheck_Action.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).attr('src', newSrc);
		});
		jjq("input[src*='images/icon_DoubleCheck_Action.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).attr('src', newSrc);
		});
				
		jjq("img[src*='images/icon_SignedBlue.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).attr('src', newSrc);
		});
	
		jjq("img[src*='images/create_new.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).attr('src', newSrc);
		});
		
		jjq("img[src*='images/icon_NDs.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).attr('src', newSrc);
		});
		
		jjq("input.button_medium").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).css('background-image', newSrc);
		});

		jjq("input.button").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq("input.button_long").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq("input.button_xlong").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq("button.button_remove").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/violet/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq('input.navSearchButton:submit').css('background-image', 'url(images/violet/bt_navSearch.gif)');
		jjq('input.button_search:submit').css('background-image', 'url(images/violet/button_Search_BG.gif)');
		jjq('button.button_search').css('background-image', 'url(images/violet/button_Search_BG.gif)');

	</script>
     </c:if>
	 <c:if test="${(color == 'green')}">
	<script language="JavaScript" type="text/JavaScript">
		
		if(window.$jq == null) {
		var jjq = jQuery;
		}
		else {
			var jjq = $jq;
		}
		
		window.onload = function() {
		setTimeout(
			function(){
				var s, styles = document.getElementsByTagName('style');
				var i = styles.length;
				while (i--) {
				s = styles[i];
				if (s.className == 'hideStuff') {
					s.parentNode.removeChild(s);
					return;
					}
				}
			}, 300);
		}
		
		jjq('a').css('color','#75b894');
		jjq('a').removeAttr('onmousedown');
		jjq('a').removeAttr('onmouseup');
		jjq('H3').css('color', '#75b894');
		jjq('H1').css('color', '#75b894');
		
		jjq(".nav_bt").find('a:link').css('color','#FFFFFF');
		jjq('.oc_nav').find('.nav_bt').css("background-color", "#75b894");
		jjq('#bt_Home td, #bt_Home li').css("background-color", "#75b894");
		jjq('input.navSearch:text').css('background-color', "#c9e3d5");
		jjq('.table_cell_noborder').css('color', "#75b894");
		jjq('.table_cell_top').css('color', "#75b894");
		jjq('.table_header_column_top').css('color', "#75b894");
		jjq('.table_title_manage').css('color', "#75b894");
		jjq('.title_manage').css('color', '#75b894');
		
		jjq("img[src*='bt_']").each(function() {
			var newSrc = jjq(this).attr('src');
			var greenCheck = "/green/";
				if(newSrc.indexOf(greenCheck) == -1){
				newSrc = newSrc.replace('images/','images/green/');
				jjq(this).attr('src', newSrc);
			} 
		});
		
		jjq("img[src*='arrow_']").each(function() {
			var newSrc = jjq(this).attr('src');
			var greenCheck = "/green/";
			var defaultArrow = "_dis";
				if(newSrc.indexOf(greenCheck) == -1 && newSrc.indexOf(defaultArrow) == -1){
				newSrc = newSrc.replace('images/','images/green/');
				jjq(this).attr('src', newSrc);
			} 
		});
		
		jjq('.jmesa').find("tr[class*='header']").find('td').css('background-color','#75b894');
		jjq('.sidebar_tab_content').find('span').css('color', '#75b894');

		jjq('.tab_R_h').find('span').css('color', '#75b894');
		jjq('.jmesa').find("tr[class*='header']").find('td').css('color','#FFFFFF');
		jjq('.aka_revised_content').find('span.title_submit').css('color','#D4A718');
		
		jjq("img[src*='images/icon_DoubleCheck_Action.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).attr('src', newSrc);
		});
		jjq("input[src*='images/icon_DoubleCheck_Action.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).attr('src', newSrc);
		});
				
		jjq("img[src*='images/icon_SignedBlue.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).attr('src', newSrc);
		});
	
		jjq("img[src*='images/create_new.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).attr('src', newSrc);
		});
		
		jjq("img[src*='images/icon_NDs.gif']").each(function() {
			var newSrc = jjq(this).attr('src');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).attr('src', newSrc);
		});
		
		jjq("input.button_medium").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).css('background-image', newSrc);
		});

		jjq("input.button").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq("input.button_long").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq("input.button_xlong").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).css('background-image', newSrc);
		});
	
		jjq("button.button_remove").each(function() {
			var newSrc = jjq(this).css('background-image');
			newSrc = newSrc.replace('images/','images/green/');
			jjq(this).css('background-image', newSrc);
		});
		
		jjq('input.navSearchButton:submit').css('background-image', 'url(images/green/bt_navSearch.gif)');
		jjq('input.button_search:submit').css('background-image', 'url(images/green/button_Search_BG.gif)');
		jjq('button.button_search').css('background-image', 'url(images/green/button_Search_BG.gif)');

	</script>
	 </c:if>
