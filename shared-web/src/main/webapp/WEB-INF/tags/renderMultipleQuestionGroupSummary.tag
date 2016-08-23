<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="questionSet" type="au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup" required="true" %>
<%@ attribute name="divclass" type="java.lang.String" required="false" %>
<%@ attribute name="pageAnswers" type="au.edu.aekos.shared.questionnaire.PageAnswersModel" required="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="${empty divclass ? 'questionDiv' : divclass}">
    <span class="questionIdSpan">${questionSet.id}</span>
    <span class="questionSpan" >${questionSet.text}</span>
    <div class="responseSummarySpan">
        <table class="summaryQuestionSetTable">
              <c:forEach items="${ pageAnswers.answers[questionSet.id].answerSetList}" var="answerSet" >
                  <tr><td class="setResponseCell">
                      <table class="summaryQuestionSetElement">
	                      <c:forEach items="${ questionSet.elements }" var="q" >
								<tr>
									<td>${q.text}</td>
								</tr>
								<tr>
									<td class="summaryQuestionAnswerCell">
										${not empty answerSet[q.id].displayResponse
								   			? answerSet[q.id].displayResponse 
								   			: answerSet[q.id].response }  
							   			${not empty answerSet[q.id].suggestedResponse
							   				? answerSet[q.id].suggestedResponse 
								   			: '' }
									</td>
								</tr>
							</c:forEach>
                      </table>
                  </td></tr>
              </c:forEach>
        </table>
    </div>
</div>