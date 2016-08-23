<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>View Submission</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.fileDownload.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/viewSiteFile.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
	var shared = shared || {};
	
	$(function(){
	    $("div.twisty").click(function(){
	    	var twistyOpen = $(this).hasClass("twistyOpen");
	    	if(twistyOpen){
	    		$(this).siblings("div.groupContent").first().hide();
	    	}else{
	    		$(this).siblings("div.groupContent").first().show();
	    	}
	    	$(this).toggleClass("twistyOpen twistyClosed");
	    	$(this).find(".ui-icon").toggleClass("ui-icon-triangle-1-s ui-icon-triangle-1-e");
	    });
	    
    	function toggleShowAllHelper(selectorFilter) {
	    	$('div.groupContent' + selectorFilter).closest(".viewSubmissionGroup").find('div.twisty').click();
    	}

    	$('#showAllLink').click(function() {
    		toggleShowAllHelper(':hidden');
	    });
    	
    	$('#hideAllLink').click(function() {
    		toggleShowAllHelper(':visible');
	    });
	    
	    $(".clickToDownload").click(function(){
	    	var submissionId="${submission.submissionId}";
	    	var fileId = $(this).siblings("div.fileIdCell").first().html();
	    	//alert('/shared-web/downloadFile?submissionId=' + submissionId + '&dataFileId='+fileId );
	    	$.fileDownload('${pageContext.request.contextPath}/downloadFile?submissionId=' + submissionId + '&dataFileId='+fileId);
	    	return false;
	    });
	    
	    $("#editIncompleteButton").click(function(){
	    	window.location="${pageContext.request.contextPath}/questionnaire/editIncompleteSubmission?submissionId=${submission.submissionId}";
	    });
	    $("#modifySubmissionButton").click(function(){
	    	window.location="${pageContext.request.contextPath}/questionnaire/modifySubmission?submissionId=${submission.submissionId}";
	    });
	    $("#manageLinksButton").click(function(){
	    	window.location="${pageContext.request.contextPath}/linkSubmission?submissionId=${submission.submissionId}";
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
	    });
    	
    	$('.followsSharedFixedHeader').css('margin-top', $('#sharedFixedHeader').height());
    	
    	$('#printButton').click(function() {
    		var top = 0;
    		var left = 0;
    		window.scrollTo(left, top);
    		window.print();
    	});
    	
    	$(".launchEmbargoButton").click(function(){
    		window.location="${pageContext.request.contextPath}/admin/editEmbargo?submissionId=${submission.submissionId}";
    	});
    	
	});
	</script>
	<style type="text/css">
		.followsSharedFixedHeader {
			margin-top: 172px;
		}
		
		.focusText {
			margin-bottom: 0.5em;
		}
		
		#dataFilesTitle {
			clear:left;
			padding-top:20px;
		}
		
		#pageHeading {
			margin: 0.4em 0;
		}
		
		.errorIcon {
			width: 26px;
			height: 26px;
		}
		
		.linkedSubmissionsContainer {
			margin-top: 1em;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div id="viewSubmission" class="main-content">
	
	    <c:choose>
	        <c:when test="${empty groupAdminView }">
				<c:set var="titleText" value="View Submission" />
				<c:set var="backUrl" value="${pageContext.request.contextPath}/manageSubmissions" />
			</c:when>
			<c:otherwise>
			    <c:set var="titleText" value="View Submission (Group Admin View)" />
			    <c:set var="backUrl" value="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions" />
			</c:otherwise>
		</c:choose>
			
	    <q:pageHeader includeHomeButton="true" titleText="${titleText}" backUrl="${backUrl}" containerId="sharedFixedHeader" >
			
			<div class="submissionMetaDetails">
			    <div>
			        <span class="titleSpan truncatable">Submission Title:</span>
			        <span class="valueSpan truncatable">${submission.submissionTitle}</span>
			        <span class="titleSpan truncatable">Submission Id:</span>
			        <span class="valueSpan truncatable">${submission.submissionId}</span>
			    </div>
			    <div>
			        <span class="titleSpan truncatable">Submitted By:</span>
			        <span class="valueSpan truncatable">${submission.submittedByUsername}</span>
			        <span class="titleSpan truncatable">Config Id:</span>
			        <span class="valueSpan truncatable">${submission.questionnaireConfigId }</span>
			    </div>
			    <div>
			        <span class="titleSpan truncatable">Status:</span>
		        	<span class="valueSpan truncatable">${submission.status}
		        	    <c:choose>
		        	        <c:when test="${lockedForReview}">
					            <img title="Submission locked by reviewer" src="${pageContext.request.contextPath}/images/lock.png" />
		            		</c:when>
		            		<c:otherwise>
		            			<c:if test="${submission.statusObj.editable and submission.isOwnedBy(username)}">
					            	<button id="editIncompleteButton">Edit</button>
				            	</c:if>
					            <c:if test="${submission.statusObj.modifiable and submission.isOwnedBy(username)}">
					            	<button id="modifySubmissionButton">Modify</button>
				            	</c:if>
		            		</c:otherwise>
		            	</c:choose>
		        	</span>
		        	<span class="titleSpan truncatable">Config Filename:</span>
		        	<span class="valueSpan truncatable">${submission.configFileName}</span>
			    </div>
			    <div>
			        <span class="titleSpan truncatable">Submission Date:</span>
			        <span class="valueSpan truncatable">
			        	<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${submission.submissionDate}" />
		        	</span>
			        <span class="titleSpan truncatable">Config version:</span>
			        <span class="valueSpan truncatable">${submission.questionnaireVersion}</span>
			    </div>
			    <c:if test="${submission.status eq 'PUBLISHED' }">
				    <div>
				        <span class="titleSpan truncatable">Publication Date:</span>
				        <span class="valueSpan truncatable">
				        	<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${submission.lastReviewDate}" />
			        	</span>
				        <span class="titleSpan truncatable">DataCite DOI:</span>
				        <span class="valueSpan truncatable">${submission.mintedDoi}</span>
				    </div>
			    </c:if>
			    <c:choose>
			      <c:when test="${ not empty submission.embargoDate}" >
				     <div>
				        <span class="titleSpan truncatable">Embargo Date:</span>
				        <span class="valueSpan truncatable">
				        	<fmt:formatDate pattern="MMM d, yyyy" value="${submission.embargoDate}" />
				        	<sec:authorize ifAllGranted="ROLE_ADMIN">
				        	    <button class="launchEmbargoButton" id="editEmbargoButton" type="button">Edit Embargo</button>
				        	</sec:authorize>
			        	</span>
				        <span class="titleSpan truncatable"></span>
				        <span class="valueSpan truncatable"></span>
				    </div>
			    </c:when>
			    <c:otherwise>
			        <sec:authorize ifAllGranted="ROLE_ADMIN">
				        <div>
					        <span class="titleSpan truncatable">
					        	<button class="launchEmbargoButton" id="addEmbargoButton" type="button">Add Embargo</button>
				        	</span>
					        <span class="valueSpan truncatable"></span>
					        <span class="titleSpan truncatable"></span>
					        <span class="valueSpan truncatable"></span>
				        </div>
			        </sec:authorize>
			    </c:otherwise>
			  </c:choose>  
			</div>
			<div class="centeredContent">
				<a href="#" id="hideAllLink" class="noUnderline">
					<button>Collapse all</button>
				</a>
				<a href="#" id="showAllLink" class="noUnderline">
					<button>Expand all</button>
				</a>
				<button id="printButton" class="noUnderline">Print</button>
			</div>
		</q:pageHeader>
			
		<div class="followsSharedFixedHeader">
			<c:if test="${submission.status eq 'REJECTED' or submission.status eq 'REJECTED_INCOMPLETE' }">
				<q:reviewRejectionInfo lastReview="${review}" />
			</c:if>
		    <c:forEach items="${submission.items }" var="item" varStatus="status">
		        <c:set var="divclass" value="${ status.count % 2 == 0 ? 'even' : 'odd' }" />
		        <c:choose>
		            <c:when test="${item.itemType eq 'QUESTION' or item.itemType eq 'QUESTION_SET'}">
		                <q:submissionQuestion model="${item}" groupIndent="0" divclass="${divclass}"
		                	review="${not empty review ? review : '' }" showResponseType="${showResponseType}" />
		            </c:when>
		            <c:when test="${item.itemType eq 'GROUP'}">
		                <q:submissionGroup model="${item}" groupIndent="0" divclass="${divclass}"
		                	review="${ not empty review ? review : '' }" showResponseType="${showResponseType}" />
		            </c:when>
		        </c:choose>   
		    </c:forEach>
		</div>
		<h2 id="dataFilesTitle" class="centeredContent">Submission Data Files</h2>
		<div class="uploadedFilesContainer">
		    <display:table name="submission.fileList" id="file">
		        <display:column title="Data File Name" property="fileName" class="filename" />
		        <display:column title="Size" property="fileSizeString" class="filesize" />
		        <display:column title="Description" property="fileDescription"/>
		        <display:column title="Type" property="fileTypeTitle" class="centeredContent" />
		        <display:column title="Format" property="formatTitle"/>
		        <display:column title="Version" property="formatVersion"/>

				<display:column title="Actions">
					<c:if test="${ file.fileAvailableForDownload}">
						<button class="clickToDownload">Download</button>
						<div class="fileIdCell" style="display: none">${file.id}</div>
					</c:if>
				</display:column>
				<c:set var="thereAreRejectedFiles" value="${(submission.status eq 'REJECTED' or submission.status eq 'REJECTED_SAVED') and not empty review and review.hasRejectedFiles}" />
				<c:if test="${thereAreRejectedFiles}">
		            <display:column title="Rejection">
			            <c:if test="${not empty review.fileReviews[file.id] and review.fileReviews[file.id].reviewOutcome eq 'REJECT'}">
			                <div class="errorBox">${review.fileReviews[file.id].comments}</div>
			            </c:if>
		            </display:column>
		        </c:if>
		    </display:table>
		</div>
		
		<div class="centeredContent">
			<h2>Linked Submissions</h2>
			<c:if test="${empty groupAdminView }"><button id="manageLinksButton" type="button">Manage Links</button></c:if>
		</div>
		
		
		<div class="linkedSubmissionsContainer uploadedFilesContainer">
		    <c:choose>
                <c:when test="${empty submissionLinks}">
                <div class="infoBox msgBoxSpacing centeredContent">No linked submissions</div>
                </c:when>
                <c:otherwise>
                    <display:table name="submissionLinks" id="sl" >
                      <display:column title="ID" property="linkedSubmission.id" href="${pageContext.request.contextPath}/viewSubmission" paramId="submissionId"/>
                      <display:column title="Title" property="linkedSubmission.title"/>
                      <display:column title="Type" property="linkType" />
                      <display:column title="SourceLink" property="sourceLink" />
                      <display:column title="Description" property="description" />
                      <display:column title="Linked By" property="linkedByUser.username" />
                      <display:column title="Date" property="linkDate" format="{0,date,dd/MM/yyyy}"/>
                    </display:table>
                </c:otherwise>  
		    </c:choose>
		</div>
	</div>
</body>
</html>