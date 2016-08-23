<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="model" type="au.edu.aekos.shared.web.model.QuestionModel" required="true" %>
<%@ attribute name="review" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="false" %>
<%@ attribute name="groupIndent" type="java.lang.Integer" required="true" %>
<%@ attribute name="showResponseType" type="java.lang.Boolean" required="false" %>
<%@ attribute name="divclass" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${empty showResponseType }">
    <c:set var="showResponseType" value="false" />
</c:if>

<c:set var="fontClass" value=""/>
<c:if test="${not model.hasResponse and empty model.questionSetModelList}">
    <c:set var="fontClass" value="noResponseFontClass"/>
</c:if>

<c:set var="divclass" value="${not empty divclass ? divclass : ''}" />

<c:if test="${model.visible}" >
	<div id="Q${model.questionId}" class="questionDiv ${fontClass} ${divclass}">
	    <div class="submissionQuestionNumber ${divclass}">${model.questionId}</div>
	    <div class="submissionQuestionText ${divclass}">
	    	${model.questionText}
	        <c:if test="${not empty model.questionHelpText}"></c:if>
	    </div>
	    <div class="submissionAnswer ${divclass}">
	        <c:if test="${not empty review and not empty review.answerReviews[model.questionId] and review.answerReviews[model.questionId].outcome eq 'REJECT' }">
			    <div class="reviewRejectionMessage errorBox centeredContent">
			       ${ review.answerReviews[model.questionId].comment }
			    </div>
		    </c:if>
	        <c:choose>
	            <c:when test="${model.responseType eq 'GEO_FEATURE_SET' }">
	                <c:set var="featureSetObj" value="${model.jsonGeoFeatureSet}" />
	                <c:if test="${not empty featureSetObj and fn:length(featureSetObj.features) gt 0}">
	                    <table>
	                    	<thead>
								<tr>
									<th>Feature ID</th>
									<th>Description</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${featureSetObj.features}" var="feature">
									<tr>
										<td>${feature.id}</td>
										<td>${feature.description}</td>
									</tr>
								</c:forEach>
							</tbody>
	                    </table>
	                    <button class="viewFeaturesButton">View Features</button>
	                    <span class="geoFeatureSetJsonStr" style="display:none;">${model.responseText }</span>
	                </c:if>
	            </c:when>
	            <c:when test="${model.responseType eq 'MULTIPLE_QUESTION_GROUP' }">
	                <table>
		                <c:forEach items="${model.questionSetModelList }" var="qsm" varStatus="indx">
		                    <c:forEach items="${qsm.questionModelList }" var="ques" >
								<tr>
									<td>${ques.questionText}</td>
									<td>${ques.responseText} 
									    <c:if test="${not empty ques.suggestionText }">
									        ${ques.suggestionText} 
									    </c:if>
									</td>
								</tr>
							</c:forEach>
			                <c:if test="${not indx.last}">
								<tr>
									<td></td>
									<td></td>
								</tr>
							</c:if>
		                </c:forEach>
	                </table>
	            </c:when>
	            <c:when test="${not model.isMultiSelect }">
			        ${model.responseText } 
			        <c:if test="${not empty model.suggestionText }">
			            &nbsp;&nbsp;${model.suggestionText}
			        </c:if>
			    </c:when>
			    <c:otherwise>
			        <table class="submissionViewMultiSelectTable">
			            <c:forEach items="${model.multiselectAnswerList }" var="ans">
                            <tr><td>${ans.responseText}</td>
                                <c:if test="${ans.responseType eq 'IMAGE'}">
									<!-- Multi-select images -->
	                                <td>
	                                    <a target="_blank" href="getSubmissionImage?imageId=${ans.imageObjectId}&questionId=${model.questionId}&submissionId=${model.submissionId}">
	                                        <img class="thumbnail" src="getSubmissionImage?imageId=${ans.imageThumbId}&questionId=${model.questionId}&submissionId=${model.submissionId}"/>
	                                    </a>
	                                 </td>
                                </c:if>
                            </tr>			            
			            </c:forEach>
			        </table>
			    </c:otherwise>
	        </c:choose>
	        <c:if test="${model.responseType eq 'SITE_FILE' and not empty model.siteFileCoordSystem}">
	            (Coordinate system=${model.siteFileCoordSystem})
	        </c:if>
		    <c:if test="${showResponseType and not empty model.responseType}">
		        <div class="submissionResponseType">Response Type = ${model.responseType}</div>
		    </c:if>
		    <c:if test="${model.responseType eq 'IMAGE' and not empty model.imageObjectId}">
				<!-- Single image (not multi-select) -->
				<div class="submissionImageResponse">
					<a target="_blank" href="getSubmissionImage?imageId=${model.imageObjectId}&questionId=${model.questionId}&submissionId=${model.submissionId}">
						<img class="thumbnail" src="getSubmissionImage?imageId=${model.imageThumbId}&questionId=${model.questionId}&submissionId=${model.submissionId}" />
					</a>
				</div>
			</c:if>
		    <c:if test="${model.responseType eq 'SITE_FILE' and model.canRenderSiteFile and not empty model.siteFileDataId}">
		        <div class="siteFileViewDiv">
		            <button class="siteFileViewButton">View Study Locations</button>
		            <div style="display:none;" class="siteFileName" >${model.responseText}</div>
		            <div style="display:none;" class="siteFileDataId" >${model.siteFileDataId}</div>
		        </div>
		    </c:if>
	    </div>
	    <div class="answerId" style="display:none;">${model.submissionAnswerId}</div>
	</div>
</c:if>