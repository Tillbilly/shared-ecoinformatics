<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Questionnaire Summary</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fileuploader.css"></link>
	<script type="text/javascript">
		$(function(){
	        $('.followsSharedFixedHeader').css('margin-top', $('#sharedFixedHeader').height());
		});
	</script>
	<style type="text/css">
		.page {
			margin: 0.5em 0;
		}
		
		.page h2 {
			background-color: #A3D4FF;
			margin: 0;
		}
	</style>
</head>
<body class="sharedBodyBg questionnaireSummary">
	<q:questionnaireLogout displayConfirmation="true" />
	<div class="main-content" >
		<q:pageHeader includeHomeButton="false" titleText="Submission Summary" containerId="sharedFixedHeader">
			<div class="centeredContent">
				<a href="submitFiles" class="noUnderline">
					<button>Back</button>
				</a>
				<a href="saveSubmission" class="noUnderline">
					<button>Save</button>
				</a>
				<a href="finaliseSubmission" class="noUnderline">
					<button>Finalise Submission</button>
				</a>
			</div>
		</q:pageHeader>
		<div class="submissionSummaryContainer followsSharedFixedHeader">  
			<c:forEach var="page" items="${quest.pages}">
				<div class="page">
				    <h2 class="centeredContent">Page ${page.pageNumber}: ${page.pageTitle}</h2>
				    <c:forEach var="element" items="${page.elements}">
				        <c:choose>
				            <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
				                <q:renderQuestionGroupSummary group="${element }" pageAnswers="${page.pageAnswers}" 
				                								imageAnswerMap="${quest.imageAnswerMap}" geoFeatureSetMap="${geoFeatureSetMap }"/>
				            </c:when>
				            <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
				                <q:renderQuestionSummary question="${element }" pageAnswers="${page.pageAnswers}" 
				                							imageAnswerMap="${quest.imageAnswerMap}" geoFeatureSetMap="${geoFeatureSetMap }"/>
				            </c:when>
				            <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup'}">
				                <q:renderMultipleQuestionGroupSummary questionSet="${element }" pageAnswers="${page.pageAnswers}" />
				            </c:when>
				        </c:choose>
					</c:forEach>
				</div>
			</c:forEach>
		</div>
		<c:if test="${not empty exampleCitation}">
			<div class="centeredContent">
				<h2>Example Citation</h2>
				${exampleCitation}
			</div>
		</c:if>
		<div class="uploadedFilesContainer">
			<h2 class="centeredContent">Submission Data Files</h2>
		    <c:choose>
				<c:when test="${not empty submissionDataFiles}" >
					<display:table name="submissionDataFiles" id="file">
						<display:column title="Data File Name" property="fileName" class="filename" />
						<display:column title="Size" property="fileSizeString" class="filesize" />
						<display:column title="Description" property="fileDescription" />
						<display:column title="Type" property="fileTypeTitle" class="centeredContent" />
						<display:column title="Format" property="formatTitle" />
						<display:column title="Version" property="formatVersion" />
					</display:table>
				</c:when>
				<c:otherwise>
				    <p class="noDataFilesSubmitted">No files submitted.</p>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="centeredContent">
			<img src="${pageContext.request.contextPath}/images/branding-band.png" />
		</div>
	</div>
</body>
</html>