function tollbarToogle(e){
	if ($(e).children().text() == "Hide"){
		$(e).parent().animate({right: "-412px"}, 500);
		$(e).children().html("Show");
	} else {
		$(e).parent().animate({right: "0px"}, 500);
		$(e).children().html("Hide");
	}
}

if (!Array.prototype.forEach) {
    Array.prototype.forEach = function(fn, scope) {
        for(var i = 0, len = this.length; i < len; ++i) {
            fn.call(scope, this[i], i, this);
        }
    }
}

function inicializeDnD(){
	$(".droptrue").sortable({
			connectWith: '.droptrue',
			opacity: 0.6,
			update: updatePostOrder
		});

		$("#toolbar, #layout").disableSelection();
		$(".widget .chart_wrapper a").attr("href", "#");
		updatePostOrder();
}

function updatePostOrder() {
		var arr = [];
		$(".column1 div.widget").each(function () {
			arr.push($(this).attr('id'));
		});
		$('#postOrder1').val(arr.join(','));
		
		var arr2 = [];
		$(".column2 div.widget").each(function () {
			arr2.push($(this).attr('id'));
		});
		$('#postOrder2').val(arr2.join(','));
		
		var arr3 = [];
		$("#toolbar div.widget").each(function () {
			arr3.push($(this).attr('id'));
		});
		$('#unusedWidgets').val(arr3.join(','));
}

function removeWidget(elemet){
	var itemToRemove = $(elemet).parent();
		$("#toolbar").prepend(itemToRemove);
		updatePostOrder();
}

function saveLayoutAndExit(){
	$.ajax({
		type:"POST",
		url:"saveHomePage",
		data:{
			orderInColumn1: $("#postOrder1").val(),
			orderInColumn2: $("#postOrder2").val(),
			unusedWidgets: $("#unusedWidgets").val(),
			userId: $("#userId").val(),
			studyId: $("#studyId").val()
			},
		success:function() {			
			$("form[id=goToHomePage]").submit();
		},
		error:function(e){
			console.log("Error:" + e);
		}
	});	
}

function initNdsAssignedToMeWidget(){	
	var url = getCurentUrl();
	
	$.ajax({		
		type:"POST",
		url:url + "initNdsAssignedToMeWidget",
		data:{		
			userId: $("#userId").val(),
			studyId: $("#studyId").val()
			},
		success:function(html) {
			var array = html.split(',');			
			var newNds = parseInt(array[0]);
			var updatedNds = parseInt(array[1]);
			var resolutionProposedDns = parseInt(array[2]);
			var closedNds = parseInt(array[3]);

			if (resolutionProposedDns != 0) {
				$(".widget table.signs td.optional").css("display","table-cell");
			}
			
			var totalNds = newNds + updatedNds + closedNds + resolutionProposedDns;
			captionLimit = countCaptionLimit(totalNds);				
			var captionSelector = ".dns_assigned_to_me .captions td";
			setCaption (captionSelector, captionLimit);
			var valuesSecelctor = ".dns_assigned_to_me #stack";
			setStacksLenghts (valuesSecelctor,array,captionLimit);
		},
		error:function(e){
			console.log("Error:" + e);
		}
	});
}


function updateStucksNumbers(){
	$(".events_completion #stacked_bar").each(function(index){	
				$(this).attr("id","stacked_bar"+index);
	});
}

function getEventsCompletionRow(){
	var url = getCurentUrl();
	
	$.ajax({		
		type:"POST",
		url:url + "eventCompletionRow",
		data:{},
		success:function(html) {
			$(".events_completion .chart_wrapper").append(html);			
		},
		error:function(e){
			console.log("Error:" + e);
		}
	});
}

function countCaptionLimit(total){
	if (total < 4) {
		total=4;
	} else if (total%2==0) {
		(total/2)%2==0 ? total : total = total+2;
	} else {
		(total+1)%4==0 ? total = total+1: total = total+3;
	}
	return total;
}

function setCaption (selector, maxValue){
	var counter = 0;
	$(selector).each(function(){
		if(counter==0){
			$(this).html(0);
		} else if(counter==1){
			$(this).html(maxValue/4);
		} else if(counter==2){
			$(this).html(maxValue/2);
		} else if(counter==3){
			$(this).html(maxValue/4*3);
		} else if(counter==4){
			$(this).html(maxValue);
		}
		counter++;
	});
}

function setBarName(names, selector){
	$(selector).each(function(index){				
		$(this).html(names[index]);
	});
}

function setStacksLenghts(selector, values, captionLimit){	
	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	var counter = 0;
	
	$(selector).each(function(){
		var stackWidth = parseInt(values[counter],10)*unitSize;		
		
		$(this).animate({width: stackWidth}, 500);	
		$(this).find("#pop-up").css("margin-left",(parseInt(values[counter])*unitSize)/2).html(parseInt(values[counter]));
		counter++;
	});
}

function getCurentUrl(){
	var urlTemp = new RegExp("^.*(pages)").exec(window.location.href.toString());
	var url = "";

	if(urlTemp==null){
		url="pages/";
	} else {
		url="";
	}
	return url;
}
