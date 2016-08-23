<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Submission Publication Console</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Publication Console" />
		<div>
			<c:if test="${not empty republishSubmissionId }">
				<div class="infoBox msgBoxSpacing centeredContent">
					Republish operation for ${republishSubmissionId } finished with status ${republishStatus}
				</div>
			</c:if>
			<c:if test="${not empty rifcsRecordsGenerated}">
				<div class="infoBox msgBoxSpacing centeredContent">
					Regenerated ${rifcsRecordsGenerated} RIF-CS records.
				</div>
			</c:if>

			<table class="centeredBlock">
				<tr>
					<td><a class="hp-nav" href="${pageContext.request.contextPath}/admin/reindexSubmissions">Re-index submissions</a></td>
					<td class="nav-desc">Regenerate the solr index for all published submissions</td>
				</tr>
				<tr>
					<td><a class="hp-nav" href="${pageContext.request.contextPath}/admin/regenerateRifcs">Regenerate Rifcs Files</a></td>
					<td class="nav-desc">Regenerates Rifcs documents to local configured location ( disaster recovery) </td>
				</tr>
				
				
			</table>
			<c:if test="${not empty failedSubmissionList }">
				<div class="errorBox msgBoxSpacing centeredContent">
					<p>Some submissions have failed the publish workflow!!</p>
				</div>
				<c:forEach items="${failedSubmissionList}" var="sub">
					<div class="failedSubmission">
						<div class="centeredContent">
							<h3>${sub.title}</h3>
							<a href="${pageContext.request.contextPath}/admin/republishSubmission?submissionId=${sub.id}">
								<button>Republish</button>
							</a>
						</div>
						<q:viewPublicationLog log="${publicationLogMap[sub.id]}" />
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</body>
</html>