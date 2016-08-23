<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Manage Group Submissions</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/datatables/css/jquery.dataTables.css"></link>
    <script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.dataTables.min.js"></script>
	<style type="text/css">
		table.displayTable td {
			vertical-align: middle;
		}
	</style>
	<script type="text/javascript">
	    function getSubmissionIdForControl(ctrlEl){
	    	return $(ctrlEl).closest("tr").find("td.submissionId").first().html();
	    };
	    function getGroupIdForControl(ctrlEl){
	    	return $(ctrlEl).closest("div.groupSubmissionsDiv").find("div.groupIdDiv").first().html();
	    };
	    
	    
		$(function(){
			$('.submissionTable').dataTable({
				"aoColumns" : [ null, null, null, null, null, null,{ "orderable": false }]
			});
			$('.submissionTable').on("click",".ctrlViewButton", function(){
				var subId = getSubmissionIdForControl(this);
				window.location="${pageContext.request.contextPath}/groupAdmin/viewSubmission?submissionId=" + subId;
			});
			$('.submissionTable').on("click",".ctrlCloneButton",function(){
				var subId = getSubmissionIdForControl(this);
				window.location="${pageContext.request.contextPath}/groupAdmin/cloneSubmission?submissionId=" + subId;
			});
			$('.submissionTable').on("click",".ctrlLinkButton",function(){
				var subId =  getSubmissionIdForControl(this);
				var grpId = getGroupIdForControl(this);
				window.location="${pageContext.request.contextPath}/groupAdmin/linkSubmission?submissionId=" + subId + "&groupId=" + grpId;
			});
			$('.submissionTable').on("click",".ctrlEditButton",function(){
				var subId = getSubmissionIdForControl(this);
				window.location="${pageContext.request.contextPath}/groupAdmin/editSubmission?submissionId=" + subId;
			});
			$('.submissionTable').on("click",".ctrlDeleteButton",function(){
				var subId = getSubmissionIdForControl(this);
				if(confirm("You are about to permanently delete Submission " + subId + ". Are you sure??")){
				    window.location="${pageContext.request.contextPath}/groupAdmin/deleteSubmission?submissionId=" + subId;
				}
			});
			$('.submissionTable').on("click",".ctrlTransferButton",function(){
				var subId = getSubmissionIdForControl(this);
				var grpId = getGroupIdForControl(this);
				window.location="${pageContext.request.contextPath}/groupAdmin/transferSubmission?submissionId=" + subId + "&groupId=" + grpId;
			});
			$('.submissionTable').on("click",".ctrlViewPCButton", function(){
				var subId = getSubmissionIdForControl(this);
				window.location="${pageContext.request.contextPath}/reports/publicationCertificate?submissionId=" + subId;
			});
			
		});
	
	</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader titleText="Manage Group Submissions" includeHomeButton="true" />
		<div class="centeredContent">
			<h2 class="noMargin">Group Administrator: <span class="focusText">${groupAdminUsername}</span></h2>
		</div>
		<c:if test="${not empty groupAdminErrorMessage}">
		    <div class="errorMessage">${groupAdminErrorMessage}</div>
		</c:if>
		<c:if test="${not empty groupAdminMessage }">
		    <div>${groupAdminMessage}</div>
		</c:if>
		<!-- Iterate over the groupSubmissionMap -->
		<c:forEach items="${ groupSubmissionMap}" var="entry" varStatus="indx">
		    <div class="groupHeaderBlock">
		        <div style="float: left">
		        <span>Group Name: ${entry.key.name }</span><br/>
		    <c:if test="${not empty entry.key.organisation }">
		        <span>Organisation: ${entry.key.organisation }</span><br/>
		    </c:if>
		        </div>
		        <div style="float: right">
		            <span>Group Members</span><br>
		            <table>
					    <c:forEach items="${entry.key.memberList}" var="member" >
					        <tr><td>${member.username}</td><td>${member.emailAddress}</td></tr>
					    </c:forEach>
				    </table>
		        </div>
		    </div>
		    
		    <c:set var="submissionList" value="${entry.value}" scope="request"  />
			<div class="groupSubmissionsDiv">
			    <div class="groupIdDiv" style="display:none;">${entry.key.id}</div>
			    <!-- Replace this with DataTables -->
			    <table class="submissionTable">
			        <thead>
			          <tr>
			            <th>ID</th>
			            <th>Submitter</th>
			            <th>Submission Title</th>
			            <th>Created</th>
			            <th>Status</th>
			            <th>Last Review</th>
			            <th>Controls</th>
			          </tr>
			        </thead>
			        <tbody>
				    <c:forEach items="${submissionList}" var="sub" varStatus="indx" >
					  <tr>
					    <td class="submissionId">${sub.id}</td>
					    <td class="username">${sub.submitter.username}</td>
					    <td class="title">
					      <c:choose>
				            <c:when test="${sub.status.deleted}" >
				                ${sub.title}
				            </c:when>
				            <c:otherwise>
				                <a href="${pageContext.request.contextPath}/groupAdmin/viewSubmission?submissionId=${sub.id}">${sub.title}</a>
				            </c:otherwise>
			              </c:choose>
					    </td>
					    <td class="createdDate"><fmt:formatDate pattern="dd/MM/yyyy" value="${sub.submissionDate}" /></td>
					    <td class="status">${sub.status}</td>
					    <td class="lastReview">
					        <div><fmt:formatDate pattern="dd/MM/yyyy" value="${sub.lastReviewDate}" /></div>
					        
					    </td>
					    <td class="controls">
					       <!-- We will always have the top line of View Clone Link -->
					       <!-- The second line depends on whether the submission is published or not -->
					       <!-- If not published  Edit  Delete  Transfer  -->
					       <!-- If published - publication certificate -->
					        <div class="controlButtonsTopLine">
					          <button title="View Submission ${sub.id}" type="button" class="ctrlViewButton">V</button>
					          <button title="Clone Submission ${sub.id}" type="button" class="ctrlCloneButton">C</button>
					          <c:if test="${sub.status.linkable }">
					            <button title="Link Submission ${sub.id}" type="button" class="ctrlLinkButton">L</button>
					          </c:if>
					        </div>
					        <c:choose>
					          <c:when test="${not sub.status.approved }" >
					            <div class="controlButtonsBottomLine">
					              <button title="Edit Submission ${sub.id}" type="button" class="ctrlEditButton">E</button>
					              <button title="Delete Submission ${sub.id}" type="button" class="ctrlDeleteButton">D</button>
					              <button title="Transfer Submission ${sub.id}" type="button" class="ctrlTransferButton">T</button>
					            </div>
					          </c:when>
					          <c:when test="${sub.status.publicationCertificateAvailable }">
					            <div class="controlButtonsBottomLine">
					                <button title="View Publication Certificate For ${sub.id}" type="button" class="ctrlViewPCButton">PC</button>
					            </div>
					          </c:when>
					        </c:choose>
					    </td>
					  </tr>
				    </c:forEach>
				    </tbody>
			    </table>
			
			</div>
		
		</c:forEach>
		<hr>
	</div>
</body>
</html>