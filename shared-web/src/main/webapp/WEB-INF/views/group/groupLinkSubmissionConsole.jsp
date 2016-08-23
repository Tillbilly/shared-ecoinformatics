<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Group Link Console </q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
    $(function(){
	    $(".linkSubmissionIcon").click(function(){
	    	var subIdToLink = $(this).closest("tr").find("td.subIdCell").first().html();
	    	$("#linkToSubmissionId").val(subIdToLink);
	    	var toLinkTitle = $(this).closest("tr").find("td.toLinkSubTitleCell").first().html();
	    	$("#linkToSubmissionNameCell").html(toLinkTitle);
	    	$("#createLinkFormDiv").show();
	    });
	    
	    $(".unlinkSubmissionIcon").click(function(){
	    	var sourceSubmissionId = '${submission.submissionId}';
	    	var subIdToUnlink = $(this).closest("tr").find("td.linkedSubIdCell").first().html();
	    	window.location.href = '${pageContext.request.contextPath}/groupAdmin/unlinkSubmissions/' + ${groupId} +'/' + sourceSubmissionId + "/" + subIdToUnlink;
	    });
	    
	    $("#createLinkCancelButton").click(function(){
	    	$("#linkToSubmissionId").val(null);
	    	$("#linkToSubmissionNameCell").html(null);
	    	$("#linkDescription").val(null);
	    	$("#createLinkFormDiv").hide();
	    });
    });
    </script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Link Console" backUrl="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions" />
		<h2 class="centeredContent">Link submissions</h2>
		<div class="submissionMetaDetails">
		    <div>
		        <span class="titleSpan">Submission Title:</span>
		        <span class="valueSpan">${submission.submissionTitle}</span>
		        <span class="titleSpan">Submission Id:</span>
		        <span class="valueSpan">${submission.submissionId}</span>
		    </div>
		    <div>
		        <span class="titleSpan">Submitted By:</span>
		        <span class="valueSpan">${submission.submittedByUsername}</span>
		        <span class="titleSpan">Config Id:</span>
		        <span class="valueSpan">${submission.questionnaireConfigId}</span>
		    </div>
		</div>
		
		<h2 class="centeredContent">Submissions to Link</h2>
		<c:choose>
		    <c:when test="${empty linkableSubmissionList}">
		       <span>No submissions available to link!</span><br/>
		    </c:when>
		    <c:otherwise>
		      <table class="sharedTable">
					<tr>
						<th>ID</th>
						<th>Submission Title</th>
						<th>Status</th>
						<th>Submitter</th>
						<th>Submission Date</th>
						<th></th>
					</tr>
					<c:forEach items="${linkableSubmissionList}" var="sub">
						<tr>
							<td class="subIdCell">${sub.id}</td>
							<td class="toLinkSubTitleCell">${sub.title}</td>
							<td>${sub.status}</td>
							<td>${sub.submitter.username}</td>
							<td>
								<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${sub.submissionDate}" />
							</td>
							<td>
								<button class="linkSubmissionIcon controlButton">
									<img src="${pageContext.request.contextPath}/images/icon_link.png" alt="Link Submission" />
									<br />Link
								</button>
							</td>
						</tr>
					</c:forEach>
				</table>
		    </c:otherwise>
		</c:choose>
		
		<div id="createLinkFormDiv" style="display:none">
		  <form id="createLinkForm" action="${pageContext.request.contextPath}/groupAdmin/linkSubmission" method="post">
		      <input type="hidden" id="linkFromSubmissionId" name="linkFromSubmissionId" value="${submission.submissionId}" >
		      <input type="hidden" name="groupId" value="${groupId}" >
		      <input class="linkFormField" type="hidden" id="linkToSubmissionId" name="linkToSubmissionId" value="" >
			  <table>
					<tr>
						<th>Name</th>
						<td id="linkToSubmissionNameCell"></td>
					</tr>
					<tr>
						<th>Link Type</th>
						<td><select id="linkTypeSelect" name="linkType">
								<option>LINKED</option>
								<option>NEW_VERSION</option>
								<option>RELATED</option>
						</select></td>
					</tr>
					<tr>
						<th>Description</th>
						<td><textarea class="linkFormField" id="linkDescription" name="description"></textarea></td>
					</tr>
					<tr>
						<td></td>
						<td><button id="createLinkCancelButton" type="button">Cancel</button>
							<button type="submit">Submit</button>
						</td>
				</table>
		  </form>
		</div>
		
		<c:if test="${not empty submissionLinkList}">
			<h1>Linked Submissions</h1>
			<table>
				<tr>
					<th>ID</th>
					<th>Title</th>
					<th>Type</th>
					<th>Description</th>
					<th>Link Date</th>
					<th>Linked By</th>
					<th></th>
				</tr>
				<c:forEach items="${submissionLinkList}" var="subLink">
			    <tr><td class="subLinkIdCell" style="display:none;">${subLink.id}</td>
			        <td class="linkedSubIdCell">${subLink.linkedSubmission.id }</td>
			        <td>${subLink.linkedSubmission.title }</td>
			        <td>${subLink.linkType}</td>
			        <td>${subLink.description}</td>
			        <td>${subLink.linkDate}</td>
			        <td>${subLink.linkedByUser.username}</td>
			        <td>
			        	<button class="unlinkSubmissionIcon controlButton">
					        <img src="${pageContext.request.contextPath}/images/icon_unlink.png" alt="Unlink Submission"/>
					        <br />Unlink
			        	</button>
			        </td>
			    </tr>
			  </c:forEach>
			</table> 
		</c:if>
	</div>
</body>
</html>