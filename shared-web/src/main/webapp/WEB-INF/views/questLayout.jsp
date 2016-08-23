<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<title>${quest.title}</title>
	<q:sharedCommonIncludes />
	<script type="text/javascript">
	</script>
</head>
<body>

<h1>${quest.title}</h1>
<h2>${quest.subtitle}</h2>
<h2>${quest.questionnaireVersion}</h2>
<p>${quest.introduction}</p>
<c:forEach var="page" items="${quest.pages}">
    <hr/>
    <c:if test="${not empty page.pageTitle }"><h2>${empty page.pageTitle ? '':page.pageTitle}  page# ${page.pageNumber }</h2></c:if>
    <c:forEach var="element" items="${page.elements}">
        <c:choose>
            <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
                <%-- <q:renderQuestionGroup group="${element }" /> --%>
            </c:when>
            <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
                <%-- <q:renderQuestion question="${element }" /> --%>
            </c:when>
        </c:choose>
    </c:forEach>

</c:forEach>





</body>
</html>