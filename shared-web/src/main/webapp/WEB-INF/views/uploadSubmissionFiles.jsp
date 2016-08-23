<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>${quest.title}</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fileuploader.css"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/helpIcon.js?version=${sharedVersion}"></script>
    <script id="submittedFileTemplate" type="text/x-jquery-tmpl">
		<tr class="submittedFile">
			<td class="filename">\${filename}</td>
			<td class="filesize centeredContent">\${fileSize}</td>
			<td class="description">
			   	<textarea name="submissionFileList[\${fileIndex}].description" class="fileDescriptionTextArea">\${description}</textarea>
			</td>
			<td class="filetype centeredContent">\${filetype}</td>
			<td class="centeredContent">\${fileFormatTitle}</td>
			<td class="centeredContent">\${fileFormatVersion}</td>
			<td class="controls">
				<div class="removeFile deleteIcon"></div>
				<input type="hidden" name="submissionFileList[\${fileIndex}].storedFilename" value="\${storedFilename}" />
        		<input type="hidden" class="filename" name="submissionFileList[\${fileIndex}].filename" value="\${filename}" />
        		<input type="hidden" name="submissionFileList[\${fileIndex}].deleted" value="false" class="deletedInput"/>
			</td>
			<c:if test="${not empty quest.lastReview and quest.lastReview.hasRejectedFiles}">
				<td></td>
			</c:if>
		</tr>
    </script>
	<script>
		// Taken from http://stackoverflow.com/a/2548133/1410035
		function endsWith(str, suffix) {
		    return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}    
	
		function isFileAlreadyUploaded(fileName){
	    	var submittedFileArray = $(".submittedFile");
	    	if(submittedFileArray.length == 0 ){
	    		return false;
	    	}
    		for (var i = 0; i < submittedFileArray.length; i++) {
				var submittedFileDiv = submittedFileArray[i];
				var uploadedFilename = $(submittedFileDiv).find("input.filename").first().val();
				var fileIsDeleted = $(submittedFileDiv).find("input.deletedInput").first().val();
				if( (fileName == uploadedFilename || endsWith(fileName, uploadedFilename)) && fileIsDeleted == "false"){
					return true;
				}
    		}	
	    	return false;
	    }
	
        $(function() { 
        	$( "#submittedFileTemplate" ).template( "fileTemplate" );
        	
        	function refreshRemoveIconClickBinding() {
	        	$(".removeFile").click(function(){
	        		var fileDiv = $(this).closest(".submittedFile");
	        		fileDiv.find(".deletedInput").attr("value","true");
	        		fileDiv.hide();
	        	});
        	}
        	
        	refreshRemoveIconClickBinding();
            
            var bar = $('.bar');
            var percent = $('.percent');
            var status = $('#status');
               
            $('#uploadform').ajaxForm({
            	dataType : 'json',
            	resetForm : true,
                beforeSubmit: function() {
                	//Need to validate whether file has already been uploaded and has`nt been deleted.
                	if(isFileAlreadyUploaded( $("#fileNameInput").val() )){
                		alert( $("#fileNameInput").val() + " already uploaded for this questionnaire." );
                		return false;
                	}
                	
                	//Validate that the user has entered a description for the file
                	if( $("#fileDescription").val() == null || $("#fileDescription").val() == ''){
                		alert( "Please enter a description for the file" );
                		$("#fileDescription").focus();
                		return false;
                	}
                	
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
                	var newfileIndex = $("#uploadedFiles tr.submittedFile").length;
                	if(! newfileIndex ){
                		newfileIndex = "0";
                	}
                	$.tmpl("fileTemplate", {filename:data.origFilename,
                		                    filetype:data.fileTypeTitle,
                		                    description:data.description,
                		                    storedFilename:data.storedFilename,
                		                    fileIndex:newfileIndex,
                		                    fileSize:data.humanReadableSize,
                		                    fileFormatVersion: data.fileFormatVersion,
                		                    fileFormatTitle: data.fileFormatTitle}).appendTo("#uploadedFiles tbody");
                	
                	refreshRemoveIconClickBinding();
                	$("#fileNameInput").change();
                 }
                 //,complete: function(xhr) {status.html(xhr.responseText);} 
            });
            $("#finishedButton").click(function(){
            	//Check if there is a file name in the submission data file field
            	//If there is, fire up a warning - tell the user they need to click the 'Upload File' button
            	if( $("#fileNameInput").val() && ! confirm("File selected but not uploaded. To upload, press cancel then press the 'Upload File' button.")){
            		return;
            	}
            	
            	$("#uploadedFiles").submit();
            });
            $("#backButton").click(function(){
            	window.location='${lastPageNumber}';
            })
            $("#fileNameInput").change(function(){
            	var path = $(this).val();
            	if(path == null || path.trim().length == 0){
            		shared.disableButton("#uploadFileSubmit input");
            		return;
            	}
           		shared.enableButton("#uploadFileSubmit input");
            });
            $("#fileFormat").change(function (d) {
            	$("#selectedFileFormatTitle").val($(this).find(":selected").text());
           	})
        }); 
    </script> 
	<style type="text/css">
		#uploadForm table {
			margin: 0 auto;
		}
		
		#mailingAddress {
			margin-left: 2em;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout displayConfirmation="true" />
	<div class="main-content" >
		<q:pageHeader titleText="Data File Submission" includeHomeButton="false" />
		<q:reviewRejectionInfo lastReview="${quest.lastReview}" />
		<div class="lessImportantText">
			<p>This is the final form of the questionnaire where data submitters are requested to upload their data files and any associated data that has been previously described in the questionnaire.</p>
			<p>To complete this form;</p>
			<ul>
				<li>Please choose one or more files to upload (Please note individual uploaded files may not exceed 1 GB in size. Larger files can be split into several files of less than 1 GB)</li>
				<li>Please identify if the file is a data file or is associated/supplementary information</li>
				<li>Please provide a short description of the file(s) you are submitting. This description should allow re-users of the data to understand what each file contains.</li> 
				<li>Please choose the file format (e.g. MS Word, Excel, csv etc)</li>
				<li>Please enter the format version (e.g. MS Word 2007, Excel 2010)</li>
			</ul>
			<p>When all the required files have been uploaded please finalise the submission.</p>
			<p>Thank you for your submission.</p>
		</div>
		<!-- TODO fix this ajax-upload path -->
		<form id="uploadform" action="../ajax-upload" method="post" enctype="multipart/form-data">
			<table class="centeredBlock">
				<tr>
					<td class="inputLabelTd">Submission Data File:</td>
					<td>
						<input id="fileNameInput" type="file" name="myfile">
					</td>
				</tr>
				<tr>
					<td class="inputLabelTd">File Type:</td>
					<td>
						<select id="fileType" name="fileType">
							<c:forEach var="currType" items="${fileTypes}">
								<option value="${currType}">${currType.title}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="inputLabelTd">
						File Description:
						<div class="helpIcon" alt="More information"></div>
						<div class="helpText">
							<p>Please describe the types of data associated with this data file(s) (including tables, tabs, columns, measurements), an overview of the
								data file types and their format. If you have submitted an MS Excel file and have inserted a "Readme" tab as part of the file, make a note of
								this in the File Description.</p>
						</div>
					</td>
					<td>
						<textarea id="fileDescription" name="description" class="fileDescriptionTextArea"></textarea>
					</td>
				</tr>
				<tr>
					<td class="inputLabelTd">File Format:</td>
					<td>
						<select id="fileFormat" name="fileFormat" class="fileFormat">
							<q:sharedDefaultOption isMandatory="false" />
			                <q:sharedOptions items="${fileFormatValues}" itemLabel="displayString" itemTitle="description" 
									itemValue="traitValue" selectedValue="" />
						</select>
					<input type="hidden" id="selectedFileFormatTitle" name="fileFormatTitle" />
					</td>
				</tr>
				<tr>
					<td class="inputLabelTd">Format Version:</td>
					<td><input id="fileFormatVersion" name="fileFormatVersion" class="fileFormatVersion" /></td>
				</tr>
				<tr>
					<td colspan="2" align="right">
						<div id="uploadFileSubmit">
							<input type="submit" value="Upload File" disabled="disabled">
						</div>
					</td>
				</tr>
			</table>
		</form>
		<div class="progress">
	        <div class="loading-gif" style="display:none;">
	        	<img src="${pageContext.request.contextPath}/images/loading.gif"></img>
	        	<span class="percent">0%</span>
        	</div >
	    </div>
	    <div id="status"></div>
		<div>
			<form:form id="uploadedFiles" commandName="submissionFiles" action="${pageContext.request.contextPath}/questionnaire/processSubmissionFiles" cssClass="uploadedFilesContainer" method="POST">
			    <table>
			    	<thead>
			    		<tr>
			    			<th>Data File Name</th>
			    			<th>Size</th>
			    			<th>Description</th>
			    			<th>Type</th>
			    			<th>Format</th>
			    			<th>Version</th>
			    			<th></th>
			    			<c:if test="${not empty quest.lastReview and quest.lastReview.hasRejectedFiles}">
			    				<th>Rejection</th>
			    			</c:if>
		    			</tr>
			    	</thead>
			    	<tbody>
						<c:forEach items="${submissionFiles.submissionFileList}" var="file" varStatus="indx">  
							<tr class="submittedFile">
								<td class="filename">${file.filename}</td>
							    <td class="filesize centeredContent">${file.humanReadableFileSize}</td>
							    <td>
							    	<textarea name="submissionFileList[${indx.index}].description" class="fileDescriptionTextArea">${file.description}</textarea>
						    	</td>
							    <td class="centeredContent">${file.fileTypeTitle}</td>
							    <td class="centeredContent">${file.fileFormatTitle}</td>
							    <td class="centeredContent">${file.fileFormatVersion}</td>
							    <td class="controls">
							        <div class="removeFile deleteIcon"></div>
								    <input type="hidden" value="${file.id}" name="submissionFileList[${indx.index}].id" >
								    <input type="hidden" value="${file.storedFilename}" name="submissionFileList[${indx.index}].storedFilename">
								    <input type="hidden" value="${file.filename}" name="submissionFileList[${indx.index}].filename" class="filename">
								    <input type="hidden" value="false" name="submissionFileList[${indx.index}].deleted" class="deletedInput">
								    <form:errors path="submissionFileList[${indx.index}].description" cssClass="formErrorSpan"/>
							    </td>
							    <c:if test="${not empty quest.lastReview and quest.lastReview.hasRejectedFiles}">
				    				<td>
									    <c:if test="${not empty quest.lastReview.fileReviews[file.id] 
									    			and quest.lastReview.fileReviews[file.id].reviewOutcome eq 'REJECT'}">
									        <div class="errorBox">
									        	${quest.lastReview.fileReviews[file.id].comments}
									    	</div>
									    </c:if>
									</td>
				    			</c:if>
							</tr>
						</c:forEach>  
			    	</tbody>
			    </table>
			</form:form>
		</div>
		<div class="uploadFilesButtonDiv">
		    <button id="backButton">Back</button>
		    <button id="finishedButton">Next</button> 
		</div>
	</div>
</body>
</html>	