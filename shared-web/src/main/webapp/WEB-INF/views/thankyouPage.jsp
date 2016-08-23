<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Submission Processing</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Submission Finalised" />
		<div class="infoBox msgBoxSpacing centeredContent">
			<p>Thank you for your submission.</p>
			<div>
				<a href="${pageContext.request.contextPath}/">
					<button>Home</button>
				</a>
			</div>
		</div>
		<p>Your data submission will now be reviewed by a TERN Eco-informatics Data Analyst to make sure that the submission is as complete and
			comprehensive as it can be thereby ensuring your data is as discoverable as possible in the AEKOS portal promoting it's re-use.</p>
		<p>If the submission is confirmed as accepted then the submission will be published to AEKOS - the data submitter will be notified of this
			activity via email.</p>
		<p>If the submission requires some modification and enhancement then a Data Analyst will identify the questions that need attention and an
			email will be sent to the data submitter requesting that the submission be edited and then re-submitted. If the Data Analyst discovers small
			issues such as typo's or misspellings they will rectify them themselves rather than contact the data submitter.</p>
		<p>Once the issues have been addressed the submission can be re-submitted, it will be reviewed again and if the recommended changes have been
			made the data will be published to AEKOS.</p>
		<q:brandingBlurb />
	</div>
</body>
</html>