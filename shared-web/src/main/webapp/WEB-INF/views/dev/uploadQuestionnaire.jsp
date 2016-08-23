<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>SHaRED Questionnaire Development</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Questionnaire Dev" />
		<div class="centeredContent">
			<h2>Select a questionnaire file then click upload.</h2>
		</div>
		<form id="uploadform" action="uploadQuestionnaire" method="post" enctype="multipart/form-data" class="centeredContent">
		    <input type="file" name="file" id="theFile">
		    <input type="submit" value="Submit">
		</form>
	</div>
</body>
</html>