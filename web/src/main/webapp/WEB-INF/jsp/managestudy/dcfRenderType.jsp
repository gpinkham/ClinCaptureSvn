<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div id="dcfRenderType" title="<fmt:message key="dcf_generate" bundle="${resword}"/>" >
	<div style="clear: both; margin-top: 2%;">
		<p><fmt:message key="dcf_specify_render_type" bundle="${resword}"/>:</p>
		<input type="checkbox" name="dcfRenderType" value="save" /> <fmt:message key="dcf_render_save" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" value="print" /> <fmt:message key="dcf_render_print" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" value="email" /> <fmt:message key="dcf_render_email" bundle="${resword}"/>: <input id="email" type="text" cols="30" /> <br/>
		<input type="button" id="btn_cancel_dcf" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" style="margin-top:20px" />
		<input type="hidden" id="generateDcf" name="generateDcf" value="" />
		&nbsp;&nbsp;
		<input type="submit" value="<fmt:message key="dcf_generate" bundle="${resword}"/>" class="button_medium" style="margin-top:20px" />
	</div>
</div>

<script>
        $(document).ready(function() {
        	$("#dcfRenderType").hide();
        	$("#btn_generate_dcf").click(function() {
        		$("#generateDcf").val("yes");
        		$("#dnform").submit();
        		$("#generateDcf").val("");
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
    		$("#btn_cancel_dcf").click(function() {
        		$("#dcfRenderType").hide();
        	});
        }
</script>