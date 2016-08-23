<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<html>
<head>
	<q:sharedHeadTitle>Study Location File Upload for Q${questionId}</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fileuploader.css"></link>
	<q:mapScripts />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-compressed.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-projections.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplayers-google.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/renderSiteFileMap.js"></script>
	<script> 
		var shared = shared || {};
		shared.siteFileUploadedFileName = '';
        $(function() { 
        	$("#confirmFile").hide();
        	$(".jsonResponseErrorMessage").hide();
        	var uploadSiteDetailsFromJson = function(data){
            	$(".uploadedSiteFileDetails").empty();
            	if(data.error != null && data.error != ''){
            		$("#finishedButton").hide();
            		$(".jsonResponseMessage").empty();
            		$(".jsonResponseErrorMessage").html(data.error).show();
            		$("div.siteMap").hide();
            		return;
            	}
            	$(".jsonResponseErrorMessage").empty().hide();
            	$(".jsonResponseMessage").html(data.message).show();
            	$("#finishedButton").show();
            	$("#uploadFile").hide();
            	$("#confirmFile").show();
				$('<table class="centeredBlock leftAligned">')
            	.append("<tr><th>Filename:</th><td>" + data.fileName+"</td></tr>")
            	.append("<tr><th>Description:</th><td>"+ data.description + "</td></tr>")
            	.append("<tr><th>Coord System:</th><td>"+ data.coordSys +"</td></tr>")
            	.append("<tr><th>Other CS:</th><td>"+ data.coordSysOther + "</td></tr>")
            	.appendTo(".uploadedSiteFileDetails");
				shared.siteFileUploadedFileName = data.fileName;
            	if(data.coordSys != 'Other'){
	            	$("div.siteMap").show();
	            	drawSitePointsOnMap(data.sites, data.coordSys);
            	}else{
            		$("div.siteMap").hide();
            	}
            };
            
            var drawPreviouslyUploadedSiteFileJson = function(data){
            	uploadSiteDetailsFromJson(data);
            	$("#finishedButton").hide();
            }
        	
        	var launchFileName = "${siteFileName}";
        	if(launchFileName != null && launchFileName != ''){
        		$.getJSON("retrieveSiteFileAjax?filename=" + launchFileName , drawPreviouslyUploadedSiteFileJson);
        	}
        	
            $('#uploadform').ajaxForm({
            	dataType : 'json',
            	resetForm : false,
                beforeSubmit: function() {
                	//Ensure there is a value in the description field - might be unnecessary SHD-123
                	/* if($("#fileDescription").val() == null || $("#fileDescription").val() == '' ){
                		alert("Please enter a description for the file!");
                		$("#fileDescription").focus();
                		return false;
                	} */
                },
                success: uploadSiteDetailsFromJson
            });
            
            $("#finishedButton").click(function(){
            	//On chrome, IE8 etc, need to remove the path information 'C:\fakepath\' from the value.
            	var siteFileName = shared.siteFileUploadedFileName;
            	if(siteFileName != null && siteFileName != "" && siteFileName.indexOf("\\") > -1 ){
            		siteFileName = siteFileName.substring( siteFileName.lastIndexOf("\\") + 1 );
            	}
            	opener.updateSiteFileName(siteFileName);
            	window.close();
            });
            
            $(".cancelButton").click(function(){
            	window.close();
            });
            
            $("#backButton").click(function(){
            	$("#uploadFile").show();
            	$("#confirmFile").hide();
            });
            
            $("#fileNameInput").change(function(){
            	//Need to check the suffix of the file name - 
            	//HAS to be ".csv"
            	var filename = $(this).val();
            	if(filename != null && filename != ''){
            		if( filename.indexOf('.csv') !== -1 && ( filename.length - filename.indexOf('.csv') == 4)){
            		    shared.enableButton("#uploadFileSubmit");
            		}else{
            			alert("Invalid file type. Must end with suffix '.csv'");
            			shared.disableButton("#uploadFileSubmit");
            		}
            	}else{
            		shared.disableButton("#uploadFileSubmit");
            	}
            });
            //Show and hide other coord sys String input
            $("#coordSysList").change(function(){
            	if($(this).find(":selected").text() == "Other" ){
            		$("#otherCoordSys").show();
            	}else{
            		$("#otherCoordSys").hide();
            	}
            });
            if($("#coordSysList").find(":selected").text() == "Other" ){
            	$("#otherCoordSys").show();
            }
        }); 
    </script> 
	<style type="text/css">
		.jsonResponseMessage {
			display: none;
		}
		
		.uploadedSiteFileDetails {
			text-align: left;
		}
		
		.infoBox {
			margin: 0 2em;
		}
		
		#infoPanel {
		}
		
		.siteMap {
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			top: 272px;
		}
		
		#confirmFileTop {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			height: 272px;
			overflow: auto;
		}
		
		.main-content {
			width: auto;
		}
		
		#confirmFile {
			position: absolute;
			left: 0;
			right: 0;
			top: 0;
			bottom: 0;
			background-color: #FFF;
		}
		
		#confirmFile h3 {
			margin: 0;
		}
		
		#confirmFile p {
			padding: 0 1em;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<div id="uploadFile">
			<div class="centeredContent">
				<q:sharedSmallLogo />
				<h3>Study Location File Upload</h3>
			</div>
			<p>SHaRED can process study location coordinates provided in a csv file (comma-separated values file). For more information on how to create a
				csv file with Microsoft Excel, please use this 
				<a href="http://office.microsoft.com/en-au/excel-help/save-a-workbook-in-another-file-format-HA102840050.aspx?CTT=1" target="_blank">link</a>.</p>
			<p>The csv file must comply with the following requirements:</p>
			<ul>
				<li>The file needs to be composed of the following columns (in this order):<br />
				<span class="csvFormat">study location identifier, x coordinate, y coordinate, zone, comments</span>
				</li>
				<li>Column headers are not required.</li>
				<li>The 'zone' and 'comments' values are optional, but the file should contain empty strings ("") in their place.</li>
			</ul>
			<p>
				The following text is a row from a study location file in csv format without 'zone' and 'comments' values:<br />
				<span class="csvFormat">NTBHR001,137.34,-38.33,"",""</span>
			</p>
			<div class="jsonResponseErrorMessage errorBox centeredContent"></div>
			<form id="uploadform" action="uploadSiteFileAjax" method="post" enctype="multipart/form-data">
				<input type="hidden" name="questionId" value="${questionId}">
				<table class="centeredBlock">
					<tr>
						<td class="inputLabelTd">Study Location File:</td>
						<td><input id="fileNameInput" type="file" name="siteFileName"></td>
					</tr>
					<tr>
						<td class="inputLabelTd">Description:</td>
						<td><textarea id="fileDescription" name="description"></textarea></td>
					</tr>
					<tr>
						<td class="inputLabelTd">Coordinate System:</td>
						<td><select id="coordSysList" name="coordSys">
								<c:forEach var="coordPair" items="${coordList}">
									<option value="${coordPair.epsgCode}">${coordPair.displayStr}</option>
								</c:forEach>
						</select><input id="otherCoordSys" name="otherCoordSys" style="display: none;"></input></td>
					</tr>
					<tr>
						<td colspan="2" class="centeredContent">
							<button class="cancelButton">Cancel</button>
							<input id="uploadFileSubmit" type="submit" value="Upload File" disabled="disabled">
						</td>
					</tr>
				</table>
			</form>
		</div>
		<div id="confirmFile">
			<div id="confirmFileTop">
				<h3 class="centeredContent">File Confirmation</h3>
				<p>Here is a Map of the Points. If you are happy with your uploaded study location file, please press the 'Finished' button.</p>
				<div id="infoPanel">
					<div class="jsonResponseMessage infoBox msgBoxSpacing centeredContent"></div>
					<div class="centeredContent">
						<button class="cancelButton">Cancel</button>
						<button id="backButton">Back</button>
						<button id="finishedButton" style="display:none;">Finished</button>
					</div>
					<div class="uploadedSiteFileDetails"></div>
				</div>
			</div>
			<div class="siteMap" style="display:none;">
				<div id="map" style="height: 100%;"></div>
			</div>
		</div>
	</div>
</body>
</html>	