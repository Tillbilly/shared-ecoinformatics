<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Submission ${review.peerReview ? 'Peer ' : ''}Review Confirmation</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<script type="text/javascript">
	$(function(){
	    $(".backButton").click(function(){
	    	window.location="${pageContext.request.contextPath}/modifyReview?submissionId=${submission.submissionId}";
	    });
        $(".publishButton").click(function(){
        	window.location="${pageContext.request.contextPath}/finaliseReview?submissionId=${submission.submissionId}";
	    });
	});
	</script>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Submission Review" />
		<div class="centeredContent infoBox msgBoxSpacing">
			<h3>Thanks for ${review.peerReview ? 'peer' : ''} reviewing the submission:</h3>
			<p>${submission.submissionTitle}</p>
		</div>
		<c:choose>
		  <c:when test="${not review.peerReview}">
		    <p>Based on your review, this submission can be published into AEKOS.</p>
		  </c:when>
		  <c:otherwise>
		    <p>Based on your peer review, this submission will now be reviewed by a AEKOS reviewer.</p>
		  </c:otherwise>
		</c:choose>  
		<p>
		  <c:choose>
		      <c:when test="${not review.peerReview}">
		  Select 'Back' to make changes, or 'Publish' to continue.
		      </c:when>
		      <c:otherwise>
		      Select 'Back' to make changes, or 'Finalise' to notify an AEKOS reviewer.
		      </c:otherwise>
		  </c:choose>
		</p>
		<c:if test="${not empty review.notes}">
			<p>
				You have entered the following notes:<br>
				${review.notes}
			</p>
		</c:if>
		<div class="centeredContent">
			<button class="backButton">Back</button>
			<button class="publishButton">${review.peerReview ? 'Finalise' : 'Publish'}</button>
		</div>
	</div>
</body>
</html>