<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
<q:sharedHeadTitle>Superuser Clone Submission</q:sharedHeadTitle>
<q:sharedCommonIncludes />
<q:jQueryUiIncludes />
<style type="text/css">
.pageFocus {
	padding: 0.5em;
	width: 77%;
	margin: 1em auto;
}

.twoColLeft {
	width: 5%;
}

label.twoColRight {
	display: block;
}

.textAlignLeft {
	text-align: left;
}
</style>
<script type="text/javascript">
$(function(){
	$("#licenseCheckbox").change(function(){
		var cloneButton = $('#cloneSubmissionButton');
		if ( $(this).attr("checked") == "checked" ){
			shared.enableButton(cloneButton);
			return
		}
		shared.disableButton(cloneButton);
	});
	
	$("#cloneSubmissionButton").click(function(){
		//Get the cloned submission title - 
		var cloneTitle = $("#clonedSubmissionTitle").val();
		if(cloneTitle == null || cloneTitle ==''){
			alert("You must enter a title for the cloned submission");
			$("#clonedSubmissionTitle").focus();
			return;
		}
		var url = '${pageContext.request.contextPath}/groupAdmin/executeClone?submissionId=${submission.submissionId}&cloneTitle=' + cloneTitle;
		//Check For Migrate Checkbox - may or may not be present
		if($("#migrateCheckbox").length > 0 && $("#migrateCheckbox").is(':checked') ){
			url = url + '&migrate=true';
		}
		urlEncoded = encodeURI(url);
		window.location.href= urlEncoded;
	});
});

</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Clone Group Submission" backUrl="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions" />
		<div class="centeredContent">
			<h4 class="centeredContent">Clone Group Submission</h4>
			You are about to clone the following submission:<br/>
			<div class="centeredContent">
				<table style="margin-left:auto;margin-right:auto;" class="textAlignLeft">
					<tr><th>Submission Title</th><td>${submission.submissionTitleForClone}</td></tr>
					<tr><th>Owned by User</th><td>${submission.submittedByUsername }</td></tr>
				</table>
			</div>
			<br/>
			The cloned submission will be owned by: ${clonedToUser}<br/>
			<br/>
			What would you like to call the cloned submission? <input type="text" id="clonedSubmissionTitle" ><br/>
			
		    <c:if test="${not empty configLatestVersion and not configLatestVersion}">
		    <br/>
		    The submission questionnaire is not the latest version.<br/>
		    Would you like to migrate the submission to the latest questionnaire? <input type="checkbox" id="migrateCheckbox" ><br/>
		    Warning: You might lose some answers if you migrate, and the submission may not validate.<br>
		    </c:if>
		    <br/>Warning: Data, spatial and species files are not cloned. <br/>
			<br/>
			<p class="lessImportantText">
				You must accept the following statement before you can clone a submission.
			</p>
			<div>
				<div class="twoColLeft">
					<input id="licenseCheckbox" type="checkbox" name="licenseCheckbox" />
				</div>
				<label class="twoColRight lessImportantText" for="licenseCheckbox">
					I confirm that I am the rights holder or have the permission of the rights holder to submit this data under an open access licence.
				</label>
			</div>
			<br/>
			<div class="centeredContent">
	          <button id="cloneSubmissionButton" disabled="disabled">Clone Submission</button>
            </div>
		</div>
		
	</div>
</body>
</html>