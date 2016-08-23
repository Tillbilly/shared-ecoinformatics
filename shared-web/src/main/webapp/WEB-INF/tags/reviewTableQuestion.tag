<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="model" type="au.edu.aekos.shared.web.model.QuestionModel" required="true" %>
<%@ attribute name="reviewModel" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" %>
<%@ attribute name="answerHistoryMap" type="java.util.Map" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

  <tr class="reviewQuestionRow">
       <c:set var="indicatorClass" value="" />
       <c:if test="${not empty answerHistoryMap and not empty answerHistoryMap[model.questionId] }">
        <c:choose>
            <c:when test="${answerHistoryMap[model.questionId] eq 'WAS_REJECTED'}" >
               <c:set var="indicatorClass" value="reviewAnswerWasRejectedClass" />
            </c:when>
            <c:when test="${answerHistoryMap[model.questionId] eq 'ANSWER_CHANGED' }">
                <c:set var="indicatorClass" value="reviewAnswerChangedClass" />
            </c:when>
       </c:choose>
      </c:if>
      <td class="reviewIdColumn ${empty indicatorClass?'':indicatorClass}">${model.questionId}</td>
      <td class="reviewQ_Quest">${model.questionText}</td>
      <c:choose>
          <c:when test="${ not empty model.responseText or ( model.isMultiSelect and not empty model.multiselectAnswerList ) or (model.responseType eq 'MULTIPLE_QUESTION_GROUP' and not empty model.questionSetModelList ) }" >
        <td class="reviewQ_Response">
             <c:choose>
              <c:when test="${model.responseType eq 'GEO_FEATURE_SET' }">
                <c:set var="featureSetObj" value="${model.jsonGeoFeatureSet}" />
                <c:if test="${not empty featureSetObj and fn:length(featureSetObj.features) gt 0}">
                    <table class="featureReviewTable">
                        <tr><th>Feature ID</th><th>Description</th></tr>
	                    <c:forEach items="${featureSetObj.features}" var="feature">
	                        <tr><td>${feature.id }</td><td>${feature.description}</td></tr>
	                    </c:forEach>
                    </table>
                    <button class="viewFeaturesButton">View Features</button>
                    <span class="geoFeatureSetJsonStr" style="display:none;">${model.responseText }</span>
                </c:if>
          	  </c:when>
          	  <c:when test="${model.responseType eq 'MULTIPLE_QUESTION_GROUP' }">
          	      <table class="reviewQuestionSetTable">
		                <c:forEach items="${model.questionSetModelList }" var="qsm" varStatus="indx">
		                    <c:forEach items="${qsm.questionModelList }" var="ques" >
		                        <tr><td class="reviewQuestionSetRowTitle">${ques.questionText}</td>
		                          <td>
		                            <c:choose>
		                              <c:when test="${not empty ques.suggestionText }">
		                                  ${ques.suggestionText} 
		                                  <c:if test="${empty reviewModel.peerReview or ( not empty reviewModel.peerReview and not reviewModel.peerReview ) }">
			                                  <a href="#" class="analyseVocabSuggestionButtonQSR">Analyse</a>
			                                  <span class="analyseQSRquestionId" style="display:none">${ques.questionId}</span>
			                                  <span class="analyseQSRgroupIndex" style="display:none">${indx.index}</span>
		                                  </c:if>
		                              </c:when>
		                              <c:otherwise>
		                                  ${ques.responseText}
		                              </c:otherwise>
		                            </c:choose>  
		                          </td></tr>
		                    </c:forEach>
			                <c:if test="${not indx.last }">
			                    <tr><td></td><td></td></tr>
			                </c:if>
		                </c:forEach>
	                </table>
          	  </c:when>
              <c:when test="${not model.isMultiSelect }">
                <c:choose>
                    <c:when test="${model.responseType eq 'IMAGE' and not empty model.imageObjectId}">
                        <a target="_blank" href="getSubmissionImage?imageId=${model.imageObjectId}&questionId=${model.questionId}&submissionId=${model.submissionId}">
                               <img title="${model.responseText }" class="thumbnail" src="getSubmissionImage?imageId=${model.imageThumbId}&questionId=${model.questionId}&submissionId=${model.submissionId}"/>
                            </a>
                    </c:when>
                    <c:otherwise>
				        ${model.responseText }
				        <c:if test="${not empty model.suggestionText }">
				            &nbsp;&nbsp;${model.suggestionText}
				        </c:if>
				        <c:if test="${model.hasSuggestedVocabEntry }" >
				            <c:if test="${empty reviewModel.peerReview or ( not empty reviewModel.peerReview and not reviewModel.peerReview ) }">
				                <a href="#" class="analyseVocabSuggestionButton">Analyse</a>
				            </c:if>
				        </c:if>
		            </c:otherwise>
	            </c:choose>
	            <c:if test="${model.responseType eq 'SITE_FILE'}">
                          &nbsp;&nbsp;${model.siteFileCoordSystem}
                          <c:if test="${model.canRenderSiteFile and not empty model.siteFileDataId}">
				            <button class="siteFileViewButton">View Sites</button>
				            <div style="display:none;" class="siteFileName" >${model.responseText }</div>
				            <div style="display:none;" class="siteFileDataId" >${model.siteFileDataId}</div>
  						</c:if>
                      </c:if>
		    </c:when>
		    <c:otherwise>
		        <table class="multiselectReviewTable">
		            <c:forEach items="${model.multiselectAnswerList }" var="ans" varStatus="indx">
                           <tr><td style="border: none;">${ans.responseText} 
                                 <c:if test="${indx.first and model.hasSuggestedVocabEntry }" >
                                     <c:if test="${empty reviewModel.peerReview or ( not empty reviewModel.peerReview and not reviewModel.peerReview ) }">
                                     <td><a href="#" class="analyseVocabSuggestionButton">Analyse</a></td>
                                     </c:if>
				                 </c:if> 
                                </td>
                                <c:if test="${ans.responseType eq 'IMAGE' }">
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
        <c:if test="${reviewModel.submissionStatus eq 'RESUBMITTED' }">
            <span style="float:right;"><img class="showQuestionReviewHistoryIcon" src="${pageContext.request.contextPath}/images/history.png"/></span>
        </c:if>
        </td>
		    <c:choose>    
		        <c:when test="${model.titleQuestion }">
		            <td colspan="3" class="reviewNoResponse"><form:hidden path="answerReviews[${model.questionId}].outcome" /></td>
		        </c:when>
		        <c:otherwise>
			        <td class="reviewQ_PASS"><form:radiobutton path="answerReviews[${model.questionId}].outcome" value="PASS" cssClass="outcomeRadio" cssErrorClass="outcomeRadio formInputError"/></td>
			        <td class="reviewQ_REJECT"><form:radiobutton path="answerReviews[${model.questionId}].outcome" value="REJECT" cssClass="outcomeRadio" cssErrorClass="outcomeRadio formInputError"/></td>
			        <td class="reviewQ_Comments">
			            <form:errors path="answerReviews[${model.questionId}].outcome" cssClass="formErrorSpan radioErrorMessage" />
			            <form:textarea path="answerReviews[${model.questionId}].comment" cssErrorClass="formInputError"/>
			            <form:errors path="answerReviews[${model.questionId}].comment" cssClass="formErrorSpan" />
			        </td>
		        </c:otherwise>
		    </c:choose>    
          </c:when>
          <c:otherwise>
              <td class="reviewNoResponse" colspan="4" />
          </c:otherwise>
      </c:choose>
  </tr>    
