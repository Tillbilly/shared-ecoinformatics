<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="group" type="au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup" required="true" %>
<%@ attribute name="controlledVocab" type="java.util.Map" required="true" %>
<%@ attribute name="questionDisplayConditionMap" type="java.util.Map" required="true" %>
<%@ attribute name="groupDisplayConditionMap" type="java.util.Map" required="true" %>
<%@ attribute name="reusableGroupOptionMap" type="java.util.Map" required="true" %>
<%@ attribute name="dynatreeDivMap" type="java.util.Map" required="false" %>
<%@ attribute name="imageAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="imageSeriesAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="reviewModel" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="false" %>
<%@ attribute name="titleQuestionId" type="java.lang.String" required="false" %>
<%@ attribute name="displayQuest" type="au.edu.aekos.shared.questionnaire.DisplayQuestionnaire" required="true" %>
<%@ attribute name="divclass" type="java.lang.String" required="false" %>
<%@ attribute name="indentDepth" type="java.lang.Integer" required="true" %>
<%@ attribute name="answers" type="au.edu.aekos.shared.questionnaire.PageAnswersModel" required="true" %>

<c:set var="styleString" value="" />
<c:if test="${not empty groupDisplayConditionMap[group.id] and not groupDisplayConditionMap[group.id].isVisible}">
    <c:set var="styleString" value="display:none;" />
</c:if>
<c:set var="nextIndentDepth" value="${indentDepth + 1 }" />

<div id="G${group.id}" class="questionGroup ${not empty divclass ? divclass : ''}" style="${styleString}">
    <c:if test="${not empty group.groupTitle}">
        <div class="questionGroupHeader">
	        <c:choose>
	            <c:when test="${group.showId}">
	            	<span class="questionGroupHeaderText">${group.id} <span class="questionGroupTitle">${group.groupTitle}</span></span>
	            </c:when>
	            <c:otherwise>
	                <span class="questionGroupHeaderText">${group.groupTitle}</span>
	            </c:otherwise>
	        </c:choose>    
	         <c:if test="${not empty group.groupDescription and group.groupDescription.trim().length() > 0}">
	       		<p class="questionGroupHelpText">${group.groupDescription}</p>
			</c:if>
		</div>
    </c:if>
    <c:if test="${not empty reusableGroupOptionMap[group.id] and fn:length( reusableGroupOptionMap[group.id] ) > 0 }">
        <form:select path="selectedReusableGroupMap[${group.id}]" cssClass="reusableGroupSelect">
             <form:option label="" value="" />
             <form:options items="${reusableGroupOptionMap[group.id] }" />
       	</form:select>
    </c:if>	
    <c:if test="${group.clearButton }">
        <button class="groupClearButton">Clear Group</button>
    </c:if>
	<c:forEach var="element" items="${group.elements}">
	   <c:choose>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
	           <q:renderQuestionGroup group="${element}" controlledVocab="${controlledVocab }" 
	                                  groupDisplayConditionMap="${groupDisplayConditionMap }" 
	                                  questionDisplayConditionMap="${questionDisplayConditionMap }"
	                                  imageSeriesAnswerMap="${quest.imageSeriesAnswerMap}"
	                                  imageAnswerMap="${imageAnswerMap}" reviewModel="${ empty reviewModel ? null : reviewModel }"
	                                  titleQuestionId="${ empty titleQuestionId ? null : titleQuestionId }"
	                                  reusableGroupOptionMap="${reusableGroupOptionMap }"
	                                  displayQuest="${displayQuest}"
	                                  indentDepth="${nextIndentDepth}"
	                                  answers="${answers}"
	                                  dynatreeDivMap="${empty dynatreeDivMap ? null : dynatreeDivMap }"/>
	       </c:when>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
	           <q:renderQuestion question="${element}" controlledVocab="${controlledVocab }" 
	                             questionDisplayConditionMap="${questionDisplayConditionMap }" 
	                             imageAnswerMap="${imageAnswerMap}"
	                             imageSeriesAnswerMap="${quest.imageSeriesAnswerMap}"
	                             reviewModel="${ empty reviewModel ? null : reviewModel }" 
	                             titleQuestionId="${ empty titleQuestionId ? null : titleQuestionId }"
	                             displayQuest="${displayQuest}"
	                             indentDepth="${nextIndentDepth}"
	                             dynatreeDivMap="${empty dynatreeDivMap ? null : dynatreeDivMap }" />
	       </c:when>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup' }">
	            <q:renderQuestionSet set="${element}"
	                                 controlledVocab="${controlledVocab }"
	                                 reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
	                                 displayQuest="${displayQuest}"
	                                 indentDepth="${nextIndentDepth}"
	                                 answers="${answers}"/>
	        </c:when>
	       
	       
	       
	   </c:choose>
	</c:forEach>
</div>