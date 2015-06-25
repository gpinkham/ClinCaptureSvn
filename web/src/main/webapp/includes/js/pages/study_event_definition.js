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