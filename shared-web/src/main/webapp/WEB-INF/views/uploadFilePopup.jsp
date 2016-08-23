<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>File Upload for Q${questionId}</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="css/fileuploader.css"></link>
	<script type="text/javascript" src="script/jquery.form.js"></script>
	<script type="text/javascript" src="script/jquery.tmpl.js"></script>
	<script id="submittedFileTemplate" type="text/x-jquery-tmpl">
        <div class="uploadedFile">
            Successfully uploaded the file <span class="filename">\${fileName}</span>
			(<span class="filesize">\${fileSize}</span>)
        </div>
    </script>
	<script> 
        $(function() { 
        	$( "#submittedFileTemplate" ).template( "fileTemplate" );
            // bind 'myForm' and provide a simple callback function 
            var percent = $('.percent');
            var status = $('#status');
               
            function handleFailure(message) {
            	$("div.loading-gif").hide();
            	if(message == null || message == ""){
           	 	    status.html('<div class="msgBoxSpacing errorBox">Upload failed. Close and re-open the upload window.</div>');
            	}else{
            		status.html('<div class="msgBoxSpacing errorBox">File upload failed with message:' + message +'</div>');
            	}
           	 	
            }
            
            $('#uploadform').ajaxForm({
            	dataType : 'json',
            	resetForm : true,
                beforeSubmit: function() {
                    status.empty();
                    var percentVal = '0%';
                    percent.html(percentVal);
                    $("div.loading-gif").show();
                },
                uploadProgress: function(event, position, total, percentComplete) {
                    var percentVal = percentComplete + '%';
                    percent.html(percentVal);
                },
                success:function(data, statusText){
                	if (!data.success) {
                		handleFailure(data.failureMessage);
                		return;
                	}
                	$("div.loading-gif").hide();
                	$("#uploadedFiles").empty();
                	$.tmpl("fileTemplate", {fileName:data.origFilename,
	                    fileSize:data.humanReadableSize } ).appendTo("#uploadedFiles");
                	$("#finishedButton").show();
                 },
                 error: function(data) {
                	 handleFailure(null);
                 } 
            });
            
			$("#finishedButton").click(function(){
				var filename = $("div.uploadedFile span.filename").first().html();
				opener.updateUploadedFileName("${questionId}",filename);
				self.close ();
            });
			
            $("#cancelButton").click(function(){
            	self.close ();
            });
            
            $("#uploadFileSubmitInput").click(function(){
            	var fileName = $("#fileNameInput").val();
            	if(fileName == null || fileName == ""){
            		alert("No file has been selected. Select a file by clicking 'Browse'");
            		return false;
            	}
            });
        }); 
    </script> 
	<style type="text/css">
		.main-content {
			width: 600px;
		}
		
		.msgBoxSpacing {
			margin: 0;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<c:set var="fileTypeDesc" value="File" />
		<c:choose>
		    <c:when test="${responseType eq 'LICENSE_CONDITIONS' }">
		        <c:set var="fileTypeDesc" value="License Conditions" />
		    </c:when>
		    <c:when test="${responseType eq 'DOCUMENT'}">
		        <c:set var="fileTypeDesc" value="Document" />
		    </c:when>
		    <c:when test="${responseType eq 'SPECIES_LIST'}">
		        <c:set var="fileTypeDesc" value="Species List" />
		    </c:when>
		</c:choose>
		
		<div class="centeredContent">
			<q:sharedSmallLogo />
			<h2>Upload ${fileTypeDesc} For Question ${questionId}</h2>
		</div>
		<c:if test="${responseType eq 'SPECIES_LIST'}">
			<div>
			    <p>The species file uploaded through this form can contain either scientific or common names (but not 
			    both at the same time), either for all flora or all fauna taxa covered by the dataset you are submitting.</p>
	            <p>The file must be a <span class="filename">.txt</span> file, where each row contains an individual taxonomic name. Scientific 
	            names can be specified at species, genus or family level. Common names can only be supplied at species 
	            level. No header is required for the file.</p>
	            <p class="reducedBottomMargin">Here are some examples for what the file can contain:</p>
	            <div class="speciesListExampleContainer centeredBlock">
		            <p class="reducedBottomMargin">Example 1: Scientific names for flora taxa</p>
		            <div class="speciesListExample">Atriplex vesicaria
						Chenopodium sp.
						Cenchrus ciliaris
						Poaceae sp.</div>
					<p class="reducedBottomMargin">Example 2: Common names for flora taxa</p>
		            <div class="speciesListExample">Buffel Grass
						Old Man Saltbush
						Golden Wattle</div>
					<p class="reducedBottomMargin">Example 3: Scientific names for fauna taxa</p>
		            <div class="speciesListExample">Tachyglossus aculeatus
						Macropodidae sp.
						Dromaius sp.
						Ctenophorus pictus</div>
					<p class="reducedBottomMargin">Example 4: Common names for fauna taxa</p>
		            <div class="speciesListExample">Short-Beaked Echidna
						Rosenberg Goanna
						Sleepy Lizard</div>
	            </div>
			</div>
		</c:if>
		
		<form id="uploadform" action="questionFileUpload" method="post" enctype="multipart/form-data">
		    <table class="centeredBlock">
		      <tr><td class="inputLabelTd">${fileTypeDesc} File:</td><td><input id="fileNameInput" type="file" name="file"></td></tr>
		      <tr><td class="inputLabelTd">Description:</td><td><textarea id="fileDescription" name="description"></textarea></td></tr>
			</table>
			<div id="uploadFileSubmit" class="rightAligned">
				<input id="uploadFileSubmitInput" type="submit" value="Upload File">
			</div>
			<input type="hidden" name="questionId" value="${questionId}" />
			<input type="hidden" name="responseType" value="${responseType}" />
		</form>
		<div class="progress">
	        <div class="loading-gif" style="display:none;">
	        	<img src="images/loading.gif"></img>
	        	<span class="percent">0%</span>
        	</div >
	    </div>
		    
	    <div id="status"></div>
		<br/>
		<div id="uploadedFiles" class="centeredContent"></div>
		<div class="rightAligned">
			<button id="cancelButton" onclick="close();">Cancel</button>
			<button id="finishedButton" style="display:none;">Finished</button>
		</div>
	</div>
</body>
</html>	