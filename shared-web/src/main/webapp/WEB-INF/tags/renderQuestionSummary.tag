<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="question" type="au.edu.aekos.shared.questionnaire.jaxb.Question" required="true" %>
<%@ attribute name="divclass" type="java.lang.String" required="false" %>
<%@ attribute name="pageAnswers" type="au.edu.aekos.shared.questionnaire.PageAnswersModel" required="true" %>
<%@ attribute name="imageAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="geoFeatureSetMap" type="java.util.Map" required="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${empty summary }" >
   <c:set var="summary" value="false" />
</c:if>

<div class="${empty divclass ? 'questionDiv' : divclass}">
    <span class="questionIdSpan" style="vertical-align:top;">${question.id}</span>
    <span class="questionSpan" >${question.text}</span>
    <c:choose>
        <c:when test="${question.responseType eq 'GEO_FEATURE_SET'}">
            <c:if test="${ not empty geoFeatureSetMap[question.id] and fn:length( geoFeatureSetMap[question.id].features) > 0 }"> 
            	<table class="responseSummarySpan">
                   <tr><th>Feature ID</th><th>Description</th></tr>
                	  <c:forEach items="${geoFeatureSetMap[question.id].features }" var="feature">
                          <tr><td>${feature.id }</td><td>${feature.description}</td></tr>
                      </c:forEach>
                </table>
            </c:if>
        </c:when>
        <c:when test="${question.responseType eq 'TEXT_BOX'}">
            <div class="responseSummarySpan">
                <c:if test="${not empty pageAnswers.answers[question.id].response }">
                    <textarea style="border:none;" readonly="readonly" class="${empty question.responseInputClass ? '' : question.responseInputClass}">${ pageAnswers.answers[question.id].response }</textarea>
                </c:if>
            </div>
        </c:when>
        <c:when test="${not pageAnswers.answers[question.id].isMultiSelect}">
		   <div class="responseSummarySpan">
		   		${not empty pageAnswers.answers[question.id].displayResponse
		   			? pageAnswers.answers[question.id].displayResponse 
		   			: pageAnswers.answers[question.id].response }  
	   			${not empty pageAnswers.answers[question.id].suggestedResponse
	   				? pageAnswers.answers[question.id].suggestedResponse 
		   			: '' }
   			</div>
		   
		   <c:if test="${question.responseType eq 'IMAGE' and not empty pageAnswers.answers[question.id].response and not empty imageAnswerMap[question.id]}">
		        <span class="imageThumbSpan">
		            <a target="_blank" href="../getImage?imageId=${imageAnswerMap[question.id].imageObjectName }">
		                <img class="thumbnail" src="../getImage?imageId=${imageAnswerMap[question.id].imageThumbnailName }"/>
		            </a>
		        </span>
		   </c:if>
		</c:when>
		<c:otherwise>
		    <span class="responseSummarySpan" style="display:inline-block;">
		        <table>
	               <c:forEach items="${ pageAnswers.answers[question.id].multiselectAnswerList}" var="ans" >
	                   <tr><td>${empty ans.displayResponse ? ans.response : ans.displayResponse }</td></tr>
	               </c:forEach>
		        </table>
	        </span>
		</c:otherwise>
	</c:choose>
		   
</div>