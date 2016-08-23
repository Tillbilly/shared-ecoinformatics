<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="lastReview" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${not empty lastReview}" >
	<div class="errorBox msgBoxSpacing centeredContent">
	    <h2>There are some issues with your submission</h2>
	    <p>General review comments are as follows:<br/>
		    <c:choose>
		    	<c:when test="${not empty lastReview.notes}">${lastReview.notes}</c:when>
		    	<c:otherwise>(No General Comments)</c:otherwise>
		    </c:choose>
	    </p>
	    <p>Issues are shown in red</p>
	    <c:choose>
	    	<c:when test="${lastReview.hasRejectedAnswers}">
		        <p>There are issues related to the metadata.</p>
	    	</c:when>
	    	<c:otherwise>
	    		<p>All the metadata has passed the review.</p>
	    	</c:otherwise>
	    </c:choose>
	    <c:choose>
	    	<c:when test="${lastReview.hasRejectedFiles}">
		        <p>There are issues related to the file entries.</p>
	    	</c:when>
	    	<c:otherwise>
	    		<p>All the file entries have passed the review.</p>
	    	</c:otherwise>
	    </c:choose>
	</div>
</c:if>