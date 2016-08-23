<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="question" type="au.edu.aekos.shared.questionnaire.jaxb.Question" required="true" %>
<%@ attribute name="divclass" type="java.lang.String" required="false" %>
<%@ attribute name="indentDepth" type="java.lang.Integer" required="true" %>
<%@ attribute name="controlledVocab" type="java.util.Map" required="true" %>
<%@ attribute name="questionDisplayConditionMap" type="java.util.Map" required="true" %>
<%@ attribute name="imageAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="imageSeriesAnswerMap" type="java.util.Map" required="true" %>
<%@ attribute name="dynatreeDivMap" type="java.util.Map" required="false" %>
<%@ attribute name="reviewModel" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" required="false" %>
<%@ attribute name="titleQuestionId" type="java.lang.String" required="false" %>
<%@ attribute name="displayQuest" type="au.edu.aekos.shared.questionnaire.DisplayQuestionnaire" required="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>

<c:set var="styleString" value="" />
<c:set var="textMaxLength" value="500" />

<c:if test="${not empty questionDisplayConditionMap[question.id] and not questionDisplayConditionMap[question.id].isVisible}">
    <c:set var="styleString" value="display:none;" />
</c:if>


<div id="Q${question.id}" class="questionDiv ${empty divclass ? '' : divclass}" style="${styleString}">
    <span class="questionIdSpan${indentDepth}" style="${question.showId?'':'display:none;'}">
    	<span class="questionIdSpan">${question.id}</span>
	    <span class="helpSpan">
	        <c:if test="${not empty question.description}">
	        	<div class="helpIcon" alt="Help ${question.id}"></div>
	        	<div class="helpText">
	        		<p>${question.description}</p>
	       		</div>
			</c:if>
	    </span>
   	</span>
    <span class="questionSpan">
    	${question.text}
   		<c:if test="${question.responseMandatory}">
   			<span class="mandatoryIndicator">&nbsp;*</span>
 		</c:if>
	</span>
    
    <c:choose>
        <c:when test="${question.responseType eq 'CONTROLLED_VOCAB' }">
            <span class="responseInputSpan answerContainer">
            	<c:choose>
	                <c:when test="${ question.autocomplete }" >
	                    <input class="autocomplete_${question.urlTraitName}" />
	                    <form:input path="answers[${question.id}].response" cssErrorClass="formInputError" readonly="true" cssClass="sharedAnswer" />
	                    <div class="deleteIcon autoInputClearIcon"></div>
	                </c:when>
	                <c:otherwise>
			            <form:select path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="sharedAnswer">
			                <q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
			                <q:sharedOptions items="${controlledVocab[question.traitName]}" itemLabel="displayString" itemTitle="description" 
									itemValue="traitValue" selectedValue="${answers.answers[question.id].response}" />
			            </form:select>
	                </c:otherwise>
            	</c:choose>   
	            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'CONTROLLED_VOCAB_SUGGEST' }">
             <span class="responseInputSpan answerContainer">
	            <form:select path="answers[${question.id}].response" cssClass="selectWithSuggestOption sharedAnswer" cssErrorClass="formInputError selectWithSuggestOption">
					<q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
	                <form:option label="Other - Please suggest" value="OTHER" />
	                <q:sharedOptions items="${controlledVocab[question.traitName]}" itemLabel="displayString" itemTitle="description" 
							itemValue="traitValue" selectedValue="${answers.answers[question.id].response}" />
	            </form:select>
            </span>
            <span class="responseSuggestInputSpan" style="display:none;">
                <form:input path="answers[${question.id}].suggestedResponse" cssErrorClass="formInputError" cssClass="sharedAnswer" maxlength="${textMaxLength}"/>
            </span>
            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
            <form:errors path="answers[${question.id}].suggestedResponse" cssClass="formErrorSpan" />
        </c:when>
        <c:when test="${question.responseType eq 'DATE' }">
            <span class="responseInputSpan answerContainer">
            	<form:input path="answers[${question.id}].response" cssClass="dateResponseInput questionResponseInput sharedAnswer" cssErrorClass="dateResponseInput questionResponseInput formInputError"/>
           	</span>
            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
        </c:when>
        <c:when test="${question.responseType eq 'YES_NO' }">
            <span class="responseInputSpan answerContainer">
	            <form:select path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="sharedAnswer">
	                <q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
	                <form:option label="Yes" value="Y" />
	                <form:option label="No" value="N" />
	            </form:select>
	            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'IMAGE' }">
            <!--  launch pop up window containing image upload dialog -->
            <span class="responseInputSpan answerContainer">
                <form:input readonly="true" path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="sharedAnswer"/>
                <input type="button" class="uploadImageButton" value="Upload Image" />
                <input style="display:none;" type="button" class="uploadImageClearButton" value="Clear" />
            </span>
            <c:choose>
	            <c:when test="${not empty answers.answers[question.id].response and not empty imageAnswerMap[question.id]}">
	                <span class="imageThumbSpan">
	                    <a target="_blank" href="${pageContext.request.contextPath}/getImage?imageId=${imageAnswerMap[question.id].imageObjectName }">
	                        <img class="thumbnail" src="${pageContext.request.contextPath}/getImage?imageId=${imageAnswerMap[question.id].imageThumbnailName }"/>
	                    </a>
	                </span>
	            </c:when>
	            <c:otherwise>
	                <span class="imageThumbSpan" style="display:none;"><a target="_blank"><img class="thumbnail" /></a></span>
	            </c:otherwise>
	        </c:choose>
        </c:when>
        <c:when test="${question.responseType eq 'MULTISELECT_IMAGE' }">
             <div class="multiselectAnswer answerContainer">
                <table>
	            <c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
	                <tr class="multirow" >
	                  <td class="multiAnswerCell">
	                  
			            <span class="responseInputSpan">
			                <form:input readonly="true" path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="msImageInput formInputError" cssClass="msImageInput sharedAnswer"/>
			                <input  type="button" style="${ empty answers.answers[ question.id ].multiselectAnswerList[ indx.index].response ? 'display:none;' : '' }" class="uploadMultiImageClearButton" value="Clear" />
                            <input type="button" class="uploadMultiImageButton" value="Upload Image" />
			             </span>
			            
			            <c:if test="${ indx.last}">
			                <span class="multiAddRowImage">
			                	<button type="button">Add another</button>
		                	</span>
			            </c:if>
			            
			            <div class="multiRowIndex" style="display:none;">${indx.index}</div>
			            
			            <c:choose>
				            <c:when test="${not empty answers.answers[question.id].multiselectAnswerList[ indx.index].response 
				                            and not empty imageSeriesAnswerMap[question.id] and not empty imageSeriesAnswerMap[question.id].imageAnswerList[indx.index] }">
				                <span class="imageThumbSpan">
				                    <a target="_blank" href="../getImage?imageId=${imageSeriesAnswerMap[question.id].imageAnswerList[indx.index].imageObjectName }">
				                        <img class="thumbnail" src="../getImage?imageId=${imageSeriesAnswerMap[question.id].imageAnswerList[indx.index].imageThumbnailName }"/>
				                    </a>
				                </span>
				            </c:when>
				            <c:otherwise>
				                <span class="imageThumbSpan" style="display:none;"><a target="_blank"><img class="thumbnail" /></a></span>
				            </c:otherwise>
				        </c:choose>
			          
			          </td>
		            </tr>
	            </c:forEach>
	            </table>
	            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
            </div>
        </c:when>  
        <c:when test="${question.responseType eq 'MULTISELECT_TEXT' }">
            <div class="multiselectAnswer answerContainer">
                <table>
	            <c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
	                <tr class="multirow" >
	                  <td class="multiAnswerCell">
			            <span class="responseInputSpan">
			                <c:choose>
			                  <c:when test="${empty question.maxLength }">
			            	    <form:input path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="formInputError" cssClass="sharedAnswer" maxlength="${textMaxLength}"/>
		            	      </c:when>
		            	      <c:otherwise>
		            	        <form:input path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="formInputError" cssClass="sharedAnswer" maxlength="${question.maxLength }"/>
		            	      </c:otherwise>
		            	    </c:choose>  
		            	</span>
			            <span class="multiAddRowText" style="${indx.last ? '' : 'display:none;'}">
							<button type="button">Add another</button>
		            	</span>
		            	<div class="deleteIcon clearNearestSharedAnswerButton"></div>
			            <form:errors path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssClass="formErrorSpan" />
			            <div class="multiRowIndex" style="display:none;">${indx.index}</div>
			          </td>
		            </tr>
	            </c:forEach>
	            </table>
            </div>
        </c:when>
        <c:when test="${question.responseType eq 'MULTISELECT_TEXT_BOX' }">
            <div class="multiselectAnswer">
                <table>
	            <c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
	                <tr class="multirow" >
	                  <td class="multiAnswerCell">
			            <span class="responseInputSpan">
			            	<form:textarea path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="formInputError" cssClass="sharedAnswer" />
		            	</span>
			            <span class="multiAddRowTextArea" style="${indx.last ? '' : 'display:none;'}">
			            	<button type="button">Add another</button>
		            	</span>
			            <form:errors path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssClass="formErrorSpan" />
			            <div class="multiRowIndex" style="display:none;">${indx.index}</div>
			          </td>
		            </tr>
	            </c:forEach>
	            </table>
            </div>
        </c:when>
        <c:when test="${question.responseType eq 'MULTISELECT_CONTROLLED_VOCAB' and question.autocomplete}">
            <div class="multiselectAnswer answerContainer">
				<table>
					<tr>
						<td class="autoCompleteMultiSelectControlledVocabInput">
							<input class="autocomplete_${question.urlTraitName}_multi" />
						</td>
						<td>
							<table class="multiAutoAnswerTable">
								<c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
									<tr class="multiAutoAnswerRow">
										<td>
											<form:input path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="formInputError"
												readonly="true" cssClass="sharedAnswer" />
										</td>
										<td>
											<div class="deleteIcon ${indx.first ? 'autoInputClearIconMSFirst' : 'autoInputClearIconMS'}"></div>
										</td>
										<td>
											<form:errors path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssClass="formErrorSpan" />
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</table>
			</div>
        </c:when>
        <c:when test="${question.responseType eq 'MULTISELECT_CONTROLLED_VOCAB' }">
            <div class="multiselectAnswer answerContainer">
                <table>
	            <c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
	                <tr class="multirow" >
	                  <td class="multiAnswerCell">
			            <span class="responseInputSpan">
			                <form:select path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssErrorClass="formInputError" cssClass="sharedAnswer">
								<q:sharedDefaultOption isMandatory="${question.responseMandatory}" />
								<q:sharedOptions items="${controlledVocab[question.traitName]}" itemLabel="displayString" itemTitle="description" 
									itemValue="traitValue" selectedValue="${answers.answers[question.id].multiselectAnswerList[indx.index].response}" />
				            </form:select>
			            </span>
			            <span class="multiAddRowCV" style="${indx.last ? '' : 'display:none;'}">
			            	<button type="button">Add another</button>
		            	</span>
		            	<div class="deleteIcon clearNearestSharedAnswerButton"></div>
			            <form:errors path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssClass="formErrorSpan" />
			            <div class="multiRowIndex" style="display:none;">${indx.index}</div>
			          </td>
		            </tr>
	            </c:forEach>
	            </table>
            </div>
        </c:when>
        <c:when test="${question.responseType eq 'TREE_SELECT' }">
            <!-- 
	            <p>
					<a href="#" class="dynatreeExpandAll">Expand all</a>
					<a href="#" class="dynatreeCollapseAll">Collapse all</a>
				</p>	
			 -->	
	            <div id="${dynatreeDivMap[question.id ]}" style="display:inline-block;" class="dynatreeDiv answerContainer">
	            
	            </div>
	            <span class="responseInputSpan">
	                <c:forEach items="${ answers.answers[question.id].multiselectAnswerList}" var="ans" varStatus="indx">
	                    <form:hidden path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" />
		            	<form:errors path="answers[${question.id}].multiselectAnswerList[${indx.index}].response" cssClass="formErrorSpan" />
	                </c:forEach>
	            </span>
        </c:when>
        <c:when test="${question.responseType eq 'GEO_FEATURE_SET' }">
            <div class="geoFeatureSet answerContainer" style="display:inline-block;">
	            <form:hidden readonly="true" path="answers[${question.id}].response" cssErrorClass="geometryInput formInputError" cssClass="geometryInput" />
	            <div class="featureList">
	            </div>
	            <input type="button" class="viewFeatureSetMapButton" value="Map Tool" /> 
            </div>
            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
        </c:when>
        <c:when test="${question.responseType eq 'SITE_FILE' }">
            <span class="responseInputSpan answerContainer">
                <form:input readonly="true" path="answers[${question.id}].response" cssErrorClass="formInputError siteFileInput questionResponseInput" cssClass="siteFileInput questionResponseInput sharedAnswer"/>
                <input type="button" class="uploadSiteFileButton" value="Study Location File Upload" />
                <button class="clearSiteFileButton uploadFileClearButton">Clear</button>
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'SPECIES_LIST' }">
            <span class="responseInputSpan answerContainer">
                <form:input readonly="true" path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="speciesListInput questionResponseInput sharedAnswer"/>
                <input type="button" class="uploadSpeciesListButton uploadFileButton" value="Upload Species List" />
                <button class="clearSpeciesListButton uploadFileClearButton">Clear</button>
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'LICENSE_CONDITIONS' }">
            <span class="responseInputSpan answerContainer">
                <form:input readonly="true" path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="licenseConditionsInput questionResponseInput sharedAnswer"/>
                <input type="button" class="uploadLicenseConditionsButton uploadFileButton" value="Upload License Conditions" />
                <button class="clearLicenseConditionsButton uploadFileClearButton">Clear</button>
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'DOCUMENT' }">
            <span class="responseInputSpan answerContainer">
                <form:input readonly="true" path="answers[${question.id}].response" cssErrorClass="formInputError" cssClass="documentInput questionResponseInput sharedAnswer"/>
                <input type="button" class="uploadDocumentButton uploadFileButton" value="Upload Document" />
                <button class="clearDocumentButton uploadFileClearButton">Clear</button>
            </span>
        </c:when>
        <c:when test="${question.responseType eq 'TEXT'}">
            <c:set var="modifyReadOnly" value="false" />
            <c:if test="${not empty displayQuest.submissionId and question.id eq titleQuestionId }">
                <c:set var="modifyReadOnly" value="true" />
            </c:if>
            <span class="responseInputSpan answerContainer">
                <c:choose>
			      <c:when test="${empty question.maxLength }">
	            	<form:input readonly="${modifyReadOnly}" path="answers[${question.id}].response" 
	            		cssClass="${empty question.responseInputClass ? '' : question.responseInputClass } questionResponseInput sharedAnswer" 
	            		cssErrorClass="formInputError questionResponseInput" maxlength="${textMaxLength}"/>
                  </c:when>
                  <c:otherwise>
                    <form:input readonly="${modifyReadOnly}" path="answers[${question.id}].response" 
	            		cssClass="${empty question.responseInputClass ? '' : question.responseInputClass } questionResponseInput sharedAnswer" 
	            		cssErrorClass="formInputError questionResponseInput" maxlength="${question.maxLength}"/>
                  </c:otherwise>
                </c:choose>
	            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
           	</span>
        </c:when>
        <c:when test="${question.responseType eq 'TEXT_BOX'}">
            <c:set var="textAreaClassList" value="${empty question.responseInputClass ? '' : question.responseInputClass } questionResponseInput sharedAnswer textAreaInput" />
            <span class="responseInputSpan answerContainer">
            	<form:textarea path="answers[${question.id}].response" 
                  cssClass="${textAreaClassList}" 
                  cssErrorClass="formInputError ${textAreaClassList}"/>
             </span>
            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
        </c:when>
        <c:otherwise>
            <span class="responseInputSpan answerContainer">
            	<form:input path="answers[${question.id}].response" cssErrorClass="formInputError" maxlength="${textMaxLength }"/>
	            <form:errors path="answers[${question.id}].response" cssClass="formErrorSpan" />
           	</span>
        </c:otherwise>
    </c:choose>    
    
    <form:hidden path="answers[${question.id}].responseType" cssClass="responseType"/>
	<c:if test="${not empty reviewModel and not empty reviewModel.answerReviews[ question.id ] and reviewModel.answerReviews[ question.id ].outcome eq 'REJECT' }">
	    <div class="modifySubmissionAnswerRejectionMessage">This answer was rejected with comment: ${ reviewModel.answerReviews[ question.id ].comment }</div>
	</c:if>
</div>