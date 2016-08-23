<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Submission Saved</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<style type="text/css">
		.pageNameReference {
			font-weight: bold;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Submission Saved" />
		<div class="infoBox msgBoxSpacing centeredContent">
			<p>Your submission has been saved</p>
			<p>Your submission ID is: ${submissionId}</p>
			<p>
				You don't need to record this ID; you can re-launch your partially saved submission from the <span class="pageNameReference">Manage Current
					Submissions</span> page, to complete and submit.
			</p>
			<div>
				<a href="${pageContext.request.contextPath}/"><button>Home</button></a>
			</div>
		</div>
	</div>
</body>
</html>