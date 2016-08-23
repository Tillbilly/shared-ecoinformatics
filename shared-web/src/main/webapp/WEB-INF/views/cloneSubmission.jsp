<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Clone Submission</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<style type="text/css">
		.pageFocus {
			padding: 0.5em;
			width: 77%;
			margin: 1em auto;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Clone Submission" backUrl="${pageContext.request.contextPath}/manageSubmissions" />
		<div class="pageFocus centeredContent">
			<h4 class="centeredContent">Clone Existing Submission</h4>
			<p class="lessImportantText">
				You must accept the following statement before you can clone a submission.
			</p>
			<q:createSubmissionAgreementFragment buttonText="Clone Submission" />
		</div>
		<q:brandingBlurb />
	</div>
</body>
</html>