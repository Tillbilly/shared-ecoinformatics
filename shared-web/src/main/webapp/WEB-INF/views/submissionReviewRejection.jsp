<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
    <q:sharedHeadTitle>Submission Rejection</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<script type="text/javascript">
	   $(function(){
		  $("#backButton").click(function(){
			  window.location="${pageContext.request.contextPath}/modifyReview?submissionId=${rejection.submissionId}";
		  });
          $("#sendButton").click(function(){
        	  $("#rejectionEmailForm").submit();
		  });
	   });
	</script>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Submission Rejection Email" backUrl="${pageContext.request.contextPath}/modifyReview?submissionId=${rejection.submissionId}" />
	    <p>You have chosen to reject the submission titled '<span class="focusText">${submission.submissionTitle}</span>'. Please review the email that will be sent.</p>
	    <c:choose>
	        <c:when test="${review.reviewOutcome eq 'REJECTED'}"> 
		        <p>This submission will be discarded after you click the <span class="buttonName">send</span> button.
		           If this is not what you desire please click back and perform the review,
		           but select <span class="buttonName">finalise</span>.
           		</p>
				<p>
		           You may modify the email text below before sending.
		        </p>
	        </c:when>
	        <c:otherwise>
	           <p>After you click the <span class="buttonName">send</span> button, the submitter will be sent the email below
	           		informing them of the reasons for the submission rejection.
	           		The email will also contain a link for them to modify their submission and resubmit.
	           		If this is not what you desire please click <span class="buttonName">back</span>.
				</p>
	           <p>
	           		You may modify the email text below before sending.
         		</p>
	        </c:otherwise>
	    </c:choose>
	
	    <form:form action="processRejection" commandName="rejection" id="rejectionEmailForm" class="centeredContent">
	        <form:hidden path="submissionId" />
	        <form:textarea path="rejectionEmail" cssClass="rejectionEmailTextArea" />
	    </form:form>
	    <div class="centeredContent">
		    <button id="backButton">Back</button>
		    <button id="sendButton">Send Email</button>
	    </div>
	</div>
</body>
</html>