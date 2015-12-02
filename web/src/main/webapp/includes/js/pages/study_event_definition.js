var typeChangeFunction = function() {
    if($('select[name="type"]').find(":selected").val() == '') {
        $('tr[name="repeating"]').hide();
        $(".showHide").css('display', 'none');
        $('input[name*="Day"]').attr('value','').attr('readonly','');
        $('.showHide input[type="checkbox"]').attr('checked', false);
        $('input[name="emailUser"]').attr('value','').attr('readonly','');
    } else if($('select[name="type"]').find(":selected").val() == 'calendared_visit') {
        $(".showHide").css('display', '');
        $('tr[name="repeating"]').css('display', 'none');
        $('input[name=repeating][value=false]').attr('checked', true);
    } else {
        $('tr[name="repeating"]').css('display', '');
        $(".showHide").css('display', 'none');
        $('input[name*="Day"]').attr('value','').attr('readonly','');
        $('.showHide input[type="checkbox"]').attr('checked', false);
        $('input[name="emailUser"]').attr('value','').attr('readonly','');
    }
};

$(document).ready(function() {
    $(".showHide").css('display', 'none');
    $('select[name="type"]').change(typeChangeFunction);
    $('input[name="isReference"]').click(function() {
        if ($(this).is(':checked')) {
            $('input[name*="Day"]').attr('value','0').attr('readonly','true');
            $('input[name="emailUser"]').attr('value','').attr('readonly','true');
            $("tr[name^='email']").css('display', 'none');

        } else {
            $('input[name*="Day"]').attr('value','').attr('readonly','');
            $('input[name="emailUser"]').attr('readonly','');
            $("tr[name^='email']").css('display', '');

        }
    });
    $('input[name="isReference"]').each(function() {
        if ($(this).is(':checked')) {
            $('tr[name="repeating"]').hide();
            $('input[name*="Day"]').attr('value','0').attr('readonly','true');
            $('input[name="emailUser"]').attr('value','').attr('readonly','true');
            $("tr[name^='email']").hide();
        }
    });
    typeChangeFunction();
    captureFirstFormState();
});


function updateEventDefinitionAddCRF(){
	$("input[name=action]").attr("value","addCrfs");
	$("#updateEventDefinition").submit();
}

function checkItemLevelSDV(element) {
	var $configureButton = $(element).parent().find("[class^=bt_edit_edc_sdv_]");
	if ($configureButton.attr("class")) {
		if ($(element).val() == "2") {
			$configureButton.css("display", "inline-block");
		} else {
			$configureButton.css("display", "none");
		}
	}
}

function configureItemLevelSDV(edcId) {
	$("input[name=action]").val("configureItemLevelSDV");
	$("input[name=edcToConfigure]").val(edcId);
	$("#updateEventDefinition").submit();
}

function checkItemLevelSDVChanges(message, button) {
	var errorsNotExists = validateCustomFields({expectedValues: ['email'], selectors: ['.email_to_check_field'], returnTrue: 'true'});

	if (errorsNotExists) {
		var itemLevelSDVChanged = false;
		var $sdvSelects = $("select[name^=sdvOption]");

		$sdvSelects.each(function() {
			var $select = $(this);
			var initialValue = $select.attr("initial-value");
			var value = $select.val();
			if (initialValue == "2" && value != initialValue) {
				itemLevelSDVChanged = true;
			}
		});

		if (itemLevelSDVChanged) {
			return confirmSubmit({ message: message, height: 180, width: 500, submit: button });
		} else {
			return true;
		}
	} else {
		return false;
	}
}