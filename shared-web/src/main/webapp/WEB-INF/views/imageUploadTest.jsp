<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<html>
<head>
	<title>${quest.title}</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="css/fileuploader.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script id="submittedImageTemplate" type="text/x-jquery-tmpl">
        <div class="uploadedImage">
            <span class="filename">\${fileName}</span><span class="filesize">\${fileSize} bytes</span><span class="imageThumbSpan"><a target="_blank" href="getImage?imageId=\${imageId}"><img class="thumbnail" src="getImage?imageId=\${imageThumbId}"></img></a></span>                   
        </div> 
    </script>
	<script> 
	    
        $(function() { 
        	$( "#submittedImageTemplate" ).template( "imageTemplate" );
            // bind 'myForm' and provide a simple callback function 
            var percent = $('.percent');
            var status = $('#status');
               
            $('#uploadform').ajaxForm({
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
           
            
            $(".dateResponseInput").datepicker(
   	    	     { dateFormat: "dd/mm/yy",
   	    	       changeMonth : true,
   	    	       changeYear  : true,
   	    	       yearRange: "1950:2020"
   	    	     }
   	    		
   	    	);
        }); 
    </script> 
	
</head>
<body>


<form id="uploadform" action="imageUpload" method="post" enctype="multipart/form-data">
    <table>
      <tr><td class="inputLabelTd">Image File:</td><td><input id="fileNameInput" type="file" name="file"></td></tr>
      <tr><td class="inputLabelTd">File Description:</td><td><textarea id="fileDescription" name="description"></textarea></td></tr>
      <!-- <tr><td class="inputLabelTd">Embargo Date:</td><td><input type="text" class="dateResponseInput" name="embargoDate" /></td></tr> -->
	</table>
	<div id="uploadFileSubmit"><input type="submit" value="Upload File"></div>
	
</form>
<div class="progress">
        <div class="loading-gif" style="display:none;"><img src="images/loading.gif"></img><span class="percent">0%</span></div >
    </div>
    
    <div id="status"></div>
<br/>
<div id="uploadedImages">

</div>

</body>
</html>	