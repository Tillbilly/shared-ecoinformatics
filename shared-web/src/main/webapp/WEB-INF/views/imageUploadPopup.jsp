<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Image upload for Q${questionId}</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fileuploader.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script id="submittedImageTemplate" type="text/x-jquery-tmpl">
        <div class="uploadedImage">
            <div class="uploadedImageDetailsTitle centeredContent">Uploaded image</div>
            <div class="centeredContent">
            	<a id="imageLink" target="_blank" href="getImage?imageId=\${imageId}">
            		<img id="imageThumb" class="thumbnail" src="getImage?imageId=\${imageThumbId}"></img>
        		</a>
            	<p><span class="filename">\${fileName}</span>(<span class="filesize">\${fileSize} bytes</span>)</p>
    		</div>
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
                	// TODO Need to validate whether file has already been uploaded and hasn't been deleted.
                	
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
                	$("#uploadedImages").empty();
                	$.tmpl("imageTemplate", {fileName:data.imageName,
                		imageThumbId:data.thumbnailId,
                		imageId:data.imageId,
	                    fileSize:data.size } ).appendTo("#uploadedImages");
                	shared.enableButton("#finishedButton");
                 }
            });
            
			$("#finishedButton").click(function(){
				var filename = $(".filename").first().html();
				var uploadedImageHref = $("#imageLink").first().attr("href");
				var uploadedThumbImgSrc = $("#imageThumb").first().attr("src");
				
				var multiIndex = "${multiIndex}";
				if(multiIndex != null && multiIndex != ""){
					opener.updateUploadedImageMulti("${questionId}",multiIndex, filename, uploadedThumbImgSrc, uploadedImageHref);
				}else{
				    opener.updateUploadedImage("${questionId}",filename, uploadedThumbImgSrc, uploadedImageHref);
				}
				self.close();
            });
			
            $("#cancelButton").click(function(){
            	self.close();
            });
            
            $("#uploadFileSubmitInput").click(function(){
            	var fileName = $("#fileNameInput").val();
            	if(fileName == null || fileName == ""){
            		alert("No image file has been selected. Select an image by clicking 'Browse'");
            		return false;
            	}
            	//Check that we support the image suffix
            	var failed = true;
            	var periodIndex = fileName.lastIndexOf(".");
            	if(periodIndex != -1){
            		var suffix = fileName.substr(periodIndex + 1).toLowerCase();
                    if( suffix == 'jpeg' || suffix == 'jpg' || suffix == 'png' || suffix == 'gif' || suffix == 'bmp'){
                    	failed = false;
                    }            		
            	}
            	if(failed){
            		alert("Image file suffix not recognised. Must be 'jpg','jpeg','png','gif' or 'bmp.");
            		return false;
            	}
            });
           
        }); 
    </script> 
</head>
<body class="imagePopup">
	<div id="header" class="centeredContent">
		<h2 class="sharedHeaderBackground">Upload Image For Q${questionId}</h2>
	</div>
	<form id="uploadform" action="questionImageUpload" method="post" enctype="multipart/form-data">
	    <table>
			<tr>
				<td class="inputLabelTd">Image File:</td>
				<td><input id="fileNameInput" type="file" name="file"></td>
			</tr>
			<tr>
				<td class="inputLabelTd">File Description:</td>
				<td><textarea id="fileDescription" name="description"></textarea></td>
			</tr>
		</table>
		<div id="uploadFileSubmit" class="centeredContent">
			<input id="uploadFileSubmitInput" type="submit" value="Upload Image">
		</div>
		<div class="progress centeredContent">
			<div class="loading-gif" style="display: none;">
				<img src="images/loading.gif"></img>
				<span class="percent">0%</span>
			</div>
		</div>
		<input type="hidden" name="questionId" value="${questionId}" />
		<c:if test="${not empty multiIndex }">
			<input type="hidden" name="multiIndex" value="${multiIndex}" />
		</c:if>
	</form>
    
    <div id="status"></div>
	<div id="uploadedImages"></div>
	<div id="footer" class="centeredContent">
		<button id="cancelButton" onclick="close();">Cancel</button>
		<button id="finishedButton" disabled="disabled">Finished</button>
	</div>
</body>
</html>	