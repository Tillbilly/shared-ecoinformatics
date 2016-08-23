<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="set" type="au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup" required="true" %>
<%@ attribute name="controlledVocab" type="java.util.Map" required="true" %>
<%@ attribute name="reviewModel" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="false" %>
<%@ attribute name="displayQuest" type="au.edu.aekos.shared.questionnaire.DisplayQuestionnaire" required="true" %>
<%@ attribute name="divclass" type="java.lang.String" required="false" %>
<%@ attribute name="indentDepth" type="java.lang.Integer" required="true" %>
<%@ attribute name="answers" type="au.edu.aekos.shared.questionnaire.PageAnswersModel" required="true" %>

<c:set var="nextIndentDepth" value="${indentDepth + 1 }" />

<div id="QS${set.id}" class="questionSet questionDiv ${not empty divclass ? divclass : ''}" style="${styleString}">
    <c:if test="${not empty set.text}">
		<div class="questionSetTitle">
			<c:if test="${set.showId}">
				${set.id}
			</c:if>
	        <span class="helpSpan">
		        <c:if test="${not empty set.description}">
		            <div class="helpIcon" alt="Group Help ${set.id}"></div>
		        	<div class="helpText">
		        		<p>${set.description}</p>
		       		</div>
	        	</c:if>
	        </span>
	      	<span class="questionSpan">
				${set.text}
			</span>
		</div>
    </c:if>
    <c:if test="${not empty set.textDetails}">
		<div class="questionSetDetails">${set.textDetails}</div>
    </c:if>
	<c:if test="${not empty reviewModel and not empty reviewModel.answerReviews[ set.id ] and reviewModel.answerReviews[ set.id ].outcome eq 'REJECT' }">
		<div class="modifySubmissionAnswerRejectionMessage">This answer was rejected with comment: ${ reviewModel.answerReviews[ set.id].comment }</div>
	</c:if>  
    <br/>
    <!-- <c:if test="${not empty set.description}"><p>${set.description}</p></c:if> -->
    <!--   -->
    <div class="questionSets">
		<c:forEach var="answerMap" items="${ answers.answers[set.id].answerSetList}" varStatus="indx">  <!-- indx.index -->
			<div class="questionSetEntryDiv" >
				<c:forEach var="element" items="${set.elements}" varStatus="elementStatus">
			       <c:if test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
						<c:set var="question" value="${element}"/>
						<div id="Q${question.id}_${indx.index}" class="questionDiv">
							<span class="questionSpan">
								<span class="helpSpan" >
									<c:if test="${indx.first and not empty question.description}">
										<div class="helpIcon" alt="Help ${question.id}"></div>
							        	<div class="helpText">
							        		<p>${question.description}</p>
							       		</div>
									</c:if>
								</span>
								${question.text}
								<c:if test="${question.responseMandatory}">
									<span class="mandatoryIndicator">&nbsp;*</span>
								</c:if>
							</span>
							<c:choose>
				           		<c:when test="${question.responseType eq 'TEXT'}">
				            		<span class="responseInputSpan"><form:input path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="${empty question.responseInputClass ? '' : question.responseInputClass }" cssErrorClass="formInputError"/></span>
				                    <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="formErrorSpan" />
				        		</c:when>
						        <c:when test="${question.responseType eq 'TEXT_BOX'}">
						            <span class="responseInputSpan"><form:textarea path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" 
						                  cssClass="${empty question.responseInputClass ? '' : question.responseInputClass }" cssErrorClass="formInputError"/></span>
						            <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="formErrorSpan" />
						        </c:when>
					            <c:when test="${question.responseType eq 'CONTROLLED_VOCAB'}">
						            <span class="responseInputSpan">
							            <form:select path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssErrorClass="formInputError">
							                <q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
							                <form:options items="${controlledVocab[question.traitName]}" itemLabel="displayString" itemValue="traitValue" />
							            </form:select>
						            	<form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="formErrorSpan" />
						            </span>
				        		</c:when>
						        <c:when test="${question.responseType eq 'CONTROLLED_VOCAB_SUGGEST' }">
						             <span class="responseInputSpan">
							            <form:select path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="selectWithSuggestOption" cssErrorClass="formInputError selectWithSuggestOption">
											<q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
							                <form:option label="Other - Please suggest" value="OTHER" />
							                <form:options items="${controlledVocab[question.traitName]}" itemLabel="displayString" itemValue="traitValue" />
							            </form:select>
						            </span>
						            <span class="responseSuggestInputSpan" style="display:none;">
						                <form:input path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].suggestedResponse" cssErrorClass="formInputError"/>
						            </span>
						            <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" />
						            <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].suggestedResponse" cssClass="formErrorSpan" />
						        </c:when>
						        <c:when test="${question.responseType eq 'DATE' }">
						            <span class="responseInputSpan"><form:input path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="dateResponseInput" cssErrorClass="dateResponseInput formInputError"/></span>
						            <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="formErrorSpan" />
						        </c:when>
						        <c:when test="${question.responseType eq 'YES_NO' }">
						            <span class="responseInputSpan">
							            <form:select path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssErrorClass="formInputError">
							                <q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
							                <form:option label="Yes" value="Y" />
							                <form:option label="No" value="N" />
							            </form:select>
							            <form:errors path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].response" cssClass="formErrorSpan" />
						            </span>
						        </c:when>
						        <c:otherwise>
						        	<h2 class="errorBox">ERROR: no handler for a '${question.responseType}' type question in a Question Set!</h2>
						        </c:otherwise>
				        	</c:choose>
				        	<form:hidden path="answers[${set.id}].answerSetList[${indx.index}][${question.id }].responseType" cssClass="responseType" />
				        	<c:if test="${elementStatus.first }" >
				        	    <span class="removeQuestionSetButtonSpan" style="${ indx.first ? 'display:none;' : ''}">
				        	        <button class="removeQuestionSet" >Remove</button>
				        	    </span>
				        	</c:if>
				        	
				        	<div style="display:none;" class="questionSetNameTemplate" >answers[${set.id}].answerSetList[XXX][${question.id }].response</div>
				        	<div style="display:none;" class="questionSetIdTemplate" >answers${set.id}.answerSetListXXX${question.id}.response</div>
			        	</div>
			       	</c:if>
			    </c:forEach>
			    <c:if test="${indx.last}" >
			        <button class="addAnotherQuestionSet" >Add another</button><div style="display:none;" class="questionSetIndex" >${indx.index}</div>
			    </c:if>
		    </div>
	    </c:forEach>
    </div>
</div>
      