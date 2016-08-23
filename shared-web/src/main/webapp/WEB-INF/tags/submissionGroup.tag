<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="model" type="au.edu.aekos.shared.web.model.GroupModel" required="true" %>
<%@ attribute name="review" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="false" %>
<%@ attribute name="groupIndent" type="java.lang.Integer" required="true" %>
<%@ attribute name="showTitle" type="java.lang.Boolean" required="false" %>
<%@ attribute name="showDescription" type="java.lang.Boolean" required="false" %>
<%@ attribute name="showResponseType" type="java.lang.Boolean" required="false" %>
<%@ attribute name="divclass" required="false" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty showDescription}">
    <c:set var="showDescription" value="false" />
</c:if>

<div id="G${model.groupId}" class="viewSubmissionGroup vsg${groupIndent} ${not empty divclass ? divclass : ''}">
    <div class="twisty twistyOpen">
	    <div class="submissionQuestionGroupNumber">${model.groupId}</div>
	    <div class="submissionQuestionGroupText">${model.title}</div>
	    <div class="submissionQuestionGroupIcon">
	    	<div class="ui-icon ui-icon-triangle-1-s"></div>
	   	</div>
    </div>
    <div class="groupContent">
	    <c:if test="${showDescription and not empty model.description}">
	        <div class="groupDescription">
	            <p>${model.description}</p>
	        </div>
	    </c:if>
	   
		<c:forEach var="item" items="${model.items}">
		   <c:choose>
		       <c:when test="${item.itemType eq 'QUESTION' or item.itemType eq 'QUESTION_SET'}">
	                <q:submissionQuestion model="${item}" groupIndent="${groupIndent + 1}" showResponseType="${showResponseType }" review="${review}"/>
	            </c:when>
	            <c:when test="${item.itemType eq 'GROUP'}">
	                <q:submissionGroup model="${item}" groupIndent="${groupIndent + 1}" showResponseType="${showResponseType }" review="${review}"/>
	            </c:when>
		   </c:choose>
		</c:forEach>
	</div>
</div>