<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ attribute name="group" type="au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup" required="true" %>
<%@ attribute name="pageAnswers" type="au.edu.aekos.shared.questionnaire.PageAnswersModel" required="true" %>
<%@ attribute name="imageAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="geoFeatureSetMap" type="java.util.Map" required="true" %>

<div class="questionGroup">
    <c:if test="${not empty group.groupTitle}"><h4>${ group.id}&nbsp;${ group.groupTitle }</h4></c:if>	
	<c:forEach var="element" items="${group.elements}">
	   <c:choose>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
	           <q:renderQuestionGroupSummary group="${element }" pageAnswers="${pageAnswers}" imageAnswerMap="${imageAnswerMap}" geoFeatureSetMap="${geoFeatureSetMap }"/>
	       </c:when>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
	           <q:renderQuestionSummary question="${element }" pageAnswers="${pageAnswers}" imageAnswerMap="${imageAnswerMap}" geoFeatureSetMap="${geoFeatureSetMap }"/>
	       </c:when>
	       <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup' }">
                <q:renderMultipleQuestionGroupSummary questionSet="${element }" pageAnswers="${pageAnswers}" />
            </c:when>
	   </c:choose>
	</c:forEach>
</div>