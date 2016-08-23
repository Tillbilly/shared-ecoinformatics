
<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Manage Current Submissions</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<script type="text/javascript">
		$(function(){
		    $(".reviewButton").each(function(){
		    	$(this).click(function(){
		    		//Get the submissionId
		    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
		    		window.location="${pageContext.request.contextPath}/reviewSubmission?submissionId=" + submissionId;
		    	});
		    });
		    
		    $(".manageControlCloneButton").each(function(){
		    	$(this).click(function(){
		    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
		    		window.location="${pageContext.request.contextPath}/cloneSubmission?submissionId=" + submissionId;
		    	});
		    });
		    
		    $(".manageControlLinkButton").each(function(){
		    	$(this).click(function(){
		    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
		    		window.location="${pageContext.request.contextPath}/linkSubmission?submissionId=" + submissionId;
		    	});
		    });
		    
		    $(".manageControlDeleteButton").each(function(){
		    	$(this).click(function(){
		    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
		    		if(confirm("You are about to permanently delete Submission " + submissionId)){
		    			window.location="${pageContext.request.contextPath}/deleteSubmission?submissionId=" + submissionId;
		    		}
		    	});
		    });
		    
		    $(".manageControlReportsButton").each(function(){
		    	$(this).click(function(){
		    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
		    		window.location="${pageContext.request.contextPath}/reports/publicationCertificate?submissionId=" + submissionId;
		    	});
		    });
		    
		});
	</script>
	<style type="text/css">
		table.displayTable td {
			vertical-align: middle;
		}
		
		table.displayTable td.centeredContent {
			text-align: center;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader titleText="Manage Current Submissions" includeHomeButton="true" />
		<div class="centeredContent">
			<h2 class="noMargin">User: <span class="focusText">${username}</span></h2>
		</div>
		<div>
		    <display:table name="userSubmissionList" id="sub" class="displayTable" sort="page" requestURI="manageSubmissions" htmlId="manageSubmissionsListing">
				<display:setProperty name="basic.empty.showtable" value="true" />
		        <display:column title="ID" property="id" sortable="true" >
		        	${sub.id}
	        	</display:column>
		        <display:column title="Submission Title" sortable="true" >
		            <c:choose>
			            <c:when test="${sub.status.deleted}" >
			                ${sub.title}
			            </c:when>
			            <c:otherwise>
			                <a title="${not empty sub.draftForSubmissionId ? 'Latest version' : ''}" href="viewSubmission?submissionId=${sub.id}">${sub.listDisplayTitle}</a>
			                <c:if test="${not empty sub.draftForSubmissionId }"><span title="Latest version" class="latestVersionStar">&#9733;</span></c:if>
			            </c:otherwise>
		            </c:choose>
		        </display:column>
		        <display:column title="Created Date" sortProperty="submissionDate" sortable="true" class="centeredContent" >
		        	<span class="dontWrap"><fmt:formatDate pattern="MMM d, yyyy" value="${sub.submissionDate}" /></span>
		            <span class="dontWrap"><fmt:formatDate pattern="HH:mm" value="${sub.submissionDate}" /></span>
	        	</display:column>
		        <display:column title="Status" property="status" sortable="true"  class="centeredContent" />
		        <display:column title="Last review" sortable="true" sortProperty="lastReviewDate" class="centeredContent">
		            <span class="dontWrap"><fmt:formatDate pattern="MMM d, yyyy" value="${sub.lastReviewDate}" /></span>
		            <span class="dontWrap"><fmt:formatDate pattern="HH:mm" value="${sub.lastReviewDate}" /></span>
		        </display:column>
		        <display:column title="Controls" class="dontWrap">
		        	<button class="manageControlCloneButton controlButton">
			            <img title="Clone Submission" src="${pageContext.request.contextPath}/images/icon_copy.png"/>
			            <br />Clone
		        	</button>
		        	<c:set var="linkButtonVisibility" value="${sub.status.linkable ? '' : 'invisible'}" />
		        	<button class="manageControlLinkButton controlButton ${linkButtonVisibility}">
			            <img title="Launch Link Submission Console" src="${pageContext.request.contextPath}/images/icon_link.png"/>
			            <br />Link
		        	</button>
		            <c:set var="deleteButtonVisibility" value="${sub.status.deletable ? '' : 'invisible'}" />
	                <button class="manageControlDeleteButton controlButton ${deleteButtonVisibility}">
		                <img title="Delete Submission" src="${pageContext.request.contextPath}/images/icon_trash.png"/>
		                <br />Delete
	                </button>
	                <c:set var="reportButtonVisibility" value="${sub.status.publicationCertificateAvailable ? '' : 'invisible'}" />
	                <button class="manageControlReportsButton ${reportButtonVisibility}">
		                <img title="View Publication Certificate" src="${pageContext.request.contextPath}/images/icon_report.png"/>
		                <br />Certificate
	                </button>
	            </display:column>
		        <display:column headerClass="hiddenColumn" class="submissionIdCell hiddenColumn">${sub.id}</display:column>
		    </display:table>
		</div>
	</div>
</body>
</html>