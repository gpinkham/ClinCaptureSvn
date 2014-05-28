// New tab functions 8-8-06

var TabValue = 0;
var PrevTabNumber = 0;

	  
function IncreaseTabValue() 
	{
	TabValue = TabValue+1;
	}

function DecreaseTabValue() 
	{
	TabValue = TabValue-1;
	}

function AdvanceTabs()
	{
	leftnavExpand('Tab' + TabValue); 
	leftnavExpand('Tab' + (TabValue+TabsShown));
	}

function EnableArrows()
	{

	if (TabValue==0)
		{
		leftnavExpand('TabsBackDis');
		leftnavExpand('TabsBack');
		}
	else {}

	if (TabValue==TabsNumber-(TabsShown+1))
		{
		leftnavExpand('TabsNextDis');
		leftnavExpand('TabsNext');
		}
	else {}

}

function HighlightTab(TabNumber)
	{
	leftnavExpand('Tab' + TabNumber + 'NotSelected'); 
	leftnavExpand('Tab' + TabNumber + 'Selected');
	leftnavExpand('Table' + TabNumber);

	if (PrevTabNumber>=0)
		{
		leftnavExpand('Tab' + PrevTabNumber + 'NotSelected'); 
		leftnavExpand('Tab' + PrevTabNumber + 'Selected');
		leftnavExpand('Table' + PrevTabNumber);
		}

	PrevTabNumber=TabNumber
	}

function TabsBack()
	{
	AdvanceTabs();
	DecreaseTabValue();
	EnableArrows();
	}
	
function TabsForwardByNum(num)
	{
	var i=1;
	while (i<num){
	TabsForward();
	i++;
	}
}	

function TabsForward()
	{
	EnableArrows();
	IncreaseTabValue();
	AdvanceTabs();
	}

function DisplayTabs()
	{
	TabID=1;

	while (TabID<=TabsNumber)
		{
		if (TabID<=TabsShown)
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: all" width="180">');
			}
		else
			{
			document.write('<td valign="bottom" id="Tab' + TabID + '" style="display: none" width="180">');
			}
		document.write('<div id="Tab' + TabID + 'NotSelected" style="display:all"><div class="tab_BG"><div class="tab_L"><div class="tab_R">');
		document.write('<a class="tabtext" href="javascript:HighlightTab(' + TabID + ');">' + TabLabel[(TabID-1)] + '</a></div></div></div></div>');
		document.write('<div id="Tab' + TabID + 'Selected" style="display:none"><div class="tab_BG_h"><div class="tab_L_h"><div class="tab_R_h"><span class="tabtext">' + TabLabel[(TabID-1)] + '</span></div></div></div></div>');
		document.write('</td>');
	
		TabID++
		}
	}

function HideGroups(TableID, GroupsColumns,GroupsRows)
	{
	leftnavExpand('HideGroups');
	leftnavExpand('ShowGroups');
	ColumnNumber=0;
	RowNumber=0;
	while (ColumnNumber<=GroupsColumns)
		{
		while (RowNumber<=GroupsRows)
			{
			leftnavExpand('Groups_' + TableID + '_' + ColumnNumber + '_' + RowNumber)
			RowNumber++
			}
		RowNumber=0;
		ColumnNumber++
		}
	}
	
	
// new functions for scrolling status boxes 06/15/07

function SetStatusBoxValue(StatusBoxID,StatusBoxNum) 
	{
	if (StatusBoxValue > 1)
		{
		StatusBoxSkip(StatusBoxID,StatusBoxNum,1)
		}
	StatusBoxValue=1;
	}

function IncreaseStatusBoxValue() 
	{
	StatusBoxValue = StatusBoxValue+1;
	}

function DecreaseStatusBoxValue() 
	{
	StatusBoxValue = StatusBoxValue-1;
	}

function StatusBoxNext(StatusBoxID,StatusBoxNum)
	{
	NextStatusBox = StatusBoxValue+3;
	leftnavExpand('Event_' + StatusBoxID + '_' + NextStatusBox);
	leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
	if (NextStatusBox==StatusBoxNum)
		{
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
		}
	if (StatusBoxValue==1)
		{
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
		}
	StatusBoxValue = StatusBoxValue+1;
	}

function StatusBoxBack(StatusBoxID,StatusBoxNum)
	{
	StatusBoxValue = StatusBoxValue-1;
	NextStatusBox = StatusBoxValue+3;
	leftnavExpand('Event_' + StatusBoxID + '_' + NextStatusBox);
	leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
	if (NextStatusBox==(StatusBoxNum))
		{
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
		}
	if (StatusBoxValue==1)
		{
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
		}
	}

function StatusBoxSkip(StatusBoxID,StatusBoxNum,StatusBoxJumpTo)
	{
	if (StatusBoxJumpTo >= (StatusBoxNum-2))
		{
		StatusBoxJumpTo = StatusBoxNum-2;
		}
	if (StatusBoxJumpTo != StatusBoxValue)
		{
		leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
		leftnavExpand('Event_' + StatusBoxID + '_' + (StatusBoxValue+1));
		leftnavExpand('Event_' + StatusBoxID + '_' + (StatusBoxValue+2));
		leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxJumpTo);
		leftnavExpand('Event_' + StatusBoxID + '_' + (StatusBoxJumpTo+1));
		leftnavExpand('Event_' + StatusBoxID + '_' + (StatusBoxJumpTo+2));
		if (StatusBoxNum==(StatusBoxValue+2))
			{
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
			}
		if (StatusBoxNum==(StatusBoxJumpTo+2))
			{
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
			}
		if (StatusBoxValue==1)
			{
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
			}
		if (StatusBoxJumpTo==1)
			{
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
			leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
			}
		StatusBoxValue=StatusBoxJumpTo;
		}
	}

function EnableScrollArrows(StatusBoxID,StatusBoxNum)
	{
	leftnavExpand('Scroll_off_' + StatusBoxID + '_back');
	leftnavExpand('Scroll_on_' + StatusBoxID + '_back');
	leftnavExpand('Scroll_off_' + StatusBoxID + '_next');
	leftnavExpand('Scroll_on_' + StatusBoxID + '_next');
	if (StatusBoxNum <= 3)
		{
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
		leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
		}
	}

//added on 06/22/07
function ExpandEventOccurrences(StatusBoxID,StatusBoxNum)
	{
	SetStatusBoxValue(StatusBoxID,StatusBoxNum) 
	EnableScrollArrows(StatusBoxID,StatusBoxNum);
	while (StatusBoxNum > 0)
		{
		leftnavExpand('Menu_on_' + StatusBoxID + '_' + StatusBoxNum);
		StatusBoxNum --
		}
	}

// clinovo ....
// custom methods

function StatusBoxNext2(StatusBoxID,StatusBoxNum) {
    NextStatusBox = StatusBoxValue + 1;
    leftnavExpand('Event_' + StatusBoxID + '_' + NextStatusBox);
    leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
    if (NextStatusBox == StatusBoxNum) {
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
    }
    if (StatusBoxValue == 1) {
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
    }
    StatusBoxValue = StatusBoxValue + 1;
    loadCRFList(StatusBoxID,StatusBoxValue);
}

function StatusBoxBack2(StatusBoxID,StatusBoxNum) {
    NextStatusBox = StatusBoxValue - 1;
    leftnavExpand('Event_' + StatusBoxID + '_' + NextStatusBox);
    leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
    if (NextStatusBox == (StatusBoxNum - 1)) {
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
    }
    if (NextStatusBox == 1) {
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back');
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_back_dis');
    }
    StatusBoxValue = StatusBoxValue - 1;
    loadCRFList(StatusBoxID,StatusBoxValue);
}

function StatusBoxSkip2(StatusBoxID,StatusBoxNum,StatusBoxJumpTo) {
    if (StatusBoxJumpTo != StatusBoxValue) {
        leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxValue);
        leftnavExpand('Event_' + StatusBoxID + '_' + StatusBoxJumpTo);
        if (StatusBoxJumpTo == StatusBoxNum) {
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next').style.display = "none";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next_dis').style.display = "";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back').style.display = "";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back_dis').style.display = "none";
        } else
        if (StatusBoxJumpTo == 1) {
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next').style.display = "";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next_dis').style.display = "none";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back').style.display = "none";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back_dis').style.display = "";
        } else  {
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next').style.display = "";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next_dis').style.display = "none";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back').style.display = "";
            document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back_dis').style.display = "none";
        }
        StatusBoxValue = StatusBoxJumpTo;
        loadCRFList(StatusBoxID,StatusBoxValue);
    }
}

function EnableScrollArrows2(StatusBoxID, StatusBoxNum) {
    StatusBoxValue = 1;
    for (var i = 1; i <= StatusBoxNum; i++) {
    	
    	if(document.getElementById('Event_' + StatusBoxID + '_' + i) != undefined){
    		if (i == 1 ) {
                document.getElementById('Event_' + StatusBoxID + '_' + i).style.display = "";
            } else {
                document.getElementById('Event_' + StatusBoxID + '_' + i).style.display = "none";
            }
    	}
        
        if (document.getElementById('Menu_off_' + StatusBoxID + '_' + i) != null) {
            document.getElementById('Menu_off_' + StatusBoxID + '_' + i).style.display = "none";
        }
        document.getElementById('Menu_on_' + StatusBoxID + '_' + i).style.display = "";
    }
    if (StatusBoxNum > 1) {
        document.getElementById('Scroll_off_' + StatusBoxID + '_back').style.display = "none";
        document.getElementById('Scroll_on_' + StatusBoxID + '_back').style.display = "";
        document.getElementById('Scroll_off_' + StatusBoxID + '_next').style.display = "none";
        document.getElementById('Scroll_on_' + StatusBoxID + '_next').style.display = "";

        document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next').style.display = "";
        document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next_dis').style.display = "none";
    } else {
        document.getElementById('Scroll_off_' + StatusBoxID + '_back').style.display = "";
        document.getElementById('Scroll_on_' + StatusBoxID + '_back').style.display = "none";
        document.getElementById('Scroll_off_' + StatusBoxID + '_next').style.display = "";
        document.getElementById('Scroll_on_' + StatusBoxID + '_next').style.display = "none";

        document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next').style.display = "none";
        document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_next_dis').style.display = "";
    }
    document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back').style.display = "none";
    document.getElementById('bt_Scroll_Event_' + StatusBoxID + '_back_dis').style.display = "";
    if (StatusBoxNum == 1) {
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next_dis');
        leftnavExpand('bt_Scroll_Event_' + StatusBoxID + '_next');
    }
    loadCRFList(StatusBoxID,1);
}

function adjustCrfListTable2(studyEventId, StatusBoxID, StatusBoxNum) {
  var wereRemoved = 0;
  var popupTotalColumns = 8;
  var idAttribute = StatusBoxID + "_" + StatusBoxNum;

  var hideCol = function(num) {
    $(".crfListTable tr").find("td:last").find("img:eq(" + (num - wereRemoved) + ")").remove();
    popupTotalColumns--;
    wereRemoved++;
  }

  if ($("#crfListWrapper_" + idAttribute + " #hideCol1").val() == 'true') hideCol(0);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol2").val() == 'true') hideCol(1);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol3").val() == 'true') hideCol(2);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol4").val() == 'true') hideCol(3);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol5").val() == 'true') hideCol(4);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol6").val() == 'true') hideCol(5);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol7").val() == 'true') hideCol(6);
  if ($("#crfListWrapper_" + idAttribute + " #hideCol8").val() == 'true') hideCol(7);

  $("#popupTotalColumns").val(popupTotalColumns);

  var crfActionIconWidth = 33;
  var crfActionsMaxIconsCount = parseInt($("#popupTotalColumns").val());
  crfActionsMaxIconsCount = (crfActionsMaxIconsCount < 2 ? 2 : crfActionsMaxIconsCount);
  jQuery('#crfListWrapper_' + idAttribute + ' .crfListTableActions').attr("style", "width: " + (crfActionIconWidth * crfActionsMaxIconsCount) + "px;");

  $(".crfListTable tr").find("td:first").each(function() {
    $(this).attr("style", "border-left : none !important;" + $(this).attr("style"));
  });
}

function loadCRFList(StatusBoxID, StatusBoxNum) {
	var idAttribute = StatusBoxID + "_" + StatusBoxNum;
    var href = jQuery("tr[id^='Menu_on_" + idAttribute + "'] a[href^='UpdateStudyEvent']").attr("href");
    jQuery("a#" + StatusBoxID).attr("href", href);
    var eventElement = document.getElementById('Event_' + idAttribute);
    var studyEventId = parseInt(eventElement.getAttribute("rel"));
    jQuery('.crfListTable').remove();
    jQuery('#crfListWrapper_' + idAttribute).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
    jQuery('#crfListWrapper_' + idAttribute).css("height", "18px");
    jQuery.ajax({
        url: "CRFListForStudyEvent",
        type: "GET",
        data: {studyEventId: studyEventId},
        cache: false,
        success: function (data) {
            jQuery('#crfListWrapper_' + idAttribute).css("height", "auto");
            jQuery('#crfListWrapper_' + idAttribute).html(data);
            adjustCrfListTable2(studyEventId, StatusBoxID, StatusBoxNum);
        }
    });
}
