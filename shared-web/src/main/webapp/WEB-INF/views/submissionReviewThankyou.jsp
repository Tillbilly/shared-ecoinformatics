<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>SHaRED Submission Review Thank You</title>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<script type="text/javascript">
		$(function(){
		    $("#homeButton").click(function(){
		    	window.location="${pageContext.request.contextPath}";
		    });
		});
	</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Submission Review" />
		<div class="msgBoxSpacing infoBox centeredContent">
		    
			<h3>Thanks for ${reviewModel.peerReview ? 'peer ' : '' }reviewing the submission:</h3>
		
			<p>${reviewModel.submissionTitle}</p>
		    <c:choose>
		        <c:when test="${reviewModel.reviewOutcome eq 'PUBLISH' and not reviewModel.peerReview}" >
		            
					<p>The submission has been <b>validated</b>.</p>
					<p>It has begun the process for publication into AEKOS.</p>
				
				</c:when>
				<c:when test="${reviewModel.reviewOutcome eq 'PUBLISH' and reviewModel.peerReview}" >
					<p>The submission has been <b>peer reviewed</b>.</p>
					<p>A SHaRED reviewer will now review the submission before publication into AEKOS.</p>
					<p>You will be notified of the outcome.</p>
				</c:when>
			    <c:when test="${reviewModel.reviewOutcome eq 'MOD_REQUESTED'}">
		    		<p>The submission has been <b>rejected</b>.</p>
		    		<p>It is now awaiting further information from the submitter.</p>
		    	</c:when>
		    </c:choose>
		    <div>
				<button id="homeButton">Home</button>
		    </div>
		</div>
	</div>
</body>
</html>