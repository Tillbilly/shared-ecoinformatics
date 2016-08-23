<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="questionId" required="true" %>
<%@ attribute name="questionType" type="java.lang.String" required="true" %>
<%@ attribute name="uniqueFormId" required="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript"> 
     $(function() { 
     	$( "#submittedImageTemplate" ).template( "imageTemplate" );
         // bind 'myForm' and provide a simple callback function 
         var percent = $('.percent');
         var status = $('#status');
            
         $('#${uniqueFormId}').ajaxForm({
         	dataType : 'json',
         	resetForm : true,
             beforeSubmit: function() {
             	//Need to validate whether file has already been uploaded and has`nt been deleted.
                 status.empty();
                 var percentVal = '0%';
                 percent.html(percentVal);
                 $("div.loading-gif").show();
             },
             uploadProgress: function(event, position, total, percentComplete) {
                 var percentVal = percentComplete + '%';
                 percent.html(percentVal);
             },
             success:function(data){
             	$("div.loading-gif").hide();
             	//status.html(data.origFilename + "  " + data.size + " bytes");
             	$.tmpl("imageTemplate", {fileName:data.imageName,
             		imageThumbId:data.thumbnailId,
             		imageId:data.imageId,
                  fileSize:data.size } ).appendTo("#uploadedImages");
              }
              //,complete: function(xhr) {status.html(xhr.responseText);} 
         });
        
     }); 
</script> 
<div class="imageUploadDialog">

    <span class="uploadDialogQuestionIdSpan" style="display:none;">${question.id}</span>
    <span class="uploadDialogQuestionTypeSpan" style="display:none;">IMAGE</span>
    <form id="${uniqueFormId}" action="imageUpload" method="post" enctype="multipart/form-data">
	    <table>
	      <tr><td class="inputLabelTd">Image File:</td><td><input id="fileNameInput" type="file" name="file"></td></tr>
	      <tr><td class="inputLabelTd">File Description:</td><td><textarea id="fileDescription" name="description"></textarea></td></tr>
		</table>
		<div id="uploadFileSubmit"><input type="submit" value="Upload Image"></div>
	</form>
    <div class="progress">
        <div class="loading-gif" style="display:none;"><img src="../images/loading.gif"></img><span class="percent">0%</span></div >
    </div>
    
    <div id="status"></div>
	<br/>
	<div id="uploadedImages">

	</div>        
</div>