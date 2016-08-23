<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>Vocab Suggestion Analysis</title>
	<q:sharedCommonIncludes />
	<script type="text/javascript">
	    <!-- ADD, REJECT, CHOOSE, SUGGEST -->
	    $(function(){
	    	$(".addToVocabButton").click(function(){
	    		var $divResponseRow = $(this).closest("div.responseRow");
	    		$divResponseRow.find(".responseActionSpan input").first().val("ADD");
	    		$divResponseRow.find(".chosenValueSpan input").first().val("");
	    	});
            $(".rejectButton").click(function(){
            	var $divResponseRow = $(this).closest("div.responseRow");
            	$divResponseRow.find(".responseActionSpan input").first().val("REJECT");
            	$divResponseRow.find(".chosenValueSpan input").first().val("");
	    	});
            $(".clearRowButton").click(function(){
            	var $divResponseRow = $(this).closest("div.responseRow");
            	$divResponseRow.find(".responseActionSpan input").first().val("");
            	$divResponseRow.find(".chosenValueSpan input").first().val("");
	    	});
            
            $(".chooseVocabValueButton").click(function(){
            	var $selInput = $("input.suggestionSelectInput:checked");
            	if( $selInput.length == 0 ){
            		alert("Please choose a response to operate on!");
            		return;
            	}
            	//Determine picked value
            	var vocabVal = $(this).closest("tr").find("td.vocabValue").first().html();
            	var $divResponseRow = $selInput.closest("div.responseRow");
            	$divResponseRow.find(".responseActionSpan input").first().val("CHOOSE");
            	$divResponseRow.find(".chosenValueSpan input").first().val(vocabVal);
            });
            $(".suggestVocabValueButton").click(function(){
            	var $selInput = $("input.suggestionSelectInput:checked");
            	if( $selInput.length == 0 ){
            		alert("Please choose a response to operate on!");
            		return;
            	}
            	//Determine picked value
            	var vocabVal = $(this).closest("tr").find("td.vocabValue").first().html();
            	var $divResponseRow = $selInput.closest("div.responseRow");
            	$divResponseRow.find(".responseActionSpan input").first().val("SUGGEST");
            	$divResponseRow.find(".chosenValueSpan input").first().val(vocabVal);
            });
            $(".useParentButton").click(function(){
            	var $divResponseRow = $(this).closest("div.responseRow");
            	$divResponseRow.find(".responseActionSpan input").first().val("PARENT");
            	$divResponseRow.find(".chosenValueSpan input").first().val("");
            	
            });
            
            $("#cancelButton").click(function(){
            	window.location.href='${pageContext.request.contextPath}/modifyReview?submissionId=${submission.submissionId}';
            });
            $("#processButton").click(function(){
            	$("#controlForm").submit();
            });
	    });
	</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Vocab Suggestion Analysis" />
		<div class="centeredContent">
			<h2>Question ${question.questionId} for vocabulary <span class="focusText">${vocabName}</span></h2>
		</div>
		<div>
			<div class="vsa-left">
				<table class="sharedTable">
					<tr>
						<th>Q#</th>
						<th>Text</th>
						<th>Type</th>
						<th>MetaTag</th>
					</tr>
					<tr>
						<td>${question.questionId}</td>
						<td class="vsa-questionText">${question.questionText}</td>
						<td>${question.responseType}</td>
						<td>${question.metatag}</td>
					</tr>
					<c:if test="${not empty parentQuestion }">
						<tr>
							<th>Parent Q#</th>
							<th>Text</th>
							<th>Type</th>
							<th>MetaTag</th>
						</tr>
						<tr>
							<td>${parentQuestion.questionId}</td>
							<td class="vsa-questionText">${parentQuestion.questionText}</td>
							<td>${parentQuestion.responseType}</td>
							<td>${parentQuestion.metatag}</td>
						</tr>
						<c:choose>
							<c:when test="${parentQuestion.isMultiSelect and parentQuestion.hasResponse}">
								<c:forEach items="${parentQuestion.multiselectAnswerList}" var="ans" varStatus="indx">
									<tr>
										<th><c:if test="${indx.first}">Response</c:if></th>
										<td class="vsa-pqresponse" colspan="3">${ans.responseText}</td>
									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr>
									<th>Response</th>
									<td class="vsa-pqresponse" colspan="3">${parentQuestion.responseText}</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test="${not empty containingQuestion }">
					    <tr>
							<th>Containing Group #</th>
							<th>Text</th>
							<th>Group Index</th>
							<th></th>
						</tr>
					    <tr>
					        <td>${containingQuestion.questionId }</td>
					        <td>${containingQuestion.questionText}</td>
					        <td>${groupIndex}</td>
					        <td></td>
					    </tr>
					</c:if>
					
					<!-- 
					 model.addAttribute("containingQuestion", containingQuestionSetModel);
		             model.addAttribute("groupIndex", groupIndex);
					-->
					
				</table>
			</div>
			<div class="centeredContent">
				<h2>Response(s)</h2>
				<form id="controlForm" action="${pageContext.request.contextPath}/processVocabAnalysis" method="post">
				    <input type="hidden" name="submissionId" value="${submission.submissionId}" >
				    <input type="hidden" name="questionId" value="${question.questionId}" >
				    <input type="hidden" name="parentQuestionId" value="${not empty parentQuestion.questionId ? parentQuestion.questionId : ''}" >
				    <input type="hidden" name="vocabName" value="${vocabName}" >
				    <input type="hidden" name="responseType" value="${question.responseType}" >
				    <input type="hidden" name="groupQuestionId" value="${not empty containingQuestion.questionId ? containingQuestion.questionId : ''}" >
				    <input type="hidden" name="groupIndex" value="${not empty groupIndex ? groupIndex : ''}" >
					<c:choose>
						<c:when test="${question.responseType eq 'CONTROLLED_VOCAB_SUGGEST'}" > <!-- this is the 'Organisation' case -->
							<div class="responseRow">
								<span class="suggestionSelectSpan">
									<input class="suggestionSelectInput" name="rowSelector" type="radio" >
								</span>
								<span class="suggestedResponseSpan">
									${question.suggestionText}
								</span>
								<input type="hidden" name="responseList[0].originalValue" value="${question.suggestionText}" >
								<span class="chosenValueSpan">
									<input name="responseList[0].chosenValue" readonly="readonly">
								</span>
								<span class="responseActionSpan">
									<input name="responseList[0].action" readonly="readonly">
								</span>
								<span class="suggestionControlsSpan">
									<button type="button" class="addToVocabButton">Add to Vocab</button>
									<button type="button" class="rejectButton">Reject</button> 
									<button type="button" class="clearRowButton">Clear</button>    
								</span>
							</div>
						</c:when>
						<c:when test="${question.responseType  eq 'MULTISELECT_TEXT'}" > <!-- this is the '*Suggest' case -->
							<c:forEach items="${question.multiselectAnswerList}" var="msa" varStatus="indx" >  
								<div class="responseRow">
									<span class="suggestionSelectSpan">
										<input class="suggestionSelectInput" name="rowSelector" type="radio" />
									</span>
									<span class="suggestedResponseSpan">
										${msa.responseText}
									</span>
									<input type="hidden" name="responseList[${indx.index }].originalValue" value="${msa.responseText}" />
									<span class="chosenValueSpan">
										<input name="responseList[${indx.index }].chosenValue" readonly="readonly" />
									</span>
									<span class="responseActionSpan">
										<input name="responseList[${indx.index }].action" readonly="readonly" />
									</span>
									<span class="suggestionControlsSpan">
										<button type="button" class="addToVocabButton">Add to Vocab</button>
										<button type="button" class="rejectButton">Reject</button>
										<button type="button" class="useParentButton">Use Parent</button>  
										<button type="button" class="clearRowButton">Clear</button>    
									</span>
								</div>  
							</c:forEach>
						</c:when>
						<c:when test="${question.responseType eq 'TEXT'}">
							<div class="responseRow">
								<span class="suggestionSelectSpan">
									<input class="suggestionSelectInput" name="rowSelector" type="radio" />
								</span>
								<span class="suggestedResponseSpan">
									${question.suggestionText}
								</span>
								<input type="hidden" name="responseList[0].originalValue" value="${question.suggestionText}" />
								<span class="chosenValueSpan">
									<input name="responseList[0].chosenValue" readonly="readonly" />
								</span>
								<span class="responseActionSpan">
									<input name="responseList[0].action" readonly="readonly" />
								</span>
								<span class="suggestionControlsSpan">
									<button type="button" class="addToVocabButton">Add to Vocab</button>
									<button type="button" class="rejectButton">Reject</button> 
									<button type="button" class="clearRowButton">Clear</button>    
								</span>
							</div>
						</c:when>
					</c:choose>
				</form>
			</div>
			<div class="centeredContent">
				<img src="${pageContext.request.contextPath}/images/ArrowUpDown.svg" id="upDownArrowIcon" />
				<p>Select a response from above, then select the entry and type you would like to assign it to below</p>
			</div>
			<div>
				<table class="sharedTable">
					<tr>
						<th colspan="3">Vocabulary table for: '${vocabName}'</th>
					</tr>
					<tr>
						<th>Vocab Value</th>
						<th>Display</th>
						<th>Controls</th>
					</tr>
					<c:forEach items="${vocabList}" var="tv">
						<tr>
							<td class="vocabValue">${tv.traitValue}</td>
							<td>${tv.displayString}</td>
							<td>
								<button type="button" class="chooseVocabValueButton">Choose</button>
								<button type="button" class="suggestVocabValueButton">Suggest</button>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<div class="centeredContent">
			<button type="button" id="cancelButton">Cancel</button>
			<button type="button" id="processButton">Process Changes</button>
		</div>
	</div>
</body>
</html>	
