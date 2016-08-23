<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
    <q:sharedHeadTitle>Submission Review</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.fileDownload.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/viewSiteFile.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
	$(function(){
	    var passAllClassName = 'passAll';
	    var rejectAllClassName = 'rejectAll';
	    
	    $("#finaliseButton").click(function(){
	    	if(confirm("You are about to submit the review. Click 'Cancel' to go back or 'OK' to continue.") ){
	    	    $("#reviewForm").submit();
	    	}
	    	return false;
	    });
	    
	    $("#cancelButton").click(function(){
	        var peerReview = ${peerReview};
	        if(peerReview == true){
	        	window.location="${pageContext.request.contextPath}/groupAdmin/peerReviewSubmissions?clearLock=true&submissionId=${submission.submissionId}";
	        	return;
	        }
	    	window.location="${pageContext.request.contextPath}/listSubmissionsForReview?clearLock=true&submissionId=${submission.submissionId}";
	    });
	    
	    $("#saveButton").click(function(){
	    	var saveFormAction = "${pageContext.request.contextPath}/saveIncompleteReview";
	    	$("#reviewForm").attr("action", saveFormAction);
	    	$("#reviewForm").submit();
	    });
	    
	    $(".clickToDownload").click(function(){
	    	var submissionId="${submission.submissionId}";
	    	var fileId = $(this).siblings("div.fileIdCell").first().html();
	    	$.fileDownload('${pageContext.request.contextPath}/downloadFile?submissionId=' + submissionId + '&dataFileId='+fileId);
	    	return false;
	    });
	    
	    $("#editIncompleteButton").click(function(){
	    	window.location="${pageContext.request.contextPath}/questionnaire/editIncompleteSubmission?submissionId=${submission.submissionId}";
	    });
	    
	    function thisElementOrAnyAncestorHasClass(selector, className) {
	    	return $(selector).hasClass(className) || $(selector).parents("."+className).length > 0;
	    }
	    
        $(window).click(function(d){
        	if (thisElementOrAnyAncestorHasClass(d.target, passAllClassName)) {
		    	$("td.reviewQ_PASS input").attr("checked","checked");
		    	return;
        	}
        	if (thisElementOrAnyAncestorHasClass(d.target, rejectAllClassName)) {
	        	$("td.reviewQ_REJECT input").attr("checked","checked");
	        	return;
        	}
        });
        
        //Remove radio validation error once a radio is selected.
        $("input.outcomeRadio.formInputError").click(function(){
        	$(this).parent().siblings("td.reviewQ_Comments").find(".radioErrorMessage").first().hide();
        });
        //Remove no reason error once keystroke event fired in comment box
        $("td.reviewQ_Comments textarea.formInputError,td.fileReviewComments textarea.formInputError").keyup(function(){
        	$(this).siblings("span.formErrorSpan").hide();
        });
        
        //When reject radio is selected, put the focus on the comments box
        $("td.reviewQ_REJECT input").click(function(){
        	$(this).parent("td").siblings("td.reviewQ_Comments").find("textarea").focus();
        });
        
        //File table operations
        $("input.fileRadio.formInputError").click(function(){
        	$(this).parent().siblings("td.fileReviewComments").find("span.formErrorSpan").first().hide();
        });
        
        $("input.fileRadioReject").click(function(){
        	$(this).parent("td").siblings("td.fileReviewComments").find("textarea").focus();
        });
        
        //The discard button is designed to save time on nonsense submissions
        //The submitter does not have a chance to edit the submission in an outright rejected ( discarded )submission.
        $(".discardButton").click(function(){
            if(confirm("If you discard this submission, the submitter will NOT be able to resubmit changes.") ){
            	$("#outrightRejectFlag").val("true");
            	$("#reviewForm").submit();
            }        	
        });
        
        initViewSiteFile('${pageContext.request.contextPath}');
	    $(".viewFeaturesButton").click(function(){
			var geometryJson = $(this).siblings("span.geoFeatureSetJsonStr").first().html();
			shared.geoFeatureSetPopupCallback = function() {
				var result = $.parseJSON(geometryJson);
				result.readOnly = true;
				return result;
			}
			var url = "${pageContext.request.contextPath}/viewFeatures";
			var popup = window.open(url, "Map", "location=0,toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no,width=1000,height=550");	
			popup.screenX = window.screenX + 200;
			popup.screenY = window.screenY + 100;
			popup.focus();
			return false;
	    });
	    
	  //pop up a box, fill it with content from an ajax service - review process history
	    $(".showQuestionReviewHistoryIcon").click(function(){
	    	//get the question id
	    	var qId = $(this).closest("tr.reviewQuestionRow").find("td.reviewIdColumn").first().html();
	    	var subId = $("#submissionId").val();
	    	var $dialog = $('<div></div>');
	    	var dialogOptions = {
	    	        title: qId + " Answer History",
	    	        modal: true,
	    	        width: 900,
	    	        open: function() {
	    	          $(this).load("${pageContext.request.contextPath}/ajax/getReviewAnswerHistory?submissionId=" +subId + "&questionId=" + qId );}
	    	    };
	    	$dialog.dialog(dialogOptions);
	    	$dialog.dialog("open");
	    });
	  
	    $("a.analyseVocabSuggestionButton").click(function(){
	    	//Work out which question id this was clicked for, then submit the form to
	    	//${pageContext.request.contextPath}/analyseVocabSuggestion/{questionId}
	    	var qId = $(this).closest("tr.reviewQuestionRow").find("td.reviewIdColumn").first().html();
	    	var url = "${pageContext.request.contextPath}/analyseVocabSuggestion/" + qId ;
	    	$("#reviewForm").attr("action", url);
	    	$("#reviewForm").submit();
	    });
	    
	    //Analyse a question set controlled vocab suggest
	    $("a.analyseVocabSuggestionButtonQSR").click(function(){
	    	
	    	//Need to do a few things here 
	    	// - work out the parent MQG question ID
	    	var qId = $(this).closest("tr.reviewQuestionRow").find("td.reviewIdColumn").first().html();
	    	// - the index of the group,
	    	var groupIndex = $(this).siblings(".analyseQSRgroupIndex").first().html();
	    	// - and the question id
	    	var groupQId = $(this).siblings(".analyseQSRquestionId").first().html();
	    	var url = "${pageContext.request.contextPath}/analyseVocabSuggestion/" + qId + "/" + groupIndex + "/" + groupQId;
	    	$("#reviewForm").attr("action", url);
	    	$("#reviewForm").submit();
	    });
	  
	});
	</script>
	<script type="text/javascript">
		$(function() {
			$('.followsSharedFixedHeader').css('margin-top', $('#sharedFixedHeader').height());
			var tableOffset = $("#realReviewTable").offset().top;
			var header = $("#realReviewTable > thead").clone();
			var fixedHeader = $("#stickyHeaderTable").append(header);
			var fixedHeaderHeight = $("#sharedFixedHeader").height();

			function copyHeaderWidths() {
				$("#realReviewTable th").width(function(i,e){
					$($("#stickyHeaderTable th").get(i)).width(e);
				});
			}
			copyHeaderWidths();
			
			$(window).bind("scroll", function() {
			    var offset = $(this).scrollTop() + fixedHeaderHeight;
			    if (offset > tableOffset && fixedHeader.is(":hidden")) {
			        fixedHeader.show();
			        return;
			    }
			    if (offset <= tableOffset) {
			        fixedHeader.hide();
			        return;
			    }
			});
		});
	</script>
	<style type="text/css">
		.reviewTable {
			font-size: 0.8em;
		}
		
		#stickyHeaderTable {
		    position: fixed; 
		    top: 140px;
		    display: none;
		    width: 960px;
		}
		
		.passAll, .rejectAll {
			font-size: 0.7em;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout displayConfirmation="true" />
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Review Submission" containerId="sharedFixedHeader">
			<h4 class="noMargin centeredContent">Submission Title:</h4>
			<p class="focusText noMargin centeredContent">${submission.submissionTitle}</p>
			<div class="centeredContent">
				<button id="cancelButton">Cancel</button>
				<button id="saveButton">Save</button>
				<c:if test="${not peerReview}">
				    <button class="discardButton">Discard</button>
				</c:if>
				<button id="finaliseButton">Submit Review</button>
			</div>
		</q:pageHeader>
		<div class="submissionMetaDetails followsSharedFixedHeader">
		    <div>
		        <span class="titleSpan">Submission Title:</span>
		        <span class="valueSpan">${submission.submissionTitle}</span>
		        <span class="titleSpan">Submission Id:</span>
		        <span class="valueSpan">${submission.submissionId}</span>
		    </div>
		    <div>
		        <span class="titleSpan">Submitted By:</span>
		        <span class="valueSpan">${submission.submittedByUsername}</span>
		        <span class="titleSpan">Config Id:</span>
		        <span class="valueSpan">${submission.questionnaireConfigId}</span>
		    </div>
		    <div>
		        <span class="titleSpan">Status:</span>
		        <span class="valueSpan">${submission.status}</span>
		        <span class="titleSpan">Config Filename:</span>
		        <span class="valueSpan">${submission.configFileName}</span>
		    </div>
		    <div>
		        <span class="titleSpan">Submission Date:</span>
		        <span class="valueSpan">
		        	<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${submission.submissionDate}" />
	        	</span>
		        <span class="titleSpan">Config version:</span>
		        <span class="valueSpan">${submission.questionnaireVersion}</span>
		    </div>
		</div>
		<h2 class="centeredContent">Review the Submission Metadata</h2>
		<c:if test="${validationFailed}">
			<div class="errorBox msgBoxSpacing centeredContent">
				There are errors on the page. Please fix them.
			</div>
		</c:if>
		<form:form id="reviewForm" commandName="review" action="submitReview" method="POST">
		    <form:errors element="div" cssClass="errorSummary" />
		    <form:hidden path="submissionId" />
		    <form:hidden path="submissionStatus" />
		    <form:hidden path="submissionTitle" />
		    <form:hidden id="outrightRejectFlag" path="outrightReject" />
		    <form:hidden path="peerReview" />
		    <div style="clear:right;">
		        <table id="realReviewTable" class="reviewTable">
		        	<thead>
			            <tr class="reviewHeaderRow">
			            	<th class="reviewIdColumn">ID</th>
			            	<th class="reviewHeaderQA">Question</th>
			            	<th class="reviewHeaderQA">Response</th>
			            	<th class="radioHeader">
			            		Pass
			            		<button type="button" class="passAll">Pass all</button>
		            		</th>
			            	<th class="radioHeader">
			            		Reject
			            		<button type="button" class="rejectAll">Reject all</button>
		            		</th>
			            	<th class="reviewHeaderQA">Comments</th>
		            	</tr>
	            	</thead>
	            	<tbody>
			            <c:forEach items="${submission.items }" var="item">
					        <c:choose>
					            <c:when test="${item.itemType eq 'QUESTION' or item.itemType eq 'QUESTION_SET'}">
					                <q:reviewTableQuestion model="${item}" reviewModel="${review}" answerHistoryMap="${answerHistoryMap}"/>
					            </c:when>
					            <c:when test="${item.itemType eq 'GROUP'}">
					                <q:reviewTableGroup model="${item}" reviewModel="${review}" answerHistoryMap="${answerHistoryMap}"/>
					            </c:when>
					        </c:choose>   
				    	</c:forEach>
			    	</tbody>
		        </table>
		        <table id="stickyHeaderTable" class="reviewTable"></table>
		    </div>
			
			<h2 class="centeredContent">Review the submitted data files</h2>
			<div class="uploadedFilesContainer centeredBlock">
			    <display:table name="submission.fileList" id="file">
			        <display:column title="Data File Name" property="fileName" class="filename" />
			        <display:column title="Size" property="fileSizeString" class="filesize" />
			        <display:column title="Description" property="fileDescription"/>
			        <display:column title="Type" property="fileType"/>
			        <display:column title="Format" property="format"/>
		            <display:column title="Version" property="formatVersion"/>
			        <display:column title="Download?" >
			        		<button class="clickToDownload">Download</button><div class="fileIdCell" style="display:none">${file.id}</div>
			        </display:column>
			        <display:column title="Pass">
			        	<form:radiobutton path="fileReviews[${file.id}].reviewOutcome" value="PASS" cssClass="fileRadio" cssErrorClass="fileRadio formInputError"/>
		        	</display:column>
			        <display:column title="Reject">
			        	<form:radiobutton path="fileReviews[${file.id}].reviewOutcome" value="REJECT" cssClass="fileRadio fileRadioReject" cssErrorClass="fileRadio fileRadioReject formInputError"/>
		        	</display:column>
			        <display:column title="Comments" class="fileReviewComments">
			            <form:errors path="fileReviews[${file.id}].reviewOutcome" cssClass="formErrorSpan" />
			            <form:textarea path="fileReviews[${file.id}].comments" cssErrorClass="formInputError"/>
			            <form:errors path="fileReviews[${file.id}].comments" cssClass="formErrorSpan" />
			        </display:column>
			    </display:table>
			</div>
			<div class="centeredContent">
				<h2>Notes</h2>
			    <form:textarea path="notes" cssClass="reviewNotes" cssErrorClass="formInputError reviewNotes reviewNotesError"/><br/>
			    <form:errors path="notes" cssClass="formErrorSpan" />
			</div>
		</form:form>
	</div>
</body>
</html>