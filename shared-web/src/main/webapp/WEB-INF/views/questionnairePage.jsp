<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>${quest.title}</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/fileuploader.css"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/skin/ui.dynatree-cust.css"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplauncher.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/maxlength/jquery.plugin.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/maxlength/jquery.maxlength.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.dynatree-1.2.4.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/fileupload-ui.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/siteFileUpload.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/multiselect_response.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/autocomplete.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/groupClearButton.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/helpIcon.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/image-ui.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/isSessionActive.js?version=${sharedVersion}"></script>
	<style>
		.ui-autocomplete-loading {
			background: white url('${pageContext.request.contextPath}/images/ui-anim_basic_16x16.gif') right center no-repeat;
		}
		
		#navButtons button {
			margin: 0;
		}
		
		#navButtonsTop {
			margin-top: 0.4em;
		}
		
		#navButtonsBottom {
			margin-top: 0.9em;
		}
	</style>
	<script id="submittedImageAnswerTemplate" type="text/x-jquery-tmpl">
        <div class="uploadedImage">
            <span class="filename">\${fileName}</span><span class="filesize">\${fileSize} bytes</span><span class="imageThumbSpan"><a target="_blank" href="getImage?imageId=\${imageId}"><img class="thumbnail" src="getImage?imageId=\${imageThumbId}"></img></a></span>                   
            <input type="hidden" name="answers[\${questionId}].response" value="\${fileName}" />
        </div> 
    </script>
	<script type="text/javascript">
		var shared = shared || {};
	
		function adjustWidth($el){
			var textInputMinWidth = 200;
	    	var textInputMaxWidth = 800;
	    	var characterWidth = 9;
			
			var content = $el.val();
			if( content == null || content == ''){
				$el.width(textInputMinWidth);
				return;
			}
			
			var contentWidth = content.length * characterWidth;
			if(contentWidth < textInputMinWidth ){
				contentWidth = textInputMinWidth;
			}else if(contentWidth > textInputMaxWidth){
				contentWidth = textInputMaxWidth;
			}
			$el.width(contentWidth);
		};
		
		function getDynatreeDivId(questionId){
			var divId = "dt" + questionId.replace(/\./g, "_");
			return divId;
		};
		
	    $(function(){
	    	
	    	$(".selectWithSuggestOption").each(function(){
	    		if( $(this).find(":selected").val() == 'OTHER' ){
	    			$(this).closest("div.questionDiv").find("span.responseSuggestInputSpan").show();
	    		}else{
	    			$(this).closest("div.questionDiv").find("span.responseSuggestInputSpan").hide();
	    		}
	    	});
	    	
	    	$(".selectWithSuggestOption").change(function(){
	    		if( $(this).find(":selected").val() == 'OTHER' ){
	    			$(this).closest("div.questionDiv").find("span.responseSuggestInputSpan").show();
	    		}else{
	    			$(this).closest("div.questionDiv").find("span.responseSuggestInputSpan").hide();
	    		}
	    	});
	    	
	    	$(".dateResponseInput").datepicker(
	    	     { dateFormat: "dd/mm/yy",
	    	       changeMonth : true,
	    	       changeYear  : true,
	    	       yearRange: "1700:2030"
	    	     }
	    	);
	    	$("div.imageInputDiv").show();
	    	
	    	function arePopUpsOpen() {
	    		try {
	    			var popUpsOpen = 0;
	    			for (i=0; i < shared.openPopUps.length; i++){
	    				var popUp = shared.openPopUps[i];
	    				if (!popUp.closed) {
		    				popUpsOpen++;
	    				}
	    			}
	    			if (popUpsOpen == 0) {
	    				return false;
	    			}
	    			alert('Cannot continue while pop ups are still open. Please close the ' + popUpsOpen
	    					+ ' pop up(s) that are still open before retrying this action.');
	    			for (i=0; i < shared.openPopUps.length; i++){
	    				var popUp = shared.openPopUps[i];
	    				if (!popUp.closed) {
		    				popUp.focus();
	    				}
	    			}
	    			return true;
	    		} catch (err) {
	    			//swallow
	    		}
	    		return false;
	    	}
	    	
	    	shared.hasPagePossiblyChanged = false;
	    	
	    	(function bindAllChangeListeners() {
		    	function markPageAsPossiblyChanged() {
		    		shared.hasPagePossiblyChanged = true;
		    	}
		    	
		    	function whereDidThisEventComeFrom(e) {
					if (e.srcElement.classList.contains('dynatree-checkbox')) {
						markPageAsPossiblyChanged();
					}
		    	}
		    	
		    	$(document).change(markPageAsPossiblyChanged);
		    	$(document).click(whereDidThisEventComeFrom);
	    	})()
	    	
	    	$(".answerFormSubmitButton").click(function(){
	    		$("div.imageInputDiv").hide();
	    		if (arePopUpsOpen()) {
	    			return;
	    		}
	    		$("#answerForm").submit();
	    	});
	    	
	    	$(".answerFormBackButton").click(function(){
	    		if (arePopUpsOpen()) {
	    			return;
	    		}
	    		if (shared.hasPagePossiblyChanged 
	    				&& !window.confirm("Going back will lose unsaved changes for this page. " +
	    						"Press OK to go back anyway or cancel to stay on this page so you can Save.")) {
	    			return;
	    		}
	    		var prevPage = $("#pageNumber").val() - 1;
	    		window.location = '${pageContext.request.contextPath}/questionnaire/' + prevPage;
	    	});
	    	
	    	$(".answerFormCancelButton").click(function(){
	    		if (arePopUpsOpen()) {
	    			return;
	    		}
	    		var retVal = confirm("Are you sure you want to Cancel? All unsaved answers on this page will be lost.");
	    		if( retVal == true ){
	    			window.location = "${pageContext.request.contextPath}";
	    		}
	    	});
	    	
	    	$(".answerFormSaveButton").click(function(){
	    		if (arePopUpsOpen()) {
	    			return;
	    		}
				$("#answerForm")
				.attr('action', '${pageContext.request.contextPath}/questionnaire/userSave/${page.pageNumber}')
	    		.submit();
	    	});
	    	
	    	$(".addAnotherQuestionSet").click(function(){
	    		//Need to clone the previous question div, update all of the paths
	    		var $newQS = $(this).closest(".questionSetEntryDiv").clone(true);
	    		var newIndex =parseInt( $(this).siblings("div.questionSetIndex").first().html() ) + 1;
	    		
	    		$newQS.find("div.questionSetIndex").first().html(newIndex.toString());
	    		
	    		$newQS.find("div.questionDiv").each(function(){
	    		    var divId = $(this).attr("id");
	    		    divId =  divId.substring(0, divId.indexOf("_") + 1 ) + newIndex.toString() ;
	    		    $(this).attr("id", divId);
	    		    //find the name template, replace the XXX with the index and assign it to the input name attribute
	    		    var name = $(this).find("div.questionSetNameTemplate").first().html();
	    		    name = name.replace("XXX", newIndex.toString());
	    		    var inputId = $(this).find("div.questionSetIdTemplate").first().html();
	    		    inputId = inputId.replace("XXX", newIndex.toString());
	    		    var $respTypeInput = $(this).find("input[type=hidden]").first();
	    		    var responseType = $respTypeInput.val();
	    		    if(responseType == 'CONTROLLED_VOCAB' || responseType == 'CONTROLLED_VOCAB_SUGGEST'){
	    		    	var $select = $(this).find("select").first();
		    		    $select.attr("name",name);
		    		    $select.attr("id",inputId);
		    		    $select.val(null);
		    		    if(responseType == 'CONTROLLED_VOCAB_SUGGEST'){
		    		    	var $suggestSpan = $(this).find("span.responseSuggestInputSpan").first();
		    		    	$suggestSpan.hide();
		    		    	var suggestId = inputId.replace("response","suggestedResponse");
		    		    	var suggestName = name.replace("response","suggestedResponse");
		    		    	var $suggestInput = $suggestSpan.find("input").first();
		    		    	$suggestInput.attr("name", suggestName);
		    		    	$suggestInput.attr("id", suggestId);
		    		    	$suggestInput.val(null);
		    		    }
	    		    }else{
		    		    var $input = $(this).find("input,textarea").first();
		    		    $input.attr("name",name);
		    		    $input.attr("id",inputId);
		    		    $input.val(null);
	    		    }
	    		    
	    		    var responseTypeName = name + "Type";
	    		    var responseTypeId = inputId + "Type";
	    		    $respTypeInput.attr("name",responseTypeName);
	    		    $respTypeInput.attr("id",responseTypeId);
	    		    
	    		});
	    		
	    		$newQS.find(".removeQuestionSetButtonSpan").first().show();
	    		$newQS.find(".helpIcon").hide();
	    		$(this).closest("div.questionSets").append($newQS);
	    		
	    		$(this).hide();
	    		return false;
	    	});
	    	
	    	$(".removeQuestionSet").click(function(){
	    		var $questionSetDiv = $(this).closest(".questionSetEntryDiv");
	    		$questionSetDiv.find("input,select").each(function(){
	    			$(this).val(null);
	    		});
	    		$questionSetDiv.hide();
	    		//Find the last visible question set div and make the 'Add another' button visible.
	    		$(this).closest("div.questionSets").find("div.questionSetEntryDiv:visible").last().find(".addAnotherQuestionSet").first().show();
	    		return false;
	    	});
	    	
	    	$( "#submittedImageAnswerTemplate" ).template( "imageAnswerTemplate" );
	    	
	    	$('form.uploadImageForm').ajaxForm({
            	dataType : 'json',
            	resetForm : true,
                beforeSubmit: function() {
                	return false;
                },
                success:function(data){
                	$.tmpl("imageTemplate", {fileName:data.imageName,
                		imageThumbId:data.thumbnailId,
                		imageId:data.imageId,
	                    fileSize:data.size,
	                    questionId:data.questionId} ).appendTo("#uploadedImages");
                 }
            });
	    	
	    	$(".reusableGroupSelect").change(function(){
	    		//$("#changedReusableGroupId").val( $(this).closest(".questionGroup").attr("id").substr(1) );
	    		//var changedReusableGroupId = $(this).closest(".questionGroup").attr("id").substr(1);
	    		var test = $(this).closest(".questionGroup").attr("id").substr(1);
	    		var test2 = $(this).closest(".questionGroup").attr("id");
	    		var formAction = $("#answerForm").attr("action") + "/" + $(this).closest(".questionGroup").attr("id").substr(1);
	    		$("#answerForm").attr("action", formAction);
	    		$("#answerForm").submit();
	    	});
	    	
	    	initSiteFileButtons("${pageContext.request.contextPath}");
	    	initGeoFeatureSetMapLaunch("${pageContext.request.contextPath}");
	    	
	    	//input width expansion - work out an average pixel width per character, and style the input widths based on the text.
	    	//ensure a minimum / maximum width   -  These sizes are in pixels
	    	$("#answerForm input:text").each(function(){
	    		adjustWidth($(this));
	    		$(this).keyup(function(){
	    			adjustWidth($(this));
	    		});
	    	});
	    	//maxlength for text areas
	    	$(".textAreaInput").maxlength({max: 10000, showFeedback:false});
	    	
	    	
	    });
	</script>
	<c:if test="${ page.hasPageTriggerDisplayConditions }">
	    <c:forEach items="${page.pageTriggerConditionJquery}" var="jqueryStr" >
	    	<script type="text/javascript">
		    $(function(){
		    	${jqueryStr}
			});
		    </script>
	    </c:forEach>
	</c:if>
	<c:if test="${ page.hasPageForwardPopulate }">
	    <c:forEach items="${page.pageForwardPopulateJquery}" var="jqueryStr" >
	    	<script type="text/javascript">
		    $(function(){
		    	${jqueryStr}
			});
		    </script>
	    </c:forEach>
	</c:if>
	<!-- Populate any Dynatree tree selects -->
	<c:if test="${not empty dynatreeDataMap }" >
	   <script type="text/javascript">
	       $(function(){
	    	   $(".dynatreeCollapseAll").click(function(){
	    		var $dynatreeDiv = $(this).closest(".questionDiv").find(".dynatreeDiv").first();
	    		$dynatreeDiv.dynatree("getRoot").visit(function(node){
	   				node.expand(false);
	   			});
	   			return false;
	   		});
	   		$(".dynatreeExpandAll").click(function(){
	   			var $dynatreeDiv = $(this).closest(".questionDiv").find(".dynatreeDiv").first();
	   			$dynatreeDiv.dynatree("getRoot").visit(function(node){
	   				node.expand(true);
	   			});
	   			return false;
	   		});
	       });
	   </script>
	    <c:forEach var="entry" items="${dynatreeDataMap}">
		   <script type="text/javascript">
			    $(function(){
			    	var divId = getDynatreeDivId( '${entry.key}' )
			    	var dynatreeDivSelector = "#" + divId;
			    	$(dynatreeDivSelector).dynatree({
			    		checkbox : true,
			    		selectMode : 2,
			    		children : ${entry.value} ,
			    		cookieId: divId,
			    		idPrefix : divId + "-",
			    		onSelect: function(select, node) {
							// Display list of selected nodes
							var qId = '${entry.key}';
							var selNodes = node.tree.getSelectedNodes();
							var $responseSpan = $(dynatreeDivSelector).next(".responseInputSpan").first();
							$responseSpan.empty();
							for(var x = 0; x < selNodes.length ; x++ ){
								var title = selNodes[x].data.title;
								var key = selNodes[x].data.key;
								var name = "answers["+ qId +"].multiselectAnswerList["+ x.toString() +"].response";
								var newInput = "<input type='hidden' name='"+name + "' value='" + key +"'>";
								$responseSpan.append(newInput);
							}
						}
			    	});
			    });
			</script>
		</c:forEach>    
	</c:if>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout displayConfirmation="true" />
	<div class="main-content">
	    <q:pageHeader includeHomeButton="false" titleText="Data Submission to &AElig;KOS" containerId="sharedFixedHeader">
			<jsp:attribute name="col3Content">
				<div id="navButtons">
					<div id="navButtonsTop">
						<button class="answerFormCancelButton">Cancel</button>
						<button class="answerFormSaveButton">Save</button>
					</div>
					<div id="navButtonsBottom">
						<c:if test="${answers.pageNumber ne 1}">
						    <button class="answerFormBackButton">Back</button>
						</c:if>
						<button class="answerFormSubmitButton">Next</button>
					</div>
				</div>
			</jsp:attribute>
			<jsp:body>
				<q:questionnaireProgressIndicator progressIndicatorInfo="${progressIndicatorInfo}" />
			</jsp:body>
	    </q:pageHeader>
		<div class="pageQuestionsDiv followsSharedFixedHeader">
			<c:if test="${not empty page.pageTitle}">
				<h3 class="centeredContent">${page.pageTitle}</h3>
			</c:if>
		    <form:errors cssClass="errorBox msgBoxSpacing centeredContent"></form:errors>
			<q:reviewRejectionInfo lastReview="${quest.lastReview}" />
		    <form:form id="answerForm" commandName="answers" action="${pageContext.request.contextPath}/questionnaire/${quest.currentPageNumber}" cssClass="answerForm">
		        <form:errors cssClass="errorSummary msgBoxSpacing errorBox centeredContent" element="div"/>
		        <form:hidden path="pageNumber" />
				<c:forEach var="element" items="${page.elements}" varStatus="status">
				    <c:set var="divclass" value="${ status.count % 2 == 0 ? 'even' : 'odd' }" />
				    <c:choose>
				        <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
				            <q:renderQuestionGroup group="${element}" 
				                                   controlledVocab="${controlledVocab }" 
				                                   groupDisplayConditionMap="${page.groupDisplayConditions }" 
				                                   questionDisplayConditionMap="${page.questionDisplayConditions}"
				                                   imageAnswerMap="${quest.imageAnswerMap}"
				                                   imageSeriesAnswerMap="${quest.imageSeriesAnswerMap}"
				                                   reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
				                                   titleQuestionId="${ empty quest.titleQuestionId ? null : quest.titleQuestionId }"
				                                   reusableGroupOptionMap="${quest.reusableGroupIdOptionListMap }"
				                                   displayQuest="${quest}"
				                                   divclass="${divclass}"
				                                   indentDepth="1"
				                                   answers="${answers}"
				                                   dynatreeDivMap="${empty dynatreeDivMap ? null : dynatreeDivMap }"/>
				        </c:when>
				        <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
				            <q:renderQuestion question="${element }" controlledVocab="${controlledVocab }" 
				                              questionDisplayConditionMap="${page.questionDisplayConditions }" 
				                              imageAnswerMap="${quest.imageAnswerMap}"
				                              imageSeriesAnswerMap="${quest.imageSeriesAnswerMap}"
				                              reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
				                              titleQuestionId="${ empty quest.titleQuestionId ? null : quest.titleQuestionId }"
				                              displayQuest="${quest}"
				                              divclass="${divclass}"
				                              indentDepth="1"
				                              dynatreeDivMap="${empty dynatreeDivMap ? null : dynatreeDivMap }"/>
				        </c:when>
				        <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.MultipleQuestionGroup' }">
				            <q:renderQuestionSet set="${element}"
				                                 controlledVocab="${controlledVocab }"
				                                 reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
				                                 displayQuest="${quest}"
				                                 divclass="${divclass}"
				                                 indentDepth="1"
				                                 answers="${answers}"/>
				        </c:when>
				    </c:choose>
				</c:forEach>
			</form:form>
		</div>
		<div class="centeredContent">
			<img src="${pageContext.request.contextPath}/images/branding-band.png" />
		</div>
	</div>
</body>
</html>