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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.tmpl.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplauncher.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/multiselect_response.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/image-ui.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/siteFileUpload.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/helpIcon.js?version=${sharedVersion}"></script>
	<script id="submittedImageAnswerTemplate" type="text/x-jquery-tmpl">
        <div class="uploadedImage">
            <span class="filename">\${fileName}</span><span class="filesize">\${fileSize} bytes</span><span class="imageThumbSpan"><a target="_blank" href="getImage?imageId=\${imageId}"><img class="thumbnail" src="getImage?imageId=\${imageThumbId}"></img></a></span>                   
            <input type="hidden" name="answers[\${questionId}].response" value="\${fileName}" />
        </div> 
    </script>
	<script type="text/javascript">
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
	    	       yearRange: "1950:2020"
	    	     }
	    	);
	    	
	    	$("div.imageInputDiv").show();

	    	$( "#submittedImageAnswerTemplate" ).template( "imageAnswerTemplate" );
	    	
	    	//Image support not enabled . . .  for now . . .
	    	$('form.uploadImageForm').ajaxForm({
            	dataType : 'json',
            	resetForm : true,
                beforeSubmit: function() {
                	return false;
                },
                success:function(data){
                	//status.html(data.origFilename + "  " + data.size + " bytes");
                	$.tmpl("imageTemplate", {fileName:data.imageName,
                		imageThumbId:data.thumbnailId,
                		imageId:data.imageId,
	                    fileSize:data.size,
	                    questionId:data.questionId} ).appendTo("#uploadedImages");
                	
                 }
            });
	    });
	</script>
	<c:forEach var="page" items="${quest.pages}">
		<c:if test="${ page.hasPageTriggerDisplayConditions }">
		    <c:forEach items="${page.pageTriggerConditionJquery}" var="jqueryStr" >
		    	<script type="text/javascript">
			    $(function(){
			    	${jqueryStr}
				});
			    </script>
		    </c:forEach>
		</c:if>
	</c:forEach>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<div class="centeredContent">
		    <q:sharedSmallLogo />
			<h1>Test Questionnaire Config: <span class="focusText">${configFileName}</span></h1>
			<p>
				<a href="${pageContext.request.contextPath}/dev/uploadQuestionnaire"><button>Back</button></a>	
			    <a href="${pageContext.request.contextPath}"><button>Home</button></a>
		    </p>
		</div>
	    <form:errors ></form:errors>
	    <form:form id="answerForm" commandName="answers" action="${quest.currentPageNumber}">
			<c:forEach var="page" items="${quest.pages}">
				<c:forEach var="element" items="${page.elements}">
				    <c:choose>
				        <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.QuestionGroup'}">
				            <q:renderQuestionGroup group="${element}" 
				                                   controlledVocab="${controlledVocab }" 
				                                   groupDisplayConditionMap="${page.groupDisplayConditions }" 
				                                   questionDisplayConditionMap="${page.questionDisplayConditions}"
				                                   imageAnswerMap="${quest.imageAnswerMap}"
				                                   reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
				                                   reusableGroupOptionMap="${null }"
				                                   titleQuestionId="${ empty quest.titleQuestionId ? null : quest.titleQuestionId }"
				                                   displayQuest="${quest}"
				                                   answers="${null}"
				                                   imageSeriesAnswerMap="${null}"
				                                   indentDepth="${null}"
				                               />
				        </c:when>
				        <c:when test="${element['class'].name eq 'au.edu.aekos.shared.questionnaire.jaxb.Question' }">
				            <q:renderQuestion question="${element }" controlledVocab="${controlledVocab }" 
				                              questionDisplayConditionMap="${page.questionDisplayConditions }" 
				                              imageAnswerMap="${quest.imageAnswerMap}"
				                              reviewModel="${ empty quest.lastReview ? null : quest.lastReview }"
				                              titleQuestionId="${ empty quest.titleQuestionId ? null : quest.titleQuestionId }"
				                              displayQuest="${quest}"
				                              imageSeriesAnswerMap="${null}"
				                              indentDepth="${null}"/>
				        </c:when>
				    </c:choose>
				</c:forEach>
			    <p class="centeredContent">
			    	----------------- *******  PAGE BREAK  ******* -----------------
			    </p>
			</c:forEach>
		</form:form>
		<div class="centeredContent">
			<p>
				<a href="${pageContext.request.contextPath}/dev/uploadQuestionnaire"><button>Back</button></a>	
			    <a href="${pageContext.request.contextPath}"><button>Home</button></a>
		    </p>
			<img src="${pageContext.request.contextPath}/images/branding-band.png" />
		</div>
	</div>
</body>
</html>