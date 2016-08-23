<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Main Menu</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<style type="text/css">
		#menuTable {
			margin-top: 1em;
			margin-bottom: 1em;
		}
		
		html {
			height: 100%;
		}
		
		.main-content {
			border-radius: 10px;
		}
		
		#wrapper {
			display:table;
			width: 100%;
			height: 66%;
		}
		
		#cell {
			display: table-cell;
			vertical-align: middle;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div id="wrapper">
		<div id="cell">
			<div class="main-content">
				<q:pageHeader includeHomeButton="false" titleText="Main Menu" />
				<c:if test="${not empty sessionExpiredMessage }">
					<div class="errorBox msgBoxSpacing centeredContent">${sessionExpiredMessage}</div>
				</c:if>
				<div>
					<table id="menuTable" class="centeredBlock">
						<tr>
							<td>
								<a class="hp-nav" href="${pageContext.request.contextPath}/newSubmission">
									<button>New Data Submission</button>
								</a>
							</td>
							<td class="nav-desc">Create a new submission from scratch</td>
						</tr>
						<tr>
							<td>
								<a class="hp-nav" href="${pageContext.request.contextPath}/manageSubmissions">
									<button>Manage Current Submissions</button>
								</a>
							</td>
							<td class="nav-desc">Manage your current un-published submissions</td>
						</tr>
						<tr>
							<td>
								<a class="hp-nav" href="${pageContext.request.contextPath}/useradmin/updateDetails">
									<button>Update Your Details</button>
								</a>
							</td>
							<td class="nav-desc">Update your SHaRED user profile</td>
						</tr>
						<sec:authorize ifAllGranted="ROLE_REVIEWER">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/listSubmissionsForReview">
										<button>Review Submissions</button>
									</a>
								</td>
								<td class="nav-desc">Review Submissions</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAllGranted="ROLE_ADMIN">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/admin/userManagement">
										<button>Manage Users</button>
									</a>
								</td>
								<td class="nav-desc">Activate/Deactivate users, assign roles</td>
							</tr>
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/admin/publicationConsole">
										<button>Publication Console</button>
									</a>
								</td>
								<td class="nav-desc">Check publication error messages, re-publish submissions</td>
							</tr>
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/admin/viewConfig">
										<button>View Config Properties</button>
									</a>
								</td>
								<td class="nav-desc">View config settings from the .properties file</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_ADMIN, ROLE_REVIEWER">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/showAllSubmissions">
										<button>View All Submissions</button>
									</a>
								</td>
								<td class="nav-desc">View all submissions regardless of user, status, etc</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAllGranted="ROLE_VOCAB_MANAGER">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/vocabManagement/vocabularyManagement">
										<button>Vocabulary Management</button>
									</a>
								</td>
								<td class="nav-desc">Manage custom vocabulary lists</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_GROUP_ADMIN">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions">
										<button>Manage Group Submissions</button>
									</a>
								</td>
								<td class="nav-desc">User Group Member Submission Management</td>
							</tr>
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/groupAdmin/peerReviewSubmissions">
										<button>Review Group Submissions</button>
									</a>
								</td>
								<td class="nav-desc">Peer Review Group Submissions</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAllGranted="ROLE_DEVELOPER">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/dev/uploadQuestionnaire">
										<button>Test Questionnaire</button>
									</a>
								</td>
								<td class="nav-desc">Test a Questionnaire Configuration</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAnyGranted="ROLE_ADMIN, ROLE_REVIEWER, ROLE_DEVELOPER">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/admin/statistics">
										<button>Statistics</button>
									</a>
								</td>
								<td class="nav-desc">Stats about this SHaRED installation</td>
							</tr>
						</sec:authorize>
						<sec:authorize ifAllGranted="ROLE_ADMIN">
							<tr>
								<td>
									<a class="hp-nav" href="${pageContext.request.contextPath}/admin/activeSessions">
										<button>Check Active Sessions</button>
									</a>
								</td>
								<td class="nav-desc">View Users Currently Logged In</td>
							</tr>
						</sec:authorize>
					</table>
				</div>
				<p class="centeredContent lessImportantText">
					If you have any queries regarding SHaRED Submissions please contact us at <a href="mailto:enquiry@aekos.org.au">enquiry@aekos.org.au</a>.
				</p>
				<q:brandingBlurb />
			</div>
		</div>
	</div>
</body>
</html>