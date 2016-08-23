package au.edu.aekos.shared.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import au.edu.aekos.shared.questionnaire.DisplayCondition;
import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

/**
 * TODO Tidy up and reuse pieces of generation code ( freemarker or some other templating )
 * 
 * @author btill
 */


@Component
public class QuestionnaireDisplaySupport {
	
	public static String generateJQueryPageTriggerDisplayCondition(String id, DisplayCondition dc, boolean isQuestion){
		if(dc.getChildDisplayConditionList() != null && dc.getChildDisplayConditionList().size() > 0){
			return generateJQueryPageTriggerDisplayConditionWithChild(id, dc,isQuestion);
		}
		String answerJQueryID = "'#answers" + dc.getQuestionId() + ".response'";
		String jqueryDivID = isQuestion ? "#Q" + id : "#G" + id ;
		
		answerJQueryID = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(answerJQueryID);
		jqueryDivID = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(jqueryDivID );
		
		StringBuilder sb = new StringBuilder("$(").append(answerJQueryID).append(")");
		if( dc.getConditionalAnswer() == null || 
				( ! ResponseType.TEXT.equals( dc.getConditionalAnswer().getResponseType() ) 
						&& ! ResponseType.SITE_FILE.equals( dc.getConditionalAnswer().getResponseType() )   ) ){
			sb.append(".change(function(){");
			if( dc.getResponseNull() ){
				sb.append("if($(this).is(':visible') && $(this).find(':selected').val() != '' ){");
				sb.append("$('"+jqueryDivID+"').hide();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').show();}");
			}else if( dc.getResponseNotNull() && ! StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).find(':selected').val() != '' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else if( StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).find(':selected').val() == '").append(dc.getResponseValue()).append("' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else{ //This should be the question visible condition, 
				return null;
			}
		}else if(ResponseType.SITE_FILE.equals( dc.getConditionalAnswer().getResponseType() )){
			sb.append(".change(function(){");
			if( dc.getResponseNull() ){
				sb.append("if($(this).is(':visible') && $(this).val() != '' ){");
				sb.append("$('"+jqueryDivID+"').hide();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').show();}");
			}else if( dc.getResponseNotNull() && ! StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() != '' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else if( StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() == '").append(dc.getResponseValue()).append("' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else{
				return null;
			}
		}
		else{
			//For text entry fields
			sb.append(".keyup(function(){");
			if( dc.getResponseNull() ){
				sb.append("if($(this).is(':visible') && ( $(this).val() == null || $(this).val() == '' )){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			} else if( dc.getResponseNotNull() && ! StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() != '' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else if( StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() == '").append(dc.getResponseValue()).append("' ){");
				sb.append("$('"+jqueryDivID+"').show();");
				sb.append("}else{");
				sb.append("$('"+jqueryDivID+"').hide();}");
			}else{
				return null;
			}
		}
		sb.append("});");
		return sb.toString();
	}
	

	/**
	 * 
	 * 
	 * 
	 * @param id
	 * @param dc
	 * @param isQuestion
	 * @return
	 */
	
	
	public static String generateJQueryPageTriggerDisplayConditionWithChild(String id, DisplayCondition dc, boolean isQuestion){
		
		String answerJQueryID = "'#answers" + dc.getQuestionId() + ".response'";
		String jqueryDivID = isQuestion ? "#Q" + id : "#G" + id ;
		answerJQueryID = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(answerJQueryID);
		jqueryDivID = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(jqueryDivID );
		
		String childTriggerAnswerId = "'#answers" + id + ".response'";
		childTriggerAnswerId = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(childTriggerAnswerId);
		
		StringBuilder sb = new StringBuilder("$(").append(answerJQueryID).append(")");
		List<String> triggerStrings = new ArrayList<String>();
		
		if( dc.getConditionalAnswer() == null || 
				( ! ResponseType.TEXT.equals( dc.getConditionalAnswer().getResponseType() ) 
						&& ! ResponseType.SITE_FILE.equals( dc.getConditionalAnswer().getResponseType() )   )
						&& ! ! ResponseType.TEXT_BOX.equals( dc.getConditionalAnswer().getResponseType() ) ){
			
			sb.append(".change(function(){");
			if( dc.getResponseNull() ){
				sb.append("if($(this).is(':visible') && ( $(this).find(':selected').val() == null || $(this).find(':selected').val() == '' ) ){");
			}else if( dc.getResponseNotNull() && ! StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && ( $(this).find(':selected').val() != '' ) ){");
			}else if( StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && ( $(this).find(':selected').val() == '").append(dc.getResponseValue()).append("' ) ){");
			}else{ //This should be the question visible condition, 
				return null;
			}
		}
		else{
			if(ResponseType.TEXT.equals( dc.getConditionalAnswer().getResponseType()) 
					|| ResponseType.TEXT_BOX.equals( dc.getConditionalAnswer().getResponseType())){
				sb.append(".keyup(function(){");
			}else{
				sb.append(".change(function(){");
			}
			if( dc.getResponseNull() ){
				sb.append("if($(this).is(':visible') && ($(this).val() == null || $(this).val() == '' )){");
				
			} else if( dc.getResponseNotNull() && ! StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() != '' ){");
				
			}else if( StringUtils.hasLength(dc.getResponseValue() ) ){
				sb.append("if($(this).is(':visible') && $(this).val() == '").append(dc.getResponseValue()).append("' ){");
			}else{ //This should be the question visible condition, 
				return null;
			}
		}
		sb.append("$('"+jqueryDivID+"').show();");
		for(DisplayCondition childDC : dc.getChildDisplayConditionList()){
			String childDcId = childDC.getOwnerId();
			if( childDC.getIsVisibleOnlyCondition() ){
				String jqueryChildDivId = "#Q" + childDcId;
				jqueryChildDivId = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(jqueryChildDivId);
				sb.append("$('"+jqueryChildDivId+"').show();");
			}
			String childAnswerJQueryID = "'#answers" + childDcId + ".response'";
			childAnswerJQueryID = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(childAnswerJQueryID);
			//Im going to trigger both event types for the element - easier than finding out exactly what response type the input is for.
			triggerStrings.add("$(" + childAnswerJQueryID + ").trigger('keyup');");
			triggerStrings.add("$(" + childAnswerJQueryID + ").trigger('change');");
		}
		sb.append("}else{");
		sb.append("$('"+jqueryDivID+"').hide();");
		
		for(DisplayCondition childDC : dc.getChildDisplayConditionList()){
			String childDcId = childDC.getOwnerId();
			if( childDC.getIsVisibleOnlyCondition() ){
				String jqueryChildDivId = "#Q" + childDcId;
				jqueryChildDivId = QuestionnaireDisplaySupport.dereferenceDotInSelectorId(jqueryChildDivId);
				sb.append("$('"+jqueryChildDivId+"').hide();");
			}
		}
		sb.append("}");
		sb.append("$(" + childTriggerAnswerId + ").trigger('change');");
		sb.append("$(" + childTriggerAnswerId + ").trigger('keyup');");
		if(triggerStrings.size() > 0){
			for(String triggerStr : triggerStrings){
				sb.append(triggerStr);
			}
		}
		sb.append("});");
		return sb.toString();
	}
	
	public static String dereferenceDotInSelectorId(String selectorId){
		if(selectorId.contains(".")){
			String [] pieces = selectorId.split("\\.");
			String result = pieces[0];
			for(int x = 1; x < pieces.length; x++ ){
				result += "\\\\." + pieces[x];
			}
			return result;
			
		}else{
			return selectorId;
		}
	}
	
	public static String generateJQueryForwardPopulateString(String sourceQId, String targetQId, ResponseType responseType){
		String sourceSelector = QuestionnaireDisplaySupport.dereferenceDotInSelectorId( "'#answers" + sourceQId + ".response'" );
		String targetSelector = QuestionnaireDisplaySupport.dereferenceDotInSelectorId( "'#answers" + targetQId + ".response'" );
		
		if(ResponseType.TEXT.equals(responseType) || ResponseType.TEXT_BOX.equals(responseType) ){
			StringBuilder sb = new StringBuilder("$(").append(sourceSelector).append(").keyup(function(){");
			//If data key is not set then it is the first keyup
			sb.append("var canChangeFlag = $(").append(targetSelector).append(").data('canChange');");
			sb.append("if(canChangeFlag == null){");
				sb.append("if($(").append(targetSelector).append(").val() == null || $(").append(targetSelector).append(").val() == '' ){");
					sb.append("$(").append(targetSelector).append(").data('canChange','YES');");
					sb.append("canChangeFlag = 'YES';");
				sb.append("}else{");
				sb.append("$(").append(targetSelector).append(").data('canChange','NO');");
				sb.append("canChangeFlag = 'NO';");
				sb.append("}");
			sb.append("}");
			sb.append("if(canChangeFlag == 'YES' ){");
			sb.append("$(").append(targetSelector).append(").val( $(this).val() );");
			sb.append("}");
			sb.append("});");
			//If the user changes the target, need to set canChange to no on the target.
			sb.append("$(").append(targetSelector).append(").keyup(function(){");
			sb.append("$(this).data('canChange','NO');");
			sb.append("});");
			return sb.toString();
		}else if(ResponseType.CONTROLLED_VOCAB.equals(responseType) || ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(responseType) ){
			
			String sourceSuggestSelector = QuestionnaireDisplaySupport.dereferenceDotInSelectorId( "'#answers" + sourceQId + ".suggestedResponse'" );
			String targetSuggestSelector = QuestionnaireDisplaySupport.dereferenceDotInSelectorId( "'#answers" + targetQId + ".suggestedResponse'" );
			
			StringBuilder sb = new StringBuilder("$(").append(sourceSelector).append(").change(function(){");
			//If data key is not set then it is the first change
			sb.append("var canChangeFlag = $(").append(targetSelector).append(").data('canChange');");
			sb.append("if(canChangeFlag == null){");
				sb.append("if($(").append(targetSelector).append(").find(':selected').val() == null || $(").append(targetSelector).append(").find(':selected').val() == '' ){");
					sb.append("$(").append(targetSelector).append(").data('canChange','YES');");
					sb.append("canChangeFlag = 'YES';");
				sb.append("}else{");
				sb.append("$(").append(targetSelector).append(").data('canChange','NO');");
				sb.append("canChangeFlag = 'NO';");
				sb.append("}");
			sb.append("}");
			sb.append("if(canChangeFlag == 'YES' ){");
			sb.append("$(").append(targetSelector).append(").val( $(this).find(':selected').val() );");
			if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(responseType) ){
                //If value is 'OTHER', make target suggested response visible, copy the source suggestedResponse to the target suggested response
				sb.append("if( $(this).find(':selected').val() == 'OTHER'){");
				sb.append("$(").append(targetSelector).append(").closest('div.questionDiv').find('span.responseSuggestInputSpan').show();");
				sb.append("}else{");		
				sb.append("$(").append(targetSelector).append(").closest('div.questionDiv').find('span.responseSuggestInputSpan').hide();");
				sb.append("}");
			}
			sb.append("}");
			sb.append("});");
			//If the user changes the target, need to set canChange to no on the target.
			sb.append("$(").append(targetSelector).append(").change(function(){");
			sb.append("$(this).data('canChange','NO');");
			sb.append("});");
			
			if(ResponseType.CONTROLLED_VOCAB_SUGGEST.equals(responseType) ){
				//If the source suggested response changes, update the target ( if canChange is 'YES' )
				sb.append("$(").append(sourceSuggestSelector).append(").keyup(function(){");
				sb.append("if($(" + targetSelector + ").data('canChange') == 'YES' ){");
				sb.append("$(").append(targetSuggestSelector).append(").val( $(").append(sourceSuggestSelector).append(").val() );");	
				sb.append("}});");
                //If Target is 'OTHER', and the user changes the other text, set the target canChange to 'NO' 
				sb.append("$(").append(targetSuggestSelector).append(").keyup(function(){")
				  .append("$(").append(targetSelector).append(").data('canChange','NO');")
				.append("});");
			}
			
			return sb.toString();
		}
		return null;
	}
	
}
