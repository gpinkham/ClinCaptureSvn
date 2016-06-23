<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<script>
	var fsCrf = "${isFSCRF}" == "true";

	function conditionalShow(strLeftNavRowElementName) {
		var objLeftNavRowElement;
		var toShow = "false";

		objLeftNavRowElement = MM_findObj("t" + strLeftNavRowElementName);
		if (objLeftNavRowElement != null) {
			if (objLeftNavRowElement.style) {
				objLeftNavRowElement = objLeftNavRowElement.style;
			}
			if (objLeftNavRowElement.display == "none") {
				objLeftNavRowElement.display = "";
				toShow = "true";
				showRow(strLeftNavRowElementName);
			}
		}


		if (toShow == "true") {
			if (fsCrf) {
				var itemSelector = "#t" + strLeftNavRowElementName;
				if (!allItemsInTheRowAreHidden(itemSelector)) {
					var $row = getRowByItemSelector(itemSelector);
					$row.css("display", "");
					var rowNum = $row.attr("row-number");
					$(".item-subheader[row-number=" + rowNum + "]").css("display", "");
					$(".item-header[row-number=" + rowNum + "]").css("display", "");
				}
			} else {
				var objLeftNavRowElement1 = MM_findObj("hd" + strLeftNavRowElementName);
				if (objLeftNavRowElement1 != null) {
					if (objLeftNavRowElement1.style) {
						objLeftNavRowElement1 = objLeftNavRowElement1.style;
					}
					if (objLeftNavRowElement1.display == "none") {
						objLeftNavRowElement1.display = "";
					}
				}
				var objLeftNavRowElement2 = MM_findObj("sub" + strLeftNavRowElementName);
				if (objLeftNavRowElement2 != null) {
					if (objLeftNavRowElement2.style) {
						objLeftNavRowElement2 = objLeftNavRowElement2.style;
					}
					if (objLeftNavRowElement2.display == "none") {
						objLeftNavRowElement2.display = "";
					}
				}
			}
		}
	}

	function conditionalHide(strLeftNavRowElementName) {
		var objLeftNavRowElement;
		var toHide = "true";

		objLeftNavRowElement = MM_findObj("t" + strLeftNavRowElementName);
		if (objLeftNavRowElement != null) {
			var obj = MM_findObj("ft" + strLeftNavRowElementName);
			if (obj != null) {
				if (obj.value != "") {
					toHide = "false";
				}
			} else {
				obj = MM_findObj("a" + strLeftNavRowElementName);
				if (obj != null) {
					if (obj.value != "") {
						toHide = "false";
					}
				} else {
					obj = MM_findObj("input" + strLeftNavRowElementName);
					var type = obj.type;
					if (obj.value != "" && (type == "textarea" || type == "text" || type == "select-one" || type == "select-multiple")) {
						toHide = "false";
					} else if (obj.length > 0) {
						for (var i = 0; i < obj.length; ++i) {
							if (obj[i].checked && obj[i].value != "") {
								toHide = "false";
								break;
							}
						}
					}
				}
			}
			if (toHide == "true") {
				if (objLeftNavRowElement.style) {
					objLeftNavRowElement = objLeftNavRowElement.style;
				}
				if (objLeftNavRowElement.display == "none") {
					toHide = "false";
				} else {
					objLeftNavRowElement.display = "none";
					hideRow(strLeftNavRowElementName);
				}
			}
		}


		if (toHide == "true") {
			if (fsCrf) {
				var itemSelector = "#t" + strLeftNavRowElementName;
				if (allItemsInTheRowAreHidden(itemSelector)) {
					var $row = getRowByItemSelector(itemSelector);
					var rowNum = $row.attr("row-number");
					$row.css("display", "none");
					$(".item-subheader[row-number=" + rowNum + "]").css("display", "none");
					$(".item-header[row-number=" + rowNum + "]").css("display", "none");
				}
			} else {
				var objLeftNavRowElement1 = MM_findObj("hd" + strLeftNavRowElementName);
				if (objLeftNavRowElement1 != null) {
					if (objLeftNavRowElement1.style) {
						objLeftNavRowElement1 = objLeftNavRowElement1.style;
					}
					objLeftNavRowElement1.display = "none";
				}
				var objLeftNavRowElement2 = MM_findObj("sub" + strLeftNavRowElementName);
				if (objLeftNavRowElement2 != null) {
					if (objLeftNavRowElement2.style) {
						objLeftNavRowElement2 = objLeftNavRowElement2.style;
					}
					objLeftNavRowElement2.display = "none";
				}
			}
		}
	}

	function selectControlShow(element, scdPairStr) {
		var showIds = [];
		var n = 0;
		var m = 0;
		var hideIds = [];
		var arr = scdPairStr.split('-----');
		for (var j = 1; j < arr.length; j += 2) {
			hideIds[m] = arr[j];
			for (var i = 0; i < element.options.length; i++) {
				if (element.options[i].selected) {
					if (element.options[i].value == arr[j + 1]) {
						showIds[n] = arr[j];
						hideIds[m] = -1;
						++n;
					}
				}
			}
			++m;
		}
		for (var t = 0; t < showIds.length; ++t) {
			conditionalShow(showIds[t]);
		}
		for (var k = 0; k < hideIds.length; ++k) {
			if (hideIds[k] != -1) {
				conditionalHide(hideIds[k]);
			}
		}
	}

	function checkControlShow(element, scdPairStr) {
		var arr = scdPairStr.split('-----');
		for (var j = 1; j < arr.length; j += 2) {
			if (element.value == arr[j + 1]) {
				if (element.checked) {
					conditionalShow(arr[j]);
				} else {
					conditionalHide(arr[j]);
				}
			}
		}
	}

	function radioControlShow(element, scdPairStr) {
		var m = 0;
		var hideIds = [];
		var arr = scdPairStr.split('-----');
		for (var j = 1; j < arr.length; j += 2) {
			hideIds[m] = arr[j];
			if (element.value == arr[j + 1]) {
				if (element.checked) {
					conditionalShow(arr[j]);
					hideIds[m] = -1;
				}
			}
			++m;
		}
		for (var i = 0; i < hideIds.length; ++i) {
			if (hideIds[i] != -1) {
				conditionalHide(hideIds[i]);
			}
		}
	}

	function showRow(itemId) {
		var objCol = MM_findObj("col" + itemId);
		if (objCol != null) {
			var numOfTr = objCol.value;
			var objIDs = MM_findObj("rowSCDShowIDs" + numOfTr);
			var ids = objIDs.value;
			if (ids.length > 1) {
				if (ids.indexOf("-" + itemId + "-") == -1) {
					ids = ids + itemId + "-";
					objIDs.value = ids;
				}
			} else {
				ids = "-" + itemId + "-";
				objIDs.value = ids;
			}
			var objTr = MM_findObj("tr" + numOfTr);
			if (objTr != null && objTr.style.display == "none") {
				objTr.style.display = "";
			}
		}
	}

	function hideRow(itemId) {
		var objCol = MM_findObj("col" + itemId);
		if (objCol != null) {
			var numOfTr = objCol.value;
			var objIDs = MM_findObj("rowSCDShowIDs" + numOfTr);
			var ids = objIDs.value;
			if (ids.length > 1) {
				if (ids.indexOf("-" + itemId + "-") != -1) {
					ids = ids.replace(itemId + "-", "");
					objIDs.value = ids;
				}
			}
			if (ids.length <= 1) {
				var objTr = MM_findObj("tr" + numOfTr);
				if (objTr != null) {
					objTr.style.display = "none";
				}
			}
		}
	}

	function allItemsInTheRowAreHidden(selector) {
		var result = true;
		getRowByItemSelector(selector).find(".item-cell").each(function() {
			var $item = $(this);
			if ($item.css("display") != "none") {
				result = false;
			}
		});
		return result;
	}

	function getRowByItemSelector(selector) {
		return $(selector).parents(".items-row");
	}
</script>
