<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
    <q:sharedHeadTitle>Reviewable Submission List</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <style type="text/css">
    	table.displayTable {
    		width: 100%;
    	}
    </style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Reviewable Submissions" />
		<c:set var="peerReviewFlag" value="${not empty peerReview and peerReview }" />
		<c:choose>
		   <c:when test="${peerReviewFlag}">
		       <p>Please select a submission for peer review</p>
		   </c:when>
		   <c:otherwise>
		       <p>Please select a submission to review</p>
		   </c:otherwise>
	    </c:choose>	   
		<div>
		    <display:table name="submissionList" id="sub" class="displayTable" sort="page" requestURI="${currMappedUri}">
		        <display:column title="ID" sortable="true" sortProperty="submitter.id" >${sub.id}</display:column>
		        <display:column title="Submission Title" sortable="true" sortProperty="title">
		            <c:choose>
		                <c:when test="${sub.status eq 'SUBMITTED' or sub.status eq 'RESUBMITTED' or sub.status eq 'PEER_REVIEWED' }">
		        	        <a href="${pageContext.request.contextPath}/reviewSubmission?submissionId=${sub.id}&peerReview=${peerReviewFlag}">${sub.title}</a>
		        	    </c:when>
		        	    <c:when test="${sub.status eq 'REJECTED' or sub.status eq 'REJECTED_SAVED' }">
		        	        <a href="${pageContext.request.contextPath}/viewSubmission?submissionId=${sub.id}">${sub.title}</a>
		        	    </c:when>
		        	    <c:otherwise>
		        	        ${sub.title}
		        	    </c:otherwise>
		        	</c:choose>
	        	</display:column>
		        <display:column title="Submitter" sortable="true" sortProperty="submitter.username" >${sub.submitter.username}</display:column>
		        <display:column title="Created Date" sortable="true" sortProperty="submissionDate">
		        	<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${sub.submissionDate}" />
	        	</display:column>
		        <display:column title="Status" property="status" sortable="true" />
		        <display:column title="Last review" property="lastReviewDate" sortable="true"/>
		        <display:column headerClass="hiddenColumn" style="display:none;" class="submissionIdCell">${sub.id}</display:column>
		    </display:table>
		</div>
	</div>
</body>
</html>