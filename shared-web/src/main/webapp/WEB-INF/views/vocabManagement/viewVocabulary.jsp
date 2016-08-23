<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>View Vocabulary</title>
    <q:sharedCommonIncludes />
    <script type="text/javascript">
    $(function(){
        $(".removeValueButton").click(function(){
        	var tv = $(this).closest("tr").find("td.traitValue").first().html();
        	window.location.href='${pageContext.request.contextPath}/vocabManagement/removeVocabularyValue?vocabularyName=${vocabularyName}&vocabValue=' + tv;
    	});
    });
    </script>
    <style type="text/css">
	    #addVocabTable td input[type=text] {
	    	width: 100%;
	   	}
    </style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div style="clear:both;" class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="View Vocabulary" backUrl="${pageContext.request.contextPath}/vocabManagement/vocabularyManagement" />
		<div class="centeredContent">
			<h2>Vocabulary: <span class="focusText">${vocabularyName}</span></h2>
		</div>
		<c:if test="${not empty message}">
			<div class="infoBox msgBoxSpacing centeredContent">
				<p>${message}</p>
			</div>
		</c:if>
		<div>
			<table class="sharedTable">
				<thead>
					<tr>
						<th>Value</th>
						<th>Display</th>
						<th>Formatted</th>
						<th>Description</th>
						<th>Parent</th>
						<th colspan="2">Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${traitValueList}" var="tv">
				        <tr><td class="traitValue">${tv.traitValue}</td>
				            <td>${tv.displayString}</td>
				            <td>${tv.formattedDisplayString}</td>
				            <td>${tv.description}</td>
				            <td>${tv.parent}</td>
				            <td><button class="removeValueButton">Remove</button></td></tr>
				    </c:forEach>
				</tbody>
			</table>
		</div>
		<div class="addVocabularyValueDiv">
		    <form:form id="addVocabValueForm" commandName="newVocabValue" action="${pageContext.request.contextPath}/vocabManagement/addVocabValue/${vocabularyName}">
		        <table id="addVocabTable" class="sharedTable">
					<thead>
						<tr>
							<th>Value</th>
							<th>Display</th>
							<th>Description</th>
							<th>Parent</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
						<tr><td><form:input path="traitValue" /></td>
			            <td><form:input path="displayString" /></td>
			            <td><form:input path="description" /></td>
			            <td><form:input path="parent" /></td>
			            <td><button>Add</button></td></tr>
					</tbody>
		        </table>
		    </form:form>
		</div>
	</div>
</body>
</html>