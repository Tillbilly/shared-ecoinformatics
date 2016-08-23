<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<span class="reviewAnswerHistoryQuestionText">${answerHistoryModel.questionText}</span><br/>
<c:if test="${not empty answerHistoryModel.reviewHistoryRows }">
    <table class="reviewAnswerHistoryTable">
        <tr><th>Event</th><th>Response</th><th>Outcome</th><th>Comments</th></tr>
	    <c:forEach items="${answerHistoryModel.reviewHistoryRows}" var="row">
	        <tr>
	          <td class="reviewAnswerContextTd">${row.contextLine1}<br/>${row.contextLine2}</td>
	          <td class="reviewAnswerTextTd">${row.answerText}</td>
	          <td class="reviewAnswerStatusTd">${row.reviewStatus}</td>
	          <td class="reviewAnswerCommentsTd">${row.comments}</td>
	        </tr>
	    </c:forEach>
    </table>
</c:if>

	