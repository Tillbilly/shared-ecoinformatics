<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>SHaRED Show All Submissions</title>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/datatables/css/jquery.dataTables.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.dataTables.min.js"></script>
	<script type="text/javascript">
		$.fn.dataTableExt.ofnSearch['html'] = function ( sData ) {
			var n = document.createElement('div');
			n.innerHTML = sData;
			if ( n.textContent ) {
				return n.textContent.replace(/\n/g," ");
			} else {
				return n.innerText.replace(/\n/g," ");
			}
		};	
	
		var asInitVals = new Array();
		
		$(document).ready(function() {
			var oTable = $('#submissionListing').dataTable({
				"aoColumns": [
					null, null, null, null, null, null
				],
				"oLanguage": {
					"sSearch": "Search all columns:"
				}
			});
			
			// Multi-filter code copied from http://datatables.net/examples/api/multi_filter.html
			$("tfoot input").keyup( function () {
				/* Filter on the column (the index) of this element */
				oTable.fnFilter( this.value, $("tfoot input").index(this) );
			} );
			
			// Support functions to provide a little bit of 'user friendlyness' to the textboxes in the footer
			$("tfoot input").each( function (i) {
				asInitVals[i] = this.value;
			} );
			
			$("tfoot input").focus( function () {
				if ( this.className == "search_init" ) {
					this.className = "";
					this.value = "";
				}
			} );
			
			$("tfoot input").blur( function (i) {
				if ( this.value == "" ) {
					this.className = "search_init";
					this.value = asInitVals[$("tfoot input").index(this)];
				}
			} );
		});
	</script>
	<style type="text/css">
		tfoot input.search_init {
			color: #999;
		}
		
		tfoot input {
			margin: 0.5em 0;
			width: 100%;
			color: #444;
		}
		
		.pageText {
			margin-top: 2em;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="All Submissions" />
		<div>
			<table id="submissionListing" class="sharedDatatable">
				<thead>
					<tr>
						<th>ID</th>
						<th>Submission Title</th>
						<th>Created Date</th>
						<th>Status</th>
						<th>Last review</th>
						<th>Owner</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<th><input type="text" name="search_id" value="Search ID" class="search_init"></th>
						<th><input type="text" name="search_title" value="Search submission title" class="search_init"></th>
						<th><input type="text" name="search_created" value="Search created date" class="search_init"></th>
						<th><input type="text" name="search_status" value="Search status" class="search_init"></th>
						<th><input type="text" name="search_reviewed" value="Search last review" class="search_init"></th>
						<th><input type="text" name="search_owner" value="Search owner" class="search_init"></th>
					</tr>
				</tfoot>
				<tbody>
					<c:forEach var="sub" items="${submissionList}">
						<tr>
							<td>${sub.id}</td>
							<td>
								<c:choose>
						            <c:when test="${sub.status.deleted}">
						                ${sub.title}
						            </c:when>
						            <c:otherwise>
						                <a href="${pageContext.request.contextPath}/viewSubmission?submissionId=${sub.id}">${sub.title}</a>
						            </c:otherwise>
					            </c:choose>
							</td>
							<td>
								<fmt:formatDate pattern="dd-MM-yyyy HH:mm" value="${sub.submissionDate}" />
							</td>
							<td>${sub.status}</td>
							<td>
								<fmt:formatDate pattern="dd-MM-yyyy HH:mm" value="${sub.lastReviewDate}" />
							</td>
							<td>${sub.submittingUsername}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<p class="pageText lessImportantText centeredContent">This page lists all submissions in this installation of SHaRED.
			<br />You can sort or search any column and view any non-deleted submission by clicking its ID</p>
	</div>
</body>
</html>