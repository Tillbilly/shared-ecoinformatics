<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Vocabulary Suggestion Analysis - Add Vocabulary Entry</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<script type="text/javascript">
		$(function(){
			$(".clearVocabRowButton").click(function(){
				$(this).closest("tr").find("input").each(function(){$(this).val(null);});
			});
	        $("#cancelButton").click(function(){
        		$("#entryForm").attr("action", "${pageContext.request.contextPath}/cancelAddVocabValues" );
	        	$("#entryForm").submit();
			});
		});
	</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Add New Vocabulary Entry" />
		<div class="centeredContent">
		    <h2>Add to vocabulary: <span class="focusText">${analysisModel.vocabName}</span></h2>
		</div>
	    <form:form id="entryForm" commandName="analysisModel" action="${pageContext.request.contextPath}/processAddVocabValues" method="POST">
		    <!-- A whole bunch of hidden fields on this form -->
		    <form:hidden path="submissionId" />
		    <form:hidden path="questionId" />
		    <form:hidden path="parentQuestionId" />
		    <form:hidden path="vocabName" />
		    <form:hidden path="responseType" />
		    <c:forEach items="${analysisModel.responseList}" var="resp" varStatus="indx" >
		        <form:hidden path="responseList[${indx.index}].originalValue" />
		        <form:hidden path="responseList[${indx.index}].chosenValue" />
		        <form:hidden path="responseList[${indx.index}].action" />
		    </c:forEach>
		    <c:if test="${isCustom}">
			    <table class="centeredBlock">
			        <tr><th>Value</th><th>Display</th><th>Description</th><th>Parent</th><th></th></tr>
				    <c:forEach items="${analysisModel.newVocabEntryMap }" var="entry">
				        <tr><td><form:input path="newVocabEntryMap[${entry.key}].traitValue" /><form:errors path="newVocabEntryMap[${entry.key}].traitValue" cssErrorClass="formInputError"/></td>
				            <td><form:input path="newVocabEntryMap[${entry.key}].displayString" /></td>
				            <td><form:input path="newVocabEntryMap[${entry.key}].description" /></td>
				            <td><form:input path="newVocabEntryMap[${entry.key}].parent" /></td>
				            <td><button type="button" class="clearVocabRowButton">Clear</button></td></tr>
				    </c:forEach>
			    </table>
		    </c:if>
		    <c:if test="${not isCustom}">
			    <table class="centeredBlock">
			        <tr><th>Value</th><th></th></tr>
				    <c:forEach items="${analysisModel.newVocabEntryMap }" var="entry">
						<tr>
							<td>
								<form:input path="newVocabEntryMap[${entry.key}].traitValue" />
								<form:errors path="newVocabEntryMap[${entry.key}].traitValue" cssErrorClass="formInputError" />
							</td>
							<td><button type="button" class="clearVocabRowButton">Clear</button></td>
						</tr>
					</c:forEach>
			    </table>
		    </c:if>
		    <div class="centeredContent">
				<button id="cancelButton" type="button">Cancel</button>
				<button type="submit">Submit</button>
		    </div>
		</form:form>
	</div>
</body>
</html>