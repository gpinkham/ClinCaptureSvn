<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div id="dcfRenderType" title="<fmt:message key="dcf_generate" bundle="${resword}"/>" >
	<div style="clear: both; margin-top: 2%;">
		<p><fmt:message key="dcf_specify_render_type" bundle="${resword}"/>:</p>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="save" /> <fmt:message key="dcf_render_save" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="print" /> <fmt:message key="dcf_render_print" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="email" /> <fmt:message key="dcf_render_email" bundle="${resword}"/>: 
		<input id="email" name="email" type="text" value="" style="width: 200px;" /> <br/>
		<div id="invalid_email" style="display: none; text-align: center;" class="alert">
			<fmt:message key="dcf_invalid_email_message" bundle="${resword}"/>
		</div>
		<input type="button" id="btn_cancel_dcf" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" style="margin-top:20px" />
		<input type="hidden" id="generateDcf" name="generateDcf" value="" />
		&nbsp;&nbsp;
		<input type="button" id="btn_submit_dcf" value="<fmt:message key="dcf_generate" bundle="${resword}"/>" class="button_medium" style="margin-top:20px" />
	</div>
</div>

<c:if test="${printDcf ne null}">
	<input type="hidden" id="printDcf" value="${printDcf}" onclick="javascript:openPopup();" />
</c:if>
<c:if test="${saveDcf ne null}">
	<input type="hidden" id="saveDcf" name="saveDcf" value="${saveDcf}" />
</c:if>

<script>
        $(document).ready(function() {
        	$("#dcfRenderType").hide();
        	toggleButtonEnable("dcf", "btn_generate_dcf");
        	toggleButtonEnable("dcfRenderType", "btn_submit_dcf");
        	$("#btn_generate_dcf").click(function() {
        		$("#generateDcf").val("yes");
        		showRenderDialog();
        		$(".dcfRenderType").click(function() {
            		toggleButtonEnable("dcfRenderType", "btn_submit_dcf");
             	});
        		setDefaultEmail();
        	});
        	$("#email").focus(function() {
        		checkEmailCheckbox();
        	});        	        		       	
            checkIfShouldPrintOrSave();
           	$("a.allcheckbox").each(function(){
                var a = $(this);
                var check = a.hasClass("check");
                a.click(function(){
                    checkOrUncheckAllByClass("dcf", check);
                    toggleButtonEnable("dcf", "btn_generate_dcf");
                    return false;
                });
            });
        });
        
        function showRenderDialog() {
        	$("#dcfRenderType").show();
    		$("#dcfRenderType").dialog({
    			autoOpen : true,
    	        closeOnEscape : false,
    	        modal : true,
    	        width : 400,
				open : function() {
					openDialog({ 
						dialogDiv : this,
						closable : true, 
		            	imagesFolderPath: determineImagesPath()
					});
				}
    		});
    		$("#btn_submit_dcf").click(function() {
    			if(validateDcfRequest()) {
    				appendPopupDivToDnForm();
    				$("#dnform").submit();
    				$("#generateDcf").val("");
    			} else {
    				$("#invalid_email").show();
    				setTimeout(function() { 
    					$("#invalid_email").fadeOut(2000);
    				}, 2000);
    			}
    		});
    		$("#btn_cancel_dcf").click(function() {    			
    			$("#generateDcf").val("");
    			appendPopupDivToDnForm();
        	});
        }
        
        function appendPopupDivToDnForm() {
        	var div = $("#dcfRenderType");
    		div.remove();
    		$("#dnform").append(div);
        }
        
        function validateDcfRequest() {
        	var valid = true;
        	$("input[type=checkbox][class=dcfRenderType]").each(function() {
        		if(this.checked && $(this).val() == "email") {
        			var emailReg = new RegExp(/^[\w\-\.\+]+\@[a-zA-Z0-9\.\-]+\.[a-zA-z0-9]{2,4}$/);
        			valid = emailReg.test($("#email").val());
        			if(!valid) {
        				return valid;
        			}
        		}
        	});        	
        	return valid;
        }
        
        function checkEmailCheckbox() {
        	$("input[type=checkbox][class=dcfRenderType]").each(function() {
        		if($(this).val() == "email" && !this.checked) {
        			this.checked = true;
        			$(this).trigger("click");
        			this.checked = true;
        		}
        	});
        }
        
        function toggleButtonEnable(checkBoxClass, buttonId) {
        	var checkboxes = "input[type=checkbox][class=" + checkBoxClass + "]";
        	var button = $("#" + buttonId);
        	var atleastOneChecked = false;
        	$(checkboxes).each(function() {
        		if(this.checked) {
        			atleastOneChecked = true;
        			return false;
        		}
        	});
        	if(atleastOneChecked) {
        		button.show();
        	} else {
        		button.hide();
        	}        	
        }
        
        function checkIfShouldPrintOrSave() {
        	var shouldPrint = $("#printDcf").val() == "yes";
        	var shouldSave = $("#saveDcf").val() == "yes";
        	if(shouldPrint && shouldSave) {
        		printDcf();
        		setTimeout(function() {
        			saveDcf()
        			}, 2000);
        	} else if (shouldPrint) {
        		printDcf();
        	} else if (shouldSave) {
        		saveDcf();
        	}        	
        }
        
        function printDcf() {
        	openDocWindow("<%=request.getContextPath()%>/ViewNotes?module=submit&printDcf=yes");
        }
        
        function saveDcf() {
        	var url = "<%=request.getContextPath()%>/ViewNotes?module=submit&saveDcf=yes";
        	window.location = url;
        }
        
        function setDefaultEmail() {
        	$("#email").val("");
        	var checkboxes = "input[type=checkbox][class=dcf]";
        	$(checkboxes).each(function() {
        		if(this.checked) {
   	        		$("#email").val($(this).attr("data-site-email"));
   	        		return false;
   	        	}         	
        	});
        }
</script>